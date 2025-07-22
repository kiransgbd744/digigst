package com.ey.advisory.app.services.reports.gstr1a;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import com.google.common.base.Strings;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr1AAspProcessVsSubmitB2csaReportDaoImpl")
public class Gstr1AAspProcessVsSubmitB2csaReportDaoImpl
		implements Gstr1AAspProcessVsSubmitDao {

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

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" WHERE SUPPLIER_GSTIN IN :gstinList");

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
		obj.setGstnOrgRetPeriod(arr[3] != null ? arr[3].toString() : null);
		obj.setGstnSuppType(arr[4] != null ? arr[4].toString() : null);
		// obj.setGstnStateName(arr[5] != null ? arr[5].toString() : null);
		obj.setGstnPos(arr[6] != null ? arr[6].toString() : null);
		obj.setGstnOrgPos(arr[7] != null ? arr[7].toString() : null);
		obj.setGstnRateTax(arr[8] != null ? arr[8].toString() : null);
		obj.setGstnTaxvalue(arr[9] != null ? arr[9].toString() : null);
		obj.setGstnIgst(arr[10] != null ? arr[10].toString() : null);
		obj.setGstnCgst(arr[11] != null ? arr[11].toString() : null);
		obj.setGstnSgst(arr[12] != null ? arr[12].toString() : null);
		obj.setGstnCess(arr[13] != null ? arr[13].toString() : null);
		obj.setGstnDiffPerRate(arr[14] != null ? arr[14].toString() : null);
		obj.setGstnType(arr[15] != null ? arr[15].toString() : null);
		obj.setGstnEcomGstin(arr[16] != null ? arr[16].toString() : null);

		//obj.setGstnIsFiled(arr[17] != null ? arr[17].toString() : null);
		obj.setGstnIsFiled(isGstinTaxperiodFiled(context,arr[0].toString(), arr[1].toString()));
		obj.setDigiGstTransType(arr[18] != null ? arr[18].toString() : null);
		obj.setDigiGstMonth(arr[19] != null ? arr[19].toString() : null);
		obj.setDigiGstOrgPos(arr[20] != null ? arr[20].toString() : null);
		obj.setDigiGstOrgHsn(arr[21] != null ? arr[21].toString() : null);
		obj.setDigiGstOrgUom(arr[22] != null ? arr[22].toString() : null);
		obj.setDigiGstOrgQnt(arr[23] != null ? arr[23].toString() : null);
		obj.setDigiGstOrgRate(arr[24] != null ? arr[24].toString() : null);
		obj.setDigiGstOrgTaxbleValue(
				arr[25] != null ? arr[25].toString() : null);
		obj.setDigiGstOrgEcomGstin(arr[26] != null ? arr[26].toString() : null);
		obj.setDigiGstOrgEcomSupValue(
				arr[27] != null ? arr[27].toString() : null);
		obj.setDigiGstNewPos(arr[28] != null ? arr[28].toString() : null);
		obj.setDigiGstOrgNewHsn(arr[29] != null ? arr[29].toString() : null);
		obj.setDigiGstOrgNewUom(arr[30] != null ? arr[30].toString() : null);

		obj.setDigiGstOrgNewQnt(arr[31] != null ? arr[31].toString() : null);
		obj.setDigiGstOrgNewRate(arr[32] != null ? arr[32].toString() : null);
		obj.setDigiGstOrgNewTaxbleValue(
				arr[33] != null ? arr[33].toString() : null);
		obj.setDigiGstOrgNewEcomGstin(
				arr[34] != null ? arr[34].toString() : null);
		obj.setDigiGstOrgNewEcomSupValue(
				arr[35] != null ? arr[35].toString() : null);
		obj.setDigiGstIgst(arr[36] != null ? arr[36].toString() : null);
		obj.setDigiGstCgst(arr[37] != null ? arr[37].toString() : null);
		obj.setDigiGstSgst(arr[38] != null ? arr[38].toString() : null);
		obj.setDigiGstCess(arr[39] != null ? arr[39].toString() : null);
		obj.setDigiGstTotValue(arr[40] != null ? arr[40].toString() : null);
		obj.setDigiGstProfitCenter(arr[41] != null ? arr[41].toString() : null);
		obj.setDigiGstPlant(arr[42] != null ? arr[42].toString() : null);
		obj.setDigiGstDivision(arr[43] != null ? arr[43].toString() : null);
		obj.setDigiGstLocation(arr[44] != null ? arr[44].toString() : null);
		obj.setDigiGstSalesOrg(arr[45] != null ? arr[45].toString() : null);

		obj.setDigiGstDistChannel(arr[46] != null ? arr[46].toString() : null);
		obj.setDigiGstUserAcc1(arr[47] != null ? arr[47].toString() : null);
		obj.setDigiGstUserAcc2(arr[48] != null ? arr[48].toString() : null);
		obj.setDigiGstUserAcc3(arr[49] != null ? arr[49].toString() : null);
		obj.setDigiGstUserAcc4(arr[50] != null ? arr[50].toString() : null);
		obj.setDigiGstUserAcc5(arr[51] != null ? arr[51].toString() : null);
		obj.setDigiGstUserAcc6(arr[52] != null ? arr[52].toString() : null);
		obj.setDigiGstUserDef1(arr[53] != null ? arr[53].toString() : null);
		obj.setDigiGstUserDef2(arr[54] != null ? arr[54].toString() : null);
		obj.setDigiGstUserDef3(arr[55] != null ? arr[55].toString() : null);
		obj.setDigiGstAspInfo(arr[56] != null ? arr[56].toString() : null);
		obj.setDigiGstAspInfoDescription(
				arr[57] != null ? arr[57].toString() : null);
		obj.setDigiGstSaveStatus(arr[58] != null ? arr[58].toString() : null);
		obj.setDigiGstGstnRefId(arr[59] != null ? arr[59].toString() : null);

		obj.setDigiGstGstnRefTime(arr[60] != null ? arr[60].toString() : null);
		obj.setDigiGstGstnErrorCode(
				arr[61] != null ? arr[61].toString() : null);
		obj.setDigiGstGstnErrorDes(arr[62] != null ? arr[62].toString() : null);
		obj.setDigiGstSourceId(arr[63] != null ? arr[63].toString() : null);
		obj.setDigiGstFileName(arr[64] != null ? arr[64].toString() : null);
		obj.setDigiGstAspDate(arr[65] != null ? arr[65].toString() : null);
		// obj.setGstnStateCode(arr[66] != null ? arr[66].toString() : null);
		String stateName = null;
		String stateCode = (arr[66] != null ? arr[66].toString() : null);
		obj.setGstnStateCode(stateCode);

		if (!Strings.isNullOrEmpty(stateCode)) {
			stateName = stateCode + "-" + hsnMap.get(stateCode);
		}
		obj.setGstnStateName(stateName);
		obj.setGstnSno(arr[67] != null ? arr[67].toString() : null);
		obj.setDiffTaxableValue(arr[68] != null ? arr[68].toString() : null);
		obj.setDiffIgst(arr[69] != null ? arr[69].toString() : null);
		obj.setDiffCgst(arr[70] != null ? arr[70].toString() : null);
		obj.setDiffSgst(arr[71] != null ? arr[71].toString() : null);
		obj.setDiffCess(arr[72] != null ? arr[72].toString() : null);
		return obj;
	}

	private String createApiProcessedQueryString(String buildquery) {
		StringBuilder build = new StringBuilder();
		build.append(
				"SELECT SUPPLIER_GSTIN, RETURN_PERIOD, DERIVED_RET_PERIOD, ORGRET_PERIOD,");
		build.append("SUPPLY_TYPE,STATE_NAME,POS,GSTN_ORG_POS,TAX_RATE,TAXABLE_VALUE,");
		build.append("GSTN_IGST_AMT,GSTN_CGST_AMT,GSTN_SGST_AMT,GSTN_CESS_AMT,DIFF_PERCENT,");
		build.append("ECOM_TYPE,ECOM_GSTIN,IS_FILED,TRAN_TYPE,MONTH,ORG_POS,ORG_HSNORSAC,");
		build.append("ORG_UOM,ORG_QNT,ORG_RATE,ORG_TAXABLE_VALUE,ORG_ECOM_GSTIN,");
		build.append("ORG_ECOM_SUP_VAL,NEW_POS,NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,");
		build.append("NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN,NEW_ECOM_SUP_VAL,ASP_IGST_AMT,");
		build.append("ASP_CGST_AMT,ASP_SGST_AMT,ASP_CESS_AMT,TOT_VAL,PROFIT_CENTRE,");
		build.append("PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,");
		build.append("USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,");
		build.append("USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3,");
		build.append("INFO_ERROR_CODE_ASP,INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,");
		build.append("GSTIN_REF_ID,GSTIN_REF_ID_TIME,GSTN_ERROR_CODE,GSTN_ERROR_DESCRIPTION,");
		build.append("SOURCE_ID,FILE_NAME,ASP_DATE_TIME,STATE_CODE,SNO,");
		build.append("(IFNULL(NEW_TAXABLE_VALUE,0) -IFNULL(TAXABLE_VALUE,0)) as DIFF_TAXABLE_VALUE,");
		build.append("(IFNULL(ASP_IGST_AMT,0) -IFNULL(GSTN_IGST_AMT,0)) as DIFF_IGST_AMT,");
		build.append("(IFNULL(ASP_CGST_AMT,0) -IFNULL(GSTN_CGST_AMT,0)) as DIFF_CGST_AMT,");
		build.append("(IFNULL(ASP_SGST_AMT,0) -IFNULL(GSTN_SGST_AMT,0)) as DIFF_SGST_AMT,");
		build.append("(IFNULL(ASP_CESS_AMT,0) -IFNULL(GSTN_CESS_AMT,0)) as DIFF_CESS_AMT FROM ( ");
		build.append(
				" SELECT SUPPLIER_GSTIN, RETURN_PERIOD, DERIVED_RET_PERIOD,");
		build.append(
				" ORGRET_PERIOD,SUPPLY_TYPE, STATE_NAME, POS,GSTN_ORG_POS, ");
		build.append(" TAX_RATE, SUM(TAXABLE_VALUE)TAXABLE_VALUE,");
		build.append("SUM(GSTN_IGST_AMT)GSTN_IGST_AMT,");
		build.append("SUM(GSTN_CGST_AMT)GSTN_CGST_AMT,");
		build.append("SUM(GSTN_SGST_AMT)GSTN_SGST_AMT,");
		build.append("SUM(GSTN_CESS_AMT)GSTN_CESS_AMT,");
		build.append(" DIFF_PERCENT,  ECOM_TYPE,  ECOM_GSTIN,");
		build.append("  IS_FILED,TRAN_TYPE,MONTH, ORG_POS,ORG_HSNORSAC,");
		build.append("ORG_UOM,SUM(ORG_QNT)ORG_QNT,ORG_RATE,");
		build.append("SUM(ORG_TAXABLE_VALUE)ORG_TAXABLE_VALUE,");
		build.append("ORG_ECOM_GSTIN,SUM(ORG_ECOM_SUP_VAL)ORG_ECOM_SUP_VAL,");
		build.append(
				" NEW_POS,NEW_HSNORSAC,'' as NEW_UOM,'' as NEW_QNT,NEW_RATE,");
		build.append("SUM(NEW_TAXABLE_VALUE)NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN,");
		build.append("SUM(NEW_ECOM_SUP_VAL)NEW_ECOM_SUP_VAL, ");
		build.append(
				"SUM(ASP_IGST_AMT)ASP_IGST_AMT,SUM(ASP_CGST_AMT)ASP_CGST_AMT,");
		build.append(
				"SUM(ASP_SGST_AMT)ASP_SGST_AMT,SUM(ASP_CESS_AMT)ASP_CESS_AMT,");
		build.append(
				"SUM(TOT_VAL)TOT_VAL, PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION,");
		build.append(
				" SALES_ORGANIZATION, DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,");
		build.append(
				"USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6, USERDEFINED_FIELD1,");
		build.append(
				"USERDEFINED_FIELD2,USERDEFINED_FIELD3, INFO_ERROR_CODE_ASP, ");
		build.append("INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS, GSTIN_REF_ID ,");
		build.append(
				" GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,");
		build.append(" SOURCE_ID,FILE_NAME, ASP_DATE_TIME,STATE_CODE,");
		build.append("ROW_NUMBER () OVER (ORDER BY SUPPLIER_GSTIN) SNO  FROM ");
		build.append("(select ");
		build.append(
				" SUPPLIER_GSTIN, RETURN_PERIOD, DERIVED_RET_PERIOD, ORGRET_PERIOD,");
		build.append(
				"SUPPLY_TYPE,STATE_CODE,STATE_NAME,POS,GSTN_ORG_POS,TAX_RATE,");
		build.append(
				"TAXABLE_VALUE, GSTN_IGST_AMT,GSTN_CGST_AMT,GSTN_SGST_AMT,");
		build.append(
				"GSTN_CESS_AMT, DIFF_PERCENT,ECOM_TYPE,ECOM_GSTIN,IS_FILED, ");
		build.append(
				"NULL TRAN_TYPE,NULL MONTH, NULL ORG_POS,NULL ORG_HSNORSAC,");
		build.append(
				"NULL ORG_UOM,NULL ORG_QNT,NULL ORG_RATE,0 ORG_TAXABLE_VALUE,");
		build.append("NULL ORG_ECOM_GSTIN,0 ORG_ECOM_SUP_VAL, NULL NEW_POS,");
		build.append(
				"NULL NEW_HSNORSAC,NULL NEW_UOM,NULL NEW_QNT,NULL NEW_RATE,");
		build.append(
				"0 NEW_TAXABLE_VALUE,NULL NEW_ECOM_GSTIN,0 NEW_ECOM_SUP_VAL,");
		build.append(
				" 0 ASP_IGST_AMT,0 ASP_CGST_AMT,0 ASP_SGST_AMT,0 ASP_CESS_AMT,");
		build.append(
				"0 TOT_VAL, NULL PROFIT_CENTRE,NULL PLANT_CODE,NULL DIVISION,");
		build.append(
				" NULL LOCATION, NULL SALES_ORGANIZATION, NULL DISTRIBUTION_CHANNEL,");
		build.append(
				" NULL USERACCESS1,NULL USERACCESS2,NULL USERACCESS3,NULL USERACCESS4,");
		build.append(
				"NULL USERACCESS5,NULL USERACCESS6, NULL USERDEFINED_FIELD1,");
		build.append("NULL USERDEFINED_FIELD2,NULL USERDEFINED_FIELD3, ");
		build.append(
				"NULL INFO_ERROR_CODE_ASP, NULL INFO_ERROR_DESCRIPTION_ASP,");
		build.append(
				"NULL SAVE_STATUS, NULL GSTIN_REF_ID , NULL GSTIN_REF_ID_TIME, ");
		build.append("NULL GSTN_ERROR_CODE , NULL GSTN_ERROR_DESCRIPTION, ");
		build.append(
				"NULL SOURCE_ID,NULL FILE_NAME, NULL ASP_DATE_TIME  from ");
		build.append("(SELECT  HDR.GSTIN  SUPPLIER_GSTIN,HDR.RETURN_PERIOD ");
		build.append(
				"RETURN_PERIOD,HDR.DERIVED_RET_PERIOD  DERIVED_RET_PERIOD,");
		build.append("HDR.ORG_INV_MONTH AS ORGRET_PERIOD, ");
		build.append("SUPPLY_TYPE,SUBSTR(HDR.GSTIN,1,2) AS STATE_CODE, ");
		build.append("'' STATE_NAME,POS,ORG_POS AS GSTN_ORG_POS,ITM.TAX_RATE,");
		build.append(" SUM(ITM.TAXABLE_VALUE) TAXABLE_VALUE, ");
		build.append(
				"SUM(ITM.IGST_AMT) GSTN_IGST_AMT,SUM(ITM.CGST_AMT) GSTN_CGST_AMT,");
		build.append(
				"SUM(ITM.SGST_AMT) GSTN_SGST_AMT,SUM(ITM.CESS_AMT) GSTN_CESS_AMT, ");
		build.append(
				"HDR.DIFF_PERCENT,HDR.ECOM_TYPE,HDR.ETIN ECOM_GSTIN, BT.IS_FILED,");
		build.append(
				"'B2CSA' TABLE_TYPE FROM GETGSTR1A_B2CSA_HEADER HDR INNER ");
		build.append("JOIN GETGSTR1A_B2CSA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		build.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT ");
		build.append(
				"JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID WHERE ");
		build.append(
				"HDR.IS_DELETE=FALSE AND BT.IS_DELETE=FALSE GROUP BY HDR.GSTIN,");
		build.append(
				"HDR.RETURN_PERIOD,ORG_INV_MONTH , SUPPLY_TYPE,ORG_POS,POS,");
		build.append("ITM.TAX_RATE,HDR.DIFF_PERCENT,");
		build.append(
				" HDR.ECOM_TYPE,HDR.ETIN,BT.IS_FILED,HDR.DERIVED_RET_PERIOD) UNION ALL");

		build.append(
				" SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
		build.append("NULL ORGRET_PERIOD,NULL SUPPLY_TYPE,NULL STATE_CODE,");
		build.append(
				"NULL STATE_NAME,NULL POS,NULL GSTN_ORG_POS, NULL TAX_RATE, ");
		build.append(
				"0 TAXABLE_VALUE,0 GSTN_IGST_AMT,0 GSTN_CGST_AMT,0 GSTN_SGST_AMT,");
		build.append(
				"0 GSTN_CESS_AMT, NULL DIFF_PERCENT,NULL ECOM_TYPE,NULL ECOM_GSTIN,");
		build.append(
				"NULL IS_FILED,TRAN_TYPE,MONTH, ORG_POS,ORG_HSNORSAC,ORG_UOM,");
		build.append(
				"ORG_QNT,ORG_RATE,ORG_TAXABLE_VALUE,ORG_ECOM_GSTIN,ORG_ECOM_SUP_VAL,");
		build.append(
				" NEW_POS,NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,NEW_TAXABLE_VALUE,");
		build.append(
				"NEW_ECOM_GSTIN,NEW_ECOM_SUP_VAL, IGST_AMT AS ASP_IGST_AMT,");
		build.append(
				"CGST_AMT ASP_CGST_AMT,SGST_AMT ASP_SGST_AMT,CESS_AMT ASP_CESS_AMT,");
		build.append(
				"TOT_VAL, PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION, SALES_ORGANIZATION,");
		build.append(
				" DISTRIBUTION_CHANNEL, USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,");
		build.append(
				"USERACCESS5,USERACCESS6, USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3,");
		build.append(
				" INFO_ERROR_CODE_ASP, INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS, GSTIN_REF_ID ,");
		build.append(
				" GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION, SOURCE_ID,FILE_NAME, ");
		build.append(
				"ASP_DATE_TIME  from ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
		build.append(
				" TRAN_TYPE,MONTH, MAX(ORG_POS)ORG_POS,MAX(ORG_HSNORSAC)ORG_HSNORSAC,");
		build.append(
				"MAX(ORG_UOM)ORG_UOM, MAX(ORG_QNT)ORG_QNT,MAX(ORG_RATE)ORG_RATE,");
		build.append(
				"SUM(ORG_TAXABLE_VALUE)ORG_TAXABLE_VALUE,MAX(ORG_ECOM_GSTIN)ORG_ECOM_GSTIN,");
		build.append("SUM(ORG_ECOM_SUP_VAL)ORG_ECOM_SUP_VAL,NEW_POS,");
		build.append("MAX(NEW_HSNORSAC)NEW_HSNORSAC,MAX(NEW_UOM)NEW_UOM,");
		build.append("MAX(NEW_QNT)NEW_QNT,MAX(NEW_RATE)NEW_RATE,");
		build.append("IFNULL(SUM(NEW_TAXABLE_VALUE),0) AS NEW_TAXABLE_VALUE,");
		build.append(
				" NEW_ECOM_GSTIN, SUM(NEW_ECOM_SUP_VAL) NEW_ECOM_SUP_VAL,");
		build.append(
				" IFNULL(SUM(IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(CGST_AMT),0)");
		build.append(
				" AS CGST_AMT, IFNULL(SUM(SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(CESS_AMT),0) AS CESS_AMT,");
		build.append(
				" IFNULL(SUM(TOT_VAL),0) AS TOT_VAL, MAX(PROFIT_CENTRE)PROFIT_CENTRE, ");
		build.append(
				" MAX(PLANT_CODE)PLANT_CODE, MAX(DIVISION)DIVISION, MAX(LOCATION)LOCATION,");
		build.append(
				" MAX(SALES_ORGANIZATION)SALES_ORGANIZATION, MAX(DISTRIBUTION_CHANNEL)DISTRIBUTION_CHANNEL,");
		build.append(
				"MAX(USERACCESS1)USERACCESS1,MAX(USERACCESS2)USERACCESS2,");
		build.append(
				"MAX(USERACCESS3)USERACCESS3,MAX(USERACCESS4)USERACCESS4,");
		build.append(
				" MAX(USERACCESS5)USERACCESS5,MAX(USERACCESS6)USERACCESS6, ");
		build.append(" MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1, ");
		build.append(" MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,");
		build.append("MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,");
		build.append(" MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP, ");
		build.append(
				"  MAX(INFO_ERROR_DESCRIPTION_ASP)INFO_ERROR_DESCRIPTION_ASP, ");
		build.append(
				"MAX(SAVE_STATUS)SAVE_STATUS,MAX(GSTIN_REF_ID)GSTIN_REF_ID , ");
		build.append(" MAX(GSTIN_REF_ID_TIME )GSTIN_REF_ID_TIME,");
		build.append("MAX(GSTN_ERROR_CODE)GSTN_ERROR_CODE ,");
		build.append("MAX(GSTN_ERROR_DESCRIPTION)GSTN_ERROR_DESCRIPTION,");
		build.append("MAX(SOURCE_ID)SOURCE_ID,MAX(FILE_NAME)FILE_NAME,");
		build.append("MAX(ASP_DATE_TIME)ASP_DATE_TIME ");
		build.append("  FROM ( SELECT (IFNULL(B2CS.SUPPLIER_GSTIN,'') ||'|'||");
		build.append(
				" IFNULL(B2CS.RETURN_PERIOD,'')  ||'|'||IFNULL(TRAN_TYPE,'') ||'|'||IFNULL(NEW_POS,9999)||'|'||IFNULL(NEW_ECOM_GSTIN,'')||'|'||IFNULL(MONTH,9999) ) DOC_KEY, B2CS.SUPPLIER_GSTIN,");
		build.append(
				"B2CS.RETURN_PERIOD, B2CS.DERIVED_RET_PERIOD,TRAN_TYPE,MONTH,NULL AS ORG_POS,0 ORG_HSNORSAC,''ORG_UOM, 0 ORG_QNT,NULL AS ORG_RATE, ORG_TAXABLE_VALUE,NULL AS ORG_ECOM_GSTIN,");
		build.append(
				" ORG_ECOM_SUP_VAL,NEW_POS, 0 NEW_HSNORSAC,'' NEW_UOM,0 NEW_QNT,NULL AS NEW_RATE,NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN, NEW_ECOM_SUP_VAL,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT, ");
		build.append(
				"IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0) AS TOT_VAL, '' PROFIT_CENTRE,");
		build.append(
				"''PLANT_CODE,''DIVISION, ''LOCATION,''SALES_ORGANIZATION,''DISTRIBUTION_CHANNEL, ''USERACCESS1,''USERACCESS2,''USERACCESS3,''USERACCESS4, ''USERACCESS5,''USERACCESS6,");
		build.append(
				"''USERDEFINED_FIELD1,''USERDEFINED_FIELD2, ''USERDEFINED_FIELD3, ''INFO_ERROR_CODE_ASP,''INFO_ERROR_DESCRIPTION_ASP, (CASE WHEN B2CS.IS_SAVED_TO_GSTN = TRUE AND B2CS.IS_DELETE = FALSE ");
		build.append(
				" THEN 'IS_SAVED'  ELSE 'NOT_SAVED' END)  AS SAVE_STATUS,GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,GSTNBATCH.GSTN_RESP_DATE AS GSTIN_REF_ID_TIME,");
		build.append(
				" '' AS GSTN_ERROR_CODE,'' AS GSTN_ERROR_DESCRIPTION, '' SOURCE_ID,'' FILE_NAME, '' ASP_DATE_TIME FROM GSTR1A_PROCESSED_B2CS B2CS");
		build.append(
				" LEFT OUTER JOIN (SELECT COMMON_ID,VAL_TYPE,FILE_ID,INV_KEY, TABLE_TYPE, STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP,");
		build.append(
				"STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP  FROM (SELECT COMMON_ID,VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE,");
		build.append(
				" CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END  AS INFO_ERROR_CODE_ASP, CASE WHEN ERROR_TYPE='INFO'  THEN ERROR_DESCRIPTION END AS INFO_ERROR_DESCRIPTION_ASP ");
		build.append(
				" FROM ANX_VERTICAL_ERROR_1A) GROUP BY COMMON_ID, VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE) ");
		build.append(
				"GSTRERR ON B2CS.AS_ENTERED_ID= GSTRERR.COMMON_ID AND  B2CS.FILE_ID=GSTRERR.FILE_ID AND GSTRERR.TABLE_TYPE='B2CS'  AND RETURN_TYPE='GSTR1A' LEFT OUTER JOIN ");
		build.append(
				" FILE_STATUS FIL ON B2CS.FILE_ID=FIL.ID AND GSTRERR.FILE_ID=FIL.ID  ");
		build.append(
				" AND GSTRERR.INV_KEY=B2CS.B2CS_INVKEY LEFT OUTER JOIN   GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = B2CS.BATCH_ID  ");
		build.append("WHERE B2CS.IS_DELETE=FALSE AND IS_AMENDMENT=TRUE )");
		build.append(
				"  GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,NEW_POS,NEW_ECOM_GSTIN,");
		build.append("MONTH,DERIVED_RET_PERIOD  )  )  ");
		build.append(buildquery);
		build.append(
				"  GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD, TRAN_TYPE,MONTH, ORG_POS,ORG_HSNORSAC,ORG_UOM,ORG_RATE,ORG_ECOM_GSTIN, ");
		build.append(
				"NEW_POS,NEW_HSNORSAC,NEW_RATE,NEW_ECOM_GSTIN, PROFIT_CENTRE,PLANT_CODE,DIVISION, LOCATION, SALES_ORGANIZATION, DISTRIBUTION_CHANNEL, USERACCESS1,");
		build.append(
				"USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6, USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3, INFO_ERROR_CODE_ASP, INFO_ERROR_DESCRIPTION_ASP,");
		build.append(
				"SAVE_STATUS, GSTIN_REF_ID , GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION, SOURCE_ID,FILE_NAME, ASP_DATE_TIME, SUPPLY_TYPE,STATE_CODE, STATE_NAME, POS,");
		build.append(
				" TAX_RATE, DIFF_PERCENT,  ECOM_TYPE,  ECOM_GSTIN,  IS_FILED,ORGRET_PERIOD,GSTN_ORG_POS );");
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
