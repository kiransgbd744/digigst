package com.ey.advisory.app.services.docs.gstr2a;

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
import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr2aFileProcessService")
public class Gstr2aFileProcessService {

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDNR = "CDNR";
	private static final String CDNRA = "CDNRA";
	private static final String ISD = "ISD";
	private static final String ISDA = "ISDA";
	private static final String TDS = "TDS";
	private static final String TDSA = "TDSA";
	private static final String TCS = "TCS";
	private static final String IMPG = "IMPG";
	private static final String IMPGSEZ = "IMPG SEZ";
	private static final String ECOM = "ECOM";
	private static final String ECOMA = "ECOMA";

	private static final List<String> supportedFunctionality = Lists.newArrayList(B2B, B2BA, CDNR, CDNRA, ISD, ISDA,
			IMPG, IMPGSEZ, ECOM, ECOMA);
	
	@Autowired
	private Get2aRepositoryHandler repoHandler;
	
	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;
	
	@Autowired
	private GSTNDetailRepository gstnRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	private ErpScenarioPermissionRepository erpScenPermissionRepo;
	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	public int processGstr2File(InputStream inputStream, String fileName, Long batchId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2aFileProcessService -> processGstr2File with fileName: [{}]", fileName);
		}
		int count = 0;
		// fileName = fileName.substring(0, fileName.length() - 3);
		String fileNameArray[] = fileName.split("_");
		String cgstin = fileNameArray[2];
		String taxPeriod = fileNameArray[3];
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("From the processed file cgstin: [{}] and taxPeriod: [{}]", cgstin, taxPeriod);
		}
		Map<String, List<Integer>> sectionsMap = Maps.newHashMap();
		sectionsMap.put(B2B, Lists.newArrayList(6, 24));
		sectionsMap.put(B2BA, Lists.newArrayList(7, 23));
		sectionsMap.put(CDNR, Lists.newArrayList(6, 25));
		sectionsMap.put(CDNRA, Lists.newArrayList(7, 25));
		sectionsMap.put(ISD, Lists.newArrayList(6, 17));
		sectionsMap.put(ISDA, Lists.newArrayList(7, 20));
		sectionsMap.put(TDS, Lists.newArrayList(6, 7));
		sectionsMap.put(TDSA, Lists.newArrayList(6, 8));
		sectionsMap.put(TCS, Lists.newArrayList(6, 9));
		sectionsMap.put(IMPG, Lists.newArrayList(6, 8));
		sectionsMap.put(IMPGSEZ, Lists.newArrayList(6, 10));
		sectionsMap.put(ECOM, Lists.newArrayList(6, 19));
		sectionsMap.put(ECOMA, Lists.newArrayList(7, 21));
		
		LoadOptions options = new LoadOptions(FileFormatType.XLSX);
		CommonUtility.setAsposeLicense();
		Workbook workbook = new Workbook(inputStream, options);
		workbook.getSettings().setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Choosen worksheets to be processed: [{}]", supportedFunctionality);
		}

		WorksheetCollection collection = workbook.getWorksheets();

		for (int i = 0; i <= 13; i++) {
			Worksheet worksheet = collection.get(i);
			String sheetName = worksheet.getName();
			if (supportedFunctionality.contains(sheetName)) {
				long startTime = System.currentTimeMillis();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Processing the work sheet::[{}]", sheetName);
				}
				List<Integer> intList = sectionsMap.get(sheetName);
				count = count
						+ processData(worksheet, intList.get(0), intList.get(1), sheetName, cgstin, taxPeriod, batchId);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total time taken:: [{}] ms to process the sheet:: [{}]",
							(System.currentTimeMillis() - startTime), sheetName);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total records processed [{}]", count);
				}
			}
		}
		
		batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
				false);
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

		public ProcessWorkSheetTask(String sheetName, Worksheet worksheet, Map<String, List<Integer>> sectionsMap,
				String cgstin, String taxPeriod) {
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
			int count = processData(worksheet, intList.get(0), intList.get(1), sheetName, cgstin, taxPeriod, null);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Total time taken:: [{}] ms to process the sheet:: [{}]",
						(System.currentTimeMillis() - startTime), sheetName);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Total records processed [{}]", count);
			}
			return count;
		}
	}

	private int processData(Worksheet worksheet, int startRow, int columnCount, String sheetName, String cgstin,
			String taxPeriod, Long batchId) throws Exception {
		EntityConfigPrmtEntity entityConfig = null;
		Cells cells = worksheet.getCells();
		Object[][] objList = cells.exportArray(startRow, 0,
				cells.getMaxDataRow(), columnCount);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Object list [{}] for the work sheet::[{}]",
					objList.length, sheetName);
		}
		Pair<Long, Integer> pair = null;
		int count = 0;
		// Long batchId = null;
		boolean isFDeltaGetData = false;
		
		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(cgstin);

		if (gstinInfo != null) {
			entityConfig = entityConfigRepo
					.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
							TenantContext.getTenantId(),
							gstinInfo.getEntityId(), "I25");
		}
		String paramValue = entityConfig!= null ? entityConfig.getParamValue() : "A";
		if ("B".equals(paramValue)) {
			isFDeltaGetData = true;
		}
		if (!isFDeltaGetData) {
			switch (sheetName) {
			case B2B: {
				pair = repoHandler.processUserB2BWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case B2BA: {
				pair = repoHandler.processUserB2BAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case CDNR: {
				pair = repoHandler.processUserCDNRWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case CDNRA: {
				pair = repoHandler.processUserCDNRAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case ISD: {
				pair = repoHandler.processUserISDWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case ISDA: {
				pair = repoHandler.processUserISDAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case TDS: {
				pair = repoHandler.processUserTDSWorkSheetData(objList,
						columnCount);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case TDSA: {
				pair = repoHandler.processUserTDSAWorkSheetData(objList,
						columnCount);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case TCS: {
				pair = repoHandler.processUserTCSWorkSheetData(objList,
						columnCount);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case IMPG: {
				pair = repoHandler.processUserIMPGWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case IMPGSEZ: {
				pair = repoHandler.processUserIMPGSEZWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case ECOM: {
				pair = repoHandler.processUserEcomWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			case ECOMA: {
				pair = repoHandler.processUserEcomaWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
				}
				break;
			}
			default:
				System.out
						.println("Not supported/enabled to process the sheet::"
								+ sheetName);
			}
		} else {
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
						docRepository.getGstr2aProcCall(cgstin, taxPeriod,
								"B2B", batchId, true);
						createErpJob(cgstin, taxPeriod, B2B, batchId);
					}
				}
				break;
			}
			case B2BA: {
				pair = repoHandler.processB2BAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();

					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, B2BA,
								batchId, true);
						createErpJob(cgstin, taxPeriod, B2BA, batchId);
					}
				}
				break;
			}
			case CDNR: {
				pair = repoHandler.processCDNRWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod,
								"CDN", batchId, true);
						createErpJob(cgstin, taxPeriod, "CDN", batchId);
					}
				}
				break;
			}
			case CDNRA: {
				pair = repoHandler.processCDNRAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod,
								"CDNA", batchId, true);
						createErpJob(cgstin, taxPeriod, "CDNA", batchId);
					}
				}
				break;
			}
			case ISD: {
				pair = repoHandler.processISDWorkSheetData(objList, columnCount,
						cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, ISD,
								batchId, true);
						createErpJob(cgstin, taxPeriod, ISD, batchId);
					}
				}
				break;
			}
			case ISDA: {
				pair = repoHandler.processISDAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, ISDA,
								batchId, true);
						createErpJob(cgstin, taxPeriod, ISDA, batchId);
					}
				}
				break;
			}
			case TDS: {
				pair = repoHandler.processTDSWorkSheetData(objList,
						columnCount);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, TDS,
								batchId, true);
						createErpJob(cgstin, taxPeriod, TDS, batchId);
					}
				}
				break;
			}
			case TDSA: {
				pair = repoHandler.processTDSAWorkSheetData(objList,
						columnCount);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, TDSA,
								batchId, true);
						createErpJob(cgstin, taxPeriod, TDSA, batchId);
					}
				}
				break;
			}
			case TCS: {
				pair = repoHandler.processTCSWorkSheetData(objList,
						columnCount);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, TCS,
								batchId, true);
						createErpJob(cgstin, taxPeriod, TCS, batchId);
					}
				}
				break;
			}
			case IMPG: {
				pair = repoHandler.processIMPGWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, IMPG,
								batchId, true);
						createErpJob(cgstin, taxPeriod, IMPG, batchId);
					}
				}
				break;
			}
			case IMPGSEZ: {
				pair = repoHandler.processIMPGSEZWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();
					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod,
								APIConstants.IMPGSEZ, batchId, true);
						createErpJob(cgstin, taxPeriod, APIConstants.IMPGSEZ,
								batchId);
					}
				}
				break;
			}
			case ECOM: {
				pair = repoHandler.processEcomWorkSheetData(objList, columnCount,
						cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();

					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod,
								APIConstants.ECOM, batchId, true);
						createErpJob(cgstin, taxPeriod, APIConstants.ECOM, batchId);
					}
				}
				break;
			}
			case ECOMA: {
				pair = repoHandler.processEcomAWorkSheetData(objList,
						columnCount, cgstin, taxPeriod, batchId);
				batchId = pair.getValue0();
				if (batchId != 0L) {
					count = pair.getValue1();

					if (count > 0) {
						// Proc call to insert the Delta data into original
						// tables
						docRepository.getGstr2aProcCall(cgstin, taxPeriod, APIConstants.ECOMA,
								batchId, true);
						createErpJob(cgstin, taxPeriod, APIConstants.ECOMA, batchId);
					}
				}
				break;
			}
			default:
				System.out
						.println("Not supported/enabled to process the sheet::"
								+ sheetName);
			}
		
		}
		return count;
	}

	

	private void createErpJob(String gstin, String taxPeriod, String section,
			Long batchId) {
		Long scenarioId = scenarioMasterRepo
				.findSceIdOnScenarioName(JobConstants.NEW_GSTR2A_GET_REV_INTG);

		GSTNDetailEntity gstinInfo = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
		Long gstinId = gstinInfo.getId();

		List<ErpScenarioPermissionEntity> scenarioPermisionList = erpScenPermissionRepo
				.findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId,
						gstinId);
		if (!scenarioPermisionList.isEmpty()) {
			// Code to generate trigger/ create async job for ERP reverse
			// integration.
			for (ErpScenarioPermissionEntity scenarioPermision : scenarioPermisionList) {
				Gson gson = GsonUtil.newSAPGsonInstance();
				Get2ARevIntReqDto erpReqDto = new Get2ARevIntReqDto();
				erpReqDto.setGstin(gstin);
				erpReqDto.setRetPeriod(taxPeriod);
				erpReqDto.setSection(section);
				erpReqDto.setBatchId(batchId);
				erpReqDto.setDestinationName(scenarioPermision.getDestName());
				erpReqDto.setErpId(scenarioPermision.getErpId());
				String erpReqJson = gson.toJson(erpReqDto);
				AsyncExecJob job = asyncJobsService.createJob(
						TenantContext.getTenantId(),
						JobConstants.NEW_GSTR2A_GET_REV_INTG, erpReqJson,
						JobConstants.SYSTEM, JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);
			}
		}
	}
}
