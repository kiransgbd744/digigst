/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1ProcessedRecordsFetchService;
import com.ey.advisory.app.data.views.client.GSTR1ProcessedRecordsScreenDto;
import com.ey.advisory.app.services.search.docsummarysearch.GstnSummarySectionService;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenAdvReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenDocReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenHSNReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenSezReqRespHandler;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("Gstr1ProcessedRecordsScreenServiceImpl")
public class Gstr1ProcessedRecordsScreenServiceImpl
		implements Gstr1ProcessedScreenDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ProcessedRecordsScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsFetchService")
	private Gstr1ProcessedRecordsFetchService gstr1ProcessedRecordsFetchService;

	@Autowired
	@Qualifier("Gstr1SummaryScreenReqRespHandler")
	private Gstr1SummaryScreenReqRespHandler gstr1ReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenAdvReqRespHandler")
	private Gstr1SummaryScreenAdvReqRespHandler gstr1AdvReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenSezReqRespHandler")
	private Gstr1SummaryScreenSezReqRespHandler gstr1SezReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenDocReqRespHandler")
	private Gstr1SummaryScreenDocReqRespHandler gstr1DocReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenHSNReqRespHandler")
	private Gstr1SummaryScreenHSNReqRespHandler gstr1HsnReqRespHandler;

	@Autowired
	@Qualifier("GstnSummarySectionService")
	private GstnSummarySectionService gstnService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Override
	public Workbook findProcessedScreenDownload(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		List<Gstr1ProcessedRecordsRespDto> responseFromViewProcess = request
				.getReport();
		for (Gstr1ProcessedRecordsRespDto gstr1ProcessedRecordsRespDto : responseFromViewProcess) {

			gstr1ProcessedRecordsRespDto.setStateCode(
					gstr1ProcessedRecordsRespDto.getGstin().substring(0, 2));
		}
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		/*
		 * Gstr1ProcessedRecordsReqDto finalRequest = new
		 * Gstr1ProcessedRecordsReqDto( SearchTypeConstants.REPORTS_SEARCH);
		 * finalRequest.setDataSecAttrs(request.getDataSecAttrs());
		 * finalRequest.setEntityId(request.getEntityId());
		 * finalRequest.setRetunPeriod(request.getTaxperiod());
		 */

		/*
		 * SearchResult<Gstr1ProcessedRecordsRespDto> respDtos =
		 * gstr1ProcessedRecordsFetchService .find(finalRequest, null,
		 * Gstr1ProcessedRecordsRespDto.class);
		 */

		// List<Gstr1ReviwSummReportsReqDto> report =
		// convertProcessRecordsToScreenDtos(
		// request);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-1_Processed_Records_Screen_Download.xlsx");

		LOGGER.debug(
				"Gstr1 Processed rec data response" + responseFromViewProcess);

		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr1.processed.rec.screen.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewProcess,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	/*
	 * private List<GSTR1ProcessedRecordsScreenDto>
	 * convertProcessRecordsToScreenDtos( Gstr1ReviwSummReportsReqDto request) {
	 * List<GSTR1ProcessedRecordsScreenDto> dtos = new
	 * ArrayList<GSTR1ProcessedRecordsScreenDto>(); request.stream().forEach(dto
	 * -> { GSTR1ProcessedRecordsScreenDto screenDto = new
	 * GSTR1ProcessedRecordsScreenDto(); screenDto.setGSTIN(dto.getGstin());
	 * screenDto.setStateCode(dto.getGstin().substring(0, 2));
	 * screenDto.setStateName(dto.getState()); List<String> regName =
	 * gSTNDetailRepository .findRegTypeByGstin(dto.getGstin()); if (regName !=
	 * null && regName.size() > 0) { String regTypeName = regName.get(0);
	 * 
	 * screenDto.setRegistrationType(regTypeName.toUpperCase()); } else {
	 * screenDto.setRegistrationType(""); }
	 * 
	 * //screenDto.setRegistrationType(dto.getRegType());
	 * screenDto.setSaveStatus(dto.getStatus());
	 * screenDto.setDateTime(dto.getTimeStamp());
	 * screenDto.setCount(String.valueOf(dto.getCount()));
	 * screenDto.setTaxableValue(String.valueOf(dto.getSupplies()));
	 * screenDto.setIgst(String.valueOf(dto.getIgst()));
	 * screenDto.setCgst(String.valueOf(dto.getCgst()));
	 * screenDto.setSgst(String.valueOf(dto.getSgst()));
	 * screenDto.setCess(String.valueOf(dto.getCess())); dtos.add(screenDto);
	 * }); return dtos; }
	 */

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