package com.ey.advisory.controllers.anexure1;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.AsyncFileStatusRptTypeEntity;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.AsyncFileStatusReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */

@RestController
@Slf4j
public class AsyncReportDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AsyncFileStatusReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	@Autowired
	@Qualifier("AsyncFileStatusReportTypeRepository")
	AsyncFileStatusReportTypeRepository requestChildConfigRepo;

	@PostMapping(value = "/ui/downloadFileStatusCsvReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Download CSV Report Controller";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download CSV Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}
			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Download CSV Report Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reqDto.getType());
			
			if ("OUTWARD".equalsIgnoreCase(entity.getDataType())
					&& !ReportTypeConstants.EINVOICE_RECON
							.equalsIgnoreCase(entity.getReportCateg())
					|| "OUTWARD_1A".equalsIgnoreCase(entity.getDataType())
							&& !ReportTypeConstants.EINVOICE_RECON
									.equalsIgnoreCase(
											entity.getReportCateg())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.ASYNC_FILE_PROCESS, jobParams.toString(),
						userName, 1L, null, null);
			} else if ("INWARD".equalsIgnoreCase(entity.getDataType())
					&& "FileStatus".equalsIgnoreCase(entity.getReportCateg())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.ASYNC_FILE_PROCESS_INWARD,
						jobParams.toString(), userName, 1L, null, null);
			} else if ("INWARD".equalsIgnoreCase(entity.getDataType())
					&& "DataStatus".equalsIgnoreCase(entity.getReportCateg())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.ASYNC_REPORT_DATA_STATUS_INWARD,
						jobParams.toString(), userName, 1L, null, null);
			} else if ("GSTR3B".equalsIgnoreCase(entity.getDataType())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.ASYNC_GSTR3B_SAVE_SUBMIT,
						jobParams.toString(), userName, 1L, null, null);
			} else if ("OUTWARD".equalsIgnoreCase(entity.getDataType())
					&& ReportTypeConstants.EINVOICE_RECON
							.equalsIgnoreCase(entity.getReportCateg())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.EINVOICE_RECON, jobParams.toString(),
						userName, 1L, null, null);
			} else if ("vendor_payment"
					.equalsIgnoreCase(entity.getDataType())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.REVERSAL_180_DAYS_FILE_DOWNLOAD,
						jobParams.toString(), userName, 1L, null, null);
			}
			// GSTR7 TXN
			else if ("gstr7txn"
					.equalsIgnoreCase(reqDto.getDataType())) {
				asyncJobsService.createJob(groupCode,
						JobConstants.GSTR7_TXN_API_PUSH_REPORT,
						jobParams.toString(), userName, 1L, null, null);
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s for data type: %s ",
						jobParams.toString(), reqDto.getDataType());
				LOGGER.debug(msg);
			}

			String reportType = getReportType(reqDto.getType(),
					reqDto.getStatus());

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private static String getReportType(String type, String status) {
		String reportType = null;
		try {
			String status1 = null;
			if (!Strings.isNullOrEmpty(status)) {

				if (status.equalsIgnoreCase("active")) {
					status1 = "Active";
				} else if (status.equalsIgnoreCase("inactive")) {
					status1 = "Inactive";
				}
			}
			switch (type) {

			case ReportTypeConstants.ERROR_BV:
				reportType = "Business Validation ";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
				break;

			case ReportTypeConstants.ERROR:
				reportType = "Error ";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
				break;

			case ReportTypeConstants.PROCESSED_RECORDS:
				reportType = "Processed Records ";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1;
				break;

			case ReportTypeConstants.ERROR_SV:
				reportType = "Structural Validation";
				break;

			case ReportTypeConstants.TOTAL_ERRORS:
				reportType = "Total Errors";
				break;

			case ReportTypeConstants.TOTAL_RECORDS:
				reportType = "Total Records";
				break;

			case ReportTypeConstants.GSTR3B_SAVESUBMIT_REPORT:
				reportType = "Save Submit";
				break;

			case ReportTypeConstants.EINVPROCESS:
				reportType = "Einvoice Processed Records";
				break;
				
			case "ITC RCM Ledger (Opening Balance)":
				reportType = "ITC RCM Ledger (Opening Balance)";
				break;
				
			case "ITC Reversal & Re-Claim Ledger (Opening Balance)":
				reportType = "ITC Reversal & Re-Claim Ledger (Opening Balance)";
				break;
				
				//RCM
			case "ITC RCM Ledger":
				reportType = "ITC RCM Ledger";
				break;
				
			case "Negative Liability Ledger":
				reportType = "Negative Liability Ledger";
				break;
				
				
			case "IMS Action Error 2AvsPR (Recon Result)":
				reportType = "IMS Action Error 2AvsPR (Recon Result)";
				break;
				
			case "A":
				reportType = "Accepted Records";
				break;
				
			case "A,R,P,N":
				reportType = "All Records";
				break;
				
			case "R":
				reportType = "Rejected Records";
				break;
				
			case "P":
				reportType = "Pending Records";
				break;
				
			case "N":
				reportType = "No Action Records";
				break;
				
			case "Accepted Records":
				reportType = "Accepted Records";
				break;
				
			case "All Records":
				reportType = "All Records";
				break;
				
			case "Rejected Records":
				reportType = "Rejected Records";
				break;
				
			case "Pending Records":
				reportType = "Pending Records";
				break;
				
			case "No Action Records":
				reportType = "No Action Records";
				break;
				
			case "Detailed Summary Report":
				reportType = "Detailed Summary Report";
				break;
				
			case "Consolidated GL Records" :
				reportType = "Consolidated GL Records";
				break;
				
				

			default:
				reportType = type;

			}

			return reportType;
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
			return reportType;
		}
	}

	@GetMapping(value = "/ui/fileStatusDownloadDocument")
	public void fileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			LOGGER.debug("inside Async Report file Downloads");

			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			String id = request.getParameter("configId");
			// String id="15723";//15714//gstr3b-15723//hsn summery15711
			Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
					.findById(Long.valueOf(id));

			Optional<AsyncFileStatusRptTypeEntity> chdEntityList = requestChildConfigRepo
					.findById(Long.valueOf(id));

			String fileFolder = null;
			String fileName = null;
			Document document = null;
			if (entity.isPresent()) {

				fileName = entity.get().getFilePath();
				if ("GSTR9 Dump Reports"
						.equalsIgnoreCase(entity.get().getReportCateg())) {
					fileFolder = "Gstr9DumpReports";

				} else if (GSTConstants.GSTR6ISDANNEX
						.equalsIgnoreCase(entity.get().getReportCateg())) {
					fileFolder = "GSTR6IsdAnnexReport";

				} else if ("ISD Distribution"
						.equalsIgnoreCase(entity.get().getReportCateg())) {
					fileFolder = "PdfGstr6Reports";
				} else if (ReportTypeConstants.GSTR1_PDF
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "PdfGstr1Reports";
				} else if (ReportTypeConstants.ITC_REVERSAL_RULE_42
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.COMMONCREDITDOWNLOAD;
				} else if (ReportTypeConstants.STOCK_TRACKING_REPORT
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.STOCK_TRACKING_REPORT;
				} else if (ReportTypeConstants.DRC01B
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.DRC_REPORT_DOWNLOAD;
				} else if (ReportTypeConstants.DRC01C
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.DRC01C_REPORT_DOWNLOAD;
				} else if (ReportTypeConstants.GSTR8_PROCESSED_REPORT
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.GSTR8_DOWNLOAD_REPORT;
				} else if (ReportTypeConstants.CASH_LEDGER
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.lEDGER_REPORT_DOWNLOAD;
				}

				else if (ReportTypeConstants.CREDIT_LEDGER
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.lEDGER_REPORT_DOWNLOAD;
				} else if (ReportTypeConstants.INWARD_EINVOICE_SUMMARY_REPORT
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "EinvoiceSummaryReport";
				}

				else if ("Liability Ledger"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "LiabilityLedgerReport";
				} else if ("Vendor E-Invoice Applicability Status"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "vendorGstinDetailsReportDownload";
				} else if ("Reversal & Reclaim Ledger"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "LiabilityLedgerReport";
				} else if (ReportTypeConstants.GSTR3_PDF
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "PdfGstr3bReports";
				} else if ("totalrecords"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.EXCEPTIONAL_TAGGING_REPORT;
				} else if ("processed"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.EXCEPTIONAL_TAGGING_REPORT;
				} else if ("error"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.EXCEPTIONAL_TAGGING_REPORT;
				}

				else if ("Recon Result"
						.equalsIgnoreCase(entity.get().getReportCateg())) {

					if ("Error 2A-6AvsPR Report (Recon Result)"
							.equalsIgnoreCase(entity.get().getReportType())) {
						fileFolder = "Gstr2UserResponseUploads";
					} else if ("Error 2BvsPR Report (Recon Result)"
							.equalsIgnoreCase(entity.get().getReportType())) {
						fileFolder = "Gstr2bprUserResponseUploads";

					}
				} else if ("Liability Payment Details"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "GSTR3BLiabilityDashboard";
				} else if ("Detailed Report (Line Item level)"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "ReconResultConsolidatedReports";
				} else if (ReportTypeConstants.GSTR6_PDF
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "PdfGstr3bReports";
				} else if (ReportTypeConstants.GSTR7_PDF
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "PdfGstr3bReports";
				} /*else if (ReportTypeConstants.GSTR7_PDF
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "PdfGstr3bReports";
				}*/ else if (ReportTypeConstants.GSTR3B_ENTITY_SUMMARY_REPORT
						.equalsIgnoreCase(entity.get().getReportType())
						&& APIConstants.GSTR3B.toUpperCase().equalsIgnoreCase(
								entity.get().getReportCateg())) {
					fileFolder = ConfigConstants.GSTR3B_SUMMARY_DOWNLOAD_REPORT;
				} else if (ReportTypeConstants.GSTR8_PDF
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "PdfGstr3bReports";
				} else if (ReportTypeConstants.GSTR1A_AS_UPLOADED
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_GSTR1A_REPORT;
				}else if (ReportTypeConstants.GSTR1A_ASPERROR
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_GSTR1A_REPORT;
				}else if (ReportTypeConstants.GSTR1A_GSTNERROR
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_GSTR1A_REPORT;
				}
				else if (ReportTypeConstants.GSTR1AENTITYLEVEL
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_GSTR1A_REPORT;
				}
				else if (ReportTypeConstants.PROCESSED_RECORDS
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if (ReportTypeConstants.ERROR
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if (ReportTypeConstants.TOTAL_RECORDS
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("IMS Records Report"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("ITC RCM Ledger (Opening Balance)"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "LiabilityLedgerReport";
				}else if ("ITC Reversal & Re-Claim Ledger (Opening Balance)"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = "LiabilityLedgerReport";
				}else if ("IMS Action Error 2BvsPR (Recon Result)"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.GSTR2BPR_IMS_USERRESPONSE_UPLOADS;
				}
				else if ("IMS Action Error 2AvsPR (Recon Result)"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.GSTR2APR_IMS_USERRESPONSE_UPLOADS;
				}else if ("A"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("R"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("P"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("N"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("A,R,P,N"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("Accepted Records"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("Rejected Records"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("Pending Records"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("No Action Records"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}else if ("All Records"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}
				else if ("Detailed Summary Report"
						.equalsIgnoreCase(entity.get().getReportType())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}

				else {
					fileFolder = "Anx1FileStatusReport";
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Downloading Document with fileName : %s and Folder Name: %s",
							fileName, fileFolder);
					LOGGER.debug(msg);
				}

				if (Strings.isNullOrEmpty(entity.get().getDocId())) {
					document = DocumentUtility.downloadDocument(fileName,
							fileFolder);
				} else {
					String docId = entity.get().getDocId();
					String msg = String.format(
							"Doc Id is available for File Name %s and Doc Id %s",
							fileName, docId);
					LOGGER.debug(msg);
					document = DocumentUtility
							.downloadDocumentByDocId(entity.get().getDocId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Downloading Document with fileName : %s and Folder Name: %s",
							fileName, fileFolder);
					LOGGER.debug(msg);
				}
			} else if (chdEntityList.isPresent()) {
				AsyncFileStatusRptTypeEntity chdEntity = chdEntityList.get();
				fileName = chdEntity.getFilePath();
				if ("Recon Result".equalsIgnoreCase(fileStatusDownloadReportRepo
						.findById(chdEntity.getReportDwnldId()).get()
						.getReportCateg())) {
					fileFolder = "ReconResultConsolidatedReports";
				}

				if ("ProcessedSummary"
						.equalsIgnoreCase(fileStatusDownloadReportRepo
								.findById(chdEntity.getReportDwnldId()).get()
								.getReportCateg())
						|| "ReviewSummary"
								.equalsIgnoreCase(fileStatusDownloadReportRepo
										.findById(chdEntity.getReportDwnldId())
										.get().getReportCateg()) 
								|| "GSTR3B"
								.equalsIgnoreCase(fileStatusDownloadReportRepo
										.findById(chdEntity.getReportDwnldId()).get()
										.getReportCateg())) {
					fileFolder = "Anx1FileStatusReport";
				}
				if ("InvoiceManagement"
						.equalsIgnoreCase(fileStatusDownloadReportRepo
								.findById(chdEntity.getReportDwnldId()).get()
								.getReportCateg())) {
					fileFolder = "Anx1FileStatusReport";
				}
				if ("ISDAnnexure".equalsIgnoreCase(fileStatusDownloadReportRepo
						.findById(chdEntity.getReportDwnldId()).get()
						.getReportType())) {
					fileFolder = "GSTR6IsdAnnexReport";
				}
				if ("GSTR-2A".equalsIgnoreCase(fileStatusDownloadReportRepo
						.findById(chdEntity.getReportDwnldId()).get()
						.getReportCateg())) {
					fileFolder = "Anx1FileStatusReport";
				}
				if ("E-invoice".equalsIgnoreCase(fileStatusDownloadReportRepo
						.findById(chdEntity.getReportDwnldId()).get()
						.getReportCateg())) {
					fileFolder = "JsonReportDownload";
				}
				if ("IMS".equalsIgnoreCase(fileStatusDownloadReportRepo
						.findById(chdEntity.getReportDwnldId()).get()
						.getReportCateg())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}if ("Consolidated Report – Action Wise".equalsIgnoreCase(fileStatusDownloadReportRepo
						.findById(chdEntity.getReportDwnldId()).get()
						.getReportCateg())) {
					fileFolder = ConfigConstants.ASYNC_IMS_REPORT;
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Downloading Document with fileName : %s and Folder Name: %s",
							fileName, fileFolder);
					LOGGER.debug(msg);
				}

				String docId = chdEntity.getDocId();
				fileName = chdEntity.getFilePath();

				if (docId != null && !docId.isEmpty()) {

					String msg = String.format(
							"Doc Id is available for File Name %s and Doc Id %s",
							fileName, docId);
					LOGGER.debug(msg);
					document = DocumentUtility.downloadDocumentByDocId(docId);

				} else {

					document = DocumentUtility.downloadDocument(fileName,
							fileFolder);

					if (fileName == null) {
						throw new AppException("FileName is null");

					}

				}

			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}

			if (document == null) {
				LOGGER.error("document is empty");
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}

		} catch (Exception ex) {
			String msg = "Exception while downloading files ";
			LOGGER.error(msg, ex);
		}

	}

}
