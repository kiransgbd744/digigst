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
import com.ey.advisory.app.data.services.anx1.Gstr7ProcessedRecordsFetchService;
import com.ey.advisory.app.data.views.client.GSTR1ProcessedRecordsScreenDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr7ProcessedRecordsRespDto;
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
 */

@Service("Gstr7ProcessedScreenServiceImpl")
public class Gstr7ProcessedScreenServiceImpl
		implements Gstr7ProcessedScreenDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7ProcessedScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr7ProcessedRecordsFetchService")
	private Gstr7ProcessedRecordsFetchService gstr7ProcessedRecordsFetchService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Override
	public Workbook findProcessedScreenDownload(SearchCriteria criteria,
			PageRequest pageReq) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		Gstr1ProcessedRecordsReqDto finalRequest = new Gstr1ProcessedRecordsReqDto(
				SearchTypeConstants.REPORTS_SEARCH);
		finalRequest.setDataSecAttrs(request.getDataSecAttrs());
		finalRequest.setEntityId(request.getEntityId());
		finalRequest.setRetunPeriod(request.getTaxperiod());

		List<Gstr7ProcessedRecordsRespDto> responseFromView = gstr7ProcessedRecordsFetchService
				.response(finalRequest);
		List<GSTR1ProcessedRecordsScreenDto> responseFromViewProcess = convertProcessRecordsToScreenDtos(
				responseFromView);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-7_Processed_Records_Screen_Download.xlsx");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr7 Processed rec data response"
					+ responseFromViewProcess);
		}

		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("gstr7.processed.rec.screen.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewProcess,
					invoiceHeaders, isHeaderRequired, startRow, startcolumn,
					responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private List<GSTR1ProcessedRecordsScreenDto> convertProcessRecordsToScreenDtos(
			List<? extends Gstr7ProcessedRecordsRespDto> result) {
		List<GSTR1ProcessedRecordsScreenDto> dtos = new ArrayList<GSTR1ProcessedRecordsScreenDto>();
		result.stream().forEach(dto -> {
			GSTR1ProcessedRecordsScreenDto screenDto = new GSTR1ProcessedRecordsScreenDto();
			screenDto.setGSTIN(dto.getGstin());
			screenDto.setStateCode(dto.getGstin().substring(0, 2));
			screenDto.setStateName(dto.getState());
			screenDto.setRegistrationType("TDS");
			screenDto.setSaveStatus(dto.getSaveStatus());
			screenDto.setDateTime(dto.getSaveDateTime());
			screenDto.setCount(String.valueOf(dto.getCount()));
			screenDto.setTaxableValue(String.valueOf(dto.getTotalAmount()));
			screenDto.setIgst(String.valueOf(dto.getIgst()));
			screenDto.setCgst(String.valueOf(dto.getCgst()));
			screenDto.setSgst(String.valueOf(dto.getSgst()));
			dtos.add(screenDto);
		});
		return dtos;
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
