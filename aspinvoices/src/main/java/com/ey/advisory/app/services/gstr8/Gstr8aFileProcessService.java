package com.ey.advisory.app.services.gstr8;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr8aFileProcessService")
public class Gstr8aFileProcessService {

	@Autowired
	private Gstr8aRepositoryHandler repoHandler;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;
	

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDNR = "CDNR";
	private static final String CDNRA = "CDNRA";

	private static final List<String> supportedFunctionality = Lists.newArrayList(B2B, B2BA, CDNR, CDNRA);

	public int processGstr8aFile(InputStream inputStream,
			String fileName, Gstr1GetInvoicesReqDto dto, String userName) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr8aFileProcessService -> processGstr8aFile with fileName: [{}]",
					fileName);
		}
		int count = 0;
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

		String fileNameArray[] = fileNameWithoutExtension.split("_");
		String gstin = fileNameArray[4];
		String fy = fileNameArray[5];
		String taxPeriod = GenUtil
				.getFinancialPeriodFromFY(GenUtil.getFormattedFy(fy));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"From the processed file cgstin: [{}] and taxPeriod: [{}]",
					gstin, taxPeriod);
		}
		
		Map<String, List<Integer>> sectionsMap = Maps.newHashMap();
		sectionsMap.put(B2B, Lists.newArrayList(6, 18));
		sectionsMap.put(B2BA, Lists.newArrayList(7, 20));
		sectionsMap.put(CDNR, Lists.newArrayList(6, 19));
		sectionsMap.put(CDNRA, Lists.newArrayList(7, 22));

		LoadOptions options = new LoadOptions(FileFormatType.XLSX);
		CommonUtility.setAsposeLicense();
		Workbook workbook = new Workbook(inputStream, options);
		workbook.getSettings()
				.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);

		WorksheetCollection collection = workbook.getWorksheets();
		
		Long batchId = createBatchAndSave(gstin, GenUtil
				.getFinancialPeriodFromFY(GenUtil.getFormattedFy(fy)),
				userName);
		
		for (int i = 0; i < 4; i++) {
			Worksheet worksheet = collection.get(i);
			String sheetName = worksheet.getName();
			if (supportedFunctionality.contains(sheetName)) {
				long startTime = System.currentTimeMillis();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Processing the work sheet::[{}]", sheetName);
				}
				List<Integer> intList = sectionsMap.get(sheetName);
				count = count
						+ processData(worksheet, intList.get(0), intList.get(1), sheetName, gstin, taxPeriod, batchId, fy);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total time taken:: [{}] ms to process the sheet:: [{}]",
							(System.currentTimeMillis() - startTime), sheetName);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total records processed [{}]", count);
				}
			}
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
			Long batchId, String fy) throws Exception {
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
				pair = repoHandler.processUserB2BWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId, fy);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case B2BA: {
				pair = repoHandler.processUserB2BAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId, fy);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case CDNR: {
				pair = repoHandler.processUserCDNRWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId, fy);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case CDNRA: {
				pair = repoHandler.processUserCDNRAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId, fy);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
		}

		return count;
	}
	
	private Long createBatchAndSave(String gstin, String retPeriod,
			String userName) {

		batchRepo.softlyDelete("GSTR8A", APIConstants.GSTR8A.toUpperCase(),
				gstin, retPeriod);
		GetAnx1BatchEntity batch = batchUtil.makeBatch(gstin, retPeriod,
				"GSTR8A", APIConstants.GSTR8A.toUpperCase(), userName);
		batch = batchRepo.save(batch);
		return batch.getId();
	}
}
