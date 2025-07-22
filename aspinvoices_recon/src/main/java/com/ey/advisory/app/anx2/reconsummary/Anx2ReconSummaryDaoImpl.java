package com.ey.advisory.app.anx2.reconsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Anx2ReconSummaryDaoImpl")
public class Anx2ReconSummaryDaoImpl implements Anx2ReconSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public static ImmutableMap<String, String> categoryMap = ImmutableMap
			.<String, String>builder().put("Exact Match", "Match")
			.put("Document Type Mismatch", "Mismatches")
			.put("Match upto Tolerance", "Match")
			.put("Document Date Mismatch", "Mismatches")
			.put("Value Mismatch", "Mismatches")
			.put("POS Mismatch", "Mismatches")
			.put("Multi-Mismatch", "Mismatches")
			.put("Fuzzy Match", "Probable Match")
			.put("Forced Match", "Forced Match")
			.put("Addition in ANX-2", "Additional Entries")
			.put("Addition in PR", "Additional Entries").build();

	Function<String, String> getCatergyName = (obj) -> {
		if (categoryMap.containsKey(obj)) {
			return categoryMap.get(obj);
		} else {
			return "";
		}
	};

	@Override
	public List<Anx2ReconSummaryDto> findReconSummDetails(
			ReconSummaryReqDto reqDto, int taxPeriod, String validQuery) {
		List<String> arrName = new ArrayList<String>();
		ImmutableSet<String> reconParticulars = categoryMap.keySet();
		List<Anx2ReconSummaryDto> respDto = new ArrayList<Anx2ReconSummaryDto>();
		String prCountQuery = getA2PRCountSql();
		Query q = entityManager.createNativeQuery(prCountQuery);
		q.setParameter("returnPeriod", taxPeriod);
		q.setParameter("gstins", reqDto.getGstins());
		Object obj = q.getSingleResult();
		BigInteger prCount = (BigInteger) obj;
		String a2CountQuery = getA2CountSql();
		q = entityManager.createNativeQuery(a2CountQuery);
		q.setParameter("returnPeriod", taxPeriod);
		q.setParameter("gstins", reqDto.getGstins());
		obj = q.getSingleResult();
		BigInteger a2Count = (BigInteger) obj;
		String summaryQuery = getReconSummaryQuery(validQuery);
		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Anx2ReconSummaryDetails "
							+ "on FilterConditions Provided By User : %s",
					summaryQuery);
			LOGGER.debug(str);
		}
		q = entityManager.createNativeQuery(summaryQuery);
		q.setParameter("returnPeriod", taxPeriod);
		q.setParameter("gstins", reqDto.getGstins());
		q.setParameter("a2Count", a2Count);
		q.setParameter("prCount", prCount);
		
		/*
		 * DataSecurities are commited as per discussion with Saurabh Dhariwal
		 * and Anand Srinivasan as we cannot get datasecurity for reconsolitate
		 * removing it 
		 */
		
	/*	if (!CollectionUtils.isEmpty(reqDto.getProfitCentres())) {
			q.setParameter("profitCenters", reqDto.getProfitCentres());
		}

		if (!CollectionUtils.isEmpty(reqDto.getPlants())) {
			q.setParameter("plants", reqDto.getPlants());
		}

		if (!CollectionUtils.isEmpty(reqDto.getDivisions())) {
			q.setParameter("divisions", reqDto.getDivisions());
		}

		if (!CollectionUtils.isEmpty(reqDto.getLocations())) {
			q.setParameter("locations", reqDto.getLocations());
		}

		if (!CollectionUtils.isEmpty(reqDto.getPurchaseOrgs())) {
			q.setParameter("purchaseOrgs", reqDto.getPurchaseOrgs());
		}

		if (!CollectionUtils.isEmpty(reqDto.getUserAccess1())) {
			q.setParameter("userAccess1", reqDto.getUserAccess1());
		}

		if (!CollectionUtils.isEmpty(reqDto.getUserAccess2())) {
			q.setParameter("userAccess2", reqDto.getUserAccess2());
		}

		if (!CollectionUtils.isEmpty(reqDto.getUserAccess3())) {
			q.setParameter("userAccess3", reqDto.getUserAccess3());
		}

		if (!CollectionUtils.isEmpty(reqDto.getUserAccess4())) {
			q.setParameter("userAccess4", reqDto.getUserAccess4());
		}

		if (!CollectionUtils.isEmpty(reqDto.getUserAccess5())) {
			q.setParameter("userAccess5", reqDto.getUserAccess5());
		}

		if (!CollectionUtils.isEmpty(reqDto.getUserAccess6())) {
			q.setParameter("userAccess6", reqDto.getUserAccess6());
		}*/
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		list.forEach(arr -> {
			Anx2ReconSummaryDto resp = convert(arr);
			respDto.add(resp);
		});
		arrName = respDto.stream().map(object -> object.getParticulars())
				.collect(Collectors.toList());

		for (String partiName : reconParticulars) {
			if (!arrName.contains(partiName)) {
				Anx2ReconSummaryDto dummyResp = converDummyResponse(partiName);
				respDto.add(dummyResp);
			}

		}
		return respDto;
	}

	private Anx2ReconSummaryDto converDummyResponse(String particular) {
		Anx2ReconSummaryDto reconSummary = new Anx2ReconSummaryDto();
		reconSummary.setParticulars(particular);
		reconSummary.setSuggestedResponse("");
		reconSummary.setAnx2count(BigInteger.ZERO);
		reconSummary.setAnxtaxvalue(BigDecimal.ZERO);
		reconSummary.setAnxtotaltax(BigDecimal.ZERO);
		reconSummary.setAnx2per(BigDecimal.ZERO);
		reconSummary.setPr2count(BigInteger.ZERO);
		reconSummary.setPrtaxvalue(BigDecimal.ZERO);
		reconSummary.setPrtotaltax(BigDecimal.ZERO);
		reconSummary.setPr2per(BigDecimal.ZERO);
		reconSummary.setTotalAvailableTax(BigDecimal.ZERO);
		reconSummary.setCategory(getCatergyName.apply(particular));
		return reconSummary;
	}

	private Anx2ReconSummaryDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Anx2ReconSummaryDto object";
			LOGGER.debug(str);
		}

		Anx2ReconSummaryDto reconSummary = new Anx2ReconSummaryDto();
		reconSummary.setParticulars(arr[0] != null ? (String) arr[0] : null);
		reconSummary
				.setSuggestedResponse(arr[1] != null ? (String) arr[1] : null);
		if("Addition in PR".equalsIgnoreCase(reconSummary.getParticulars())) {
			reconSummary.setAnx2count(BigInteger.ZERO);
		} else {
			reconSummary.setAnx2count(
					arr[2] != null ? GenUtil.getBigInteger(arr[2]) : BigInteger.ZERO);
		}
		
		reconSummary.setAnxtaxvalue(
				arr[3] != null ? (BigDecimal) arr[3] : BigDecimal.ZERO);
		reconSummary.setAnxtotaltax(
				arr[4] != null ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		reconSummary.setAnx2per(
				arr[5] != null ? (BigDecimal) arr[5] : BigDecimal.ZERO);
		if("Addition in ANX-2".equalsIgnoreCase(reconSummary.getParticulars())) {
			reconSummary.setPr2count(BigInteger.ZERO);
		} else {
		reconSummary.setPr2count(
				arr[2] != null ? GenUtil.getBigInteger(arr[2]) : BigInteger.ZERO);
		}
		
		reconSummary.setPrtaxvalue(
				arr[6] != null ? (BigDecimal) arr[6] : BigDecimal.ZERO);
		reconSummary.setPrtotaltax(
				arr[7] != null ? (BigDecimal) arr[7] : BigDecimal.ZERO);
		reconSummary.setPr2per(
				arr[8] != null ? (BigDecimal) arr[8] : BigDecimal.ZERO);
		reconSummary.setTotalAvailableTax(
				arr[9] != null ? (BigDecimal) arr[9] : BigDecimal.ZERO);
		reconSummary.setCategory(getCatergyName.apply((String) arr[0]));

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Anx2ReconSummaryStatusDto object " + "is %s",
					reconSummary);
			LOGGER.debug(str);
		}
		return reconSummary;

	}

	public String getA2CountSql() {
		return "SELECT COUNT(RECON_LINK_ID) AS VAR_A2_COUNT  FROM  "
				+ "\"CLIENT1_GST\".\"LINK_A2_PR\" "
				+ "WHERE A2_RECIPIENT_GSTIN IN (:gstins) "
				+ "      AND DERIVED_RET_PERIOD =:returnPeriod AND "
				+ "IS_ACTIVE=TRUE AND IS_DELETED=FALSE";
	}

	public String getA2PRCountSql() {
		return "SELECT COUNT(RECON_LINK_ID) AS VAR_PR_COUNT  FROM  "
				+ "\"CLIENT1_GST\".\"LINK_A2_PR\" "
				+ "WHERE PR_RECIPIENT_GSTIN IN (:gstins) "
				+ "      AND DERIVED_RET_PERIOD =:returnPeriod AND "
				+ "IS_ACTIVE=TRUE AND IS_DELETED=FALSE";
	}

	public String getReconSummaryQuery(String validQuery) {
		return "SELECT  CURRENT_REPORT_TYPE "
				+ " , CASE WHEN SUGGESTED_RESPONSE='Accept ANX2 and Claim ITC as per PR' THEN 'Accept A2 & ITC PR Tax'"
                + " WHEN SUGGESTED_RESPONSE='Pending' THEN 'Pending ANX2' "
                + " ELSE SUGGESTED_RESPONSE END AS SUGGESTED_RESPONSE "
				+ "      , COUNT(RECON_LINK_ID) AS \"COUNT\" "
				+ "      , SUM(CASE WHEN A2_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(A2_TAXABLE_VALUE,0) "
				+ "                 ELSE IFNULL(A2_TAXABLE_VALUE,0) END) A2_TAXABLE_VALUE "
				+ "      , SUM((CASE WHEN A2_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(A2_CGST,0) "
				+ "                 ELSE IFNULL(A2_CGST,0) END) "
				+ "           +(CASE WHEN A2_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(A2_SGST,0) "
				+ "                 ELSE IFNULL(A2_SGST,0) END) "
				+ "           +(CASE WHEN A2_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(A2_IGST,0) "
				+ "                 ELSE IFNULL(A2_IGST,0) END) "
				+ "           +(CASE WHEN A2_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(A2_CESS,0) "
				+ "                 ELSE IFNULL(A2_CESS,0) END)) AS A2_TOTAL_TAX "
				+ "      ,CASE WHEN CURRENT_REPORT_TYPE='Addition in PR' THEN 0 "
				+ "            ELSE (COUNT(RECON_LINK_ID)*100)/:a2Count END AS A2_PERCENTAGE "
				+ "      , SUM(CASE WHEN PR_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(PR_TAXABLE_VALUE,0) "
				+ "                 ELSE IFNULL(PR_TAXABLE_VALUE,0) END) PR_TAXABLE_VALUE "
				+ "      , SUM ((CASE WHEN PR_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(PR_CGST,0) "
				+ "                 ELSE IFNULL(PR_CGST,0) END) "
				+ "           +(CASE WHEN PR_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(PR_SGST,0) "
				+ "                 ELSE IFNULL(PR_SGST,0) END) "
				+ "           +(CASE WHEN PR_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(PR_IGST,0) "
				+ "                 ELSE IFNULL(PR_IGST,0) END) "
				+ "           +(CASE WHEN PR_DOC_TYPE IN ('CR','RCR') THEN -IFNULL(PR_CESS,0) "
				+ "                 ELSE IFNULL(PR_CESS,0) END)) AS PR_TOTAL_TAX "
				+ "      ,CASE WHEN CURRENT_REPORT_TYPE='Addition in ANX-2' THEN 0 "
				+ "            ELSE (COUNT(RECON_LINK_ID)*100)/:prCount "
				+ "            END  AS PR_PERCENTAGE "
				+ "     , SUM ((CASE WHEN DH.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(DH.AVAILABLE_IGST,0) "
				+ "                 ELSE IFNULL(DH.AVAILABLE_IGST,0) END) "
				+ "           +(CASE WHEN DH.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(DH.AVAILABLE_CGST,0) "
				+ "                 ELSE IFNULL(DH.AVAILABLE_CGST,0) END) "
				+ "           +(CASE WHEN DH.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(DH.AVAILABLE_SGST,0) "
				+ "                 ELSE IFNULL(DH.AVAILABLE_SGST,0) END) "
				+ "           +(CASE WHEN DH.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(DH.AVAILABLE_CESS,0) "
				+ "                 ELSE IFNULL(DH.AVAILABLE_CESS,0) END)) AS TOTAL_AVAILABLE_TAX "
				+ "      FROM \"CLIENT1_GST\".\"LINK_A2_PR\" LK "
				+ "      LEFT OUTER JOIN \"CLIENT1_GST\".\"ANX_INWARD_DOC_HEADER\" DH ON LK.PR_ID=DH.ID "
				+ "      AND LK.PR_TABLE='ANX_INWARD_DOC_HEADER' AND DH.IS_DELETE=FALSE "
				+ "      WHERE (LK.PR_RECIPIENT_GSTIN IN (:gstins) OR LK.A2_RECIPIENT_GSTIN IN (:gstins)) "
				+ "      AND LK.DERIVED_RET_PERIOD=:returnPeriod AND LK.IS_ACTIVE=TRUE AND LK.IS_DELETED=FALSE "
				+ "      GROUP BY CURRENT_REPORT_TYPE,SUGGESTED_RESPONSE";
	
	}

}
