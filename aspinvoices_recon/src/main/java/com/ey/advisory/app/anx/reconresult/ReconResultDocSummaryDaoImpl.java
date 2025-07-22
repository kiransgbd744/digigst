/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Nikhil.Duseja
 *
 */

@Slf4j
@Repository("ReconResultDocSummaryDaoImpl")
public class ReconResultDocSummaryDaoImpl implements ReconResultDocSummaryDao {
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ReconResultDocSummaryRespDto> findReconResultDocSummaryDetails(
			ReconResultDocSummaryReqDto reqDto,
			String validQuery) {
		
		String queryString = createQueryString(validQuery);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for ReconResultDocSummary"
							+ "on FilterConditions Provided By User : %s",
					queryString);
			LOGGER.debug(str);
		}
		
		List<String> gstin = reqDto.getGstin();

	Query q = entityManager.createNativeQuery(queryString);

	if (!CollectionUtils.isEmpty(gstin)) {
		q.setParameter("gstin", gstin);
	}
	
	if (!CollectionUtils.isEmpty(reqDto.getReportType())) {
		q.setParameter("reportType", reqDto.getReportType());
	}
	
	if(!CollectionUtils.isEmpty(reqDto.getTableType())) {
		q.setParameter("tableType", reqDto.getTableType());
	}
	
	if(!CollectionUtils.isEmpty(reqDto.getDocType())) {
		q.setParameter("docType", reqDto.getDocType());
	}
	
	q.setParameter("derivedRetPer", reqDto.getTaxPeriod());

	@SuppressWarnings("unchecked")
	List<Object[]> list = q.getResultList();
	List<ReconResultDocSummaryRespDto> retList = list.parallelStream()
			.map(o -> convert(o))
			.collect(Collectors.toCollection(ArrayList::new));

	return retList;
}

private ReconResultDocSummaryRespDto convert(Object[] arr) {

	if (LOGGER.isDebugEnabled()) {
		String str = "Converting generic object to"
				+ " ReconResultDocSummaryRespDto object";
		LOGGER.debug(str);
	}
	ReconResultDocSummaryRespDto obj = new ReconResultDocSummaryRespDto();
	obj.setReportType((arr[18] != null) ? (String) arr[18] : " ");
	if(obj.getReportType() != null && 
			obj.getReportType().trim().equalsIgnoreCase("Addition in ANX-2")) {
		obj.setRecipientGstin((arr[0] != null) ? (String) arr[0] : " ");
		obj.setSupplierGstin((arr[38] != null) ? (String) arr[38] : " ");
	} else {
		obj.setRecipientGstin((arr[1] != null) ? (String) arr[1] : " ");
		obj.setSupplierGstin((arr[37] != null) ? (String) arr[37] : " ");
	}
	obj.setPrdocType((arr[3] != null) ? (String) arr[3] : " ");
	obj.setAnx2docType((arr[2] != null) ? (String) arr[2] : " ");
	obj.setAnx2docNo((arr[23] != null) ? (String) arr[23] : " ");
	obj.setPrdocNo((arr[22] != null) ? (String) arr[22] : " ");
	String prDate = ((((Date)arr[5])!= null ) ? ((Date)arr[5]).toString() : " ") ;
	obj.setPrdocdate(prDate);
	String anxDate =  ((((Date)arr[4])!= null ) ? ((Date)arr[4]).toString() : " ") ;
	obj.setAnx2docdate(anxDate);
	obj.setPrpos((arr[17] != null) ? (String) arr[17] : " ");
	obj.setAnx2pos((arr[16] != null) ? (String) arr[16] : " ");
	obj.setPrTaxableValue((arr[7] != null) ? (BigDecimal) arr[7] : BigDecimal.ZERO);
	obj.setAnx2TaxableValue((arr[6] != null) ? (BigDecimal) arr[6] : BigDecimal.ZERO);
	obj.setPrTotalTax((arr[34] != null) ? (BigDecimal) arr[34] : BigDecimal.ZERO);
    obj.setAnx2TotalTax((arr[33] != null) ? (BigDecimal) arr[33] : BigDecimal.ZERO);
	obj.setPrigst((arr[13] != null) ? (BigDecimal) arr[13] : BigDecimal.ZERO);
	obj.setAnx2igst((arr[12] != null) ? (BigDecimal) arr[12] : BigDecimal.ZERO);
	obj.setPrcgst((arr[9] != null) ? (BigDecimal) arr[9] : BigDecimal.ZERO);
	obj.setAnx2cgst((arr[8] != null) ? (BigDecimal) arr[8] : BigDecimal.ZERO);
	obj.setPrsgst((arr[11] != null) ? (BigDecimal) arr[11] : BigDecimal.ZERO);
	obj.setAnx2sgst((arr[10] != null) ? (BigDecimal) arr[10] : BigDecimal.ZERO);
	obj.setPrcess((arr[15] != null) ? (BigDecimal) arr[15] : BigDecimal.ZERO);
	obj.setAnx2cess((arr[14] != null) ? (BigDecimal) arr[14] : BigDecimal.ZERO);
	obj.setPrAvailableIgst((arr[26] != null) ? (BigDecimal) arr[26] : BigDecimal.ZERO);
	obj.setPrAvailableCgst((arr[27] != null) ? (BigDecimal) arr[27] : BigDecimal.ZERO);
	obj.setPrAvailableSgst((arr[28] != null) ? (BigDecimal) arr[28] : BigDecimal.ZERO);
	obj.setPrAvailableCess((arr[29] != null) ? (BigDecimal) arr[29] : BigDecimal.ZERO);
	obj.setRetImpactA10Igst(BigDecimal.ZERO);
	obj.setRetImpactA10Cgst(BigDecimal.ZERO);
	obj.setRetImpactA10Sgst(BigDecimal.ZERO);
    obj.setRetImpactA10Cess(BigDecimal.ZERO);
    obj.setRetImpactA11Igst(BigDecimal.ZERO);
	obj.setRetImpactA11Cgst(BigDecimal.ZERO);
	obj.setRetImpactA11Sgst(BigDecimal.ZERO);
	obj.setRetImpactA11Cess(BigDecimal.ZERO);
	obj.setRetImpactB2Igst(BigDecimal.ZERO);
	obj.setRetImpactB2Cgst(BigDecimal.ZERO);
	obj.setRetImpactB2Sgst(BigDecimal.ZERO);
	obj.setRetImpactB2Cess(BigDecimal.ZERO);
	obj.setRetImpactB3Igst(BigDecimal.ZERO);
	obj.setRetImpactB3Cgst(BigDecimal.ZERO);
	obj.setRetImpactB3Sgst(BigDecimal.ZERO);
	obj.setRetImpactB3Cess(BigDecimal.ZERO);
	obj.setUserAction((arr[20] != null) ? (String) arr[20] : " ");
	obj.setReconLinkId((arr[36] != null) ? GenUtil.getBigInteger(arr[36]) : BigInteger.ZERO);
	obj.setPrKey((arr[24] != null) ? (String) arr[24] : " ");
	obj.setA2Key((arr[25] != null) ? (String) arr[25] : " ");
	obj.setIsAction((arr[32] != null) ? (Integer) arr[32] : 0);
	obj.setSuggestedResponse((arr[21] != null) ? (String) arr[21] : " ");
	obj.setPrPreviousResponse((arr[31] != null) ? (String) arr[31] : " ");
	obj.setAnx2pPreviousResponse((arr[30] != null) ? (String) arr[30] : " ");
	obj.setReturnPeriod((arr[35] != null) ? (String) arr[35] : " ");
	return obj;
}


public String createQueryString(String validQuery) {
  return "SELECT "
		  + "       LT.a2_recipient_gstin, "
		  + "       LT.pr_recipient_gstin, "
		  + "       LT.a2_doc_type, "
		  + "       LT.pr_doc_type, "
		  + "       LT.a2_doc_date, "
		  + "       LT.pr_doc_date, "
		  + "       LT.a2_taxable_value, "
		  + "       LT.pr_taxable_value, "
		  + "       LT.a2_cgst, "
		  + "       LT.pr_cgst, "
		  + "       LT.a2_sgst, "
		  + "       LT.pr_sgst, "
		  + "       LT.a2_igst, "
		  + "       LT.pr_igst, "
		  + "       LT.a2_cess, "
		  + "       LT.pr_cess, "
		  + "       LT.a2_pos, "
		  + "       LT.pr_pos, "
		  + "       LT.current_report, "
		  + "       LT.pr_doc_num               AS PR_DNUM, "
		  + "       LT.user_response, "
		  + "       CASE "
		  + "         WHEN LT.suggested_response = "
		  + "       'Accept ANX2 and Claim ITC as per PR' "
		  + "		THEN "
		  + "         'Accept A2 & ITC PR Tax' "
		  + "         WHEN LT.suggested_response = "
		  + "			'Pending' THEN 'Pending ANX2' "
		  + "         ELSE LT.suggested_response "
		  + "       END                         AS SUGGESTED_RESPONSE, "
		  + "       LT.pr_doc_num, "
		  + "       LT.a2_doc_num, "
		  + "       LT.pr_invoice_key, "
		  + "       LT.a2_invoice_key, "
		  + "       HD.available_igst           AS A_IGST, "
		  + "       HD.available_cgst           AS A_CGST, "
		  + "       HD.available_sgst           AS A_SGST, "
		  + "       HD.available_cess           AS A_CESS, "
		  + "       pre_user_response_a2        AS PREVIOUSE_RESPONSE_A2, "
		  + "       pre_user_response_pr        AS PREVIOUSE_RESPONSE_PR, "
		  + "       is_information_report, "
		  + "       Ifnull (LT.a2_cgst, 0.00) "
		  + "       + Ifnull (LT.a2_sgst, 0.00) "
		  + "       + Ifnull (LT.a2_igst, 0.00) "
		  + "       + Ifnull (LT.a2_cess, 0.00) A2_TOTAL_TAX, "
		  + "       Ifnull (LT.pr_cgst, 0.00) "
		  + "       + Ifnull (LT.pr_sgst, 0.00) "
		  + "       + Ifnull (LT.pr_igst, 0.00) "
		  + "       + Ifnull (LT.pr_cess, 0.00) PR_TOTAL_TAX, "
		  + "       LT.tax_period, "
		  + "       LT.recon_link_id, "
		  + "       LT.pr_supplier_gstin, "
		  + "       LT.a2_supplier_gstin, "
		  + "       LT.pre_report_a2, "
		  + "       LT.pre_report_pr "
		  + "FROM   (SELECT A.* "
		  + "        FROM   (SELECT pr_doc_num, "
		  + "                       a2_doc_num, "
		  + "                       a2_recipient_gstin, "
		  + "                       pr_recipient_gstin, "
		  + "                       pr_supplier_gstin, "
		  + "                       a2_supplier_gstin, "
		  + "                       bucket_type                            AS "
		  + "                       CURRENT_REPORT, "
		  + "                       matching_score, "
		  + "                       a2_doc_type, "
		  + "                       a2_gstn_saved_response, "
		  + "                       pr_doc_type, "
		  + "                       a2_doc_date, "
		  + "                       pr_doc_date, "
		  + "                       a2_taxable_value, "
		  + "                       pr_taxable_value, "
		  + "                       a2_cgst, "
		  + "                       pr_cgst, "
		  + "                       a2_sgst, "
		  + "                       pr_sgst, "
		  + "                       a2_igst, "
		  + "                       pr_igst, "
		  + "                       a2_cess, "
		  + "                       pr_cess, "
		  + "                       a2_pos, "
		  + "                       pr_pos, "
		  + "                       tax_period, "
		  + "                       a2_invoice_key, "
		  + "                       pr_invoice_key, "
		  + "                       user_response, "
		  + "                       suggested_response, "
		  + "                       current_mismatch_reason, "
		  + "                       forced_match_response, "
		  + "                       recon_report_config_id, "
		  + "                       addition_a2_pr_response_tax_period, "
		  + "                       bucket_type, "
		  + "                       pre_a2_report_type                      AS "
		  + "                       PRE_REPORT_A2, "
		  + "                       pre_pr_report_type                     	AS "
		  + "                       PRE_REPORT_PR, "
		  + "                       pre_a2_user_response                    AS "
		  + "                       PRE_USER_RESPONSE_A2, "
		  + "                       pre_pr_user_response                    AS "
		  + "                       PRE_USER_RESPONSE_PR, "
		  + "                       is_information_report, "
		  + "                       a2_table, "
		  + "                       pr_table, "
		  + "                       CASE "
		  + "                 WHEN Substring (a2_table, 9, 2) = 'DE' THEN 'DE' "
		  + "               WHEN Substring (a2_table, 9, 3) = 'B2B' THEN 'B2B' "
		  + "                  WHEN Substring (a2_table, 9, 5) = 'SEZWOP' THEN "
		  + "                         'SEZWOP' "
		  + "           WHEN Substring (a2_table, 9, 4) = 'SEZWP' THEN 'SEZWP' "
		  + "                       END                                     AS "
		  + "                       A2_TABLE_TYPE, "
		  + "                       recon_link_id, "
		  + "                       Rank() "
		  + "                         OVER ( "
		  + "                partition BY Ifnull(a2_invoice_key, 'A'), Ifnull( "
		  + "                         pr_invoice_key, 'A' "
		  + "                         ) "
		  + "                  ORDER BY recon_report_config_id DESC ) AS Rank, "
		  + "                       CASE "
		  + "                        WHEN pr_doc_type IS NULL THEN a2_doc_type "
		  + "                         ELSE pr_doc_type "
		  + "                       END                                     AS "
		  + "                       LK_DOCUMENT_TYPE "
		  + "                FROM   link_a2_pr "
		  + "				 WHERE IS_ACTIVE=TRUE AND IS_DELETED=FALSE " 
		  + "             AND  ( a2_recipient_gstin IN(SELECT DISTINCT gstin "
		  + "                                               FROM "
		  + "                         recon_report_gstin_details "
		  + "                                         WHERE  is_active = true) "
		  + "                          OR pr_recipient_gstin IN"
		  + "					(SELECT DISTINCT gstin "
		  + "                                                   FROM "
		  + "                          recon_report_gstin_details "
		  + "                                    WHERE  is_active = true) )) A "
		  + "        WHERE  A.rank = 1) LT "
		  + "       LEFT OUTER JOIN (SELECT itc_entitlement, "
		  + "                               cust_supp_name, "
		  + "                               doc_amt, "
		  + "                               available_igst, "
		  + "                               cust_gstin, "
		  + "                               available_cgst, "
		  + "                               available_sgst, "
		  + "                               available_cess, "
		  + "                               an_tax_doc_type, "
		  + "                               section7_of_igst_flag, "
		  + "                               autopopulate_to_refund, "
		  + "                               diff_percent, "
		  + "                               original_doc_type, "
		  + "                               original_doc_num, "
		  + "                               original_doc_date, "
		  + "                               user_id, "
		  + "                               source_filename, "
		  + "                               profit_centre, "
		  + "                               plant_code, "
		  + "                               division, "
		  + "                               location, "
		  + "                               purchase_organization, "
		  + "                               useraccess1, "
		  + "                               useraccess2, "
		  + "                               useraccess3, "
		  + "                               useraccess4, "
		  + "                               useraccess5, "
		  + "                               useraccess6, "
		  + "                               supply_type, "
		  + "                               crdr_pre_gst, "
		  + "                               cust_supp_type, "
		  + "                               cust_supp_code, "
		  + "                               cust_supp_address1, "
		  + "                               cust_supp_address2, "
		  + "                               cust_supp_address3, "
		  + "                               cust_supp_address4, "
		  + "                               state_applying_cess, "
		  + "                               ship_port_code, "
		  + "                               id, "
		  + "                               bill_of_entry, "
		  + "                               bill_of_entry_date, "
		  + "                               eway_bill_num, "
		  + "                               eway_bill_date, "
		  + "                               doc_date, "
		  + "                               supplier_gstin, "
		  + "                               doc_type, "
		  + "                               doc_num, "
		  + "                               orig_supplier_gstin "
		  + "                        FROM   anx_inward_doc_header "
		  + "                        WHERE  is_processed = true "
		  + "                               AND is_delete = false "
		  + "                               AND an_return_type = 'ANX2' "
		  + "                               AND an_tax_doc_type IN "
		  + "							( 'B2B', 'DXP', 'SEZWP', "
		  + "                                                        'SEZWOP' "
		  + "                                                      )) HD "
		  + "                    ON LT.pr_doc_date = HD.doc_date "
		  + "                     AND LT.pr_supplier_gstin = HD.supplier_gstin "
		  + "                       AND LT.pr_recipient_gstin = HD.cust_gstin "
		  + "                       AND LT.pr_doc_type = HD.doc_type "
		  + "                       AND LT.pr_doc_num = HD.doc_num "
		  + "       LEFT OUTER JOIN (SELECT id, "
		  + "                               itcentitlement, "
		  + "                               doc_amt, "
		  + "                               cfs, "
		  + "                               supplier_inv_num, "
		  + "                               supplier_inv_date, "
		  + "                               inv_type, "
		  + "                               sgstin, "
		  + "                               cgstin, "
		  + "                               section7_igst, "
		  + "                               diff_percent, "
		  + "                               'B2B' AS TABLE_TYPE, "
		  + "                               supplier_inv_val "
		  + "                        FROM   (SELECT id, "
		  + "                              itc_ent          AS ITCEntitlement, "
		  + "                                     supplier_inv_val AS DOC_AMT, "
		  + "                                       cfs              AS CFS, "
		  + "                                       supplier_inv_num, "
		  + "                                       supplier_inv_date, "
		  + "                                       inv_type, "
		  + "                                       sgstin, "
		  + "                                       cgstin, "
		  + "                               sec_7_act        AS Section7_IGST, "
		  + "                                diff_percent     AS DIFF_PERCENT, "
		  + "                                  'B2B'            AS TABLE_TYPE, "
		  + "                                       supplier_inv_val "
		  + "                            FROM   getanx2_b2b_header "
		  + "                                WHERE  is_delete = false) "
		  + "                        UNION ALL "
		  + "                        (SELECT id, "
		  + "                              itc_ent          AS ITCEntitlement, "
		  + "                                supplier_inv_val AS DOC_AMT, "
		  + "                                cfs              AS CFS, "
		  + "                                supplier_inv_num, "
		  + "                                supplier_inv_date, "
		  + "                                inv_type, "
		  + "                                sgstin, "
		  + "                                cgstin, "
		  + "                               sec_7_act        AS Section7_IGST, "
		  + "                                diff_percent     AS DIFF_PERCENT, "
		  + "                                'DE'             AS TABLE_TYPE, "
		  + "                                supplier_inv_val "
		  + "                         FROM   getanx2_de_header "
		  + "                         WHERE  is_delete = false) "
		  + "                        UNION ALL "
		  + "                        (SELECT id, "
		  + "                                itc_ent                        AS "
		  + "                                ITCEntitlement, "
		  + "                                supplier_inv_val               AS "
		  + "                                DOC_AMT, "
		  + "                                cfs                        	AS "
		  + "                                CFS, "
		  + "                                supplier_inv_num, "
		  + "                                supplier_inv_date, "
		  + "                                inv_type, "
		  + "                                sgstin, "
		  + "                                cgstin, "
		  + "                                ''                        		AS "
		  + "                                Section7_IGST, "
		  + "                                diff_percent               	AS "
		  + "                                DIFF_PERCENT, "
		  + "                                '  "
		  + "                                    SEZWP' AS "
		  + "                                TABLE_TYPE, "
		  + "                                supplier_inv_val "
		  + "                         FROM   getanx2_sezwp_header "
		  + "                         WHERE  is_delete = false) "
		  + "                        UNION ALL "
		  + "                        (SELECT id, "
		  + "                              itc_ent          AS ITCEntitlement, "
		  + "                                supplier_inv_val AS DOC_AMT, "
		  + "                                cfs              AS CFS, "
		  + "                                supplier_inv_num, "
		  + "                                supplier_inv_date, "
		  + "                                inv_type, "
		  + "                                sgstin, "
		  + "                                cgstin, "
		  + "                               ''               AS Section7_IGST, "
		  + "                                0                AS DIFF_PERCENT, "
		  + "                                'SEZWP'          AS TABLE_TYPE, "
		  + "                                supplier_inv_val "
		  + "                         FROM   getanx2_sezwop_header "
		  + "                         WHERE  is_delete = false)) A2 "
		  + "                    ON LT.a2_doc_date = A2.supplier_inv_date "
		  + "                       AND LT.a2_supplier_gstin = A2.sgstin "
		  + "                       AND LT.a2_recipient_gstin = A2.cgstin "
		  + "                       AND LT.a2_doc_type = A2.inv_type "
		  + "                       AND LT.a2_doc_num = A2.supplier_inv_num "
		  + "	WHERE " 
		  + validQuery
		  + "ORDER  BY LT.a2_doc_num, "
		  + "          LT.pr_doc_num";

}

}