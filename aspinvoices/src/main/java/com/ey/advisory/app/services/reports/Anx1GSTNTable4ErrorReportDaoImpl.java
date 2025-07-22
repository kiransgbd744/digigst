/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1GSTNTable4ErrorReportsView;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Anx1GSTNTable4ErrorReportDaoImpl")
public class Anx1GSTNTable4ErrorReportDaoImpl implements Anx1GstnErrorDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx1GstnErrorReport(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String GSTIN = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HT.SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" WHERE HT.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = GSTNTable4ErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertGSTNtable4Error(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1GSTNTable4ErrorReportsView convertGSTNtable4Error(
			Object[] arr) {
		Anx1GSTNTable4ErrorReportsView obj = new Anx1GSTNTable4ErrorReportsView();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setEcomGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSuppliesMade(arr[2] != null ? arr[2].toString() : null);
		obj.setSuppliesReturned(arr[3] != null ? arr[3].toString() : null);
		obj.setNetSupplies(arr[4] != null ? arr[4].toString() : null);
		obj.setIntegTaxAmt(arr[5] != null ? arr[5].toString() : null);
		obj.setCentralTaxAmt(arr[6] != null ? arr[6].toString() : null);
		obj.setStateUTTaxAmt(arr[7] != null ? arr[7].toString() : null);
		obj.setCessAmt(arr[8] != null ? arr[8].toString() : null);
		obj.setgSTNErrorCode(arr[9] != null ? arr[9].toString() : null);
		obj.setgSTNErrorMessage(arr[10] != null ? arr[10].toString() : null);
		obj.setRefID(arr[11] != null ? arr[11].toString() : null);
		obj.setRefDate(arr[12] != null ? arr[12].toString() : null);
		return obj;
	}

	private String GSTNTable4ErrorQueryString(String buildQuery) {
		return "SELECT HT.SUPPLIER_GSTIN,ECOM_GSTIN,ECOM_VAL_SUPMADE,"
				+ "ECOM_VAL_SUPRET,ECOM_NETVAL_SUP,SUM(IGST_AMT) IGST_AMT,"
				+ "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT,"
				+ "SUM(CGST_AMT) CESS_AMT,"
				+ "STRING_AGG((CASE WHEN ERT.ERROR_TYPE='ERR'  "
				+ "THEN ERT.ERROR_CODE END),',' ) AS GSTN_ERROR_CODE,"
				+ "STRING_AGG((CASE WHEN ERT.ERROR_TYPE='ERR'  "
				+ "THEN ERT.ERROR_DESCRIPTION END),',' ) "
				+ "AS GSTN_ERROR_DESCRIPTION," + "GSTN_SAVE_REF_ID AS REFID,"
				+ "GSTN_RESP_DATE AS REFDT FROM ANX_PROCESSED_TABLE4 HT "
				+ "INNER JOIN ANX_OUTWARD_DOC_ERROR ERT "
				+ "ON HT.ID = ERT.DOC_HEADER_ID "
				+ "AND HT.GSTN_ERROR = TRUE INNER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSB ON GSB.ID = HT.BATCH_ID "
				+ "AND HT.GSTN_ERROR = TRUE "
				+ "AND HT.DERIVED_RET_PERIOD = GSB.DERIVED_RET_PERIOD " 
				+ buildQuery
				+ " GROUP BY HT.SUPPLIER_GSTIN,ECOM_GSTIN,ECOM_VAL_SUPMADE,"
				+ "ECOM_VAL_SUPRET,ECOM_NETVAL_SUP,"
				+ "GSTN_SAVE_REF_ID,GSTN_RESP_DATE";

	}

}
