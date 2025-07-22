/**
 * 
 */
package com.ey.advisory.app.gstr1.einv;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1VsEInvExcSaveRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
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

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr1VsEInvEecSaveArrivalService")
public class Gstr1VsEInvEecSaveArrivalService {

	private static List<String> HEADER_LIST = null;
	private static final String UPLOAD = "Upload";

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docHeaderRepo;

	@Autowired
	@Qualifier("Gstr1VsEInvExcSaveRepository")
	private Gstr1VsEInvExcSaveRepository saveRepo;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

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
			InputStream fin = getFileInpStream(fileName, folderName,fileId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 6);

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

			if (actualHeaderNames.size() != 6) {
				String msg = "The number of columns in the file should be 6. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
				LOGGER.equals(msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = loadExpectedHeaders(fileId);

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
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
				LOGGER.equals(msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			markFileAsFailed(fileId, msg);
			if (msg.length() > 200)
				msg = msg.substring(0, 200);

			fileStatusRepository.updateErrorFieNameById(fileId, msg);
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
			if (msg.length() > 200)
				msg = msg.substring(0, 200);
			fileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw new AppException(msg, ex);
		}
	}

	public void validateResponseFile(Long fileId, String fileName,
			String folderName) {

		LOGGER.debug("Validating user uploaded header with template header");

		try {
			// reading row data
			validateHeaders(fileName, folderName, fileId);
			InputStream fin = getFileInpStream(fileName, folderName,fileId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(6);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(fin, layout, rowHandler, null);

			List<Object[]> fileList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			if (fileList.isEmpty() || fileList == null) {

				String msg = "Failed Empty file..";
				LOGGER.error(msg);
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
				throw new AppException(msg);

			}
			Integer errorCount = 0;
			Integer psdCount = 0;

			// validation and adding error code
			Triplet<List<Gstr1VsEInvExcSaveEntity>, Integer, Integer> entityTriplet = validation(
					fileList, fileId, errorCount, psdCount);
			List<Gstr1VsEInvExcSaveEntity> entityList = entityTriplet
					.getValue0();
			psdCount = entityTriplet.getValue1();
			errorCount = entityTriplet.getValue2();

			List<String> docToBeSaved = new ArrayList<>();
			List<String> docToBeSkiped = new ArrayList<>();

			List<String> docKeyList = entityList.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			List<Object[]> totalDocKeys = getChuckedDocKey(docKeyList);

			Map<String, Integer> dockeyStausMap = totalDocKeys.stream()
					.collect(Collectors.toMap(o -> (String) o[0],
							o -> (Integer) o[1], (o1, o2) -> o2));

			for (Gstr1VsEInvExcSaveEntity entity : entityList) {

				if (entity.isPsd()) {

					if (!dockeyStausMap.containsKey(entity.getDocKey())
							|| dockeyStausMap.get(entity.getDocKey()) == 1) {

						entity.setErrorCode("ER-107");
						entity.setErrorDesc("Document does not exists in "
								+ "DigiGST Processed Data");
						entity.setPsd(false);

						errorCount++;
						psdCount--;
						continue;
					}
					if (entity.getResp().equalsIgnoreCase("N")) {
						docToBeSkiped.add(entity.getDocKey());

					} else {
						docToBeSaved.add(entity.getDocKey());
					}

				}
			}

			updateUserResp(docToBeSaved, false);
			updateUserResp(docToBeSkiped, true);

			LOGGER.debug("updated user response fileId {} ", fileId);

			// making isDelete flag true before saving in table
			LOGGER.debug("about to SoftDelete fileId {} ", fileId);
			softDelete(docKeyList);

			LOGGER.debug("SoftDeleted fileId {} ", fileId);

			// saving into db table
			LOGGER.debug("about to save fileId {} ", fileId);
			saveRepo.saveAll(entityList);
			LOGGER.debug("saved fileId {} ", fileId);

			LOGGER.debug("About to update count summry for fileId {}", fileId);
			// update error and psd counts
			fileStatusRepository.updateCountSummary(fileId, entityList.size(),
					psdCount, errorCount);
			LOGGER.debug("updated count summry for fileId {}, About to "
					+ "update the user response", fileId);
			fileStatusRepository.updateFileStatus(fileId, "Processed");

		} catch (Exception ex) {

			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			fileStatusRepository.updateFileStatus(fileId, "failed");
			if (msg.length() > 200)
				msg = msg.substring(0, 200);
			fileStatusRepository.updateErrorFieNameById(fileId, msg);
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

	private synchronized List<String> loadExpectedHeaders(Long fileId) {

		try {
			// Load the excel sheet and return the list of headers
			// using the traverseHeaderOnly method.
			if (HEADER_LIST == null) {
				HEADER_LIST = loadExpectedHeadersFromTemplate();
			}

			return HEADER_LIST;
		} catch (Exception ex) {
			String msg = "Failed to read the headers from the template file.";
			LOGGER.error(msg, ex);
			if (msg.length() > 200)
				msg = msg.substring(0, 200);

			fileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw new AppException(msg, ex);
		}
	}

	private List<String> loadExpectedHeadersFromTemplate()
			throws FileNotFoundException {

		ClassLoader classLoader = this.getClass().getClassLoader();
		URL template_Dir = classLoader.getResource("ReportTemplates/");
		String templatePath = template_Dir.getPath()
				+ "Response on DigiGST PSD Data.csv";
		FileInputStream fin = new FileInputStream(new File(templatePath));

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser("Response on DigiGST PSD Data.csv");
		TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 6);

		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();

		traverser.traverseHeaderOnly(fin, layout, rowHandler, null);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> templateHeaderNames = new ArrayList(
				Arrays.asList(rowHandler.getHeaderRow()));

		return templateHeaderNames;
	}

	private Triplet<List<Gstr1VsEInvExcSaveEntity>, Integer, Integer> validation(
			List<Object[]> fileList, Long fileId, Integer errorCount,
			Integer psdCount) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Gstr1VsEInvEecSaveArrivalService "
					+ "Validation method ";
			LOGGER.debug(msg);
		}
		List<ProcessingResult> validationResult = null;

		List<Gstr1VsEInvExcSaveEntity> processedEntityList = new ArrayList<>();

		// gstin ifno list

		List<String> gstinList = gSTNDetailRepository.findAllGstin();
		
		Map<String, Gstr1VsEInvExcSaveEntity> processedMap = new HashMap<>();

		Map<String, List<Gstr1VsEInvExcSaveEntity>> errorMap = new HashMap<>();

		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();
			String errorMessage = null;
			String errorCodes = null;

			rowDataValidation(validationResult, rowData, gstinList);

			List<String> errorArrayList = validationResult.stream()
					.filter(result -> result.getDescription() != null)
					.map(ProcessingResult::getDescription)
					.collect(Collectors.toList());

			List<String> errorCodeList = validationResult.stream()
					.filter(result -> result.getCode() != null)
					.map(ProcessingResult::getCode)
					.collect(Collectors.toList());

			String[] stringArray = new String[6];

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
				//psdCount++;

			} else {
				makeProcessedEntityList(stringArray, processedMap, errorMap,
						fileId, false, errorCodes, errorMessage);
				//errorCount++;
			}
		}
		
		processedMap.forEach((k, v) -> processedEntityList.add(v));
		errorMap.forEach((k, v) -> processedEntityList.addAll(v));

		
		List<Gstr1VsEInvExcSaveEntity> psdList = processedEntityList.stream()
				.filter(result -> result.isPsd())
				.collect(Collectors.toList());
		
		psdCount = psdList.size();
		errorCount = processedEntityList.size()-psdCount;

		return new Triplet<>(processedEntityList, psdCount, errorCount);
	}

	private void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData, List<String> gstinList) {

		isSupplierGstinValid(rowData, validationResult, gstinList);

		isReturnPeriod(rowData, validationResult);

		isDocType(rowData, validationResult);

		isDocumentNumberValid(rowData, validationResult);

		isDocumentDateValid(rowData, validationResult);

		isResponseValid(rowData, validationResult);

	}
	// SupplierGSTIN ReturnPeriod DocumentType DocumentNumber DocumentDate
	// Response

	private void isSupplierGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, List<String> gstinList) {
		int index = 0;
		String errorCode = "ER-101";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("SupplierGstin cannot be blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(rowData[index].toString());

		String vendorGstin = rowData[index].toString().trim();
		if (!matcher.matches() || vendorGstin.length() != 15) {
			String errMsg = String.format("Invalid SupplierGstin(%s).",
					vendorGstin);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);

			validationResult.add(result);
			if (rowData[0].toString().length() > 100)
				rowData[0] = rowData[0].toString().substring(0, 100);
			return;
		}
		if (!gstinList.contains(rowData[index].toString())) {

			String errMsg = String.format(
					"SupplierGstin(%s) is not available " + "in DigiGST.",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isDocumentNumberValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 3;
		String errorCodeBlank = "ER-104";

		if (!isPresent(rowData[index])) {

			String errMsg = String.format("DocumentNumber cannot be blank.");
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
			if (docNo.length() > 100)
				rowData[index] = rowData[index].toString().substring(0, 100);
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
		}
	}

	private void isDocumentDateValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 4;
		String errorCodeBlank = "ER-105";

		if (!isPresent(rowData[index])) {
			String errMsg = String.format("DocumentDate cannot be blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(rowData[index].toString().trim());

		if (date == null) {
			String errMsg = String.format("Invalid DocumentDate. %s",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			rowData[4] = null;
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
		if (returnPeriod.length() > 6) {
			String errMsg = String.format("Invalid Return Period(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (rowData[index].toString().length() > 100)
				rowData[index] = rowData[index].toString().substring(0, 100);
			return;
		}

		if (!returnPeriod.matches("[0-9]+") || returnPeriod.length() != 6) {
			String errMsg = String.format("Invalid Return Period(%s).",
					returnPeriod);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		if (returnPeriod.matches("[0-9]+")) {

			int month = Integer.valueOf(returnPeriod.substring(0, 2));
			if (returnPeriod.length() != 6 || month > 12 || month == 00) {
				String errMsg = String.format("Invalid Return Period(%s).",
						returnPeriod);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}
	}

	private void isDocType(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 2;
		String errorCode = "ER-103";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Document Type cannot be blank");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		String docType = rowData[index].toString();

		if (docType.length() > 6) {
			String errMsg = String.format("Invalid Document Type(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (rowData[2].toString().length() > 100)
				rowData[2] = rowData[2].toString().substring(0, 100);
			return;
		}

		String tableType = rowData[index].toString().trim();
		if (!Stream.of("INV", "CR", "DR").anyMatch(tableType::equals)) {
			String errMsg = String.format("Invalid Document Type(%s).",
					tableType);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
	}

	private void isResponseValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		int index = 5;
		String errorCode = "ER-106";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Response cannot be blank");
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
		if (rowData[index].toString().length() > 1) {
			String errMsg = String.format("Invalid Response Type(%s).",
					rowData[index].toString());
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			if (rowData[5].toString().length() > 100)
				rowData[5] = rowData[5].toString().substring(0, 100);
			return;
		}

		String response = rowData[index].toString().trim();
		if (!Stream.of("N", "S").anyMatch(response::equals)) {
			String errMsg = String.format("Invalid Response Type(%s).",
					response);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
			return;
		}
	}

	String userName = SecurityContext.getUser() != null
			? (SecurityContext.getUser().getUserPrincipalName() != null
					? SecurityContext.getUser().getUserPrincipalName()
					: "SYSTEM")
			: "SYSTEM";

	private void makeProcessedEntityList(String[] arr,
			Map<String, Gstr1VsEInvExcSaveEntity> processedMap,
			Map<String, List<Gstr1VsEInvExcSaveEntity>> errorMap, Long fileId,
			boolean isProceesedFlag, String errorCodes, String errorMessage) {

		Gstr1VsEInvExcSaveEntity entity = new Gstr1VsEInvExcSaveEntity();

		String sGstin = arr[0] != null ? arr[0].toString().trim() : null;
		String taxPeriod = arr[1] != null ? arr[1].toString().trim() : null;
		String docType = arr[2] != null ? arr[2].toString().trim() : null;
		String docNum = arr[3] != null ? arr[3].toString().trim() : null;
		String docDate = arr[4] != null ? arr[4].toString().trim() : null;

		String fy = GenUtil.getFinYear(DateUtil.parseObjToDate(docDate));

		String resp = arr[5] != null ? arr[5].toString().trim() : null;

		entity.setDelete(false);
		entity.setDocDate(DateUtil.parseObjToDate(docDate));
		// entity.setDocDate(DateUtil.parseObjToDate(docDate));
		String docKey = ValidationUtility.createDocKey(sGstin, docType, docNum,
				fy.toString());
		entity.setDocKey(docKey);
		entity.setDocNum(docNum);
		entity.setDocType(docType);
		entity.setErrorCode(errorCodes);
		entity.setErrorDesc(errorMessage);
		entity.setFileId(fileId.intValue());
		if (fy != null && !fy.isEmpty())
			entity.setFy(Integer.valueOf(fy));
		entity.setPsd(isProceesedFlag);
		entity.setResp(resp);
		entity.setSGstin(sGstin);
		entity.setTaxPeriod(taxPeriod);
		entity.setCreatedBy(userName);
		entity.setCreateDate(LocalDateTime.now());
		if (isProceesedFlag) {
			if (processedMap.containsKey(docKey)) {
				Gstr1VsEInvExcSaveEntity existingEntity = processedMap
						.get(docKey);
				existingEntity.setPsd(false);
				existingEntity.setErrorCode("ER-108");
				existingEntity.setErrorDesc("Duplicate Invoice");
				errorMap.computeIfAbsent(docKey,
						obj -> new ArrayList<Gstr1VsEInvExcSaveEntity>())
						.add(existingEntity);
				processedMap.put(docKey, entity);
			} else {
				processedMap.put(docKey, entity);
			}
		} else {
			errorMap.computeIfAbsent(docKey,
					obj -> new ArrayList<Gstr1VsEInvExcSaveEntity>())
					.add(entity);
		}

	}

	private void updateUserResp(List<String> docKeys, Boolean userResp) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = docHeaderRepo
					.updateIsSavetoGstnAndIsSendToGstnIn(userResp, userResp,
							chunk);
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in " + "ANX_OUTWARD_DOC_HEADER table ",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private void softDelete(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = saveRepo.updateIsDeleteFlag(chunk);
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in "
							+ "GSTR1_GSTN_SAVE_RESPONSE_DATA table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private List<Object[]> getChuckedDocKey(List<String> docKeys) {

		List<Object[]> totalDocKeys = new ArrayList<>();

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");

			List<Object[]> chunkedDockeys = docHeaderRepo
					.findAspInvoiceStatusByDocKeys(chunk);

			String msg = String.format(" %s dockeys in docheader table ",
					chunks);
			LOGGER.debug(msg);
			totalDocKeys.addAll(chunkedDockeys);

		}
		return totalDocKeys;
	}

}
