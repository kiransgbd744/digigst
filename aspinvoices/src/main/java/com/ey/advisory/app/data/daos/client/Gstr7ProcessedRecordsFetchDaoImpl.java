package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.gstr1.Gstr7ProcessedRecordsRespDto;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7ProcessedRecordsFetchDaoImpl")
public class Gstr7ProcessedRecordsFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GstnSubmitRepository")
	private GstnSubmitRepository gstnSubmitRepository;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	public List<Gstr7ProcessedRecordsRespDto> loadGstr7ProcessedRecords(
			Gstr1ProcessedRecordsReqDto req) {

		int taxPeriod = 0;
		String taxPeriodReq = req.getRetunPeriod();
		if (!Strings.isNullOrEmpty(taxPeriodReq)) {
			taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		}
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("dataSecAttrs {} ", dataSecAttrs);
		}


		String profitCenter = null;
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
		String returnType = req.getReturnType();
		String queryStr = "";

		if (returnType == null) {
			returnType = "GSTR7";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ReturnType -->" + returnType);
		}
		
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
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
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}

			}
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GstinList -->" + gstinList);
			LOGGER.debug("TaxPeriod -->" + taxPeriod);
		}
		
		StringBuilder build = new StringBuilder();
		
		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					build.append(" AND DEDUCTOR_GSTIN IN :gstinList ");
				}
			}
			if (profitCenter != null && !profitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					build.append(" AND PROFIT_CENTRE1 IN :pcList ");

				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					build.append(" AND DIVISION IN :divisionList ");

				}
			}
			if (plant != null && !plant.isEmpty() && plantList != null
					&& plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList ");
			}

			if (taxPeriod != 0) {

				build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");

			}
		} else {

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					build.append(" AND TDS_DEDUCTOR_GSTIN IN :gstinList ");
	
				}
			}
			if (profitCenter != null && !profitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					build.append(" AND PROFIT_CENTRE1 IN :pcList ");
	
				}
			}
	
			if (purchase != null && !purchase.isEmpty()) {
				if (purcList != null && purcList.size() > 0) {
					build.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
	
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && distList.size() > 0) {
	
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					build.append(" AND DIVISION IN :divisionList ");
	
				}
			}
	
			if (taxPeriod != 0) {
	
				build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
	
			}
		}

		String buildQuery = build.toString();

		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {
			queryStr = createGSTR7TransQueryString(buildQuery);
		} else {
			queryStr = createQueryString(buildQuery);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);
			
			if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {

				if (gstin != null && !gstin.isEmpty()) {
					if (gstinList != null && !gstinList.isEmpty()
							&& gstinList.size() > 0) {
						q.setParameter("gstinList", gstinList);
					}
				}

				if (plant != null && !plant.isEmpty() && !plant.isEmpty()
						&& plantList != null && plantList.size() > 0) {
					q.setParameter("plantList", plantList);
				}

				if (profitCenter != null && !profitCenter.isEmpty()) {
					if (pcList != null && !pcList.isEmpty()
							&& pcList.size() > 0) {
						q.setParameter("pcList", pcList);
					}
				}
				if (division != null && !division.isEmpty()) {
					if (divisionList != null && !divisionList.isEmpty()
							&& divisionList.size() > 0) {
						q.setParameter("divisionList", divisionList);
					}
				}

				if (taxPeriod != 0) {
					q.setParameter("taxPeriod", taxPeriod);
				}
			} else {

				if (profitCenter != null && !profitCenter.isEmpty()) {
					if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
						q.setParameter("pcList", pcList);
					}
				}
	
				if (gstin != null && !gstin.isEmpty()) {
					if (gstinList != null && !gstinList.isEmpty()
							&& gstinList.size() > 0) {
						q.setParameter("gstinList", gstinList);
					}
				}
				if (division != null && !division.isEmpty()) {
					if (divisionList != null && !divisionList.isEmpty()
							&& divisionList.size() > 0) {
						q.setParameter("divisionList", divisionList);
					}
				}
	
				if (purchase != null && !purchase.isEmpty()) {
					if (purcList != null && !purcList.isEmpty()
							&& purcList.size() > 0) {
						q.setParameter("purcList", purcList);
					}
				}
	
				if (taxPeriod != 0) {
					q.setParameter("taxPeriod", taxPeriod);
				}
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();

			statecodeRepository = StaticContextHolder.getBean(
					"StatecodeRepositoryMaster", StatecodeRepository.class);

			List<Gstr7ProcessedRecordsRespDto> retList = list.stream()
					.map(o -> Conertingtodto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			fillTheDataFromDataSecAttr(retList, gstinList, taxPeriod);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	public Gstr7ProcessedRecordsRespDto Conertingtodto(Object[] obj) {

		Gstr7ProcessedRecordsRespDto dto = new Gstr7ProcessedRecordsRespDto();

		String gstin = (String) obj[0];
		dto.setGstin(gstin);
		String retPer = (String) obj[1];
		dto.setReturnPerid(retPer);
		// dto.setCount((BigInteger)obj[2]);

		dto.setNotSentCount(Integer.parseInt(obj[2].toString()));
		dto.setSavedCount(Integer.parseInt(obj[3].toString()));
		dto.setNotSavedCount(Integer.parseInt(obj[4].toString()));
		dto.setErrorCount(Integer.parseInt(obj[5].toString()));
		dto.setTotalCountAsp(Integer.parseInt(obj[6].toString()));
		dto.setCount(GenUtil.getBigInteger(obj[7]));
		dto.setTotalAmount((BigDecimal) obj[8]);
		dto.setIgst((BigDecimal) obj[9]);
		dto.setCgst((BigDecimal) obj[10]);
		dto.setSgst((BigDecimal) obj[11]);

		String stateCode = obj[0].toString().substring(0, 2);
		String stateName = statecodeRepository.findStateNameByCode(stateCode);
		dto.setState(stateName);
		dto.setFileDateTime(LocalDate.now().toString());
		dto.setSaveDateTime(LocalDate.now().toString());

		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin((String) obj[0]);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				dto.setAuthToken("Active");
			} else {
				dto.setAuthToken("Inactive");
			}
		} else {
			dto.setAuthToken("Inactive");
		}
		// dto.setSaveStatus(
		// deriveStatusByTotSavedErrorCount(dto.getTotalCountAsp(),
		// dto.getSavedCount(), dto.getErrorCount(),
		// dto.getNotSentCount(), dto.getNotSavedCount()));

		List<String> findStatusP = gstnSubmitRepository
				.findStatusPByGstinAndRetPeriodAndRetrunType(gstin, retPer,
						APIConstants.GSTR7.toUpperCase());
		Pair<String, LocalDate> status = null;
		if (!findStatusP.isEmpty()) {
			dto.setSaveStatus("SUBMITTED");
		} else {
			status = deriveStatusByTotSavedErrorCount(dto.getTotalCountAsp(),
					dto.getSavedCount(), dto.getErrorCount(),
					dto.getNotSentCount(), gstin, retPer, APIConstants.GSTR7,
					TenantContext.getTenantId());
			dto.setSaveStatus(status.getValue0());
		}

		if (obj[12] == null || obj[12] == "null") {
			dto.setSaveDateTime("");
		} else {
			if (status.getValue0().equals("FILED")) {
				dto.setSaveDateTime(EYDateUtil.fmtLocalDate(status.getValue1()));
			} else {
				Timestamp date = (Timestamp) obj[12];
				LocalDateTime dt = date.toLocalDateTime();
				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(dt);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy : HH:mm:ss");
				String newdate = FOMATTER.format(dateTimeFormatter);
				dto.setSaveDateTime(newdate);
			}
		}
		return dto;
	}

	private String createQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT GSTIN,RETURN_PERIOD,"
				+ "SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
				+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT,"
				+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
				+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
				+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP,"
				+ "COUNT(DISTINCT KEY) COUNT,"
				+ "SUM(TOTAL_AMT)TOTAL_AMT,SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
				+ "SUM(SGST_AMT) SGST_AMT ,MAX(MODIFIED_ON) MODIFIED_ON FROM "
				+ "( SELECT TDS_DEDUCTOR_GSTIN AS GSTIN, RETURN_PERIOD, "
				+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,"
				+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND IS_DELETE = FALSE "
				+ "THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,"
				+ "COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) "
				+ "TOTAL_COUNT_IN_ASP, (TDS_DEDUCTOR_GSTIN||'-'||RETURN_PERIOD||'-'||NEW_TDS_DEDUCTEE_GSTIN) "
				+ "AS KEY, IFNULL(SUM(NEW_GROSS_AMT),0) AS TOTAL_AMT,"
				+ " IFNULL(SUM(IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(CGST_AMT),0) "
				+ "AS CGST_AMT, IFNULL(SUM(SGST_AMT),0) AS SGST_AMT, "
				+ "MAX(MODIFIED_ON) MODIFIED_ON FROM GSTR7_PROCESSED_TDS WHERE "
				+ "IS_DELETE = FALSE AND TABLE_NUM='Table-3' AND ACTION_TYPE IS NULL"
				+ buildQuery + " GROUP BY "
				+ "TDS_DEDUCTOR_GSTIN,RETURN_PERIOD,NEW_TDS_DEDUCTEE_GSTIN ) "
				+ "GROUP BY GSTIN,RETURN_PERIOD ";
		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		return queryStr;
	}

	public void fillTheDataFromDataSecAttr(
			List<Gstr7ProcessedRecordsRespDto> retList, List<String> gstinList,
			int taxPeriod) {
		List<String> dataGstinList = new ArrayList<>();
		retList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstin : gstinList) {
				if (!dataGstinList.contains(gstin)) {
					Gstr7ProcessedRecordsRespDto dummy = new Gstr7ProcessedRecordsRespDto();

					dummy.setGstin(gstin);
					String status = "NOT INITIATED";
					dummy.setCount(new BigInteger("0"));
					dummy.setIgst(new BigDecimal("0.0"));
					dummy.setCgst(new BigDecimal("0.0"));
					dummy.setSgst(new BigDecimal("0.0"));
					dummy.setTotalAmount(new BigDecimal("0.0"));
					String gstintoken = defaultGSTNAuthTokenService
							.getAuthTokenStatusForGstin(gstin);
					if (gstintoken != null) {
						if ("A".equalsIgnoreCase(gstintoken)) {
							dummy.setAuthToken("Active");
						} else {
							dummy.setAuthToken("Inactive");
						}
					} else {
						dummy.setAuthToken("Inactive");
					}
					String stateCode = gstin.substring(0, 2);

					String stateName = statecodeRepository
							.findStateNameByCode(stateCode);
					dummy.setState(stateName);
					dummy.setSaveStatus(status);
					dataGstinList.add(gstin);
					retList.add(dummy);
				}
			}
		}

	}

	private Pair<String, LocalDate> deriveStatusByTotSavedErrorCount(
			int totalCount, int savedCount, int errorCount, int notSentCount,
			String gstin, String taxPeriod, String returnType,
			String groupCode) {

		if (gstin != null && taxPeriod != null && returnType != null
				&& groupCode != null) {

			GstrReturnStatusEntity signedStatusP = returnstatusRepo
					.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
							gstin, taxPeriod, returnType.toUpperCase());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Return Status Entry:: ",
						signedStatusP.toString());
			}
			if (signedStatusP != null
					&& "FILED".equalsIgnoreCase(signedStatusP.getStatus())) {
				return new Pair<String, LocalDate>("FILED",
						signedStatusP.getFilingDate());

			}
			List<String> submittedStatusP = gstnSubmitRepository
					.findStatusPByGstinAndRetPeriodAndRetrunType(gstin,
							taxPeriod, returnType.toUpperCase());
			if (!submittedStatusP.isEmpty()) {
				return new Pair<String, LocalDate>("SUBMITTED", null);
			}

		}

		if (totalCount != 0) {
			if (totalCount == notSentCount) {
				return new Pair<String, LocalDate>("NOT INITIATED", null);
			} else if (totalCount == savedCount) {
				return new Pair<String, LocalDate>("SAVED", null);
			} else if (totalCount == errorCount) {
				return new Pair<String, LocalDate>("FAILED", null);
			} else {
				return new Pair<String, LocalDate>("PARTIALLY SAVED", null);
			}
		} else {
			return new Pair<String, LocalDate>("NOT INITIATED", null);
		}
	}
	
	private String createGSTR7TransQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT GSTIN, " + "RETURN_PERIOD, "
				+ "SUM(GSTN_NOT_SENT_COUNT)  GSTN_NOT_SENT_COUNT, "
				+ "SUM(GSTN_SAVED_COUNT)     GSTN_SAVED_COUNT, "
				+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT, "
				+ "SUM(GSTN_ERROR_COUNT)     GSTN_ERROR_COUNT, "
				+ "SUM(TOTAL_COUNT_IN_ASP)   TOTAL_COUNT_IN_ASP, "
				+ "COUNT(DISTINCT KEY)       COUNT, "
				+ "SUM(TOTAL_AMT)            TOTAL_AMT, "
				+ "SUM(IGST_AMT)             IGST_AMT, "
				+ "SUM(CGST_AMT)             CGST_AMT, "
				+ "SUM(SGST_AMT)             SGST_AMT, "
				+ "MAX(MODIFIED_ON)          MODIFIED_ON " + "FROM ( "
				+ "SELECT DEDUCTOR_GSTIN AS GSTIN, " + "RETURN_PERIOD, "
				+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT, "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT, "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT, "
				+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT, "
				+ "COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP, "
				+ " DOC_KEY AS KEY, "
				+ "IFNULL(SUM(TAXABLE_VALUE), 0) AS TOTAL_AMT, "
				+ "IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ "IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ "IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ "MAX(MODIFIED_ON) AS MODIFIED_ON "
				+ "FROM GSTR7_TRANS_DOC_HEADER TDS "
				+ "WHERE IS_DELETE = FALSE " + "AND SECTION = 'TDS' "
				+ "AND SUPPLY_TYPE = 'TAX' " + buildQuery
				+ "GROUP BY DEDUCTOR_GSTIN, RETURN_PERIOD, DOC_KEY "
				+ "UNION ALL " + "SELECT DEDUCTOR_GSTIN AS GSTIN, "
				+ "RETURN_PERIOD, "
				+ "COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT, "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT, "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT, "
				+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT, "
				+ "COUNT(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP, "
				+ " DOC_KEY AS KEY, "
				+ "IFNULL(SUM(TAXABLE_VALUE), 0) AS TOTAL_AMT, "
				+ "IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ "IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ "IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ "MAX(MODIFIED_ON) AS MODIFIED_ON "
				+ "FROM GSTR7_TRANS_DOC_HEADER TDS "
				+ "WHERE IS_DELETE = FALSE " + "AND SECTION = 'TDSA' "
				+ "AND SUPPLY_TYPE = 'TAX' " + buildQuery
				+ "GROUP BY DEDUCTOR_GSTIN, RETURN_PERIOD, DOC_KEY " + ") "
				+ "GROUP BY GSTIN, RETURN_PERIOD";

		LOGGER.debug("Outward FROM B2B TO EXPA Query Execution END ");
		return queryStr;
	}
	
	public static void main(String[] args) {

		String ProfitCenter = "XYZ";
		String plant = "XYZ";
		;
		String sales = "XYZ";
		;
		String division = "XYZ";
		;
		String location = "XYZ";
		;
		String purchase = "XYZ";
		;
		String distChannel = "XYZ";
		;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = "XYZ";

		List<String> pcList = Arrays.asList("asd");
		;
		List<String> plantList = Arrays.asList("asd");
		List<String> divisionList = Arrays.asList("asd");
		;
		List<String> locationList = Arrays.asList("asd");
		;
		List<String> salesList = Arrays.asList("asd");
		;
		List<String> purcList = Arrays.asList("asd");
		;
		List<String> distList = Arrays.asList("asd");
		;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = Arrays.asList("asd");
		String returnType = "GSTR7_TRANSACTIONAL";

		int taxPeriod = 202504;

		StringBuilder build = new StringBuilder();

		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					build.append(" AND DEDUCTOR_GSTIN IN :gstinList ");

				}
			}

			build.append(" AND RETURN_PERIOD = :taxPeriod ");
		} else {

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					build.append(" AND TDS_DEDUCTOR_GSTIN IN :gstinList ");

				}
			}
			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					build.append(" AND PROFIT_CENTRE1 IN :pcList ");

				}
			}

			if (purchase != null && !purchase.isEmpty()) {
				if (purcList != null && purcList.size() > 0) {
					build.append(" AND PURCHASE_ORGANIZATION IN :purcList ");

				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && distList.size() > 0) {

				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					build.append(" AND DIVISION IN :divisionList ");

				}
			}

			if (taxPeriod != 0) {

				build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");

			}
		}

		String buildQuery = build.toString();

		String queryStr = "";

		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {
			queryStr = new Gstr7ProcessedRecordsFetchDaoImpl()
					.createGSTR7TransQueryString(buildQuery);
		} else {
			queryStr = new Gstr7ProcessedRecordsFetchDaoImpl()
					.createQueryString(buildQuery);
		}

		System.out.println(queryStr);
	}
}
