package com.ey.advisory.app.data.daos.client.simplified;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr1VerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr1AspVerticalSectionDaoImpl")
public class Gstr1AspVerticalSectionDaoImpl
		implements Gstr1AspVerticalSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AspVerticalSectionDaoImpl.class);

	@Override
	public List<Ret1AspVerticalSummaryDto> lateBasicSummarySection(
			Annexure1SummaryReqDto request) {
		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String docType = req.getDocType();
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
		StringBuilder build = new StringBuilder();
		StringBuilder buildVertical = new StringBuilder();

		if (taxPeriod != 0) {

			build.append(" HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			buildVertical.append(" DERIVED_RET_PERIOD = :taxPeriod ");
		}

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList ");
				buildVertical.append(" AND SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE IN :pcList ");
				buildVertical.append(" AND PROFIT_CENTRE IN :pcList ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND HDR.PLANT_CODE IN :plantList ");
				buildVertical.append(" AND PLANT_CODE IN :plantList ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND HDR.SALES_ORGANIZATION IN :salesList ");
				buildVertical.append(" AND SALES_ORGANIZATION IN :salesList ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND HDR.PURCHASE_ORGANIZATION IN :purcList ");
				buildVertical
						.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList ");
				buildVertical.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN :divisionList ");
				buildVertical.append(" AND DIVISION IN :divisionList ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND HDR.LOCATION IN :locationList ");
				buildVertical.append(" AND LOCATION IN :locationList ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND HDR.USERACCESS1 IN :ud1List ");
				buildVertical.append(" AND USERACCESS1 IN :ud1List ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND HDR.USERACCESS2 IN :ud2List ");
				buildVertical.append(" AND USERACCESS2 IN :ud2List ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND HDR.USERACCESS3 IN :ud3List ");
				buildVertical.append(" AND USERACCESS3 IN :ud3List ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND HDR.USERACCESS4 IN :ud4List ");
				buildVertical.append(" AND USERACCESS4 IN :ud4List ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND HDR.USERACCESS5 IN :ud5List ");
				buildVertical.append(" AND USERACCESS5 IN :ud5List ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND HDR.USERACCESS6 IN :ud6List ");
				buildVertical.append(" AND USERACCESS6 IN :ud6List ");
			}
		}

		String buildQuery = build.toString();
		String buildQuery2 = buildVertical.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Build Query For B2CS Transactional ----->",
					buildQuery);
			LOGGER.debug(" Build Query For B2CS Transactional ----->",
					buildQuery2);
		}

		String queryStr = createQueryString(buildQuery, buildQuery2, docType,
				taxPeriod, gstinList);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("B2CS Executing Query  -->" + queryStr);
		}

		List<Ret1AspVerticalSummaryDto> retList = new ArrayList<>();
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
			
			retList.sort(Comparator.comparing(Ret1AspVerticalSummaryDto::getOrder));
			
			if(LOGGER.isDebugEnabled())
			{
			LOGGER.debug(" summaryDetails {} ",retList.toString());
			}
			
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

	private Ret1AspVerticalSummaryDto convert(Object[] arr) {

		Ret1AspVerticalSummaryDto obj = new Ret1AspVerticalSummaryDto();
		obj.setType((String) arr[0]);

		obj.setCount((GenUtil.getBigInteger(arr[1])).intValue());
		obj.setInvoiceValue((BigDecimal) arr[2]);
		obj.setTaxableValue((BigDecimal) arr[3]);
		obj.setIgst((BigDecimal) arr[4]);
		obj.setCgst((BigDecimal) arr[5]);
		obj.setSgst((BigDecimal) arr[6]);
		obj.setCess((BigDecimal) arr[7]);
		
		if(obj.getType().equalsIgnoreCase("TRANSACTIONAL"))
			obj.setOrder("A");
		else if (obj.getType().equalsIgnoreCase("TOTAL"))
			obj.setOrder("C");
		else
			obj.setOrder("B");
		
		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	
	public static void main(String[] args) {
		StringBuilder build = new StringBuilder();
		StringBuilder buildVertical = new StringBuilder();


			build.append(" HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			buildVertical.append(" DERIVED_RET_PERIOD = :taxPeriod ");
	
				build.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList ");
				buildVertical.append(" AND SUPPLIER_GSTIN IN :gstinList ");

				build.append(" AND HDR.PLANT_CODE IN :plantList ");
				buildVertical.append(" AND PLANT_CODE IN :plantList ");
		
		
				String buildQuery = build.toString();
		String buildQuery2 = buildVertical.toString();
		System.out.println(createQueryString(buildQuery, buildQuery2, "B2CSA",
				0, null));
	}
	private static String createQueryString(String buildQuery, String buildQuery2,
			String docType, int taxPeriod, List<String> gstinList) {
		// TODO Auto-generated method stub

		String queryStr = null;
		if ("B2CS".equalsIgnoreCase(docType)) {
			queryStr = "WITH TRANS AS ( "
					+ "  SELECT "
					+ "    ( "
					+ "      IFNULL(HDR.SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999)|| '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(ITM.TAX_RATE, 9999) "
					+ "    ) KEY, "
					+ "    IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN ONB_LINE_ITEM_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    )- IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('CR') THEN ONB_LINE_ITEM_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    ) AS DOC_AMT, "
					+ "    IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN ITM.TAXABLE_VALUE END "
					+ "      ), "
					+ "      0 "
					+ "    )- IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE END "
					+ "      ), "
					+ "      0 "
					+ "    ) AS TAXABLE_VALUE, "
					+ "    IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN ITM.IGST_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    ) - IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    ) AS IGST_AMT, "
					+ "    IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN ITM.CGST_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    ) - IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    ) AS CGST_AMT, "
					+ "    IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN ITM.SGST_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    ) - IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT END "
					+ "      ), "
					+ "      0 "
					+ "    ) AS SGST_AMT, "
					+ "    IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+ IFNULL(ITM.CESS_AMT_ADVALOREM, 0) END "
					+ "      ), "
					+ "      0 "
					+ "    )- IFNULL( "
					+ "      SUM( "
					+ "        CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+ IFNULL(ITM.CESS_AMT_ADVALOREM, 0) END "
					+ "      ), "
					+ "      0 "
					+ "    ) AS CESS_AMT "
					+ "  FROM "
					+ "    ANX_OUTWARD_DOC_HEADER HDR "
					+ "    INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ "  WHERE "
					+ buildQuery
					+ "    AND ASP_INVOICE_STATUS = 2 "
					+ "    AND COMPLIANCE_APPLICABLE = TRUE "
					+ "    AND HDR.IS_DELETE = FALSE "
					+ "    AND ITM.ITM_TAX_DOC_TYPE IN ('B2CS') "
					+ "    AND HDR.RETURN_TYPE = 'GSTR1' "
					+ "  GROUP BY "
					+ "    HDR.SUPPLIER_GSTIN, "
					+ "    HDR.RETURN_PERIOD, "
					+ "    HDR.DIFF_PERCENT, "
					+ "    HDR.POS, "
					+ "    HDR.ECOM_GSTIN, "
					+ "    ITM.TAX_RATE "
					+ "), "
					+ "VER AS ( "
					+ "  SELECT "
					+ "    ( "
					+ "      IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(RETURN_PERIOD, '') || '|' || IFNULL(TRAN_TYPE, '') || '|' || IFNULL(NEW_POS, 9999)|| '|' || IFNULL(NEW_ECOM_GSTIN, '')|| '|' || IFNULL(NEW_RATE, 9999) "
					+ "    ) KEY, "
					+ "    SUM( "
					+ "      IFNULL(NEW_TAXABLE_VALUE, 0)+ IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0) "
					+ "    ) AS DOC_AMT, "
					+ "    IFNULL( "
					+ "      SUM(NEW_TAXABLE_VALUE), "
					+ "      0 "
					+ "    ) AS TAXABLE_VALUE, "
					+ "    IFNULL( "
					+ "      SUM(IGST_AMT), "
					+ "      0 "
					+ "    ) AS IGST_AMT, "
					+ "    IFNULL( "
					+ "      SUM(CGST_AMT), "
					+ "      0 "
					+ "    ) AS CGST_AMT, "
					+ "    IFNULL( "
					+ "      SUM(SGST_AMT), "
					+ "      0 "
					+ "    ) AS SGST_AMT, "
					+ "    IFNULL( "
					+ "      SUM(CESS_AMT), "
					+ "      0 "
					+ "    ) AS CESS_AMT "
					+ "  FROM "
					+ "    GSTR1_PROCESSED_B2CS "
					+ "  WHERE "
					+ buildQuery2
					+ "    And IS_DELETE = FALSE "
					+ "    AND IS_AMENDMENT = FALSE "
					+ "  GROUP BY "
					+ "    SUPPLIER_GSTIN, "
					+ "    RETURN_PERIOD, "
					+ "    TRAN_TYPE, "
					+ "    NEW_POS, "
					+ "    NEW_ECOM_GSTIN, "
					+ "    NEW_RATE "
					+ ") "
					+ "SELECT "
					+ "  TYPE, "
					+ "  SUM(KEYCOUNT) COUNT, "
					+ "  SUM(DOC_AMT) DOC_AMT, "
					+ "  SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
					+ "  SUM(IGST_AMT) IGST_AMT, "
					+ "  SUM(CGST_AMT) CGST_AMT, "
					+ "  SUM(SGST_AMT) SGST_AMT, "
					+ "  SUM(CESS_AMT) CESS_AMT "
					+ "FROM "
					+ "  ( "
					+ "    SELECT "
					+ "      'TOTAL' AS TYPE, "
					+ "      COUNT(DISTINCT KEY) KEYCOUNT, "
					+ "      SUM(DOC_AMT) DOC_AMT, "
					+ "      SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
					+ "      SUM(IGST_AMT) IGST_AMT, "
					+ "      SUM(CGST_AMT) CGST_AMT, "
					+ "      SUM(SGST_AMT) SGST_AMT, "
					+ "      SUM(CESS_AMT) CESS_AMT "
					+ "    FROM "
					+ "      TRANS "
					+ "    UNION ALL "
					+ "    SELECT "
					+ "      'TOTAL' AS TYPE, "
					+ "      COUNT(DISTINCT KEY) KEYCOUNT, "
					+ "      SUM(DOC_AMT) DOC_AMT, "
					+ "      SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
					+ "      SUM(IGST_AMT) IGST_AMT, "
					+ "      SUM(CGST_AMT) CGST_AMT, "
					+ "      SUM(SGST_AMT) SGST_AMT, "
					+ "      SUM(CESS_AMT) CESS_AMT "
					+ "    FROM "
					+ "      VER "
					+ "    UNION ALL "
					+ "    SELECT "
					+ "      'TRANSACTIONAL' AS TYPE, "
					+ "      COUNT(DISTINCT KEY) KEYCOUNT, "
					+ "      SUM(DOC_AMT) DOC_AMT, "
					+ "      SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
					+ "      SUM(IGST_AMT) IGST_AMT, "
					+ "      SUM(CGST_AMT) CGST_AMT, "
					+ "      SUM(SGST_AMT) SGST_AMT, "
					+ "      SUM(CESS_AMT) CESS_AMT "
					+ "    FROM "
					+ "      TRANS "
					+ "    UNION ALL "
					+ "    SELECT "
					+ "      'VERTICAL' AS TYPE, "
					+ "      COUNT(DISTINCT KEY) KEYCOUNT, "
					+ "      SUM(DOC_AMT) DOC_AMT, "
					+ "      SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
					+ "      SUM(IGST_AMT) IGST_AMT, "
					+ "      SUM(CGST_AMT) CGST_AMT, "
					+ "      SUM(SGST_AMT) SGST_AMT, "
					+ "      SUM(CESS_AMT) CESS_AMT "
					+ "    FROM "
					+ "      VER "
					+ "  ) "
					+ "GROUP BY "
					+ "  TYPE";
			// backup-22-07-2024
			/*
			 * queryStr =
			 * "SELECT TYPE,SUM(KEYCOUNT) COUNT,SUM(DOC_AMT) DOC_AMT," +
			 * "SUM(TAXABLE_VALUE) TAXABLE_VALUE,SUM(IGST_AMT) IGST_AMT," +
			 * "SUM(CGST_AMT) CGST_AMT,SUM(SGST_AMT) SGST_AMT," +
			 * "SUM(CESS_AMT) CESS_AMT FROM ( " +
			 * "SELECT 'TOTAL' AS TYPE ,COUNT(DISTINCT KEY) KEYCOUNT," +
			 * "SUM(DOC_AMT) DOC_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE," +
			 * "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT," +
			 * "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM( " +
			 * "SELECT (IFNULL(HDR.SUPPLIER_GSTIN,'') ||'|'|| IFNULL(HDR.RETURN_PERIOD,'')  "
			 * +
			 * "||'|'||IFNULL(HDR.DIFF_PERCENT,'')  ||'|'||IFNULL(HDR.POS,9999)||'|'|| "
			 * +
			 * "IFNULL(HDR.ECOM_GSTIN,'') ||'|'||IFNULL(ITM.TAX_RATE,9999)) KEY,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ONB_LINE_ITEM_AMT END),0)-"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ONB_LINE_ITEM_AMT END),0) AS DOC_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.TAXABLE_VALUE END),0)-"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE END),0) AS TAXABLE_VALUE,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.IGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT END),0) AS IGST_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.CGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT END),0) AS CGST_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.SGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT END),0) AS SGST_AMT,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN " +
			 * "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0)-"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN " +
			 * "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0) AS CESS_AMT "
			 * + "FROM ANX_OUTWARD_DOC_HEADER HDR " +
			 * "INNER JOIN ANX_OUTWARD_DOC_ITEM ITM " +
			 * "ON HDR.ID=ITM.DOC_HEADER_ID " +
			 * "WHERE ASP_INVOICE_STATUS = 2 AND " +
			 * "COMPLIANCE_APPLICABLE=TRUE AND " +
			 * "HDR.IS_DELETE = FALSE AND HDR.TAX_DOC_TYPE IN ('B2CS')  AND HDR.RETURN_TYPE='GSTR1' AND "
			 * + buildQuery +
			 * "GROUP BY HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.DIFF_PERCENT,HDR.POS, HDR.ECOM_GSTIN ,ITM.TAX_RATE) "
			 * + "UNION ALL " +
			 * "SELECT 'TOTAL' AS TYPE ,COUNT(DISTINCT KEY) KEYCOUNT," +
			 * "SUM(DOC_AMT) DOC_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE," +
			 * "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT," +
			 * "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM ( " +
			 * "SELECT (IFNULL(SUPPLIER_GSTIN,'') ||'|'|| IFNULL(RETURN_PERIOD,'')  "
			 * +
			 * "||'|'||IFNULL(TRAN_TYPE,'')  ||'|'||IFNULL(NEW_POS,9999)||'|'|| "
			 * + "IFNULL(NEW_ECOM_GSTIN,'')||'|'||IFNULL(NEW_RATE,9999) ) KEY,"
			 * + "SUM(IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+" +
			 * "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS DOC_AMT,"
			 * + "IFNULL(SUM(NEW_TAXABLE_VALUE),0) AS TAXABLE_VALUE," +
			 * "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT," +
			 * "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT," +
			 * "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT," +
			 * "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT " +
			 * "FROM GSTR1_PROCESSED_B2CS " +
			 * "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND " +
			 * buildQuery2 +
			 * "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,NEW_POS,NEW_ECOM_GSTIN,NEW_RATE ) "
			 * + "UNION ALL " +
			 * "SELECT 'TRANSACTIONAL' AS TYPE ,COUNT(DISTINCT KEY) KEYCOUNT," +
			 * "SUM(DOC_AMT) DOC_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE," +
			 * "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT," +
			 * "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM ( " +
			 * "SELECT (IFNULL(HDR.SUPPLIER_GSTIN,'') ||'|'|| IFNULL(HDR.RETURN_PERIOD,'') "
			 * +
			 * "||'|'||IFNULL(HDR.DIFF_PERCENT,'')  ||'|'|| IFNULL(HDR.POS,9999)||'|'|| "
			 * +
			 * "IFNULL(HDR.ECOM_GSTIN,'')||'|'||IFNULL(ITM.TAX_RATE,9999) ) KEY,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ONB_LINE_ITEM_AMT END),0)-"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ONB_LINE_ITEM_AMT END),0) AS DOC_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.TAXABLE_VALUE END),0)-"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE END),0) AS TAXABLE_VALUE,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.IGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT END),0) AS IGST_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.CGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT END),0) AS CGST_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.SGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT END),0) AS SGST_AMT,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN " +
			 * "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0)-"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN " +
			 * "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0) AS CESS_AMT "
			 * + "FROM ANX_OUTWARD_DOC_HEADER HDR " +
			 * "INNER JOIN ANX_OUTWARD_DOC_ITEM ITM " +
			 * "ON HDR.ID=ITM.DOC_HEADER_ID " +
			 * "WHERE ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE AND "
			 * + "IS_DELETE = FALSE AND HDR.TAX_DOC_TYPE IN ('B2CS')  AND " +
			 * buildQuery +
			 * "GROUP BY HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.DIFF_PERCENT,"
			 * + "HDR.POS, HDR.ECOM_GSTIN,ITM.TAX_RATE ) " + "UNION ALL " +
			 * "SELECT 'VERTICAL' AS TYPE ,COUNT(DISTINCT KEY) KEYCOUNT," +
			 * "SUM(DOC_AMT) DOC_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE," +
			 * "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT," +
			 * "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM( " +
			 * "SELECT " +
			 * "(IFNULL(SUPPLIER_GSTIN,'') ||'|'|| IFNULL(RETURN_PERIOD,'') " +
			 * "||'|'||IFNULL(TRAN_TYPE,'')  ||'|'||IFNULL(NEW_POS,9999)||'|'||IFNULL(NEW_ECOM_GSTIN,'')"
			 * + "||'|'||IFNULL(NEW_RATE,9999) ) KEY," +
			 * "SUM(IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+" +
			 * "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS DOC_AMT,"
			 * + "IFNULL(SUM(NEW_TAXABLE_VALUE),0) AS TAXABLE_VALUE," +
			 * "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT," +
			 * "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT," +
			 * "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT," +
			 * "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT " +
			 * "FROM GSTR1_PROCESSED_B2CS " +
			 * "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND " +
			 * buildQuery2 + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,"
			 * + "NEW_POS,NEW_ECOM_GSTIN,NEW_RATE)) GROUP BY TYPE";
			 */
		} else if ("B2CSA".equalsIgnoreCase(docType)) {

			queryStr = "SELECT 'VERTICAL' AS TYPE ,COUNT(DISTINCT KEY) KEYCOUNT,"
					+ "SUM(DOC_AMT) DOC_AMT, SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
					+ "SUM(SGST_AMT) SGST_AMT,SUM(CESS_AMT) CESS_AMT FROM( "
					+ "SELECT "
					+ "(IFNULL(SUPPLIER_GSTIN,'') ||'|'|| IFNULL(RETURN_PERIOD,'') "
					+ "||'|'||IFNULL(TRAN_TYPE,'')  ||'|'||IFNULL(NEW_POS,9999)"
					+ "||'|'||IFNULL(NEW_ECOM_GSTIN,'')||'|'||IFNULL(MONTH,'') ) KEY,"
					+ "SUM(IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
					+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS DOC_AMT,"
					+ "IFNULL(SUM(NEW_TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT "
					+ "FROM GSTR1_PROCESSED_B2CS "
					+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=TRUE  AND "
					+ buildQuery2
					+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,"
					+ "NEW_POS,NEW_ECOM_GSTIN) ";
		}

		LOGGER.debug("B2cs Vertical & Transactional Query executed ");
		return queryStr;
	}

	/**
	 * For B2CS & B2CSA GSTNView Query IMplementation
	 */

	@Override
	public List<Ret1AspVerticalSummaryDto> gstnBasicSummarySection(
			Annexure1SummaryReqDto request) {

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodReq = req.getTaxPeriod();
		String docType = request.getDocType();
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
		StringBuilder build = new StringBuilder();
		StringBuilder buildVertical = new StringBuilder();

		if (taxPeriod != 0) {

			build.append(" B2CS.DERIVED_RET_PERIOD = :taxPeriod ");
			buildVertical.append(" DERIVED_RET_PERIOD = :taxPeriod ");
		}

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND B2CS.SUPPLIER_GSTIN IN :gstinList ");
				buildVertical.append(" AND SUPPLIER_GSTIN IN :gstinList ");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND B2CS.PROFIT_CENTRE IN :pcList ");
				buildVertical.append(" AND PROFIT_CENTRE IN :pcList ");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND B2CS.PLANT_CODE IN :plantList ");
				buildVertical.append(" AND PLANT_CODE IN :plantList ");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND B2CS.SALES_ORGANIZATION IN :salesList ");
				buildVertical.append(" AND SALES_ORGANIZATION IN :salesList ");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND B2CS.PURCHASE_ORGANIZATION IN :purcList ");
				buildVertical
						.append(" AND PURCHASE_ORGANIZATION IN :purcList ");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND B2CS.DISTRIBUTION_CHANNEL IN :distList ");
				buildVertical.append(" AND DISTRIBUTION_CHANNEL IN :distList ");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND B2CS.DIVISION IN :divisionList ");
				buildVertical.append(" AND DIVISION IN :divisionList ");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND B2CS.LOCATION IN :locationList ");
				buildVertical.append(" AND LOCATION IN :locationList ");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND B2CS.USERACCESS1 IN :ud1List ");
				buildVertical.append(" AND USERACCESS1 IN :ud1List ");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND B2CS.USERACCESS2 IN :ud2List ");
				buildVertical.append(" AND USERACCESS2 IN :ud2List ");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND B2CS.USERACCESS3 IN :ud3List ");
				buildVertical.append(" AND USERACCESS3 IN :ud3List ");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND B2CS.USERACCESS4 IN :ud4List ");
				buildVertical.append(" AND USERACCESS4 IN :ud4List ");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND B2CS.USERACCESS5 IN :ud5List ");
				buildVertical.append(" AND USERACCESS5 IN :ud5List ");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND B2CS.USERACCESS6 IN :ud6List ");
				buildVertical.append(" AND USERACCESS6 IN :ud6List ");
			}
		}
		/*
		 * if (taxPeriod != 0) {
		 * 
		 * build.append(" AND B2CS.DERIVED_RET_PERIOD = :taxPeriod ");
		 * buildVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod "); }
		 */

		String buildQuery = build.toString();
		String buildQuery2 = buildVertical.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Build Query For B2CS Transactional ----->",
					buildQuery);
			LOGGER.debug(" Build Query For B2CS Transactional ----->",
					buildQuery2);
		}

		String queryStr = createGstnQueryString(buildQuery, docType,
				buildQuery2);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("B2CS Executing Query  -->" + queryStr);
		}

		List<Ret1AspVerticalSummaryDto> retList = new ArrayList<>();
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
			LOGGER.debug("ResultList data Converting to Dto");
			retList = list.parallelStream().map(o -> convertGstn(o, docType))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("While Fetching B2CS Vertical Date getting Error ", e);
			// throw new AppException("Unexpected error in query execution.");
		}
		return retList;
	}

	private Ret1AspVerticalSummaryDto convertGstn(Object[] arr,
			String docType) {
		Ret1AspVerticalSummaryDto obj = new Ret1AspVerticalSummaryDto();
		if ("B2CS".equalsIgnoreCase(docType)) {
			// obj.setPos((String) arr[2]);

			String stateName = statecodeRepository
					.findStateNameByCode((String) arr[2]);
			obj.setPos(((String) arr[2] + " - " + stateName));

			obj.setTransType((String) arr[3]);
			obj.setRate((BigDecimal) arr[4]);
			obj.setTaxableValue((BigDecimal) arr[5]);
			obj.setIgst((BigDecimal) arr[6]);
			obj.setCgst((BigDecimal) arr[7]);
			obj.setSgst((BigDecimal) arr[8]);
			obj.setCess((BigDecimal) arr[9]);
		} else {
			// obj.setPos((String) arr[2]);

			String stateName = statecodeRepository
					.findStateNameByCode((String) arr[2]);
			obj.setPos(((String) arr[2] + " - " + stateName));

			obj.setTransType((String) arr[3]);
			// obj.setRate((BigDecimal) arr[4]);
			obj.setTaxableValue((BigDecimal) arr[5]);
			obj.setIgst((BigDecimal) arr[6]);
			obj.setCgst((BigDecimal) arr[7]);
			obj.setSgst((BigDecimal) arr[8]);
			obj.setCess((BigDecimal) arr[9]);

		}
		return obj;
	}

	private String createGstnQueryString(String buildQuery, String docType,
			String buildQuery2) {
		LOGGER.debug("B2CS Gstin  Query Execution BEGIN ");

		String queryString = null;
		if ("B2CS".equalsIgnoreCase(docType)) {

			queryString = "WITH HDR AS ( "
					+ "  SELECT "
					+ "    B2CS.ID, "
					+ "    B2CS.DERIVED_RET_PERIOD, "
					+ "    SUPPLIER_GSTIN, "
					+ "    B2CS.RETURN_PERIOD, "
					+ "    B2CS.POS, "
					+ "    DOC_TYPE, "
					+ "    ITM.DIFF_PERCENT, "
					+ "    ITM.TAX_RATE, "
					+ "    ITM.TAXABLE_VALUE, "
					+ "    ITM.IGST_AMT, "
					+ "    ITM.CGST_AMT, "
					+ "    ITM.SGST_AMT, "
					+ "    ITM.CESS_AMT_SPECIFIC, "
					+ "    ITM.CESS_AMT_ADVALOREM, "
					+ "    ITM.ECOM_GSTIN "
					+ "  FROM "
					+ "    ANX_OUTWARD_DOC_HEADER B2CS "
					+ "	INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON B2CS.ID = ITM.DOC_HEADER_ID "
					+ "      AND B2CS.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "  WHERE "
					+ buildQuery
					+ "    AND ITM.ITM_TAX_DOC_TYPE = 'B2CS' "
					+ "    AND RETURN_TYPE = 'GSTR1' "
					+ "    AND ASP_INVOICE_STATUS = 2 "
					+ "    AND COMPLIANCE_APPLICABLE = TRUE "
					+ "    AND IS_DELETE = FALSE "
					+ ") "
					+ "SELECT "
					+ "  SUPPLIER_GSTIN, "
					+ "  RETURN_PERIOD, "
					+ "  POS, "
					+ "  TRAN_TYPE, "
					+ "  RATE, "
					+ "  SUM(NEW_TAXABLE_VALUE) AS ACCESSABLE_AMT, "
					+ "  SUM(IGST_AMT) IGST_AMT, "
					+ "  SUM(CGST_AMT) CGST_AMT, "
					+ "  SUM(SGST_AMT) SGST_AMT, "
					+ "  SUM(CESS_AMT) CESS_AMT "
					+ "FROM "
					+ "  ( "
					+ "    SELECT "
					+ "      B2CS.SUPPLIER_GSTIN, "
					+ "      B2CS.RETURN_PERIOD, "
					+ "      NEW_POS AS POS, "
					+ "      TRAN_TYPE, "
					+ "      NEW_RATE AS RATE, "
					+ "      NEW_TAXABLE_VALUE, "
					+ "      IGST_AMT, "
					+ "      CGST_AMT, "
					+ "      SGST_AMT, "
					+ "      CESS_AMT "
					+ "    FROM "
					+ "      GSTR1_PROCESSED_B2CS B2CS "
					+ "    WHERE "
					+ buildQuery
					+ "      AND B2CS.IS_DELETE = FALSE "
					+ "      AND IS_AMENDMENT = FALSE "
					+ "    UNION ALL "
					+ "      SELECT "
					+ "      SUPPLIER_GSTIN, "
					+ "      RETURN_PERIOD, "
					+ "      POS, "
					+ "      DIFF_PERCENT AS TRAN_TYPE, "
					+ "      TAX_RATE AS RATE, "
					+ "      IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN TAXABLE_VALUE END "
					+ "        ), "
					+ "        0 "
					+ "      ) - IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('CR') THEN TAXABLE_VALUE END "
					+ "        ), "
					+ "        0 "
					+ "      ) AS TAXABLE_VALUE, "
					+ "      IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN IGST_AMT END "
					+ "        ), "
					+ "        0 "
					+ "      ) - IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('CR') THEN IGST_AMT END "
					+ "        ), "
					+ "        0 "
					+ "      ) AS IGST_AMT, "
					+ "      IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN CGST_AMT END "
					+ "        ), "
					+ "        0 "
					+ "      ) - IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('CR') THEN CGST_AMT END "
					+ "        ), "
					+ "        0 "
					+ "      ) AS CGST_AMT, "
					+ "      IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN SGST_AMT END "
					+ "        ), "
					+ "        0 "
					+ "      ) - IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('CR') THEN SGST_AMT END "
					+ "        ), "
					+ "        0 "
					+ "      ) AS SGST_AMT, "
					+ "      IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('INV', 'DR') THEN IFNULL(CESS_AMT_SPECIFIC, 0) + IFNULL(CESS_AMT_ADVALOREM, 0) END "
					+ "        ), "
					+ "        0 "
					+ "      ) - IFNULL( "
					+ "        SUM( "
					+ "          CASE WHEN DOC_TYPE IN ('CR') THEN IFNULL(CESS_AMT_SPECIFIC, 0) + IFNULL(CESS_AMT_ADVALOREM, 0) END "
					+ "        ), "
					+ "        0 "
					+ "      ) AS CESS_AMT "
					+ "    FROM "
					+ "      HDR B2CS "
					+ "    GROUP BY "
					+ "      SUPPLIER_GSTIN, "
					+ "      RETURN_PERIOD, "
					+ "      POS, "
					+ "      DIFF_PERCENT, "
					+ "      ECOM_GSTIN, "
					+ "      TAX_RATE "
					+ "  ) "
					+ "GROUP BY "
					+ "  SUPPLIER_GSTIN, "
					+ "  RETURN_PERIOD, "
					+ "  POS, "
					+ "  TRAN_TYPE, "
					+ "  RATE";

			// backup--22-7-2024
			/*
			 * queryString =
			 * "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,POS,TRAN_TYPE,RATE," +
			 * "SUM(NEW_TAXABLE_VALUE) AS ACCESSABLE_AMT," +
			 * "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT," +
			 * "SUM(SGST_AMT)SGST_AMT,SUM(CESS_AMT)CESS_AMT " +
			 * "FROM ( SELECT B2CS.SUPPLIER_GSTIN,B2CS.RETURN_PERIOD," +
			 * "NEW_POS AS POS,TRAN_TYPE,NEW_RATE AS RATE,NEW_TAXABLE_VALUE," +
			 * "IGST_AMT,CGST_AMT,SGST_AMT, CESS_AMT " +
			 * "FROM GSTR1_PROCESSED_B2CS B2CS " +
			 * "WHERE B2CS.IS_DELETE=FALSE AND IS_AMENDMENT=FALSE AND " +
			 * buildQuery + "UNION ALL " +
			 * "SELECT B2CS.SUPPLIER_GSTIN,B2CS.RETURN_PERIOD," +
			 * "B2CS.POS , ITM.DIFF_PERCENT AS TRAN_TYPE," +
			 * "ITM.TAX_RATE AS RATE," +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.TAXABLE_VALUE END),0)-"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE END),0) "
			 * + "AS TAXABLE_VALUE," +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.IGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT END),0) AS IGST_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.CGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT END),0) AS CGST_AMT,"
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN ITM.SGST_AMT END),0) - "
			 * +
			 * "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT END),0) AS SGST_AMT,"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR') THEN " +
			 * "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0)-"
			 * + "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') THEN " +
			 * "IFNULL(ITM.CESS_AMT_SPECIFIC,0)+IFNULL(ITM.CESS_AMT_ADVALOREM,0) END),0) "
			 * + "AS CESS_AMT " + "FROM ANX_OUTWARD_DOC_HEADER B2CS INNER JOIN "
			 * + "ANX_OUTWARD_DOC_ITEM ITM ON B2CS.ID = ITM.DOC_HEADER_ID " +
			 * "AND B2CS.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD " +
			 * "WHERE B2CS.RETURN_TYPE = 'GSTR1' " +
			 * "AND B2CS.ASP_INVOICE_STATUS = 2 AND " +
			 * "B2CS.COMPLIANCE_APPLICABLE=TRUE AND B2CS.IS_DELETE=FALSE " +
			 * "AND B2CS.TAX_DOC_TYPE='B2CS' AND " + buildQuery +
			 * "GROUP BY B2CS.SUPPLIER_GSTIN,B2CS.RETURN_PERIOD," +
			 * "B2CS.POS,ITM.DIFF_PERCENT,ITM.ECOM_GSTIN,ITM.TAX_RATE ) " +
			 * "GROUP BY  SUPPLIER_GSTIN, RETURN_PERIOD,POS," +
			 * "TRAN_TYPE,RATE";
			 */

		} else if ("B2CSA".equalsIgnoreCase(docType)) {

			queryString = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,"
					+ "NEW_POS AS POS,TRAN_TYPE,'' NEW_RATE,"
					+ "IFNULL(SUM(NEW_TAXABLE_VALUE ),0) ACCESSABLE_AMT,"
					+ "IFNULL(SUM(IGST_AMT ),0) IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT ),0) CESS_AMT " + "FROM "
					+ "GSTR1_PROCESSED_B2CS "
					+ "WHERE IS_DELETE=FALSE AND IS_AMENDMENT=TRUE AND "
					+ buildQuery2 + "GROUP BY "
					+ "SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,NEW_POS,"
					+ "MONTH,NEW_ECOM_GSTIN";

			/*
			 * queryString = "SELECT SUPPLIER_GSTIN,RETURN_PERIOD," +
			 * "NEW_POS AS POS,TRAN_TYPE," +
			 * "SUM(NEW_TAXABLE_VALUE) AS ACCESSABLE_AMT," +
			 * "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT," +
			 * "SUM(SGST_AMT)SGST_AMT,SUM(CESS_AMT)CESS_AMT " +
			 * "FROM GSTR1_PROCESSED_B2CS B2CS " +
			 * "WHERE IS_DELETE=FALSE AND IS_AMENDMENT=TRUE AND " + buildQuery2
			 * + "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE," +
			 * "NEW_POS,MONTH,NEW_ECOM_GSTIN";
			 */
		}
		return queryString;
	}

	@Override
	public List<Gstr1VerticalSummaryRespDto> verticalBasicSummarySection(
			Annexure1SummaryReqDto req) {
		String taxPeriodReq = req.getTaxPeriod();

		String docType = req.getDocType();
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

		String buildQuery = buildVertical.toString();
		LOGGER.debug("Prepared where Condition and apply in B2CS Query BEGIN");

		String queryStr = createVerticalQueryString(buildQuery, docType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For B2CS Sections is -->" + queryStr);
		}

		List<Gstr1VerticalSummaryRespDto> retList = new ArrayList<>();
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

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			retList = list.parallelStream().map(o -> convertVertical(o, req))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution getting the data ----->" + retList);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("while Executing b2cs Vertical Query getting error ",
					e);
		}
		return retList;
	}

	private Gstr1VerticalSummaryRespDto convertVertical(Object[] arr,
			Annexure1SummaryReqDto req) {

		statecodeRepository = StaticContextHolder.getBean(
				"StatecodeRepositoryMaster", StatecodeRepository.class);

		Gstr1VerticalSummaryRespDto obj = new Gstr1VerticalSummaryRespDto();
		if ("B2CS".equalsIgnoreCase(req.getDocType())) {
			obj.setId((GenUtil.getBigInteger(arr[0])).longValue());
			obj.setSgstn((String) arr[1]);
			obj.setTaxPeriod((String) arr[2]);
			obj.setTransType((String) arr[3]);
			obj.setNewPos((String) arr[4]);

			String stateName = statecodeRepository
					.findStateNameByCode((String) arr[4]);
			// obj.setNewPos(((String) arr[5] +"-"+stateName));
			if (stateName != null) {
				obj.setNewStateName(stateName);
			}
			obj.setNewHsnOrSac((String) arr[5]);
			obj.setNewUom((String) arr[6]);
			obj.setNewQunty((BigDecimal) arr[7]);
			obj.setNewRate((BigDecimal) arr[8]);
			obj.setNewTaxableValue((BigDecimal) arr[9]);
			obj.setNewEcomGstin((String) arr[10]);
			obj.setNewEcomSupplValue((BigDecimal) arr[11]);
			obj.setIgst((BigDecimal) arr[12]);
			obj.setCgst((BigDecimal) arr[13]);
			obj.setSgst((BigDecimal) arr[14]);
			obj.setCess((BigDecimal) arr[15]);
			obj.setTotalValue((BigDecimal) arr[16]);
			obj.setProfitCntr((String) arr[17]);
			obj.setPlant((String) arr[18]);
			obj.setDivision((String) arr[19]);
			obj.setLocation((String) arr[20]);
			obj.setSalesOrg((String) arr[21]);
			obj.setDistrChannel((String) arr[22]);
			obj.setUsrAccess1((String) arr[23]);
			obj.setUsrAccess2((String) arr[24]);
			obj.setUsrAccess3((String) arr[25]);
			obj.setUsrAccess4((String) arr[26]);
			obj.setUsrAccess5((String) arr[27]);
			obj.setUsrAccess6((String) arr[28]);
			obj.setUsrDefined1((String) arr[29]);
			obj.setUsrDefined2((String) arr[30]);
			obj.setUsrDefined3((String) arr[31]);
		} else if ("B2CSA".equalsIgnoreCase(req.getDocType())) {

			obj.setId((GenUtil.getBigInteger(arr[0])).longValue());
			obj.setSgstn((String) arr[1]);
			obj.setTaxPeriod((String) arr[2]);
			obj.setTransType((String) arr[3]);
			obj.setMonth((String) arr[4]);
			obj.setOrgPos((String) arr[5]);

			String stateName = statecodeRepository
					.findStateNameByCode((String) arr[5]);
			if (stateName != null) {
				obj.setOrgStateName(stateName);
			}
			obj.setOrgHsnOrSac((String) arr[6]);
			obj.setOrgUom((String) arr[7]);
			obj.setOrgQunty((BigDecimal) arr[8]);
			obj.setOrgRate((BigDecimal) arr[9]);
			obj.setOrgTaxableValue((BigDecimal) arr[10]);
			obj.setOrgEcomGstin((String) arr[11]);
			obj.setOrgEcomSupplValue((BigDecimal) arr[12]);

			String stateNameNew = statecodeRepository
					.findStateNameByCode((String) arr[13]);

			obj.setNewPos((String) arr[13]);
			if (stateNameNew != null) {
				obj.setNewStateName(stateNameNew);
			}

			obj.setNewHsnOrSac((String) arr[14]);
			obj.setNewUom((String) arr[15]);
			obj.setNewQunty((BigDecimal) arr[16]);
			obj.setNewRate((BigDecimal) arr[17]);
			obj.setNewTaxableValue((BigDecimal) arr[18]);
			obj.setNewEcomGstin((String) arr[19]);
			obj.setNewEcomSupplValue((BigDecimal) arr[20]);
			obj.setIgst((BigDecimal) arr[21]);
			obj.setCgst((BigDecimal) arr[22]);
			obj.setSgst((BigDecimal) arr[23]);
			obj.setCess((BigDecimal) arr[24]);
			obj.setTotalValue((BigDecimal) arr[25]);
			obj.setProfitCntr((String) arr[26]);
			obj.setPlant((String) arr[27]);
			obj.setDivision((String) arr[28]);
			obj.setLocation((String) arr[29]);
			obj.setSalesOrg((String) arr[30]);
			obj.setDistrChannel((String) arr[31]);
			obj.setUsrAccess1((String) arr[32]);
			obj.setUsrAccess2((String) arr[33]);
			obj.setUsrAccess3((String) arr[34]);
			obj.setUsrAccess4((String) arr[35]);
			obj.setUsrAccess5((String) arr[36]);
			obj.setUsrAccess6((String) arr[37]);
			obj.setUsrDefined1((String) arr[38]);
			obj.setUsrDefined2((String) arr[39]);
			obj.setUsrDefined3((String) arr[40]);
		}

		return obj;
	}

	private String createVerticalQueryString(String buildQuery1,
			String docType) {
		LOGGER.debug("Vertical B2cs  Query Execution BEGIN ");

		String queryString = null;
		if ("B2CS".equalsIgnoreCase(docType)) {
			queryString = "SELECT ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,NEW_POS,NEW_HSNORSAC,"
					+ "NEW_UOM,NEW_QNT,NEW_RATE,"
					+ "SUM(NEW_TAXABLE_VALUE)NEW_TAXABLE_VALUE,NEW_ECOM_GSTIN,"
					+ "SUM(NEW_ECOM_SUP_VAL)NEW_ECOM_SUP_VAL,SUM(IGST_AMT)IGST_AMT,"
					+ "SUM(CGST_AMT)CGST_AMT,SUM(SGST_AMT)SGST_AMT,SUM(CESS_AMT)CESS_AMT,"
					+ "SUM(TOT_VALUE)TOT_VALUE,PROFIT_CENTRE,PLANT_CODE,DIVISION,"
					+ "LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
					+ "USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3 "
					+ "FROM (SELECT ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,NEW_POS,NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,"
					+ "IFNULL(SUM(NEW_TAXABLE_VALUE),0) AS NEW_TAXABLE_VALUE,"
					+ "NEW_ECOM_GSTIN,IFNULL(SUM(NEW_ECOM_SUP_VAL),0) AS NEW_ECOM_SUP_VAL,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,IFNULL(SUM(CESS_AMT),0) AS CESS_AMT,"
					+ "SUM(IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
					+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOT_VALUE,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,USERACCESS3,"
					+ "USERACCESS4,USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
					+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3 "
					+ "FROM GSTR1_PROCESSED_B2CS "
					+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=FALSE AND "
					+ buildQuery1
					+ "GROUP BY ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,NEW_POS,NEW_HSNORSAC,NEW_UOM,"
					+ "NEW_QNT,NEW_RATE,NEW_ECOM_GSTIN,PROFIT_CENTRE,PLANT_CODE,"
					+ "DIVISION,LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
					+ "USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
					+ "USERDEFINED_FIELD3 ) GROUP BY ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,NEW_POS,"
					+ "NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,NEW_ECOM_GSTIN,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,USERACCESS3,"
					+ "USERACCESS4,USERACCESS5,USERACCESS6,USERDEFINED_FIELD1,"
					+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3";
		} else if ("B2CSA".equalsIgnoreCase(docType)) {
			queryString = "SELECT ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,ORG_POS,ORG_HSNORSAC,"
					+ "ORG_UOM,ORG_QNT,ORG_RATE,"
					+ "SUM(ORG_TAXABLE_VALUE)ORG_TAXABLE_VALUE,ORG_ECOM_GSTIN,"
					+ "SUM(ORG_ECOM_SUP_VAL)ORG_ECOM_SUP_VAL,"
					+ "NEW_POS,NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,"
					+ "SUM(NEW_TAXABLE_VALUE)NEW_TAXABLE_VALUE,"
					+ "NEW_ECOM_GSTIN,SUM(NEW_ECOM_SUP_VAL)NEW_ECOM_SUP_VAL,"
					+ "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,"
					+ "SUM(SGST_AMT)SGST_AMT,SUM(CESS_AMT)CESS_AMT,"
					+ "SUM(TOT_VALUE)TOT_VALUE,PROFIT_CENTRE,PLANT_CODE,"
					+ "DIVISION,LOCATION,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,"
					+ "USERACCESS1,USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
					+ "USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
					+ "USERDEFINED_FIELD3 "
					+ "FROM ( SELECT ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,ORG_POS,ORG_HSNORSAC,"
					+ "ORG_UOM,ORG_QNT,ORG_RATE,"
					+ "IFNULL(SUM(ORG_TAXABLE_VALUE),0) AS ORG_TAXABLE_VALUE,"
					+ "ORG_ECOM_GSTIN,"
					+ "IFNULL(SUM(ORG_ECOM_SUP_VAL),0) AS ORG_ECOM_SUP_VAL,"
					+ "NEW_POS,NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,"
					+ "IFNULL(SUM(NEW_TAXABLE_VALUE),0) AS NEW_TAXABLE_VALUE,"
					+ "NEW_ECOM_GSTIN,"
					+ "IFNULL(SUM(NEW_ECOM_SUP_VAL),0) AS NEW_ECOM_SUP_VAL,"
					+ "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT,"
					+ "IFNULL(SUM(CESS_AMT),0) AS CESS_AMT,"
					+ "SUM(IFNULL(NEW_TAXABLE_VALUE,0)+IFNULL(IGST_AMT,0)+"
					+ "IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOT_VALUE,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
					+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
					+ "USERDEFINED_FIELD1,USERDEFINED_FIELD2,USERDEFINED_FIELD3 "
					+ "FROM GSTR1_PROCESSED_B2CS "
					+ "WHERE IS_DELETE = FALSE AND IS_AMENDMENT=TRUE AND "
					+ buildQuery1
					+ "GROUP BY ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,ORG_POS,ORG_HSNORSAC,"
					+ "ORG_UOM,ORG_QNT,ORG_RATE,ORG_ECOM_GSTIN,NEW_POS,"
					+ "NEW_HSNORSAC,NEW_UOM,NEW_QNT,NEW_RATE,NEW_ECOM_GSTIN,"
					+ "PROFIT_CENTRE,PLANT_CODE,DIVISION,LOCATION,"
					+ "SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,USERACCESS1,"
					+ "USERACCESS2,USERACCESS3,USERACCESS4,USERACCESS5,"
					+ "USERACCESS6,USERDEFINED_FIELD1,USERDEFINED_FIELD2,"
					+ "USERDEFINED_FIELD3 ) "
					+ "GROUP BY ID,SUPPLIER_GSTIN,RETURN_PERIOD,TRAN_TYPE,MONTH,ORG_POS,ORG_HSNORSAC,ORG_UOM,"
					+ "ORG_QNT,ORG_RATE,ORG_ECOM_GSTIN,NEW_POS,NEW_HSNORSAC,"
					+ "NEW_UOM,NEW_QNT,NEW_RATE,NEW_ECOM_GSTIN,PROFIT_CENTRE,"
					+ "PLANT_CODE,DIVISION,LOCATION,SALES_ORGANIZATION,"
					+ "DISTRIBUTION_CHANNEL,USERACCESS1,USERACCESS2,"
					+ "USERACCESS3,USERACCESS4,USERACCESS5,USERACCESS6,"
					+ "USERDEFINED_FIELD1,"
					+ "USERDEFINED_FIELD2,USERDEFINED_FIELD3";
		}
		return queryString;
	}

}
