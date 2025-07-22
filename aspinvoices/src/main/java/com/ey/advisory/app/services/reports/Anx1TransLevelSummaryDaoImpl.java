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

import com.ey.advisory.app.data.views.client.Anx1TransactionalLevelDataDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Anx1TransLevelSummaryDaoImpl")
public class Anx1TransLevelSummaryDaoImpl implements Anx1TransLevelSummaryDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1TransLevelSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getAnx1TransReport(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String purchase = null;
		String division = null;
		String location = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String GSTIN = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purchaseList = null;
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
				buildQuery.append(" WHERE SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
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
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Anx1TransactionalLevelDataDto convertTransactionalLevel(
			Object[] arr) {
		Anx1TransactionalLevelDataDto obj = new Anx1TransactionalLevelDataDto();

		obj.setgSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setTransactionType(arr[2] != null ? arr[2].toString() : null);
		obj.setTableType(arr[3] != null ? arr[3].toString() : null);
		obj.setTableNo(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		//
		obj.setDocumentNumber(arr[7] != null ? arr[7].toString() : null);
		obj.setDocumentDate(arr[8] != null ? arr[8].toString() : null);
		obj.setCounterPartyGSTINUIN(arr[9] != null ? arr[9].toString() : null);
		obj.setActionByCounterParty(
				arr[10] != null ? arr[10].toString() : null);
		obj.setOriginalCounterPartyGSTINUIN(
				arr[11] != null ? arr[11].toString() : null);
		obj.setOriginalDocumentType(
				arr[12] != null ? arr[12].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[13] != null ? arr[13].toString() : null);
		obj.setOriginalDocumentDate(
				arr[14] != null ? arr[14].toString() : null);
		obj.setPortCode(arr[15] != null ? arr[15].toString() : null);
		obj.setShippingbillNumber(arr[16] != null ? arr[16].toString() : null);
		obj.setShippingbillDate(arr[17] != null ? arr[17].toString() : null);
		obj.setLineNumber(arr[18] != null ? arr[18].toString() : null);
		obj.setHsn(arr[19] != null ? arr[19].toString() : null);
		obj.setTaxRate(arr[20] != null ? arr[20].toString() : null);
		obj.setTaxableValue(arr[21] != null ? arr[21].toString() : null);
		obj.setiGSTAmount(arr[22] != null ? arr[22].toString() : null);
		obj.setcGSTAmount(arr[23] != null ? arr[23].toString() : null);
		obj.setsGSTUTGSTAmount(arr[24] != null ? arr[24].toString() : null);
		obj.setCessAmount(arr[25] != null ? arr[25].toString() : null);
		obj.setInvoiceValue(arr[26] != null ? arr[26].toString() : null);
		obj.setPos(arr[27] != null ? arr[27].toString() : null);
		obj.setDifferentialPercentage(
				arr[28] != null ? arr[28].toString() : null);
		obj.setAutoPopulateToRefund(
				arr[29] != null ? arr[29].toString() : null);
		obj.setSection7Supply(arr[30] != null ? arr[30].toString() : null);
		obj.setSupplierClaimingRefund(
				arr[31] != null ? arr[31].toString() : null);
		obj.setAmended(arr[32] != null ? arr[32].toString() : null);
		obj.setAmendmentPeriod(arr[33] != null ? arr[33].toString() : null);
		obj.setAmendmentType(arr[34] != null ? arr[34].toString() : null);
		obj.setInvalid(arr[35] != null ? arr[35].toString() : null);
		obj.setTablereference(arr[36] != null ? arr[36].toString() : null);

		return obj;
	}

	private String creategstnTransQueryString(String buildQuery) {

		return "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TRANSACTION_TYPE,"
				+ "TABLE_TYPE,TABLE_NUM,DOC_TYPE,TYPE,DOC_NUMBER,DOC_DATE,"
				+ "COUNTER_PARTY_GSTIN,ACTION_BY_COUNTER_PARTY,"
				+ "ORG_CNTR_PARTY_GSTINUIN,ORG_DOC_TYPE,ORG_DOC_NUM,ORG_DOC_DATE,"
				+ "PORT_CODE,SHIPP_BILL_NUM, SHIPP_BILL_DATE,LINE_NUMBER,HSN,"
				+ "TAX_RATE,TAXABLE_VALUE, IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "INV_VAL,POS,DIFF_PERCENT, AUTOPAPULATE_REFUND,SEC7ACT,SUPP_CLM_REFUND,"
				+ "AMENDED,AMENDED_PERIOD,AMENDED_TYPE,INVALID,"
				+ "TABLE_REFERENCE,IS_FILED,DERIVED_RET_PERIOD "
				+ "FROM ( SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD, "
				+ " 'OUTWARD' TRANSACTION_TYPE,'B2B' TABLE_TYPE,'3B' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE,HDR.CTIN "
				+ "AS COUNTER_PARTY_GSTIN, HDR.ACTION ACTION_BY_COUNTER_PARTY,"
				+ " '' AS ORG_CNTR_PARTY_GSTINUIN,'' AS ORG_DOC_TYPE, "
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE,'' PORT_CODE, "
				+ " '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,'' LINE_NUMBER,"
				+ "ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT,HDR.DOC_AMT INV_VAL,"
				+ "HDR.POS,HDR.DIFF_PERCENT, HDR.RFNDELG as AUTOPAPULATE_REFUND,"
				+ "HDR.SEC7ACT, '' SUPP_CLM_REFUND,NULL AS AMENDED,"
				+ " '' AS AMENDED_PERIOD,'' AS AMENDED_TYPE,'' AS INVALID, "
				+ " 'B2B' AS TABLE_REFERENCE, BT.IS_FILED,HDR.DERIVED_RET_PERIOD "
				+ "FROM GETANX1_B2B_HEADER HDR INNER JOIN GETANX1_B2B_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE,'B2BA' TABLE_TYPE,'3B' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE,"
				+ "HDR.CTIN AS COUNTER_PARTY_GSTIN, HDR.ACTION ACTION_BY_COUNTER_PARTY,"
				+ "HDR.ORG_CTIN AS ORG_CNTR_PARTY_GSTINUIN,HDR.ORG_DOC_TYPE,"
				+ "HDR.ORG_DOC_NUMBER,HDR.ORG_DOC_DATE,'' PORT_CODE, "
				+ " '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE, '' LINE_NUMBER,"
				+ "ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT,HDR.DOC_AMT INV_VAL,"
				+ "HDR.POS,HDR.DIFF_PERCENT,HDR.RFNDELG AS AUTOPAPULATE_REFUND,"
				+ "HDR.SEC7ACT, '' SUPP_CLM_REFUND,IS_AMENDED AS AMENDED,"
				+ "AMD_PERIOD AS AMENDED_PERIOD,AMD_TYPE AS AMENDED_TYPE, '' AS INVALID,"
				+ " 'B2BA' AS TABLE_REFERENCE, BT.IS_FILED,HDR.DERIVED_RET_PERIOD "
				+ "FROM GETANX1_B2BA_HEADER HDR INNER JOIN GETANX1_B2BA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT "
				+ "ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE "
				+ "AND BT.IS_DELETE = FALSE UNION SELECT HDR.GSTIN SUPPLIER_GSTIN,"
				+ "HDR.TAX_PERIOD RETURN_PERIOD, 'OUTWARD' TRANSACTION_TYPE,"
				+ " 'EXPWP' TABLE_TYPE,'3C' TABLE_TYPE, HDR.DOC_TYPE,'' TYPE,"
				+ "HDR.INV_NUMBER DOC_NUMBER, HDR.DOC_DATE,'' AS COUNTER_PARTY_GSTIN,"
				+ " '' ACTION_BY_COUNTER_PARTY,'' AS ORG_CNTR_PARTY_GSTINUIN, "
				+ " '' AS ORG_DOC_TYPE, '' ORG_DOC_NUM,'' ORG_DOC_DATE,PORT_CODE,"
				+ "SHIP_BILL_NUM SHIPP_BILL_NUM, SHIPP_DATE SHIPP_BILL_DATE, "
				+ " '' LINE_NUMBER,ITM.HSN, ITM.TAX_RATE,ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_AMT,NULL CGST_AMT,NULL SGST_AMT,ITM.CESS_AMT,"
				+ "HDR.DOC_AMT INV_VAL,'' POS, NULL DIFF_PERCENT,HDR.RFNDELG "
				+ "AS AUTOPAPULATE_REFUND, '' SEC7ACT, '' SUPP_CLM_REFUND,"
				+ "NULL AS AMENDED, '' AS AMENDED_PERIOD,'' AMENDED_TYPE,"
				+ "'' AS INVALID, 'EXPWP' AS TABLE_REFERENCE,BT.IS_FILED,"
				+ "HDR.DERIVED_RET_PERIOD FROM GETANX1_EXPWP_HEADER HDR INNER JOIN "
				+ "GETANX1_EXPWP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE, 'EXPWOP' TABLE_TYPE,"
				+ " '3D' TABLE_TYPE, HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE,"
				+ " '' AS COUNTER_PARTY_GSTIN, '' ACTION_BY_COUNTER_PARTY, "
				+ " '' AS ORG_CNTR_PARTY_GSTINUIN,'' AS ORG_DOC_TYPE, "
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE,PORT_CODE, SHIP_BILL_NUM SHIPP_BILL_NUM,"
				+ "SHIPP_DATE SHIPP_BILL_DATE, '' LINE_NUMBER,ITM.HSN,ITM.TAX_RATE,"
				+ "ITM.TAXABLE_VALUE, NULL IGST_AMT,NULL CGST_AMT,NULL SGST_AMT,"
				+ "NULL CESS_AMT, HDR.DOC_AMT INV_VAL,'' POS,NULL DIFF_PERCENT,"
				+ "HDR.RFNDELG AS AUTOPAPULATE_REFUND,'' SEC7ACT, "
				+ " '' SUPP_CLM_REFUND,NULL AS AMENDED,'' AS AMENDED_PERIOD, "
				+ " '' AMENDED_TYPE,'' AS INVALID, 'EXPWOP' AS TABLE_REFERENCE,"
				+ "BT.IS_FILED,HDR.DERIVED_RET_PERIOD FROM GETANX1_EXPWOP_HEADER "
				+ "HDR INNER JOIN GETANX1_EXPWOP_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE,'SEZWP' TABLE_TYPE, "
				+ " '3E' TABLE_NUM, HDR.DOC_TYPE,'' TYPE, HDR.DOC_NUMBER,"
				+ "HDR.DOC_DATE,HDR.CTIN AS COUNTER_PARTY_GSTIN,"
				+ "HDR.ACTION ACTION_BY_COUNTER_PARTY, '' AS ORG_CNTR_PARTY_GSTINUIN,"
				+ " '' AS ORG_DOC_TYPE, '' ORG_DOC_NUM,'' ORG_DOC_DATE,'' PORT_CODE, "
				+ " '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,'' LINE_NUMBER,"
				+ "ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "NULL CGST_AMT,NULL SGST_AMT,ITM.CESS_AMT,HDR.DOC_AMT INV_VAL,"
				+ "HDR.POS,HDR.DIFF_PERCENT,HDR.RFNDELG AS AUTOPAPULATE_REFUND,"
				+ " '' SEC7ACT,CLMRFND SUPP_CLM_REFUND,NULL AS AMENDED, "
				+ " '' AS AMENDED_PERIOD,'' AS AMENDED_TYPE, "
				+ " '' AS INVALID,'SEZWP' AS TABLE_REFERENCE, BT.IS_FILED,"
				+ "HDR.DERIVED_RET_PERIOD FROM GETANX1_SEZWP_HEADER "
				+ "HDR INNER JOIN GETANX1_SEZWP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ "'OUTWARD' TRANSACTION_TYPE,'SEZWPA' TABLE_TYPE,'3E' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE, HDR.CTIN AS "
				+ "COUNTER_PARTY_GSTIN, HDR.ACTION ACTION_BY_COUNTER_PARTY,"
				+ "ORG_CTIN AS ORG_CNTR_PARTY_GSTINUIN,ORG_DOC_TYPE,"
				+ "ORG_DOC_NUMBER ORG_DOC_NUM,ORG_DOC_DATE,'' PORT_CODE,"
				+ " '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,'' LINE_NUMBER,"
				+ "ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "NULL CGST_AMT,NULL SGST_AMT,ITM.CESS_AMT, HDR.DOC_AMT INV_VAL,"
				+ "HDR.POS,NULL DIFF_PERCENT, HDR.RFNDELG AS AUTOPAPULATE_REFUND,"
				+ " '' SEC7ACT, '' SUPP_CLM_REFUND, IS_AMENDED AMENDED, AMD_PERIOD "
				+ "AS AMENDED_PERIOD,AMD_TYPE AS AMENDED_TYPE, '' AS INVALID,"
				+ " 'SEZWPA' AS TABLE_REFERENCE,BT.IS_FILED, HDR.DERIVED_RET_PERIOD "
				+ "FROM GETANX1_SEZWPA_HEADER HDR INNER JOIN GETANX1_SEZWPA_ITEM "
				+ "ITM ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE "
				+ "HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE,'SEZWOP' TABLE_TYPE, '3F' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE,"
				+ "HDR.CTIN AS COUNTER_PARTY_GSTIN, HDR.ACTION ACTION_BY_COUNTER_PARTY,"
				+ " '' AS ORG_CNTR_PARTY_GSTINUIN,'' AS ORG_DOC_TYPE, '' ORG_DOC_NUM,"
				+ " '' ORG_DOC_DATE,'' PORT_CODE, '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,"
				+ " '' LINE_NUMBER, ITM.HSN,ITM.TAX_RATE, ITM.TAXABLE_VALUE,"
				+ "NULL IGST_AMT,NULL CGST_AMT, NULL SGST_AMT,NULL CESS_AMT,"
				+ "HDR.DOC_AMT INV_VAL, HDR.POS, NULL DIFF_PERCENT,"
				+ "HDR.RFNDELG AS AUTOPAPULATE_REFUND, '' SEC7ACT,"
				+ " '' SUPP_CLM_REFUND,NULL AS AMENDED, '' AS AMENDED_PERIOD,"
				+ "'' AS AMENDED_TYPE, '' AS INVALID,'SEZWOP' AS TABLE_REFERENCE,"
				+ "BT.IS_FILED, HDR.DERIVED_RET_PERIOD FROM GETANX1_SEZWOP_HEADER "
				+ "HDR INNER JOIN GETANX1_SEZWOP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE,'SEZWOPA' TABLE_TYPE, '3F' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER, HDR.DOC_DATE,"
				+ "HDR.CTIN AS COUNTER_PARTY_GSTIN, HDR.ACTION ACTION_BY_COUNTER_PARTY,"
				+ "ORG_CTIN AS ORG_CNTR_PARTY_GSTINUIN,ORG_DOC_TYPE,"
				+ "ORG_DOC_NUMBER ORG_DOC_NUM,ORG_DOC_DATE,'' PORT_CODE,"
				+ "'' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,'' LINE_NUMBER,"
				+ "ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "NULL CGST_AMT,NULL SGST_AMT,ITM.CESS_AMT,HDR.DOC_AMT INV_VAL,"
				+ "HDR.POS,NULL DIFF_PERCENT,HDR.RFNDELG AS AUTOPAPULATE_REFUND,"
				+ "'' SEC7ACT, '' SUPP_CLM_REFUND,IS_AMENDED AMENDED,AMD_PERIOD AS AMENDED_PERIOD,"
				+ "AMD_TYPE AS AMENDED_TYPE, '' INVALID, 'SEZWOPA' AS TABLE_REFERENCE,"
				+ "BT.IS_FILED, HDR.DERIVED_RET_PERIOD FROM GETANX1_SEZWOPA_HEADER "
				+ "HDR INNER JOIN GETANX1_SEZWOPA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE,'DE' TABLE_TYPE,'3G' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE,"
				+ "HDR.CTIN AS COUNTER_PARTY_GSTIN,HDR.ACTION ACTION_BY_COUNTER_PARTY,"
				+ " '' AS ORG_CNTR_PARTY_GSTINUIN,'' ORG_DOC_TYPE, '' ORG_DOC_NUM,"
				+ " '' ORG_DOC_DATE,'' PORT_CODE,'' SHIPP_BILL_NUM, '' SHIPP_BILL_DATE,"
				+ " '' LINE_NUMBER, ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "NULL CGST_AMT,NULL SGST_AMT,ITM.CESS_AMT,HDR.DOC_AMT INV_VAL,"
				+ "HDR.POS,HDR.DIFF_PERCENT,HDR.RFNDELG AS AUTOPAPULATE_REFUND,"
				+ " '' SEC7ACT, '' SUPP_CLM_REFUND,NULL AMENDED,"
				+ " '' AS AMENDED_PERIOD,'' AS AMENDED_TYPE,'' INVALID, "
				+ " 'DE' AS TABLE_REFERENCE,BT.IS_FILED,HDR.DERIVED_RET_PERIOD "
				+ "FROM GETANX1_DE_HEADER HDR INNER JOIN GETANX1_DE_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'OUTWARD' TRANSACTION_TYPE,'DEA' TABLE_TYPE,'3G' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE,"
				+ "HDR.CTIN AS COUNTER_PARTY_GSTIN,HDR.ACTION ACTION_BY_COUNTER_PARTY,"
				+ " '' AS ORG_CNTR_PARTY_GSTINUIN, HDR.ORG_DOC_TYPE,"
				+ "HDR.ORG_DOC_NUMBER ORG_DOC_NUM, HDR.ORG_DOC_DATE,"
				+ " '' PORT_CODE,'' SHIPP_BILL_NUM, '' SHIPP_BILL_DATE,"
				+ " '' LINE_NUMBER, ITM.HSN,ITM.TAX_RATE, ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_AMT,NULL CGST_AMT,NULL SGST_AMT, ITM.CESS_AMT,"
				+ "HDR.DOC_AMT INV_VAL,HDR.POS,HDR.DIFF_PERCENT,"
				+ "HDR.RFNDELG AS AUTOPAPULATE_REFUND,'' SEC7ACT, "
				+ " '' SUPP_CLM_REFUND,AMENDED,AMD_PERIOD AS AMENDED_PERIOD,"
				+ "AMD_TYPE AS AMENDED_TYPE,'' INVALID, 'DEA' AS TABLE_REFERENCE,"
				+ "BT.IS_FILED,HDR.DERIVED_RET_PERIOD FROM GETANX1_DEA_HEADER "
				+ "HDR INNER JOIN GETANX1_DEA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'INWARD' TRANSACTION_TYPE,'IMPG' TABLE_TYPE,'3J' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.BILL_ENTRY_NUM DOC_NUMBER,"
				+ "HDR.BILL_ENTRY_DATE DOC_DATE,'' AS COUNTER_PARTY_GSTIN,"
				+ " '' ACTION_BY_COUNTER_PARTY,'' AS ORG_CNTR_PARTY_GSTINUIN, "
				+ " '' ORG_DOC_TYPE, '' ORG_DOC_NUM,'' ORG_DOC_DATE,'' PORT_CODE,"
				+ " '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,'' LINE_NUMBER,"
				+ "ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "NULL CGST_AMT,NULL SGST_AMT,ITM.CESS_AMT, HDR.DOC_AMT INV_VAL,"
				+ "HDR.POS,NULL DIFF_PERCENT, '' AS AUTOPAPULATE_REFUND,"
				+ " '' SEC7ACT,'' SUPP_CLM_REFUND, NULL AMENDED,'' AS AMENDED_PERIOD,"
				+ " '' AS AMENDED_TYPE, '' INVALID, 'IMPG' AS TABLE_REFERENCE,"
				+ "BT.IS_FILED, HDR.DERIVED_RET_PERIOD FROM GETANX1_IMPG_HEADER "
				+ "HDR INNER JOIN GETANX1_IMPG_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'INWARD' TRANSACTION_TYPE,'IMPGSEZ' TABLE_TYPE,'3K' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.BILL_ENTRY_NUM DOC_NUMBER,"
				+ "HDR.BILL_ENTRY_DATE DOC_DATE,HDR.CTIN AS COUNTER_PARTY_GSTIN,"
				+ " '' ACTION_BY_COUNTER_PARTY,'' AS ORG_CNTR_PARTY_GSTINUIN, "
				+ " '' ORG_DOC_TYPE, '' ORG_DOC_NUM,'' ORG_DOC_DATE,'' PORT_CODE, "
				+ " '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,'' LINE_NUMBER,"
				+ "ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,ITM.IGST_AMT,NULL CGST_AMT,"
				+ "NULL SGST_AMT,ITM.CESS_AMT, HDR.DOC_AMT INV_VAL,HDR.POS,"
				+ "NULL DIFF_PERCENT, HDR.RFNDELG AS AUTOPAPULATE_REFUND,"
				+ " '' SEC7ACT, '' SUPP_CLM_REFUND,NULL AMENDED, "
				+ " '' AS AMENDED_PERIOD, '' AS AMENDED_TYPE,'' INVALID,'IMPGSEZ' "
				+ "AS TABLE_REFERENCE, BT.IS_FILED,HDR.DERIVED_RET_PERIOD "
				+ "FROM GETANX1_IMPGSEZ_HEADER HDR INNER JOIN GETANX1_IMPGSEZ_ITEM "
				+ "ITM ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.TAX_PERIOD RETURN_PERIOD,"
				+ " 'INWARD' TRANSACTION_TYPE,'MIS' TABLE_TYPE,'3L' TABLE_NUM,"
				+ "HDR.DOC_TYPE,'' TYPE,HDR.DOC_NUMBER,HDR.DOC_DATE,"
				+ "HDR.CTIN AS COUNTER_PARTY_GSTIN, '' ACTION_BY_COUNTER_PARTY,"
				+ " '' AS ORG_CNTR_PARTY_GSTINUIN, '' ORG_DOC_TYPE, '' ORG_DOC_NUM,"
				+ " '' ORG_DOC_DATE, '' PORT_CODE,'' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE, "
				+ " '' LINE_NUMBER, ITM.HSN,ITM.TAX_RATE,ITM.TAXABLE_VALUE,"
				+ "ITM.IGST_AMT,NULL CGST_AMT,NULL SGST_AMT,ITM.CESS_AMT,"
				+ "HDR.DOC_AMT INV_VAL,HDR.POS,NULL DIFF_PERCENT,"
				+ " '' AS AUTOPAPULATE_REFUND,'' SEC7ACT,'' SUPP_CLM_REFUND,"
				+ "NULL AMENDED, '' AS AMENDED_PERIOD,'' AS AMENDED_TYPE,"
				+ " '' INVALID,'MIS' AS TABLE_REFERENCE,BT.IS_FILED,"
				+ "HDR.DERIVED_RET_PERIOD FROM GETANX1_MIS_HEADER HDR INNER JOIN "
				+ "GETANX1_MIS_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE) "
				+ buildQuery;

	}
}
