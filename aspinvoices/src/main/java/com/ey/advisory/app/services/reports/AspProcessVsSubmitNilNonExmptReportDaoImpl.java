/**
 * 
 */
package com.ey.advisory.app.services.reports;

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

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Slf4j
@Component("AspProcessVsSubmitNilNonExmptReportDaoImpl")
public class AspProcessVsSubmitNilNonExmptReportDaoImpl
		implements AspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;
	
	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	
	private static final String DOC_KEY_JOINER = "|";

	
	public static void main(String[] args) {
		
		
		
		StringBuilder buildHeader = new StringBuilder();
		StringBuilder buildHeaderNen = new StringBuilder();
		StringBuilder buildHeaderHdr = new StringBuilder();
		StringBuilder buildHeaderGstinRetPeriod = new StringBuilder();
		StringBuilder buildHeaderSuppGstin = new StringBuilder();
		
		
		
		if (true) {

			    
				buildHeader.append(" WHERE SUPPLIER_GSTIN IN :gstinList " );
				buildHeaderNen.append(" AND NEN.SUPPLIER_GSTIN IN :gstinList ");
				buildHeaderHdr.append(" AND HDR.GSTIN IN :gstinList ");
				buildHeaderGstinRetPeriod.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				buildHeaderSuppGstin.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				

			}
		
		
		if (true) {
			buildHeader.append(" AND A.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo");
			buildHeaderNen.append(" AND NEN.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			buildHeaderHdr.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			buildHeaderGstinRetPeriod.append(" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");

		}
		
		
		String queryStr = createApiProcessedQueryString(buildHeader.toString(),buildHeaderNen.toString(), 
				buildHeaderHdr.toString(),buildHeaderGstinRetPeriod.toString(),buildHeaderSuppGstin.toString(),"A");
		
		System.out.println(queryStr);

	}

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
		StringBuilder buildHeaderHdr = new StringBuilder();
		StringBuilder buildHeaderGstinRetPeriod = new StringBuilder();
		StringBuilder buildHeaderSuppGstin = new StringBuilder();
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" WHERE SUPPLIER_GSTIN IN :gstinList " );
				buildHeaderNen.append(" AND NEN.SUPPLIER_GSTIN IN :gstinList ");
				buildHeaderHdr.append(" AND HDR.GSTIN IN :gstinList ");
				buildHeaderGstinRetPeriod.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				buildHeaderSuppGstin.append(" AND SUPPLIER_GSTIN IN :gstinList ");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

			//	buildHeader.append(" AND PROFIT_CENTRE IN :pcList");
				buildHeaderNen.append(" AND NEN.PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

			//	buildHeader.append(" AND PLANT_CODE IN :plantList");
				buildHeaderNen.append(" AND NEN.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

			//	buildHeader.append(" AND SALES_ORGANIZATION IN :salesList");
				buildHeaderNen.append(" AND NEN.SALES_ORGANIZATION IN :salesList");


			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

			//	buildHeader.append(" AND DISTRIBUTION_CHANNEL IN :distList");
				buildHeaderNen.append(" AND NEN.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

			//	buildHeader.append(" AND DIVISION IN :divisionList");
				buildHeaderNen.append(" AND NEN.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

			//	buildHeader.append(" AND LOCATION IN :locationList");
				buildHeaderNen.append(" AND NEN.LOCATION IN :locationList");


			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

			//	buildHeader.append(" AND USERACCESS1 IN :ud1List");
				buildHeaderNen.append(" AND NEN.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

			//	buildHeader.append(" AND USERACCESS2 IN :ud2List");
				buildHeaderNen.append(" AND NEN.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

			//	buildHeader.append(" AND USERACCESS3 IN :ud3List");
				buildHeaderNen.append(" AND NEN.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

			//	buildHeader.append(" AND USERACCESS4 IN :ud4List");
				buildHeaderNen.append(" AND NEN.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

			//	buildHeader.append(" AND USERACCESS5 IN :ud5List");
				buildHeaderNen.append(" AND NEN.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

			//	buildHeader.append(" AND USERACCESS6 IN :ud6List");
				buildHeaderNen.append(" AND NEN.USERACCESS6 IN :ud6List");
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND A.DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");
			buildHeaderNen.append(" AND NEN.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
			buildHeaderHdr.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			buildHeaderGstinRetPeriod.append(" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");

		}
		
	/*	String countQuery = "select count(1) cnt FROM GSTR1_USERINPUT_NILEXTNON WHERE "
				+ "IS_DELETE=FALSE AND SUPPLIER_GSTIN = :gstinList "
				+ "AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ";
		
		Query countQ = entityManager.createNativeQuery(countQuery);
		
		
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				countQ.setParameter("gstinList", gstinList);
			}
		}
		
		if (taxPeriodFrom != null && taxPeriodTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriodTo());
			countQ.setParameter("taxPeriodFrom", derivedRetPeriodFrom);
			countQ.setParameter("taxPeriodTo", derivedRetPeriodTo);
		}
		
	//	int count = countQ.getFirstResult();
		
		Object maxCount = countQ.getSingleResult();
		int count = ((BigInteger) maxCount).intValue();*/
		String multiSupplyTypeAns = groupConfigPrmtRepository
				.findAnswerForMultiSupplyType(); 

		String queryStr = createApiProcessedQueryString(buildHeader.toString(),buildHeaderNen.toString(), 
				"","","",multiSupplyTypeAns);
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
		//obj.setIsFilled(arr[7] != null ? arr[7].toString() : null);
		obj.setIsFilled(isGstinTaxperiodFiled(context,arr[1].toString(), arr[2].toString()));
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
	

	private static String createApiProcessedQueryString(String buildHeader,String buildHeaderNen,String buildHeaderHdr,
			String buildHeaderGstinRetPeriod, String buildHeaderSuppGstin, String multiSupplyTypeAns) {

		StringBuilder build = new StringBuilder();

			build.append("SELECT Row_number () ");
			build.append("         over ( ");
			build.append("           ORDER BY A.supplier_gstin) ");
			build.append("       SNO, ");
			build.append("       A.supplier_gstin, ");
			build.append("       A.return_period, ");
			build.append("       A.gstn_supply_type, ");
			build.append("       A.gstn_nil_amt, ");
			build.append("       A.gstn_expt_amt, ");
			build.append("       A.gstn_nongst_sup_amt, ");
			build.append("       A.is_filed, ");
			build.append("       A.dg_supply_type, ");
			build.append("       A.dg_nil_amt, ");
			build.append("       A.dg_expt_amt, ");
			build.append("       A.dg_nongst_sup_amt, ");
			build.append("       A.ui_supply_type, ");
			build.append("       A.ui_nil_amt, ");
			build.append("       A.ui_expt_amt, ");
			build.append("       A.ui_nongst_sup_amt, ");
			build.append("       A.diff_nil_amt, ");
			build.append("       A.diff_expt_amt, ");
			build.append("       A.diff_nongst_sup_amt, ");
			build.append("       Map(batch.gstn_save_status, 'P', 'SAVED', 'NOT SAVED')                 AS ");
			build.append("       SAVE_STATUS, ");
			build.append("       batch.gstn_save_ref_id, ");
			build.append("       To_char(Add_seconds(batch.batch_date, 19800), 'DD-MM-YYYY HH24:MI:SS') AS ");
			build.append("       BATCH_DATE, ");
			build.append("       batch.error_code, ");
			build.append("       batch.error_desc, ");
			build.append("       A.derived_ret_period ");
			build.append("FROM   (WITH gstn ");
			build.append("             AS (SELECT hdr.gstin                             supplier_gstin, ");
			build.append("                        hdr.return_period                     return_period, ");
			build.append("                        hdr.derived_ret_period                derived_ret_period ");
			build.append("                        , ");
			build.append("                        Map(supply_type, 'INTRB2B', ");
			build.append("                        'INTER-B2B', 'INTRAB2B', 'INTRA-B2B' ");
			build.append("                        , ");
			build.append("                        'INTRB2C', ");
			build.append("                        'INTER-B2C', 'INTRAB2C', 'INTRA-B2C') AS SUPPLY_TYPE, ");
			build.append("                        SUM(nil_amt)                          gstn_nil_amt, ");
			build.append("                        SUM(expt_amt)                         gstn_expt_amt, ");
			build.append("                        SUM(nongst_sup_amt) ");
			build.append("                        gstn_nongst_sup_amt, ");
			build.append("                        bt.is_filed, ");
			build.append("                        'NILEXTNON'                           AS table_type ");
			build.append("                 FROM   getgstr1_nilextnon HDR ");
			build.append("                        left join getanx1_batch_table BT ");
			build.append("                               ON hdr.batch_id = bt.id ");
			build.append("                 WHERE  hdr.is_delete = FALSE ");
			build.append("                        AND bt.is_delete = FALSE ");
			build.append("                 GROUP  BY hdr.id, ");
			build.append("                           hdr.gstin, ");
			build.append("                           hdr.return_period, ");
			build.append("                           supply_type, ");
			build.append("                           bt.is_filed, ");
			build.append("                           hdr.derived_ret_period), ");
			build.append("             ui ");
			build.append("             AS (SELECT NEN.supplier_gstin, ");
			build.append("                        NEN.return_period, ");
			build.append("                        NEN.derived_ret_period, ");
			build.append("                        ( CASE ");
			build.append("                            WHEN description_key = '8A' THEN 'INTER-B2B' ");
			build.append("                            WHEN description_key = '8B' THEN 'INTRA-B2B' ");
			build.append("                            WHEN description_key = '8C' THEN 'INTER-B2C' ");
			build.append("                            WHEN description_key = '8D' THEN 'INTRA-B2C' ");
			build.append("                          END )                            AS SUPPLY_TYPE, ");
			build.append("                        IFNULL(SUM(nil_rated_supplies), 0) UI_NIL_AMT, ");
			build.append("                        IFNULL(SUM(non_gst_supplies), 0)   ui_nongst_sup_amt, ");
			build.append("                        IFNULL(SUM(exmpted_supplies), 0)   ui_expt_amt, ");
			build.append("                        Max(batch_id)                      ui_batch_id ");
			build.append("                 FROM   gstr1_userinput_nilextnon NEN ");
			build.append("                 WHERE  NEN.is_delete = FALSE ");
			build.append("                 GROUP  BY NEN.supplier_gstin, ");
			build.append("                           NEN.return_period, ");
			build.append("                           NEN.derived_ret_period, ");
			build.append("                           NEN.description_key), ");
			build.append("             digi ");
			build.append("             AS (SELECT supplier_gstin, ");
			build.append("                        return_period, ");
			build.append("                        derived_ret_period, ");
			build.append("                        supply_type              supply_type, ");
			build.append("                        SUM(nil_amt)             dg_nil_amt, ");
			build.append("                        SUM(exmpted_amt)         dg_expt_amt, ");
			build.append("                        SUM(non_gst_supplies_amt)dg_nongst_sup_amt, ");
			build.append("                        Max(batch_id)            dg_batch_id ");
			build.append("                 FROM   (SELECT nen.supplier_gstin, ");
			build.append("                                nen.return_period, ");
			build.append("                                nen.derived_ret_period, ");
			build.append("                                ( CASE ");
			build.append("                                    WHEN  ITM.itm_table_section = '8A' THEN 'INTER-B2B' ");
			build.append("                                    WHEN  ITM.itm_table_section = '8B' THEN 'INTRA-B2B' ");
			build.append("                                    WHEN  ITM.itm_table_section = '8C' THEN 'INTER-B2C' ");
			build.append("                                    WHEN  ITM.itm_table_section = '8D' THEN 'INTRA-B2C' ");
			build.append("                                  END )AS supply_type, ");
			build.append("                                IFNULL(SUM(CASE WHEN doc_type IN ('INV', 'DR', 'BOS','RNV','RDR' )AND  ITM.supply_type = 'NIL' THEN  ITM.taxable_value ");
			build.append("								                WHEN doc_type IN ('CR','RCR') AND  ITM.supply_type = 'NIL' THEN - ITM.taxable_value ");
			build.append("								           END), 0) AS nil_amt, ");
			build.append("								IFNULL(SUM(CASE WHEN doc_type IN ( 'INV', 'DR', 'BOS','RNV','RDR'  ) AND ITM.supply_type IN ( 'NON', 'SCH3' ) THEN ITM.taxable_value ");
			build.append("								                WHEN doc_type IN ('CR','RCR') AND ITM.supply_type IN ( 'NON', 'SCH3' ) THEN - ITM.taxable_value ");
			build.append("                                           END), 0)  AS non_gst_supplies_amt, ");
			build.append("							    IFNULL(SUM(CASE WHEN doc_type IN ( 'INV', 'DR', 'BOS','RNV','RDR'  ) AND ITM.supply_type = 'EXT' THEN ITM.taxable_value ");
			build.append("								                WHEN doc_type IN ('CR','RCR') AND ITM.supply_type = 'EXT' THEN - ITM.taxable_value ");
			build.append("                                           END), 0) AS exmpted_amt, ");
			build.append("                                Max(batch_id)  batch_id ");
			build.append("             FROM   anx_outward_doc_header NEN ");
			build.append("             inner join anx_outward_doc_item ITM ");
			build.append("             ON NEN.id = ITM.doc_header_id ");
			build.append("             AND NEN.derived_ret_period = ITM.derived_ret_period ");
			build.append("left outer join gstr1_gstn_save_batch GSTNBATCH ");
			build.append("ON gstnbatch.id = nen.batch_id ");
			build.append("WHERE ITM.itm_tax_doc_type IN ( 'NILEXTNON' ) ");
			build.append("AND NEN.asp_invoice_status = 2 ");
			build.append("AND NEN.compliance_applicable = TRUE ");
			build.append("AND NEN.is_delete = FALSE ");
			build.append("AND NEN.supply_type <> 'CAN' ");
			build.append("AND NEN.return_type = 'GSTR1' ");
			//build.append("AND NEN.supplier_gstin IN :gstinList ");
			//build.append("AND NEN.derived_ret_period = :taxPeriod ");
			build.append(buildHeaderNen);
			build.append("GROUP  BY nen.supplier_gstin, ");
			build.append("nen.return_period, ");
			build.append("nen.derived_ret_period, ");
			build.append(" ITM.itm_table_section ");
			build.append(" ");
			build.append("UNION ALL ");
			build.append("SELECT nen.supplier_gstin, ");
			build.append("nen.return_period, ");
			build.append("nen.derived_ret_period, ");
			build.append("( CASE ");
			build.append("WHEN table_section = '8A' THEN 'INTER-B2B' ");
			build.append("WHEN table_section = '8B' THEN 'INTRA-B2B' ");
			build.append("WHEN table_section = '8C' THEN 'INTER-B2C' ");
			build.append("WHEN table_section = '8D' THEN 'INTRA-B2C' ");
			build.append("END ) AS supply_type, ");
			build.append("CASE ");
			build.append("WHEN supply_type = 'NIL' THEN IFNULL(SUM(taxable_value), 0) ");
			build.append("END     AS nil_amt, ");
			build.append("CASE ");
			build.append("WHEN supply_type IN ( 'NON' ) THEN IFNULL(SUM(taxable_value), 0) ");
			build.append("END     AS NON_GST_SUPPLIES, ");
			build.append("CASE ");
			build.append("WHEN supply_type = 'EXT' THEN IFNULL(SUM(taxable_value), 0) ");
			build.append("END     AS EXMPTED_SUPPLIES, ");
			build.append("Max(batch_id) ");
			build.append("FROM   gstr1_summary_nilextnon NEN ");
			build.append("WHERE  NEN.is_delete = FALSE ");
			build.append("GROUP  BY nen.supplier_gstin, ");
			build.append("nen.return_period, ");
			build.append("nen.derived_ret_period, ");
			build.append("nen.table_section, ");
			build.append("NEN.supply_type) ");
			build.append("GROUP  BY supplier_gstin, ");
			build.append("return_period, ");
			build.append("derived_ret_period, ");
			build.append("supply_type) ");
			build.append("SELECT ");
			build.append("IFNULL(IFNULL(gstn.supplier_gstin, ui.supplier_gstin), digi.supplier_gstin) ");
			build.append("AS ");
			build.append("supplier_gstin, ");
			build.append("IFNULL(IFNULL(gstn.return_period, ui.return_period), digi.return_period) ");
			build.append("AS ");
			build.append("return_period, ");
			build.append("gstn.supply_type ");
			build.append("AS gstn_SUPPLY_TYPE, ");
			build.append("gstn_nil_amt ");
			build.append("AS gstn_nil_amt, ");
			build.append("gstn_expt_amt ");
			build.append("AS gstn_expt_amt, ");
			build.append("gstn_nongst_sup_amt ");
			build.append("AS gstn_nongst_sup_amt, ");
			build.append("is_filed ");
			build.append("is_filed, ");
			build.append("digi.supply_type ");
			build.append("AS dg_supply_type, ");
			build.append("dg_nil_amt ");
			build.append("AS dg_nil_amt, ");
			build.append("dg_expt_amt ");
			build.append("AS dg_expt_amt, ");
			build.append("dg_nongst_sup_amt ");
			build.append("AS dg_nongst_sup_amt, ");
			build.append("ui.supply_type ");
			build.append("AS ui_supply_type, ");
			build.append("ui_nil_amt ");
			build.append("AS ui_nil_amt, ");
			build.append("ui_expt_amt ");
			build.append("AS ui_expt_amt, ");
			build.append("ui_nongst_sup_amt ");
			build.append("AS ui_nongst_sup_amt, ");
			build.append("( IFNULL(dg_nil_amt, 0) - IFNULL(gstn_nil_amt, 0) ) ");
			build.append("AS diff_nil_amt, ");
			build.append("( IFNULL(dg_expt_amt, 0) - IFNULL(gstn_expt_amt, 0) ) ");
			build.append("AS diff_expt_amt, ");
			build.append("( IFNULL(dg_nongst_sup_amt, 0) - IFNULL(gstn_nongst_sup_amt, 0) ) ");
			build.append("AS ");
			build.append("diff_nongst_sup_amt, ");
			build.append("IFNULL(IFNULL(gstn.derived_ret_period, ui.derived_ret_period), digi.derived_ret_period) AS DERIVED_RET_PERIOD, ");
			build.append("( CASE ");
			build.append("    WHEN ui_batch_id > dg_batch_id THEN ui_batch_id ");
			build.append("    ELSE dg_batch_id ");
			build.append("  END ) ");
			build.append("       AS batch_id ");
			build.append(" FROM   gstn ");
			build.append("        full outer join ui ");
			build.append("                     ON gstn.supplier_gstin = ui.supplier_gstin ");
			build.append("                        AND gstn.derived_ret_period = ui.derived_ret_period ");
			build.append("                        AND gstn.supply_type = ui.supply_type ");
			build.append("        full outer join digi ");
			build.append("                     ON IFNULL(ui.supplier_gstin, gstn.supplier_gstin) = ");
			build.append("                        digi.supplier_gstin ");
			build.append("                        AND ");
			build.append("        IFNULL(ui.derived_ret_period, gstn.derived_ret_period) = ");
			build.append("        digi.derived_ret_period ");
			build.append("                        AND IFNULL(ui.supply_type, gstn.supply_type) = ");
			build.append("                            digi.supply_type) A ");
			build.append("left join (SELECT id, ");
			build.append("                  gstn_save_status, ");
			build.append("                  gstn_save_ref_id, ");
			build.append("                  batch_date, ");
			build.append("                  error_code, ");
			build.append("                  error_desc ");
			build.append("           FROM   gstr1_gstn_save_batch ");
			build.append("           WHERE  is_delete = FALSE ");
			build.append("                  AND SECTION = 'NIL') batch ");
			build.append("       ON A.batch_id = batch.id ");
			//build.append("WHERE  supplier_gstin IN :gstinList ");
			//build.append("       AND A.derived_ret_period = :taxPeriod");
			build.append(buildHeader);

			
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("createApiProcessedQueryString query " + build.toString());
			}
			
			
		
		
		
				
		return build.toString();
		
	}
	
	private void settingFiledGstins(ProcessingContext context) {

		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR1", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {
			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}
	
	
	private String isGstinTaxperiodFiled(ProcessingContext context, String gstin, String taxPeriod) {
		String filingStatus = "False";
		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + DOC_KEY_JOINER + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			filingStatus = "True";
		}
		return filingStatus;

	}

}
