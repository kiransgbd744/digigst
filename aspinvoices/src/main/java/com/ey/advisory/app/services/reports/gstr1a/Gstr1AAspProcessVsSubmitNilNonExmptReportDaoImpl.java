/**
 * 
 */
package com.ey.advisory.app.services.reports.gstr1a;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.views.client.NilNonProcessSubmitdto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

@Component("Gstr1AAspProcessVsSubmitNilNonExmptReportDaoImpl")
public class Gstr1AAspProcessVsSubmitNilNonExmptReportDaoImpl
		implements Gstr1AAspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public List<Object> aspProcessVsSubmitDaoReports(SearchCriteria criteria) {

		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		// String dataType = request.getDataType();
		// String taxperiod = request.getTaxperiod();
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
		StringBuilder buildHeaderNen = new StringBuilder();
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" WHERE SUPPLIER_GSTIN IN :gstinList ");
				buildHeaderNen.append(" NEN.SUPPLIER_GSTIN IN :gstinList ");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				// buildHeader.append(" AND PROFIT_CENTRE IN :pcList");
				buildHeaderNen.append(" AND NEN.PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				// buildHeader.append(" AND PLANT_CODE IN :plantList");
				buildHeaderNen.append(" AND NEN.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				// buildHeader.append(" AND SALES_ORGANIZATION IN :salesList");
				buildHeaderNen
						.append(" AND NEN.SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				// buildHeader.append(" AND DISTRIBUTION_CHANNEL IN :distList");
				buildHeaderNen
						.append(" AND NEN.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				// buildHeader.append(" AND DIVISION IN :divisionList");
				buildHeaderNen.append(" AND NEN.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				// buildHeader.append(" AND LOCATION IN :locationList");
				buildHeaderNen.append(" AND NEN.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				// buildHeader.append(" AND USERACCESS1 IN :ud1List");
				buildHeaderNen.append(" AND NEN.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				// buildHeader.append(" AND USERACCESS2 IN :ud2List");
				buildHeaderNen.append(" AND NEN.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				// buildHeader.append(" AND USERACCESS3 IN :ud3List");
				buildHeaderNen.append(" AND NEN.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				// buildHeader.append(" AND USERACCESS4 IN :ud4List");
				buildHeaderNen.append(" AND NEN.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				// buildHeader.append(" AND USERACCESS5 IN :ud5List");
				buildHeaderNen.append(" AND NEN.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				// buildHeader.append(" AND USERACCESS6 IN :ud6List");
				buildHeaderNen.append(" AND NEN.USERACCESS6 IN :ud6List");
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND A.DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");
			buildHeaderNen.append(
					" AND NEN.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");

		}


//		String multiSupplyTypeAns = groupConfigPrmtRepository
//				.findAnswerForMultiSupplyType();

		String multiSupplyTypeAns = "";
		String queryStr = createApiProcessedQueryString(buildHeader.toString(),
				buildHeaderNen.toString(), multiSupplyTypeAns);
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

		List<Object[]> list = q.getResultList();
		ProcessingContext context = new ProcessingContext();
		settingFiledGstins(context);
		return list.parallelStream()
				.map(o -> convertApiInwardProcessedRecords(o, context))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private NilNonProcessSubmitdto convertApiInwardProcessedRecords(
			Object[] arr, ProcessingContext context) {
		NilNonProcessSubmitdto obj = new NilNonProcessSubmitdto();

		obj.setsNo(arr[0] != null ? arr[0].toString() : null);
		obj.setGstnSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setGstnReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setGstnSupplyType(arr[3] != null ? arr[3].toString() : null);
		obj.setGstnNilAmt(arr[4] != null ? arr[4].toString() : null);
		obj.setGstnExemptedAmt(arr[5] != null ? arr[5].toString() : null);
		obj.setGstnNonGstSupAmt(arr[6] != null ? arr[6].toString() : null);
		// obj.setIsFilled(arr[7] != null ? arr[7].toString() : null);
		obj.setIsFilled(isGstinTaxperiodFiled(context, arr[1].toString(),
				arr[2].toString()));
		/*
		 * obj.setDigigstSupplierGSTIN(arr[9] != null ? arr[9].toString() :
		 * null); obj.setDigigstReturnPeriod(arr[10] != null ?
		 * arr[10].toString() : null);
		 */
		obj.setDigigstSupplyType(arr[8] != null ? arr[8].toString() : null);
		obj.setDigigstNilAmt(arr[9] != null ? arr[9].toString() : null);
		obj.setDigigstExemptedAmt(arr[10] != null ? arr[10].toString() : null);
		obj.setDigigstNonGstSupAmt(arr[11] != null ? arr[11].toString() : null);

		obj.setUiSupplyType(arr[12] != null ? arr[12].toString() : null);
		obj.setUiNilAmt(arr[13] != null ? arr[13].toString() : null);
		obj.setUiExemptedAmt(arr[14] != null ? arr[14].toString() : null);
		obj.setUiNonGstSupAmt(arr[15] != null ? arr[15].toString() : null);

		obj.setDiffNilAmt(arr[16] != null ? arr[16].toString() : null);
		obj.setDiffExemptedAmt(arr[17] != null ? arr[17].toString() : null);
		obj.setDiffNonGstSupAmt(arr[18] != null ? arr[18].toString() : null);
		// gstn Ref ID List

		obj.setSaveStatus(arr[19] != null ? arr[19].toString() : null);
		obj.setgSTNRefID(arr[20] != null ? arr[20].toString() : null);
		obj.setgSTNRefIDTime(arr[21] != null ? arr[21].toString() : null);
		obj.setgSTNErrorcode(arr[22] != null ? arr[22].toString() : null);
		obj.setgSTNErrorDescription(
				arr[23] != null ? arr[23].toString() : null);

		return obj;
	}

	private static String createApiProcessedQueryString(String buildHeader,
			String buildHeaderNen, String multiSupplyTypeAns) {

		StringBuilder build = new StringBuilder();

			build.append(
					"select  ROW_NUMBER () OVER (ORDER BY A.supplier_gstin) SNO,");
			build.append(
					"A.SUPPLIER_GSTIN,A.RETURN_PERIOD,A.GSTN_SUPPLY_TYPE,A.GSTN_NIL_AMT,");
			build.append(
					"A.GSTN_EXPT_AMT,A.GSTN_NONGST_SUP_AMT,A.IS_FILED,A.DG_SUPPLY_TYPE,");
			build.append(
					"A.DG_NIL_AMT,A.DG_EXPT_AMT,A.DG_NONGST_SUP_AMT,A.UI_SUPPLY_TYPE,");
			build.append(
					"A.UI_NIL_AMT,A.UI_EXPT_AMT,A.UI_NONGST_SUP_AMT,A.DIFF_NIL_AMT,");
			build.append("A.DIFF_EXPT_AMT,A.DIFF_NONGST_SUP_AMT,");
			build.append(
					"map(batch.GSTN_SAVE_STATUS,'P','SAVED','NOT SAVED') as SAVE_STATUS,");
			build.append("batch.GSTN_SAVE_REF_ID,");
			build.append(
					" TO_CHAR(ADD_SECONDS(batch.BATCH_DATE,19800),'DD-MM-YYYY HH24:MI:SS') AS BATCH_DATE , ");
			// build.append("add_seconds(batch.BATCH_DATE,19800) BATCH_DATE,");
			build.append(
					"batch.ERROR_CODE,batch.ERROR_DESC,A.DERIVED_RET_PERIOD ");
			build.append("from ( with gstn as ");
			build.append("( SELECT   hdr.gstin   supplier_gstin,");
			build.append("hdr.return_period      return_period,");
			build.append("hdr.derived_ret_period derived_ret_period,");
			build.append(
					"map(SUPPLY_TYPE,'INTRB2B','INTER-B2B','INTRAB2B','INTRA-B2B','INTRB2C','INTER-B2C','INTRAB2C','INTRA-B2C')  as  SUPPLY_TYPE,");
			build.append(
					"Sum(nil_amt)        gstn_nil_amt, Sum(expt_amt)       gstn_expt_amt,");
			build.append(
					"Sum(nongst_sup_amt) gstn_nongst_sup_amt, bt.is_filed,");
			build.append(
					" 'NILEXTNON' AS table_type FROM      getgstr1a_nilextnon HDR ");
			build.append("LEFT JOIN getanx1_batch_table BT ");
			build.append("ON        hdr.batch_id = bt.id ");
			build.append("WHERE     hdr.is_delete=false ");
			build.append("AND       bt.is_delete=false GROUP BY  hdr.id,");
			build.append(
					"hdr.gstin, hdr.return_period, supply_type, bt.is_filed, hdr.derived_ret_period ) ");
			build.append(", UI as (  ");
			build.append(
					"SELECT NEN.SUPPLIER_GSTIN ,NEN.RETURN_PERIOD,	NEN.DERIVED_RET_PERIOD,");
			build.append("(CASE WHEN DESCRIPTION_KEY='8A' THEN 'INTER-B2B'  ");
			build.append("WHEN DESCRIPTION_KEY='8B' THEN 'INTRA-B2B'  ");
			build.append("WHEN DESCRIPTION_KEY='8C' THEN 'INTER-B2C' ");
			build.append("WHEN DESCRIPTION_KEY='8D' THEN 'INTRA-B2C' ");
			build.append(
					"END) AS SUPPLY_TYPE, IFNULL(SUM(NIL_RATED_SUPPLIES),0) UI_NIL_AMT, ");
			build.append("IFNULL(SUM(NON_GST_SUPPLIES),0) ui_nongst_sup_amt ,");
			build.append("IFNULL(SUM(EXMPTED_SUPPLIES),0) ui_expt_amt, ");
			build.append(
					"max(batch_id) ui_batch_id FROM GSTR1A_USERINPUT_NILEXTNON NEN ");
			build.append(
					"WHERE NEN.IS_DELETE=FALSE  GROUP BY NEN.SUPPLIER_GSTIN,");
			build.append(
					"NEN.RETURN_PERIOD,NEN.DERIVED_RET_PERIOD,NEN.DESCRIPTION_KEY ) ");
			build.append(",  digi as ( SELECT   supplier_gstin,");
			build.append(
					"return_period,derived_ret_period,supply_type              supply_type,");
			build.append("Sum(nil_amt)             dg_nil_amt,");
			build.append("Sum(exmpted_amt)         dg_expt_amt ,");
			build.append("Sum(non_gst_supplies_amt)dg_nongst_sup_amt,");
			build.append("max(batch_id) dg_batch_id ");
			build.append("FROM     ( ");
			build.append(
					"SELECT          nen.supplier_gstin ,nen.return_period,nen.derived_ret_period, ( ");
			build.append("CASE WHEN TABLE_SECTION='8A' THEN 'INTER-B2B' ");
			build.append("WHEN TABLE_SECTION='8B' THEN 'INTRA-B2B' ");
			build.append("WHEN TABLE_SECTION='8C' THEN 'INTER-B2C' ");
			build.append(" WHEN TABLE_SECTION='8D' THEN 'INTRA-B2C' ");
			build.append("END)          AS supply_type, ");
			build.append(
					"IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND ");
			build.append("SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) ");
			build.append(
					" - IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0)  AS nil_amt,");
			build.append(
					"IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) ");
			build.append(
					"- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0)    AS non_gst_supplies_amt ,");
			build.append(
					"IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0)-  ");
			build.append(
					"IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS exmpted_amt,");
			build.append(
					"max(batch_id) batch_id FROM            ANX_OUTWARD_DOC_HEADER_1A NEN ");
			build.append("LEFT OUTER JOIN gstr1_gstn_save_batch GSTNBATCH ");
			build.append("ON   gstnbatch.id = nen.batch_id ");
			build.append("WHERE  NEN.TAX_DOC_TYPE IN ('NILEXTNON') ");
			build.append(
					"AND NEN.ASP_INVOICE_STATUS = 2 AND NEN.COMPLIANCE_APPLICABLE=TRUE  ");
			build.append(
					"AND NEN.IS_DELETE = FALSE AND NEN.SUPPLY_TYPE <> 'CAN'  ");
			build.append("AND NEN.RETURN_TYPE='GSTR1A' AND ");
			build.append(buildHeaderNen);
			build.append(" GROUP BY   nen.supplier_gstin,");
			build.append(
					"nen.return_period, nen.derived_ret_period,nen.TABLE_SECTION ");

			build.append("UNION ALL SELECT nen.supplier_gstin ,");
			build.append("nen.return_period,nen.derived_ret_period, ( ");
			build.append("CASE WHEN TABLE_SECTION='8A' THEN 'INTER-B2B' ");
			build.append("WHEN TABLE_SECTION='8B' THEN 'INTRA-B2B' ");
			build.append("WHEN TABLE_SECTION='8C' THEN 'INTER-B2C' ");
			build.append("WHEN TABLE_SECTION='8D' THEN 'INTRA-B2C' ");
			build.append("END)  AS supply_type, ");
			build.append(
					"CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(SUM(TAXABLE_VALUE),0) END AS nil_amt,");
			build.append(
					"CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(SUM(TAXABLE_VALUE),0)  ");
			build.append("END AS NON_GST_SUPPLIES,");
			build.append(
					"CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(SUM(TAXABLE_VALUE),0) END AS EXMPTED_SUPPLIES,");
			build.append("max(batch_id) FROM   GSTR1A_SUMMARY_NILEXTNON NEN ");
			build.append("WHERE           NEN.IS_DELETE = FALSE  ");
			build.append("GROUP BY        nen.supplier_gstin,");
			build.append("nen.return_period, nen.derived_ret_period,");
			build.append("nen.TABLE_SECTION,NEN.SUPPLY_TYPE   ) ");
			build.append(
					"GROUP BY supplier_gstin,return_period, derived_ret_period, supply_type ) ");
			build.append("select  ");
			build.append(
					"ifnull(ifnull(gstn.SUPPLIER_GSTIN,UI.supplier_gstin),digi.supplier_gstin) as supplier_gstin,");
			build.append(
					"ifnull(ifnull(gstn.return_period,UI.return_period),digi.return_period) as return_period,");
			build.append("gstn.SUPPLY_TYPE  as gstn_SUPPLY_TYPE,");
			build.append(
					"gstn_nil_amt as gstn_nil_amt,	gstn_expt_amt as gstn_expt_amt,");
			build.append("gstn_nongst_sup_amt as gstn_nongst_sup_amt,");
			build.append(
					"is_filed is_filed,digi.SUPPLY_TYPE as dg_supply_type,");
			build.append(
					"dg_nil_amt as dg_nil_amt,dg_expt_amt as dg_expt_amt,");
			build.append("dg_nongst_sup_amt as dg_nongst_sup_amt,");
			build.append(
					"ui.supply_type as ui_supply_type,ui_nil_amt as ui_nil_amt,");
			build.append(
					"ui_expt_amt as ui_expt_amt,ui_nongst_sup_amt as ui_nongst_sup_amt,");
			build.append(
					"(ifnull(dg_nil_amt,0)-ifnull(gstn_nil_amt,0)) as diff_nil_amt,");
			build.append(
					"(ifnull(dg_expt_amt,0)-ifnull(gstn_expt_amt,0)) as diff_expt_amt,");
			build.append(
					"(ifnull(dg_nongst_sup_amt,0)-ifnull(gstn_nongst_sup_amt,0)) as diff_nongst_sup_amt,");
			build.append(
					"ifnull(ifnull(gstn.derived_ret_period,ui.derived_ret_period),digi.derived_ret_period) as DERIVED_RET_PERIOD,");
			build.append(
					"(case when UI_BATCH_ID> DG_BATCH_ID then UI_BATCH_ID ");
			build.append("else DG_BATCH_ID  end) as batch_id from gstn ");
			build.append(
					"full outer join ui on gstn.supplier_gstin = UI.supplier_gstin ");
			build.append("and gstn.derived_ret_period =UI.derived_ret_period ");
			build.append("and gstn.supply_type = ui.supply_type ");
			build.append("full outer join digi ");
			build.append(
					"on IFNULL(UI.supplier_gstin,gstn.supplier_gstin)  = digi.supplier_gstin ");
			build.append(
					"and IFNULL(ui.derived_ret_period,gstn.derived_ret_period)  = digi.derived_ret_period ");
			build.append(
					"and IFNULL(ui.supply_type,gstn.supply_type) = digi.supply_type ) A left join ");
			build.append(
					"( select ID,GSTN_SAVE_STATUS,GSTN_SAVE_REF_ID,BATCH_DATE,ERROR_CODE,ERROR_DESC ");
			build.append("from GSTR1_GSTN_SAVE_BATCH where is_delete = FALSE ");
			build.append("and section='NIL' ) batch on  A.BATCH_ID=batch.id ");
			build.append(buildHeader);
		

		return build.toString();

	}

	private void settingFiledGstins(ProcessingContext context) {

		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR1A", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {
			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	private String isGstinTaxperiodFiled(ProcessingContext context,
			String gstin, String taxPeriod) {
		String filingStatus = "False";
		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + DOC_KEY_JOINER + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			filingStatus = "True";
		}
		return filingStatus;

	}

}
