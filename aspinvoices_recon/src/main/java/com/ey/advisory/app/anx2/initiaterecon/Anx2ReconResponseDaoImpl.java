package com.ey.advisory.app.anx2.initiaterecon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Anx2ReconResponseDaoImpl")
public class Anx2ReconResponseDaoImpl implements Anx2ReconResponseDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<Anx2ReconRespResultSetDataDTO> getA2ReconData(
			List<String> gstins, String taxPeriod, List<String> tableType,
			List<String> docType, String response) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Anx2ReconResponseDaoImpl.getA2ReconData"
					+ " gstins : %s , taxPeriod : %s",gstins,taxPeriod);
			LOGGER.debug(msg);
		}

		List<Object[]> result = new ArrayList<>();
		List<Anx2ReconRespResultSetDataDTO> retList = new ArrayList<>();
		StringBuilder build = new StringBuilder();
		if (gstins != null && taxPeriod != null) {
			build.append(" AND A2_RECIPIENT_GSTIN in :gstins");
			build.append(" AND TAX_PERIOD = :taxPeriod");
		}
		if (tableType != null && !tableType.isEmpty()) {
			build.append(" AND A2_TABLE in :tableType");
		}
		if (docType != null && !docType.isEmpty()) {
			build.append("  AND (CASE WHEN PR_DOC_TYPE is null THEN A2_DOC_TYPE  ELSE PR_DOC_TYPE END) in :docType ");
			 
		}

		String sql = createA2Query(build.toString());
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Anx2ReconResponseDaoImpl.getA2ReconData"
					+ " sql : %s",sql);
			LOGGER.debug(msg);
		}
		if (sql != null && sql.trim().length() > 0) {
			try {
				Query query = entityManager.createNativeQuery(sql);
				if (gstins != null && taxPeriod != null) {
					query.setParameter("gstins", gstins);
					query.setParameter("taxPeriod", taxPeriod);
				}
				if (tableType != null && !tableType.isEmpty()) {
					query.setParameter("tableType", tableType);
				}
				if (docType != null && !docType.isEmpty()) {
					query.setParameter("docType", docType);
				}
				result = query.getResultList();
				retList = result.parallelStream().map(o -> convert(o))
						.collect(Collectors.toCollection(ArrayList::new));
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Anx2ReconResponseDaoImpl.getA2ReconData"
							+ " retList : %s",retList);
					LOGGER.debug(msg);
				}
			} catch (Exception e) {
				LOGGER.error(
						"Erorr While Executing the Query for getA2ReconData  {}",
						e);
				e.printStackTrace();
			}
		}
		return retList;
	}

	@SuppressWarnings("unchecked")
	public List<Anx2ReconRespResultSetDataDTO> getPRReconData(
			List<String> gstins, String taxPeriod, List<String> tableType,
			List<String> docType, String response) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Anx2ReconResponseDaoImpl.getPRReconData"
					+ " gstins : %s , taxPeriod : %s",gstins,taxPeriod);
			LOGGER.debug(msg);
		}
		List<Object[]> result = new ArrayList<>();
		StringBuilder build = new StringBuilder();
		List<Anx2ReconRespResultSetDataDTO> retList = new ArrayList<>();
		if (gstins != null && taxPeriod != null) {
			build.append(" AND PR_RECIPIENT_GSTIN in :gstins");
			build.append(" AND TAX_PERIOD = :taxPeriod");
		}
		if (tableType != null && !tableType.isEmpty()) {
			build.append(" AND PR_TABLE in :tableType");
		}
		if (docType != null && !docType.isEmpty()) {
			build.append(" AND (CASE WHEN PR_DOC_TYPE is null THEN A2_DOC_TYPE  ELSE PR_DOC_TYPE END) in :docType ");
		}

		String sql = createPRQuery(build.toString());
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Anx2ReconResponseDaoImpl.getPRReconData"
					+ " sql : %s",sql);
			LOGGER.debug(msg);
		}
		if (sql != null && sql.trim().length() > 0) {
			try {
				Query query = entityManager.createNativeQuery(sql);
				if (gstins != null && taxPeriod != null) {
					query.setParameter("gstins", gstins);
					query.setParameter("taxPeriod", taxPeriod);
				}
				if (tableType != null && !tableType.isEmpty()) {
					query.setParameter("tableType", tableType);
				}
				if (docType != null && !docType.isEmpty()) {
					query.setParameter("docType", docType);
				}
				result = query.getResultList();
				retList = result.parallelStream().map(o -> convert(o))
						.collect(Collectors.toCollection(ArrayList::new));
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Anx2ReconResponseDaoImpl.getPRReconData"
							+ " retList : %s",retList);
					LOGGER.debug(msg);
				}
			} catch (Exception e) {
				LOGGER.error(
						"Erorr While Executing the Query for getPRReconData Data {}",
						e);
				e.printStackTrace();
			}
		}
		return retList;
	}

	private String createA2Query() {
		return "select A2_RECIPIENT_GSTIN,USER_RESPONSE,sum(A2_IGST + A2_CGST"
				+ " + A2_SGST + A2_CESS) as TAX_PAYABLE from \"CLIENT1_GST\"."
				+ "\"LINK_A2_PR\" where ";
	}

	private String createPRQuery() {
		return "select PR_RECIPIENT_GSTIN,USER_RESPONSE,sum(PR_IGST + PR_CGST"
				+ " + PR_SGST + PR_CESS) as TAX_PAYABLE from \"CLIENT1_GST\"."
				+ "\"LINK_A2_PR\" where ";
	}

	private String createPRAQuery() {
		return "select PR_RECIPIENT_GSTIN,USER_RESPONSE,sum(AVAILABLE_IGST + "
				+ "AVAILABLE_CGST + AVAILABLE_SGST + AVAILABLE_CESS) as TAX_PAYABLE "
				+ "FROM \"CLIENT1_GST\".\"LINK_A2_PR\" LK INNER JOIN \"CLIENT1_GST\"."
				+ "\"ANX_INWARD_DOC_HEADER\" DH ON LK.PR_ID=DH.ID AND LK.PR_TABLE="
				+ "\'ANX_INWARD_DOC_HEADER\' where ";
	}

	private Anx2ReconRespResultSetDataDTO convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Anx2ReconRespResultSetDataDTO object";
			LOGGER.debug(str);
		}
		Anx2ReconRespResultSetDataDTO obj = new Anx2ReconRespResultSetDataDTO();
		obj.setGstin((String) arr[0]);
		obj.setUserResponse((String) arr[1]);
		obj.setTaxPayable((BigDecimal) arr[2]);
		return obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Anx2ReconRespResultSetDataDTO> getPRAReconData(
			List<String> gstins, String taxPeriod, List<String> tableType,
			List<String> docType, String response) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Anx2ReconResponseDaoImpl.getPRAReconData"
					+ " gstins : %s , taxPeriod : %s",gstins,taxPeriod);
			LOGGER.debug(msg);
		}
		List<Object[]> result = new ArrayList<>();
		StringBuilder build = new StringBuilder();
		List<Anx2ReconRespResultSetDataDTO> retList = new ArrayList<>();
		if (gstins != null && taxPeriod != null) {
			build.append(" AND PR_RECIPIENT_GSTIN in :gstins");
			build.append(" AND TAX_PERIOD = :taxPeriod");
		}
		if (tableType != null && !tableType.isEmpty()) {
			build.append(" AND PR_TABLE in :tableType");
		}
		if (docType != null && !docType.isEmpty()) {
			build.append("  AND (CASE WHEN PR_DOC_TYPE is null THEN A2_DOC_TYPE  ELSE PR_DOC_TYPE END) in :docType ");
		}

		String sql = createPRAQuery(build.toString());
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Anx2ReconResponseDaoImpl.getPRAReconData"
					+ " sql : %s",sql);
			LOGGER.debug(msg);
		}
		if (sql != null && sql.trim().length() > 0) {
			try {
				Query query = entityManager.createNativeQuery(sql);
				if (gstins != null && taxPeriod != null) {
					query.setParameter("gstins", gstins);
					query.setParameter("taxPeriod", taxPeriod);
				}
				if (tableType != null && !tableType.isEmpty()) {
					query.setParameter("tableType", tableType);
				}
				if (docType != null && !docType.isEmpty()) {
					query.setParameter("docType", docType);
				}
				result = query.getResultList();
				retList = result.parallelStream().map(o -> convert(o))
						.collect(Collectors.toCollection(ArrayList::new));
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Anx2ReconResponseDaoImpl.getPRAReconData"
							+ " retList : %s",retList);
					LOGGER.debug(msg);
				}
			} catch (Exception e) {
				LOGGER.error(
						"Erorr While Executing the Query for getPRAReconData Data {}",
						e);
				e.printStackTrace();
			}
		}
		return retList;
	}
	
	private String createPRAQuery(String queryCondition) {
		return  ""
		+ "SELECT (CASE WHEN A.pr_recipient_gstin IS NULL THEN B.pr_recipient_gstin ELSE A.pr_recipient_gstin END) AS RECIPIENT_GSTIN, "
		+ "		  (CASE WHEN A.user_response IS NULL THEN B.user_response ELSE A.user_response END) AS USER_RESPONSE, "
		+ "       Ifnull(B.sum_plus, 0) - Ifnull(A.sum_minus, 0) AS TAX_PAYABLE "
		+ "FROM   (SELECT LK.pr_recipient_gstin, "
		+ "               LK.user_response, "
		+ "               Sum(Ifnull(available_igst, 0) "
		+ "                   + Ifnull(available_cgst, 0) "
		+ "                   + Ifnull(available_sgst, 0) "
		+ "                   + Ifnull(available_cess, 0)) AS SUM_MINUS "
		+ "        FROM   link_a2_pr LK "
		+ "               INNER JOIN anx_inward_doc_header DH "
		+ "                       ON LK.pr_id = DH.id "
		+ "                          AND LK.pr_table = 'ANX_INWARD_DOC_HEADER' "
		+ "        WHERE  ( (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END = 'CR') "
		            +"      OR (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END = 'RCR')) "
		+ "       AND is_deleted = false and is_active = true "
		+ "               AND LK.user_response IS NOT NULL " +queryCondition
		+ "        GROUP  BY LK.pr_recipient_gstin, "
		+ "                  LK.user_response "
		+ "        UNION ALL "
		+ "        SELECT LK.pr_recipient_gstin, "
		+ "               ( CASE "
		+ "                   WHEN LK.bucket_type = 'Addition in ANX-2' THEN 'naA2' "
		+ "                   WHEN bucket_type = 'Addition in PR' THEN 'naPr' "
		+ "                   ELSE 'naRc' "
		+ "                 END )                          AS USER_RESPONSE, "
		+ "               Sum(Ifnull(available_igst, 0) "
		+ "                   + Ifnull(available_cgst, 0) "
		+ "                   + Ifnull(available_sgst, 0) "
		+ "                   + Ifnull(available_cess, 0)) AS SUM_MINUS "
		+ "        FROM   link_a2_pr LK "
		+ "               INNER JOIN anx_inward_doc_header DH "
		+ "                       ON LK.pr_id = DH.id "
		+ "                          AND LK.pr_table = 'ANX_INWARD_DOC_HEADER' "
		+ "        WHERE  ( (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END = 'CR') "
		            +"      OR (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END = 'RCR') ) "
		+ "       AND is_deleted = false and is_active = true "
		+ "               AND LK.user_response IS NULL " +queryCondition
		+ "        GROUP  BY LK.pr_recipient_gstin, "
		+ "                  CASE "
		+ "                    WHEN LK.bucket_type = 'Addition in ANX-2' THEN 'naA2' "
		+ "                    WHEN LK.bucket_type = 'Addition in PR' THEN 'naPr' "
		+ "                    ELSE 'naRc' "
		+ "                  END) A "
		+ "       FULL OUTER JOIN (SELECT LK.pr_recipient_gstin, "
		+ "                               LK.user_response, "
		+ "                               Sum(Ifnull(available_igst, 0) "
		+ "                                   + Ifnull(available_cgst, 0) "
		+ "                                   + Ifnull(available_sgst, 0) "
		+ "                                   + Ifnull(available_cess, 0)) AS SUM_PLUS "
		+ "                        FROM  link_a2_pr LK "
		+ "                               INNER JOIN  anx_inward_doc_header DH "
		+ "                                       ON LK.pr_id = DH.id "
		+ "                                          AND "
		+ "                               LK.pr_table = 'ANX_INWARD_DOC_HEADER' "
		+ "                        WHERE  ( (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END != 'CR') "
		            +"      AND (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END != 'RCR') ) "
		+ "       AND is_deleted = false and is_active = true "
		+ "                               AND LK.user_response IS NOT NULL " +queryCondition
		+ "                        GROUP  BY LK.pr_recipient_gstin, "
		+ "                                  LK.user_response "
		+ "                        UNION ALL "
		+ "                        SELECT LK.pr_recipient_gstin, "
		+ "                               ( CASE "
		+ "                                   WHEN LK.bucket_type = 'Addition in ANX-2' "
		+ "                                 THEN "
		+ "                                   'naA2' "
		+ "                                   WHEN bucket_type = 'Addition in PR' THEN "
		+ "                                   'naPr' "
		+ "                                   ELSE 'naRc' "
		+ "                                 END )                          AS USER_RESPONSE "
		+ "                               , "
		+ "       Sum(Ifnull(available_igst, 0) "
		+ "           + Ifnull(available_cgst, 0) "
		+ "           + Ifnull(available_sgst, 0) "
		+ "           + Ifnull(available_cess, 0)) AS SUM_PLUS "
		+ "                        FROM   link_a2_pr LK "
		+ "                               INNER JOIN  anx_inward_doc_header DH "
		+ "                                       ON LK.pr_id = DH.id "
		+ "                                          AND "
		+ "                               LK.pr_table = 'ANX_INWARD_DOC_HEADER' "
		+ "                        WHERE  (  (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END != 'CR') "
		            +"      AND (CASE WHEN LK.pr_doc_type is null THEN LK.a2_doc_type  ELSE LK.pr_doc_type END != 'RCR')) "
		+ "       AND is_deleted = false and is_active = true "
		+ "                               AND LK.user_response IS NULL " + queryCondition
		+ "                        GROUP  BY LK.pr_recipient_gstin, "
		+ "                                  CASE "
		+ "                                    WHEN LK.bucket_type = 'Addition in ANX-2' "
		+ "                        THEN "
		+ "                                    'naA2' "
		+ "                                    WHEN LK.bucket_type = 'Addition in PR' THEN "
		+ "                                    'naPr' "
		+ "                                    ELSE 'naRc' "
		+ "                                  END) B "
		+ "                    ON A.pr_recipient_gstin = B.pr_recipient_gstin "
		+ "                       AND A.user_response = B.user_response;";
	}
	
	private String createPRQuery(String queryCondition) {
		return ""
				+ "SELECT (CASE WHEN A.pr_recipient_gstin IS NULL THEN B.pr_recipient_gstin ELSE A.pr_recipient_gstin END) AS RECIPIENT_GSTIN, "
				+ "		  (CASE WHEN A.user_response IS NULL THEN B.user_response ELSE A.user_response END) AS USER_RESPONSE, "
				+ "       Ifnull(B.sum_plus, 0) - Ifnull(A.sum_minus, 0) AS TAX_PAYABLE "
				+ "FROM   (SELECT pr_recipient_gstin, "
				+ "               user_response, "
				+ "               Ifnull(Sum(Ifnull(pr_igst, 0) + Ifnull(pr_cgst, 0) "
				+ "                          + Ifnull(pr_sgst, 0) + Ifnull(pr_cess, 0)), 0) AS "
				+ "               SUM_MINUS "
				+ "        FROM  link_a2_pr "
				+ "        WHERE  (  (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'CR') "
		            +"      OR (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'RCR')) "
				+ "               AND  user_response IS NOT NULL "
				+ "       AND is_deleted = false and is_active = true " + queryCondition
				+ "        GROUP  BY pr_recipient_gstin, "
				+ "                  user_response "
				+ "        UNION "
				+ "        SELECT pr_recipient_gstin, "
				+ "               ( CASE "
				+ "                   WHEN bucket_type = 'Addition in ANX-2' THEN 'naA2' "
				+ "                   WHEN bucket_type = 'Addition in PR' THEN 'naPr' "
				+ "                   ELSE 'naRc' "
				+ "                 END )                                                   AS "
				+ "               USER_RESPONSE, "
				+ "               Ifnull(Sum(Ifnull(pr_igst, 0) + Ifnull(pr_cgst, 0) "
				+ "                          + Ifnull(pr_sgst, 0) + Ifnull(pr_cess, 0)), 0) AS "
				+ "               SUM_MINUS "
				+ "        FROM  link_a2_pr "
				+ "        WHERE  ( (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'CR') "
		            +"      OR (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'RCR')) "
				+ "       AND is_deleted = false and is_active = true "
				+ "               AND user_response IS NULL "  + queryCondition
				+ "        GROUP  BY pr_recipient_gstin, "
				+ "                  CASE "
				+ "                    WHEN bucket_type = 'Addition in ANX-2' THEN 'naA2' "
				+ "                    WHEN bucket_type = 'Addition in PR' THEN 'naPr' "
				+ "                    ELSE 'naRc' "
				+ "                  END) A "
				+ "       FULL OUTER JOIN (SELECT pr_recipient_gstin, "
				+ "                               user_response, "
				+ "                               Sum(Ifnull(pr_igst, 0) + Ifnull(pr_cgst, 0) "
				+ "                                   + Ifnull(pr_sgst, 0) + Ifnull(pr_cess, 0)) AS "
				+ "                                                 SUM_PLUS "
				+ "                        FROM  link_a2_pr "
				+ "                        WHERE  ( (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'CR') "
		            +"      AND (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'RCR')) "
				+ "       AND is_deleted = false and is_active = true "
				+ "                               AND user_response IS NOT NULL " +queryCondition
				+ "                        GROUP  BY pr_recipient_gstin, "
				+ "                                  user_response "
				+ "                        UNION "
				+ "                        SELECT pr_recipient_gstin, "
				+ "                               ( CASE "
				+ "                                   WHEN bucket_type = 'Addition in ANX-2' THEN "
				+ "                                   'naA2' "
				+ "                                   WHEN bucket_type = 'Addition in PR' THEN "
				+ "                                   'naPr' "
				+ "                                   ELSE 'naRc' "
				+ "                                 END )                                        AS "
				+ "                               USER_RESPONSE, "
				+ "                               Sum(Ifnull(pr_igst, 0) + Ifnull(pr_cgst, 0) "
				+ "                                   + Ifnull(pr_sgst, 0) + Ifnull(pr_cess, 0)) AS "
				+ "                               SUM_MINUS "
				+ "                        FROM   link_a2_pr "
				+ "                        WHERE  ( (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'CR') "
		            +"      AND (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'RCR') ) "
				+ "       AND is_deleted = false and is_active = true "
				+ "                               AND user_response IS NULL " + queryCondition
				+ "                        GROUP  BY pr_recipient_gstin, "
				+ "                                  CASE "
				+ "                                    WHEN bucket_type = 'Addition in ANX-2' THEN "
				+ "                                    'naA2' "
				+ "                                    WHEN bucket_type = 'Addition in PR' THEN "
				+ "                                    'naPr' "
				+ "                                    ELSE 'naRc' "
				+ "                                  END) B "
				+ "                    ON A.pr_recipient_gstin = B.pr_recipient_gstin "
				+ "                       AND A.user_response = B.user_response;";
	}

	
	
	private String createA2Query(String queryCondition) {
			return ""
					+ "SELECT (CASE WHEN A.a2_recipient_gstin IS NULL THEN B.a2_recipient_gstin ELSE A.a2_recipient_gstin END) AS RECIPIENT_GSTIN, "
					+ "		  (CASE WHEN A.user_response IS NULL THEN B.user_response ELSE A.user_response END) AS USER_RESPONSE, "	
					+ "       Ifnull(B.sum_plus, 0) - Ifnull(A.sum_minus, 0) AS TAX_PAYABLE "
					+ "FROM   (SELECT a2_recipient_gstin, "
					+ "               user_response, "
					+ "               Ifnull(Sum(Ifnull(a2_igst, 0) + Ifnull(a2_cgst, 0) "
					+ "                          + Ifnull(a2_sgst, 0) + Ifnull(a2_cess, 0)), 0) AS "
					+ "               SUM_MINUS "
					+ "        FROM   link_a2_pr "
					+ "        WHERE  ( (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'CR') "
		            +"      OR (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'RCR')) "
					+ "               AND  user_response IS NOT NULL "
					+ "       AND is_deleted = false and is_active = true "
					+ queryCondition
					+ "        GROUP  BY a2_recipient_gstin, "
					+ "                  user_response "
					+ "        UNION "
					+ "        SELECT a2_recipient_gstin, "
					+ "               ( CASE "
					+ "                   WHEN bucket_type = 'Addition in ANX-2' THEN 'naA2' "
					+ "                   WHEN bucket_type = 'Addition in PR' THEN 'naPr' "
					+ "                   ELSE 'naRc' "
					+ "                 END )                                                   AS "
					+ "               USER_RESPONSE, "
					+ "               Ifnull(Sum(Ifnull(a2_igst, 0) + Ifnull(a2_cgst, 0) "
					+ "                          + Ifnull(a2_sgst, 0) + Ifnull(a2_cess, 0)), 0) AS "
					+ "               SUM_MINUS "
					+ "        FROM  link_a2_pr "
					+ "        WHERE  ((CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'CR') "
		            + "      OR (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END = 'RCR') ) "
					+ "       AND is_deleted = false and is_active = true "
					+ "               AND user_response IS NULL "  + queryCondition
					+ "        GROUP  BY a2_recipient_gstin, "
					+ "                  CASE "
					+ "                    WHEN bucket_type = 'Addition in ANX-2' THEN 'naA2' "
					+ "                    WHEN bucket_type = 'Addition in PR' THEN 'naPr' "
					+ "                    ELSE 'naRc' "
					+ "                  END) A "
					+ "       FULL OUTER JOIN (SELECT a2_recipient_gstin, "
					+ "                               user_response, "
					+ "                               Sum(Ifnull(a2_igst, 0) + Ifnull(a2_cgst, 0) "
					+ "                                   + Ifnull(a2_sgst, 0) + Ifnull(a2_cess, 0)) AS "
					+ "                                                 SUM_PLUS "
					+ "                        FROM   link_a2_pr "
					+ "                        WHERE  ( (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'CR')"
		            + "      AND (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'RCR')) "
					+ "       AND is_deleted = false and is_active = true "
					+ "                               AND user_response IS NOT NULL " +queryCondition
					+ "                        GROUP  BY a2_recipient_gstin, "
					+ "                                  user_response "
					+ "                        UNION "
					+ "                        SELECT a2_recipient_gstin, "
					+ "                               ( CASE "
					+ "                                   WHEN bucket_type = 'Addition in ANX-2' THEN "
					+ "                                   'naA2' "
					+ "                                   WHEN bucket_type = 'Addition in PR' THEN "
					+ "                                   'naPr' "
					+ "                                   ELSE 'naRc' "
					+ "                                 END )                                        AS "
					+ "                               USER_RESPONSE, "
					+ "                               Sum(Ifnull(a2_igst, 0) + Ifnull(a2_cgst, 0) "
					+ "                                   + Ifnull(a2_sgst, 0) + Ifnull(a2_cess, 0)) AS "
					+ "                               SUM_MINUS "
					+ "                        FROM   link_a2_pr "
					+ "                        WHERE  ((CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'CR')"
		            + "    AND (CASE WHEN pr_doc_type is null THEN a2_doc_type  ELSE pr_doc_type END != 'RCR') ) "
					+ "       AND is_deleted = false and is_active = true "
					+ "                               AND user_response IS NULL " + queryCondition
					+ "                        GROUP  BY a2_recipient_gstin, "
					+ "                                  CASE "
					+ "                                    WHEN bucket_type = 'Addition in ANX-2' THEN "
					+ "                                    'naA2' "
					+ "                                    WHEN bucket_type = 'Addition in PR' THEN "
					+ "                                    'naPr' "
					+ "                                    ELSE 'naRc' "
					+ "                                  END) B "
					+ "                    ON A.a2_recipient_gstin = B.a2_recipient_gstin "
					+ "                       AND A.user_response = B.user_response;";
		}
	
}
