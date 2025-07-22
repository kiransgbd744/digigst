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
import com.ey.advisory.app.data.daos.client.Ret1ProcessedRecordsDao;
import com.ey.advisory.app.data.views.client.Ret1ProcessedSummaryScreenDto;
import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsRequestDto;
import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsResponseDto;
import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Service("Ret1ProcessedScreenServiceImpl")
public class Ret1ProcessedScreenServiceImpl
		implements Ret1ProcessedScreenService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1ProcessedScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Ret1ProcessedRecordsServiceImpl")
	private Ret1ProcessedRecordsService ret1ProcessedRecordsService;

	@Autowired
	@Qualifier("Ret1ProcessedRecordsDaoImpl")
	private Ret1ProcessedRecordsDao ret1ProcessedRecordsDao;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Override
	public Workbook findProcessedScreen(Ret1ProcessedRecordsRequestDto criteria,
			PageRequest pageReq) throws Exception {

		// Ret1ProcessedRecordsRequestDto dto = new
		// Ret1ProcessedRecordsRequestDto();
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Ret1ProcessedRecordsResponseDto> processedRecordsResponseDtos = ret1ProcessedRecordsService
				.fetchProcessedRecords(criteria);
		List<Ret1ProcessedSummaryScreenDto> summaryData = convertAndSetTScreenObj(
				processedRecordsResponseDtos);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"RET1_Processed_Summary_screen.xlsx");

		LOGGER.debug("Ret1 Processed rec data response" + summaryData);

		if (summaryData != null && summaryData.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("ret1.processed.rec.screen.report.headers")
					.split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(summaryData, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, summaryData.size(),
					true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	/**
	 * @param ret1ProcessedRecordsResponseDtos
	 * @return
	 */
	private List<Ret1ProcessedSummaryScreenDto> convertAndSetTScreenObj(
			List<Ret1ProcessedRecordsResponseDto> ret1ProcessedRecordsResponseDtos) {
		List<Ret1ProcessedSummaryScreenDto> dtos = new ArrayList<>();
		for (Ret1ProcessedRecordsResponseDto dto : ret1ProcessedRecordsResponseDtos) {
			Ret1ProcessedSummaryScreenDto res = new Ret1ProcessedSummaryScreenDto();
			res.setGstin(dto.getGstin());
			res.setStateCode(dto.getGstin().substring(0, 2));
			res.setStateName(dto.getState());
			res.setRegistrationType(
					dto.getRegType().equals("") ? "Regular" : dto.getRegType());
			res.setSaveStatus(dto.getStatus());
			res.setDateTime(dto.getTimestamp());
			res.setLiability(dto.getLiability());
			res.setRevCharge(dto.getRevCharge());
			res.setOtherCharge(dto.getOtherCharge());
			res.setItc(dto.getItc());
			res.setTds(dto.getTds());
			res.setTcs(dto.getTcs());
			dtos.add(res);

		}

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
