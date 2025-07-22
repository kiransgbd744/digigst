package com.ey.advisory.app.filereport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.core.config.ConfigConstants;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@Component("ReportFileStatusServiceImpl")
public class ReportFileStatusServiceImpl implements ReportFileStatusService {

	@Autowired
	@Qualifier("ReportFileStatusDaoImpl")
	ReportFileStatusDao reportFileStatusDao;

	@Autowired
	@Qualifier("FileStatusDownloadReportRepository")
	FileStatusDownloadReportRepository fileStatusRepo;

	@Autowired
	private CommonUtility commonUtility;

	@Override
	public Pair<List<ReportFileStatusReportDto>, Integer> getFileStatusDetails(
			String userName, List<String> reportCateg, String fromDate,
			String toDate, int pageSize, int pageNum, List<String> dataType,
			Long entityId) {

		int recordsToStart = pageNum;

		int noOfRowstoFetch = pageSize;

		int totalCount = 0;

		Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
				Direction.DESC, "id");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime fromDate1 = LocalDate.parse(fromDate, formatter)
				.atStartOfDay();
		LocalDateTime toDate1 = LocalDate.parse(toDate, formatter)
				.atStartOfDay().plusDays(1);
		List<FileStatusDownloadReportEntity> fileStatusList = new ArrayList<>();
		boolean isusernamereq = true;
		String ansfromques1 = "B";
		ansfromques1 = commonUtility.getAnsFromQue(entityId,
				"Multiple User Access to Async Reports");
		if ("A".equalsIgnoreCase(ansfromques1)) {
			isusernamereq = false;
		} else if ("B".equalsIgnoreCase(ansfromques1)) {
			isusernamereq = true;
		}
		if (reportCateg != null && !reportCateg.isEmpty()) {
			if (isusernamereq) {
				fileStatusList = fileStatusRepo
						.findAllByCreatedByAndDataTypeInAndReportCategInAndCreatedDateBetween(
								userName, dataType, reportCateg, fromDate1,
								toDate1, pageReq);

				totalCount = fileStatusRepo
						.countByCreatedByAndDataTypeInAndReportCategInAndCreatedDateBetween(
								userName, dataType, reportCateg, fromDate1,
								toDate1);
			} else {
				fileStatusList = fileStatusRepo
						.findAllByDataTypeInAndReportCategInAndCreatedDateBetween(
								dataType, reportCateg, fromDate1, toDate1,
								pageReq);

				totalCount = fileStatusRepo
						.countByDataTypeInAndReportCategInAndCreatedDateBetween(
								dataType, reportCateg, fromDate1, toDate1);

			}

		} else if (reportCateg.isEmpty() && !dataType.isEmpty()) {
			if (isusernamereq) {
				fileStatusList = fileStatusRepo
						.findAllByCreatedByAndDataTypeInAndCreatedDateBetween(
								userName, dataType, fromDate1, toDate1,
								pageReq);

				totalCount = fileStatusRepo
						.countByCreatedByAndDataTypeInAndCreatedDateBetween(
								userName, dataType, fromDate1, toDate1);
			} else {
				fileStatusList = fileStatusRepo
						.findAllByDataTypeInAndCreatedDateBetween(dataType,
								fromDate1, toDate1, pageReq);

				totalCount = fileStatusRepo
						.countByDataTypeInAndCreatedDateBetween(dataType,
								fromDate1, toDate1);
			}
		} else {
			if (isusernamereq) {

				fileStatusList = fileStatusRepo
						.findAllByCreatedByAndCreatedDateBetween(userName,
								fromDate1, toDate1, pageReq);

				totalCount = fileStatusRepo
						.countByCreatedByAndCreatedDateBetween(userName,
								fromDate1, toDate1);
			} else {
				fileStatusList = fileStatusRepo.findAllByCreatedDateBetween(
						fromDate1, toDate1, pageReq);

				totalCount = fileStatusRepo.countByCreatedDateBetween(fromDate1,
						toDate1);
			}
		}
		Comparator<FileStatusDownloadReportEntity> createdDate = new Comparator<FileStatusDownloadReportEntity>() {

			public int compare(FileStatusDownloadReportEntity c1,
					FileStatusDownloadReportEntity c2) {
				return (c2.getCreatedDate()).compareTo(c1.getCreatedDate());
			}
		};
		Collections.sort(fileStatusList, createdDate);

		List<ReportFileStatusReportDto> response = fileStatusList.stream()
				.map(o -> convert(o)).collect(Collectors.toList());

		return new Pair<>(response, totalCount);
	}

	private ReportFileStatusReportDto convert(
			FileStatusDownloadReportEntity o) {
		ReportFileStatusReportDto dto = new ReportFileStatusReportDto();
		dto.setRequestId(o.getId());
		dto.setReportCateg(o.getReportCateg());
		if ("FileStatus".equalsIgnoreCase(o.getReportCateg())) {
			dto.setReportCateg("File Status");
		} else if ("Gstr1".equalsIgnoreCase(o.getReportCateg())) {
			dto.setReportCateg("GSTR1");
		}
		dto.setReportType(getReportType(o.getReportType(), o.getStatus()));

		if (!Strings.isNullOrEmpty(o.getUpldFileName())) {
			dto.setFileName(o.getUpldFileName());
		} else if (!Strings.isNullOrEmpty(o.getFilePath())) {
			dto.setFileName(o.getFilePath());
		}

		dto.setInitiatedOn(EYDateUtil.toISTDateTimeFromUTC(o.getCreatedDate()));
		dto.setInitiatedBy(o.getCreatedBy());
		dto.setStatus(o.getReportStatus());
		dto.setDataType(o.getDataType());
		if ("vendor_payment".equalsIgnoreCase(o.getDataType())) {
			dto.setDataType("GSTR3B");
			dto.setReportCateg("GSTR3B");
			if ("totalrecords".equalsIgnoreCase(o.getReportType())) {
				dto.setReportType("VendorPaymentReference_API_Total Records");
			} else if ("error".equalsIgnoreCase(o.getReportType())) {
				dto.setReportType("VendorPaymentReference_API_Error Records");
			} else if ("processed".equalsIgnoreCase(o.getReportType())) {
				dto.setReportType(
						"VendorPaymentReference_API_Processed Records");
			}
		}

		if ("OUTWARD".equalsIgnoreCase(o.getDataType())
				|| "outward".equalsIgnoreCase(o.getDataType())) {
			dto.setDataType("Outward");
		} else if ("OUTWARD_1A".equalsIgnoreCase(o.getDataType())
				|| "outward_1a".equalsIgnoreCase(o.getDataType())) {
			dto.setDataType("Outward - GSTR 1A");
		} else if ("INWARD".equalsIgnoreCase(o.getDataType())
				|| "inward".equalsIgnoreCase(o.getDataType())) {
			dto.setDataType("Inward");
		} else if ("All".equalsIgnoreCase(o.getDataType())
				&& "GSTR1,GSTR3B".equalsIgnoreCase(o.getReportCateg()))
			dto.setDataType("All Complaints");
		return dto;
	}

	private String getReportType(String reportType, String status) {
		LOGGER.debug("Report Type {} ", reportType);
		try {
			String status1 = null;
			if (!Strings.isNullOrEmpty(status)) {
				if (status.equalsIgnoreCase("active")) {
					status1 = "Active";
				} else if (status.equalsIgnoreCase("inactive")) {
					status1 = "Inactive";
				}
			}
			switch (reportType) {

			case ReportTypeConstants.ERROR_BV:
				reportType = "Business Validation-";
				if (!Strings.isNullOrEmpty(status))
					reportType += status1;
				break;

			case ReportTypeConstants.PROCESSED_RECORDS:
				reportType = "Processed Records ";
				if (!Strings.isNullOrEmpty(status))
					reportType += status1;
				break;

			case ReportTypeConstants.ERROR:
				reportType = "Error ";
				if (!Strings.isNullOrEmpty(status))
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

			case ReportTypeConstants.AS_UPLOADED:
				reportType = "GSTR1 Transactional As Processed";
				break;

			case ReportTypeConstants.ASPERROR:
				reportType = "GSTR1 Consolidated Asp Error";
				break;

			case ReportTypeConstants.GSTNERROR:
				reportType = "GSTR1 Consolidated Gstn Error";
				break;

			case ReportTypeConstants.GSTR1A_AS_UPLOADED:
				reportType = "GSTR1A Transactional As Processed";
				break;

			case ReportTypeConstants.GSTR1A_ASPERROR:
				reportType = "GSTR1A Consolidated Asp Error";
				break;

			case ReportTypeConstants.GSTR1A_GSTNERROR:
				reportType = "GSTR1A Consolidated Gstn Error";
				break;

			case ReportTypeConstants.GSTR1ENTITYLEVEL:
				reportType = "GSTR1 EntityLevel Summary";
				break;

			case ReportTypeConstants.GSTR1AENTITYLEVEL:
				reportType = "GSTR1A EntityLevel Summary";
				break;

			case ReportTypeConstants.GSTR2PROCESS:
				reportType = "GSTR2/GSTR6 Processed Records";
				break;

			case ReportTypeConstants.GSTR6PROCESS:
				reportType = "GSTR6 Processed Records";
				break;

			case ReportTypeConstants.GSTR3B_SAVESUBMIT_REPORT:
				reportType = "Save Submit";
				break;

			case ReportTypeConstants.INWARD:
				reportType = "GSTR3B Inward";
				break;

			case ReportTypeConstants.OUTWARD:
				reportType = "GSTR3B Outward";
				break;

			case ReportTypeConstants.GSTR1GETCALL:
				reportType = "GSTR1 Api Call";
				break;

			case ReportTypeConstants.GSTR3BGETCALL:
				reportType = "GSTR3B Api Call";
				break;

			case ReportTypeConstants.GSTR8GETCALL:
				reportType = "GSTR8 API Call";
				break;

			case ReportTypeConstants.GSTR1ENVPROCESS:
				reportType = "GSTR1_E_Invoice_Records";
				break;

			case "GSTR-2A (Compete)":
				reportType = "GSTR-2A (Complete)";
				break;

			case ReportTypeConstants.EINVPROCESS:
				reportType = "Processed Records";
				break;

			case ReportTypeConstants.EINVCONSOLIDATED:
				reportType = "EINV Consolidated";
				break;

			case ReportTypeConstants.EINV_NOT_APPLICABLE:
				reportType = "EINV Not Applicable";
				break;

			case ReportTypeConstants.EINV_APPLICABLE:
				reportType = "EINV Applicable";
				break;
			case ReportTypeConstants.EINV_IRN_GENERATE:
				reportType = "EINV IRN Generate";
				break;
			case ReportTypeConstants.EINV_IRN_CANCELED:
				reportType = "EINV IRN Canceled";
				break;

			case ReportTypeConstants.EINV_ERROR:
				reportType = "Error DigiGST IRP";
				break;
			case ReportTypeConstants.EINV_ERROR_FROM_IRP:
				reportType = "Error From IRP";
				break;

			case ReportTypeConstants.EWB_NOT_APPLICABLE:
				reportType = "EWB Not Applicable";
				break;

			case ReportTypeConstants.EWB_APPLICABLE:
				reportType = "EWB Applicable";
				break;
			case ReportTypeConstants.EWB_GENERATED:
				reportType = "EWB Generated";
				break;
			case ReportTypeConstants.EWB_CANCEL:
				reportType = "EWB Canceled";
				break;
			case ReportTypeConstants.EWB_ERROR:
				reportType = "EWB Error DigiGST+NIC";
				break;
			case ReportTypeConstants.EWB_ERROR_FROM_NIC:
				reportType = "EWB Error From Nic";
				break;

			case ReportTypeConstants.RET_NOT_APPLICABLE:
				reportType = "RET Not Applicable";
				break;

			case ReportTypeConstants.RET_APPLICABLE:
				reportType = "RET Applicable";
				break;
			case ReportTypeConstants.RET_PROCESS:
				reportType = "RET Process";
				break;
			case ReportTypeConstants.RET_INFO:
				reportType = "RET Info Message";
				break;
			case ReportTypeConstants.RET_ERROR:
				reportType = "RET Errors";
				break;
			case "ProcessedRecords":
				reportType = "InvoiceMgt_ProcessedRecords";
				break;

			case "ErrorRecords":
				reportType = "InvoiceMgt_ErrorRecords";
				break;

			case "ProcessedInfoData":
				reportType = "InvoiceMgt_ProcessedInfoData";
				break;

			case "data":
				reportType = "InvoiceMgt_data";
				break;

			case "Gstr1Adata":
				reportType = "Gstr1A InvoiceMgt_data";
				break;
			case "InwardData":
				reportType = "InvoiceMgt_data";
				break;

			case ReportTypeConstants.GSTR9_API_CALL:
				reportType = "GSTR9 API Call";
				break;

			case ReportTypeConstants.GSTR8A_API_CALL:
				reportType = "Table-8A API Call";
				break;

			case ReportTypeConstants.GSTR1A_API_CALL:
				reportType = "GSTR1A API Call";
				break;

			case ReportTypeConstants.GSTR2X_API_CALL:
				reportType = "GSTR2X API Call";
				break;

			case ReportTypeConstants.REV_RESP180_PROCESSED:
				reportType = "Processed Records";
				break;

			case ReportTypeConstants.ITC04_API_CALL:
				reportType = "ITC04 API Call";
				break;

			case ReportTypeConstants.GSTR7_API_CALL:
				reportType = ReportTypeConstants.GSTR7_API_CALL;
				break;

			case ReportTypeConstants.GSTR6_API_CALL:
				reportType = ReportTypeConstants.GSTR6_API_CALL;
				break;

			case ReportTypeConstants.GSTR9_DIGIGST_COMPUTE:
				reportType = ReportTypeConstants.GSTR9_DIGIGST_COMPUTE;
				break;

			case ReportTypeConstants.VENDOR_COMPLIANCE_RATING:
				reportType = "Vendor Compliance History";
				break;

			case ReportTypeConstants.VENDOR_COMPLIANCE_SUMMARY:
				reportType = ReportTypeConstants.VENDOR_COMPLIANCE_SUMMARY;
				break;
			case ReportTypeConstants.CUSTOMER_COMPLIANCE_RATING:
				reportType = "Customer Compliance History";
				break;

			case ReportTypeConstants.CUSTOMER_COMPLIANCE_SUMMARY:
				reportType = ReportTypeConstants.CUSTOMER_COMPLIANCE_SUMMARY;
				break;
			case ReportTypeConstants.MY_COMPLIANCE_RATING:
				reportType = "My Compliance History";
				break;

			case ReportTypeConstants.MY_COMPLIANCE_SUMMARY:
				reportType = ReportTypeConstants.MY_COMPLIANCE_SUMMARY;
				break;
			case ReportTypeConstants.GSTR3B_180DAYS_REV_RESP:
				reportType = "180 Days Reversal response report";
				break;
			case ReportTypeConstants.STOCK_TRANSFER:
				reportType = "Stock Transfer";
				break;
			case ReportTypeConstants.EWB_REPORT:
				reportType = "EWB";
				break;
			case ReportTypeConstants.GSTR6_ISD_ANNEX:
				reportType = "Credit Distribution Annexure Report";
				break;

			case ReportTypeConstants.SHIPPING_BILL:
				reportType = "Missing Shipping Bill Details";
				break;
			case ReportTypeConstants.EWB_DETAIL_REPORT:
				reportType = "EWB Detail Report";
				break;
			case ReportTypeConstants.GSTR6A_SUMMARY:
				reportType = "GSTR6A Summary";
				break;
			case ReportTypeConstants.RATE_LEVEL_REPORT:
				reportType = "Outward - Rate Level (Limited Column)";
				break;

			case "Consolidated 2A-6AvsPR Report (Recon Result)":
				reportType = "Consolidated 2A-6AvsPR Report (Recon Result)";
				break;

			case "Consolidated 2BvsPR Report (Recon Result)":
				reportType = "Consolidated 2BvsPR Report (Recon Result)";
				break;

			case ReportTypeConstants.GSTR3B_Table4_Transactional:
				reportType = "GSTR3B Table4 Transactional Report";
				break;

			case "ISD Document":
				reportType = "ISD Document";
				break;

			case "Error 2A-6AvsPR Report (Recon Result)":
				reportType = "Error 2A-6AvsPR Report (Recon Result)";
				break;

			case "Error 2BvsPR Report (Recon Result)":
				reportType = "Error 2BvsPR Report (Recon Result)";
				break;

			case "InProcRecords":
				reportType = "InvoiceMgt_ProcessedRecords";
				break;

			case "InErrRecords":
				reportType = "InvoiceMgt_ErrorRecords";
				break;

			case "InProcInfoData":
				reportType = "InvoiceMgt_ProcessedInfoData";
				break;

			case ReportTypeConstants.TOTAL_180DAYS:
				reportType = "180daysPaymentReference_Total Records";
				break;

			case ReportTypeConstants.ERROR_180DAYS:
				reportType = "180daysPaymentReference_Error Records";
				break;

			case ReportTypeConstants.PROCESSED_180DAYS:
				reportType = "180daysPaymentReference_Processed Records";
				break;

			case "Sales Register Total Records":
				reportType = "Sales Register Total Records";
				break;

			case "Sales Register Processed Records":
				reportType = "Sales Register Processed Records";
				break;

			case "Sales Register Error Records":
				reportType = "Sales Register Error Records";
				break;

			case "GSTR-2A_Get_Records":
				reportType = "GSTR-2A_Get_Records";
				break;

			case "Liability Payment Details":
				reportType = "Liability Payment Details";
				break;

			case ReportTypeConstants.HSN_SUMMARY:
				reportType = "HSNSummaryReport";
				break;
			case ReportTypeConstants.GSTR1_PDF:
				reportType = ReportTypeConstants.GSTR1_PDF;
				break;
			case ReportTypeConstants.GSTR1A_PDF:
				reportType = ReportTypeConstants.GSTR1A_PDF;
				break;
			case ReportTypeConstants.GSTR3_PDF:
				reportType = ReportTypeConstants.GSTR3_PDF;
				break;

			case ReportTypeConstants.ITC_REVERSAL_RULE_42:
				reportType = ReportTypeConstants.ITC_REVERSAL_RULE_42;
				break;

			case ReportTypeConstants.STOCK_TRACKING_REPORT:
				reportType = ReportTypeConstants.STOCK_TRACKING_REPORT;
				break;

			case ReportTypeConstants.HSNUPLOAD:
				reportType = ReportTypeConstants.HSNUPLOAD;
				break;

			case ReportTypeConstants.DRC01B:
				reportType = ReportTypeConstants.DRC01B;
				break;

			case ReportTypeConstants.DRC01C:
				reportType = ReportTypeConstants.DRC01C;
				break;

			case "Credit Ledger":
				reportType = "Credit Ledger";
				break;

			case "Total Records":
				reportType = "Total Records";
				break;

			case "Error Records":
				reportType = "Error Records";
				break;

			case "Processed Records":
				reportType = "Processed Records";
				break;

			case ReportTypeConstants.INWARDEINVOICE_JSONS:
				reportType = ReportTypeConstants.INWARDEINVOICE_JSONS;
				break;

			case ReportTypeConstants.INWARD_EINVOICE_SUMMARY_REPORT:
				reportType = ReportTypeConstants.INWARD_EINVOICE_SUMMARY_REPORT;
				break;

			case ReportTypeConstants.INWARD_EINVOICE_DETAILED_REPORT:
				reportType = ReportTypeConstants.INWARD_EINVOICE_DETAILED_REPORT;
				break;

			case "Cash Ledger":
				reportType = "Cash Ledger";
				break;

			case "Liability Ledger":
				reportType = "Liability Ledger";
				break;

			case "Vendor E-Invoice Applicability Status":
				reportType = "Vendor E-Invoice Applicability Status";
				break;

			case "Reversal & Reclaim Ledger":
				reportType = "Reversal & Reclaim Ledger";
				break;

			case ReportTypeConstants.NESTED_ARRAY:
				reportType = ReportTypeConstants.NESTED_ARRAY;
				break;

			case "Detailed Report (Line Item level)":
				reportType = "Detailed Report (Line Item level)";
				break;
			case ReportTypeConstants.GSTR6_PDF:
				reportType = ReportTypeConstants.GSTR6_PDF;
				break;
			case ReportTypeConstants.GSTR7_PDF:
				reportType = ReportTypeConstants.GSTR7_PDF;
				break;
			case "Entity Level Summary":
				reportType = "Entity Level Summary";
				break;
			case "GSTR3B Entity Level Summary":
				reportType = "GSTR3B Entity Level Summary";
				break;
			case "All Vendor":
				reportType = "All Vendor";
				break;
			case "Compliance Vendor":
				reportType = "Compliance Vendor";
				break;
			case "Non-Compliance Vendor":
				reportType = "Non-Compliance Vendor";
				break;
			case ReportTypeConstants.GSTR8_PDF:
				reportType = ReportTypeConstants.GSTR8_PDF;
				break;
			case "Consolidated DigiGST Error":
				reportType = "Consolidated DigiGST Error";
				break;

			case "Processed Records (Recon Tagging)":
				reportType = "Processed Records (Recon Tagging)";
				break;

			case "Vendor Compliance Table Report":
				reportType = "Vendor Compliance Table Report";
				break;

			case "Customer Compliance Table Report":
				reportType = "Customer Compliance Table Report";
				break;

			case "My Compliance Table Report":
				reportType = "My Compliance Table Report";
				break;

			case "GSTR1_B2B":
				reportType = "GSTR1 B2B";
				break;

			case "GSTR1_B2BA":
				reportType = "GSTR1 B2BA";
				break;

			case "GSTR1_B2CL":
				reportType = "GSTR1 B2CL";
				break;

			case "GSTR1_B2CLA":
				reportType = "GSTR1 B2CLA";
				break;

			case "GSTR1_EXPORTS":
				reportType = "GSTR1 EXPORTS";
				break;

			case "GSTR1_EXPORTA":
				reportType = "GSTR1 EXPORTA";
				break;

			case "GSTR1_CDNR":
				reportType = "GSTR1 CDNR";
				break;

			case "GSTR1_CDNRA":
				reportType = "GSTR1 CDNRA";
				break;

			case "GSTR1_CDNUR":
				reportType = "GSTR1 CDNUR";
				break;

			case "GSTR1_CDNURA":
				reportType = "GSTR1 CDNURA";
				break;

			case "GSTR1_B2CS":
				reportType = "GSTR1 B2CS";
				break;

			case "GSTR1_B2CSA":
				reportType = "GSTR1 B2CSA";
				break;

			case "GSTR1A_B2B":
				reportType = "GSTR1A B2B";
				break;

			case "GSTR1A_B2BA":
				reportType = "GSTR1A B2BA";
				break;

			case "GSTR1A_B2CL":
				reportType = "GSTR1A B2CL";
				break;

			case "GSTR1A_B2CLA":
				reportType = "GSTR1A B2CLA";
				break;

			case "GSTR1A_EXPORTS":
				reportType = "GSTR1A EXPORTS";
				break;

			case "GSTR1A_EXPORTA":
				reportType = "GSTR1A EXPORTA";
				break;

			case "GSTR1A_CDNR":
				reportType = "GSTR1A CDNR";
				break;

			case "GSTR1A_CDNRA":
				reportType = "GSTR1A CDNRA";
				break;

			case "GSTR1A_CDNUR":
				reportType = "GSTR1A CDNUR";
				break;

			case "GSTR1A_CDNURA":
				reportType = "GSTR1A CDNURA";
				break;

			case "GSTR1A_B2CS":
				reportType = "GSTR1A B2CS";
				break;

			case "GSTR1A_B2CSA":
				reportType = "GSTR1A B2CSA";
				break;

			case ReportTypeConstants.GSTR1ASHIPPING_BILL:
				reportType = "GSTR1A Missing Shipping Bill Details";
				break;

			case "IMS Records Report":
				reportType = "IMS Records Report";// IMS Records Active+Inactive
													// Report
				break;

			case "ITC RCM Ledger (Opening Balance)":
				reportType = "ITC RCM Ledger (Opening Balance)";
				break;

			case "ITC Reversal & Re-Claim Ledger (Opening Balance)":
				reportType = "ITC Reversal & Re-Claim Ledger (Opening Balance)";
				break;

			// RCM
			case "ITC RCM Ledger":
				reportType = "ITC RCM Ledger";
				break;

			case "Negative Liability Ledger":
				reportType = "Negative Liability Ledger";
				break;

			case "IMS Action Error 2BvsPR (Recon Result)":
				reportType = "IMS Action Error 2BvsPR (Recon Result)";
				break;

			case "IMS Records Active Report":
				reportType = "IMS Records Active Report";
				break;

			case "IMS Records Active+Inactive Report":
				reportType = "IMS Records Active+Inactive Report";// IMS Records
																	// Active+Inactive
																	// Report
				break;

			case "IMS Action Error 2AvsPR (Recon Result)":
				reportType = "IMS Action Error 2AvsPR (Recon Result)";
				break;

			case "TOTAL_RECORDS":
				reportType = "Total Records";
				break;

			case "PSD_ACTIVE":
				reportType = "Processed Records";
				break;

			case "PSD_INACTIVE":
				reportType = "Processed Records";
				break;

			case "ERR_ACTIVE":
				reportType = "Error Records";
				break;

			case "ERR_INACTIVE":
				reportType = "Error Records";
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

			case "DigiGST Processed Data":
				reportType = "DigiGST Processed Data";
				break;

			case "Consolidated DigiGst Error":
				reportType = "Consolidated DigiGst Error";
				break;

			case "Consolidated GSTN Error":
				reportType = "Consolidated GSTN Error";
				break;

			case "Consolidated GL Records":
				reportType = "Consolidated GL Records";
				break;
				
			case "Detailed Report":
				reportType = "Detailed Report";
				break;

			default:
				reportType = "Invalid report type";

			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}

		return reportType;
	}
}
