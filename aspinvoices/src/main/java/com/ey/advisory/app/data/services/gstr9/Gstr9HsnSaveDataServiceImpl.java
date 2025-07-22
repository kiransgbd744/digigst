package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.master.HsnOrSacMasterEntity;
import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.data.entities.gstr9.Gstr9DigiComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnProcessEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9DigiComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnDetailsSummaryDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9ItemsResponseDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table17ItemsReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Service("Gstr9HsnSaveDataServiceImpl")
@Slf4j
public class Gstr9HsnSaveDataServiceImpl implements Gstr9HsnSaveDataService {

	@Autowired
	@Qualifier("Gstr9HsnProcessedRepository")
	private Gstr9HsnProcessedRepository gstr9HsnProcessRepo;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnOrSacRepository;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSUmmaryRepo;

	@Autowired
	private Gstr9DigiComputeRepository gstr9DigiComputeRepository;

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static ImmutableList<String> hsnSectionList = ImmutableList.of("17",
			"18");

	public String saveHsnOutwardInwardData(List<Gstr9Table17ItemsReqDto> req,
			String type, String gstin, String fy) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr9HsnSaveDataServiceImpl.saveHsnOutwardData() starts: "
							+ " Gstin is '%s', Type is  '%s', Financial Year is  '%s'",
					gstin, type, fy);
			LOGGER.debug(msg);
		}

		String returnPeriod = "03" + fy.split("-")[1];
		List<Gstr9HsnProcessEntity> listEntity = new ArrayList<>();

		try {
			List<String> docKeys = new ArrayList<>();
			req.forEach(request -> {
				Gstr9HsnProcessEntity gstr9ProcessEntity = new Gstr9HsnProcessEntity();
				String docKey = null;
				if ("O".equalsIgnoreCase(type)) {
					gstr9ProcessEntity.setTableNumber("17");
					docKey = CommonUtility.generateGstr9DocKey(gstin, fy, "17",
							request.getHsnSc(), request.getRt().toString(),
							request.getUqc());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("Gstr9HsnSaveDataServiceImpl"
								+ ".saveHsnOutwardData DocKey for Outward is, Dockey = "
								+ docKey);
					}
				} else {

					gstr9ProcessEntity.setTableNumber("18");
					docKey = CommonUtility.generateGstr9DocKey(gstin, fy, "18",
							request.getHsnSc(), request.getRt().toString(),
							request.getUqc());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("Gstr9HsnSaveDataServiceImpl"
								+ ".saveHsnOutwardData DocKey for Inward is, Dockey = "
								+ docKey);
					}
				}

				gstr9ProcessEntity.setHsn(request.getHsnSc());
				gstr9ProcessEntity.setDesc(request.getDesc());
				gstr9ProcessEntity.setRateOfTax(request.getRt());
				gstr9ProcessEntity.setUqc(request.getUqc());
				gstr9ProcessEntity.setTotalQnt(request.getQty());
				gstr9ProcessEntity.setTaxableVal(request.getTxval());
				gstr9ProcessEntity.setConRateFlag(request.getIsconcesstional());
				gstr9ProcessEntity.setIgst(request.getIamt());
				gstr9ProcessEntity.setCgst(request.getCamt());
				gstr9ProcessEntity.setSgst(request.getSamt());
				gstr9ProcessEntity.setCess(request.getCsamt());
				gstr9ProcessEntity.setGstin(gstin);
				gstr9ProcessEntity.setFy(fy);
				gstr9ProcessEntity.setGst9HsnDocKey(docKey);
				gstr9ProcessEntity.setRetPeriod(returnPeriod);
				gstr9ProcessEntity.setSource("U");
				docKeys.add(docKey);

				listEntity.add(gstr9ProcessEntity);

			});
			gstr9HsnProcessRepo.updateSameInvKey(docKeys);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9HsnSaveDataServiceImpl"
						+ ".saveHsnOutwardData Soft Delete completed");
			}
			gstr9HsnProcessRepo.saveAll(listEntity);

		} catch (Exception ex) {
			String msg = "Exception occured in Gstr9HsnSaveDataServiceImpl.saveHsnOutwardData()";
			LOGGER.error(msg, ex);
			return "Failed to save data";
		}
		return "Data saved successfully";
	}

	public String deleteHsnOutwardInwardData(List<Gstr9Table17ItemsReqDto> req,
			String type, String gstin, String fy) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr9HsnSaveDataServiceImpl.deleteHsnOutwardData() starts: "
							+ " Gstin is '%s', Type is  '%s', Financial Year is  '%s'",
					gstin, type, fy);
			LOGGER.debug(msg);
		}
		try {
			List<String> docKeys = new ArrayList<>();
			req.forEach(request -> {
				String docKey = null;
				if ("O".equalsIgnoreCase(type)) {
					docKey = CommonUtility.generateGstr9DocKey(gstin, fy, "17",
							request.getHsnSc(), request.getRt().toString(),
							request.getUqc());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("Gstr9HsnSaveDataServiceImpl"
								+ ".deleteHsnOutwardData DocKey for Outward is, Dockey = "
								+ docKey);
					}
				} else {
					docKey = CommonUtility.generateGstr9DocKey(gstin, fy, "18",
							request.getHsnSc(), request.getRt().toString(),
							request.getUqc());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("Gstr9HsnSaveDataServiceImpl"
								+ ".deleteHsnOutwardData DocKey for Inward is, Dockey = "
								+ docKey);
					}
				}
				docKeys.add(docKey);
			});
			gstr9HsnProcessRepo.updateSameInvKey(docKeys);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9HsnSaveDataServiceImpl"
						+ ".deleteHsnOutwardData Soft Delete completed");
			}
		} catch (Exception ex) {
			String msg = "Exception occured in Gstr9HsnSaveDataServiceImpl.saveHsnOutwardData()";
			LOGGER.error(msg, ex);
			return "Failed to delete user edited data";
		}
		return "User edited data deleted successfully";
	}

	@Override
	public List<Gstr9ItemsResponseDto> getHsnProcessedData(String gstin,
			String fyYear, String type) {

		String tableNumber = null;
		try {
			if ("I".equalsIgnoreCase(type)) {

				tableNumber = "18";
			} else if ("O".equalsIgnoreCase(type)) {

				tableNumber = "17";
			}
			List<Gstr9HsnProcessEntity> listGstr9HsnData = gstr9HsnProcessRepo
					.listAllGstr9ProcessedData(gstin, fyYear, tableNumber);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9HsnSaveDataServiceImpl"
						+ ".getHsnProcessedData Gstr9 Process Data : = "
						+ listGstr9HsnData);
			}

			long dbLoadStTime = System.currentTimeMillis();

			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data"
								+ " from DB is '%d' millisecs Data count: '%s'",
						dbLoadTimeDiff, listGstr9HsnData.size());
				LOGGER.debug(msg);
			}

			List<Gstr9Table17ItemsReqDto> resp = listGstr9HsnData.stream()
					.map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String, Gstr9Table17ItemsReqDto> userMap = new HashMap<>();
			for (Gstr9Table17ItemsReqDto userDto : resp) {
				String rate = null;
				if (userDto.getRt() != null) {
					rate = String.format("%.2f", userDto.getRt());
				}
				userMap.put(userDto.getHsnSc() + "|" + rate + "|"
						+ userDto.getUqc(), userDto);
			}

			StringBuilder buildQuery = new StringBuilder();
			StringBuilder buildHeader = new StringBuilder();

			if (tableNumber != null && !tableNumber.isEmpty()) {
				buildQuery.append(" WHERE SECTION = :tableNumber");
			}
			buildQuery.append(" AND IS_ACTIVE = TRUE");

			if (gstin != null && !gstin.isEmpty()) {
				buildQuery.append(" AND GSTIN = :gstin");
			}
			Integer updatedFy = 0;
			if (fyYear != null && !fyYear.isEmpty()) {
				String[] fy = fyYear.split("-");
				updatedFy = Integer.parseInt(fy[0] + fy[1].substring(2, 4));
				buildQuery.append(" AND FINANCIAL_YEAR = :updatedFy");
			}
			buildQuery.append(" Group by HSNSAC,UQC,tax_rate,ISCONCESSTIONAL");

			String queryStr = createApiProcessedQueryString(
					buildQuery.toString(), buildHeader.toString());
			Query q = entityManager.createNativeQuery(queryStr);
			if (gstin != null && !gstin.isEmpty()) {
				q.setParameter("gstin", gstin);
			}
			q.setParameter("tableNumber", tableNumber);
			q.setParameter("updatedFy", updatedFy);
			List<Object[]> list = q.getResultList();
			List<Gstr9Table17ItemsReqDto> resp1 = list.stream()
					.map(o -> convertArrayToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			Map<String, Gstr9Table17ItemsReqDto> processedMap = new HashMap<>();
			for (Gstr9Table17ItemsReqDto processedDto : resp1) {
				String rate = null;
				if (processedDto.getRt() != null) {
					rate = String.format("%.2f", processedDto.getRt());
				}
				processedMap.put(processedDto.getHsnSc() + "|" + rate + "|"
						+ processedDto.getUqc(), processedDto);
			}
			Map<String, Gstr9ItemsResponseDto> responseMap = new HashMap<>();
			// Merge processedMap into responseMap
			for (Map.Entry<String, Gstr9Table17ItemsReqDto> entry : processedMap
					.entrySet()) {
				String key = entry.getKey();

				// Check if key exists in userMap
				if (userMap.containsKey(key)) {
					Gstr9Table17ItemsReqDto processedDto = processedMap
							.get(key);
					Gstr9Table17ItemsReqDto userDto = userMap.get(key);

					Gstr9ItemsResponseDto responseDto = new Gstr9ItemsResponseDto();
					responseDto.setHsnSc(processedDto.getHsnSc());
					responseDto.setRt(processedDto.getRt());
					responseDto.setUqc(processedDto.getUqc());
					responseDto.setProcesseddesc(processedDto.getDesc());
					responseDto.setProcessedqty(processedDto.getQty());
					responseDto.setProcessedtxval(processedDto.getTxval());
					responseDto.setIsprocessedconcesstional(
							processedDto.getIsconcesstional());
					responseDto.setProcessedigst(processedDto.getIamt());
					responseDto.setProcessedcgst(processedDto.getCamt());
					responseDto.setProcessedsgst(processedDto.getSamt());
					responseDto.setProcessedcess(processedDto.getCsamt());
					responseDto.setUserdesc(userDto.getDesc());
					responseDto.setUserqty(userDto.getQty());
					responseDto.setUsertxval(userDto.getTxval());
					responseDto.setIsuserconcesstional(
							userDto.getIsconcesstional());
					responseDto.setUserigst(userDto.getIamt());
					responseDto.setUsercgst(userDto.getCamt());
					responseDto.setUsersgst(userDto.getSamt());
					responseDto.setUsercess(userDto.getCsamt());

					responseMap.put(key, responseDto);
				} else {
					Gstr9Table17ItemsReqDto processedDto = processedMap
							.get(key);
					Gstr9ItemsResponseDto responseDto = new Gstr9ItemsResponseDto();
					responseDto.setHsnSc(processedDto.getHsnSc());
					responseDto.setRt(processedDto.getRt());
					responseDto.setUqc(processedDto.getUqc());
					responseDto.setProcesseddesc(processedDto.getDesc());
					responseDto.setProcessedqty(processedDto.getQty());
					responseDto.setProcessedtxval(processedDto.getTxval());
					responseDto.setIsprocessedconcesstional(
							processedDto.getIsconcesstional());
					responseDto.setProcessedigst(processedDto.getIamt());
					responseDto.setProcessedcgst(processedDto.getCamt());
					responseDto.setProcessedsgst(processedDto.getSamt());
					responseDto.setProcessedcess(processedDto.getCsamt());

					responseMap.put(key, responseDto);

				}
			}

			for (Map.Entry<String, Gstr9Table17ItemsReqDto> entry : userMap
					.entrySet()) {
				String key = entry.getKey();
				if (!responseMap.containsKey(key)) {
					Gstr9Table17ItemsReqDto userDto = userMap.get(key);
					Gstr9ItemsResponseDto responseDto = new Gstr9ItemsResponseDto();
					responseDto.setHsnSc(userDto.getHsnSc());
					responseDto.setRt(userDto.getRt());
					responseDto.setUqc(userDto.getUqc());
					responseDto.setUserdesc(userDto.getDesc());
					responseDto.setUserqty(userDto.getQty());
					responseDto.setUsertxval(userDto.getTxval());
					responseDto.setIsuserconcesstional(
							userDto.getIsconcesstional());
					responseDto.setUserigst(userDto.getIamt());
					responseDto.setUsercgst(userDto.getCamt());
					responseDto.setUsersgst(userDto.getSamt());
					responseDto.setUsercess(userDto.getCsamt());

					responseMap.put(key, responseDto);
				}
			}

			List<Gstr9ItemsResponseDto> valuesList = new ArrayList<>(
					responseMap.values());

			return valuesList;

		} catch (Exception ex) {
			String msg = "Exception occured in Gstr9HsnSaveDataServiceImpl.getHsnProcessedData()";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private Gstr9Table17ItemsReqDto convertArrayToDto(Object[] arr) {

		Gstr9Table17ItemsReqDto dto = new Gstr9Table17ItemsReqDto();

		dto.setHsnSc(arr[1] != null ? arr[1].toString() : null);
		if (arr[1] != null) {
			HsnOrSacMasterEntity hsnOrSacMasterEntity = hsnOrSacRepository
					.findByHsnSac(arr[1].toString());
			if (hsnOrSacMasterEntity != null)
				dto.setDesc(hsnOrSacMasterEntity.getDescription());
		}
		dto.setRt(arr[3] != null ? (new BigDecimal(arr[3].toString())) : null);
		dto.setUqc(arr[4] != null ? arr[4].toString() : null);
		dto.setQty(arr[5] != null ? (new BigDecimal(arr[5].toString())) : null);
		dto.setTxval(
				arr[6] != null ? (new BigDecimal(arr[6].toString())) : null);
		dto.setIsconcesstional(arr[7] != null ? arr[7].toString() : null);
		dto.setIamt(
				arr[8] != null ? (new BigDecimal(arr[8].toString())) : null);
		dto.setCamt(
				arr[9] != null ? (new BigDecimal(arr[9].toString())) : null);
		dto.setSamt(
				arr[10] != null ? (new BigDecimal(arr[10].toString())) : null);
		dto.setCsamt(
				arr[11] != null ? (new BigDecimal(arr[11].toString())) : null);

		return dto;
	}

	private String createApiProcessedQueryString(String buildQuery,
			String buildHeader) {

		return " SELECT	Row_number () over (order by HSNSAC,UQC,tax_rate) AS S_NO,HSNSAC,null AS description,"
				+ "tax_rate	,UQC,sum(QTY) QTY,IFNULL(sum(TAXABLE_VALUE),0) TAXABLE_VALUE,ISCONCESSTIONAL,"
				+ "IFNULL(sum(IGST_AMT),0) IGST_AMT,IFNULL(sum(CGST_AMT),0) CGST_AMT,"
				+ "IFNULL(sum(SGST_AMT),0) SGST_AMT,IFNULL(sum(CESS_AMT),0) CESS_AMT"
				+ " FROM TBL_GSTR9_DIGI_COMPUTE" + buildQuery;
	}

	private Gstr9Table17ItemsReqDto convertToDto(Gstr9HsnProcessEntity entity) {

		Gstr9Table17ItemsReqDto dto = new Gstr9Table17ItemsReqDto();

		dto.setHsnSc(entity.getHsn());
		dto.setDesc(entity.getDesc());
		dto.setRt(entity.getRateOfTax());
		dto.setUqc(entity.getUqc());
		dto.setQty(entity.getTotalQnt());
		dto.setTxval(entity.getTaxableVal());
		dto.setIsconcesstional(entity.getConRateFlag());
		dto.setIamt(entity.getIgst());
		dto.setCamt(entity.getCgst());
		dto.setSamt(entity.getSgst());
		dto.setCsamt(entity.getCess());
		return dto;
	}

	@Override
	public List<Gstr9HsnDetailsSummaryDto> getHsnSummaryDetails(String gstin,
			String fyYear) {

		AtomicInteger outwardPrCount = new AtomicInteger(0);

		AtomicInteger inwardPrCount = new AtomicInteger(0);
		
		AtomicInteger outwardDigiCount = new AtomicInteger(0);

		AtomicInteger inwardDigiCount = new AtomicInteger(0);
		
		AtomicInteger outwardGstnCount = new AtomicInteger(0);

		AtomicInteger inwardGstnCount = new AtomicInteger(0);

		AtomicInteger outwardGstinCount = new AtomicInteger(0);

		AtomicInteger inwardGstinCount = new AtomicInteger(0);
		String taxPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fyYear);
		List<Gstr9DigiComputeEntity> gstr9DigiComputeEntityList = gstr9DigiComputeRepository
				.findByGstinAndSectionInAndRetPeriodAndIsActiveTrue(gstin,
						hsnSectionList, taxPeriod);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr9HsnSaveDataServiceImpl"
					+ ".getgstr9DashBoardList DigiCompute list size = "
					+ gstr9DigiComputeEntityList.size());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr9HsnSaveDataServiceImpl"
					+ ".getgstr9DashBoardList DigiCompute list {}"
					+ gstr9DigiComputeEntityList);
		}

		List<Gstr9HsnProcessEntity> listGstr9HsnData = gstr9HsnProcessRepo
				.listAllGstr9ProcessedDetails(gstin, fyYear);

		List<Gstr9HsnDetailsSummaryDto> resp = listGstr9HsnData.stream()
				.map(o -> convertToProcess(o))
				.collect(Collectors.toCollection(ArrayList::new));

		resp.forEach(e -> {
			if (e.getSection() != null && e.getSection().equals("17")) {
				outwardPrCount.getAndIncrement();
			} else if (e.getSection() != null && e.getSection().equals("18")) {
				inwardPrCount.getAndIncrement();
			}
		});

		List<Gstr9GetSummaryEntity> listGetSummary = gstr9GetSUmmaryRepo
				.listAllGstr9SummaryData(gstin, fyYear);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9HsnSaveDataServiceImpl"
					+ ".getHsnSummaryDetails Outward count for section 17 is : = "
					+ outwardGstnCount);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9HsnSaveDataServiceImpl"
					+ ".getHsnSummaryDetails Inward count for section 18 is : = "
					+ inwardGstnCount);
		}

		List<Gstr9HsnDetailsSummaryDto> respDetailsSummary = listGetSummary
				.stream().map(o -> convertToSummary(o))
				.collect(Collectors.toCollection(ArrayList::new));

		respDetailsSummary.forEach(e -> {
			if (e.getSection() != null && e.getSection().equals("17")) {
				outwardGstinCount.getAndIncrement();
			} else if (e.getSection() != null && e.getSection().equals("18")) {
				inwardGstinCount.getAndIncrement();
			}
		});

		List<Gstr9HsnDetailsSummaryDto> digiComputeDtoList = gstr9DigiComputeEntityList
				.stream().map(o -> convertToDigiGstProcess(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		digiComputeDtoList.forEach(e -> {
			if (e.getSection() != null && e.getSection().equals("17")) {
				outwardDigiCount.getAndIncrement();
			} else if (e.getSection() != null && e.getSection().equals("18")) {
				inwardDigiCount.getAndIncrement();
			}
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9HsnSaveDataServiceImpl"
					+ ".getHsnSummaryDetails Outward count for section 17 is : = "
					+ outwardGstinCount);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr9HsnSaveDataServiceImpl" + ".digiComputeDtoList  {}",
					digiComputeDtoList);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9HsnSaveDataServiceImpl"
					+ ".getHsnSummaryDetails Inward count for section 18 is : = "
					+ inwardGstinCount);
		}

		Map<String, Gstr9HsnDetailsSummaryDto> digiComputeMap = digiComputeDtoList
				.stream()
				.collect(Collectors.groupingBy(o -> o.getSection(),
						Collectors.collectingAndThen(
								Collectors.reducing(
										(a, b) -> addDtoCompute(a, b)),
								Optional::get)));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr9HsnSaveDataServiceImpl" + ".digiComputeMap  {}",
					digiComputeMap);
		}

		Map<String, Gstr9HsnDetailsSummaryDto> processMap = resp.stream()
				.collect(Collectors.groupingBy(o -> o.getSection(),
						Collectors.collectingAndThen(
								Collectors.reducing((a, b) -> addDto(a, b)),
								Optional::get)));

		Map<String, Gstr9HsnDetailsSummaryDto> summaryMap = respDetailsSummary
				.stream()
				.collect(Collectors.groupingBy(o -> o.getSection(),
						Collectors.collectingAndThen(
								Collectors.reducing((a, b) -> addDto1(a, b)),
								Optional::get)));

		Map<String, Gstr9HsnDetailsSummaryDto> gstrMap = gstr9DetailsSummaryMap();

		gstrMap.replaceAll((k, v) -> {
			if (digiComputeMap.containsKey(k))
				setDigiComputeData(v, digiComputeMap.get(k), outwardDigiCount,
						outwardGstinCount, inwardDigiCount, inwardGstinCount);
			if (processMap.containsKey(k))
				setProcessedData(v, processMap.get(k), outwardPrCount,
						outwardGstinCount, inwardPrCount, inwardGstinCount);
			if (summaryMap.containsKey(k))
				setSummaryData(v, summaryMap.get(k), outwardGstnCount,
						outwardGstinCount, inwardGstnCount, inwardGstinCount);
			return v;
		});

		List<Gstr9HsnDetailsSummaryDto> respList = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"Gstr9HsnSaveDataServiceImpl" + " gstrMap {}" + gstrMap);
		}

		respList = new ArrayList<>(gstrMap.values());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9HsnSaveDataServiceImpl"
					+ ".getHsnSummaryDetails Hsn Details  response  = "
					+ respList);
		}

		return respList;
	}

	private Gstr9HsnDetailsSummaryDto convertToDigiGstProcess(
			Gstr9DigiComputeEntity entity) {

		Gstr9HsnDetailsSummaryDto dto = new Gstr9HsnDetailsSummaryDto();

		dto.setDigiComputetotalQty(
				entity.getQty() != null ? entity.getQty() : BigDecimal.ZERO);
		dto.setDigiComputeTaxableVal(entity.getTaxableValue() != null
				? entity.getTaxableValue() : BigDecimal.ZERO);
		dto.setDigiComputeIgst(entity.getIgstAmount() != null
				? entity.getIgstAmount() : BigDecimal.ZERO);
		dto.setDigiComputeCgst(entity.getCgstAmount() != null
				? entity.getCgstAmount() : BigDecimal.ZERO);
		dto.setDigiComputeSgst(entity.getSgstAmount() != null
				? entity.getSgstAmount() : BigDecimal.ZERO);
		dto.setDigiComputeCess(entity.getCessAmount() != null
				? entity.getCessAmount() : BigDecimal.ZERO);
		dto.setSection(entity.getSection());
		return dto;
	}

	private Gstr9HsnDetailsSummaryDto convertToProcess(
			Gstr9HsnProcessEntity entity) {

		Gstr9HsnDetailsSummaryDto dto = new Gstr9HsnDetailsSummaryDto();

		dto.setDigiPrtotalQty(entity.getTotalQnt() != null
				? entity.getTotalQnt() : BigDecimal.ZERO);
		dto.setDigiPrTaxableVal(entity.getTaxableVal() != null
				? entity.getTaxableVal() : BigDecimal.ZERO);
		dto.setDigiPrIgst(
				entity.getIgst() != null ? entity.getIgst() : BigDecimal.ZERO);
		dto.setDigiPrCgst(
				entity.getCgst() != null ? entity.getCgst() : BigDecimal.ZERO);
		dto.setDigiPrSgst(
				entity.getSgst() != null ? entity.getSgst() : BigDecimal.ZERO);
		dto.setDigiPrCess(
				entity.getCess() != null ? entity.getCess() : BigDecimal.ZERO);
		dto.setSection(entity.getTableNumber());
		return dto;
	}

	private Gstr9HsnDetailsSummaryDto convertToSummary(
			Gstr9GetSummaryEntity entity) {

		Gstr9HsnDetailsSummaryDto dto = new Gstr9HsnDetailsSummaryDto();

		dto.setGstr9SummQty(
				entity.getQty() != null ? entity.getQty() : BigDecimal.ZERO);
		dto.setGstr9SummTaxableVal(entity.getTxVal() != null ? entity.getTxVal()
				: BigDecimal.ZERO);
		dto.setGstr9SummIgst(
				entity.getIamt() != null ? entity.getIamt() : BigDecimal.ZERO);
		dto.setGstr9SummCgst(
				entity.getCamt() != null ? entity.getCamt() : BigDecimal.ZERO);
		dto.setGstr9SummSgst(
				entity.getSamt() != null ? entity.getSamt() : BigDecimal.ZERO);
		dto.setGstr9SummCess(entity.getCsamt() != null ? entity.getCsamt()
				: BigDecimal.ZERO);
		dto.setSection(entity.getSection());
		return dto;
	}

	private Gstr9HsnDetailsSummaryDto addDtoCompute(Gstr9HsnDetailsSummaryDto a,
			Gstr9HsnDetailsSummaryDto b) {
		Gstr9HsnDetailsSummaryDto dto = new Gstr9HsnDetailsSummaryDto();

		dto.setDigiComputetotalQty(
				a.getDigiComputetotalQty().add(b.getDigiComputetotalQty()));
		dto.setDigiComputeTaxableVal(
				a.getDigiComputeTaxableVal().add(b.getDigiComputeTaxableVal()));
		dto.setDigiComputeIgst(
				a.getDigiComputeIgst().add(b.getDigiComputeIgst()));
		dto.setDigiComputeCgst(
				a.getDigiComputeCgst().add(b.getDigiComputeCgst()));
		dto.setDigiComputeSgst(
				a.getDigiComputeSgst().add(b.getDigiComputeSgst()));
		dto.setDigiComputeCess(
				a.getDigiComputeCess().add(b.getDigiComputeCess()));

		return dto;

	}

	private Gstr9HsnDetailsSummaryDto addDto1(Gstr9HsnDetailsSummaryDto a,
			Gstr9HsnDetailsSummaryDto b) {

		Gstr9HsnDetailsSummaryDto dto = new Gstr9HsnDetailsSummaryDto();

		dto.setGstr9SummQty(a.getGstr9SummQty().add(b.getGstr9SummQty()));
		dto.setGstr9SummTaxableVal(
				a.getGstr9SummTaxableVal().add(b.getGstr9SummTaxableVal()));
		dto.setGstr9SummIgst(a.getGstr9SummIgst().add(b.getGstr9SummIgst()));
		dto.setGstr9SummCgst(a.getGstr9SummCgst().add(b.getGstr9SummCgst()));
		dto.setGstr9SummSgst(a.getGstr9SummSgst().add(b.getGstr9SummSgst()));
		dto.setGstr9SummCess(a.getGstr9SummCess().add(b.getGstr9SummCess()));

		return dto;
	}

	public Map<String, Gstr9HsnDetailsSummaryDto> gstr9DetailsSummaryMap() {

		Map<String, Gstr9HsnDetailsSummaryDto> map = new LinkedHashMap<>();

		map.put("17", new Gstr9HsnDetailsSummaryDto("17", "HSNOUTWARD"));
		map.put("18", new Gstr9HsnDetailsSummaryDto("18", "HSNINWARD"));

		return map;
	}

	private void setDigiComputeData(Gstr9HsnDetailsSummaryDto v,
			Gstr9HsnDetailsSummaryDto inputDto, AtomicInteger outwardCount,
			AtomicInteger outwardGstinCount, AtomicInteger inwardCount,
			AtomicInteger inwardGstinCount) {

		v.setDigiComputetotalQty(inputDto.getDigiComputetotalQty());
		v.setDigiComputeTaxableVal(inputDto.getDigiComputeTaxableVal());
		v.setDigiComputeIgst(inputDto.getDigiComputeIgst());
		v.setDigiComputeCgst(inputDto.getDigiComputeCgst());
		v.setDigiComputeSgst(inputDto.getDigiComputeSgst());
		v.setDigiComputeCess(inputDto.getDigiComputeCess());
		if (v.getSection() != null && v.getSection().equals("17")) {
			v.setDigiComputetotalQtycount(outwardCount);
		} else if (v.getSection() != null && v.getSection().equals("18")) {
			v.setDigiComputetotalQtycount(inwardCount);
		}
	}

	private void setProcessedData(Gstr9HsnDetailsSummaryDto v,
			Gstr9HsnDetailsSummaryDto inputDto, AtomicInteger outwardCount,
			AtomicInteger outwardGstinCount, AtomicInteger inwardCount,
			AtomicInteger inwardGstinCount) {

		v.setDigiPrtotalQty(inputDto.getDigiPrtotalQty());
		v.setDigiPrTaxableVal(inputDto.getDigiPrTaxableVal());
		v.setDigiPrIgst(inputDto.getDigiPrIgst());
		v.setDigiPrCgst(inputDto.getDigiPrCgst());
		v.setDigiPrSgst(inputDto.getDigiPrSgst());
		v.setDigiPrCess(inputDto.getDigiPrCess());
		if (v.getSection() != null && v.getSection().equals("17")) {
			v.setDigiPrcount(outwardCount);
		} else if (v.getSection() != null && v.getSection().equals("18")) {
			v.setDigiPrcount(inwardCount);
		}

	}

	private void setSummaryData(Gstr9HsnDetailsSummaryDto v,
			Gstr9HsnDetailsSummaryDto inputDto, AtomicInteger outwardCount,
			AtomicInteger outwardGstinCount, AtomicInteger inwardCount,
			AtomicInteger inwardGstinCount) {

		v.setGstr9SummQty(inputDto.getGstr9SummQty());
		v.setGstr9SummTaxableVal(inputDto.getGstr9SummTaxableVal());
		v.setGstr9SummIgst(inputDto.getGstr9SummIgst());
		v.setGstr9SummCgst(inputDto.getGstr9SummCgst());
		v.setGstr9SummSgst(inputDto.getGstr9SummSgst());
		v.setGstr9SummCess(inputDto.getGstr9SummCess());
		if (v.getSection() != null && v.getSection().equals("17")) {
			v.setGstr9Summcount(outwardGstinCount);
		} else if (v.getSection() != null && v.getSection().equals("18")) {
			v.setGstr9Summcount(inwardGstinCount);
		}
	}

	private Gstr9HsnDetailsSummaryDto addDto(Gstr9HsnDetailsSummaryDto a,
			Gstr9HsnDetailsSummaryDto b) {

		Gstr9HsnDetailsSummaryDto dto = new Gstr9HsnDetailsSummaryDto();

		dto.setDigiPrtotalQty(a.getDigiPrtotalQty().add(b.getDigiPrtotalQty()));
		dto.setDigiPrTaxableVal(
				a.getDigiPrTaxableVal().add(b.getDigiPrTaxableVal()));
		dto.setDigiPrIgst(a.getDigiPrIgst().add(b.getDigiPrIgst()));
		dto.setDigiPrCgst(a.getDigiPrCgst().add(b.getDigiPrCgst()));
		dto.setDigiPrSgst(a.getDigiPrSgst().add(b.getDigiPrSgst()));
		dto.setDigiPrCess(a.getDigiPrCess().add(b.getDigiPrCess()));
		return dto;
	}
}
