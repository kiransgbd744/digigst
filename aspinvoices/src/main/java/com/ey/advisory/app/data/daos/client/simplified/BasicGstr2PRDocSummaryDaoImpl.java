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
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("BasicGstr2PRDocSummaryDaoImpl")
public class BasicGstr2PRDocSummaryDaoImpl
		implements BasicGstr2PRDocSummaryDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicGstr2PRDocSummaryDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@SuppressWarnings("unchecked")
	@Override
	public List<Gstr2PRSummarySectionDto> loadBasicSummarySection(
			Gstr2ProcessedRecordsReqDto req) {

		Gstr2ProcessedRecordsReqDto request = basicCommonSecParam
				.setInwardGstr2PRSumDataSecuritySearchParams(req);

		// List<String> gstin = req.getGstin();
		LocalDate docRecvTo = null;
		LocalDate docRecvFrom = null;
		if(request.getDocRecvTo() != null && request.getDocRecvFrom() != null){
		 docRecvTo = request.getDocRecvTo();
		 docRecvFrom = request.getDocRecvFrom();
		}
		
		String taxPeriodTo = request.getTaxPeriodTo();
		String taxPeriodFrom = request.getTaxPeriodFrom();
		int derTaxPeriodTo = 0;
		int derTaxPeriodFrom = 0;
		if(request.getTaxPeriodTo() != null && request.getTaxPeriodFrom() != null){
		
		 derTaxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
		
		 derTaxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
		}

		
		List<String> docCategory = request.getDocCategory();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

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

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" CUST_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List");
			}
		}
		if (docRecvFrom != null && docRecvTo != null) {
			build.append(" AND DOC_DATE BETWEEN :docRecvFrom "
					+ "AND :docRecvTo ");
		}
		if (derTaxPeriodFrom != 0 && derTaxPeriodTo != 0) {
			build.append(" AND DERIVED_RET_PERIOD BETWEEN "
					+ ":derTaxPeriodFrom AND :derTaxPeriodTo ");
		}
		if (docCategory != null && docCategory.size() > 0) {
			build.append(" AND DATA_CATEGORY IN : docCategory ");
		}

		String buildQuery = build.toString();
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");

		String queryStr = createQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}
		List<Gstr2PRSummarySectionDto> retList = new ArrayList<>();
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
			if (derTaxPeriodFrom != 0 && derTaxPeriodTo != 0) {
				q.setParameter("derTaxPeriodFrom", derTaxPeriodFrom);
				q.setParameter("derTaxPeriodTo", derTaxPeriodTo);
			}
			if (docRecvFrom != null && docRecvTo != null) {
				q.setParameter("docRecvFrom", docRecvFrom);
				q.setParameter("docRecvTo", docRecvTo);
			}
			if (docCategory != null && docCategory.size()>0) {
				q.setParameter("docCategory", docCategory);
			}

			
			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			 retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution getting the data ----->" + retList);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("while Fetching data for Gstr2 PR Review Summary ---->",e);
		//	throw new AppException("Unexpected error in query execution.", e);
		}
		return retList;
	}

	private Gstr2PRSummarySectionDto convert(Object[] arr) {
		Gstr2PRSummarySectionDto obj = new Gstr2PRSummarySectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTable((String) arr[0]);
		obj.setDocType((String) arr[1]);
		obj.setCount((GenUtil.getBigInteger(arr[2])).intValue());
		obj.setInvoiceValue((BigDecimal) arr[3]);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setTaxPayable((BigDecimal) arr[5]);
		obj.setTaxPayableIgst((BigDecimal) arr[6]);
		obj.setTaxPayableCgst((BigDecimal) arr[7]);
		obj.setTaxPayableSgst((BigDecimal) arr[8]);
		obj.setTaxPayableCess((BigDecimal) arr[9]);
		obj.setCrEligibleTotal((BigDecimal) arr[10]);
		obj.setCrEligibleIgst((BigDecimal) arr[11]);
		obj.setCrEligibleCgst((BigDecimal) arr[12]);
		obj.setCrEligibleSgst((BigDecimal) arr[13]);
		obj.setCrEligibleCess((BigDecimal) arr[14]);

		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

			String queryStr = "SELECT "
					+ "  TABLE_TYPE, "
					+ "  DOC_TYPE, "
					+ "  COUNT(DOC_KEY) COUNT, "
					+ "  SUM(INV_VALUE) INV_VALUE, "
					+ "  SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
					+ "  SUM(TOT_TAX) TOT_TAX, "
					+ "  SUM(TAX_PAYABLE_IGST) TAX_PAYABLE_IGST, "
					+ "  SUM(TAX_PAYABLE_CGST) TAX_PAYABLE_CGST, "
					+ "  SUM(TAX_PAYABLE_SGST) TAX_PAYABLE_SGST, "
					+ "  SUM(TAX_PAYABLE_CESS) TAX_PAYABLE_CESS, "
					+ "  SUM(TOT_CREDIT_ELIGIBLE) TOT_CREDIT_ELIGIBLE, "
					+ "  SUM(CR_ELG_IGST) CR_ELG_IGST, "
					+ "  SUM(CR_ELG_CGST) CR_ELG_CGST, "
					+ "  SUM(CR_ELG_SGST) CR_ELG_SGST, "
					+ "  SUM(CR_ELG_CESS) CR_ELG_CESS "
					+ "FROM "
					+ "  ( "
					+ "    SELECT "
					+ "      ( "
					+ "        CASE WHEN TAX_DOC_TYPE IN ('B2B') THEN '1-B2B' WHEN TAX_DOC_TYPE IN ('B2BA') THEN '2-B2BA' WHEN TAX_DOC_TYPE IN ('CDN') THEN '3-CDN' WHEN TAX_DOC_TYPE IN ('CDNA') THEN '4-CDNA' WHEN TAX_DOC_TYPE IN ('ISD') THEN '5-ISD' WHEN TAX_DOC_TYPE IN ('ISDA') THEN '6-ISDA' WHEN TAX_DOC_TYPE IN ('IMPS', 'IMPG', 'IMPGS') THEN '10-IMP' WHEN TAX_DOC_TYPE IN ('IMPSA', 'IMPGA', 'IMPGSA') THEN '11-IMPA' WHEN TAX_DOC_TYPE IN ('RCURD') THEN '12-RCURD' WHEN TAX_DOC_TYPE IN ('RCURDA') THEN '13-RCURDA' WHEN TAX_DOC_TYPE IN ('RCMADV') THEN '14-RCMADV' END "
					+ "      ) AS TABLE_TYPE, "
					+ "      'TOTAL' AS DOC_TYPE, "
					+ "      DOC_KEY, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(DOC_AMT,0)) ELSE (IFNULL(DOC_AMT,0)) END AS INV_VALUE, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(TAXABLE_VALUE,0)) ELSE (IFNULL(TAXABLE_VALUE,0)) END AS TAXABLE_VALUE, "
					+ "     CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(IGST_AMT, 0)+ IFNULL (CGST_AMT,0)+ IFNULL(SGST_AMT, 0)+ IFNULL (CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) "
					+ "           ELSE (IFNULL(IGST_AMT, 0)+ IFNULL (CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL (CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) END AS TOT_TAX, "
					+ "     CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(IGST_AMT,0)) ELSE (IFNULL(IGST_AMT,0)) END AS TAX_PAYABLE_IGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(CGST_AMT,0)) ELSE (IFNULL(CGST_AMT,0)) END AS TAX_PAYABLE_CGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(SGST_AMT,0)) ELSE (IFNULL(SGST_AMT,0)) END AS TAX_PAYABLE_SGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) "
					+ "           ELSE (IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) END AS TAX_PAYABLE_CESS, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_IGST, 0)+ IFNULL(AVAILABLE_CGST, 0)+ IFNULL (AVAILABLE_SGST, 0)+ IFNULL(AVAILABLE_CESS, 0)) "
					+ "           ELSE (IFNULL(AVAILABLE_IGST, 0)+ IFNULL(AVAILABLE_CGST, 0)+ IFNULL (AVAILABLE_SGST, 0)+ IFNULL(AVAILABLE_CESS, 0)) END AS TOT_CREDIT_ELIGIBLE, "
					+ "       CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_IGST,0)) ELSE (IFNULL(AVAILABLE_IGST,0)) END AS CR_ELG_IGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_CGST,0)) ELSE (IFNULL(AVAILABLE_CGST,0)) END AS CR_ELG_CGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_SGST,0)) ELSE (IFNULL(AVAILABLE_SGST,0)) END AS CR_ELG_SGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_CESS,0)) ELSE (IFNULL(AVAILABLE_CESS,0)) END AS CR_ELG_CESS "
					+ "    FROM "
					+ "      ANX_INWARD_DOC_HEADER "
					+ "    WHERE "
					+ "      IS_PROCESSED = TRUE "
					+ "      AND IS_DELETE = FALSE "
					+ "      AND TAX_DOC_TYPE IN ( "
					+ "        'B2B', 'B2BA', 'CDN', 'CDNA', 'ISD', "
					+ "        'ISDA', 'IMPS', 'IMPSA', 'IMPG', 'IMPGA', "
					+ "        'IMPGS', 'IMPGSA', 'RCURD', 'RCURDA','RCMADV' "
					+ "      ) "
					+ "      AND DOC_TYPE IN ( "
					+ "        'INV', 'SLF', 'DR', 'CR', 'RNV', 'RSLF', "
					+ "        'RDR', 'RCR','ADV','ADJ' "
					+ "      ) AND "
					+ buildQuery
					+ "    UNION ALL "
					+ "    SELECT "
					+ "      CASE WHEN TAX_DOC_TYPE = 'B2B' THEN '1-B2B' WHEN TAX_DOC_TYPE = 'B2BA' THEN '2-B2BA' WHEN TAX_DOC_TYPE = 'CDN' THEN '3-CDN' WHEN TAX_DOC_TYPE = 'CDNA' THEN '4-CDNA' WHEN TAX_DOC_TYPE = 'ISD' THEN '5-ISD' WHEN TAX_DOC_TYPE = 'ISDA' THEN '6-ISDA' WHEN TAX_DOC_TYPE='RCMADV' THEN '14-RCMADV' ELSE TABLE_SECTION || '-' || TAX_DOC_TYPE END TABLE_TYPE, "
					+ "      DOC_TYPE AS DOC_TYPE, "
					+ "      DOC_KEY, "
					+ "      IFNULL( "
					+ "        SUM(DOC_AMT), "
					+ "        0 "
					+ "      ) AS INV_VALUE, "
					+ "      IFNULL( "
					+ "        SUM(TAXABLE_VALUE), "
					+ "        0 "
					+ "      ) AS TAXABLE_VALUE, "
					+ "      ( "
					+ "        IFNULL( "
					+ "          SUM(IGST_AMT), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(CGST_AMT), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(SGST_AMT), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(CESS_AMT_SPECIFIC), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(CESS_AMT_ADVALOREM), "
					+ "          0 "
					+ "        ) "
					+ "      ) AS TOT_TAX, "
					+ "      IFNULL( "
					+ "        SUM(IGST_AMT), "
					+ "        0 "
					+ "      ) AS TAX_PAYABLE_IGST, "
					+ "      IFNULL( "
					+ "        SUM(CGST_AMT), "
					+ "        0 "
					+ "      ) AS TAX_PAYABLE_CGST, "
					+ "      IFNULL( "
					+ "        SUM(SGST_AMT), "
					+ "        0 "
					+ "      ) AS TAX_PAYABLE_SGST, "
					+ "      ( "
					+ "        IFNULL( "
					+ "          SUM(CESS_AMT_SPECIFIC), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(CESS_AMT_ADVALOREM), "
					+ "          0 "
					+ "        ) "
					+ "      ) AS TAX_PAYABLE_CESS, "
					+ "      ( "
					+ "        IFNULL( "
					+ "          SUM(AVAILABLE_IGST), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(AVAILABLE_CGST), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(AVAILABLE_SGST), "
					+ "          0 "
					+ "        )+ IFNULL( "
					+ "          SUM(AVAILABLE_CESS), "
					+ "          0 "
					+ "        ) "
					+ "      ) AS TOT_CREDIT_ELIGIBLE, "
					+ "      IFNULL( "
					+ "        SUM(AVAILABLE_IGST), "
					+ "        0 "
					+ "      ) AS CR_ELG_IGST, "
					+ "      IFNULL( "
					+ "        SUM(AVAILABLE_CGST), "
					+ "        0 "
					+ "      ) AS CR_ELG_CGST, "
					+ "      IFNULL( "
					+ "        SUM(AVAILABLE_SGST), "
					+ "        0 "
					+ "      ) AS CR_ELG_SGST, "
					+ "      IFNULL( "
					+ "        SUM(AVAILABLE_CESS), "
					+ "        0 "
					+ "      ) AS CR_ELG_CESS "
					+ "    FROM "
					+ "      ANX_INWARD_DOC_HEADER "
					+ "    WHERE "
					+ "      IS_PROCESSED = TRUE "
					+ "      AND IS_DELETE = FALSE "
					+ "      AND TAX_DOC_TYPE IN ( "
					+ "        'B2B', 'B2BA', 'CDN', 'CDNA', 'ISD', "
					+ "        'ISDA','RCMADV' "
					+ "      ) "
					+ "      AND DOC_TYPE IN ( "
					+ "        'INV', 'DR', 'CR', 'RNV', 'RDR', 'RCR','ADV','ADJ' "
					+ "      ) AND "
					+ buildQuery
					+ "    GROUP BY "
					+ "      TAX_DOC_TYPE, "
					+ "      TABLE_SECTION, "
					+ "      DOC_TYPE,DOC_KEY "
					+ "    UNION ALL "
					+ "    SELECT "
					+ "      ( "
					+ "        CASE WHEN TAX_DOC_TYPE IN ('IMPS', 'IMPG', 'IMPGS') THEN '10-IMP' WHEN TAX_DOC_TYPE IN ('IMPSA', 'IMPGA', 'IMPGSA') THEN '11-IMPA' WHEN TAX_DOC_TYPE IN ('RCURD') THEN '12-RCURD' WHEN TAX_DOC_TYPE IN ('RCURDA') THEN '13-RCURDA' END "
					+ "      ) AS TABLE_TYPE, "
					+ "      ( "
					+ "        CASE WHEN TAX_DOC_TYPE IN ('IMPS') THEN 'IMPS' WHEN TAX_DOC_TYPE IN ('IMPG') THEN 'IMPG' WHEN TAX_DOC_TYPE IN ('IMPGS') THEN 'IMPGS' WHEN TAX_DOC_TYPE IN ('IMPSA') THEN 'IMPSA' WHEN TAX_DOC_TYPE IN ('IMPGA') THEN 'IMPGA' WHEN TAX_DOC_TYPE IN ('IMPGSA') THEN 'IMPGSA' WHEN TAX_DOC_TYPE IN ('RCURD') THEN 'RCURD' WHEN TAX_DOC_TYPE IN ('RCURDA') THEN 'RCURDA' END "
					+ "      ) AS DOC_TYPE, "
					+ "      DOC_KEY, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(DOC_AMT,0)) ELSE (IFNULL(DOC_AMT,0)) END AS INV_VALUE, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(TAXABLE_VALUE,0)) ELSE (IFNULL(TAXABLE_VALUE,0)) END AS TAXABLE_VALUE, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(IGST_AMT, 0)+ IFNULL (CGST_AMT,0)+ IFNULL(SGST_AMT, 0)+ IFNULL (CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) "
					+ "           ELSE (IFNULL(IGST_AMT, 0)+ IFNULL (CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL (CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) END AS TOT_TAX, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(IGST_AMT,0)) ELSE (IFNULL(IGST_AMT,0)) END AS TAX_PAYABLE_IGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(CGST_AMT,0)) ELSE (IFNULL(CGST_AMT,0)) END AS TAX_PAYABLE_CGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(SGST_AMT,0)) ELSE (IFNULL(SGST_AMT,0)) END AS TAX_PAYABLE_SGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) "
					+ "           ELSE (IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) END AS TAX_PAYABLE_CESS, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_IGST, 0)+ IFNULL(AVAILABLE_CGST, 0)+ IFNULL (AVAILABLE_SGST, 0)+ IFNULL(AVAILABLE_CESS, 0)) "
					+ "           ELSE (IFNULL(AVAILABLE_IGST, 0)+ IFNULL(AVAILABLE_CGST, 0)+ IFNULL (AVAILABLE_SGST, 0)+ IFNULL(AVAILABLE_CESS, 0)) END AS TOT_CREDIT_ELIGIBLE, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_IGST,0)) ELSE (IFNULL(AVAILABLE_IGST,0)) END AS CR_ELG_IGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_CGST,0)) ELSE (IFNULL(AVAILABLE_CGST,0)) END AS CR_ELG_CGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_SGST,0)) ELSE (IFNULL(AVAILABLE_SGST,0)) END AS CR_ELG_SGST, "
					+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_CESS,0)) ELSE (IFNULL(AVAILABLE_CESS,0)) END AS CR_ELG_CESS "
					+ "    FROM "
					+ "      ANX_INWARD_DOC_HEADER "
					+ "    WHERE "
					+ "      IS_PROCESSED = TRUE "
					+ "      AND IS_DELETE = FALSE "
					+ "      AND TAX_DOC_TYPE IN ( "
					+ "        'IMPS', 'IMPSA', 'IMPG', 'IMPGA', 'IMPGS', "
					+ "        'IMPGSA', 'RCURD', 'RCURDA' "
					+ "      ) "
					+ "      AND DOC_TYPE IN ( "
					+ "        'INV', 'SLF', 'DR', 'CR', 'RNV', 'RSLF', "
					+ "        'RDR', 'RCR' "
					+ "      ) AND "
					+ buildQuery
					+ "  ) "
					+ "GROUP BY "
					+ "  TABLE_TYPE, "
					+ "  DOC_TYPE; ";
		
		LOGGER.debug("Gstr2 PR Summary Query Execution ENDs----->",queryStr);
		return queryStr;
	}

}
