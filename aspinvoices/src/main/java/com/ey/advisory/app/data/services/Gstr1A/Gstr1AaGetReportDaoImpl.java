/**
 * 
 */
package com.ey.advisory.app.data.services.Gstr1A;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR1GSTNTransactionalLevelTablesDto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1AaGetReportDaoImpl")
@Slf4j
public class Gstr1AaGetReportDaoImpl implements Gstr1AaGetDao {

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
	public List<Object> getGstnConsolidatedReports(SearchCriteria criteria) {

		GstnConsolidatedReqDto request = (GstnConsolidatedReqDto) criteria;
		String taxPeriod = request.getTaxPeriod();

		List<String> gstr1aSections = request.getGstr1aSections();
		String gstin = request.getGstin();

		StringBuilder buildQuery = new StringBuilder();

		if (StringUtils.isNotBlank(gstin)) {
			buildQuery.append(" WHERE SUPPLIER_GSTIN IN :gstin");
		}

		if (CollectionUtils.isNotEmpty(gstr1aSections)) {
			buildQuery.append(" AND TABLE_TYPE IN :gstr1aSections");
		}

		if (taxPeriod != null) {
			buildQuery.append(" AND DERIVED_RET_PERIOD  = :taxPeriod ");
		}

		String queryStr = createGstnErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (StringUtils.isNotBlank(gstin)) {
			q.setParameter("gstin", Lists.newArrayList(gstin));
		}
		if (taxPeriod != null) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriod());
			q.setParameter("taxPeriod", derivedRetPeriod);

		}
		if (CollectionUtils.isNotEmpty(gstr1aSections)) {
			q.setParameter("gstr1aSections", gstr1aSections);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertTransactionalLevel(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GSTR1GSTNTransactionalLevelTablesDto convertTransactionalLevel(
			Object[] arr) {
		GSTR1GSTNTransactionalLevelTablesDto obj = new GSTR1GSTNTransactionalLevelTablesDto();

		obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setTableType(arr[2] != null ? arr[2].toString() : null);
		obj.setDocumentType(arr[3] != null ? arr[3].toString() : null);
		obj.setType(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentNo(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentDate(arr[6] != null ? arr[6].toString() : null);
		obj.setOriginalDocumentNo(arr[7] != null ? arr[7].toString() : null);
		obj.setOriginalDocumentDate(arr[8] != null ? arr[8].toString() : null);
		obj.setCrdrpregst(arr[9] != null ? arr[9].toString() : null);
		obj.setRecipientGSTIN(arr[10] != null ? arr[10].toString() : null);
		obj.setPos(arr[11] != null ? arr[11].toString() : null);
		obj.setLineNumber(arr[12] != null ? arr[12].toString() : null);
		obj.setTaxRate(arr[13] != null ? arr[13].toString() : null);
		obj.setDifferentialPercentageRate(
				arr[14] != null ? arr[14].toString() : null);
		obj.setTaxableValue(arr[15] != null ? arr[15].toString() : null);
		obj.setIgstAmount(arr[16] != null ? arr[16].toString() : null);
		obj.setCgstAmount(arr[17] != null ? arr[17].toString() : null);
		obj.setSgstAmountutgstAmount(
				arr[18] != null ? arr[18].toString() : null);
		obj.setCessAmount(arr[19] != null ? arr[19].toString() : null);
		obj.setInvoiceValue(arr[20] != null ? arr[20].toString() : null);
		obj.setReverseCharge(arr[21] != null ? arr[21].toString() : null);
		obj.setEcomGSTIN(arr[22] != null ? arr[22].toString() : null);
		obj.setOriginalInvoiceNumber(
				arr[23] != null ? arr[23].toString() : null);
		obj.setOriginalInvoiceDate(arr[24] != null ? arr[24].toString() : null);
		obj.setPortCode(arr[25] != null ? arr[25].toString() : null);
		obj.setShippingbillNumber(arr[26] != null ? arr[26].toString() : null);
		obj.setShippingbillDate(arr[27] != null ? arr[27].toString() : null);
		/*try {

			if (arr[28] == null) {
				obj.setIsFilled(APIConstants.N);
			}
			if (arr[28] != null) {
				Boolean b = (Boolean) arr[28];
				if (b) {
					obj.setIsFilled(APIConstants.Y);
				} else {
					obj.setIsFilled(APIConstants.N);
				}
			}
		} catch (Exception e) {
			obj.setIsFilled(APIConstants.N);
		}*/

		obj.setIsFilled(isDecimal(arr[28]));
		obj.setDelinkFlag(arr[29] != null ? arr[29].toString() : null);
		obj.setIrnNum(arr[31] != null ? arr[31].toString() : null);
		obj.setIrnGenDate(arr[32] != null ? arr[32].toString() : null);
		obj.setIrnSourceType(arr[33] != null ? arr[33].toString() : null);
		// obj.setSerialNumber(arr[34] != null ? arr[34].toString() : null);

		return obj;
	}

	/**
	 * @param object
	 * @return
	 */
	private String isDecimal(Object obj) {
		try{
		 if (obj == null)
		        return "N";
		    if (obj instanceof Long){
		        if(Long.parseLong(obj.toString())==0){
		            return "N";
		        }else{
		            return "Y";
		        }
		    }
		
		    if (obj instanceof Boolean) {
		        Boolean b = (Boolean) obj;
		        if(b){
		            return "Y";
		        }else{
		            return "N";
		        }
		    }
		    } catch (Exception e) {
		    	
		    }
		return "N";
	}

	private String createGstnErrorQueryString(String buildQuery) {

		return " SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TABLE_TYPE,INV_TYPE,TYPE, "
				+ "INV_NUM,INV_DATE,ORG_DOC_NUM,ORG_DOC_DATE,CRDRPreGST, "
				+ "CUST_GSTIN, POS,LINE_NUM,TAX_RATE,DIFF_PERCENT,TAXABLE_VALUE, "
				+ "IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,INV_VALUE,RCHRG, "
				+ "E_COM_GSTIN,ORG_INV_NUM,ORG_INV_DATE, PORT_CODE, SHIPP_BILL_NUM, "
				+ "SHIPP_BILL_DATE, IS_FILED,D_FLAG,DERIVED_RET_PERIOD,"
				+ "IRN_NUM,IRN_GEN_DATE,IRN_SOURCE_TYPE,"
				+ "ROW_NUMBER () OVER (ORDER BY SUPPLIER_GSTIN) SNO "
				+ "FROM (SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ " 'B2B' TABLE_TYPE,HDR.INV_TYPE,'' TYPE, HDR.INV_NUM,"
				+ "HDR.INV_DATE, '' ORG_DOC_NUM,'' ORG_DOC_DATE,'' CRDRPreGST,"
				+ "HDR.CTIN CUST_GSTIN, HDR.POS, ITM.SERIAL_NUM LINE_NUM,"
				+ "ITM.TAX_RATE, HDR.DIFF_PERCENT, ITM.TAXABLE_VALUE,ITM.IGST_AMT, "
				+ "ITM.CGST_AMT,ITM.SGST_AMT, ITM.CESS_AMT,ITM.INV_VALUE, "
				+ "HDR.RCHRG,HDR.ETIN E_COM_GSTIN, '' ORG_INV_NUM,'' ORG_INV_DATE, "
				+ " '' PORT_CODE, '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE, BT.IS_FILED, "
				+ " '' D_FLAG,HDR.DERIVED_RET_PERIOD,"
				+ "HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE FROM "
				+ "GETGSTR1A_B2B_HEADER HDR INNER JOIN GETGSTR1A_B2B_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,'B2BA' TABLE_TYPE, "
				+ "HDR.INV_TYPE,'' TYPE,HDR.INV_NUM DOC_NUM,HDR.INV_DATE DOC_DATE, "
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE, '' CRDRPreGST, "
				+ "HDR.CTIN CUST_GSTIN, HDR.POS, ITM.SERIAL_NUM LINE_NUM,"
				+ "ITM.TAX_RATE,HDR.DIFF_PERCENT, ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT, ITM.CESS_AMT,ITM.INV_VALUE,"
				+ "HDR.RCHRG,HDR.ETIN E_COM_GSTIN, '' ORG_INV_NUM,'' ORG_INV_DATE,"
				+ " '' PORT_CODE, '' SHIPP_BILL_NUM, '' SHIPP_BILL_DATE,BT.IS_FILED,"
				+ " '' D_FLAG,HDR.DERIVED_RET_PERIOD,"
				+ "'' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ "FROM GETGSTR1A_B2BA_HEADER "
				+ "HDR INNER JOIN GETGSTR1A_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION ALL "
				+ " SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TABLE_TYPE,INV_TYPE, "
				+ "TYPE,DOC_NUM,DOC_DATE,ORG_DOC_NUM,ORG_DOC_DATE,CRDRPreGST, CUST_GSTIN, "
				+ "POS, LINE_NUM, TAX_RATE, DIFF_PERCENT, TAXABLE_VALUE, IGST_AMT, CGST_AMT, SGST_AMT, "
				+ "CESS_AMT, INV_VALUE,RCHRG,E_COM_GSTIN,  ORG_INV_NUM,  ORG_INV_DATE, PORT_CODE,"
				+ "SHIPP_BILL_NUM, SHIPP_BILL_DATE, IS_FILED ,D_FLAG, DERIVED_RET_PERIOD, IRN_NUM, "
				+ "IRN_GEN_DATE, IRN_SOURCE_TYPE FROM ( SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ "'CDNR' TABLE_TYPE,HDR.NOTE_TYPE INV_TYPE, "
				+ " '' TYPE, HDR.NOTE_NUM DOC_NUM, HDR.NOTE_DATE DOC_DATE,"
				+ " '' ORG_DOC_NUM, '' ORG_DOC_DATE,"
				+ "HDR.PRE_GST CRDRPreGST, HDR.CTIN CUST_GSTIN,"
				+ "HDR.POS,ITM.SERIAL_NUM LINE_NUM, ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT,ITM.TAXABLE_VALUE,ITM.IGST_AMT, "
				+ "ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT, ITM.INV_VALUE,HDR.RCHRG, "
				+ " '' E_COM_GSTIN,'' ORG_INV_NUM, '' ORG_INV_DATE,'' PORT_CODE, "
				+ " '' SHIPP_BILL_NUM, '' SHIPP_BILL_DATE,BT.IS_FILED,D_FLAG, "
				+ "HDR.DERIVED_RET_PERIOD,HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE "
				+ "FROM GETGSTR1A_CDNR_HEADER "
				+ "HDR INNER JOIN GETGSTR1A_CDNR_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE) "
				+ " UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,TABLE_TYPE,"
				+ "INV_TYPE, TYPE,DOC_NUM,"
				+ "DOC_DATE,ORG_DOC_NUM,ORG_DOC_DATE,CRDRPreGST, CUST_GSTIN, POS, "
				+ "LINE_NUM, TAX_RATE, DIFF_PERCENT, TAXABLE_VALUE, IGST_AMT, "
				+ "CGST_AMT, SGST_AMT, CESS_AMT, INV_VALUE,RCHRG, E_COM_GSTIN, "
				+ "ORG_INV_NUM, ORG_INV_DATE, PORT_CODE, SHIPP_BILL_NUM, "
				+ "SHIPP_BILL_DATE, IS_FILED , D_FLAG, DERIVED_RET_PERIOD, "
				+ "IRN_NUM, IRN_GEN_DATE, IRN_SOURCE_TYPE FROM ( SELECT "
				+ "HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ " 'CDNRA' TABLE_TYPE, " + "HDR.NOTE_TYPE INV_TYPE,"
				+ " '' TYPE,HDR.NOTE_NUM DOC_NUM, "
				+ "HDR.NOTE_DATE DOC_DATE,HDR.ORG_NOTE_NUM ORG_DOC_NUM, "
				+ "HDR.ORG_NOTE_DATE ORG_DOC_DATE,HDR.PRE_GST CRDRPreGST, "
				+ "HDR.CTIN CUST_GSTIN,HDR.POS,ITM.SERIAL_NUM LINE_NUM, "
				+ "ITM.TAX_RATE,HDR.DIFF_PERCENT,ITM.TAXABLE_VALUE,ITM.IGST_AMT, "
				+ "ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT,ITM.INV_VALUE,HDR.RCHRG, "
				+ " '' E_COM_GSTIN,HDR.INV_NUM ORG_INV_NUM, HDR.INV_DATE ORG_INV_DATE, "
				+ " '' PORT_CODE, '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE, BT.IS_FILED ,"
				+ "D_FLAG,HDR.DERIVED_RET_PERIOD,'' IRN_NUM, '' IRN_GEN_DATE, "
				+ " '' IRN_SOURCE_TYPE FROM GETGSTR1A_CDNRA_HEADER "
				+ "HDR INNER JOIN GETGSTR1A_CDNRA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE) "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ " 'CDNUR' TABLE_TYPE, HDR.NOTE_TYPE AS INV_TYPE,HDR.TYPE,"
				+ "HDR.NOTE_NUM DOC_NUM, HDR.NOTE_DATE DOC_DATE,"
				+ " '' ORG_DOC_NUM, '' ORG_DOC_DATE,"
				+ "HDR.PRE_GST CRDRPreGST, '' CUST_GSTIN,HDR.POS,"
				+ "ITM.SERIAL_NUM LINE_NUM, ITM.TAX_RATE, HDR.DIFF_PERCENT,"
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT, 0 CGST_AMT,0 SGST_AMT,"
				+ "ITM.CESS_AMT, ITM.INV_VALUE,'' RCHRG, '' E_COM_GSTIN,"
				+ " '' ORG_INV_NUM,'' ORG_INV_DATE,'' PORT_CODE, '' SHIPP_BILL_NUM,"
				+ " '' SHIPP_BILL_DATE,BT.IS_FILED ,D_FLAG, HDR.DERIVED_RET_PERIOD,"
				+ "HDR.IRN_NUM,HDR.IRN_GEN_DATE,HDR.IRN_SOURCE_TYPE "
				+ " FROM GETGSTR1A_CDNUR_HEADER HDR INNER JOIN GETGSTR1A_CDNUR_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ " 'CDNURA' TABLE_TYPE,HDR.NOTE_TYPE AS INV_TYPE, HDR.TYPE, "
				+ "HDR.NOTE_NUM DOC_NUM, HDR.NOTE_DATE DOC_DATE,"
				+ "HDR.ORG_NOTE_NUM ORG_DOC_NUM, HDR.ORG_NOTE_DATE ORG_DOC_DATE, "
				+ "HDR.PRE_GST CRDRPreGST, '' CUST_GSTIN,HDR.POS, "
				+ "ITM.SERIAL_NUM LINE_NUM,ITM.TAX_RATE, HDR.DIFF_PERCENT, "
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT,0 CGST_AMT, 0 SGST_AMT, "
				+ "ITM.CESS_AMT,ITM.INV_VALUE,'' RCHRG,'' E_COM_GSTIN, "
				+ "HDR.INV_NUM ORG_INV_NUM,HDR.INV_DATE ORG_INV_DATE, "
				+ " '' PORT_CODE,'' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE, "
				+ "BT.IS_FILED,D_FLAG,HDR.DERIVED_RET_PERIOD,"
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE FROM "
				+ "GETGSTR1A_CDNURA_HEADER HDR INNER JOIN GETGSTR1A_CDNURA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, 'EXP' TABLE_TYPE,"
				+ "HDR.EXPORT_TYPE,'' TYPE, HDR.INV_NUM DOC_NUM, HDR.INV_DATE DOC_DATE, "
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE, '' CRDRPreGST,'' CUST_GSTIN, "
				+ " '' POS,NULL AS LINE_NUM,ITM.TAX_RATE, HDR.DIFF_PERCENT, "
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT, 0 CGST_AMT, 0 SGST_AMT,"
				+ "ITM.CESS_AMT,ITM.INV_VALUE,'' RCHRG, '' E_COM_GSTIN,"
				+ " '' ORG_INV_NUM, '' ORG_INV_DATE,"
				+ "HDR.SHIPP_BILL_PORT_CODE PORT_CODE, HDR.SHIPP_BILL_NUM,"
				+ "HDR.SHIPP_BILL_DATE,BT.IS_FILED , '' D_FLAG,"
				+ "HDR.DERIVED_RET_PERIOD,HDR.IRN_NUM,HDR.IRN_GEN_DATE,"
				+ "HDR.IRN_SOURCE_TYPE FROM GETGSTR1A_EXP_HEADER "
				+ "HDR INNER JOIN GETGSTR1A_EXP_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ " 'EXPA' TABLE_TYPE,HDR.EXPORT_TYPE,'' TYPE, HDR.INV_NUM DOC_NUM,"
				+ "HDR.INV_DATE DOC_DATE, HDR.ORG_INV_NUM ORG_DOC_NUM,"
				+ "HDR.ORG_INV_DATE ORG_DOC_DATE, '' CRDRPreGST,'' CUST_GSTIN,"
				+ " '' POS,NULL AS LINE_NUM,ITM.TAX_RATE, HDR.DIFF_PERCENT,"
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT,0 CGST_AMT, 0 SGST_AMT,"
				+ "ITM.CESS_AMT,ITM.INV_VALUE,'' RCHRG,'' E_COM_GSTIN, "
				+ "HDR.INV_NUM ORG_INV_NUM,HDR.INV_DATE ORG_INV_DATE, "
				+ "HDR.SHIPP_BILL_PORT_CODE PORT_CODE,HDR.SHIPP_BILL_NUM, "
				+ "HDR.SHIPP_BILL_DATE,BT.IS_FILED , '' D_FLAG, "
				+ "HDR.DERIVED_RET_PERIOD,'' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE "
				+ " FROM GETGSTR1A_EXPA_HEADER "
				+ "HDR INNER JOIN GETGSTR1A_EXPA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN, HDR.RETURN_PERIOD,"
				+ " 'B2CL' TABLE_TYPE,HDR.INV_TYPE, '' TYPE,HDR.INV_NUM DOC_NUM, "
				+ "HDR.INV_DATE DOC_DATE, '' ORG_DOC_NUM,'' ORG_DOC_DATE, "
				+ " '' CRDRPreGST,'' CUST_GSTIN, HDR.POS,ITM.SERIAL_NUM LINE_NUM,"
				+ "ITM.TAX_RATE,HDR.DIFF_PERCENT, ITM.TAXABLE_VALUE,ITM.IGST_AMT, "
				+ " 0 CGST_AMT,0 SGST_AMT, ITM.CESS_AMT,ITM.INV_VALUE,"
				+ " '' RCHRG, HDR.ETIN E_COM_GSTIN, '' ORG_INV_NUM, "
				+ " '' ORG_INV_DATE, '' PORT_CODE,'' SHIPP_BILL_NUM, "
				+ " '' SHIPP_BILL_DATE, BT.IS_FILED,'' D_FLAG,"
				+ "HDR.DERIVED_RET_PERIOD,'' IRN_NUM, '' IRN_GEN_DATE, "
				+ " '' IRN_SOURCE_TYPE FROM GETGSTR1A_B2CL_HEADER "
				+ "HDR INNER JOIN GETGSTR1A_B2CL_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ " 'B2CLA' TABLE_TYPE,HDR.INV_TYPE,'' TYPE,HDR.INV_NUM DOC_NUM, "
				+ "HDR.INV_DATE DOC_DATE,HDR.ORG_INV_NUM ORG_DOC_NUM, "
				+ "HDR.ORG_INV_DATE ORG_DOC_DATE,'' CRDRPreGST, '' CUST_GSTIN, "
				+ "HDR.POS,ITM.SERIAL_NUM LINE_NUM,ITM.TAX_RATE, HDR.DIFF_PERCENT, "
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT, 0 CGST_AMT,0 SGST_AMT, "
				+ "ITM.CESS_AMT,ITM.INV_VALUE, '' RCHRG,HDR.ETIN E_COM_GSTIN, "
				+ "HDR.INV_NUM ORG_INV_NUM, HDR.INV_DATE ORG_INV_DATE, "
				+ " '' PORT_CODE,'' SHIPP_BILL_NUM, '' SHIPP_BILL_DATE, "
				+ "BT.IS_FILED,'' D_FLAG,HDR.DERIVED_RET_PERIOD,"
				+ " '' IRN_NUM, '' IRN_GEN_DATE, '' IRN_SOURCE_TYPE FROM "
				+ "GETGSTR1A_B2CLA_HEADER HDR INNER JOIN GETGSTR1A_B2CLA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE ) "
				+ buildQuery;

	}
	
	
	
}
