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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1SummaryLevelDataDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Anx1SummaryLevelSummaryDaoImpl")
public class Anx1SummaryLevelSummaryDaoImpl
		implements Anx1TransLevelSummaryDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1SummaryLevelSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx1TransReport(SearchCriteria criteria) {
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
				buildQuery.append(" AND HDR.GSTIN IN :gstinList");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
		}

		String queryStr = creategstnTransQueryString(buildQuery.toString());
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
		return list.parallelStream().map(o -> convertSummaryLevel(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1SummaryLevelDataDto convertSummaryLevel(Object[] arr) {
		Anx1SummaryLevelDataDto obj = new Anx1SummaryLevelDataDto();

		obj.setgSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setTransactionType(arr[2] != null ? arr[2].toString() : null);
		obj.setTableType(arr[3] != null ? arr[3].toString() : null);
		obj.setTableNo(arr[4] != null ? arr[4].toString() : null);
		obj.setCounterPartyGSTINUIN(arr[5] != null ? arr[5].toString() : null);
		obj.setPos(arr[6] != null ? arr[6].toString() : null);
		obj.setHsn(arr[7] != null ? arr[7].toString() : null);
		obj.setTaxRate(arr[8] != null ? arr[8].toString() : null);
		obj.setTotalSupplies(arr[9] != null ? arr[9].toString() : null);
		obj.setSuppliesReturned(arr[10] != null ? arr[10].toString() : null);
		obj.setNetSupplies(arr[11] != null ? arr[11].toString() : null);
		obj.setTaxableValue(arr[12] != null ? arr[12].toString() : null);
		obj.setiGSTAmount(arr[13] != null ? arr[13].toString() : null);
		obj.setcGSTAmount(arr[14] != null ? arr[14].toString() : null);
		obj.setsGSTUTGSTAmount(arr[15] != null ? arr[15].toString() : null);
		obj.setCessAmount(arr[16] != null ? arr[16].toString() : null);
		obj.setDifferentialPercentage(
				arr[17] != null ? arr[17].toString() : null);
		obj.setSection7Supply(arr[18] != null ? arr[18].toString() : null);
		obj.setRefundelg(arr[19] != null ? arr[19].toString() : null);

		return obj;
	}

	private String creategstnTransQueryString(String buildQuery) {

		return "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TRANSACTION_TYPE,TABLE_TYPE,"
				+ "TABLE_NUM,COUNTER_PARTY_GSTIN,POS,HSN,TAX_RATE,TOTAL_SUPPLIES,"
				+ "SUPPLIES_RETURNED,NET_SUPPLIES, TAXABLE_VALUE,IGST_AMT,"
				+ "CGST_AMT,SGST_AMT,CESS_AMT, DIFF_PERCENT,SEC7ACT,"
				+ "RFNDELG FROM ( SELECT HDR.GSTIN SUPPLIER_GSTIN,"
				+ "HDR.TAX_PERIOD RETURN_PERIOD,'OUTWARD' TRANSACTION_TYPE, "
				+ " 'B2C - 3A' TABLE_TYPE, '3A' TABLE_NUM, '' AS COUNTER_PARTY_GSTIN,"
				+ "HDR.POS,'' HSN,ITM.TAX_RATE,NULL TOTAL_SUPPLIES,"
				+ "NULL SUPPLIES_RETURNED,NULL NET_SUPPLIES,SUM(ITM.TAXABLE_VALUE) "
				+ "TAXABLE_VALUE,SUM(ITM.IGST_AMT) IGST_AMT,SUM(ITM.CGST_AMT) CGST_AMT,"
				+ "SUM(ITM.SGST_AMT) SGST_AMT,SUM(ITM.CESS_AMT) CESS_AMT,"
				+ "HDR.DIFF_PERCENT, HDR.SEC7ACT,RFNDELG FROM GETANX1_B2C_HEADER "
				+ "HDR INNER JOIN GETANX1_B2C_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "WHERE HDR.IS_DELETE = FALSE GROUP BY HDR.GSTIN,"
				+ "HDR.TAX_PERIOD,HDR.POS,ITM.TAX_RATE,HDR.DIFF_PERCENT,"
				+ "HDR.SEC7ACT,RFNDELG UNION ALL SELECT HDR.GSTIN SUPPLIER_GSTIN,"
				+ "HDR.TAX_PERIOD RETURN_PERIOD,'INWARD' TRANSACTION_TYPE,"
				+ " 'B2C - 3H' TABLE_TYPE,'3H' TABLE_NUM,HDR.CTIN AS "
				+ "COUNTER_PARTY_GSTIN, HDR.POS,'' HSN,ITM.TAX_RATE,"
				+ "NULL TOTAL_SUPPLIES, NULL SUPPLIES_RETURNED,NULL NET_SUPPLIES,"
				+ "SUM(ITM.TAXABLE_VALUE) TAXABLE_VALUE,SUM(ITM.IGST_AMT) "
				+ "IGST_AMT, SUM(ITM.CGST_AMT) CGST_AMT,SUM(ITM.SGST_AMT) "
				+ "SGST_AMT, SUM(ITM.CESS_AMT) CESS_AMT,HDR.DIFF_PERCENT,"
				+ "HDR.SEC7ACT,'' RFNDELG FROM GETANX1_REV_HEADER HDR INNER JOIN "
				+ "GETANX1_REV_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "WHERE HDR.IS_DELETE = FALSE "
				+ " GROUP BY "
				+ "HDR.GSTIN, HDR.TAX_PERIOD,HDR.CTIN,HDR.POS,ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT, HDR.SEC7ACT "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'INWARD' TRANSACTION_TYPE, 'B2C - 3I' TABLE_TYPE,'3I' TABLE_NUM,"
				+ " '' AS COUNTER_PARTY_GSTIN, HDR.POS,'' HSN,ITM.TAX_RATE,NULL TOTAL_SUPPLIES,"
				+ "NULL SUPPLIES_RETURNED,NULL NET_SUPPLIES,"
				+ "SUM(ITM.TAXABLE_VALUE) TAXABLE_VALUE,SUM(ITM.IGST_AMT) "
				+ "IGST_AMT,NULL CGST_AMT,NULL SGST_AMT,SUM(ITM.CESS_AMT) "
				+ "CESS_AMT, NULL DIFF_PERCENT,'' SEC7ACT,'' RFNDELG FROM "
				+ "GETANX1_IMPS_HEADER HDR INNER JOIN GETANX1_IMPS_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "WHERE HDR.IS_DELETE = FALSE "
				+ " GROUP BY "
				+ "HDR.GSTIN,HDR.TAX_PERIOD,HDR.POS,ITM.TAX_RATE "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE, 'ECOM - 4' TABLE_TYPE,'4' TABLE_NUM, "
				+ " '' AS COUNTER_PARTY_GSTIN, '' POS,'' HSN,NULL TAX_RATE,"
				+ "SUPP_MADE_VALUE TOTAL_SUPPLIES,SUPP_RET_VALUE SUPPLIES_RETURNED,"
				+ "SUPP_NET_VALUE NET_SUPPLIES,NULL TAXABLE_VALUE,"
				+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
				+ "SUM(SGST_AMT) SGST_AMT, SUM(CESS_AMT) CESS_AMT,"
				+ "NULL DIFF_PERCENT,'' SEC7ACT,'' RFNDELG FROM GETANX1_ECOM_DETAILS "
				+ "HDR WHERE HDR.IS_DELETE = FALSE "
                + buildQuery
				+ " GROUP BY "
				+ "HDR.GSTIN,HDR.TAX_PERIOD,SUPP_MADE_VALUE,"
				+ "SUPP_RET_VALUE,SUPP_NET_VALUE) ";

	}
}