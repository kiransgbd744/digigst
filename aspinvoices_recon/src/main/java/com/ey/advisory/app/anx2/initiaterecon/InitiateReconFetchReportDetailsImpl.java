package com.ey.advisory.app.anx2.initiaterecon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.app.data.repositories.client.asprecon.ReconConfigRepository;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.ReconStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component("InitiateReconFetchReportDetailsImpl")
public class InitiateReconFetchReportDetailsImpl
		implements InitiateReconFetchReportDetails {

	private static int CSV_BUFFER_SIZE = 8192;
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ReconConfigRepository")
	ReconConfigRepository reconConfigRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Override
	public String getReconReportData(Long configId) {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String[] reportType = { "Doc Date & Value Mismatch",
				"Document Type Mismatch", "Probable-1", "Probable-11",
				"Addition in ANX-2", "Addition in PR", "Exact Match",
				"Match upto Tolerance", "Value Mismatch", "POS Mismatch",
				"Document Date Mismatch", "Multi-Mismatch", "Consolidated",
				"Fuzzy Match", "Potential Match" };
		try {
			tempDir = createTempDir(configId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config ID is '%s',"
						+ " Created temporary directory to generate "
								+ "zip file: %s",
						configId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			updateReconConfigStatus(
					ReconStatusConstants.REPORT_GENERATION_FAILED, configId);
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					configId, ex);
		}

		for (int i = 0; i < reportType.length; i++) {

			String queryString = createQueryString(reportType[i]);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Query for Recon Reports: '%s' - '%s'", reportType[i],
						queryString);
				LOGGER.debug(msg);
			}

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("configId", configId);

			if (!reportType[i].equals("Consolidated"))
				q.setParameter("reportType", reportType[i]);
			long dbLoadStTime = System.currentTimeMillis();
			@SuppressWarnings("unchecked")
			List<Object[]> records = q.getResultList();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data"
								+ " from DB is '%d' millisecs,"
								+ " Report Name and Data count: '%s' - '%s'",
						dbLoadTimeDiff, reportType[i], records.size());
				LOGGER.debug(msg);
			}

			if (records != null && !records.isEmpty()) {

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ reportType[i] + ".csv";
				List<Anx2InitiateReconReportDto> reconDataList =
						new ArrayList<>();

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {
					reconDataList = records.stream().map(o -> convert(o))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Report Name and row count: '%s' - '%s'",
								reportType[i], reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						String invoiceHeadersTemplate = commonUtility.getProp(
								"anx2.initiate.recon.report.header.template");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp(
										"anx2.initiate.recon.report.column.mapping")
								.split(",");

						ColumnPositionMappingStrategy
						<Anx2InitiateReconReportDto> mappingStrategy =
						new ColumnPositionMappingStrategy<>();
						mappingStrategy
								.setType(Anx2InitiateReconReportDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder
						<Anx2InitiateReconReportDto> builder =
						new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv
						<Anx2InitiateReconReportDto> beanWriter = builder
								.withMappingStrategy(mappingStrategy)
								.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
								.withLineEnd(CSVWriter.DEFAULT_LINE_END)
								.withEscapechar(
										CSVWriter.DEFAULT_ESCAPE_CHARACTER)
								.build();
						long generationStTime = System.currentTimeMillis();
						beanWriter.write(reconDataList);
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Total Time taken to"
											+ " Generate the report is '%d' millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									generationTimeDiff, reportType[i],
									records.size());
							LOGGER.debug(msg);
						}
					}
				} catch (Exception ex) {
					LOGGER.error("Exception while executing the query for "
							+ "Report Type :{}", reportType[i], ex);
					updateReconConfigStatus(
							ReconStatusConstants.REPORT_GENERATION_FAILED,
							configId);
				}

			}
		}

		String zipFileName = "";
		if (tempDir.list().length > 0) {
			createLegendsInTempPath(tempDir, configId);
			zipFileName = combineAndZipXlsxFiles.zipfolder(configId, tempDir);

			File zipFile = new File(tempDir, zipFileName);

			String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
					"Anx2ReconReports");
			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATED, uploadedDocName,
					new Date(), configId);

		} else {
			updateReconConfigStatus(ReconStatusConstants.NO_DATA_FOUND,
					configId);
		}
		deleteTemporaryDirectory(tempDir);
		return zipFileName;
	}

	private void createLegendsInTempPath(File tempDir, Long configId) {
		try {
			File file = ResourceUtils
					.getFile("classpath:ReportTemplates/Recon Legends.csv");

			String legedPath = tempDir.getAbsolutePath() + File.separator
					+ "Recon Legends.csv";
			File destFile = new File(legedPath);

			copy(file, destFile);
		} catch (Exception ex) {
			updateReconConfigStatus(
					ReconStatusConstants.REPORT_GENERATION_FAILED, configId);
			LOGGER.error("Exception while creating the"
					+ " Legends File in Temp Path", ex);
		}

	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private void updateReconConfigStatus(String status, Long configId) {
		reconConfigRepo.updateReconConfigStatusAndReportName(status, null,
				new Date(), configId);
	}

	private String createQueryString(String reportType) {

		StringBuilder condition = new StringBuilder();

		condition.append(" RECON_REPORT_CONFIG_ID = :configId");

		if (!reportType.equals("Consolidated"))
			condition.append(" AND LT.CURRENT_REPORT = :reportType");

		if (reportType.equals("Potential Match")) {
			condition.append(" AND is_information_report = 1 ");
		} else {
			condition.append(" AND is_information_report = 0 ");
		}

		String queryStr = "SELECT ''                  AS A2_ITC_ENTITLEMENT, "
				+ "       HD.itc_entitlement           AS PR_ITC_ENTITLEMENT, "
				+ "       previouse_report_a2, "
				+ "       previouse_report_pr, " + "       LT.user_response, "
				+ "       LT.suggested_response, "
				+ "       LT.current_mismatch_reason, "
				+ "       LT.forced_match_response, "
				+ "       ''                                              AS "
				+ "       INFORMATION_REPORT_REFERENCE, "
				+ "       LT.addition_a2_pr_response_tax_period, "
				+ "       previouse_response_a2, "
				+ "       previouse_response_pr, "
				+ "       LT.current_report, " + "       LT.pr_doc_num, "
				+ "       LT.a2_doc_num, " + "       LT.a2_recipient_gstin, "
				+ "       LT.pr_recipient_gstin, " + "       LT.a2_doc_type, "
				+ "       LT.pr_doc_type, " + "       LT.a2_doc_date, "
				+ "       LT.pr_doc_date, " + "       LT.a2_taxable_value, "
				+ "       LT.pr_taxable_value, " + "       LT.a2_cgst, "
				+ "       LT.pr_cgst, " + "       LT.a2_sgst, "
				+ "       LT.pr_sgst, " + "       LT.a2_igst, "
				+ "       LT.pr_igst, " + "       LT.a2_cess, "
				+ "       LT.pr_cess, " + "       LT.a2_pos, "
				+ "       LT.pr_pos, "
				+ "       ''                                              AS "
				+ "       A2_SAVED_RESPONSE_REPORTTYPE, "
				+ "       ''                                              AS "
				+ "       A2_DIGIGST_SAVEDRESPONSE, "
				+ "       LT.a2_gstn_saved_response    AS GSTN_SAVED_RESPONSE, "
				+ "       LT.matching_score                 AS MATCHING_SCORE, "
				+ "       LT.tax_period                    AS RECON_TAXPERIOD, "
				+ "       ''                             AS CALENDAR_MONTH_PR, "
				+ "       ''                                   AS DATE_UPLOAD, "
				+ "       ''                                  AS ORG_MONTH_A2, "
				+ "       ''                                  AS ORG_MONTH_PR, "
				+ "       ''                              AS A2_SUPPLIER_NAME, "
				+ "       HD.cust_supp_name               AS PR_SUPPLIER_NAME, "
				+ "       Ifnull(LT.a2_cgst, 0) + Ifnull(LT.a2_sgst, 0) "
				+ "       + Ifnull(LT.a2_igst, 0) + Ifnull(LT.a2_cess, 0)"
				+ "		  AS A2_TOTAL_TAX, "
				+ "       Ifnull(LT.pr_cgst, 0) + Ifnull(LT.pr_sgst, 0) "
				+ "       + Ifnull(LT.pr_igst, 0) + Ifnull(LT.pr_cess, 0) "
				+ "		  AS PR_TOTAL_TAX, "
				+ "       A2.supplier_inv_val              AS A2_INVOICE_VALE, "
				+ "       HD.doc_amt                       AS PR_INVOICE_VALE, "
				+ "       HD.available_igst                         AS A_IGST, "
				+ "       HD.available_cgst                         AS A_CGST, "
				+ "       HD.available_sgst                         AS A_SGST, "
				+ "       HD.available_cess                         AS A_CESS, "
				+ "       ''                                 AS A2_TABLE_TYPE, "
				+ "       HD.an_tax_doc_type                 AS PR_TABLE_TYPE, "
				+ "       ''                                      AS CFS_FLAG, "
				+ "       ''                               AS A2_CLAIM_REFUND, "
				+ "       ''                               AS PR_CLAIM_REFUND, "
				+ "       ''                              AS A2_SECTION7_IGST, "
				+ "       HD.section7_of_igst_flag        AS PR_SECTION7_IGST, "
				+ "       ''                                              AS "
				+ "       A2_AUTOPOPULATED_REFUND, "
				+ "       HD.autopopulate_to_refund                       AS "
				+ "       PR_AUTOPOPULATED_REFUND, "
				+ "       ''                               AS A2_DIFF_PERCENT, "
				+ "       HD.diff_percent                  AS PR_DIFF_PERCENT, "
				+ "       ''                              AS A2_ORIG_DOC_TYPE, "
				+ "       HD.original_doc_type            AS PR_ORIG_DOC_TYPE, "
				+ "       ''                               AS A2_ORIG_DOC_NUM, "
				+ "       HD.original_doc_num              AS PR_ORIG_DOC_NUM, "
				+ "       ''                              AS A2_ORIG_DOC_DATE, "
				+ "       HD.original_doc_date            AS PR_ORIG_DOC_DATE, "
				+ "       LT.pr_supplier_gstin, "
				+ "       HD.user_id                               AS USER_ID, "
				+ "       HD.source_filename               AS SOURCE_FLIENAME, "
				+ "       HD.profit_centre                   AS PROFIT_CENTRE, "
				+ "       HD.plant_code                              AS PLANT, "
				+ "       HD.division                             AS DIVISION, "
				+ "       HD.location                             AS LOCATION, "
				+ "       HD.purchase_organization   AS PURCHASE_ORGANISATION, "
				+ "       HD.useraccess1                       AS USERACCESS1, "
				+ "       HD.useraccess2                       AS USERACCESS2, "
				+ "       HD.useraccess3                       AS USERACCESS3, "
				+ "       HD.useraccess4                       AS USERACCESS4, "
				+ "       HD.useraccess5                       AS USERACCESS5, "
				+ "       HD.useraccess6                       AS USERACCESS6, "
				+ "       ''                          AS GLCODE_TAXABLE_VALUE, "
				+ "       ''                                  AS GL_CODE_IGST, "
				+ "       ''                                  AS GL_CODE_CGST, "
				+ "       ''                                  AS GL_CODE_SGST, "
				+ "       ''                         AS GL_CODE_ADVALOREM_CESS "
				+ "       , "
				+ "       ''                                              AS "
				+ "       GL_CODE_SPECIFIC_CESS, "
				+ "       ''                             AS GL_CODESTATE_CESS, "
				+ "       HD.supply_type                       AS SUPPLY_TYPE, "
				+ "       HD.crdr_pre_gst                       AS CRDRPERGST, "
				+ "       HD.cust_supp_type                  AS SUPPLIER_TYPE, "
				+ "       HD.cust_supp_code                  AS SUPPLIER_CODE, "
				+ "       HD.cust_supp_address1          AS SUPPLIER_ADDERSS1, "
				+ "       HD.cust_supp_address2          AS SUPPLIER_ADDERSS2, "
				+ "       HD.cust_supp_address3          AS SUPPLIER_ADDERSS3, "
				+ "       HD.cust_supp_address4          AS SUPPLIER_ADDERSS4, "
				+ "       HD.state_applying_cess       AS STATE_APPLYING_CESS, "
				+ "       HD.ship_port_code                      AS PORT_CODE, "
				+ "       HD.bill_of_entry                   AS BILL_OF_ENTRY, "
				+ "       HD.bill_of_entry_date         AS BILL_OF_ENTRY_DATE, "
				+ "       ''                                     AS CIF_VALUE, "
				+ "       ''                                   AS CUSTOM_DUTY, "
				+ "       ''                                      AS QUANTITY, "
				+ "       ''                            AS CESS_AMT_ADVALOREM, "
				+ "       ''                             AS CESS_AMT_SPECIFIC, "
				+ "       ''                             AS STATE_CESS_AMOUNT, "
				+ "       ''                                   AS OTHER_VALUE, "
				+ "       ''                         AS PURCHASE_VOUCHER_DATE, "
				+ "       ''                          AS PURCHASE_VOUCHER_NUM, "
				+ "       ''                                  AS POSTING_DATE, "
				+ "       ''                           AS PAYMENT_VOUCHER_NUM, "
				+ "       ''                              	  AS PAYMENT_DATE, "
				+ "       ''                                  AS CONTRACT_NUM, "
				+ "       ''                                  AS CONTRACT_VAL, "
				+ "       HD.eway_bill_num                 AS E_WAYBILLNUMBER, "
				+ "       HD.eway_bill_date                  AS E_WAYBILLDATE, "
				+ "       ''                            AS USERDEFINED_FIELD1, "
				+ "       ''                            AS USERDEFINED_FIELD2, "
				+ "       ''                            AS USERDEFINED_FIELD3, "
				+ "       ''                            AS USERDEFINED_FIELD4, "
				+ "       ''                            AS USERDEFINED_FIELD5, "
				+ "       ''                            AS USERDEFINED_FIELD6, "
				+ "       ''                            AS USERDEFINED_FIELD7, "
				+ "       ''                            AS USERDEFINED_FIELD8, "
				+ "       ''                            AS USERDEFINED_FIELD9, "
				+ "       ''                           AS USERDEFINED_FIELD10, "
				+ "       ''                           AS USERDEFINED_FIELD11, "
				+ "       ''                           AS USERDEFINED_FIELD12, "
				+ "       ''                           AS USERDEFINED_FIELD13, "
				+ "       ''                           AS USERDEFINED_FIELD14, "
				+ "       ''                           AS USERDEFINED_FIELD15, "
				+ "       ''                                   AS MATCHING_ID, "
				+ "       LT.recon_report_config_id             AS REQUEST_ID, "
				+ "       HD.id                                      AS PR_ID, "
				+ "       A2.id                                      AS A2_ID, "
				+ "       LT.pr_invoice_key, " + "       LT.a2_invoice_key, "
				+ "       LT.a2_supplier_gstin " + "FROM   (SELECT pr_doc_num, "
				+ "               a2_doc_num, "
				+ "               a2_recipient_gstin, "
				+ "               pr_recipient_gstin, "
				+ "               pr_supplier_gstin, "
				+ "               a2_supplier_gstin, "
				+ "               bucket_type          AS CURRENT_REPORT, "
				+ "               matching_score, "
				+ "               a2_doc_type, "
				+ "               a2_gstn_saved_response, "
				+ "               pr_doc_type, "
				+ "               a2_doc_date, "
				+ "               pr_doc_date, "
				+ "               a2_taxable_value, "
				+ "               pr_taxable_value, "
				+ "               a2_cgst, " + "               pr_cgst, "
				+ "               a2_sgst, " + "               pr_sgst, "
				+ "               a2_igst, " + "               pr_igst, "
				+ "               a2_cess, " + "               pr_cess, "
				+ "               a2_pos, " + "               pr_pos, "
				+ "               tax_period, "
				+ "               a2_invoice_key, "
				+ "               pr_invoice_key, "
				+ "               user_response, " + "               CASE "
				+ "                 WHEN suggested_response = "
				+ "				'Accept ANX2 and Claim ITC as per PR' "
				+ "               THEN " + "                 'A3' "
				+ "             WHEN suggested_response = 'Pending' THEN 'P1' "
				+ "                 ELSE suggested_response "
				+ "               END                  AS SUGGESTED_RESPONSE, "
				+ "               current_mismatch_reason, "
				+ "               forced_match_response, "
				+ "               recon_report_config_id, "
				+ "               addition_a2_pr_response_tax_period, "
				+ "               bucket_type, "
				+ "               pre_a2_report_type   AS PREVIOUSE_REPORT_A2, "
				+ "               pre_pr_report_type   AS PREVIOUSE_REPORT_PR, "
				+ "             pre_a2_user_response AS PREVIOUSE_RESPONSE_A2, "
				+ "             pre_pr_user_response AS PREVIOUSE_RESPONSE_PR, "
				+ "               is_information_report "
				+ "        FROM   link_a2_pr)LT "
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
				+ "                  FROM   anx_inward_doc_header "
				+ "                        WHERE  is_processed = true "
				+ "                               AND is_delete = false "
				+ "                               AND an_return_type = 'ANX2' "
				+ "                               AND an_tax_doc_type IN "
				+ "								( 'B2B', 'DXP', 'SEZWP', "
				+ "                                                   'SEZWOP' "
				+ "                                                      )) HD "
				+ "                    ON LT.pr_doc_date = HD.doc_date "
				+ "               AND LT.pr_supplier_gstin = HD.supplier_gstin "
				+ "                  AND LT.pr_recipient_gstin = HD.cust_gstin "
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
				+ "                        itc_ent          AS ITCEntitlement, "
				+ "                               supplier_inv_val AS DOC_AMT, "
				+ "                                       cfs          AS CFS, "
				+ "                                       supplier_inv_num, "
				+ "                                       supplier_inv_date, "
				+ "                                       inv_type, "
				+ "                                       sgstin, "
				+ "                                       cgstin, "
				+ "                         sec_7_act        AS Section7_IGST, "
				+ "                          diff_percent     AS DIFF_PERCENT, "
				+ "                                       'B2B' AS TABLE_TYPE, "
				+ "                                       supplier_inv_val "
				+ "                      FROM   getanx2_b2b_header "
				+ "                                WHERE  is_delete = false) "
				+ "                        UNION ALL "
				+ "                        (SELECT id, "
				+ "                                itc_ent  AS ITCEntitlement, "
				+ "                               supplier_inv_val AS DOC_AMT, "
				+ "                                cfs              AS CFS, "
				+ "                                supplier_inv_num, "
				+ "                                supplier_inv_date, "
				+ "                                inv_type, "
				+ "                                sgstin, "
				+ "                                cgstin, "
				+ "                         sec_7_act        AS Section7_IGST, "
				+ "                          diff_percent     AS DIFF_PERCENT, "
				+ "                                'DE'         AS TABLE_TYPE, "
				+ "                                supplier_inv_val "
				+ "                       FROM   getanx2_de_header "
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
				+ "                                                     SEZWP' "
				+ "       AS " + "       TABLE_TYPE, "
				+ "       supplier_inv_val "
				+ "       FROM   getanx2_sezwp_header "
				+ "       WHERE  is_delete = false) " + "        UNION ALL "
				+ "        (SELECT id, "
				+ "                itc_ent          AS ITCEntitlement, "
				+ "                supplier_inv_val AS DOC_AMT, "
				+ "                cfs              AS CFS, "
				+ "                supplier_inv_num, "
				+ "                supplier_inv_date, "
				+ "                inv_type, " + "                sgstin, "
				+ "                cgstin, "
				+ "                ''               AS Section7_IGST, "
				+ "                0                AS DIFF_PERCENT, "
				+ "                'SEZWP'          AS TABLE_TYPE, "
				+ "                supplier_inv_val "
				+ "         FROM   getanx2_sezwop_header "
				+ "         WHERE  is_delete = false)) A2 "
				+ "                   ON LT.a2_doc_date = A2.supplier_inv_date "
				+ "                       AND LT.a2_supplier_gstin = A2.sgstin "
				+ "                      AND LT.a2_recipient_gstin = A2.cgstin "
				+ "                       AND LT.a2_doc_type = A2.inv_type "
				+ "                    AND LT.a2_doc_num = A2.supplier_inv_num "
				+ "	WHERE " + condition.toString()
				+ "	ORDER  BY LT.a2_doc_num";

		return queryStr;
	}

	private Anx2InitiateReconReportDto convert(Object[] arr) {
		Anx2InitiateReconReportDto obj = new Anx2InitiateReconReportDto();
		obj.setPreviousReportTypeANX2(
				(arr[2] != null) ? arr[2].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[3] != null) ? arr[3].toString() : null);
		obj.setUserResponse((arr[4] != null) ? arr[4].toString() : null);
		obj.setSuggestedResponse((arr[5] != null) ? arr[5].toString() : null);
		obj.setCurrentMismatchReason(
				(arr[6] != null) ? arr[6].toString() : null);
		obj.setForcedMatchResponse((arr[7] != null) ? arr[7].toString() : null);
		obj.setInformationReportReference(
				(arr[8] != null) ? arr[8].toString() : null);
		obj.setResponseTaxPeriod(
				(arr[9] != null) ? "`".concat(arr[9].toString()) : null);
		obj.setPreviousResponseANX2(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setPreviousResponsePR(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setCurrentReportType((arr[12] != null) ? arr[12].toString() : null);
		obj.setDocumentNumberPR((arr[13] != null) ? arr[13].toString() : null);
		obj.setDocumentNumberANX2(
				(arr[14] != null) ? arr[14].toString() : null);
		obj.setRecipientGSTINANX2(
				(arr[15] != null) ? arr[15].toString() : null);
		obj.setRecipientGSTINPR((arr[16] != null) ? arr[16].toString() : null);
		obj.setDocTypeANX2((arr[17] != null) ? arr[17].toString() : null);
		obj.setDocTypePR((arr[18] != null) ? arr[18].toString() : null);
		obj.setDocumentDateANX2((arr[19] != null) ? arr[19].toString() : null);
		obj.setDocumentDatePR((arr[20] != null) ? arr[20].toString() : null);
		obj.setTaxableValueANX2((arr[21] != null) ? arr[21].toString() : null);
		obj.setTaxableValuePR((arr[22] != null) ? arr[22].toString() : null);
		obj.setCGSTANX2((arr[23] != null) ? arr[23].toString() : null);
		obj.setCGSTPR((arr[24] != null) ? arr[24].toString() : null);
		obj.setSGSTANX2((arr[25] != null) ? arr[25].toString() : null);
		obj.setSGSTPR((arr[26] != null) ? arr[26].toString() : null);
		obj.setIGSTANX2((arr[27] != null) ? arr[27].toString() : null);
		obj.setIGSTPR((arr[28] != null) ? arr[28].toString() : null);
		obj.setCessANX2((arr[29] != null) ? arr[29].toString() : null);
		obj.setCessPR((arr[30] != null) ? arr[30].toString() : null);
		obj.setPOSANX2((arr[31] != null) ? arr[31].toString() : null);
		obj.setPOSPR((arr[32] != null) ? arr[32].toString() : null);
		obj.setSavedResponseReportTypeANX2(
				(arr[33] != null) ? arr[33].toString() : null);
		obj.setDigiGSTSavedResponseANX2(
				(arr[34] != null) ? arr[34].toString() : null);
		obj.setGSTNSavedResponseANX2(
				(arr[35] != null) ? arr[35].toString() : null);
		obj.setMatchingScore((arr[36] != null) ? arr[36].toString() : null);
		obj.setReconTaxPeriod(
				(arr[37] != null) ? "`".concat(arr[37].toString()) : null);
		obj.setCalendarMonthPR((arr[38] != null) ? arr[38].toString() : null);
		obj.setDateofUploadANX2((arr[39] != null) ? arr[39].toString() : null);
		obj.setOrgMonthANX2((arr[40] != null) ? arr[40].toString() : null);
		obj.setOrgMonthPR((arr[41] != null) ? arr[41].toString() : null);
		obj.setSupplierNameANX2((arr[42] != null) ? arr[42].toString() : null);
		obj.setSupplierNamePR((arr[43] != null) ? arr[43].toString() : null);
		obj.setTotalTaxANX2((arr[44] != null) ? arr[44].toString() : null);
		obj.setTotalTaxPR((arr[45] != null) ? arr[45].toString() : null);
		obj.setInvoiceValueANX2((arr[46] != null) ? arr[46].toString() : null);
		obj.setInvoiceValuePR((arr[47] != null) ? arr[47].toString() : null);
		obj.setAvailableIGST((arr[48] != null) ? arr[48].toString() : null);
		obj.setAvailableCGST((arr[49] != null) ? arr[49].toString() : null);
		obj.setAvailableSGST((arr[50] != null) ? arr[50].toString() : null);
		obj.setAvailableCESS((arr[51] != null) ? arr[51].toString() : null);
		obj.setTableTypeANX2((arr[52] != null) ? arr[52].toString() : null);
		obj.setTableTypePR((arr[53] != null) ? arr[53].toString() : null);
		obj.setCFSFlagANX2((arr[54] != null) ? arr[54].toString() : null);
		obj.setClaimRefundFlagANX2(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setClaimRefundFlagPR((arr[56] != null) ? arr[56].toString() : null);
		obj.setSection7ofIGSTANX2(
				(arr[57] != null) ? arr[57].toString() : null);
		obj.setSection7ofIGSTPR((arr[58] != null) ? arr[58].toString() : null);
		obj.setAutoPopulateToRefundANX2(
				(arr[59] != null) ? arr[59].toString() : null);
		obj.setAutoPopulateToRefundPR(
				(arr[60] != null) ? arr[60].toString() : null);
		obj.setDifferentialPercentageANX2(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setOrgDocTypeANX2((arr[63] != null) ? arr[63].toString() : null);
		obj.setOrgDocTypePR((arr[64] != null) ? arr[64].toString() : null);
		obj.setOrgDocNumberANX2((arr[65] != null) ? arr[65].toString() : null);
		obj.setOrgDocNumberPR((arr[66] != null) ? arr[66].toString() : null);
		obj.setOrgDocDateANX2((arr[67] != null) ? arr[67].toString() : null);
		obj.setOrgDocDatePR((arr[68] != null) ? arr[68].toString() : null);
		obj.setSupplierGSTINPR((arr[69] != null) ? arr[69].toString() : null);
		obj.setUserID((arr[70] != null) ? arr[70].toString() : null);
		obj.setSourceFileName((arr[71] != null) ? arr[71].toString() : null);
		obj.setProfitCentre((arr[72] != null) ? arr[72].toString() : null);
		obj.setPlant((arr[73] != null) ? arr[73].toString() : null);
		obj.setDivision((arr[74] != null) ? arr[74].toString() : null);
		obj.setLocation((arr[75] != null) ? arr[75].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[76] != null) ? arr[76].toString() : null);
		obj.setUserAccess1((arr[77] != null) ? arr[77].toString() : null);
		obj.setUserAccess2((arr[78] != null) ? arr[78].toString() : null);
		obj.setUserAccess3((arr[79] != null) ? arr[79].toString() : null);
		obj.setUserAccess4((arr[80] != null) ? arr[80].toString() : null);
		obj.setUserAccess5((arr[81] != null) ? arr[81].toString() : null);
		obj.setUserAccess6((arr[82] != null) ? arr[82].toString() : null);
		obj.setGlCodeTaxableValue(
				(arr[83] != null) ? arr[83].toString() : null);
		obj.setGlCodeIGST((arr[84] != null) ? arr[84].toString() : null);
		obj.setGlCodeCGST((arr[85] != null) ? arr[85].toString() : null);
		obj.setGlCodeSGST((arr[86] != null) ? arr[86].toString() : null);
		obj.setGlCodeAdvaloremCess(
				(arr[87] != null) ? arr[87].toString() : null);
		obj.setGlCodeSpecificCess(
				(arr[88] != null) ? arr[88].toString() : null);
		obj.setGlCodeStateCess((arr[89] != null) ? arr[89].toString() : null);
		obj.setSupplyType((arr[90] != null) ? arr[90].toString() : null);
		obj.setCRDRPreGST((arr[91] != null) ? arr[91].toString() : null);
		obj.setSupplierType((arr[92] != null) ? arr[92].toString() : null);
		obj.setSupplierCode((arr[93] != null) ? arr[93].toString() : null);
		obj.setSupplierAddress1((arr[94] != null) ? arr[94].toString() : null);
		obj.setSupplierAddress2((arr[95] != null) ? arr[95].toString() : null);
		obj.setSupplierAddress3((arr[96] != null) ? arr[96].toString() : null);
		obj.setSupplierAddress4((arr[97] != null) ? arr[97].toString() : null);
		obj.setStateApplyingCess((arr[98] != null) ? arr[98].toString() : null);
		obj.setPortCode((arr[99] != null) ? arr[99].toString() : null);
		obj.setBillOfEntry((arr[100] != null) ? arr[100].toString() : null);
		obj.setBillOfEntryDate((arr[101] != null) ? arr[101].toString() : null);
		obj.setCIFValue((arr[102] != null) ? arr[102].toString() : null);
		obj.setCustomDuty((arr[103] != null) ? arr[103].toString() : null);
		obj.setQuantity((arr[104] != null) ? arr[104].toString() : null);
		obj.setCessAmountAdvalorem(
				(arr[105] != null) ? arr[105].toString() : null);
		obj.setCessAmountSpecific(
				(arr[106] != null) ? arr[106].toString() : null);
		obj.setStateCessAmount((arr[107] != null) ? arr[107].toString() : null);
		obj.setOtherValue((arr[108] != null) ? arr[108].toString() : null);
		obj.setPurchaseVoucherDate(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setPurchaseVoucherNumber(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setPostingDate((arr[111] != null) ? arr[111].toString() : null);
		obj.setPaymentVoucherNumber(
				(arr[112] != null) ? arr[112].toString() : null);
		obj.setPaymentDate((arr[113] != null) ? arr[113].toString() : null);
		obj.setContractNumber((arr[114] != null) ? arr[114].toString() : null);
		obj.setContractValue((arr[115] != null) ? arr[115].toString() : null);
		obj.setEWayBillNumber((arr[116] != null) ? arr[116].toString() : null);
		obj.setEWayBillDate((arr[117] != null) ? arr[117].toString() : null);
		obj.setUserDefinedField1(
				(arr[118] != null) ? arr[118].toString() : null);
		obj.setUserDefinedField2(
				(arr[119] != null) ? arr[119].toString() : null);
		obj.setUserDefinedField3(
				(arr[120] != null) ? arr[120].toString() : null);
		obj.setUserDefinedField4(
				(arr[121] != null) ? arr[121].toString() : null);
		obj.setUserDefinedField5(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setUserDefinedField6(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setUserDefinedField7(
				(arr[124] != null) ? arr[124].toString() : null);
		obj.setUserDefinedField8(
				(arr[125] != null) ? arr[125].toString() : null);
		obj.setUserDefinedField9(
				(arr[126] != null) ? arr[126].toString() : null);
		obj.setUserDefinedField10(
				(arr[127] != null) ? arr[127].toString() : null);
		obj.setUserDefinedField11(
				(arr[128] != null) ? arr[128].toString() : null);
		obj.setUserDefinedField12(
				(arr[129] != null) ? arr[129].toString() : null);
		obj.setUserDefinedField13(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setUserDefinedField14(
				(arr[131] != null) ? arr[131].toString() : null);
		obj.setUserDefinedField15(
				(arr[132] != null) ? arr[132].toString() : null);
		obj.setMatchingID((arr[133] != null) ? arr[133].toString() : null);
		obj.setRequestID(
				(arr[134] != null) ? "`".concat(arr[134].toString()) : null);
		obj.setIDA2((arr[135] != null) ? arr[135].toString() : null);
		obj.setIDPR((arr[136] != null) ? arr[136].toString() : null);
		obj.setInvoiceKeyPR((arr[137] != null) ? arr[137].toString() : null);
		obj.setInvoiceKeyA2((arr[138] != null) ? arr[138].toString() : null);
		obj.setSupplierGSTINANX2(
				(arr[139] != null) ? arr[139].toString() : null);

		return obj;

	}

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "ReconReports" + "_" + configId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private static void copy(File src, File dest) throws IOException {
		try (InputStream is = new FileInputStream(src);
				OutputStream os = new FileOutputStream(dest);) {

			// buffer size 1K
			byte[] buf = new byte[1024];

			int bytesRead;
			while ((bytesRead = is.read(buf)) > 0) {
				os.write(buf, 0, bytesRead);
			}
		}
	}

}
