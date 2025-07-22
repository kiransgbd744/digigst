package com.ey.advisory.app.services.daos.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
import com.ey.advisory.app.docs.dto.GetGstr2aDetailStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author SriBhavya
 *
 */
@Component("Gstr6AProcessedDataDaoImpl")
public class Gstr6AProcessedDataDaoImpl implements Gstr6AProcessedDataDao {

	private static final String NOT_INITIATED = "NOT_INITIATED";
	private static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";
	private static final String INPROGRESS = "INPROGRESS";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	private static final String INITIATED = "INITIATED";
	private static final String PARTIALLY_SUCCESS = "PARTIALLY_SUCCESS";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GetGstr6aDetailStatusService")
	private GetGstr6aDetailStatusService getGstr6aDetailStatusService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Override
	public List<Gstr6AProcessedDataResponseDto> getGstr6AProcessedData(
			Gstr6AProcessedDataRequestDto criteria) throws Exception {
		String taxperiod = criteria.getTaxPeriod();
		String fromPeriod = criteria.getFromPeriod();
		String toPeriod = criteria.getToPeriod();
		List<String> tableSection = criteria.getTableType();
		if (!(tableSection.isEmpty())) {
			tableSection.replaceAll(String::toUpperCase);
		}
		List<String> docType = criteria.getDocType();
		if (docType.contains("CR")) {
			docType.remove("CR");
			docType.add("C");
		}
		if (docType.contains("DR")) {
			docType.remove("DR");
			docType.add("D");
		}
		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();

		String GSTIN = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = gstnDetailRepository.getGstinRegTypeISD(
								dataSecAttrs.get(OnboardingConstant.GSTIN));
					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			buildQuery.append(" AND CTIN IN :gstinList ");
		}
		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			buildQuery.append(
					" AND DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod ");
		}
		if (tableSection != null && !tableSection.isEmpty()) {
			buildQuery1.append(" WHERE TABLE_SECTION IN :tableSection ");
		}
		if (docType != null && !docType.isEmpty()) {
			if (tableSection != null && !tableSection.isEmpty()) {
				buildQuery1.append(" AND DOC_TYPE IN :docType ");
			} else {
				buildQuery1.append(" WHERE DOC_TYPE IN :docType ");
			}
		}

		String queryStr = createQueryString(buildQuery, buildQuery1);
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}

		if (tableSection != null && !tableSection.isEmpty()) {
			q.setParameter("tableSection", tableSection);
		}
		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}

		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			q.setParameter("fromPeriod",
					GenUtil.convertTaxPeriodToInt(fromPeriod));
			q.setParameter("toPeriod", GenUtil.convertTaxPeriodToInt(toPeriod));
		}

		List<Object[]> list = q.getResultList();
		List<Gstr6AProcessedDataResponseDto> processedDataList = convertGetProcessedData(
				list, criteria, gstinList);

		convertDummyProcessedData(processedDataList, gstinList, taxperiod,
				criteria);

		return processedDataList;
	}

	private void convertDummyProcessedData(
			List<Gstr6AProcessedDataResponseDto> processedDataList,
			List<String> gstinList, String taxperiod,
			Gstr6AProcessedDataRequestDto gstr6aPRReqDto) throws Exception {
		List<String> dataGstinList = new ArrayList<>();
		Map<String, String> gstinsStatusMap = getStatusByCriteria(
				gstr6aPRReqDto, gstinList);
		processedDataList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		for (String gstin : gstinList) {
			// int count = checkTheCountByGstinTaxperiod(gstin, taxPeriod);
			if (!dataGstinList.contains(gstin)) {
				Gstr6AProcessedDataResponseDto dummy = new Gstr6AProcessedDataResponseDto();
				dummy.setGstin(gstin);
				String stateCode = gstin.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dummy.setState(stateName);
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dummy.setAuthToken(APIConstants.ACTIVE);
					} else {
						dummy.setAuthToken(APIConstants.IN_ACTIVE);
					}
				} else {
					dummy.setAuthToken(APIConstants.IN_ACTIVE);
				}
				dummy.setGstrStatus("");

				if (gstinsStatusMap.containsKey(gstin)) {
					String value[] = gstinsStatusMap.get(gstin).split("__");
					if (value[0] != null) {
						dummy.setStatus(value[0]);
					}
					if (value[1] != null) {
						dummy.setTimeStamp(value[1]);
					}
				} else {
					dummy.setStatus(NOT_INITIATED);
					dummy.setTimeStamp("");
				}
				/*
				 * String status = "NOT INITIATED"; dummy.setStatus(status);
				 * LocalDateTime now =
				 * EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				 * dummy.setTimeStamp(EYDateUtil.toISTDateTimeFromUTC(now));
				 */
				if (taxperiod != null && !taxperiod.toString().isEmpty()) {
					dummy.setRetPeriod(taxperiod);
				}
				dummy.setCount(new BigInteger("0"));
				dummy.setTaxableValue(new BigDecimal("0.0"));
				dummy.setTotalTax(new BigDecimal("0.0"));
				dummy.setIgst(new BigDecimal("0.0"));
				dummy.setCgst(new BigDecimal("0.0"));
				dummy.setSgst(new BigDecimal("0.0"));
				dummy.setCess(new BigDecimal("0.0"));
				dummy.setInVoiceVal(new BigDecimal("0.0"));

				dataGstinList.add(gstin);
				processedDataList.add(dummy);
			}
		}

	}

	private List<Gstr6AProcessedDataResponseDto> convertGetProcessedData(
			List<Object[]> outDataArray,
			Gstr6AProcessedDataRequestDto gstr6aPRReqDto,
			List<String> gstinList) throws Exception {
		List<Gstr6AProcessedDataResponseDto> responseList = new ArrayList<Gstr6AProcessedDataResponseDto>();
		if (!outDataArray.isEmpty()) {
			Map<String, String> gstinsStatusMap = getStatusByCriteria(
					gstr6aPRReqDto, gstinList);
			for (Object list[] : outDataArray) {
				Gstr6AProcessedDataResponseDto responseCriteria = new Gstr6AProcessedDataResponseDto();
				String GSTIN = (String) list[0];
				responseCriteria.setGstin(GSTIN);
				responseCriteria.setRegType("ISD");
				if (gstinsStatusMap.containsKey(GSTIN)) {
					String value[] = gstinsStatusMap.get(GSTIN).split("__");
					if (value[0] != null) {
						responseCriteria.setStatus(value[0]);
					}
					if (value[1] != null) {
						responseCriteria.setTimeStamp(value[1]);
					}
				} else {
					responseCriteria.setStatus(NOT_INITIATED);
					responseCriteria.setTimeStamp("");
				}
				String stateCode = GSTIN.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				responseCriteria.setState(stateName);
				// responseCriteria.setState((String) list[2]);
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(GSTIN);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						responseCriteria.setAuthToken(APIConstants.ACTIVE);
					} else {
						responseCriteria.setAuthToken(APIConstants.IN_ACTIVE);
					}
				} else {
					responseCriteria.setAuthToken(APIConstants.IN_ACTIVE);
				}
				responseCriteria.setGstrStatus("");
				// responseCriteria.setStatus(APIConstants.SUCCESS);
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				// responseCriteria.setTimeStamp(EYDateUtil.toISTDateTimeFromUTC(now));

				BigDecimal decVal = BigDecimal.ZERO;
				if (list[2] == null || list[2].toString().isEmpty()) {
					responseCriteria.setCount(BigInteger.ZERO);
				} else {
					responseCriteria.setCount(GenUtil.getBigInteger(list[2]));
				}
				if (list[3] == null || list[3].toString().isEmpty()) {
					responseCriteria.setTaxableValue(decVal);
				} else {
					responseCriteria.setTaxableValue((BigDecimal) list[3]);
				}
				if (list[4] == null || list[4].toString().isEmpty()) {
					responseCriteria.setTotalTax(decVal);
				} else {
					responseCriteria.setTotalTax((BigDecimal) list[4]);
				}
				if (list[5] == null || list[5].toString().isEmpty()) {
					responseCriteria.setIgst(decVal);
				} else {
					responseCriteria.setIgst((BigDecimal) list[5]);
				}
				if (list[6] == null || list[6].toString().isEmpty()) {
					responseCriteria.setCgst(decVal);
				} else {
					responseCriteria.setCgst((BigDecimal) list[6]);
				}
				if (list[7] == null || list[7].toString().isEmpty()) {
					responseCriteria.setSgst(decVal);
				} else {
					responseCriteria.setSgst((BigDecimal) list[7]);
				}
				if (list[8] == null || list[8].toString().isEmpty()) {
					responseCriteria.setCess(decVal);
				} else {
					responseCriteria.setCess((BigDecimal) list[8]);
				}
				if (list[9] == null || list[9].toString().isEmpty()) {
					responseCriteria.setInVoiceVal(decVal);
				} else {
					responseCriteria.setInVoiceVal((BigDecimal) list[9]);
				}
				responseList.add(responseCriteria);
			}
		}
		return responseList;
	}

	private static boolean isStringOnlyAlphabet(String str) {
		return ((str != null) && (!str.equals("")) && (!str.equals("-"))
				&& (str.matches("^[a-zA-Z]*$")));
	}

	private Map<String, String> getStatusByCriteria(
			Gstr6AProcessedDataRequestDto gstr6aPRReqDto,
			List<String> gstinList) throws Exception {
		Map<String, String> gstinStatusMap = Maps.newHashMap();
		GetAnx2DetailStatusReqDto criteria = new GetAnx2DetailStatusReqDto();
		criteria.setEntityId(String.valueOf(
				gstr6aPRReqDto.getEntityId().stream().findFirst().get()));
		criteria.setGstin(gstinList);
		criteria.setFromPeriod(gstr6aPRReqDto.getFromPeriod());
		criteria.setToPeriod(gstr6aPRReqDto.getToPeriod());
		// criteria.setTaxPeriod(gstr6aPRReqDto.getTaxPeriod());
		List<GetGstr2aDetailStatusRespDto> dtos = getGstr6aDetailStatusService
				.findByCriteria(criteria);

		Map<String, Set<String>> gstinsMap = Maps.newHashMap();
		Map<String, List<String>> timestampMap = Maps.newHashMap();
		dtos.forEach(dto -> {
			Set<String> statusList = Sets.newHashSet();
			statusList.add(
					dto.getB2bStatus() != null ? dto.getB2bStatus() : null);
			statusList.add(
					dto.getB2baStatus() != null ? dto.getB2baStatus() : null);
			statusList.add(
					dto.getCdnStatus() != null ? dto.getCdnStatus() : null);
			statusList.add(
					dto.getCdnaStatus() != null ? dto.getCdnaStatus() : null);

			List<String> timestampList = Lists.newArrayList();
			timestampList.add(dto.getB2bTimeStamp() != null
					? dto.getB2bTimeStamp() : null);
			timestampList.add(dto.getB2baTimeStamp() != null
					? dto.getB2baTimeStamp() : null);
			timestampList.add(dto.getCdnTimeStamp() != null
					? dto.getCdnTimeStamp() : null);
			timestampList.add(dto.getCdnaTimeStamp() != null
					? dto.getCdnaTimeStamp() : null);

			timestampMap.put(dto.getGstin(),
					timestampList.stream().filter(
							str -> (str != null && !str.trim().equals("-")))
							.collect(Collectors.toList()));
			gstinsMap.put(dto.getGstin(), statusList);
		});

		gstinsMap.keySet().forEach(gstin -> {
			String finalStatus = NOT_INITIATED;
			Set<String> statusList = gstinsMap.get(gstin);
			List<String> unqueSatusList = statusList.stream()
					.filter(status -> status != null)
					.collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(unqueSatusList)) {
				if (unqueSatusList.contains(INPROGRESS)) {
					finalStatus = INPROGRESS;
				} else if (unqueSatusList.contains(INITIATED)) {
					finalStatus = INITIATED;
				} else if (unqueSatusList.contains(FAILED)
						&& (unqueSatusList.contains(SUCCESS) || unqueSatusList
								.contains(SUCCESS_WITH_NO_DATA))) {
					finalStatus = PARTIALLY_SUCCESS;
				} else if (unqueSatusList.contains(FAILED)) {
					finalStatus = FAILED;
				} else if (unqueSatusList.contains(SUCCESS)
						|| unqueSatusList.contains(SUCCESS_WITH_NO_DATA)) {
					finalStatus = SUCCESS;
				}
			}

			gstinStatusMap.put(gstin, finalStatus);
		});

		return updateTimeStampOnExisitngGstin(timestampMap, gstinStatusMap);
	}

	private Map<String, String> updateTimeStampOnExisitngGstin(
			Map<String, List<String>> timestampMap,
			Map<String, String> gstinStatusMap) {
		Map<String, String> finalMap = Maps.newHashMap();
		gstinStatusMap.keySet().forEach(gstin -> {
			List<String> timestampList = timestampMap.get(gstin);
			finalMap.put(gstin, gstinStatusMap.get(gstin) + "__"
					+ getTimestamp(timestampList));
		});

		return finalMap;
	}

	private String getTimestamp(List<String> timestampList) {
		SimpleDateFormat out = new SimpleDateFormat("dd-MM-yyyy : HH:mm:ss");
		String returnStamp = null;
		try {
			if (CollectionUtils.isNotEmpty(timestampList)) {
				Date startValue = out.parse(timestampList.get(0));
				for (int i = 1; i < timestampList.size(); i++) {
					Date nextValue = out.parse(timestampList.get(i));
					if (nextValue.after(startValue)) {
						startValue = nextValue;
					}
				}
				returnStamp = out.format(startValue);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnStamp;
	}

	private String createQueryString(StringBuilder buildQuery,
			StringBuilder buildQuery1) {
		StringBuilder bf = new StringBuilder();
		bf.append("SELECT ISD_GSTIN,fnGetState(ISD_GSTIN),SUM(ID),");
		bf.append("SUM(TAXABLE_VALUE1 + TAXABLE_VALUE2) AS TAXABLE_VALUE ,");
		bf.append(
				"SUM(IGST_AMT1 + IGST_AMT2 + CGST_AMT1 + CGST_AMT2 +SGST_AMT1 ");
		bf.append("+ SGST_AMT2 +CESS_AMT1 + CESS_AMT2) AS TOTAL_TAX,");
		bf.append("SUM(IGST_AMT1 + IGST_AMT2) AS IGST_AMT,");
		bf.append("SUM(CGST_AMT1 + CGST_AMT2) AS CGST_AMT,");
		bf.append(" SUM(SGST_AMT1 + SGST_AMT2) AS SGST_AMT,");
		bf.append("SUM(CESS_AMT1 + CESS_AMT2) AS CESS_AMT,");
		bf.append("SUM(DOC_AMT1 +  DOC_AMT2) AS  DOC_AMT ");
		bf.append("FROM ");
		bf.append("(");
		bf.append("SELECT CTIN AS ISD_GSTIN,TAX_PERIOD,COUNT(ID) AS ID,");
		bf.append("fnGetState(CTIN),");
		bf.append("SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE1 ,");
		bf.append("SUM(IFNULL(IGST_AMT,0)) AS IGST_AMT1,");
		bf.append("SUM(IFNULL(CGST_AMT,0)) AS CGST_AMT1,");
		bf.append("SUM(IFNULL(SGST_AMT,0)) AS SGST_AMT1,");
		bf.append("SUM(IFNULL(CESS_AMT,0)) AS CESS_AMT1,");
		bf.append("SUM(IFNULL(DOC_AMT,0)) AS DOC_AMT1,");
		bf.append("0 AS TAXABLE_VALUE2,");
		bf.append("0 AS DOC_AMT2,0 AS IGST_AMT2,0 AS CGST_AMT2,");
		bf.append("0 AS SGST_AMT2,0 AS CESS_AMT2,");
		bf.append("'INV' AS DOC_TYPE,");
		bf.append("'B2B' AS TABLE_SECTION ");
		bf.append("FROM GETGSTR6A_B2B_HEADER ");
		bf.append("WHERE IS_DELETE = FALSE ");
		bf.append(buildQuery);
		bf.append("GROUP BY CTIN,TAX_PERIOD ");
		bf.append("UNION ALL ");
		bf.append("SELECT CTIN AS ISD_GSTIN,TAX_PERIOD,COUNT(ID) AS ID,");
		bf.append("fnGetState(CTIN),");
		bf.append("SUM(IFNULL(TAXABLE_VALUE,0)) AS TAXABLE_VALUE1 ,");
		bf.append("SUM(IFNULL(IGST_AMT,0)) AS IGST_AMT1,");
		bf.append("SUM(IFNULL(CGST_AMT,0)) AS CGST_AMT1,");
		bf.append("SUM(IFNULL(SGST_AMT,0)) AS SGST_AMT1,");
		bf.append("SUM(IFNULL(CESS_AMT,0)) AS CESS_AMT1,");
		bf.append("SUM(IFNULL(DOC_AMT,0))  DOC_AMT1,");
		bf.append("0 AS TAXABLE_VALUE2,");
		bf.append("0 AS DOC_AMT2,0 AS IGST_AMT2,0 AS CGST_AMT2,");
		bf.append("0 AS SGST_AMT2,0 AS CESS_AMT2,");
		bf.append("'RNV' AS DOC_TYPE,");
		bf.append("'B2BA' AS TABLE_SECTION ");
		bf.append("FROM GETGSTR6A_B2BA_HEADER ");
		bf.append("WHERE IS_DELETE = FALSE ");
		bf.append(buildQuery);
		bf.append("GROUP BY CTIN,TAX_PERIOD ");
		bf.append("UNION ALL ");
		bf.append("SELECT CTIN AS ISD_GSTIN,TAX_PERIOD,COUNT(ID) AS ID,");
		bf.append("fnGetState(CTIN),");
		bf.append("0 AS TAXABLE_VALUE1 ,");
		bf.append("0 AS IGST_AMT1,0  AS CGST_AMT1,");
		bf.append("0 AS SGST_AMT1,0 AS CESS_AMT1,");
		bf.append(" 0 AS DOC_AMT1,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='D' THEN ");
		bf.append("IFNULL(TAXABLE_VALUE,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'C' THEN ");
		bf.append("IFNULL(TAXABLE_VALUE,0) END),0) AS TAXABLE_VALUE2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='D' THEN ");
		bf.append("IFNULL(INV_VALUE,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'C' THEN ");
		bf.append("IFNULL(INV_VALUE,0) END),0) AS DOC_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='D' THEN ");
		bf.append("IFNULL(IGST_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'C' THEN ");
		bf.append("IFNULL(IGST_AMT,0) END),0) AS IGST_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='D' THEN ");
		bf.append("IFNULL(CGST_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'C' THEN ");
		bf.append("IFNULL(CGST_AMT,0) END),0) AS CGST_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='D' THEN ");
		bf.append("IFNULL(SGST_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'C' THEN ");
		bf.append("IFNULL(SGST_AMT,0) END),0) AS SGST_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='D' THEN ");
		bf.append("IFNULL(CESS_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'C' THEN ");
		bf.append("IFNULL(CESS_AMT,0) END),0) AS CESS_AMT2,");
		bf.append("NOTE_TYPE AS DOC_TYPE,");
		bf.append("'CDN' AS TABLE_SECTION ");
		bf.append("FROM GETGSTR6A_CDN_HEADER ");
		bf.append("WHERE IS_DELETE = FALSE ");
		bf.append(buildQuery);
		bf.append("GROUP BY CTIN,TAX_PERIOD,NOTE_TYPE ");
		bf.append("UNION ALL ");
		bf.append("SELECT CTIN AS ISD_GSTIN,TAX_PERIOD,COUNT(ID) AS ID,");
		bf.append("fnGetState(CTIN),");
		bf.append("0 AS TAXABLE_VALUE1 ,");
		bf.append("0 AS IGST_AMT1,0  AS CGST_AMT1,");
		bf.append("0 AS SGST_AMT1,0 AS CESS_AMT1,");
		bf.append("0 AS DOC_AMT1,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='RDR' THEN ");
		bf.append("IFNULL(TAXABLE_VALUE,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'RCR' THEN ");
		bf.append("IFNULL(TAXABLE_VALUE,0) END),0) AS TAXABLE_VALUE2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='RDR' THEN ");
		bf.append("IFNULL(INV_VALUE,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'RCR' THEN ");
		bf.append("IFNULL(INV_VALUE,0) END),0) AS DOC_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='RDR' THEN ");
		bf.append("IFNULL(IGST_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'RCR' THEN ");
		bf.append("IFNULL(IGST_AMT,0) END),0) AS IGST_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='RDR' THEN ");
		bf.append("IFNULL(CGST_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'RCR' THEN ");
		bf.append("IFNULL(CGST_AMT,0) END),0) AS CGST_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='RDR' THEN ");
		bf.append("IFNULL(SGST_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'RCR' THEN ");
		bf.append("IFNULL(SGST_AMT,0) END),0) AS SGST_AMT2,");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  ='RDR' THEN ");
		bf.append("IFNULL(CESS_AMT,0) END),0) - ");
		bf.append("IFNULL(SUM(CASE WHEN NOTE_TYPE  = 'RCR' ");
		bf.append("THEN IFNULL(CESS_AMT,0) END),0) AS CESS_AMT2,");
		bf.append("NOTE_TYPE AS DOC_TYPE,");
		bf.append("'CDNA' AS TABLE_SECTION ");
		bf.append("FROM GETGSTR6A_CDNA_HEADER ");
		bf.append("WHERE IS_DELETE = FALSE ");
		bf.append(buildQuery);
		bf.append("GROUP BY CTIN,TAX_PERIOD,NOTE_TYPE ");
		bf.append(")");
		bf.append(buildQuery1);
		bf.append("GROUP BY ISD_GSTIN");
		return bf.toString();
	}

}
