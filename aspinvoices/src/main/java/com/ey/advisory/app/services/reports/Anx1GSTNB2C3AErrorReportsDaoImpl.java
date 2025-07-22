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

import com.ey.advisory.app.data.views.client.Anx1GSTNB2C3AErrorReportsView;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Anx1GSTNB2C3AErrorReportsDaoImpl")
public class Anx1GSTNB2C3AErrorReportsDaoImpl implements Anx1GstnErrorDao {

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

		String queryStr = GSTNB2C3AErrorQueryString(buildQuery.toString());
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
		return list.parallelStream().map(o -> convertgstnB2CError(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1GSTNB2C3AErrorReportsView convertgstnB2CError(Object[] arr) {
		Anx1GSTNB2C3AErrorReportsView obj = new Anx1GSTNB2C3AErrorReportsView();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setPos(arr[1] != null ? arr[1].toString() : null);
		obj.setTaxRate(arr[2] != null ? arr[2].toString() : null);
		obj.setTaxableValue(arr[3] != null ? arr[3].toString() : null);
		obj.setIntegTaxAmt(arr[4] != null ? arr[4].toString() : null);
		obj.setCentralTaxAmt(arr[5] != null ? arr[5].toString() : null);
		obj.setStateUTTaxAmt(arr[6] != null ? arr[6].toString() : null);
		obj.setCessAmt(arr[7] != null ? arr[7].toString() : null);
		obj.setDiffPerFlag(arr[8] != null ? arr[8].toString() : null);
		obj.setAutoPopltToRefund(arr[9] != null ? arr[9].toString() : null);
		obj.setSec7ofIGSTFlag(arr[10] != null ? arr[10].toString() : null);
		obj.setgSTNErrorCode(arr[11] != null ? arr[11].toString() : null);
		obj.setgSTNErrorMessage(arr[12] != null ? arr[12].toString() : null);
		obj.setRefID(arr[13] != null ? arr[13].toString() : null);
		obj.setRefDate(arr[14] != null ? arr[14].toString() : null);

		return obj;
	}

	private String GSTNB2C3AErrorQueryString(String buildQuery) {
		return "SELECT HT.SUPPLIER_GSTIN,HT.POS,TAX_RATE,"
				+ "SUM(IT.TAXABLE_VALUE) TAXABLE_VALUE,"
				+ "SUM(IT.IGST_AMT) IGST_AMT,SUM(IT.CGST_AMT) CGST_AMT,"
				+ "SUM(IT.SGST_AMT) SGST_AMT,SUM(IT.CESS_AMT_SPECIFIC) "
				+ "CESS_AMT,HT.DIFF_PERCENT,HT.AUTOPOPULATE_TO_REFUND,"
				+ "HT.SECTION7_OF_IGST_FLAG,"
				+ "STRING_AGG((CASE WHEN ERT.ERROR_TYPE='ERR'  "
				+ "THEN ERT.ERROR_CODE END),',' ) AS GSTN_ERROR_CODE,"
				+ "STRING_AGG((CASE WHEN ERT.ERROR_TYPE='ERR'  "
				+ "THEN ERT.ERROR_DESCRIPTION END),',' ) AS GSTN_ERROR_DESCRIPTION,"
				+ "GSTN_SAVE_REF_ID AS REFID,"
				+ "GSTN_RESP_DATE AS REFDT FROM ANX_OUTWARD_DOC_HEADER HT "
				+ "INNER JOIN ANX_OUTWARD_DOC_ITEM IT "
				+ "ON HT.ID = IT.DOC_HEADER_ID "
				+ "AND HT.DERIVED_RET_PERIOD = IT.DERIVED_RET_PERIOD "
				+ "AND UCASE(HT.AN_TAX_DOC_TYPE) = UCASE('B2C') "
				+ "AND HT.GSTN_ERROR = TRUE INNER JOIN "
				+ "ANX_OUTWARD_DOC_ERROR ERT ON HT.ID = ERT.DOC_HEADER_ID "
				+ "AND IT.DOC_HEADER_ID = ERT.DOC_HEADER_ID "
				+ "INNER JOIN GSTR1_GSTN_SAVE_BATCH GSB "
				+ "ON GSB.ID = HT.BATCH_ID " + buildQuery
				+ " GROUP BY HT.SUPPLIER_GSTIN,HT.POS,TAX_RATE,"
				+ "HT.DIFF_PERCENT,HT.AUTOPOPULATE_TO_REFUND,"
				+ "HT.SECTION7_OF_IGST_FLAG,GSTN_SAVE_REF_ID,GSTN_RESP_DATE "
				+ " ORDER BY 1 ";

	}
}
