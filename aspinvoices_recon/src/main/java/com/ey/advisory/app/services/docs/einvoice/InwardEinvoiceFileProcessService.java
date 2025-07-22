package com.ey.advisory.app.services.docs.einvoice;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceRepositoryHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("InwardEinvoiceFileProcessService")
public class InwardEinvoiceFileProcessService {

	@Autowired
	private InwardEinvoiceRepositoryHandler repoHandler;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private GSTNDetailRepository gstnRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	public int processInwardEinvoiceFile(InputStream inputStream,
			String fileName, Gstr1GetInvoicesReqDto dto) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"InwardEinvoiceFileProcessService -> processInwardEinvoiceFile with fileName: [{}]",
					fileName);
		}
		int count = 0;
		String fileNameArray[] = fileName.split("_");
		String cgstin = fileNameArray[2];
		String taxPeriod = fileNameArray[3];
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"From the processed file cgstin: [{}] and taxPeriod: [{}]",
					cgstin, taxPeriod);
		}

		LoadOptions options = new LoadOptions(FileFormatType.XLSX);
		CommonUtility.setAsposeLicense();
		Workbook workbook = new Workbook(inputStream, options);
		workbook.getSettings()
				.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);

		WorksheetCollection collection = workbook.getWorksheets();

		Worksheet worksheet = collection.get(0);
		String sheetName = worksheet.getName();
		long startTime = System.currentTimeMillis();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing the work sheet::[{}]", sheetName);
		}
		count = count + processData(worksheet, 1, 147, sheetName, cgstin,
				taxPeriod, dto);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Total time taken:: [{}] ms to process the sheet:: [{}]",
					(System.currentTimeMillis() - startTime), sheetName);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total records processed [{}]", count);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total no of records [{}] processed.", count);
		}
		return count;
	}

	private int processData(Worksheet worksheet, int startRow, int columnCount,
			String sheetName, String cgstin, String taxPeriod,
			Gstr1GetInvoicesReqDto dto) throws Exception {
		Cells cells = worksheet.getCells();
		Object[][] objList = cells.exportArray(startRow, 0,
				cells.getMaxDataRow(), columnCount);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Object list [{}] for the work sheet::[{}]",
					objList.length, sheetName);
		}
		Integer pair = null;
		int count = 0;

		pair = repoHandler.processInwardEinvoiceWorkSheetData(objList,
				columnCount, cgstin, taxPeriod, dto);

		count = pair;

		return count;
	}
}
