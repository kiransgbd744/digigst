package com.ey.advisory.app.services.daos.get2a;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Anx2GetProcessedRequestDto;
import com.ey.advisory.app.docs.dto.Anx2GetProcessedResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("AnxGet2aprocessedDaoImpl")
public class AnxGet2aprocessedDaoImpl implements AnxGet2aprocessedDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Override
	public List<Anx2GetProcessedResponseDto> getAnx2Get2aProcessedData(
			Anx2GetProcessedRequestDto criteria) {

		String taxperiod = criteria.getTaxPeriod();
		List<String> docType = criteria.getDocType();
		List<String> recordType = criteria.getRecordType();

		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();

		String gstn = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstn = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		StringBuilder buildQuery = new StringBuilder();

		if (gstn != null && !gstn.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			buildQuery.append(" AND CGSTIN IN :gstinList");
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
		}

		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND INV_TYPE IN :docType ");
		}
		if (recordType != null && !recordType.isEmpty()) {
			buildQuery.append(" AND TABLE_SECTION IN :recordType ");
		}

		String queryStr = createGetProcessedQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (gstn != null && !gstn.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(taxperiod);
			q.setParameter("taxperiod", derivedRetPeriod);
		}
		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}
		if (recordType != null && !recordType.isEmpty()) {
			q.setParameter("recordType", recordType);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertGetProcessedData(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx2GetProcessedResponseDto convertGetProcessedData(Object[] arr) {
		Anx2GetProcessedResponseDto obj = new Anx2GetProcessedResponseDto();

		BigInteger count = GenUtil.getBigInteger(arr[0]);
		obj.setCount(count.intValue());
		obj.setState((String) arr[1]);
		String GSTIN = (String) arr[2];
		obj.setGstin(GSTIN);
		BigDecimal taxableVal = BigDecimal
				.valueOf(Double.valueOf(arr[3].toString()));
		obj.setTaxableValue(taxableVal);
		BigDecimal ivnVal = BigDecimal
				.valueOf(Double.valueOf(arr[4].toString()));
		obj.setInvValue(ivnVal);
		BigDecimal igst = BigDecimal.valueOf(Double.valueOf(arr[5].toString()));
		obj.setIGST(igst);
		BigDecimal cgst = BigDecimal.valueOf(Double.valueOf(arr[6].toString()));
		obj.setCGST(cgst);
		BigDecimal sgst = BigDecimal.valueOf(Double.valueOf(arr[7].toString()));
		obj.setSGST(sgst);
		BigDecimal cess = BigDecimal.valueOf(Double.valueOf(arr[8].toString()));
		obj.setCess(cess);
		BigDecimal tiTax = BigDecimal
				.valueOf(Double.valueOf(arr[9].toString()));
		obj.setTiTax(tiTax);
		obj.setHighlight(true);
		obj.setLastUpdated(LocalDateTime.now());
		obj.setStatus("Success");
		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin(GSTIN);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				obj.setAuthToken("Active");
			} else {
				obj.setAuthToken("Inactive");
			}
		} else {
			obj.setAuthToken("Inactive");
		}

		return obj;
	}

	private String createGetProcessedQueryString(String buildQuery) {
		return "SELECT count(ID) AS ID_COUNT,STATE,CUST_GSTIN, (SUM(IFNULL"
				+ "(TAXABLE_VALUE2,'0'))-SUM(IFNULL(TAXABLE_VALUE1,'0'))) AS "
				+ "TAXABLE_VALUE,(SUM(IFNULL(SUPPLIER_INV_VAL2,'0'))- "
				+ "SUM(IFNULL(SUPPLIER_INV_VAL1,'0'))) AS INVOICE_VALUE, "
				+ "(SUM(IFNULL(IGST_AMT2,'0'))-SUM(IFNULL(IGST_AMT1,'0'))) "
				+ "AS IGST_AMT, (SUM(IFNULL(CGST_AMT2,'0'))-SUM(IFNULL"
				+ "(CGST_AMT1,'0'))) AS CGST_AMT, (SUM (IFNULL(SGST_AMT2,'0'))-"
				+ "SUM(IFNULL(SGST_AMT1,'0')))AS SGST_AMT, (SUM (IFNULL"
				+ "(CESS_AMT2,'0'))-SUM(IFNULL(CESS_AMT1,'0'))) AS CESS_AMT , "
				+ "(SUM(IFNULL(IGST_AMT2,'0') +IFNULL(CGST_AMT2,'0')+ "
				+ "IFNULL(SGST_AMT2,'0')+ IFNULL(CESS_AMT2,'0'))- "
				+ "SUM(IFNULL(IGST_AMT1,'0') +IFNULL(CGST_AMT1,'0')+ "
				+ "IFNULL(SGST_AMT1,'0')+IFNULL(CESS_AMT1,'0'))) AS TOTAL_TAX "
				+ "FROM ( SELECT INV_TYPE, TAX_PERIOD,ID, CGSTIN AS CUST_GSTIN,"
				+ "fnGetState(CGSTIN) AS STATE, CASE WHEN INV_TYPE IN ('CR','C') "
				+ "THEN SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL1, "
				+ "CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL2, "
				+ "CASE WHEN INV_TYPE IN ('CR','C') THEN SUM(IFNULL(TAXABLE_VALUE,'0')) "
				+ "END AS TAXABLE_VALUE1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(TAXABLE_VALUE,'0')) END AS TAXABLE_VALUE2, "
				+ "CASE WHEN INV_TYPE IN ('CR','C') THEN SUM(IFNULL(IGST_AMT,'0')) "
				+ "END AS IGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(IGST_AMT,'0')) END AS IGST_AMT2, CASE WHEN "
				+ "INV_TYPE IN ('CR','C') THEN SUM(IFNULL(CGST_AMT,'0')) END AS "
				+ "CGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(CGST_AMT,'0')) END AS CGST_AMT2, CASE WHEN "
				+ "INV_TYPE IN ('CR','C') THEN SUM(IFNULL(SGST_AMT,'0')) END AS "
				+ "SGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(SGST_AMT,'0')) END AS SGST_AMT2, CASE WHEN "
				+ "INV_TYPE IN ('CR','C') THEN SUM(IFNULL(CESS_AMT,'0')) END "
				+ "AS CESS_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(CESS_AMT,'0')) END AS CESS_AMT2, TABLE_SECTION "
				+ "FROM GETANX2_B2B_HEADER WHERE IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY "
				+ "INV_TYPE,TAX_PERIOD,ID,CGSTIN,TABLE_SECTION "
				+ "UNION ALL "
				+ "SELECT INV_TYPE, TAX_PERIOD,ID,CGSTIN AS CUST_GSTIN, "
				+ "fnGetState(CGSTIN) AS STATE,CASE WHEN INV_TYPE IN ('CR','C') "
				+ "THEN SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL1, "
				+ "CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL2, "
				+ "CASE WHEN INV_TYPE IN ('CR','C') THEN SUM(IFNULL(TAXABLE_VALUE,'0')) "
				+ "END AS TAXABLE_VALUE1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(TAXABLE_VALUE,'0')) END AS TAXABLE_VALUE2, CASE "
				+ "WHEN INV_TYPE IN ('CR','C') THEN SUM(IFNULL(IGST_AMT,'0')) "
				+ "END AS IGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(IGST_AMT,'0')) END AS IGST_AMT2, CASE WHEN "
				+ "INV_TYPE IN ('CR','R') THEN SUM(IFNULL(CGST_AMT,'0')) END AS "
				+ "CGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(CGST_AMT,'0')) END AS CGST_AMT2, CASE WHEN "
				+ "INV_TYPE IN ('CR','C') THEN SUM(IFNULL(SGST_AMT,'0')) END AS "
				+ "SGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(SGST_AMT,'0')) END AS SGST_AMT2, CASE WHEN "
				+ "INV_TYPE IN ('CR','C') THEN SUM(IFNULL(CESS_AMT,'0')) END "
				+ "AS CESS_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(CESS_AMT,'0')) END AS CESS_AMT2,TABLE_SECTION FROM "
				+ "GETANX2_DE_HEADER WHERE IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY INV_TYPE,"
				+ "TAX_PERIOD,ID,CGSTIN,TABLE_SECTION "
				+ "UNION ALL "
				+ "SELECT INV_TYPE,TAX_PERIOD,ID,CGSTIN AS CUST_GSTIN, "
				+ "fnGetState(CGSTIN) AS STATE,CASE WHEN INV_TYPE IN ('CR','C') THEN "
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL1, "
				+ "CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL2, "
				+ "CASE WHEN INV_TYPE IN ('CR','C') THEN SUM(IFNULL(TAXABLE_VALUE,'0')) "
				+ "END AS TAXABLE_VALUE1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(TAXABLE_VALUE,'0')) END AS TAXABLE_VALUE2, CASE "
				+ "WHEN INV_TYPE IN ('CR','C') THEN SUM(IFNULL(IGST_AMT,'0')) "
				+ "END AS IGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(IGST_AMT,'0')) END AS IGST_AMT2, CASE WHEN "
				+ "INV_TYPE IN ('CR','C') THEN SUM(IFNULL(CGST_AMT,'0')) END "
				+ "AS CGST_AMT1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(CGST_AMT,'0')) END AS CGST_AMT2, CASE WHEN INV_TYPE "
				+ "IN ('CR','C') THEN SUM(IFNULL(SGST_AMT,'0')) END AS SGST_AMT1, "
				+ "CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(SGST_AMT,'0')) END AS SGST_AMT2, CASE WHEN INV_TYPE "
				+ "IN ('CR','C') THEN SUM(IFNULL(CESS_AMT,'0')) END AS CESS_AMT1, "
				+ "CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(CESS_AMT,'0')) END AS CESS_AMT2, TABLE_SECTION "
				+ "FROM GETANX2_SEZWP_HEADER WHERE IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY "
				+ "INV_TYPE,TAX_PERIOD,ID,CGSTIN,TABLE_SECTION "
				+ "UNION ALL "
				+ "SELECT INV_TYPE, TAX_PERIOD,ID,CGSTIN AS CUST_GSTIN, "
				+ "fnGetState(CGSTIN) AS STATE,CASE WHEN INV_TYPE IN ('CR','C') THEN "
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL1, "
				+ "CASE WHEN INV_TYPE IN ('INV','DR','I','D') THEN "
				+ "SUM(IFNULL(SUPPLIER_INV_VAL,'0')) END AS SUPPLIER_INV_VAL2, "
				+ "CASE WHEN INV_TYPE IN ('CR','C') THEN SUM(IFNULL(TAXABLE_VALUE,'0')) "
				+ "END AS TAXABLE_VALUE1, CASE WHEN INV_TYPE IN ('INV','DR','I','D') "
				+ "THEN SUM(IFNULL(TAXABLE_VALUE,'0')) END AS TAXABLE_VALUE2, '0' "
				+ "AS IGST_AMT1,'0' AS IGST_AMT2,'0' AS CGST_AMT1, '0' AS "
				+ "CGST_AMT2,'0' AS SGST_AMT1,'0' AS SGST_AMT2, '0' AS CESS_AMT1,"
				+ "'0' AS CESS_AMT2,TABLE_SECTION FROM GETANX2_SEZWOP_HEADER WHERE "
				+ "IS_DELETE = FALSE " + buildQuery
				+ " GROUP BY INV_TYPE,TAX_PERIOD,ID,CGSTIN,TABLE_SECTION ) "
				+ "GROUP BY STATE,CUST_GSTIN ";
	}

}
