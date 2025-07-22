package com.ey.advisory.app.services.daos.saveanx2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Anx2SaveAnx2ProcessedRequestDto;
import com.ey.advisory.app.docs.dto.Anx2SaveAnx2ProcessedResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

@Component("SaveAnx2ProcessedDataDaoImpl")
public class SaveAnx2ProcessedDataDaoImpl implements SaveAnx2ProcessedDataDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Override
	public List<Anx2SaveAnx2ProcessedResponseDto> getSaveAnx2ProcessedData(
			Anx2SaveAnx2ProcessedRequestDto criteria) {
		String taxperiod = criteria.getTaxPeriod();

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
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder buildQuery1 = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			buildQuery.append(" AND CGSTIN IN :gstinList");
			buildQuery1.append(" AND A2_RECIPIENT_GSTIN IN :gstinList");
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod");
			buildQuery1.append(" AND DERIVED_RET_PERIOD = :taxperiod");
		}

		String queryStr = createQueryString(buildQuery, buildQuery1);
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(taxperiod);
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertGetProcessedData(o))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	private Anx2SaveAnx2ProcessedResponseDto convertGetProcessedData(
			Object[] arr) {
		Anx2SaveAnx2ProcessedResponseDto responseCriteria = 
				new Anx2SaveAnx2ProcessedResponseDto();
		String GSTIN = (String) arr[0];
		responseCriteria.setGstin(GSTIN);
		// STATE NAME
		String stateCode = GSTIN.substring(0, 2);
		String stateName = statecodeRepository.findStateNameByCode(stateCode);
		responseCriteria.setState(stateName);
		// AUTH TOKEN
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
		responseCriteria.setSaveStatus(APIConstants.SUCCESS);
		responseCriteria.setTimeStamp(LocalDateTime.now());

		responseCriteria.setTypeCount(APIConstants.TYPE_COUNT);

		BigInteger intVal = BigInteger.ZERO;
		BigDecimal decVal = BigDecimal.ZERO;

		if (arr[2] == null || arr[2].toString().isEmpty()) {
			responseCriteria.setAspCountAccept(intVal);
		} else {
			responseCriteria.setAspCountAccept(GenUtil.getBigInteger(arr[2]));
		}
		if (arr[4] == null || arr[4].toString().isEmpty()) {
			responseCriteria.setAspCountPending(intVal);
		} else {
			responseCriteria.setAspCountPending(GenUtil.getBigInteger(arr[4]));
		}
		if (arr[3] == null || arr[3].toString().isEmpty()) {
			responseCriteria.setAspCountReject(intVal);
		} else {
			responseCriteria.setAspCountReject(GenUtil.getBigInteger(arr[3]));
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			responseCriteria.setAspCountnoAction(intVal);
		} else {
			responseCriteria.setAspCountnoAction(GenUtil.getBigInteger(arr[5]));
		}
		responseCriteria.setAspCountisdCredit(intVal);

		if (arr[14] == null || arr[14].toString().isEmpty()) {
			responseCriteria.setGstnCountAccept(intVal);
		} else {
			responseCriteria.setGstnCountAccept(GenUtil.getBigInteger(arr[14]));
		}
		if (arr[16] == null || arr[16].toString().isEmpty()) {
			responseCriteria.setGstnCountpending(intVal);
		} else {
			responseCriteria.setGstnCountpending(GenUtil.getBigInteger(arr[16]));
		}
		if (arr[15] == null || arr[15].toString().isEmpty()) {
			responseCriteria.setGstnCountreject(intVal);
		} else {
			responseCriteria.setGstnCountreject(GenUtil.getBigInteger(arr[15]));
		}
		if (arr[17] == null || arr[17].toString().isEmpty()) {
			responseCriteria.setGstnCountnoAction(intVal);
		} else {
			responseCriteria.setGstnCountnoAction(GenUtil.getBigInteger(arr[17]));
		}
		if (arr[26] == null || arr[26].toString().isEmpty()) {
			responseCriteria.setGstnCountisdCredit(intVal);
		} else {
			responseCriteria.setGstnCountisdCredit(GenUtil.getBigInteger(arr[26]));
		}
		if (arr[26] == null || arr[26].toString().isEmpty()) {
			responseCriteria.setGstnCountisdCredit(intVal);
		} else {
			responseCriteria.setGstnCountisdCredit(GenUtil.getBigInteger(arr[26]));
		}
		responseCriteria.setTypeTaxable(APIConstants.TYPE_TAXABLE);

		if (arr[6] == null || arr[6].toString().isEmpty()) {
			responseCriteria.setAspTaxableAccept(decVal);
		} else {
			responseCriteria.setAspTaxableAccept((BigDecimal) arr[6]);
		}
		if (arr[8] == null || arr[8].toString().isEmpty()) {
			responseCriteria.setAspTaxablePending(decVal);
		} else {
			responseCriteria.setAspTaxablePending((BigDecimal) arr[8]);
		}
		if (arr[7] == null || arr[7].toString().isEmpty()) {
			responseCriteria.setAspTaxableReject(decVal);
		} else {
			responseCriteria.setAspTaxableReject((BigDecimal) arr[7]);
		}
		if (arr[9] == null || arr[9].toString().isEmpty()) {
			responseCriteria.setAspTaxablenoAction(decVal);
		} else {
			responseCriteria.setAspTaxablenoAction((BigDecimal) arr[9]);
		}
		responseCriteria.setAspTaxableisdCredit(decVal);

		if (arr[18] == null || arr[18].toString().isEmpty()) {
			responseCriteria.setGstnTaxableAccept(decVal);
		} else {
			responseCriteria.setGstnTaxableAccept((BigDecimal) arr[18]);
		}
		if (arr[20] == null || arr[20].toString().isEmpty()) {
			responseCriteria.setGstnTaxablepending(decVal);
		} else {
			responseCriteria.setGstnTaxablepending((BigDecimal) arr[20]);
		}
		if (arr[19] == null || arr[19].toString().isEmpty()) {
			responseCriteria.setGstnTaxablereject(decVal);
		} else {
			responseCriteria.setGstnTaxablereject((BigDecimal) arr[19]);
		}
		if (arr[21] == null || arr[21].toString().isEmpty()) {
			responseCriteria.setGstnTaxablenoAction(decVal);
		} else {
			responseCriteria.setGstnTaxablenoAction((BigDecimal) arr[21]);
		}
		if (arr[27] == null || arr[27].toString().isEmpty()) {
			responseCriteria.setGstnTaxableisdCredit(intVal);
		} else {
			responseCriteria.setGstnTaxableisdCredit(GenUtil.getBigInteger(arr[27]));
		}
		responseCriteria.setTypeTotalTax(APIConstants.TYPE_TOTALTAX);
		
		if (arr[10] == null || arr[10].toString().isEmpty()) {
			responseCriteria.setAspTotalTaxAccept(decVal);
		} else {
			responseCriteria.setAspTotalTaxAccept((BigDecimal) arr[10]);
		}
		if (arr[12] == null || arr[12].toString().isEmpty()) {
			responseCriteria.setAspTotalTaxPending(decVal);
		} else {
			responseCriteria.setAspTotalTaxPending((BigDecimal) arr[12]);
		}
		if (arr[11] == null || arr[11].toString().isEmpty()) {
			responseCriteria.setAspTotalTaxReject(decVal);
		} else {
			responseCriteria.setAspTotalTaxReject((BigDecimal) arr[11]);
		}
		if (arr[13] == null || arr[13].toString().isEmpty()) {
			responseCriteria.setAspTotalTaxnoAction(decVal);
		} else {
			responseCriteria.setAspTotalTaxnoAction((BigDecimal) arr[13]);
		}
		responseCriteria.setAspTotalTaxisdCredit(decVal);

		if (arr[22] == null || arr[22].toString().isEmpty()) {
			responseCriteria.setGstnTotalTaxAccept(decVal);
		} else {
			responseCriteria.setGstnTotalTaxAccept((BigDecimal) arr[22]);
		}
		if (arr[24] == null || arr[24].toString().isEmpty()) {
			responseCriteria.setGstnTotalTaxpending(decVal);
		} else {
			responseCriteria.setGstnTotalTaxpending((BigDecimal) arr[24]);
		}
		if (arr[23] == null || arr[23].toString().isEmpty()) {
			responseCriteria.setGstnTotalTaxreject(decVal);
		} else {
			responseCriteria.setGstnTotalTaxreject((BigDecimal) arr[23]);
		}
		if (arr[25] == null || arr[25].toString().isEmpty()) {
			responseCriteria.setGstnTotalTaxnoAction(decVal);
		} else {
			responseCriteria.setGstnTotalTaxnoAction((BigDecimal) arr[25]);
		}
		if (arr[28] == null || arr[28].toString().isEmpty()) {
			responseCriteria.setGstnTotalTaxisdCredit(intVal);
		} else {
			responseCriteria.setGstnTotalTaxisdCredit(GenUtil.getBigInteger(arr[28]));
		}

		return responseCriteria;
	}

	private String createQueryString(StringBuilder buidQuery,
			StringBuilder buildQuery1) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("SUM(ACCEPT_CNT_DIGIGST) ACCEPT_CNT_DIGIGST,");
		queryString.append("SUM(REJECT_CNT_DIGIGST) REJECT_CNT_DIGIGST,");
		queryString.append("SUM(PENDING_CNT_DIGIGST) PENDING_CNT_DIGIGST,");     
		queryString.append("SUM(NO_ACTION_CNT_DIGIGST) NO_ACTION_CNT_DIGIGST,");
		queryString.append
		("SUM(ACCEPT_TAXABLE_DIGIGST) ACCEPT_TAXABLE_DIGIGST,");
		queryString.append
		("SUM(REJECT_TAXABLE_DIGIGST) REJECT_TAXABLE_DIGIGST,");
		queryString.append
		("SUM(PENDING_TAXABLE_DIGIGST) PENDING_TAXABLE_DIGIGST,");
		queryString.append
		("SUM(NO_ACTION_TAXABLE_DIGIGST) NO_ACTION_TAXABLE_DIGIGST,");
		queryString.append
		("SUM(ACCEPT_TOTAL_TAX_DIGIGST) ACCEPT_TOTAL_TAX_DIGIGST,");
		queryString.append
		("SUM(REJECT_TOTAL_TAX_DIGIGST) REJECT_TOTAL_TAX_DIGIGST,");
		queryString.append
		("SUM(PENDING_TOTAL_TAX_DIGIGST) PENDING_TOTAL_TAX_DIGIGST,");
		queryString.append
		("SUM(NO_ACTION_TOTAL_TAX_DIGIGST) NO_ACTION_TOTAL_TAX_DIGIGST,");
		queryString.append("SUM(ACCEPT_CNT_GSTN) ACCEPT_CNT_GSTN,");
		queryString.append("SUM(REJECT_CNT_GSTN) REJECT_CNT_GSTN,");
		queryString.append("SUM(PENDING_CNT_GSTN) PENDING_CNT_GSTN,");
		queryString.append("SUM(NO_ACTION_CNT_GSTN) NO_ACTION_CNT_GSTN,");
		queryString.append("SUM(ACCEPT_TAXABLE_GSTN) ACCEPT_TAXABLE_GSTN,");
		queryString.append("SUM(REJECT_TAXABLE_GSTN) REJECT_TAXABLE_GSTN,");
		queryString.append("SUM(PENDING_TAXABLE_GSTN) PENDING_TAXABLE_GSTN,");
		queryString.append
		("SUM(NO_ACTION_TAXABLE_GSTN) NO_ACTION_TAXABLE_GSTN,");
		queryString.append("SUM(ACCEPT_TOT_TAX_GSTN) ACCEPT_TOT_TAX_GSTN,");
		queryString.append("SUM(REJECT_TOT_TAX_GSTN) REJECT_TOT_TAX_GSTN,");
		queryString.append("SUM(PENDING_TOT_TAX_GSTN) PENDING_TOT_TAX_GSTN,");
		queryString.append("SUM(NO_ACTION_TOT_TAX_GSTN) NO_ACTION_TOT_TAX_GSTN,");
		queryString.append("SUM(ISDC_CNT_GSTN) ISDC_CNT_GSTN,");
		queryString.append("SUM(ISDC_TAXABLE_GSTN) ISDC_TAXABLE_GSTN,");
		queryString.append("SUM(ISDC_TOT_TAX_GSTN) ISDC_TOT_TAX_GSTN ");
		queryString.append("FROM (");
		queryString.append("SELECT CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("COUNT(CASE WHEN USER_RESPONS_DIGI_GST = 'ACCEPT' ");
		queryString.append("THEN 1 ELSE NULL END ) ACCEPT_CNT_DIGIGST,");
		queryString.append("COUNT(CASE WHEN USER_RESPONS_DIGI_GST = 'REJECT' ");
		queryString.append("THEN 1 ELSE NULL END ) REJECT_CNT_DIGIGST,");
		queryString.append(
				"COUNT(CASE WHEN USER_RESPONS_DIGI_GST = 'PENDING' ");
		queryString.append("THEN 1 ELSE NULL END ) PENDING_CNT_DIGIGST,");
		queryString.append(
				"COUNT(CASE WHEN USER_RESPONS_DIGI_GST = 'NO_ACTION' ");
		queryString.append("THEN 1 ELSE NULL END ) NO_ACTION_CNT_DIGIGST,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'ACCEPT' ");
		queryString.append(
				"THEN TAXABLE_VALUE_DIGI_GST END) ACCEPT_TAXABLE_DIGIGST,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'REJECT' ");
		queryString.append(
				"THEN TAXABLE_VALUE_DIGI_GST END) REJECT_TAXABLE_DIGIGST,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'PENDING' ");
		queryString.append(
				"THEN TAXABLE_VALUE_DIGI_GST END) PENDING_TAXABLE_DIGIGST,");
		queryString.append(
				"SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'NO_ACTION' ");
		queryString.append(
				"THEN TAXABLE_VALUE_DIGI_GST END) NO_ACTION_TAXABLE_DIGIGST,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'ACCEPT' ");
		queryString.append(
				"THEN TOTAL_TAX_DIGI_GST END) ACCEPT_TOTAL_TAX_DIGIGST,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'REJECT' ");
		queryString.append(
				"THEN TOTAL_TAX_DIGI_GST END) REJECT_TOTAL_TAX_DIGIGST,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'PENDING' ");
		queryString.append(
				"THEN TOTAL_TAX_DIGI_GST END) PENDING_TOTAL_TAX_DIGIGST,");
		queryString.append(
				"SUM(CASE WHEN USER_RESPONS_DIGI_GST = 'NO_ACTION' ");
		queryString.append(
				"THEN TOTAL_TAX_DIGI_GST END) NO_ACTION_TOTAL_TAX_DIGIGST,");
		queryString.append("0 ACCEPT_CNT_GSTN,0 REJECT_CNT_GSTN,");
		queryString.append("0 PENDING_CNT_GSTN,0 NO_ACTION_CNT_GSTN,");
		queryString.append("0 ACCEPT_TAXABLE_GSTN,0 REJECT_TAXABLE_GSTN,");
		queryString.append("0 PENDING_TAXABLE_GSTN, 0 NO_ACTION_TAXABLE_GSTN,");
		queryString.append("0 ACCEPT_TOT_TAX_GSTN,0 REJECT_TOT_TAX_GSTN,");
		queryString.append("0 PENDING_TOT_TAX_GSTN, 0 NO_ACTION_TOT_TAX_GSTN,");
		queryString.append("0 ISDC_CNT_GSTN,0 ISDC_TAXABLE_GSTN,");
		queryString.append("0 ISDC_TOT_TAX_GSTN ");
		queryString.append("FROM(");
		queryString.append(
				"SELECT A2_RECIPIENT_GSTIN AS CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append(
				"( CASE WHEN USER_RESPONSE IN ('A1','A2','A3') THEN 'ACCEPT' ");
		queryString.append("WHEN USER_RESPONSE IN ('P1') THEN 'PENDING' ");
		queryString.append(
				"WHEN USER_RESPONSE IN ('R1','R1U1','R1U2') THEN 'REJECT' ");
		queryString.append(
				"WHEN USER_RESPONSE IN ('C1') THEN 'NO_ACTION' END ) ");
		queryString.append("AS USER_RESPONS_DIGI_GST,");
		queryString.append("SUM(A2_TAXABLE_VALUE) AS TAXABLE_VALUE_DIGI_GST,");
		queryString.append("SUM(IFNULL(A2_IGST,0)+IFNULL(A2_CGST,0)+");
		queryString.append(
				"IFNULL(A2_SGST,0)+ IFNULL(A2_CESS,0)) AS TOTAL_TAX_DIGI_GST,");
		queryString.append("'ISDC' AS TTYPE ");
		queryString.append("FROM LINK_A2_PR ");
		queryString.append("WHERE A2_ID IS NOT NULL AND USER_RESPONSE ");
		queryString.append("IN ('A1','A2','A3','P1','R1','R1U1','R1U2','C1') ");
		queryString.append("AND IS_DELETED = TRUE AND IS_ACTIVE = FALSE ");
		queryString.append(buildQuery1);
		queryString.append(
				" GROUP BY A2_RECIPIENT_GSTIN,TAX_PERIOD,USER_RESPONSE");
		queryString.append(")");
		queryString.append(" GROUP BY CUST_GSTIN_DIGI_GST,TAX_PERIOD ");
		queryString.append("UNION ALL ");
		queryString.append("SELECT CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("0 ACCEPT_CNT_DIGIGST,0 REJECT_CNT_DIGIGST,");
		queryString.append("0 PENDING_CNT_DIGIGST,0 NO_ACTION_CNT_DIGIGST,");
		queryString.append(
				"0 ACCEPT_TAXABLE_DIGIGST,0 REJECT_TAXABLE_DIGIGST,");
		queryString.append(
				"0 PENDING_TAXABLE_DIGIGST,0 NO_ACTION_TAXABLE_DIGIGST,");
		queryString.append(
				"0 ACCEPT_TOTAL_TAX_DIGIGST,0 REJECT_TOTAL_TAX_DIGIGST,");
		queryString.append(
				"0 PENDING_TOTAL_TAX_DIGIGST,0 NO_ACTION_TOTAL_TAX_DIGIGST,");
		queryString.append("COUNT(CASE WHEN USER_RESPONS_GSTN = 'ACCEPT' ");
		queryString.append("THEN 1 ELSE NULL END ) ACCEPT_CNT_GSTN,");
		queryString.append("COUNT(CASE WHEN USER_RESPONS_GSTN = 'REJECT' ");
		queryString.append("THEN 1 ELSE NULL END ) REJECT_CNT_GSTN,");
		queryString.append("COUNT(CASE WHEN USER_RESPONS_GSTN = 'PENDING' ");
		queryString.append("THEN 1 ELSE NULL END ) PENDING_CNT_GSTN,");
		queryString.append("COUNT(CASE WHEN USER_RESPONS_GSTN = 'NO_ACTION' ");
		queryString.append("THEN 1 ELSE NULL END ) NO_ACTION_CNT_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'ACCEPT' ");
		queryString.append("THEN TAXABLE_VALUE_GSTN END) ACCEPT_TAXABLE_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'REJECT' ");
		queryString.append("THEN TAXABLE_VALUE_GSTN END) REJECT_TAXABLE_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'PENDING' ");
		queryString.append(
				"THEN TAXABLE_VALUE_GSTN END) PENDING_TAXABLE_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'NO_ACTION' ");
		queryString.append(
				"THEN TAXABLE_VALUE_GSTN END) NO_ACTION_TAXABLE_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'ACCEPT' ");
		queryString.append("THEN TOTAL_TAX_GSTN END) ACCEPT_TOT_TAX_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'REJECT' ");
		queryString.append("THEN TOTAL_TAX_GSTN END) REJECT_TOT_TAX_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'PENDING' ");
		queryString.append("THEN TOTAL_TAX_GSTN END) PENDING_TOT_TAX_GSTN,");
		queryString.append("SUM(CASE WHEN USER_RESPONS_GSTN = 'NO_ACTION' ");
		queryString.append("THEN TOTAL_TAX_GSTN END) NO_ACTION_TOT_TAX_GSTN,");
		queryString.append("COUNT(CASE WHEN TTYPE = 'ISDC' THEN 1 ");
		queryString.append("ELSE NULL END ) ISDC_CNT_GSTN,");
		queryString.append("COUNT(CASE WHEN TTYPE = 'ISDC' ");
		queryString.append("THEN TAXABLE_VALUE_GSTN END ) ISDC_TAXABLE_GSTN,");
		queryString.append("COUNT(CASE WHEN TTYPE = 'ISDC' ");
		queryString.append("THEN TOTAL_TAX_GSTN END ) ISDC_TOT_TAX_GSTN ");
		queryString.append("FROM ");
		queryString.append("(");
		queryString.append("SELECT CGSTIN CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("CASE WHEN ACTION_TAKEN IN ('A') THEN 'ACCEPT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('P') THEN 'PENDING' ");
		queryString.append(
				"WHEN ACTION_TAKEN IN ('R','R1U1','R1U2') " + "THEN 'REJECT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('N') THEN 'NO_ACTION' ");
		queryString.append("END AS USER_RESPONS_GSTN,");
		queryString.append("'' AS TTYPE,");
		queryString.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE_GSTN,");
		queryString.append("SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)");
		queryString.append(
				"+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX_GSTN ");
		queryString.append("FROM GETANX2_B2B_HEADER ");
		queryString.append("WHERE IS_DELETE = FALSE AND ACTION_TAKEN ");
		queryString.append("IN ('A','P','R','N') ");
		queryString.append(buidQuery);
		queryString.append(" GROUP BY CGSTIN,TAX_PERIOD,ACTION_TAKEN ");
		queryString.append("UNION ALL ");
		queryString.append("SELECT  CGSTIN CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("CASE WHEN ACTION_TAKEN IN ('A') THEN 'ACCEPT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('P') THEN 'PENDING' ");
		queryString.append("WHEN ACTION_TAKEN IN ('R','R1U1','R1U2') ");
		queryString.append("THEN 'REJECT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('N') THEN 'NO_ACTION' ");
		queryString.append("END AS USER_RESPONS_GSTN,");
		queryString.append("'' AS TTYPE,");
		queryString.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE_GSTN,");
		queryString.append("SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)");
		queryString.append(
				"+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX_GSTN ");
		queryString.append("FROM GETANX2_DE_HEADER ");
		queryString.append("WHERE IS_DELETE = FALSE AND ACTION_TAKEN ");
		queryString.append("IN ('A','P','R','N') ");
		queryString.append(buidQuery);
		queryString.append(" GROUP BY CGSTIN,TAX_PERIOD,ACTION_TAKEN ");
		queryString.append("UNION ALL ");
		queryString.append("SELECT  CGSTIN CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("CASE WHEN ACTION_TAKEN IN ('A') THEN 'ACCEPT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('P') THEN 'PENDING' ");
		queryString.append("WHEN ACTION_TAKEN IN ('R','R1U1','R1U2') ");
		queryString.append("THEN 'REJECT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('N') THEN 'NO_ACTION' ");
		queryString.append("END AS USER_RESPONS_GSTN,");
		queryString.append("'' AS TTYPE,");
		queryString.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE_GSTN,");
		queryString.append("SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)");
		queryString.append(
				"+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX_GSTN ");
		queryString.append("FROM GETANX2_SEZWP_HEADER ");
		queryString.append("WHERE IS_DELETE = FALSE AND ACTION_TAKEN ");
		queryString.append("IN ('A','P','R','N') ");
		queryString.append(buidQuery);
		queryString.append(" GROUP BY CGSTIN,TAX_PERIOD,ACTION_TAKEN ");
		queryString.append("UNION ALL ");
		queryString.append("SELECT  CGSTIN CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("CASE WHEN ACTION_TAKEN IN ('A') THEN 'ACCEPT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('P') THEN 'PENDING' ");
		queryString.append("WHEN ACTION_TAKEN IN ('R','R1U1','R1U2') ");
		queryString.append("THEN 'REJECT' ");
		queryString.append("WHEN ACTION_TAKEN IN ('N') THEN 'NO_ACTION' ");
		queryString.append("END AS USER_RESPONS_GSTN,");
		queryString.append("'' AS TTYPE,");
		queryString.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE_GSTN,0 ");
		queryString.append("AS TOTAL_TAX_GSTN ");
		queryString.append("FROM GETANX2_SEZWOP_HEADER ");
		queryString.append("WHERE IS_DELETE = FALSE AND ACTION_TAKEN ");
		queryString.append("IN ('A','P','R','N') ");
		queryString.append(buidQuery);
		queryString.append(" GROUP BY CGSTIN,TAX_PERIOD,ACTION_TAKEN ");
		queryString.append("UNION ALL ");
		queryString.append("SELECT CGSTIN CUST_GSTIN_DIGI_GST,TAX_PERIOD,");
		queryString.append("''  AS USER_RESPONS_GSTN,'ISDC' AS TTYPE,");
		queryString.append("0 AS TAXABLE_VALUE_GSTN,");
		queryString.append("SUM(IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)");
		queryString.append("+IFNULL(SGST_AMT,0)) AS TOTAL_TAX_GSTN ");
		queryString.append("FROM  GETANX2_ISDC_HEADER ");
		queryString.append("WHERE IS_DELETE = FALSE ");
		queryString.append(buidQuery);
		queryString.append(" GROUP BY CGSTIN,TAX_PERIOD ");
		queryString.append(")");
		queryString.append(" GROUP BY CUST_GSTIN_DIGI_GST,TAX_PERIOD");
		queryString.append(")");
		queryString.append(" GROUP BY CUST_GSTIN_DIGI_GST,TAX_PERIOD ");
		queryString.append("ORDER BY CUST_GSTIN_DIGI_GST,TAX_PERIOD");
		return queryString.toString();
	}

}
