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
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataScreenResponseDto;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6AProcessedDataDao;
import com.ey.advisory.common.CommonUtility;

/**
 * @author Sasidhar
 *
 * 
 */

@Service("Gstr6AProcessedRecordsScreenServiceImpl")
public class Gstr6AProcessedRecordsScreenServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr6AProcessedRecordsScreenServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6AProcessedDataDaoImpl")
	Gstr6AProcessedDataDao gstr6AProcessedDataDao;

	public Workbook findGstr6aProcessedScreenDownload(List<Gstr6AProcessedDataResponseDto> responseData)
			throws Exception {
		Workbook workbook = new Workbook();

		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<Gstr6AProcessedDataScreenResponseDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
				responseData);

		workbook = createWorkbookWithExcelTemplate("ReportTemplates", "GSTR-6A_Processed_Summary_Screen_Download.xlsx");

		LOGGER.debug("GSTR-6A_Processed_Summary_Screen_Download" + responseFromViewProcess);

		if (responseFromViewProcess != null && responseFromViewProcess.size() > 0) {
			String[] invoiceHeaders = commonUtility.getProp("gstr6A.processed.rec.screen.report.headers").split(",");

			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromViewProcess, invoiceHeaders, isHeaderRequired, startRow,
					startcolumn, responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
		}

		return workbook;

	}

	private List<Gstr6AProcessedDataScreenResponseDto> convertProcessSummaryRecordsToScreenDtos(
			List<Gstr6AProcessedDataResponseDto> respDtos) {
		List<Gstr6AProcessedDataScreenResponseDto> dtos = new ArrayList<Gstr6AProcessedDataScreenResponseDto>();
		respDtos.stream().forEach(dto -> {
			Gstr6AProcessedDataScreenResponseDto screenDto = new Gstr6AProcessedDataScreenResponseDto();
			screenDto.setGstin(dto.getGstin());
			screenDto.setState(dto.getState());
			screenDto.setStatus(dto.getStatus());
			screenDto.setTimeStamp(dto.getTimeStamp());
			screenDto.setCount(dto.getCount().toString());
			screenDto.setInvoiceValue(dto.getInVoiceVal().toString());
			screenDto.setTaxableValue(dto.getTaxableValue().toString());
			screenDto.setTotalTax(dto.getTotalTax().toString());
			screenDto.setIgst(dto.getIgst().toString());
			screenDto.setCgst(dto.getCgst().toString());
			screenDto.setSgst(dto.getSgst().toString());
			screenDto.setCess(dto.getCess().toString());
			dtos.add(screenDto);
		});
		return dtos;
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
