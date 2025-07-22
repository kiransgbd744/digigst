/**
 * 
 */
package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author BalaKrishna S
 *
 */
@Service("BasicDocSummaryScreenHsnSectionDaoImpl")
public class BasicDocSummaryScreenHsnSectionDaoImpl
        implements BasicGstr1DocSummaryScreenHsnSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;
	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final Logger LOGGER = LoggerFactory
	        .getLogger(BasicDocSummaryScreenHsnSectionDaoImpl.class);

	private static final String CONF_KEY = "hsn.section.derived.taxperiod";
	private static final String CONF_CATEG = "HSN_SECTION";
	
	@Override
	public List<Gstr1SummaryCDSectionDto> loadBasicSummarySection(
	        Annexure1SummaryReqDto request) {

		if (Strings.isNullOrEmpty(request.getReturnType())) {
			request.setReturnType(APIConstants.GSTR1.toUpperCase());
		}

		Annexure1SummaryReqDto req = basicCommonSecParam
		        .setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();
		boolean isGstr1a = req.getIsGstr1a();
		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(taxPeriodReq);
		List<Long> entityId = req.getEntityId();
		LocalDate docFromDate = req.getDocFromDate();
		LocalDate docToDate = req.getDocToDate();
		List<String> einvGenerated = req.getEINVGenerated();
		List<String> ewbGenerated = req.getEWBGenerated();

		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}

		String ewbGen = null;
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			ewbGen = ewbGenerated.get(0);
		}
		String ewbResp = null;
		if (ewbGen != null) {
			if (ewbGen.equalsIgnoreCase("NO")) {
				ewbResp = "IS NULL";
			} else if (ewbGen.equalsIgnoreCase("YES") && ewbGen != null) {
				ewbResp = "IS NOT NULL";
			}
		}
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String gstin = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
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

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
					        && dataSecAttrs.get(OnboardingConstant.GSTIN)
					                .size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
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
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
					        && dataSecAttrs.get(OnboardingConstant.PO)
					                .size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
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
		StringBuilder build = new StringBuilder();
		StringBuilder build1 = new StringBuilder();
		StringBuilder buildB2cs = new StringBuilder();
		StringBuilder build2 = new StringBuilder();

		StringBuilder buildSupplyType = new StringBuilder();
		StringBuilder buildDocType = new StringBuilder();

		String supplyType = loadOnStartSupplyType(entityId);
		String docType = loadOnStartDocType(entityId);

		if ("A".equalsIgnoreCase(docType)) {
			buildDocType.append("WHERE DOC_TYPE IN ('INV','BOS','CR','DR')  ");
		} else if ("B".equalsIgnoreCase(docType)) {
			buildDocType.append("WHERE DOC_TYPE IN ('INV','BOS')  ");
		} else if ("C".equalsIgnoreCase(docType)) {
			buildDocType.append("WHERE DOC_TYPE IN ('INV','BOS','CR')  ");
		} else if ("D".equalsIgnoreCase(docType)) {
			buildDocType.append("WHERE  DOC_TYPE IN ('INV','BOS','DR') ");
		} else if ("E".equalsIgnoreCase(docType)) {
			buildDocType.append("WHERE DOC_TYPE IN ('CR','DR') ");
		}

		String docTypeQuery = buildDocType.toString();

		StringBuilder buildHdr1SupplyType = new StringBuilder();

		if (supplyType == null || supplyType.isEmpty()) {
			buildSupplyType.append(
			        " AND SUPPLY_TYPE NOT IN ('NON','SCH3','NIL','EXT') ");
			buildHdr1SupplyType.append(
			        " OR ITM.SUPPLY_TYPE NOT IN ('NON','SCH3','NIL','EXT') ) AND ");
		}
		if ("A".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('NIL','EXT') ");
			buildHdr1SupplyType
			        .append(" OR ITM.SUPPLY_TYPE NOT IN ('NIL','EXT') ) AND ");
		} else if ("B".equalsIgnoreCase(supplyType)) {
			buildSupplyType
			        .append(" AND SUPPLY_TYPE NOT IN ('NON','SCH3','EXT') ");
			buildHdr1SupplyType.append(
			        " OR ITM.SUPPLY_TYPE NOT IN ('NON','SCH3','EXT') ) AND ");
		} else if ("C".equalsIgnoreCase(supplyType)) {
			buildSupplyType
			        .append(" AND SUPPLY_TYPE NOT IN ('NON','SCH3','NIL') ");
			buildHdr1SupplyType.append(
			        " OR ITM.SUPPLY_TYPE NOT IN ('NON','SCH3','NIL') ) AND ");
		} else if ("A*B".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('EXT') ");
			buildHdr1SupplyType
			        .append(" OR ITM.SUPPLY_TYPE NOT IN ('EXT') ) AND ");
		} else if ("A*C".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('NIL') ");
			buildHdr1SupplyType
			        .append(" OR ITM.SUPPLY_TYPE NOT IN ('NIL') ) AND  ");
		} else if ("B*C".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE NOT IN ('NON','SCH3') ");
			buildHdr1SupplyType
			        .append(" OR ITM.SUPPLY_TYPE NOT IN ('NON','SCH3') ) AND ");
		} else if ("A*B*C".equalsIgnoreCase(supplyType)) {
			buildSupplyType.append(" AND SUPPLY_TYPE IS NOT NULL ");
			buildHdr1SupplyType
			        .append(" OR ITM.SUPPLY_TYPE IS NOT NULL ) AND ");
		}

		String supplyTypeQuery = buildSupplyType.toString();

		String hdrBuildSupplyType = buildHdr1SupplyType.toString();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND HDR.SUPPLIER_GSTIN IN (:gstinList) ");
				build1.append(" AND SUPPLIER_GSTIN IN (:gstinList) ");
				buildB2cs.append(" AND HDR.SUPPLIER_GSTIN IN (:gstinList) ");
				build2.append(" AND SUPPLIER_GSTIN IN (:gstinList) ");
			}
		}
		if (docFromDate != null && docToDate != null) {
			build.append(
			        " AND HDR.DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}

		if (ewbGen != null && ewbGen.equalsIgnoreCase("YES")) {
			build.append(" AND HDR.EWB_NO_RESP IS NOT NULL ");
		}
		if (ewbResp != null && ewbGen.equalsIgnoreCase("NO")) {
			build.append(" AND HDR.EWB_NO_RESP IS NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			build.append(" AND HDR.IRN_RESPONSE IS NOT NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			build.append(" AND HDR.IRN_RESPONSE IS NULL ");
		}

		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE IN (:pcList) ");
				build1.append(" AND PROFIT_CENTRE IN (:pcList) ");
				buildB2cs.append(" AND HDR.PROFIT_CENTRE IN (:pcList) ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND HDR.PLANT_CODE IN (:plantList) ");
				build1.append(" AND PLANT_CODE IN (:plantList) ");
				buildB2cs.append(" AND HDR.PLANT_CODE IN (:plantList) ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND HDR.SALES_ORGANIZATION IN (:salesList) ");
				build1.append(" AND SALES_ORGANIZATION IN (:salesList) ");
				buildB2cs
				        .append(" AND HDR.SALES_ORGANIZATION IN (:salesList) ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND HDR.PURCHASE_ORGANIZATION IN (:purcList) ");
				build1.append(" AND PURCHASE_ORGANIZATION IN (:purcList) ");
				buildB2cs.append(
				        " AND HDR.PURCHASE_ORGANIZATION IN (:purcList) ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND HDR.DISTRIBUTION_CHANNEL IN (:distList) ");
				build1.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
				buildB2cs.append(
				        " AND HDR.DISTRIBUTION_CHANNEL IN (:distList) ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN (:divisionList) ");
				build1.append(" AND DIVISION IN (:divisionList) ");
				buildB2cs.append(" AND HDR.DIVISION IN (:divisionList) ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND HDR.LOCATION IN (:locationList) ");
				build1.append(" AND LOCATION IN (:locationList) ");
				buildB2cs.append(" AND HDR.LOCATION IN (:locationList) ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND HDR.USERACCESS1 IN (:ud1List) ");
				build1.append(" AND USERACCESS1 IN (:ud1List) ");
				buildB2cs.append(" AND HDR.USERACCESS1 IN (:ud1List) ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND HDR.USERACCESS2 IN (:ud2List) ");
				build1.append(" AND USERACCESS2 IN (:ud2List) ");
				buildB2cs.append(" AND HDR.USERACCESS2 IN (:ud2List) ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND HDR.USERACCESS3 IN (:ud3List) ");
				build1.append(" AND USERACCESS3 IN (:ud3List) ");
				buildB2cs.append(" AND HDR.USERACCESS3 IN (:ud3List) ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND HDR.USERACCESS4 IN (:ud4List) ");
				build1.append(" AND USERACCESS4 IN (:ud4List) ");
				buildB2cs.append(" AND HDR.USERACCESS4 IN (:ud4List)");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND HDR.USERACCESS5 IN (:ud5List)");
				build1.append(" AND USERACCESS5 IN (:ud5List)");
				buildB2cs.append(" AND HDR.USERACCESS5 IN (:ud5List)");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND HDR.USERACCESS6 IN (:ud6List)");
				build1.append(" AND USERACCESS6 IN (:ud6List)");
				buildB2cs.append(" AND HDR.USERACCESS6 IN (:ud6List) ");
			}
		}
		if (taxPeriod != 0) {

			build.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			build1.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildB2cs.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			build2.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString().substring(4);
		String buildQueryB2cs = build1.toString().substring(4);
		String buildQueryVertical = buildB2cs.toString().substring(4);
		String buildQueryNil = build2.toString().substring(4);

		String queryStr = null;
		if (Boolean.TRUE.equals(isGstr1a)) {
			request.setReturnType(APIConstants.GSTR1A);
		}


		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		
		Integer hsnTaxPeriod = 202501;
		if (config != null)
			hsnTaxPeriod = Integer.valueOf(config.getValue());
		Boolean hsnFlag = false ;
		if(taxPeriod > hsnTaxPeriod)
			hsnFlag = true;
		else 
			hsnFlag = false;
		if (!Strings.isNullOrEmpty(request.getReturnType())
		        && APIConstants.GSTR1A
		                .equalsIgnoreCase(request.getReturnType())) {
			queryStr = createQueryStringForGstr1a(buildQuery, buildQueryB2cs,
			        buildQueryNil, buildQueryVertical, supplyTypeQuery,
			        docTypeQuery, hdrBuildSupplyType, rateIncludedInHsn,hsnFlag);

		} else {
			queryStr = createQueryString(buildQuery, buildQueryB2cs,
			        buildQueryNil, buildQueryVertical, supplyTypeQuery,
			        docTypeQuery, hdrBuildSupplyType, rateIncludedInHsn,hsnFlag);

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is --> {} ", queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);
			if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
				if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
					q.setParameter("pcList", pcList);
				}
			}
			if (docFromDate != null && docToDate != null) {
				q.setParameter("docFromDate", docFromDate);
				q.setParameter("docToDate", docToDate);
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
			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
				        && gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
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
			if (purchase != null && !purchase.isEmpty()) {
				if (purcList != null && !purcList.isEmpty()
				        && purcList.size() > 0) {
					q.setParameter("purcList", purcList);
				}
			}
			if (distChannel != null && !distChannel.isEmpty()) {
				if (distList != null && !distList.isEmpty()
				        && distList.size() > 0) {
					q.setParameter("distList", distList);
				}
			}
			if (ud1 != null && !ud1.isEmpty()) {
				if (ud1List != null && !ud1List.isEmpty()
				        && ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty()
				        && ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty()
				        && ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty()
				        && ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty()
				        && ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty()
				        && ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}
			List<Object[]> list = q.getResultList();

			if (LOGGER.isDebugEnabled()) {
			    for (Object[] objArray : list) {
			        LOGGER.debug("Record data: {}", Arrays.toString(objArray));
			    }
			}
			List<Gstr1SummaryCDSectionDto> retList = list.parallelStream()
			        .map(o -> convert(o))
			        .collect(Collectors.toCollection(ArrayList::new));
			if (LOGGER.isDebugEnabled()) {
			    for (Gstr1SummaryCDSectionDto retData : retList) {
			        LOGGER.debug("retList data: {}", retData);
			    }
			}
			
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception while Fetching Review Summary For HSN ", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryCDSectionDto convert(Object[] arr) {
		Gstr1SummaryCDSectionDto obj = new Gstr1SummaryCDSectionDto();

		obj.setTaxDocType((String) arr[0]);
		obj.setRecords(
		        arr[1] != null ? Integer.parseInt(String.valueOf(arr[1])) : 0);
		obj.setInvValue((BigDecimal) arr[2]);

		obj.setTaxableValue((BigDecimal) arr[3]);
		obj.setTaxPayable((BigDecimal) arr[4]);
		obj.setIgst((BigDecimal) arr[5]);
		obj.setCgst((BigDecimal) arr[6]);
		obj.setSgst((BigDecimal) arr[7]);
		obj.setCess((BigDecimal) arr[8]);
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Object data: {}", Arrays.toString(arr));
		}
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private static String createQueryString(String buildQuery,
	        String buildQueryB2cs, String buildQueryNil,
	        String buildQueryVertical, String buildSupplyType,
	        String buildDocType, String hdrBuildSupplyType,
	        Boolean rateIncludedInHsn,Boolean hsnFlag) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Outward Query Execution BEGIN ");
		}

		StringBuilder strBuilder = new StringBuilder();
		String queryStr = null;
		if (!rateIncludedInHsn) {
			LOGGER.error("Inside If block");

			strBuilder = strBuilder
			        .append("SELECT TAX_DOC_TYPE, SUM( IFNULL(COUNT_HSNUQC, 0) ) AS "
			                + "COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE, 0) ) AS TOTAL_VALUE, "
			                + "SUM( IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, "
			                + "SUM( IFNULL(TOTAL_TAX, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST, 0) ) "
			                + "AS IGST, SUM( IFNULL(CGST, 0) ) AS CGST, SUM( IFNULL(SGST, 0) ) "
			                + "AS SGST, SUM( IFNULL(CESS, 0) ) AS CESS FROM ( SELECT 'HSN_ASP' AS TAX_DOC_TYPE,"
			                + " COUNT( DISTINCT ITM_HSNSAC || '-' || ITM_UQC ) COUNT_HSNUQC, "
			                + "SUM( IFNULL(TOTAL_VALUE_A, 0)- IFNULL(TOTAL_VALUE_S, 0) ) AS TOTAL_VALUE, "
			                + "SUM( IFNULL(TAXABLE_VALUE_A, 0)- IFNULL(TAXABLE_VALUE_S, 0) ) AS TAXABLE_VALUE, "
			                + "SUM( IFNULL(TOTAL_TAX_A, 0)- IFNULL(TOTAL_TAX_S, 0) ) AS TOTAL_TAX, "
			                + "SUM( IFNULL(IGST_A, 0)- IFNULL(IGST_S, 0) ) AS IGST, "
			                + "SUM( IFNULL(CGST_A, 0)- IFNULL(CGST_S, 0) ) AS CGST, "
			                + "SUM( IFNULL(SGST_A, 0)- IFNULL(SGST_S, 0) ) AS SGST, "
			                + "SUM( IFNULL(CESS_A, 0)- IFNULL(CESS_S, 0) ) AS CESS "
			                + "FROM ( SELECT SUPPLIER_GSTIN, DOC_NUM, ITM_UQC, ITM_QTY, ITM_HSNSAC, "
			                + "RETURN_PERIOD, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_VALUE "
			                + "END TOTAL_VALUE_A, CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE, 0) "
			                + "END TOTAL_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN "
			                + "TAXABLE_VALUE END TAXABLE_VALUE_A, "
			                + "CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S, "
			                + "CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_TAX END TOTAL_TAX_A, "
			                + "CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_TAX END TOTAL_TAX_S, "
			                + "CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IGST END IGST_A, CASE WHEN DOC_TYPE IN('CR') THEN IGST END IGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CGST END CGST_A, CASE WHEN DOC_TYPE IN('CR') THEN CGST END CGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN SGST END SGST_A, CASE WHEN DOC_TYPE IN('CR') THEN SGST END SGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CESS END CESS_A, CASE WHEN DOC_TYPE IN('CR') THEN CESS END CESS_S FROM ( SELECT * FROM ( SELECT B.RETURN_PERIOD, B.SUPPLIER_GSTIN, QUESTION_CODE, ANSWER, B.TAX_DOC_TYPE, B.SUPPLY_TYPE, B.TABLE_SECTION, DOC_TYPE, B.ID, B.DOC_NUM, ONB_LINE_ITEM_AMT, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(TAXABLE_VALUE, 0) END AS TAXABLE_VALUE, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS TOTAL_TAX, CASE WHEN TABLE_SECTION IN "
			                + "( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0) END AS IGST, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(CGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(CGST_AMT, 0) END AS CGST, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(SGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(SGST_AMT, 0) END AS SGST, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS CESS, CASE WHEN TABLE_SECTION IN ( '4A', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('A') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0) "
			                + "WHEN TABLE_SECTION IN "
			                + "( '4A', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) "
			                + "AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION IN ('6A', '9B') "
			                + "AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE IN ('EXPT', 'EXPWT') "
			                + "THEN IFNULL(TAXABLE_VALUE, 0) WHEN TABLE_SECTION IN ('6A', '9B') "
			                + "AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE NOT IN ('EXPT', 'EXPWT') "
			                + "THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION = '4B' THEN IFNULL(TAXABLE_VALUE, 0) "
			                + "END AS TOTAL_VALUE, ITM_UQC, ITM_HSNSAC, "
			                + "ITM_QTY FROM ( SELECT HDR.RETURN_PERIOD, HDR.SUPPLIER_GSTIN, HDR.TAX_DOC_TYPE, DOC_TYPE, "
			                + "HDR.SUPPLY_TYPE, ITM.ITM_TABLE_SECTION AS TABLE_SECTION, HDR.ID, HDR.DOC_NUM, ONB_LINE_ITEM_AMT,"
			                + " ITM.TAXABLE_VALUE, ITM.IGST_AMT, ITM.CGST_AMT, ITM.SGST_AMT, ITM.CESS_AMT_SPECIFIC, "
			                + "ITM.CESS_AMT_ADVALOREM, ITM.OTHER_VALUES, ITM_UQC, "
			                + "ITM_HSNSAC, ITM_QTY FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  ");

			strBuilder
			        .append(" WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND HDR.SUPPLY_TYPE <> 'CAN' "
			                + "AND IS_DELETE = false AND ( ITM.ITM_TABLE_SECTION NOT "
			                + " IN ('8','8A','8B','8C','8D') ");

			strBuilder.append(hdrBuildSupplyType);
			strBuilder.append(buildQuery);
			strBuilder.append(
			        " UNION ALL SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'B2CS' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, 'TAX' SUPPLY_TYPE, '7' TABLE_SECTION, HDR.ID, '' DOC_NUM, IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS ONB_LINE_ITEM_AMT, NEW_TAXABLE_VALUE AS TAXABLE_VALUE, IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT AS CESS_AMT_SPECIFIC, NULL CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, NEW_UOM AS ITM_UQC, NEW_HSNORSAC AS ITM_HSNSAC, NEW_QNT AS ITM_QTY FROM GSTR1_PROCESSED_B2CS HDR WHERE HDR.IS_DELETE = FALSE AND IS_AMENDMENT = FALSE AND ");
			strBuilder.append(buildQueryVertical);

			strBuilder.append(
			        " UNION ALL SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'NILEXTNON' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, SUPPLY_TYPE, '8' TABLE_SECTION, HDR.ID, '' DOC_NUM, TAXABLE_VALUE AS ONB_LINE_ITEM_AMT, TAXABLE_VALUE, 0 IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, 0 CESS_AMT_SPECIFIC, 0 CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY FROM GSTR1_SUMMARY_NILEXTNON HDR WHERE HDR.IS_DELETE = FALSE AND ");
			strBuilder.append(buildQueryNil);
			strBuilder.append(buildSupplyType);

			strBuilder.append(" ) ");

			strBuilder.append(
			        "  B INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN ");
			strBuilder.append(
			        "INNER JOIN ENTITY_CONFG_PRMTR ECP ON ECP.ENTITY_ID= GSTN.ENTITY_ID ");
			strBuilder.append(
			        "AND ECP.IS_ACTIVE=TRUE AND ECP.IS_DELETE=FALSE AND ECP.QUESTION_CODE IN ('O21'))C ");
			strBuilder.append(buildDocType);
			strBuilder.append(")) GROUP BY 'HSN_ASP' ) "
			        + " GROUP BY TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE, IFNULL( SUM(COUNT_HSNUQC), 0 ) AS COUNT_HSNUQC, IFNULL( SUM(TOTAL_VALUE), 0 ) AS TOTAL_VALUE, IFNULL( SUM(TAXABLE_VALUE), 0 ) AS TAXABLE_VALUE, IFNULL( SUM(TOTAL_TAX), 0 ) AS TOTAL_TAX, IFNULL( SUM(IGST), 0 ) AS IGST, IFNULL( SUM(CGST), 0 ) AS CGST, IFNULL( SUM(SGST), 0 ) AS SGST, IFNULL( SUM(CESS), 0 ) AS CESS FROM ( SELECT 'HSN_UI' AS TAX_DOC_TYPE, RETURN_PERIOD, SUPPLIER_GSTIN, COUNT(DISTINCT HSNSAC || '-' || UQC) COUNT_HSNUQC, SUM(TOTAL_VALUE) AS TOTAL_VALUE, SUM(TAXABLE_VALUE) AS TAXABLE_VALUE, SUM(TOTAL_TAX) AS TOTAL_TAX, SUM(IGST) AS IGST, SUM(CGST) AS CGST, SUM(SGST) AS SGST, SUM(CESS) AS CESS FROM ( SELECT RETURN_PERIOD, SUPPLIER_GSTIN, IFNULL(TOTAL_VALUE, 0) TOTAL_VALUE, IFNULL(TAXABLE_VALUE, 0) TAXABLE_VALUE, IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS TOTAL_TAX, IFNULL(IGST_AMT, 0) IGST, IFNULL(CGST_AMT, 0) CGST, IFNULL(SGST_AMT, 0) SGST, IFNULL(CESS_AMT, 0) CESS, UQC, HSNSAC FROM GSTR1_USERINPUT_HSNSAC WHERE IS_DELETE = FALSE AND "
			        + buildQueryNil
			        + " ) GROUP BY RETURN_PERIOD, SUPPLIER_GSTIN ) GROUP BY TAX_DOC_TYPE");
		} else if (rateIncludedInHsn && hsnFlag){ 
			LOGGER.error("Inside Else If block");

			strBuilder = strBuilder
			        .append("SELECT TAX_DOC_TYPE, SUM( IFNULL(COUNT_HSNUQC, 0) ) AS COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE, 0) ) "
			                + "AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX, 0) ) "
			                + "AS TOTAL_TAX, SUM( IFNULL(IGST, 0) ) AS IGST, SUM( IFNULL(CGST, 0) ) AS CGST, SUM( IFNULL(SGST, 0) ) AS SGST,"
			                + " SUM( IFNULL(CESS, 0) ) AS CESS FROM ( SELECT 'HSN_ASP' AS TAX_DOC_TYPE,"
			                + " COUNT( DISTINCT ITM_HSNSAC || '-' || ( CASE WHEN LEFT(ITM_HSNSAC, 2) = 99 then 'NA' else ITM_UQC end ) ||RECORDTYPE ) COUNT_HSNUQC,"
			                + " SUM( IFNULL(TOTAL_VALUE_A, 0)- IFNULL(TOTAL_VALUE_S, 0) ) AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE_A, 0)- IFNULL(TAXABLE_VALUE_S, 0) ) AS TAXABLE_VALUE,"
			                + " SUM( IFNULL(TOTAL_TAX_A, 0)- IFNULL(TOTAL_TAX_S, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST_A, 0)- IFNULL(IGST_S, 0) ) AS IGST,"
			                + " SUM( IFNULL(CGST_A, 0)- IFNULL(CGST_S, 0) ) AS CGST,"
			                + " SUM( IFNULL(SGST_A, 0)- IFNULL(SGST_S, 0) ) AS SGST, SUM( IFNULL(CESS_A, 0)- IFNULL(CESS_S, 0) ) AS CESS,"
			                + " IFNULL(TAX_RATE, 0) TAX_RATE FROM ( SELECT SUPPLIER_GSTIN, DOC_NUM, ITM_UQC, ITM_QTY, ITM_HSNSAC, RETURN_PERIOD,"
			                + " CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_VALUE END TOTAL_VALUE_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE, 0) END TOTAL_VALUE_S,"
			                + " CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_TAX END TOTAL_TAX_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_TAX END TOTAL_TAX_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IGST END IGST_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN IGST END IGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CGST END CGST_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN CGST END CGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN SGST END SGST_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN SGST END SGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CESS END CESS_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN CESS END CESS_S, IFNULL(TAX_RATE, 0) TAX_RATE, RECORDTYPE FROM "
			                + " ( SELECT * FROM ( SELECT B.RETURN_PERIOD, B.SUPPLIER_GSTIN, QUESTION_CODE, ANSWER, B.TAX_DOC_TYPE, B.SUPPLY_TYPE, B.TABLE_SECTION,"
			                + " DOC_TYPE, B.ID, B.DOC_NUM, ONB_LINE_ITEM_AMT, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)',"
			                + " '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(TAXABLE_VALUE, 0) END AS TAXABLE_VALUE,"
			                + " CASE WHEN TABLE_SECTION IN ( '4A','4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)',"
			                + " '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+"
			                + " IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ "
			                + " IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) "
			                + " END AS TOTAL_TAX, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A',"
			                + " '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' )"
			                + " THEN IFNULL(IGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0) END AS IGST, CASE WHEN TABLE_SECTION IN ( '4A','4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)',"
			                + " '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(CGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(CGST_AMT, 0) END AS CGST,"
			                + " CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B',"
			                + "'15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(SGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(SGST_AMT, 0) END AS SGST, CASE WHEN TABLE_SECTION IN ( '4A', '4B',"
			                + " '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)',"
			                + " '14(ii)' ) THEN IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL( "
			                + " CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) "
			                + " END AS CESS, CASE WHEN TABLE_SECTION IN ( '4A', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D',"
			                + " '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('A') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0)"
			                + " WHEN TABLE_SECTION IN ( '4A', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D','15(i)', '15(ii)', '15(iii)',"
			                + " '15(iv)','14(ii)' ) AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION IN ('6A', '9B') "
			                + " AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE IN ('EXPT', 'EXPWT') THEN IFNULL(TAXABLE_VALUE, 0)"
			                + " WHEN TABLE_SECTION IN ('6A', '9B') AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE NOT IN ('EXPT', 'EXPWT')"
			                + " THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION = '4B' THEN IFNULL(TAXABLE_VALUE, 0) END AS TOTAL_VALUE, ITM_UQC, ITM_HSNSAC,"
			                + " ITM_QTY, TAX_RATE, RECORDTYPE FROM ( SELECT HDR.RETURN_PERIOD, HDR.SUPPLIER_GSTIN, HDR.TAX_DOC_TYPE, DOC_TYPE, HDR.SUPPLY_TYPE,"
			                + " ITM.ITM_TABLE_SECTION AS TABLE_SECTION, HDR.ID, HDR.DOC_NUM, ONB_LINE_ITEM_AMT, ITM.TAXABLE_VALUE, ITM.IGST_AMT, ITM.CGST_AMT, ITM.SGST_AMT,"
			                + " ITM.CESS_AMT_SPECIFIC, ITM.CESS_AMT_ADVALOREM, ITM.OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, TAX_RATE, "
			                
							+ "	CASE "
							+ "	WHEN ITM.CUST_GSTIN IS NOT NULL AND LENGTH(ITM.CUST_GSTIN) = 15 THEN 'HSN_ASP_B2B' "
							+ "	WHEN (LENGTH(ITM.CUST_GSTIN) < 15 OR ITM.CUST_GSTIN IS NULL)    THEN 'HSN_ASP_B2C' END AS RecordType "

			                
			                +" FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM"
			                + " ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  ");

			strBuilder
			        .append(" WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND HDR.SUPPLY_TYPE <> 'CAN' "
			                + " AND IS_DELETE = false AND ( ITM.ITM_TABLE_SECTION NOT "
			                + " IN ('8','8A','8B','8C','8D') ");

			strBuilder.append(hdrBuildSupplyType);
			strBuilder.append(buildQuery);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        " SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'B2CS' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, 'TAX' SUPPLY_TYPE, '7' TABLE_SECTION, HDR.ID, '' DOC_NUM,"
			        + " IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS ONB_LINE_ITEM_AMT,"
			        + " NEW_TAXABLE_VALUE AS TAXABLE_VALUE, IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT AS CESS_AMT_SPECIFIC, NULL CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES,"
			        + " NEW_UOM AS ITM_UQC, NEW_HSNORSAC AS ITM_HSNSAC, NEW_QNT AS ITM_QTY, IFNULL(NEW_RATE, 0) TAX_RATE, 'HSN_ASP_B2C' AS RECORDTYPE"
			        + " FROM GSTR1_PROCESSED_B2CS HDR WHERE HDR.IS_DELETE = FALSE AND IS_AMENDMENT = FALSE AND ");
			strBuilder.append(buildQueryVertical);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        "SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'NILEXTNON' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, SUPPLY_TYPE, '8' TABLE_SECTION, HDR.ID, '' DOC_NUM,"
			        + " TAXABLE_VALUE AS ONB_LINE_ITEM_AMT, TAXABLE_VALUE, 0 IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, 0 CESS_AMT_SPECIFIC, 0 CESS_AMT_ADVALOREM,"
			        + " 0.00 OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, 0.00 TAX_RATE, CASE WHEN TABLE_SECTION IN ( '8A', '8B') THEN 'HSN_ASP_B2B'"
			        + " WHEN TABLE_SECTION IN ( '8C', '8D') THEN 'HSN_ASP_B2C' END AS RecordType FROM GSTR1_SUMMARY_NILEXTNON HDR WHERE HDR.IS_DELETE = FALSE AND ");
			strBuilder.append(buildQueryNil);
			strBuilder.append(buildSupplyType);

			strBuilder.append(" ) ");

			strBuilder.append(
			        " B INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN INNER JOIN ENTITY_CONFG_PRMTR ECP ON ECP.ENTITY_ID = GSTN.ENTITY_ID"
			        + " AND ECP.IS_ACTIVE = TRUE AND ECP.IS_DELETE = FALSE AND ECP.QUESTION_CODE IN ('O21') ) C ");
			strBuilder.append(buildDocType);
			strBuilder.append(" )) GROUP BY 'HSN_ASP',TAX_RATE"
			        + " ) GROUP BY TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE, SUM(IFNULL(COUNT_HSNUQC, 0) ) AS COUNT_HSNUQC, SUM(IFNULL(TOTAL_VALUE, 0) )"
			        + " AS TOTAL_VALUE, SUM(IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, SUM(IFNULL(TOTAL_TAX, 0) ) AS TOTAL_TAX, SUM(IFNULL(IGST, 0) )"
			        + " AS IGST, SUM(IFNULL(CGST, 0) ) AS CGST, SUM(IFNULL(SGST, 0) ) AS SGST, SUM(IFNULL(CESS, 0)) AS CESS FROM ( SELECT RecordType AS tax_doc_type,"
			        + " Count( DISTINCT itm_hsnsac || '-' || ( CASE WHEN Left(itm_hsnsac, 2) = 99 THEN 'NA' ELSE itm_uqc END)||RecordType) count_hsnuqc,"
			        + " SUM( Ifnull(total_value_a, 0) - Ifnull(total_value_s, 0)) AS total_value, SUM( Ifnull(taxable_value_a, 0)- Ifnull(taxable_value_s, 0) )"
			        + " AS taxable_value, SUM(Ifnull(total_tax_a, 0) - Ifnull(total_tax_s, 0) ) AS total_tax, SUM(Ifnull(igst_a, 0) - Ifnull(igst_s, 0)) AS igst,"
			        + " SUM(Ifnull(cgst_a, 0) - Ifnull(cgst_s, 0)) AS cgst, SUM(Ifnull(sgst_a, 0) - Ifnull(sgst_s, 0)) AS sgst,"
			        + " SUM(Ifnull(cess_a, 0) - Ifnull(cess_s, 0)) AS cess, Ifnull(tax_rate, 0) tax_rate FROM ( SELECT supplier_gstin,doc_num,itm_uqc,"
			        + " itm_qty,itm_hsnsac,return_period,CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN total_value END total_value_a, CASE WHEN doc_type IN('CR')"
			        + " THEN Ifnull(total_value, 0) END total_value_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN taxable_value END taxable_value_a, CASE"
			        + " WHEN doc_type IN('CR') THEN taxable_value END taxable_value_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN total_tax END total_tax_a,"
			        + " CASE WHEN doc_type IN('CR') THEN total_tax END total_tax_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN igst END igst_a,"
			        + " CASE WHEN doc_type IN('CR') THEN igst END igst_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN cgst END cgst_a, CASE WHEN doc_type"
			        + " IN('CR') THEN cgst END cgst_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN sgst END sgst_a, CASE WHEN doc_type IN('CR') THEN sgst END sgst_s,"
			        + " CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN cess END cess_a, CASE WHEN doc_type IN('CR') THEN cess END cess_s, Ifnull(tax_rate, 0) tax_rate, RecordType FROM "
			        + " ( SELECT * FROM ( SELECT b.return_period, b.supplier_gstin,question_code, answer, b.tax_doc_type, b.supply_type, b.table_section,"
			        + " doc_type, b.id, b.doc_num, onb_line_item_amt, CASE WHEN table_section IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8',"
			        + " '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(taxable_value, 0) END AS taxable_value,"
			        + " CASE WHEN table_section IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)',"
			        + " '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(igst_amt, 0)+ Ifnull(cgst_amt, 0)+ Ifnull(sgst_amt, 0)+ Ifnull(cess_amt_specific, 0)+"
			        + " Ifnull(cess_amt_advalorem, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ "
			        + " IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) "
			        + " END AS total_tax, CASE WHEN table_section IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8',"
			        + " '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(igst_amt, 0) "
			        + " WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0) END AS igst,"
			        + " CASE WHEN table_section IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)',"
			        + " '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(cgst_amt, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(CGST_AMT, 0) END AS cgst,"
			        + " CASE WHEN table_section IN ( '4A', '4B', '5A', '6B', '6C', '7',"
			        + " '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) "
			        + " THEN IFNULL(SGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(SGST_AMT, 0) END AS SGST, "
			        + " CASE WHEN table_section IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)',"
			        + " '15(iii)', '15(iv)', '14(ii)'  ) THEN Ifnull(cess_amt_specific, 0)+ Ifnull(cess_amt_advalorem, 0) WHEN TABLE_SECTION = '6A' THEN "
			        + " IFNULL( CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS CESS, CASE WHEN table_section IN "
			        + " ( '4A', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' )"
			        + " AND ( question_code = 'O21' AND answer IN ('A') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ( '4A', '5A', '6B', '6C', '7',"
			        + " '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) AND ( "
			        + " question_code = 'O21' AND answer IN ('B') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ('6A', '9B') AND ( question_code = 'O21'"
			        + " AND answer IN ('B') ) AND supply_type IN ('EXPT', 'EXPWT') THEN Ifnull(taxable_value, 0) WHEN table_section IN ('6A', '9B') AND "
			        + " ( question_code = 'O21' AND answer IN ('B') ) AND supply_type NOT IN ('EXPT', 'EXPWT') THEN Ifnull(onb_line_item_amt, 0) WHEN "
			        + " table_section = '4B' THEN Ifnull(taxable_value, 0) END AS total_value, itm_uqc, itm_hsnsac, itm_qty, tax_rate,RecordType FROM "
			        + " ( SELECT  hdr.return_period, hdr.supplier_gstin, hdr.tax_doc_type, doc_type, hdr.supply_type,"
			        + " ITM.ITM_TABLE_SECTION AS TABLE_SECTION, hdr.id, hdr.doc_num, onb_line_item_amt, itm.taxable_value,"
			        + " itm.igst_amt, itm.cgst_amt, itm.sgst_amt, itm.cess_amt_specific, itm.cess_amt_advalorem, itm.other_values, itm_uqc, itm_hsnsac, itm_qty, "
			        + " tax_rate, "
			        
					+ "	CASE "
					+ "	WHEN ITM.CUST_GSTIN IS NOT NULL AND LENGTH(ITM.CUST_GSTIN) = 15 THEN 'HSN_ASP_B2B' "
					+ "	WHEN (LENGTH(ITM.CUST_GSTIN) < 15 OR ITM.CUST_GSTIN IS NULL)    THEN 'HSN_ASP_B2C' END AS RecordType "

			        + " FROM anx_outward_doc_header hdr inner join anx_outward_doc_item itm ON hdr.id = itm.doc_header_id AND "
			        + " hdr.derived_ret_period = itm.derived_ret_period WHERE asp_invoice_status = 2 AND compliance_applicable = TRUE AND hdr.supply_type <> 'CAN'"
			        + " AND is_delete = FALSE and ( ITM.ITM_TABLE_SECTION NOT IN('8', '8A', '8B', '8C', '8D') "
			        + hdrBuildSupplyType
			        + buildQuery 
			        
			        + " UNION all SELECT return_period, supplier_gstin, 'B2CS' AS tax_doc_type, 'INV' doc_type, 'TAX' supply_type,"
			        + " '7' table_section, hdr.id, '' doc_num, ifnull(new_taxable_value, 0)+ ifnull(igst_amt, 0)+ ifnull(cgst_amt, 0)+ ifnull(sgst_amt, 0)+"
			        + " ifnull(cess_amt, 0) AS onb_line_item_amt, new_taxable_value AS taxable_value, igst_amt, cgst_amt, sgst_amt, cess_amt AS cess_amt_specific,"
			        + " NULL cess_amt_advalorem, 0.00 other_values, new_uom AS itm_uqc, new_hsnorsac AS itm_hsnsac, new_qnt AS itm_qty, ifnull(new_rate, 0) tax_rate, "
			        + " 'HSN_ASP_B2C' AS RecordType FROM gstr1_processed_b2cs hdr WHERE hdr.is_delete = FALSE AND is_amendment = FALSE AND "
			        + buildQueryVertical
			        + " UNION ALL SELECT return_period, supplier_gstin, 'NILEXTNON' AS tax_doc_type, 'INV' doc_type, supply_type, '8' table_section, hdr.id,"
			        + " '' doc_num, taxable_value AS onb_line_item_amt, taxable_value, 0 igst_amt, 0 cgst_amt, 0 sgst_amt, 0 cess_amt_specific, 0 cess_amt_advalorem,"
			        + " 0.00 other_values, itm_uqc, itm_hsnsac, itm_qty, 0.00 tax_rate, CASE WHEN TABLE_SECTION IN ( '8A', '8B') THEN 'HSN_ASP_B2B' "
			        + " WHEN TABLE_SECTION IN ( '8C', '8D') THEN 'HSN_ASP_B2C' END AS RecordType FROM gstr1_summary_nilextnon hdr WHERE hdr.is_delete = FALSE AND "
			        + buildQueryNil
			        + buildSupplyType 
			        + " ) b  inner join gstin_info gstn ON b.supplier_gstin = gstn.gstin inner join entity_confg_prmtr ecp ON ecp.entity_id = gstn.entity_id"
			        + " AND ecp.is_active = TRUE AND ecp.is_delete = FALSE AND ecp.question_code IN ('O21') ) c " + buildDocType + " )) "
			        + " GROUP BY tax_rate, RecordType ) GROUP BY tax_doc_type "
			        + " UNION ALL SELECT tax_doc_type, ifnull( SUM(count_hsnuqc), 0 ) AS count_hsnuqc, ifnull( SUM(total_value),0 ) AS total_value,"
			        + " ifnull( SUM(taxable_value),0 ) AS taxable_value, ifnull( SUM(total_tax),0 ) AS total_tax, ifnull( SUM(igst),0 ) AS igst, ifnull( SUM(cgst),0)"
			        + " AS cgst, ifnull(SUM(sgst),0 ) AS sgst, ifnull(SUM(cess),0 ) AS cess FROM "
			        + " ( SELECT 'HSN_UI' AS tax_doc_type, return_period, supplier_gstin, count( DISTINCT hsnsac || '-' || ( CASE WHEN left(hsnsac, 2) = 99 THEN 'NA' "
			        + " ELSE uqc END )||RecordType ) count_hsnuqc, SUM(total_value) AS total_value, SUM(taxable_value) AS taxable_value, SUM(total_tax) AS total_tax,"
			        + " SUM(igst) AS igst, SUM(cgst) AS cgst, SUM(sgst) AS sgst, SUM(cess) AS cess, tax_rate, RecordType FROM "
			        + " ( SELECT return_period, supplier_gstin, ifnull(total_value, 0) total_value, ifnull(taxable_value, 0) taxable_value, ifnull(igst_amt, 0)+"
			        + " ifnull(cgst_amt, 0)+ ifnull(sgst_amt, 0)+ ifnull(cess_amt, 0) AS total_tax, ifnull(igst_amt, 0) igst, ifnull(cgst_amt, 0) cgst,"
			        + " ifnull(sgst_amt, 0) sgst, ifnull(cess_amt, 0) cess, uqc, hsnsac, tax_rate, Record_Type AS  RecordType FROM gstr1_userinput_hsnsac"
			        + " WHERE is_delete = FALSE AND "
			        + buildQueryNil 
			        + "  ) GROUP BY return_period, supplier_gstin, tax_rate, RecordType "
			        + " ) GROUP BY TAX_DOC_TYPE "
					+" UNION ALL SELECT tax_doc_type, ifnull( SUM(count_hsnuqc), 0 ) AS count_hsnuqc, ifnull( SUM(total_value),0 ) AS total_value,"
					+ " ifnull( SUM(taxable_value),0 ) AS taxable_value, ifnull( SUM(total_tax),0 ) AS total_tax, ifnull( SUM(igst),0 ) AS igst, ifnull( SUM(cgst),0)"
					+ " AS cgst, ifnull(SUM(sgst),0 ) AS sgst, ifnull(SUM(cess),0 ) AS cess FROM "
					+ " ( SELECT RecordType AS tax_doc_type, return_period, supplier_gstin, count( DISTINCT hsnsac || '-' || ( CASE WHEN left(hsnsac, 2) = 99 THEN 'NA'"
					+ " ELSE uqc END )||RecordType ) count_hsnuqc, SUM(total_value) AS total_value, SUM(taxable_value) AS taxable_value, SUM(total_tax) AS total_tax,"
					+ " SUM(igst) AS igst, SUM(cgst) AS cgst, SUM(sgst) AS sgst, SUM(cess) AS cess, tax_rate FROM "
	        		+ " ( SELECT  return_period, supplier_gstin, ifnull(total_value, 0) total_value, ifnull(taxable_value, 0) taxable_value,"
	        		+ " ifnull(igst_amt, 0)+ ifnull(cgst_amt, 0)+ ifnull(sgst_amt, 0)+ ifnull(cess_amt, 0) AS total_tax, ifnull(igst_amt, 0) igst,"
	        		+ " ifnull(cgst_amt, 0) cgst, ifnull(sgst_amt, 0) sgst, ifnull(cess_amt, 0) cess, uqc, hsnsac, tax_rate, Record_Type AS  RecordType FROM gstr1_userinput_hsnsac"
	        		+ " WHERE is_delete = FALSE AND "
	        		+ buildQueryNil
	        		+ "  ) GROUP BY return_period, supplier_gstin, tax_rate, RecordType "
			        + " ) GROUP BY TAX_DOC_TYPE ");

		} else {
			LOGGER.error("Inside Else block");

			strBuilder = strBuilder
			        .append("SELECT TAX_DOC_TYPE, SUM( IFNULL(COUNT_HSNUQC, 0) ) AS COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE, 0) ) "
			                + ""
			                + "AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX, 0) ) "
			                + "AS TOTAL_TAX, SUM( IFNULL(IGST, 0) ) AS IGST, SUM( IFNULL(CGST, 0) ) AS CGST, SUM( IFNULL(SGST, 0) ) AS SGST, SUM( IFNULL(CESS, 0) ) AS CESS FROM ( SELECT 'HSN_ASP' AS TAX_DOC_TYPE, COUNT( DISTINCT ITM_HSNSAC || '-' || ( CASE WHEN LEFT(ITM_HSNSAC, 2) = 99 then 'NA' else ITM_UQC end ) ) COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE_A, 0)- IFNULL(TOTAL_VALUE_S, 0) ) AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE_A, 0)- IFNULL(TAXABLE_VALUE_S, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX_A, 0)- IFNULL(TOTAL_TAX_S, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST_A, 0)- IFNULL(IGST_S, 0) ) AS IGST, SUM( IFNULL(CGST_A, 0)- IFNULL(CGST_S, 0) ) AS CGST, SUM( IFNULL(SGST_A, 0)- IFNULL(SGST_S, 0) ) AS SGST, SUM( IFNULL(CESS_A, 0)- IFNULL(CESS_S, 0) ) AS CESS, IFNULL(TAX_RATE, 0) TAX_RATE FROM ( SELECT SUPPLIER_GSTIN, DOC_NUM, ITM_UQC, ITM_QTY, ITM_HSNSAC, RETURN_PERIOD, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_VALUE END TOTAL_VALUE_A, CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE, 0) END TOTAL_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A, CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_TAX END TOTAL_TAX_A, CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_TAX END TOTAL_TAX_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IGST END IGST_A, CASE WHEN DOC_TYPE IN('CR') THEN IGST END IGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CGST END CGST_A, CASE WHEN DOC_TYPE IN('CR') THEN CGST END CGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN SGST END SGST_A, CASE WHEN DOC_TYPE IN('CR') THEN SGST END SGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CESS END CESS_A, CASE WHEN DOC_TYPE IN('CR') THEN CESS END CESS_S, IFNULL(TAX_RATE, 0) TAX_RATE FROM ( SELECT * FROM ( SELECT B.RETURN_PERIOD, B.SUPPLIER_GSTIN, QUESTION_CODE, ANSWER, B.TAX_DOC_TYPE, B.SUPPLY_TYPE, B.TABLE_SECTION, DOC_TYPE, B.ID, B.DOC_NUM, ONB_LINE_ITEM_AMT, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(TAXABLE_VALUE, 0) END AS TAXABLE_VALUE, CASE WHEN TABLE_SECTION IN ( '4A','4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS TOTAL_TAX, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(IGST_AMT, 0) END AS IGST, CASE WHEN TABLE_SECTION IN ( '4A','4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(CGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(CGST_AMT, 0) END AS CGST, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(SGST_AMT, 0) WHEN TABLE_SECTION = '6A' THEN IFNULL(SGST_AMT, 0) END AS SGST, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) "
			                + " WHEN TABLE_SECTION = '6A' THEN IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS CESS, CASE WHEN TABLE_SECTION IN ( '4A', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('A') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION IN ( '4A', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION IN ('6A', '9B') AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE IN ('EXPT', 'EXPWT') THEN IFNULL(TAXABLE_VALUE, 0) WHEN TABLE_SECTION IN ('6A', '9B') AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE NOT IN ('EXPT', 'EXPWT') THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION = '4B' THEN IFNULL(TAXABLE_VALUE, 0) END AS TOTAL_VALUE, ITM_UQC, ITM_HSNSAC, ITM_QTY, TAX_RATE FROM ( SELECT HDR.RETURN_PERIOD, HDR.SUPPLIER_GSTIN, HDR.TAX_DOC_TYPE, DOC_TYPE, HDR.SUPPLY_TYPE, ITM.ITM_TABLE_SECTION AS TABLE_SECTION, HDR.ID, HDR.DOC_NUM, ONB_LINE_ITEM_AMT, ITM.TAXABLE_VALUE, ITM.IGST_AMT, ITM.CGST_AMT, ITM.SGST_AMT, ITM.CESS_AMT_SPECIFIC, ITM.CESS_AMT_ADVALOREM, ITM.OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, TAX_RATE FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  ");

			strBuilder
			        .append(" WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND HDR.SUPPLY_TYPE <> 'CAN' "
			                + "AND IS_DELETE = false AND ( ITM.ITM_TABLE_SECTION NOT "
			                + " IN ('8','8A','8B','8C','8D') ");

			strBuilder.append(hdrBuildSupplyType);
			strBuilder.append(buildQuery);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        "SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'B2CS' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, 'TAX' SUPPLY_TYPE, '7' TABLE_SECTION, HDR.ID, '' DOC_NUM, IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS ONB_LINE_ITEM_AMT, NEW_TAXABLE_VALUE AS TAXABLE_VALUE, IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT AS CESS_AMT_SPECIFIC, NULL CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, NEW_UOM AS ITM_UQC, NEW_HSNORSAC AS ITM_HSNSAC, NEW_QNT AS ITM_QTY, IFNULL(NEW_RATE, 0) TAX_RATE FROM GSTR1_PROCESSED_B2CS HDR WHERE HDR.IS_DELETE = FALSE AND IS_AMENDMENT = FALSE AND ");
			strBuilder.append(buildQueryVertical);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        "SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'NILEXTNON' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, SUPPLY_TYPE, '8' TABLE_SECTION, HDR.ID, '' DOC_NUM, TAXABLE_VALUE AS ONB_LINE_ITEM_AMT, TAXABLE_VALUE, 0 IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, 0 CESS_AMT_SPECIFIC, 0 CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, 0.00 TAX_RATE FROM GSTR1_SUMMARY_NILEXTNON HDR WHERE HDR.IS_DELETE = FALSE AND ");
			strBuilder.append(buildQueryNil);
			strBuilder.append(buildSupplyType);

			strBuilder.append(" ) ");

			strBuilder.append(
			        " B INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN INNER JOIN ENTITY_CONFG_PRMTR ECP ON ECP.ENTITY_ID = GSTN.ENTITY_ID AND ECP.IS_ACTIVE = TRUE AND ECP.IS_DELETE = FALSE AND ECP.QUESTION_CODE IN ('O21') ) C ");
			strBuilder.append(buildDocType);
			strBuilder.append(")) GROUP BY 'HSN_ASP',TAX_RATE"
			        + " ) GROUP BY TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE, IFNULL( SUM(COUNT_HSNUQC), 0 ) AS COUNT_HSNUQC, IFNULL( SUM(TOTAL_VALUE), 0 ) AS TOTAL_VALUE, IFNULL( SUM(TAXABLE_VALUE), 0 ) AS TAXABLE_VALUE, IFNULL( SUM(TOTAL_TAX), 0 ) AS TOTAL_TAX, IFNULL( SUM(IGST), 0 ) AS IGST, IFNULL( SUM(CGST), 0 ) AS CGST, IFNULL( SUM(SGST), 0 ) AS SGST, IFNULL( SUM(CESS), 0 ) AS CESS FROM ( SELECT 'HSN_UI' AS TAX_DOC_TYPE, RETURN_PERIOD, SUPPLIER_GSTIN, COUNT( DISTINCT HSNSAC || '-' || ( CASE WHEN LEFT(HSNSAC, 2) = 99 then 'NA' else UQC end ) ) COUNT_HSNUQC, SUM(TOTAL_VALUE) AS TOTAL_VALUE, SUM(TAXABLE_VALUE) AS TAXABLE_VALUE, SUM(TOTAL_TAX) AS TOTAL_TAX, SUM(IGST) AS IGST, SUM(CGST) AS CGST, SUM(SGST) AS SGST, SUM(CESS) AS CESS, TAX_RATE FROM ( SELECT RETURN_PERIOD, SUPPLIER_GSTIN, IFNULL(TOTAL_VALUE, 0) TOTAL_VALUE, IFNULL(TAXABLE_VALUE, 0) TAXABLE_VALUE, IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS TOTAL_TAX, IFNULL(IGST_AMT, 0) IGST, IFNULL(CGST_AMT, 0) CGST, IFNULL(SGST_AMT, 0) SGST, IFNULL(CESS_AMT, 0) CESS, UQC, HSNSAC, TAX_RATE FROM GSTR1_USERINPUT_HSNSAC WHERE IS_DELETE = FALSE AND "
			        + buildQueryNil
			        + " ) GROUP BY RETURN_PERIOD, SUPPLIER_GSTIN, TAX_RATE"
			        + " ) GROUP BY TAX_DOC_TYPE ");
		}

		queryStr = strBuilder.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" HSN Section Query Execution END ");
		}

		return queryStr;
	}

	private String createQueryStringForGstr1a(String buildQuery,
	        String buildQueryB2cs, String buildQueryNil,
	        String buildQueryVertical, String buildSupplyType,
	        String buildDocType, String hdrBuildSupplyType,
	        Boolean rateIncludedInHsn,Boolean hsnFlag) { 
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Outward Query Execution BEGIN ");
		}

		StringBuilder strBuilder = new StringBuilder();
		if (!rateIncludedInHsn) {
			LOGGER.error("Inside If block");
			strBuilder = strBuilder.append(
			        "SELECT TAX_DOC_TYPE, SUM( IFNULL(COUNT_HSNUQC, 0) ) AS COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE, 0) ) AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST, 0) ) AS IGST, SUM( IFNULL(CGST, 0) ) AS CGST, SUM( IFNULL(SGST, 0) ) AS SGST, SUM( IFNULL(CESS, 0) ) AS CESS FROM ( SELECT 'HSN_ASP' AS TAX_DOC_TYPE, COUNT( DISTINCT ( ITM_HSNSAC || '-' || ITM_UQC ) ) COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE_A, 0)- IFNULL(TOTAL_VALUE_S, 0) ) AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE_A, 0)- IFNULL(TAXABLE_VALUE_S, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX_A, 0)- IFNULL(TOTAL_TAX_S, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST_A, 0)- IFNULL(IGST_S, 0) ) AS IGST, SUM( IFNULL(CGST_A, 0)- IFNULL(CGST_S, 0) ) AS CGST, SUM( IFNULL(SGST_A, 0)- IFNULL(SGST_S, 0) ) AS SGST, SUM( IFNULL(CESS_A, 0)- IFNULL(CESS_S, 0) ) AS CESS FROM ( SELECT SUPPLIER_GSTIN, DOC_NUM, ITM_UQC, ITM_QTY, ITM_HSNSAC, RETURN_PERIOD, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_VALUE  END TOTAL_VALUE_A, CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE, 0) END TOTAL_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A, CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_TAX END TOTAL_TAX_A, CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_TAX END TOTAL_TAX_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IGST END IGST_A, CASE WHEN DOC_TYPE IN('CR') THEN IGST END IGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CGST END CGST_A, CASE WHEN DOC_TYPE IN('CR') THEN CGST END CGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN SGST END SGST_A, CASE WHEN DOC_TYPE IN('CR') THEN SGST END SGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CESS END CESS_A, CASE WHEN DOC_TYPE IN('CR') THEN CESS END CESS_S FROM ( SELECT * FROM ( SELECT B.RETURN_PERIOD, B.SUPPLIER_GSTIN, QUESTION_CODE, ANSWER, B.TAX_DOC_TYPE, B.SUPPLY_TYPE, B.TABLE_SECTION, DOC_TYPE, B.ID, B.DOC_NUM, ONB_LINE_ITEM_AMT, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(TAXABLE_VALUE, 0) END AS TAXABLE_VALUE, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS total_tax, "
			        + " CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) THEN Ifnull(cgst_amt, 0) END AS cgst, CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) THEN Ifnull(sgst_amt, 0) END AS sgst, CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) THEN Ifnull(cess_amt_specific, 0) + Ifnull(cess_amt_advalorem, 0) END AS cess, CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) AND ( question_code = 'O21' AND  answer IN ('A') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) AND ( question_code = 'O21' AND  answer IN ('B') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ('6A','9B') "
			        + " AND ( question_code = 'O21' AND  answer IN ('B')) AND supply_type IN ('EXPT','EXPWT') THEN Ifnull(taxable_value, 0) WHEN table_section IN ('6A','9B') AND ( question_code = 'O21' AND answer IN ('B') ) AND supply_type NOT IN ('EXPT','EXPWT') THEN Ifnull(onb_line_item_amt, 0) WHEN table_section = '4B' THEN Ifnull(taxable_value, 0) END AS total_value,itm_uqc,itm_hsnsac,itm_qty FROM ( SELECT HDR.RETURN_PERIOD, HDR.SUPPLIER_GSTIN, HDR.TAX_DOC_TYPE, DOC_TYPE, HDR.SUPPLY_TYPE, HDR.TABLE_SECTION, HDR.ID, HDR.DOC_NUM, ONB_LINE_ITEM_AMT, ITM.TAXABLE_VALUE, ITM.IGST_AMT, ITM.CGST_AMT, ITM.SGST_AMT, ITM.CESS_AMT_SPECIFIC, ITM.CESS_AMT_ADVALOREM, ITM.OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND HDR.SUPPLY_TYPE <> 'CAN' AND IS_DELETE = false ");
			strBuilder.append(
			        " AND (TABLE_SECTION NOT IN('8','8A','8B','8C','8D') ");
			strBuilder.append(hdrBuildSupplyType);
			strBuilder.append(buildQuery);
			strBuilder.append(
			        " UNION ALL SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'B2CS' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, 'TAX' SUPPLY_TYPE, '7' TABLE_SECTION, HDR.ID, '' DOC_NUM, IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS ONB_LINE_ITEM_AMT, NEW_TAXABLE_VALUE AS TAXABLE_VALUE, IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT AS CESS_AMT_SPECIFIC, NULL CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, NEW_UOM AS ITM_UQC, NEW_HSNORSAC AS ITM_HSNSAC, NEW_QNT AS ITM_QTY FROM GSTR1A_PROCESSED_B2CS HDR WHERE HDR.IS_DELETE = FALSE AND IS_AMENDMENT = FALSE AND ");
			strBuilder.append(buildQueryVertical);

			strBuilder.append(
			        " UNION ALL SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'NILEXTNON' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, SUPPLY_TYPE, '8' TABLE_SECTION, HDR.ID, '' DOC_NUM, TAXABLE_VALUE AS ONB_LINE_ITEM_AMT, TAXABLE_VALUE, 0 IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, 0 CESS_AMT_SPECIFIC, 0 CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY FROM GSTR1A_SUMMARY_NILEXTNON HDR WHERE HDR.IS_DELETE = FALSE AND ");
			strBuilder.append(buildQueryNil);
			strBuilder.append(buildSupplyType);
			strBuilder.append(
			        " ) B INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN ");
			strBuilder.append(
			        "INNER JOIN ENTITY_CONFG_PRMTR ECP ON ECP.ENTITY_ID= GSTN.ENTITY_ID ");
			strBuilder.append(
			        "AND ECP.IS_ACTIVE=TRUE AND ECP.IS_DELETE=FALSE AND ECP.QUESTION_CODE IN ('O21'))C ");
			strBuilder.append(buildDocType);
			strBuilder.append(")) GROUP BY 'HSN_ASP' ) "
			        + " GROUP BY TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE, IFNULL( SUM(COUNT_HSNUQC), 0 ) AS COUNT_HSNUQC, IFNULL( SUM(TOTAL_VALUE), 0 ) AS TOTAL_VALUE, IFNULL( SUM(TAXABLE_VALUE), 0 ) AS TAXABLE_VALUE, IFNULL( SUM(TOTAL_TAX), 0 ) AS TOTAL_TAX, IFNULL( SUM(IGST), 0 ) AS IGST, IFNULL( SUM(CGST), 0 ) AS CGST, IFNULL( SUM(SGST), 0 ) AS SGST, IFNULL( SUM(CESS), 0 ) AS CESS FROM ( SELECT 'HSN_UI' AS TAX_DOC_TYPE, RETURN_PERIOD, SUPPLIER_GSTIN, COUNT(DISTINCT HSNSAC || '-' || UQC) COUNT_HSNUQC, SUM(TOTAL_VALUE) AS TOTAL_VALUE, SUM(TAXABLE_VALUE) AS TAXABLE_VALUE, SUM(TOTAL_TAX) AS TOTAL_TAX, SUM(IGST) AS IGST, SUM(CGST) AS CGST, SUM(SGST) AS SGST, SUM(CESS) AS CESS FROM ( SELECT RETURN_PERIOD, SUPPLIER_GSTIN, IFNULL(TOTAL_VALUE, 0) TOTAL_VALUE, IFNULL(TAXABLE_VALUE, 0) TAXABLE_VALUE, IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS TOTAL_TAX, IFNULL(IGST_AMT, 0) IGST, IFNULL(CGST_AMT, 0) CGST, IFNULL(SGST_AMT, 0) SGST, IFNULL(CESS_AMT, 0) CESS, UQC, HSNSAC FROM GSTR1A_USERINPUT_HSNSAC WHERE IS_DELETE = FALSE AND "
			        + buildQueryNil
			        + " ) GROUP BY RETURN_PERIOD, SUPPLIER_GSTIN ) GROUP BY TAX_DOC_TYPE");
		} else if (rateIncludedInHsn && hsnFlag){
			LOGGER.error("Inside Else If block");

			strBuilder = strBuilder
			        .append("SELECT TAX_DOC_TYPE, SUM( IFNULL(COUNT_HSNUQC, 0) ) AS COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE, 0) ) "
			                + "AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX, 0) ) "
			                + "AS TOTAL_TAX, SUM( IFNULL(IGST, 0) ) AS IGST, SUM( IFNULL(CGST, 0) ) AS CGST, SUM( IFNULL(SGST, 0) ) AS SGST,"
			                + " SUM( IFNULL(CESS, 0) ) AS CESS FROM ( SELECT 'HSN_ASP' AS TAX_DOC_TYPE,"
			                + " COUNT( DISTINCT ITM_HSNSAC || '-' || ( CASE WHEN LEFT(ITM_HSNSAC, 2) = 99 then 'NA' else ITM_UQC end ) ||RECORDTYPE ) COUNT_HSNUQC,"
			                + " SUM( IFNULL(TOTAL_VALUE_A, 0)- IFNULL(TOTAL_VALUE_S, 0) ) AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE_A, 0)- IFNULL(TAXABLE_VALUE_S, 0) ) AS TAXABLE_VALUE,"
			                + " SUM( IFNULL(TOTAL_TAX_A, 0)- IFNULL(TOTAL_TAX_S, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST_A, 0)- IFNULL(IGST_S, 0) ) AS IGST,"
			                + " SUM( IFNULL(CGST_A, 0)- IFNULL(CGST_S, 0) ) AS CGST,"
			                + " SUM( IFNULL(SGST_A, 0)- IFNULL(SGST_S, 0) ) AS SGST, SUM( IFNULL(CESS_A, 0)- IFNULL(CESS_S, 0) ) AS CESS,"
			                + " IFNULL(TAX_RATE, 0) TAX_RATE FROM ( SELECT SUPPLIER_GSTIN, DOC_NUM, ITM_UQC, ITM_QTY, ITM_HSNSAC, RETURN_PERIOD,"
			                + " CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_VALUE END TOTAL_VALUE_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE, 0) END TOTAL_VALUE_S,"
			                + " CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_TAX END TOTAL_TAX_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_TAX END TOTAL_TAX_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IGST END IGST_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN IGST END IGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CGST END CGST_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN CGST END CGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN SGST END SGST_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN SGST END SGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CESS END CESS_A,"
			                + " CASE WHEN DOC_TYPE IN('CR') THEN CESS END CESS_S, IFNULL(TAX_RATE, 0) TAX_RATE, RECORDTYPE FROM "
			                + " ( SELECT * FROM ( SELECT B.RETURN_PERIOD, B.SUPPLIER_GSTIN, QUESTION_CODE, ANSWER, B.TAX_DOC_TYPE, B.SUPPLY_TYPE, B.TABLE_SECTION,"
			                + " DOC_TYPE, B.ID, B.DOC_NUM, ONB_LINE_ITEM_AMT, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)',"
			                + " '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(TAXABLE_VALUE, 0) END AS TAXABLE_VALUE,"
			                + " CASE WHEN TABLE_SECTION IN ( '4A','4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)',"
			                + " '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+"
			                + " IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS TOTAL_TAX, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A',"
			                + " '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' )"
			                + " THEN IFNULL(IGST_AMT, 0) END AS IGST, CASE WHEN TABLE_SECTION IN ( '4A','4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)',"
			                + " '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(CGST_AMT, 0) END AS CGST,"
			                + " CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B',"
			                + "'15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(SGST_AMT, 0) END AS SGST, CASE WHEN TABLE_SECTION IN ( '4A', '4B',"
			                + " '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)',"
			                + " '14(ii)' ) THEN IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) "
			                + " END AS CESS, CASE WHEN TABLE_SECTION IN ( '4A', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D',"
			                + " '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('A') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0)"
			                + " WHEN TABLE_SECTION IN ( '4A', '5A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D','15(i)', '15(ii)', '15(iii)',"
			                + " '15(iv)','14(ii)' ) AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION IN"
			                + " ('6A', '9B') AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE IN ('EXPT', 'EXPWT') THEN IFNULL(TAXABLE_VALUE, 0)"
			                + " WHEN TABLE_SECTION IN ('6A', '9B') AND ( QUESTION_CODE = 'O21' AND ANSWER IN ('B') ) AND SUPPLY_TYPE NOT IN ('EXPT', 'EXPWT')"
			                + " THEN IFNULL(ONB_LINE_ITEM_AMT, 0) WHEN TABLE_SECTION = '4B' THEN IFNULL(TAXABLE_VALUE, 0) END AS TOTAL_VALUE, ITM_UQC, ITM_HSNSAC,"
			                + " ITM_QTY, TAX_RATE, RECORDTYPE FROM ( SELECT HDR.RETURN_PERIOD, HDR.SUPPLIER_GSTIN, HDR.TAX_DOC_TYPE, DOC_TYPE, HDR.SUPPLY_TYPE,"
			                + " HDR.TABLE_SECTION, HDR.ID, HDR.DOC_NUM, ONB_LINE_ITEM_AMT, ITM.TAXABLE_VALUE, ITM.IGST_AMT, ITM.CGST_AMT, ITM.SGST_AMT,"
			                + " ITM.CESS_AMT_SPECIFIC, ITM.CESS_AMT_ADVALOREM, ITM.OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, TAX_RATE, "
			               
							+ "	CASE "
							+ "	WHEN ITM.CUST_GSTIN IS NOT NULL AND LENGTH(ITM.CUST_GSTIN) = 15 THEN 'HSN_ASP_B2B' "
							+ "	WHEN (LENGTH(ITM.CUST_GSTIN) < 15 OR ITM.CUST_GSTIN IS NULL)    THEN 'HSN_ASP_B2C' END AS RecordType "

			                
			                +" FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM"
			                + " ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  ");

			strBuilder
			        .append(" WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND HDR.SUPPLY_TYPE <> 'CAN' "
			                + "AND IS_DELETE = false AND ( TABLE_SECTION NOT "
			                + " IN ('8','8A','8B','8C','8D') ");

			strBuilder.append(hdrBuildSupplyType);
			strBuilder.append(buildQuery);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        " SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'B2CS' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, 'TAX' SUPPLY_TYPE, '7' TABLE_SECTION, HDR.ID, '' DOC_NUM,"
			        + " IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS ONB_LINE_ITEM_AMT,"
			        + " NEW_TAXABLE_VALUE AS TAXABLE_VALUE, IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT AS CESS_AMT_SPECIFIC, NULL CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES,"
			        + " NEW_UOM AS ITM_UQC, NEW_HSNORSAC AS ITM_HSNSAC, NEW_QNT AS ITM_QTY, IFNULL(NEW_RATE, 0) TAX_RATE, 'HSN_ASP_B2C' AS RECORDTYPE"
			        + " FROM GSTR1A_PROCESSED_B2CS HDR WHERE HDR.IS_DELETE = FALSE AND IS_AMENDMENT = FALSE AND ");
			strBuilder.append(buildQueryVertical);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        "SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'NILEXTNON' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, SUPPLY_TYPE, '8' TABLE_SECTION, HDR.ID, '' DOC_NUM,"
			        + " TAXABLE_VALUE AS ONB_LINE_ITEM_AMT, TAXABLE_VALUE, 0 IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, 0 CESS_AMT_SPECIFIC, 0 CESS_AMT_ADVALOREM,"
			        + " 0.00 OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, 0.00 TAX_RATE, CASE WHEN TABLE_SECTION IN ( '8A', '8B') THEN 'HSN_ASP_B2B'"
			        + " WHEN TABLE_SECTION IN ( '8C', '8D') THEN 'HSN_ASP_B2C' END AS RecordType FROM GSTR1A_SUMMARY_NILEXTNON HDR WHERE HDR.IS_DELETE = FALSE AND ");
			strBuilder.append(buildQueryNil);
			strBuilder.append(buildSupplyType);

			strBuilder.append(" ) ");

			strBuilder.append(
			        " B INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN INNER JOIN ENTITY_CONFG_PRMTR ECP ON ECP.ENTITY_ID = GSTN.ENTITY_ID"
			        + " AND ECP.IS_ACTIVE = TRUE AND ECP.IS_DELETE = FALSE AND ECP.QUESTION_CODE IN ('O21') ) C ");
			strBuilder.append(buildDocType);
			strBuilder.append(" )) GROUP BY 'HSN_ASP',TAX_RATE"
			        + " ) GROUP BY TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE, SUM(IFNULL(COUNT_HSNUQC, 0) ) AS COUNT_HSNUQC, SUM(IFNULL(TOTAL_VALUE, 0) )"
			        + " AS TOTAL_VALUE, SUM(IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, SUM(IFNULL(TOTAL_TAX, 0) ) AS TOTAL_TAX, SUM(IFNULL(IGST, 0) )"
			        + " AS IGST, SUM(IFNULL(CGST, 0) ) AS CGST, SUM(IFNULL(SGST, 0) ) AS SGST, SUM(IFNULL(CESS, 0)) AS CESS FROM ( SELECT RecordType AS tax_doc_type,"
			        + " Count( DISTINCT itm_hsnsac || '-' || ( CASE WHEN Left(itm_hsnsac, 2) = 99 THEN 'NA' ELSE itm_uqc END)||RecordType) count_hsnuqc,"
			        + " SUM( Ifnull(total_value_a, 0) - Ifnull(total_value_s, 0)) AS total_value, SUM( Ifnull(taxable_value_a, 0)- Ifnull(taxable_value_s, 0) )"
			        + " AS taxable_value, SUM(Ifnull(total_tax_a, 0) - Ifnull(total_tax_s, 0) ) AS total_tax, SUM(Ifnull(igst_a, 0) - Ifnull(igst_s, 0)) AS igst,"
			        + " SUM(Ifnull(cgst_a, 0) - Ifnull(cgst_s, 0)) AS cgst, SUM(Ifnull(sgst_a, 0) - Ifnull(sgst_s, 0)) AS sgst,"
			        + " SUM(Ifnull(cess_a, 0) - Ifnull(cess_s, 0)) AS cess, Ifnull(tax_rate, 0) tax_rate FROM ( SELECT RecordType,supplier_gstin,doc_num,itm_uqc,"
			        + " itm_qty,itm_hsnsac,return_period,CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN total_value END total_value_a, CASE WHEN doc_type IN('CR')"
			        + " THEN Ifnull(total_value, 0) END total_value_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN taxable_value END taxable_value_a, CASE"
			        + " WHEN doc_type IN('CR') THEN taxable_value END taxable_value_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN total_tax END total_tax_a,"
			        + " CASE WHEN doc_type IN('CR') THEN total_tax END total_tax_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN igst END igst_a,"
			        + " CASE WHEN doc_type IN('CR') THEN igst END igst_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN cgst END cgst_a, CASE WHEN doc_type"
			        + " IN('CR') THEN cgst END cgst_s, CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN sgst END sgst_a, CASE WHEN doc_type IN('CR') THEN sgst END sgst_s,"
			        + " CASE WHEN doc_type IN('INV', 'BOS', 'DR') THEN cess END cess_a, CASE WHEN doc_type IN('CR') THEN cess END cess_s, Ifnull(tax_rate, 0) tax_rate FROM "
			        + " ( SELECT * FROM ( SELECT RecordType, b.return_period, b.supplier_gstin,question_code, answer, b.tax_doc_type, b.supply_type, b.table_section,"
			        + " doc_type, b.id, b.doc_num, onb_line_item_amt, CASE WHEN table_section IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8',"
			        + " '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(taxable_value, 0) END AS taxable_value,"
			        + " CASE WHEN table_section IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)',"
			        + " '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(igst_amt, 0)+ Ifnull(cgst_amt, 0)+ Ifnull(sgst_amt, 0)+ Ifnull(cess_amt_specific, 0)+"
			        + " Ifnull(cess_amt_advalorem, 0) END AS total_tax, CASE WHEN table_section IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8',"
			        + " '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(igst_amt, 0) END AS igst,"
			        + " CASE WHEN table_section IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)',"
			        + " '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(cgst_amt, 0) END AS cgst, CASE WHEN table_section IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7',"
			        + " '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) THEN Ifnull(sgst_amt, 0) END AS sgst,"
			        + " CASE WHEN table_section IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)',"
			        + " '15(iii)', '15(iv)', '14(ii)'  ) THEN Ifnull(cess_amt_specific, 0)+ Ifnull(cess_amt_advalorem, 0) END AS cess, CASE WHEN table_section IN"
			        + " ( '4A', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' )"
			        + " AND ( question_code = 'O21' AND answer IN ('A') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ( '4A', '5A', '6B', '6C', '7',"
			        + " '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '15(i)', '15(ii)', '15(iii)', '15(iv)', '14(ii)' ) AND ( "
			        + " question_code = 'O21' AND answer IN ('B') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ('6A', '9B') AND ( question_code = 'O21'"
			        + " AND answer IN ('B') ) AND supply_type IN ('EXPT', 'EXPWT') THEN Ifnull(taxable_value, 0) WHEN table_section IN ('6A', '9B') AND"
			        + " ( question_code = 'O21' AND answer IN ('B') ) AND supply_type NOT IN ('EXPT', 'EXPWT') THEN Ifnull(onb_line_item_amt, 0) WHEN"
			        + " table_section = '4B' THEN Ifnull(taxable_value, 0) END AS total_value, itm_uqc, itm_hsnsac, itm_qty, tax_rate FROM"
			        + " ( SELECT "
			        
					+ "	CASE "
					+ "	WHEN ITM.CUST_GSTIN IS NOT NULL AND LENGTH(ITM.CUST_GSTIN) = 15 THEN 'HSN_ASP_B2B' "
					+ "	WHEN (LENGTH(ITM.CUST_GSTIN) < 15 OR ITM.CUST_GSTIN IS NULL)    THEN 'HSN_ASP_B2C' END AS RecordType "

			        
			        +" , hdr.return_period,"
			        + " hdr.supplier_gstin, hdr.tax_doc_type, doc_type, hdr.supply_type, hdr.table_section, hdr.id, hdr.doc_num, onb_line_item_amt, itm.taxable_value,"
			        + " itm.igst_amt, itm.cgst_amt, itm.sgst_amt, itm.cess_amt_specific, itm.cess_amt_advalorem, itm.other_values, itm_uqc, itm_hsnsac, itm_qty,"
			        + " tax_rate FROM anx_outward_doc_header_1a hdr inner join anx_outward_doc_item_1a itm ON hdr.id = itm.doc_header_id AND"
			        + " hdr.derived_ret_period = itm.derived_ret_period WHERE asp_invoice_status = 2 AND compliance_applicable = TRUE AND hdr.supply_type <> 'CAN'"
			        + " AND is_delete = FALSE and ( table_section NOT IN('8', '8A', '8B', '8C', '8D') "
			        + hdrBuildSupplyType
			        + buildQuery 
			        
			        + " UNION all SELECT 'HSN_ASP_B2C' AS RecordType, return_period, supplier_gstin, 'B2CS' AS tax_doc_type, 'INV' doc_type, 'TAX' supply_type,"
			        + " '7' table_section, hdr.id, '' doc_num, ifnull(new_taxable_value, 0)+ ifnull(igst_amt, 0)+ ifnull(cgst_amt, 0)+ ifnull(sgst_amt, 0)+"
			        + " ifnull(cess_amt, 0) AS onb_line_item_amt, new_taxable_value AS taxable_value, igst_amt, cgst_amt, sgst_amt, cess_amt AS cess_amt_specific,"
			        + " NULL cess_amt_advalorem, 0.00 other_values, new_uom AS itm_uqc, new_hsnorsac AS itm_hsnsac, new_qnt AS itm_qty, ifnull(new_rate, 0) tax_rate"
			        + " FROM gstr1a_processed_b2cs hdr WHERE hdr.is_delete = FALSE AND is_amendment = FALSE AND "
			        + buildQueryVertical
			        + " UNION ALL SELECT CASE WHEN TABLE_SECTION IN ( '8A', '8B') THEN 'HSN_ASP_B2B' WHEN TABLE_SECTION IN ( '8C', '8D') THEN 'HSN_ASP_B2C'"
			        + " END AS RecordType, return_period, supplier_gstin, 'NILEXTNON' AS tax_doc_type, 'INV' doc_type, supply_type, '8' table_section, hdr.id,"
			        + " '' doc_num, taxable_value AS onb_line_item_amt, taxable_value, 0 igst_amt, 0 cgst_amt, 0 sgst_amt, 0 cess_amt_specific, 0 cess_amt_advalorem,"
			        + " 0.00 other_values, itm_uqc, itm_hsnsac, itm_qty, 0.00 tax_rate FROM gstr1a_summary_nilextnon hdr WHERE hdr.is_delete = FALSE AND "
			        + buildQueryNil
			        + buildSupplyType 
			        + " ) b  inner join gstin_info gstn ON b.supplier_gstin = gstn.gstin inner join entity_confg_prmtr ecp ON ecp.entity_id = gstn.entity_id"
			        + " AND ecp.is_active = TRUE AND ecp.is_delete = FALSE AND ecp.question_code IN ('O21') ) c " + buildDocType + " )) "
			        + " GROUP BY tax_rate, RecordType ) GROUP BY tax_doc_type "
			        + " UNION ALL SELECT tax_doc_type, ifnull( SUM(count_hsnuqc), 0 ) AS count_hsnuqc, ifnull( SUM(total_value),0 ) AS total_value,"
			        + " ifnull( SUM(taxable_value),0 ) AS taxable_value, ifnull( SUM(total_tax),0 ) AS total_tax, ifnull( SUM(igst),0 ) AS igst, ifnull( SUM(cgst),0)"
			        + " AS cgst, ifnull(SUM(sgst),0 ) AS sgst, ifnull(SUM(cess),0 ) AS cess FROM "
			        + " ( SELECT 'HSN_UI' AS tax_doc_type, return_period, supplier_gstin, count( DISTINCT hsnsac || '-' || ( CASE WHEN left(hsnsac, 2) = 99 THEN 'NA' "
			        + " ELSE uqc END )||RecordType ) count_hsnuqc, SUM(total_value) AS total_value, SUM(taxable_value) AS taxable_value, SUM(total_tax) AS total_tax,"
			        + " SUM(igst) AS igst, SUM(cgst) AS cgst, SUM(sgst) AS sgst, SUM(cess) AS cess, tax_rate FROM "
			        + " ( SELECT return_period, supplier_gstin, ifnull(total_value, 0) total_value, ifnull(taxable_value, 0) taxable_value, ifnull(igst_amt, 0)+"
			        + " ifnull(cgst_amt, 0)+ ifnull(sgst_amt, 0)+ ifnull(cess_amt, 0) AS total_tax, ifnull(igst_amt, 0) igst, ifnull(cgst_amt, 0) cgst,"
			        + " ifnull(sgst_amt, 0) sgst, ifnull(cess_amt, 0) cess, uqc, hsnsac, tax_rate, Record_Type AS  RecordType FROM gstr1a_userinput_hsnsac"
			        + " WHERE is_delete = FALSE AND "
			        + buildQueryNil 
			        + "  ) GROUP BY return_period, supplier_gstin, tax_rate "
			        + " ) GROUP BY TAX_DOC_TYPE "
					+" UNION ALL SELECT tax_doc_type, ifnull( SUM(count_hsnuqc), 0 ) AS count_hsnuqc, ifnull( SUM(total_value),0 ) AS total_value,"
					+ " ifnull( SUM(taxable_value),0 ) AS taxable_value, ifnull( SUM(total_tax),0 ) AS total_tax, ifnull( SUM(igst),0 ) AS igst, ifnull( SUM(cgst),0)"
					+ " AS cgst, ifnull(SUM(sgst),0 ) AS sgst, ifnull(SUM(cess),0 ) AS cess FROM "
					+ " ( SELECT RecordType AS tax_doc_type, return_period, supplier_gstin, count( DISTINCT hsnsac || '-' || ( CASE WHEN left(hsnsac, 2) = 99 THEN 'NA'"
					+ " ELSE uqc END )||RecordType ) count_hsnuqc, SUM(total_value) AS total_value, SUM(taxable_value) AS taxable_value, SUM(total_tax) AS total_tax,"
					+ " SUM(igst) AS igst, SUM(cgst) AS cgst, SUM(sgst) AS sgst, SUM(cess) AS cess, tax_rate FROM "
	        		+ " ( SELECT Record_Type AS  RecordType, return_period, supplier_gstin, ifnull(total_value, 0) total_value, ifnull(taxable_value, 0) taxable_value,"
	        		+ " ifnull(igst_amt, 0)+ ifnull(cgst_amt, 0)+ ifnull(sgst_amt, 0)+ ifnull(cess_amt, 0) AS total_tax, ifnull(igst_amt, 0) igst,"
	        		+ " ifnull(cgst_amt, 0) cgst, ifnull(sgst_amt, 0) sgst, ifnull(cess_amt, 0) cess, uqc, hsnsac, tax_rate FROM gstr1a_userinput_hsnsac"
	        		+ " WHERE is_delete = FALSE AND "
	        		+ buildQueryNil
	        		+ "  ) GROUP BY return_period, supplier_gstin, tax_rate, RecordType "
			        + " ) GROUP BY TAX_DOC_TYPE ");

		}  else {
			LOGGER.error("Inside Else block");
			
			strBuilder = strBuilder.append(
			        "SELECT TAX_DOC_TYPE, SUM( IFNULL(COUNT_HSNUQC, 0) ) AS COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE, 0) ) AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST, 0) ) AS IGST, SUM( IFNULL(CGST, 0) ) AS CGST, SUM( IFNULL(SGST, 0) ) AS SGST, SUM( IFNULL(CESS, 0) ) AS CESS FROM ( SELECT 'HSN_ASP' AS TAX_DOC_TYPE, COUNT( DISTINCT ITM_HSNSAC || '-' || ( CASE WHEN LEFT(ITM_HSNSAC, 2) = 99 then 'NA' else ITM_UQC end ) ) COUNT_HSNUQC, SUM( IFNULL(TOTAL_VALUE_A, 0)- IFNULL(TOTAL_VALUE_S, 0) ) AS TOTAL_VALUE, SUM( IFNULL(TAXABLE_VALUE_A, 0)- IFNULL(TAXABLE_VALUE_S, 0) ) AS TAXABLE_VALUE, SUM( IFNULL(TOTAL_TAX_A, 0)- IFNULL(TOTAL_TAX_S, 0) ) AS TOTAL_TAX, SUM( IFNULL(IGST_A, 0)- IFNULL(IGST_S, 0) ) AS IGST, SUM( IFNULL(CGST_A, 0)- IFNULL(CGST_S, 0) ) AS CGST, SUM( IFNULL(SGST_A, 0)- IFNULL(SGST_S, 0) ) AS SGST, SUM( IFNULL(CESS_A, 0)- IFNULL(CESS_S, 0) ) AS CESS, IFNULL(TAX_RATE, 0) TAX_RATE FROM ( SELECT SUPPLIER_GSTIN, DOC_NUM, ITM_UQC, ITM_QTY, ITM_HSNSAC, RETURN_PERIOD, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_VALUE END TOTAL_VALUE_A, CASE WHEN DOC_TYPE IN('CR') THEN IFNULL(TOTAL_VALUE, 0) END TOTAL_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A, CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN TOTAL_TAX END TOTAL_TAX_A, CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_TAX END TOTAL_TAX_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN IGST END IGST_A, CASE WHEN DOC_TYPE IN('CR') THEN IGST END IGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CGST END CGST_A, CASE WHEN DOC_TYPE IN('CR') THEN CGST END CGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN SGST END SGST_A, CASE WHEN DOC_TYPE IN('CR') THEN SGST END SGST_S, CASE WHEN DOC_TYPE IN('INV', 'BOS', 'DR') THEN CESS END CESS_A, CASE WHEN DOC_TYPE IN('CR') THEN CESS END CESS_S, IFNULL(TAX_RATE, 0) TAX_RATE FROM ( SELECT * FROM ( SELECT B.RETURN_PERIOD, B.SUPPLIER_GSTIN, QUESTION_CODE, ANSWER, B.TAX_DOC_TYPE, B.SUPPLY_TYPE, B.TABLE_SECTION, DOC_TYPE, B.ID, B.DOC_NUM, ONB_LINE_ITEM_AMT, CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(TAXABLE_VALUE, 0) END AS TAXABLE_VALUE, "
			        + " CASE WHEN TABLE_SECTION IN ( '4A', '4B', '5A', '6A', '6B', '6C', '7', '7A(1)', '7B(1)', '8', '8A', '8B', '8C', '8D', '9B','15(i)', '15(ii)', '15(iii)', '15(iv)','14(ii)' ) THEN IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0) END AS total_tax, CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) THEN Ifnull(igst_amt, 0) END AS igst, CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) THEN Ifnull(cgst_amt, 0) END AS cgst, CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) THEN Ifnull(sgst_amt, 0) END AS sgst, CASE WHEN table_section IN ( '4A','4B','5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) THEN Ifnull(cess_amt_specific, 0)+Ifnull(cess_amt_advalorem, 0) END AS cess, CASE WHEN table_section IN ( '4A', '5A','6A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','9B','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) AND ( question_code = 'O21' AND answer IN ('A') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ( '4A','5A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','15(i)','15(ii)','15(iii)','15(iv)','14(ii)' ) AND ( question_code = 'O21' AND    answer IN ('B') ) THEN Ifnull(onb_line_item_amt, 0) WHEN table_section IN ('6A','9B') AND  ( question_code = 'O21' AND    answer IN ('B') ) AND    supply_type IN ('EXPT','EXPWT') THEN Ifnull(taxable_value, 0) WHEN table_section IN ('6A','9B') AND ( question_code = 'O21' AND    answer IN ('B') ) AND    supply_type NOT IN ('EXPT','EXPWT') THEN Ifnull(onb_line_item_amt, 0) WHEN table_section = '4B' THEN Ifnull(taxable_value, 0) END AS total_value,itm_uqc,itm_hsnsac,itm_qty,tax_rate FROM ( SELECT HDR.RETURN_PERIOD, HDR.SUPPLIER_GSTIN, HDR.TAX_DOC_TYPE, DOC_TYPE, HDR.SUPPLY_TYPE, HDR.TABLE_SECTION, HDR.ID, HDR.DOC_NUM, ONB_LINE_ITEM_AMT, ITM.TAXABLE_VALUE, ITM.IGST_AMT, ITM.CGST_AMT, ITM.SGST_AMT, ITM.CESS_AMT_SPECIFIC, ITM.CESS_AMT_ADVALOREM, ITM.OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, TAX_RATE FROM ANX_OUTWARD_DOC_HEADER_1A HDR INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND HDR.SUPPLY_TYPE <> 'CAN' AND IS_DELETE = false ");
			strBuilder.append(
			        " AND (TABLE_SECTION NOT IN('8','8A','8B','8C','8D') ");
			strBuilder.append(hdrBuildSupplyType);
			strBuilder.append(buildQuery);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        "SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'B2CS' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, 'TAX' SUPPLY_TYPE, '7' TABLE_SECTION, HDR.ID, '' DOC_NUM, IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS ONB_LINE_ITEM_AMT, NEW_TAXABLE_VALUE AS TAXABLE_VALUE, IGST_AMT, CGST_AMT, SGST_AMT, CESS_AMT AS CESS_AMT_SPECIFIC, NULL CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, NEW_UOM AS ITM_UQC, NEW_HSNORSAC AS ITM_HSNSAC, NEW_QNT AS ITM_QTY, IFNULL(NEW_RATE, 0) TAX_RATE FROM GSTR1A_PROCESSED_B2CS HDR WHERE HDR.IS_DELETE = FALSE AND IS_AMENDMENT = FALSE AND ");
			strBuilder.append(buildQueryVertical);
			strBuilder.append(" UNION ALL ");
			strBuilder.append(
			        "SELECT RETURN_PERIOD, SUPPLIER_GSTIN, 'NILEXTNON' AS TAX_DOC_TYPE, 'INV' DOC_TYPE, SUPPLY_TYPE, '8' TABLE_SECTION, HDR.ID, '' DOC_NUM, TAXABLE_VALUE AS ONB_LINE_ITEM_AMT, TAXABLE_VALUE, 0 IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, 0 CESS_AMT_SPECIFIC, 0 CESS_AMT_ADVALOREM, 0.00 OTHER_VALUES, ITM_UQC, ITM_HSNSAC, ITM_QTY, 0.00 TAX_RATE FROM GSTR1A_SUMMARY_NILEXTNON HDR WHERE HDR.IS_DELETE = FALSE AND ");
			strBuilder.append(buildQueryNil);
			strBuilder.append(buildSupplyType);
			strBuilder.append(
			        " ) B INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN INNER JOIN ENTITY_CONFG_PRMTR ECP ON ECP.ENTITY_ID = GSTN.ENTITY_ID AND ECP.IS_ACTIVE = TRUE AND ECP.IS_DELETE = FALSE AND ECP.QUESTION_CODE IN ('O21') ) C ");
			strBuilder.append(buildDocType);
			strBuilder.append(")) GROUP BY 'HSN_ASP',TAX_RATE"
			        + " ) GROUP BY TAX_DOC_TYPE UNION ALL SELECT TAX_DOC_TYPE, IFNULL( SUM(COUNT_HSNUQC), 0 ) AS COUNT_HSNUQC, IFNULL( SUM(TOTAL_VALUE), 0 ) AS TOTAL_VALUE, IFNULL( SUM(TAXABLE_VALUE), 0 ) AS TAXABLE_VALUE, IFNULL( SUM(TOTAL_TAX), 0 ) AS TOTAL_TAX, IFNULL( SUM(IGST), 0 ) AS IGST, IFNULL( SUM(CGST), 0 ) AS CGST, IFNULL( SUM(SGST), 0 ) AS SGST, IFNULL( SUM(CESS), 0 ) AS CESS FROM ( SELECT 'HSN_UI' AS TAX_DOC_TYPE, RETURN_PERIOD, SUPPLIER_GSTIN, COUNT( DISTINCT HSNSAC || '-' || ( CASE WHEN LEFT(HSNSAC, 2) = 99 then 'NA' else UQC end ) ) COUNT_HSNUQC, SUM(TOTAL_VALUE) AS TOTAL_VALUE, SUM(TAXABLE_VALUE) AS TAXABLE_VALUE, SUM(TOTAL_TAX) AS TOTAL_TAX, SUM(IGST) AS IGST, SUM(CGST) AS CGST, SUM(SGST) AS SGST, SUM(CESS) AS CESS, TAX_RATE FROM ( SELECT RETURN_PERIOD, SUPPLIER_GSTIN, IFNULL(TOTAL_VALUE, 0) TOTAL_VALUE, IFNULL(TAXABLE_VALUE, 0) TAXABLE_VALUE, IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) AS TOTAL_TAX, IFNULL(IGST_AMT, 0) IGST, IFNULL(CGST_AMT, 0) CGST, IFNULL(SGST_AMT, 0) SGST, IFNULL(CESS_AMT, 0) CESS, UQC, HSNSAC, TAX_RATE FROM GSTR1A_USERINPUT_HSNSAC WHERE IS_DELETE = FALSE AND "
			        + buildQueryNil
			        + " ) GROUP BY RETURN_PERIOD, SUPPLIER_GSTIN, TAX_RATE"
			        + " ) GROUP BY TAX_DOC_TYPE ");
		}

		String queryStr = strBuilder.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" HSN Section Query Execution END ");
		}

		return queryStr;
	}

	public String loadOnStartSupplyType(List<Long> entityId) {

		StringBuilder buildQuery = new StringBuilder();

		if (!entityId.isEmpty()) {
			buildQuery.append("AND ENTITY_ID IN (:entityId)");

		}

		String sql = "SELECT ANSWER FROM ENTITY_CONFG_PRMTR "
		        + "WHERE QUESTION_CODE = 'O11' " + "AND IS_DELETE = FALSE "
		        + buildQuery.toString();

		Query q = entityManager.createNativeQuery(sql);
		if (!entityId.isEmpty()) {
			q.setParameter("entityId", entityId);
		}
		@SuppressWarnings("unchecked")
		List<Object> list = q.getResultList();

		String supplyType = null;
		if (list != null && !list.isEmpty()) {
			supplyType = (String) list.get(0);
		}

		return supplyType;
	}

	public String loadOnStartDocType(List<Long> entityId) {

		StringBuilder buildQuery = new StringBuilder();

		if (entityId.size() > 0) {

			buildQuery.append(" AND ENTITY_ID IN (:entityId) ");

		}
		String sql = "SELECT ANSWER FROM ENTITY_CONFG_PRMTR "
		        + "WHERE QUESTION_CODE = 'O20' " + "AND IS_DELETE = FALSE "
		        + buildQuery.toString();

		Query q = entityManager.createNativeQuery(sql);
		if (!entityId.isEmpty()) {
			q.setParameter("entityId", entityId);
		}
		@SuppressWarnings("unchecked")
		List<Object> list = q.getResultList();

		String supplyType = null;
		if (list != null && !list.isEmpty()) {
			supplyType = (String) list.get(0);
		}

		return supplyType;
	}
	
	public static void main(String[] args) {
		
		String queryStr = createQueryString("buildQuery", "buildQueryB2cs",
		        "buildQueryNil", "buildQueryVertical", "supplyTypeQuery",
		        "docTypeQuery", "hdrBuildSupplyType", true,true);
		
		System.out.println("Hi");
	}
}
