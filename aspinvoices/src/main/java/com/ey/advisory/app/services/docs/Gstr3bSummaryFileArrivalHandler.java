package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bSummaryErrorRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bSummaryUploadVarCharRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.app.gstr3b.Table4d1ValidationtSaveUpdateServiceImpl;
import com.ey.advisory.app.services.strcutvalidation.einvoice.Gstr3bSummaryStructuralChainValidation;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadDto;
import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadErrorEntity;
import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadVarCharEntity;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Service("Gstr3bSummaryFileArrivalHandler")
@Slf4j
public class Gstr3bSummaryFileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr3bSummaryUploadVarCharRepository")
	private Gstr3bSummaryUploadVarCharRepository varcharRepo;

	@Autowired
	@Qualifier("Gstr3bSummaryStructuralChainValidation")
	private Gstr3bSummaryStructuralChainValidation structuralValidation;

	@Autowired
	@Qualifier("Gstr3bSummaryErrorRepository")
	private Gstr3bSummaryErrorRepository errRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository psdRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("Table4d1ValidationtSaveUpdateServiceImpl")
	private Table4d1ValidationtSaveUpdateServiceImpl validationService;

	private static ThreadLocal<NumberFormat> numberFormatter = ThreadLocal
			.withInitial(() -> new DecimalFormat("0"));
	private static String[] headerArray = { "ReturnPeriod", "GSTIN",
			"TableNumber", "TotalTaxablevalue", "IGSTAmount", "CGSTAmount",
			"SGST/UTAmount", "CessAmount", "POS" };

	public static List<String> ab4List = Arrays.asList("4(a)(1)", "4(a)(2)",
			"4(a)(3)", "4(a)(4)", "4(a)(5)", "4(b)(1)", "4(b)(2)");

	public static List<String> a4List = Arrays.asList("4(a)(1)", "4(a)(2)",
			"4(a)(3)", "4(a)(4)", "4(a)(5)");

	public static List<String> b4List = Arrays.asList("4(b)(1)", "4(b)(2)");

	private static List<String> HEADER_LIST = Arrays.asList(headerArray);
	private static final String DOC_KEY_JOINER = "|";

	public void processProductFile(Message message, AppExecContext context) {

		FileArrivalMsgDto msg = extractAndValidateMessage(message);
		
		// Join the file path and file name to get the file path.

		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		Long fileId = json.get("fileId").getAsLong();
		String docId = json.get("docId").getAsString();
		Document document = null;
		try {
			// reading row data

			validateHeaders(fileName, fileFolder, fileId);
			// doc id changes
			if (Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"documnet is not downloaded using doc id(GSTR-3B Form) ");
				}
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"documnet is downloaded using doc id(GSTR-3B Form) ");
				}
			}
			if (document == null) {
				String errMsg = String.format(
						"Unable to find the file %s in folder %s", fileName,
						fileFolder);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			InputStream fin = document.getContentStream().getStream();

			TabularDataLayout layout = new DummyTabularDataLayout(9);
			// Gstr3bSummaryDocumentKeyBuilder documentKeyBuilder = new
			// Gstr3bSummaryDocumentKeyBuilder();
			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			traverser.traverse(fin, layout, rowHandler, null);
			List<Object[]> fileList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();
			if (fileList.isEmpty() || fileList == null) {

				String msg1 = "Failed Empty file..";
				LOGGER.error(msg1);
				if (msg1.length() > 200)
					msg1 = msg1.substring(0, 200);
				gstr1FileStatusRepository.updateErrorFieNameById(fileId, msg1);
				throw new AppException(msg1);

			}
			// saving to varchar table
			List<Gstr3bSummaryUploadVarCharEntity> dumpRecords = fileList
					.stream().map(o -> convertToStageEntity(o, fileId))
					.collect(Collectors.toList());

			List<String> docKeyList = dumpRecords.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));
			
			//get option opted
			List<String> gstinList = dumpRecords.stream().distinct().map(o -> o.getGstin())
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String, Object> gstinEntityIdMap = getGstinInfoMap(gstinList);

			Map<String, String> gstinToOptionOptedMap = gstinEntityIdMap.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, // Key: GSTIN
							entry -> {
								try {
									Long entityId = Long.valueOf(entry.getValue().toString());
									return entityConfigPemtRepo.findAnsbyQuestion(entityId,
											"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
								} catch (Exception e) {
									LOGGER.error("unable to find entity Id so using default {}");
									return "A";
								}
							}));

			// dump softDelete
			dumpSoftDelete(docKeyList);
			// save
			varcharRepo.saveAll(dumpRecords);

			Integer errorCount = 0;
			Integer psdCount = 0;

			// validation and adding error code
			Triplet<List<Gstr3bSummaryUploadDto>, Integer, Integer> entityTriplet = validation(
					fileList, fileId, errorCount, psdCount, docKeyList);

			List<Gstr3bSummaryUploadDto> entityList = entityTriplet.getValue0();
			psdCount = entityTriplet.getValue1();
			errorCount = entityTriplet.getValue2();

			// creating psd list
			List<Gstr3BGstinAspUserInputEntity> psdEntityList1 = entityList
					.stream().filter(o -> o.isPsd())
					.map(o -> convertToPsdList(o)).collect(Collectors.toList());

			List<Gstr3BGstinAspUserInputEntity> psdEntityList = new ArrayList<>();
			for (Gstr3BGstinAspUserInputEntity e : psdEntityList1) {
				if (e.getSectionName().equalsIgnoreCase("4(a)(5)")
						|| e.getSectionName().equalsIgnoreCase("4(b)(2)")
						|| e.getSectionName().equalsIgnoreCase("4(b)(1)")
						|| e.getSectionName().equalsIgnoreCase("4(a)(1)")
						|| e.getSectionName().equalsIgnoreCase("4(a)(3)")) {
					psdEntityList.add(setSpecificPsdEntity(e,gstinToOptionOptedMap));
				}
				psdEntityList.add(e);
			}

			// creating Error list
			List<Gstr3bSummaryUploadErrorEntity> errEntityList = entityList
					.stream().filter(o -> !o.isPsd())
					.map(o -> convertToErrorList(o))
					.collect(Collectors.toList());

			// making isDelete flag true before saving in psd table
			LOGGER.debug("about to psd SoftDelete fileId {} ", fileId);
			// softDelete(psdDocKeyList, true);
			for (Gstr3BGstinAspUserInputEntity entity : psdEntityList) {
				if (entity.getPos() != null) {
					psdRepo.updateUserActiveFlagforAllSections(
							entity.getTaxPeriod(), entity.getGstin(),
							entity.getSectionName(), entity.getPos());
				} else {
					psdRepo.updateUserActiveFlagforAllSectionsWithoutPos(
							entity.getTaxPeriod(), entity.getGstin(),
							entity.getSectionName());
				}
			}
			LOGGER.debug("psd SoftDeleted fileId {} ", fileId);

			// saving into psd db table
			LOGGER.debug("about to save Psd fileId {} ", fileId);
			psdRepo.saveAll(psdEntityList);
			LOGGER.debug("saved psd fileId {} ", fileId);

			// saving into Error db table
			LOGGER.debug("about to save Error fileId {} ", fileId);
			errRepo.saveAll(errEntityList);
			LOGGER.debug("saved Error fileId {} ", fileId);

			LOGGER.debug("About to update count summry for fileId {}", fileId);
			// update error and psd counts
			gstr1FileStatusRepository.updateCountSummary(fileId,
					dumpRecords.size(),
					dumpRecords.size() - errEntityList.size(),
					errEntityList.size());
			LOGGER.debug("updated count summry for fileId {}, About to "
					+ "update the user response", fileId);
			gstr1FileStatusRepository.updateFileStatus(fileId, "Processed");

			HashSet<String> set = new HashSet<>();

			// 4C
			for (Gstr3BGstinAspUserInputEntity entity : psdEntityList) {

				String key = entity.getGstin() + "_" + entity.getTaxPeriod();
				set.add(key);

			}

			for (String element : set) {

				String[] gstinAndTaxPeriod = element.split("_");

				save4cData(gstinAndTaxPeriod[0], gstinAndTaxPeriod[1], ab4List);
			}

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(e);
		}
	}

	public Map<String, Object> getGstinInfoMap(List<String> gstins) {
		List<GSTNDetailEntity> gstinInfoList = gSTNDetailRepository.findByGstinInAndIsDeleteFalse(gstins);

		return gstinInfoList.stream().collect(Collectors.toMap(GSTNDetailEntity::getGstin, // Key: GSTIN
				entity -> entity.getEntityId()
		));
	}
	private void save4cData(String gstin, String taxPeriod,
			List<String> sections) {
		try {
			List<Gstr3BGstinAspUserInputEntity> entityList = psdRepo
					.findByGstinAndTaxPeriodAndSectionNameInAndIsActiveTrue(
							gstin, taxPeriod, sections);

			Gstr3BGstinAspUserInputEntity section4a = entityList.stream()
					.filter(o -> o.getSectionName().equalsIgnoreCase("4(a)(1)")
							|| o.getSectionName().equalsIgnoreCase("4(a)(2)")
							|| o.getSectionName().equalsIgnoreCase("4(a)(3)")
							|| o.getSectionName().equalsIgnoreCase("4(a)(4)")
							|| o.getSectionName().equalsIgnoreCase("4(a)(5)"))
					.reduce((a, b) -> {
						a.setIgst(a.getIgst().add(b.getIgst()));
						a.setCgst(a.getCgst().add(b.getCgst()));
						a.setSgst(a.getSgst().add(b.getSgst()));
						a.setCess(a.getCess().add(b.getCess()));
						return a;
					}).orElse(null);

			Gstr3BGstinAspUserInputEntity section4b = entityList.stream()
					.filter(o -> o.getSectionName().equalsIgnoreCase("4(b)(1)")
							|| o.getSectionName().equalsIgnoreCase("4(b)(2)"))
					.reduce((a, b) -> {
						a.setIgst(a.getIgst().add(b.getIgst()));
						a.setCgst(a.getCgst().add(b.getCgst()));
						a.setSgst(a.getSgst().add(b.getSgst()));
						a.setCess(a.getCess().add(b.getCess()));
						return a;
					}).orElse(null);

			if (section4a == null) {
				section4a = new Gstr3BGstinAspUserInputEntity();
				// Create a new instance with default values
				section4a.setIgst(BigDecimal.ZERO);
				section4a.setCgst(BigDecimal.ZERO);
				section4a.setSgst(BigDecimal.ZERO);
				section4a.setCess(BigDecimal.ZERO);
			}

			if (section4b == null) {
				section4b = new Gstr3BGstinAspUserInputEntity();
				// Create a new instance with default values
				section4b.setIgst(BigDecimal.ZERO);
				section4b.setCgst(BigDecimal.ZERO);
				section4b.setSgst(BigDecimal.ZERO);
				section4b.setCess(BigDecimal.ZERO);
			}

			// Calculate 4C
			Gstr3BGstinAspUserInputEntity section4c = new Gstr3BGstinAspUserInputEntity();

			section4c
					.setIgst(section4a.getIgst().subtract(section4b.getIgst()));
			section4c
					.setCgst(section4a.getCgst().subtract(section4b.getCgst()));
			section4c
					.setSgst(section4a.getSgst().subtract(section4b.getSgst()));
			section4c
					.setCess(section4a.getCess().subtract(section4b.getCess()));
			section4c.setGstin(gstin);
			section4c.setTaxPeriod(taxPeriod);
			section4c.setIsActive(true);
			section4c.setCreatedBy("SYSTEM");
			section4c.setCreateDate(LocalDateTime.now());
			section4c.setSectionName("4(c)");
			section4c.setSubSectionName("ITC_AVL");
			section4c.setIsITCActive(true);
			section4c.setUpdatedBy("SYSTEM");
			section4c.setUpdateDate(LocalDateTime.now());

			psdRepo.updateUserActiveFlagforAllSectionsWithoutPos(taxPeriod,
					gstin, "4(c)");

			psdRepo.save(section4c);

		} catch (Exception ex) {
			String msg = String.format("Error Occured while calcuating 4C {}",
					ex);
			LOGGER.error(msg);
			throw new AppException(ex);
		}

	}

	private FileArrivalMsgDto extractAndValidateMessage(Message message) {
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance()
				.fromJson(message.getParamsJson(), FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}
		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}

	public static Object trimObj(Object obj) {
		return obj != null ? obj.toString().trim() : null;
	}

	private Gstr3bSummaryUploadVarCharEntity convertToStageEntity(Object[] arr,
			Long fileId) {
		Gstr3bSummaryUploadVarCharEntity entity = new Gstr3bSummaryUploadVarCharEntity();
		String returnPeriod = arr[0] != null
				? (Strings.isNullOrEmpty(arr[0].toString().trim()) != true
						? arr[0].toString().trim() : null)
				: null;
		String gstin = arr[1] != null
				? (Strings.isNullOrEmpty(arr[1].toString().trim()) != true
						? arr[1].toString().trim() : null)
				: null;
		String tableNumber = arr[2] != null
				? (Strings.isNullOrEmpty(arr[2].toString().trim()) != true
						? arr[2].toString().trim() : null)
				: null;
		String totalTaxableValue = arr[3] != null
				? (Strings.isNullOrEmpty(arr[3].toString().trim()) != true
						? arr[3].toString().trim() : null)
				: null;
		String igstAmount = arr[4] != null
				? (Strings.isNullOrEmpty(arr[4].toString().trim()) != true
						? arr[4].toString().trim() : null)
				: null;
		String cgstAmount = arr[5] != null
				? (Strings.isNullOrEmpty(arr[5].toString().trim()) != true
						? arr[5].toString().trim() : null)
				: null;
		String sgstAmount = arr[6] != null
				? (Strings.isNullOrEmpty(arr[6].toString().trim()) != true
						? arr[6].toString().trim() : null)
				: null;
		String cessAmount = arr[7] != null
				? (Strings.isNullOrEmpty(arr[7].toString().trim()) != true
						? arr[7].toString().trim() : null)
				: null;
		String pos = arr[8] != null
				? (Strings.isNullOrEmpty(arr[8].toString().trim()) != true
						? arr[8].toString().trim() : null)
				: null;

		String docKey = new StringJoiner(DOC_KEY_JOINER).add(returnPeriod)
				.add(gstin).add(tableNumber).add(pos).toString();
		entity.setReturnPeriod(returnPeriod);
		entity.setGstin(gstin);
		entity.setTableNumber(tableNumber);
		entity.setTotalTaxableValue(totalTaxableValue);
		entity.setIgstAmount(igstAmount);
		entity.setCgstAmount(cgstAmount);
		entity.setSgstAmount(sgstAmount);
		entity.setCessAmount(cessAmount);
		entity.setPos(pos);
		entity.setDocKey(docKey);
		entity.setFileId(fileId);
		entity.setIsActive(true);
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
		return entity;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {
			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 9);

			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverseHeaderOnly(fin, layout, rowHandler, null);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<String> actualHeaderNames = new ArrayList(
					Arrays.asList(rowHandler.getHeaderRow()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s "
								+ "and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != 9) {
				String msg = "The number of columns in the file should be 29. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				gstr1FileStatusRepository.updateErrorFieNameById(fileId, msg);
				LOGGER.equals(msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = HEADER_LIST;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected User response headers names "
								+ "%s and header count %d",
						expectedHeaderNames.toString(),
						expectedHeaderNames.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(expectedHeaderNames,
					actualHeaderNames);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Comparing two files header name " + "and count: %s",
						isMatch);
				LOGGER.debug(msg);
			}

			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				markFileAsFailed(fileId, msg);
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				gstr1FileStatusRepository.updateErrorFieNameById(fileId, msg);
				LOGGER.equals(msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			markFileAsFailed(fileId, msg);
			if (msg.length() > 200)
				msg = msg.substring(0, 200);

			gstr1FileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private InputStream getFileInpStream(String fileName, String folderName) {
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession:{} ", openCmisSession);
			}
			Document document = gstr1FileUploadUtil.getDocument(openCmisSession,
					fileName, folderName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name:{}", document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in reading File Gstr3Summary File Arrival Handler",
					e);
			throw new AppException(
					"Error occured while " + "reading the file " + fileName, e);
		}
		return inputStream;
	}

	private void markFileAsFailed(Long fileId, String reason) {
		try {
			gstr1FileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = "[SEVERE] Unable to mark the file as failed. "
					+ "Reason for file failure is: [" + reason + "]";
			LOGGER.error(msg, ex);
			if (msg.length() > 200)
				msg = msg.substring(0, 200);
			gstr1FileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw new AppException(msg, ex);
		}
	}

	private boolean headersMatch(List<String> expected, List<String> actual) {
		return !Streams
				.zip(actual.stream(), expected.stream(),
						(a, e) -> createPair(a, e))
				.filter(p -> !p.getValue0().equals(p.getValue1())).findAny()
				.isPresent();
	}

	private Pair<String, String> createPair(String val1, String val2) {
		String val1Str = (val1 == null) ? "" : val1.trim().toUpperCase();
		String val2Str = (val2 == null) ? "" : val2.trim().toUpperCase();
		return new Pair<>(val1Str, val2Str);
	}

	private Triplet<List<Gstr3bSummaryUploadDto>, Integer, Integer> validation(
			List<Object[]> fileList, Long fileId, Integer errorCount,
			Integer psdCount, List<String> docKeyList) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Gstr3bSummaryFileArrivalHandler "
					+ "Validation method ";
			LOGGER.debug(msg);
		}
		List<ProcessingResult> validationResult = null;

		List<Gstr3bSummaryUploadDto> processedEntityList = new ArrayList<>();
		// gstin info list
		List<String> gstinList = gSTNDetailRepository.findAllGstin();

		Map<String, Gstr3bSummaryUploadDto> processedMap = new HashMap<>();

		Map<String, List<Gstr3bSummaryUploadDto>> errorMap = new HashMap<>();

		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();
			String errorMessage = null;
			String errorCodes = null;

			structuralValidation.rowDataValidation(validationResult, rowData,
					gstinList);

			List<String> errorArrayList = validationResult.stream()
					.filter(result -> result.getDescription() != null)
					.map(ProcessingResult::getDescription)
					.collect(Collectors.toList());

			List<String> errorCodeList = validationResult.stream()
					.filter(result -> result.getCode() != null)
					.map(ProcessingResult::getCode)
					.collect(Collectors.toList());

			String[] stringArray = new String[9];

			for (int i = 0; i < stringArray.length; i++) {
				if (i < rowData.length) {
					stringArray[i] = rowData[i] == null ? null
							: (rowData[i].toString());
				}
			}

			if (!CollectionUtils.isEmpty(errorArrayList))
				errorMessage = String.join(",", errorArrayList);
			if (!CollectionUtils.isEmpty(errorCodeList))
				errorCodes = String.join(",", errorCodeList);

			if (validationResult.isEmpty()) {
				makeProcessedEntityList(stringArray, processedMap, errorMap,
						fileId, true, errorCodes, errorMessage);

			} else {
				makeProcessedEntityList(stringArray, processedMap, errorMap,
						fileId, false, errorCodes, errorMessage);
			}
		}

		processedMap.forEach((k, v) -> processedEntityList.add(v));
		errorMap.forEach((k, v) -> processedEntityList.addAll(v));

		List<Gstr3bSummaryUploadDto> psdList = processedEntityList.stream()
				.filter(result -> result.isPsd()).collect(Collectors.toList());

		psdCount = psdList.size();
		errorCount = processedEntityList.size() - psdCount;

		return new Triplet<>(processedEntityList, psdCount, errorCount);
	}

	private void makeProcessedEntityList(String[] arr,
			Map<String, Gstr3bSummaryUploadDto> processedMap,
			Map<String, List<Gstr3bSummaryUploadDto>> errorMap, Long fileId,
			boolean isProceesedFlag, String errorCodes, String errorMessage) {
		Gstr3bSummaryUploadDto entity = new Gstr3bSummaryUploadDto();

		String returnPeriod = arr[0] != null ? arr[0].toString().trim() : null;
		String gstin = arr[1] != null ? arr[1].toString().trim() : null;
		String tableNumber = arr[2] != null ? arr[2].toString().trim() : null;
		String totalTaxableValue = arr[3] != null ? arr[3].toString().trim()
				: null;
		String igstAmount = arr[4] != null ? arr[4].toString().trim() : null;
		String cgstAmount = arr[5] != null ? arr[5].toString().trim() : null;
		String sgstAmount = arr[6] != null ? arr[6].toString().trim() : null;
		String cessAmount = arr[7] != null ? arr[7].toString().trim() : null;
		String pos = arr[8] != null ? arr[8].toString().trim() : null;

		entity.setReturnPeriod(returnPeriod);
		entity.setGstin(gstin);
		if (tableNumber != null) {
			entity.setTableNumber(tableNumber.toLowerCase());
		}
		entity.setTotalTaxableValue(totalTaxableValue);
		entity.setIgstAmount(igstAmount);
		entity.setCgstAmount(cgstAmount);
		entity.setSgstAmount(sgstAmount);
		entity.setCessAmount(cessAmount);
		entity.setErrorCode(errorCodes);
		entity.setErrorDesc(errorMessage);
		entity.setPos(pos);
		String docKey = new StringJoiner(DOC_KEY_JOINER).add(returnPeriod)
				.add(gstin).add(tableNumber).add(pos).toString();
		entity.setDocKey(docKey);
		entity.setFileId(fileId);
		entity.setPsd(isProceesedFlag);

		if (isProceesedFlag) {
			if (processedMap.containsKey(docKey)) {
				Gstr3bSummaryUploadDto existingEntity = processedMap
						.get(docKey);
				existingEntity.setPsd(false);
				existingEntity.setErrorCode("ER-108");
				existingEntity.setErrorDesc("Duplicate Invoice key is not "
						+ "allowed in the same file/payload");
				errorMap.computeIfAbsent(docKey,
						obj -> new ArrayList<Gstr3bSummaryUploadDto>())
						.add(existingEntity);
				processedMap.put(docKey, entity);
			} else {
				processedMap.put(docKey, entity);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Gstr3bSummaryFileArrivalHandler makeProcessedEntityList() tableNumber : "
								+ tableNumber);
			}
			// write logic here for : US 112491 Warning message wrt ITC Reclaim
			// - GSTR 3B summary screen (Table 4D1) - Part 2
			if (tableNumber.equalsIgnoreCase("4(d)(1)")
					|| tableNumber.equalsIgnoreCase("4(b)(2)")
					|| tableNumber.equalsIgnoreCase("4.d.1")
					|| tableNumber.equalsIgnoreCase("4.b.2")) {
				if ((!Strings.isNullOrEmpty(gstin))
						&& (!Strings.isNullOrEmpty(returnPeriod))) {
					validationService.update4D1ValidationStatus(gstin,
							returnPeriod);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE Gstr3bSummaryFileArrivalHandler update4D1ValidationStatus() method called");
			}
		} else {
			errorMap.computeIfAbsent(docKey,
					obj -> new ArrayList<Gstr3bSummaryUploadDto>()).add(entity);
		}

	}

	private void dumpSoftDelete(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = varcharRepo.updateIsActiveFlag(chunk);
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in "
							+ "TBL_GSTR3BSummaryUpload_Varchar table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private Gstr3bSummaryUploadErrorEntity convertToErrorList(
			Gstr3bSummaryUploadDto dto) {

		Gstr3bSummaryUploadErrorEntity entity = new Gstr3bSummaryUploadErrorEntity();
		entity.setGstin(dto.getGstin());
		entity.setFileId(dto.getFileId());
		entity.setIgstAmount(dto.getIgstAmount());
		entity.setCgstAmount(dto.getCgstAmount());
		entity.setTotalTaxablevalue(dto.getTotalTaxableValue());
		entity.setReturnPeriod(dto.getReturnPeriod());
		entity.setCreateDate(LocalDateTime.now());
		entity.setDocKey(dto.getDocKey());
		entity.setIsActive(true);
		entity.setCessAmount(dto.getCessAmount());
		entity.setPos(dto.getPos());
		entity.setSgstAmount(dto.getSgstAmount());
		entity.setErrorCode(dto.getErrorCode());
		entity.setErrorMessage(dto.getErrorDesc());
		if (dto.getTableNumber() != null) {
			entity.setTableNumber(dto.getTableNumber().toLowerCase());
		}
		return entity;
	}

	private Gstr3BGstinAspUserInputEntity convertToPsdList(
			Gstr3bSummaryUploadDto dto) {
		
		Gstr3BGstinAspUserInputEntity entity = new Gstr3BGstinAspUserInputEntity();
		entity.setGstin(dto.getGstin());
		entity.setFileId(dto.getFileId());
		BigDecimal cessAmount = dto.getCessAmount() != null
				&& !dto.getCessAmount().isEmpty()
						? new BigDecimal(dto.getCessAmount()) : BigDecimal.ZERO;
		entity.setCess(cessAmount);
		BigDecimal igstAmount = dto.getIgstAmount() != null
				&& !dto.getIgstAmount().isEmpty()
						? new BigDecimal(dto.getIgstAmount()) : BigDecimal.ZERO;
		entity.setIgst(igstAmount);
		BigDecimal cgstAmount = dto.getCgstAmount() != null
				&& !dto.getCgstAmount().isEmpty()
						? new BigDecimal(dto.getCgstAmount()) : BigDecimal.ZERO;
		entity.setCgst(cgstAmount);
		BigDecimal totalTaxableValue = dto.getTotalTaxableValue() != null
				&& !dto.getTotalTaxableValue().isEmpty()
						? new BigDecimal(dto.getTotalTaxableValue())
						: BigDecimal.ZERO;
		entity.setTaxableVal(totalTaxableValue);
		entity.setTaxPeriod(dto.getReturnPeriod());
		entity.setCreateDate(LocalDateTime.now());
		entity.setDocKey(dto.getDocKey());
		entity.setIsActive(true);
		entity.setPos(dto.getPos());
		BigDecimal sgstAmount = dto.getSgstAmount() != null
				&& !dto.getSgstAmount().isEmpty()
						? new BigDecimal(dto.getSgstAmount()) : BigDecimal.ZERO;
		entity.setSgst(sgstAmount);
		if (dto.getTableNumber() != null) {
			if (dto.getTableNumber().equalsIgnoreCase("5.a.1")
					|| dto.getTableNumber().equalsIgnoreCase("5.a.2")) {
				entity.setSectionName("5(a)");
				if (dto.getTableNumber().equalsIgnoreCase("5.a.1")) {
					entity.setInterState(totalTaxableValue);
					entity.setIntraState(BigDecimal.ZERO);
				} else if (dto.getTableNumber().equalsIgnoreCase("5.a.2")) {
					entity.setInterState(BigDecimal.ZERO);
					entity.setIntraState(totalTaxableValue);
				}
			} else if (dto.getTableNumber().equalsIgnoreCase("5.b.1")
					|| dto.getTableNumber().equalsIgnoreCase("5.b.2")) {
				entity.setSectionName("5(b)");
				if (dto.getTableNumber().equalsIgnoreCase("5.b.1")) {
					entity.setInterState(totalTaxableValue);
					entity.setIntraState(BigDecimal.ZERO);
				} else if (dto.getTableNumber().equalsIgnoreCase("5.b.2")) {
					entity.setInterState(BigDecimal.ZERO);
					entity.setIntraState(totalTaxableValue);
				}
			} else if (dto.getTableNumber().equalsIgnoreCase("3.1.a")) {
				entity.setSectionName("3.1(a)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.1.b")) {
				entity.setSectionName("3.1(b)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.1.c")) {
				entity.setSectionName("3.1(c)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.1.d")) {
				entity.setSectionName("3.1(d)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.1.e")) {
				entity.setSectionName("3.1(e)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.1.1.a")) {
				entity.setSectionName("3.1.1(a)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.1.1.b")) {
				entity.setSectionName("3.1.1(b)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.2.a")) {
				entity.setSectionName("3.2(a)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.2.b")) {
				entity.setSectionName("3.2(b)");
			} else if (dto.getTableNumber().equalsIgnoreCase("3.2.c")) {
				entity.setSectionName("3.2(c)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.a.1")) {
				entity.setSectionName("4(a)(1)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.a.2")) {
				entity.setSectionName("4(a)(2)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.a.3")) {
				entity.setSectionName("4(a)(3)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.a.4")) {
				entity.setSectionName("4(a)(4)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.a.5")) {
				entity.setSectionName("4(a)(5)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.b.1")) {
				entity.setSectionName("4(b)(1)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.b.2")) {
				entity.setSectionName("4(b)(2)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.d.1")) {
				entity.setSectionName("4(d)(1)");
			} else if (dto.getTableNumber().equalsIgnoreCase("4.d.2")) {
				entity.setSectionName("4(d)(2)");
			} else if (dto.getTableNumber().equalsIgnoreCase("5.1.a")) {
				entity.setSectionName("5.1(a)");
			} else if (dto.getTableNumber().equalsIgnoreCase("5.1.b")) {
				entity.setSectionName("5.1(b)");
			} else {
				entity.setSectionName(dto.getTableNumber().toLowerCase());
			}
		}
		entity.setSubSectionName("");
		entity.setIsITCActive(true);
		return entity;
	}

	private Gstr3BGstinAspUserInputEntity setSpecificPsdEntity(
			Gstr3BGstinAspUserInputEntity dto ,Map<String, String> gstinOptionOptedMap) {

		String optionOpted = gstinOptionOptedMap.get(dto.getGstin());
		LOGGER.debug("optionOpted for entity {} ", optionOpted);
		Gstr3BGstinAspUserInputEntity entity = new Gstr3BGstinAspUserInputEntity();
		entity.setGstin(dto.getGstin());
		entity.setFileId(dto.getFileId());
		entity.setCess(dto.getCess());
		entity.setIgst(dto.getIgst());
		entity.setCgst(dto.getCgst());
		entity.setTaxableVal(dto.getTaxableVal());
		entity.setTaxPeriod(dto.getTaxPeriod());
		entity.setCreateDate(LocalDateTime.now());
		entity.setDocKey(dto.getDocKey());
		entity.setIsActive(true);
		entity.setPos(dto.getPos());
		entity.setSgst(dto.getSgst());
		entity.setSubSectionName("");
		entity.setIsITCActive(true);
		if (dto.getSectionName().equalsIgnoreCase("4(a)(5)")) {
			entity.setSectionName("4(a)(5)(5.1.a)");
		} else if (dto.getSectionName().equalsIgnoreCase("4(b)(2)")) {
			entity.setSectionName("4(b)(2)(2.1)");
		} else if (dto.getSectionName().equalsIgnoreCase("4(b)(1)")) {
			entity.setSectionName("4(b)(1)(1.1)");
		} else if (dto.getSectionName().equalsIgnoreCase("4(a)(1)")) {
			if ("A".equals(optionOpted) || "C".equals(optionOpted)) {
				entity.setSectionName("4(a)(1)(1.1.a)");
			}
		} else if (dto.getSectionName().equalsIgnoreCase("4(a)(3)")) {
			if ("A".equals(optionOpted) || "C".equals(optionOpted)) {
				entity.setSectionName("4(a)(3)(3.1.a.a)");
			}
		}
		return entity;

	}
}
