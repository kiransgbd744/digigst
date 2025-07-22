package com.ey.advisory.service.gstr1.sales.register;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1StagingRegisterRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.docs.gstr2a.Gstr1FileProcessService;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Service("Gstr1SalesRegisterFileArrivalHandler")
public class Gstr1SalesRegisterFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SalesRegisterFileArrivalHandler.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileProcessService")
	private Gstr1FileProcessService gstr1FileProcessService;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("Gstr1StagingRegisterRepository")
	private Gstr1StagingRegisterRepository repo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1SalesRegisterStructuralChainValidation")
	private Gstr1SalesRegisterStructuralChainValidation structuralValidation;

	private static String[] headerArray = { "ReturnPeriod", "BusinessPlace",
			"CustomerGSTIN", "DocumentType", "SupplyType", "DocumentNumber",
			"DocumentDate", "ItemSerialNumber", "HSN/SAC", "TaxRate",
			"ItemAssessableValue", "IGST", "CGST", "SGST", "AdvaloremCess",
			"SpecificCess", "InvoiceValue", "POS", "TransactionType" };
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

		try {
			// reading row data

			validateHeaders(fileName, fileFolder, fileId);
			Document document = DocumentUtility.downloadDocument(fileName,
					fileFolder);
			if (document == null) {
				String errMsg = String.format(
						"Unable to find the file %s in folder %s", fileName,
						fileFolder);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			InputStream fin = document.getContentStream().getStream();
			TabularDataLayout layout = new DummyTabularDataLayout(19);
			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			traverser.traverse(fin, layout, rowHandler, null);
			List<Object[]> fileList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();
			if (fileList.isEmpty() || fileList == null) {

				String msg1 = "Failed Empty file..";
				gstr1FileStatusRepository.updateErrorFieNameById(fileId, msg1);
				throw new AppException(msg1);

			}
			// saving to varchar table
			List<String> docKeyList = fileList.stream().map(o -> getDocKey(o))
					.collect(Collectors.toList());

			// dump softDelete
			// dumpSoftDelete(docKeyList);

			Integer errorCount = 0;
			Integer psdCount = 0;

			// validation and adding error code
			Triplet<List<StagingSalesRegisterUploadDto>, Integer, Integer> entityTriplet = validation(
					fileList, fileId, errorCount, psdCount, docKeyList);

			List<StagingSalesRegisterUploadDto> dtoList = entityTriplet
					.getValue0();
			psdCount = entityTriplet.getValue1();
			errorCount = entityTriplet.getValue2();
			LOGGER.debug("About to update count summry for dtoList {}",
					dtoList);
			// creating psd list
			List<StagingSalesRegisterEntity> entityList = dtoList.stream()
					.map(o -> convertToEntity(o)).collect(Collectors.toList());
			LOGGER.debug("About to update count summry for entityList {}",
					entityList);
			repo.saveAll(entityList);
			LOGGER.debug("About to update count summry for fileId {}", fileId);
			// update error and psd counts
//			gstr1FileStatusRepository.updateCountSummary(fileId,
//					(psdCount + errorCount), psdCount, errorCount);
//			LOGGER.debug("updated count summry for fileId {}, About to "
//					+ "update the user response", fileId);
			gstr1FileStatusRepository.updateFileStatus(fileId, "Processed");

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_LOAD_SRFILE_DATA");

			storedProc.registerStoredProcedureParameter("FILEID", Long.class,
					ParameterMode.IN);

			storedProc.setParameter("FILEID", fileId);

			storedProc.execute();

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

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {
			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 19);

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

			if (actualHeaderNames.size() != 19) {
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

	private Triplet<List<StagingSalesRegisterUploadDto>, Integer, Integer> validation(
			List<Object[]> fileList, Long fileId, Integer errorCount,
			Integer psdCount, List<String> docKeyList) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Gstr1SalesRegisterFileArrivalHandler "
					+ "Validation method ";
			LOGGER.debug(msg);
		}
		List<ProcessingResult> validationResult = null;

		List<StagingSalesRegisterUploadDto> processedEntityList = new ArrayList<>();

		Map<String, StagingSalesRegisterUploadDto> processedMap = new HashMap<>();

		Map<String, List<StagingSalesRegisterUploadDto>> errorMap = new HashMap<>();

		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();
			String errorMessage = null;
			String errorCodes = null;

			structuralValidation.rowDataValidation(validationResult, rowData);

			List<String> errorArrayList = validationResult.stream()
					.filter(result -> result.getDescription() != null)
					.map(ProcessingResult::getDescription)
					.collect(Collectors.toList());
			
			String[] stringArray = new String[19];

			for (int i = 0; i < stringArray.length; i++) {
				if (i < rowData.length) {
					stringArray[i] = rowData[i] == null ? null
							: (rowData[i].toString());
				}
			}

			if (!CollectionUtils.isEmpty(errorArrayList))
				errorMessage = String.join(",", errorArrayList);

			if (validationResult.isEmpty()) {
				makeProcessedEntityList(stringArray, processedMap, errorMap,
						fileId, true, errorCodes, errorMessage);

			} else {
				makeProcessedEntityList(stringArray, processedMap, errorMap,
						fileId, false, errorCodes, errorMessage);
			}
		}
		LOGGER.debug("processedMap {}", processedMap);
		processedMap.forEach((k, v) -> processedEntityList.add(v));
		errorMap.forEach((k, v) -> processedEntityList.addAll(v));

		List<StagingSalesRegisterUploadDto> psdList = new ArrayList<>();
		for(StagingSalesRegisterUploadDto psdEntity : processedEntityList){
			if(psdEntity.getIsError() == null){
				psdList.add(psdEntity);
			}
		}
				
		LOGGER.debug(" processedEntityList {}", processedEntityList);
		LOGGER.debug(" psdList {}", psdList);
		psdCount = psdList.size();
		errorCount = processedEntityList.size() - psdCount;

		return new Triplet<>(processedEntityList, psdCount, errorCount);
	}

	private void makeProcessedEntityList(String[] arr,
			Map<String, StagingSalesRegisterUploadDto> processedMap,
			Map<String, List<StagingSalesRegisterUploadDto>> errorMap,
			Long fileId, boolean isProceesedFlag, String errorCodes,
			String errorMessage) {
		StagingSalesRegisterUploadDto entity = new StagingSalesRegisterUploadDto();

		entity.setFileId(fileId);
		entity.setReturnPeriod(
				arr[0] != null ? arr[0].toString().trim() : null);
		entity.setBusinessPlace(
				arr[1] != null ? truncate(arr[1].toString().trim()) : null);
		entity.setCustGstin(arr[2] != null ? truncate(arr[2].toString().trim()) : null);
		entity.setDocType(arr[3] != null ? arr[3].toString().trim() : null);
		entity.setSupplyType(arr[4] != null ? truncate(arr[4].toString().trim()) : null);
		entity.setDocNum(arr[5] != null ? truncate(arr[5].toString().trim()) : null);
		if (arr[6] != null) {
			LocalDate date = DateUtil.parseObjToDate(arr[6]);
			entity.setDocDate(date != null ? date.toString() : arr[6]);
		} else{
			entity.setDocDate(null);
		}
		entity.setItemSerialNumber(
				arr[7] != null ? truncate(arr[7].toString().trim()) : null);
		entity.setHsnSac(arr[8] != null ? truncate(arr[8].toString().trim()) : null);
		entity.setTaxRate(arr[9] != null ? truncate(arr[9].toString().trim()) : null);
		entity.setItemAssessableValue(
				arr[10] != null ? arr[10].toString().trim() : null);
		entity.setIgst(arr[11] != null ? arr[11].toString().trim() : null);
		entity.setCgst(arr[12] != null ? arr[12].toString().trim() : null);
		entity.setSgst(arr[13] != null ? arr[13].toString().trim() : null);
		entity.setAdvaloremAmountCess(
				arr[14] != null ? arr[14].toString().trim() : null);
		entity.setSpecificCess(
				arr[15] != null ? arr[15].toString().trim() : null);
		entity.setInvoiceValue(
				arr[16] != null ? arr[16].toString().trim() : null);
		entity.setPos(arr[17] != null ? truncate(arr[17].toString().trim()) : null);
		entity.setTransactionType(
				arr[18] != null ? truncate(arr[18].toString().trim()) : null);
		String docKey = new StringJoiner(DOC_KEY_JOINER).add(arr[1]).add(arr[3])
				.add(arr[5]).add(arr[6]).toString();
		if (isProceesedFlag) {
			if (processedMap.containsKey(docKey)) {
				StagingSalesRegisterUploadDto existingEntity = processedMap
						.get(docKey);
				errorMap.computeIfAbsent(docKey,
						obj -> new ArrayList<StagingSalesRegisterUploadDto>())
						.add(existingEntity);
				processedMap.put(docKey, entity);
			} else {
				processedMap.put(docKey, entity);
			}
		} else {
			entity.setIsError(true);
			entity.setErrorDescription(errorMessage);
			errorMap.computeIfAbsent(docKey,
					obj -> new ArrayList<StagingSalesRegisterUploadDto>())
					.add(entity);
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

	private String getDocKey(Object[] arr) {
		String bussinessPlace = arr[1] != null
				? (Strings.isNullOrEmpty(arr[1].toString().trim()) != true
						? arr[1].toString().trim() : null)
				: null;
		String docType = arr[3] != null
				? (Strings.isNullOrEmpty(arr[3].toString().trim()) != true
						? arr[3].toString().trim() : null)
				: null;
		String docNo = arr[5] != null
				? (Strings.isNullOrEmpty(arr[5].toString().trim()) != true
						? arr[5].toString().trim() : null)
				: null;
		String docDate = arr[6] != null
				? (Strings.isNullOrEmpty(arr[6].toString().trim()) != true
						? arr[6].toString().trim() : null)
				: null;
		return new StringJoiner(DOC_KEY_JOINER).add(bussinessPlace).add(docType)
				.add(docNo).add(docDate).toString();
	}

	private StagingSalesRegisterEntity convertToEntity(
			StagingSalesRegisterUploadDto dto) {
		StagingSalesRegisterEntity entity = new StagingSalesRegisterEntity();
		entity.setReturnPeriod(removeQuotes(dto.getReturnPeriod()));
		entity.setBusinessPlace(removeQuotes(dto.getBusinessPlace()));
		entity.setCustGstin(removeQuotes(dto.getCustGstin()));
		entity.setDocType(removeQuotes(dto.getDocType()));
		entity.setSupplyType(removeQuotes(dto.getSupplyType()));
		entity.setDocNum(removeQuotes(dto.getDocNum()));
		entity.setDocDate(removeQuotes(dto.getDocDate()));
		entity.setItemSerialNumber(removeQuotes(dto.getItemSerialNumber()));
		entity.setHsnSac(removeQuotes(dto.getHsnSac()));
		entity.setTaxRate(removeQuotes(dto.getTaxRate()));
		entity.setIgst(removeQuotes(dto.getIgst()));
		entity.setCgst(removeQuotes(dto.getCgst()));
		entity.setSgst(removeQuotes(dto.getSgst()));
		entity.setCessAmountAdvalorem(removeQuotes(dto.getAdvaloremAmountCess()));
		entity.setInvoiceValue(removeQuotes(dto.getInvoiceValue()));
		entity.setPos(removeQuotes(dto.getPos()));
		entity.setTransType(removeQuotes(dto.getTransactionType()));
		entity.setCessAmountSpecific(removeQuotes(dto.getSpecificCess()));
		entity.setTaxableValue(removeQuotes(dto.getItemAssessableValue()));
		entity.setFileId(dto.getFileId());
		entity.setIsError(dto.getIsError());
		entity.setErrDesc(dto.getErrorDescription());
		entity.setCreatedOn(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
		
		return entity;
	}
	
	private String removeQuotes(String data) {
		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		if (data.contains("'")) {
			return data.replace("'", "");
		}
		return data;

	}

	public String truncate(String field){
		if(field.length() > 500){
			return field.substring(0,500);
		} else {
			return field;
		}
	}
}
