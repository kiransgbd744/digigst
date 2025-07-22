package com.ey.advisory.app.services.jobs.gstr6;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.javatuples.Pair;
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
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr6aFileProcessService")
public class Gstr6aFileProcessService {

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDNR = "CDNR";
	private static final String CDNRA = "CDNRA";

	private static final List<String> supportedFunctionality = Lists
			.newArrayList(B2B, B2BA, CDNR, CDNRA);

	@Autowired
	private Get6aRepositoryHandler repoHandler;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	public int processGstr2File(InputStream inputStream, String fileName,
			Long batchId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6aFileProcessService -> processGstr6File with fileName: [{}]",
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
		Map<String, List<Integer>> sectionsMap = Maps.newHashMap();
		sectionsMap.put(B2B, Lists.newArrayList(6, 19));
		sectionsMap.put(B2BA, Lists.newArrayList(7, 18));
		sectionsMap.put(CDNR, Lists.newArrayList(6, 22));
		sectionsMap.put(CDNRA, Lists.newArrayList(7, 22));

		LoadOptions options = new LoadOptions(FileFormatType.XLSX);
		CommonUtility.setAsposeLicense();
		Workbook workbook = new Workbook(inputStream, options);
		workbook.getSettings()
				.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Choosen worksheets to be processed: [{}]",
					supportedFunctionality);
		}

		WorksheetCollection collection = workbook.getWorksheets();

		for (int i = 0; i <= 4; i++) {
			Worksheet worksheet = collection.get(i);
			String sheetName = worksheet.getName();
			if (supportedFunctionality.contains(sheetName)) {
				long startTime = System.currentTimeMillis();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Processing the work sheet::[{}]", sheetName);
				}
				List<Integer> intList = sectionsMap.get(sheetName);
				count = count + processData(worksheet, intList.get(0),
						intList.get(1), sheetName, cgstin, taxPeriod, batchId);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Total time taken:: [{}] ms to process the sheet:: [{}]",
							(System.currentTimeMillis() - startTime),
							sheetName);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total records processed [{}]", count);
				}
			}
		}

		batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null, false);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total no of records [{}] processed.", count);
		}
		return count;
	}

	class ProcessWorkSheetTask implements Callable<Integer> {
		private String sheetName;
		private Worksheet worksheet;
		private Map<String, List<Integer>> sectionsMap;
		private String cgstin;
		private String taxPeriod;

		public ProcessWorkSheetTask(String sheetName, Worksheet worksheet,
				Map<String, List<Integer>> sectionsMap, String cgstin,
				String taxPeriod) {
			this.sheetName = sheetName;
			this.worksheet = worksheet;
			this.sectionsMap = sectionsMap;
			this.cgstin = cgstin;
			this.taxPeriod = taxPeriod;
		}

		@Override
		public Integer call() throws Exception {
			long startTime = System.currentTimeMillis();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Processing the work sheet::[{}]", sheetName);
			}
			List<Integer> intList = sectionsMap.get(sheetName);
			int count = processData(worksheet, intList.get(0), intList.get(1),
					sheetName, cgstin, taxPeriod, null);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Total time taken:: [{}] ms to process the sheet:: [{}]",
						(System.currentTimeMillis() - startTime), sheetName);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Total records processed [{}]", count);
			}
			return count;
		}
	}

	private int processData(Worksheet worksheet, int startRow, int columnCount,
			String sheetName, String cgstin, String taxPeriod, Long batchId)
			throws Exception {
		Cells cells = worksheet.getCells();
		Object[][] objList = cells.exportArray(startRow, 0,
				cells.getMaxDataRow(), columnCount);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Object list [{}] for the work sheet::[{}]",
					objList.length, sheetName);
		}
		Pair<Long, Integer> pair = null;
		int count = 0;

		switch (sheetName) {
		case B2B: {
			pair = repoHandler.processB2BWorkSheetData(objList, columnCount,
					cgstin, taxPeriod, batchId);
			batchId = pair.getValue0();
			if (batchId != 0L) {
				count = pair.getValue1();

				if (count > 0) {
					// Proc call to insert the Delta data into original
					// tables
					docRepository.getGstr6aProcCall(cgstin, taxPeriod, "B2B",
							batchId, true);
				}
			}
			break;
		}
		case B2BA: {
			pair = repoHandler.processB2BAWorkSheetData(objList, columnCount,
					cgstin, taxPeriod, batchId);
			batchId = pair.getValue0();
			if (batchId != 0L) {
				count = pair.getValue1();

				if (count > 0) {
					// Proc call to insert the Delta data into original
					// tables
					docRepository.getGstr6aProcCall(cgstin, taxPeriod, B2BA,
							batchId, true);
				}
			}
			break;
		}
		case CDNR: {
			pair = repoHandler.processCDNRWorkSheetData(objList, columnCount,
					cgstin, taxPeriod, batchId);
			batchId = pair.getValue0();
			if (batchId != 0L) {
				count = pair.getValue1();
				if (count > 0) {
					// Proc call to insert the Delta data into original
					// tables
					docRepository.getGstr6aProcCall(cgstin, taxPeriod, "CDN",
							batchId, true);
				}
			}
			break;
		}
		case CDNRA: {
			pair = repoHandler.processCDNRAWorkSheetData(objList, columnCount,
					cgstin, taxPeriod, batchId);
			batchId = pair.getValue0();
			if (batchId != 0L) {
				count = pair.getValue1();
				if (count > 0) {
					// Proc call to insert the Delta data into original
					// tables
					docRepository.getGstr6aProcCall(cgstin, taxPeriod, "CDNA",
							batchId, true);
				}
			}
			break;
		}
		default:
			System.out.println(
					"Not supported/enabled to process the sheet::" + sheetName);
		}
		return count;
	}
}
