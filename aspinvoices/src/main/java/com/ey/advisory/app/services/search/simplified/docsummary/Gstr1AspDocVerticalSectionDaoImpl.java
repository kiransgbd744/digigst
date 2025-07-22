package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputHsnSacRepository;
import com.ey.advisory.app.docs.dto.Gstr1HsnSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import com.google.common.base.Strings;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr1AspDocVerticalSectionDaoImpl")
public class Gstr1AspDocVerticalSectionDaoImpl {

	@Autowired
	@Qualifier("OnboardingConfigParmUtil")
	OnboardingConfigParmUtil onBoardingAnswer;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnOrSacRepository;

	@Autowired
	@Qualifier("Gstr1UserInputHsnSacRepository")
	Gstr1UserInputHsnSacRepository userHsnRepo;

	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AspDocVerticalSectionDaoImpl.class);

	private static final String CONF_KEY = "hsn.section.derived.taxperiod";
	private static final String CONF_CATEG = "HSN_SECTION";
	
	public List<Gstr1SummaryDocSectionDto> basicDocSummarySection(
			Annexure1SummaryReqDto request) {
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

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
		StringBuilder buildVertical = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildVertical.append(" SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildVertical.append(" AND PROFIT_CENTRE IN :pcList ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildVertical.append(" AND PLANT_CODE IN :plantList ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildVertical.append(" AND SALES_ORGANIZATION IN :salesList ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				buildVertical
						.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildVertical.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildVertical.append(" AND DIVISION IN :divisionList ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildVertical.append(" AND LOCATION IN :locationList ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildVertical.append(" AND USERACCESS1 IN :ud1List ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildVertical.append(" AND USERACCESS2 IN :ud2List ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildVertical.append(" AND USERACCESS3 IN :ud3List ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildVertical.append(" AND USERACCESS4 IN :ud4List ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildVertical.append(" AND USERACCESS5 IN :ud5List ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildVertical.append(" AND USERACCESS6 IN :ud6List ");
			}
		}
		if (taxPeriod != 0) {

			buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery2 = buildVertical.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DOC Isssue Build Query For DOC Issue  ----->",
					buildQuery2);
		}

		String queryStr = createQueryString(buildQuery2);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Executing Query  -->" + queryStr);
		}

		List<Gstr1SummaryDocSectionDto> retList = new ArrayList<>();
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
			retList = list.parallelStream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Fetching Transactional & Vertical Data ----->",
					retList);

		} catch (Exception e) {
			LOGGER.error("While Fetching DOC Issue Data getting Error ", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
		return retList;
	}

	private Gstr1SummaryDocSectionDto convert(Object[] arr) {

		Gstr1SummaryDocSectionDto obj = new Gstr1SummaryDocSectionDto();
		obj.setSrNo((Integer) arr[0]);
		obj.setDocName((String) arr[1]);
		obj.setTotal((Integer) arr[0]);
		obj.setDocCancelled((Integer) arr[0]);
		obj.setNetIssued((Integer) arr[0]);
		return obj;
	}

	private String createQueryString(String buildQuery2) {
		String queryStr = "SELECT SERIAL_NUM,NATURE_OF_DOC,"
				+ "IFNULL(SUM(TOT_NUM),0) AS TOT_NUM,"
				+ "IFNULL(SUM(CANCELED),0) AS CANCELED,"
				+ "IFNULL(SUM(NET_NUM),0) AS NET_ISSUED " + "FROM "
				+ "GSTR1_PROCESSED_INV_SERIES " + "WHERE IS_DELETE=FALSE AND "
				+ buildQuery2 + "GROUP BY SERIAL_NUM,NATURE_OF_DOC ";
		LOGGER.debug("B2cs Vertical & Transactional Query executed ");
		return queryStr;
	}

	public List<Gstr1SummaryDocSectionDto> docBasicSummarySection(
			Annexure1SummaryReqDto request) {
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

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
		StringBuilder buildVertical = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildVertical.append(" SUPPLIER_GSTIN IN :gstinList ");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildVertical.append(" AND PROFIT_CENTRE IN :pcList ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildVertical.append(" AND PLANT_CODE IN :plantList ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildVertical.append(" AND SALES_ORGANIZATION IN :salesList ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				buildVertical
						.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildVertical.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildVertical.append(" AND DIVISION IN :divisionList ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildVertical.append(" AND LOCATION IN :locationList ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildVertical.append(" AND USERACCESS1 IN :ud1List ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildVertical.append(" AND USERACCESS2 IN :ud2List ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildVertical.append(" AND USERACCESS3 IN :ud3List ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildVertical.append(" AND USERACCESS4 IN :ud4List ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildVertical.append(" AND USERACCESS5 IN :ud5List ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildVertical.append(" AND USERACCESS6 IN :ud6List ");
			}
		}
		if (taxPeriod != 0) {

			buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery2 = buildVertical.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Build Query For DOC ISSSUE Vertical ----->",
					buildQuery2);
		}

		String queryStr = createDocQueryString(buildQuery2);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DOC ISsue Executing Query  -->" + queryStr);
		}

		List<Gstr1SummaryDocSectionDto> retList = new ArrayList<>();
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
			retList = list.parallelStream().map(o -> convertDoc(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Fetching Transactional & Vertical Data ----->",
					retList);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("While Fetching B2CS Transational Data getting Error ",
					e);
			// throw new AppException("Unexpected error in query execution.",
			// e);
		}
		return retList;
	}

	private Gstr1SummaryDocSectionDto convertDoc(Object[] arr) {

		Gstr1SummaryDocSectionDto obj = new Gstr1SummaryDocSectionDto();
		obj.setSrNo((Integer) arr[0]);
		obj.setSeriesFrom((String) arr[1]);
		obj.setSeriesTo((String) arr[2]);
		obj.setTotal((Integer) arr[3]);
		obj.setDocCancelled((Integer) arr[4]);
		obj.setNetIssued((Integer) arr[5]);
		return obj;
	}

	private String createDocQueryString(String buildQuery2) {
		// TODO Auto-generated method stub

		String queryStr = "SELECT SERIAL_NUM,DOC_SERIES_FROM,DOC_SERIES_TO,"
				+ "IFNULL(SUM(TOT_NUM),0) AS TOT_NUM,"
				+ "IFNULL(SUM(CANCELED),0) AS CANCELED,"
				+ "IFNULL(SUM(NET_NUM),0) AS NET_ISSUED FROM "
				+ "GSTR1_PROCESSED_INV_SERIES "
				+ "WHERE IS_DELETE=FALSE AND SERIAL_NUM='2' AND " + buildQuery2
				+ "GROUP BY SERIAL_NUM,DOC_SERIES_FROM,DOC_SERIES_TO ";
		return queryStr;

	}

	/**
	 * Review Summary HSN POpup Screen Query
	 * 
	 * 
	 */

	public List<Gstr1HsnSummarySectionDto> hsnBasicSummarySection(
			Annexure1SummaryReqDto request) {
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();

		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(taxPeriodReq);
		List<Long> entityId = req.getEntityId();

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
		StringBuilder buildVertical = new StringBuilder();
		StringBuilder buildUser = new StringBuilder();

		StringBuilder buildSupplyType = new StringBuilder();
		StringBuilder buildDocType = new StringBuilder();

		String supplyType = loadOnStartSupplyType(entityId);
		String docType = loadOnStartDocType(entityId);

		if ("A".equalsIgnoreCase(docType)) {
			buildDocType.append(" AND DOC_TYPE IN ('INV','BOS','CR','DR')  ");
		} else if ("B".equalsIgnoreCase(docType)) {
			buildDocType.append(" AND DOC_TYPE IN ('INV','BOS')  ");
		} else if ("C".equalsIgnoreCase(docType)) {
			buildDocType.append(" AND DOC_TYPE IN ('INV','BOS','CR')  ");
		} else if ("D".equalsIgnoreCase(docType)) {
			buildDocType.append(" AND DOC_TYPE IN ('INV','BOS','DR')  ");
		} else if ("E".equalsIgnoreCase(docType)) {
			buildDocType.append(" AND DOC_TYPE IN ('CR','DR') ");
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
					.append(" OR ITM.SUPPLY_TYPE NOT IN ('NIL') ) AND ");
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
		String hdrSupplyTypeQuery = buildHdr1SupplyType.toString();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList ");
				buildVertical.append(" AND SUPPLIER_GSTIN IN :gstinList ");
				buildUser.append(" AND SUPPLIER_GSTIN IN :gstinList ");
			}
		}

		if (taxPeriod != 0) {
			build.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			buildUser.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		String buildQuery1 = build.toString().substring(4);
		String buildQuery2 = buildVertical.toString().substring(4);
		String buildQueeryUser = buildUser.toString().substring(4);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Build Query For HSN Transactional ----->",
					buildQuery2);
		}

		// getting onboarding answer
		String configAnswer = onBoardingAnswer.getConfigAnswer(entityId.get(0));

		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		
		Integer hsnTaxPeriod = 202501;
		if (config != null)
			hsnTaxPeriod = Integer.valueOf(config.getValue());
		Boolean hsnFlag = false ;
		if(taxPeriod > hsnTaxPeriod)
			hsnFlag = true;
		else 
			hsnFlag = false;
		
		List<Gstr1HsnSummarySectionDto> secDtos = new ArrayList<>();
		String queryStr = createHsnQueryString(buildQuery1, buildQuery2,
				buildQueeryUser, docTypeQuery, supplyTypeQuery,
				hdrSupplyTypeQuery, rateIncludedInHsn, hsnFlag);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("HSN  Executing Query  -->" + queryStr);
		}

		List<Gstr1HsnSummarySectionDto> retList = new ArrayList<>();
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

			q.setParameter("configAnswer", configAnswer);

			List<Object[]> list = q.getResultList();

			Map<String, String> hsnMap = new HashMap<String, String>();
			Map<String, String> hsnUserMap = new HashMap<String, String>();

			if (CollectionUtils.isNotEmpty(list)) {
				List<Object[]> hsnOrSacMasterEntities = hsnOrSacRepository
						.findHsnDesc();
				hsnOrSacMasterEntities.forEach(entity -> {
					hsnMap.put(entity[0].toString(), entity[1].toString());
				});
			}
			secDtos = convertHsn(list, hsnMap, rateIncludedInHsn,hsnFlag);
		} catch (Exception e) {
			LOGGER.error("While Fetching HSN Transational Data getting Error ",
					e);
			// throw new AppException("Unexpected error in query execution.",
			// e);
		}
		return secDtos;
	}

	private List<Gstr1HsnSummarySectionDto> convertHsn(List<Object[]> array,
			Map<String, String> hsnMap, Boolean rateIncludedInHsn, Boolean hsnFlag) {

		List<Gstr1HsnSummarySectionDto> list = new ArrayList<>();

		for (Object[] arr : array) {

			Gstr1HsnSummarySectionDto obj = new Gstr1HsnSummarySectionDto();
			String docKey = (String) arr[0];
			obj.setDocKey(docKey);
			obj.setSgstn((String) arr[1]);
			obj.setTaxPeriod((String) arr[2]);
			obj.setHsn((String) arr[4]);
			String hsnCode = (String) arr[4];
			if (!Strings.isNullOrEmpty(hsnCode)) {
				String hsnDesc = hsnMap.get(hsnCode);
				if (hsnDesc != null) {
					if (hsnDesc.length() > 30) {
						obj.setAspDesc(hsnDesc.substring(0, 30));
					} else {
						obj.setAspDesc(hsnDesc);
					}
				}
			}

			String docKeyUi = userHsnRepo.getHsnDescData(docKey);
			if (docKeyUi != null) {
				obj.setUiDesc(docKeyUi);
			}

			obj.setUqc((String) arr[5]);
			obj.setAspQunty((BigDecimal) arr[6]);
			obj.setAspTotalValue((BigDecimal) arr[7]);
			obj.setAspTaxableValue((BigDecimal) arr[8]);
			obj.setAspIgst((BigDecimal) arr[9]);
			obj.setAspCgst((BigDecimal) arr[10]);
			obj.setAspSgst((BigDecimal) arr[11]);
			obj.setAspCess((BigDecimal) arr[12]);
			obj.setUsrQunty((BigDecimal) arr[13]);
			obj.setUsrTotalValue((BigDecimal) arr[14]);
			obj.setUsrTaxableValue((BigDecimal) arr[15]);
			obj.setUsrIgst((BigDecimal) arr[16]);
			obj.setUsrSgst((BigDecimal) arr[18]);
			obj.setUsrCgst((BigDecimal) arr[17]);
			obj.setUsrCess((BigDecimal) arr[19]);
			if (rateIncludedInHsn) {
				if (arr[20] != null) {
					obj.setTaxRate((BigDecimal) arr[20]);
				} else {
					obj.setTaxRate(new BigDecimal("0.0"));
				}
				if (hsnFlag) {
					if (arr[21] != null) {
						if (arr[21].toString().equalsIgnoreCase("HSN_B2B")) {
							obj.setRecordType("B2B");
						} else if (arr[21].toString()
								.equalsIgnoreCase("HSN_B2C")) {
							obj.setRecordType("B2C");
						} else {
							obj.setRecordType((String) arr[21]);
						}
					} else {
						obj.setRecordType("");
					}
				}

			}

			list.add(obj);

		}
		return list;
	}

	private String createHsnQueryString(String buildQuery1,
			String buildQuery2, String buildQueeryUser, String docTypeQuery,
			String supplyTypeQuery, String hdrSupplyTypeQuery,
			Boolean rateIncludedInHsn, Boolean hsnFlag) {
		StringBuilder builder = new StringBuilder();
		if (rateIncludedInHsn && hsnFlag) {
			System.out.println("If---------");
			builder.append("SELECT DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append("SUM(ASP_ITM_QTY)ASP_ITM_QTY,");
			builder.append(
					"SUM(ASP_TOTAL_VALUE)ASP_TOTAL_VALUE,SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE,");
			builder.append("SUM(ASP_IGST)ASP_IGST,SUM(ASP_CGST)ASP_CGST,");
			builder.append("SUM(ASP_SGST)ASP_SGST,SUM(ASP_CESS)ASP_CESS,");
			builder.append("SUM(UI_ITM_QTY)UI_ITM_QTY,");
			builder.append("SUM(UI_TOTAL_VALUE)UI_TOTAL_VALUE,");
			builder.append(
					"SUM(UI_TAXABLE_VALUE)UI_TAXABLE_VALUE,SUM(UI_IGST)UI_IGST,");
			builder.append(
					"SUM(UI_CGST)UI_CGST,SUM(UI_SGST)UI_SGST ,SUM(UI_CESS)UI_CESS,TAX_RATE,RECORDTYPE ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT  (CASE WHEN LEFT(ITM_HSNSAC,2) =99 and LENGTH(ITM_UQC)>2 then REPLACE(DOC_KEY,SUBSTR(DOC_KEY,24,3),'NA') else DOC_KEY end) as DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
			builder.append("ITM_HSNSAC,");
			builder.append(
					"(CASE WHEN LEFT(ITM_HSNSAC,2) =99 then 'NA' else ITM_UQC end) as ITM_UQC,");
			builder.append(
					"(CASE WHEN LEFT(ITM_HSNSAC,2) =99 then 0 else ASP_ITM_QTY end ) as ASP_ITM_QTY,");
			builder.append(
					"ASP_TOTAL_VALUE ,ASP_TAXABLE_VALUE, ASP_IGST,ASP_CGST,ASP_SGST,ASP_CESS,");
			builder.append(
					"0 UI_ITM_QTY,0 UI_TOTAL_VALUE, 0 UI_TAXABLE_VALUE, 0 UI_IGST,0 UI_CGST,0 UI_SGST ,0 UI_CESS,TAX_RATE,RECORDTYPE ");
			builder.append("FROM ( ");
			builder.append("SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append(
					"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,ASP_ITM_QTY,ASP_TOTAL_VALUE,");
			builder.append(
					"ASP_TAXABLE_VALUE, ASP_IGST,ASP_CGST,ASP_SGST,ASP_CESS,TAX_RATE,RECORDTYPE ");
			builder.append("FROM ");

			builder.append("(SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append(
					"SUM(IFNULL(ITM_QTY_A,0)- IFNULL(ITM_QTY_S,0)) AS  ASP_ITM_QTY,");
			builder.append(
					"SUM(IFNULL(TOTAL_VALUE_A,0)- IFNULL(TOTAL_VALUE_S,0)) AS  ASP_TOTAL_VALUE,");
			builder.append(
					"SUM(IFNULL(TAXABLE_VALUE_A,0)- IFNULL(TAXABLE_VALUE_S,0)) AS  ASP_TAXABLE_VALUE,");
			builder.append(
					"SUM(IFNULL(IGST_A,0)- IFNULL(IGST_S,0)) AS  ASP_IGST,");
			builder.append(
					"SUM(IFNULL(CGST_A,0)- IFNULL(CGST_S,0)) AS  ASP_CGST,");
			builder.append(
					"SUM(IFNULL(SGST_A,0)- IFNULL(SGST_S,0)) AS  ASP_SGST,");
			builder.append(
					"SUM(IFNULL(CESS_A,0)- IFNULL(CESS_S,0)) AS  ASP_CESS,TAX_RATE,RECORDTYPE ");
			builder.append("FROM ");

			builder.append("(  SELECT DOC_KEY,DERIVED_RET_PERIOD,");
			builder.append("SUPPLIER_GSTIN,RETURN_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN ITM_QTY END ITM_QTY_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN ITM_QTY END ITM_QTY_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TOTAL_VALUE END TOTAL_VALUE_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_VALUE END TOTAL_VALUE_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IGST_AMT END IGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN IGST_AMT END IGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CGST_AMT END CGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN CGST_AMT END CGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN SGST_AMT END SGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN SGST_AMT END SGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CESS_AMT END CESS_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN CESS_AMT END CESS_S ,TAX_RATE ,RECORDTYPE ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT B.DOC_KEY,B.DERIVED_RET_PERIOD,B.SUPPLIER_GSTIN,B.RETURN_PERIOD,B.SUPPLY_TYPE,B.DOC_TYPE, ");
			builder.append(
					"ITM_HSNSAC,ITM_UQC,ITM_QTY,ECP.QUESTION_CODE,ECP.ANSWER ");
			builder.append(
					",CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','14(ii)','15(i)','15(ii)','15(iii)','15(iv)') THEN IFNULL(ONB_LINE_ITEM_AMT,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN (ECP.QUESTION_CODE='O21' AND ECP.ANSWER IN ('B') AND SUPPLY_TYPE IN ('EXPT','EXPWT')) ");
			builder.append("THEN IFNULL(TAXABLE_VALUE,0) ");
			builder.append(
					"ELSE (IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT,0)+IFNULL(OTHER_VALUES,0)) ");
			builder.append("END AS TOTAL_VALUE,TAXABLE_VALUE,IGST_AMT, ");
			builder.append("CGST_AMT,SGST_AMT,CESS_AMT,TAX_RATE,RECORDTYPE  ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT ( HDR.SUPPLIER_GSTIN || '|' ||HDR.DERIVED_RET_PERIOD || '|' || CASE WHEN ITM.CUST_GSTIN IS NOT NULL"
					+ " AND LENGTH(ITM.CUST_GSTIN) = 15 THEN 'B2B' WHEN (LENGTH(ITM.CUST_GSTIN) < 15 OR ITM.CUST_GSTIN IS NULL)"
					+ " THEN 'B2C' END || '|' ||IFNULL(ITM_HSNSAC, 'null') || '|' || IFNULL(ITM_UQC, 'OTH')|| '|' || IFNULL(TAX_RATE, 0)) AS DOC_KEY,");
			builder.append(
					"ITM.DERIVED_RET_PERIOD,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,");
			builder.append(
					"HDR.SUPPLY_TYPE,DOC_TYPE,ITM.ITM_HSNSAC,IFNULL(ITM.ITM_UQC,'OTH') AS ITM_UQC,HDR.TABLE_SECTION,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append(
					"OR (TABLE_SECTION IN ('9B') AND HDR.SUPPLY_TYPE NOT IN ('EXPT','EXPWT'))  ");
			builder.append("THEN IFNULL(ITM_QTY_NEW,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(ITM.ITM_QTY_NEW,0) ");
			builder.append(
					"WHEN TABLE_SECTION IN ('6A','9B') AND HDR.SUPPLY_TYPE IN ('EXPT','EXPWT') ");
			builder.append(
					"THEN IFNULL(ITM.ITM_QTY_NEW,0) END  AS ITM_QTY,IFNULL(ITM.ONB_LINE_ITEM_AMT,0) ONB_LINE_ITEM_AMT,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append("THEN IFNULL(ITM.TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(ITM.TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.TAXABLE_VALUE,0) END  AS TAXABLE_VALUE,");
			builder.append("CASE WHEN TABLE_SECTION IN ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')   ");
			builder.append("THEN IFNULL(ITM.IGST_AMT,0) ");
			builder.append("WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.IGST_AMT,0) END  AS IGST_AMT, ");
			builder.append("CASE WHEN TABLE_SECTION IN  ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')  ");
			builder.append(
					"THEN IFNULL(ITM.CGST_AMT,0) WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.CGST_AMT,0) END  AS CGST_AMT,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append(
					"THEN IFNULL(ITM.SGST_AMT,0) WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.SGST_AMT,0) END  AS SGST_AMT, ");
			builder.append("CASE WHEN TABLE_SECTION IN ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append(
					"THEN IFNULL(ITM.CESS_AMT_SPECIFIC,0)+ IFNULL(ITM.CESS_AMT_ADVALOREM,0)  ");
			builder.append("WHEN TABLE_SECTION ='4B' THEN '0'  ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.CESS_AMT_SPECIFIC,0)+ ");
			builder.append(
					"IFNULL(ITM.CESS_AMT_ADVALOREM,0) END  AS CESS_AMT,ITM.OTHER_VALUES,IFNULL(TAX_RATE,0) TAX_RATE,  ");
			
			builder.append(" CASE WHEN ITM.CUST_GSTIN IS NOT NULL AND LENGTH(ITM.CUST_GSTIN) = 15 THEN 'B2B' ");
			builder.append(" WHEN (LENGTH(ITM.CUST_GSTIN) < 15 OR ITM.CUST_GSTIN IS NULL)    THEN 'B2C' END AS RecordType ");
			
			builder.append(" FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ");
			builder.append(
					" (SELECT CASE WHEN 'B' =:configAnswer AND DOC_TYPE='CR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
			builder.append(
					"WHEN 'B' =:configAnswer AND DOC_TYPE='DR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
			builder.append("ELSE IFNULL(ITM_QTY,0) END AS ITM_QTY_NEW,ITM1.* ");
			builder.append("FROM ANX_OUTWARD_DOC_ITEM ITM1 ");
			builder.append("INNER JOIN ");
			builder.append(
					"ANX_OUTWARD_DOC_HEADER HDR1 ON HDR1.ID = ITM1.DOC_HEADER_ID) ITM ON HDR.ID = ITM.DOC_HEADER_ID  ");
			builder.append(
					"LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID ");
			builder.append(
					"LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON  ");
			builder.append(
					"GSTNBATCH.ID = HDR.BATCH_ID WHERE ASP_INVOICE_STATUS = 2  ");
			builder.append(
					"AND COMPLIANCE_APPLICABLE=TRUE AND HDR.IS_DELETE=FALSE  ");
			builder.append("AND HDR.RETURN_TYPE='GSTR1'  ");
			builder.append(
					" AND (ITM.ITM_TABLE_SECTION NOT IN('8','8A','8B','8C','8D')  ");
			builder.append(hdrSupplyTypeQuery);
			builder.append(buildQuery1);
			builder.append("UNION ALL  ");
			builder.append(
					"SELECT ( SUPPLIER_GSTIN|| '|' ||DERIVED_RET_PERIOD|| '|' ||'B2C'|| '|' ||IFNULL(NEW_HSNORSAC, 'null')|| '|' ||IFNULL(NEW_UOM, 'OTH') || '|' ||IFNULL(NEW_RATE, 0)) DOC_KEY,");
			builder.append(
					"DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,'TAX' SUPPLY_TYPE,");
			builder.append(
					"'INV' DOC_TYPE,NEW_HSNORSAC AS ITM_HSNSAC,IFNULL(NEW_UOM,'OTH') AS ITM_UQC,");
			builder.append("'7' TABLE_SECTION,IFNULL(NEW_QNT,0)  AS ITM_QTY,");
			builder.append("IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+ ");
			builder.append(
					"IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)  AS DOC_AMT,");
			builder.append("IFNULL(NEW_TAXABLE_VALUE,0) AS TAXABLE_VALUE,");
			builder.append(
					"IFNULL(IGST_AMT,0) AS IGST_AMT,IFNULL(CGST_AMT,0) AS CGST_AMT,");
			builder.append("IFNULL(SGST_AMT,0) AS SGST_AMT,");
			builder.append(
					"IFNULL(CESS_AMT,0) AS CESS_AMT,0.00 OTHER_VALUES,IFNULL(NEW_RATE,0) TAX_RATE,'B2C' AS RECORDTYPE  ");
			builder.append("FROM GSTR1_PROCESSED_B2CS ");
			builder.append(
					"WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE  AND ");

			builder.append(buildQuery2);
			builder.append("UNION ALL ");
			builder.append(
					"SELECT ( SUPPLIER_GSTIN || '|' ||DERIVED_RET_PERIOD || '|' ||RECORDTYPE|| '|' || IFNULL(ITM_HSNSAC, 'null')|| '|' || IFNULL(ITM_UQC, 'OTH') || '|' || 0.00) AS DOC_KEY, ");
			builder.append(
					"DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,SUPPLY_TYPE,");
			builder.append(
					"'INV' DOC_TYPE,ITM_HSNSAC,IFNULL(ITM_UQC,'OTH') AS ITM_UQC,'8' TABLE_SECTION,");
			builder.append(
					"IFNULL( SUM(CASE WHEN RN = 1 THEN ITM_QTY ELSE 0 END) , 0) AS ITM_QTY,");
			builder.append("SUM(TAXABLE_VALUE) AS DOC_AMT, ");
			builder.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,");

			builder.append(
					"0 AS IGST_AMT,0 CGST_AMT,0 AS SGST_AMT,0 AS CESS_AMT,0.00 OTHER_VALUES,0.00 TAX_RATE,RECORDTYPE   ");
			builder.append(" FROM ");

			builder.append(" ( SELECT *,  ROW_NUMBER() OVER ");
			builder.append(" ( PARTITION BY (DERIVED_RET_PERIOD || '|' || ");
			builder.append(
					" SUPPLIER_GSTIN || '|' || IFNULL(ITM_UQC, 'OTH') || '|' ");
			builder.append("|| 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null')  || '|' || ( CASE WHEN TABLE_SECTION "
					+ " IN ( '8A', '8B') THEN 'HSN_B2B' WHEN TABLE_SECTION IN ( '8C', '8D') THEN 'HSN_B2C' END) "
					+ "  ) ");
			builder.append(
					"ORDER BY (DERIVED_RET_PERIOD || '|' || SUPPLIER_GSTIN || '|' || ");
			builder.append(
					"IFNULL(ITM_UQC, 'OTH') || '|' || 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null')  || '|' || ( CASE WHEN TABLE_SECTION "
							+ " IN ( '8A', '8B') THEN 'HSN_B2B' WHEN TABLE_SECTION IN ( '8C', '8D') THEN 'HSN_B2C' END) "
							+ ") ) AS RN , ");
			builder.append(" CASE WHEN TABLE_SECTION IN ( '8A', '8B') THEN 'B2B' ");
			builder.append(" WHEN TABLE_SECTION IN ( '8C', '8D') THEN 'B2C' ");
			builder.append(" END AS RECORDTYPE ");
			builder.append("FROM GSTR1_SUMMARY_NILEXTNON ");
			builder.append("WHERE IS_DELETE = FALSE AND ");
			builder.append(buildQuery2);
			builder.append(supplyTypeQuery);
			builder.append(
					" ) GROUP BY ( DERIVED_RET_PERIOD || '|' || SUPPLIER_GSTIN || '|' || IFNULL(ITM_UQC, 'OTH') || '|' || 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null') ");
			builder.append(
					" ),DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD, ");
			builder.append("SUPPLY_TYPE,ITM_HSNSAC,IFNULL(ITM_UQC, 'OTH'),RECORDTYPE ");
			builder.append(" ) B ");
			builder.append(
					"INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN  ");
			builder.append("INNER JOIN ENTITY_CONFG_PRMTR ECP  ");
			builder.append(
					"ON ECP.ENTITY_ID= GSTN.ENTITY_ID  AND ECP.IS_ACTIVE=TRUE  ");
			builder.append(
					"AND ECP.IS_DELETE=FALSE AND ECP.QUESTION_CODE='O21' ");
			builder.append(docTypeQuery);
			builder.append(" )C)D ");
			builder.append("GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append(
					"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,TAX_RATE,RECORDTYPE ) WHERE ");
			builder.append(
					"( ITM_HSNSAC!='0' OR ITM_UQC!='0' OR ASP_TOTAL_VALUE !='0'  ");
			builder.append("OR ASP_TAXABLE_VALUE!='0' OR  ASP_IGST!='0' OR ");
			builder.append(
					"ASP_CGST!='0' OR ASP_SGST!='0' OR ASP_CESS!='0' )) ");

			builder.append("UNION ALL ");

			builder.append(
					"SELECT DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,0 ASP_ITM_QTY,");
			builder.append(
					"0 ASP_TOTAL_VALUE,0 ASP_TAXABLE_VALUE,0 ASP_IGST,0 ASP_CGST,0 ASP_SGST,0 ASP_CESS,");
			builder.append(
					"UI_ITM_QTY,UI_TOTAL_VALUE,UI_TAXABLE_VALUE,UI_IGST,UI_CGST,UI_SGST,UI_CESS,TAX_RATE,RECORDTYPE ");

			builder.append("FROM (  ");
			builder.append("SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append("SUM(UI_ITM_QTY) UI_ITM_QTY,");
			builder.append("SUM(UI_TOTAL_VALUE) UI_TOTAL_VALUE,");
			builder.append(
					"SUM(UI_TAXABLE_VALUE) UI_TAXABLE_VALUE,SUM(UI_IGST) UI_IGST,");
			builder.append("SUM(UI_CGST) UI_CGST,SUM(UI_SGST) UI_SGST,");
			builder.append("SUM(UI_CESS) UI_CESS,TAX_RATE,RECORDTYPE ");
			builder.append("FROM(  ");
			builder.append(
					"SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
			builder.append(
					"HSNSAC AS ITM_HSNSAC, UQC AS ITM_UQC,ITM_QTY AS UI_ITM_QTY,");
			builder.append(
					"TOTAL_VALUE as UI_TOTAL_VALUE,TAXABLE_VALUE as UI_TAXABLE_VALUE,");
			builder.append(
					"IGST_AMT as UI_IGST,CGST_AMT as UI_CGST,SGST_AMT as UI_SGST,");
			builder.append(
					"CESS_AMT as UI_CESS,TAX_RATE,RECORD_TYPE AS RECORDTYPE FROM GSTR1_USERINPUT_HSNSAC  ");
			builder.append("WHERE IS_DELETE=FALSE AND ");

			builder.append(buildQueeryUser);
			builder.append(") GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append(
					"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,TAX_RATE,RECORDTYPE )) ");

			builder.append("GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,TAX_RATE,RECORDTYPE ");
		} else if (rateIncludedInHsn){
			System.out.println("Else If---------");
			builder.append("SELECT DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append("SUM(ASP_ITM_QTY)ASP_ITM_QTY,");
			builder.append(
					"SUM(ASP_TOTAL_VALUE)ASP_TOTAL_VALUE,SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE,");
			builder.append("SUM(ASP_IGST)ASP_IGST,SUM(ASP_CGST)ASP_CGST,");
			builder.append("SUM(ASP_SGST)ASP_SGST,SUM(ASP_CESS)ASP_CESS,");
			builder.append("SUM(UI_ITM_QTY)UI_ITM_QTY,");
			builder.append("SUM(UI_TOTAL_VALUE)UI_TOTAL_VALUE,");
			builder.append(
					"SUM(UI_TAXABLE_VALUE)UI_TAXABLE_VALUE,SUM(UI_IGST)UI_IGST,");
			builder.append(
					"SUM(UI_CGST)UI_CGST,SUM(UI_SGST)UI_SGST ,SUM(UI_CESS)UI_CESS,TAX_RATE ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT  (CASE WHEN LEFT(ITM_HSNSAC,2) =99 and LENGTH(ITM_UQC)>2 then REPLACE(DOC_KEY,SUBSTR(DOC_KEY,24,3),'NA') else DOC_KEY end) as DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
			builder.append("ITM_HSNSAC,");
			builder.append(
					"(CASE WHEN LEFT(ITM_HSNSAC,2) =99 then 'NA' else ITM_UQC end) as ITM_UQC,");
			builder.append(
					"(CASE WHEN LEFT(ITM_HSNSAC,2) =99 then 0 else ASP_ITM_QTY end ) as ASP_ITM_QTY,");
			builder.append(
					"ASP_TOTAL_VALUE ,ASP_TAXABLE_VALUE, ASP_IGST,ASP_CGST,ASP_SGST,ASP_CESS,");
			builder.append(
					"0 UI_ITM_QTY,0 UI_TOTAL_VALUE, 0 UI_TAXABLE_VALUE, 0 UI_IGST,0 UI_CGST,0 UI_SGST ,0 UI_CESS,TAX_RATE ");
			builder.append("FROM ( ");
			builder.append("SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append(
					"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,ASP_ITM_QTY,ASP_TOTAL_VALUE,");
			builder.append(
					"ASP_TAXABLE_VALUE, ASP_IGST,ASP_CGST,ASP_SGST,ASP_CESS,TAX_RATE ");
			builder.append("FROM ");

			builder.append("(SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append(
					"SUM(IFNULL(ITM_QTY_A,0)- IFNULL(ITM_QTY_S,0)) AS  ASP_ITM_QTY,");
			builder.append(
					"SUM(IFNULL(TOTAL_VALUE_A,0)- IFNULL(TOTAL_VALUE_S,0)) AS  ASP_TOTAL_VALUE,");
			builder.append(
					"SUM(IFNULL(TAXABLE_VALUE_A,0)- IFNULL(TAXABLE_VALUE_S,0)) AS  ASP_TAXABLE_VALUE,");
			builder.append(
					"SUM(IFNULL(IGST_A,0)- IFNULL(IGST_S,0)) AS  ASP_IGST,");
			builder.append(
					"SUM(IFNULL(CGST_A,0)- IFNULL(CGST_S,0)) AS  ASP_CGST,");
			builder.append(
					"SUM(IFNULL(SGST_A,0)- IFNULL(SGST_S,0)) AS  ASP_SGST,");
			builder.append(
					"SUM(IFNULL(CESS_A,0)- IFNULL(CESS_S,0)) AS  ASP_CESS,TAX_RATE ");
			builder.append("FROM ");

			builder.append("(  SELECT DOC_KEY,DERIVED_RET_PERIOD,");
			builder.append("SUPPLIER_GSTIN,RETURN_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN ITM_QTY END ITM_QTY_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN ITM_QTY END ITM_QTY_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TOTAL_VALUE END TOTAL_VALUE_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_VALUE END TOTAL_VALUE_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IGST_AMT END IGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN IGST_AMT END IGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CGST_AMT END CGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN CGST_AMT END CGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN SGST_AMT END SGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN SGST_AMT END SGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CESS_AMT END CESS_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN CESS_AMT END CESS_S ,TAX_RATE ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT B.DOC_KEY,B.DERIVED_RET_PERIOD,B.SUPPLIER_GSTIN,B.RETURN_PERIOD,B.SUPPLY_TYPE,B.DOC_TYPE, ");
			builder.append(
					"ITM_HSNSAC,ITM_UQC,ITM_QTY,ECP.QUESTION_CODE,ECP.ANSWER ");
			builder.append(
					",CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','14(ii)','15(i)','15(ii)','15(iii)','15(iv)') THEN IFNULL(ONB_LINE_ITEM_AMT,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN (ECP.QUESTION_CODE='O21' AND ECP.ANSWER IN ('B') AND SUPPLY_TYPE IN ('EXPT','EXPWT')) ");
			builder.append("THEN IFNULL(TAXABLE_VALUE,0) ");
			builder.append(
					"ELSE (IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT,0)+IFNULL(OTHER_VALUES,0)) ");
			builder.append("END AS TOTAL_VALUE,TAXABLE_VALUE,IGST_AMT, ");
			builder.append("CGST_AMT,SGST_AMT,CESS_AMT,TAX_RATE  ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT (HDR.DERIVED_RET_PERIOD||'|'||HDR.SUPPLIER_GSTIN||'|'||IFNULL(ITM_UQC,'OTH')||'|'||IFNULL(TAX_RATE,0)||'|'||IFNULL(ITM_HSNSAC,'null')) DOC_KEY,");
			builder.append(
					"ITM.DERIVED_RET_PERIOD,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,");
			builder.append(
					"HDR.SUPPLY_TYPE,DOC_TYPE,ITM.ITM_HSNSAC,IFNULL(ITM.ITM_UQC,'OTH') AS ITM_UQC,HDR.TABLE_SECTION,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append(
					"OR (TABLE_SECTION IN ('9B') AND HDR.SUPPLY_TYPE NOT IN ('EXPT','EXPWT'))  ");
			builder.append("THEN IFNULL(ITM_QTY_NEW,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(ITM.ITM_QTY_NEW,0) ");
			builder.append(
					"WHEN TABLE_SECTION IN ('6A','9B') AND HDR.SUPPLY_TYPE IN ('EXPT','EXPWT') ");
			builder.append(
					"THEN IFNULL(ITM.ITM_QTY_NEW,0) END  AS ITM_QTY,IFNULL(ITM.ONB_LINE_ITEM_AMT,0) ONB_LINE_ITEM_AMT,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append("THEN IFNULL(ITM.TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(ITM.TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.TAXABLE_VALUE,0) END  AS TAXABLE_VALUE,");
			builder.append("CASE WHEN TABLE_SECTION IN ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')   ");
			builder.append("THEN IFNULL(ITM.IGST_AMT,0) ");
			builder.append("WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.IGST_AMT,0) END  AS IGST_AMT, ");
			builder.append("CASE WHEN TABLE_SECTION IN  ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')  ");
			builder.append(
					"THEN IFNULL(ITM.CGST_AMT,0) WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.CGST_AMT,0) END  AS CGST_AMT,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append(
					"THEN IFNULL(ITM.SGST_AMT,0) WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.SGST_AMT,0) END  AS SGST_AMT, ");
			builder.append("CASE WHEN TABLE_SECTION IN ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append(
					"THEN IFNULL(ITM.CESS_AMT_SPECIFIC,0)+ IFNULL(ITM.CESS_AMT_ADVALOREM,0)  ");
			builder.append("WHEN TABLE_SECTION ='4B' THEN '0'  ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.CESS_AMT_SPECIFIC,0)+ ");
			builder.append(
					"IFNULL(ITM.CESS_AMT_ADVALOREM,0) END  AS CESS_AMT,ITM.OTHER_VALUES,IFNULL(TAX_RATE,0) TAX_RATE  ");
			builder.append("FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ");
			builder.append(
					" (SELECT CASE WHEN 'B' =:configAnswer AND DOC_TYPE='CR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
			builder.append(
					"WHEN 'B' =:configAnswer AND DOC_TYPE='DR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
			builder.append("ELSE IFNULL(ITM_QTY,0) END AS ITM_QTY_NEW,ITM1.* ");
			builder.append("FROM ANX_OUTWARD_DOC_ITEM ITM1 ");
			builder.append("INNER JOIN ");
			builder.append(
					"ANX_OUTWARD_DOC_HEADER HDR1 ON HDR1.ID = ITM1.DOC_HEADER_ID) ITM ON HDR.ID = ITM.DOC_HEADER_ID  ");
			builder.append(
					"LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID ");
			builder.append(
					"LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON  ");
			builder.append(
					"GSTNBATCH.ID = HDR.BATCH_ID WHERE ASP_INVOICE_STATUS = 2  ");
			builder.append(
					"AND COMPLIANCE_APPLICABLE=TRUE AND HDR.IS_DELETE=FALSE  ");
			builder.append("AND HDR.RETURN_TYPE='GSTR1'  ");
			builder.append(
					" AND (ITM.ITM_TABLE_SECTION NOT IN('8','8A','8B','8C','8D')  ");
			builder.append(hdrSupplyTypeQuery);
			builder.append(buildQuery1);
			builder.append("UNION ALL  ");
			builder.append(
					"SELECT (DERIVED_RET_PERIOD  ||'|'||  SUPPLIER_GSTIN ||'|'|| IFNULL(NEW_UOM,'OTH') ||'|'|| IFNULL(NEW_RATE,0) ||'|'|| IFNULL(NEW_HSNORSAC,'null' )) DOC_KEY,");
			builder.append(
					"DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,'TAX' SUPPLY_TYPE,");
			builder.append(
					"'INV' DOC_TYPE,NEW_HSNORSAC AS ITM_HSNSAC,IFNULL(NEW_UOM,'OTH') AS ITM_UQC,");
			builder.append("'7' TABLE_SECTION,IFNULL(NEW_QNT,0)  AS ITM_QTY,");
			builder.append("IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+ ");
			builder.append(
					"IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)  AS DOC_AMT,");
			builder.append("IFNULL(NEW_TAXABLE_VALUE,0) AS TAXABLE_VALUE,");
			builder.append(
					"IFNULL(IGST_AMT,0) AS IGST_AMT,IFNULL(CGST_AMT,0) AS CGST_AMT,");
			builder.append("IFNULL(SGST_AMT,0) AS SGST_AMT,");
			builder.append(
					"IFNULL(CESS_AMT,0) AS CESS_AMT,0.00 OTHER_VALUES,IFNULL(NEW_RATE,0) TAX_RATE  ");
			builder.append("FROM GSTR1_PROCESSED_B2CS ");
			builder.append(
					"WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE  AND ");

			builder.append(buildQuery2);
			builder.append("UNION ALL ");
			builder.append(
					"SELECT (DERIVED_RET_PERIOD  ||'|'||  SUPPLIER_GSTIN  ||'|'|| IFNULL(ITM_UQC,'OTH')   ||'|'||0.00||'|'|| IFNULL(ITM_HSNSAC,'null' )) DOC_KEY, ");
			builder.append(
					"DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,SUPPLY_TYPE,");
			builder.append(
					"'INV' DOC_TYPE,ITM_HSNSAC,IFNULL(ITM_UQC,'OTH') AS ITM_UQC,'8' TABLE_SECTION,");
			builder.append(
					"IFNULL( SUM(CASE WHEN RN = 1 THEN ITM_QTY ELSE 0 END) , 0) AS ITM_QTY,");
			builder.append("SUM(TAXABLE_VALUE) AS DOC_AMT, ");
			builder.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,");

			builder.append(
					"0 AS IGST_AMT,0 CGST_AMT,0 AS SGST_AMT,0 AS CESS_AMT,0.00 OTHER_VALUES,0.00 TAX_RATE   ");
			builder.append(" FROM ");

			builder.append(" ( SELECT *,  ROW_NUMBER() OVER ");
			builder.append(" ( PARTITION BY (DERIVED_RET_PERIOD || '|' || ");
			builder.append(
					" SUPPLIER_GSTIN || '|' || IFNULL(ITM_UQC, 'OTH') || '|' ");
			builder.append("|| 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null')) ");
			builder.append(
					"ORDER BY (DERIVED_RET_PERIOD || '|' || SUPPLIER_GSTIN || '|' || ");
			builder.append(
					"IFNULL(ITM_UQC, 'OTH') || '|' || 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null') ) ) AS RN ");
			builder.append("FROM GSTR1_SUMMARY_NILEXTNON ");
			builder.append("WHERE IS_DELETE = FALSE AND ");
			builder.append(buildQuery2);
			builder.append(supplyTypeQuery);
			builder.append(
					" ) GROUP BY ( DERIVED_RET_PERIOD || '|' || SUPPLIER_GSTIN || '|' || IFNULL(ITM_UQC, 'OTH') || '|' || 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null') ");
			builder.append(
					" ),DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD, ");
			builder.append("SUPPLY_TYPE,ITM_HSNSAC,IFNULL(ITM_UQC, 'OTH') ");
			builder.append(" ) B ");
			builder.append(
					"INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN  ");
			builder.append("INNER JOIN ENTITY_CONFG_PRMTR ECP  ");
			builder.append(
					"ON ECP.ENTITY_ID= GSTN.ENTITY_ID  AND ECP.IS_ACTIVE=TRUE  ");
			builder.append(
					"AND ECP.IS_DELETE=FALSE AND ECP.QUESTION_CODE='O21' ");
			builder.append(docTypeQuery);
			builder.append(" )C)D ");
			builder.append("GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append(
					"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,TAX_RATE ) WHERE ");
			builder.append(
					"( ITM_HSNSAC!='0' OR ITM_UQC!='0' OR ASP_TOTAL_VALUE !='0'  ");
			builder.append("OR ASP_TAXABLE_VALUE!='0' OR  ASP_IGST!='0' OR ");
			builder.append(
					"ASP_CGST!='0' OR ASP_SGST!='0' OR ASP_CESS!='0' )) ");

			builder.append("UNION ALL ");

			builder.append(
					"SELECT DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,0 ASP_ITM_QTY,");
			builder.append(
					"0 ASP_TOTAL_VALUE,0 ASP_TAXABLE_VALUE,0 ASP_IGST,0 ASP_CGST,0 ASP_SGST,0 ASP_CESS,");
			builder.append(
					"UI_ITM_QTY,UI_TOTAL_VALUE,UI_TAXABLE_VALUE,UI_IGST,UI_CGST,UI_SGST,UI_CESS,TAX_RATE ");

			builder.append("FROM (  ");
			builder.append("SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append("SUM(UI_ITM_QTY) UI_ITM_QTY,");
			builder.append("SUM(UI_TOTAL_VALUE) UI_TOTAL_VALUE,");
			builder.append(
					"SUM(UI_TAXABLE_VALUE) UI_TAXABLE_VALUE,SUM(UI_IGST) UI_IGST,");
			builder.append("SUM(UI_CGST) UI_CGST,SUM(UI_SGST) UI_SGST,");
			builder.append("SUM(UI_CESS) UI_CESS,TAX_RATE ");
			builder.append("FROM(  ");
			builder.append(
					"SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
			builder.append(
					"HSNSAC AS ITM_HSNSAC, UQC AS ITM_UQC,ITM_QTY AS UI_ITM_QTY,");
			builder.append(
					"TOTAL_VALUE as UI_TOTAL_VALUE,TAXABLE_VALUE as UI_TAXABLE_VALUE,");
			builder.append(
					"IGST_AMT as UI_IGST,CGST_AMT as UI_CGST,SGST_AMT as UI_SGST,");
			builder.append(
					"CESS_AMT as UI_CESS,TAX_RATE FROM GSTR1_USERINPUT_HSNSAC  ");
			builder.append("WHERE IS_DELETE=FALSE AND ");

			builder.append(buildQueeryUser);
			builder.append(") GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append(
					"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,TAX_RATE )) ");

			builder.append("GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,TAX_RATE ");
		} else {

			builder.append("SELECT DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append("SUM(ASP_ITM_QTY)ASP_ITM_QTY,");
			builder.append(
					"SUM(ASP_TOTAL_VALUE)ASP_TOTAL_VALUE,SUM(ASP_TAXABLE_VALUE)ASP_TAXABLE_VALUE,");
			builder.append("SUM(ASP_IGST)ASP_IGST,SUM(ASP_CGST)ASP_CGST,");
			builder.append("SUM(ASP_SGST)ASP_SGST,SUM(ASP_CESS)ASP_CESS,");
			builder.append("SUM(UI_ITM_QTY)UI_ITM_QTY,");
			builder.append("SUM(UI_TOTAL_VALUE)UI_TOTAL_VALUE,");
			builder.append(
					"SUM(UI_TAXABLE_VALUE)UI_TAXABLE_VALUE,SUM(UI_IGST)UI_IGST,");
			builder.append(
					"SUM(UI_CGST)UI_CGST,SUM(UI_SGST)UI_SGST ,SUM(UI_CESS)UI_CESS ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
			builder.append(
					"ITM_HSNSAC,ITM_UQC, ASP_ITM_QTY,ASP_TOTAL_VALUE ,ASP_TAXABLE_VALUE, ASP_IGST,ASP_CGST,ASP_SGST,ASP_CESS,");
			builder.append(
					"0 UI_ITM_QTY,0 UI_TOTAL_VALUE, 0 UI_TAXABLE_VALUE, 0 UI_IGST,0 UI_CGST,0 UI_SGST ,0 UI_CESS ");
			builder.append("FROM ( ");
			builder.append("SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append(
					"DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,ASP_ITM_QTY,ASP_TOTAL_VALUE,");
			builder.append(
					"ASP_TAXABLE_VALUE, ASP_IGST,ASP_CGST,ASP_SGST,ASP_CESS ");
			builder.append("FROM ");

			builder.append("(SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append(
					"SUM(IFNULL(ITM_QTY_A,0)- IFNULL(ITM_QTY_S,0)) AS  ASP_ITM_QTY,");
			builder.append(
					"SUM(IFNULL(TOTAL_VALUE_A,0)- IFNULL(TOTAL_VALUE_S,0)) AS  ASP_TOTAL_VALUE,");
			builder.append(
					"SUM(IFNULL(TAXABLE_VALUE_A,0)- IFNULL(TAXABLE_VALUE_S,0)) AS  ASP_TAXABLE_VALUE,");
			builder.append(
					"SUM(IFNULL(IGST_A,0)- IFNULL(IGST_S,0)) AS  ASP_IGST,");
			builder.append(
					"SUM(IFNULL(CGST_A,0)- IFNULL(CGST_S,0)) AS  ASP_CGST,");
			builder.append(
					"SUM(IFNULL(SGST_A,0)- IFNULL(SGST_S,0)) AS  ASP_SGST,");
			builder.append(
					"SUM(IFNULL(CESS_A,0)- IFNULL(CESS_S,0)) AS  ASP_CESS ");
			builder.append("FROM ");

			builder.append("(  SELECT DOC_KEY,DERIVED_RET_PERIOD,");
			builder.append("SUPPLIER_GSTIN,RETURN_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN ITM_QTY END ITM_QTY_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN ITM_QTY END ITM_QTY_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TOTAL_VALUE END TOTAL_VALUE_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN TOTAL_VALUE END TOTAL_VALUE_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN TAXABLE_VALUE END TAXABLE_VALUE_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN TAXABLE_VALUE END TAXABLE_VALUE_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN IGST_AMT END IGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN IGST_AMT END IGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CGST_AMT END CGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN CGST_AMT END CGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN SGST_AMT END SGST_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN SGST_AMT END SGST_S,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('INV','BOS','DR') THEN CESS_AMT END CESS_A,");
			builder.append(
					"CASE WHEN DOC_TYPE IN('CR') THEN CESS_AMT END CESS_S  ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT B.DOC_KEY,B.DERIVED_RET_PERIOD,B.SUPPLIER_GSTIN,B.RETURN_PERIOD,B.SUPPLY_TYPE,B.DOC_TYPE, ");
			builder.append(
					"ITM_HSNSAC,ITM_UQC,ITM_QTY,ECP.QUESTION_CODE,ECP.ANSWER ");
			builder.append(
					",CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7','7A(1)','7B(1)','8','8A','8B','8C','8D','14(ii)','15(i)','15(ii)','15(iii)','15(iv)') THEN IFNULL(ONB_LINE_ITEM_AMT,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN (ECP.QUESTION_CODE='O21' AND ECP.ANSWER IN ('B') AND SUPPLY_TYPE IN ('EXPT','EXPWT')) ");
			builder.append("THEN IFNULL(TAXABLE_VALUE,0) ");
			builder.append(
					"ELSE (IFNULL(TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT,0)+IFNULL(OTHER_VALUES,0)) ");
			builder.append("END AS TOTAL_VALUE,TAXABLE_VALUE,IGST_AMT, ");
			builder.append("CGST_AMT,SGST_AMT,CESS_AMT  ");
			builder.append("FROM ( ");
			builder.append(
					"SELECT (HDR.DERIVED_RET_PERIOD||'|'||HDR.SUPPLIER_GSTIN||'|'||IFNULL(ITM_UQC,'OTH')||'|'||IFNULL(ITM_HSNSAC,'null')) DOC_KEY,");
			builder.append(
					"ITM.DERIVED_RET_PERIOD,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,");
			builder.append(
					"HDR.SUPPLY_TYPE,DOC_TYPE,ITM.ITM_HSNSAC,IFNULL(ITM.ITM_UQC,'OTH') AS ITM_UQC,HDR.TABLE_SECTION,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','14(ii)','15(i)','15(ii)','15(iii)','15(iv)') ");
			builder.append(
					"OR (TABLE_SECTION IN ('9B') AND HDR.SUPPLY_TYPE NOT IN ('EXPT','EXPWT'))  ");
			builder.append("THEN IFNULL(ITM_QTY_NEW,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(ITM.ITM_QTY_NEW,0) ");
			builder.append(
					"WHEN TABLE_SECTION IN ('6A','9B') AND HDR.SUPPLY_TYPE IN ('EXPT','EXPWT') ");
			builder.append(
					"THEN IFNULL(ITM.ITM_QTY_NEW,0) END  AS ITM_QTY,IFNULL(ITM.ONB_LINE_ITEM_AMT,0) ONB_LINE_ITEM_AMT,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append("THEN IFNULL(ITM.TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='4B' THEN IFNULL(ITM.TAXABLE_VALUE,0) ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.TAXABLE_VALUE,0) END  AS TAXABLE_VALUE,");
			builder.append("CASE WHEN TABLE_SECTION IN ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')   ");
			builder.append("THEN IFNULL(ITM.IGST_AMT,0) ");
			builder.append("WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.IGST_AMT,0) END  AS IGST_AMT, ");
			builder.append("CASE WHEN TABLE_SECTION IN  ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')  ");
			builder.append(
					"THEN IFNULL(ITM.CGST_AMT,0) WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.CGST_AMT,0) END  AS CGST_AMT,");
			builder.append(
					"CASE WHEN TABLE_SECTION IN ('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)')");
			builder.append(
					"THEN IFNULL(ITM.SGST_AMT,0) WHEN TABLE_SECTION ='4B' THEN '0' ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.SGST_AMT,0) END  AS SGST_AMT, ");
			builder.append("CASE WHEN TABLE_SECTION IN ");
			builder.append(
					"('4A','5A','6B','6C','7A(1)','7B(1)','8A','8B','8C','8D','9B','14(ii)','15(i)','15(ii)','15(iii)','15(iv)') ");
			builder.append(
					"THEN IFNULL(ITM.CESS_AMT_SPECIFIC,0)+ IFNULL(ITM.CESS_AMT_ADVALOREM,0)  ");
			builder.append("WHEN TABLE_SECTION ='4B' THEN '0'  ");
			builder.append(
					"WHEN TABLE_SECTION ='6A' THEN IFNULL(ITM.CESS_AMT_SPECIFIC,0)+ ");
			builder.append(
					"IFNULL(ITM.CESS_AMT_ADVALOREM,0) END  AS CESS_AMT,ITM.OTHER_VALUES ");
			builder.append("FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN ");
			builder.append(
					" (SELECT CASE WHEN 'B' =:configAnswer AND DOC_TYPE='CR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
			builder.append(
					"WHEN 'B' =:configAnswer AND DOC_TYPE='DR' AND IFNULL(CRDR_REASON,'NA')  <>'SR' THEN 0 ");
			builder.append("ELSE IFNULL(ITM_QTY,0) END AS ITM_QTY_NEW,ITM1.* ");
			builder.append("FROM ANX_OUTWARD_DOC_ITEM ITM1 ");
			builder.append("INNER JOIN ");
			builder.append(
					"ANX_OUTWARD_DOC_HEADER HDR1 ON HDR1.ID = ITM1.DOC_HEADER_ID) ITM ON HDR.ID = ITM.DOC_HEADER_ID  ");
			builder.append(
					"LEFT OUTER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID ");
			builder.append(
					"LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON  ");
			builder.append(
					"GSTNBATCH.ID = HDR.BATCH_ID WHERE ASP_INVOICE_STATUS = 2  ");
			builder.append(
					"AND COMPLIANCE_APPLICABLE=TRUE AND HDR.IS_DELETE=FALSE  ");
			builder.append("AND HDR.RETURN_TYPE='GSTR1'  ");
			builder.append(
					" AND (ITM.ITM_TABLE_SECTION NOT IN('8','8A','8B','8C','8D')  ");
			builder.append(hdrSupplyTypeQuery);
			builder.append(buildQuery1);
			builder.append("UNION ALL  ");
			builder.append(
					"SELECT (DERIVED_RET_PERIOD  ||'|'||  SUPPLIER_GSTIN  ");
			builder.append(
					"||'|'|| IFNULL(NEW_UOM,'OTH')   ||'|'|| IFNULL(NEW_HSNORSAC,'null' )) DOC_KEY,");
			builder.append(
					"DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,'TAX' SUPPLY_TYPE,");
			builder.append(
					"'INV' DOC_TYPE,NEW_HSNORSAC AS ITM_HSNSAC,IFNULL(NEW_UOM,'OTH') AS ITM_UQC,");
			builder.append("'7' TABLE_SECTION,IFNULL(NEW_QNT,0)  AS ITM_QTY,");
			builder.append("IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+ ");
			builder.append(
					"IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)  AS DOC_AMT,");
			builder.append("IFNULL(NEW_TAXABLE_VALUE,0) AS TAXABLE_VALUE,");
			builder.append(
					"IFNULL(IGST_AMT,0) AS IGST_AMT,IFNULL(CGST_AMT,0) AS CGST_AMT,");
			builder.append("IFNULL(SGST_AMT,0) AS SGST_AMT,");
			builder.append(
					"IFNULL(CESS_AMT,0) AS CESS_AMT,0.00 OTHER_VALUES  ");
			builder.append("FROM GSTR1_PROCESSED_B2CS ");
			builder.append(
					"WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE  AND ");

			builder.append(buildQuery2);
			builder.append("UNION ALL ");
			builder.append(
					"SELECT (DERIVED_RET_PERIOD  ||'|'||  SUPPLIER_GSTIN   ");
			builder.append(
					"||'|'|| IFNULL(ITM_UQC,'OTH')   ||'|'|| IFNULL(ITM_HSNSAC,'null' )) DOC_KEY, ");
			builder.append(
					"DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD,SUPPLY_TYPE,");
			builder.append(
					"'INV' DOC_TYPE,ITM_HSNSAC,IFNULL(ITM_UQC,'OTH') AS ITM_UQC,'8' TABLE_SECTION,");
			builder.append("IFNULL(ITM_QTY,0)  AS ITM_QTY,");
			builder.append("SUM(TAXABLE_VALUE) AS DOC_AMT, ");
			builder.append("SUM(TAXABLE_VALUE) AS TAXABLE_VALUE,");
			builder.append(
					"0 AS IGST_AMT,0 CGST_AMT,0 AS SGST_AMT,0 AS CESS_AMT,0.00 OTHER_VALUES  ");
			builder.append(" FROM ");

			builder.append(" ( SELECT *,  ROW_NUMBER() OVER ");
			builder.append(" ( PARTITION BY (DERIVED_RET_PERIOD || '|' || ");
			builder.append(
					" SUPPLIER_GSTIN || '|' || IFNULL(ITM_UQC, 'OTH') || '|' ");
			builder.append("|| 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null')) ");
			builder.append(
					"ORDER BY (DERIVED_RET_PERIOD || '|' || SUPPLIER_GSTIN || '|' || ");
			builder.append(
					"IFNULL(ITM_UQC, 'OTH') || '|' || 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null') ) ) AS RN ");
			builder.append("FROM GSTR1_SUMMARY_NILEXTNON ");
			builder.append("WHERE IS_DELETE = FALSE AND ");
			builder.append(buildQuery2);
			builder.append(supplyTypeQuery);

			builder.append(
					" ) GROUP BY ( DERIVED_RET_PERIOD || '|' || SUPPLIER_GSTIN || '|' || IFNULL(ITM_UQC, 'OTH') || '|' || 0.00 || '|' || IFNULL(ITM_HSNSAC, 'null') ");
			builder.append(
					" ),DERIVED_RET_PERIOD,SUPPLIER_GSTIN,RETURN_PERIOD, ");
			builder.append(
					"SUPPLY_TYPE,ITM_HSNSAC,IFNULL(ITM_UQC, 'OTH'),IFNULL(ITM_QTY, 0) ");
			builder.append(" ) B ");
			builder.append(
					"INNER JOIN GSTIN_INFO GSTN ON B.SUPPLIER_GSTIN = GSTN.GSTIN  ");
			builder.append("INNER JOIN ENTITY_CONFG_PRMTR ECP  ");
			builder.append(
					"ON ECP.ENTITY_ID= GSTN.ENTITY_ID  AND ECP.IS_ACTIVE=TRUE  ");
			builder.append(
					"AND ECP.IS_DELETE=FALSE AND ECP.QUESTION_CODE='O21' ");
			builder.append(docTypeQuery);
			builder.append(" )C)D ");
			builder.append("GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC) WHERE ");
			builder.append(
					"( ITM_HSNSAC!='0' OR ITM_UQC!='0' OR ASP_TOTAL_VALUE !='0'  ");
			builder.append("OR ASP_TAXABLE_VALUE!='0' OR  ASP_IGST!='0' OR ");
			builder.append(
					"ASP_CGST!='0' OR ASP_SGST!='0' OR ASP_CESS!='0' )) ");

			builder.append("UNION ALL ");

			builder.append(
					"SELECT DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,0 ASP_ITM_QTY,");
			builder.append(
					"0 ASP_TOTAL_VALUE,0 ASP_TAXABLE_VALUE,0 ASP_IGST,0 ASP_CGST,0 ASP_SGST,0 ASP_CESS,");
			builder.append(
					"UI_ITM_QTY,UI_TOTAL_VALUE,UI_TAXABLE_VALUE,UI_IGST,UI_CGST,UI_SGST,UI_CESS ");

			builder.append("FROM (  ");
			builder.append("SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC,");
			builder.append("SUM(UI_ITM_QTY) UI_ITM_QTY,");
			builder.append("SUM(UI_TOTAL_VALUE) UI_TOTAL_VALUE,");
			builder.append(
					"SUM(UI_TAXABLE_VALUE) UI_TAXABLE_VALUE,SUM(UI_IGST) UI_IGST,");
			builder.append("SUM(UI_CGST) UI_CGST,SUM(UI_SGST) UI_SGST,");
			builder.append("SUM(UI_CESS) UI_CESS ");
			builder.append("FROM(  ");
			builder.append(
					"SELECT  DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,");
			builder.append(
					"HSNSAC AS ITM_HSNSAC, UQC AS ITM_UQC,ITM_QTY AS UI_ITM_QTY,");
			builder.append(
					"TOTAL_VALUE as UI_TOTAL_VALUE,TAXABLE_VALUE as UI_TAXABLE_VALUE,");
			builder.append(
					"IGST_AMT as UI_IGST,CGST_AMT as UI_CGST,SGST_AMT as UI_SGST,");
			builder.append("CESS_AMT as UI_CESS FROM GSTR1_USERINPUT_HSNSAC  ");
			builder.append("WHERE IS_DELETE=FALSE AND ");
			builder.append(buildQueeryUser);
			builder.append(") GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC )) ");

			builder.append("GROUP BY DOC_KEY,SUPPLIER_GSTIN,RETURN_PERIOD,");
			builder.append("DERIVED_RET_PERIOD,ITM_HSNSAC,ITM_UQC ");
		}

		return builder.toString();

	}

	public String loadOnStartSupplyType(List<Long> entityId) {

		StringBuilder buildQuery = new StringBuilder();

		if (entityId.size() > 0) {

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
		if (list != null && !list.isEmpty())
			supplyType = (String) list.get(0);

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
}