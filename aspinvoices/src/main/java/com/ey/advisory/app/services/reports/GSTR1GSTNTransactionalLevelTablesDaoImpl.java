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

import com.ey.advisory.app.data.views.client.GSTR1GSTNTransactionalLevelTablesDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("GSTR1GSTNTransactionalLevelTablesDaoImpl")
public class GSTR1GSTNTransactionalLevelTablesDaoImpl
		implements Gstr1GstnErrorDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSTR1GSTNTransactionalLevelTablesDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getGstr1GstnErrorReport(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		
		String ProfitCenter = null;
		String plant = null;
		String sales = null;
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
				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.SO)
									.size() > 0) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
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
		
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND ITM.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND ITM.PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND ITM.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS6 IN :ud6List");
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
		
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
				q.setParameter("pcList", pcList);
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && !plantList.isEmpty()
					&& plantList.size() > 0) {
				q.setParameter("plantList", plantList);
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && !salesList.isEmpty()
					&& salesList.size() > 0) {
				q.setParameter("salesList", salesList);
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && !divisionList.isEmpty()
					&& divisionList.size() > 0) {
				q.setParameter("divisionList", divisionList);
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && !locationList.isEmpty()
					&& locationList.size() > 0) {
				q.setParameter("locationList", locationList);
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && !distList.isEmpty()
					&& distList.size() > 0) {
				q.setParameter("distList", distList);
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && !ud1List.isEmpty() && ud1List.size() > 0) {
				q.setParameter("ud1List", ud1List);
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && !ud2List.isEmpty() && ud2List.size() > 0) {
				q.setParameter("ud2List", ud2List);
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && !ud3List.isEmpty() && ud3List.size() > 0) {
				q.setParameter("ud3List", ud3List);
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && !ud4List.isEmpty() && ud4List.size() > 0) {
				q.setParameter("ud4List", ud4List);
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && !ud5List.isEmpty() && ud5List.size() > 0) {
				q.setParameter("ud5List", ud5List);
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && !ud6List.isEmpty() && ud6List.size() > 0) {
				q.setParameter("ud6List", ud6List);
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
		obj.setIsFilled(arr[28] != null ? arr[28].toString() : null);
		obj.setDelinkFlag(arr[29] != null ? arr[29].toString() : null);

		return obj;
	}

	private String creategstnTransQueryString(String buildQuery) {

		return "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ "'B2B' TABLE_TYPE,HDR.INV_TYPE,'' TYPE, HDR.INV_NUM,HDR.INV_DATE,"
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE,'' CRDRPreGST,HDR.CTIN CUST_GSTIN,"
				+ "HDR.POS, HDR.SERIAL_NUM LINE_NUM,ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT, ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT, ITM.CESS_AMT,HDR.INV_VALUE,"
				+ "HDR.RCHRG,HDR.ETIN E_COM_GSTIN, '' ORG_INV_NUM,'' ORG_INV_DATE,"
				+ " '' PORT_CODE, '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,"
				+ "BT.IS_FILED, '' D_FLAG FROM GETGSTR1_B2B_HEADER HDR INNER JOIN "
				+ "GETGSTR1_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,'B2BA' TABLE_TYPE,"
				+ "HDR.INV_TYPE,'' TYPE,HDR.INV_NUM DOC_NUM,HDR.INV_DATE DOC_DATE,"
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE, '' CRDRPreGST, HDR.CTIN CUST_GSTIN,"
				+ "HDR.POS, HDR.SERIAL_NUM LINE_NUM,ITM.TAX_RATE,HDR.DIFF_PERCENT,"
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT,ITM.CGST_AMT,ITM.SGST_AMT,"
				+ "ITM.CESS_AMT,HDR.INV_VALUE,HDR.RCHRG,HDR.ETIN E_COM_GSTIN, "
				+ " '' ORG_INV_NUM,'' ORG_INV_DATE,'' PORT_CODE, '' SHIPP_BILL_NUM,"
				+ " '' SHIPP_BILL_DATE,BT.IS_FILED,'' D_FLAG "
				+ " FROM GETGSTR1_B2BA_HEADER HDR INNER JOIN "
				+ "GETGSTR1_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE "
				+ "HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ " 'CDNR' TABLE_TYPE,"
				+ "HDR.INV_TYPE||','||HDR.NOTE_TYPE AS INV_TYPE,'' TYPE,"
				+ "HDR.NOTE_NUM DOC_NUM,"
				+ "HDR.NOTE_DATE DOC_DATE,HDR.INV_NUM ORG_DOC_NUM,"
				+ "HDR.INV_DATE ORG_DOC_DATE,HDR.PRE_GST CRDRPreGST,"
				+ "HDR.CTIN CUST_GSTIN,HDR.POS,HDR.SERIAL_NUM LINE_NUM,"
				+ "ITM.TAX_RATE,HDR.DIFF_PERCENT,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT, 0 INV_VALUE,HDR.RCHRG,"
				+ " '' E_COM_GSTIN,'' ORG_INV_NUM, '' ORG_INV_DATE,'' PORT_CODE,"
				+ " '' SHIPP_BILL_NUM, '' SHIPP_BILL_DATE,BT.IS_FILED,D_FLAG FROM "
				+ "GETGSTR1_CDNR_HEADER HDR INNER JOIN GETGSTR1_CDNR_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ " 'CDNRA' TABLE_TYPE,"
				+ "HDR.INV_TYPE||','||HDR.NOTE_TYPE AS INV_TYPE,"
				+ " '' TYPE,HDR.NOTE_NUM DOC_NUM,"
				+ "HDR.NOTE_DATE DOC_DATE,HDR.ORG_NOTE_NUM ORG_DOC_NUM,"
				+ "HDR.ORG_NOTE_DATE ORG_DOC_DATE,HDR.PRE_GST CRDRPreGST,"
				+ "HDR.CTIN CUST_GSTIN,HDR.POS,HDR.SERIAL_NUM LINE_NUM,"
				+ "ITM.TAX_RATE,HDR.DIFF_PERCENT,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "ITM.CGST_AMT,ITM.SGST_AMT,ITM.CESS_AMT,0 INV_VALUE,HDR.RCHRG, "
				+ " '' E_COM_GSTIN,HDR.INV_NUM ORG_INV_NUM, HDR.INV_DATE ORG_INV_DATE,"
				+ " '' PORT_CODE, '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,"
				+ "BT.IS_FILED ,D_FLAG FROM GETGSTR1_CDNRA_HEADER HDR INNER JOIN "
				+ "GETGSTR1_CDNRA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,'CDNUR' TABLE_TYPE,"
				+ "HDR.NOTE_TYPE AS INV_TYPE,HDR.TYPE,HDR.NOTE_NUM DOC_NUM,"
				+ "HDR.NOTE_DATE DOC_DATE,HDR.INV_NUM ORG_DOC_NUM,"
				+ "HDR.INV_DATE ORG_DOC_DATE,HDR.PRE_GST CRDRPreGST,"
				+ "'' CUST_GSTIN,HDR.POS,HDR.SERIAL_NUM LINE_NUM, ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT,ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "0 CGST_AMT,0 SGST_AMT,ITM.CESS_AMT,0 INV_VALUE,'' RCHRG, "
				+ " '' E_COM_GSTIN,'' ORG_INV_NUM,'' ORG_INV_DATE,'' PORT_CODE,"
				+ " '' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE,BT.IS_FILED ,D_FLAG FROM "
				+ "GETGSTR1_CDNUR_HEADER HDR INNER JOIN GETGSTR1_CDNUR_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD, "
				+ " 'CDNURA' TABLE_TYPE,HDR.NOTE_TYPE AS INV_TYPE,"
				+ "HDR.TYPE, HDR.NOTE_NUM DOC_NUM,"
				+ "HDR.NOTE_DATE DOC_DATE,HDR.ORG_NOTE_NUM ORG_DOC_NUM,"
				+ "HDR.ORG_NOTE_DATE ORG_DOC_DATE, HDR.PRE_GST CRDRPreGST,"
				+ " '' CUST_GSTIN,HDR.POS, HDR.SERIAL_NUM LINE_NUM,ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT, ITM.TAXABLE_VALUE,ITM.IGST_AMT,0 CGST_AMT,"
				+ "0 SGST_AMT, ITM.CESS_AMT,0 INV_VALUE,'' RCHRG,'' E_COM_GSTIN,"
				+ "HDR.INV_NUM ORG_INV_NUM,HDR.INV_DATE ORG_INV_DATE,"
				+ " '' PORT_CODE,'' SHIPP_BILL_NUM,'' SHIPP_BILL_DATE, "
				+ "BT.IS_FILED,D_FLAG "
				+ "FROM GETGSTR1_CDNURA_HEADER HDR INNER JOIN "
				+ "GETGSTR1_CDNURA_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE "
				+ "HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ " 'EXP' TABLE_TYPE,HDR.EXPORT_TYPE,'' TYPE,"
				+ "HDR.INV_NUM DOC_NUM, HDR.INV_DATE DOC_DATE,"
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE, '' CRDRPreGST,'' CUST_GSTIN,"
				+ " '' POS,0 LINE_NUM,ITM.TAX_RATE, HDR.DIFF_PERCENT,"
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT, 0 CGST_AMT,"
				+ "0 SGST_AMT,ITM.CESS_AMT,0 INV_VALUE,'' RCHRG, "
				+ " '' E_COM_GSTIN,HDR.INV_NUM ORG_INV_NUM,"
				+ "HDR.INV_DATE ORG_INV_DATE,HDR.SHIPP_BILL_PORT_CODE PORT_CODE,"
				+ "HDR.SHIPP_BILL_NUM,HDR.SHIPP_BILL_DATE,BT.IS_FILED ,'' D_FLAG "
				+ "FROM GETGSTR1_EXP_HEADER HDR INNER JOIN GETGSTR1_EXP_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT JOIN "
				+ "GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ " 'EXPA' TABLE_TYPE,HDR.EXPORT_TYPE,'' TYPE,"
				+ "HDR.INV_NUM DOC_NUM,HDR.INV_DATE DOC_DATE,"
				+ "HDR.ORG_INV_NUM ORG_DOC_NUM,HDR.ORG_INV_DATE ORG_DOC_DATE,"
				+ " '' CRDRPreGST,'' CUST_GSTIN,'' POS,0 LINE_NUM,ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT,ITM.TAXABLE_VALUE,ITM.IGST_AMT,0 CGST_AMT,"
				+ "0 SGST_AMT,ITM.CESS_AMT,0 INV_VALUE,'' RCHRG,'' E_COM_GSTIN,"
				+ "HDR.INV_NUM ORG_INV_NUM,HDR.INV_DATE ORG_INV_DATE,"
				+ "HDR.SHIPP_BILL_PORT_CODE PORT_CODE,HDR.SHIPP_BILL_NUM,"
				+ "HDR.SHIPP_BILL_DATE,BT.IS_FILED ,  '' D_FLAG "
				+ "FROM GETGSTR1_EXPA_HEADER "
				+ "HDR INNER JOIN GETGSTR1_EXPA_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT "
				+ "ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE "
				+ "AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,'B2CL' TABLE_TYPE,HDR.INV_TYPE,"
				+ " '' TYPE,HDR.INV_NUM DOC_NUM, HDR.INV_DATE DOC_DATE,"
				+ " '' ORG_DOC_NUM,'' ORG_DOC_DATE, '' CRDRPreGST,'' CUST_GSTIN,"
				+ "HDR.POS,HDR.SERIAL_NUM LINE_NUM,ITM.TAX_RATE,HDR.DIFF_PERCENT,"
				+ "ITM.TAXABLE_VALUE,ITM.IGST_AMT, 0 CGST_AMT,0 SGST_AMT,"
				+ "ITM.CESS_AMT,0 INV_VALUE,'' RCHRG, HDR.ETIN E_COM_GSTIN,"
				+ "HDR.INV_NUM ORG_INV_NUM, HDR.INV_DATE ORG_INV_DATE,"
				+ " '' PORT_CODE,'' SHIPP_BILL_NUM, '' SHIPP_BILL_DATE,"
				+ "BT.IS_FILED,'' D_FLAG FROM GETGSTR1_B2CL_HEADER HDR INNER JOIN "
				+ "GETGSTR1_B2CL_ITEM ITM ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ buildQuery
				+ " UNION ALL "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ " 'B2CLA' TABLE_TYPE,HDR.INV_TYPE,'' TYPE,HDR.INV_NUM DOC_NUM,"
				+ "HDR.INV_DATE DOC_DATE,HDR.ORG_INV_NUM ORG_DOC_NUM,"
				+ "HDR.ORG_INV_DATE ORG_DOC_DATE,'' CRDRPreGST,"
				+ " '' CUST_GSTIN, HDR.POS,HDR.SERIAL_NUM LINE_NUM,ITM.TAX_RATE,"
				+ "HDR.DIFF_PERCENT, ITM.TAXABLE_VALUE,ITM.IGST_AMT,"
				+ "0 CGST_AMT,0 SGST_AMT, ITM.CESS_AMT,0 INV_VALUE,"
				+ " '' RCHRG,HDR.ETIN E_COM_GSTIN, HDR.INV_NUM ORG_INV_NUM,"
				+ "HDR.INV_DATE ORG_INV_DATE, '' PORT_CODE,'' SHIPP_BILL_NUM,"
				+ " '' SHIPP_BILL_DATE, BT.IS_FILED,'' D_FLAG "
				+ "FROM GETGSTR1_B2CLA_HEADER "
				+ "HDR INNER JOIN GETGSTR1_B2CLA_ITEM ITM ON "
				+ "HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT "
				+ "ON HDR.BATCH_ID = BT.ID WHERE HDR.IS_DELETE = FALSE "
				+ "AND BT.IS_DELETE = FALSE "
				+ buildQuery ;
				
				

	}
}
