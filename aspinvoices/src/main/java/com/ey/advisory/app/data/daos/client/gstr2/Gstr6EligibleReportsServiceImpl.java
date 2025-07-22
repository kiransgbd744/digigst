package com.ey.advisory.app.data.daos.client.gstr2;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenResponseDto;
import com.ey.advisory.app.docs.service.gstr6.Gstr6DistributedSummaryService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Gstr6EligibleReportsServiceImpl")
@Slf4j
public class Gstr6EligibleReportsServiceImpl {

	@Autowired
	CommonUtility commonUtility;

/*	@Autowired
	@Qualifier("Gstr6EntityLevelSummReportsDaoImpl")
	private Gstr6EntityLevelSummReportsDaoImpl gstr6EntityLevelReportsDaoImpl;
*/	
	
	@Autowired
	@Qualifier("Gstr6DistributedSummaryServiceImpl")
	private Gstr6DistributedSummaryService gstr6DistributedSummaryScreenService;
	
	
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	/**
	 * Eligible Data For Distribution 
	 * @param setDataSecurity
	 * @param pageReq
	 * @return
	 */
	
	public Workbook findEligibleDistrbSummary(Annexure1SummaryReqDto setDataSecurity,
			PageRequest pageReq) {
		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) setDataSecurity;
		Workbook workbook = new Workbook();
		
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr6DistributedSummaryScreenResponseDto> responseFromView = new ArrayList<>();
		responseFromView = gstr6DistributedSummaryScreenService.getGstr6DistributedEliSummaryList(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Distribution_Eligible_Report.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6A.eligible.distribution.report.headers").split(",");
			
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			
		
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;

	}
	
	/**
	 * Inligible Data For Distribution
	 * @param setDataSecurity
	 * @param pageReq
	 * @return
	 */
	
	public Workbook findInEligibleSummary(Annexure1SummaryReqDto setDataSecurity,
			PageRequest pageReq) {
		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) setDataSecurity;
		Workbook workbook = new Workbook();
		
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr6DistributedSummaryScreenResponseDto> responseFromView = new ArrayList<>();
		responseFromView = gstr6DistributedSummaryScreenService.getGstr6DistributedInEliSummaryList(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Distribution_Ineligible_Report.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6A.ineligible.distribution.report.headers").split(",");
			
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			
		
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;

	}


	
	/**
	 * Eligible Data For ReDistribution
	 * @param setDataSecurity
	 * @param pageReq
	 * @return
	 */
	
	public Workbook findReDistrbEligibleSummary(Annexure1SummaryReqDto setDataSecurity,
			PageRequest pageReq) {
		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) setDataSecurity;
		Workbook workbook = new Workbook();
		
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr6DistributedSummaryScreenResponseDto> responseFromView = new ArrayList<>();
		responseFromView = gstr6DistributedSummaryScreenService.getGstr6ReDistributedSummaryList(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Redistribution_Eligible_Report.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6A.eligible.redistribution.report.headers").split(",");
			
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			
		
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;

	}

	/**
	 * InEligible Data For ReDistribution
	 * @param setDataSecurity
	 * @param pageReq
	 * @return
	 */
	
	public Workbook findReDistrbInEligibleSummary(Annexure1SummaryReqDto setDataSecurity,
			PageRequest pageReq) {
		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) setDataSecurity;
		Workbook workbook = new Workbook();
		
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		List<Gstr6DistributedSummaryScreenResponseDto> responseFromView = new ArrayList<>();
		responseFromView = gstr6DistributedSummaryScreenService.getGstr6ReDistributedInEligibleSummaryList(request);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Redistribution_Ineligible_Report.xlsx");

		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr6A.ineligible.redistribution.report.headers").split(",");
			
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			
		
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
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

	
	
	
}
