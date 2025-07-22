/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.beust.jcommander.internal.Lists;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.views.client.ComptReversalAmtDto;
import com.ey.advisory.app.data.views.client.ReversalComputeDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("ItcReversalInwardServiceImpl")
public class ItcReversalInwardServiceImpl
		implements ItcReversalInwardReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItcReversalInwardServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ComputationofRevAmtDaoImpl")
	private ItcReversalInwardDao itcComptDao;

	@Autowired
	@Qualifier("ComptTurnoverReportDaoImpl")
	private ItcReversalInwardDao itcReversalComptDao;

	@Autowired
	@Qualifier("ItcInwardReportDaoImpl")
	private ItcReversalInwardDao itcReversalInwardDao;

	@Autowired
	@Qualifier("ItcOutwardReportDaoImpl")
	private ItcReversalInwardDao itcReversalOutwardDao;

	@Autowired
	@Qualifier("ItcB2csReportDaoImpl")
	private ItcReversalInwardDao itcReversalb2CSDao;

	@Autowired
	@Qualifier("ItcNilNonDaoImpl")
	private ItcNilNonDaoImpl itcReversalnilDao;
	
	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;

	@Override
	public Workbook findItcReversal(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Object> responseFromcompAmt = new ArrayList<>();
		responseFromcompAmt = itcComptDao.getItcReports(request);

		List<Object> responseFromcomp = new ArrayList<>();
		responseFromcomp = itcReversalComptDao.getItcReports(request);

		List<Object> responseFromdist = new ArrayList<>();
		responseFromdist = itcReversalInwardDao.getItcReports(request);

		List<Object> responseFromrev = new ArrayList<>();
		responseFromrev = itcReversalOutwardDao.getItcReports(request);

		List<Object> responseFromB2CS = new ArrayList<>();
		responseFromB2CS = itcReversalb2CSDao.getItcReports(request);

		List<Object> responseFromNil = new ArrayList<>();
		responseFromNil = itcReversalnilDao.getItcReports(request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"Revsersal_Inward.xlsx");
		
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("ITC Reversal data response" + responseFromcomp);
		}
		
		String entityName = repo.findEntityNameByEntityId(request.getEntityId().stream().findFirst().get());
		String fromTaxPeriod = request.getTaxperiod();
		LocalDate startDate = LocalDate.of(
				Integer.parseInt(fromTaxPeriod.substring(2)),
				Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
		String taxPeriod = startDate.getMonth().getDisplayName(TextStyle.SHORT,
				Locale.US) + "-" + startDate.getYear();

		if (responseFromcompAmt != null && responseFromcompAmt.size() > 0) {
			List<ComptReversalAmtDto> computeDtoList = Lists.newArrayList();

			responseFromcompAmt.forEach(dto -> {
				ComptReversalAmtDto computeDto = (ComptReversalAmtDto) dto;
				computeDtoList.add(computeDto);
			});
			
			ReversalAmtItcUtil.prepareDataForComputeAmtItcReport(workbook,
					computeDtoList, entityName, taxPeriod);

		}

		if (responseFromcomp != null && responseFromcomp.size() > 0) {
			List<ReversalComputeDto> computeDtoList = Lists.newArrayList();

			responseFromcomp.forEach(dto -> {
				ReversalComputeDto computeDto = (ReversalComputeDto) dto;
				computeDtoList.add(computeDto);
			});

			try {
				ReversalComputeItcUtil.prepareDataForComputeItcReport(workbook,
						computeDtoList, entityName, taxPeriod);
			} catch(Exception e) {
				LOGGER.error("Exception :->", e);
			}
			

		}

		if (responseFromdist != null && responseFromdist.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("reversal.inward.api.report.columns").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(2).getCells();
			errorDumpCells.importCustomObjects(responseFromdist, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromdist.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromrev != null && responseFromrev.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("reversal.outward.api.report.columns").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(3).getCells();
			errorDumpCells.importCustomObjects(responseFromrev, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromrev.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromB2CS != null && responseFromB2CS.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("reversal.B2CS.api.report.columns").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(4).getCells();
			errorDumpCells.importCustomObjects(responseFromB2CS, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromB2CS.size(), true, "yyyy-mm-dd", false);
		}

		if (responseFromNil != null && responseFromNil.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.Asp.Nil.itc.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(5).getCells();
			errorDumpCells.importCustomObjects(responseFromNil, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromNil.size(), true, "yyyy-mm-dd", false);
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
