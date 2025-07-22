/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.services.onboarding.DataSecurityService;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("GroupSummaryComplainceReportHandler")
@Slf4j
public class GroupSummaryComplainceReportHandler {

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("CompienceHistoryHelperService")
	private CompienceHistoryHelperService complienceSummery;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("Itc04ComplianceServiceImpl")
	private ComplienceSummeryService complienceSummeryService;

	@Autowired
	@Qualifier("Gstr9ComplianceServiceImpl")
	private ComplienceSummeryService gstr9ComplienceSummeryService;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("dataSecurityService")
	private DataSecurityService dataSecurityService;

	public static String Not_Initiated = "Not Initiated";

	public Workbook getComplianceHistoryReport(
			GroupComplianceHistoryDataRecordsReqDto groupComplianceHistoryDataRecordsReqDto) {

		List<Gstr2aProcessedDataRecordsReqDto> groupComplianceProcessed = getUserData(
				groupComplianceHistoryDataRecordsReqDto);
		List<GroupComplianceResponseDto> groupComplianceResponseList = new ArrayList<>();
		for (Gstr2aProcessedDataRecordsReqDto gstr2AProcessedDataRecordsReqDto : groupComplianceProcessed) {
			GroupComplianceResponseDto groupComplianceResponseDto = new GroupComplianceResponseDto();
			int aprilStatusCount, mayStatusCount, juneStatusCount,
					julyStatusCount, augStatusCount, sepStatusCount,
					octStatusCount, novStatusCount, decStatusCount,
					janStatusCount, febStatusCount, marchStatusCount,
					totalCount, filingStatusCount;
			filingStatusCount = totalCount = aprilStatusCount = mayStatusCount = juneStatusCount = julyStatusCount = augStatusCount = sepStatusCount = octStatusCount = novStatusCount = decStatusCount = janStatusCount = febStatusCount = marchStatusCount = 0;
			if (!gstr2AProcessedDataRecordsReqDto.getDataSecAttrs().get("GSTIN")
					.isEmpty()) {
				List<ComplienceSummeryRespDto> respDtos = complienceSummery
						.findcomplienceSummeryRecords(
								gstr2AProcessedDataRecordsReqDto);
				for (int i = 0; i < respDtos.size(); i++) {
					if (respDtos.get(i) != null) {
						if (respDtos.get(i).getAprilStatus() != null)
							if (respDtos.get(i).getAprilStatus()
									.equalsIgnoreCase("Filed"))
								aprilStatusCount++;
						if (respDtos.get(i).getMayStatus() != null)
							if (respDtos.get(i).getMayStatus()
									.equalsIgnoreCase("Filed"))
								mayStatusCount++;
						if (respDtos.get(i).getJuneStatus() != null)
							if (respDtos.get(i).getJuneStatus()
									.equalsIgnoreCase("Filed"))
								juneStatusCount++;
						if (respDtos.get(i).getJulyStatus() != null)
							if (respDtos.get(i).getJulyStatus()
									.equalsIgnoreCase("Filed"))
								julyStatusCount++;
						if (respDtos.get(i).getAugStatus() != null)
							if (respDtos.get(i).getAugStatus()
									.equalsIgnoreCase("Filed"))
								augStatusCount++;
						if (respDtos.get(i).getSepStatus() != null)
							if (respDtos.get(i).getSepStatus()
									.equalsIgnoreCase("Filed"))
								sepStatusCount++;
						if (respDtos.get(i).getOctStatus() != null)
							if (respDtos.get(i).getOctStatus()
									.equalsIgnoreCase("Filed"))
								octStatusCount++;
						if (respDtos.get(i).getNovStatus() != null)
							if (respDtos.get(i).getNovStatus()
									.equalsIgnoreCase("Filed"))
								novStatusCount++;
						if (respDtos.get(i).getDecStatus() != null)
							if (respDtos.get(i).getDecStatus()
									.equalsIgnoreCase("Filed"))
								decStatusCount++;
						if (respDtos.get(i).getJanStatus() != null)
							if (respDtos.get(i).getJanStatus()
									.equalsIgnoreCase("Filed"))
								janStatusCount++;
						if (respDtos.get(i).getFebStatus() != null)
							if (respDtos.get(i).getFebStatus()
									.equalsIgnoreCase("Filed"))
								febStatusCount++;
						if (respDtos.get(i).getMarchStatus() != null)
							if (respDtos.get(i).getMarchStatus()
									.equalsIgnoreCase("Filed"))
								marchStatusCount++;
						if (respDtos.get(i).getFilingStatus() != null)
							if (respDtos.get(i).getFilingStatus()
									.equalsIgnoreCase("Filed"))
								filingStatusCount++;

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"filingStatusCount" + filingStatusCount);
						}
					}
					totalCount = respDtos.size();
				}
			}
			String fy[] = gstr2AProcessedDataRecordsReqDto.getFinancialYear()
					.split("-");

			groupComplianceResponseDto
					.setEntityId(gstr2AProcessedDataRecordsReqDto.getEntity());
			String entityName = entityInfoRepository.findEntityNameByEntityId(
					Long.valueOf(gstr2AProcessedDataRecordsReqDto.getEntity()));
			groupComplianceResponseDto.setEntityName(entityName);

			String taxPeriod = "04" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setAprilFiledCount(String.valueOf(aprilStatusCount)
								+ "/" + String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setAprilFiledCount(Not_Initiated);
			}
			taxPeriod = "05" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setMayFiledCount(String.valueOf(mayStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setMayFiledCount(Not_Initiated);
			}
			taxPeriod = "06" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setJuneFiledCount(String.valueOf(juneStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setJuneFiledCount(Not_Initiated);
			}
			taxPeriod = "07" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setJulyFiledCount(String.valueOf(julyStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setJulyFiledCount(Not_Initiated);
			}
			taxPeriod = "08" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setAugFiledCount(String.valueOf(augStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setAugFiledCount(Not_Initiated);
			}
			taxPeriod = "09" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setSepFiledCount(String.valueOf(sepStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setSepFiledCount(Not_Initiated);
			}
			taxPeriod = "10" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setOctFiledCount(String.valueOf(octStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setOctFiledCount(Not_Initiated);
			}
			taxPeriod = "11" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setNovFiledCount(String.valueOf(novStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setNovFiledCount(Not_Initiated);
			}
			taxPeriod = "12" + fy[0];
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setDecFiledCount(String.valueOf(decStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setDecFiledCount(Not_Initiated);
			}
			taxPeriod = "01" + (Integer.parseInt(fy[0]) + 1);
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setJanFiledCount(String.valueOf(janStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setJanFiledCount(Not_Initiated);
			}
			taxPeriod = "02" + (Integer.parseInt(fy[0]) + 1);
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setFebFiledCount(String.valueOf(febStatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setFebFiledCount(Not_Initiated);
			}
			taxPeriod = "03" + (Integer.parseInt(fy[0]) + 1);
			if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setMarchFiledCount(String.valueOf(marchStatusCount)
								+ "/" + String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setMarchFiledCount(Not_Initiated);
			}
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate now = LocalDate.now();
			String tax = "0104" + fy[0];
			LocalDate returnPeriod = LocalDate.parse(tax, formatter);
			if (returnPeriod.compareTo(now) < 0) {
				groupComplianceResponseDto
						.setFilingStatusCount(String.valueOf(filingStatusCount)
								+ "/" + String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto
						.setFilingStatusCount("Not Initiated");
			}
			groupComplianceResponseList.add(groupComplianceResponseDto);
		}
		return dataToWorkbook(groupComplianceResponseList,
				groupComplianceHistoryDataRecordsReqDto);
	}

	public Workbook getGstr9ComplianceHistoryReport(
			GroupComplianceHistoryDataRecordsReqDto groupComplianceHistoryDataRecordsReqDto) {
		List<Gstr2aProcessedDataRecordsReqDto> groupComplianceProcessed = getUserData(
				groupComplianceHistoryDataRecordsReqDto);
		List<GroupComplianceResponseDto> groupComplianceResponseList = new ArrayList<>();
		for (Gstr2aProcessedDataRecordsReqDto gstr2AProcessedDataRecordsReqDto : groupComplianceProcessed) {
			GroupComplianceResponseDto groupComplianceResponseDto = new GroupComplianceResponseDto();

			groupComplianceResponseDto
					.setEntityId(gstr2AProcessedDataRecordsReqDto.getEntity());
			String entityName = entityInfoRepository.findEntityNameByEntityId(
					Long.valueOf(gstr2AProcessedDataRecordsReqDto.getEntity()));
			groupComplianceResponseDto.setEntityName(entityName);
			int filingStatusCount = 0;
			int totalCount = 0;
			if (!gstr2AProcessedDataRecordsReqDto.getDataSecAttrs().get("GSTIN")
					.isEmpty()) {
				List<ComplienceSummeryRespDto> respDtos = gstr9ComplienceSummeryService
						.findcomplienceSummeryRecords(
								gstr2AProcessedDataRecordsReqDto);
				for (int i = 0; i < respDtos.size(); i++) {
					if (respDtos.get(i) != null) {
						if (respDtos.get(i).getFilingStatus() != null)
							if (respDtos.get(i).getFilingStatus()
									.equalsIgnoreCase("Filed"))
								filingStatusCount++;
					}
					totalCount = respDtos.size();
				}
			}

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate now = LocalDate.now();
			String fy[] = gstr2AProcessedDataRecordsReqDto.getFinancialYear()
					.split("-");
			String tax = "0104" + fy[0];
			LocalDate returnPeriod = LocalDate.parse(tax, formatter);
			if (returnPeriod.compareTo(now) < 0) {
				groupComplianceResponseDto
						.setFilingStatusCount(String.valueOf(filingStatusCount)
								+ "/" + String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto
						.setFilingStatusCount("Not Initiated");
			}
			groupComplianceResponseList.add(groupComplianceResponseDto);
		}
		return dataToWorkbook(groupComplianceResponseList,
				groupComplianceHistoryDataRecordsReqDto);
	}

	public Workbook getItc04ComplianceHistoryReport(
			GroupComplianceHistoryDataRecordsReqDto groupComplianceHistoryDataRecordsReqDto) {
		List<Gstr2aProcessedDataRecordsReqDto> groupComplianceProcessed = getUserData(
				groupComplianceHistoryDataRecordsReqDto);
		List<GroupComplianceResponseDto> groupComplianceResponseList = new ArrayList<>();
		for (Gstr2aProcessedDataRecordsReqDto gstr2AProcessedDataRecordsReqDto : groupComplianceProcessed) {
			GroupComplianceResponseDto groupComplianceResponseDto = new GroupComplianceResponseDto();

			int q1StatusCount, q2StatusCount, q3StatusCount, q4StatusCount,
					h1StatusCount, h2StatusCount, totalCount;
			totalCount = q1StatusCount = q2StatusCount = q3StatusCount = q4StatusCount = h1StatusCount = h2StatusCount = 0;
			if (!gstr2AProcessedDataRecordsReqDto.getDataSecAttrs().get("GSTIN")
					.isEmpty()) {
				List<ComplienceSummeryRespDto> respDtos = complienceSummeryService
						.findcomplienceSummeryRecords(
								gstr2AProcessedDataRecordsReqDto);
				for (int i = 0; i < respDtos.size(); i++) {
					if (respDtos.get(i) != null) {
						if (respDtos.get(i).getQ1Status() != null)
							if (respDtos.get(i).getQ1Status()
									.equalsIgnoreCase("Filed"))
								q1StatusCount++;
						if (respDtos.get(i).getQ2Status() != null)
							if (respDtos.get(i).getQ2Status()
									.equalsIgnoreCase("Filed"))
								q2StatusCount++;
						if (respDtos.get(i).getQ3Status() != null)
							if (respDtos.get(i).getQ3Status()
									.equalsIgnoreCase("Filed"))
								q3StatusCount++;
						if (respDtos.get(i).getQ4Status() != null)
							if (respDtos.get(i).getQ4Status()
									.equalsIgnoreCase("Filed"))
								q4StatusCount++;
						if (respDtos.get(i).getH1Status() != null)
							if (respDtos.get(i).getH1Status()
									.equalsIgnoreCase("Filed"))
								h1StatusCount++;
						if (respDtos.get(i).getH2Status() != null)
							if (respDtos.get(i).getH2Status()
									.equalsIgnoreCase("Filed"))
								h2StatusCount++;
					}
					totalCount = respDtos.size();
				}
			}

			groupComplianceResponseDto
					.setEntityId(gstr2AProcessedDataRecordsReqDto.getEntity());
			String entityName = entityInfoRepository.findEntityNameByEntityId(
					Long.valueOf(gstr2AProcessedDataRecordsReqDto.getEntity()));
			groupComplianceResponseDto.setEntityName(entityName);
			String fy[] = gstr2AProcessedDataRecordsReqDto.getFinancialYear()
					.split("-");
			String taxPeriod = "13" + fy[0];
			if (GenUtil.isValidQuarterForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setQ1FiledCount(String.valueOf(q1StatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setQ1FiledCount(Not_Initiated);
			}
			taxPeriod = "14" + fy[0];
			if (GenUtil.isValidQuarterForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setQ2FiledCount(String.valueOf(q2StatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setQ2FiledCount(Not_Initiated);
			}
			taxPeriod = "15" + fy[0];
			if (GenUtil.isValidQuarterForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setQ3FiledCount(String.valueOf(q3StatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setQ3FiledCount(Not_Initiated);
			}
			taxPeriod = "16" + fy[0];
			if (GenUtil.isValidQuarterForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setQ4FiledCount(String.valueOf(q4StatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setQ4FiledCount(Not_Initiated);
			}
			taxPeriod = "17" + fy[0];
			if (GenUtil.isValidQuarterForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setH1FiledCount(String.valueOf(h1StatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setH1FiledCount(Not_Initiated);
			}
			taxPeriod = "18" + fy[0];
			if (GenUtil.isValidQuarterForCurrentFy(taxPeriod)) {
				groupComplianceResponseDto
						.setH2FiledCount(String.valueOf(h2StatusCount) + "/"
								+ String.valueOf(totalCount));
			} else {
				groupComplianceResponseDto.setH2FiledCount(Not_Initiated);
			}
			groupComplianceResponseList.add(groupComplianceResponseDto);
		}
		return dataToWorkbook(groupComplianceResponseList,
				groupComplianceHistoryDataRecordsReqDto);
	}

	private List<Gstr2aProcessedDataRecordsReqDto> getUserData(
			GroupComplianceHistoryDataRecordsReqDto groupComplianceHistoryDataRecordsReqDto) {
		List<Gstr2aProcessedDataRecordsReqDto> groupComplianceProcessed = new ArrayList<>();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GroupSummaryComplainceReportHandler User Name: {}",
					userName);
		}
		Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap = user
				.getAttributeMap();
		List<Long> entityIdList = new ArrayList<>(attributeMap.keySet());

		for (Long entityId : entityIdList) {
			Gstr2aProcessedDataRecordsReqDto req = new Gstr2aProcessedDataRecordsReqDto();
			req.setEntity(entityId.toString());
			req.setFinancialYear(
					groupComplianceHistoryDataRecordsReqDto.getFinancialYear());
			req.setReturnType(
					groupComplianceHistoryDataRecordsReqDto.getReturnType());
			Map<String, List<String>> dataSecAttrs = new HashedMap<>();
			List<String> gstinList = getGstins(new ArrayList<>(), entityId,
					groupComplianceHistoryDataRecordsReqDto.getReturnType(),
					dataSecAttrs);
			dataSecAttrs.put("GSTIN", gstinList);
			req.setDataSecAttrs(dataSecAttrs);
			groupComplianceProcessed.add(req);
		}
		return groupComplianceProcessed;
	}

	private List<String> getGstins(List<String> gstinList, Long entityId,
			String returnType, Map<String, List<String>> dataSecAttrs) {
		if (!gstinList.isEmpty())
			return gstinList;
		if ("GSTR6".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr6DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		if ("GSTR7".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr7DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		// gstr1,gstr3b,gstr9,itc04
		Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
		dto.setDataSecAttrs(dataSecAttrs);
		dto.setEntityId(Arrays.asList(entityId));
		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(dto);

		return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);

	}

	public Workbook dataToWorkbook(
			List<GroupComplianceResponseDto> groupComplianceResponseList,
			GroupComplianceHistoryDataRecordsReqDto criteria) {

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Complaince Report response" + groupComplianceResponseList);
		}
		String[] invoiceHeaders = null;
		if (groupComplianceResponseList != null
				&& groupComplianceResponseList.size() > 0) {
			if (criteria.getReturnType().equalsIgnoreCase("GSTR1")
					|| criteria.getReturnType().equalsIgnoreCase("GSTR1A")
					|| criteria.getReturnType().equalsIgnoreCase("GSTR3B")
					|| criteria.getReturnType().equalsIgnoreCase("GSTR6")
					|| criteria.getReturnType().equalsIgnoreCase("GSTR7")) {
				invoiceHeaders = commonUtility
						.getProp("grpcomp.report.headers.common").split(",");
				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"Group_Compliance_Common_Summary.xlsx");
			} else if (criteria.getReturnType().equalsIgnoreCase("GSTR9")) {
				invoiceHeaders = commonUtility
						.getProp("grpcomp.report.headers.gstr9.common")
						.split(",");
				workbook = createWorkbookWithExcelTemplate("ReportTemplates",
						"Group_Compliance_Gstr9_Summary.xlsx");
			} else if (criteria.getReturnType().equalsIgnoreCase("ITC04")) {
				String fy[] = criteria.getFinancialYear().split("-");
				Integer financialYear = Integer.parseInt(fy[0]);
				if (financialYear < 2021) {
					invoiceHeaders = commonUtility
							.getProp("grpcomp.report.headers.itc04.type1")
							.split(",");
					workbook = createWorkbookWithExcelTemplate(
							"ReportTemplates",
							"Group_Compliance_Itc04_Summary_Type1.xlsx");
				} else if (financialYear == 2021) {
					invoiceHeaders = commonUtility
							.getProp("grpcomp.report.headers.itc04.type2")
							.split(",");
					workbook = createWorkbookWithExcelTemplate(
							"ReportTemplates",
							"Group_Compliance_Itc04_Summary_Type2.xlsx");
				} else {
					invoiceHeaders = commonUtility
							.getProp("grpcomp.report.headers.itc04.type3")
							.split(",");
					workbook = createWorkbookWithExcelTemplate(
							"ReportTemplates",
							"Group_Compliance_Itc04_Summary_Type3.xlsx");
				}
			}
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(groupComplianceResponseList,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					groupComplianceResponseList.size(), true, "yyyy-mm-dd",
					false);
		}
		return workbook;
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;

	}

	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		LocalDate now = LocalDate.now();
		String tax = "0104" + "2021";
		LocalDate returnPeriod = LocalDate.parse(tax, formatter);
		if (returnPeriod.compareTo(now) < 0) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}
	}
}
