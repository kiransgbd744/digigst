package com.ey.advisory.app.data.services.einvseries;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.EinvSeriRecPattConfEntity;
import com.ey.advisory.app.data.repositories.client.EinvSeriRecPattConfRepo;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("EinvSeriRecPattServiceImpl")
public class EinvSeriRecPattServiceImpl implements GSTR1EinvSeriesCompService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private EinvSeriRecPattConfRepo pattRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void compandperstSeriesData(Long configId, String gstin,
			String retPeriod, String implType) {
		try {

			String groupCode = TenantContext.getTenantId();
			Map<String, Config> configMap = configManager.getConfigs(
					"LOGICALPATTERN", "logical.patterncount.considered",
					groupCode);
			String patternCount = configMap != null
					&& configMap.get("logical.patterncount.considered") != null
							? configMap.get("logical.patterncount.considered")
									.getValue()
							: "8";

			List<InvoiceSeries> invSeriesList = callProcandGetData(gstin,
					retPeriod);
			if (invSeriesList.isEmpty()) {
				String msg = String.format(
						"No Documents found for GSTIN %s and Return Period %s",
						gstin, retPeriod);
				LOGGER.error(msg);
				return;
			}
			Invoice2BSeriesGenerator invGen = new Invoice2BSeriesGenerator();
			List<InvoiceSeriesDTO> serDto = invGen
					.generateInvoiceSeries(invSeriesList, retPeriod, gstin);
			List<EinvSeriRecPattConfEntity> entList = convertInvSerToEntity(
					serDto);
			groupandperstSeriesData(entList, Integer.valueOf(patternCount));

		} catch (Exception e) {
			LOGGER.error("Exception while compandperstSeriesData the Data", e);
			throw new AppException(e.getMessage());
		}
	}

	private InvoiceSeries convert(Object[] arr) {
		InvoiceSeries obj = new InvoiceSeries();
		obj.setGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setDocNum(arr[1] != null ? arr[1].toString() : null);
		obj.setDocType(arr[2] != null ? arr[2].toString() : null);
		obj.setTaxPeriod(
				arr[3] != null ? GenUtil.convertDerivedTaxPeriodToTaxPeriod(
						Integer.parseInt(arr[3].toString())) : null);
		return obj;
	}

	private List<InvoiceSeries> callProcandGetData(String gstin,
			String retPeriod) {
		StoredProcedureQuery dispProc = entityManager
				.createStoredProcedureQuery("USP_DOC_SERIES_PATTERN");
		dispProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);
		dispProc.setParameter("P_GSTIN", gstin);
		dispProc.registerStoredProcedureParameter("P_RET_PERIOD", Integer.class,
				ParameterMode.IN);
		dispProc.setParameter("P_RET_PERIOD",
				GenUtil.convertTaxPeriodToInt(retPeriod));
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Data proc Executed"
					+ " USP_COMPUTE_INOVICE_SERIES: gstin '%s', "
					+ "taxPeriod %s ", gstin, retPeriod);
			LOGGER.debug(msg);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> records = dispProc.getResultList();
		List<InvoiceSeries> retList = records.parallelStream()
				.map(o -> convert(o))
				.sorted(Comparator.comparing(InvoiceSeries::getDocNum))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}

	private List<EinvSeriRecPattConfEntity> convertInvSerToEntity(
			List<InvoiceSeriesDTO> busProcessRecords) {
		List<EinvSeriRecPattConfEntity> invoices = new ArrayList<>();
		EinvSeriRecPattConfEntity invoice = null;
		for (InvoiceSeriesDTO obj : busProcessRecords) {
			invoice = new EinvSeriRecPattConfEntity();
			invoice.setPattern(obj.getPattern());
			invoice.setPatternCount(Long.valueOf(obj.getNetNumber()));
			invoices.add(invoice);
		}
		return invoices;
	}

	private EinvSeriRecPattConfEntity generateFinalEntity(String pattern,
			Long patternCount) {
		EinvSeriRecPattConfEntity invoice = new EinvSeriRecPattConfEntity();
		invoice.setPattern(pattern);
		invoice.setPatternCount(patternCount);
		invoice.setUserType("DIGIGST");
		invoice.setFilterType("Y");
		invoice.setCreatedOn(LocalDateTime.now());
		invoice.setDelete(false);
		return invoice;
	}

	private void groupandperstSeriesData(
			List<EinvSeriRecPattConfEntity> entList,
			Integer patternToConsidered) {
		List<EinvSeriRecPattConfEntity> invoicesList = new ArrayList<>();
		List<String> patternList = new ArrayList<>();

		try {
			Map<String, Long> patterGrpMap = entList.stream()
					.filter(s -> StringUtils.isNotBlank(s.getPattern()))
					.collect(Collectors.groupingBy(
							EinvSeriRecPattConfEntity::getPattern, Collectors
									.summingLong(
											EinvSeriRecPattConfEntity::getPatternCount)));

			for (Map.Entry<String, Long> entry : patterGrpMap.entrySet()) {
				String msg = String.format(
						"Pattern is %s and Count of the Pattern is %s",
						entry.getKey(), entry.getValue());
				LOGGER.debug(msg);
				Long patternCount = 0L;
				if (entry.getKey().length() == patternToConsidered) {
					patternList.add(entry.getKey());
					EinvSeriRecPattConfEntity invEntity = pattRepo
							.findByPatternAndIsDeleteFalse(entry.getKey());
					if (invEntity == null) {
						patternCount = entry.getValue();
					} else {
						patternCount = invEntity.getPatternCount()
								+ entry.getValue();
					}
					invEntity = generateFinalEntity(entry.getKey(),
							patternCount);
					invoicesList.add(invEntity);
				} else {
					LOGGER.error(
							"Skipping the pattern since pattern count is too low {} {}",
							entry.getKey(), entry.getValue());
				}
			}
			if (!patternList.isEmpty()) {
				LOGGER.debug("Updating Pattern {}", patternList);
				pattRepo.updateExistPatterns(patternList);
			}
			if (!invoicesList.isEmpty()) {
				pattRepo.saveAll(invoicesList);
			}
		} catch (Exception e) {
			LOGGER.error("Exception while groupandperstSeriesData the Data", e);
			throw new AppException(e.getMessage());
		}
	}
}
