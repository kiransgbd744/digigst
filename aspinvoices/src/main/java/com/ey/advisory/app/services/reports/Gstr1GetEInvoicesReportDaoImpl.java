package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR1GetEInvoicesTablesDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1EInvReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1GetEInvoicesReportDaoImpl")
public class Gstr1GetEInvoicesReportDaoImpl
		implements Gstr1GetEInvoicesReportsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1GetEInvoicesReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("batchSaveStatusRepository") private Gstr1BatchRepository
	 * gstr1BatchRepository;
	 */

	static Integer cutoffPeriod = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<GSTR1GetEInvoicesTablesDto> getGstr1EIReports(
			SearchCriteria criteria) {

		Gstr1EInvReportsReqDto request = (Gstr1EInvReportsReqDto) criteria;
		// String taxPeriod = request.getTaxPeriod();

		// List<String> gstr1EISections = request.getGstr1EinvSections();
		List<String> gstin = request.getGstin();
		String fromPeriod = request.getFromPeriod();
		String toPeriod = request.getToPeriod();

		StringBuilder buildQuery = new StringBuilder();

		if (CollectionUtils.isNotEmpty(gstin)) {
			buildQuery.append(" WHERE GSTIN IN (:gstin)");
		}

		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			buildQuery.append(
					" AND RETURN_PERIOD BETWEEN :fromPeriod AND :toPeriod ");

		}

		/*
		 * if (CollectionUtils.isNotEmpty(gstr1EISections)) {
		 * buildQuery.append(" AND TABLE_TYPE IN (:gstr1EISections); }
		 */

		/*
		 * if (taxPeriod != null) {
		 * buildQuery.append(" AND RETURN_PERIOD = :taxPeriod "); }
		 */

		String queryStr = createGstnQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);
		LOGGER.debug("outquery -->" + q);

		if (CollectionUtils.isNotEmpty(gstin)) {
			q.setParameter("gstin", Lists.newArrayList(gstin));
		}
		/*
		 * if (taxPeriod != null) { q.setParameter("taxPeriod", taxPeriod);
		 * 
		 * }
		 */
		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			q.setParameter("fromPeriod", fromPeriod);
			q.setParameter("toPeriod", toPeriod);
		}
		//
		// if (CollectionUtils.isNotEmpty(gstr1EISections)) {
		// q.setParameter("gstr1EISections", gstr1EISections); }

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GSTR1GetEInvoicesTablesDto convertTransactionalLevel(Object[] arr) {
		GSTR1GetEInvoicesTablesDto obj = new GSTR1GetEInvoicesTablesDto();

		obj.setIrnNum(arr[0] != null ? arr[0].toString() : null);
		obj.setIrnGenDate(arr[1] != null ? arr[1].toString() : null);
		obj.seteInvStatus(arr[2] != null ? arr[2].toString() : null);
		obj.setAutoDraftStatus(arr[3] != null ? arr[3].toString() : null);
		obj.setAutoDraftDate(arr[4] != null ? arr[4].toString() : null);
		obj.setErrorCode(arr[5] != null ? arr[5].toString() : null);
		obj.setErrorMsg(arr[6] != null ? arr[6].toString() : null);
		obj.setReturnPeriod(arr[7] != null ? arr[7].toString() : null);
		obj.setSupplierGSTIN(arr[8] != null ? arr[8].toString() : null);
		obj.setCustomerGSTIN(arr[9] != null ? arr[9].toString() : null);
		obj.setCustTradeName(arr[10] != null ? arr[10].toString() : null);
		obj.setDocumentType(arr[11] != null ? arr[11].toString() : null);
		obj.setSupplyType(arr[12] != null ? arr[12].toString() : null);
		obj.setDocumentNo(arr[13] != null ? arr[13].toString() : null);
		obj.setDocumentDate(arr[14] != null ? arr[14].toString() : null);
		obj.setPos(arr[15] != null ? arr[15].toString() : null);
		obj.setPortCode(arr[16] != null ? arr[16].toString() : null);
		obj.setShippingbillNumber(arr[17] != null ? arr[17].toString() : null);
		obj.setShippingbillDate(arr[18] != null ? arr[18].toString() : null);
		obj.setReverseCharge(arr[19] != null ? arr[19].toString() : null);
		obj.setEcomGSTIN(arr[20] != null ? arr[20].toString() : null);
		obj.setItemSerialNumber(arr[21] != null ? arr[21].toString() : null);

		BigDecimal bigDecimaItem = (BigDecimal) arr[22];
		if (bigDecimaItem != null) {
			obj.setItemAssessAmount(
					bigDecimaItem.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		/*
		 * BigDecimal bigDecimaItem = (BigDecimal) arr[22]; if (bigDecimaItem !=
		 * null) { BigDecimal einvItem = new
		 * BigDecimal(bigDecimaItem.longValue());
		 * obj.setItemAssessAmount(einvItem); }
		 */

		BigDecimal bigDecimaTax = (BigDecimal) arr[23];
		if (bigDecimaTax != null) {
			obj.setTaxRate(bigDecimaTax.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimaIgst = (BigDecimal) arr[24];
		if (bigDecimaIgst != null) {
			obj.setIgstAmount(
					bigDecimaIgst.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimaCgst = (BigDecimal) arr[25];
		if (bigDecimaCgst != null) {
			obj.setCgstAmount(
					bigDecimaCgst.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimaSgst = (BigDecimal) arr[26];
		if (bigDecimaSgst != null) {
			obj.setSgstAmount(
					bigDecimaSgst.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimaCess = (BigDecimal) arr[27];
		if (bigDecimaCess != null) {
			obj.setCessAmount(
					bigDecimaCess.setScale(2, BigDecimal.ROUND_HALF_UP));

		}
		BigDecimal bigDecimaValue = (BigDecimal) arr[28];
		if (bigDecimaValue != null) {
			obj.setInvoiceValue(
					bigDecimaValue.setScale(2, BigDecimal.ROUND_HALF_UP));

		}

		obj.setIrnSourceType(arr[29] != null ? arr[29].toString() : null);
		obj.setTableType(arr[30] != null ? arr[30].toString() : null);

		return obj;
	}

	private String createGstnQueryString(String buildQuery) {

		return "SELECT IRN_NUM,IRN_GEN_DATE,EINV_STATUS,AUTODFT,AUTODFT_DATE,ERROR_CODE,ERROR_DESC,RETURN_PERIOD ,"
				+ "GSTIN,CTIN,TRADE_NAME,DOC_TYPE,SUPPLY_TYPE,DOC_NUM,DOC_DATE,POS ,PORT_CODE,SHIP_BILL_NUM,"
				+ "SHIP_BILL_DATE,RCHRG,ETIN,SERIAL_NUM,TAX_RATE ,SUM(IFNULL(TAXABLE_VALUE,0)) TAXABLE_VALUE ,"
				+ "SUM(IFNULL(IGST_AMT,0)) IGST_AMT ,SUM(IFNULL(CGST_AMT,0)) CGST_AMT ,"
				+ "SUM(IFNULL(SGST_AMT,0)) SGST_AMT ,SUM(IFNULL(CESS_AMT,0)) CESS_AMT ,"
				+ "SUM(IFNULL(INV_VALUE,0)) INV_VALUE,IRN_SOURCE_TYPE,TABLE_TYPE "
				+ "FROM( SELECT IRN_NUM,IRN_GEN_DATE,EINV_STATUS,AUTODFT,AUTODFT_DATE,HDR.ERROR_CODE,HDR.ERROR_DESC,"
				+ "HDR.RETURN_PERIOD ,HDR.GSTIN,HDR.CTIN,TRADE_NAME,'INV' DOC_TYPE ,"
				+ "INV_TYPE as SUPPLY_TYPE ,INV_NUM DOC_NUM,INV_DATE DOC_DATE,POS ,"
				+ "'' PORT_CODE,'' SHIP_BILL_NUM,'' SHIP_BILL_DATE ,RCHRG,HDR.ETIN,SERIAL_NUM,TAX_RATE ,"
				+ "SUM(IFNULL(ITM.TAXABLE_VALUE,0)) TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT,0)) IGST_AMT ,"
				+ "SUM(IFNULL(ITM.CGST_AMT,0)) CGST_AMT ,SUM(IFNULL(ITM.SGST_AMT,0)) SGST_AMT ,"
				+ "SUM(IFNULL(ITM.CESS_AMT,0)) CESS_AMT ,SUM(IFNULL(ITM.INV_VALUE,0)) INV_VALUE,IRN_SOURCE_TYPE, "
				+ "'B2B' TABLE_TYPE FROM GETGSTR1_EINV_B2B_HEADER HDR INNER JOIN "
				+ "GETGSTR1_EINV_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE AND "
				+ "BT.IS_DELETE = FALSE AND BT.GET_TYPE = 'B2B' AND API_SECTION='GSTR1_EINV' GROUP BY IRN_NUM,IRN_GEN_DATE,EINV_STATUS,"
				+ "AUTODFT,AUTODFT_DATE,HDR.ERROR_CODE,HDR.ERROR_DESC,HDR.RETURN_PERIOD ,HDR.GSTIN,HDR.CTIN,TRADE_NAME,"
				+ "INV_TYPE,INV_NUM,INV_DATE,POS,RCHRG,HDR.ETIN,SERIAL_NUM,TAX_RATE,IRN_SOURCE_TYPE "
				+ "UNION ALL SELECT IRN_NUM,IRN_GEN_DATE,EINV_STATUS,AUTODFT,AUTODFT_DATE,HDR.ERROR_CODE,"
				+ "HDR.ERROR_DESC,HDR.RETURN_PERIOD ,HDR.GSTIN,HDR.CTIN,TRADE_NAME,"
				+ "(CASE WHEN NOTE_TYPE = 'C' THEN 'CR' WHEN NOTE_TYPE = 'D' THEN 'DR' END) DOC_TYPE , "
				+ "INV_TYPE as SUPPLY_TYPE ,NOTE_NUM DOC_NUM,NOTE_DATE DOC_DATE,POS ,'' PORT_CODE,'' SHIP_BILL_NUM,"
				+ "'' SHIP_BILL_DATE ,RCHRG,'' ETIN,SERIAL_NUM,TAX_RATE ,SUM(IFNULL(ITM.TAXABLE_VALUE,0)) TAXABLE_VALUE ,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)) IGST_AMT ,SUM(IFNULL(ITM.CGST_AMT,0)) CGST_AMT ,"
				+ "SUM(IFNULL(ITM.SGST_AMT,0)) SGST_AMT ,SUM(IFNULL(ITM.CESS_AMT,0)) CESS_AMT ,"
				+ "SUM(IFNULL(ITM.INV_VALUE,0)) INV_VALUE,IRN_SOURCE_TYPE, 'CDNR' TABLE_TYPE "
				+ "FROM GETGSTR1_EINV_CDNR_HEADER HDR INNER JOIN GETGSTR1_EINV_CDNR_ITEM ITM ON "
				+ "HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT "
				+ "OUTER JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE BT.IS_DELETE = FALSE "
				+ "AND HDR.IS_DELETE = FALSE AND BT.GET_TYPE ='CDNR' AND API_SECTION='GSTR1_EINV' GROUP BY IRN_NUM,IRN_GEN_DATE,EINV_STATUS,"
				+ "AUTODFT,AUTODFT_DATE,HDR.ERROR_CODE,HDR.ERROR_DESC,HDR.RETURN_PERIOD ,HDR.GSTIN,HDR.CTIN,TRADE_NAME,"
				+ "NOTE_TYPE,INV_TYPE,NOTE_NUM,NOTE_DATE,POS,RCHRG,SERIAL_NUM,TAX_RATE,IRN_SOURCE_TYPE "
				+ "UNION ALL SELECT IRN_NUM,IRN_GEN_DATE,EINV_STATUS,AUTODFT,AUTODFT_DATE,HDR.ERROR_CODE,HDR.ERROR_DESC,"
				+ "HDR.RETURN_PERIOD ,HDR.GSTIN,'' CTIN,'' TRADE_NAME,(CASE WHEN NOTE_TYPE = 'C' THEN 'CR' "
				+ "WHEN NOTE_TYPE = 'D' THEN 'DR' END) DOC_TYPE ,TYPE SUPPLY_TYPE ,NOTE_NUM DOC_NUM,"
				+ "NOTE_DATE DOC_DATE,POS ,'' PORT_CODE,'' SHIP_BILL_NUM,'' SHIP_BILL_DATE ,'' RCHRG,'' ETIN,SERIAL_NUM,"
				+ "TAX_RATE ,SUM(IFNULL(ITM.TAXABLE_VALUE,0)) TAXABLE_VALUE ,SUM(IFNULL(ITM.IGST_AMT,0)) IGST_AMT,"
				+ "0.00 CGST_AMT,0.00 SGST_AMT ,SUM(IFNULL(ITM.CESS_AMT,0)) CESS_AMT ,"
				+ "SUM(IFNULL(ITM.INV_VALUE,0)) INV_VALUE,IRN_SOURCE_TYPE, 'CDNUR' TABLE_TYPE "
				+ "FROM GETGSTR1_EINV_CDNUR_HEADER HDR INNER JOIN "
				+ "GETGSTR1_EINV_CDNUR_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN GETANX1_BATCH_TABLE "
				+ "BT ON HDR.BATCH_ID = BT.ID WHERE BT.IS_DELETE = FALSE AND HDR.IS_DELETE = FALSE "
				+ "AND BT.GET_TYPE ='CDNUR' AND API_SECTION='GSTR1_EINV' GROUP BY IRN_NUM,IRN_GEN_DATE,EINV_STATUS,AUTODFT,AUTODFT_DATE,"
				+ "HDR.ERROR_CODE,HDR.ERROR_DESC,HDR.RETURN_PERIOD ,HDR.GSTIN,NOTE_TYPE,TYPE,NOTE_NUM,NOTE_DATE,POS,"
				+ "SERIAL_NUM,TAX_RATE,IRN_SOURCE_TYPE UNION ALL SELECT IRN_NUM,IRN_GEN_DATE,EINV_STATUS,AUTODFT,"
				+ "AUTODFT_DATE,HDR.ERROR_CODE,HDR.ERROR_DESC,HDR.RETURN_PERIOD ,HDR.GSTIN,'' CTIN,'' TRADE_NAME,"
				+ "'INV' DOC_TYPE , EXPORT_TYPE SUPPLY_TYPE ,INV_NUM DOC_NUM,INV_DATE DOC_DATE,'' POS ,"
				+ "SHIPP_BILL_PORT_CODE PORT_CODE,SHIPP_BILL_NUM SHIP_BILL_NUM,SHIPP_BILL_DATE SHIP_BILL_DATE ,"
				+ "'' RCHRG,'' ETIN,0 SERIAL_NUM,TAX_RATE ,SUM(IFNULL(ITM.TAXABLE_VALUE,0)) TAXABLE_VALUE ,"
				+ "SUM(IFNULL(ITM.IGST_AMT,0)) IGST_AMT,0.00 CGST_AMT,0.00 SGST_AMT ,SUM(IFNULL(ITM.CESS_AMT,0)) CESS_AMT ,"
				+ "SUM(IFNULL(ITM.INV_VALUE,0)) INV_VALUE,IRN_SOURCE_TYPE, 'EXP' TABLE_TYPE FROM "
				+ "GETGSTR1_EINV_EXP_HEADER HDR INNER JOIN GETGSTR1_EINV_EXP_ITEM ITM ON "
				+ "HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT "
				+ "OUTER JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE BT.IS_DELETE = FALSE "
				+ "AND HDR.IS_DELETE = FALSE AND BT.GET_TYPE ='EXP' AND API_SECTION='GSTR1_EINV' GROUP BY IRN_NUM,IRN_GEN_DATE,EINV_STATUS,"
				+ "AUTODFT,AUTODFT_DATE,HDR.ERROR_CODE,HDR.ERROR_DESC,HDR.RETURN_PERIOD ,HDR.GSTIN,EXPORT_TYPE,"
				+ "INV_NUM,INV_DATE,SHIPP_BILL_PORT_CODE,SHIPP_BILL_NUM,SHIPP_BILL_DATE,TAX_RATE,IRN_SOURCE_TYPE ) "
				+ buildQuery + "GROUP BY "
				+ "IRN_NUM,IRN_GEN_DATE,EINV_STATUS,AUTODFT,AUTODFT_DATE,ERROR_CODE,ERROR_DESC,RETURN_PERIOD ,GSTIN,CTIN,"
				+ "TRADE_NAME,DOC_TYPE,SUPPLY_TYPE,DOC_NUM,DOC_DATE,POS ,PORT_CODE,SHIP_BILL_NUM,SHIP_BILL_DATE,RCHRG,ETIN,"
				+ "SERIAL_NUM,TAX_RATE,IRN_SOURCE_TYPE,TABLE_TYPE";
	}

}
