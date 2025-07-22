/**
 * 
 */
package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author BalaKrishna S
 *
 */

@Service("BasicDocIssuedSummaryScreenSectionDaoImpl")
public class BasicDocIssuedSummaryScreenSectionDaoImpl
		implements BasicGstr1DocIssuedSummaryScreenSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicDocIssuedSummaryScreenSectionDaoImpl.class);

	@Override
	public List<Gstr1SummaryDocSectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub
		
		if (Strings.isNullOrEmpty(request.getReturnType())) {
			request.setReturnType(APIConstants.GSTR1.toUpperCase());
		}

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		

		String taxPeriodReq = req.getTaxPeriod();
		boolean isGstr1a = req.getIsGstr1a();
		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String gstin = null;

		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}

		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");

		}

		String buildQuery = build.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Prepared where Condition and apply in DocIssued Query BEGIN");
		}

		String queryStr = null;
		if (Boolean.TRUE.equals(isGstr1a)) {
		    request.setReturnType(APIConstants.GSTR1A);
		}
		if (!Strings.isNullOrEmpty(request.getReturnType())
				&& APIConstants.GSTR1A
						.equalsIgnoreCase(request.getReturnType())) {
			queryStr = createQueryStringForGstr1a(buildQuery);

		} else {
			queryStr = createQueryString(buildQuery);

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Executing DocIssued Query For Sections is -->" + queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}

			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummaryDocSectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution DocIssued Query "
					+ "getting the data ----->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryDocSectionDto convert(Object[] arr) {
		Gstr1SummaryDocSectionDto obj = new Gstr1SummaryDocSectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		obj.setTotal((Integer) arr[1]);
		obj.setDocCancelled((Integer) arr[2]);
		obj.setNetIssued((Integer) arr[3]);
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "select 'DOC ISSUED' AS TAX_DOC_TYPE,"
				+ "IFNULL(SUM(TOT_NUM),0) AS TOT_NUM_DOC_ISSUED,"
				+ "IFNULL(SUM(CANCELED),0) AS TOT_DOC_CANCELLED,"
				+ "IFNULL(SUM(NET_NUM),0) AS NET_DOC_ISSUED ,COUNT(ID) CNT  "
				+ "FROM GSTR1_PROCESSED_INV_SERIES WHERE IS_DELETE = FALSE "
				+ buildQuery;
		LOGGER.debug("DocIssued Section Query Execution END ");
		return queryStr;
	}
	private String createQueryStringForGstr1a(String buildQuery) {
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "select 'DOC ISSUED' AS TAX_DOC_TYPE,"
				+ "IFNULL(SUM(TOT_NUM),0) AS TOT_NUM_DOC_ISSUED,"
				+ "IFNULL(SUM(CANCELED),0) AS TOT_DOC_CANCELLED,"
				+ "IFNULL(SUM(NET_NUM),0) AS NET_DOC_ISSUED ,COUNT(ID) CNT  "
				+ "FROM GSTR1A_PROCESSED_INV_SERIES WHERE IS_DELETE = FALSE "
				+ buildQuery;
		LOGGER.debug("DocIssued Section Query Execution END ");
		return queryStr;
	}

	/**
	 * Fetching the Data For Nil Section From Query
	 */

	@Override
	public List<Gstr1SummaryNilSectionDto> loadBasicSummarySectionNil(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub
		
		if (Strings.isNullOrEmpty(request.getReturnType())) {
			request.setReturnType(APIConstants.GSTR1.toUpperCase());
		}

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		

		String taxPeriodReq = req.getTaxPeriod();
		boolean isGstr1a = req.getIsGstr1a();
		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		LocalDate docFromDate = req.getDocFromDate();
		LocalDate docToDate = req.getDocToDate();
		List<String> einvGenerated = req.getEINVGenerated();
		List<String> ewbGenerated = req.getEWBGenerated();

		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}
		String einvResp = null;

		if (einvGen != null) {
			if (einvGen.equalsIgnoreCase("NO")) {
				einvResp = "IS NULL";
			} else if (einvGen.equalsIgnoreCase("YES") && einvGen != null) {
				einvResp = "IS NOT NULL";
			}
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
		StringBuilder buildVertical = new StringBuilder();
		StringBuilder mulBuild = new StringBuilder();

		String multiSupplyTypeAns = groupConfigPrmtRepository
				.findAnswerForMultiSupplyType();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
			
					build.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
				

				buildVertical.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ewbGen != null && ewbGen.equalsIgnoreCase("YES")) {
			build.append(" AND EWB_NO_RESP IS NOT NULL ");
		}
		if (ewbResp != null && ewbGen.equalsIgnoreCase("NO")) {
			build.append(" AND EWB_NO_RESP IS NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			build.append(" AND IRN_RESPONSE IS NOT NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			build.append(" AND IRN_RESPONSE IS NULL ");
		}
		if (docFromDate != null && docToDate != null) {
			build.append(" AND DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND HDR.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND HDR.PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND HDR.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND HDR.USERACCESS2 IN :ud2List");

			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND HDR.USERACCESS4 IN :ud4List");

			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND HDR.USERACCESS5 IN :ud5List");

			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND HDR.USERACCESS6 IN :ud6List");

			}
		}
		if (taxPeriod != 0) {
			
				build.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
				mulBuild.append(" DERIVED_RET_PERIOD = :taxPeriod ");
			

			buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery = build.toString().substring(4);
		String buildQueryVert = buildVertical.toString().substring(4);
		String mulCondition = mulBuild.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Prepared where Condition and apply in "
					+ "Nil,Non Exempted Query BEGIN");
		}

		String queryStr = null;
		if (Boolean.TRUE.equals(isGstr1a)) {
		    request.setReturnType(APIConstants.GSTR1A);
		}
		if (!Strings.isNullOrEmpty(request.getReturnType())
				&& APIConstants.GSTR1A
						.equalsIgnoreCase(request.getReturnType())) {
			queryStr = createQueryStringNilForGstr1a(buildQuery,
					buildQueryVert);

		} else {
			queryStr = createQueryStringNil(buildQuery, buildQueryVert,
					multiSupplyTypeAns, mulCondition);

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Nil,Non Exempted Query For Sections is -->"
					+ queryStr);
		}

		try {
			Query q = entityManager.createNativeQuery(queryStr);

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
			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (docFromDate != null && docToDate != null) {
				q.setParameter("docFromDate", docFromDate);
				q.setParameter("docToDate", docToDate);
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
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummaryNilSectionDto> retList = list.parallelStream()
					.map(o -> convertNilSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution Nil,Exempted Query "
					+ "getting the data ----->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Getting error Executing NILNoNEXMPT Query ", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryNilSectionDto convertNilSection(Object[] arr) {
		Gstr1SummaryNilSectionDto obj = new Gstr1SummaryNilSectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		// obj.setDocType((String) arr[0]);
		obj.setAspNitRated((BigDecimal) arr[1]);
		obj.setAspExempted((BigDecimal) arr[2]);
		obj.setAspNonGst((BigDecimal) arr[3]);
		if (arr[4] != null) {
			obj.setTotal((GenUtil.getBigInteger(arr[4])).intValue());
		}
		// obj.setRecords((GenUtil.getBigInteger(arr[1])).intValue());
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	public static void main(String[] args) {
		StringBuilder build = new StringBuilder();
		StringBuilder buildVertical = new StringBuilder();
		StringBuilder mulBuild = new StringBuilder();

		String multiSupplyTypeAns = "A";


				if ("A".equalsIgnoreCase(multiSupplyTypeAns)) {
					LOGGER.debug(
							"Multi Supply Type Answer :" + multiSupplyTypeAns);
					build.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
				} else {
					LOGGER.debug(
							"Multi Supply Type Answer :" + multiSupplyTypeAns);
					build.append(" AND SUPPLIER_GSTIN IN :gstinList");
				}

				buildVertical.append(" AND SUPPLIER_GSTIN IN :gstinList");
		

			if ("A".equalsIgnoreCase(multiSupplyTypeAns)) {
				LOGGER.debug("Multi Supply Type Answer :" + multiSupplyTypeAns);
				build.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
				mulBuild.append(" DERIVED_RET_PERIOD = :taxPeriod ");
			} else {
				LOGGER.debug("Multi Supply Type Answer :" + multiSupplyTypeAns);
				build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			}

			buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			String buildQuery = build.toString().substring(4);
			String buildQueryVert = buildVertical.toString().substring(4);
			String mulCondition = mulBuild.toString();
		String queryStr = createQueryStringNil(buildQuery, buildQueryVert,
				multiSupplyTypeAns, mulCondition);
		System.out.println(queryStr);
	}
	private static String createQueryStringNil(String buildQuery1,
			String buildQuery2, String multiSupplyTypeAns, String mulCondition) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");
		
		String queryStr = "SELECT 'ASP_NILEXTNON' DOC_TYPE,SUM( nil_rated_supplies)  NIL_RATED_SUPPLIES,"
				+ "SUM( exmpted_supplies)  EXMPTED_SUPPLIES,"
				+ "SUM( non_gst_supplies) NON_GST_SUPPLIES,sum(CNT) CNT FROM ( "
				+ "SELECT SUM(CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS','RNV','RDR') AND ITM.SUPPLY_TYPE = 'NIL' THEN ITM.TAXABLE_VALUE "
				+"WHEN DOC_TYPE IN ('CR','RCR') AND ITM.SUPPLY_TYPE = 'NIL' THEN - ITM.TAXABLE_VALUE ELSE 0 END) AS NIL_RATED_SUPPLIES,"
				+"SUM(CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS','RNV','RDR') AND ITM.SUPPLY_TYPE = 'EXT' THEN ITM.TAXABLE_VALUE "
				+"WHEN DOC_TYPE IN ('CR','RCR') AND ITM.SUPPLY_TYPE = 'EXT' THEN - ITM.TAXABLE_VALUE ELSE 0 END) AS EXMPTED_SUPPLIES,"
				 +"SUM(CASE WHEN DOC_TYPE IN ('INV', 'DR', 'BOS','RNV','RDR') AND ITM.SUPPLY_TYPE IN ('NON', 'SCH3') THEN ITM.TAXABLE_VALUE "
				 +"WHEN DOC_TYPE IN ('CR','RCR') AND ITM.SUPPLY_TYPE IN ('NON', 'SCH3') THEN -ITM.TAXABLE_VALUE ELSE 0 END) AS NON_GST_SUPPLIES,Count(HDR.ID)CNT "
				  +"FROM   ANX_OUTWARD_DOC_HEADER HDR "
				  +"INNER JOIN (SELECT ITM_TABLE_SECTION,SUPPLY_TYPE,ITM_TAX_DOC_TYPE, SUM(TAXABLE_VALUE)  AS TAXABLE_VALUE,DOC_HEADER_ID "
				  +"FROM   ANX_OUTWARD_DOC_ITEM WHERE "
				  //+"  DERIVED_RET_PERIOD = :taxPeriod" // make changes here
				  + mulCondition 
				  +"GROUP  BY DOC_HEADER_ID, ITM_TABLE_SECTION, SUPPLY_TYPE,ITM_TAX_DOC_TYPE) ITM "
				  +"ON HDR.ID = ITM.DOC_HEADER_ID WHERE  ITM.ITM_TAX_DOC_TYPE IN ( 'NILEXTNON' ) "
				  +"AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE AND IS_DELETE = FALSE "
				  +"AND HDR.SUPPLY_TYPE <> 'CAN' AND RETURN_TYPE = 'GSTR1' AND "
				 // +"AND HDR.SUPPLIER_GSTIN IN:gstinList AND HDR.DERIVED_RET_PERIOD = :taxPeriod "  // make changes here
				 + buildQuery1
				  +"GROUP  BY HDR.RETURN_PERIOD, HDR.DERIVED_RET_PERIOD, SUPPLIER_GSTIN, ITM.ITM_TABLE_SECTION "
				  +"UNION ALL "
				  +"SELECT SUM(nil_rated_supplies) NIL_RATED_SUPPLIES,SUM(exmpted_supplies) EXMPTED_SUPPLIES,"
				  +"SUM(non_gst_supplies) NON_GST_SUPPLIES,Count(DISTINCT as_processed_id) CNT "
				  +"FROM  (SELECT ( CASE "
				  +"WHEN table_section = '8A' THEN 'Inter-State Supplies to Registered Person' "
				  +"WHEN table_section = '8B' THEN 'Intra-State Supplies to Registered Person' "
				  +"WHEN table_section = '8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				  +"WHEN table_section = '8D' THEN 'Intra-State Supplies to UnRegistered Person' "
				  +"END )             DESCRIPTION,"
				  +"( supplier_gstin ||'|' ||return_period ||'|' ||table_section ) AS DOC_KEY, "
				  +"CASE WHEN supply_type = 'NIL' THEN Ifnull(taxable_value, 0) END AS NIL_RATED_SUPPLIES, "
				  +"CASE WHEN supply_type = 'EXT' THEN Ifnull(taxable_value, 0) END  AS EXMPTED_SUPPLIES, "
				  +"CASE WHEN supply_type IN ( 'NON' ) THEN Ifnull(taxable_value, 0) END AS NON_GST_SUPPLIES, "
				  +"as_processed_id  FROM   gstr1_summary_nilextnon "
				  +" WHERE  is_delete = FALSE AND "
				  //+"supplier_gstin IN:gstinList   AND derived_ret_period = :taxPeriod " // changes
				  + buildQuery2
				  +")) UNION ALL SELECT 'UI_NILEXTNON'  DOC_TYPE, "
				  +"SUM(nil_rated_supplies) NIL_RATED_SUPPLIES, "
				  +"SUM(exmpted_supplies)   EXMPTED_SUPPLIES, "
				  +"SUM(non_gst_supplies)   NON_GST_SUPPLIES, "
				  +"SUM(cnt) CNT "
				  +"FROM   (SELECT Ifnull(SUM(nil_rated_supplies), 0) AS NIL_RATED_SUPPLIES, "
				  +"Ifnull(SUM(exmpted_supplies), 0)   AS EXMPTED_SUPPLIES, "
				  +"Ifnull(SUM(non_gst_supplies), 0)   AS NON_GST_SUPPLIES, "
				  +"Count(cnt_id) CNT "
				  +"FROM   (SELECT Ifnull(SUM(nil_rated_supplies),0) NIL_RATED_SUPPLIES, "
				  +"Ifnull(SUM(exmpted_supplies), 0) EXMPTED_SUPPLIES, "
				  +"Ifnull(SUM(non_gst_supplies), 0) NON_GST_SUPPLIES,"
				  +"Count(id) CNT_ID  FROM   gstr1_userinput_nilextnon "
				  +"WHERE  is_delete = FALSE AND "
				  //+" supplier_gstin IN:gstinList AND derived_ret_period = :taxPeriod " // make changes here
				  +buildQuery2
				  +"GROUP  BY id,return_period,description_key,supplier_gstin,derived_ret_period,doc_key))";

				  /*
			String queryStr = "SELECT 'ASP_NILEXTNON' DOC_TYPE,SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES,"
					+ "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES,"
					+ "SUM( NON_GST_SUPPLIES) NON_GST_SUPPLIES,sum(CNT) CNT FROM ( "
					+ " SELECT (CASE WHEN ITM.ITM_TABLE_SECTION ='8A' THEN  'Inter-State Supplies to Registered Person' "
					+ " WHEN ITM.ITM_TABLE_SECTION ='8B' THEN  'Intra-State Supplies to Registered Person' "
					+ " WHEN ITM.ITM_TABLE_SECTION ='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
					+ " WHEN ITM.ITM_TABLE_SECTION ='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION, "
					+ " (SUPPLIER_GSTIN || '|' || HDR.RETURN_PERIOD || '|' ||  ITM.ITM_TABLE_SECTION ) AS DOC_KEY,"
					+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS','RNV', 'RDR') AND  ITM.SUPPLY_TYPE ='NIL' THEN  ITM.TAXABLE_VALUE  END),0) -  IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR' ,'RCR')  AND  ITM.SUPPLY_TYPE ='NIL' THEN  ITM.TAXABLE_VALUE END),0) AS NIL_RATED_SUPPLIES, "
					+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS', 'RNV', 'RDR') AND ITM.SUPPLY_TYPE ='EXT' THEN  ITM.TAXABLE_VALUE END),0) -  IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR' ,'RCR')  AND  ITM.SUPPLY_TYPE ='EXT' THEN  ITM.TAXABLE_VALUE END),0) AS EXMPTED_SUPPLIES, "
					+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS', 'RNV', 'RDR') AND  ITM.SUPPLY_TYPE  IN ('NON','SCH3') THEN  ITM.TAXABLE_VALUE END),0) -  IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR' ,'RCR')  AND  ITM.SUPPLY_TYPE IN ('NON','SCH3') THEN  ITM.TAXABLE_VALUE END),0) AS NON_GST_SUPPLIES, "
					+ " HDR.RETURN_PERIOD, " + " HDR.DERIVED_RET_PERIOD, "
					+ " SUPPLIER_GSTIN, "
					+ "  ITM.ITM_TABLE_SECTION AS DESCRIPTION_KEY,"
					+ " COUNT(HDR.ID) CNT "
					+ " FROM ANX_OUTWARD_DOC_HEADER HDR "
					+ " INNER JOIN (SELECT ITM_TABLE_SECTION, SUPPLY_TYPE, ITM_TAX_DOC_TYPE, SUM(TAXABLE_VALUE) AS TAXABLE_VALUE, DOC_HEADER_ID "
					+ " FROM ANX_OUTWARD_DOC_ITEM WHERE "
					+ mulCondition 
					+ " GROUP BY DOC_HEADER_ID,ITM_TABLE_SECTION,SUPPLY_TYPE,ITM_TAX_DOC_TYPE) ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ " WHERE  ITM.ITM_TAX_DOC_TYPE IN ('NILEXTNON')  "
					+ " AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE "
					+ " AND IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN' AND RETURN_TYPE='GSTR1' AND "
					+ buildQuery1
					+ " GROUP BY HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,SUPPLIER_GSTIN,  ITM.ITM_TABLE_SECTION ,ITM.ITM_TABLE_SECTION "
					+ " UNION ALL "
					+ "SELECT 'A' DESCRIPTION,'A' DOC_KEY,SUM(NIL_RATED_SUPPLIES) NIL_RATED_SUPPLIES,"
					+ "SUM(EXMPTED_SUPPLIES) EXMPTED_SUPPLIES,SUM(NON_GST_SUPPLIES) NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, 'A' DESCRIPTION_KEY,"
					+ "COUNT(DISTINCT AS_PROCESSED_ID) CNT "
					+ "FROM( SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  'Inter-State Supplies to Registered Person' "
					+ "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
					+ "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
					+ "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
					+ "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
					+ "CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES,"
					+ "CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES,"
					+ "CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE,0) END AS NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY,AS_PROCESSED_ID "
					+ "FROM GSTR1_SUMMARY_NILEXTNON "
					+ "WHERE IS_DELETE = FALSE AND " + buildQuery2
					+ ") GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN ) "
					+ "UNION ALL " + "SELECT 'UI_NILEXTNON' DOC_TYPE,"
					+ "SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES,"
					+ "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES,"
					+ "SUM( NON_GST_SUPPLIES)  NON_GST_SUPPLIES,SUM(CNT) CNT FROM ( "
					+ "SELECT IFNULL(SUM(NIL_RATED_SUPPLIES ), 0) AS NIL_RATED_SUPPLIES,"
					+ "IFNULL(SUM(EXMPTED_SUPPLIES ), 0) AS EXMPTED_SUPPLIES,"
					+ "IFNULL(SUM(NON_GST_SUPPLIES ), 0) AS NON_GST_SUPPLIES,COUNT(CNT_ID) CNT "
					+ "FROM ( SELECT "
					+ "(CASE WHEN  DESCRIPTION_KEY='8A' THEN  'Inter-State Supplies to Registered Person' "
					+ "WHEN  DESCRIPTION_KEY='8B' THEN  'Intra-State Supplies to Registered Person' "
					+ "WHEN  DESCRIPTION_KEY='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
					+ "WHEN  DESCRIPTION_KEY='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
					+ "DOC_KEY,IFNULL(SUM(NIL_RATED_SUPPLIES),0) NIL_RATED_SUPPLIES,"
					+ "IFNULL(SUM(EXMPTED_SUPPLIES),0) EXMPTED_SUPPLIES,"
					+ "IFNULL(SUM(NON_GST_SUPPLIES),0) NON_GST_SUPPLIES,"
					+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY,count(ID) CNT_ID "
					+ "FROM GSTR1_USERINPUT_NILEXTNON WHERE "
					+ "IS_DELETE=FALSE AND " + buildQuery2
					+ "GROUP BY ID,RETURN_PERIOD,DESCRIPTION_KEY,SUPPLIER_GSTIN,DERIVED_RET_PERIOD,DOC_KEY))";
			*/		
		
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("createQueryStringNil query " + queryStr);
			}

			LOGGER.debug("Nil Section Query Execution END ");
			return queryStr;
		

			/*
			 * String queryStr = "SELECT 'ASP_NILEXTNON' DOC_TYPE," +
			 * "SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES," +
			 * "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES," +
			 * "SUM( NON_GST_SUPPLIES) NON_GST_SUPPLIES,sum(CNT) CNT  " +
			 * "FROM ( " +
			 * "SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  'Inter-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
			 * +
			 * "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) "
			 * + "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND " +
			 * "SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) AS NIL_RATED_SUPPLIES,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) "
			 * + "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND " +
			 * "SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS EXMPTED_SUPPLIES,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND " +
			 * "SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) " +
			 * "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND SUPPLY_TYPE IN ('NON','SCH3') "
			 * + "THEN TAXABLE_VALUE END),0) AS NON_GST_SUPPLIES," +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY,COUNT(ID) CNT "
			 * + "FROM ANX_OUTWARD_DOC_HEADER  " +
			 * "WHERE TAX_DOC_TYPE IN ('NILEXTNON') " +
			 * "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE  " +
			 * "AND IS_DELETE = FALSE AND SUPPLY_TYPE <> 'CAN' AND RETURN_TYPE='GSTR1' AND "
			 * + buildQuery1 +
			 * "GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION "
			 * + "UNION ALL " +
			 * "SELECT DESCRIPTION,DOC_KEY,NIL_RATED_SUPPLIES,EXMPTED_SUPPLIES,NON_GST_SUPPLIES,"
			 * +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY,COUNT(ID) CNT "
			 * + "FROM( " +
			 * "SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  'Inter-State Supplies to Registered Person'  "
			 * +
			 * "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
			 * +
			 * "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
			 * +
			 * "CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES,"
			 * +
			 * "CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES,"
			 * +
			 * "CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE,0) END AS NON_GST_SUPPLIES,"
			 * +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY,ID "
			 * + "FROM GSTR1_SUMMARY_NILEXTNON " +
			 * "WHERE IS_DELETE = FALSE AND " + buildQuery2 + ") " +
			 * "GROUP BY DESCRIPTION,DOC_KEY,NIL_RATED_SUPPLIES,EXMPTED_SUPPLIES,NON_GST_SUPPLIES,"
			 * +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY ) "
			 * + "UNION ALL  " + "SELECT 'UI_NILEXTNON' DOC_TYPE," +
			 * "SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES," +
			 * "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES," +
			 * "SUM( NON_GST_SUPPLIES)  NON_GST_SUPPLIES,SUM(CNT) CNT FROM ( SELECT "
			 * + "IFNULL(SUM(NIL_RATED_SUPPLIES ), 0) AS NIL_RATED_SUPPLIES," +
			 * "IFNULL(SUM(EXMPTED_SUPPLIES ), 0) AS EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES ), 0) AS NON_GST_SUPPLIES,COUNT(CNT_ID) CNT "
			 * + "FROM ( SELECT " +
			 * "(CASE WHEN  DESCRIPTION_KEY='8A' THEN  'Inter-State Supplies to Registered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
			 * + "DOC_KEY,IFNULL(SUM(NIL_RATED_SUPPLIES),0) NIL_RATED_SUPPLIES,"
			 * + "IFNULL(SUM(EXMPTED_SUPPLIES),0) EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES),0) NON_GST_SUPPLIES," +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY,count(ID) CNT_ID "
			 * + "FROM GSTR1_USERINPUT_NILEXTNON WHERE " +
			 * "IS_DELETE=FALSE AND " + buildQuery2 +
			 * "GROUP BY ID,RETURN_PERIOD,DESCRIPTION_KEY,SUPPLIER_GSTIN,DERIVED_RET_PERIOD,DOC_KEY))"
			 * ;
			 * 
			 * 
			 */

			/*
			 * String queryStr = "SELECT 'ASP_NILEXTNON' DOC_TYPE," +
			 * "SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES," +
			 * "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES," +
			 * "SUM( NON_GST_SUPPLIES) NON_GST_SUPPLIES,SUM(CNT) CNT " +
			 * "FROM ( SELECT " +
			 * "IFNULL(SUM(NIL_RATED_SUPPLIES ), 0) AS NIL_RATED_SUPPLIES," +
			 * "IFNULL(SUM(EXMPTED_SUPPLIES ), 0) AS EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES ), 0) AS NON_GST_SUPPLIES,SUM(CNT) CNT "
			 * + "FROM ( " +
			 * "SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  'Inter-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
			 * +
			 * "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) "
			 * + "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND " +
			 * "SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) AS NIL_RATED_SUPPLIES,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) "
			 * + "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND " +
			 * "SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS EXMPTED_SUPPLIES,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND " +
			 * "SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) " +
			 * "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND SUPPLY_TYPE IN ('NON','SCH3') "
			 * + "THEN TAXABLE_VALUE END),0) AS NON_GST_SUPPLIES," +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY,COUNT(ID) CNT "
			 * + "FROM ANX_OUTWARD_DOC_HEADER " +
			 * "WHERE TAX_DOC_TYPE IN ('NILEXTNON') " +
			 * "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE  " +
			 * "AND IS_DELETE = FALSE AND SUPPLY_TYPE <> 'CAN' AND RETURN_TYPE='GSTR1' AND "
			 * + buildQuery1 +
			 * "GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION "
			 * + "UNION ALL " +
			 * "SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  'Inter-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
			 * +
			 * "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
			 * +
			 * "CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES,"
			 * +
			 * "CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES,"
			 * +
			 * "CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE,0) END AS NON_GST_SUPPLIES,"
			 * +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY,ID "
			 * + "FROM GSTR1_SUMMARY_NILEXTNON WHERE IS_DELETE = FALSE AND " +
			 * buildQuery2 + ")) UNION ALL SELECT 'UI_NILEXTNON' DOC_TYPE," +
			 * "SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES," +
			 * "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES," +
			 * "SUM( NON_GST_SUPPLIES)  NON_GST_SUPPLIES,SUM(CNT) CNT FROM ( " +
			 * "SELECT " +
			 * "IFNULL(SUM(NIL_RATED_SUPPLIES ), 0) AS NIL_RATED_SUPPLIES," +
			 * "IFNULL(SUM(EXMPTED_SUPPLIES ), 0) AS EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES ), 0) AS NON_GST_SUPPLIES,COUNT(CNT_ID) CNT "
			 * + "FROM ( SELECT " +
			 * "(CASE WHEN  DESCRIPTION_KEY='8A' THEN  'Inter-State Supplies to Registered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
			 * + "DOC_KEY,IFNULL(SUM(NIL_RATED_SUPPLIES),0) NIL_RATED_SUPPLIES,"
			 * + "IFNULL(SUM(EXMPTED_SUPPLIES),0) EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES),0) NON_GST_SUPPLIES," +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY,ID CNT_ID "
			 * + "FROM GSTR1_USERINPUT_NILEXTNON WHERE " +
			 * "IS_DELETE=FALSE AND " + buildQuery2 +
			 * "GROUP BY ID,RETURN_PERIOD,DESCRIPTION_KEY,SUPPLIER_GSTIN,DERIVED_RET_PERIOD,DOC_KEY))"
			 * ;
			 */
			/*
			 * String queryStr = "SELECT DOC_TYPE," +
			 * "IFNULL(SUM(NIL_RATED_SUPPLIES),0) NIL_RATED_SUPPLIES," +
			 * "IFNULL(SUM(EXMPTED_SUPPLIES),0) EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES),0) NON_GST_SUPPLIES " +
			 * "FROM ( SELECT DOC_TYPE," + "CASE WHEN SUPPLY_TYPE='NIL' THEN " +
			 * "IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES," +
			 * "CASE WHEN SUPPLY_TYPE='EXT' THEN " +
			 * "IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES," +
			 * "CASE WHEN SUPPLY_TYPE IN ('NON','SCH3') THEN " +
			 * "IFNULL(DOC_AMT,0) END AS NON_GST_SUPPLIES " +
			 * "FROM ANX_OUTWARD_DOC_HEADER " +
			 * "WHERE TAX_DOC_TYPE IN ('NILEXTNON') " +
			 * "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE = TRUE " +
			 * "AND IS_DELETE = FALSE " + "AND RETURN_TYPE='GSTR1' " +
			 * buildQuery1 +"UNION ALL  " + "SELECT 'INV' AS DOC_TYPE," +
			 * "CASE WHEN SUPPLY_TYPE='NIL' THEN " +
			 * "IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES," +
			 * "CASE WHEN SUPPLY_TYPE='EXT' THEN " +
			 * "IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES," +
			 * "CASE WHEN SUPPLY_TYPE IN ('NON') THEN " +
			 * "IFNULL(TAXABLE_VALUE,0) END AS NON_GST_SUPPLIES " +
			 * "FROM GSTR1_SUMMARY_NILEXTNON " + "WHERE IS_DELETE = FALSE " +
			 * buildQueryVert + ") GROUP BY DOC_TYPE ";
			 */
			/*
			 * String queryStr = "SELECT TABLE_SECTION," +
			 * "SUM(NIL_RATED_SUPPLIES) NIL_RATED_SUPPLIES," +
			 * "SUM(EXMPTED_SUPPLIES) EXMPTED_SUPPLIES," +
			 * "SUM(NON_GST_SUPPLIES)  NON_GST_SUPPLIES " +
			 * "FROM ( SELECT 'NILEXTNON' AS TABLE_SECTION," +
			 * "IFNULL(SUM(NIL_RATED_SUPPLIES),0) NIL_RATED_SUPPLIES," +
			 * "IFNULL(SUM(EXMPTED_SUPPLIES),0) EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES),0) NON_GST_SUPPLIES " +
			 * "FROM GSTR1_USERINPUT_NILEXTNON WHERE IS_DELETE=FALSE " +
			 * buildQuery1 + " ) GROUP BY TABLE_SECTION";
			 */
			/*
			 * String queryStr = "SELECT 'NILEXTNON' AS TAX_DOC_TYPE," +
			 * "SUM(ASP_NIL_RATED_SUPPLIES) ASP_NIL_RATED_SUPPLIES," +
			 * "SUM(ASP_EXMPTED_SUPPLIES) ASP_EXMPTED_SUPPLIES," +
			 * "SUM(ASP_NON_GST_SUPPLIES) AS ASP_NON_GST_SUPPLIES,"
			 * SUM(UI_NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES,
			 * SUM(UI_EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES,
			 * SUM(UI_NON_GST_SUPPLIES) UI_NON_GST_SUPPLIES ,
			 * +"RETURN_PERIOD,SUPPLIER_GSTIN FROM ( " +
			 * "SELECT DESCRIPTION,DOC_KEY,ASP_NIL_RATED_SUPPLIES," +
			 * "ASP_EXMPTED_SUPPLIES, ASP_NON_GST_SUPPLIES," +
			 * "0 UI_NIL_RATED_SUPPLIES,0 UI_EXMPTED_SUPPLIES," +
			 * "0 UI_NON_GST_SUPPLIES,RETURN_PERIOD,DERIVED_RET_PERIOD," +
			 * "SUPPLIER_GSTIN, DESCRIPTION_KEY FROM ( " +
			 * "SELECT DESCRIPTION,DOC_KEY," +
			 * "IFNULL(SUM(NIL_RATED_SUPPLIES),0) ASP_NIL_RATED_SUPPLIES," +
			 * "IFNULL(SUM(EXMPTED_SUPPLIES),0) ASP_EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES),0) ASP_NON_GST_SUPPLIES," +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
			 * + "FROM ( " + "SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  " +
			 * "'Inter-State Supplies to Registered Person' " +
			 * "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION, "
			 * +
			 * "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') " +
			 * "AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) " +
			 * "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  " +
			 * "AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) AS NIL_RATED_SUPPLIES,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') " +
			 * "AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) " +
			 * "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  " +
			 * "AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS EXMPTED_SUPPLIES,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') " +
			 * "AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) " +
			 * "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  " +
			 * "AND SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) " +
			 * "AS NON_GST_SUPPLIES,RETURN_PERIOD,DERIVED_RET_PERIOD," +
			 * "SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY " +
			 * "FROM ANX_OUTWARD_DOC_HEADER WHERE TAX_DOC_TYPE IN ('NILEXTNON') "
			 * + "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE  " +
			 * "AND IS_DELETE = FALSE AND RETURN_TYPE='GSTR1' AND " +
			 * buildQuery1 +
			 * "GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN,TABLE_SECTION "
			 * + "UNION ALL " + "SELECT (CASE WHEN  TABLE_SECTION='8A' " +
			 * "THEN  'Inter-State Supplies to Registered Person' " +
			 * "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
			 * +
			 * "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
			 * + "CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE,0) " +
			 * "END AS NIL_RATED_SUPPLIES," +
			 * "CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE,0) " +
			 * "END AS EXMPTED_SUPPLIES," +
			 * "CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE,0) "
			 * + "END AS NON_GST_SUPPLIES,RETURN_PERIOD,DERIVED_RET_PERIOD," +
			 * "SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY " +
			 * "FROM GSTR1_SUMMARY_NILEXTNON WHERE IS_DELETE = FALSE AND " +
			 * buildQuery1 +
			 * ") GROUP BY DESCRIPTION,SUPPLIER_GSTIN,DERIVED_RET_PERIOD," +
			 * "DESCRIPTION_KEY,RETURN_PERIOD,DOC_KEY ) " +
			 * "UNION ALL SELECT DESCRIPTION,DOC_KEY," +
			 * "0 ASP_NIL_RATED_SUPPLIES,0 ASP_EXMPTED_SUPPLIES, 0 ASP_NON_GST_SUPPLIES,"
			 * +
			 * "UI_NIL_RATED_SUPPLIES,UI_EXMPTED_SUPPLIES,UI_NON_GST_SUPPLIES,"
			 * +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
			 * + "FROM ( SELECT DESCRIPTION,DOC_KEY," +
			 * "SUM(NIL_RATED_SUPPLIES) UI_NIL_RATED_SUPPLIES," +
			 * "SUM(EXMPTED_SUPPLIES) UI_EXMPTED_SUPPLIES," +
			 * "SUM(NON_GST_SUPPLIES) UI_NON_GST_SUPPLIES," +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
			 * + "FROM ( SELECT " +
			 * "(CASE WHEN  DESCRIPTION_KEY='8A' THEN  'Inter-State Supplies to Registered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8B' THEN  'Intra-State Supplies to Registered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
			 * +
			 * "WHEN  DESCRIPTION_KEY='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,DOC_KEY,"
			 * + "IFNULL(SUM(NIL_RATED_SUPPLIES),0) NIL_RATED_SUPPLIES," +
			 * "IFNULL(SUM(EXMPTED_SUPPLIES),0) EXMPTED_SUPPLIES," +
			 * "IFNULL(SUM(NON_GST_SUPPLIES),0) NON_GST_SUPPLIES," +
			 * "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY "
			 * + "FROM GSTR1_USERINPUT_NILEXTNON WHERE IS_DELETE=FALSE AND " +
			 * buildQuery2 +
			 * "GROUP BY RETURN_PERIOD,DESCRIPTION_KEY,SUPPLIER_GSTIN," +
			 * "DERIVED_RET_PERIOD,DOC_KEY ) " +
			 * "GROUP BY RETURN_PERIOD,DESCRIPTION_KEY,DESCRIPTION," +
			 * "SUPPLIER_GSTIN,DERIVED_RET_PERIOD,DOC_KEY )) " +
			 * "GROUP BY RETURN_PERIOD,SUPPLIER_GSTIN";
			 */

		

	}

	private static String createQueryStringNilForGstr1a(String buildQuery1,
			String buildQuery2) {
		String queryStr = "SELECT 'ASP_NILEXTNON' DOC_TYPE,SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES,"
				+ "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES,"
				+ "SUM( NON_GST_SUPPLIES) NON_GST_SUPPLIES,sum(CNT) CNT FROM ( "
				+ "SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  'Inter-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
				+ "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
				+ "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) "
				+ "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND "
				+ "SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE END),0) AS NIL_RATED_SUPPLIES,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) "
				+ "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND "
				+ "SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE END),0) AS EXMPTED_SUPPLIES,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','BOS') AND "
				+ "SUPPLY_TYPE IN ('NON','SCH3') THEN TAXABLE_VALUE END),0) "
				+ "-  IFNULL(SUM(CASE WHEN DOC_TYPE='CR'  AND SUPPLY_TYPE IN ('NON','SCH3') "
				+ "THEN TAXABLE_VALUE END),0) AS NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY,COUNT(ID) CNT "
				+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR "
				+ "WHERE TAX_DOC_TYPE IN ('NILEXTNON') "
				+ "AND ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE "
				+ "AND IS_DELETE = FALSE AND SUPPLY_TYPE <> 'CAN' AND RETURN_TYPE='GSTR1A' AND "
				// AND SUPPLIER_GSTIN IN ('33GSPTN0481G1ZA') AND
				// DERIVED_RET_PERIOD = 202003
				+ buildQuery1
				+ "GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION "
				+ "UNION ALL "
				+ "SELECT 'A' DESCRIPTION,'A' DOC_KEY,SUM(NIL_RATED_SUPPLIES) NIL_RATED_SUPPLIES,"
				+ "SUM(EXMPTED_SUPPLIES) EXMPTED_SUPPLIES,SUM(NON_GST_SUPPLIES) NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, 'A' DESCRIPTION_KEY,"
				+ "COUNT(DISTINCT AS_PROCESSED_ID) CNT "
				+ "FROM( SELECT (CASE WHEN  TABLE_SECTION='8A' THEN  'Inter-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8B' THEN  'Intra-State Supplies to Registered Person' "
				+ "WHEN  TABLE_SECTION='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
				+ "WHEN  TABLE_SECTION='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
				+ "(SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY,"
				+ "CASE WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE,0) END AS NIL_RATED_SUPPLIES,"
				+ "CASE WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE,0) END AS EXMPTED_SUPPLIES,"
				+ "CASE WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE,0) END AS NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, TABLE_SECTION AS DESCRIPTION_KEY,AS_PROCESSED_ID "
				+ "FROM GSTR1A_SUMMARY_NILEXTNON "
				+ "WHERE IS_DELETE = FALSE AND " + buildQuery2
				+ ") GROUP BY RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN ) "
				+ "UNION ALL " + "SELECT 'UI_NILEXTNON' DOC_TYPE,"
				+ "SUM( NIL_RATED_SUPPLIES)  NIL_RATED_SUPPLIES,"
				+ "SUM( EXMPTED_SUPPLIES)  EXMPTED_SUPPLIES,"
				+ "SUM( NON_GST_SUPPLIES)  NON_GST_SUPPLIES,SUM(CNT) CNT FROM ( "
				+ "SELECT IFNULL(SUM(NIL_RATED_SUPPLIES ), 0) AS NIL_RATED_SUPPLIES,"
				+ "IFNULL(SUM(EXMPTED_SUPPLIES ), 0) AS EXMPTED_SUPPLIES,"
				+ "IFNULL(SUM(NON_GST_SUPPLIES ), 0) AS NON_GST_SUPPLIES,COUNT(CNT_ID) CNT "
				+ "FROM ( SELECT "
				+ "(CASE WHEN  DESCRIPTION_KEY='8A' THEN  'Inter-State Supplies to Registered Person' "
				+ "WHEN  DESCRIPTION_KEY='8B' THEN  'Intra-State Supplies to Registered Person' "
				+ "WHEN  DESCRIPTION_KEY='8C' THEN  'Inter-State Supplies to UnRegistered Person' "
				+ "WHEN  DESCRIPTION_KEY='8D' THEN  'Intra-State Supplies to UnRegistered Person' END ) DESCRIPTION,"
				+ "DOC_KEY,IFNULL(SUM(NIL_RATED_SUPPLIES),0) NIL_RATED_SUPPLIES,"
				+ "IFNULL(SUM(EXMPTED_SUPPLIES),0) EXMPTED_SUPPLIES,"
				+ "IFNULL(SUM(NON_GST_SUPPLIES),0) NON_GST_SUPPLIES,"
				+ "RETURN_PERIOD,DERIVED_RET_PERIOD,SUPPLIER_GSTIN, DESCRIPTION_KEY,count(ID) CNT_ID "
				+ "FROM GSTR1A_USERINPUT_NILEXTNON WHERE "
				+ "IS_DELETE=FALSE AND "
				// AND SUPPLIER_GSTIN IN ('33GSPTN0481G1ZA') AND
				// DERIVED_RET_PERIOD = 202003
				+ buildQuery2
				+ "GROUP BY ID,RETURN_PERIOD,DESCRIPTION_KEY,SUPPLIER_GSTIN,DERIVED_RET_PERIOD,DOC_KEY))";

		LOGGER.debug("Nil Section Query Execution END ");
		
		
		return queryStr;
	}
	
	
	
}
