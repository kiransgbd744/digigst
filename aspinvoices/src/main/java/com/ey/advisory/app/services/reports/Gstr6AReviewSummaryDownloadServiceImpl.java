package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataItemsResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataScreenResponseDto;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ASummaryDataDaoImpl;
import com.ey.advisory.common.CommonUtility;
import com.google.common.collect.Lists;

@Service("Gstr6AReviewSummaryDownloadServiceImpl")
public class Gstr6AReviewSummaryDownloadServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr6AReviewSummaryDownloadServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6ASummaryDataDaoImpl")
	Gstr6ASummaryDataDaoImpl gstr6ASummaryDataDaoImpl;

	public Workbook getGstr6aReviewsummReports(List<Gstr6ASummaryDataResponseDto> responseData) throws Exception {
		Workbook workbook = new Workbook();

		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Gstr6ASummaryDataScreenResponseDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
				responseData);
		LOGGER.error("responseFromViewProcess -- >", responseFromViewProcess);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates", "GSTR-6A_Review_Summary_Screen_Download.xlsx");

		LOGGER.debug("Gstr6 Processed rec data response" + responseFromViewProcess);

		if (responseFromViewProcess != null && responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr6A.review.rec.screen.report.headers").split(",");
			LOGGER.error("responseFromViewProcess -- >", invoiceHeaders);
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewProcess, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private List<Gstr6ASummaryDataScreenResponseDto> convertProcessSummaryRecordsToScreenDtos(
			List<Gstr6ASummaryDataResponseDto> response) {
		List<Gstr6ASummaryDataScreenResponseDto> dtos = Lists.newLinkedList();
		response.stream().forEach(dto -> {
			Gstr6ASummaryDataScreenResponseDto screenDto = new Gstr6ASummaryDataScreenResponseDto();
			screenDto.setTable(String.valueOf(dto.getTable()));
			screenDto.setCount(String.valueOf(dto.getCount()));
			screenDto.setInVoiceVal(String.valueOf(dto.getInVoiceVal()));
			screenDto.setTaxableValue(String.valueOf(dto.getTaxableValue()));
			screenDto.setIgst(String.valueOf(dto.getIgst()));
			screenDto.setCgst(String.valueOf(dto.getCgst()));
			screenDto.setSgst(String.valueOf(dto.getSgst()));
			screenDto.setCess(String.valueOf(dto.getCess()));
			dtos.add(screenDto);
			buildItemsForSection(dtos, dto);
		});
		return dtos;
	}

	private void buildItemsForSection(List<Gstr6ASummaryDataScreenResponseDto> dtos, Gstr6ASummaryDataResponseDto dto) {
		List<Gstr6ASummaryDataItemsResponseDto> items = dto.getItems();
		if (CollectionUtils.isNotEmpty(items)) {
			items.forEach(item -> {
				Gstr6ASummaryDataScreenResponseDto screenDto1 = new Gstr6ASummaryDataScreenResponseDto();
				screenDto1.setTable(String.valueOf(" -" + item.getTable()));
				screenDto1.setCount(String.valueOf(item.getCount()));
				screenDto1.setInVoiceVal(String.valueOf(item.getInVoiceVal()));
				screenDto1.setTaxableValue(String.valueOf(item.getTaxableValue()));
				screenDto1.setIgst(String.valueOf(item.getIgst()));
				screenDto1.setCgst(String.valueOf(item.getCgst()));
				screenDto1.setSgst(String.valueOf(item.getSgst()));
				screenDto1.setCess(String.valueOf(item.getCess()));
				dtos.add(screenDto1);
			});
		}
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName, String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings().setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}

}
