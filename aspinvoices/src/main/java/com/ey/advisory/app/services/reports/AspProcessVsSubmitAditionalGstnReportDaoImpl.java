package com.ey.advisory.app.services.reports;

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

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.views.client.AspProcessVsSubmitAdditionalGstnDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Component("AspProcessVsSubmitAditionalGstnReportDaoImpl")
public class AspProcessVsSubmitAditionalGstnReportDaoImpl
		implements AspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Override
	public List<Object> aspProcessVsSubmitDaoReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String taxPeriodFrom = request.getTaxPeriodFrom();
		String taxPeriodTo = request.getTaxPeriodTo();
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

		StringBuilder buildHeader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" WHERE SUPPLIER_GSTIN IN :gstinList");

			}
		}
/*		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeader.append(" AND PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeader.append(" AND PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeader.append(" AND SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeader.append(" AND DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeader.append(" AND DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeader.append(" AND LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeader.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeader.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeader.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeader.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeader.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeader.append(" AND USERACCESS6 IN :ud6List");
			}
		}*/
		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");

		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodTo());
			q.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("taxPeriodTo", derivedRetPeriodTo);
		}
		/*if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
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
		}*/
		List<Object[]> list = q.getResultList();
		List<Object> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<StateCodeInfoEntity> MasterEntities = statecodeRepository
					.findAll();
			Map<String, String> hsnMap = new HashMap<String, String>();
			MasterEntities.forEach(entity -> {
				hsnMap.put(entity.getStateCode(), entity.getStateName());
			});

			for (Object arr[] : list) {
				verticalHsnList.add(createApiProcessedConvertions(arr, hsnMap));
			}
		}
		return verticalHsnList;
	}

	private AspProcessVsSubmitAdditionalGstnDto createApiProcessedConvertions(
			Object[] arr, Map<String, String> map) {
		AspProcessVsSubmitAdditionalGstnDto obj = new AspProcessVsSubmitAdditionalGstnDto();
		obj.setIrn(arr[0] != null ? arr[0].toString() : null);
		obj.setIrnDate(arr[1] != null ? arr[1].toString() : null);
		obj.setEInvStatus(arr[2] != null ? arr[2].toString() : null);
		obj.setAutoDraftStatus(arr[3] != null ? arr[3].toString() : null);
		obj.setErrorCode(arr[4] != null ? arr[4].toString() : null);
		obj.setErrorMessage(arr[5] != null ? arr[5].toString() : null);
		obj.setRetPeriod(arr[6] != null ? arr[6].toString() : null);
		obj.setSupplierGstn(arr[7] != null ? arr[7].toString() : null);
		obj.setCustGstin(arr[8] != null ? arr[8].toString() : null);
		obj.setCustTradeName(arr[9] != null ? arr[9].toString() : null);
		obj.setDocumentType(arr[10] != null ? arr[10].toString() : null);
		obj.setSuuplyType(arr[11] != null ? arr[11].toString() : null);
		obj.setDocumentNumber(arr[12] != null ? arr[12].toString() : null);
		obj.setDocumnetDate(arr[13] != null ? arr[13].toString() : null);
		obj.setBillingPos(arr[14] != null ? arr[14].toString() : null);
		obj.setPortCode(arr[15] != null ? arr[15].toString() : null);
		obj.setShippingBillNumber(arr[16] != null ? arr[16].toString() : null);
		obj.setShippingBillDate(arr[17] != null ? arr[17].toString() : null);
		obj.setRevChargeFlag(arr[18] != null ? arr[18].toString() : null);
		obj.setEcomGstin(arr[19] != null ? arr[19].toString() : null);
		obj.setItemSerialNumber(arr[20] != null ? arr[20].toString() : null);
		obj.setItemAccessableAmt(arr[21] != null ? arr[21].toString() : null);
		obj.setTaxRate(arr[22] != null ? arr[22].toString() : null);
		obj.setIgstAmt(arr[23] != null ? arr[23].toString() : null);
		obj.setCgstAmt(arr[24] != null ? arr[24].toString() : null);
		obj.setSgstAmt(arr[25] != null ? arr[25].toString() : null);
		obj.setCessAmt(arr[26] != null ? arr[26].toString() : null);
		obj.setInvValue(arr[27] != null ? arr[27].toString() : null);
		obj.setSourceTypeIrn(arr[28] != null ? arr[28].toString() : null);
		obj.setTableType(arr[29] != null ? arr[29].toString() : null);
		obj.setDerRetPeriod(arr[30] != null ? arr[30].toString() : null);
		// obj.setTableType(arr[31] != null ? arr[31].toString() : null);
		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery) {
		StringBuilder build = new StringBuilder();

		
		build.append("SELECT irn_num,irn_gen_date,e_invoicestatus,autodraftstatus,errorcode, "
				+ "errormessage,return_period,supplier_gstin,cust_gstin,customertradename, "
				+ "documenttype,supplytype,documentnumber,documentdate,billingpos,port_code, "
				+ "shipp_bill_num,shipp_bill_date,rchrg,e_com_gstin,serial_num,taxable_value, "
				+ "tax_rate,igst_amt,cgst_amt,sgst_amt,cess_amt,inv_value,irn_source_type, "
				+ "table_type,derived_ret_period FROM ( "
				+ "SELECT irn_num,irn_gen_date,NULL e_invoicestatus,NULL autodraftstatus, "
				+ "NULL autodrafteddate,NULL errorcode,NULL errormessage,hdr.return_period, "
				+ "hdr.gstin  supplier_gstin,hdr.ctin     cust_gstin,NULL         customertradename, "
				+ "'INV'        documenttype,hdr.inv_type supplytype,inv_num      documentnumber, "
				+ "inv_date     documentdate,hdr.pos      billingpos,''           port_code, "
				+ " ''           shipp_bill_num,''           shipp_bill_date,hdr.rchrg, "
				+ "hdr.etin e_com_gstin,itm.serial_num,itm.taxable_value,itm.tax_rate, "
				+ "itm.igst_amt,itm.cgst_amt,itm.sgst_amt,itm.cess_amt,itm.inv_value, "
				+ "irn_source_type,'B2B' table_type,hdr.derived_ret_period FROM  getgstr1_b2b_header HDR "
				+ "INNER JOIN getgstr1_b2b_item ITM  ON         hdr.id = itm.header_id "
				+ "AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "AND IS_DELETE = FALSE AND TAX_DOC_TYPE = 'B2B' ) TRANS "
				+ "ON HDR.ID = TRANS.TABLE_ID LEFT JOIN  getanx1_batch_table BT "
				+ "ON         hdr.batch_id = bt.id WHERE      hdr.is_delete = false "
				+ "AND        bt.is_delete = false UNION ALL "
				+ "SELECT     NULL irn_num,NULL irn_gen_date,NULL e_invoicestatus,NULL autodraftstatus, "
				+ "NULL autodrafteddate,NULL errorcode,NULL errormessage,hdr.return_period, "
				+ "hdr.gstin    supplier_gstin,hdr.ctin     cust_gstin,NULL         customertradename, "
				+ "'RNV'        documenttype,hdr.inv_type supplytype,inv_num      documentnumber,inv_date     documentdate, "
				+ "hdr.pos      billingpos, ''           port_code, ''           shipp_bill_num,''           shipp_bill_date, "
				+ "hdr.rchrg, hdr.etin e_com_gstin,itm.serial_num,itm.taxable_value,itm.tax_rate, itm.igst_amt, "
				+ "itm.cgst_amt,itm.sgst_amt,itm.cess_amt,itm.inv_value,NULL   irn_source_type,'B2BA' table_type, "
				+ "hdr.derived_ret_period FROM       getgstr1_b2ba_header HDR INNER JOIN getgstr1_b2ba_item ITM "
				+ "ON         hdr.id = itm.header_id AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "AND IS_DELETE = FALSE AND TAX_DOC_TYPE = 'B2BA' ) TRANS "
				+ "ON HDR.ID = TRANS.TABLE_ID LEFT JOIN  getanx1_batch_table BT "
				+ "ON         hdr.batch_id = bt.id WHERE      hdr.is_delete = false AND        bt.is_delete = false "
				+ " UNION ALL SELECT     irn_num, irn_gen_date, NULL e_invoicestatus, NULL autodraftstatus, NULL autodrafteddate, "
				+ " NULL errorcode, NULL errormessage,hdr.return_period,hdr.gstin     supplier_gstin,hdr.ctin      cust_gstin, "
				+ " NULL          customertradename,hdr.note_type documenttype,hdr.inv_type  supplytype,note_num      documentnumber, "
				+ " note_date     documentdate, hdr.pos       billingpos,''            port_code, ''            shipp_bill_num, ''            shipp_bill_date, "
				+ "hdr.rchrg,NULL e_com_gstin,itm.serial_num,itm.taxable_value,itm.tax_rate,itm.igst_amt, "
				+ "itm.cgst_amt,itm.sgst_amt,itm.cess_amt,itm.inv_value,irn_source_type,'CDNR' table_type, "
				+ " hdr.derived_ret_period FROM  getgstr1_cdnr_header HDR INNER JOIN getgstr1_cdnr_item ITM "
				+ "ON         hdr.id = itm.header_id AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "         INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "         WHERE REPORT_TYPE='ADDITIONAL IN GSTN' AND IS_DELETE = FALSE AND TAX_DOC_TYPE = 'CDNR' "
				+ "        ) TRANS ON HDR.ID = TRANS.TABLE_ID LEFT JOIN  getanx1_batch_table BT ON         hdr.batch_id = bt.id "
				+ "         WHERE      hdr.is_delete = false AND bt.is_delete = false UNION ALL "
				+ "        SELECT     NULL irn_num,NULL irn_gen_date,NULL e_invoicestatus,NULL autodraftstatus, "
				+ "        NULL autodrafteddate,NULL errorcode,NULL errormessage,hdr.return_period,hdr.gstin     supplier_gstin, "
				+ "        hdr.ctin      cust_gstin,NULL          customertradename,hdr.note_type documenttype, "
				+ "        hdr.inv_type  supplytype,note_num      documentnumber,note_date     documentdate,hdr.pos       billingpos, "
				+ "         ''            port_code,''            shipp_bill_num,''            shipp_bill_date, "
				+ "        hdr.rchrg,NULL e_com_gstin,itm.serial_num,itm.taxable_value,itm.tax_rate,itm.igst_amt, "
				+ "        itm.cgst_amt,itm.sgst_amt,itm.cess_amt,itm.inv_value,NULL    irn_source_type, "
				+ "        'CDNRA' table_type, hdr.derived_ret_period FROM       getgstr1_cdnra_header HDR "
				+ "        INNER JOIN getgstr1_cdnra_item ITM ON         hdr.id = itm.header_id "
				+ "         AND hdr.derived_ret_period = itm.derived_ret_period "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "         WHERE REPORT_TYPE='ADDITIONAL IN GSTN' AND IS_DELETE = FALSE AND TAX_DOC_TYPE = 'CDNRA' "
				+ "         ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        LEFT JOIN  getanx1_batch_table BT "
				+ "         ON         hdr.batch_id = bt.id "
				+ "        WHERE      hdr.is_delete = false "
				+ "        AND        bt.is_delete = false "
				+ "        UNION ALL "
				+ "        SELECT     irn_num, "
				+ "        irn_gen_date, "
				+ "        NULL e_invoicestatus, "
				+ "        NULL autodraftstatus, "
				+ "        NULL autodrafteddate, "
				+ "        NULL errorcode, "
				+ "        NULL errormessage, "
				+ "        hdr.return_period, "
				+ "        hdr.gstin supplier_gstin, "
				+ "        NULL      cust_gstin, "
				+ "        NULL      customertradename, "
				+ "        'INV'     documenttype, "
				+ "        hdr.type  supplytype, "
				+ "        note_num  documentnumber, "
				+ "        note_date documentdate, "
				+ "        hdr.pos   billingpos, "
				+ "         ''        port_code, "
				+ "         ''        shipp_bill_num, "
				+ "         ''        shipp_bill_date, "
				+ "         NULL      rchrg, "
				+ "         NULL      e_com_gstin, "
				+ "         itm.serial_num, "
				+ "         itm.taxable_value, "
				+ "        itm.tax_rate, "
				+ "        itm.igst_amt, "
				+ "         0 cgst_amt, "
				+ "         0 sgst_amt, "
				+ "         itm.cess_amt, "
				+ "        itm.inv_value, "
				+ "        irn_source_type, "
				+ "        'CDNUR' table_type, "
				+ "         hdr.derived_ret_period "
				+ "        FROM       getgstr1_cdnur_header HDR "
				+ "        INNER JOIN getgstr1_cdnur_item ITM "
				+ "        ON         hdr.id = itm.header_id "
				+ "        AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "        WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "        AND IS_DELETE = FALSE "
				+ "        AND TAX_DOC_TYPE = 'CDNUR' "
				+ "        ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        LEFT JOIN  getanx1_batch_table BT "
				+ "        ON         hdr.batch_id = bt.id "
				+ "        WHERE      hdr.is_delete = false "
				+ "        AND        bt.is_delete = false "
				+ "        UNION ALL "
				+ "        SELECT     NULL irn_num, "
				+ "        NULL irn_gen_date, "
				+ "        NULL e_invoicestatus, "
				+ "        NULL autodraftstatus, "
				+ "        NULL autodrafteddate, "
				+ "        NULL errorcode, "
				+ "        NULL errormessage, "
				+ "        hdr.return_period, "
				+ "        hdr.gstin supplier_gstin, "
				+ "        NULL      cust_gstin, "
				+ "        NULL      customertradename, "
				+ "        'RNV'     documenttype, "
				+ "        hdr.type  supplytype, "
				+ "        note_num  documentnumber, "
				+ "        note_date documentdate, "
				+ "        hdr.pos   billingpos, "
				+ "        ''        port_code, "
				+ "        ''        shipp_bill_num, "
				+ "         ''        shipp_bill_date, "
				+ "         NULL      rchrg, "
				+ "        NULL      e_com_gstin, "
				+ "        itm.serial_num, "
				+ "        itm.taxable_value, "
				+ "        itm.tax_rate, "
				+ "        itm.igst_amt, "
				+ "        0 cgst_amt, "
				+ "        0 sgst_amt, "
				+ "        itm.cess_amt, "
				+ "        itm.inv_value, "
				+ "        NULL     irn_source_type, "
				+ "        'CDNURA' table_type, "
				+ "        hdr.derived_ret_period "
				+ "        FROM       getgstr1_cdnura_header HDR "
				+ "        INNER JOIN getgstr1_cdnura_item ITM "
				+ "        ON         hdr.id = itm.header_id "
				+ "        AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "        WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "        AND IS_DELETE = FALSE "
				+ "        AND TAX_DOC_TYPE = 'CDNURA' "
				+ "        ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        LEFT JOIN  getanx1_batch_table BT "
				+ "        ON         hdr.batch_id = bt.id "
				+ "        WHERE      hdr.is_delete = false "
				+ "        AND        bt.is_delete = false "
				+ "        UNION ALL "
				+ "        SELECT     irn_num, "
				+ "        irn_gen_date, "
				+ "        NULL e_invoicestatus, "
				+ "        NULL autodraftstatus, "
				+ "        NULL autodrafteddate, "
				+ "        NULL errorcode, "
				+ "        NULL errormessage, "
				+ "        hdr.return_period, "
				+ "        hdr.gstin                supplier_gstin, "
				+ "        NULL                     cust_gstin, "
				+ "        NULL                     customertradename, "
				+ "        'INV'                    documenttype, "
				+ "        hdr.export_type          supplytype, "
				+ "        inv_num                  documentnumber, "
				+ "        inv_date                 documentdate, "
				+ "        NULL                     billingpos, "
				+ "        hdr.shipp_bill_port_code port_code, "
				+ "        hdr.shipp_bill_num, "
				+ "        hdr.shipp_bill_date, "
				+ "        NULL rchrg, "
				+ "         NULL e_com_gstin, "
				+ "        0    serial_num, "
				+ "        itm.taxable_value, "
				+ "        itm.tax_rate, "
				+ "        itm.igst_amt, "
				+ "        0 cgst_amt, "
				+ "        0 sgst_amt, "
				+ "        itm.cess_amt, "
				+ "        itm.inv_value, "
				+ "        irn_source_type, "
				+ "        'EXP' table_type, "
				+ "        hdr.derived_ret_period "
				+ "        FROM       getgstr1_exp_header HDR "
				+ "        INNER JOIN getgstr1_exp_item ITM "
				+ "        ON         hdr.id = itm.header_id "
				+ "        AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "        WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "        AND IS_DELETE = FALSE "
				+ "        AND TAX_DOC_TYPE = 'EXPORTS' "
				+ "        ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        LEFT JOIN  getanx1_batch_table BT "
				+ "        ON         hdr.batch_id = bt.id "
				+ "        WHERE      hdr.is_delete = false "
				+ "        AND        bt.is_delete = false "
				+ "        UNION ALL "
				+ "        SELECT     NULL irn_num, "
				+ "        NULL irn_gen_date, "
				+ "        NULL e_invoicestatus, "
				+ "        NULL autodraftstatus, "
				+ "        NULL autodrafteddate, "
				+ "        NULL errorcode, "
				+ "        NULL errormessage, "
				+ "        hdr.return_period, "
				+ "        hdr.gstin                supplier_gstin, "
				+ "        NULL                     cust_gstin, "
				+ "        NULL                     customertradename, "
				+ "        'RNV'                    documenttype, "
				+ "        hdr.export_type          supplytype, "
				+ "        inv_num                  documentnumber, "
				+ "        inv_date                 documentdate, "
				+ "        NULL                     billingpos, "
				+ "        hdr.shipp_bill_port_code port_code, "
				+ "        hdr.shipp_bill_num, "
				+ "        hdr.shipp_bill_date, "
				+ "         NULL rchrg, "
				+ "        NULL e_com_gstin, "
				+ "        0    serial_num, "
				+ "        itm.taxable_value, "
				+ "        itm.tax_rate, "
				+ "        itm.igst_amt, "
				+ "        0 cgst_amt, "
				+ "        0 sgst_amt, "
				+ "        itm.cess_amt, "
				+ "        itm.inv_value, "
				+ "        NULL   irn_source_type, "
				+ "        'EXPA' table_type, "
				+ "        hdr.derived_ret_period "
				+ "        FROM       getgstr1_expa_header HDR "
				+ "        INNER JOIN getgstr1_expa_item ITM "
				+ "        ON         hdr.id = itm.header_id "
				+ "        AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "         WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "        AND IS_DELETE = FALSE "
				+ "         AND TAX_DOC_TYPE = 'EXPORTS-A' "
				+ "         ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        LEFT JOIN  getanx1_batch_table BT "
				+ "        ON         hdr.batch_id = bt.id "
				+ "        WHERE      hdr.is_delete = false "
				+ "        AND        bt.is_delete = false "
				+ "        UNION ALL "
				+ "        SELECT     NULL irn_num, "
				+ "        NULL irn_gen_date, "
				+ "        NULL e_invoicestatus, "
				+ "        NULL autodraftstatus, "
				+ "        NULL autodrafteddate, "
				+ "        NULL errorcode, "
				+ "        NULL errormessage, "
				+ "        hdr.return_period, "
				+ "        hdr.gstin    supplier_gstin, "
				+ "        NULL         cust_gstin, "
				+ "        NULL         customertradename, "
				+ "        'INV'        documenttype, "
				+ "        hdr.inv_type supplytype, "
				+ "        inv_num      documentnumber, "
				+ "        inv_date     documentdate, "
				+ "        hdr.pos      billingpos, "
				+ "        NULL         port_code, "
				+ "        NULL         shipp_bill_num, "
				+ "        NULL         shipp_bill_date, "
				+ "         NULL         rchrg, "
				+ "        hdr.etin     e_com_gstin, "
				+ "        itm.serial_num, "
				+ "        itm.taxable_value, "
				+ "        itm.tax_rate, "
				+ "        itm.igst_amt, "
				+ "        0 cgst_amt, "
				+ "        0 sgst_amt, "
				+ "        itm.cess_amt, "
				+ "        itm.inv_value, "
				+ "        NULL   irn_source_type, "
				+ "         'B2CL' table_type, "
				+ "        hdr.derived_ret_period "
				+ "        FROM       getgstr1_b2cl_header HDR "
				+ "         INNER JOIN getgstr1_b2cl_item ITM "
				+ "        ON         hdr.id = itm.header_id "
				+ "         AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "        WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "         AND IS_DELETE = FALSE "
				+ "         AND TAX_DOC_TYPE = 'B2CL' "
				+ "        ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        LEFT JOIN  getanx1_batch_table BT "
				+ "        ON         hdr.batch_id = bt.id "
				+ "        WHERE      hdr.is_delete = false "
				+ "        AND        bt.is_delete = false "
				+ "        UNION ALL "
				+ "        SELECT     NULL irn_num, "
				+ "        NULL irn_gen_date, "
				+ "        NULL e_invoicestatus, "
				+ "        NULL autodraftstatus, "
				+ "        NULL autodrafteddate, "
				+ "        NULL errorcode, "
				+ "        NULL errormessage, "
				+ "        hdr.return_period, "
				+ "        hdr.gstin    supplier_gstin, "
				+ "        NULL         cust_gstin, "
				+ "        NULL         customertradename, "
				+ "        'RNV'        documenttype, "
				+ "        hdr.inv_type supplytype, "
				+ "        inv_num      documentnumber, "
				+ "        inv_date     documentdate, "
				+ "        hdr.pos      billingpos, "
				+ "        NULL         port_code, "
				+ "        NULL         shipp_bill_num, "
				+ "        NULL         shipp_bill_date, "
				+ "        NULL         rchrg, "
				+ "        hdr.etin     e_com_gstin, "
				+ "        itm.serial_num, "
				+ "        itm.taxable_value, "
				+ "        itm.tax_rate, "
				+ "        itm.igst_amt, "
				+ "        0 cgst_amt, "
				+ "        0 sgst_amt, "
				+ "        itm.cess_amt, "
				+ "        itm.inv_value, "
				+ "        NULL    irn_source_type, "
				+ "        'B2CLA' table_type, "
				+ "        hdr.derived_ret_period "
				+ "        FROM       getgstr1_b2cla_header HDR "
				+ "        INNER JOIN getgstr1_b2cla_item ITM "
				+ "        ON         hdr.id = itm.header_id "
				+ "        AND        hdr.derived_ret_period = itm.derived_ret_period "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "        WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "        AND IS_DELETE = FALSE "
				+ "        AND TAX_DOC_TYPE = 'B2CLA' "
				+ "         ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        LEFT JOIN  getanx1_batch_table BT "
				+ "        ON         hdr.batch_id = bt.id "
				+ "        WHERE      hdr.is_delete = false "
				+ "        AND        bt.is_delete = false "
				+ "		UNION ALL "
				+ "		SELECT NULL irn_num, "
				+ "		       NULL irn_gen_date, "
				+ "			   NULL e_invoicestatus, "
				+ "			   NULL autodraftstatus, "
				+ "               NULL autodrafteddate, "
				+ "		       NULL errorcode, "
				+ "			   NULL errormessage, "
				+ "			   hdr.return_period, "
				+ "			   hdr.gstin     supplier_gstin, "
				+ "		       hdr.rtin      cust_gstin, "
				+ "			   NULL  customertradename, "
				+ "			   CASE WHEN TABLE_SECTION = '15(i)' THEN '15(i)' ELSE '15(iii)'  END AS documenttype, "
				+ "		       hdr.inv_type supplytype, "
				+ "			   inv_num      documentnumber, "
				+ "			   inv_date     documentdate, "
				+ "               hdr.pos      billingpos, "
				+ "			   ''            port_code, "
				+ "			   ''            shipp_bill_num, "
				+ "			   ''            shipp_bill_date, "
				+ "			   NULL rchrg, "
				+ "			   HDR.stin e_com_gstin, "
				+ "		       itm.serial_num, "
				+ "			   itm.taxable_value, "
				+ "			   itm.tax_rate, "
				+ "			   itm.igst_amt, "
				+ "			   itm.cgst_amt, "
				+ "			   itm.sgst_amt, "
				+ "			   itm.cess_amt, "
				+ "			   itm.inv_value, "
				+ "			   NULL  irn_source_type, "
				+ "		       CASE WHEN TABLE_SECTION = '15(i)' THEN '15(i)' "
				+ "       			    ELSE '15(iii)' "
				+ "				END AS table_type, "
				+ "				hdr.derived_ret_period "
				+ "		FROM GETGSTR1_ECOMSUP_HEADER HDR "
				+ "        INNER JOIN GETGSTR1_ECOMSUP_ITEM ITM "
				+ "        ON HDR.ID = ITM.HEADER_ID "
				+ "        AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "        WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "        AND IS_DELETE = FALSE AND TAX_DOC_TYPE = 'ECOM' ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        WHERE HDR.IS_DELETE = FALSE "
				+ "        AND  TABLE_SECTION IN ('15(i)','15(iii)') "
				+ "		 "
				+ "		UNION ALL "
				+ "		SELECT NULL irn_num, "
				+ "		       NULL irn_gen_date, "
				+ "			   NULL e_invoicestatus, "
				+ "			   NULL autodraftstatus, "
				+ "               NULL autodrafteddate, "
				+ "		       NULL errorcode, "
				+ "			   NULL errormessage, "
				+ "			   hdr.return_period, "
				+ "			   hdr.gstin     supplier_gstin, "
				+ "		       hdr.rtin      cust_gstin, "
				+ "			   NULL  customertradename, "
				+ "			   CASE WHEN TABLE_SECTION = '15A(i).a' THEN '15A(i).a' ELSE '15A(i).b'  END AS documenttype, "
				+ "		       hdr.inv_type supplytype, "
				+ "			   inv_num      documentnumber, "
				+ "			   inv_date     documentdate, "
				+ "               hdr.pos      billingpos, "
				+ "			   ''            port_code, "
				+ "			   ''            shipp_bill_num, "
				+ "			   ''            shipp_bill_date, "
				+ "			   NULL rchrg, "
				+ "			   HDR.stin e_com_gstin, "
				+ "		       itm.serial_num, "
				+ "			   itm.taxable_value, "
				+ "			   itm.tax_rate, "
				+ "			   itm.igst_amt, "
				+ "			   itm.cgst_amt, "
				+ "			   itm.sgst_amt, "
				+ "			   itm.cess_amt, "
				+ "			   itm.inv_value, "
				+ "			   NULL  irn_source_type, "
				+ "		       CASE WHEN TABLE_SECTION = '15A(i).a' THEN '15A(i).a' "
				+ "					ELSE '15A(i).b' "
				+ "				END AS table_type, "
				+ "				hdr.derived_ret_period "
				+ "		FROM GETGSTR1_ECOMSUP_AMD_HEADER HDR "
				+ "        INNER JOIN GETGSTR1_ECOMSUPAMD_ITEM ITM "
				+ "        ON HDR.ID = ITM.HEADER_ID "
				+ "        AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "        INNER JOIN ( select TABLE_ID from GSTR1_SUBMITTED_PS_TRANS "
				+ "        WHERE REPORT_TYPE='ADDITIONAL IN GSTN' "
				+ "        AND IS_DELETE = FALSE AND TAX_DOC_TYPE = 'ECOMA' ) TRANS "
				+ "        ON HDR.ID = TRANS.TABLE_ID "
				+ "        WHERE HDR.IS_DELETE = FALSE "
				+ "        AND  TABLE_SECTION IN ('15A(i).a','15A(i).b') "
				+ "		) "
				+ buildQuery);
		
		return build.toString();
	}

}
