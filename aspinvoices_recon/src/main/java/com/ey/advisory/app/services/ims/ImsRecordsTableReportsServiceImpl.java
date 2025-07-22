package com.ey.advisory.app.services.ims;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.Font;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.service.ims.ImsEntityLevelSummaryService;
import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.app.service.ims.ImsEntitySummaryResponseDto;
import com.ey.advisory.app.service.ims.ImsGetCallResponseDto;
import com.ey.advisory.app.service.ims.ImsGetCallService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;

@Service("ImsRecordsTableReportsServiceImpl")
public class ImsRecordsTableReportsServiceImpl
		implements ImsRecordsTableReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ImsRecordsTableReportsServiceImpl.class);

	@Autowired
	private CommonUtility commonUtility;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("ImsRecordsTableReportDaoImpl") private
	 * ImsRecordsTableReportDao imsRecordsTableReportDao;
	 */

	@Autowired
	@Qualifier("ImsEntityLevelSummaryServiceImpl")
	private ImsEntityLevelSummaryService imsEntityLevelSummaryService;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;

	@Autowired
	@Qualifier("ImsGetCallServiceImpl")
	private ImsGetCallService service;

	@Override
	public Workbook findImsSummaryEntityLevel(ImsEntitySummaryReqDto criteria,
			PageRequest pageReq) {
		ImsEntitySummaryReqDto request = (ImsEntitySummaryReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 5;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		String entityName = repo
				.findEntityNameByEntityId(request.getEntityId());

		List<ImsEntitySummaryResponseDto> responseFromQuery = imsEntityLevelSummaryService
				.getImsSummaryEntityLvlData(request);

		List<ImsSummaryResponseReportDto> reportDtoList = new ArrayList<>();

		for (ImsEntitySummaryResponseDto responseDto : responseFromQuery) {

			ImsSummaryResponseReportDto obj = new ImsSummaryResponseReportDto();

			obj.setGstin(responseDto.getGstin());
			obj.setGstnTotalCount(responseDto.getGstnTotal());
			obj.setGstnNoActionCount(responseDto.getGstnNoAction());
			obj.setGstnAcceptedCount(responseDto.getGstnAccepted());
			obj.setGstnRejectedCount(responseDto.getGstnRejected());
			obj.setGstnPendingCount(responseDto.getGstnPendingTotal());
			obj.setTotalCount(responseDto.getAspTotal());
			obj.setNoActionCount(responseDto.getAspNoAction());
			obj.setAcceptedCount(responseDto.getAspAccepted());
			obj.setRejectedCount(responseDto.getAspRejected());
			obj.setPendingCount(responseDto.getAspPendingTotal());

			reportDtoList.add(obj);

		}

		/*DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDate = LocalDateTime.now().format(formatter);*/

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"IMS_Count_Summary.xlsx");
		// Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

		Cells reportCells = workbook.getWorksheets().get(0).getCells();
		Worksheet sheet = workbook.getWorksheets().get(0);

		Cell cellB1 = sheet.getCells().get("B1");
		cellB1.setValue(entityName);
		Cell cellB2 = sheet.getCells().get("B2");
		cellB2.setValue(formatDateTime(LocalDateTime.now()));
		Style style = workbook.createStyle();
		Font font = style.getFont();
		font.setBold(true);
		font.setSize(12);

		if (reportDtoList != null && reportDtoList.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("get.summary.ims.report.column.mapping")
					.split(",");

			reportCells.importCustomObjects(reportDtoList, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					reportDtoList.size(), true, "yyyy-mm-dd", false);
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

	@Override
	public Workbook findImsRecordsGetcall(ImsEntitySummaryReqDto criteria,
			PageRequest pageReq) {
		ImsEntitySummaryReqDto request = (ImsEntitySummaryReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 4;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		String entityName = repo
				.findEntityNameByEntityId(request.getEntityId());

		List<ImsGetCallResponseDto> responseList = service
				.getImsCallSummary(criteria);

		List<ImsAnx1DashboardResponseReportDto> reportDtoList = new ArrayList<>();

		for (ImsGetCallResponseDto responseDto : responseList) {

			ImsAnx1DashboardResponseReportDto reportDto = new ImsAnx1DashboardResponseReportDto();

			reportDto.setGstin(responseDto.getGstin());

			if ("NOT INITIATED"
					.equalsIgnoreCase(responseDto.getInvCountStatus())) {
				reportDto.setInvCountGetCall(responseDto.getInvCountStatus());
			}else{
				reportDto.setInvCountGetCall(responseDto.getInvCountStatus()
						+ " (" + responseDto.getInvCountTimeStamp() + ")");
			}
			if ("NOT INITIATED"
					.equalsIgnoreCase(responseDto.getInvDetailsStatus())) {
				reportDto.setInvDetailsGetCall(
						responseDto.getInvDetailsStatus());

			} else{
				reportDto.setInvDetailsGetCall(responseDto.getInvDetailsStatus()
						+ " (" + responseDto.getInvDetailsTimeStamp() + ")");
			}
			

			reportDtoList.add(reportDto);
		}
	 
	
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"IMS_GetCall_Dashboard.xlsx");
		Cells reportCells = workbook.getWorksheets().get(0).getCells();

		Worksheet sheet = workbook.getWorksheets().get(0);

		Cell cellB1 = sheet.getCells().get("B1");
		cellB1.setValue(entityName);
		Cell cellB2 = sheet.getCells().get("B2");
		cellB2.setValue(formatDateTime(LocalDateTime.now()));
		Style style = workbook.createStyle();
		Font font = style.getFont();
		font.setBold(true);
		font.setSize(12);

		if (reportDtoList != null && reportDtoList.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("get.call.ims.report.column.mapping").split(",");

			reportCells.importCustomObjects(reportDtoList, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					reportDtoList.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;

	}
	private String formatDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null)
			return null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		
		    ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));

	        // Convert to IST
	        ZonedDateTime istNow = utcNow.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

	        // Format the date and time
	        String formattedDate = istNow.format(formatter);
		
		return formattedDate;
	}
}
