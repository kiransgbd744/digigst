package com.ey.advisory.common;


import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.Collator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.hibernate.PropertyNotFoundException;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommonUtility {

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailsRepo;
	
	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	@Qualifier("GstinSourceInfoRepository")
	private GstinSourceInfoRepository gstinSourceInfoRepository;

	@Autowired
	private CommonUtility commonUtility;

	private static final String OUTWARD = "OUTWARD";
	private static final String INWARD = "INWARD";
	private static final String GSTR9 = "GSTR9";
	private static final String ISD = "ISD";
	private static final String GSTR6A = "GSTR6A";
	static final List<String> fileStatusReportTypeList = ImmutableList.of(
			ReportTypeConstants.ERROR_BV, ReportTypeConstants.ERROR_SV,
			ReportTypeConstants.PROCESSED_RECORDS,
			ReportTypeConstants.TOTAL_ERRORS,
			ReportTypeConstants.TOTAL_RECORDS);

	static final List<String> fileStatusEinvReportTypeList = ImmutableList.of(
			ReportTypeConstants.EINV_NOT_APPLICABLE,
			ReportTypeConstants.EINV_APPLICABLE,
			ReportTypeConstants.EINV_IRN_GENERATE,
			ReportTypeConstants.EINV_IRN_CANCELED,
			ReportTypeConstants.EINV_ERROR,
			ReportTypeConstants.EINV_ERROR_FROM_IRP);

	static final List<String> dataStatusReportTypeList = ImmutableList.of(
			ReportTypeConstants.ERROR, ReportTypeConstants.PROCESSED_RECORDS,
			ReportTypeConstants.TOTAL_RECORDS);

	static final List<String> processedSummaryReportTypeList = ImmutableList.of(
			ReportTypeConstants.AS_UPLOADED, ReportTypeConstants.ASPERROR,
			ReportTypeConstants.GSTNERROR, ReportTypeConstants.GSTR2PROCESS,
			ReportTypeConstants.GSTR1ENTITYLEVEL,ReportTypeConstants.GSTR1A_AS_UPLOADED,
			ReportTypeConstants.GSTR1A_ASPERROR,
			ReportTypeConstants.GSTR1A_GSTNERROR,ReportTypeConstants.GSTR1AENTITYLEVEL);

	static final List<String> outwardFileStatusRetReportTypeList = ImmutableList
			.of(ReportTypeConstants.RET_NOT_APPLICABLE,
					ReportTypeConstants.RET_APPLICABLE,
					ReportTypeConstants.RET_PROCESS,
					ReportTypeConstants.RET_ERROR,
					ReportTypeConstants.RET_INFO);

	static final List<String> outwardFileStatusEwbReportTypeList = ImmutableList
			.of(ReportTypeConstants.EWB_NOT_APPLICABLE,
					ReportTypeConstants.EWB_APPLICABLE,
					ReportTypeConstants.EWB_ERROR,
					ReportTypeConstants.EWB_GENERATED,
					ReportTypeConstants.EWB_ERROR_FROM_NIC,
					ReportTypeConstants.EWB_CANCEL);

	@SuppressWarnings("unchecked")
	public static final Map<String, Pair<String, String>> Gstr3bTableHeadingAndDescription = ImmutableMap
			.<String, Pair<String, String>>builder()
			.put(Gstr3BConstants.Table3_1_A, new Pair("Outward Supplies",
					"Outward Taxable supplies  (other than zero rated, nil rated and exempted)"))
			.put(Gstr3BConstants.Table3_1_B,
					new Pair("Outward Supplies",
							"Outward Taxable supplies  (zero rated )"))
			.put(Gstr3BConstants.Table3_1_C, new Pair("Outward Supplies",
					"Other Outward Taxable supplies (Nil rated, exempted)"))
			.put(Gstr3BConstants.Table3_1_D,
					new Pair("Inward Supplies",
							"Inward supplies (liable to reverse charge) "))
			.put(Gstr3BConstants.Table3_1_E,
					new Pair("Outward Supplies", "Non-GST Outward supplies"))
			.put(Gstr3BConstants.Table3_2_A,
					new Pair("Outward Supplies",
							"Supplies made to Unregistered Persons"))
			.put(Gstr3BConstants.Table3_2_B,
					new Pair("Outward Supplies",
							"Supplies made to Composition Taxable Persons"))
			.put(Gstr3BConstants.Table3_2_C,
					new Pair("Outward Supplies",
							"Supplies made to UIN holders"))
			.put(Gstr3BConstants.Table4A1,
					new Pair("ITC Available (Whether in full or part)",
							"Import of goods"))
			
			.put(Gstr3BConstants.Table4_A_1_1,
					new Pair("ITC Available (Whether in full or part)",
							"Current tax period"))
			.put(Gstr3BConstants.Table4_A_1_1_A,
					new Pair("ITC Available (Whether in full or part)",
							"As per Processed PR"))
			.put(Gstr3BConstants.Table4_A_1_1_B,
					new Pair("ITC Available (Whether in full or part)",
							"As per Get GSTR-2B for current tax period"))
			.put(Gstr3BConstants.Table4_A_1_1_C,
					new Pair("ITC Available (Whether in full or part)",
							"As per GSTR-2BvsPR 3B Response"))
			.put(Gstr3BConstants.Table4_A_1_2,
					new Pair("ITC Available (Whether in full or part)",
							"ITC reclaimed which was reversed in earlier tax period"))
			
			.put(Gstr3BConstants.Table4A2,
					new Pair("ITC Available (Whether in full or part)",
							"Import of services"))
			.put(Gstr3BConstants.Table4A3, new Pair(
					"ITC Available (Whether in full or part)",
					"Inward supplies liable to reverse charge (other than 1&2 above)"))
			
			
			.put(Gstr3BConstants.Table4_A_3_1,
					new Pair("ITC Available (Whether in full or part)",
							"Inward supplies liable to reverse charge from registered person"))
			.put(Gstr3BConstants.Table4_A_3_1_a,
					new Pair("ITC Available (Whether in full or part)",
							"Current tax period"))
			.put(Gstr3BConstants.Table4_A_3_1_a_a,
					new Pair("ITC Available (Whether in full or part)",
							"As per Processed PR"))
			.put(Gstr3BConstants.Table4_A_3_1_a_b,
					new Pair("ITC Available (Whether in full or part)",
							"As per Get GSTR-2B for current tax period"))
			.put(Gstr3BConstants.Table4_A_3_1_a_c,
					new Pair("ITC Available (Whether in full or part)",
							"As per GSTR-2BvsPR 3B Response"))
			.put(Gstr3BConstants.Table4_A_3_1_b,
					new Pair("ITC Available (Whether in full or part)",
							"ITC reclaimed which was reversed in earlier tax period"))
			.put(Gstr3BConstants.Table4_A_3_2,
					new Pair("ITC Available (Whether in full or part)",
							"Inward supplies liable to reverse charge from Un-registered person"))
			
			.put(Gstr3BConstants.Table4A4,
					new Pair("ITC Available (Whether in full or part)",
							"Inward supplies from ISD"))
			
			.put(Gstr3BConstants.Table4_A_4_1,
					new Pair("ITC Available (Whether in full or part)",
							"As per Processed PR"))
			.put(Gstr3BConstants.Table4_A_4_2,
					new Pair("ITC Available (Whether in full or part)",
							"As per Get GSTR-2B and Response Upload"))
			.put(Gstr3BConstants.Table4_A_4_2_a,
					new Pair("ITC Available (Whether in full or part)",
							"As per Get GSTR-2B for current tax period"))
			.put(Gstr3BConstants.Table4_A_4_2_b,
					new Pair("ITC Available (Whether in full or part)",
							"ITC reclaimed which was reversed in earlier tax period"))
			.put(Gstr3BConstants.Table4_A_4_3,
					new Pair("ITC Available (Whether in full or part)",
							"Distributed ITC through GSTR 6 (DigiGST computed)"))
			.put(Gstr3BConstants.Table4_A_4_4,
					new Pair("ITC Available (Whether in full or part)",
							"As per GSTR-2BvsPR 3B Response"))
			
			
			
			.put(Gstr3BConstants.Table4A5,
					new Pair("ITC Available (Whether in full or part)",
							"All other ITC"))
			
			.put(Gstr3BConstants.Table4_A_5_5_1,
					new Pair("ITC Available (Whether in full or part)",
							"Current tax period"))
			.put(Gstr3BConstants.Table4_A_5_5_1_A,
					new Pair("ITC Available (Whether in full or part)",
							"As per Processed PR"))
			.put(Gstr3BConstants.Table4_A_5_5_1_B,
					new Pair("ITC Available (Whether in full or part)",
							"As per Get GSTR-2B"))
			.put(Gstr3BConstants.Table4_A_5_5_C,
					new Pair("ITC Available (Whether in full or part)",
							"As per GSTR-2BvsPR 3B Response"))
			.put(Gstr3BConstants.Table4_A_5_5_1_D,
					new Pair("ITC Available (Whether in full or part)",
							"As per GSTR-2AvsPR 3B Response"))
			.put(Gstr3BConstants.Table4_A_5_5_2,
					new Pair("ITC Available (Whether in full or part)",
							"ITC reclaimed which was reversed in earlier tax period"))
			.put(Gstr3BConstants.Table4_A_5_5_2_A,
					new Pair("ITC Available (Whether in full or part)",
							"ITC Reversal File (Re-availment of Credit)"))
			.put(Gstr3BConstants.Table4_A_5_5_2_B,
					new Pair("ITC Available (Whether in full or part)",
							"180 Day Response upload (Re-availment of credit)"))
			.put(Gstr3BConstants.Table4_A_5_5_2_C,
					new Pair("ITC Available (Whether in full or part)",
							"As per Recon Response upload file"))
			
			.put(Gstr3BConstants.Table4B1, new Pair("ITC Reversed",
					"As per rules 38 & 42 and 43 of CGST Rules and sub-section (5) of section 17"))
			
			
			.put(Gstr3BConstants.Table4B1_1_1, new Pair("ITC Reversed",
					"As per rules 38, 42 and 43 of CGST Rules"))
			.put(Gstr3BConstants.Table4B1_1_2, new Pair("ITC Reversed",
					"Ineligible ITC as per section 17(5)"))
			.put(Gstr3BConstants.Table4B1_1_2_A, new Pair("ITC Reversed",
					"Table 4(A)(2), 4(A)(3) Unregistered RCM"))
			.put(Gstr3BConstants.Table4B1_1_2_B, new Pair("ITC Reversed",
					"Table 4(A)(1), 4(A)(3) Registered RCM, 4(A)(4)"))
			.put(Gstr3BConstants.Table4B1_1_2_C, new Pair("ITC Reversed",
					"Table 4(A)(5)"))
			
			
			.put(Gstr3BConstants.Table4B2,
					new Pair("ITC Reversed", "Others Reversal"))
			
			
			.put(Gstr3BConstants.Table4B2_2_1, new Pair("ITC Reversed",
					"As per Reversal file upload"))
			.put(Gstr3BConstants.Table4B2_2_2, new Pair("ITC Reversed",
					"As per 180 days reversal file upload responses"))
			.put(Gstr3BConstants.Table4B2_2_3, new Pair("ITC Reversed",
					"Ineligible ITC other than section 17(5)"))
			.put(Gstr3BConstants.Table4B2_2_3_A, new Pair("ITC Reversed",
					"Table 4(A)(2), 4(A)(3) Unregistered RCM"))
			.put(Gstr3BConstants.Table4B2_2_3_B, new Pair("ITC Reversed",
					"Table 4(A)(1), 4(A)(3) Registered RCM, 4(A)(4)"))
			.put(Gstr3BConstants.Table4B2_2_3_C, new Pair("ITC Reversed",
					"Table 4(A)(5)"))
			
			
			
			.put(Gstr3BConstants.Table4C,
					new Pair("Net Available ITC", "Net Available ITC (4A-4B)"))
			.put(Gstr3BConstants.Table4D1, new Pair("Other Details",
					"ITC reclaimed which was reversed under Table 4(B)(2) in earlier tax period"))
			.put(Gstr3BConstants.Table4D2, new Pair("Other Details",
					"Ineligible ITC under section 16(4) and ITC restricted due to PoS provisions"))
			.put(Gstr3BConstants.Table5A1, new Pair(
					"Exempt & Nil & Non-GST Inward Supplies (Inter State)",
					"From a supplier under composition scheme and Exempt and Nil rated supply "))
			.put(Gstr3BConstants.Table5A2, new Pair(
					"Exempt & Nil & Non-GST Inward Supplies (Intra State)",
					"From a supplier under composition scheme and Exempt and Nil rated supply "))
			.put(Gstr3BConstants.Table5B1, new Pair(
					"Exempt & Nil & Non-GST Inward Supplies (Inter State)",
					"Non GST supply "))
			.put(Gstr3BConstants.Table5B2, new Pair(
					"Exempt & Nil & Non-GST Inward Supplies (Intra State)",
					"Non GST supply "))
			.put(Gstr3BConstants.Table5_1A, new Pair("Interest", "Interest"))
			.put(Gstr3BConstants.Table5_1B, new Pair("Late Fee", "Late Fee"))
			.put(Gstr3BConstants.Table3_1_1_A, new Pair(
					"Details of Supplies notified under section 9(5)",
					"Taxable supplies on which E-com operator pays tax u/s 9(5)"))
			.put(Gstr3BConstants.Table3_1_1_B, new Pair(
					"Details of Supplies notified under section 9(5)",
					"Taxable supplies made by registered person through E-com operator"))

			.build();

	public static void setAsposeLicense() {
		InputStream stream = CommonUtility.class.getClassLoader()
				.getResourceAsStream("licenses/Aspose.Cells.lic");
		com.aspose.cells.License license = new com.aspose.cells.License();
		license.setLicense(stream);
	}

	public String getProp(String key) {
		if (env.containsProperty(key))
			return env.getProperty(key);
		else
			throw new PropertyNotFoundException(
					"Could not find the Property with key:" + key);
	}

	public Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath.replace("%20", " "), options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}

	public Workbook createWorkbookWithCSVTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.CSV);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}

	public static Pair<String, String> getReportHeaderMapping(String reportType,
			String reportCateg, String dataType) {

		if ((ReportTypeConstants.FILE_STATUS.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& (OUTWARD.equalsIgnoreCase(dataType)|| "OUTWARD_1A".equalsIgnoreCase(dataType) )
				&& (fileStatusReportTypeList.contains(reportType)
						|| fileStatusEinvReportTypeList.contains(reportType))) {
			return new Pair<>("gsrt1.buss.error.headers",
					"gstr1.buss.error.mapping");
		} else if ((ReportTypeConstants.FILE_STATUS
				.equalsIgnoreCase(reportCateg) && ISD.equalsIgnoreCase(dataType)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg)
						&& ISD.equalsIgnoreCase(dataType))
				|| "SFTPSend".equalsIgnoreCase(reportCateg)
						&& ISD.equalsIgnoreCase(dataType)) {
			if (reportType.equals("processed")) {
				return new Pair<>("isd.buss.processed.headers",
						"isd.buss.processed.mapping");
			} else {
				return new Pair<>("isd.buss.error.headers",
						"isd.buss.error.mapping");
			}
		} else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& OUTWARD.equalsIgnoreCase(dataType)
				&& dataStatusReportTypeList.contains(reportType)) {
			return new Pair<>("anx1.api.sync.csv.report.headers",
					"anx1.api.new.report.headers");

		} else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& (OUTWARD.equalsIgnoreCase(dataType) 
						|| "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& processedSummaryReportTypeList.contains(reportType)) {
			return new Pair<>("anx1.api.sync.csv.report.headers",
					"anx1.api.new.report.headers");

		} else if (((ReportTypeConstants.GSTR1ENTITYLEVEL.equalsIgnoreCase(
				reportType) || ReportTypeConstants.GSTR1AENTITYLEVEL.equalsIgnoreCase(
						reportType)) && (OUTWARD.equalsIgnoreCase(dataType) 
						|| "OUTWARD_1A".equalsIgnoreCase(dataType)))) {
			return new Pair<>("gstr1.outward.entitylevel.api.report.headers",
					"gstr1.outward.entitylevel.report.column.mapping");

		} else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& processedSummaryReportTypeList.contains(reportType)) {
			return new Pair<>("gstr2.inward.processed.report.headers",
					"gstr2.inward.processed.report.column.mapping");

		}

		else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.GSTR6PROCESS
						.equalsIgnoreCase(reportType)) {

			return new Pair<>("anx1.inward.processed.report.headers",
					"anx1.inward.processed.report.column.mapping");

		} else if ((ReportTypeConstants.GET_GSTR1_EINVOICE
				.equalsIgnoreCase(reportCateg))
				&& "OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.GSTR1ENVPROCESS
						.equalsIgnoreCase(reportType)) {

			return new Pair<String, String>("gstr1.einvoices.report.headers",
					"gstr1.einvoices.report.headers.mapping");

		}
		// inward - file status
		else if ((ReportTypeConstants.FILE_STATUS.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.TOTAL_RECORDS
						.equalsIgnoreCase(reportType)) {
			return new Pair<String, String>("anx1.inward.total.report.headers",
					"anx1.inward.total.report.column.mapping");

		} else if ((ReportTypeConstants.FILE_STATUS
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.PROCESSED_RECORDS
						.equalsIgnoreCase(reportType)) {
			return new Pair<String, String>(
					"anx1.inward.processed.report.headers",
					"anx1.inward.processed.report.column.mapping");

		} else if ((ReportTypeConstants.FILE_STATUS
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.ERROR_BV.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.ERROR_SV
								.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.TOTAL_ERRORS
								.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>(
					"anx1.inward.api.error.report.headers",
					"anx1.inward.api.error.report.column.mapping");

		}
		// inward - Data status
		else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.TOTAL_RECORDS
						.equalsIgnoreCase(reportType)) {
			return new Pair<String, String>("anx1.inward.total.report.headers",
					"anx1.inward.total.report.column.mapping");

		} else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.PROCESSED_RECORDS
						.equalsIgnoreCase(reportType)) {
			return new Pair<String, String>(
					"anx1.inward.processed.report.headers",
					"anx1.inward.processed.report.column.mapping");

		} else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)) {
			return new Pair<String, String>(
					"anx1.inward.api.error.report.headers",
					"anx1.inward.api.error.report.column.mapping");

		} else if ("Get GSTR-2A".equalsIgnoreCase(dataType)
				&& "GSTR-2A (Compete)".equalsIgnoreCase(reportType)
				|| "GSTR-2A_Get_Records".equalsIgnoreCase(reportType)) {
			return new Pair<String, String>("gstr2a.inward.api.report.headers",
					"gstr2a.inward.api.report.column.mapping");

		} else if (ReportTypeConstants.EINVOICE_RECON.equalsIgnoreCase(
				reportCateg) && OUTWARD.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.EINVPROCESS
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr1.einv.recon.headers",
					"gstr1.einv.recon.column.mapping");

		} else if (ReportTypeConstants.EINVOICE_RECON.equalsIgnoreCase(
				reportCateg) && OUTWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)) {
			return new Pair<String, String>("gstr1.einv.recon.error.headers",
					"gstr1.einv.recon.error.column.mapping");
			//gstr1a- code
		} else if ((OUTWARD.equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& outwardFileStatusRetReportTypeList.contains(reportType)) {
			return new Pair<>("anx1.api.sync.csv.report.headers",
					"anx1.api.new.report.headers");

		} 
		else if ((OUTWARD.equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& outwardFileStatusEwbReportTypeList.contains(reportType)) {
			return new Pair<>("anx1.api.sync.csv.report.headers",
					"anx1.api.new.report.headers");

		}

		else if ("Get GSTR-2A".equalsIgnoreCase(dataType)
				&& "GSTR-2A (popUp)".equalsIgnoreCase(reportType)) {
			return new Pair<String, String>("gstr2a.b2b.api.report.headers",
					"gstr2a.b2b.api.report.column.mapping");

		} else if (ReportTypeConstants.EINVSUMMARY.equalsIgnoreCase(reportCateg)
				&& OUTWARD.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.EINVCONSOLIDATED
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr1.einv.recon.headers",
					"gstr1.einv.recon.column.mapping");

		}

		// Gstr9DumpReports
		else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_TRANSLVL_GSTR1
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.translvl.gstr1.headers",
					"gstr9.translvl.gstr1.columns");

		} else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_B2C_B2CSA
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.b2cs.b2csa.headers",
					"gstr9.b2cs.b2csa.columns");

		} else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_ADVREC_AMD
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.advrec.amd.headers",
					"gstr9.advrec.amd.columns");

		} else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_ADVADJ_AMD
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.advadj.amd.headers",
					"gstr9.advadj.amd.columns");

		} else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_NIL_NON_EXT
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.nilnon.ext.headers",
					"gstr9.nilnon.ext.columns");

		} else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_HSN_SUMMARY
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.hsn.sum.headers",
					"gstr9.hsn.sum.columns");

		} else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_GSTR3B_SUMMARY
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.gstr3b.sum.headers",
					"gstr9.gstr3b.sum.columns");

		} else if (ReportTypeConstants.GSTR9_DUMP_REPORTS.equalsIgnoreCase(
				reportCateg) && GSTR9.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.GSTR9_GSTR3B_TXPAID
						.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("gstr9.gstr3b.txpaid.headers",
					"gstr9.gstr3b.txpaid.columns");

		} else if ("GSTR3B".equalsIgnoreCase(reportCateg)
				&& "GSTR3B".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.GSTR3B_180DAYS_REV_RESP
						.equalsIgnoreCase(reportType)) {
			return new Pair<>("gstr3b.180days.reversal.resposne.headers",
					"gstr3b.180days.reversal.resposne.column.mapping");

		} else if (GSTR6A.equalsIgnoreCase(reportCateg)
				&& GSTR6A.equalsIgnoreCase(dataType)) {
			return new Pair<>("gstr6a.api.report.download.headers",
					"gstr6a.api.report.download.mapping");
		} else if (ReportTypeConstants.ITC_REVERSAL_RULE_42.equalsIgnoreCase(
				reportType) && "Inward".equalsIgnoreCase(dataType)) {
			return new Pair<>("reversal.inward.api.report.headers",
					"reversal.inward.api.report.mapping");
		} else if (ReportTypeConstants.ITC_REVERSAL_RULE_42.equalsIgnoreCase(
				reportType) && "Outward".equalsIgnoreCase(dataType)) {
			return new Pair<>("reversal.outward.api.report.headers",
					"reversal.outward.api.report.mapping");
		} else if (ReportTypeConstants.STOCK_TRACKING_REPORT
				.equalsIgnoreCase(reportType)) {
			return new Pair<>("itc04.stocktracking.report.headers",
					"itc04.stocktracking.report.mapping");
		}

		else if (ReportTypeConstants.GSTR8_PROCESSED_REPORT
				.equalsIgnoreCase(reportType)
				&& APIConstants.GSTR8.toUpperCase()
						.equalsIgnoreCase(dataType)) {
			return new Pair<>("gstr8.processed.report.headers",
					"gstr8.processed.report.mapping");

			// DigiGST Error Report
		} else if ((ReportTypeConstants.PROCESSED_SUMMARY
			.equalsIgnoreCase(reportCateg) || ReportTypeConstants.REVIEW_SUMMARY
				.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& "Consolidated DigiGST Error".contains(reportType)) {
			return new Pair<>("gstr2.asp.error.report.headers",
					"gstr2.asp.error.report.column");

		} // DigiGST Processed Recon Tagging Report
		else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)|| ReportTypeConstants.REVIEW_SUMMARY
				.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& "Processed Records (Recon Tagging)".contains(reportType)) {
			return new Pair<>("gstr2.asp.recon.tagging.report.headers",
					"gstr2.asp.recon.tagging.report.column");

		}else if ("IMS".equalsIgnoreCase(
				reportCateg) && INWARD.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)
				 ||	ReportTypeConstants.TOTAL_RECORDS.equalsIgnoreCase(reportType))) {
			return new Pair<String, String>("ims.error.headers",
					"ims.error.column.mapping");
			
		} else if ("IMS".equalsIgnoreCase(
				reportCateg) && INWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.PROCESSED_RECORDS.equalsIgnoreCase(reportType)) {
			return new Pair<String, String>("ims.processed.headers",
					"ims.processed.column.mapping");
			
		} else if ("GL_DUMP".equalsIgnoreCase(reportCateg)) {
			return new Pair<String, String>("gl.recon.headers",
					"gl.recon.column.mapping");
			
		}
		else if ("GL Recon".equalsIgnoreCase(reportCateg)) {
			return new Pair<String, String>("gl.consolidated.summary.report.headers",
					"gl.consolidated.summary.report.mappings");
			
		}

		return null;
	}

	public static Pair<String, String> getInvMangmntReportHeaderMapping() {
		return new Pair<String, String>("gsrt1.invmangment.headers",
				"gstr1.invmangment.mapping");
	}

	public static Pair<String, String> getInwardInvMangmntReportHeaderMapping() {
		return new Pair<String, String>("inward.invmangment.headers",
				"inward.invmangment.mapping");
	}

	public static Object exponentialAndZeroCheck(Object obj) {
		Object object = obj != null ? obj.toString().trim() : null;
		if (obj != null && !obj.toString().trim().isEmpty()) {
			if (obj.toString().contains(".") && obj.toString()
					.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)")) {

				BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
				docNoDecimalFormat = new BigDecimal(obj.toString());
				Long supplierPhoneLong = docNoDecimalFormat.longValue();
				object = String.valueOf(supplierPhoneLong);
			}
		}
		return object;
	}

	public static Object exponentialAndZeroCheckForBigDecimal(Object obj) {
		Object object = obj != null ? obj.toString().trim() : null;
		if (obj != null && !obj.toString().trim().isEmpty()) {
			if (obj.toString().contains(".") && obj.toString()
					.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)")) {

				BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
				docNoDecimalFormat = new BigDecimal(obj.toString());
				// Long supplierPhoneLong = docNoDecimalFormat.longValue();
				object = String.valueOf(docNoDecimalFormat);
			}
		}
		return object;
	}

	public static Object singleQuoteCheck(Object obj) {
		Object object = obj != null ? obj.toString().trim() : null;
		if (obj != null && obj.toString().startsWith(GSTConstants.SPE_CHAR)) {
			object = obj.toString().substring(1);
		}
		return object;
	}

	public static Pair<String, String> getGstr3bHeadingandDesc(
			String tableSec) {
		return Gstr3bTableHeadingAndDescription.get(tableSec);
	}

	public static Object capital(Object obj) {
		Object object = obj != null ? obj.toString().trim() : null;
		if (obj != null && !obj.toString().trim().isEmpty()) {
			object = obj.toString().toUpperCase();
		}
		return object;
	}

	public static Boolean deriveEinvStatus(LocalDateTime irnDate) {
		LocalDateTime presentDate = EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.now());
		LocalDateTime payLoadDate = irnDate.plusHours(24);
		return presentDate.isBefore(payLoadDate);

	}

	public static String generateGstr9DocKey(String gstin, String fy,
			String tableNo) {
		gstin = (gstin != null) ? gstin.trim() : "";
		fy = (fy != null) ? fy.trim() : "";
		tableNo = (tableNo != null) ? tableNo.trim() : "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(gstin).add(fy).add(tableNo)
				.toString();
	}

	public static String generateGstr9DocKey(String gstin, String fy,
			String subSection, String hsn, String rateOfTax, String uqc) {
		gstin = (gstin != null) ? gstin.trim() : "";
		fy = (fy != null) ? fy.trim() : "";
		subSection = (subSection != null) ? subSection.trim() : "";
		hsn = (hsn != null) ? hsn.trim() : "";
		String updatedRateOfTax = "";
		if (rateOfTax != null && !rateOfTax.isEmpty()) {
			BigDecimal number = new BigDecimal(rateOfTax);
			BigDecimal formattedNumber = number.setScale(1, RoundingMode.DOWN);
			updatedRateOfTax = formattedNumber.toPlainString();
		}
		uqc = (uqc != null) ? uqc.trim() : "";
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(gstin).add(fy)
				.add(subSection).add(hsn).add(updatedRateOfTax).add(uqc).toString();
	}

	// This method will return all the valid financial year based on current
	// month and year

	public static List<FinancialYearDto> getValidFinYear() {
		try {
			int currYear = LocalDate.now().getYear();
			int month = LocalDate.now().getMonthValue();
			int startYear = 2017;
			List<FinancialYearDto> finYearList = new ArrayList<>();
			for (int year = startYear; year < currYear; year++) {
				FinancialYearDto dto = new FinancialYearDto();
				String finYear = year + "-"
						+ String.valueOf(year + 1).substring(2);
				String fullFy = year + "-" + String.valueOf(year + 1);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Financial year : {}, {} ", finYear, fullFy);
				dto.setFy(finYear);
				dto.setFullFy(fullFy);
				dto.setKey(String.valueOf(year));
				finYearList.add(dto);
			}
			if (month > 3) {
				FinancialYearDto dto = new FinancialYearDto();
				String currentFy = GenUtil.getCurrentFinancialYear();
				StringBuilder fullFy = new StringBuilder();
				fullFy.append(currentFy.substring(0, 5));
				fullFy.append(currentFy.substring(0, 2));
				fullFy.append(currentFy.substring(5));
				dto.setFy(currentFy);
				dto.setFullFy(String.valueOf(fullFy));
				dto.setKey(currentFy.substring(0, 4));
				finYearList.add(dto);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("CurrentFinancial year : {} ", currentFy);
			}
			Collator collator = Collator.getInstance();
			finYearList
					.sort((o1, o2) -> collator.compare(o2.getFy(), o1.getFy()));
			return finYearList;
		} catch (Exception ex) {
			String msg = String.format(
					"Unexpected error while fetching all financial years");
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		}
	}

	public String getAnsFromQue(Long entityId, String question) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				question);
		return optAns;
	}

	public String getAnsFromQue1(Long entityId, String question,
			String questionCateg) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion1(entityId,
				question, questionCateg);
		return optAns;
	}

	public Pair<Map<String, String>, Map<String, String>> getGstnRegMap() {
		List<GSTNDetailEntity> findAllGstins = gSTNDetailRepository
				.findDetails();

		Map<String, String> regTypeMap = new HashMap<String, String>();
		Map<String, String> gstinAuthMap = new HashMap<String, String>();
		if (findAllGstins.isEmpty()) {
			return new Pair<Map<String, String>, Map<String, String>>(
					gstinAuthMap, regTypeMap);
		}
		gstinAuthMap = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstins(findAllGstins.stream()
						.map(e -> e.getGstin()).collect(Collectors.toList()));

		for (GSTNDetailEntity regCode : findAllGstins) {
			regTypeMap.put(regCode.getGstin(), regCode.getRegistrationType());
		}
		return new Pair<Map<String, String>, Map<String, String>>(gstinAuthMap,
				regTypeMap);
	}

	public String setFiscalYear(String gstin, String docDate, String type) {
		String finYear = null;
		try {
			if (!Strings.isNullOrEmpty(gstin)
					&& !Strings.isNullOrEmpty(docDate)) {
				GSTNDetailEntity gstnDetailEntity = gstnDetailsRepo
						.findByGstinAndIsDeleteFalse(gstin);
				String questionCategory = type;
				String ansfromques = getAnsFromQue1(
						gstnDetailEntity.getEntityId(),
						"Fiscal year to be followed in ERP ?",
						questionCategory);
				if (ansfromques == null) {
					ansfromques = "B";
				}
				LocalDate docDateLoc = LocalDate.parse(docDate);
				if (ansfromques.equalsIgnoreCase("A")) {
					finYear = GenUtil.getFinYearJanToDec(docDateLoc);
				}
				if (ansfromques.equalsIgnoreCase("B")) {
					finYear = GenUtil.getFinYear(docDateLoc);
				}
				if (ansfromques.equalsIgnoreCase("C")) {
					finYear = GenUtil.getFinYearJulToJune(docDateLoc);
				}

				if (ansfromques.equalsIgnoreCase("D")) {
					finYear = GenUtil.getFinYearOctToSept(docDateLoc);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected error while retriving fiscal year", e);
		}
		return !Strings.isNullOrEmpty(finYear) ? finYear.substring(0, 4) : null;
	}

	public static String getReportZipFiles(File tempDir, String entityName,
			String compressedFileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}

		CombineAndZipXlsxFiles.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);
		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}
		return zipFileName;

	}

	private static List<String> getAllFilesToBeZipped(File tmpDir) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".xlsx");
			}
		};
		File[] files = tmpDir.listFiles(pdfFilter);
		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List of files to be zipped %s",
					retFileNames);
			LOGGER.debug(msg);
		}
		return retFileNames;
	}

	public boolean getAnsForQueMultipleUserAccessToAsyncReports(Long entityId) {
		boolean isusernamereq = true;
		String ansfromques = "B";
		ansfromques = commonUtility.getAnsFromQue(entityId,
				"Multiple User Access to Async Reports");
		if (ansfromques.equalsIgnoreCase("A")) {
			isusernamereq = false;
		} else if (ansfromques.equalsIgnoreCase("B")) {
			isusernamereq = true;
		}
		return isusernamereq;

	}
	public Long getErpIdfromGstin(String gstin) {

		String sourceId = gstinSourceInfoRepository.findByGstin(gstin);

		if (sourceId == null) {

			LOGGER.error(
					"sourceId {} is not configured for group {},"
							+ "Hence reverse integartion job is not posted for {}",
					APIConstants.GSTR2B_TRANSACT_REV_INT,
					TenantContext.getTenantId(), gstin);

			return 0L;
		}

		Long erpId = erpInfoEntityRepository.getErpId(sourceId);

		if (erpId == null) {

			LOGGER.error(
					"erpId {} is not configured for group {},"
							+ "Hence reverse integartion job is not posted for {}",
					APIConstants.GSTR2B_TRANSACT_REV_INT,
					TenantContext.getTenantId(), gstin);

			return 0L;
		}

		return erpId;

	}
	public static Object convertScientificNotationWithSpace(Object obj) {
	    if (obj == null || obj.toString().isEmpty()) {
	        return obj;
	    }

	    String originalValue = obj.toString(); // Preserve spaces
	    String trimmedValue = originalValue.trim(); // Trim only for validation

	    // Check if the trimmed value is in scientific notation
	    if (trimmedValue.contains(".") && trimmedValue.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)")) {
	        BigDecimal decimalValue = new BigDecimal(trimmedValue);
	        Long wholeNumber = decimalValue.longValue();
	        
	        // Replace the trimmed value inside original string to preserve spaces
	        return originalValue.replace(trimmedValue, String.valueOf(wholeNumber));
	    }

	    return obj; // Return original input if no scientific notation found
	}

	public static Object capitalWithSpace(Object obj) {
	    if (obj != null && !obj.toString().isEmpty()) {
	        return obj.toString().toUpperCase();
	    }
	    return obj;
	}

	public static Object singleQuoteCheckWithSpace(Object obj) {
	    if (obj != null && obj.toString().startsWith(GSTConstants.SPE_CHAR)) {
	        return obj.toString().substring(1);
	    }
	    return obj;
	}


}
