package com.ey.advisory.app.anx2.reconresponse.reviewsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("ReconrResponseReviewSummaryDaoImpl")
public class ReconrResponseReviewSummaryDaoImpl
		implements ReconrResponseReviewSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ReconrResponseReviewSummaryDto> findReconResponseSummary(
			String taxPeriod, String gstin, List<String> docTypeList,
			List<String> tableTypeList, List<String> typeList) {
		try {

			/*
			 * LOGGER.info("Invoking FETCH_RECON_RESP_SUMMARY Stored Proc");
			 * StoredProcedureQuery storedProc = entityManager
			 * .createNamedStoredProcedureQuery("FETCH_RECON_RESP_SUMMARY");
			 * 
			 * storedProc.setParameter("VAR_GSTIN", gstin);
			 * storedProc.setParameter("VAR_TAX_PERIOD", taxPeriod);
			 * storedProc.setParameter("VAR_TABLE_TYPE",
			 * StringUtils.join(tableTypeList, ","));
			 * storedProc.setParameter("VAR_DOC_TYPE",
			 * StringUtils.join(docTypeList, ","));
			 * storedProc.setParameter("VAR_TYPE", StringUtils.join(typeList,
			 * ","));
			 */

			String queryStr = createQueryString(taxPeriod, gstin, docTypeList,
					tableTypeList, typeList);

			Query q = entityManager.createNativeQuery(queryStr);
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);
			
			if(tableTypeList != null && tableTypeList.size() > 0)
			q.setParameter("tableTypeList", tableTypeList);
			
			if (docTypeList != null && docTypeList.size() > 0) 
				q.setParameter("docTypeList", docTypeList);
		
			if(typeList != null && typeList.size() > 0)
			q.setParameter("typeList", typeList);
			
			LOGGER.error(queryStr);
			
			if (LOGGER.isDebugEnabled()) {
				String str = String.format("executing query to get the data "
						+ "for gstin %s, taxPeriod %s, tableType %s, "
						+ "docType %s, type %s", gstin, taxPeriod, 
						tableTypeList, docTypeList, typeList);
				LOGGER.debug(str);

			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<ReconrResponseReviewSummaryDto> retList = list.parallelStream()
					.map(o -> converttoDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			if (LOGGER.isDebugEnabled()) {
				String str = String.format("Before returning DB ResultSet",
						retList);
				LOGGER.debug(str);
			}
			
			return retList;
			
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "ReconrResponseReviewSummaryDaoImpl"
					+ ".findReconResponseSummary");
		}
	}

	private ReconrResponseReviewSummaryDto converttoDto(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Converting generic object to"
					+ " ReconrResponseReviewSummaryDto object", arr);
			LOGGER.debug(str);
		}
		ReconrResponseReviewSummaryDto obj = 
				new ReconrResponseReviewSummaryDto("L3");
		obj.setResponse((String) arr[0]);
		obj.setTableType((String) arr[1]);
		obj.setDocType((String) arr[2]);
		obj.setExactMatch((BigDecimal) arr[3]);
		obj.setDocTypeMismatch((BigDecimal) arr[4]);
		obj.setMatchUpToTolerance((BigDecimal) arr[5]);
		obj.setDocDateMismatch((BigDecimal) arr[6]);
		obj.setValueMismatch((BigDecimal) arr[7]);
		obj.setPosMismatch((BigDecimal) arr[8]);
		obj.setMultiMismatch((BigDecimal) arr[9]);
		obj.setFuzzyMatch((BigDecimal) arr[10]);
		obj.setAddtionalAnx2((BigDecimal) arr[11]);
		obj.setAddtionalPR((BigDecimal) arr[12]);
		return obj;

	}

	private String createQueryString(String taxPeriod, String gstin,
			List<String> docTypeList, List<String> tableTypeList,
			List<String> typeList) {
		
		StringBuilder condition1 = new StringBuilder();
		StringBuilder condition2 = new StringBuilder();
		StringBuilder condition3 = new StringBuilder();

		if (docTypeList != null && docTypeList.size() > 0) {
			condition1.append(" AND DOC_TYPE IN (:docTypeList) ");
		}

		if (tableTypeList != null && tableTypeList.size() > 0) {
			condition2.append(" AND TABLE_TYPE IN (:tableTypeList) ");
		}
		
		if (typeList != null && typeList.size() > 0) {
			condition3.append(" AND  B.TYPE IN (:typeList) ");
		}

		String query = "SELECT USER_RESPONSE AS \"Response\" "
				+ ", TABLE_TYPE AS \"Table Type\" "
				+ ", DOC_TYPE AS \"Document Type\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Exact Match' THEN TAX_AMOUNT ELSE 0 END) AS \"Exact Match\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Document Type Mismatch' THEN TAX_AMOUNT ELSE 0 END) AS \"Document Type Mismatch\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Match upto Tolerance' THEN TAX_AMOUNT ELSE 0 END) AS \"Match upto Tolerance\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Document Date Mismatch' THEN TAX_AMOUNT ELSE 0 END) AS \"Document Date Mismatch\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Value Mismatch' THEN TAX_AMOUNT ELSE 0 END) AS \"Value Mismatch\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='POS Mismatch' THEN TAX_AMOUNT ELSE 0 END) AS \"POS Mismatch\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Multi-Mismatch' THEN TAX_AMOUNT ELSE 0 END) AS \"Multi-Mismatch\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Fuzzy Match' THEN TAX_AMOUNT ELSE 0 END) AS \"Fuzzy Match\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Addition in ANX-2' THEN TAX_AMOUNT ELSE 0 END) AS \"Addition in ANX-2\" "
				+ ", SUM(CASE WHEN CURRENT_REPORT_TYPE ='Addition in PR' THEN TAX_AMOUNT ELSE 0 END) AS \"Addition in PR\" "
				+ " " + "FROM " + " " + "( " + "SELECT USER_RESPONSE "
				+ "      ,CURRENT_REPORT_TYPE "
				+ "      ,CASE WHEN A2_TABLE IS NULL THEN TABLE_TYPE "
				+ "            ELSE A2_TABLE END AS TABLE_TYPE "
				+ "      ,DOC_TYPE "
				+ "      , CASE WHEN USER_RESPONSE='A1' THEN 'A2' "
				+ "             WHEN USER_RESPONSE='A2' THEN 'PR Available' "
				+ "             WHEN USER_RESPONSE='A3' THEN 'PR Tax' "
				+ "             WHEN USER_RESPONSE='P1' THEN 'A2' "
				+ "             WHEN USER_RESPONSE='R1' THEN 'A2' "
				+ "             WHEN USER_RESPONSE='R1U1' THEN 'PR Available' "
				+ "             WHEN USER_RESPONSE='U1' THEN 'PR Available' "
				+ "             WHEN USER_RESPONSE='U2' THEN 'PR Tax' "
				+ "             WHEN USER_RESPONSE='R1U2' THEN 'PR Tax' "
				+ "             WHEN USER_RESPONSE='A1U1' THEN 'PR Available' "
				+ "             WHEN USER_RESPONSE='A1U2' THEN 'PR Tax' "
				+ "             WHEN USER_RESPONSE='A4' THEN 'A2' "
				+ "             WHEN USER_RESPONSE='No Action(Addl ANX-2)' THEN 'A2' "
				+ "             WHEN USER_RESPONSE='No Action(Addl PR)' THEN 'PR Tax' "
				+ "             WHEN USER_RESPONSE='No Action(Reconciled)' THEN 'PR Tax' "
				+ "             END AS \"TYPE\" "
				+ "      , CASE WHEN USER_RESPONSE='A1' THEN A2_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='A2' THEN TOTAL_AVAILABLE_TAX "
				+ "             WHEN USER_RESPONSE='A3' THEN PR_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='P1' THEN A2_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='R1' THEN A2_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='R1U1' THEN TOTAL_AVAILABLE_TAX "
				+ "             WHEN USER_RESPONSE='U1' THEN TOTAL_AVAILABLE_TAX "
				+ "             WHEN USER_RESPONSE='U2' THEN PR_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='R1U2' THEN PR_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='A1U1' THEN TOTAL_AVAILABLE_TAX "
				+ "             WHEN USER_RESPONSE='A1U2' THEN PR_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='A4' THEN A2_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='No Action(Addl ANX-2)'  THEN A2_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='No Action(Addl PR)'  THEN PR_TOTAL_TAX "
				+ "             WHEN USER_RESPONSE='No Action(Reconciled)'  THEN PR_TOTAL_TAX "
				+ "             END AS \"TAX_AMOUNT\" " + "              "
				+ " FROM " + "( " + "SELECT " + " "
				+ "     CASE WHEN (LK.USER_RESPONSE IS NULL OR LK.USER_RESPONSE='') AND CURRENT_REPORT_TYPE='Addition in ANX-2' THEN 'No Action(Addl ANX-2)' "
				+ "          WHEN (LK.USER_RESPONSE IS NULL OR LK.USER_RESPONSE='') AND CURRENT_REPORT_TYPE='Addition in PR'    THEN 'No Action(Addl PR)' "
				+ "          WHEN (LK.USER_RESPONSE IS NULL OR LK.USER_RESPONSE='') AND CURRENT_REPORT_TYPE NOT IN('Addition in PR','Addition in ANX-2') THEN 'No Action(Reconciled)' "
				+ "          ELSE LK.USER_RESPONSE END  AS USER_RESPONSE "
				+ "   , DH.AN_TAX_DOC_TYPE AS TABLE_TYPE "
				+ "   , CASE WHEN LK.A2_TABLE='GETANX2_DE_HEADER' THEN 'DXP' "
				+ "          WHEN LK.A2_TABLE='GETANX2_B2B_HEADER' THEN 'B2B' "
				+ "          WHEN LK.A2_TABLE='GETANX2_SEZWOP_HEADER' THEN 'SEZWOP' "
				+ "          WHEN LK.A2_TABLE='GETANX2_SEZWP_HEADER' THEN 'SEZWP' "
				+ "          ELSE LK.A2_TABLE END AS A2_TABLE "
				+ "   , CASE WHEN LK.PR_DOC_TYPE IS NULL THEN LK.A2_DOC_TYPE "
				+ "     ELSE LK.PR_DOC_TYPE END DOC_TYPE "
				+ "   , LK.CURRENT_REPORT_TYPE "
				+ "   , SUM(IFNULL(A2_CGST,0)+IFNULL(A2_SGST,0)+IFNULL(A2_IGST,0)+IFNULL(A2_CESS,0)) AS A2_TOTAL_TAX "
				+ "   , SUM(IFNULL(PR_CGST,0)+IFNULL(PR_SGST,0)+IFNULL(PR_IGST,0)+IFNULL(PR_CESS,0)) AS PR_TOTAL_TAX "
				+ "   , SUM(IFNULL(DH.AVAILABLE_IGST,0)+IFNULL(DH.AVAILABLE_CGST,0) "
				+ "        +IFNULL(DH.AVAILABLE_SGST,0)+IFNULL(DH.AVAILABLE_CESS,0)) AS TOTAL_AVAILABLE_TAX "
				+ " " + "FROM \"CLIENT1_GST\".\"LINK_A2_PR\" LK "
				+ "      LEFT OUTER JOIN \"CLIENT1_GST\".\"ANX_INWARD_DOC_HEADER\" DH ON LK.PR_ID=DH.ID "
				+ "      AND LK.PR_TABLE='ANX_INWARD_DOC_HEADER' "
				+ "      AND DH.IS_DELETE=FALSE " + "       " + " " + " "
				+ "      WHERE  LK.IS_ACTIVE=TRUE AND LK.IS_DELETED=FALSE "
				+ "      AND (LK.PR_RECIPIENT_GSTIN=:gstin OR LK.A2_RECIPIENT_GSTIN=:gstin) "
				+ "      AND LK.TAX_PERIOD=:taxPeriod " + "       "
				+ "      GROUP BY LK.USER_RESPONSE,DH.AN_TAX_DOC_TYPE,LK.A2_TABLE,LK.PR_DOC_TYPE,LK.A2_DOC_TYPE,LK.CURRENT_REPORT_TYPE "
				+ "      )A " + "      )B " + "      WHERE  1=1 "
				+ 		 condition3.toString()     
				+        condition2.toString()
				+        condition1.toString()
				+ "      GROUP BY USER_RESPONSE,TABLE_TYPE,DOC_TYPE "
				+ "      ORDER BY USER_RESPONSE,TABLE_TYPE,DOC_TYPE;";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("generating query string %s ", query);
			LOGGER.debug(str);
		}
		return query;
	}
}
