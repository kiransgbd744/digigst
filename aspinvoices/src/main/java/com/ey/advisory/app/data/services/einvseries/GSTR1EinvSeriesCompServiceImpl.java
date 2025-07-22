package com.ey.advisory.app.data.services.einvseries;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.Gstr1InvoiceFileUploadEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1EinvSeriesCompRepo;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("GSTR1EinvSeriesCompServiceImpl")
public class GSTR1EinvSeriesCompServiceImpl
		implements GSTR1EinvSeriesCompService {

	private final static String WEB_UPLOAD_KEY = "|";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1InvoiceRepository;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfPrmRepository;

	@Autowired
	private Gstr1EinvSeriesCompRepo gstr1EinvSeriesCompRepo;

	@Autowired
	private ObjectFactory<InvoiceSeriesGenerator> invGenFactory;

	@Autowired
	private ObjectFactory<InvoiceBrkUpSeriesGenerator> invBrkUpFactory;

	private static final List<String> INCLUDE_TYPES = ImmutableList.of("INV",
			"BOS", "SLF", "RNV", "DR", "CR", "DLC");

	@Override
	public void compandperstSeriesData(Long configId, String gstin,
			String retPeriod, String implType) {
		List<InvoiceSeriesDTO> serDto = new ArrayList<>();
		LocalDateTime startTime = LocalDateTime.now();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoice Series Started for " + " gstin '%s', "
								+ "taxPeriod '%s' and Impl to be executed '%s' ",
						gstin, retPeriod, implType);
				LOGGER.debug(msg);
			}
			
			if (implType.equalsIgnoreCase("A")
					|| implType.equalsIgnoreCase("B")) {

				List<InvoiceSeries> invSeriesList = callProcandGetData(gstin,
						retPeriod);

				LOGGER.debug(
						"invSeries List size {} for gstin and taxperiod {}, {} ",
						invSeriesList.size(), gstin, retPeriod);
				if (invSeriesList.isEmpty()) {
					String msg = String.format(
							"No Documents found for GSTIN %s and Return Period %s",
							gstin, retPeriod);
					LOGGER.error(msg);
					gstr1EinvSeriesCompRepo.updateRequestStatus(configId,
							APIConstants.SUCCESS_WITH_NO_DATA, startTime,
							LocalDateTime.now());
					return;
				}
				invSeriesList
						.removeIf(x -> !INCLUDE_TYPES.contains(x.getDocType()));

				List<InvoiceSeries> invSortedList = invSeriesList.stream()
						.sorted(Comparator.comparing(InvoiceSeries::getDocNum))
						.collect(Collectors.toList());

				for (InvoiceSeries invoiceSeries : invSortedList) {
					if ("DLC".equalsIgnoreCase(invoiceSeries.getDocType())) {
						if ("JWK".equalsIgnoreCase(
								invoiceSeries.getSubSupplyType())) {
							invoiceSeries.setDocType("DLC");
						} else {
							invoiceSeries.setDocType("DLCOTH");
						}
					}
					if ("BOS".equalsIgnoreCase(invoiceSeries.getDocType())) {
						invoiceSeries.setDocType("INV");
					}
				}

				if ("B".equalsIgnoreCase(implType)) {
					InvoiceSeriesGenerator invGen = invGenFactory.getObject();
					serDto = invGen.generateInvoiceSeries(invSortedList,
							retPeriod, gstin);
				} else {
					InvoiceBrkUpSeriesGenerator invBrkUpGen = invBrkUpFactory
							.getObject();
					serDto = invBrkUpGen.generateInvoiceSeries(invSortedList,
							retPeriod, gstin);
				}
				List<Gstr1InvoiceFileUploadEntity> entList = convertInvSerToEntity(
						serDto);
				if (entList != null && !entList.isEmpty()) {
					persistInvSeriesData(gstin, retPeriod, entList);
					gstr1EinvSeriesCompRepo.updateRequestStatus(configId,
							APIConstants.SUCCESS, startTime,
							LocalDateTime.now());
				}
			}
			else {

				String[] parts = implType.split("\\*");

				String optionOpted = parts[0];

				serDto = callAdvanceCompProcandGetData(gstin, retPeriod,
						optionOpted, parts.length == 2 ? parts[1] : "50");
				LOGGER.debug(
						"invSeries List size {} for gstin and taxperiod {}, {} ",
						serDto.size(), gstin, retPeriod);
				if (serDto.isEmpty()) {
					String msg = String.format(
							"No Documents found for GSTIN %s and Return Period %s",
							gstin, retPeriod);
					LOGGER.error(msg);
					gstr1EinvSeriesCompRepo.updateRequestStatus(configId,
							APIConstants.SUCCESS_WITH_NO_DATA, startTime,
							LocalDateTime.now());
					return;
				}

				List<Gstr1InvoiceFileUploadEntity> entList = convertInvSerToEntity(
						serDto);
				if (entList != null && !entList.isEmpty()) {
					persistInvSeriesData(gstin, retPeriod, entList);
					gstr1EinvSeriesCompRepo.updateRequestStatus(configId,
							APIConstants.SUCCESS, startTime,
							LocalDateTime.now());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception while compandperstSeriesData the Data", e);
			gstr1EinvSeriesCompRepo.updateRequestStatus(configId,
					APIConstants.FAILED, startTime, LocalDateTime.now());
			throw new AppException(e.getMessage());
		}
	}

	
	private List<InvoiceSeries> callProcandGetData(String gstin,
			String retPeriod) {

		StoredProcedureQuery dispProc = entityManager
				.createStoredProcedureQuery("USP_COMPUTE_INOVICE_SERIES");

		dispProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		dispProc.setParameter("P_GSTIN", gstin);

		dispProc.registerStoredProcedureParameter("P_RET_PERIOD", String.class,
				ParameterMode.IN);

		dispProc.setParameter("P_RET_PERIOD", retPeriod);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Data proc Executed"
					+ " USP_COMPUTE_INOVICE_SERIES: gstin %s, "
					+ "taxPeriod %s ", gstin, retPeriod);
			LOGGER.debug(msg);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> records = dispProc.getResultList();
		List<InvoiceSeries> retList = records.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}


	private List<InvoiceSeriesDTO> callAdvanceCompProcandGetData(String gstin,
			String retPeriod, String optionOpted, String breakSeriesValue) {

		StoredProcedureQuery dispProc = entityManager
				.createStoredProcedureQuery("USP_INVOICE_SERIES_ADVANCED");

		dispProc.registerStoredProcedureParameter("IP_SUPPLIER_GSTIN",
				String.class, ParameterMode.IN);

		dispProc.setParameter("IP_SUPPLIER_GSTIN", gstin);

		dispProc.registerStoredProcedureParameter("IP_RETURN_PERIOD",
				String.class, ParameterMode.IN);

		dispProc.setParameter("IP_RETURN_PERIOD", retPeriod);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Data proc Executed"
					+ " USP_COMPUTE_INOVICE_SERIES: gstin %s, "
					+ "taxPeriod %s ", gstin, retPeriod);
			LOGGER.debug(msg);
		}

		if ("D".equalsIgnoreCase(optionOpted)) {
			dispProc.registerStoredProcedureParameter("IP_BREAK_SERIES",
					Integer.class, ParameterMode.IN);
			dispProc.setParameter("IP_BREAK_SERIES",
					Integer.valueOf(breakSeriesValue));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> records = dispProc.getResultList();
		
		if(records == null || records.isEmpty()){
			return new ArrayList<InvoiceSeriesDTO>(); 
		}
		List<InvoiceSeriesDTO> retList = records.parallelStream()
				.map(o -> advanceCompConvert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}
	
	private InvoiceSeries convert(Object[] arr) {
		InvoiceSeries obj = new InvoiceSeries();

		obj.setGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setDocType(arr[1] != null ? arr[1].toString() : null);
		obj.setDocNum(arr[2] != null ? arr[2].toString() : null);
		obj.setSubSupplyType(arr[3] != null ? arr[3].toString() : null);
		obj.setSupplyType(arr[4] != null ? arr[4].toString() : null);
		return obj;
	}

	//for Option C and D
	private InvoiceSeriesDTO advanceCompConvert(Object[] arr) {
		InvoiceSeriesDTO obj = new InvoiceSeriesDTO();
		
		obj.setGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setTaxPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setInvoiceseriesID(arr[2] != null ? arr[2].toString() : null);
		obj.setDocumentType(arr[3] != null ? arr[3].toString() : null);
		obj.setNatureOfSupp(arr[4] != null ? arr[4].toString() : null);
		obj.setFromSeries(arr[5] != null ? arr[5].toString() : null);
		obj.setToSeries(arr[6] != null ? arr[6].toString() : null);
		obj.setTotalNumber(arr[7] != null ? arr[7].toString() : null);
		obj.setCancelled(arr[8] != null ? arr[8].toString() : null);
		obj.setNetNumber(arr[9] != null ? arr[9].toString() : null);
//		obj.setPattern(arr[10] != null ? arr[10].toString() : null);
		return obj;
	}


	private String generateInvKey(InvoiceSeriesDTO dto) {
		String sgstin = dto.getGstin();
		String retPeriod = dto.getTaxPeriod();
		String serialNum = dto.getInvoiceseriesID();
		String from = dto.getFromSeries();
		String to = dto.getToSeries();
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(retPeriod)
				.add(serialNum).add(from).add(to).toString();
	}

	public List<Gstr1InvoiceFileUploadEntity> convertInvSerToEntity(
			List<InvoiceSeriesDTO> busProcessRecords) {
		List<Gstr1InvoiceFileUploadEntity> invoices = new ArrayList<>();
		Gstr1InvoiceFileUploadEntity invoice = null;
		for (InvoiceSeriesDTO obj : busProcessRecords) {
			invoice = new Gstr1InvoiceFileUploadEntity();
			invoice.setSgstin(obj.getGstin());
			invoice.setReturnPeriod(obj.getTaxPeriod());
			Integer deriRetPeriod = 0;
			if (obj.getTaxPeriod() != null && !obj.getTaxPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getTaxPeriod());
			}
			invoice.setSerialNo(Integer.parseInt(obj.getInvoiceseriesID()));
			invoice.setNatureOfDocument(obj.getNatureOfSupp());
			invoice.setFrom(obj.getFromSeries());
			invoice.setTo(obj.getToSeries());
			Integer total = Integer.parseInt(obj.getTotalNumber());
			invoice.setTotalNumber(total);
			Integer can = Integer.parseInt(obj.getCancelled());
			invoice.setCancelled(can);
			Integer netNum = Integer.parseInt(obj.getNetNumber());
			invoice.setNetNumber(netNum);
			invoice.setInvoiceKey(generateInvKey(obj));
			invoice.setDerivedRetPeriod(deriRetPeriod);
			invoice.setDataOriginType("C");
			invoice.setDelete(false);
			invoice.setCreatedBy("COMPUTE");
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			invoice.setCreatedOn(convertNow);
			invoices.add(invoice);
		}
		return invoices;
	}

	private void persistInvSeriesData(String gstin, String retPeriod,
			List<Gstr1InvoiceFileUploadEntity> invoiceDoc) {
		try {
			gstr1InvoiceRepository.updateExistingEntries(gstin, retPeriod,
					"COMPUTE");
			gstr1InvoiceRepository.saveAll(invoiceDoc);
		} catch (Exception e) {
			LOGGER.error("Exception while persisting the Data", e);
			throw new AppException(e.getMessage());
		}
	}
}
