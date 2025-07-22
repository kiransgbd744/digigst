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
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.services.anx1.Anx1ProcessedRecordsFetchService;
import com.ey.advisory.app.data.views.client.Anx1ProcessedScreenDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsFinalRespDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ProcessedRecordsRespDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Service("Anx1ProcessedScreenDownloadServiceImpl")
public class Anx1ProcessedScreenDownloadServiceImpl
		implements Anx1ProcessedScreenDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ProcessedScreenDownloadServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1ProcessedRecordsFetchService")
	Anx1ProcessedRecordsFetchService anx1ProcessedRecordsFetchService;

	@Override
	public Workbook findProcessedScreenDownload(SearchCriteria criteria,
			PageRequest pageReq) {
		Anx1ProcessedRecordsReqDto request = (Anx1ProcessedRecordsReqDto) criteria;
		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Anx1ProcessedRecordsFinalRespDto> respDtos = anx1ProcessedRecordsFetchService
				.find(request, "ANX1");
		List<Anx1ProcessedScreenDto> summaryData = convertAndSetTScreenObj(
				respDtos);

		if (summaryData != null && summaryData.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("anx1.processed.screen.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"ANX-1_Processed Records_Screen Download.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(summaryData, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, summaryData.size(),
					true, "yyyy-mm-dd", false);
		}
		return workbook;

	}

	/**
	 * @param respDtos
	 * @return
	 */
	private List<Anx1ProcessedScreenDto> convertAndSetTScreenObj(
			List<Anx1ProcessedRecordsFinalRespDto> respDtos) {
		List<Anx1ProcessedScreenDto> dtos = new ArrayList<>();
		for (Anx1ProcessedRecordsFinalRespDto dto : respDtos) {
			if (dto.getOutType().equals("Outward")) {
				Anx1ProcessedScreenDto screenDto = createAndAddDataToScreenObj(dto);
				screenDto.setTransactionType(dto.getOutType());
				screenDto.setCount(dto.getOutCount());
				screenDto.setTaxableValue(dto.getOutSupplies());
				screenDto.setIgst(dto.getOutIgst());
				screenDto.setCgst(dto.getOutCgst());
				screenDto.setSgst(dto.getOutSgst());
				screenDto.setCess(dto.getOutCess());
				dtos.add(screenDto);
			}

			if (dto.getInType().equals("Inward")) {
				Anx1ProcessedScreenDto screenDto = createAndAddDataToScreenObj(dto);
				screenDto.setTransactionType(dto.getInType());
				screenDto.setCount(dto.getInCount());
				screenDto.setTaxableValue(dto.getInSupplies());
				screenDto.setIgst(dto.getInIgst());
				screenDto.setCgst(dto.getInCgst());
				screenDto.setSgst(dto.getInSgst());
				screenDto.setCess(dto.getInCess());
				dtos.add(screenDto);
			}

		}
		return dtos;
	}

	/**
	 * @return
	 */
	private Anx1ProcessedScreenDto createAndAddDataToScreenObj(Anx1ProcessedRecordsFinalRespDto dto) {
		Anx1ProcessedScreenDto screenDto = new Anx1ProcessedScreenDto();
		screenDto.setGSTIN(dto.getGstin());
		screenDto.setStateCode(dto.getGstin().substring(0, 2));
		screenDto.setStateName(dto.getState());
		screenDto.setRegistrationType(
				dto.getRegType().equals("") ? "Regular" : dto.getRegType());
		screenDto.setSaveStatus(dto.getStatus());
		screenDto.setDateTime(dto.getTimeStamp());
		return screenDto;
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
