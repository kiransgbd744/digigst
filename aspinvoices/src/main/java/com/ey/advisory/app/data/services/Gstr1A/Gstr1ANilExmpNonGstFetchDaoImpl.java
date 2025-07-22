package com.ey.advisory.app.data.services.Gstr1A;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.Gstr1NilDetailsEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilDetailsEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AUserInputNilExtnOnEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilNonExtSummaryRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AUserInputNilExtnOnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputNilExtnOnEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputNilExtnOnRepository;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstSummaryStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstVerticalStatusRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;

@Component("Gstr1ANilExmpNonGstFetchDaoImpl")
public class Gstr1ANilExmpNonGstFetchDaoImpl {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ANilExmpNonGstFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1AUserInputNilExtnOnRepository")
	private Gstr1AUserInputNilExtnOnRepository repository;

	@Autowired
	@Qualifier("Gstr1ANilRepository")
	Gstr1ANilRepository gstr1NilRepository;
	@Autowired
	@Qualifier("Gstr1ANilNonExtSummaryRepository")
	Gstr1ANilNonExtSummaryRepository gstr1NilNonExtSummaryRepository;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	public List<Gstr1NilExmpNonGstStatusRespDto> loadNillExmpNonGstRecords(
			Gstr1ProcessedRecordsReqDto reqDto) {

		List<Long> entityId = reqDto.getEntityId();
		String taxPeriod = reqDto.getRetunPeriod();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1NilExmpNonGstFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
					reqDto);
		}
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;
		String cgstin = null;

		List<String> gstinList = null;
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

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					pcList = dataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					divisionList = dataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					locationList = dataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					salesList = dataSecAttrs.get(OnboardingConstant.SO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					distList = dataSecAttrs.get(OnboardingConstant.DC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		int taxPeriod1 = 0;
		if (taxPeriod != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriod);
		}

		String queryStr = null;


			queryStr = createQueryStringGstr1A(entityId, gstinList, taxPeriod1,
					taxPeriod, dataSecAttrs, profitCenter, sgstin, cgstin,
					plant, division, location, sales, distChannel, ud1, ud2,
					ud3, ud4, ud5, ud6, pcList, plantList, salesList,
					divisionList, locationList, distList, ud1List, ud2List,
					ud3List, ud4List, ud5List, ud6List);

				
			LOGGER.debug("outQueryStr-->" + queryStr);

		List<Gstr1NilExmpNonGstStatusRespDto> finalDtoList = new ArrayList<>();
		try {

			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (taxPeriod1 != 0) {
				Q.setParameter("taxPeriod", taxPeriod1);
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				Q.setParameter("sgstin", gstinList);
			}
			if (profitCenter != null && !profitCenter.isEmpty()
					&& !profitCenter.isEmpty() && pcList != null
					&& pcList.size() > 0) {
				Q.setParameter("pcList", pcList);
			}
			if (plant != null && !plant.isEmpty() && !plant.isEmpty()
					&& plantList != null && plantList.size() > 0) {
				Q.setParameter("plantList", plantList);
			}
			if (sales != null && !sales.isEmpty() && salesList != null
					&& salesList.size() > 0) {
				Q.setParameter("salesList", salesList);
			}
			if (division != null && !division.isEmpty() && divisionList != null
					&& divisionList.size() > 0) {
				Q.setParameter("divisionList", divisionList);
			}
			if (location != null && !location.isEmpty() && locationList != null
					&& locationList.size() > 0) {
				Q.setParameter("locationList", locationList);
			}
			if (distChannel != null && !distChannel.isEmpty()
					&& distList != null && distList.size() > 0) {
				Q.setParameter("distList", distList);
			}
			if (ud1 != null && !ud1.isEmpty() && ud1List != null
					&& ud1List.size() > 0) {
				Q.setParameter("ud1List", ud1List);
			}
			if (ud2 != null && !ud2.isEmpty() && ud2List != null
					&& ud2List.size() > 0) {
				Q.setParameter("ud2List", ud2List);
			}
			if (ud3 != null && !ud3.isEmpty() && ud3List != null
					&& ud3List.size() > 0) {
				Q.setParameter("ud3List", ud3List);
			}
			if (ud4 != null && !ud4.isEmpty() && ud4List != null
					&& ud4List.size() > 0) {
				Q.setParameter("ud4List", ud4List);
			}
			if (ud5 != null && !ud5.isEmpty() && ud5List != null
					&& ud5List.size() > 0) {
				Q.setParameter("ud5List", ud5List);
			}
			if (ud6 != null && !ud6.isEmpty() && ud6List != null
					&& ud6List.size() > 0) {
				Q.setParameter("ud6List", ud6List);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> Qlist = Q.getResultList();
			LOGGER.error("bufferString-------------------------->" + Qlist);
			List<Gstr1NilExmpNonGstStatusRespDto> outwardFinalList = convertGstr1RecordsIntoObject(
					Qlist);
			finalDtoList.addAll(outwardFinalList);
			LOGGER.debug("Data list from database is-->" + Qlist);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr1NilExmpNonGstStatusRespDto> convertGstr1RecordsIntoObject(
			List<Object[]> savedDataList) {
		List<Gstr1NilExmpNonGstStatusRespDto> summaryList = new ArrayList<Gstr1NilExmpNonGstStatusRespDto>();
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr1NilExmpNonGstStatusRespDto dto = new Gstr1NilExmpNonGstStatusRespDto();
				dto.setDesc(String.valueOf(data[0]));
				dto.setDocKey(String.valueOf(data[1]));
				dto.setAspNilRated((BigDecimal) data[2]);
				dto.setAspExempted((BigDecimal) data[3]);
				dto.setAspNonGst((BigDecimal) data[4]);
				dto.setUsrNilRated((BigDecimal) data[5]);
				dto.setUsrExempted((BigDecimal) data[6]);
				dto.setUsrNonGst((BigDecimal) data[7]);
				dto.setDerivedRetPeriod(String.valueOf(data[8]));
				dto.setGstin(String.valueOf(data[9]));

				dto.setId(String.valueOf(data[11]));

				dto.setTaxPeriod(String.valueOf(data[10]));

				summaryList.add(dto);
			}
		}
		return summaryList;

	}

	public String createQueryString(List<Long> entityId, List<String> gstinList,
			int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List, String multiSupplyTypeAns) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		StringBuilder queryBuilder1 = new StringBuilder();
		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND SUPPLIER_GSTIN IN (:sgstin) ");
			queryBuilder1.append(" AND SUPPLIER_GSTIN IN (:sgstin) ");
		}
		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod) ");
			queryBuilder1.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod) ");
		}

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			queryBuilder.append(" AND PROFIT_CENTRE IN :pcList ");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			queryBuilder.append(" AND PLANT_CODE IN :plantList ");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			queryBuilder.append(" AND SALES_ORGANIZATION IN :salesList ");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			queryBuilder.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			queryBuilder.append(" AND DIVISION IN :divisionList ");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			queryBuilder.append(" AND LOCATION IN :locationList ");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& ud1List.size() > 0) {
			queryBuilder.append(" AND USERACCESS1 IN :ud1List ");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& ud2List.size() > 0) {
			queryBuilder.append(" AND USERACCESS2 IN :ud2List ");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& ud3List.size() > 0) {
			queryBuilder.append(" AND USERACCESS3 IN :ud3List ");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& ud4List.size() > 0) {
			queryBuilder.append(" AND USERACCESS4 IN :ud4List ");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& ud5List.size() > 0) {
			queryBuilder.append(" AND USERACCESS5 IN :ud5List ");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& ud6List.size() > 0) {
			queryBuilder.append(" AND USERACCESS6 IN :ud6List ");
		}

		String condition = queryBuilder.toString().substring(4);
		String condition1 = queryBuilder1.toString().substring(4);
		// StringBuilder bufferString = new StringBuilder();
		String query = null;
		if ("A".equalsIgnoreCase(multiSupplyTypeAns)) {
			LOGGER.debug("Multi Supply Type Answer :" + multiSupplyTypeAns);
			query = "SELECT DESCRIPTION, DOC_KEY, SUM(ASP_NIL_RATED_SUPPLIES) ASP_NIL_RATED_SUPPLIES, SUM(ASP_EXMPTED_SUPPLIES) ASP_EXMPTED_SUPPLIES, "
					+ " SUM(ASP_NON_GST_SUPPLIES) ASP_NON_GST_SUPPLIES, SUM(UI_NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES, "
					+ " SUM(UI_EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES, SUM(UI_NON_GST_SUPPLIES) UI_NON_GST_SUPPLIES, DERIVED_RET_PERIOD, SUPPLIER_GSTIN, "
					+ " RETURN_PERIOD, DESCRIPTION_KEY "
					+ " FROM ( SELECT DESCRIPTION, DOC_KEY, ASP_NIL_RATED_SUPPLIES, ASP_EXMPTED_SUPPLIES, "
					+ " ASP_NON_GST_SUPPLIES, 0 UI_NIL_RATED_SUPPLIES, 0 UI_EXMPTED_SUPPLIES, 0 UI_NON_GST_SUPPLIES, RETURN_PERIOD, "
					+ " DERIVED_RET_PERIOD, SUPPLIER_GSTIN, DESCRIPTION_KEY FROM ( "
					+ " SELECT DESCRIPTION, DOC_KEY, IFNULL(SUM(NIL_RATED_SUPPLIES),  0) ASP_NIL_RATED_SUPPLIES, "
					+ " IFNULL(SUM(EXMPTED_SUPPLIES),  0) ASP_EXMPTED_SUPPLIES, IFNULL(SUM(NON_GST_SUPPLIES),  0) ASP_NON_GST_SUPPLIES, "
					+ " RETURN_PERIOD, DERIVED_RET_PERIOD, SUPPLIER_GSTIN, DESCRIPTION_KEY FROM ( "
					+ " SELECT (CASE WHEN ITM.ITM_TABLE_SECTION = '8A' THEN 'Inter-State Supplies to Registered Person' "
					+ " WHEN ITM.ITM_TABLE_SECTION = '8B' THEN 'Intra-State Supplies to Registered Person' "
					+ " WHEN ITM.ITM_TABLE_SECTION = '8C' THEN 'Inter-State Supplies to UnRegistered Person' "
					+ " WHEN ITM.ITM_TABLE_SECTION = '8D' THEN 'Intra-State Supplies to UnRegistered Person' END) DESCRIPTION, "
					+ " ( SUPPLIER_GSTIN || '|' || HDR.RETURN_PERIOD || '|' || (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TABLE_SECTION ELSE ITM.ITM_TABLE_SECTION END)) AS DOC_KEY, "
					+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
					+ " AND (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.SUPPLY_TYPE ELSE ITM.SUPPLY_TYPE END) = 'NIL' THEN (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) END"
					+ " ), 0) "
					+ " - IFNULL( SUM(CASE WHEN DOC_TYPE = 'CR' AND (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.SUPPLY_TYPE ELSE ITM.SUPPLY_TYPE END) = 'NIL' THEN (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) "
					+ " END),  0) AS NIL_RATED_SUPPLIES, "
					+ "  IFNULL( SUM(CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
					+ " AND (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.SUPPLY_TYPE ELSE ITM.SUPPLY_TYPE END) = 'EXT' THEN (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) END"
					+ " ), 0) " + " - IFNULL( SUM( "
					+ " CASE WHEN DOC_TYPE = 'CR' AND (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.SUPPLY_TYPE ELSE ITM.SUPPLY_TYPE END) = 'EXT' THEN (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) "
					+ " END ),  0) AS EXMPTED_SUPPLIES, "
					+ " IFNULL( SUM( CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
					+ " AND (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.SUPPLY_TYPE ELSE ITM.SUPPLY_TYPE END) IN ('NON', 'SCH3') THEN (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) "
					+ " END ), 0 "
					+ "  ) - IFNULL( SUM( CASE WHEN DOC_TYPE = 'CR' "
					+ " AND (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.SUPPLY_TYPE ELSE ITM.SUPPLY_TYPE END) IN ('NON', 'SCH3') THEN (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) "
					+ " END ), 0 "
					+ " ) AS NON_GST_SUPPLIES, HDR.RETURN_PERIOD, HDR.DERIVED_RET_PERIOD, SUPPLIER_GSTIN, "
					+ " (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TABLE_SECTION ELSE ITM.ITM_TABLE_SECTION END) AS DESCRIPTION_KEY "
					+ " FROM ANX_OUTWARD_DOC_HEADER HDR "
					+ " INNER JOIN  ANX_OUTWARD_DOC_ITEM ITM ON  HDR.ID= ITM.DOC_HEADER_ID "
					+ " LEFT OUTER JOIN (SELECT GCP.Answer,GI.GSTIN "
					+ " FROM GROUP_CONFG_PRMTR GCP "
					+ " INNER JOIN GSTIN_INFO GI ON GCP.GROUP_ID = GI.GROUP_ID "
					+ " WHERE CONFG_QUESTION_ID = (SELECT ID FROM CONFG_QUESTION WHERE QUESTION_DESCRIPTION = 'Do you want to bifurcate TAX/NIL/EXT/NON to Tables 4 and 8 separately basis the supply type?') "
					+ " AND GCP.IS_DELETE = FALSE)A ON A.GSTIN = HDR.SUPPLIER_GSTIN "
					+ " WHERE (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TAX_DOC_TYPE ELSE ITM.ITM_TAX_DOC_TYPE END) IN ('NILEXTNON') "
					+ " AND ASP_INVOICE_STATUS = 2 "
					+ " AND COMPLIANCE_APPLICABLE = TRUE "
					+ " AND IS_DELETE = FALSE "
					+ " AND RETURN_TYPE = 'GSTR1' AND "

					+ condition
					+ "  GROUP BY HDR.RETURN_PERIOD, HDR.DERIVED_RET_PERIOD, SUPPLIER_GSTIN, "
					+ " (CASE WHEN A.ANSWER = 'B' OR HDR.IS_MULTI_SUPP_INV = FALSE THEN HDR.TABLE_SECTION ELSE ITM.ITM_TABLE_SECTION END),ITM.ITM_TABLE_SECTION "
					+ " UNION ALL  SELECT ( "
					+ " CASE WHEN TABLE_SECTION = '8A' THEN 'Inter-State Supplies to Registered Person' WHEN TABLE_SECTION = '8B' THEN 'Intra-State Supplies to Registered Person' WHEN TABLE_SECTION = '8C' THEN 'Inter-State Supplies to UnRegistered Person' WHEN TABLE_SECTION = '8D' THEN 'Intra-State Supplies to UnRegistered Person' END "
					+ " ) DESCRIPTION, ( SUPPLIER_GSTIN || '|' || RETURN_PERIOD || '|' || TABLE_SECTION ) AS DOC_KEY, "
					+ " CASE WHEN SUPPLY_TYPE = 'NIL' THEN IFNULL(TAXABLE_VALUE, 0) END AS NIL_RATED_SUPPLIES, "
					+ " CASE WHEN SUPPLY_TYPE = 'EXT' THEN IFNULL(TAXABLE_VALUE, 0) END AS EXMPTED_SUPPLIES, "
					+ " CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE, 0) END AS NON_GST_SUPPLIES, "
					+ "  RETURN_PERIOD, DERIVED_RET_PERIOD, SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY FROM  GSTR1_SUMMARY_NILEXTNON "
					+ " WHERE IS_DELETE = FALSE AND "

					+ condition1
					+ " ) GROUP BY DESCRIPTION,SUPPLIER_GSTIN,DERIVED_RET_PERIOD,"
					+ "DESCRIPTION_KEY,RETURN_PERIOD,DOC_KEY ) " + "UNION ALL "
					+ "SELECT DESCRIPTION,DOC_KEY,"
					+ "0 ASP_NIL_RATED_SUPPLIES,0 ASP_EXMPTED_SUPPLIES,"
					+ "0 ASP_NON_GST_SUPPLIES,UI_NIL_RATED_SUPPLIES,"
					+ "UI_EXMPTED_SUPPLIES,UI_NON_GST_SUPPLIES,RETURN_PERIOD,"
					+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM ( SELECT  DESCRIPTION,DOC_KEY,"
					+ "SUM(NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES,"
					+ "SUM(EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES,"
					+ "SUM(NON_GST_SUPPLIES) UI_NON_GST_SUPPLIES,RETURN_PERIOD,"
					+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM ( SELECT  (CASE WHEN DESCRIPTION_KEY='8A' "
					+ "THEN 'Inter-State Supplies to Registered Person' "
					+ "WHEN DESCRIPTION_KEY='8B' THEN 'Intra-State Supplies to Registered Person' "
					+ "WHEN DESCRIPTION_KEY='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
					+ "WHEN DESCRIPTION_KEY='8D' "
					+ "THEN 'Intra-State Supplies to UnRegistered Person' END)DESCRIPTION,"
					+ "DOC_KEY,IFNULL(NIL_RATED_SUPPLIES,0) NIL_RATED_SUPPLIES,"
					+ "IFNULL(EXMPTED_SUPPLIES,0) EXMPTED_SUPPLIES,"
					+ "IFNULL(NON_GST_SUPPLIES,0) NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM GSTR1_USERINPUT_NILEXTNON WHERE IS_DELETE=FALSE AND "
					+ condition1 + ") GROUP BY DESCRIPTION,DOC_KEY,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY)) "
					+ "GROUP BY DESCRIPTION,DOC_KEY,DERIVED_RET_PERIOD,SUPPLIER_GSTIN,"
					+ "RETURN_PERIOD,DESCRIPTION_KEY ";
		} else {
			LOGGER.debug("Multi Supply Type Answer :" + multiSupplyTypeAns);
			query = "SELECT DESCRIPTION,DOC_KEY,"
					+ "SUM(ASP_NIL_RATED_SUPPLIES) ASP_NIL_RATED_SUPPLIES,"
					+ "SUM(ASP_EXMPTED_SUPPLIES) ASP_EXMPTED_SUPPLIES,"
					+ "SUM(ASP_NON_GST_SUPPLIES)  ASP_NON_GST_SUPPLIES,"
					+ "SUM(UI_NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES,"
					+ "SUM(UI_EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES,"
					+ "SUM(UI_NON_GST_SUPPLIES)  UI_NON_GST_SUPPLIES,"
					+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,DESCRIPTION_KEY FROM ( "
					+ "SELECT DESCRIPTION,DOC_KEY,ASP_NIL_RATED_SUPPLIES,"
					+ "ASP_EXMPTED_SUPPLIES, ASP_NON_GST_SUPPLIES,"
					+ "0 UI_NIL_RATED_SUPPLIES,0 UI_EXMPTED_SUPPLIES,"
					+ "0 UI_NON_GST_SUPPLIES,RETURN_PERIOD,DERIVED_RET_PERIOD,"
					+ "SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM ( SELECT DESCRIPTION,DOC_KEY,"
					+ "IFNULL(SUM(NIL_RATED_SUPPLIES),0) ASP_NIL_RATED_SUPPLIES,"
					+ "IFNULL(SUM(EXMPTED_SUPPLIES),0) ASP_EXMPTED_SUPPLIES,"
					+ "IFNULL(SUM(NON_GST_SUPPLIES),0) ASP_NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM ( SELECT  (CASE WHEN  TABLE_SECTION='8A' "
					+ "THEN  'Inter-State Supplies to Registered Person' "
					+ "WHEN  TABLE_SECTION='8B' "
					+ "THEN  'Intra-State Supplies to Registered Person' "
					+ "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
					+ "WHEN  TABLE_SECTION='8D' "
					+ "THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
					+ "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
					+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') "
					+ "AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) -  "
					+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND SUPPLY_TYPE='NIL' "
					+ "THEN TAXABLE_VALUE END),0) AS NIL_RATED_SUPPLIES,"
					+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') "
					+ "AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) -  "
					+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  "
					+ "AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS EXMPTED_SUPPLIES,"
					+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') "
					+ "AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) "
					+ "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  "
					+ "AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) AS NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY "
					+ "FROM ANX_OUTWARD_DOC_HEADER "
					+ "WHERE TAX_DOC_TYPE IN ('NILEXTNON') "
					+ "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE  "
					+ "AND IS_DELETE = FALSE AND RETURN_TYPE='GSTR1' AND "
					+ condition
					+ "GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION "
					+ "UNION ALL " + "SELECT  (CASE WHEN  TABLE_SECTION='8A' "
					+ "THEN  'Inter-State Supplies to Registered Person' "
					+ "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
					+ "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
					+ "WHEN  TABLE_SECTION='8D' "
					+ "THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
					+ "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
					+ "CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES,"
					+ "CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES,"
					+ "CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE,0) END AS NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY "
					+ "FROM GSTR1_SUMMARY_NILEXTNON "
					+ "WHERE   IS_DELETE = FALSE AND " + condition1
					+ ") GROUP BY DESCRIPTION,SUPPLIER_GSTIN,DERIVED_RET_PERIOD,"
					+ "DESCRIPTION_KEY,RETURN_PERIOD,DOC_KEY ) " + "UNION ALL "
					+ "SELECT DESCRIPTION,DOC_KEY,"
					+ "0 ASP_NIL_RATED_SUPPLIES,0 ASP_EXMPTED_SUPPLIES,"
					+ "0 ASP_NON_GST_SUPPLIES,UI_NIL_RATED_SUPPLIES,"
					+ "UI_EXMPTED_SUPPLIES,UI_NON_GST_SUPPLIES,RETURN_PERIOD,"
					+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM ( SELECT  DESCRIPTION,DOC_KEY,"
					+ "SUM(NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES,"
					+ "SUM(EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES,"
					+ "SUM(NON_GST_SUPPLIES) UI_NON_GST_SUPPLIES,RETURN_PERIOD,"
					+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM ( SELECT  (CASE WHEN DESCRIPTION_KEY='8A' "
					+ "THEN 'Inter-State Supplies to Registered Person' "
					+ "WHEN DESCRIPTION_KEY='8B' THEN 'Intra-State Supplies to Registered Person' "
					+ "WHEN DESCRIPTION_KEY='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
					+ "WHEN DESCRIPTION_KEY='8D' "
					+ "THEN 'Intra-State Supplies to UnRegistered Person' END)DESCRIPTION,"
					+ "DOC_KEY,IFNULL(NIL_RATED_SUPPLIES,0) NIL_RATED_SUPPLIES,"
					+ "IFNULL(EXMPTED_SUPPLIES,0) EXMPTED_SUPPLIES,"
					+ "IFNULL(NON_GST_SUPPLIES,0) NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
					+ "FROM GSTR1_USERINPUT_NILEXTNON WHERE IS_DELETE=FALSE AND "
					+ condition1 + ") GROUP BY DESCRIPTION,DOC_KEY,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY)) "
					+ "GROUP BY DESCRIPTION,DOC_KEY,DERIVED_RET_PERIOD,SUPPLIER_GSTIN,"
					+ "RETURN_PERIOD,DESCRIPTION_KEY ";
		}

		return query;
	}

	public String createQueryStringGstr1A(List<Long> entityId,
			List<String> gstinList, int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		StringBuilder queryBuilder1 = new StringBuilder();
		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND SUPPLIER_GSTIN IN (:sgstin) ");
			queryBuilder1.append(" AND SUPPLIER_GSTIN IN (:sgstin) ");
		}
		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod) ");
			queryBuilder1.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod) ");
		}

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			queryBuilder.append(" AND PROFIT_CENTRE IN :pcList ");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			queryBuilder.append(" AND PLANT_CODE IN :plantList ");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			queryBuilder.append(" AND SALES_ORGANIZATION IN :salesList ");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			queryBuilder.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			queryBuilder.append(" AND DIVISION IN :divisionList ");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			queryBuilder.append(" AND LOCATION IN :locationList ");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& ud1List.size() > 0) {
			queryBuilder.append(" AND USERACCESS1 IN :ud1List ");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& ud2List.size() > 0) {
			queryBuilder.append(" AND USERACCESS2 IN :ud2List ");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& ud3List.size() > 0) {
			queryBuilder.append(" AND USERACCESS3 IN :ud3List ");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& ud4List.size() > 0) {
			queryBuilder.append(" AND USERACCESS4 IN :ud4List ");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& ud5List.size() > 0) {
			queryBuilder.append(" AND USERACCESS5 IN :ud5List ");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& ud6List.size() > 0) {
			queryBuilder.append(" AND USERACCESS6 IN :ud6List ");
		}

		String condition = queryBuilder.toString().substring(4);
		String condition1 = queryBuilder1.toString().substring(4);
		// StringBuilder bufferString = new StringBuilder();
		String query = null;

		query = "SELECT DESCRIPTION,DOC_KEY,"
				+ "SUM(ASP_NIL_RATED_SUPPLIES) ASP_NIL_RATED_SUPPLIES,"
				+ "SUM(ASP_EXMPTED_SUPPLIES) ASP_EXMPTED_SUPPLIES,"
				+ "SUM(ASP_NON_GST_SUPPLIES)  ASP_NON_GST_SUPPLIES,"
				+ "SUM(UI_NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES,"
				+ "SUM(UI_EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES,"
				+ "SUM(UI_NON_GST_SUPPLIES)  UI_NON_GST_SUPPLIES,"
				+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,DESCRIPTION_KEY FROM ( "
				+ "SELECT DESCRIPTION,DOC_KEY,ASP_NIL_RATED_SUPPLIES,"
				+ "ASP_EXMPTED_SUPPLIES, ASP_NON_GST_SUPPLIES,"
				+ "0 UI_NIL_RATED_SUPPLIES,0 UI_EXMPTED_SUPPLIES,"
				+ "0 UI_NON_GST_SUPPLIES,RETURN_PERIOD,DERIVED_RET_PERIOD,"
				+ "SUPPLIER_GSTIN, DESCRIPTION_KEY "
				+ "FROM ( SELECT DESCRIPTION,DOC_KEY,"
				+ "IFNULL(SUM(NIL_RATED_SUPPLIES),0) ASP_NIL_RATED_SUPPLIES,"
				+ "IFNULL(SUM(EXMPTED_SUPPLIES),0) ASP_EXMPTED_SUPPLIES,"
				+ "IFNULL(SUM(NON_GST_SUPPLIES),0) ASP_NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
				+ "FROM ( SELECT  (CASE WHEN  TABLE_SECTION='8A' "
				+ "THEN  'Inter-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8B' "
				+ "THEN  'Intra-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
				+ "WHEN  TABLE_SECTION='8D' "
				+ "THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
				+ "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') "
				+ "AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) -  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND SUPPLY_TYPE='NIL' "
				+ "THEN TAXABLE_VALUE END),0) AS NIL_RATED_SUPPLIES,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') "
				+ "AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) -  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  "
				+ "AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS EXMPTED_SUPPLIES,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') "
				+ "AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) "
				+ "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  "
				+ "AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) AS NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A "
				+ "WHERE TAX_DOC_TYPE IN ('NILEXTNON') "
				+ "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE  "
				+ "AND IS_DELETE = FALSE AND RETURN_TYPE='GSTR1A' AND "
				+ condition
				+ "GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION "
				+ "UNION ALL " + "SELECT  (CASE WHEN  TABLE_SECTION='8A' "
				+ "THEN  'Inter-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
				+ "WHEN  TABLE_SECTION='8D' "
				+ "THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
				+ "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
				+ "CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES,"
				+ "CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES,"
				+ "CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE,0) END AS NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY "
				+ "FROM GSTR1A_SUMMARY_NILEXTNON "
				+ "WHERE   IS_DELETE = FALSE AND " + condition1
				+ ") GROUP BY DESCRIPTION,SUPPLIER_GSTIN,DERIVED_RET_PERIOD,"
				+ "DESCRIPTION_KEY,RETURN_PERIOD,DOC_KEY ) " + "UNION ALL "
				+ "SELECT DESCRIPTION,DOC_KEY,"
				+ "0 ASP_NIL_RATED_SUPPLIES,0 ASP_EXMPTED_SUPPLIES,"
				+ "0 ASP_NON_GST_SUPPLIES,UI_NIL_RATED_SUPPLIES,"
				+ "UI_EXMPTED_SUPPLIES,UI_NON_GST_SUPPLIES,RETURN_PERIOD,"
				+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
				+ "FROM ( SELECT  DESCRIPTION,DOC_KEY,"
				+ "SUM(NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES,"
				+ "SUM(EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES,"
				+ "SUM(NON_GST_SUPPLIES) UI_NON_GST_SUPPLIES,RETURN_PERIOD,"
				+ "DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
				+ "FROM ( SELECT  (CASE WHEN DESCRIPTION_KEY='8A' "
				+ "THEN 'Inter-State Supplies to Registered Person' "
				+ "WHEN DESCRIPTION_KEY='8B' THEN 'Intra-State Supplies to Registered Person' "
				+ "WHEN DESCRIPTION_KEY='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				+ "WHEN DESCRIPTION_KEY='8D' "
				+ "THEN 'Intra-State Supplies to UnRegistered Person' END)DESCRIPTION,"
				+ "DOC_KEY,IFNULL(NIL_RATED_SUPPLIES,0) NIL_RATED_SUPPLIES,"
				+ "IFNULL(EXMPTED_SUPPLIES,0) EXMPTED_SUPPLIES,"
				+ "IFNULL(NON_GST_SUPPLIES,0) NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
				+ "FROM GSTR1A_USERINPUT_NILEXTNON WHERE IS_DELETE=FALSE AND "
				+ condition1 + ") GROUP BY DESCRIPTION,DOC_KEY,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY)) "
				+ "GROUP BY DESCRIPTION,DOC_KEY,DERIVED_RET_PERIOD,SUPPLIER_GSTIN,"
				+ "RETURN_PERIOD,DESCRIPTION_KEY ";

		return query;
	}

	public static void main(String[] args) {

	}

	public void saveNilExmpNonGstStauts(
			List<Gstr1AUserInputNilExtnOnEntity> entites) {
		if (CollectionUtils.isNotEmpty(entites)) {
			entites.forEach(entity -> repository.updateAllToDelete(
					entity.getReturnPeriod(), entity.getSupplierGstin(),
					entity.getDocKey()));
			repository.saveAll(entites);
		}
	}

	public List<Gstr1NilExmpNonGstSummaryStatusRespDto> loadNillExmpNonGstSummaryRecords(
			Gstr1ProcessedRecordsReqDto reqDto) {

		List<Long> entityId = reqDto.getEntityId();
		String taxPeriod = reqDto.getRetunPeriod();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1NilExmpNonGstFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
					reqDto);
		}
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;
		String cgstin = null;

		List<String> gstinList = null;
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

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					pcList = dataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					divisionList = dataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					locationList = dataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					salesList = dataSecAttrs.get(OnboardingConstant.SO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					distList = dataSecAttrs.get(OnboardingConstant.DC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		int taxPeriod1 = 0;
		if (taxPeriod != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriod);
		}

		
		StringBuilder queryStr = createSummaryQueryString(entityId, gstinList,
				taxPeriod1, taxPeriod, dataSecAttrs, profitCenter, sgstin,
				cgstin, plant, division, location, sales, distChannel, ud1, ud2,
				ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
				locationList, distList, ud1List, ud2List, ud3List, ud4List,
				ud5List, ud6List);

		LOGGER.debug("outQueryStr-->" + queryStr);

		List<Gstr1NilExmpNonGstSummaryStatusRespDto> finalDtoList = new ArrayList<>();
		try {

			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (taxPeriod1 != 0) {
				Q.setParameter("taxPeriod", taxPeriod1);
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				Q.setParameter("sgstin", gstinList);
			}
			if (profitCenter != null && !profitCenter.isEmpty()
					&& !profitCenter.isEmpty() && pcList != null
					&& pcList.size() > 0) {
				Q.setParameter("pcList", pcList);
			}
			if (plant != null && !plant.isEmpty() && !plant.isEmpty()
					&& plantList != null && plantList.size() > 0) {
				Q.setParameter("plantList", plantList);
			}
			if (sales != null && !sales.isEmpty() && salesList != null
					&& salesList.size() > 0) {
				Q.setParameter("salesList", salesList);
			}
			if (division != null && !division.isEmpty() && divisionList != null
					&& divisionList.size() > 0) {
				Q.setParameter("divisionList", divisionList);
			}
			if (location != null && !location.isEmpty() && locationList != null
					&& locationList.size() > 0) {
				Q.setParameter("locationList", locationList);
			}
			if (distChannel != null && !distChannel.isEmpty()
					&& distList != null && distList.size() > 0) {
				Q.setParameter("distList", distList);
			}
			if (ud1 != null && !ud1.isEmpty() && ud1List != null
					&& ud1List.size() > 0) {
				Q.setParameter("ud1List", ud1List);
			}
			if (ud2 != null && !ud2.isEmpty() && ud2List != null
					&& ud2List.size() > 0) {
				Q.setParameter("ud2List", ud2List);
			}
			if (ud3 != null && !ud3.isEmpty() && ud3List != null
					&& ud3List.size() > 0) {
				Q.setParameter("ud3List", ud3List);
			}
			if (ud4 != null && !ud4.isEmpty() && ud4List != null
					&& ud4List.size() > 0) {
				Q.setParameter("ud4List", ud4List);
			}
			if (ud5 != null && !ud5.isEmpty() && ud5List != null
					&& ud5List.size() > 0) {
				Q.setParameter("ud5List", ud5List);
			}
			if (ud6 != null && !ud6.isEmpty() && ud6List != null
					&& ud6List.size() > 0) {
				Q.setParameter("ud6List", ud6List);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> Qlist = Q.getResultList();
			LOGGER.error("bufferString-------------------------->" + Qlist);
			List<Gstr1NilExmpNonGstSummaryStatusRespDto> outwardFinalList = convertGstr1SummaryRecordsIntoObject(
					Qlist);
			finalDtoList.addAll(outwardFinalList);
			LOGGER.debug("Data list from database is-->" + Qlist);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr1NilExmpNonGstSummaryStatusRespDto> convertGstr1SummaryRecordsIntoObject(
			List<Object[]> savedDataList) {
		List<Gstr1NilExmpNonGstSummaryStatusRespDto> summaryList = new ArrayList<Gstr1NilExmpNonGstSummaryStatusRespDto>();
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr1NilExmpNonGstSummaryStatusRespDto dto = new Gstr1NilExmpNonGstSummaryStatusRespDto();
				dto.setType(String.valueOf(data[0]));
				dto.setCount(Integer.valueOf(data[1].toString()));
				dto.setNilAmount(
						data[2] != null && !data[2].toString().isEmpty()
								? (BigDecimal) data[2] : BigDecimal.ZERO);
				dto.setExtAmount(
						data[3] != null && !data[3].toString().isEmpty()
								? (BigDecimal) data[3] : BigDecimal.ZERO);
				dto.setNonAmount(
						data[4] != null && !data[4].toString().isEmpty()
								? (BigDecimal) data[4] : BigDecimal.ZERO);
				
				if(dto.getType().equalsIgnoreCase("TRANSACTIONAL"))
					dto.setOrder("A");
				else if (dto.getType().equalsIgnoreCase("TOTAL"))
					dto.setOrder("C");
				else
					dto.setOrder("B");
					
				
				summaryList.add(dto);
			}
		}
		return summaryList;

	}

	public StringBuilder createSummaryQueryString(List<Long> entityId,
			List<String> gstinList, int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		StringBuilder queryVertBuilder = new StringBuilder();
		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod)");
			queryVertBuilder.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod)");
		}

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND SUPPLIER_GSTIN IN (:sgstin)");
			queryVertBuilder.append(" AND SUPPLIER_GSTIN IN (:sgstin)");
		}

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			queryBuilder.append(" AND PROFIT_CENTRE IN :pcList");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			queryBuilder.append(" AND PLANT_CODE IN :plantList");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			queryBuilder.append(" AND SALES_ORGANIZATION IN :salesList");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			queryBuilder.append(" AND DISTRIBUTION_CHANNEL IN :distList");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			queryBuilder.append(" AND DIVISION IN :divisionList");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			queryBuilder.append(" AND LOCATION IN :locationList");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& ud1List.size() > 0) {
			queryBuilder.append(" AND USERACCESS1 IN :ud1List");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& ud2List.size() > 0) {
			queryBuilder.append(" AND USERACCESS2 IN :ud2List");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& ud3List.size() > 0) {
			queryBuilder.append(" AND USERACCESS3 IN :ud3List");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& ud4List.size() > 0) {
			queryBuilder.append(" AND USERACCESS4 IN :ud4List");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& ud5List.size() > 0) {
			queryBuilder.append(" AND USERACCESS5 IN :ud5List");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& ud6List.size() > 0) {
			queryBuilder.append(" AND USERACCESS6 IN :ud6List");
		}

		String condition = queryBuilder.toString();
		String vertCondition = queryVertBuilder.toString();
		StringBuilder bufferString = new StringBuilder();

		bufferString.append("WITH TRANS AS (" + "       SELECT "
				+ "          DOC_KEY AS KEYCOUNT, "
				+ "          SUPPLIER_GSTIN, "
				+ "          DERIVED_RET_PERIOD, " + "          IFNULL("
				+ "            SUM("
				+ "              CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ "              AND SUPPLY_TYPE = 'NIL' THEN TAXABLE_VALUE END"
				+ "            ), " + "            0" + "          ) - IFNULL("
				+ "            SUM("
				+ "              CASE WHEN DOC_TYPE = 'CR' "
				+ "              AND SUPPLY_TYPE = 'NIL' THEN TAXABLE_VALUE END"
				+ "            ), " + "            0"
				+ "          ) AS NIL_AMT, " + "          IFNULL("
				+ "            SUM("
				+ "              CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ "              AND SUPPLY_TYPE = 'EXT' THEN TAXABLE_VALUE END"
				+ "            ), " + "            0" + "          ) - IFNULL("
				+ "            SUM("
				+ "              CASE WHEN DOC_TYPE = 'CR' "
				+ "              AND SUPPLY_TYPE = 'EXT' THEN TAXABLE_VALUE END"
				+ "            ), " + "            0"
				+ "          ) AS EXMPTED_AMT, " + "          IFNULL("
				+ "            SUM("
				+ "              CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ "              AND SUPPLY_TYPE IN ('NON', 'SCH3') THEN TAXABLE_VALUE END"
				+ "            ), " + "            0" + "          ) - IFNULL("
				+ "            SUM("
				+ "              CASE WHEN DOC_TYPE = 'CR' "
				+ "              AND SUPPLY_TYPE IN ('NON', 'SCH3') THEN TAXABLE_VALUE END"
				+ "            ), " + "            0"
				+ "          ) AS NON_GST_SUPPLIES_AMT " + "        FROM "
				+ "          ANX_OUTWARD_DOC_HEADER_1A " + "        WHERE " +
				/*
				 * "          DERIVED_RET_PERIOD IN (:taxPeriod) " +
				 * "          AND SUPPLIER_GSTIN IN (:sgstin) " +
				 */
				"          ASP_INVOICE_STATUS = 2 "
				+ "          AND COMPLIANCE_APPLICABLE = TRUE "
				+ "          AND IS_DELETE = FALSE "
				+ "          AND TAX_DOC_TYPE IN ('NILEXTNON') " + condition
				+ "        GROUP BY " + "          DOC_KEY, "
				+ "          SUPPLIER_GSTIN, " + "          DERIVED_RET_PERIOD"
				+ "), " + "VER AS (" + "    SELECT "
				+ "          N_INVKEY AS KEYCOUNT, "
				+ "          SUPPLIER_GSTIN, "
				+ "          DERIVED_RET_PERIOD, "
				+ "          CASE WHEN SUPPLY_TYPE = 'NIL' THEN IFNULL(TAXABLE_VALUE, 0) END AS NIL_AMT, "
				+ "          CASE WHEN SUPPLY_TYPE = 'EXT' THEN IFNULL(TAXABLE_VALUE, 0) END AS EXMPTED_AMT, "
				+ "          CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE, 0) END AS NON_GST_SUPPLIES_AMT "
				+ "        FROM " + "          GSTR1A_SUMMARY_NILEXTNON "
				+ "        WHERE " + "          IS_DELETE = FALSE "
				+ vertCondition +
				/*
				 * "          AND DERIVED_RET_PERIOD IN (:taxPeriod) " +
				 * "          AND SUPPLIER_GSTIN IN (:sgstin)" +
				 */
				") " + "SELECT " + "  TYPE, " + "  SUM(KEYCOUNT) COUNT, "
				+ "  SUM(NIL_AMT) NIL_AMT, "
				+ "  SUM(EXMPTED_AMT) EXMPTED_AMT, "
				+ "  SUM(NON_GST_SUPPLIES_AMT) NON_GST_SUPPLIES_AMT " + "FROM "
				+ "  (" + "      SELECT " + "      'TOTAL' AS TYPE, "
				+ "      COUNT(KEYCOUNT) KEYCOUNT, "
				+ "      SUM(NIL_AMT) NIL_AMT, "
				+ "      SUM(EXMPTED_AMT) EXMPTED_AMT, "
				+ "      SUM(NON_GST_SUPPLIES_AMT) NON_GST_SUPPLIES_AMT "
				+ "      FROM TRANS " + "      UNION ALL " + "      SELECT "
				+ "      'TOTAL' AS TYPE, "
				+ "      COUNT (DISTINCT KEYCOUNT) AS KEYCOUNT, "
				+ "      SUM(NIL_AMT) NIL_AMT, "
				+ "      SUM(EXMPTED_AMT) EXMPTED_AMT, "
				+ "      SUM(NON_GST_SUPPLIES_AMT) NON_GST_SUPPLIES_AMT "
				+ "      FROM VER " + "      GROUP BY " + "      KEYCOUNT "
				+ "      UNION ALL " + "      SELECT "
				+ "      'TRANSACTIONAL' AS TYPE, "
				+ "      COUNT(KEYCOUNT) KEYCOUNT, "
				+ "      SUM(NIL_AMT) NIL_AMT, "
				+ "      SUM(EXMPTED_AMT) EXMPTED_AMT, "
				+ "      SUM(NON_GST_SUPPLIES_AMT) NON_GST_SUPPLIES_AMT "
				+ "      FROM TRANS " + "      UNION ALL " + "      SELECT "
				+ "      'VERTICAL' AS TYPE, "
				+ "      COUNT (DISTINCT KEYCOUNT) AS KEYCOUNT, "
				+ "      SUM(NIL_AMT) NIL_AMT, "
				+ "      SUM(EXMPTED_AMT) EXMPTED_AMT, "
				+ "      SUM(NON_GST_SUPPLIES_AMT) NON_GST_SUPPLIES_AMT "
				+ "      FROM VER " + "      GROUP BY " + "      KEYCOUNT "
				+ ")" + "GROUP BY TYPE ");

		LOGGER.debug(
				"Gstr1 NilExmpNonGst Query from database is-->" + bufferString);

		LOGGER.error("bufferString-------------------------->" + bufferString);
		return bufferString;
	}

	public List<Gstr1NilExmpNonGstVerticalStatusRespDto> loadNillExmpNonGstVerticalRecords(
			Gstr1ProcessedRecordsReqDto reqDto) {

		List<Long> entityId = reqDto.getEntityId();
		String taxPeriod = reqDto.getRetunPeriod();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1NilExmpNonGstFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
					reqDto);
		}
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;
		String cgstin = null;

		List<String> gstinList = null;
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

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					pcList = dataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					divisionList = dataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					locationList = dataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					salesList = dataSecAttrs.get(OnboardingConstant.SO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					distList = dataSecAttrs.get(OnboardingConstant.DC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		int taxPeriod1 = 0;
		if (taxPeriod != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriod);
		}

		StringBuilder queryStr = createVerticalQueryString(entityId, gstinList,
				taxPeriod1, taxPeriod, dataSecAttrs, profitCenter, sgstin,
				cgstin, plant, division, location, sales, distChannel, ud1, ud2,
				ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
				locationList, distList, ud1List, ud2List, ud3List, ud4List,
				ud5List, ud6List);

		LOGGER.debug("outQueryStr-->" + queryStr);

		List<Gstr1NilExmpNonGstVerticalStatusRespDto> finalDtoList = new ArrayList<>();
		try {

			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (taxPeriod1 != 0) {
				Q.setParameter("taxPeriod", taxPeriod1);
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				Q.setParameter("sgstin", gstinList);
			}
		
			@SuppressWarnings("unchecked")
			List<Object[]> Qlist = Q.getResultList();
			LOGGER.error("bufferString-------------------------->" + Qlist);
			List<Gstr1NilExmpNonGstVerticalStatusRespDto> outwardFinalList = convertGstr1NilnonVerticalRecordsIntoObject(
					Qlist);
			finalDtoList.addAll(outwardFinalList);
			LOGGER.debug("Data list from database is-->" + Qlist);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr1NilExmpNonGstVerticalStatusRespDto> convertGstr1NilnonVerticalRecordsIntoObject(
			List<Object[]> savedDataList) {
		List<Gstr1NilExmpNonGstVerticalStatusRespDto> summaryList = new ArrayList<Gstr1NilExmpNonGstVerticalStatusRespDto>();
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr1NilExmpNonGstVerticalStatusRespDto dto = new Gstr1NilExmpNonGstVerticalStatusRespDto();
				dto.setHsn(data[0] != null ? data[0].toString() : null);
				dto.setDesc(data[1] != null ? data[1].toString() : null);
				dto.setUqc(data[2] != null ? data[2].toString() : null);
				dto.setQunty(data[3] != null ? data[3].toString() : null);
				dto.setNilInterReg(
						data[4] != null && !data[4].toString().isEmpty()
								? (BigDecimal) data[4] : BigDecimal.ZERO);
				dto.setNilIntraReg(
						data[5] != null && !data[5].toString().isEmpty()
								? (BigDecimal) data[5] : BigDecimal.ZERO);
				dto.setNilInterUnreg(
						data[6] != null && !data[6].toString().isEmpty()
								? (BigDecimal) data[6] : BigDecimal.ZERO);
				dto.setNilIntraUnreg(
						data[7] != null && !data[7].toString().isEmpty()
								? (BigDecimal) data[7] : BigDecimal.ZERO);
				dto.setExtInterReg(
						data[8] != null && !data[8].toString().isEmpty()
								? (BigDecimal) data[8] : BigDecimal.ZERO);
				dto.setExtIntraReg(
						data[9] != null && !data[9].toString().isEmpty()
								? (BigDecimal) data[9] : BigDecimal.ZERO);
				dto.setExtInterUnreg(
						data[10] != null && !data[10].toString().isEmpty()
								? (BigDecimal) data[10] : BigDecimal.ZERO);
				dto.setExtIntraUnreg(
						data[11] != null && !data[11].toString().isEmpty()
								? (BigDecimal) data[11] : BigDecimal.ZERO);
				dto.setNonInterReg(
						data[12] != null && !data[12].toString().isEmpty()
								? (BigDecimal) data[12] : BigDecimal.ZERO);
				dto.setNonIntraReg(
						data[13] != null && !data[13].toString().isEmpty()
								? (BigDecimal) data[13] : BigDecimal.ZERO);
				dto.setNonInterUnreg(
						data[14] != null && !data[14].toString().isEmpty()
								? (BigDecimal) data[14] : BigDecimal.ZERO);
				dto.setNonIntraUnreg(
						data[15] != null && !data[15].toString().isEmpty()
								? (BigDecimal) data[15] : BigDecimal.ZERO);
				dto.setId(data[16] != null ? data[16].toString() : null);
				dto.setDocKey(data[17] != null ? data[17].toString() : null);
				summaryList.add(dto);
			}
		}
		return summaryList;

	}

	public StringBuilder createVerticalQueryString(List<Long> entityId,
			List<String> gstinList, int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod)");
		}

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND SUPPLIER_GSTIN IN :sgstin");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString
				.append(" SELECT ITM_HSNSAC, ITM_DESCRIPTION,ITM_UQC,ITM_QTY,"
						+ " SUM(NIL_INTERSTATE_REG) AS NIL_INTERSTATE_REG, "
						+ "SUM(NIL_INTRASTATE_REG) AS NIL_INTRASTATE_REG, "
						+ "SUM(NIL_INTERSTATE_UNREG) AS NIL_INTERSTATE_UNREG, "
						+ "SUM(NIL_INTRASTATE_UNREG) AS NIL_INTRASTATE_UNREG, "
						+ "SUM(EXT_INTERSTATE_REG) AS EXT_INTERSTATE_REG, "
						+ "SUM(EXT_INTRASTATE_REG) AS EXT_INTRASTATE_REG, "
						+ "SUM(EXT_INTERSTATE_UNREG) AS EXT_INTERSTATE_UNREG, "
						+ "SUM(EXT_INTRASTATE_UNREG) AS EXT_INTRASTATE_UNREG, "
						+ "SUM(NON_INTERSTATE_REG) AS NON_INTERSTATE_REG, "
						+ "SUM(NON_INTRASTATE_REG) AS NON_INTRASTATE_REG, "
						+ "SUM(NON_INTERSTATE_UNREG) AS NON_INTERSTATE_UNREG, "
						+ "SUM(NON_INTRASTATE_UNREG) AS NON_INTRASTATE_UNREG,id, DOC_KEY FROM"
						+ " ( select ITM_HSNSAC,"
						+ " ITM_DESCRIPTION,ITM_UQC,ITM_QTY, IFNULL(SUM"
						+ "(NIL_INTERSTATE_REG),0) AS NIL_INTERSTATE_REG, "
						+ "IFNULL(SUM(NIL_INTRASTATE_REG),0) AS NIL_INTRASTATE_REG,"
						+ " IFNULL(SUM(NIL_INTERSTATE_UNREG),0) AS "
						+ "NIL_INTERSTATE_UNREG, IFNULL(SUM(NIL_INTRASTATE_UNREG),0)"
						+ " AS NIL_INTRASTATE_UNREG, IFNULL(SUM(NON_INTERSTATE_REG),0)"
						+ " AS NON_INTERSTATE_REG, IFNULL(SUM(NON_INTRASTATE_REG),0)"
						+ " AS NON_INTRASTATE_REG, IFNULL(SUM(NON_INTERSTATE_UNREG),0)"
						+ " AS NON_INTERSTATE_UNREG, IFNULL(SUM(NON_INTRASTATE_UNREG)"
						+ ",0) AS NON_INTRASTATE_UNREG, IFNULL(SUM(EXT_INTERSTATE_REG)"
						+ ",0) AS EXT_INTERSTATE_REG, IFNULL(SUM(EXT_INTRASTATE_REG)"
						+ ",0) AS EXT_INTRASTATE_REG, IFNULL(SUM(EXT_INTERSTATE_UNREG)"
						+ ",0) AS EXT_INTERSTATE_UNREG, IFNULL(SUM"
						+ "(EXT_INTRASTATE_UNREG),0) AS EXT_INTRASTATE_UNREG,id,"
						+ "N_INVKEY as DOC_KEY  FROM "
						+ "GSTR1A_PROCESSED_NILEXTNON WHERE IS_DELETE = FALSE ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" GROUP BY "
				+ "id,ITM_HSNSAC,ITM_DESCRIPTION,ITM_UQC,ITM_QTY,N_INVKEY)GROUP BY "
				+ "id,ITM_HSNSAC,ITM_DESCRIPTION,ITM_UQC,ITM_QTY,DOC_KEY ");

		LOGGER.debug(
				"Gstr1 NilExmpNonGst Query from database is-->" + bufferString);

		LOGGER.error("bufferString-------------------------->" + bufferString);
		return bufferString;
	}

	public void saveNilExmpNonVerticalStauts(
			List<Gstr1ANilDetailsEntity> entites) {
		if (CollectionUtils.isNotEmpty(entites)) {
			entites.forEach(entity -> {
				Long entityId = entity.getId();
				if (entityId != null && entityId != 0L) {
					Optional<Gstr1ANilDetailsEntity> dbEntity = gstr1NilRepository
							.findById(entity.getId());
					if (dbEntity.isPresent()) {
						Gstr1ANilDetailsEntity detailsEntity = dbEntity.get();
						detailsEntity.setHsn(entity.getHsn());
						detailsEntity.setReturnPeriod(entity.getReturnPeriod());
						detailsEntity.setDerivedRetPeriod(
								GenUtil.convertTaxPeriodToInt(
										entity.getReturnPeriod()));
						detailsEntity.setSgstin(entity.getSgstin());
						detailsEntity.setId(entity.getId());
						detailsEntity.setHsn(entity.getHsn());
						detailsEntity.setDescription(entity.getDescription());
						detailsEntity.setQnt(entity.getQnt());
						detailsEntity.setNilInterReg(entity.getNilInterReg());
						detailsEntity.setNilIntraReg(entity.getNilIntraReg());
						detailsEntity
								.setNilInterUnReg(entity.getNilInterUnReg());
						detailsEntity
								.setNilIntraUnReg(entity.getNilIntraUnReg());
						detailsEntity.setExtInterReg(entity.getExtInterReg());
						detailsEntity.setExtIntraReg(entity.getExtIntraReg());
						detailsEntity
								.setExtInterUnReg(entity.getExtInterUnReg());
						detailsEntity
								.setExtIntraUnReg(entity.getExtIntraUnReg());
						detailsEntity.setNonInterReg(entity.getNonInterReg());
						detailsEntity.setNonIntraReg(entity.getNonIntraReg());
						detailsEntity
								.setNonInterUnReg(entity.getNonInterUnReg());
						detailsEntity
								.setNonIntraUnReg(entity.getNonIntraUnReg());
						detailsEntity.setCreatedBy("SYSTEM");
						detailsEntity.setCreatedOn(LocalDateTime.now());
						detailsEntity.setModifiedBy("SYSTEM");
						detailsEntity.setModifiedOn(LocalDateTime.now());
						detailsEntity.setDelete(false);
						detailsEntity.setNKey(entity.getNKey());

						gstr1NilRepository.save(detailsEntity);
					}
				} else {
					StringBuffer nKey = new StringBuffer();
					nKey.append(entity.getSgstin()).append("|")
							.append(entity.getReturnPeriod()).append("|")
							.append(entity.getDescription()).append("|")
							.append(entity.getUqc());
					entity.setNKey(nKey.toString());
					entity.setId(null);
					gstr1NilRepository.save(entity);
				}
			});
		}
	}

}