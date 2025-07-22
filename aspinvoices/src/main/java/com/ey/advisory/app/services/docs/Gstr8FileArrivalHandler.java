package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.gstr8.Gstr8UploadErrorEntity;
import com.ey.advisory.app.data.entities.gstr8.Gstr8UploadProcessedEntity;
import com.ey.advisory.app.data.entities.gstr8.Gstr8UploadVarCharEntity;
import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8ErrorRepository;
import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8UploadPsdRepository;
import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8UploadVarCharRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.strcutvalidation.einvoice.Gstr8StructuralChainValidation;
import com.ey.advisory.app.services.strcutvalidation.gstr8.Gstr8Dto;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
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

@Slf4j
@Service("Gstr8FileArrivalHandler")
public class Gstr8FileArrivalHandler {

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
	@Qualifier("Gstr8UploadVarCharRepository")
	private Gstr8UploadVarCharRepository varcharRepo;

	@Autowired
	@Qualifier("Gstr8StructuralChainValidation")
	private Gstr8StructuralChainValidation structuralValidation;

	@Autowired
	@Qualifier("Gstr8ErrorRepository")
	private Gstr8ErrorRepository errRepo;

	@Autowired
	@Qualifier("Gstr8UploadPsdRepository")
	private Gstr8UploadPsdRepository psdRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	private static String[] headerArray = { "eCOMGSTIN", "ReturnPeriod",
			"Identifier", "OriginalReturnPeriod", "OriginalNetSupplies",
			"DocumentType", "SupplyType", "SupplierGSTINorEnrolmentID",
			"OriginalSupplierGSTINorEnrolmentID", "POS", "OriginalPOS",
			"SuppliesToRegistered", "ReturnsFromRegistered",
			"SuppliesToUnRegistered", "ReturnsFromUnRegistered", "NetSupplies",
			"IntegratedTaxAmount", "CentralTaxAmount", "StateUTTaxAmount",
			"UserDefinedField1", "UserDefinedField2", "UserDefinedField3",
			"UserDefinedField4", "UserDefinedField5", "UserDefinedField6",
			"UserDefinedField7", "UserDefinedField8", "UserDefinedField9",
			"UserDefinedField10", "UserDefinedField11", "UserDefinedField12",
			"UserDefinedField13", "UserDefinedField14", "UserDefinedField15" };

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
		InputStream inputStream = null;
		try {
			validateHeaders(fileName, fileFolder, fileId);
			Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
					.getFileName(fileName);
			if (updateFileStatus == null) {
				String errMsg = String.format(
						"No Record available for the file Name %s", fileName);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			LOGGER.debug("openCmisSession " + openCmisSession);
			String docId = updateFileStatus.getDocId();
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = gstr1FileUploadUtil.getDocument(openCmisSession,
						fileName, fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			if (document == null) {
				LOGGER.debug("Document is not available in repo");
				throw new AppException("Document is not available in repo ");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name:{}", document.getName());
			}
			inputStream = document.getContentStream().getStream();
			TabularDataLayout layout = new DummyTabularDataLayout(34);
			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			traverser.traverse(inputStream, layout, rowHandler, null);
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
			for (Object[] row : fileList) {
				for (int i = 0; i < row.length; i++) {
					if (row[i] != null) {
						row[i] = row[i].toString().replace("'", "");
						if (row[i].toString().equalsIgnoreCase("")) {
							row[i] = null;
						}
					}
				}
			}
			// saving to varchar table
			List<Gstr8UploadVarCharEntity> dumpRecords = fileList.stream()
					.map(o -> convertToStageEntity(o, fileId))
					.collect(Collectors.toList());

			List<String> docKeyList = dumpRecords.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			// save
			varcharRepo.saveAll(dumpRecords);

			Integer errorCount = 0;
			Integer psdCount = 0;

			// validation and adding error code
			Triplet<List<Gstr8Dto>, Integer, Integer> entityTriplet = validation(
					fileList, fileId, errorCount, psdCount, docKeyList);

			List<Gstr8Dto> entityList = entityTriplet.getValue0();
			psdCount = entityTriplet.getValue1();
			errorCount = entityTriplet.getValue2();

			// creating psd list
			List<Gstr8UploadProcessedEntity> psdEntityList = entityList.stream()
					.filter(o -> o.isPsd()).map(o -> convertToPsdList(o))
					.collect(Collectors.toList());

			// creating Error list
			List<Gstr8UploadErrorEntity> errEntityList = entityList.stream()
					.filter(o -> !o.isPsd()).map(o -> convertToErrorList(o))
					.collect(Collectors.toList());

			LOGGER.debug("psd SoftDeleted fileId {} ", fileId);
			List<String> psdDocKeyList = psdEntityList.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));
			dumpSoftDelete(psdDocKeyList);
			psdRepo.saveAll(psdEntityList);
			LOGGER.debug("saved psd fileId {} ", fileId);

			// saving into Error db table
			LOGGER.debug("about to save Error fileId {} ", fileId);
			errRepo.saveAll(errEntityList);
			LOGGER.debug("saved Error fileId {} ", fileId);

			LOGGER.debug("About to update count summry for fileId {}", fileId);
			// update error and psd counts
			gstr1FileStatusRepository.updateCountSummary(fileId,
					psdEntityList.size() + errEntityList.size(),
					psdEntityList.size(), errEntityList.size());
			
			LOGGER.debug("updated count summry for fileId {}, About to "
					+ "update the user response", fileId);
			gstr1FileStatusRepository.updateFileStatus(fileId, "Processed");

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(e);
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

	private Gstr8UploadVarCharEntity convertToStageEntity(Object[] arr,
			Long fileId) {
		Gstr8UploadVarCharEntity entity = new Gstr8UploadVarCharEntity();

		String eComGstin = arr[0] != null
				? (Strings.isNullOrEmpty(arr[0].toString().trim()) != true
						? arr[0].toString().trim() : null)
				: null;
		String returnPeriod = arr[1] != null
				? (Strings.isNullOrEmpty(arr[1].toString().trim()) != true
						? arr[1].toString().trim() : null)
				: null;
		String identifier = arr[2] != null
				? (Strings.isNullOrEmpty(arr[2].toString().trim()) != true
						? arr[2].toString().trim() : null)
				: null;
		String originalReturnPeriod = arr[3] != null
				? (Strings.isNullOrEmpty(arr[3].toString().trim()) != true
						? arr[3].toString().trim() : null)
				: null;
		String originialNetSupplies = arr[4] != null
				? (Strings.isNullOrEmpty(arr[4].toString().trim()) != true
						? arr[4].toString().trim() : null)
				: null;
		String documentType = arr[5] != null
				? (Strings.isNullOrEmpty(arr[5].toString().trim()) != true
						? arr[5].toString().trim() : null)
				: null;
		String supplyType = arr[6] != null
				? (Strings.isNullOrEmpty(arr[6].toString().trim()) != true
						? arr[6].toString().trim() : null)
				: null;
		String supplierGSTINorEnrolmentID = arr[7] != null
				? (Strings.isNullOrEmpty(arr[7].toString().trim()) != true
						? arr[7].toString().trim() : null)
				: null;
		String originalSupplierGSTINorEnrolmentID = arr[8] != null
				? (Strings.isNullOrEmpty(arr[8].toString().trim()) != true
						? arr[8].toString().trim() : null)
				: null;
		String pos = arr[9] != null
				? (Strings.isNullOrEmpty(arr[9].toString().trim()) != true
						? arr[9].toString().trim() : null)
				: null;
		String originalPos = arr[10] != null
				? (Strings.isNullOrEmpty(arr[10].toString().trim()) != true
						? arr[10].toString().trim() : null)
				: null;
		String suppliesToRegistered = arr[11] != null
				? (Strings.isNullOrEmpty(arr[11].toString().trim()) != true
						? arr[11].toString().trim() : null)
				: null;
		String returnsFromRegistered = arr[12] != null
				? (Strings.isNullOrEmpty(arr[12].toString().trim()) != true
						? arr[12].toString().trim() : null)
				: null;
		String suppliesToUnRegistered = arr[13] != null
				? (Strings.isNullOrEmpty(arr[13].toString().trim()) != true
						? arr[13].toString().trim() : null)
				: null;

		String returnsFromUnRegistered = arr[14] != null
				? (Strings.isNullOrEmpty(arr[14].toString().trim()) != true
						? arr[14].toString().trim() : null)
				: null;
		String netSupplies = arr[15] != null
				? (Strings.isNullOrEmpty(arr[15].toString().trim()) != true
						? arr[15].toString().trim() : null)
				: null;
		String integratedTaxAmount = arr[16] != null
				? (Strings.isNullOrEmpty(arr[16].toString().trim()) != true
						? arr[16].toString().trim() : null)
				: null;
		String centralTaxAmount = arr[17] != null
				? (Strings.isNullOrEmpty(arr[17].toString().trim()) != true
						? arr[17].toString().trim() : null)
				: null;
		String stateUTTaxAmount = arr[18] != null
				? (Strings.isNullOrEmpty(arr[18].toString().trim()) != true
						? arr[18].toString().trim() : null)
				: null;
		String userDefinedField1 = arr[19] != null
				? (Strings.isNullOrEmpty(arr[19].toString().trim()) != true
						? arr[19].toString().trim() : null)
				: null;
		String userDefinedField2 = arr[20] != null
				? (Strings.isNullOrEmpty(arr[20].toString().trim()) != true
						? arr[20].toString().trim() : null)
				: null;
		String userDefinedField3 = arr[21] != null
				? (Strings.isNullOrEmpty(arr[21].toString().trim()) != true
						? arr[21].toString().trim() : null)
				: null;
		String userDefinedField4 = arr[22] != null
				? (Strings.isNullOrEmpty(arr[22].toString().trim()) != true
						? arr[22].toString().trim() : null)
				: null;
		String userDefinedField5 = arr[23] != null
				? (Strings.isNullOrEmpty(arr[23].toString().trim()) != true
						? arr[23].toString().trim() : null)
				: null;
		String userDefinedField6 = arr[24] != null
				? (Strings.isNullOrEmpty(arr[24].toString().trim()) != true
						? arr[24].toString().trim() : null)
				: null;
		String userDefinedField7 = arr[25] != null
				? (Strings.isNullOrEmpty(arr[25].toString().trim()) != true
						? arr[25].toString().trim() : null)
				: null;
		String userDefinedField8 = arr[26] != null
				? (Strings.isNullOrEmpty(arr[26].toString().trim()) != true
						? arr[26].toString().trim() : null)
				: null;
		String userDefinedField9 = arr[27] != null
				? (Strings.isNullOrEmpty(arr[27].toString().trim()) != true
						? arr[27].toString().trim() : null)
				: null;
		String userDefinedField10 = arr[28] != null
				? (Strings.isNullOrEmpty(arr[28].toString().trim()) != true
						? arr[28].toString().trim() : null)
				: null;
		String userDefinedField11 = arr[29] != null
				? (Strings.isNullOrEmpty(arr[29].toString().trim()) != true
						? arr[29].toString().trim() : null)
				: null;
		String userDefinedField12 = arr[30] != null
				? (Strings.isNullOrEmpty(arr[30].toString().trim()) != true
						? arr[30].toString().trim() : null)
				: null;
		String userDefinedField13 = arr[31] != null
				? (Strings.isNullOrEmpty(arr[31].toString().trim()) != true
						? arr[31].toString().trim() : null)
				: null;
		String userDefinedField14 = arr[32] != null
				? (Strings.isNullOrEmpty(arr[32].toString().trim()) != true
						? arr[32].toString().trim() : null)
				: null;
		String userDefinedField15 = arr[33] != null
				? (Strings.isNullOrEmpty(arr[33].toString().trim()) != true
						? arr[33].toString().trim() : null)
				: null;

		String docKey = new StringJoiner(DOC_KEY_JOINER).add(eComGstin)
				.add(returnPeriod).add(identifier).add(documentType)
				.add(supplierGSTINorEnrolmentID).add(pos).toString();
		entity.setGstin(eComGstin);
		entity.setReturnPeriod(returnPeriod);
		entity.setIdentifier(identifier);
		entity.setOriginalReturnPeriod(originalReturnPeriod);
		entity.setOriginalNetSupplies(originialNetSupplies);
		entity.setDocType(documentType);
		entity.setSupplyType(supplyType);
		entity.setSgstinOrEnrolId(supplierGSTINorEnrolmentID);
		entity.setOriginalSgstinOrEnrolId(originalSupplierGSTINorEnrolmentID);
		entity.setPos(pos);
		entity.setOriginalPos(originalPos);
		entity.setSuppliesToRegistered(suppliesToRegistered);
		entity.setReturnsFromRegistered(returnsFromRegistered);
		entity.setSuppliesToUnregistered(suppliesToUnRegistered);
		entity.setReturnsFromUnregistered(returnsFromUnRegistered);
		entity.setNetSupplies(netSupplies);
		entity.setIgstAmount(integratedTaxAmount);
		entity.setCgstAmount(centralTaxAmount);
		entity.setSgstAmount(stateUTTaxAmount);
		entity.setUserDefinedField1(userDefinedField1);
		entity.setUserDefinedField2(userDefinedField2);
		entity.setUserDefinedField3(userDefinedField3);
		entity.setUserDefinedField4(userDefinedField4);
		entity.setUserDefinedField5(userDefinedField5);
		entity.setUserDefinedField6(userDefinedField6);
		entity.setUserDefinedField7(userDefinedField7);
		entity.setUserDefinedField8(userDefinedField8);
		entity.setUserDefinedField9(userDefinedField9);
		entity.setUserDefinedField10(userDefinedField10);
		entity.setUserDefinedField11(userDefinedField11);
		entity.setUserDefinedField12(userDefinedField12);
		entity.setUserDefinedField13(userDefinedField13);
		entity.setUserDefinedField14(userDefinedField14);
		entity.setUserDefinedField15(userDefinedField15);

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
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 34);

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

			if (actualHeaderNames.size() != 34) {
				String msg = "The number of columns in the file should be 32 "
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

	private Triplet<List<Gstr8Dto>, Integer, Integer> validation(
			List<Object[]> fileList, Long fileId, Integer errorCount,
			Integer psdCount, List<String> docKeyList) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Gstr8ArrivalHandler " + "Validation method ";
			LOGGER.debug(msg);
		}
		List<ProcessingResult> validationResult = null;

		List<Gstr8Dto> processedEntityList = new ArrayList<>();

		Map<String, Gstr8Dto> processedMap = new HashMap<>();
		Map<String, List<Gstr8Dto>> errorMap = new HashMap<>();

		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();
			String errorMessage = null;
			String errorCodes = null;

			structuralValidation.validation(validationResult, rowData);
			List<String> errorArrayList = validationResult.stream()
					.filter(result -> result.getDescription() != null)
					.map(ProcessingResult::getDescription)
					.collect(Collectors.toList());

			List<String> errorCodeList = validationResult.stream()
					.filter(result -> result.getCode() != null)
					.map(ProcessingResult::getCode)
					.collect(Collectors.toList());

			String[] stringArray = new String[34];

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

		List<Gstr8Dto> psdList = processedEntityList.stream()
				.filter(result -> result.isPsd()).collect(Collectors.toList());

		psdCount = psdList.size();
		errorCount = processedEntityList.size() - psdCount;

		return new Triplet<>(processedEntityList, psdCount, errorCount);
	}

	private void makeProcessedEntityList(String[] arr,
			Map<String, Gstr8Dto> processedMap,
			Map<String, List<Gstr8Dto>> errorMap, Long fileId,
			boolean isProceesedFlag, String errorCodes, String errorMessage) {
		Gstr8Dto entity = new Gstr8Dto();

		String eComGstin = arr[0] != null
				? (Strings.isNullOrEmpty(arr[0].toString().trim()) != true
						? arr[0].toString().trim() : null)
				: null;
		String returnPeriod = arr[1] != null
				? (Strings.isNullOrEmpty(arr[1].toString().trim()) != true
						? arr[1].toString().trim() : null)
				: null;
		String identifier = arr[2] != null
				? (Strings.isNullOrEmpty(arr[2].toString().trim()) != true
						? arr[2].toString().trim() : null)
				: null;
		String originalReturnPeriod = arr[3] != null
				? (Strings.isNullOrEmpty(arr[3].toString().trim()) != true
						? arr[3].toString().trim() : null)
				: null;
		String originialNetSupplies = arr[4] != null
				? (Strings.isNullOrEmpty(arr[4].toString().trim()) != true
						? arr[4].toString().trim() : null)
				: null;
		String documentType = arr[5] != null
				? (Strings.isNullOrEmpty(arr[5].toString().trim()) != true
						? arr[5].toString().trim() : null)
				: null;
		String supplyType = arr[6] != null
				? (Strings.isNullOrEmpty(arr[6].toString().trim()) != true
						? arr[6].toString().trim() : null)
				: null;
		String supplierGSTINorEnrolmentID = arr[7] != null
				? (Strings.isNullOrEmpty(arr[7].toString().trim()) != true
						? arr[7].toString().trim() : null)
				: null;
		String originalSupplierGSTINorEnrolmentID = arr[8] != null
				? (Strings.isNullOrEmpty(arr[8].toString().trim()) != true
						? arr[8].toString().trim() : null)
				: null;
		String pos = arr[9] != null
				? (Strings.isNullOrEmpty(arr[9].toString().trim()) != true
						? arr[9].toString().trim() : null)
				: null;
		String originalPos = arr[10] != null
				? (Strings.isNullOrEmpty(arr[10].toString().trim()) != true
						? arr[10].toString().trim() : null)
				: null;
		String suppliesToRegistered = arr[11] != null
				? (Strings.isNullOrEmpty(arr[11].toString().trim()) != true
						? arr[11].toString().trim() : null)
				: null;
		String returnsFromRegistered = arr[12] != null
				? (Strings.isNullOrEmpty(arr[12].toString().trim()) != true
						? arr[12].toString().trim() : null)
				: null;
		String suppliesToUnRegistered = arr[13] != null
				? (Strings.isNullOrEmpty(arr[13].toString().trim()) != true
						? arr[13].toString().trim() : null)
				: null;

		String returnsFromUnRegistered = arr[14] != null
				? (Strings.isNullOrEmpty(arr[14].toString().trim()) != true
						? arr[14].toString().trim() : null)
				: null;
		String netSupplies = arr[15] != null
				? (Strings.isNullOrEmpty(arr[15].toString().trim()) != true
						? arr[15].toString().trim() : null)
				: null;
		String integratedTaxAmount = arr[16] != null
				? (Strings.isNullOrEmpty(arr[16].toString().trim()) != true
						? arr[16].toString().trim() : null)
				: null;
		String centralTaxAmount = arr[17] != null
				? (Strings.isNullOrEmpty(arr[17].toString().trim()) != true
						? arr[17].toString().trim() : null)
				: null;
		String stateUTTaxAmount = arr[18] != null
				? (Strings.isNullOrEmpty(arr[18].toString().trim()) != true
						? arr[18].toString().trim() : null)
				: null;
		String userDefinedField1 = arr[19] != null
				? (Strings.isNullOrEmpty(arr[19].toString().trim()) != true
						? arr[19].toString().trim() : null)
				: null;
		String userDefinedField2 = arr[20] != null
				? (Strings.isNullOrEmpty(arr[20].toString().trim()) != true
						? arr[20].toString().trim() : null)
				: null;
		String userDefinedField3 = arr[21] != null
				? (Strings.isNullOrEmpty(arr[21].toString().trim()) != true
						? arr[21].toString().trim() : null)
				: null;
		String userDefinedField4 = arr[22] != null
				? (Strings.isNullOrEmpty(arr[22].toString().trim()) != true
						? arr[22].toString().trim() : null)
				: null;
		String userDefinedField5 = arr[23] != null
				? (Strings.isNullOrEmpty(arr[23].toString().trim()) != true
						? arr[23].toString().trim() : null)
				: null;
		String userDefinedField6 = arr[24] != null
				? (Strings.isNullOrEmpty(arr[24].toString().trim()) != true
						? arr[24].toString().trim() : null)
				: null;
		String userDefinedField7 = arr[25] != null
				? (Strings.isNullOrEmpty(arr[25].toString().trim()) != true
						? arr[25].toString().trim() : null)
				: null;
		String userDefinedField8 = arr[26] != null
				? (Strings.isNullOrEmpty(arr[26].toString().trim()) != true
						? arr[26].toString().trim() : null)
				: null;
		String userDefinedField9 = arr[27] != null
				? (Strings.isNullOrEmpty(arr[27].toString().trim()) != true
						? arr[27].toString().trim() : null)
				: null;
		String userDefinedField10 = arr[28] != null
				? (Strings.isNullOrEmpty(arr[28].toString().trim()) != true
						? arr[28].toString().trim() : null)
				: null;
		String userDefinedField11 = arr[29] != null
				? (Strings.isNullOrEmpty(arr[29].toString().trim()) != true
						? arr[29].toString().trim() : null)
				: null;
		String userDefinedField12 = arr[30] != null
				? (Strings.isNullOrEmpty(arr[30].toString().trim()) != true
						? arr[30].toString().trim() : null)
				: null;
		String userDefinedField13 = arr[31] != null
				? (Strings.isNullOrEmpty(arr[31].toString().trim()) != true
						? arr[31].toString().trim() : null)
				: null;
		String userDefinedField14 = arr[32] != null
				? (Strings.isNullOrEmpty(arr[32].toString().trim()) != true
						? arr[32].toString().trim() : null)
				: null;
		String userDefinedField15 = arr[33] != null
				? (Strings.isNullOrEmpty(arr[33].toString().trim()) != true
						? arr[33].toString().trim() : null)
				: null;

		String docKey = new StringJoiner(DOC_KEY_JOINER).add(eComGstin)
				.add(returnPeriod).add(identifier).add(documentType)
				.add(supplierGSTINorEnrolmentID).add(pos).toString();
		entity.setEcomGstin(eComGstin);
		entity.setReturnPeriod(returnPeriod);
		entity.setIdentifier(identifier);
		entity.setOriginalReturnPeriod(originalReturnPeriod);
		entity.setOriginalNetSupplies(originialNetSupplies);
		entity.setDocumentType(documentType);
		entity.setSupplyType(supplyType);
		entity.setSupplierGSTINorEnrolmentID(supplierGSTINorEnrolmentID);
		entity.setOriginalSupplierGSTINorEnrolmentID(
				originalSupplierGSTINorEnrolmentID);
		entity.setPos(pos);
		entity.setOriginalPos(originalPos);
		entity.setSuppliesToRegistered(suppliesToRegistered);
		entity.setReturnsFromRegistered(returnsFromRegistered);
		entity.setSuppliesToUnRegistered(suppliesToUnRegistered);
		entity.setReturnsFromUnRegistered(returnsFromUnRegistered);
		entity.setNetSupplies(netSupplies);
		entity.setIntegratedTaxAmount(integratedTaxAmount);
		entity.setCentralTaxAmount(centralTaxAmount);
		entity.setStateUTTaxAmount(stateUTTaxAmount);
		entity.setUserDefinedField1(userDefinedField1);
		entity.setUserDefinedField2(userDefinedField2);
		entity.setUserDefinedField3(userDefinedField3);
		entity.setUserDefinedField4(userDefinedField4);
		entity.setUserDefinedField5(userDefinedField5);
		entity.setUserDefinedField6(userDefinedField6);
		entity.setUserDefinedField7(userDefinedField7);
		entity.setUserDefinedField8(userDefinedField8);
		entity.setUserDefinedField9(userDefinedField9);
		entity.setUserDefinedField10(userDefinedField10);
		entity.setUserDefinedField11(userDefinedField11);
		entity.setUserDefinedField12(userDefinedField12);
		entity.setUserDefinedField13(userDefinedField13);
		entity.setUserDefinedField14(userDefinedField14);
		entity.setUserDefinedField15(userDefinedField15);
		entity.setErrorCode(errorCodes);
		entity.setErrorDesc(errorMessage);
		entity.setDocKey(docKey);
		entity.setFileId(fileId);
		entity.setPsd(isProceesedFlag);

		Boolean isRnv = true;
		Boolean isCanError = false;
		Boolean isBussinessProcessed = true;
		if (isProceesedFlag) {
			if (processedMap.containsKey(docKey)) {
				Gstr8Dto existingEntity = processedMap.get(docKey);
				existingEntity.setPsd(false);
				existingEntity.setErrorCode("ER8025");
				existingEntity
						.setErrorDesc("Duplicate combination with in the file");
				errorMap.computeIfAbsent(docKey,
						obj -> new ArrayList<Gstr8Dto>()).add(existingEntity);
				processedMap.put(docKey, entity);
			} else {
				if (documentType.equalsIgnoreCase("RNV")) {
					List<Gstr8UploadProcessedEntity> entities = psdRepo
							.findByGstinAndIdentifierAndDocTypeAndSgstinAndIsActive(
									eComGstin, identifier, "INV",
									supplierGSTINorEnrolmentID, true);
					if (!entities.isEmpty()) {
						int derivedReturnPeriod = GenUtil
								.getDerivedTaxPeriod(returnPeriod);
						for (Gstr8UploadProcessedEntity e : entities) {
							int derivedTaxPeriodInv = GenUtil
									.getDerivedTaxPeriod(e.getReturnPeriod());
							if (derivedTaxPeriodInv <= derivedReturnPeriod) {
								isRnv = true;
								break;
							} else {
								isRnv = false;
							}
						}
					} else {
						isRnv = false;
					}
					if (!isRnv) {
						entity.setPsd(false);
						Pair<String, String> error = populateErrorCodesAndDescription(
								"ER8020", "Document doesnot exist", entity);
						entity.setErrorCode(error.getValue0());
						entity.setErrorDesc(error.getValue1());
						isBussinessProcessed = false;
					}
				}
				if (supplyType.equalsIgnoreCase("CAN")) {
					List<Gstr8UploadProcessedEntity> entities = psdRepo
							.findByGstinAndReturnPeriodAndIdentifierAndSgstinAndDocTypeAndIsActive(
									eComGstin, returnPeriod, identifier,
									supplierGSTINorEnrolmentID, documentType,
									true);
					for (Gstr8UploadProcessedEntity e : entities) {
						if (!e.getSupplyType().equalsIgnoreCase("TAX"))
							isCanError = true;
					}
					if (isCanError) {
						entity.setPsd(false);
						Pair<String, String> error = populateErrorCodesAndDescription(
								"ER8021",
								"Document doesnot exist or already Cancelled",
								entity);
						entity.setErrorCode(error.getValue0());
						entity.setErrorDesc(error.getValue1());
						isBussinessProcessed = false;
					}

				}

				// After extracting all fields in makeProcessedEntityList

				BigDecimal suppliesToRegisteredUpdated = toBigDecimal(
						suppliesToRegistered);
				BigDecimal returnsFromRegisteredUpdated = toBigDecimal(
						returnsFromRegistered);
				BigDecimal suppliesToUnRegisteredUpdated = toBigDecimal(
						suppliesToUnRegistered);
				BigDecimal returnsFromUnRegisteredUpdated = toBigDecimal(
						returnsFromUnRegistered);
				BigDecimal netSuppliesVal = toBigDecimal(netSupplies);

				// Compute NetValues
				BigDecimal computedNetValue = suppliesToRegisteredUpdated
						.subtract(returnsFromRegisteredUpdated)
						.add(suppliesToUnRegisteredUpdated)
						.subtract(returnsFromUnRegisteredUpdated);

				// Round both to nearest rupee
				BigDecimal computedNetValueRounded = computedNetValue
						.setScale(0, BigDecimal.ROUND_HALF_UP);
				BigDecimal netSuppliesRounded = netSuppliesVal.setScale(0,
						BigDecimal.ROUND_HALF_UP);

				if (computedNetValueRounded
						.compareTo(netSuppliesRounded) != 0) {
					entity.setPsd(false);
					Pair<String, String> error = populateErrorCodesAndDescription(
							"ER8030", "NetValues Incorrectly computed.",
							entity);
					entity.setErrorCode(error.getValue0());
					entity.setErrorDesc(error.getValue1());
					isBussinessProcessed = false;
				}
				String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";

				Pattern pattern1 = Pattern.compile(regex1);
				String supplierGstin = entity.getSupplierGSTINorEnrolmentID()
						.trim();
				Matcher matcher1 = pattern1.matcher(supplierGstin);

				if (matcher1.matches() && supplierGstin.length() == 15) {
					String suppGstinChar2 = supplierGstin.substring(0, 2);
					if (suppGstinChar2.equalsIgnoreCase(pos)) {
						if ((entity.getIntegratedTaxAmount() != null)
								&& (!entity.getIntegratedTaxAmount()
										.isEmpty())) {
							entity.setPsd(false);
							Pair<String, String> error = populateErrorCodesAndDescription(
									"ER8028",
									"In case of Intra-State supply IGST amount should not be charged",
									entity);
							entity.setErrorCode(error.getValue0());
							entity.setErrorDesc(error.getValue1());
							isBussinessProcessed = false;
						}
					} else {
						if (((entity.getCentralTaxAmount() != null)
								&& (!entity.getCentralTaxAmount().isEmpty()))
								|| ((entity.getStateUTTaxAmount() != null)
										&& (!entity.getStateUTTaxAmount()
												.isEmpty()))) {
							entity.setPsd(false);
							Pair<String, String> error = populateErrorCodesAndDescription(
									"ER8029",
									"In case of Inter-State supply CGST and SGST amount should not be charged",
									entity);
							entity.setErrorCode(error.getValue0());
							entity.setErrorDesc(error.getValue1());
							isBussinessProcessed = false;
						}
					}
				} 
			}
			if (isBussinessProcessed) {
				processedMap.put(docKey, entity);
			} else {
				errorMap.computeIfAbsent(docKey,
						obj -> new ArrayList<Gstr8Dto>()).add(entity);
			}
		} else {
			errorMap.computeIfAbsent(docKey, obj -> new ArrayList<Gstr8Dto>())
					.add(entity);
		}

	}

	// Helper method
	private BigDecimal toBigDecimal(String val) {
		return (val != null && !val.isEmpty()) ? new BigDecimal(val)
				: BigDecimal.ZERO;
	}

	private Pair<String, String> populateErrorCodesAndDescription(
			String errorCode, String errordesc, Gstr8Dto entity) {

		String updatedErrorCode = (errorCode == null || errorCode.isEmpty())
				? entity.getErrorCode()
				: ((entity.getErrorCode() == null
						|| entity.getErrorCode().isEmpty()) ? errorCode
								: (entity.getErrorCode() + "," + errorCode));

		String updatedErrorDesc = (errordesc == null || errordesc.isEmpty())
				? entity.getErrorDesc()
				: ((entity.getErrorDesc() == null
						|| entity.getErrorDesc().isEmpty()) ? errordesc
								: (entity.getErrorDesc() + "," + errordesc));

		return new Pair<String, String>(updatedErrorCode, updatedErrorDesc);
	}

	private void dumpSoftDelete(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = psdRepo.updateIsDeleteFlag(chunk);
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in " + "TBL_GSTR8_UPLOAD_STAGING table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private Gstr8UploadErrorEntity convertToErrorList(Gstr8Dto dto) {

		Gstr8UploadErrorEntity entity = new Gstr8UploadErrorEntity();

		entity.setGstin(dto.getEcomGstin());
		entity.setReturnPeriod(dto.getReturnPeriod());
		entity.setIdentifier(dto.getIdentifier());
		entity.setOriginalReturnPeriod(dto.getOriginalReturnPeriod());
		entity.setOriginalNetSupplies(dto.getOriginalNetSupplies());
		entity.setDocType(dto.getDocumentType());
		entity.setSupplyType(dto.getSupplyType());
		entity.setSgstinOrEnrolId(dto.getSupplierGSTINorEnrolmentID());
		entity.setOriginalSgstinOrEnrolId(
				dto.getOriginalSupplierGSTINorEnrolmentID());
		entity.setPos(dto.getPos());
		entity.setOriginalPos(dto.getOriginalPos());
		entity.setSuppliesToRegistered(dto.getSuppliesToRegistered());
		entity.setReturnsFromRegistered(dto.getReturnsFromRegistered());
		entity.setSuppliesToUnregistered(dto.getSuppliesToUnRegistered());
		entity.setReturnsFromUnregistered(dto.getReturnsFromUnRegistered());
		entity.setNetSupplies(dto.getNetSupplies());
		entity.setIgstAmount(dto.getIntegratedTaxAmount());
		entity.setCgstAmount(dto.getCentralTaxAmount());
		entity.setSgstAmount(dto.getStateUTTaxAmount());
		entity.setUserDefinedField1(dto.getUserDefinedField1());
		entity.setUserDefinedField2(dto.getUserDefinedField2());
		entity.setUserDefinedField3(dto.getUserDefinedField3());
		entity.setUserDefinedField4(dto.getUserDefinedField4());
		entity.setUserDefinedField5(dto.getUserDefinedField5());
		entity.setUserDefinedField6(dto.getUserDefinedField6());
		entity.setUserDefinedField7(dto.getUserDefinedField7());
		entity.setUserDefinedField8(dto.getUserDefinedField8());
		entity.setUserDefinedField9(dto.getUserDefinedField9());
		entity.setUserDefinedField10(dto.getUserDefinedField10());
		entity.setUserDefinedField11(dto.getUserDefinedField11());
		entity.setUserDefinedField12(dto.getUserDefinedField12());
		entity.setUserDefinedField13(dto.getUserDefinedField13());
		entity.setUserDefinedField14(dto.getUserDefinedField14());
		entity.setUserDefinedField15(dto.getUserDefinedField15());

		entity.setDocKey(dto.getDocKey());
		entity.setFileId(dto.getFileId());
		entity.setIsActive(true);
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setIsActive(true);

		entity.setErrorCode(dto.getErrorCode());
		entity.setErrorDesc(dto.getErrorDesc());

		return entity;
	}

	private Gstr8UploadProcessedEntity convertToPsdList(Gstr8Dto dto) {

		Gstr8UploadProcessedEntity entity = new Gstr8UploadProcessedEntity();
		entity.setGstin(dto.getEcomGstin());
		entity.setFileId(dto.getFileId());
		entity.setReturnPeriod(dto.getReturnPeriod());
		entity.setIdentifier(dto.getIdentifier());
		entity.setOriginalReturnPeriod(dto.getOriginalReturnPeriod());
		String section = getSectionByBifurcation(dto.getDocumentType(),
				dto.getSupplyType(), dto.getSupplierGSTINorEnrolmentID());
		entity.setSection(section);

		BigDecimal originalNetSupplies = dto.getOriginalNetSupplies() != null
				&& !dto.getOriginalNetSupplies().isEmpty()
						? new BigDecimal(dto.getOriginalNetSupplies())
						: BigDecimal.ZERO;
		entity.setOriginalNetSupplies(originalNetSupplies);
		entity.setDocType(dto.getDocumentType());
		entity.setSupplyType(dto.getSupplyType());
		entity.setSgstin(dto.getSupplierGSTINorEnrolmentID());
		entity.setOriginalSgstinOrEnrolId(
				dto.getOriginalSupplierGSTINorEnrolmentID());
		entity.setPos(dto.getPos());
		entity.setOriginalPos(dto.getOriginalPos());
		BigDecimal suppliesToRegistered = dto.getSuppliesToRegistered() != null
				&& !dto.getSuppliesToRegistered().isEmpty()
						? new BigDecimal(dto.getSuppliesToRegistered())
						: BigDecimal.ZERO;
		entity.setSuppliesToRegistered(suppliesToRegistered);
		BigDecimal returnsFromRegistered = dto
				.getReturnsFromRegistered() != null
				&& !dto.getReturnsFromRegistered().isEmpty()
						? new BigDecimal(dto.getReturnsFromRegistered())
						: BigDecimal.ZERO;
		entity.setReturnsFromRegistered(returnsFromRegistered);
		BigDecimal SuppliesToUnRegistered = dto
				.getSuppliesToUnRegistered() != null
				&& !dto.getSuppliesToUnRegistered().isEmpty()
						? new BigDecimal(dto.getSuppliesToUnRegistered())
						: BigDecimal.ZERO;
		entity.setSuppliesToUnregistered(SuppliesToUnRegistered);
		BigDecimal returnsFromUnregistered = dto
				.getReturnsFromUnRegistered() != null
				&& !dto.getReturnsFromUnRegistered().isEmpty()
						? new BigDecimal(dto.getReturnsFromUnRegistered())
						: BigDecimal.ZERO;
		entity.setReturnsFromUnregistered(returnsFromUnregistered);
		BigDecimal netSupplies = dto.getNetSupplies() != null
				&& !dto.getNetSupplies().isEmpty()
						? new BigDecimal(dto.getNetSupplies())
						: BigDecimal.ZERO;
		entity.setNetSupplies(netSupplies);
		BigDecimal igstAmount = dto.getIntegratedTaxAmount() != null
				&& !dto.getIntegratedTaxAmount().isEmpty()
						? new BigDecimal(dto.getIntegratedTaxAmount())
						: BigDecimal.ZERO;
		entity.setIgstAmount(igstAmount);
		BigDecimal cgstAmount = dto.getCentralTaxAmount() != null
				&& !dto.getCentralTaxAmount().isEmpty()
						? new BigDecimal(dto.getCentralTaxAmount())
						: BigDecimal.ZERO;
		entity.setCgstAmount(cgstAmount);
		BigDecimal sgstAmount = dto.getStateUTTaxAmount() != null
				&& !dto.getStateUTTaxAmount().isEmpty()
						? new BigDecimal(dto.getStateUTTaxAmount())
						: BigDecimal.ZERO;
		entity.setSgstAmount(sgstAmount);
		entity.setUserDefinedField1(dto.getUserDefinedField1());
		entity.setUserDefinedField2(dto.getUserDefinedField2());
		entity.setUserDefinedField3(dto.getUserDefinedField3());
		entity.setUserDefinedField4(dto.getUserDefinedField4());
		entity.setUserDefinedField5(dto.getUserDefinedField5());
		entity.setUserDefinedField6(dto.getUserDefinedField6());
		entity.setUserDefinedField7(dto.getUserDefinedField7());
		entity.setUserDefinedField8(dto.getUserDefinedField8());
		entity.setUserDefinedField9(dto.getUserDefinedField9());
		entity.setUserDefinedField10(dto.getUserDefinedField10());
		entity.setUserDefinedField11(dto.getUserDefinedField11());
		entity.setUserDefinedField12(dto.getUserDefinedField12());
		entity.setUserDefinedField13(dto.getUserDefinedField13());
		entity.setUserDefinedField14(dto.getUserDefinedField14());
		entity.setUserDefinedField15(dto.getUserDefinedField15());
		entity.setCreatedDate(LocalDateTime.now());
		entity.setDocKey(dto.getDocKey());
		entity.setIsActive(true);
		return entity;
	}

	private String getSectionByBifurcation(String documentType,
			String supplyType, String supplierGstinOrEnrolmentID) {
		String section = "";

		String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";
		Pattern pattern1 = Pattern.compile(regex1);
		String supplierGstin = supplierGstinOrEnrolmentID;
		Matcher matcher1 = pattern1.matcher(supplierGstin);
		String regex2 = "^[0-9]{12}[E][S][0-9a-zA-Z]{1}$";
		Pattern pattern2 = Pattern.compile(regex2);
		Matcher matcher2 = pattern2.matcher(supplierGstin);

		if (matcher1.matches()) {
			if (documentType.equalsIgnoreCase("INV")
					&& (supplyType.equalsIgnoreCase("TAX")
							|| supplyType.equalsIgnoreCase("CAN")))
				return "TCS";
			if (documentType.equalsIgnoreCase("RNV")
					&& supplyType.equalsIgnoreCase("TAX")
					|| supplyType.equalsIgnoreCase("CAN"))
				return "TCSA";
		} else if (matcher2.matches()) {
			if (documentType.equalsIgnoreCase("INV")
					&& supplyType.equalsIgnoreCase("TAX")
					|| supplyType.equalsIgnoreCase("CAN"))
				return "URD";
			if (documentType.equalsIgnoreCase("RNV")
					&& supplyType.equalsIgnoreCase("TAX")
					|| supplyType.equalsIgnoreCase("CAN"))
				return "URDA";
		}

		return section;

	}
}
