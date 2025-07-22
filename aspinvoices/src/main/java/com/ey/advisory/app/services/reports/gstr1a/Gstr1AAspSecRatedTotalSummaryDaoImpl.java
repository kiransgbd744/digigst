package com.ey.advisory.app.services.reports.gstr1a;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.views.client.Gstr1AspVerticalNilDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

	@Component("Gstr1AAspSecRatedTotalSummaryDaoImpl")
	public class Gstr1AAspSecRatedTotalSummaryDaoImpl
			implements Gstr1AOutwardVerticalProcessNilDao {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr1AAspSecRatedTotalSummaryDaoImpl.class);

		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;
		
		@Autowired
		@Qualifier("GroupConfigPrmtRepository")
		private GroupConfigPrmtRepository groupConfigPrmtRepository;

		@Override
		public List<Object> getGstr1RSReports(SearchCriteria criteria) {
			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

			String dataType = request.getDataType();
			String taxPeriodFrom = request.getTaxPeriodFrom();
			String taxPeriodTo = request.getTaxPeriodTo();
			String taxDocType = request.getTaxDocType();

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
			StringBuilder buildQueryNen = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildQueryNen.append(" SUPPLIER_GSTIN IN :gstinList");
					buildQuery.append(" NEN.SUPPLIER_GSTIN IN :gstinList");
				}
			}
			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {
					buildQuery.append(" AND NEN.PROFIT_CENTRE IN :pcList");
				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && plantList.size() > 0) {
					buildQuery.append(" AND NEN.PLANT_CODE IN :plantList");
				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && salesList.size() > 0) {
					buildQuery.append(" AND NEN.SALES_ORGANIZATION IN :salesList");
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && distList.size() > 0) {
					buildQuery.append(" AND NEN.DISTRIBUTION_CHANNEL IN :distList");
				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {
					buildQuery.append(" AND NEN.DIVISION IN :divisionList");
				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && locationList.size() > 0) {
					buildQuery.append(" AND NEN.LOCATION IN :locationList");
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && ud1List.size() > 0) {
					buildQuery.append(" AND NEN.USERACCESS1 IN :ud1List");
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && ud2List.size() > 0) {
					buildQuery.append(" AND NEN.USERACCESS2 IN :ud2List");
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && ud3List.size() > 0) {
					buildQuery.append(" AND NEN.USERACCESS3 IN :ud3List");
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && ud4List.size() > 0) {
					buildQuery.append(" AND NEN.USERACCESS4 IN :ud4List");
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && ud5List.size() > 0) {
					buildQuery.append(" AND NEN.USERACCESS5 IN :ud5List");
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && ud6List.size() > 0) {
					buildQuery.append(" AND NEN.USERACCESS6 IN :ud6List");
				}
			}

			if (taxPeriodFrom != null && taxPeriodTo != null) {
				buildQuery.append(" AND NEN.DERIVED_RET_PERIOD BETWEEN ");
				buildQuery.append(":taxPeriodFrom AND :taxPeriodTo");
				
				buildQueryNen.append(" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo " );
				
			}

//			String multiSupplyTypeAns = groupConfigPrmtRepository
//					.findAnswerForMultiSupplyType(); 
			String multiSupplyTypeAns =  "";
			String queryStr = createNilRatedTotalSummQueryString(
					buildQuery.toString(),buildQueryNen.toString(), multiSupplyTypeAns);
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug(queryStr);
			}
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
			return list.parallelStream().map(o -> convertProcessedNil(o))
					.collect(Collectors.toCollection(ArrayList::new));
		}

		private Gstr1AspVerticalNilDto convertProcessedNil(Object[] arr) {
			Gstr1AspVerticalNilDto obj = new Gstr1AspVerticalNilDto();

			obj.setSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
			obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
			obj.setSupplyType(arr[2] != null ? arr[2].toString() : null);
			obj.setNilAmt(arr[3] != null ? arr[3].toString() : null);
			obj.setNonGstSupAmt(arr[5] != null ? arr[5].toString() : null);
			obj.setExemptedAmt(arr[4] != null ? arr[4].toString() : null);
			
			obj.setUnilAmt(arr[6] != null ? arr[6].toString() : null);
			obj.setUexemptedAmt(arr[7] != null ? arr[7].toString() : null);
			obj.setUnonGstSupAmt(arr[8] != null ? arr[8].toString() : null);
			
			obj.setSaveStatus(arr[9] != null ? arr[9].toString() : null);
			obj.setgSTNRefID(arr[10] != null ? arr[10].toString() : null);
			obj.setgSTNRefIDTime(arr[11] != null ? arr[11].toString() : null);
			obj.setgSTNErrorcode(arr[12] != null ? arr[12].toString() : null);
			obj.setgSTNErrorDescription(
					arr[13] != null ? arr[13].toString() : null);

			return obj;
		}

		private static String createNilRatedTotalSummQueryString(String buildQuery,String buildQueryNen, String multiSupplyTypeAns) {
			StringBuilder build = new StringBuilder();
							
				build.append("SELECT supplier_gstin,return_period,");
				build.append("SUPPLY_TYPE,");
				build.append("SUM(dg_nil_amt) as dg_nil_amt,");
				build.append("SUM(dg_expt_amt) as dg_expt_amt,");
				build.append("SUM(dg_gstn_nongst_sup_amt) as dg_gstn_nongst_sup_amt,");
				build.append("SUM(ui_nil_amt) as ui_nil_amt,");
				build.append("SUM(ui_expt_amt) as ui_expt_amt,");
				build.append("SUM(ui_nongst_sup_amt) as ui_nongst_sup_amt,");
				build.append("save_status,");
				build.append("gstin_ref_id,");
				build.append("gstin_ref_id_time,");
				build.append("gstin_error_code,");
				build.append("gstin_error_description_asp,DERIVED_RET_PERIOD "); 
				build.append("FROM ( SELECT supplier_gstin,return_period,DERIVED_RET_PERIOD,");
				build.append("UI_SUPPLY_TYPE AS SUPPLY_TYPE,");
				build.append("SUM(ui_nil_amt) ui_nil_amt,");
				build.append("SUM(ui_expt_amt) ui_expt_amt,");
				build.append("SUM(ui_nongst_sup_amt) ui_nongst_sup_amt,");
				build.append("0 dg_nil_amt,");
				build.append("0 dg_expt_amt,");
				build.append("0 dg_gstn_nongst_sup_amt,");
				build.append("save_status,");
				build.append("gstin_ref_id,");
				build.append("gstin_ref_id_time,");
				build.append("gstin_error_code,");
				build.append("gstin_error_description_asp ");
				build.append("FROM ( SELECT NEN.SUPPLIER_GSTIN ,NEN.RETURN_PERIOD,NEN.DERIVED_RET_PERIOD,");
				build.append("(CASE WHEN DESCRIPTION_KEY='8A' THEN 'INTER-B2B'  ");
				build.append("WHEN DESCRIPTION_KEY='8B' THEN 'INTRA-B2B' ");
				build.append("WHEN DESCRIPTION_KEY='8C' THEN 'INTER-B2C' ");
				build.append("WHEN DESCRIPTION_KEY='8D' THEN 'INTRA-B2C' ");
				build.append("END) AS UI_SUPPLY_TYPE, ");             
				build.append("IFNULL(SUM(NIL_RATED_SUPPLIES),0) UI_NIL_AMT, ");
				build.append("IFNULL(SUM(NON_GST_SUPPLIES),0) ui_nongst_sup_amt ,");
				build.append("IFNULL(SUM(EXMPTED_SUPPLIES),0) ui_expt_amt, ");
				build.append("(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND NEN.IS_DELETE = FALSE  ");
				build.append("THEN 'SAVED'  ELSE 'NOT_SAVED' END) SAVE_STATUS, ");
				build.append("(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND NEN.IS_DELETE = FALSE  ");
				build.append("THEN GSTNBATCH.GSTN_SAVE_REF_ID ELSE NULL  ");
				build.append("END) GSTIN_REF_ID, ");
				build.append("(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND NEN.IS_DELETE = FALSE  ");
				build.append("THEN GSTNBATCH.BATCH_DATE ELSE NULL  ");
				build.append("END) GSTIN_REF_ID_TIME, ");
				build.append("(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND NEN.IS_DELETE = FALSE  ");
				build.append("THEN IFNULL( GSTNBATCH.ERROR_CODE,'') ELSE NULL ");
				build.append("END) GSTIN_ERROR_CODE, ");
				build.append("(CASE WHEN IS_SAVED_TO_GSTN = TRUE AND NEN.IS_DELETE = FALSE ");
				build.append("THEN IFNULL( GSTNBATCH.ERROR_DESC,'') ");
				build.append("ELSE NULL END) GSTIN_ERROR_DESCRIPTION_ASP ");
				build.append("FROM GSTR1A_USERINPUT_NILEXTNON NEN ");
				build.append("LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH  ");
				build.append("ON GSTNBATCH.ID = NEN.BATCH_ID WHERE NEN.IS_DELETE=FALSE ");
				build.append("GROUP BY NEN.SUPPLIER_GSTIN,NEN.RETURN_PERIOD,NEN.DERIVED_RET_PERIOD,");
				build.append("NEN.DESCRIPTION_KEY, IS_SAVED_TO_GSTN,NEN.IS_DELETE ,");
				build.append("GSTNBATCH.GSTN_SAVE_REF_ID,BATCH_DATE,ERROR_CODE,ERROR_DESC ) ");
				build.append("GROUP BY supplier_gstin,return_period,DERIVED_RET_PERIOD,ui_supply_type,");
				build.append("save_status,gstin_ref_id,gstin_ref_id_time,");
				build.append("gstin_error_code,gstin_error_description_asp  ");
				build.append("UNION ALL ( SELECT supplier_gstin,return_period,DERIVED_RET_PERIOD,");
				build.append("dg_supply_type AS SUPPLY_TYPE,0    ui_nil_amt,");
				build.append("0    ui_expt_amt,0    ui_nongst_sup_amt,dg_nil_amt,");
				build.append("dg_expt_amt,dg_gstn_nongst_sup_amt,save_status,");
				build.append("gstin_ref_id,gstin_ref_id_time,gstin_error_code,");
				build.append("gstin_error_description_asp FROM   ( SELECT   supplier_gstin,");
				build.append("return_period,DERIVED_RET_PERIOD,supply_type  dg_supply_type,Sum(nil_amt) dg_nil_amt,");
				build.append("Sum(exmpted_amt)         dg_expt_amt ,");
				build.append("Sum(non_gst_supplies_amt)dg_gstn_nongst_sup_amt,");
				build.append("save_status,gstin_ref_id,gstin_ref_id_time,gstin_error_code,");
				build.append("gstin_error_description_asp FROM     ( ");
				build.append("SELECT  nen.supplier_gstin ,nen.return_period,nen.derived_ret_period, ( ");
				build.append("CASE WHEN TABLE_SECTION='8A' THEN 'INTER-B2B' ");
				build.append("WHEN TABLE_SECTION='8B' THEN 'INTRA-B2B' ");
				build.append("WHEN TABLE_SECTION='8C' THEN 'INTER-B2C' ");
				build.append("WHEN TABLE_SECTION='8D' THEN 'INTRA-B2C' ");
				build.append("END)          AS supply_type,");
				build.append("IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') ");
				build.append("AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) ");
				build.append("- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0)  AS nil_amt,");
				build.append("IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS')  ");
				build.append("AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) ");
				build.append("- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND SUPPLY_TYPE IN ('NON','SCH3') ");
				build.append("THEN TAXABLE_VALUE END),0)    AS non_gst_supplies_amt ,");
				build.append("IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='EXT' ");
				build.append("THEN TAXABLE_VALUE END),0)- IFNULL(SUM(CASE WHEN DOC_TYPE='CR' ");
				build.append("AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS exmpted_amt, (");
				build.append("CASE WHEN is_saved_to_gstn = true AND nen.is_delete = false THEN 'SAVED' ");
				build.append("ELSE 'NOT_SAVED' END) save_status, ( CASE ");
				build.append("WHEN is_saved_to_gstn = true ");
				build.append("AND nen.is_delete = false THEN gstnbatch.gstn_save_ref_id ");
				build.append("ELSE NULL END) gstin_ref_id, ( ");
				build.append("CASE WHEN is_saved_to_gstn = true AND nen.is_delete = false THEN gstnbatch.batch_date ");
				build.append("ELSE NULL END) gstin_ref_id_time, ( ");
				build.append("CASE WHEN is_saved_to_gstn = true ");
				build.append("AND nen.is_delete = false THEN Ifnull( gstnbatch.error_code,'') ");
				build.append("ELSE NULL END) gstin_error_code, ( CASE ");
				build.append("WHEN is_saved_to_gstn = true ");
				build.append("AND nen.is_delete = false THEN Ifnull( gstnbatch.error_desc,'') ");
				build.append("ELSE NULL END) gstin_error_description_asp ");
				build.append("FROM  ANX_OUTWARD_DOC_HEADER_1A NEN ");
				build.append("LEFT OUTER JOIN gstr1_gstn_save_batch GSTNBATCH ");
				build.append("ON  gstnbatch.id = nen.batch_id ");
				build.append("WHERE  NEN.TAX_DOC_TYPE IN ('NILEXTNON') ");
				build.append("AND NEN.ASP_INVOICE_STATUS = 2 AND NEN.COMPLIANCE_APPLICABLE=TRUE  ");
				build.append("AND NEN.IS_DELETE = FALSE AND NEN.SUPPLY_TYPE <> 'CAN'  ");
				build.append("AND NEN.RETURN_TYPE='GSTR1A'  AND ");
				build.append(buildQuery);
				build.append(" GROUP BY  nen.supplier_gstin,");
				build.append("nen.return_period,nen.derived_ret_period,nen.TABLE_SECTION,");
				build.append("is_saved_to_gstn,nen.is_delete ,GSTNBATCH.gstn_save_ref_id,batch_date,");
				build.append("GSTNBATCH.error_code,GSTNBATCH.error_desc ");
	      
				build.append("UNION ALL SELECT nen.supplier_gstin ,nen.return_period,");
				build.append("nen.derived_ret_period, ( CASE ");
				build.append("WHEN TABLE_SECTION='8A' THEN 'INTER-B2B' ");
				build.append("WHEN TABLE_SECTION='8B' THEN 'INTRA-B2B' ");
				build.append("WHEN TABLE_SECTION='8C' THEN 'INTER-B2C' ");
				build.append("WHEN TABLE_SECTION='8D' THEN 'INTRA-B2C' ");
				build.append("END) AS supply_type,");
				build.append("CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(SUM(TAXABLE_VALUE),0) END AS nil_amt,");
				build.append("CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(SUM(TAXABLE_VALUE),0) END AS NON_GST_SUPPLIES,");
				build.append("CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(SUM(TAXABLE_VALUE),0) END AS EXMPTED_SUPPLIES,");
				build.append("( CASE WHEN is_saved_to_gstn = true AND nen.is_delete = false THEN 'SAVED' ");
				build.append("ELSE 'NOT_SAVED' END) save_status, ( CASE ");
				build.append("WHEN is_saved_to_gstn = true ");
				build.append("AND nen.is_delete = false THEN gstnbatch.gstn_save_ref_id ");
				build.append("ELSE NULL END) gstin_ref_id, ( ");
				build.append("CASE WHEN is_saved_to_gstn = true ");
				build.append("AND nen.is_delete = false THEN gstnbatch.batch_date ");
				build.append("ELSE NULL END) gstin_ref_id_time, ( CASE ");
				build.append("WHEN is_saved_to_gstn = true ");
				build.append("AND nen.is_delete = false THEN Ifnull( gstnbatch.error_code,'') ");
				build.append("ELSE NULL END) gstin_error_code, ( ");
				build.append("CASE WHEN is_saved_to_gstn = true ");
				build.append("AND nen.is_delete = false THEN Ifnull( gstnbatch.error_desc,'') ");
				build.append("ELSE NULL END) gstin_error_description_asp ");
				build.append("FROM  GSTR1A_SUMMARY_NILEXTNON NEN ");
				build.append("LEFT OUTER JOIN gstr1_gstn_save_batch GSTNBATCH ");
				build.append("ON gstnbatch.id = nen.batch_id ");
				build.append("WHERE NEN.IS_DELETE = FALSE  GROUP BY nen.supplier_gstin,");
				build.append("nen.return_period,nen.derived_ret_period,");
				build.append("nen.TABLE_SECTION,is_saved_to_gstn,nen.is_delete ,");
				build.append("GSTNBATCH.gstn_save_ref_id,batch_date,GSTNBATCH.error_code,");
				build.append("GSTNBATCH.error_desc,NEN.SUPPLY_TYPE ) ");
				build.append("GROUP BY supplier_gstin,return_period,derived_ret_period,supply_type,");
				build.append("save_status,gstin_ref_id,gstin_ref_id_time,");
				build.append("gstin_error_code,gstin_error_description_asp ))) WHERE "); 
				build.append(buildQueryNen);
				build.append(" GROUP BY supplier_gstin,return_period,derived_ret_period,supply_type,");
				build.append("save_status,gstin_ref_id,gstin_ref_id_time,");
				build.append("gstin_error_code,gstin_error_description_asp ");
						
				return build.toString();			
			
		}
	}
