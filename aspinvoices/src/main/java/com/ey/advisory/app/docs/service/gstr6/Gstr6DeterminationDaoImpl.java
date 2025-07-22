package com.ey.advisory.app.docs.service.gstr6;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DeterminationResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Component("Gstr6DeterminationDaoImpl")
@Slf4j
public class Gstr6DeterminationDaoImpl implements Gstr6DeterminationDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	private static final String N_I = "Not Initiated";

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<Gstr6DeterminationResponseDto> gstr6DeterminationDetails(
			SearchCriteria criteria) {
		try {
			Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria;

			List<Long> entityId = request.getEntityId();
			String taxPeriod = request.getReturnPeriod();
			List<String> tableType = request.getTableType();
			Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

			String GSTIN = null;

			List<String> gstinList = null;

			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						GSTIN = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {

							gstinList = gstnDetailRepository.getGstinRegTypeISD(
									dataSecAttrs.get(OnboardingConstant.GSTIN));
						}
					}

				}
			}

			StringBuilder buildHeader = new StringBuilder();
			StringBuilder buildHeaderTO = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildHeader.append(" AND DST.ISD_GSTIN IN :gstinList");
					buildHeaderTO.append(" AND ISD_GSTIN IN :gstinList");

				}
			}
			
			if (taxPeriod != null) {
				buildHeader.append(" AND DST.CURRENT_TAX_PERIOD = :taxPeriod");
				buildHeader.append(" AND ST.CURRENT_RET_PERIOD = :taxPeriod");
				buildHeaderTO.append(" AND CURRENT_RET_PERIOD = :taxPeriod");

			}
			if (tableType != null && !tableType.isEmpty()) {
				buildHeader.append(" AND DST.DOC_TYPE IN :tableType");
			}
			String queryStr = createApiProcessedQueryString(
					buildHeader.toString());
			String queryStatusTo = gettingStatusForTurnOver(
					buildHeaderTO.toString());
			Query q = entityManager.createNativeQuery(queryStr);
			Query q1 = entityManager.createNativeQuery(queryStatusTo);

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
					q1.setParameter("gstinList", gstinList);
				}
			}

			if (taxPeriod != null) {
				q.setParameter("taxPeriod", taxPeriod);
				q1.setParameter("taxPeriod", taxPeriod);
			}
			if (tableType != null && !tableType.isEmpty()) {
				q.setParameter("tableType", tableType);
			}
			List<Object[]> listStatus = q1.getResultList();
			Map<String, Gstr6DeterminationResponseDto> turnOStatus = createStatusConvertion(
					listStatus);

			List<Object[]> list = q.getResultList();

			List<String> nIGstinList = new ArrayList<>();

			for (Object[] gstn : list) {
				String sgtin = gstn[1] != null && !gstn[1].toString().isEmpty()
						? gstn[1].toString() : null;
				if (sgtin != null) {
					nIGstinList.add(sgtin);
				}
			}

			List<String> isdGstins = gstnDetailRepository
					.findgstinByEntityIdWithISD(entityId);

			if (CollectionUtils.isNotEmpty(gstinList)
					&& CollectionUtils.isEmpty(list)) {
				list = addedNotIntiatedValuesWithGstn(gstinList, list);

			} else if (!CollectionUtils.isEmpty(list)
					&& CollectionUtils.isEmpty(gstinList)) {
				list = addedNotIntiatedValues(nIGstinList, isdGstins, list);
			} else if (CollectionUtils.isEmpty(list)
					&& CollectionUtils.isEmpty(gstinList)) {
				list = addedNotIntiatedValues(nIGstinList, isdGstins, list);
			}

			List<Gstr6DeterminationResponseDto> verticalHsnList = Lists
					.newArrayList();
			if (CollectionUtils.isNotEmpty(list)) {
				for (Object arr[] : list) {
					verticalHsnList
							.add(convertTransactionalLevel(arr, turnOStatus));
				}
			}

			return new SearchResult<>(verticalHsnList);

		} catch (Exception e) {
			LOGGER.debug("Exception Occur in Gstr6DeterminationDaoImpl", e);
			throw new AppException(e);

		}
	}

	private List<Object[]> addedNotIntiatedValuesWithGstn(
			List<String> nIGstinList, List<Object[]> list) {
		for (String gstin : nIGstinList) {
			Object[] dummy = new Object[11];
			dummy[0] = N_I;
			dummy[1] = gstin;
			dummy[2] = BigInteger.ZERO;
			dummy[3] = BigDecimal.ZERO;
			dummy[4] = BigDecimal.ZERO;
			dummy[5] = BigDecimal.ZERO;
			dummy[6] = BigDecimal.ZERO;
			dummy[7] = BigDecimal.ZERO;
			dummy[8] = BigDecimal.ZERO;
			dummy[9] = BigDecimal.ZERO;
			list.add(dummy);
		}
		return list;
	}

	private Map<String, Gstr6DeterminationResponseDto> createStatusConvertion(
			List<Object[]> listStatus) {

		Map<String, Gstr6DeterminationResponseDto> mapStatus = new HashMap<>();
		for (Object[] arr : listStatus) {
			String gstn = arr[0] != null ? arr[0].toString() : null;
			Gstr6DeterminationResponseDto objDto = mapStatus.get(gstn);
			String statusType = arr[1] != null ? arr[1].toString() : null;

			String gstinDigiGstnTimeStame = arr[3] != null ? arr[3].toString()
					: null;

			if (gstinDigiGstnTimeStame != null) {
				Timestamp timeStamp = Timestamp.valueOf(gstinDigiGstnTimeStame);
				LocalDateTime dt = timeStamp.toLocalDateTime();
				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(dt);
				DateTimeFormatter f1 = DateTimeFormatter
						.ofPattern("dd-MM-yyyy HH:mm:ss");
				gstinDigiGstnTimeStame = f1.format(dateTimeFormatter);
			}

			if ("GSTIN_STATUS".equalsIgnoreCase(statusType)) {
				objDto = objDto == null ? new Gstr6DeterminationResponseDto()
						: objDto;
				objDto.setGstin(gstn);
				objDto.setTurnoverGstnStatus(
						arr[2] != null ? arr[2].toString() : null);
				objDto.setTurnoverGstnTimestamp(gstinDigiGstnTimeStame);
				mapStatus.put(gstn, objDto);
			}
			if ("DIGI_GST_STATUS".equalsIgnoreCase(statusType)) {
				objDto = objDto == null ? new Gstr6DeterminationResponseDto()
						: objDto;
				objDto.setGstin(gstn);
				objDto.setTurnoverGstnStatus(objDto.getTurnoverGstnStatus());
				objDto.setTurnoverGstnTimestamp(
						objDto.getTurnoverGstnTimestamp());
				objDto.setTurnoverDigiStatus(
						arr[2] != null ? arr[2].toString() : null);
				objDto.setTurnoverDigiTimestamp(gstinDigiGstnTimeStame);
			}

			mapStatus.put(gstn, objDto);
		}

		return mapStatus;

	}

	private Gstr6DeterminationResponseDto convertTransactionalLevel(
			Object[] arr, Map<String, Gstr6DeterminationResponseDto> map) {
		Gstr6DeterminationResponseDto obj = new Gstr6DeterminationResponseDto();

		obj.setDistribStatus(arr[0] != null ? arr[0].toString() : null);
		String gstn = arr[1] != null ? arr[1].toString() : null;
		obj.setGstin(gstn);

		BigDecimal disEigst = BigDecimal.ZERO;
		if (arr[2] != null && !arr[2].toString().isEmpty()) {
			disEigst = new BigDecimal(arr[2].toString());
			obj.setDisEligIGST(disEigst);
		} else {
			obj.setDisEligIGST(disEigst);
		}

		BigDecimal disIEigst = BigDecimal.ZERO;
		if (arr[3] != null && !arr[3].toString().isEmpty()) {
			disIEigst = new BigDecimal(arr[3].toString());
			obj.setDisInEligIGST(disIEigst);
		} else {
			obj.setDisInEligIGST(disIEigst);
		}

		BigDecimal disECgst = BigDecimal.ZERO;
		if (arr[4] != null && !arr[4].toString().isEmpty()) {
			disECgst = new BigDecimal(arr[4].toString());
			obj.setDisEligCGST(disECgst);
		} else {
			obj.setDisEligCGST(disECgst);
		}

		BigDecimal disIECgst = BigDecimal.ZERO;
		if (arr[5] != null && !arr[5].toString().isEmpty()) {
			disIECgst = new BigDecimal(arr[5].toString());
			obj.setDisInEligCGST(disIECgst);
		} else {
			obj.setDisInEligCGST(disIECgst);
		}

		BigDecimal disEsgst = BigDecimal.ZERO;
		if (arr[6] != null && !arr[6].toString().isEmpty()) {
			disEsgst = new BigDecimal(arr[6].toString());
			obj.setDisEligSGST(disEsgst);
		} else {
			obj.setDisEligSGST(disEsgst);
		}

		BigDecimal disIEsgst = BigDecimal.ZERO;
		if (arr[7] != null && !arr[7].toString().isEmpty()) {
			disIEsgst = new BigDecimal(arr[7].toString());
			obj.setDisInEligSGST(disIEsgst);
		} else {
			obj.setDisInEligSGST(disIEsgst);
		}

		BigDecimal disEcess = BigDecimal.ZERO;
		if (arr[8] != null && !arr[8].toString().isEmpty()) {
			disEcess = new BigDecimal(arr[8].toString());
			obj.setDisEligCESS(disEcess);
		} else {
			obj.setDisEligCESS(disEcess);
		}

		BigDecimal disIEcess = BigDecimal.ZERO;
		if (arr[9] != null && !arr[9].toString().isEmpty()) {
			disIEcess = new BigDecimal(arr[9].toString());
			obj.setDisInEligCESS(disIEcess);
		} else {
			obj.setDisInEligCESS(disIEcess);
		}

		BigDecimal totalTax = disEigst.add(disECgst).add(disEsgst).add(disEcess)
				.add(disIEigst).add(disIECgst).add(disIEsgst).add(disIEcess);
		obj.setTotalTax(totalTax);
		obj.setRegType("ISD");
		String stateCode = gstn != null ? gstn.substring(0, 2) : null;
		String stateName = stateCode != null
				? statecodeRepository.findStateNameByCode(stateCode) : null;
		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin(gstn);

		obj.setState(stateCode);
		obj.setStateName(stateName);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				obj.setAuthToken(APIConstants.ACTIVE);
			} else {
				obj.setAuthToken(APIConstants.IN_ACTIVE);
			}
		} else {
			obj.setAuthToken(APIConstants.IN_ACTIVE);
		}

		if (gstn != null) {
			Gstr6DeterminationResponseDto statusDto = map.get(gstn);
			if (statusDto != null) {
				String gstnStatus = statusDto.getTurnoverGstnStatus() != null
						&& !statusDto.getTurnoverGstnStatus().isEmpty()
								? statusDto.getTurnoverGstnStatus() : N_I;
				obj.setTurnoverGstnStatus(gstnStatus);
				obj.setTurnoverGstnTimestamp(
						statusDto.getTurnoverGstnTimestamp());
				String digiStatus = statusDto.getTurnoverDigiStatus() != null
						&& !statusDto.getTurnoverDigiStatus().isEmpty()
								? statusDto.getTurnoverDigiStatus() : N_I;
				obj.setTurnoverDigiStatus(digiStatus);
				obj.setTurnoverDigiTimestamp(
						statusDto.getTurnoverDigiTimestamp());
			} else {
				obj.setTurnoverGstnStatus(N_I);
				obj.setTurnoverDigiStatus(N_I);
			}

		} else {
			obj.setTurnoverGstnStatus(N_I);
			obj.setTurnoverDigiStatus(N_I);
		}
		String disTimeStame = arr[10] != null ? arr[10].toString() : null;
		if (disTimeStame != null) {
			Timestamp timeStamp = Timestamp.valueOf(disTimeStame);
			LocalDateTime dt = timeStamp.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter f1 = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			disTimeStame = f1.format(dateTimeFormatter);
		}

		obj.setDistribTimestamp(disTimeStame);
		return obj;
	}

	private List<Object[]> addedNotIntiatedValues(List<String> dataGstinList,
			List<String> gstinList, List<Object[]> listDummy) {

		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstin : gstinList) {
				Object[] dummy = null;
				if (!dataGstinList.contains(gstin)) {
					dummy = new Object[11];
					dummy[0] = N_I;
					dummy[1] = gstin;
					dummy[2] = BigInteger.ZERO;
					dummy[3] = BigDecimal.ZERO;
					dummy[4] = BigDecimal.ZERO;
					dummy[5] = BigDecimal.ZERO;
					dummy[6] = BigDecimal.ZERO;
					dummy[7] = BigDecimal.ZERO;
					dummy[8] = BigDecimal.ZERO;
					dummy[9] = BigDecimal.ZERO;
				}
				if (dummy != null) {
					listDummy.add(dummy);
				}
			}
		}
		return listDummy;
	}

	private String createApiProcessedQueryString(String buildQuery) {

		StringBuilder build = new StringBuilder();

		build.append("select ST.CREDIT_STATUS, DST.ISD_GSTIN, ");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'E' THEN ");
		build.append(
				"(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+ ");
		build.append("IFNULL(SGST_AMT_AS_IGST,0) ) END) AS E_IGST_AMOUNT,");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'IE' THEN ");
		build.append("(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+");
		build.append(" IFNULL(SGST_AMT_AS_IGST,0) ) END) AS IE_IGST_AMOUNT,");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'E' THEN ");
		build.append("	(IFNULL(CGST_AMT_AS_CGST,0)) END) AS E_CGST_AMOUNT,");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'IE' THEN ");
		build.append("(IFNULL(CGST_AMT_AS_CGST,0)) END) AS IE_CGST_AMOUNT,");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'E' THEN ");
		build.append("(IFNULL(SGST_AMT_AS_SGST,0)) END) AS E_SGST_AMOUNT,");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'IE' THEN ");
		build.append("(IFNULL(SGST_AMT_AS_SGST,0)) END) AS IE_SGST_AMOUNT,");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'E' THEN ");
		build.append("(IFNULL(CESS_AMT,0)) END) AS E_CESS_AMOUNT,");
		build.append("SUM(CASE WHEN ELIGIBLE_INDICATOR = 'IE' THEN ");
		build.append(
				"(IFNULL(CESS_AMT,0)) END) AS IE_CESS_AMOUNT,CREDIT_TIMESTAMP ");
		build.append("FROM GSTR6_STATUS ST INNER JOIN ");
		build.append("GSTR6_CREDIT_DISTRIBUTION DST ");
		build.append("ON ST.BATCH_ID = DST.BATCH_ID ");
		build.append("WHERE DST.IS_DELETE = FALSE AND ST.IS_DELETE = FALSE ");
		build.append(buildQuery);
		build.append("  GROUP BY DST.ISD_GSTIN,ST.CREDIT_STATUS,");
		build.append("CREDIT_TIMESTAMP");
		return build.toString();
	}

	private String gettingStatusForTurnOver(String buildQuery) {
		StringBuilder build = new StringBuilder();

		build.append("SELECT ISD_GSTIN,");
		build.append("CASE WHEN DIGI_GST_STATUS IS NULL THEN 'GSTIN_STATUS' ");
		build.append("WHEN GSTIN_STATUS IS NULL THEN 'DIGI_GST_STATUS' ");
		build.append(" END AS STATUS_TYPE,");
		build.append("IFNULL(DIGI_GST_STATUS,GSTIN_STATUS) AS STATUS,");
		build.append(
				" IFNULL(DIGI_GST_TIMESTAMP,GSTIN_TIMESTAMP) GSTIN_TIMESTAMP ");
		build.append(" FROM GSTR6_STATUS WHERE IS_DELETE = FALSE  ");
		build.append(buildQuery);
		return build.toString();
	}

	@Override
	public SearchResult<Gstr6DeterminationResponseDto> gstr6TurnOverDetails(
			SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

}
