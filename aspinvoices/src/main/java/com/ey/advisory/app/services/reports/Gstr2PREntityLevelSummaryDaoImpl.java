package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GSTR2PREntityLevelSummaryDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr2PREntityLevelSummaryDaoImpl")
@Slf4j
public class Gstr2PREntityLevelSummaryDaoImpl
		implements Gstr2PREntityLevelSummaryDao {
	
	private final List<String> predefinedOrder = ImmutableList.of("B2B", "B2BA", "CDN", "CDNA", "IMPG", "IMPGA", "IMPGS",
			"IMPGSA", "IMPS", "IMPSA", "ISD", "ISDA", "RCURD", "RCURDA","RCMADV");

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getEntityLevelSummary(SearchCriteria searchParams) {

		Gstr2ProcessedRecordsReqDto req = (Gstr2ProcessedRecordsReqDto) searchParams;

		Gstr2ProcessedRecordsReqDto request = basicCommonSecParam
				.setInwardGstr2PRSumDataSecuritySearchParams(req);

		// List<String> gstin = req.getGstin();
		int derTaxPeriodTo = 0;
		int derTaxPeriodFrom = 0;
		LocalDate docRecvTo = request.getDocRecvTo();
		LocalDate docRecvFrom = request.getDocRecvFrom();
		if (!Strings.isNullOrEmpty(request.getTaxPeriodTo())) {
			String taxPeriodTo = request.getTaxPeriodTo();
			derTaxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
		}
		if (!Strings.isNullOrEmpty(request.getTaxPeriodFrom())) {
			String taxPeriodFrom = request.getTaxPeriodFrom();
			derTaxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
		}

		List<String> tableType = request.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		List<String> docType = request.getDocType();
		List<String> docTypeUpperCase = docType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

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
					gstin = key;//GSTIN
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
				build.append(" CUST_GSTIN IN (:gstinList)");
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
			build.append(
					" AND DOC_DATE BETWEEN :docRecvFrom " + "AND :docRecvTo ");
		}
		if (derTaxPeriodFrom != 0 && derTaxPeriodTo != 0) {
			build.append(" AND DERIVED_RET_PERIOD BETWEEN "
					+ ":derTaxPeriodFrom AND :derTaxPeriodTo ");
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			build.append(" AND DOC_TYPE IN :docTypeUpperCase ");
		}
		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			build.append(" AND  TAX_DOC_TYPE IN :tableTypeUpperCase ");
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
			if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
				q.setParameter("tableTypeUpperCase", tableTypeUpperCase);
			}
			if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
				q.setParameter("docTypeUpperCase", docTypeUpperCase);
			}

			if (docCategory != null && docCategory.size() > 0) {
				q.setParameter("docCategory", docCategory);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			// List<Object[]> retList = q.getResultList();
			List<Object> sortedList = list.parallelStream().map(this::convertEntityLevelSummary).sorted(Comparator
					.comparing(obj -> predefinedOrder.indexOf(obj.getTableDescription()), Comparator.naturalOrder() 
					)).collect(Collectors.toList());
			return sortedList;
		} catch (Exception e) {
			LOGGER.error("Gstr2 PREntity Level Summary Data Error {}", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private GSTR2PREntityLevelSummaryDto convertEntityLevelSummary(
			Object[] arr) {
		GSTR2PREntityLevelSummaryDto obj = new GSTR2PREntityLevelSummaryDto();

		obj.setGSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setTableDescription(arr[1] != null ? arr[1].toString() : null);
		// obj.setCount((Integer) arr[2]);
		// int AspCount = (GenUtil.getBigInteger(arr[2])).intValue();
		obj.setCount((GenUtil.getBigInteger(arr[2])).intValue());
		// obj.setAspCount(AspCount);

		obj.setInvoiceValue((BigDecimal) arr[3]);
		obj.setTaxableValue((BigDecimal) arr[4]);
		obj.setTotalTax((BigDecimal) arr[5]);
		obj.setIGSTTax((BigDecimal) arr[6]);
		obj.setCGSTTax((BigDecimal) arr[7]);
		obj.setSGSTTax((BigDecimal) arr[8]);
		obj.setCessTax((BigDecimal) arr[9]);
		obj.setTotalCreditEligible((BigDecimal) arr[10]);
		obj.setEligibleIGST((BigDecimal) arr[11]);
		obj.setEligibleCGST((BigDecimal) arr[12]);
		obj.setEligibleSGST((BigDecimal) arr[13]);
		obj.setEligibleCess((BigDecimal) arr[14]);

		return obj;

	}

	private static String createQueryString(String buildQuery) {

		String queryString = "SELECT CUST_GSTIN,TAX_DOC_TYPE,COUNT(ID),"
				+ " IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') "
				+ "THEN IFNULL(DOC_AMT,0) END),0) - IFNULL(SUM(CASE  "
				+ "WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(DOC_AMT,0) END ),0) "
				+ "AS INVOICE_VALUE, IFNULL(SUM(CASE WHEN DOC_TYPE IN  "
				+ "('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN TAXABLE_VALUE END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN  "
				+ "TAXABLE_VALUE END),0) AS TAX_VALUE, IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN  "
				+ "IFNULL(TAX_PAYABLE,0) END),0) - IFNULL(SUM(CASE WHEN "
				+ "DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(TAX_PAYABLE,0) END),0) "
				+ "AS TOTAL_TAX, IFNULL(SUM(CASE WHEN DOC_TYPE IN  "
				+ "('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN IFNULL(IGST_AMT,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN  "
				+ "IFNULL(IGST_AMT,0) END),0) AS IGST_AMT, IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN "
				+ "IFNULL(CGST_AMT,0) END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE  "
				+ "IN ('CR','RCR','ADJ') THEN IFNULL(CGST_AMT,0)END),0) AS CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') "
				+ "THEN IFNULL(SGST_AMT,0) END),0) - IFNULL(SUM(CASE WHEN  "
				+ "DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(SGST_AMT,0)END),0) AS "
				+ "SGST_AMT, (IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR', "
				+ "'RNV','RDR','ADV','SLF', 'RSLF') THEN IFNULL(CESS_AMT_SPECIFIC,0) END),0) - "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN  "
				+ "IFNULL(CESS_AMT_SPECIFIC,0) END),0)) + (IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN  "
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) - IFNULL(SUM(CASE  "
				+ "WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(CESS_AMT_ADVALOREM,0) "
				+ "END),0)) AS CESS_AMT, (IFNULL(SUM(CASE WHEN DOC_TYPE IN  "
				+ "('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN IFNULL(AVAILABLE_IGST,0) END),0) "
				+ "+ IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') "
				+ "THEN IFNULL(AVAILABLE_CGST,0) END),0) + IFNULL(SUM(CASE  "
				+ "WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN  "
				+ "IFNULL(AVAILABLE_SGST,0) END),0) + IFNULL(SUM(CASE "
				+ "WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN "
				+ "IFNULL(AVAILABLE_CESS,0) END),0) ) - (IFNULL(SUM(CASE  "
				+ "WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(AVAILABLE_IGST,0) "
				+ "END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR','ADJ') "
				+ "THEN IFNULL(AVAILABLE_CGST,0) END),0) + IFNULL(SUM(CASE  "
				+ "WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(AVAILABLE_SGST,0) "
				+ "END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN  "
				+ "IFNULL(AVAILABLE_CESS,0) END),0) ) AS TOTAL_CREDIT_ELIGIBLE, "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV')  "
				+ "THEN IFNULL(AVAILABLE_IGST,0)END),0) - IFNULL(SUM(CASE WHEN "
				+ "DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(AVAILABLE_IGST,0) END),0) AS  "
				+ "AVAILABLE_IGST, IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR', "
				+ "'RNV','RDR','ADV','SLF','RSLF') THEN IFNULL(AVAILABLE_CGST,0) END),0) -  "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN "
				+ "IFNULL(AVAILABLE_CGST,0) END),0) AS AVAILABLE_CGST, "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV','DR','RNV','RDR','SLF','RSLF','ADV') "
				+ "THEN IFNULL(AVAILABLE_SGST,'0') END),0) - IFNULL(SUM(CASE  "
				+ "WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN IFNULL(AVAILABLE_SGST,0) "
				+ "END),0) AS AVAILABLE_SGST, IFNULL(SUM(CASE WHEN DOC_TYPE "
				+ "IN('INV','DR','RNV','RDR','SLF','RSLF','ADV') THEN IFNULL(AVAILABLE_CESS,'0') "
				+ "END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR','RCR','ADJ') THEN "
				+ "IFNULL(AVAILABLE_CESS,0) END),0) AS AVAILABLE_CESS,"
				+ "fnGetState(CUST_GSTIN) FROM ANX_INWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND "
				+ "RETURN_TYPE = 'GSTR2'  AND " + buildQuery
				+ "GROUP BY CUST_GSTIN,TAX_DOC_TYPE";
		return queryString;
	}


}
