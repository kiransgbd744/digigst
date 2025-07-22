package com.ey.advisory.app.services.daos.prsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.daos.initiaterecon.Anx2PRReviewSummeryResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;

/**
 * @author Siva.Nandam
 *
 */
@Component("GetANX2ReviewSummaryDaoImpl")
public class GetANX2ReviewSummaryDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Anx2PRReviewSummeryResponseDto> getAnx2PRSProcessedRecs(
			Anx2PRSProcessedRequestDto request) {

		String taxperiod = request.getTaxPeriod();
		List<String> docType = request.getDocType();
		List<String> recordtype = request.getRecordType();
		LocalDate fromDate = request.getFromdate();
		LocalDate toDate = request.getToDate();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String purchase = null;
		String division = null;
		String location = null;
		// String distChannel = null;
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
		List<String> purchaseList = null;
		// List<String> distList = null;
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
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
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

		StringBuilder buildQuery = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND CUST_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND PLANT_CODE IN :plantList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				buildQuery.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}

		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND USERACCESS6 IN :ud6List");
			}
		}

		if (fromDate != null && toDate != null) {
			buildQuery.append(" AND DOC_DATE BETWEEN ");
			buildQuery.append(":fromDate AND :toDate");
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxperiod ");
		}

		if (recordtype != null && recordtype.size() > 0) {
			buildQuery.append(" AND AN_TAX_DOC_TYPE IN :recordtype ");
		}

		if (docType != null && docType.size() > 0) {
			buildQuery.append(" AND DOC_TYPE IN :docType ");
		}

		String queryStr = createProcessedQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriod());
			q.setParameter("taxperiod", derivedRetPeriodFrom);
		}
		if (fromDate != null && toDate != null) {
			q.setParameter("fromDate", fromDate);
			q.setParameter("toDate", toDate);
		}
		if (recordtype != null && recordtype.size() > 0) {
			q.setParameter("recordtype", recordtype);
		}

		if (docType != null && docType.size() > 0) {
			q.setParameter("docType", docType);
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
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && !purchaseList.isEmpty()
					&& purchaseList.size() > 0) {
				q.setParameter("purchaseList", purchaseList);
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
		List<Anx2PRReviewSummeryResponseDto> retList = list.parallelStream()
				.map(o -> convertProcessedInfo(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2PRReviewSummeryResponseDto convertProcessedInfo(Object[] arr) {
		Anx2PRReviewSummeryResponseDto obj = new Anx2PRReviewSummeryResponseDto();

		if (arr[0] == null || arr[0].toString().isEmpty()) {
			arr[0] = new BigDecimal("0");
		}

		if (arr[4] == null || arr[4].toString().isEmpty()) {
			arr[4] = new BigDecimal("0.0");
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			arr[5] = new BigDecimal("0.0");
		}
		if (arr[6] == null || arr[6].toString().isEmpty()) {
			arr[6] = new BigDecimal("0.0");
		}
		if (arr[7] == null || arr[7].toString().isEmpty()) {
			arr[7] = new BigDecimal("0.0");
		}
		if (arr[8] == null || arr[8].toString().isEmpty()) {
			arr[8] = new BigDecimal("0.0");
		}
		if (arr[9] == null || arr[9].toString().isEmpty()) {
			arr[9] = new BigDecimal("0.0");
		}
		if (arr[10] == null || arr[10].toString().isEmpty()) {
			arr[10] = new BigDecimal("0.0");
		}
		if (arr[11] == null || arr[11].toString().isEmpty()) {
			arr[11] = new BigDecimal("0.0");
		}
		if (arr[12] == null || arr[12].toString().isEmpty()) {
			arr[12] = new BigDecimal("0.0");
		}
		
		if (arr[13] == null || arr[13].toString().isEmpty()) {
			arr[13] = new BigDecimal("0.0");
		}
		if (arr[14] == null || arr[14].toString().isEmpty()) {
			arr[14] = new BigDecimal("0.0");
		}
		if (arr[15] == null || arr[15].toString().isEmpty()) {
			arr[15] = new BigDecimal("0.0");
		}

		BigInteger count = GenUtil.getBigInteger(arr[0]);
		obj.setCount(count.intValue());
		obj.setInvValue((BigDecimal) arr[4]);
		obj.setTaxableValue((BigDecimal) arr[5]);
		obj.setTotalTaxPayable((BigDecimal) arr[6]);
		obj.setTpIGST((BigDecimal) arr[7]);
		obj.setTpCGST((BigDecimal) arr[8]);
		obj.setTpSGST((BigDecimal) arr[9]);
		obj.setTpCess((BigDecimal) arr[10]);
		obj.setTotalCreditEligible((BigDecimal) arr[11]);
		obj.setCeIGST((BigDecimal) arr[12]);
		obj.setCeCGST((BigDecimal) arr[13]);
		obj.setCeSGST((BigDecimal) arr[14]);
		obj.setCeCess((BigDecimal) arr[15]);
		obj.setTable((String) arr[3]);
		obj.setTaxDocType((String) arr[16]);
		return obj;

	}

	private String createProcessedQueryString(String buildQuery) {

		String queryStr = "SELECT count(ID),CUST_GSTIN,DERIVED_RET_PERIOD,"
				+ "DOC_TYPE,SUM(DOC_AMT),"
				+ "SUM(TAXABLE_VALUE),SUM(TAX_PAYABLE) AS TOTAL_TAX_PAYABLE,"
				+ "SUM(IGST_AMT),SUM(CGST_AMT),SUM(SGST_AMT),"
				+ "SUM(CESS_AMT_SPECIFIC + CESS_AMT_ADVALOREM) AS CESS_AMT,"
				+ "SUM(AVAILABLE_IGST+AVAILABLE_CGST+AVAILABLE_SGST+AVAILABLE_CESS) AS  "
				+ "TOTAL_CREDIT_ELIGIBLE,"
				+ "SUM(AVAILABLE_IGST),SUM(AVAILABLE_CGST),SUM(AVAILABLE_SGST),"
				+ "SUM(AVAILABLE_CESS),AN_TAX_DOC_TYPE "
				+ "FROM ANX_INWARD_DOC_HEADER " + "WHERE "
				+ "IS_PROCESSED = TRUE AND IS_DELETE=FALSE"
				+ buildQuery.toString()
				+ " GROUP BY(CUST_GSTIN),(DERIVED_RET_PERIOD),(DOC_TYPE),"
				+ "(AN_TAX_DOC_TYPE) ";
		return queryStr;
	}

}
