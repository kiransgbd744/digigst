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
import com.ey.advisory.app.data.views.client.AdvAdjprocessSubmitdto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import com.google.common.base.Strings;

	@Component("AspProcessVsSubmitTxpdaReportDaoImpl")
	public class AspProcessVsSubmitTxpdaReportDaoImpl
			implements AspProcessVsSubmitDao {
		
		
		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;

		private static final String OLDFARMATTER = "yyyy-MM-dd";
		private static final String NEWFARMATTER = "dd-MM-yyyy";
		
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

			//String dataType = request.getDataType();
			//String taxperiod = request.getTaxperiod();
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
					buildHeader.append(" AND ADJ.SUPPLIER_GSTIN IN :gstinList");
					buildHeader2.append(" AND HDR.GSTIN IN :gstinList");
					buildHeader3.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList ");
				}
			}
			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && pcList.size() > 0) {

					buildHeader.append(" AND ADJ.PROFIT_CENTRE IN :pcList");

				}
			}
			if (plant != null && !plant.isEmpty()) {
				if (plantList != null && plantList.size() > 0) {

					buildHeader.append(" AND ADJ.PLANT_CODE IN :plantList");

				}
			}
			if (sales != null && !sales.isEmpty()) {
				if (salesList != null && salesList.size() > 0) {

					buildHeader.append(" AND ADJ.SALES_ORGANIZATION IN :salesList");

				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && distList.size() > 0) {

					buildHeader.append(" AND ADJ.DISTRIBUTION_CHANNEL IN :distList");

				}
			}
			if (division != null && !division.isEmpty()) {
				if (divisionList != null && divisionList.size() > 0) {

					buildHeader.append(" AND ADJ.DIVISION IN :divisionList");

				}
			}
			if (location != null && !location.isEmpty()) {
				if (locationList != null && locationList.size() > 0) {

					buildHeader.append(" AND ADJ.LOCATION IN :locationList");

				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && ud1List.size() > 0) {

					buildHeader.append(" AND ADJ.USERACCESS1 IN :ud1List");
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && ud2List.size() > 0) {

					buildHeader.append(" AND ADJ.USERACCESS2 IN :ud2List");
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && ud3List.size() > 0) {

					buildHeader.append(" AND ADJ.USERACCESS3 IN :ud3List");
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && ud4List.size() > 0) {

					buildHeader.append(" AND ADJ.USERACCESS4 IN :ud4List");
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && ud5List.size() > 0) {

					buildHeader.append(" AND ADJ.USERACCESS5 IN :ud5List");
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && ud6List.size() > 0) {

					buildHeader.append(" AND ADJ.USERACCESS6 IN :ud6List");
				}
			}

			if (taxPeriodFrom != null && taxPeriodTo != null) {
				buildHeader.append(" AND ADJ.DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo ");
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
				List<StateCodeInfoEntity> MasterEntities = statecodeRepository.findAll();
				Map<String, String> hsnMap = new HashMap<String, String>();
				MasterEntities.forEach(entity -> {
					hsnMap.put(entity.getStateCode(), entity.getStateName());
				});
				ProcessingContext context = new ProcessingContext();
				settingFiledGstins(context);
				for (Object arr[] : list) {
					verticalHsnList.add(convertProcessed(arr, hsnMap, context));
				}
			}
			return verticalHsnList;
		}
		
	private AdvAdjprocessSubmitdto convertProcessed(Object[] arr, Map<String, String> hsnMap, ProcessingContext context) {
		AdvAdjprocessSubmitdto obj = new AdvAdjprocessSubmitdto();

		obj.setGstnSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setGstnReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setGstnOriginalReturnPeriod(arr[3] != null ? arr[3].toString() : null);
		obj.setGstnSupplyType(arr[4] != null ? arr[4].toString() : null);
		obj.setGstnOriginalSupplyType(arr[5] != null ? arr[5].toString() : null);
		
		String stateName = null;
		String stateCode = (arr[6] != null ? arr[6].toString() : null);
		obj.setStatecode(stateCode);

		if (!Strings.isNullOrEmpty(stateCode)) {
			stateName = stateCode + "-" + hsnMap.get(stateCode);
		}
		obj.setGstnStateName(stateName);
		/*obj.setStatecode(arr[6] != null ? arr[6].toString() : null);
		obj.setGstnStateName(arr[7] != null ? arr[7].toString() : null);*/
		obj.setGstnPos(arr[8] != null ? arr[8].toString() : null);
		obj.setGstnOriginalPos(arr[9] != null ? arr[9].toString() : null);
		obj.setGstnRateofTax(arr[10] != null ? arr[10].toString() : null);
		obj.setGstnGrossAdvanceAdjusted(arr[11] != null ? arr[11].toString() : null);
		obj.setGstnIgstAmount(arr[12] != null ? arr[12].toString() : null);
		obj.setGstnCgstAmount(arr[13] != null ? arr[13].toString() : null);
		obj.setGstnSgstUTGSTAmount(arr[14] != null ? arr[14].toString() : null);
		obj.setGstnCessAmount(arr[15] != null ? arr[15].toString() : null);
		obj.setGstnDifferentialPercentageRate(arr[16] != null ? arr[16].toString() : null);
		//obj.setGstnIsFiled(arr[17] != null ? arr[17].toString() : null);
		obj.setGstnIsFiled(isGstinTaxperiodFiled(context,arr[0].toString(), arr[1].toString()));
		/*obj.setDigigstSupplierGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setDigigstReturnPeriod(arr[1] != null ? arr[1].toString() : null);*/
		/*obj.setGstnSupplierGSTIN(arr[15] != null ? arr[15].toString() : null);
		obj.setGstnReturnPeriod(arr[16] != null ? arr[16].toString() : null);*/
		obj.setTransactionType(arr[18] != null ? arr[18].toString() : null);
		obj.setMonth(
				arr[19] != null ? arr[19].toString() : null);
		obj.setOrgPos(arr[20] != null ? arr[20].toString() : null);
		obj.setOrgRate(arr[21] != null ? arr[21].toString() : null);
		obj.setOrgGrossAdvanceAdjusted(arr[22] != null ? arr[22].toString() : null);
		obj.setNewPOS(arr[23] != null ? arr[23].toString() : null);
		obj.setNewRate(arr[24] != null ? arr[24].toString() : null);
	    obj.setNewGrossAdvanceAdjusted(arr[25] != null ? arr[25].toString() : null);
		obj.setIntegratedTaxAmount(arr[26] != null ? arr[26].toString() : null);
		obj.setCentralTaxAmount(arr[27] != null ? arr[27].toString() : null);
		obj.setStateUTTaxAmount(arr[28] != null ? arr[28].toString() : null);
		obj.setCessAmount(arr[29] != null ? arr[29].toString() : null);
		obj.setProfitCentre(arr[30] != null ? arr[30].toString() : null);
		obj.setPlant(arr[31] != null ? arr[31].toString() : null);
		obj.setDivision(arr[32] != null ? arr[32].toString() : null);
		obj.setLocation(arr[33] != null ? arr[33].toString() : null);
		obj.setSalesOrganisation(arr[34] != null ? arr[34].toString() : null);
		obj.setDistributionChannel(arr[35] != null ? arr[35].toString() : null);
		obj.setUserAccess1(arr[36] != null ? arr[36].toString() : null);
		obj.setUserAccess2(arr[37] != null ? arr[37].toString() : null);
		obj.setUserAccess3(arr[38] != null ? arr[38].toString() : null);
		obj.setUserAccess4(arr[39] != null ? arr[39].toString() : null);
		obj.setUserAccess5(arr[40] != null ? arr[40].toString() : null);
		obj.setUserAccess6(arr[41] != null ? arr[41].toString() : null);
		obj.setUserdefinedfield1(arr[42] != null ? arr[42].toString() : null);
		obj.setUserdefinedfield2(arr[43] != null ? arr[43].toString() : null);
		obj.setUserdefinedfield3(arr[44] != null ? arr[44].toString() : null);
		obj.setAspInformationID(arr[45] != null ? arr[45].toString() : null);
		obj.setAspInformationDescription(arr[46] != null ? arr[46].toString() : null);
		obj.setSaveStatus(arr[47] != null ? arr[47].toString() : null);
		obj.setgSTNRefID(arr[48] != null ? arr[48].toString() : null);
		obj.setgSTNRefIDTime(arr[49] != null ? arr[49].toString() : null);
		obj.setgSTNErrorcode(arr[50] != null ? arr[50].toString() : null);
		obj.setgSTNErrorDescription(arr[51] != null ? arr[51].toString() : null);
		obj.setSourceId(arr[52] != null ? arr[52].toString() : null);
		obj.setFileName(arr[53] != null ? arr[53].toString() : null);
		obj.setAspDateTime(arr[54] != null ? arr[54].toString() : null);
		obj.setsNo(arr[55] != null ? arr[55].toString() : null);
		
		obj.setDiffTaxableValue(arr[56] != null ? arr[56].toString() : null);
		obj.setDiffIgst(arr[57] != null ? arr[57].toString() : null);
		obj.setDiffCgst(arr[58] != null ? arr[58].toString() : null);
		obj.setDiffSgst(arr[59] != null ? arr[59].toString() : null);
		obj.setDiffCess(arr[60] != null ? arr[60].toString() : null);

		return obj;
	}

	private String createApiProcessedQueryString(String buildHeader,String buildHeader2, String buildHeader3) {
		
		
		StringBuilder build = new StringBuilder();

		build.append("SELECT  ");
		build.append("SUPPLIER_GSTIN, ");
		build.append("RETURN_PERIOD, ");
		build.append("DERIVED_RET_PERIOD, ");
		build.append("ORG_MONTH, ");
		build.append("SUPPLY_TYPE, ");
		build.append("ORG_SUPPLY_TYPE, ");
		build.append("STATE_CODE, ");
		build.append("STATE_NAME, ");
		build.append("POS, ");
		build.append("GSTN_ORG_POS, ");
		build.append("TAX_RATE, ");
		build.append("GROSS_ADV_ADJUSTED, ");
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
		build.append("ORG_GROSS_ADV_ADJUSTED, ");
		build.append("NEW_POS, ");
		build.append("NEW_RATE, ");
		build.append("NEW_GROSS_ADV_ADJUSTED, ");
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
		build.append("GSTIN_REF_ID, ");
		build.append("GSTIN_REF_ID_TIME, ");
		build.append("GSTN_ERROR_CODE, ");
		build.append("GSTN_ERROR_DESCRIPTION, ");
		build.append("SOURCE_ID, ");
		build.append("FILE_NAME, ");
		build.append("ASP_DATE_TIME, ");
		build.append("SNO, ");
		build.append("(IFNULL(NEW_GROSS_ADV_ADJUSTED,0) -IFNULL(GROSS_ADV_ADJUSTED,0)) as DIFF_TAXABLE_VALUE, ");
		build.append("(IFNULL(ASP_IGST_AMT,0) -IFNULL(GSTN_IGST_AMT,0)) as DIFF_IGST_AMT, ");
		build.append("(IFNULL(ASP_CGST_AMT,0) -IFNULL(GSTN_CGST_AMT,0)) as DIFF_CGST_AMT, ");
		build.append("(IFNULL(ASP_SGST_AMT,0) -IFNULL(GSTN_SGST_AMT,0)) as DIFF_SGST_AMT, ");
		build.append("(IFNULL(ASP_CESS_AMT,0) -IFNULL(GSTN_CESS_AMT,0)) as DIFF_CESS_AMT ");
		build.append(" from ");
		build.append("( ");
		build.append("select *,	 ROW_NUMBER () OVER ( ");
		build.append("ORDER BY IFNULL(SUPPLIER_GSTIN,SUPPLIER_GSTIN)) SNO from ");
		build.append("( with GSTN as ( SELECT HDR.GSTIN SUPPLIER_GSTIN,");
		build.append("HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,");
		build.append("(IFNULL(HDR.GSTIN,'') ||'|'|| IFNULL(HDR.RETURN_PERIOD,'')  ");
		build.append("||'|'||IFNULL(MAP(HDR.DIFF_PERCENT,0.65,'L65','N'),'') ");
		build.append("||'|'||IFNULL(TO_NUMBER(POS), 9999) ) DOC_KEY,");
	//	build.append("SUBSTR(HDR.GSTIN,1,2) AS STATE_CODE,'' STATE_NAME,");
		build.append("ORG_MONTH,''SUPPLY_TYPE, SUPPLY_TYPE ORG_SUPPLY_TYPE,SUBSTR(HDR.GSTIN,1,2) AS STATE_CODE,");
		build.append("'' STATE_NAME, '' POS,POS AS GSTN_ORG_POS,NULL as TAX_RATE,");
		
		build.append("SUM(ITM.ADVADJ_AMT) TAXABLE_VALUE,");
		build.append("SUM(ITM.IGST_AMT) IGST_AMT,");
		build.append("SUM(ITM.CGST_AMT) CGST_AMT,");
		build.append("SUM(ITM.SGST_AMT) SGST_AMT,");
		build.append("SUM(ITM.CESS_AMT) CESS_AMT,");
		build.append("HDR.DIFF_PERCENT, BT.IS_FILED,'ADV ADJ-A' AS TABLE_TYPE "); 
		build.append("FROM GETGSTR1_TXPA_HEADER HDR ");
		build.append("INNER JOIN GETGSTR1_TXPA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		build.append("AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD ");
		build.append("LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID ");
		build.append("WHERE HDR.IS_DELETE=FALSE AND BT.IS_DELETE=FALSE "); 
		build.append(buildHeader2);
						//and HDR.GSTIN='27GSPMH0482G1ZM'
			 		    //and HDR.RETURN_PERIOD='072022'

		build.append("GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,SUPPLY_TYPE,");
		build.append("POS,HDR.DIFF_PERCENT,BT.IS_FILED,ORG_MONTH ");
		build.append("), DIGI as ( SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
		build.append("(CASE WHEN TRAN_TYPE IN ('ZL65','L65')  ");
		build.append("THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL)  ");
		build.append("THEN 'N' END) TRAN_TYPE,(IFNULL(SUPPLIER_GSTIN,'') ||'|'|| IFNULL(RETURN_PERIOD,'')  ");
		build.append("||'|'||IFNULL((CASE WHEN TRAN_TYPE IN ('ZL65','L65')  ");
		build.append("THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') OR TRAN_TYPE IS NULL)  ");
		build.append("THEN 'N' END),'') ");
		build.append("||'|'||IFNULL(TO_NUMBER(NEW_POS), 9999) ) as DOC_KEY,");
		build.append("MAX(MONTH)MONTH, MAX(ORG_POS)ORG_POS, MAX(ORG_RATE)ORG_RATE,");
		build.append("MAX(ORG_GROSS_ADV_ADJUSTED)ORG_TAXABLE_VALUE, NEW_POS,");
		build.append("NULL NEW_RATE,SUM(NEW_GROSS_ADV_ADJUSTED)NEW_TAXABLE_VALUE,");
		build.append("SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,SUM(SGST_AMT)SGST_AMT,");
		build.append("SUM(CESS_AMT)CESS_AMT,MAX(PROFIT_CENTRE)PROFIT_CENTRE,");
		build.append("MAX(PLANT_CODE)PLANT_CODE,MAX(DIVISION)DIVISION,");
		build.append("MAX(LOCATION)LOCATION,");
		build.append("MAX(SALES_ORGANIZATION)SALES_ORGANIZATION,");
		build.append("MAX(DISTRIBUTION_CHANNEL)DISTRIBUTION_CHANNEL,");
		build.append("MAX(USERACCESS1)USERACCESS1,");
		build.append("MAX(USERACCESS2)USERACCESS2,");
		build.append("MAX(USERACCESS3)USERACCESS3,");
		build.append("MAX(USERACCESS4)USERACCESS4,");
		build.append("MAX(USERACCESS5)USERACCESS5,");
		build.append("MAX(USERACCESS6)USERACCESS6,");
		build.append("MAX(USERDEFINED_FIELD1)USERDEFINED_FIELD1,");
		build.append("MAX(USERDEFINED_FIELD2)USERDEFINED_FIELD2,");
		build.append("MAX(USERDEFINED_FIELD3)USERDEFINED_FIELD3,");
		build.append("MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP,");
		build.append("MAX(INFO_ERROR_DESCRIPTION_ASP)INFO_ERROR_DESCRIPTION_ASP,");
		build.append("MAX(SAVE_STATUS)SAVE_STATUS,");
		build.append("MAX(GSTIN_REF_ID)GSTIN_REF_ID,");
		build.append("MAX(GSTIN_REF_ID_TIME)GSTIN_REF_ID_TIME,");
		build.append("MAX(GSTN_ERROR_CODE)GSTN_ERROR_CODE,");
		build.append("MAX(GSTN_ERROR_DESCRIPTION)GSTN_ERROR_DESCRIPTION,");
		build.append("MAX(SOURCE_ID)SOURCE_ID,MAX(FILE_NAME)FILE_NAME,");
		build.append("MAX(ASP_DATE_TIME)ASP_DATE_TIME  FROM ( SELECT ADJ.SUPPLIER_GSTIN,");
		build.append("ADJ.RETURN_PERIOD,ADJ.DERIVED_RET_PERIOD, TRAN_TYPE,MONTH,");
		build.append("NULL ORG_POS,NULL ORG_RATE, 0 ORG_GROSS_ADV_ADJUSTED, NEW_POS,");
		build.append("NEW_RATE,NEW_GROSS_ADV_ADJUSTED,IFNULL(IGST_AMT,0) AS IGST_AMT,");
		build.append("IFNULL(CGST_AMT,0) AS CGST_AMT, IFNULL(SGST_AMT,0) AS SGST_AMT, IFNULL(CESS_AMT,0) AS CESS_AMT,'' PROFIT_CENTRE,''PLANT_CODE,'' DIVISION,");
		build.append("''LOCATION,''SALES_ORGANIZATION,''DISTRIBUTION_CHANNEL,''USERACCESS1,");
		build.append("''USERACCESS2,''USERACCESS3,''USERACCESS4,''USERACCESS5,");
		build.append("''USERACCESS6,''USERDEFINED_FIELD1,''USERDEFINED_FIELD2,");
		build.append("''USERDEFINED_FIELD3, '' AS INFO_ERROR_CODE_ASP,");
		build.append("'' AS INFO_ERROR_DESCRIPTION_ASP, (CASE WHEN ADJ.IS_SAVED_TO_GSTN = TRUE "); 
		build.append("AND ADJ.IS_DELETE = FALSE THEN 'IS_SAVED' ELSE 'NOT_SAVED'  ");
		build.append("END) AS SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,");
		build.append("GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, '' AS GSTN_ERROR_CODE,");
		build.append("'' AS GSTN_ERROR_DESCRIPTION, ''SOURCE_ID,''FILE_NAME,'' ASP_DATE_TIME  ");
		build.append("FROM GSTR1_PROCESSED_ADV_ADJUSTMENT ADJ  ");
		build.append("LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() GSTRERR ");
		build.append("ON ADJ.AS_ENTERED_ID= GSTRERR.COMMON_ID  ");
		build.append("AND ADJ.FILE_ID=GSTRERR.FILE_ID AND GSTRERR.TABLE_TYPE='ADV ADJ-A'  ");
		build.append("AND RETURN_TYPE='GSTR1' LEFT OUTER JOIN FILE_STATUS FIL ON ADJ.FILE_ID=FIL.ID  ");
		build.append("AND GSTRERR.FILE_ID=FIL.ID AND GSTRERR.INV_KEY=ADJ.TXPD_INVKEY ");
		build.append("LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = ADJ.BATCH_ID  ");
		build.append("WHERE ADJ.IS_DELETE=FALSE AND IS_AMENDMENT=TRUE ");
		build.append(buildHeader);
					//	AND ADJ.SUPPLIER_GSTIN='27GSPMH0482G1ZM'
			 		//	and ADJ.RETURN_PERIOD='072022
		
		build.append(" UNION ALL "); 
		build.append(" SELECT HDR.SUPPLIER_GSTIN "); 
		build.append(",HDR.RETURN_PERIOD"); 
		build.append(",HDR.DERIVED_RET_PERIOD"); 
		build.append(",HDR.DIFF_PERCENT AS TRAN_TYPE ");
		build.append(",NULL MONTH ");
		build.append(",NULL ORG_POS ");
		build.append(",NULL ORG_RATE ");
		build.append(",0 ORG_GROSS_ADV_ADJUSTED ");
		build.append(",HDR.POS AS NEW_POS ");
		build.append(",NULL NEW_RATE ");
		build.append(",IFNULL(ITM.TAXABLE_VALUE,0) AS NEW_GROSS_ADV_ADJUSTED ");
		build.append(",IFNULL(ITM.IGST_AMT,0) AS IGST_AMT ");
		build.append(",IFNULL(ITM.CGST_AMT,0) AS CGST_AMT ");
		build.append(",IFNULL(ITM.SGST_AMT,0) AS SGST_AMT ");
		build.append(",(IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS_AMT ");
		build.append(",'' PROFIT_CENTRE ");
		build.append(",'' PLANT_CODE ");
		build.append(",'' DIVISION ");
		build.append(",'' LOCATION ");
		build.append(",'' SALES_ORGANIZATION ");
		build.append(",'' DISTRIBUTION_CHANNEL ");
		build.append(",'' USERACCESS1 ");
		build.append(",'' USERACCESS2 ");
		build.append(",'' USERACCESS3 ");
		build.append(",'' USERACCESS4 ");
		build.append(",'' USERACCESS5 ");
		build.append(",'' USERACCESS6 ");
		build.append(",'' USERDEFINED_FIELD1 ");
		build.append(",'' USERDEFINED_FIELD2 ");
		build.append(",'' USERDEFINED_FIELD3 ");
		build.append(",NULL AS INFO_ERROR_CODE_ASP ");
		build.append(",NULL AS INFO_ERROR_DESCRIPTION_ASP ");
		build.append(" ,( ");
		build.append(" CASE ");
		build.append(" WHEN IS_SAVED_TO_GSTN = TRUE ");
		build.append(" AND IS_DELETE = FALSE ");
		build.append(" THEN 'IS_SAVED' ");
		build.append(" ELSE 'NOT_SAVED' ");
		build.append(" END ");
		build.append(" ) AS SAVE_STATUS ");
		build.append(",NULL AS GSTIN_REF_ID ");
		build.append(",NULL AS GSTIN_REF_ID_TIME ");
		build.append(",'' AS GSTN_ERROR_CODE ");
		build.append(",'' AS GSTN_ERROR_DESCRIPTION ");
		build.append(",'' SOURCE_ID ");
		build.append(",'' FILE_NAME ");
		build.append(",'' ASP_DATE_TIME ");
		build.append(" FROM ANX_OUTWARD_DOC_HEADER HDR ");
		build.append(" JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID ");
		build.append(" WHERE HDR.TAX_DOC_TYPE='TXPA' ");
		build.append(" AND HDR.RETURN_TYPE='GSTR1' AND HDR.COMPLIANCE_APPLICABLE=TRUE AND HDR.ASP_INVOICE_STATUS = 2  ");
		build.append( buildHeader3 );
		build.append(" AND IS_DELETE=FALSE ");
		
		build.append(" ) GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
		build.append("(CASE WHEN TRAN_TYPE IN ('ZL65','L65') THEN 'L65' WHEN (TRAN_TYPE IN ('Z','N','') ");
		build.append("OR TRAN_TYPE IS NULL) THEN 'N' END),NEW_POS ) SELECT ");
		build.append("IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN) as SUPPLIER_GSTIN,");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD) AS RETURN_PERIOD,");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD) AS DERIVED_RET_PERIOD,");
	//	build.append("SUPPLY_TYPE,STATE_CODE,STATE_NAME,POS,NULL AS TAX_RATE,");
		build.append("ORG_MONTH, SUPPLY_TYPE, ORG_SUPPLY_TYPE,STATE_CODE,");
	    build.append("STATE_NAME, POS,GSTN_ORG_POS,TAX_RATE,");
		
	    build.append("SUM(TAXABLE_VALUE) GROSS_ADV_ADJUSTED,");
		build.append("SUM(GSTN.IGST_AMT)GSTN_IGST_AMT,");
		build.append("SUM(GSTN.CGST_AMT)GSTN_CGST_AMT,");
		build.append("SUM(GSTN.SGST_AMT)GSTN_SGST_AMT,");
		build.append("SUM(GSTN.CESS_AMT)GSTN_CESS_AMT,");
		build.append("DIFF_PERCENT,IS_FILED,TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,");
		build.append("SUM(ORG_TAXABLE_VALUE)ORG_GROSS_ADV_ADJUSTED,");
		build.append("NEW_POS,NEW_RATE,SUM(NEW_TAXABLE_VALUE)NEW_GROSS_ADV_ADJUSTED,");
		build.append("SUM(DIGI.IGST_AMT)ASP_IGST_AMT,");
		build.append("SUM(DIGI.CGST_AMT)ASP_CGST_AMT,");
		build.append("SUM(DIGI.SGST_AMT)ASP_SGST_AMT,");
		build.append("SUM(DIGI.CESS_AMT)ASP_CESS_AMT,");
		build.append("PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,");
		build.append("DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,USERACCESS3, USERACCESS4,");
		build.append("USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,");
		build.append("USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP, INFO_ERROR_DESCRIPTION_ASP,");
		build.append("SAVE_STATUS,GSTIN_REF_ID ,");
		build.append("TO_CHAR(ADD_SECONDS(GSTIN_REF_ID_TIME,19800),'DD-MM-YYYY HH24:MI:SS') ");
		build.append("AS GSTIN_REF_ID_TIME, GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,");
		build.append("SOURCE_ID,FILE_NAME,ASP_DATE_TIME	from GSTN FULL OUTER JOIN DIGI ");
		build.append("on GSTN.DOC_KEY=  DIGI.DOC_KEY GROUP BY ");
		build.append("IFNULL(GSTN.SUPPLIER_GSTIN,DIGI.SUPPLIER_GSTIN),");
		build.append("IFNULL(GSTN.RETURN_PERIOD,DIGI.RETURN_PERIOD) ,");
		build.append("IFNULL(GSTN.DERIVED_RET_PERIOD,DIGI.DERIVED_RET_PERIOD) ,");
		
		build.append("ORG_MONTH, ORG_SUPPLY_TYPE, GSTN_ORG_POS,TAX_RATE,");
		
		build.append("TRAN_TYPE,MONTH,ORG_POS,ORG_RATE,NEW_POS,	NEW_RATE, PROFIT_CENTRE,PLANT_CODE,");
		build.append("DIVISION, LOCATION, SALES_ORGANIZATION, DISTRIBUTION_CHANNEL,");
		build.append("USERACCESS1,USERACCESS2,USERACCESS3, USERACCESS4,");
		build.append("USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,");
		build.append("USERDEFINED_FIELD2,USERDEFINED_FIELD3,INFO_ERROR_CODE_ASP,");
		build.append("INFO_ERROR_DESCRIPTION_ASP,SAVE_STATUS,GSTIN_REF_ID ,");
		build.append("GSTIN_REF_ID_TIME,GSTN_ERROR_CODE , GSTN_ERROR_DESCRIPTION,");
		build.append("SOURCE_ID,FILE_NAME,ASP_DATE_TIME,SUPPLY_TYPE,STATE_CODE,STATE_NAME,POS,");
		build.append("DIFF_PERCENT, IS_FILED )  );");

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

