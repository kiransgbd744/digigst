package com.ey.advisory.app.gstr1.einv;

/**
 * @author siva.reddy
 *
 */

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.Gstr1GSTINDeleteDataEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1GSTINDeleteDataRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
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

import lombok.extern.slf4j.Slf4j;

@Service("Gstr1GSTINDeleteDataServiceImpl")
@Slf4j
public class Gstr1GSTINDeleteDataServiceImpl
		implements Gstr1GSTINDeleteDataService {

	protected static final String[] EXPECTED_HEADERS = { "SupplierGSTIN",
			"ReturnPeriod", "DocumentType", "DocumentNumber", "DocumentDate",
			"CustomerGSTIN", "POS", "TableType" };

	private static final String UPLOAD = "Upload";

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository StatecodeRepository;

	@Autowired
	ValidationUtility validationUtility;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1GSTINDeleteDataRepository")
	private Gstr1GSTINDeleteDataRepository repo;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	String msg = null;

	private InputStream getFileInpStream(String fileName, String folderName,
			Long fileId) {

		InputStream inputStream = null;

		Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = fileStatusRepository
				.findById(fileId);

		if (!gstr1FileStatusEntity.isPresent()) {
			String errMsg = String.format(
					"No Record available for the file Name %s", fileName);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession:{} ", openCmisSession);
			}
			String docId = gstr1FileStatusEntity.get().getDocId();
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = gstr1FileUploadUtil.getDocument(openCmisSession,
						fileName, folderName);
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
		} catch (Exception e) {
			LOGGER.error("Exception occured in SR File Arrival Processor", e);
			throw new AppException(
					"Error occured while " + "reading the file " + fileName, e);

		}

		return inputStream;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = " Inside Validation Headers for File Id "
						+ "method for File Id" + fileId;
				LOGGER.debug(msg);
			}
			InputStream fin = getFileInpStream(fileName, folderName,fileId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 8);

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

			if (actualHeaderNames.size() != 8) {
				String msg = "The number of columns in the file should be 8. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = loadExpectedHeaders();

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
				LOGGER.debug(msg);
				markFileAsFailed(fileId, msg);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file for " + fileId;
			LOGGER.debug(msg);
			fileStatusRepository.updateErrorFieNameById(fileId, msg);
			markFileAsFailed(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private void markFileAsFailed(Long fileId, String reason) {
		try {
			fileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = "[SEVERE] Unable to mark the file as failed. "
					+ "Reason for file failure is: [" + reason + "]";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private synchronized List<String> loadExpectedHeaders() {
		return Arrays.asList(EXPECTED_HEADERS);
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

	@Override
	public void validateandSaveFileData(Long fileId, String fileName,
			String folderName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside validateandSaveFileData Headers for File Id "
					+ "method for File Id" + fileId;
			LOGGER.debug(msg);
		}
		try {
			validateHeaders(fileName, folderName, fileId);
			InputStream fin = getFileInpStream(fileName, folderName,fileId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(8);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(fin, layout, rowHandler, null);

			List<Object[]> fileList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			LOGGER.error(fileList.toString());

			if (fileList.isEmpty() || fileList == null) {
				String msg = "Failed Empty file..";
				LOGGER.error(msg);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
				throw new AppException(msg);
			}

			validation(fileList, fileId);
			// repo.saveAll(entityList);
			if (LOGGER.isDebugEnabled()) {
				String msg = "Validation is Done for File Id "
						+ "method for File Id" + fileId;
				LOGGER.debug(msg);
			}

		} catch (Exception e) {

			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, e);
			fileStatusRepository.updateFileStatus(fileId, "failed");
			fileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw new AppException(msg, e);
		}

	}

	private void validation(List<Object[]> fileList, Long fileId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Gstr1GSTINDeleteData Validation "
					+ "method for File Id" + fileId;
			LOGGER.debug(msg);
		}
		List<ProcessingResult> validationResult = null;

		List<Gstr1GSTINDeleteDataEntity> processedEntityList = new ArrayList<>();

		List<String> dockeysToBeSoftDelete = new ArrayList<>();
		Integer errorCount = 0;
		Integer psdCount = 0;
		String createdUser = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		Map<String, Gstr1GSTINDeleteDataEntity> processedMap = new HashMap<>();

		Map<String, List<Gstr1GSTINDeleteDataEntity>> errorMap = new HashMap<>();

		try {
			for (Object[] rowData : fileList) {
				validationResult = new ArrayList<>();
				String errorMessage = null;
				String errorCodes = null;
				rowDataValidation(validationResult, rowData);

				List<String> errorDesList = validationResult.stream()
						.filter(result -> result.getDescription() != null)
						.map(ProcessingResult::getDescription)
						.collect(Collectors.toList());

				List<String> errorCodeList = validationResult.stream()
						.filter(result -> result.getCode() != null)
						.map(ProcessingResult::getCode)
						.collect(Collectors.toList());

				String[] stringArray = new String[8];

				for (int i = 0; i < stringArray.length; i++) {
					if (i < rowData.length) {
						stringArray[i] = rowData[i] == null ? null
								: rowData[i].toString();
					}
				}

				if (!CollectionUtils.isEmpty(errorDesList))
					errorMessage = String.join(",", errorDesList);
				if (!CollectionUtils.isEmpty(errorCodeList))
					errorCodes = String.join(",", errorCodeList);

				if (validationResult.isEmpty()) {
					makeProcessedEntityList(stringArray, processedMap, errorMap,
							fileId, true, errorCodes, errorMessage,
							dockeysToBeSoftDelete, createdUser);
				} else {
					makeProcessedEntityList(stringArray, processedMap, errorMap,
							fileId, false, errorCodes, errorMessage,
							dockeysToBeSoftDelete, createdUser);
				}
			}

			List<List<String>> chunks = Lists.partition(dockeysToBeSoftDelete,
					2000);
			for (List<String> chunk : chunks) {
				LOGGER.debug("Inside Chunk Method");
				repo.updateIsDeleted(chunk);
			}

			processedMap.forEach((k, v) -> processedEntityList.add(v));
			errorMap.forEach((k, v) -> processedEntityList.addAll(v));

			if (!processedEntityList.isEmpty()) {
				repo.saveAll(processedEntityList);
			}

			List<Gstr1GSTINDeleteDataEntity> psdCountEntity = repo
					.findByFileIdAndIsProcessedTrue(fileId.intValue());
			psdCount = psdCountEntity.size();
			errorCount = processedEntityList.size() - psdCountEntity.size();
			fileStatusRepository.updateCountSummary(fileId,
					processedEntityList.size(), psdCount, errorCount);

			fileStatusRepository.updateFileStatus(fileId, "Processed");

		} catch (Exception e) {
			String msg = "Failed, error while validating file.";
			LOGGER.error(msg, e);
			fileStatusRepository.updateFileStatus(fileId, "failed");
			fileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw new AppException(msg, e);
		}
	}

	private void makeProcessedEntityList(String[] stringArray,
			Map<String, Gstr1GSTINDeleteDataEntity> processedMap,
			Map<String, List<Gstr1GSTINDeleteDataEntity>> errorMap, Long fileId,
			boolean isProceesedFlag, String errorCodes, String errorMessage,
			List<String> dockeysToBeSoftDelete, String createdUser) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside makeProcessedEntityList Method for File Id "
					+ fileId;
			LOGGER.debug(msg);
		}
		String sGstin = stringArray[0] != null ? stringArray[0].trim() : null;
		String returnPeriod = stringArray[1] != null ? stringArray[1].trim()
				: null;
		String docType = stringArray[2] != null ? stringArray[2].trim() : null;
		String docNum = stringArray[3] != null ? stringArray[3].trim() : null;
		String docDate = stringArray[4] != null ? stringArray[4].trim() : null;
		String cgstin = stringArray[5] != null ? stringArray[5].trim() : null;
		String pos = stringArray[6] != null ? stringArray[6].trim() : null;
		String tableType = stringArray[7] != null ? stringArray[7].trim()
				: null;

		Gstr1GSTINDeleteDataEntity processedEntity = new Gstr1GSTINDeleteDataEntity();
		String finYear = GenUtil.getFinYear(DateUtil.parseObjToDate(docDate));
		String docKey = ValidationUtility.createDocKey(sGstin, docType, docNum,
				finYear);
		List<Gstr1GSTINDeleteDataEntity> isDocKeyAvailable = repo
				.findDocsByDocKey(docKey);
		if (!isDocKeyAvailable.isEmpty()) {
			dockeysToBeSoftDelete.add(docKey);
		}
		processedEntity.setSgstin(sGstin);
		processedEntity.setReturnPeriod(returnPeriod);
		processedEntity.setDocumentType(docType);
		processedEntity.setDocumentNumber(docNum);
		processedEntity.setDocumentDate(DateUtil.parseObjToDate(docDate));
		processedEntity.setCgstin(cgstin);
		processedEntity.setPos(pos);
		processedEntity.setTableType(tableType);
		processedEntity.setFileId(fileId.intValue());
		processedEntity.setDocKey(docKey);
		if (finYear != null && !finYear.isEmpty()) {
			processedEntity.setFY(Integer.valueOf(finYear));
		}
		processedEntity.setProcessed(isProceesedFlag);
		processedEntity.setErrorCode(errorCodes);
		processedEntity.setErrorDesc(errorMessage);
		processedEntity.setCreatedUser(createdUser);
		processedEntity.setCreatedDate(LocalDateTime.now());
		if (isProceesedFlag) {
			if (processedMap.containsKey(docKey)) {
				Gstr1GSTINDeleteDataEntity existingEntity = processedMap
						.get(docKey);
				existingEntity.setProcessed(false);
				existingEntity.setErrorCode("ER-108");
				existingEntity.setErrorDesc("Duplicate Invoice");
				errorMap.computeIfAbsent(docKey,
						obj -> new ArrayList<Gstr1GSTINDeleteDataEntity>())
						.add(existingEntity);
				processedMap.put(docKey, processedEntity);
			} else {
				processedMap.put(docKey, processedEntity);
			}
		} else {
			errorMap.computeIfAbsent(docKey,
					obj -> new ArrayList<Gstr1GSTINDeleteDataEntity>())
					.add(processedEntity);
		}
	}

	private void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData) {
		LOGGER.debug("Inside Row Validation Method");
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Row Validation Method";
			LOGGER.debug(msg);
		}

		isSupplierGstinValid(rowData, validationResult);

		isReturnPeriod(rowData, validationResult);

		isDocumentTypeValid(rowData, validationResult);

		isDocumentNumberValid(rowData, validationResult);

		isDocumentDateValid(rowData, validationResult);

		isCustomerGstinValid(rowData, validationResult);

		isPOS(rowData, validationResult);

		isTableType(rowData, validationResult);

	}

	private void isSupplierGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 0;
		String errorCode = "ER-101";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("SupplierGstin cannot be blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		String sGstin = rowData[index].toString().trim();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Supplier GSTIN Validation Method";
			LOGGER.debug(msg);
		}

		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(rowData[index].toString());

		if (!matcher.matches() || sGstin.length() != 15) {
			String errMsg = String.format("Invalid SupplierGstin(%s).", sGstin);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (sGstin.length() > 100) {
				rowData[0] = rowData[0].toString().substring(0, 100);
			}
			return;
		}

		if (gSTNDetailRepository.findgstin(rowData[index].toString()) == 0) {
			String errMsg = String.format(
					"SupplierGstin(%s) is not available " + "in DigiGST.",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
	}

	private void isReturnPeriod(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 1;
		String errorCode = "ER-102";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Return Period cannot be blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		String returnPeriod = rowData[index].toString().trim();
		if (returnPeriod.contains("'")) {
			String returnPeriodWithOutQuotes = returnPeriod.replace("'", "");
			rowData[index] = returnPeriodWithOutQuotes;
			returnPeriod = returnPeriodWithOutQuotes;
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Return Period Validation Method for";
			LOGGER.debug(msg);
		}
		if (returnPeriod.length() > 6) {
			String errMsg = String.format("Return Period(%s) is not valid.",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (returnPeriod.length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
			return;
		}

		if (!returnPeriod.matches("[0-9]+") || returnPeriod.length() != 6) {
			String errMsg = String.format("Return Period(%s) is not valid.",
					returnPeriod);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		if (returnPeriod.matches("[0-9]+")) {

			int month = Integer.valueOf(returnPeriod.substring(0, 2));
			if (returnPeriod.length() != 6 || month > 12 || month == 00) {
				String errMsg = String.format("Return Period(%s) is not valid.",
						returnPeriod);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
	}

	private void isDocumentTypeValid(Object[] rowData,
			List<ProcessingResult> validationResult) {
		int index = 2;
		String errorCodeBlank = "ER-103";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Document Type Validation Method";
			LOGGER.debug(msg);
		}
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Document Type cannot be blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		String docType = rowData[index].toString().trim();

		if (docType.length() > 6) {
			String errMsg = String.format("Invalid DocumentType(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			if (docType.length() > 100) {
				rowData[2] = rowData[2].toString().substring(0, 100);
			}
			return;
		}

		if (!Stream.of("INV", "DR", "CR").anyMatch(docType::equals)) {
			String errMsg = String.format("Invalid DocumentType(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isDocumentNumberValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 3;
		String errorCodeBlank = "ER-104";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Document Number Validation Method";
			LOGGER.debug(msg);
		}
		if (!isPresent(rowData[index])) {

			String errMsg = String.format("DocumentNumber cannot be empty.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}
		String docNo = rowData[3].toString().trim();
		if (docNo.contains("'")) {
			String docWithOutQuotes = docNo.replace("'", "");
			rowData[index] = docWithOutQuotes;
			docNo = docWithOutQuotes;
		}

		if (docNo.length() > 16) {

			String errMsg = String.format("Invalid DocumentNumber(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			if (docNo.length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
			return;
		}

		String regex = "^[a-zA-Z0-9/-]*$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(docNo);
		if (!matcher.matches()) {
			String errMsg = String.format("Invalid DocumentNumber(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isDocumentDateValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 4;
		String errorCodeBlank = "ER-105";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Document Date Validation Method";
			LOGGER.debug(msg);
		}
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("DocumentDate cannot be empty.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(rowData[index].toString().trim());

		if (date == null) {
			String errMsg = String.format("Invalid DocumentDate(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			rowData[4] = null;
			return;
		}
	}

	private void isCustomerGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Customer GSTIN Validation Method";
			LOGGER.debug(msg);
		}
		if (rowData[7] != null && Stream.of("B2B", "CDNR")
				.anyMatch(rowData[7].toString().trim()::equals)) {

			int index = 5;
			String errorCode = "ER-106";

			if (!isPresent(rowData[index])) {
				String errMsg = String.format("CustomerGSTIN cannot be blank.");
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}

			String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(rowData[index].toString());

			Pattern pattern1 = Pattern.compile(regex1);
			Matcher matcher1 = pattern1.matcher(rowData[index].toString());

			String cgstin = rowData[index].toString().trim();
			if (cgstin.length() != 15) {
				String errMsg = String.format("Invalid CustomerGSTIN(%s).",
						cgstin);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				if (cgstin.length() > 100) {
					rowData[5] = rowData[5].toString().substring(0, 100);
				}
				return;
			}
		}
	}

	private void isPOS(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 6;
		String errorCode = "ER-108";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside POS Validation Method";
			LOGGER.debug(msg);
		}
		if (!isPresent(rowData[index])) {
			return;
		}

		String pos = rowData[index].toString().trim();
		if (pos.contains("'")) {
			String posWithOutQuotes = pos.replace("'", "");
			rowData[index] = posWithOutQuotes;
			pos = posWithOutQuotes;
		}

		if (pos.length() > 2) {
			String errMsg = String.format("Invalid POS(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (pos.length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
			return;
		}

		if (StatecodeRepository.findStateCodeCount(pos) == 0) {
			String errMsg = String.format(
					"POS(%s) is not Available in DigiGST.",
					rowData[index].toString().trim());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
	}

	private void isTableType(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 7;
		String errorCode = "ER-107";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Table Type Validation Method";
			LOGGER.debug(msg);
		}
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Table type can't be left blank");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		String tableType = rowData[index].toString().trim();
		if (tableType.length() > 20) {
			String errMsg = String.format("Invalid Table Type(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (tableType.length() > 100) {
				rowData[7] = rowData[7].toString().substring(0, 100);
			}
			return;
		}

		if (!Stream
				.of("B2B", "EXPT", "EXPWT", "CDNUR_EXPT", "CDNUR_EXPWT", "CDNR")
				.anyMatch(tableType::equals)) {
			String errMsg = String.format("Invalid Table Type(%s).", tableType);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
	}

}
