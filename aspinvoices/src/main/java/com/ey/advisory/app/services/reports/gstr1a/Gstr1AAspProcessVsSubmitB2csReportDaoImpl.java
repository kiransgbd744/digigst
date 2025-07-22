/**
 * 
 */
package com.ey.advisory.app.services.reports.gstr1a;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.views.client.AspProcessVsSubmitB2cAndB2csaDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import com.google.common.base.Strings;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
@Component("Gstr1AAspProcessVsSubmitB2csReportDaoImpl")
public class Gstr1AAspProcessVsSubmitB2csReportDaoImpl
		implements Gstr1AAspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;
	
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
		StringBuilder buildHeaderForGet = new StringBuilder();
		StringBuilder buildHeaderB2cs = new StringBuilder();

//		String multiSupplyTypeAns = groupConfigPrmtRepository
//				.findAnswerForMultiSupplyType();
		
		String multiSupplyTypeAns = "";
		
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append("  SUPPLIER_GSTIN IN :gstinList");
				buildHeaderB2cs.append(" AND B2CS.SUPPLIER_GSTIN IN :gstinList");
				buildHeaderForGet.append("  HDR.GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
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
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo ");
			buildHeaderB2cs.append(" AND B2CS.DERIVED_RET_PERIOD BETWEEN");
			buildHeaderB2cs.append(" :taxPeriodFrom AND :taxPeriodTo ");
			buildHeaderForGet.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
			

		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString(),buildHeaderForGet.toString(), buildHeaderB2cs.toString(), multiSupplyTypeAns);
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
		List<Object> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<StateCodeInfoEntity> MasterEntities = statecodeRepository
					.findAll();
			Map<String, String> hsnMap = new HashMap<String, String>();
			MasterEntities.forEach(entity -> {
				hsnMap.put(entity.getStateCode(), entity.getStateName());
			});
			ProcessingContext context = new ProcessingContext();
			settingFiledGstins(context);
			for (Object arr[] : list) {
				verticalHsnList.add(createApiProcessedConvertions(arr, hsnMap, context));
			}
		}
		return verticalHsnList;
	}

	private AspProcessVsSubmitB2cAndB2csaDto createApiProcessedConvertions(
			Object[] arr, Map<String, String> hsnMap, ProcessingContext context) {
		AspProcessVsSubmitB2cAndB2csaDto obj = new AspProcessVsSubmitB2cAndB2csaDto();

		obj.setGstnSgstin(arr[0] != null ? arr[0].toString() : null);
		obj.setGstnRetPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setGstnDerRetPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setGstnSuppType(arr[3] != null ? arr[3].toString() : null);
		// obj.setGstnStateName(arr[4] != null ? arr[4].toString() : null);
		obj.setGstnPos(arr[5] != null ? arr[5].toString() : null);
		obj.setGstnRateTax(arr[6] != null ? arr[6].toString() : null);
		obj.setGstnTaxvalue(arr[7] != null ? arr[7].toString() : null);
		obj.setGstnIgst(arr[8] != null ? arr[8].toString() : null);
		obj.setGstnCgst(arr[9] != null ? arr[9].toString() : null);
		obj.setGstnSgst(arr[10] != null ? arr[10].toString() : null);
		obj.setGstnCess(arr[11] != null ? arr[11].toString() : null);
		obj.setGstnDiffPerRate(arr[12] != null ? arr[12].toString() : null);
		obj.setGstnType(arr[13] != null ? arr[13].toString() : null);
		obj.setGstnEcomGstin(arr[14] != null ? arr[14].toString() : null);

		//obj.setGstnIsFiled(arr[15] != null ? arr[15].toString() : null);
		obj.setGstnIsFiled(isGstinTaxperiodFiled(context,arr[0].toString(), arr[1].toString()));
		obj.setDigiGstTransType(arr[16] != null ? arr[16].toString() : null);
		obj.setDigiGstMonth(arr[17] != null ? arr[17].toString() : null);

		obj.setDigiGstOrgPos(arr[18] != null ? arr[18].toString() : null);
		obj.setDigiGstOrgHsn(arr[19] != null ? arr[19].toString() : null);
		obj.setDigiGstOrgUom(arr[20] != null ? arr[20].toString() : null);
		obj.setDigiGstOrgQnt(arr[21] != null ? arr[21].toString() : null);
		obj.setDigiGstOrgRate(arr[22] != null ? arr[22].toString() : null);
		obj.setDigiGstOrgTaxbleValue(
				arr[23] != null ? arr[23].toString() : null);
		obj.setDigiGstOrgEcomGstin(arr[24] != null ? arr[24].toString() : null);
		obj.setDigiGstOrgEcomSupValue(
				arr[25] != null ? arr[25].toString() : null);
		obj.setDigiGstNewPos(arr[26] != null ? arr[26].toString() : null);
		obj.setDigiGstOrgNewHsn(arr[27] != null ? arr[27].toString() : null);
		obj.setDigiGstOrgNewUom(arr[28] != null ? arr[28].toString() : null);

		obj.setDigiGstOrgNewQnt(arr[29] != null ? arr[29].toString() : null);
		obj.setDigiGstOrgNewRate(arr[30] != null ? arr[30].toString() : null);
		obj.setDigiGstOrgNewTaxbleValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setDigiGstOrgNewEcomGstin(
				arr[32] != null ? arr[32].toString() : null);
		obj.setDigiGstOrgNewEcomSupValue(
				arr[33] != null ? arr[33].toString() : null);
		obj.setDigiGstIgst(arr[34] != null ? arr[34].toString() : null);
		obj.setDigiGstCgst(arr[35] != null ? arr[35].toString() : null);
		obj.setDigiGstSgst(arr[36] != null ? arr[36].toString() : null);
		obj.setDigiGstCess(arr[37] != null ? arr[37].toString() : null);
		obj.setDigiGstTotValue(arr[38] != null ? arr[38].toString() : null);
		obj.setDigiGstProfitCenter(arr[39] != null ? arr[39].toString() : null);
		obj.setDigiGstPlant(arr[40] != null ? arr[40].toString() : null);
		obj.setDigiGstDivision(arr[41] != null ? arr[41].toString() : null);
		obj.setDigiGstLocation(arr[42] != null ? arr[42].toString() : null);
		obj.setDigiGstSalesOrg(arr[43] != null ? arr[43].toString() : null);

		obj.setDigiGstDistChannel(arr[44] != null ? arr[44].toString() : null);
		obj.setDigiGstUserAcc1(arr[45] != null ? arr[45].toString() : null);
		obj.setDigiGstUserAcc2(arr[46] != null ? arr[46].toString() : null);
		obj.setDigiGstUserAcc3(arr[47] != null ? arr[47].toString() : null);
		obj.setDigiGstUserAcc4(arr[48] != null ? arr[48].toString() : null);
		obj.setDigiGstUserAcc5(arr[49] != null ? arr[49].toString() : null);
		obj.setDigiGstUserAcc6(arr[50] != null ? arr[50].toString() : null);
		obj.setDigiGstUserDef1(arr[51] != null ? arr[51].toString() : null);
		obj.setDigiGstUserDef2(arr[52] != null ? arr[52].toString() : null);
		obj.setDigiGstUserDef3(arr[53] != null ? arr[53].toString() : null);
		obj.setDigiGstAspInfo(arr[54] != null ? arr[54].toString() : null);
		obj.setDigiGstAspInfoDescription(
				arr[55] != null ? arr[55].toString() : null);
		obj.setDigiGstSaveStatus(arr[56] != null ? arr[56].toString() : null);
		obj.setDigiGstGstnRefId(arr[57] != null ? arr[57].toString() : null);

		obj.setDigiGstGstnRefTime(arr[58] != null ? arr[58].toString() : null);
		obj.setDigiGstGstnErrorCode(
				arr[59] != null ? arr[59].toString() : null);
		obj.setDigiGstGstnErrorDes(arr[60] != null ? arr[60].toString() : null);
		obj.setDigiGstSourceId(arr[61] != null ? arr[61].toString() : null);
		obj.setDigiGstFileName(arr[62] != null ? arr[62].toString() : null);
		obj.setDigiGstAspDate(arr[63] != null ? arr[63].toString() : null);
		// obj.setGstnStateCode(arr[64] != null ? arr[64].toString() : null);

		String stateName = null;
		String stateCode = (arr[64] != null ? arr[64].toString() : null);
		obj.setGstnStateCode(stateCode);

		if (!Strings.isNullOrEmpty(stateCode)) {
			stateName = stateCode + "-" + hsnMap.get(stateCode);
		}
		obj.setGstnStateName(stateName);
		obj.setGstnSno(arr[65] != null ? arr[65].toString() : null);
		obj.setDiffTaxableValue(arr[66] != null ? addSingleQuote(arr[66].toString()) : null);
		obj.setDiffIgst(arr[67] != null ? addSingleQuote(arr[67].toString()) : null);
		obj.setDiffCgst(arr[68] != null ? addSingleQuote(arr[68].toString()) : null);
		obj.setDiffSgst(arr[69] != null ? addSingleQuote(arr[69].toString()) : null);
		obj.setDiffCess(arr[70] != null ? addSingleQuote(arr[70].toString()) : null);
		return obj;
	}
	public static String addSingleQuote(String input) {
	    return "'" + input;
	}
	
	private static String createApiProcessedQueryString(String buildQueryb2cs,String buildQuery, String buildHeaderB2cs, String multiSupplyTypeAns) {

		StringBuilder build = new StringBuilder();

			build.append("with GSTN as ( SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD RETURN_PERIOD,");
			build.append("HDR.DERIVED_RET_PERIOD DERIVED_RET_PERIOD, (IFNULL(HDR.GSTIN,");
			build.append("'') ||'|'|| IFNULL(HDR.RETURN_PERIOD,	'') ||'|'||IFNULL(MAP(HDR.DIFF_PERCENT,0.65,");
			build.append("'L65','N'),'') ||'|'||IFNULL(TO_NUMBER(POS),");
			build.append("9999) ||'|'||IFNULL(HDR.ETIN,	'') ||'|'||IFNULL(cast(ITM.TAX_RATE as DECIMAL(15,");
			build.append("2)), 9999) ) DOC_KEY, SUPPLY_TYPE,SUBSTR(HDR.GSTIN,1,	2) AS STATE_CODE,");
			build.append("'' STATE_NAME, POS,ITM.TAX_RATE,SUM(ITM.TAXABLE_VALUE) TAXABLE_VALUE,");
			build.append("SUM(ITM.IGST_AMT) GSTN_IGST_AMT, SUM(ITM.CGST_AMT) GSTN_CGST_AMT,");
			build.append("SUM(ITM.SGST_AMT) GSTN_SGST_AMT, SUM(ITM.CESS_AMT) GSTN_CESS_AMT,");
			build.append("HDR.DIFF_PERCENT,HDR.ECOM_TYPE,HDR.ETIN ECOM_GSTIN, BT.IS_FILED,");
			build.append("'B2CS' TABLE_TYPE FROM GETGSTR1A_B2CS_HEADER HDR ");
			build.append("INNER JOIN GETGSTR1A_B2CS_ITEM ITM ON HDR.ID = ITM.HEADER_ID  ");
			build.append("AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
			build.append("LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID  ");
			build.append("WHERE HDR.IS_DELETE=FALSE AND BT.IS_DELETE=FALSE  AND ");
			build.append(buildQuery);
			//	    and HDR.GSTIN='27GSPMH0482G1ZM'
			//		and HDR.return_period ='072022' 
			build.append("GROUP BY HDR.GSTIN, HDR.RETURN_PERIOD, SUPPLY_TYPE, POS,");
			build.append("ITM.TAX_RATE, HDR.DIFF_PERCENT,HDR.ECOM_TYPE, HDR.ETIN,BT.IS_FILED,");
			build.append("HDR.DERIVED_RET_PERIOD ) ,DIGI AS ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,");
			build.append("DERIVED_RET_PERIOD, DOC_KEY,TRAN_TYPE, '' MONTH, NULL ORG_POS, 0 ORG_HSNORSAC,");
			build.append("'' ORG_UOM, 0 ORG_QNT,NULL ORG_RATE,SUM(ORG_TAXABLE_VALUE)ORG_TAXABLE_VALUE,");
			build.append("NULL ORG_ECOM_GSTIN, SUM(ORG_ECOM_SUP_VAL)ORG_ECOM_SUP_VAL, NEW_POS,");
			build.append("null as NEW_HSNORSAC, '' NEW_UOM, '' NEW_QNT,NEW_RATE,");
			build.append("IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN NEW_TAXABLE_VALUE END),");
			build.append("0) -IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR')  THEN NEW_TAXABLE_VALUE END),");
			build.append("0) AS NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN,SUM(NEW_ECOM_SUP_VAL)NEW_ECOM_SUP_VAL ,");
			build.append("IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN IGST_AMT END),");
			build.append("0) -IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR')  THEN IGST_AMT END),0) AS IGST_AMT,");
			build.append("IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN CGST_AMT END),");
			build.append("0) -IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR')  THEN CGST_AMT END),0) AS CGST_AMT,");
			build.append("IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN SGST_AMT END),");
			build.append("0) -IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN SGST_AMT END),");
			build.append("0) AS SGST_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR')  ");
			build.append("THEN CESS_AMT END), 0) -IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR')  ");
			build.append("THEN CESS_AMT END),0) AS CESS_AMT,IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR')  ");
			build.append("THEN TOT_VAL  END), 0) -IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR')  ");
			build.append("THEN TOT_VAL  END),0) AS TOT_VAL,");
			build.append("null as  PROFIT_CENTRE,null as PLANT_CODE,null as  DIVISION,");
			build.append("null as  LOCATION,null as SALES_ORGANIZATION,");
			build.append("null as  DISTRIBUTION_CHANNEL,null as  USERACCESS1,null as  USERACCESS2,");
			build.append("null as USERACCESS3,null as USERACCESS4,null as  USERACCESS5,");
			build.append("null as  USERACCESS6, '' USERDEFINED_FIELD1, '' USERDEFINED_FIELD2,");
			build.append("'' USERDEFINED_FIELD3, '' INFO_ERROR_CODE_ASP,'' INFO_ERROR_DESCRIPTION_ASP,");
			build.append("MAX(SAVE_STATUS)SAVE_STATUS, MAX(GSTIN_REF_ID)GSTIN_REF_ID ,");
			build.append("MAX(GSTIN_REF_ID_TIME )GSTIN_REF_ID_TIME,");
			build.append("'' GSTN_ERROR_CODE , '' GSTN_ERROR_DESCRIPTION,'' SOURCE_ID,'' FILE_NAME,");
			build.append("'' ASP_DATE_TIME FROM ( SELECT 'INV' AS DOC_TYPE,(IFNULL(B2CS.SUPPLIER_GSTIN,");
			build.append("'') ||'|'|| IFNULL(B2CS.RETURN_PERIOD,'') ||'|'||IFNULL(TRAN_TYPE,");
			build.append("'') ||'|'||IFNULL(NEW_POS,9999)||'|'||IFNULL(NEW_ECOM_GSTIN,");
			build.append("'')||'|'||IFNULL(NEW_RATE,9999) ) DOC_KEY,");
			build.append("B2CS.SUPPLIER_GSTIN, B2CS.RETURN_PERIOD, B2CS.DERIVED_RET_PERIOD,");
			build.append("TRAN_TYPE, '' MONTH,NULL AS ORG_POS, 0 ORG_HSNORSAC, ''ORG_UOM, 0 ORG_QNT,");
			build.append("NULL AS ORG_RATE, ORG_TAXABLE_VALUE, NULL AS ORG_ECOM_GSTIN,");
			build.append("ORG_ECOM_SUP_VAL, NEW_POS, NEW_HSNORSAC, ''NEW_UOM, '' AS NEW_QNT,");
			build.append("NEW_RATE, NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN,");
			build.append("NEW_ECOM_SUP_VAL, IGST_AMT,CGST_AMT,");
			build.append("SGST_AMT, CESS_AMT,IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,");
			build.append("0) +IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,");
			build.append("0) AS TOT_VAL, PROFIT_CENTRE,PLANT_CODE,DIVISION,");
			build.append("LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,");
			build.append("USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,");
			build.append("USERACCESS5,USERACCESS6,''USERDEFINED_FIELD1,");
			build.append("''USERDEFINED_FIELD2,''USERDEFINED_FIELD3,''INFO_ERROR_CODE_ASP,");
			build.append("''INFO_ERROR_DESCRIPTION_ASP, (CASE WHEN B2CS.IS_SAVED_TO_GSTN = TRUE  ");
			build.append("AND B2CS.IS_DELETE = FALSE  THEN 'IS_SAVED' ELSE 'NOT_SAVED'  ");
			build.append("END) AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, ");
			build.append("GSTNBATCH.GSTN_RESP_DATE AS GSTIN_REF_ID_TIME,");
			build.append("'' AS GSTN_ERROR_CODE, '' AS GSTN_ERROR_DESCRIPTION,'' SOURCE_ID,");
			build.append("'' FILE_NAME, '' ASP_DATE_TIME  FROM GSTR1A_PROCESSED_B2CS B2CS  ");
			build.append("LEFT OUTER JOIN (SELECT COMMON_ID,VAL_TYPE,FILE_ID,");
			build.append("INV_KEY,TABLE_TYPE,STRING_AGG(INFO_ERROR_CODE_ASP,");
			build.append("',') INFO_ERROR_CODE_ASP, STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,");
			build.append("',') INFO_ERROR_DESCRIPTION_ASP FROM (SELECT COMMON_ID,");
			build.append("VAL_TYPE, FILE_ID,INV_KEY,TABLE_TYPE,CASE WHEN ERROR_TYPE='INFO'  ");
			build.append("THEN ERROR_CODE END AS INFO_ERROR_CODE_ASP,CASE WHEN ERROR_TYPE='INFO'  ");
			build.append("THEN ERROR_DESCRIPTION  END AS INFO_ERROR_DESCRIPTION_ASP  ");
			build.append("FROM ANX_VERTICAL_ERROR_1A) GROUP BY COMMON_ID, VAL_TYPE,");
			build.append("FILE_ID,INV_KEY,");
			build.append("TABLE_TYPE) GSTRERR ON B2CS.AS_ENTERED_ID= GSTRERR.COMMON_ID  ");
			build.append("AND B2CS.FILE_ID=GSTRERR.FILE_ID  AND GSTRERR.TABLE_TYPE='B2CS'  ");
			build.append("AND RETURN_TYPE='GSTR1A' LEFT OUTER JOIN FILE_STATUS FIL ON B2CS.FILE_ID=FIL.ID  ");
			build.append("AND GSTRERR.FILE_ID=FIL.ID AND GSTRERR.INV_KEY=B2CS.B2CS_INVKEY  ");
			build.append("LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = B2CS.BATCH_ID  ");
			build.append("WHERE B2CS.IS_DELETE=FALSE AND IS_AMENDMENT=FALSE ");
			build.append(buildHeaderB2cs);
			build.append("UNION ALL SELECT DOC_TYPE,(IFNULL(B2CS.SUPPLIER_GSTIN,");
			build.append("'') ||'|'|| IFNULL(B2CS.RETURN_PERIOD,'') ||'|'||IFNULL(B2CS.DIFF_PERCENT,");
			build.append("'') ||'|'||IFNULL(B2CS.POS, 9999)||'|'||IFNULL(ITM.ECOM_GSTIN,");
			build.append("'')||'|'||IFNULL(ITM.TAX_RATE,9999) ) DOC_KEY,B2CS.SUPPLIER_GSTIN,");
			build.append("B2CS.RETURN_PERIOD,B2CS.DERIVED_RET_PERIOD,ITM.DIFF_PERCENT AS TRAN_TYPE,");
			build.append("'' AS MONTH, NULL AS ORG_POS, 0 AS ORG_HSNORSAC, '' AS ORG_UOM,");
			build.append("0 AS ORG_QNT, NULL AS ORG_RATE,0 AS ORG_TAXABLE_VALUE,NULL AS ORG_ECOM_GSTIN,");
			build.append("0 AS ORG_ECOM_SUP_VAL,B2CS.POS AS NEW_POS, ITM_HSNSAC AS NEW_HSNORSAC,");
			build.append("'' NEW_UOM, '' AS NEW_QNT, IFNULL(ITM.TAX_RATE,0) AS NEW_RATE,");
			build.append("ITM.TAXABLE_VALUE AS NEW_TAXABLE_VALUE, ITM.ECOM_GSTIN AS NEW_ECOM_GSTIN,");
			build.append("0 NEW_ECOM_SUP_VAL,ITM.IGST_AMT AS IGST_AMT, ITM.CGST_AMT AS CGST_AMT,");
			build.append("ITM.SGST_AMT AS SGST_AMT,");
			build.append("(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM) AS CESS_AMT,");
			build.append("ITM.ONB_LINE_ITEM_AMT AS TOT_VAL,B2CS.PROFIT_CENTRE,");
			build.append("B2CS.PLANT_CODE,B2CS.DIVISION,B2CS.LOCATION,");
			build.append("B2CS.SALES_ORGANIZATION,B2CS.DISTRIBUTION_CHANNEL,");
			build.append("B2CS.USERACCESS1,B2CS.USERACCESS2,");
			build.append("B2CS.USERACCESS3,B2CS.USERACCESS4,");
			build.append("B2CS.USERACCESS5,B2CS.USERACCESS6, '' USERDEFINED_FIELD1,");
			build.append("'' USERDEFINED_FIELD2, '' USERDEFINED_FIELD3,");
			build.append("'' AS INFO_ERROR_CODE_ASP,'' AS INFO_ERROR_DESCRIPTION_ASP,");
			build.append("( CASE WHEN B2CS.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED'  ");
			build.append("ELSE 'NOT_SAVED' END) AS SAVE_STATUS,");
			build.append("GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,");
			build.append("GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME,'' GSTN_ERROR_CODE,");
			build.append("'' GSTN_ERROR_DESCRIPTION,'' SOURCE_ID,'' FILE_NAME,");
			build.append("'' ASP_DATE_TIME FROM ANX_OUTWARD_DOC_HEADER_1A B2CS ");
			build.append("INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON B2CS.ID = ITM.DOC_HEADER_ID ");
			build.append("AND B2CS.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
			build.append("LEFT OUTER JOIN FILE_STATUS FIL ON B2CS.FILE_ID=FIL.ID ");
			build.append("LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = B2CS.BATCH_ID ");
			build.append("WHERE B2CS.RETURN_TYPE = 'GSTR1A'  ");
			build.append("AND B2CS.ASP_INVOICE_STATUS = 2 AND B2CS.COMPLIANCE_APPLICABLE=TRUE ");
			build.append("AND B2CS.IS_DELETE=FALSE AND B2CS.TAX_DOC_TYPE='B2CS' ");
			build.append(buildHeaderB2cs);
			build.append(" )  WHERE ");
			build.append(buildQueryb2cs);
			//	WHERE SUPPLIER_GSTIN='27GSPMH0482G1ZM'
			//	 and RETURN_PERIOD='072022'	 
				 
			build.append("GROUP BY SUPPLIER_GSTIN, RETURN_PERIOD,DERIVED_RET_PERIOD,");
			build.append("TRAN_TYPE,NEW_POS, NEW_ECOM_GSTIN, NEW_RATE,");
			build.append("DOC_KEY ) SELECT IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN),");
			build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD),IFNULL(GSTN.DERIVED_RET_PERIOD,");
			build.append("DIGI.DERIVED_RET_PERIOD), GSTN.SUPPLY_TYPE,GSTN.STATE_NAME,");
			build.append("GSTN.POS, GSTN.TAX_RATE,GSTN.TAXABLE_VALUE,GSTN.GSTN_IGST_AMT,");
			build.append("GSTN.GSTN_CGST_AMT,GSTN.GSTN_SGST_AMT, GSTN.GSTN_CESS_AMT,");
			build.append("GSTN.DIFF_PERCENT,GSTN.ECOM_TYPE, GSTN.ECOM_GSTIN, GSTN.IS_FILED,");
			build.append("DIGI.TRAN_TYPE, DIGI.MONTH,DIGI.ORG_POS, DIGI.ORG_HSNORSAC,");
			build.append("DIGI.ORG_UOM, DIGI.ORG_QNT, DIGI.ORG_RATE,DIGI.ORG_TAXABLE_VALUE,");
			build.append("DIGI.ORG_ECOM_GSTIN, DIGI.ORG_ECOM_SUP_VAL, DIGI.NEW_POS, DIGI.NEW_HSNORSAC,");
			build.append("DIGI.NEW_UOM, DIGI.NEW_QNT, DIGI.NEW_RATE,DIGI.NEW_TAXABLE_VALUE,");
			build.append("DIGI.NEW_ECOM_GSTIN,DIGI.NEW_ECOM_SUP_VAL,DIGI.IGST_AMT, DIGI.CGST_AMT,");
			build.append("DIGI.SGST_AMT, DIGI.CESS_AMT, DIGI.TOT_VAL, DIGI.PROFIT_CENTRE,");
			build.append("DIGI.PLANT_CODE, DIVISION,LOCATION, SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,");
			build.append("USERACCESS1, USERACCESS2, USERACCESS3,USERACCESS4, USERACCESS5,USERACCESS6,");
			build.append("USERDEFINED_FIELD1, USERDEFINED_FIELD2,USERDEFINED_FIELD3 , INFO_ERROR_CODE_ASP,");
			build.append("INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,GSTIN_REF_ID ,");
			build.append("TO_CHAR(ADD_SECONDS(GSTIN_REF_ID_TIME,19800),'DD-MM-YYYY HH24:MI:SS') ");
			build.append("GSTIN_REF_ID_TIME,GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,");
			build.append("SOURCE_ID,FILE_NAME, ASP_DATE_TIME,");
			build.append("STATE_CODE ,ROW_NUMBER () OVER ( "); 
			build.append("ORDER BY IFNULL(GSTN.SUPPLIER_GSTIN,");
			build.append("DIGI.SUPPLIER_GSTIN)) SNO,  ");
			build.append("(IFNULL(DIGI.NEW_TAXABLE_VALUE,0) -IFNULL(GSTN.TAXABLE_VALUE,0)) as DIFF_TAXABLE_VALUE,");
			build.append("(IFNULL(DIGI.IGST_AMT,0) -IFNULL(GSTN.GSTN_IGST_AMT,0)) as DIFF_IGST_AMT,");
			build.append("(IFNULL(DIGI.CGST_AMT,0) -IFNULL(GSTN.GSTN_CGST_AMT,0)) as DIFF_CGST_AMT,");
			build.append("(IFNULL(DIGI.SGST_AMT,0) -IFNULL(GSTN.GSTN_SGST_AMT,0)) as DIFF_SGST_AMT,");
			build.append("(IFNULL(DIGI.CESS_AMT,0) -IFNULL(GSTN.GSTN_CESS_AMT,0)) as DIFF_CESS_AMT ");
			build.append(" from DIGI full outer join GSTN on gstn.doc_key=DIGI.DOC_KEY;  ");

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
