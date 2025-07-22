package com.ey.advisory.app.services.docs.gstr2a;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesB2bHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpHeaderEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceCdnGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceCdnurGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceExpGstnRepository;
import com.ey.advisory.common.CommonUtility;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
@Qualifier("Gstr1FileProcessService")
public class Gstr1FileProcessService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1FileProcessService.class);

	private static final String B2B = "B2B";
	private static final String CDNR = "CDNR";
	private static final String CDNUR = "CDNUR";
	private static final String EXP = "EXP";

	private static final List<String> supportedFunctionality = Lists
			.newArrayList(B2B, CDNR, CDNUR, EXP);

	@Autowired
	private Gstr1ProcessUtil gstr1ProcessUtil;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceB2bGstnRepository")
	private Gstr1GetEInvoiceB2bGstnRepository gstr1GetEInvoiceB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceCdnGstnRepository")
	private Gstr1GetEInvoiceCdnGstnRepository gstr1GetEInvoiceCdnGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceCdnurGstnRepository")
	private Gstr1GetEInvoiceCdnurGstnRepository gstr1GetEInvoiceCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceExpGstnRepository")
	private Gstr1GetEInvoiceExpGstnRepository gstr1GetEInvoiceExpGstnRepository;

	public int processGstr1File(InputStream inputStream, String fileName,
			Long batchId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr1FileProcessService -> processGstr1File with fileName: [{}]",
					fileName);
		}
		int count = 0;

		Map<String, List<Integer>> sectionsMap = Maps.newHashMap();
		sectionsMap.put(B2B, Lists.newArrayList(1, 24));
		sectionsMap.put(CDNR, Lists.newArrayList(1, 24));
		sectionsMap.put(CDNUR, Lists.newArrayList(1, 19));
		sectionsMap.put(EXP, Lists.newArrayList(1, 19));

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

		for (int i = 0; i <= 3; i++) {
			Worksheet worksheet = collection.get(i);
			String sheetName = worksheet.getName();
			if (supportedFunctionality.contains(sheetName)) {
				long startTime = System.currentTimeMillis();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Processing the work sheet::[{}]", sheetName);
				}
				List<Integer> intList = sectionsMap.get(sheetName);
				count = count + processData(worksheet, intList.get(0),
						intList.get(1), sheetName, batchId);
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total no of records [{}] processed.", count);
		}
		return count;
	}

	class ProcessWorkSheetTask implements Callable<Integer> {
		private String sheetName;
		private Worksheet worksheet;
		private Map<String, List<Integer>> sectionsMap;

		public ProcessWorkSheetTask(String sheetName, Worksheet worksheet,
				Map<String, List<Integer>> sectionsMap) {
			this.sheetName = sheetName;
			this.worksheet = worksheet;
			this.sectionsMap = sectionsMap;

		}

		@Override
		public Integer call() throws Exception {
			long startTime = System.currentTimeMillis();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Processing the work sheet::[{}]", sheetName);
			}
			List<Integer> intList = sectionsMap.get(sheetName);
			int count = processData(worksheet, intList.get(0), intList.get(1),
					sheetName, null);
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
			String sheetName, Long batchId) {
		Cells cells = worksheet.getCells();
		Object[][] objList = cells.exportArray(startRow, 0,
				cells.getRows().getCount(), columnCount);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Object list [{}] for the work sheet::[{}]",
					objList.length, sheetName);
		}
		Pair<Long, Integer> pair = null;
		int count = 0;
		switch (sheetName) {
		case B2B: {
			count = processB2BWorkSheetData(objList, columnCount, batchId);
			break;
		}
		case CDNR: {
			count = processCDNRWorkSheetData(objList, columnCount, batchId);
			break;
		}
		case CDNUR: {
			count = processCDNURWorkSheetData(objList, columnCount, batchId);
			break;
		}
		case EXP: {
			count = processEXPWorkSheetData(objList, columnCount, batchId);
			break;
		}

		default:
			System.out.println(
					"Not supported/enabled to process the sheet::" + sheetName);
		}
		return count;
	}

	private int processB2BWorkSheetData(Object[][] objList, int columnCount,
			Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr1EInvoicesB2bHeaderEntity> b2bList = gstr1ProcessUtil
				.convertB2bWorkSheetDataToList(objList, columnCount, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}]",
					b2bList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(b2bList)) {
			b2bList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getInvDate()
						+ "_" + data.getInvNum() + "_" + data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}
				if (data.getGstin() != null && data.getInvDate() != null
						&& data.getInvNum() != null
						&& data.getInvType() != null) {
					List<GetGstr1EInvoicesB2bHeaderEntity> entityList = gstr1GetEInvoiceB2bGstnRepository
							.findByInvoiceKey(data.getGstin(),
									data.getInvDate(), data.getInvNum(),
									data.getInvType());
					if (entityList != null
							&& CollectionUtils.isNotEmpty(entityList)) {
						totalIds.addAll(entityList.stream()
								.map(GetGstr1EInvoicesB2bHeaderEntity::getId)
								.collect(Collectors.toList()));
					}
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			partions.forEach(partion -> gstr1GetEInvoiceB2bGstnRepository
					.updateSameRecords(partion));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BWorkSheetData ->Records ready for insert/update list size [{}]",
					b2bList.size());
		}
		stopWatch.start("Insert/update B2b data");
		gstr1GetEInvoiceB2bGstnRepository.saveAll(b2bList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return b2bList.size();
	}

	private int processCDNRWorkSheetData(Object[][] objList, int columnCount,
			Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr1EInvoicesCdnrHeaderEntity> cdnrList = gstr1ProcessUtil
				.convertCdnrWorkSheetDataToList(objList, columnCount, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}]",
					cdnrList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(cdnrList)) {
			cdnrList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getNoteDate()
						+ "_" + data.getNoteNum() + "_" + data.getNoteType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}
				if (data.getGstin() != null && data.getNoteDate() != null
						&& data.getNoteNum() != null
						&& data.getNoteType() != null) {
					List<GetGstr1EInvoicesCdnrHeaderEntity> entityList = gstr1GetEInvoiceCdnGstnRepository
							.findByInvoiceKey(data.getGstin(),
									data.getNoteDate(), data.getNoteNum(),
									data.getNoteType());
					if (entityList != null
							&& CollectionUtils.isNotEmpty(entityList)) {
						totalIds.addAll(entityList.stream()
								.map(GetGstr1EInvoicesCdnrHeaderEntity::getId)
								.collect(Collectors.toList()));
					}
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			partions.forEach(partion -> gstr1GetEInvoiceCdnGstnRepository
					.updateSameRecords(partion));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processCDNRWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnrList.size());
		}
		stopWatch.start("Insert/update Cdnr data");
		gstr1GetEInvoiceCdnGstnRepository.saveAll(cdnrList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return cdnrList.size();
	}

	private int processCDNURWorkSheetData(Object[][] objList, int columnCount,
			Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNURWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr1EInvoicesCdnurHeaderEntity> cdnurList = gstr1ProcessUtil
				.convertCdnurWorkSheetDataToList(objList, columnCount, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNURWorkSheetData list size [{}]",
					cdnurList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(cdnurList)) {
			cdnurList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getNoteDate()
						+ "_" + data.getNoteNum() + "_" + data.getNoteType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNURWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}
				if (data.getGstin() != null && data.getNoteDate() != null
						&& data.getNoteNum() != null
						&& data.getNoteType() != null) {
					List<GetGstr1EInvoicesCdnurHeaderEntity> entityList = gstr1GetEInvoiceCdnurGstnRepository
							.findByInvoiceKey(data.getGstin(),
									data.getNoteDate(), data.getNoteNum(),
									data.getNoteType());
					if (entityList != null
							&& CollectionUtils.isNotEmpty(entityList)) {
						totalIds.addAll(entityList.stream()
								.map(GetGstr1EInvoicesCdnurHeaderEntity::getId)
								.collect(Collectors.toList()));
					}
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			partions.forEach(partion -> gstr1GetEInvoiceCdnurGstnRepository
					.updateSameRecords(partion));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processCDNURWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnurList.size());
		}
		stopWatch.start("Insert/update Cdnr data");
		gstr1GetEInvoiceCdnurGstnRepository.saveAll(cdnurList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return cdnurList.size();
	}

	private int processEXPWorkSheetData(Object[][] objList, int columnCount,
			Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEXPWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr1EInvoicesExpHeaderEntity> expList = gstr1ProcessUtil
				.convertExpWorkSheetDataToList(objList, columnCount, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEXPWorkSheetData list size [{}]",
					expList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(expList)) {
			expList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getInvDate()
						+ "_" + data.getInvNum() + "_" + data.getExportType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processEXPWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}
				if (data.getGstin() != null && data.getInvDate() != null
						&& data.getInvNum() != null
						&& data.getExportType() != null) {
					List<GetGstr1EInvoicesExpHeaderEntity> entityList = gstr1GetEInvoiceExpGstnRepository
							.findByInvoiceKey(data.getGstin(),
									data.getInvDate(), data.getInvNum(),
									data.getExportType());

					if (entityList != null
							&& CollectionUtils.isNotEmpty(entityList)) {
						totalIds.addAll(entityList.stream()
								.map(GetGstr1EInvoicesExpHeaderEntity::getId)
								.collect(Collectors.toList()));
					}
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			partions.forEach(partion -> gstr1GetEInvoiceExpGstnRepository
					.updateSameRecords(partion));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processEXPWorkSheetData ->Records ready for insert/update list size [{}]",
					expList.size());
		}
		stopWatch.start("Insert/update Cdnr data");
		gstr1GetEInvoiceExpGstnRepository.saveAll(expList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return expList.size();
	}

}
