/**
 * 
 */
package com.ey.advisory.app.services.reports;

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
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.views.client.AspProcessVsSubmitAtAndAtaDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Component("AspProcessVsSubmitAtAReportDaoImpl")
public class AspProcessVsSubmitAtAReportDaoImpl
		implements AspProcessVsSubmitDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;
	
	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;
	
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

		StringBuilder buildHeader2 = new StringBuilder();
		StringBuilder buildHeader3 = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" AND REC.SUPPLIER_GSTIN IN :gstinList");

				buildHeader2.append(" AND HDR.GSTIN IN :gstinList");
				buildHeader3.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeader.append(" AND REC.PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeader.append(" AND REC.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeader.append(" AND REC.SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeader.append(" AND REC.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeader.append(" AND REC.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeader.append(" AND REC.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeader.append(" AND REC.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeader.append(" AND REC.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeader.append(" AND REC.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeader.append(" AND REC.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeader.append(" AND REC.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeader.append(" AND REC.USERACCESS6 IN :ud6List");
			}
		}

		if (taxPeriodFrom != null && taxPeriodTo != null) {
			buildHeader.append(" AND REC.DERIVED_RET_PERIOD BETWEEN ");
			buildHeader.append(":taxPeriodFrom AND :taxPeriodTo");

			buildHeader2.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			buildHeader3.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
			
		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString(),buildHeader2.toString(),buildHeader3.toString());
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
				verticalHsnList
						.add(convertApiInwardProcessedRecords(arr, hsnMap, context));
			}
		}
		return verticalHsnList;
	}

	private AspProcessVsSubmitAtAndAtaDto convertApiInwardProcessedRecords(
			Object[] arr, Map<String, String> hsnMap, ProcessingContext context) {
		AspProcessVsSubmitAtAndAtaDto obj = new AspProcessVsSubmitAtAndAtaDto();

		obj.setGstnSgstin(arr[0] != null ? arr[0].toString() : null);
		obj.setGstnRetPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setGstnDerRetPeriod(arr[2] != null ? arr[2].toString() : null);
		obj.setDigiGstOrgMonth(arr[3] != null ? arr[3].toString() : null);
		obj.setGstnSuppType(arr[4] != null ? arr[4].toString() : null);
		obj.setGstnOrgSuppType(arr[5] != null ? arr[5].toString() : null);
		obj.setGstnStateName(arr[6] != null ? arr[6].toString() : null);
		obj.setGstnPos(arr[7] != null ? arr[7].toString() : null);
		obj.setGstnOrgPos(arr[8] != null ? arr[8].toString() : null);
		obj.setGstnRateTax(arr[9] != null ? arr[9].toString() : null);
		obj.setGstnGrossAt(arr[10] != null ? arr[10].toString() : null);
		obj.setGstnIgst(arr[11] != null ? arr[11].toString() : null);
		obj.setGstnCgst(arr[12] != null ? arr[12].toString() : null);
		obj.setGstnSgst(arr[13] != null ? arr[13].toString() : null);
		obj.setGstnCess(arr[14] != null ? arr[14].toString() : null);
		obj.setGstnDiffPerRate(arr[15] != null ? arr[15].toString() : null);
		//obj.setGstnIsFiled(arr[16] != null ? arr[16].toString() : null);
		obj.setGstnIsFiled(isGstinTaxperiodFiled(context,arr[0].toString(), arr[1].toString()));
		obj.setDigiGstTransType(arr[17] != null ? arr[17].toString() : null);
		obj.setDigiGstMonth(arr[18] != null ? arr[18].toString() : null);
		obj.setDigiGstOrgPos(arr[19] != null ? arr[19].toString() : null);
		obj.setDigiGstOrgRate(arr[20] != null ? arr[20].toString() : null);
		obj.setDigiGstOrgGrossAt(arr[21] != null ? arr[21].toString() : null);
		obj.setDigiGstNewPos(arr[22] != null ? arr[22].toString() : null);
		obj.setDigiGstNewRate(arr[23] != null ? arr[23].toString() : null);
		obj.setDigiGstNewGrossAt(arr[24] != null ? arr[24].toString() : null);
		obj.setDigiGstIgst(arr[25] != null ? arr[25].toString() : null);
		obj.setDigiGstCgst(arr[26] != null ? arr[26].toString() : null);
		obj.setDigiGstSgst(arr[27] != null ? arr[27].toString() : null);
		obj.setDigiGstCess(arr[28] != null ? arr[28].toString() : null);
		obj.setDigiGstProfitCenter(arr[29] != null ? arr[29].toString() : null);
		obj.setDigiGstPlant(arr[30] != null ? arr[30].toString() : null);
		obj.setDigiGstDivision(arr[31] != null ? arr[31].toString() : null);
		obj.setDigiGstLocation(arr[32] != null ? arr[32].toString() : null);
		obj.setDigiGstSalesOrg(arr[33] != null ? arr[33].toString() : null);
		obj.setDigiGstDistChannel(arr[34] != null ? arr[34].toString() : null);
		obj.setDigiGstUserAcc1(arr[35] != null ? arr[35].toString() : null);
		obj.setDigiGstUserAcc2(arr[36] != null ? arr[36].toString() : null);
		obj.setDigiGstUserAcc3(arr[37] != null ? arr[37].toString() : null);
		obj.setDigiGstUserAcc4(arr[38] != null ? arr[38].toString() : null);
		obj.setDigiGstUserAcc5(arr[39] != null ? arr[39].toString() : null);
		obj.setDigiGstUserAcc6(arr[40] != null ? arr[40].toString() : null);
		obj.setDigiGstUserDef1(arr[41] != null ? arr[41].toString() : null);
		obj.setDigiGstUserDef2(arr[42] != null ? arr[42].toString() : null);
		obj.setDigiGstUserDef3(arr[43] != null ? arr[43].toString() : null);
		obj.setDigiGstAspInfo(arr[44] != null ? arr[44].toString() : null);
		obj.setDigiGstAspInfoDescription(
				arr[45] != null ? arr[45].toString() : null);
		obj.setDigiGstSaveStatus(arr[46] != null ? arr[46].toString() : null);
		obj.setDigiGstGstnRefId(arr[47] != null ? arr[47].toString() : null);
		obj.setDigiGstGstnRefTime(arr[48] != null ? arr[48].toString() : null);
		obj.setDigiGstGstnErrorCode(
				arr[49] != null ? arr[49].toString() : null);
		obj.setDigiGstGstnErrorDes(arr[50] != null ? arr[50].toString() : null);

		obj.setDigiGstSourceId(arr[51] != null ? arr[51].toString() : null);
		obj.setDigiGstFileName(arr[52] != null ? arr[52].toString() : null);
		obj.setDigiGstAspDate(arr[53] != null ? arr[53].toString() : null);
		obj.setGstnSno(arr[55] != null ? arr[55].toString() : null);

		String stateName = null;
		String stateCode = (arr[54] != null ? arr[54].toString() : null);
		obj.setGstnStateCode(stateCode);

		if (!Strings.isNullOrEmpty(stateCode)) {
			stateName = stateCode + "-" + hsnMap.get(stateCode);
		}
		obj.setGstnStateName(stateName);
		
		obj.setDiffTaxableValue(arr[56] != null ? arr[56].toString() : null);
		obj.setDiffIgst(arr[57] != null ? arr[57].toString() : null);
		obj.setDiffCgst(arr[58] != null ? arr[58].toString() : null);
		obj.setDiffSgst(arr[59] != null ? arr[59].toString() : null);
		obj.setDiffCess(arr[60] != null ? arr[60].toString() : null);
		
		return obj;
	}

	private String createApiProcessedQueryString(String buildHeader,String buildHeaderForGet, String buildHeader3) {
		
		StringBuilder build = new StringBuilder();

		build.append("SELECT ");
		build.append("SUPPLIER_GSTIN, ");
		build.append("RETURN_PERIOD, ");
		build.append("DERIVED_RET_PERIOD, ");
		build.append("ORG_MONTH, ");
		build.append("SUPPLY_TYPE, ");
		build.append("ORG_SUPPLY_TYPE, ");
		build.append("STATE_NAME, ");
		build.append("POS, ");
		build.append("GSTN_ORG_POS, ");
		build.append("TAX_RATE, ");
		build.append("GROSS_ADV_RECEIVED, ");
		build.append("GSTN_IGST_AMT, ");
		build.append("GSTN_CGST_AMT, ");
		build.append("GSTN_SGST_AMT, ");
		build.append("GSTN_CESS_AMT, ");
		build.append("DIFF_PERCENT, ");
		build.append("IS_FILED, ");
		build.append("TRAN_TYPE, ");
		build.append("MONTH, ");
		build.append("ORG_POS, ");
		build.append("ORG_RATE, ");
		build.append("ORG_GROSS_ADV_RECEIVED, ");
		build.append("NEW_POS, ");
		build.append("NEW_RATE, ");
		build.append("NEW_GROSS_ADV_RECEIVED, ");
		build.append("ASP_IGST_AMT, ");
		build.append("ASP_CGST_AMT, ");
		build.append("ASP_SGST_AMT, ");
		build.append("ASP_CESS_AMT, ");
		build.append("PROFIT_CENTRE, ");
		build.append("PLANT_CODE, ");
		build.append("DIVISION, ");
		build.append("LOCATION, ");
		build.append("SALES_ORGANIZATION, ");
		build.append("DISTRIBUTION_CHANNEL, ");
		build.append("USERACCESS1, ");
		build.append("USERACCESS2, ");
		build.append("USERACCESS3, ");
		build.append("USERACCESS4, ");
		build.append("USERACCESS5, ");
		build.append("USERACCESS6, ");
		build.append("USERDEFINED_FIELD1, ");
		build.append("USERDEFINED_FIELD2, ");
		build.append("USERDEFINED_FIELD3, ");
		build.append("INFO_ERROR_CODE_ASP, ");
		build.append("INFO_ERROR_DESCRIPTION_ASP, ");
		build.append("SAVE_STATUS, ");
		build.append("GSTN_SAVE_REF_ID, ");
		build.append("GSTIN_REF_ID_TIME, ");
		build.append("GSTN_ERROR_CODE, ");
		build.append("GSTN_ERROR_DESCRIPTION, ");
		build.append("SOURCE_ID, ");
		build.append("FILE_NAME, ");
		build.append("ASP_DATE_TIME, ");
		build.append("STATE_CODE, ");
		build.append("SNO, ");
		build.append("(IFNULL(NEW_GROSS_ADV_RECEIVED,0) -IFNULL(GROSS_ADV_RECEIVED,0)) as DIFF_TAXABLE_VALUE, ");
		build.append("(IFNULL(ASP_IGST_AMT,0) -IFNULL(GSTN_IGST_AMT,0)) as DIFF_IGST_AMT, ");
		build.append("(IFNULL(ASP_CGST_AMT,0) -IFNULL(GSTN_CGST_AMT,0)) as DIFF_CGST_AMT, ");
		build.append("(IFNULL(ASP_SGST_AMT,0) -IFNULL(GSTN_SGST_AMT,0)) as DIFF_SGST_AMT, ");
		build.append("(IFNULL(ASP_CESS_AMT,0) -IFNULL(GSTN_CESS_AMT,0)) as DIFF_CESS_AMT ");
		build.append(" FROM ");
		build.append("( ");
		build.append("select *,ROW_NUMBER () OVER ( ORDER BY SUPPLIER_GSTIN) SNO from ");
		build.append("( with GSTN as ( SELECT HDR.GSTIN SUPPLIER_GSTIN, ");
		build.append("HDR.RETURN_PERIOD RETURN_PERIOD, HDR.DERIVED_RET_PERIOD DERIVED_RET_PERIOD,");
		build.append("(IFNULL(HDR.GSTIN,'')  ||'|'|| IFNULL(HDR.RETURN_PERIOD,'')  ");
		build.append("||'|'||IFNULL(MAP(HDR.DIFF_PERCENT,0.65,'L65','N'),'') ");
		build.append(" ) DOC_KEY,");
	//	build.append("SUPPLY_TYPE,SUBSTR(HDR.GSTIN,1,2) AS STATE_CODE,'' STATE_NAME,");
		build.append("ORG_MONTH, ''SUPPLY_TYPE, SUPPLY_TYPE ORG_SUPPLY_TYPE, SUBSTR(HDR.GSTIN, 1,2) AS STATE_CODE,");
		build.append("'' STATE_NAME, '' POS, POS AS GSTN_ORG_POS,NULL as TAX_RATE,");
		build.append("SUM(ITM.ADVREC_AMT) TAXABLE_VALUE,SUM(ITM.IGST_AMT) IGST_AMT,");
		build.append("SUM(ITM.CGST_AMT) CGST_AMT,SUM(ITM.SGST_AMT) SGST_AMT,SUM(ITM.CESS_AMT) CESS_AMT,");
		build.append("HDR.DIFF_PERCENT,BT.IS_FILED,'ADV REC-A' AS TABLE_TYPE ");
		build.append("FROM GETGSTR1_ATA_HEADER HDR ");
		build.append("INNER JOIN GETGSTR1_ATA_ITEM ITM ON HDR.ID = ITM.HEADER_ID  ");
		build.append("AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  ");
		build.append("LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID  ");
		build.append("WHERE HDR.IS_DELETE=FALSE AND BT.IS_DELETE=FALSE ");
		build.append(buildHeaderForGet);
						//AND HDR.GSTIN='27GSPMH0482G1ZM'
			            //and HDR.RETURN_PERIOD ='072022'		
		build.append(" GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD, SUPPLY_TYPE,");
		build.append("POS,HDR.DIFF_PERCENT,BT.IS_FILED,ORG_MONTH ), ");
		build.append("DIGI as ( SELECT SUPPLIER_GSTIN, RETURN_PERIOD, DERIVED_RET_PERIOD,");
		build.append("(CASE WHEN TRAN_TYPE IN ('ZL65','L65') ");
		build.append("THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL)  ");
		build.append("THEN 'N' END) TRAN_TYPE,(IFNULL(SUPPLIER_GSTIN,'') "); 
		build.append("||'|'|| IFNULL(RETURN_PERIOD,'') ");
		build.append("||'|'||IFNULL((CASE WHEN TRAN_TYPE IN ('ZL65','L65') "); 
		build.append("THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL) ");
		build.append("THEN 'N' END),'') ||'|'||IFNULL(TO_NUMBER(NEW_POS), 9999) ) as DOC_KEY,");
		build.append("MAX(MONTH)MONTH, MAX(ORG_POS)ORG_POS,MAX(ORG_RATE)ORG_RATE,");
		build.append("MAX(ORG_GROSS_ADV_RECEIVED)ORG_TAXABLE_VALUE, NEW_POS, NULL NEW_RATE,");
		build.append("SUM(NEW_GROSS_ADV_RECEIVED)NEW_TAXABLE_VALUE,");
		build.append("SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT, SUM(SGST_AMT)SGST_AMT,");
		build.append("SUM(CESS_AMT)CESS_AMT, MAX(PROFIT_CENTRE)PROFIT_CENTRE,");
		build.append("MAX(PLANT_CODE)PLANT_CODE,MAX(DIVISION)DIVISION,MAX(LOCATION)LOCATION,");
		build.append("MAX(SALES_ORGANIZATION)SALES_ORGANIZATION,");
		build.append("MAX(DISTRIBUTION_CHANNEL)DISTRIBUTION_CHANNEL,");
		build.append("MAX(USERACCESS1)USERACCESS1, MAX(USERACCESS2)USERACCESS2,");
		build.append("MAX(USERACCESS3)USERACCESS3,MAX(USERACCESS4)USERACCESS4,");
		build.append("MAX(USERACCESS5)USERACCESS5, MAX(USERACCESS6)USERACCESS6,");
		build.append("MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,");
		build.append("MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,");
		build.append("MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,");
		build.append("MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP,");
		build.append("MAX(INFO_ERROR_DESCRIPTION_ASP)INFO_ERROR_DESCRIPTION_ASP ,");
		build.append("MAX(BATCH_ID) BATCH_ID,MAX(SOURCE_ID)SOURCE_ID,");
		build.append("MAX(FILE_NAME)FILE_NAME, MAX(ASP_DATE_TIME)ASP_DATE_TIME "); 
		build.append("FROM ( SELECT REC.SUPPLIER_GSTIN,REC.RETURN_PERIOD,");
		build.append("REC.DERIVED_RET_PERIOD, TRAN_TYPE,MONTH, NULL ORG_POS,");
		build.append("NULL ORG_RATE,ORG_GROSS_ADV_RECEIVED, NEW_POS,");
		build.append("NUll NEW_RATE, NEW_GROSS_ADV_RECEIVED,IFNULL(IGST_AMT,0) AS IGST_AMT,");
		build.append("IFNULL(CGST_AMT,0) AS CGST_AMT, IFNULL(SGST_AMT,0) AS SGST_AMT, IFNULL(CESS_AMT,0) AS CESS_AMT, '' PROFIT_CENTRE,");
		build.append("''PLANT_CODE,'' DIVISION,''LOCATION, ''SALES_ORGANIZATION,");
		build.append("''DISTRIBUTION_CHANNEL, ''USERACCESS1,''USERACCESS2,");
		build.append("''USERACCESS3,''USERACCESS4,''USERACCESS5, ''USERACCESS6,");
		build.append("''USERDEFINED_FIELD1,	''USERDEFINED_FIELD2,''USERDEFINED_FIELD3,");
		build.append("'' INFO_ERROR_CODE_ASP, '' INFO_ERROR_DESCRIPTION_ASP,");
		build.append("REC.BATCH_ID , '' SOURCE_ID, '' FILE_NAME,'' ASP_DATE_TIME  ");
		build.append("FROM GSTR1_PROCESSED_ADV_RECEIVED REC ");
		build.append("LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() GSTRERR ");
		build.append("ON REC.AS_ENTERED_ID= GSTRERR.COMMON_ID  ");
		build.append("AND REC.FILE_ID=GSTRERR.FILE_ID  ");
		build.append("AND GSTRERR.TABLE_TYPE='ADV REC-A' AND RETURN_TYPE='GSTR1'  ");
		build.append("LEFT OUTER JOIN FILE_STATUS FIL ON REC.FILE_ID=FIL.ID  ");
		build.append("AND GSTRERR.FILE_ID=FIL.ID AND GSTRERR.INV_KEY=REC.AT_INVKEY "); 
		build.append("WHERE REC.IS_DELETE=FALSE AND IS_AMENDMENT=TRUE  ");
		build.append(buildHeader);
		
		build.append(" UNION ALL ");
		build.append(" SELECT  SUPPLIER_GSTIN");
		build.append("	,HDR.RETURN_PERIOD");
		build.append("	,HDR.DERIVED_RET_PERIOD");
		build.append("	,HDR.DIFF_PERCENT TRAN_TYPE");
		build.append("	,NULL MONTH");
		build.append("	,NULL ORG_POS");
		build.append("	,NULL ORG_RATE");
		build.append("	,NULL ORG_GROSS_ADV_RECEIVED");
		build.append("	,HDR.POS NEW_POS");
		build.append("	,NULL NEW_RATE");
		build.append("	,IFNULL(ITM.TAXABLE_VALUE,0) AS NEW_GROSS_ADV_RECEIVED");
		build.append("	,IFNULL(ITM.IGST_AMT,0) AS IGST_AMT");
		build.append("	,IFNULL(ITM.CGST_AMT,0) AS CGST_AMT");
		build.append("	,(IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS_AMT ");
		build.append("	,(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM)");
		build.append("	,'' PROFIT_CENTRE");
		build.append("	,'' PLANT_CODE");
		build.append("	,'' DIVISION");
		build.append("	,'' LOCATION");
		build.append("	,'' SALES_ORGANIZATION");
		build.append("	,'' DISTRIBUTION_CHANNEL");
		build.append("	,'' USERACCESS1");
		build.append("	,'' USERACCESS2");
		build.append("	,'' USERACCESS3");
		build.append("	,'' USERACCESS4");
		build.append("	,'' USERACCESS5");
		build.append("	,'' USERACCESS6");
		build.append("	,'' USERDEFINED_FIELD1");
		build.append("	,'' USERDEFINED_FIELD2");
		build.append("	,'' USERDEFINED_FIELD3");
		build.append("	,'' INFO_ERROR_CODE_ASP");
		build.append("	,NULL INFO_ERROR_DESCRIPTION_ASP");
		build.append("	,NULL BATCH_ID");
		build.append("	,NULL SOURCE_ID");
		build.append("	,NULL FILE_NAME");
		build.append("	,NULL ASP_DATE_TIME");
		build.append("    FROM ANX_OUTWARD_DOC_HEADER HDR ");
		build.append("  JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID ");
		 build.append(" WHERE HDR.TAX_DOC_TYPE='ATA' ");
		build.append(" AND HDR.RETURN_TYPE='GSTR1' AND HDR.COMPLIANCE_APPLICABLE=TRUE AND HDR.ASP_INVOICE_STATUS = 2  ");
		build.append(buildHeader3);
		build.append(" AND IS_DELETE=FALSE ");
					//	and REC.SUPPLIER_GSTIN='27GSPMH0482G1ZM'
					//	and REC.RETURN_PERIOD ='072022'
		build.append(" ) GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,	DERIVED_RET_PERIOD,");
		build.append("(CASE WHEN TRAN_TYPE IN ('ZL65','L65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') "); 
		build.append("OR TRAN_TYPE IS NULL) THEN 'N' END),NEW_POS ) SELECT ");
		build.append("IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN) as SUPPLIER_GSTIN,");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD) AS RETURN_PERIOD,");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD) AS DERIVED_RET_PERIOD,");
	//	build.append("SUPPLY_TYPE, STATE_NAME, POS, NULL AS TAX_RATE,");
		build.append("ORG_MONTH, SUPPLY_TYPE, ORG_SUPPLY_TYPE, STATE_NAME, POS,GSTN_ORG_POS,TAX_RATE,");
		build.append("SUM(TAXABLE_VALUE) GROSS_ADV_RECEIVED,");
		build.append("SUM(GSTN.IGST_AMT)GSTN_IGST_AMT,");
		build.append("SUM(GSTN.CGST_AMT)GSTN_CGST_AMT,");
		build.append("SUM(GSTN.SGST_AMT)GSTN_SGST_AMT,");
		build.append("SUM(GSTN.CESS_AMT)GSTN_CESS_AMT,");
		build.append("DIFF_PERCENT,IS_FILED,TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,");
		build.append("SUM(ORG_TAXABLE_VALUE)ORG_GROSS_ADV_RECEIVED,");
		build.append("NEW_POS,NEW_RATE,SUM(NEW_TAXABLE_VALUE)NEW_GROSS_ADV_RECEIVED,");
		build.append("SUM(DIGI.IGST_AMT)ASP_IGST_AMT,");
		build.append("SUM(DIGI.CGST_AMT)ASP_CGST_AMT,");
		build.append("SUM(DIGI.SGST_AMT)ASP_SGST_AMT,");
		build.append("SUM(DIGI.CESS_AMT)ASP_CESS_AMT,");
		build.append("PROFIT_CENTRE,PLANT_CODE,	DIVISION, LOCATION,");
		build.append("SALES_ORGANIZATION, DISTRIBUTION_CHANNEL,	USERACCESS1,");
		build.append("USERACCESS2,USERACCESS3, USERACCESS4,USERACCESS5,");
		build.append("USERACCESS6,USERDEFINED_FIELD1, USERDEFINED_FIELD2,");
		build.append("USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP, INFO_ERROR_DESCRIPTION_ASP,");
		build.append("map(batch.GSTN_SAVE_STATUS,'P','SAVED','NOT SAVED') as SAVE_STATUS,");
		build.append("batch.GSTN_SAVE_REF_ID,");
		build.append("TO_CHAR(add_seconds(batch.BATCH_DATE,19800),'DD-MM-YYYY HH24:MI:SS') ");
		build.append("GSTIN_REF_ID_TIME,batch.ERROR_CODE AS GSTN_ERROR_CODE,");
		build.append("batch.ERROR_DESC AS GSTN_ERROR_DESCRIPTION,");
		build.append("SOURCE_ID,FILE_NAME, ASP_DATE_TIME,IFNULL(STATE_CODE,'') as STATE_CODE "); 
		build.append("from GSTN FULL OUTER JOIN DIGI on GSTN.DOC_KEY=  DIGI.DOC_KEY ");
		build.append("left join ( select ID,GSTN_SAVE_STATUS,GSTN_SAVE_REF_ID,BATCH_DATE,");
		build.append("ERROR_CODE,ERROR_DESC from GSTR1_GSTN_SAVE_BATCH ");
		build.append("where is_delete = FALSE ");
			//	--and section='HSNSUM'
		build.append(") batch on digi.BATCH_ID=batch.id ");
		build.append("GROUP BY IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN),");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD) ,");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD) ,");
		build.append("ORG_MONTH, SUPPLY_TYPE, ORG_SUPPLY_TYPE,GSTN_ORG_POS,TAX_RATE,");
		build.append("TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,	NEW_POS,NEW_RATE,");
		build.append("PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION, SALES_ORGANIZATION,");
		build.append("DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3, USERACCESS4,");
		build.append("USERACCESS5,USERACCESS6,USERDEFINED_FIELD1, USERDEFINED_FIELD2,");
		build.append("USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP,INFO_ERROR_DESCRIPTION_ASP,");
		build.append("map(batch.GSTN_SAVE_STATUS,'P','SAVED','NOT SAVED') ,batch.GSTN_SAVE_REF_ID,");
		build.append("batch.BATCH_DATE,	batch.ERROR_CODE ,batch.ERROR_DESC ,");
		build.append("SOURCE_ID,FILE_NAME, ASP_DATE_TIME,SUPPLY_TYPE, STATE_CODE, STATE_NAME, POS,");
		build.append("DIFF_PERCENT, IS_FILED  ) );");

		
		/*
		
		
		
		build.append("select *,ROW_NUMBER () OVER ( ORDER BY SUPPLIER_GSTIN) SNO from ( " );
		build.append("with GSTN as ( SELECT HDR.GSTIN SUPPLIER_GSTIN,");
		build.append("HDR.RETURN_PERIOD RETURN_PERIOD, HDR.DERIVED_RET_PERIOD DERIVED_RET_PERIOD,");
		build.append("(IFNULL(HDR.GSTIN,'')  ||'|'|| IFNULL(HDR.RETURN_PERIOD,'') ");
		build.append(" ||'|'||IFNULL(MAP(HDR.DIFF_PERCENT,0.65,'L65','N'),'')  ");
		build.append("||'|'||IFNULL(TO_NUMBER(POS), 9999)) DOC_KEY,");
		build.append("SUPPLY_TYPE,SUBSTR(HDR.GSTIN,1,2) AS STATE_CODE,'' STATE_NAME,");
		build.append("POS,ITM.TAX_RATE, SUM(ITM.ADVREC_AMT) TAXABLE_VALUE,");
		build.append("SUM(ITM.IGST_AMT) IGST_AMT,SUM(ITM.CGST_AMT) CGST_AMT,");
		build.append("SUM(ITM.SGST_AMT) SGST_AMT,SUM(ITM.CESS_AMT) CESS_AMT,");
		build.append("HDR.DIFF_PERCENT,BT.IS_FILED, 'ADV REC' AS TABLE_TYPE  ");
		build.append("FROM GETGSTR1_AT_HEADER HDR ");
		build.append("INNER JOIN GETGSTR1_AT_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		build.append("AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  ");
		build.append("LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID ");
		build.append("WHERE HDR.IS_DELETE=FALSE  AND BT.IS_DELETE=FALSE ");
		build.append(buildHeaderForGet );
					//	AND HDR.GSTIN='27GSPMH0482G1ZM'
					//	and HDR.RETURN_PERIOD='072022'	
		build.append("GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,");
		build.append("SUPPLY_TYPE, POS,ITM.TAX_RATE,HDR.DIFF_PERCENT,BT.IS_FILED ),");
		build.append("DIGI as (	SELECT SUPPLIER_GSTIN,RETURN_PERIOD, DERIVED_RET_PERIOD,");
		build.append("(CASE WHEN TRAN_TYPE IN ('ZL65','L65')  ");
		build.append("THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL)  ");
		build.append("THEN 'N' END) TRAN_TYPE,(IFNULL(SUPPLIER_GSTIN,'') ");
		build.append("||'|'|| IFNULL(RETURN_PERIOD,'') ");
		build.append("||'|'||IFNULL((CASE WHEN TRAN_TYPE IN ('ZL65','L65') ");
		build.append("THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL)  ");
		build.append("THEN 'N' END),'') ||'|'||IFNULL(TO_NUMBER(NEW_POS), 9999)) as DOC_KEY,");
		build.append("MAX(MONTH)MONTH, MAX(ORG_POS)ORG_POS,MAX(ORG_RATE)ORG_RATE,");
		build.append("MAX(ORG_GROSS_ADV_RECEIVED)ORG_TAXABLE_VALUE, NEW_POS,");
		build.append("MAX(NEW_RATE)NEW_RATE,SUM(NEW_GROSS_ADV_RECEIVED)NEW_TAXABLE_VALUE,");
		build.append("SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,SUM(SGST_AMT)SGST_AMT,");
		build.append("SUM(CESS_AMT)CESS_AMT, MAX(PROFIT_CENTRE)PROFIT_CENTRE,");
		build.append("MAX(PLANT_CODE)PLANT_CODE, MAX(DIVISION)DIVISION,MAX(LOCATION)LOCATION,");
		build.append("MAX(SALES_ORGANIZATION)SALES_ORGANIZATION,");
		build.append("MAX(DISTRIBUTION_CHANNEL)DISTRIBUTION_CHANNEL,");
		build.append("MAX(USERACCESS1)USERACCESS1, MAX(USERACCESS2)USERACCESS2,");
		build.append("MAX(USERACCESS3)USERACCESS3, MAX(USERACCESS4)USERACCESS4,");
		build.append("MAX(USERACCESS5)USERACCESS5,MAX(USERACCESS6)USERACCESS6,");
		build.append("MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,");
		build.append("MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,");
		build.append("MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,");
		build.append("MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP,");
		build.append("MAX(INFO_ERROR_DESCRIPTION_ASP)INFO_ERROR_DESCRIPTION_ASP ,");
		build.append("MAX(SAVE_STATUS)SAVE_STATUS,MAX(GSTIN_REF_ID)GSTIN_REF_ID,");
		build.append("MAX(GSTIN_REF_ID_TIME)GSTIN_REF_ID_TIME,");
		build.append("MAX(GSTN_ERROR_CODE)GSTN_ERROR_CODE,");
		build.append("MAX(GSTN_ERROR_DESCRIPTION)GSTN_ERROR_DESCRIPTION,");
		build.append("MAX(SOURCE_ID)SOURCE_ID, MAX(FILE_NAME)FILE_NAME,");
		build.append("MAX(ASP_DATE_TIME)ASP_DATE_TIME  FROM ( SELECT REC.SUPPLIER_GSTIN,");
		build.append("REC.RETURN_PERIOD,REC.DERIVED_RET_PERIOD, TRAN_TYPE,MONTH,");
		build.append("NULL ORG_POS,NULL ORG_RATE,ORG_GROSS_ADV_RECEIVED,NEW_POS,");
		build.append("NULL NEW_RATE, NEW_GROSS_ADV_RECEIVED,IGST_AMT,CGST_AMT, SGST_AMT,");
		build.append("CESS_AMT, '' PROFIT_CENTRE,''PLANT_CODE,'' DIVISION,''LOCATION,");
		build.append("''SALES_ORGANIZATION,	''DISTRIBUTION_CHANNEL,");
		build.append("''USERACCESS1, ''USERACCESS2,	''USERACCESS3,''USERACCESS4,");
		build.append("''USERACCESS5, ''USERACCESS6, ''USERDEFINED_FIELD1,");
		build.append("''USERDEFINED_FIELD2, ''USERDEFINED_FIELD3, '' INFO_ERROR_CODE_ASP,");
		build.append("'' INFO_ERROR_DESCRIPTION_ASP, (CASE WHEN REC.IS_SAVED_TO_GSTN = TRUE "); 
		build.append("AND REC.IS_DELETE=FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED' "); 
		build.append("END) AS SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,");
		build.append("GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, '' AS GSTN_ERROR_CODE,");
		build.append("'' AS GSTN_ERROR_DESCRIPTION, '' SOURCE_ID,'' FILE_NAME,");
		build.append("'' ASP_DATE_TIME  FROM GSTR1_PROCESSED_ADV_RECEIVED REC "); 
		build.append("LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ");
		build.append("GSTRERR ON REC.AS_ENTERED_ID= GSTRERR.COMMON_ID  ");
		build.append("AND REC.FILE_ID=GSTRERR.FILE_ID  ");
		build.append("AND GSTRERR.TABLE_TYPE='ADV REC'  ");
		build.append("AND RETURN_TYPE='GSTR1'  ");
		build.append("LEFT OUTER JOIN FILE_STATUS FIL ON REC.FILE_ID=FIL.ID  ");
		build.append("AND GSTRERR.FILE_ID=FIL.ID AND GSTRERR.INV_KEY=REC.AT_INVKEY  ");
		build.append("LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = REC.BATCH_ID  ");
		build.append("WHERE REC.IS_DELETE=FALSE AND IS_AMENDMENT=FALSE ");
		build.append(buildHeader);
					//	AND  REC.SUPPLIER_GSTIN ='27GSPMH0482G1ZM'
					//	and REC.RETURN_PERIOD ='072022'
						
		build.append(" ) GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,	DERIVED_RET_PERIOD,");
		build.append("(CASE WHEN TRAN_TYPE IN ('ZL65','L65') "); 
		build.append("THEN 'L65' WHEN (TRAN_TYPE IN ('Z', 'N','') OR TRAN_TYPE IS NULL) THEN 'N' END),");
		build.append("NEW_POS ) SELECT ");
		build.append("IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN) as SUPPLIER_GSTIN,");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD) AS RETURN_PERIOD,");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD) AS DERIVED_RET_PERIOD,");
		build.append("SUPPLY_TYPE, STATE_NAME, POS,NULL AS TAX_RATE,");
		build.append("SUM(TAXABLE_VALUE) GROSS_ADV_RECEIVED,");
		build.append("SUM(GSTN.IGST_AMT)GSTN_IGST_AMT,");
		build.append("SUM(GSTN.CGST_AMT)GSTN_CGST_AMT,");
		build.append("SUM(GSTN.SGST_AMT)GSTN_SGST_AMT,");
		build.append("SUM(GSTN.CESS_AMT)GSTN_CESS_AMT,");
		build.append("DIFF_PERCENT,	IS_FILED, TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,");
		build.append("SUM(ORG_TAXABLE_VALUE)ORG_GROSS_ADV_RECEIVED,");
		build.append("NEW_POS,NEW_RATE,SUM(NEW_TAXABLE_VALUE)NEW_GROSS_ADV_RECEIVED,");
		build.append("SUM(DIGI.IGST_AMT)ASP_IGST_AMT,");
		build.append("SUM(DIGI.CGST_AMT)ASP_CGST_AMT,");
		build.append("SUM(DIGI.SGST_AMT)ASP_SGST_AMT,");
		build.append("SUM(DIGI.CESS_AMT)ASP_CESS_AMT,");
		build.append("'' as PROFIT_CENTRE,'' as PLANT_CODE,'' as DIVISION,");
		build.append("'' as LOCATION,'' as SALES_ORGANIZATION, '' as DISTRIBUTION_CHANNEL,");
		build.append("'' as USERACCESS1,'' as USERACCESS2,'' as USERACCESS3, '' as USERACCESS4,");
		build.append("'' as USERACCESS5,'' as USERACCESS6,	USERDEFINED_FIELD1,");
		build.append("USERDEFINED_FIELD2,USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP,");
		build.append("INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,");
		build.append("GSTIN_REF_ID ,");
		build.append("TO_CHAR(ADD_SECONDS(GSTIN_REF_ID_TIME,19800),'DD-MM-YYYY HH24:MI:SS') ");
		build.append("AS GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,");
		build.append("SOURCE_ID,FILE_NAME, ASP_DATE_TIME,IFNULL(STATE_CODE,'') ");
		build.append("from GSTN FULL OUTER JOIN DIGI on GSTN.DOC_KEY=  DIGI.DOC_KEY ");
		build.append("GROUP BY  IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN),");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD) ,");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD) ,");
		build.append("TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,	NEW_POS,NEW_RATE,USERDEFINED_FIELD1,");
		build.append("USERDEFINED_FIELD2,USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP,");
		build.append("INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,GSTIN_REF_ID ,");
		build.append("GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,");
		build.append("SOURCE_ID,FILE_NAME, ASP_DATE_TIME,SUPPLY_TYPE, STATE_CODE,");
		build.append("STATE_NAME,POS,DIFF_PERCENT, IS_FILED ");
*/			 
		return build.toString();

		
		/*
		
		
		return "SELECT SUPPLIER_GSTIN, RETURN_PERIOD, DERIVED_RET_PERIOD,"
				+ "ORG_MONTH,SUPPLY_TYPE,ORG_SUPPLY_TYPE, STATE_NAME, POS,"
				+ " GSTN_ORG_POS, TAX_RATE,SUM(TAXABLE_VALUE) GROSS_ADV_RECEIVED,"
				+ "SUM(GSTN_IGST_AMT)GSTN_IGST_AMT,SUM(GSTN_CGST_AMT)GSTN_CGST_AMT,"
				+ "SUM(GSTN_SGST_AMT)GSTN_SGST_AMT,SUM(GSTN_CESS_AMT)GSTN_CESS_AMT,"
				+ "DIFF_PERCENT,IS_FILED,TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,"
				+ "SUM(ORG_TAXABLE_VALUE)ORG_GROSS_ADV_RECEIVED,"
				+ "NEW_POS,NEW_RATE,SUM(NEW_TAXABLE_VALUE)NEW_GROSS_ADV_RECEIVED,"
				+ "SUM(ASP_IGST_AMT)ASP_IGST_AMT,SUM(ASP_CGST_AMT)ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT)ASP_SGST_AMT,SUM(ASP_CESS_AMT)ASP_CESS_AMT,"
				+ "PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION, SALES_ORGANIZATION,"
				+ " DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
				+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP, "
				+ "INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,GSTIN_REF_ID ,"
				+ " GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION, "
				+ "SOURCE_ID,FILE_NAME, ASP_DATE_TIME,"
				+ "ROW_NUMBER () OVER (ORDER BY SUPPLIER_GSTIN) SNO , STATE_CODE "
				+ "FROM"
				+ " (select SUPPLIER_GSTIN, RETURN_PERIOD, DERIVED_RET_PERIOD,"
				+ "ORG_MONTH,SUPPLY_TYPE,ORG_SUPPLY_TYPE,STATE_CODE,STATE_NAME,"
				+ "POS,GSTN_ORG_POS,TAX_RATE,TAXABLE_VALUE,GSTN_IGST_AMT,"
				+ "GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT,DIFF_PERCENT,IS_FILED,"
				+ "NULL TRAN_TYPE,NULL MONTH,NULL ORG_POS, NULL ORG_RATE,"
				+ "0 ORG_TAXABLE_VALUE,NULL NEW_POS,NULL NEW_RATE,0 NEW_TAXABLE_VALUE,"
				+ "0 ASP_IGST_AMT,0 ASP_CGST_AMT,0 ASP_SGST_AMT,0 ASP_CESS_AMT,"
				+ "NULL PROFIT_CENTRE,NULL PLANT_CODE,NULL DIVISION, NULL LOCATION, "
				+ "NULL SALES_ORGANIZATION, NULL DISTRIBUTION_CHANNEL,"
				+ "NULL USERACCESS1,NULL USERACCESS2,NULL USERACCESS3,"
				+ "NULL USERACCESS4,NULL USERACCESS5,NULL USERACCESS6,"
				+ "NULL USERDEFINED_FIELD1,NULL USERDEFINED_FIELD2,"
				+ "NULL USERDEFINED_FIELD3,NULL INFO_ERROR_CODE_ASP,"
				+ " NULL INFO_ERROR_DESCRIPTION_ASP,NULL SAVE_STATUS,"
				+ "NULL GSTIN_REF_ID , NULL GSTIN_REF_ID_TIME, NULL GSTN_ERROR_CODE , "
				+ "NULL GSTN_ERROR_DESCRIPTION,NULL SOURCE_ID,NULL FILE_NAME, "
				+ "NULL ASP_DATE_TIME from (select SUPPLIER_GSTIN, RETURN_PERIOD, "
				+ "DERIVED_RET_PERIOD,ORG_MONTH,SUPPLY_TYPE, ORG_SUPPLY_TYPE,"
				+ " STATE_NAME,POS, ORG_POS AS GSTN_ORG_POS,TAX_RATE,TAXABLE_VALUE,"
				+ "IGST_AMT GSTN_IGST_AMT,CGST_AMT GSTN_CGST_AMT,"
				+ "SGST_AMT GSTN_SGST_AMT,CESS_AMT GSTN_CESS_AMT,DIFF_PERCENT,"
				+ "IS_FILED,TABLE_TYPE,STATE_CODE from ("
				+ " SELECT HDR.GSTIN  SUPPLIER_GSTIN,HDR.RETURN_PERIOD  RETURN_PERIOD,"
				+ "HDR.DERIVED_RET_PERIOD  DERIVED_RET_PERIOD,ORG_MONTH,''SUPPLY_TYPE,"
				+ "SUPPLY_TYPE ORG_SUPPLY_TYPE,SUBSTR(HDR.GSTIN,1,2) AS STATE_CODE,"
				+ "'' STATE_NAME,'' POS,POS ORG_POS,ITM.TAX_RATE,"
				+ "SUM(ITM.ADVREC_AMT) TAXABLE_VALUE,SUM(ITM.IGST_AMT) IGST_AMT,"
				+ " SUM(ITM.CGST_AMT) CGST_AMT,SUM(ITM.SGST_AMT) SGST_AMT,"
				+ "SUM(ITM.CESS_AMT) CESS_AMT,HDR.DIFF_PERCENT,BT.IS_FILED,"
				+ " 'ADV REC-A' AS TABLE_TYPE  FROM GETGSTR1_ATA_HEADER HDR "
				+ "INNER JOIN GETGSTR1_ATA_ITEM ITM  ON HDR.ID = ITM.HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ " LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ " WHERE HDR.IS_DELETE=FALSE AND BT.IS_DELETE=FALSE "
				+ "GROUP BY  HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,"
				+ "SUPPLY_TYPE, POS,ITM.TAX_RATE,HDR.DIFF_PERCENT,BT.IS_FILED,ORG_MONTH)) "
				+ "UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,"
				+ "NULL ORG_MONTH,NULL SUPPLY_TYPE,NULL ORG_SUPPLY_TYPE,"
				+ "NULL STATE_CODE,NULL STATE_NAME,NULL POS,NULL ORG_POS, "
				+ "NULL TAX_RATE,0 TAXABLE_VALUE,0 GSTN_IGST_AMT,0 GSTN_CGST_AMT,"
				+ "0 GSTN_SGST_AMT,0 GSTN_CESS_AMT,NULL DIFF_PERCENT,NULL IS_FILED,"
				+ "TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,ORG_TAXABLE_VALUE,"
				+ "NEW_POS,NEW_RATE,NEW_TAXABLE_VALUE,"
				+ "IGST_AMT AS ASP_IGST_AMT,CGST_AMT ASP_CGST_AMT,SGST_AMT ASP_SGST_AMT,"
				+ "CESS_AMT ASP_CESS_AMT,PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION, "
				+ "SALES_ORGANIZATION, DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
				+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
				+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP,"
				+ " INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,GSTIN_REF_ID , "
				+ "GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,"
				+ "SOURCE_ID,FILE_NAME, ASP_DATE_TIME from (SELECT SUPPLIER_GSTIN, "
				+ "RETURN_PERIOD, DERIVED_RET_PERIOD, "
				+ "(CASE  WHEN TRAN_TYPE IN ('ZL65','L65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL) THEN 'N' END) "
				+ " TRAN_TYPE,MONTH, MAX(ORG_POS)ORG_POS, MAX(ORG_RATE)ORG_RATE,"
				+ "MAX(ORG_GROSS_ADV_RECEIVED)ORG_TAXABLE_VALUE,NEW_POS,"
				+ "MAX(NEW_RATE)NEW_RATE,SUM(NEW_GROSS_ADV_RECEIVED)NEW_TAXABLE_VALUE,"
				+ " SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT, SUM(SGST_AMT)SGST_AMT,"
				+ " SUM(CESS_AMT)CESS_AMT, MAX(PROFIT_CENTRE)PROFIT_CENTRE,"
				+ "MAX(PLANT_CODE)PLANT_CODE,MAX(DIVISION)DIVISION, MAX(LOCATION)LOCATION,"
				+ "MAX(SALES_ORGANIZATION)SALES_ORGANIZATION,"
				+ " MAX(DISTRIBUTION_CHANNEL)DISTRIBUTION_CHANNEL,"
				+ " MAX(USERACCESS1)USERACCESS1, MAX(USERACCESS2)USERACCESS2,"
				+ "MAX(USERACCESS3)USERACCESS3,MAX(USERACCESS4)USERACCESS4,"
				+ " MAX(USERACCESS5)USERACCESS5,MAX(USERACCESS6)USERACCESS6,"
				+ " MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,"
				+ "MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,"
				+ " MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,"
				+ "MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP, "
				+ "MAX(INFO_ERROR_DESCRIPTION_ASP)INFO_ERROR_DESCRIPTION_ASP,"
				+ "MAX(SAVE_STATUS)SAVE_STATUS,MAX(GSTIN_REF_ID)GSTIN_REF_ID, "
				+ "MAX(GSTIN_REF_ID_TIME)GSTIN_REF_ID_TIME,MAX(GSTN_ERROR_CODE)GSTN_ERROR_CODE,"
				+ "MAX(GSTN_ERROR_DESCRIPTION)GSTN_ERROR_DESCRIPTION,"
				+ "MAX(SOURCE_ID)SOURCE_ID, MAX(FILE_NAME)FILE_NAME,"
				+ "MAX(ASP_DATE_TIME)ASP_DATE_TIME "
				+ "FROM ( SELECT REC.SUPPLIER_GSTIN,REC.RETURN_PERIOD,"
				+ "REC.DERIVED_RET_PERIOD, TRAN_TYPE,"
				+ "MONTH,NULL ORG_POS,NULL ORG_RATE, ORG_GROSS_ADV_RECEIVED,"
				+ "NEW_POS,NULL NEW_RATE, NEW_GROSS_ADV_RECEIVED,IGST_AMT,CGST_AMT,"
				+ "SGST_AMT,CESS_AMT, '' PROFIT_CENTRE,''PLANT_CODE,"
				+ "'' DIVISION,''LOCATION, ''SALES_ORGANIZATION,''DISTRIBUTION_CHANNEL,"
				+ "''USERACCESS1, ''USERACCESS2,''USERACCESS3,''USERACCESS4,"
				+ "''USERACCESS5,''USERACCESS6, ''USERDEFINED_FIELD1,''USERDEFINED_FIELD2,"
				+ "''USERDEFINED_FIELD3, '' INFO_ERROR_CODE_ASP, '' INFO_ERROR_DESCRIPTION_ASP,"
				+ "(CASE WHEN REC.IS_SAVED_TO_GSTN = TRUE AND REC.IS_DELETE = FALSE THEN 'IS_SAVED' "
				+ "ELSE 'NOT_SAVED' END) AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,"
				+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME,"
				+ "'' AS GSTN_ERROR_CODE, '' AS GSTN_ERROR_DESCRIPTION,"
				+ "''SOURCE_ID, '' FILE_NAME, '' ASP_DATE_TIME "
				+ "FROM GSTR1_PROCESSED_ADV_RECEIVED REC LEFT OUTER JOIN "
				+ "TF_GSTR1_ERROR_INFO() GSTRERR ON REC.AS_ENTERED_ID= GSTRERR.COMMON_ID "
				+ "AND REC.FILE_ID=GSTRERR.FILE_ID AND GSTRERR.TABLE_TYPE='ADV REC-A' "
				+ "AND RETURN_TYPE='GSTR1' LEFT OUTER JOIN FILE_STATUS FIL "
				+ "ON REC.FILE_ID=FIL.ID AND GSTRERR.FILE_ID=FIL.ID "
				+ "AND GSTRERR.INV_KEY=REC.AT_INVKEY LEFT OUTER JOIN "
				+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = REC.BATCH_ID "
				+ "WHERE REC.IS_DELETE=FALSE AND IS_AMENDMENT=TRUE) "
				+ "  GROUP BY  SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,(CASE WHEN "
				+ "TRAN_TYPE IN ('ZL65','L65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL) "
				+ "THEN 'N' END),NEW_POS,MONTH ))  " 
				+ buildHeader
				+ "  GROUP BY  SUPPLIER_GSTIN, RETURN_PERIOD, "
				+ "DERIVED_RET_PERIOD,ORG_MONTH,SUPPLY_TYPE,ORG_SUPPLY_TYPE,"
				+ "STATE_CODE, STATE_NAME, POS, GSTN_ORG_POS, TAX_RATE,DIFF_PERCENT,IS_FILED,"
				+ "TRAN_TYPE,MONTH,ORG_POS,ORG_RATE, NEW_POS,NEW_RATE,"
				+ "PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION, SALES_ORGANIZATION,"
				+ " DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,USERACCESS3,"
				+ "USERACCESS4,USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
				+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP, "
				+ "INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,GSTIN_REF_ID , "
				+ "GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,"
				+ "SOURCE_ID,FILE_NAME, ASP_DATE_TIME";
*/	
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
