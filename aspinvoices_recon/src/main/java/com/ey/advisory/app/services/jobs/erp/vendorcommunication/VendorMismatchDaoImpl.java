package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Repository("VendorMismatchDaoImpl")
public class VendorMismatchDaoImpl implements VendorMismatchDao {

	@PersistenceContext(unitName = "clientDataUnit")
	EntityManager entityManager;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfo;

	List<VendorMismatchDto> childArry;

	List<Long> ids;

	@Override
	public List<VendorMismatchDto> findVendorMismatchRecords(
			RevIntegrationScenarioTriggerDto req) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Inside VendorMismatchDaoImpl, invoking"
					+ " findVendorMismatchRecords() method :req : %s",req); 
			LOGGER.debug(msg);
		}
		
		if (LOGGER.isDebugEnabled()) {
			String msg =("Invoking findEntityByEntityId() method"); 
			LOGGER.debug(msg);
		}
		EntityInfoEntity einfo = entityInfo
				.findEntityByEntityId(req.getEntityId());
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("returing findEntityByEntityId() method"
					+ " resp %s", einfo); 
			LOGGER.debug(msg);
		}
		

		String queryString = "SELECT '' AS A2_ITC_ENTITLEMENT, "
+ "       HD.itc_entitlement        AS PR_ITC_ENTITLEMENT, "
+ "       ( CASE "
+ "           WHEN a2_invoice_key IS NULL THEN NULL "
+ "           ELSE LT.pre_report_a2 "
+ "         END )                   PREVIOUSE_REPORT_A2, "
+ "       ( CASE "
+ "           WHEN pr_invoice_key IS NULL THEN NULL "
+ "           ELSE LT.pre_report_pr "
+ "         END )                   PREVIOUSE_REPORT_PR, "
+ "       LT.user_response, "
+ "       LT.suggested_response, "
+ "       LT.current_mismatch_reason, "
+ "       LT.forced_match_response, "
+ "       ''                        AS INFORMATION_REPORT_REFERENCE, "
+ "       LT.addition_a2_pr_response_tax_period, "
+ "       ( CASE "
+ "           WHEN a2_invoice_key IS NULL THEN NULL "
+ "           ELSE LT.pre_user_response_a2 "
+ "         END )                   PREVIOUSE_RESPONSE_A2, "
+ "       ( CASE "
+ "           WHEN pr_invoice_key IS NULL THEN NULL "
+ "           ELSE LT.pre_user_response_pr "
+ "         END )                   PREVIOUSE_RESPONSE_PR, "
+ "       LT.current_report, "
+ "       LT.pr_doc_num, "
+ "       LT.a2_doc_num, "
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
+ "       ''                        AS A2_SAVED_RESPONSE_REPORTTYPE, "
+ "       ''                        AS A2_DIGIGST_SAVEDRESPONSE, "
+ "       LT.a2_gstn_saved_response AS GSTN_SAVED_RESPONSE, "
+ "       LT.matching_score         AS MATCHING_SCORE, "
+ "       LT.tax_period             AS RECON_TAXPERIOD, "
+ "       ''                        AS CALENDAR_MONTH_PR, "
+ "       ''                        AS DATE_UPLOAD, "
+ "       ''                        AS ORG_MONTH_A2, "
+ "       ''                        AS ORG_MONTH_PR, "
+ "       ''                        AS A2_SUPPLIER_NAME, "
+ "       HD.cust_supp_name         AS PR_SUPPLIER_NAME, "
+ "       ''                        AS A2_TOTAL_TAX, "
+ "       ''                        AS PR_TOTAL_TAX, "
+ "       A2.supplier_inv_val       AS A2_INVOICE_VALE, "
+ "       HD.doc_amt                AS PR_INVOICE_VALE, "
+ "       HD.available_igst         AS A_IGST, "
+ "       HD.available_cgst         AS A_CGST, "
+ "       HD.available_sgst         AS A_SGST, "
+ "       HD.available_cess         AS A_CESS, "
+ "       ''                        AS A2_TABLE_TYPE, "
+ "       HD.an_tax_doc_type        AS PR_TABLE_TYPE, "
+ "       ''                        AS CFS_FLAG, "
+ "       ''                        AS A2_CLAIM_REFUND, "
+ "       ''                        AS PR_CLAIM_REFUND, "
+ "       ''                        AS A2_SECTION7_IGST, "
+ "       HD.section7_of_igst_flag  AS PR_SECTION7_IGST, "
+ "       ''                        AS A2_AUTOPOPULATED_REFUND, "
+ "       HD.autopopulate_to_refund AS PR_AUTOPOPULATED_REFUND, "
+ "       ''                        AS A2_DIFF_PERCENT, "
+ "       HD.diff_percent           AS PR_DIFF_PERCENT, "
+ "       ''                        AS A2_ORIG_DOC_TYPE, "
+ "       HD.original_doc_type      AS PR_ORIG_DOC_TYPE, "
+ "       ''                        AS A2_ORIG_DOC_NUM, "
+ "       HD.original_doc_num       AS PR_ORIG_DOC_NUM, "
+ "       ''                        AS A2_ORIG_DOC_DATE, "
+ "       HD.original_doc_date      AS PR_ORIG_DOC_DATE, "
+ "       HD.orig_supplier_gstin, "
+ "       HD.user_id                AS USER_ID, "
+ "       HD.source_filename        AS SOURCE_FLIENAME, "
+ "       HD.profit_centre          AS PROFIT_CENTRE, "
+ "       HD.plant_code             AS PLANT, "
+ "       HD.division               AS DIVISION, "
+ "       HD.location               AS LOCATION, "
+ "       HD.purchase_organization  AS PURCHASE_ORGANISATION, "
+ "       HD.useraccess1            AS USERACCESS1, "
+ "       HD.useraccess2            AS USERACCESS2, "
+ "       HD.useraccess3            AS USERACCESS3, "
+ "       HD.useraccess4            AS USERACCESS4, "
+ "       HD.useraccess5            AS USERACCESS5, "
+ "       HD.useraccess6            AS USERACCESS6, "
+ "       ''                        AS GLCODE_TAXABLE_VALUE, "
+ "       ''                        AS GL_CODE_IGST, "
+ "       ''                        AS GL_CODE_CGST, "
+ "       ''                        AS GL_CODE_SGST, "
+ "       ''                        AS GL_CODE_ADVALOREM_CESS, "
+ "       ''                        AS GL_CODE_SPECIFIC_CESS, "
+ "       ''                        AS GL_CODESTATE_CESS, "
+ "       HD.supply_type            AS SUPPLY_TYPE, "
+ "       HD.crdr_pre_gst           AS CRDRPERGST, "
+ "       HD.cust_supp_type         AS SUPPLIER_TYPE, "
+ "       HD.cust_supp_code         AS SUPPLIER_CODE, "
+ "       HD.cust_supp_address1     AS SUPPLIER_ADDERSS1, "
+ "       HD.cust_supp_address2     AS SUPPLIER_ADDERSS2, "
+ "       HD.cust_supp_address3     AS SUPPLIER_ADDERSS3, "
+ "       HD.cust_supp_address4     AS SUPPLIER_ADDERSS4, "
+ "       HD.state_applying_cess    AS STATE_APPLYING_CESS, "
+ "       HD.ship_port_code         AS PORT_CODE, "
+ "       HD.bill_of_entry          AS BILL_OF_ENTRY, "
+ "       HD.bill_of_entry_date     AS BILL_OF_ENTRY_DATE, "
+ "       ''                        AS CIF_VALUE, "
+ "       ''                        AS CUSTOM_DUTY, "
+ "       ''                        AS QUANTITY, "
+ "       ''                        AS CESS_AMT_ADVALOREM, "
+ "       ''                        AS CESS_AMT_SPECIFIC, "
+ "       ''                        AS STATE_CESS_AMOUNT, "
+ "       ''                        AS OTHER_VALUE, "
+ "       ''                        AS PURCHASE_VOUCHER_DATE, "
+ "       ''                        AS PURCHASE_VOUCHER_NUM, "
+ "       ''                        AS POSTING_DATE, "
+ "       ''                        AS PAYMENT_VOUCHER_NUM, "
+ "       ''                        AS PAYMENT_DATE, "
+ "       ''                        AS CONTRACT_NUM, "
+ "       ''                        AS CONTRACT_VAL, "
+ "       HD.eway_bill_num          AS E_WAYBILLNUMBER, "
+ "       HD.eway_bill_date         AS E_WAYBILLDATE, "
+ "       ''                        AS USERDEFINED_FIELD1, "
+ "       ''                        AS USERDEFINED_FIELD2, "
+ "       ''                        AS USERDEFINED_FIELD3, "
+ "       ''                        AS USERDEFINED_FIELD4, "
+ "       ''                        AS USERDEFINED_FIELD5, "
+ "       ''                        AS USERDEFINED_FIELD6, "
+ "       ''                        AS USERDEFINED_FIELD7, "
+ "       ''                        AS USERDEFINED_FIELD8, "
+ "       ''                        AS USERDEFINED_FIELD9, "
+ "       ''                        AS USERDEFINED_FIELD10, "
+ "       ''                        AS USERDEFINED_FIELD11, "
+ "       ''                        AS USERDEFINED_FIELD12, "
+ "       ''                        AS USERDEFINED_FIELD13, "
+ "       ''                        AS USERDEFINED_FIELD14, "
+ "       ''                        AS USERDEFINED_FIELD15, "
+ "       ''                        AS MATCHING_ID, "
+ "       LT.recon_report_config_id AS REQUEST_ID, "
+ "       HD.id                     AS PR_ID, "
+ "       A2.id                     AS A2_ID, "
+ "       LT.pr_invoice_key, "
+ "       LT.a2_invoice_key, "
+ "       LT.a2_supplier_gstin "
+ "FROM   (SELECT pr_doc_num, "
+ "               a2_doc_num, "
+ "               a2_recipient_gstin, "
+ "               pr_recipient_gstin, "
+ "               pr_supplier_gstin, "
+ "               a2_supplier_gstin, "
+ "               bucket_type                           AS CURRENT_REPORT, "
+ "               matching_score, "
+ "               a2_doc_type, "
+ "               a2_gstn_saved_response, "
+ "               pr_doc_type, "
+ "               a2_doc_date, "
+ "               pr_doc_date, "
+ "               a2_taxable_value, "
+ "               pr_taxable_value, "
+ "               a2_cgst, "
+ "               pr_cgst, "
+ "               a2_sgst, "
+ "               pr_sgst, "
+ "               a2_igst, "
+ "               pr_igst, "
+ "               a2_cess, "
+ "               pr_cess, "
+ "               a2_pos, "
+ "               pr_pos, "
+ "               tax_period, "
+ "               a2_invoice_key, "
+ "               pr_invoice_key, "
+ "               user_response, "
+ "               suggested_response, "
+ "               current_mismatch_reason, "
+ "               forced_match_response, "
+ "               recon_report_config_id, "
+ "               addition_a2_pr_response_tax_period, "
+ "               bucket_type, "
+ "               Lag(bucket_type) "
+ "                 OVER( "
+ "                   partition BY a2_invoice_key "
+ "                   ORDER BY recon_report_config_id ) AS PRE_REPORT_A2, "
+ "               Lag(bucket_type) "
+ "                 OVER( "
+ "                   partition BY pr_invoice_key "
+ "                   ORDER BY recon_report_config_id ) AS PRE_REPORT_PR, "
+ "               Lag(user_response) "
+ "                 OVER( "
+ "                   partition BY a2_invoice_key "
+ "                   ORDER BY recon_report_config_id ) AS PRE_USER_RESPONSE_A2, "
+ "               Lag(user_response) "
+ "                 OVER( "
+ "                   partition BY pr_invoice_key "
+ "                   ORDER BY recon_report_config_id ) AS PRE_USER_RESPONSE_PR, "
+ "               is_active "
+ "        FROM   link_a2_pr "
+ "        WHERE  ( a2_recipient_gstin IN(SELECT DISTINCT gstin "
+ "                                       FROM "
+ "                 recon_report_gstin_details "
+ "                                       WHERE  is_active = true) "
+ "                  OR pr_recipient_gstin IN(SELECT DISTINCT gstin "
+ "                                           FROM "
+ "                     recon_report_gstin_details "
+ "                                           WHERE  is_active = true) ))LT "
+ "       FULL OUTER JOIN (SELECT itc_entitlement, "
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
+ "                               AND an_tax_doc_type IN ( 'B2B', 'DXP', 'SEZWP', "
+ "                                                        'SEZWOP' "
+ "                                                      )) HD "
+ "                    ON LT.pr_doc_date = HD.doc_date "
+ "                       AND LT.pr_supplier_gstin = HD.supplier_gstin "
+ "                       AND LT.pr_recipient_gstin = HD.cust_gstin "
+ "                       AND LT.pr_doc_type = HD.doc_type "
+ "                       AND LT.pr_doc_num = HD.doc_num "
+ "       FULL OUTER JOIN (SELECT id, "
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
+ "                                       itc_ent          AS ITCEntitlement, "
+ "                                       supplier_inv_val AS DOC_AMT, "
+ "                                       cfs              AS CFS, "
+ "                                       supplier_inv_num, "
+ "                                       supplier_inv_date, "
+ "                                       inv_type, "
+ "                                       sgstin, "
+ "                                       cgstin, "
+ "                                       sec_7_act        AS Section7_IGST, "
+ "                                       diff_percent     AS DIFF_PERCENT, "
+ "                                       'B2B'            AS TABLE_TYPE, "
+ "                                       supplier_inv_val "
+ "                                FROM   getanx2_b2b_header "
+ "                                WHERE  is_delete = false) "
+ "                        UNION ALL "
+ "                        (SELECT id, "
+ "                                itc_ent          AS ITCEntitlement, "
+ "                                supplier_inv_val AS DOC_AMT, "
+ "                                cfs              AS CFS, "
+ "                                supplier_inv_num, "
+ "                                supplier_inv_date, "
+ "                                inv_type, "
+ "                                sgstin, "
+ "                                cgstin, "
+ "                                sec_7_act        AS Section7_IGST, "
+ "                                diff_percent     AS DIFF_PERCENT, "
+ "                                'DE'             AS TABLE_TYPE, "
+ "                                supplier_inv_val "
+ "                         FROM   getanx2_de_header "
+ "                         WHERE  is_delete = false) "
+ "                        UNION ALL "
+ "                        (SELECT id, "
+ "                                itc_ent "
+ "                                AS ITCEntitlement, "
+ "                                supplier_inv_val "
+ "                                AS DOC_AMT, "
+ "                                cfs "
+ "                                AS CFS, "
+ "                                supplier_inv_num, "
+ "                                supplier_inv_date, "
+ "                                inv_type, "
+ "                                sgstin, "
+ "                                cgstin, "
+ "                                '' "
+ "                                AS Section7_IGST, "
+ "                                diff_percent "
+ "                                AS DIFF_PERCENT, "
+ "       ' "
+ "                                                                  SEZWP' "
+ "       AS "
+ "       TABLE_TYPE, "
+ "       supplier_inv_val "
+ "       FROM   getanx2_sezwp_header "
+ "       WHERE  is_delete = false) "
+ "        UNION ALL "
+ "        (SELECT id, "
+ "                itc_ent          AS ITCEntitlement, "
+ "                supplier_inv_val AS DOC_AMT, "
+ "                cfs              AS CFS, "
+ "                supplier_inv_num, "
+ "                supplier_inv_date, "
+ "                inv_type, "
+ "                sgstin, "
+ "                cgstin, "
+ "                ''               AS Section7_IGST, "
+ "                0                AS DIFF_PERCENT, "
+ "                'SEZWP'          AS TABLE_TYPE, "
+ "                supplier_inv_val "
+ "         FROM   getanx2_sezwop_header "
+ "         WHERE  is_delete = false)) A2 "
+ "                    ON LT.a2_doc_date = A2.supplier_inv_date "
+ "                       AND LT.a2_supplier_gstin = A2.sgstin "
+ "                       AND LT.a2_recipient_gstin = A2.cgstin "
+ "                       AND LT.a2_doc_type = A2.inv_type "
+ "                       AND LT.a2_doc_num = A2.supplier_inv_num "
+ "WHERE  LT.a2_recipient_gstin =:gstin  "
+ "       AND LT.tax_period =:taxPeriod "
+ "       AND LT.is_active = true "
+ "       AND LT.current_report IN ( 'Value Mismatch', 'POS Mismatch', "
+ "                                  'Doc Date Mismatch', "
+ "                                  'Multi-Mismatch' ) "
+ "ORDER  BY LT.a2_doc_num";

		
		String gstin = req.getGstin();
		List<VendorMismatchDto> cList = null;
		List<String> taxPeriodList = findDistTaxPeriod();
		ArrayList<VendorMismatchDto> respList = new ArrayList<>();
		for (String taxPeriod : taxPeriodList) {
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Creating query %s, gstin %s, "
						+ "and taxPeriod %s ", queryString, gstin, taxPeriod ); 
				LOGGER.debug(msg);
			}
			
			Query q = entityManager.createNativeQuery(queryString);
			
			q.setParameter("gstin", gstin );
			q.setParameter("taxPeriod", taxPeriod);
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			 cList = list.parallelStream()
					.map(o -> convertToDto(o, einfo, req))
					.collect(Collectors.toCollection(ArrayList::new));
			 
			 respList.addAll(cList);
			

			if (LOGGER.isDebugEnabled()) {
				String str = String.format(
						"Executed Query for Vendor"
								+ " Mismatch Records and Fetched %d rows",
						list.size());
				LOGGER.debug(str);
			}

			
		}
		
		return respList;

		/* return convertDocsAsDtosByChunking(list); */
		/*
		 * List<VendorMismatchChildDto> listOfChildDtos = list.parallelStream()
		 * .map(obj -> convertToDto(obj)) .collect(Collectors.toList()); return
		 * listOfChildDtos;
		 */

	}

	/*@Override
	public List<Pair<VendorMismatchRevRecordsDto, List<Long>>> convertDocsAsDtosByChunking(
			List<Object[]> objs) {
		childArry = new ArrayList<>();
		ids = new ArrayList<>();
		List<Pair<VendorMismatchRevRecordsDto, List<Long>>> list = new ArrayList<>();
		objs.forEach(obj -> {
			VendorMismatchDto child = convertToDto(obj);
			childArry.add(child);
			int pos = objs.indexOf(obj);
			if ((pos + 1) % APIConstants.MAX_NO_OF_INVOICES == 0) {

				VendorMismatchRevRecordsDto dto = new VendorMismatchRevRecordsDto();
				dto.setItem(childArry);
				list.add(new Pair<>(dto, ids));
				childArry = new ArrayList<>();
				ids = new ArrayList<>();
			}

		});
		return list;
	}*/

	public VendorMismatchDto convertToDto(Object[] obj, EntityInfoEntity obj1,
			RevIntegrationScenarioTriggerDto req ) {
		VendorMismatchDto dto = new VendorMismatchDto(req.getGstin().substring(2, 12),
				obj1.getEntityName(), obj1.getCompanyHq());
		dto.setITCEntitlementAnx2((obj[0] != null) ? obj[0].toString() : null);
		dto.setITCEntitlementPR((obj[1] != null) ? obj[1].toString() : null);
		dto.setPreviousReportTypeANX2((obj[2] != null) ? obj[2].toString() : null);
		dto.setPreviousReportTypePR((obj[3] != null) ? obj[3].toString() : null);
		dto.setUserResponse((obj[4] != null) ? obj[4].toString() : null);
		dto.setSuggestedResponse((obj[5] != null) ? obj[5].toString() : null);
		dto.setCurrentMismatchReason((obj[6] != null) ? obj[6].toString() : null);
		dto.setForcedMatchResponse((obj[7] != null) ? obj[7].toString() : null);
		dto.setInformationReportReference((obj[8] != null) ? obj[8].toString() : null);
		dto.setRespTaxPeriod((obj[9] != null) ? obj[9].toString() : null);
		dto.setPreviousResponseANX2((obj[10] != null) ? obj[10].toString() : null);
		dto.setPreviousResponsePR((obj[11] != null) ? obj[11].toString() : null);
		dto.setCurrentReportType((obj[12] != null) ? obj[12].toString() : null);
		dto.setDocumentNumberPR((obj[13] != null) ? obj[13].toString() : null);
		dto.setDocumentNumberANX2((obj[14] != null) ? obj[14].toString() : null);
		dto.setRecipientGSTINANX2((obj[15] != null) ? obj[15].toString() : null);
		dto.setRecipientGSTINPR((obj[16] != null) ? obj[16].toString() : null);
		dto.setDocTypeANX2((obj[17] != null) ? obj[17].toString() : null);
		dto.setDocTypePR((obj[18] != null) ? obj[18].toString() : null);
		dto.setDocumentDateANX2((obj[19] != null) ? obj[19].toString() : null);
		dto.setDocumentDatePR((obj[20] != null) ? obj[20].toString() : null);
		dto.setTaxableValueANX2((obj[21] != null) ? obj[21].toString() : null);
		dto.setTaxableValuePR((obj[22] != null) ? obj[22].toString() : null);
		dto.setCGSTANX2((obj[23] != null) ? obj[23].toString() : null);
		dto.setCGSTPR((obj[24] != null) ? obj[24].toString() : null);
		dto.setSGSTANX2((obj[25] != null) ? obj[25].toString() : null);
		dto.setSGSTPR((obj[26] != null) ? obj[26].toString() : null);
		dto.setIGSTANX2((obj[27] != null) ? obj[27].toString() : null);
		dto.setIGSTPR((obj[28] != null) ? obj[28].toString() : null);
		dto.setCessANX2((obj[29] != null) ? obj[29].toString() : null);
		dto.setCessPR((obj[30] != null) ? obj[30].toString() : null);
		dto.setPOSANX2((obj[31] != null) ? obj[31].toString() : null);
		dto.setPOSPR((obj[32] != null) ? obj[32].toString() : null);
		dto.setSavedResponseReportTypeANX2((obj[33] != null) ? obj[33].toString() : null);
		dto.setDigiGSTSavedResponseANX2((obj[34] != null) ? obj[34].toString() : null);
		dto.setGSTNSavedResponseANX2((obj[35] != null) ? obj[35].toString() : null);
		dto.setMatchingScore((obj[36] != null) ? obj[36].toString() : null);
		dto.setReconTaxPeriod((obj[37] != null) ? obj[37].toString() : null);
		dto.setCalendarMonthPR((obj[38] != null) ? obj[38].toString() : null);
		dto.setDateofUploadANX2((obj[39] != null) ? obj[39].toString() : null);
		dto.setOrgMonthANX2((obj[40] != null) ? obj[40].toString() : null);
		dto.setOrgMonthPR((obj[41] != null) ? obj[41].toString() : null);
		dto.setSupplierNameANX2((obj[42] != null) ? obj[42].toString() : null);
		dto.setSupplierNamePR((obj[43] != null) ? obj[43].toString() : null);
		dto.setTotalTaxANX2((obj[44] != null) ? obj[44].toString() : null);
		dto.setTotalTaxPR((obj[45] != null) ? obj[45].toString() : null);
		dto.setInvoiceValueANX2((obj[46] != null) ? obj[46].toString() : null);
		dto.setInvoiceValuePR((obj[47] != null) ? obj[47].toString() : null);
		dto.setAvailableIGST((obj[48] != null) ? obj[48].toString() : null);
		dto.setAvailableCGST((obj[49] != null) ? obj[49].toString() : null);
		dto.setAvailableSGST((obj[50] != null) ? obj[50].toString() : null);
		dto.setAvailableCESS((obj[51] != null) ? obj[51].toString() : null);
		dto.setTableTypeANX2((obj[52] != null) ? obj[52].toString() : null);
		dto.setTableTypePR((obj[53] != null) ? obj[53].toString() : null);
		dto.setCFSFlagANX2((obj[54] != null) ? obj[54].toString() : null);
		dto.setClaimRefundFlagANX2((obj[55] != null) ? obj[55].toString() : null);
		dto.setClaimRefundFlagPR((obj[56] != null) ? obj[56].toString() : null);
		dto.setSection7ofIGSTANX2((obj[57] != null) ? obj[57].toString() : null);
		dto.setSection7ofIGSTPR((obj[58] != null) ? obj[58].toString() : null);
		dto.setAutoPopulateToRefundANX2((obj[59] != null) ? obj[59].toString() : null);
		dto.setAutoPopulateToRefundPR((obj[60] != null) ? obj[60].toString() : null);
		dto.setDifferentialPercentageANX2((obj[61] != null) ? obj[61].toString() : null);
		dto.setDifferentialPercentagePR((obj[62] != null) ? obj[62].toString() : null);
		dto.setOrgDocTypeANX2((obj[63] != null) ? obj[63].toString() : null);
		// dto.setOrgDocTypePR((String)obj[64]);
		dto.setOrgDocNumberANX2((obj[65] != null) ? obj[65].toString() : null);
		dto.setOrgDocNumberPR((obj[66] != null) ? obj[66].toString() : null);
		dto.setOrgDocDateANX2((obj[67] != null) ? obj[67].toString() : null);
		dto.setOrgDocDatePR((obj[68] != null) ? obj[68].toString() : null);
		dto.setOrgSupplierGSTINPR((obj[69] != null) ? obj[69].toString() : null);
		dto.setUserID((obj[70] != null) ? obj[70].toString() : null);
		dto.setSourceFileName((obj[71] != null) ? obj[71].toString() : null);
		dto.setProfitCentre((obj[72] != null) ? obj[72].toString() : null);
		dto.setPlant((obj[73] != null) ? obj[73].toString() : null);
		dto.setDivision((obj[74] != null) ? obj[74].toString() : null);
		dto.setLocation((obj[75] != null) ? obj[75].toString() : null);
		dto.setPurchaseOrganisation((obj[76] != null) ? obj[76].toString() : null);
		dto.setUserAccess1((obj[77] != null) ? obj[77].toString() : null);
		dto.setUserAccess2((obj[78] != null) ? obj[78].toString() : null);
		dto.setUserAccess3((obj[79] != null) ? obj[79].toString() : null);
		dto.setUserAccess4((obj[80] != null) ? obj[80].toString() : null);
		dto.setUserAccess5((obj[81] != null) ? obj[81].toString() : null);
		dto.setUserAccess6((obj[82] != null) ? obj[82].toString() : null);
		dto.setGlCodeTaxableValue((obj[83] != null) ? obj[83].toString() : null);
		dto.setGlCodeIGST((obj[84] != null) ? obj[84].toString() : null);
		dto.setGlCodeCGST((obj[85] != null) ? obj[85].toString() : null);
		dto.setGlCodeSGST((obj[86] != null) ? obj[86].toString() : null);
		dto.setGlCodeAdvaloremCess((obj[87] != null) ? obj[87].toString() : null);
		dto.setGlCodeSpecificCess((obj[88] != null) ? obj[88].toString() : null);
		dto.setGlCodeStateCess((obj[89] != null) ? obj[89].toString() : null);
		dto.setSupplyType((obj[90] != null) ? obj[90].toString() : null);
		dto.setCRDRPreGST((obj[91] != null) ? obj[91].toString() : null);
		dto.setSupplierType((obj[92] != null) ? obj[92].toString() : null);
		dto.setSupplierCode((obj[93] != null) ? obj[93].toString() : null);
		dto.setSupplierAddress1((obj[94] != null) ? obj[94].toString() : null);
		dto.setSupplierAddress2((obj[95] != null) ? obj[95].toString() : null);
		dto.setSupplierAddress3((obj[96] != null) ? obj[96].toString() : null);
		dto.setSupplierAddress4((obj[97] != null) ? obj[97].toString() : null);
		dto.setStateApplyingCess((obj[98] != null) ? obj[98].toString() : null);
		dto.setPortCode((obj[99] != null) ? obj[99].toString() : null);
		dto.setBillOfEntry((obj[100] != null) ? obj[100].toString() : null);
		dto.setBillOfEntryDate((obj[101] != null) ? obj[101].toString() : null);
		dto.setCIFValue((obj[102] != null) ? obj[102].toString() : null);
		dto.setCustomDuty((obj[103] != null) ? obj[103].toString() : null);
		dto.setQuantity((obj[104] != null) ? obj[104].toString() : null);
		dto.setCessAmountAdvalorem((obj[105] != null) ? obj[105].toString() : null);
		dto.setCessAmountSpecific((obj[106] != null) ? obj[106].toString() : null);
		dto.setStateCessAmount((obj[107] != null) ? obj[107].toString() : null);
		dto.setOtherValue((obj[108] != null) ? obj[108].toString() : null);
		dto.setPurchaseVoucherDate((obj[109] != null) ? obj[109].toString() : null);
		dto.setPurchaseVoucherNumber((obj[110] != null) ? obj[110].toString() : null);
		dto.setPostingDate((obj[111] != null) ? obj[111].toString() : null);
		dto.setPaymentVoucherNumber((obj[112] != null) ? obj[112].toString() : null);
		dto.setPaymentDate((obj[113] != null) ? obj[113].toString() : null);
		dto.setContractNumber((obj[114] != null) ? obj[114].toString() : null);
		dto.setContractValue((obj[115] != null) ? obj[115].toString() : null);
		dto.setEWayBillNumber((obj[116] != null) ? obj[116].toString() : null);
		dto.setEWayBillDate((obj[117] != null) ? obj[117].toString() : null);
		dto.setUserDefinedField1((obj[118] != null) ? obj[118].toString() : null);
		dto.setUserDefinedField2((obj[119] != null) ? obj[119].toString() : null);
		dto.setUserDefinedField3((obj[120] != null) ? obj[120].toString() : null);
		dto.setUserDefinedField4((obj[121] != null) ? obj[121].toString() : null);
		dto.setUserDefinedField5((obj[122] != null) ? obj[122].toString() : null);
		dto.setUserDefinedField6((obj[123] != null) ? obj[123].toString() : null);
		dto.setUserDefinedField7((obj[124] != null) ? obj[124].toString() : null);
		dto.setUserDefinedField8((obj[125] != null) ? obj[125].toString() : null);
		dto.setUserDefinedField9((obj[126] != null) ? obj[126].toString() : null);
		dto.setUserDefinedField10((obj[127] != null) ? obj[127].toString() : null);
		dto.setUserDefinedField11((obj[128] != null) ? obj[128].toString() : null);
		dto.setUserDefinedField12((obj[129] != null) ? obj[129].toString() : null);
		dto.setUserDefinedField13((obj[130] != null) ? obj[130].toString() : null);
		dto.setUserDefinedField14((obj[131] != null) ? obj[131].toString() : null);
		dto.setUserDefinedField15((obj[132] != null) ? obj[132].toString() : null);
		dto.setMatchingID((obj[133] != null) ? obj[133].toString() : null);
		dto.setRequestID((obj[134] != null) ? obj[134].toString() : null);
		dto.setIDA2((obj[135] != null) ? obj[135].toString() : null);
		dto.setIDPR((obj[136] != null) ? obj[136].toString() : null);

		dto.setSupplierGSTINANX2((obj[139] != null) ? obj[139].toString() : null);
		return dto;

	}

	private List<String> findDistTaxPeriod() {
		String qry = "Select distinct tax_period from Link_A2_PR";
		Query q = entityManager.createNativeQuery(qry);
		@SuppressWarnings("unchecked")
		List<String> listTp = q.getResultList();

		return listTp;

	}

}
