package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.docs.dto.simplified.ITC04ProcessSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("ITC04ProcessSummaryFetchDaoImpl")
public class ITC04ProcessSummaryFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	private Gstr1ProcessedRecordsCommonUtil gstr1ProcessedRecordsCommonUtil;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	public List<ITC04ProcessSummaryRespDto> fetchITC04Records(
			ITC04RequestDto req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Group Code enttering to DAO Layer ->{}",
					TenantContext.getTenantId());
		}
		List<Long> entityId = req.getEntityId();
		String taxPeriod = req.getTaxPeriod();
		// String taxPeriodTo = req.getTaxPeriodTo();
		/*
		 * int taxPeriodFrom = 0; int taxPeriodTo = 0; if(retPeriodFrom !=
		 * null){ taxPeriodFrom = GenUtil.convertTaxPeriodToInt(retPeriodFrom);
		 * } if(retPeriodTo != null){ taxPeriodTo =
		 * GenUtil.convertTaxPeriodToInt(retPeriodTo); }
		 */

		// String taxPeriodTo = req.getTaxPeriodTo();

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ITC04ProcessSummaryFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {},"
					+ "dataSecAttrs -> {}", req);
		}
		// Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String ProfitCenter = null;
		String ProfitCenter2 = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = null;

		List<String> pcList = null;
		List<String> pc2List = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PC2)) {
					ProfitCenter2 = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC2).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC2)
									.size() > 0) {
						pc2List = dataSecAttrs.get(OnboardingConstant.PC2);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				/*
				 * if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
				 * location = key; if
				 * (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty() &&
				 * dataSecAttrs.get(OnboardingConstant.LOCATION) .size() > 0) {
				 * locationList = dataSecAttrs
				 * .get(OnboardingConstant.LOCATION); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.SO)) { sales = key;
				 * if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty() &&
				 * dataSecAttrs.get(OnboardingConstant.SO) .size() > 0) {
				 * salesList = dataSecAttrs.get(OnboardingConstant.SO); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.PO)) { purchase =
				 * key; if (dataSecAttrs.get(OnboardingConstant.PO) != null &&
				 * dataSecAttrs.get(OnboardingConstant.PO) .size() > 0) {
				 * purcList = dataSecAttrs.get(OnboardingConstant.PO); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.DC)) { distChannel =
				 * key; if (dataSecAttrs.get(OnboardingConstant.DC) != null &&
				 * dataSecAttrs.get(OnboardingConstant.DC) .size() > 0) {
				 * distList = dataSecAttrs.get(OnboardingConstant.DC); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD1)) { ud1 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD1) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD1) .size() > 0) {
				 * ud1List = dataSecAttrs.get(OnboardingConstant.UD1); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD2)) { ud2 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD2) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD2) .size() > 0) {
				 * ud2List = dataSecAttrs.get(OnboardingConstant.UD2); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD3)) { ud3 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD3) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD3) .size() > 0) {
				 * ud3List = dataSecAttrs.get(OnboardingConstant.UD3); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD4)) { ud4 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD4) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD4) .size() > 0) {
				 * ud4List = dataSecAttrs.get(OnboardingConstant.UD4); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD5)) { ud5 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD5) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD5) .size() > 0) {
				 * ud5List = dataSecAttrs.get(OnboardingConstant.UD5); } } if
				 * (key.equalsIgnoreCase(OnboardingConstant.UD6)) { ud6 = key;
				 * if (dataSecAttrs.get(OnboardingConstant.UD6) != null &&
				 * dataSecAttrs.get(OnboardingConstant.UD6) .size() > 0) {
				 * ud6List = dataSecAttrs.get(OnboardingConstant.UD6); } }
				 */ }
		}
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append("AND HDR.SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE1 IN :pcList ");

			}
		}

		if (ProfitCenter2 != null && !ProfitCenter2.isEmpty()) {
			if (pc2List != null && pc2List.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE2 IN :pc2List ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("PROFIT_CENTRE2 List is {}", pc2List);
				}

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND ITM.PLANT_CODE IN :plantList ");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("PLANT_CODE List is {}", plantList);
				}

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN :divisionList ");

			}
		}
		/*
		 * if (ud1 != null && !ud1.isEmpty()) { if (ud1List != null &&
		 * ud1List.size() > 0) {
		 * build.append(" AND ITM.USERDEFINED_FIELD1 IN :ud1List "); } } if (ud2
		 * != null && !ud2.isEmpty()) { if (ud2List != null && ud2List.size() >
		 * 0) { build.append(" AND ITM.USERDEFINED_FIELD2 IN :ud2List "); } } if
		 * (ud3 != null && !ud3.isEmpty()) { if (ud3List != null &&
		 * ud3List.size() > 0) {
		 * build.append(" AND ITM.USERDEFINED_FIELD3 IN :ud3List "); } } if (ud4
		 * != null && !ud4.isEmpty()) { if (ud4List != null && ud4List.size() >
		 * 0) { build.append(" AND ITM.USERDEFINED_FIELD4 IN :ud4List "); } } if
		 * (ud5 != null && !ud5.isEmpty()) { if (ud5List != null &&
		 * ud5List.size() > 0) {
		 * build.append(" AND ITM.USERDEFINED_FIELD5 IN :ud5List "); } } if (ud6
		 * != null && !ud6.isEmpty()) { if (ud6List != null && ud6List.size() >
		 * 0) { build.append(" AND ITM.USERDEFINED_FIELD6 IN :ud6List "); } }
		 */
		if (taxPeriod != null) {

			build.append(" AND HDR.QRETURN_PERIOD  = :taxPeriod ");
		}

		String buildQuery = build.toString();

		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For ITC04 ProcessedSummary is {}"
					+ queryStr);
		}

		List<ITC04ProcessSummaryRespDto> finalDtoList = new ArrayList<>();

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (ProfitCenter2 != null && !ProfitCenter2.isEmpty()) {
				if (pc2List != null && !pc2List.isEmpty()
						&& pc2List.size() > 0) {
					q.setParameter("pc2List", pc2List);
				}
			}
			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && !plantList.isEmpty()
						&& plantList.size() > 0) {
					q.setParameter("plantList", plantList);
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && !divisionList.isEmpty()
						&& divisionList.size() > 0) {
					q.setParameter("divisionList", divisionList);
				}
			}
			/*
			 * if (ud1 != null && !ud1.isEmpty()) { if (ud1List != null &&
			 * !ud1List.isEmpty() && ud1List.size() > 0) {
			 * q.setParameter("ud1List", ud1List); } } if (ud2 != null &&
			 * !ud2.isEmpty()) { if (ud2List != null && !ud2List.isEmpty() &&
			 * ud2List.size() > 0) { q.setParameter("ud2List", ud2List); } } if
			 * (ud3 != null && !ud3.isEmpty()) { if (ud3List != null &&
			 * !ud3List.isEmpty() && ud3List.size() > 0) {
			 * q.setParameter("ud3List", ud3List); } } if (ud4 != null &&
			 * !ud4.isEmpty()) { if (ud4List != null && !ud4List.isEmpty() &&
			 * ud4List.size() > 0) { q.setParameter("ud4List", ud4List); } } if
			 * (ud5 != null && !ud5.isEmpty()) { if (ud5List != null &&
			 * !ud5List.isEmpty() && ud5List.size() > 0) {
			 * q.setParameter("ud5List", ud5List); } } if (ud6 != null &&
			 * !ud6.isEmpty()) { if (ud6List != null && !ud6List.isEmpty() &&
			 * ud6List.size() > 0) { q.setParameter("ud6List", ud6List); } }
			 */ if (taxPeriod != null) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ResultList data Converting to Dto----->{}", list);
			}
			List<ITC04ProcessSummaryRespDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			gstr1ProcessedRecordsCommonUtil.fillITC04TheDataFromDataSecAttr(
					retList, gstinList, taxPeriod);

			List<ITC04ProcessSummaryRespDto> gstinDtoList = new ArrayList<ITC04ProcessSummaryRespDto>();
			List<String> combinedGstinList = new ArrayList<>();
			if (gstin != null && !gstin.isEmpty() && gstinList != null
					&& gstinList.size() > 0) {
				combinedGstinList.addAll(gstinList);
			}
			if (!combinedGstinList.isEmpty() && combinedGstinList.size() > 0) {
				for (ITC04ProcessSummaryRespDto processedDto : retList) {
					if (combinedGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
					}
				}
				List<ITC04ProcessSummaryRespDto> sortedGstinDtoList = gstinDtoList
						.stream()
						.sorted(Comparator.comparing(
								ITC04ProcessSummaryRespDto::getGstin))
						.collect(Collectors.toList());

				return gstr1ProcessedRecordsCommonUtil
						.convertItc04CalcuDataToResp(sortedGstinDtoList);
			}

			List<ITC04ProcessSummaryRespDto> sortedGstinDtoList = retList
					.stream()
					.sorted(Comparator
							.comparing(ITC04ProcessSummaryRespDto::getGstin))
					.collect(Collectors.toList());
			finalDtoList.addAll(sortedGstinDtoList);
			LOGGER.debug("Final list from dao is ->" + finalDtoList);

			// LOGGER.debug("After Execution getting the data ----->" +
			// retList);
			// return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Group Code is ->{}", TenantContext.getTenantId());
			LOGGER.error("ITC04 Processed Screen issue : {}", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
		return gstr1ProcessedRecordsCommonUtil
				.convertItc04CalcuDataToResp(finalDtoList);
	}

	private ITC04ProcessSummaryRespDto convert(Object[] arr) {
		// ITC04ProcessSummaryRespDto obj = new ITC04ProcessSummaryRespDto();
		LOGGER.debug("Array data Setting to Dto");
		ITC04ProcessSummaryRespDto dto = new ITC04ProcessSummaryRespDto();
		if (arr.length > 0) {

			String gstin = String.valueOf(arr[0]);

			dto.setGstin(gstin);
			dto.setTaxPeriod(String.valueOf(arr[1]));
			/*
			 * int totalCount, savedCount, errorCount, notSentCount,
			 * notSavedCount = 0; notSentCount = ((BigInteger)
			 * arr[2]).intValue(); savedCount = ((BigInteger)
			 * arr[3]).intValue(); notSavedCount = ((BigInteger)
			 * arr[4]).intValue(); errorCount = ((BigInteger)
			 * arr[5]).intValue(); totalCount = ((BigInteger)
			 * arr[6]).intValue(); dto.setTotalCount(totalCount);
			 * dto.setNotSavedCount(notSavedCount);
			 * dto.setNotSentCount(notSentCount); dto.setSavedCount(savedCount);
			 * dto.setErrorCount(errorCount);
			 * 
			 * if (arr[7] == null || arr[7] == "null") { dto.setTimeStamp(""); }
			 * else { Timestamp date = (Timestamp) arr[7]; LocalDateTime dt =
			 * date.toLocalDateTime(); LocalDateTime dateTimeFormatter =
			 * EYDateUtil .toISTDateTimeFromUTC(dt); DateTimeFormatter FOMATTER
			 * = DateTimeFormatter .ofPattern("dd-MM-yyyy : HH:mm:ss"); String
			 * newdate = FOMATTER.format(dateTimeFormatter);
			 * 
			 * dto.setTimeStamp(newdate); }
			 */

			dto.setGsCount((GenUtil.getBigInteger(arr[8])).intValue());
			dto.setGsQuantity((BigDecimal) (arr[9]));
			dto.setGsTaxableValue((BigDecimal) arr[10]);
			dto.setGrCount((GenUtil.getBigInteger(arr[11])).intValue());
			dto.setGrQuantityRece(((BigDecimal) arr[12]).toBigInteger());
			dto.setGrQuantityLoss(((BigDecimal) arr[13]).toBigInteger());
			String gstintoken = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (gstintoken != null) {
				if ("A".equalsIgnoreCase(gstintoken)) {
					dto.setAuthToken("Active");
				} else {
					dto.setAuthToken("Inactive");
				}
			} else {
				dto.setAuthToken("Inactive");
			}

			LOGGER.error("Group Code before calling RegType Method -------->{}",
					TenantContext.getTenantId());
			/*
			 * List<String> regName = gSTNDetailRepository
			 * .findRegTypeByGstin(gstin);
			 */

			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			dto.setState(stateName);

		}

		return dto;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT GSTIN, QRETURN_PERIOD,"
				+ "SUM(GSTN_NOT_SENT_COUNT)GSTN_NOT_SENT_COUNT,"
				+ "SUM(GSTN_SAVED_COUNT)GSTN_SAVED_COUNT,"
				+ "SUM(GSTN_NOT_SAVED_COUNT)GSTN_NOT_SAVED_COUNT,"
				+ "SUM(GSTN_ERROR_COUNT)GSTN_ERROR_COUNT,"
				+ "SUM(TOTAL_COUNT_IN_ASP)TOTAL_COUNT_IN_ASP,MAX(LAST_UPDATED)LAST_UPDATED,"
				+ "SUM(SENT_COUNT)SENT_COUNT,SUM(SENT_QTY)SENT_QTY,"
				+ "SUM(SENT_TAXABLE_VALUE)SENT_TAXABLE_VALUE, SUM(REC_SOLD_COUNT)REC_SOLD_COUNT,"
				+ "SUM(REC_SOLD_QTY)REC_SOLD_QTY,SUM( REC_SOLD_LOSS_QTY)REC_SOLD_LOSS_QTY FROM "
				+ "( SELECT GSTIN,RETURN_PERIOD,QRETURN_PERIOD,"
				+ "GSTN_NOT_SENT_COUNT,GSTN_SAVED_COUNT,GSTN_NOT_SAVED_COUNT,"
				+ "GSTN_ERROR_COUNT,TOTAL_COUNT_IN_ASP,LAST_UPDATED,"
				+ "SENT_COUNT,SENT_QTY,SENT_TAXABLE_VALUE,0 REC_SOLD_COUNT,"
				+ "0 REC_SOLD_QTY,0 REC_SOLD_LOSS_QTY FROM ( "
				+ "SELECT SUPPLIER_GSTIN AS GSTIN, HDR.RETURN_PERIOD, HDR.QRETURN_PERIOD,"
				+ "COUNT (DISTINCT DOC_KEY )AS SENT_COUNT,"
				+ "IFNULL(SUM(ITM.ITM_QTY) ,0) SENT_QTY,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0)   AS SENT_TAXABLE_VALUE,"
				+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,"
				+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,"
				+ "COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP,"
				+ "MAX(HDR.MODIFIED_ON) LAST_UPDATED FROM ITC04_HEADER HDR "
				+ "INNER JOIN ITC04_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ "WHERE IS_DELETE=FALSE AND TABLE_NUMBER='4'and IS_PROCESSED = TRUE "
				+ "AND (ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') "
				+ buildQuery
				+ "GROUP BY SUPPLIER_GSTIN,HDR.RETURN_PERIOD, HDR.QRETURN_PERIOD) "
				+ "UNION ALL " + "SELECT GSTIN,RETURN_PERIOD,QRETURN_PERIOD,"
				+ "GSTN_NOT_SENT_COUNT,GSTN_SAVED_COUNT,GSTN_NOT_SAVED_COUNT,"
				+ "GSTN_ERROR_COUNT,TOTAL_COUNT_IN_ASP,LAST_UPDATED,"
				+ "0 SENT_COUNT,0 SENT_QTY,0 SENT_TAXABLE_VALUE,"
				+ "REC_SOLD_COUNT,REC_SOLD_QTY, REC_SOLD_LOSS_QTY FROM ( "
				+ "SELECT SUPPLIER_GSTIN AS GSTIN,HDR.RETURN_PERIOD,HDR.QRETURN_PERIOD,"
				+ "TABLE_NUMBER,0 SENT_COUNT,0 AS SENT_QTY,"
				+ "0 AS SENT_TAXABLE_VALUE,"
				+ "COUNT (DISTINCT DOC_KEY) AS REC_SOLD_COUNT,"
				+ "IFNULL(SUM(ITM.ITM_QTY ),0) AS REC_SOLD_QTY,"
				+ "IFNULL(SUM(ITM.LOSSES_QTY ),0) AS REC_SOLD_LOSS_QTY,"
				+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,"
				+ "COUNT(CASE WHEN GSTN_ERROR = TRUE "
				+ "AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,"
				+ "COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP,"
				+ "MAX(HDR.MODIFIED_ON) LAST_UPDATED "
				+ "FROM ITC04_HEADER HDR " + "INNER JOIN ITC04_ITEM ITM "
				+ "ON HDR.ID=ITM.DOC_HEADER_ID "
				+ "WHERE IS_DELETE=FALSE and IS_PROCESSED=TRUE AND TABLE_NUMBER IN ('5A','5B','5C') AND "
				+ "(ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') " + buildQuery
				+ "GROUP BY SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.QRETURN_PERIOD,TABLE_NUMBER)) "
				+ "GROUP BY GSTIN, QRETURN_PERIOD";

		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		// return queryStr;
		return queryStr;

	}
}
