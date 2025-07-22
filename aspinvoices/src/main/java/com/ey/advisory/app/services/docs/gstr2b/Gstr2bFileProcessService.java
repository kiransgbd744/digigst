package com.ey.advisory.app.services.docs.gstr2b;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.ey.advisory.app.data.entities.client.GetGstr2bB2bEcomHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bB2bEcomaHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bEcomItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bEcomaItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2bInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2baInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnrInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnrInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnraInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnraInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgsezInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgsezInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdaInvoicesItemEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BB2bHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BB2baHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BCdnrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BCdnraHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgsezHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bB2bEcomHeaderRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bB2bEcomaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingB2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingB2baInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingCdnrInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingCdnraInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingImpgInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingImpgsezInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingIsdInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2b.GetGstr2bStagingIsdaInvoicesRepository;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BB2bHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BB2bItemEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BB2baHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BB2baItemEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BCdnrHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BCdnrItemEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BCdnraHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BCdnraItemEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BImpgHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BImpgItemEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BImpgsezHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BImpgsezItemEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BIsdHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BIsdItemEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BIsdaHeaderEntity;
import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BIsdaItemEntity;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
@Qualifier("Gstr2bFileProcessService")
public class Gstr2bFileProcessService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bFileProcessService.class);

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDNR = "B2B-CDNR";
	private static final String CDNRA = "B2B-CDNRA";
	private static final String ISD = "ISD";
	private static final String ISDA = "ISDA";
	private static final String IMPG = "IMPG";
	private static final String IMPGSEZ = "IMPGSEZ";
	private static final String ECOM = "ECOM";
	private static final String ECOMA = "ECOMA";
	
	private static final List<String> supportedFunctionality = Lists
			.newArrayList(B2B, B2BA, CDNR, CDNRA, ISD, ISDA, IMPG, IMPGSEZ,ECOM,ECOMA);

	@Autowired
	private Gstr2bProcessUtil gstr2bProcessUtil;

	@Autowired
	@Qualifier("GetGstr2bStagingB2bInvoicesRepository")
	private GetGstr2bStagingB2bInvoicesRepository getGstr2bStagingB2bInvoicesRepository;

	@Autowired
	private GetGstr2bStagingB2baInvoicesRepository getGstr2B2baInvoicesRepository;

	@Autowired
	private GetGstr2bStagingCdnrInvoicesRepository getGstr2CdnInvoicesRepository;

	@Autowired
	private GetGstr2bStagingImpgInvoicesRepository getGstr2aImpgRepository;

	@Autowired
	@Qualifier("GetGstr2bStagingCdnraInvoicesRepository")
	private GetGstr2bStagingCdnraInvoicesRepository getGstr2bCdnraRepository;

	@Autowired
	private GetGstr2bStagingImpgsezInvoicesRepository getGstr2aImpgsezRepository;

	@Autowired
	@Qualifier("GetGstr2bStagingIsdInvoicesRepository")
	private GetGstr2bStagingIsdInvoicesRepository getGstr2bStagingIsdInvoicesRepository;

	@Autowired
	@Qualifier("GetGstr2bStagingIsdaInvoicesRepository")
	private GetGstr2bStagingIsdaInvoicesRepository getGstr2bStagingIsdaInvoicesRepository;

	@Autowired
	@Qualifier("Gstr2GetGstr2BB2bHeaderRepository")
	private Gstr2GetGstr2BB2bHeaderRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BB2baHeaderRepository")
	private Gstr2GetGstr2BB2baHeaderRepository b2bAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BCdnrHeaderRepository")
	private Gstr2GetGstr2BCdnrHeaderRepository cdnrHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgHeaderRepository")
	private Gstr2GetGstr2BImpgHeaderRepository impgHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BCdnraHeaderRepository")
	private Gstr2GetGstr2BCdnraHeaderRepository cdnraHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgsezHeaderRepository")
	private Gstr2GetGstr2BImpgsezHeaderRepository impgSezHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdHeaderRepository")
	private Gstr2GetGstr2BIsdHeaderRepository isdHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdaHeaderRepository")
	private Gstr2GetGstr2BIsdaHeaderRepository isdaHeaderRepo;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Autowired
	@Qualifier("GetGstr2bB2bEcomHeaderRepository")
	private GetGstr2bB2bEcomHeaderRepository getGstr2bEcomHeaderRepository;
	
	@Autowired
	@Qualifier("GetGstr2bB2bEcomaHeaderRepository")
	private GetGstr2bB2bEcomaHeaderRepository getGstr2bB2bEcomaHeaderRepository;



	public int processGstr2File(InputStream inputStream, String fileName,
			Long fileId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2bFileProcessService -> processGstr2bFile with fileName: [{}]",
					fileName);
		}
		int count = 0;
		String fileNameArray[] = fileName.split("_");
/*//for testing
		String taxPeriod = fileNameArray[0];
		String rgstin = fileNameArray[1];
		String genDate = fileNameArray[3];
	//////////	
*/		String taxPeriod = fileNameArray[2];
		String rgstin = fileNameArray[3];
		String genDate = fileNameArray[5];
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"From the processed file cgstin: [{}] and taxPeriod: [{}]",
					rgstin, taxPeriod);
		}
		Map<String, List<Integer>> sectionsMap = Maps.newHashMap();
		sectionsMap.put(B2B, Lists.newArrayList(6, 22));
		sectionsMap.put(B2BA, Lists.newArrayList(7, 21));
		sectionsMap.put(CDNR, Lists.newArrayList(6, 23));
		sectionsMap.put(CDNRA, Lists.newArrayList(7, 23));
		sectionsMap.put(ISD, Lists.newArrayList(6, 14));
		sectionsMap.put(ISDA, Lists.newArrayList(7, 17));
		sectionsMap.put(IMPG, Lists.newArrayList(6, 8));
		sectionsMap.put(IMPGSEZ, Lists.newArrayList(6, 10));
		sectionsMap.put(ECOM, Lists.newArrayList(6, 21));
		sectionsMap.put(ECOMA, Lists.newArrayList(6, 21));

		LoadOptions options = new LoadOptions(FileFormatType.XLSX);
		Workbook workbook = new Workbook(inputStream, options);
		workbook.getSettings()
				.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Choosen worksheets to be processed: [{}]",
					supportedFunctionality);
		}

		WorksheetCollection collection = workbook.getWorksheets();
		int sheetCount = collection.getCount();

		for (int i = 0; i < sheetCount; i++) {
			Worksheet worksheet = collection.get(i);
			String sheetName = worksheet.getName();
			if (supportedFunctionality.contains(sheetName)) {
				long startTime = System.currentTimeMillis();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Processing the work sheet::[{}]", sheetName);
				}
				List<Integer> intList = sectionsMap.get(sheetName);
				count = count
						+ processData(worksheet, intList.get(0), intList.get(1),
								sheetName, rgstin, taxPeriod, fileId, genDate);
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
		private String rgstin;
		private String taxPeriod;
		private String genDate;

		public ProcessWorkSheetTask(String sheetName, Worksheet worksheet,
				Map<String, List<Integer>> sectionsMap, String rgstin,
				String taxPeriod) {
			this.sheetName = sheetName;
			this.worksheet = worksheet;
			this.sectionsMap = sectionsMap;
			this.rgstin = rgstin;
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
					sheetName, rgstin, taxPeriod, null, genDate);
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
			String sheetName, String cgstin, String taxPeriod, Long fileId,
			String genDate) {
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
			pair = processB2BWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case B2BA: {
			pair = processB2BAWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case CDNR: {
			pair = processCDNRWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case CDNRA: {
			pair = processCDNRAWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case IMPG: {
			pair = processIMPGWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case IMPGSEZ: {
			pair = processIMPGSEZWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case ISD: {
			pair = processISDWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case ISDA: {
			pair = processISDAWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case ECOM: {
			pair = processECOMWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		case ECOMA: {
			pair = processEcomaWorkSheetData(objList, columnCount, cgstin,
					taxPeriod, fileId, genDate);
			fileId = pair.getValue0();
			break;
		}
		default:
			System.out.println(
					"Not supported/enabled to process the sheet::" + sheetName);
		}
		return count;
	}

	@Transactional
	private Pair<Long, Integer> processB2BWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingB2bInvoicesHeaderEntity> b2bList = gstr2bProcessUtil
				.convertB2bWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}]",
					b2bList.size());
		}

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(b2bList)) {
			b2bList.forEach(data -> {
				String invoiceKey = data.getSGstin() + "_" + data.getRGstin()
						+ "_" + data.getInvoiceDate() + "_"
						+ data.getInvoiceNumber() + "_" + data.getInvoiceType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bStagingB2bInvoicesHeaderEntity> entityList = getGstr2bStagingB2bInvoicesRepository
						.findByInvoiceKey(data.getSGstin(), data.getRGstin(),
								data.getInvoiceDate(), data.getInvoiceNumber(),
								data.getInvoiceType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bStagingB2bInvoicesHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}

		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2bStagingB2bInvoicesRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BWorkSheetData ->Records ready for insert/update list size [{}]",
					b2bList.size());
		}
		List<GetGstr2bStagingB2bInvoicesHeaderEntity> b2bFilterList = Gstr2bTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonB2bHeaders(b2bList);
		getGstr2bStagingB2bInvoicesRepository.saveAll(b2bFilterList);

		// Saving to Main Tables
		List<Gstr2GetGstr2BB2bHeaderEntity> mainB2bList = b2bFilterList.stream()
				.map(o -> convertTob2bDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		// Soft Deleting existing Records in Main Table
		List<Long> totalIdsMainTable = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(mainB2bList)) {
			mainB2bList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BB2bHeaderEntity> entityListMain = b2bHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityListMain)) {
					totalIdsMainTable.addAll(entityListMain.stream()
							.map(Gstr2GetGstr2BB2bHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobNameMainTable = "Disabling the existing records:::->"
				+ totalIdsMainTable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMainTable);
		}

		if (CollectionUtils.isNotEmpty(totalIdsMainTable)) {
			List<List<Long>> partions = Lists.partition(totalIdsMainTable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(
					partion -> b2bHeaderRepo.updateSameRecords(partion, now));
		}

		b2bHeaderRepo.saveAll(mainB2bList);

		return new Pair<>(1L, b2bList.size());

	}

	private Gstr2GetGstr2BB2bHeaderEntity convertTob2bDto(
			GetGstr2bStagingB2bInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingB2bInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BB2bHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BB2bHeaderEntity obj = new Gstr2GetGstr2BB2bHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setInvoiceNumber(dto.getInvoiceNumber());
		obj.setInvoiceDate(dto.getInvoiceDate());
		obj.setInvoiceType(dto.getInvoiceType());
		obj.setSupInvoiceValue(dto.getSupInvoiceValue());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setPos(dto.getPos());
		obj.setRev(dto.getRev());
		obj.setItcAvailable(dto.getItcAvailable());
		obj.setRsn(dto.getRsn());
		obj.setDiffPercent(dto.getDiffPercent());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(false);
		obj.setDocKey(dto.getDocKey());
		obj.setIrnNo(dto.getIrnNo());
		obj.setIrnSrcType(dto.getIrnSrcType());
		obj.setIrnGenDate(dto.getIrnGenDate());
		// line item
		List<Gstr2GetGstr2BB2bItemEntity> itemList = new ArrayList<>();

		List<GetGstr2bStagingB2bInvoicesItemEntity> lineItems = dto
				.getLineItems();

		for (GetGstr2bStagingB2bInvoicesItemEntity item : lineItems) {
			Gstr2GetGstr2BB2bItemEntity itemEntity = new Gstr2GetGstr2BB2bItemEntity();
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setCgstAmt(item.getCgstAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setItemNumber(item.getItemNumber());
			itemEntity.setSgstAmt(item.getSgstAmt());
			itemEntity.setTaxableValue(item.getTaxableValue());
			itemEntity.setTaxRate(item.getTaxRate());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItems(itemList);
		}
		return obj;
	}

	@Transactional
	private Pair<Long, Integer> processB2BAWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingB2baInvoicesHeaderEntity> b2baList = gstr2bProcessUtil
				.convertB2baWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(b2baList)) {
			b2baList.forEach(data -> {
				String invoiceKey = data.getSGstin() + "_" + data.getRGstin()
						+ "_" + data.getInvoiceDate() + "_"
						+ data.getInvoiceNumber() + "_" + data.getInvoiceType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bStagingB2baInvoicesHeaderEntity> entityList = getGstr2B2baInvoicesRepository
						.findByInvoiceKey(data.getSGstin(), data.getRGstin(),
								data.getInvoiceDate(), data.getInvoiceNumber(),
								data.getInvoiceType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bStagingB2baInvoicesHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}

		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2B2baInvoicesRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BAWorkSheetData ->Records ready for insert/update list size [{}]",
					b2baList.size());
		}
		List<GetGstr2bStagingB2baInvoicesHeaderEntity> b2baFilterList = Gstr2bTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonB2baHeaders(b2baList);
		getGstr2B2baInvoicesRepository.saveAll(b2baFilterList);

		List<Gstr2GetGstr2BB2baHeaderEntity> mainB2baList = b2baFilterList
				.stream().map(o -> convertTob2baDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<Long> totalIdsMainTable = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(mainB2baList)) {
			mainB2baList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BB2baHeaderEntity> entityList = b2bAHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIdsMainTable.addAll(entityList.stream()
							.map(Gstr2GetGstr2BB2baHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobNameMainTable = "Disabling the existing records:::->"
				+ totalIdsMainTable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMainTable);
		}

		if (CollectionUtils.isNotEmpty(totalIdsMainTable)) {
			List<List<Long>> partionsMain = Lists.partition(totalIdsMainTable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partionsMain.forEach(
					partion -> b2bAHeaderRepo.updateSameRecords(partion, now));
		}
		b2bAHeaderRepo.saveAll(mainB2baList);

		return new Pair<>(1L, b2baList.size());
	}

	private Gstr2GetGstr2BB2baHeaderEntity convertTob2baDto(
			GetGstr2bStagingB2baInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingB2baInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BB2baHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BB2baHeaderEntity obj = new Gstr2GetGstr2BB2baHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setInvoiceNumber(dto.getInvoiceNumber());
		obj.setInvoiceDate(dto.getInvoiceDate());
		obj.setInvoiceType(dto.getInvoiceType());
		obj.setOrgInvoiceNumber(dto.getOrgInvoiceNumber());
		obj.setOrgInvoiceDate(dto.getOrgInvoiceDate());
		obj.setSupInvoiceValue(dto.getSupInvoiceValue());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setPos(dto.getPos());
		obj.setRev(dto.getRev());
		obj.setItcAvailable(dto.getItcAvailable());
		obj.setRsn(dto.getRsn());
		obj.setDiffPercent(dto.getDiffPercent());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(dto.getIsDelete());
		obj.setDocKey(dto.getDocKey());

		List<Gstr2GetGstr2BB2baItemEntity> itemList = new ArrayList<>();
		List<GetGstr2bStagingB2baInvoicesItemEntity> lineItems = dto
				.getLineItemss();
		for (GetGstr2bStagingB2baInvoicesItemEntity item : lineItems) {
			Gstr2GetGstr2BB2baItemEntity itemEntity = new Gstr2GetGstr2BB2baItemEntity();
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setCgstAmt(item.getCgstAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setItemNumber(item.getItemNumber());
			itemEntity.setSgstAmt(item.getSgstAmt());
			itemEntity.setTaxableValue(item.getTaxableValue());
			itemEntity.setTaxRate(item.getTaxRate());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItems(itemList);
		}

		return obj;
	}

	@Transactional
	private Pair<Long, Integer> processCDNRWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingCdnrInvoicesHeaderEntity> cdnList = gstr2bProcessUtil
				.convertCdnrWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(cdnList)) {
			cdnList.forEach(data -> {
				String invoiceKey = data.getSGstin() + "_" + data.getRGstin()
						+ "_" + data.getNoteDate() + "_" + data.getNoteNumber()
						+ "_" + data.getNoteType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bStagingCdnrInvoicesHeaderEntity> entityList = getGstr2CdnInvoicesRepository
						.findByInvoiceKey(data.getSGstin(), data.getRGstin(),
								data.getNoteDate(), data.getNoteNumber(),
								data.getNoteType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bStagingCdnrInvoicesHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}

		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2CdnInvoicesRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processCDNRWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnList.size());
		}
		List<GetGstr2bStagingCdnrInvoicesHeaderEntity> cdnrFilterList = Gstr2bTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonCdnrHeaders(cdnList);
		getGstr2CdnInvoicesRepository.saveAll(cdnrFilterList);

		List<Gstr2GetGstr2BCdnrHeaderEntity> mainCdnrList = cdnrFilterList
				.stream().map(o -> convertToCdnrDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<Long> totalIdsMainTable = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(mainCdnrList)) {
			mainCdnrList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BCdnrHeaderEntity> entityList = cdnrHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIdsMainTable.addAll(entityList.stream()
							.map(Gstr2GetGstr2BCdnrHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobNameMain = "Disabling the existing records:::->"
				+ totalIdsMainTable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMain);
		}

		if (CollectionUtils.isNotEmpty(totalIdsMainTable)) {
			List<List<Long>> partions = Lists.partition(totalIdsMainTable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(
					partion -> cdnrHeaderRepo.updateSameRecords(partion, now));
		}

		cdnrHeaderRepo.saveAll(mainCdnrList);

		return new Pair<>(1L, cdnList.size());
	}

	private Gstr2GetGstr2BCdnrHeaderEntity convertToCdnrDto(
			GetGstr2bStagingCdnrInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingCdnrInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BCdnrHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BCdnrHeaderEntity obj = new Gstr2GetGstr2BCdnrHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setNoteNumber(dto.getNoteNumber());
		obj.setNoteDate(dto.getNoteDate());
		obj.setNoteType(dto.getNoteType());
		obj.setInvoiceType(dto.getInvoiceType());
		obj.setSupInvoiceValue(dto.getSupInvoiceValue());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setPos(dto.getPos());
		obj.setRev(dto.getRev());
		obj.setItcAvailable(dto.getItcAvailable());
		obj.setRsn(dto.getRsn());
		obj.setDiffPercent(dto.getDiffPercent());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(false);
		obj.setDocKey(dto.getDocKey());
		obj.setIrnNo(dto.getIrnNo());
		obj.setIrnSrcType(dto.getIrnSrcType());
		obj.setIrnGenDate(dto.getIrnGenDate());

		List<Gstr2GetGstr2BCdnrItemEntity> itemList = new ArrayList<>();
		List<GetGstr2bStagingCdnrInvoicesItemEntity> lineItems = dto
				.getLineItemss();
		for (GetGstr2bStagingCdnrInvoicesItemEntity item : lineItems) {
			Gstr2GetGstr2BCdnrItemEntity itemEntity = new Gstr2GetGstr2BCdnrItemEntity();
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setCgstAmt(item.getCgstAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setItemNumber(item.getItemNumber());
			itemEntity.setSgstAmt(item.getSgstAmt());
			itemEntity.setTaxableValue(item.getTaxableValue());
			itemEntity.setTaxRate(item.getTaxRate());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItems(itemList);

		}

		return obj;
	}

	@Transactional
	private Pair<Long, Integer> processCDNRAWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingCdnraInvoicesHeaderEntity> cdnaList = gstr2bProcessUtil
				.convertCdnraWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(cdnaList)) {
			cdnaList.forEach(data -> {
				String invoiceKey = data.getSGstin() + "_" + data.getRGstin()
						+ "_" + data.getNoteDate() + "_" + data.getNoteNumber()
						+ "_" + data.getNoteType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bStagingCdnraInvoicesHeaderEntity> entityList = getGstr2bCdnraRepository
						.findByInvoiceKey(data.getSGstin(), data.getRGstin(),
								data.getNoteDate(), data.getNoteNumber(),
								data.getNoteType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bStagingCdnraInvoicesHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}

		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2bCdnraRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processCDNRAWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnaList.size());
		}
		List<GetGstr2bStagingCdnraInvoicesHeaderEntity> cdnraFilterList = Gstr2bTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonCdrnaHeaders(cdnaList);

		getGstr2bCdnraRepository.saveAll(cdnraFilterList);

		List<Gstr2GetGstr2BCdnraHeaderEntity> mainCdnraList = cdnraFilterList
				.stream().map(o -> convertToCdnraDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<Long> totalIdsMaintable = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(mainCdnraList)) {
			mainCdnraList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BCdnraHeaderEntity> entityList = cdnraHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIdsMaintable.addAll(entityList.stream()
							.map(Gstr2GetGstr2BCdnraHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobNameMain = "Disabling the existing records:::->"
				+ totalIdsMaintable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMain);
		}

		if (CollectionUtils.isNotEmpty(totalIdsMaintable)) {
			List<List<Long>> partions = Lists.partition(totalIdsMaintable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(
					partion -> cdnraHeaderRepo.updateSameRecords(partion, now));
		}

		cdnraHeaderRepo.saveAll(mainCdnraList);

		return new Pair<>(1L, cdnaList.size());
	}

	private Gstr2GetGstr2BCdnraHeaderEntity convertToCdnraDto(
			GetGstr2bStagingCdnraInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingCdnraInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BCdnraHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BCdnraHeaderEntity obj = new Gstr2GetGstr2BCdnraHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setNoteNumber(dto.getNoteNumber());
		obj.setNoteDate(dto.getNoteDate());
		obj.setNoteType(dto.getNoteType());
		obj.setInvoiceType(dto.getInvoiceType());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setPos(dto.getPos());
		obj.setRev(dto.getRev());
		obj.setItcAvailable(dto.getItcAvailable());
		obj.setRsn(dto.getRsn());
		obj.setDiffPercent(dto.getDiffPercent());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(dto.getIsDelete());
		obj.setDocKey(dto.getDocKey());
		obj.setOrgNoteDate(dto.getOrgNoteDate());
		obj.setOrgNoteNumber(dto.getOrgNoteNumber());
		obj.setOrgNoteType(dto.getOrgNoteType());

		List<Gstr2GetGstr2BCdnraItemEntity> itemList = new ArrayList<>();
		List<GetGstr2bStagingCdnraInvoicesItemEntity> lineItems = dto
				.getLineItemss();
		for (GetGstr2bStagingCdnraInvoicesItemEntity item : lineItems) {
			Gstr2GetGstr2BCdnraItemEntity itemEntity = new Gstr2GetGstr2BCdnraItemEntity();
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setCgstAmt(item.getCgstAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setItemNumber(item.getItemNumber());
			itemEntity.setSgstAmt(item.getSgstAmt());
			itemEntity.setTaxableValue(item.getTaxableValue());
			itemEntity.setTaxRate(item.getTaxRate());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItems(itemList);

		}

		return obj;
	}

	@Transactional
	private Pair<Long, Integer> processIMPGWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingImpgInvoicesHeaderEntity> impgList = gstr2bProcessUtil
				.convertImpgWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);

		LocalDateTime modifiedOn = LocalDateTime.now();
		getGstr2aImpgRepository.softlyDeleteImpgHeader(rgstin, taxPeriod,
				modifiedOn);

		getGstr2aImpgRepository.saveAll(impgList);

		List<Gstr2GetGstr2BImpgHeaderEntity> mainCdnrList = impgList.stream()
				.map(o -> convertToImpgDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		impgHeaderRepo.softlyDeleteImpgHeader(rgstin, taxPeriod, modifiedOn);

		impgHeaderRepo.saveAll(mainCdnrList);

		return new Pair<>(1L, impgList.size());

	}

	private Gstr2GetGstr2BImpgHeaderEntity convertToImpgDto(
			GetGstr2bStagingImpgInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingImpgInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BImpgHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BImpgHeaderEntity obj = new Gstr2GetGstr2BImpgHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());// change need to be done
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(false);

		List<Gstr2GetGstr2BImpgItemEntity> itemList = new ArrayList<>();
		List<GetGstr2bStagingImpgInvoicesItemEntity> lineItems = dto
				.getLineItemss();
		for (GetGstr2bStagingImpgInvoicesItemEntity item : lineItems) {
			Gstr2GetGstr2BImpgItemEntity itemEntity = new Gstr2GetGstr2BImpgItemEntity();
			itemEntity.setBoeDate(item.getBoeDate());
			itemEntity.setBoeNumber(item.getBoeNumber());
			itemEntity.setIsAmd(item.getIsAmd());
			itemEntity.setPortCode(item.getPortCode());
			itemEntity.setRecDateGstin(item.getRecDateGstin());
			itemEntity.setRefDateIcegate(item.getRefDateIcegate());
			itemEntity.setTaxValue(item.getTaxValue());
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItems(itemList);

		}
		return obj;
	}

	@Transactional
	private Pair<Long, Integer> processIMPGSEZWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGSEZWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingImpgsezInvoicesHeaderEntity> impgsezList = gstr2bProcessUtil
				.convertImpgSezWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);

		LocalDateTime modifiedOn = LocalDateTime.now();
		getGstr2aImpgsezRepository.softlyDeleteImpgsezHeader(rgstin, taxPeriod,
				modifiedOn);

		getGstr2aImpgsezRepository.saveAll(impgsezList);

		List<Gstr2GetGstr2BImpgsezHeaderEntity> mainCdnrList = impgsezList
				.stream().map(o -> convertToImpgsezDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		impgSezHeaderRepo.softlyDeleteImpgsezHeader(rgstin, taxPeriod,
				modifiedOn);

		impgSezHeaderRepo.saveAll(mainCdnrList);

		return new Pair<>(1L, impgsezList.size());

	}

	private Gstr2GetGstr2BImpgsezHeaderEntity convertToImpgsezDto(
			GetGstr2bStagingImpgsezInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingImpgsezInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BImpgsezHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BImpgsezHeaderEntity obj = new Gstr2GetGstr2BImpgsezHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(dto.getIsDelete());

		List<Gstr2GetGstr2BImpgsezItemEntity> itemList = new ArrayList<>();
		List<GetGstr2bStagingImpgsezInvoicesItemEntity> lineItems = dto
				.getLineItemss();
		for (GetGstr2bStagingImpgsezInvoicesItemEntity item : lineItems) {
			Gstr2GetGstr2BImpgsezItemEntity itemEntity = new Gstr2GetGstr2BImpgsezItemEntity();
			itemEntity.setBoeDate(item.getBoeDate());
			itemEntity.setBoeNumber(item.getBoeNumber());
			itemEntity.setIsAmd(item.getIsAmd());
			itemEntity.setPortCode(item.getPortCode());
			itemEntity.setRecDateGstin(item.getRecDateGstin());
			itemEntity.setRefDateIcegate(item.getRefDateIcegate());
			itemEntity.setTaxValue(item.getTaxValue());
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItems(itemList);

		}
		return obj;
	}

	@Transactional
	private Pair<Long, Integer> processISDWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingIsdInvoicesHeaderEntity> isdList = gstr2bProcessUtil
				.convertIsdWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}]",
					isdList.size());
		}

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(isdList)) {
			isdList.forEach(data -> {
				String invoiceKey = data.getSGstin() + "_" + data.getDocDate()
						+ "_" + data.getDocNumber() + "_"
						+ data.getIsdDocType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bStagingIsdInvoicesHeaderEntity> entityList = getGstr2bStagingIsdInvoicesRepository
						.findByInvoiceKey(data.getSGstin(), data.getDocDate(),
								data.getDocNumber(), data.getIsdDocType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bStagingIsdInvoicesHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}

		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2bStagingIsdInvoicesRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processISDWorkSheetData ->Records ready for insert/update list size [{}]",
					isdList.size());
		}
		/*
		 * List<GetGstr2bStagingIsdInvoicesHeaderEntity> isdFilterList =
		 * Gstr2bTaxRateDiffUtilStaging
		 * .filterInvoiceKeyAndMergeRateonB2bHeaders(isdList);
		 * getGstr2bStagingIsdInvoicesRepository.saveAll(isdFilterList);
		 */

		// Saving to Main Tables
		List<Gstr2GetGstr2BIsdHeaderEntity> mainIsdList = isdList.stream()
				.map(o -> convertToisdDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		// Soft Deleting existing Records in Main Table

		List<Long> totalIdsMainTable = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(mainIsdList)) {
			mainIsdList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BIsdHeaderEntity> entityListMain = isdHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityListMain)) {
					totalIdsMainTable.addAll(entityListMain.stream()
							.map(Gstr2GetGstr2BIsdHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobNameMainTable = "Disabling the existing records:::->"
				+ totalIdsMainTable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMainTable);
		}

		if (CollectionUtils.isNotEmpty(totalIdsMainTable)) {
			List<List<Long>> partions = Lists.partition(totalIdsMainTable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(
					partion -> isdHeaderRepo.updateSameRecords(partion, now));
		}

		isdHeaderRepo.saveAll(mainIsdList);

		return new Pair<>(1L, isdList.size());

	}

	private Gstr2GetGstr2BIsdHeaderEntity convertToisdDto(
			GetGstr2bStagingIsdInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingIsdInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BIsdHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BIsdHeaderEntity obj = new Gstr2GetGstr2BIsdHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setDocNumber(dto.getDocNumber());
		obj.setDocDate(dto.getDocDate());
		obj.setIsdDocType(dto.getIsdDocType());
		obj.setOrgInvoiceDate(dto.getOrgInvoiceDate());
		obj.setOrgInvoiceNumber(dto.getOrgInvoiceNumber());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(false);
		obj.setDocKey(dto.getDocKey());
		
		
		GetGstr2bStagingIsdInvoicesItemEntity lineItems = dto
				.getLineItemss();

	        Gstr2GetGstr2BIsdItemEntity itemEntity = new Gstr2GetGstr2BIsdItemEntity();
			itemEntity.setCessAmt(lineItems.getCessAmt());
			itemEntity.setCgstAmt(lineItems.getCgstAmt());
			itemEntity.setIgstAmt(lineItems.getIgstAmt());
			itemEntity.setItcEligile(lineItems.getItcEligible());
			itemEntity.setSgstAmt(lineItems.getSgstAmt());
			itemEntity.setHeader(obj);
			obj.setLineItems(itemEntity);
		
		return obj;
	}

	@Transactional
	private Pair<Long, Integer> processISDAWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bStagingIsdaInvoicesHeaderEntity> isdaList = gstr2bProcessUtil
				.convertIsdaWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}]",
					isdaList.size());
		}

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(isdaList)) {
			isdaList.forEach(data -> {
				String invoiceKey = data.getSuppSstin() + "_"
						+ data.getDocDate() + "_" + data.getDocNumber() + "_"
						+ data.getIsdDocType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bStagingIsdaInvoicesHeaderEntity> entityList = getGstr2bStagingIsdaInvoicesRepository
						.findByInvoiceKey(data.getSuppSstin(),
								data.getDocDate(), data.getDocNumber(),
								data.getIsdDocType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bStagingIsdaInvoicesHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}

		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2bStagingIsdaInvoicesRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processISDAWorkSheetData ->Records ready for insert/update list size [{}]",
					isdaList.size());
		}
		/*
		 * List<GetGstr2bStagingIsdInvoicesHeaderEntity> isdFilterList =
		 * Gstr2bTaxRateDiffUtilStaging
		 * .filterInvoiceKeyAndMergeRateonB2bHeaders(isdList);
		 * getGstr2bStagingIsdInvoicesRepository.saveAll(isdFilterList);
		 */

		// Saving to Main Tables
		List<Gstr2GetGstr2BIsdaHeaderEntity> mainIsdaList = isdaList.stream()
				.map(o -> convertToisdaDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		// Soft Deleting existing Records in Main Table

		List<Long> totalIdsMainTable = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(mainIsdaList)) {
			mainIsdaList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BIsdaHeaderEntity> entityListMain = isdaHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityListMain)) {
					totalIdsMainTable.addAll(entityListMain.stream()
							.map(Gstr2GetGstr2BIsdaHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobNameMainTable = "Disabling the existing records:::->"
				+ totalIdsMainTable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMainTable);
		}

		if (CollectionUtils.isNotEmpty(totalIdsMainTable)) {
			List<List<Long>> partions = Lists.partition(totalIdsMainTable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(
					partion -> isdaHeaderRepo.updateSameRecords(partion, now));
		}

		isdaHeaderRepo.saveAll(mainIsdaList);

		return new Pair<>(1L, isdaList.size());

	}

	private Gstr2GetGstr2BIsdaHeaderEntity convertToisdaDto(
			GetGstr2bStagingIsdaInvoicesHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bStagingIsdaInvoicesHeaderEntity "
					+ " Gstr2GetGstr2BIsdaHeaderEntity object";
			LOGGER.debug(str);
		}
		Gstr2GetGstr2BIsdaHeaderEntity obj = new Gstr2GetGstr2BIsdaHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSuppSstin(dto.getSuppSstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setDocNumber(dto.getDocNumber());
		obj.setDocDate(dto.getDocDate());
		obj.setIsdDocType(dto.getIsdDocType());
		obj.setOrgDocDate(dto.getOrgDocDate());
		obj.setOrgdocNumber(dto.getOrgdocNumber());
		obj.setOrgDocType(dto.getOrgDocType());
		obj.setOrgInvoiceDate(dto.getOrgInvoiceDate());
		obj.setOrgInvoiceNumber(dto.getOrgInvoiceNumber());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(false);
		obj.setDocKey(dto.getDocKey());
		// line item
		
			
		GetGstr2bStagingIsdaInvoicesItemEntity lineItems = dto
				.getLineItemss();

		        Gstr2GetGstr2BIsdaItemEntity itemEntity = new Gstr2GetGstr2BIsdaItemEntity();
				itemEntity.setCessAmt(lineItems.getCessAmt());
				itemEntity.setCgstAmt(lineItems.getCgstAmt());
				itemEntity.setIgstAmt(lineItems.getIgstAmt());
				itemEntity.setItcEligile(lineItems.getItcEligible());
				itemEntity.setSgstAmt(lineItems.getSgstAmt());
				itemEntity.setHeader(obj);
				obj.setLineItems(itemEntity);
		
		return obj;
	}
	
	//ecom changes
	@Transactional
	private Pair<Long, Integer> processECOMWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processECOMWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bB2bEcomHeaderEntity> ecomList = gstr2bProcessUtil
				.convertEcomWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomWorkSheetData list size [{}]",
					ecomList.size());
		}

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(ecomList)) {
			ecomList.forEach(data -> {
				String invoiceKey = data.getSGstin() + "_" + data.getRGstin()
						+ "_" + data.getInvoiceDate() + "_"
						+ data.getInvoiceNumber() + "_" + data.getInvoiceType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processEcomWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bB2bEcomHeaderEntity> entityList = getGstr2bEcomHeaderRepository
						.findByInvoiceKey(data.getSGstin(), data.getRGstin(),
								data.getInvoiceDate(), data.getInvoiceNumber(),
								data.getInvoiceType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bB2bEcomHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
//to soft deleting getting all the ids
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2bEcomHeaderRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processEcomWorkSheetData ->Records ready for insert/update list size [{}]",
					ecomList.size());
		}
		List<GetGstr2bB2bEcomHeaderEntity> b2bFilterList = Gstr2bTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonEcomHeader(ecomList);
		getGstr2bEcomHeaderRepository.saveAll(b2bFilterList);

	/*	// Saving to Main Tables
		List<GetGstr2bB2bEcomHeaderEntity> mainEcomList = b2bFilterList.stream()
				.map(o -> convertToEcomDto(o))
				.collect(Collectors.toCollection(ArrayList::new));*/

	/*	// Soft Deleting existing Records in Main Table
		List<Long> totalIdsMainTable = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(mainEcomList)) {
			mainEcomList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BB2bHeaderEntity> entityListMain = b2bHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityListMain)) {
					totalIdsMainTable.addAll(entityListMain.stream()
							.map(Gstr2GetGstr2BB2bHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}*/

		/*String jobNameMainTable = "Disabling the existing records:::->"
				+ totalIdsMainTable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMainTable);
		}

		if (CollectionUtils.isNotEmpty(totalIdsMainTable)) {
			List<List<Long>> partions = Lists.partition(totalIdsMainTable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(
					partion -> b2bHeaderRepo.updateSameRecords(partion, now));
		}

		b2bHeaderRepo.saveAll(mainB2bList);*/

		return new Pair<>(1L, b2bFilterList.size());

	}
	
	
	private GetGstr2bB2bEcomHeaderEntity convertToEcomDto(
			GetGstr2bB2bEcomHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bB2bEcomHeaderEntity "
					+ " GetGstr2bB2bEcomHeaderEntity object";
			LOGGER.debug(str);
		}
		GetGstr2bB2bEcomHeaderEntity obj = new GetGstr2bB2bEcomHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setInvoiceNumber(dto.getInvoiceNumber());
		obj.setInvoiceDate(dto.getInvoiceDate());
		obj.setInvoiceType(dto.getInvoiceType());
		obj.setSupInvoiceValue(dto.getSupInvoiceValue());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setPos(dto.getPos());
		obj.setRev(dto.getRev());
		obj.setItcAvailable(dto.getItcAvailable());
		obj.setRsn(dto.getRsn());
		//obj.setDiffPercent(dto.getDiffPercent());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(false);
		obj.setDocKey(dto.getDocKey());
		obj.setIrnNo(dto.getIrnNo());
		obj.setIrnSrcType(dto.getIrnSrcType());
		obj.setIrnGenDate(dto.getIrnGenDate());
		// line item
		List<GetGstr2bEcomItemEntity> itemList = new ArrayList<>();

		List<GetGstr2bEcomItemEntity> lineItems = dto
				.getLineItems();

		for (GetGstr2bEcomItemEntity item : lineItems) {
			GetGstr2bEcomItemEntity itemEntity = new GetGstr2bEcomItemEntity();
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setCgstAmt(item.getCgstAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setItemNumber(item.getItemNumber());
			itemEntity.setSgstAmt(item.getSgstAmt());
			itemEntity.setTaxableValue(item.getTaxableValue());
			itemEntity.setTaxRate(item.getTaxRate());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItems(itemList);
		}
		return obj;
	}
	
	@Transactional
	private Pair<Long, Integer> processEcomaWorkSheetData(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId,
			String genDate) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processECOMAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2bB2bEcomaHeaderEntity> ecomaList = gstr2bProcessUtil
				.convertEcomaWorkSheetDataToList(objList, columnCount, rgstin,
						taxPeriod, fileId, genDate);

		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(ecomaList)) {
			ecomaList.forEach(data -> {
				String invoiceKey = data.getSGstin() + "_" + data.getRGstin()
						+ "_" + data.getInvoiceDate() + "_"
						+ data.getInvoiceNumber() + "_" + data.getInvoiceType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processecomaListWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2bB2bEcomaHeaderEntity> entityList = getGstr2bB2bEcomaHeaderRepository
						.findByInvoiceKey(data.getSGstin(), data.getRGstin(),
								data.getInvoiceDate(), data.getInvoiceNumber(),
								data.getInvoiceType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2bB2bEcomaHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}

		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr2bB2bEcomaHeaderRepository
					.updateSameRecords(partion, now));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processECOMAWorkSheetData ->Records ready for insert/update list size [{}]",
					ecomaList.size());
		}
		
		//need to check why this code is written
		List<GetGstr2bB2bEcomaHeaderEntity> ecomaFilterList = Gstr2bTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonEcomaHeaders(ecomaList);
		getGstr2bB2bEcomaHeaderRepository.saveAll(ecomaFilterList);

	/*	List<GetGstr2bB2bEcomaHeaderEntity> mainB2baList = ecomaFilterList
				.stream().map(o -> convertTocomaDto(o))
				.collect(Collectors.toCollection(ArrayList::new));*/

		//List<Long> totalIdsMainTable = Lists.newArrayList();
		/*if (CollectionUtils.isNotEmpty(mainB2baList)) {
			mainB2baList.forEach(data -> {
				String invoiceKey = data.getDocKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<Gstr2GetGstr2BB2baHeaderEntity> entityList = b2bAHeaderRepo
						.findByInvoiceKey(invoiceKey);
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIdsMainTable.addAll(entityList.stream()
							.map(Gstr2GetGstr2BB2baHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}*/

		/*String jobNameMainTable = "Disabling the existing records:::->"
				+ totalIdsMainTable.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobNameMainTable);
		}*/

		/*if (CollectionUtils.isNotEmpty(totalIdsMainTable)) {
			List<List<Long>> partionsMain = Lists.partition(totalIdsMainTable,
					1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partionsMain.forEach(
					partion -> b2bAHeaderRepo.updateSameRecords(partion, now));
		}
		b2bAHeaderRepo.saveAll(mainB2baList);*/

		return new Pair<>(1L, ecomaList.size());
	}
	
	private GetGstr2bB2bEcomaHeaderEntity convertTocomaDto(
			GetGstr2bB2bEcomaHeaderEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting GetGstr2bEcomaHeaderEntity "
					+ " GetGstr2bEcomaHeaderEntity object";
			LOGGER.debug(str);
		}
		GetGstr2bB2bEcomaHeaderEntity obj = new GetGstr2bB2bEcomaHeaderEntity();
		obj.setRGstin(dto.getRGstin());
		obj.setTaxPeriod(dto.getTaxPeriod());
		obj.setVersion(dto.getVersion());
		obj.setGenDate(dto.getGenDate());
		obj.setSGstin(dto.getSGstin());
		obj.setSupTradeName(dto.getSupTradeName());
		obj.setSupFilingDate(dto.getSupFilingDate());
		obj.setSupFilingPeriod(dto.getSupFilingPeriod());
		obj.setInvoiceNumber(dto.getInvoiceNumber());
		obj.setInvoiceDate(dto.getInvoiceDate());
		obj.setInvoiceType(dto.getInvoiceType());
		obj.setSupInvoiceValue(dto.getSupInvoiceValue());
		obj.setCreatedBy(dto.getCreatedBy());
		obj.setModifiedBy(dto.getModifiedBy());
		obj.setPos(dto.getPos());
		obj.setRev(dto.getRev());
		obj.setItcAvailable(dto.getItcAvailable()); 
		obj.setRsn(dto.getRsn());
		obj.setCreatedOn(LocalDateTime.now());
		obj.setModifiedOn(LocalDateTime.now());
		obj.setIsDelete(dto.getIsDelete());
		obj.setDocKey(dto.getDocKey());
		obj.setIrnGenDate(dto.getIrnGenDate());
		obj.setIrnNo(dto.getIrnNo());
		obj.setIrnSrcType(dto.getIrnSrcType());

		List<GetGstr2bEcomaItemEntity> itemList = new ArrayList<>();
		List<GetGstr2bEcomaItemEntity> lineItems = dto
				.getLineItemss();
		for (GetGstr2bEcomaItemEntity item : lineItems) {
			GetGstr2bEcomaItemEntity itemEntity = new GetGstr2bEcomaItemEntity();
			itemEntity.setCessAmt(item.getCessAmt());
			itemEntity.setCgstAmt(item.getCgstAmt());
			itemEntity.setIgstAmt(item.getIgstAmt());
			itemEntity.setItemNumber(item.getItemNumber());
			itemEntity.setSgstAmt(item.getSgstAmt());
			itemEntity.setTaxableValue(item.getTaxableValue());
			itemEntity.setTaxRate(item.getTaxRate());
			itemEntity.setHeader(obj);
			itemList.add(itemEntity);
			obj.setLineItemss(itemList);
			//obj.setLineItems(itemList);
		}

		return obj;
	}

}
