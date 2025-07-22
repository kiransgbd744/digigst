package com.ey.advisory.app.services.reports;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.views.client.Gstr6DeterminationDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Component("Gstr6DeterminationReportDaoImpl")
public class Gstr6DeterminationReportDaoImpl implements Gstr6DeterminationDao {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> gstr6DeterminationReports(SearchCriteria criteria) {

		Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria;

		String retPeriod = request.getReturnPeriod();
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
				buildHeader.append(" AND CR.ISD_GSTIN  IN :gstinList");

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

		if (retPeriod != null) {
			buildHeader.append(" AND CR.CURRENT_TAX_PERIOD  = :retPeriod ");

		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (retPeriod != null) {
			q.setParameter("retPeriod", retPeriod);
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
			for (Object arr[] : list) {
				verticalHsnList.add(convertTransactionalLevel(arr));
			}
		}
		return verticalHsnList;
	}

	private Gstr6DeterminationDto convertTransactionalLevel(Object[] arr) {
		Gstr6DeterminationDto obj = new Gstr6DeterminationDto();

		obj.setRetPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setIsdGstin(arr[1] != null ? arr[1].toString() : null);
		String custGstin = arr[2] != null ? arr[2].toString() : null;
		obj.setReciGstin(custGstin);
		String state = "";
		String stateName = arr[3] != null ? arr[3].toString() : null;
		String sgstinRegex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";
		Pattern pattern = Pattern.compile(sgstinRegex);
		Matcher matcher = pattern.matcher(custGstin);
		if (matcher.matches()) {
			StringBuilder builder = new StringBuilder(
					custGstin.substring(0, 2));
			state = builder.toString();
		} else {
			String stateCodes = statecodeRepository.getStateCodes(stateName);
			StringBuilder builder = new StringBuilder(
					stateCodes);
			state = builder.toString();
		}
		obj.setStateCode(state);
		obj.setOrgReciGstin(arr[4] != null ? arr[4].toString() : null);
		obj.setOrgStateCode(arr[5] != null ? arr[5].toString() : null);
		obj.setDocType(arr[6] != null ? arr[6].toString() : null);
		obj.setSupType(arr[7] != null ? arr[7].toString() : null);
		obj.setDocNo(arr[8] != null ? arr[8].toString() : null);
		obj.setDocDate(arr[9] != null ? arr[9].toString() : null);
		obj.setOrgDocNo(arr[10] != null ? arr[10].toString() : null);
		obj.setOrgDocDate(arr[11] != null ? arr[11].toString() : null);
		obj.setOrgCrNoteNo(arr[12] != null ? arr[12].toString() : null);
		obj.setOrgCrNoteDate(arr[13] != null ? arr[13].toString() : null);
		obj.setElIndiCator(arr[14] != null ? arr[14].toString() : null);
		obj.setIgstAsIgst(arr[15] != null ? arr[15].toString() : null);
		obj.setIgstAsSgst(arr[16] != null ? arr[16].toString() : null);
		obj.setIgstAsCgst(arr[17] != null ? arr[17].toString() : null);
		obj.setSgstAsSgst(arr[18] != null ? arr[18].toString() : null);
		obj.setSgstAsIgst(arr[19] != null ? arr[19].toString() : null);
		obj.setCgstAsCgst(arr[20] != null ? arr[20].toString() : null);
		obj.setCgstAsIgst(arr[21] != null ? arr[21].toString() : null);
		obj.setCessAmt(arr[22] != null ? arr[22].toString() : null);
		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery) {
		StringBuilder build = new StringBuilder();
		build.append("SELECT CR.CURRENT_TAX_PERIOD,CR.ISD_GSTIN,CR.CUST_GSTIN, ");
		build.append(
				"fnGetState( USR.STATE_NAME) AS STATE_NAME,'' AS ORG_REC_GSTIN, ");
		build.append(
				"'' AS ORG_STATE_CODE, DOC_TYPE,'' AS SUPPLY_TYPE, '' AS DOC_NUM, ");
		build.append("'' AS DOC_DATE,'' AS ORG_DOC_NUM, '' AS ORG_DOC_DATE, ");
		build.append("'' AS ORG_CREDIT_NOTENUM,'' AS ORG_CREDIT_NOTEDATE, ");
		build.append("ELIGIBLE_INDICATOR,SUM(IFNULL(IGST_AMT_AS_IGST,0)), ");
		build.append("SUM(IFNULL(IGST_AMT_AS_SGST,0)),SUM(IFNULL(IGST_AMT_AS_CGST,0)), ");
		build.append(
				"SUM(IFNULL(SGST_AMT_AS_SGST,0)) ,SUM(IFNULL(SGST_AMT_AS_IGST,0)), ");
		build.append("SUM(IFNULL(CGST_AMT_AS_CGST,0)),SUM(IFNULL(CGST_AMT_AS_IGST,0)), ");
		build.append(
				"SUM(IFNULL(CESS_AMT,0)) FROM GSTR6_CREDIT_DISTRIBUTION CR INNER JOIN  ");
		build.append("GSTR6_TURN_OVER_USERINPUT USR  ON  ");
		build.append(
				"CR.ISD_GSTIN = USR.ISD_GSTIN AND CR.IS_DELETE = USR.IS_dELETE  ");
		build.append(
				"AND CR.CURRENT_TAX_PERIOD = USR.CURRENT_RET_PERIOD  WHERE ");
		build.append("CR.IS_DELETE = FALSE AND USR.GSTIN = CR.CUST_GSTIN   ");
		build.append(buildQuery);
		build.append(" GROUP BY CR.CURRENT_TAX_PERIOD, CR.ISD_GSTIN, CR.CUST_GSTIN, "
				+ " DOC_TYPE, ELIGIBLE_INDICATOR, FNGETSTATE(USR.STATE_NAME) ");
		return build.toString();
	}
}
