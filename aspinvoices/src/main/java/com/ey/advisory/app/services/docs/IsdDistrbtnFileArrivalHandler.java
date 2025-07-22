package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.IsdDistributionHeaderCheckService;
import com.ey.advisory.app.data.entities.client.Gstr6UserInputEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.IsdDistributionErr;
import com.ey.advisory.app.data.entities.client.IsdDistributionPsdEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6UserInputRepo;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.IsdDistributionErrorStatusRepository;
import com.ey.advisory.app.data.repositories.client.IsdDistributionPsdRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.strcutvalidation.einvoice.IsdDistributionStructuralChain;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Service("IsdDistrbtnFileArrivalHandler")
public class IsdDistrbtnFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IsdDistrbtnFileArrivalHandler.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("IsdDistributionHeaderCheckService")
	private IsdDistributionHeaderCheckService isdDistributionHeaderCheckService;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("IsdDistributionErrorStatusRepository")
	private IsdDistributionErrorStatusRepository isdDistributionErrorStatusRepository;

	@Autowired
	@Qualifier("IsdDistributionPsdRepository")
	private IsdDistributionPsdRepository isdDistributionPsdRepository;

	@Autowired
	@Qualifier("IsdDistributionStructuralChain")
	private IsdDistributionStructuralChain isdDistributionStructuralChain;

	@Autowired
	@Qualifier("Gstr6UserInputRepo")
	private Gstr6UserInputRepo gstr6UserInputRepo;

	@Autowired
	@Qualifier("EhcacheGstinTaxperiod")
	private EhcacheGstinTaxperiod ehcachegstinTaxPeriod;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	private static final String DOC_KEY_JOINER = "|";
	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";
	private static final Map<String, String> ERROR_CODE_MAP = new ImmutableMap.Builder<String, String>()
			.put("ER1035", "ISDGSTIN cannot be left blank.")
			.put("ER0024", "Invalid ISDGSTIN.")
			.put("ER0022", "SupplierGSTIN is mandatory.")
			.put("ER0023", "Invalid SupplierGSTIN.")
			.put("ER0027", "DocumentType cannot be left blank.")
			.put("ER0028", "Invalid DocumentType")
			.put("ER0031", "DocumentNumber cannot be left blank")
			.put("ER0032", "Invalid DocumentNumber.")
			.put("ER0033", "DocumentDate cannot be left blank.")
			.put("ER0034", "Invalid DocumentDate.")
			.put("ER0035", "Invalid Action Type.")
			.put("ER0046", "ItemSerialNumber cannot be left Blank")
			.put("ER0047", "Invalid ItemSerialNumber")
			.put("ER0048", "GSTINforDistribution mandatory.")
			.put("ER0049", "Invalid GSTINforDistribution.")
			.put("ER0050",
					"Document is not available as part of Purchase Register. Please recheck")
			.put("ER0051",
					"Item Serial Number not available in Purchase Register. Please recheck")
			.put("ER0052",
					"GSTIN for distribution is not available in turnover computation database. Please recheck")
			.put("ER0053",
					"GSTIN for distribution can not be repeated for same line number")
			.put("ER0054", "GSTR6 for selected tax period  is already filed")
			.put("ER0055", "Original Distribution record not found in DigiGST")
			.build();

	public void processProductFile(Message message, AppExecContext context) {

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String fileId = msg.getDocId();
		String userName = message.getUserName();
		try {
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileDownload_START", "IsdDistrbtnFileArrivalHandler",
					"processProductFile", "");

			Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = gstr1FileStatusRepository
					.findByDocId(fileId);
			String docId = gstr1FileStatusEntity.get().getDocId();
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileDownload_END", "IsdDistrbtnFileArrivalHandler",
					"processProductFile", "");

			if (document == null) {
				String errMsg = String.format(
						"Unable to find the file %s in folder %s", fileName,
						fileFolder);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			InputStream inputStream = document.getContentStream().getStream();
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileTransVerse_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");

			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);// XlsxLightCellsTraverser
			TabularDataLayout layout = new DummyTabularDataLayout(8);

			IsdDocumentKeyBuilder documentKeyBuilder = new IsdDocumentKeyBuilder();
			IsdFileUploadDocRowHandler rowHandler = new IsdFileUploadDocRowHandler<String>(
					documentKeyBuilder);
			traverser.traverse(inputStream, layout, rowHandler, null);

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileTransVerse_END", "IsdDistrbtnFileArrivalHandler",
					"processProductFile", "");

			Object[] getHeaders = rowHandler.getHeaderData();
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileHEADERValidate_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
					.getFileName(fileName);

			Pair<Boolean, String> checkHeaderFormat = isdDistributionHeaderCheckService
					.validate(getHeaders);//
			if (checkHeaderFormat == null || !checkHeaderFormat.getValue0()) {

				LOGGER.error("Headers not matched");
				updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
				gstr1FileStatusRepository.save(updateFileStatus);

				return;
			}

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileHEADERValidate_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");

			Map<String, List<Object[]>> rowHandlerMap = ((IsdFileUploadDocRowHandler<?>) rowHandler)
					.getDocumentMap();
			Set<String> isdGstinsSet = new HashSet<>();
			for (List<Object[]> rows : rowHandlerMap.values()) {
				for (Object[] row : rows) {
					if (row != null && row.length > 6
							&& "ALL".equalsIgnoreCase(String.valueOf(row[6]))) {
						if (row[0] != null) {
							isdGstinsSet.add(row[0].toString());
						}
					}
				}
			}

			List<String> isdGstinsList = new ArrayList<>(isdGstinsSet);
			List<Gstr6UserInputEntity> gstr6TurnoverList = gstr6UserInputRepo
					.findByIsdGstins(isdGstinsList);
			Map<String, List<Gstr6UserInputEntity>> gstr6TurnoverMap = new HashMap<>();
			for (Gstr6UserInputEntity userEntity : gstr6TurnoverList) {
				gstr6TurnoverMap
						.computeIfAbsent(userEntity.getIsdGstin(),
								obj -> new ArrayList<Gstr6UserInputEntity>())
						.add(userEntity);
			}
			for (Map.Entry<String, List<Gstr6UserInputEntity>> entry : gstr6TurnoverMap.entrySet()) {
			    Set<String> seenGstins = new HashSet<>();
			    List<Gstr6UserInputEntity> uniqueList = entry.getValue().stream()
			        .filter(entity -> seenGstins.add(entity.getGstin()))
			        .collect(Collectors.toList());
			    entry.setValue(uniqueList);
			}
			Map<String, List<Object[]>> documentMap = new HashMap<>();
			for (Map.Entry<String, List<Object[]>> entry : rowHandlerMap
					.entrySet()) {
				List<Object[]> rows = entry.getValue();
				for (Object[] row : rows) {
					if (row != null && row.length > 6
							&& "ALL".equalsIgnoreCase(String.valueOf(row[6]))) {						
						List<Gstr6UserInputEntity> turnoverList = gstr6TurnoverMap
								.get(String.valueOf(row[0]));
						if (turnoverList != null && !turnoverList.isEmpty()) {
							for (Gstr6UserInputEntity entity : turnoverList) {
								Object[] newRow = Arrays.copyOf(row,
										row.length);	
								newRow[6] = entity.getGstin();
								String docKey = documentKeyBuilder
										.buildDataBlockKey(newRow, null);
								documentMap
										.computeIfAbsent(docKey,
												k -> new ArrayList<>())
										.add(newRow);
							}
						}
					} else {
						String docKey = documentKeyBuilder
								.buildDataBlockKey(row, null);
						documentMap
								.computeIfAbsent(docKey, k -> new ArrayList<>())
								.add(row);
					}
				}
			}
			for (Map.Entry<String, List<Object[]>> entry : documentMap
					.entrySet()) {
				LOGGER.debug("Key: {}", entry.getKey());
				for (Object[] row : entry.getValue()) {
					LOGGER.debug("Row: {}", Arrays.toString(row));
				}
			}
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileSTRUCTUREValidate_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			Map<String, List<ProcessingResult>> strValidation = isdDistributionStructuralChain
					.validation(documentMap);//
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileSTRUCTUREValidate_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileSAMELINEValidate_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			documentMap.entrySet().forEach(entry -> {

				String key = entry.getKey();
				List<Object[]> list = documentMap.get(key);
				if (list.size() > 1) {
					List<ProcessingResult> errors = new ArrayList<>();
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0053",
							"GSTIN for distribution can not be repeated for same line number",
							location));
					strValidation.put(key, errors);
				}

			});

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileSAMELINEValidate_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			List<String> listKeys = new ArrayList<>();
			for (String keys : strValidation.keySet()) {
				listKeys.add(keys);
			}
			Integer totalRecords = 0;
			Integer processedRecords = 0;
			Integer errorRecords = 0;
			if (!strValidation.isEmpty()) {

				Map<String, List<Object[]>> processMapObj = new HashMap<>();
				Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
						"GSTR6FileKEYSET_START",
						"IsdDistrbtnFileArrivalHandler", "processProductFile",
						"");
				for (String keys : documentMap.keySet()) {
					if (!listKeys.contains(keys)) {
						List<Object[]> list = documentMap.get(keys);
						processMapObj.put(keys, list);
					} else {
						List<Object[]> list = documentMap.get(keys);
						errDocMapObj.put(keys, list);
					}
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
						"GSTR6FileKEYSET_END", "IsdDistrbtnFileArrivalHandler",
						"processProductFile", "");
				try {
					PerfUtil.logEventToFile(
							PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
							"GSTR6FileERRORDOC_START",
							"IsdDistrbtnFileArrivalHandler",
							"processProductFile", "");
					saveErrDocAndDoc(documentKeyBuilder, documentMap,
							updateFileStatus, strValidation, processMapObj,
							errDocMapObj, userName);
					PerfUtil.logEventToFile(
							PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
							"GSTR6FileERRORDOC_END",
							"IsdDistrbtnFileArrivalHandler",
							"processProductFile", "");

				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					gstr1FileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP, e);

				}

			} else {
				try {
					PerfUtil.logEventToFile(
							PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
							"GSTR6FileCONVERISDFILETODISTRIBUTION_START",
							"IsdDistrbtnFileArrivalHandler",
							"processProductFile", "");
					convertIsdFileToIsdDistributionDocument(documentMap,
							documentKeyBuilder, updateFileStatus, userName);
					PerfUtil.logEventToFile(
							PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
							"GSTR6FileCONVERISDFILETODISTRIBUTION_END",
							"IsdDistrbtnFileArrivalHandler",
							"processProductFile", "");
					errorRecords = isdDistributionPsdRepository
							.businessValidationCount(updateFileStatus.getId());
					totalRecords = documentMap.size();
					processedRecords = totalRecords - errorRecords;
					updateFileStatus.setTotal(totalRecords);
					updateFileStatus.setProcessed(processedRecords);
					updateFileStatus.setError(errorRecords);
					gstr1FileStatusRepository.save(updateFileStatus);
				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					gstr1FileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP, e);

				}
			}
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(EXCEPTION_APP, e);
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

	@Transactional(value = "clientTransactionManager")
	private void saveErrDocAndDoc(IsdDocumentKeyBuilder documentKeyBuilder,
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMapObj,
			Map<String, List<Object[]>> errDocMapObj, String userName) {
		Integer totalRecords;
		Integer processedRecords;
		Integer errorRecords;
		Integer businessErrorCount;
		try {
			if (!errDocMapObj.isEmpty()) {
				convertIsdFileToIsdDistributionErr(errDocMapObj,
						processingResults, updateFileStatus, userName);
			}
			if (!documentMapObj.isEmpty()) {
				convertIsdFileToIsdDistributionDocument(documentMapObj,
						documentKeyBuilder, updateFileStatus, userName);
			}
			businessErrorCount = isdDistributionPsdRepository
					.businessValidationCount(updateFileStatus.getId());
			totalRecords = documentMap.size();
			errorRecords = processingResults.size() + businessErrorCount;
			processedRecords = totalRecords - errorRecords;
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			gstr1FileStatusRepository.save(updateFileStatus);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
	}

	public List<IsdDistributionErr> convertIsdFileToIsdDistributionErr(
			Map<String, List<Object[]>> errDocMapObj,
			Map<String, List<ProcessingResult>> processingResults,
			Gstr1FileStatusEntity fileStatus, String userName) {
		List<IsdDistributionErr> errorHeaders = new ArrayList<>();
		try {
			LocalDateTime localDate = LocalDateTime.now();
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<ProcessingResult> errorList = processingResults.get(key);
				List<Object[]> objs = entry.getValue();
				for (Object[] obj : objs) {
					IsdDistributionErr errorDocument = new IsdDistributionErr();
					String isdgstn = getWithValue(obj[0]);
					String supplierGstin = getWithValue(obj[1]);
					String documentType = getWithValue(obj[2]);
					String documentNumber = getWithValue(obj[3]);
					String documentDate = getWithValue(obj[4]);
					if (documentDate.contains("T00:00:00")) {
						String[] split = documentDate.split("T");
						documentDate = split[0];
					}
					String itemSerialNumber = getWithValue(obj[5]);
					String gstinForDistribution = getWithValue(obj[6]);
					String actionType = getWithValue(obj[7]);
					errorDocument.setIsDelete(false);
					errorDocument.setIsdgstn(isdgstn);
					errorDocument.setSupplierGstin(supplierGstin);
					errorDocument.setDocType(documentType);
					errorDocument.setDocNum(documentNumber);
					errorDocument.setDocDate(documentDate);
					errorDocument.setItemSerialNum(itemSerialNumber);
					errorDocument.setGstinDistribution(gstinForDistribution);
					errorDocument.setCreatedBy(userName);
					errorDocument.setCreatedOn(localDate);
					errorDocument.setActionType(actionType);
					if (fileStatus.getSource()
							.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
						errorDocument.setDataOriginInTypeCode("E");
					} else {
						errorDocument.setDataOriginInTypeCode("S");
					}
					errorDocument.setDistrbDocKey(key);
					Pair<String, String> errorCodeDescPair = populateErrorCodesAndDescription(
							errorList);
					errorDocument.setErrorCodes(errorCodeDescPair.getValue0());
					errorDocument.setErrorDesc(errorCodeDescPair.getValue1());
					errorDocument.setFileId(fileStatus.getId().toString());
					errorDocument.setIsError(true);
					errorDocument.setUpdatedBy(userName);
					errorDocument.setUpdatedOn(localDate);//

					errorHeaders.add(errorDocument);

				}

			});
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileERRORDOCSAVE_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			isdDistributionErrorStatusRepository.saveAll(errorHeaders);
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileERRORDOCSAVE_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(EXCEPTION_APP);
		}
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Endining");
		return errorHeaders;
	}

	public List<IsdDistributionPsdEntity> convertIsdFileToIsdDistributionDocument(
			Map<String, List<Object[]>> documentMapObj,
			IsdDocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		List<IsdDistributionPsdEntity> docHeaders = new ArrayList<>();
		List<String> docKeyList = new ArrayList<>();
		Set<String> prDocKeySet = new HashSet<>();
		LocalDateTime localDate = LocalDateTime.now();
		Set<String> gstnList = new HashSet<>();
		Map<String, String> inwardDocMap = new HashMap<>();
		try {
			documentMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				docKeyList.add(key);
				List<Object[]> objs = entry.getValue();
				for (Object[] obj : objs) {
					IsdDistributionPsdEntity document = new IsdDistributionPsdEntity();
					String isdgstn = getWithValue(obj[0]);
					String supplierGstin = getWithValue(obj[1]);
					String documentType = getWithValue(obj[2]);
					String documentNumber = getWithValue(obj[3]);
					String documentDate = getWithValue(obj[4]);
					String itemSerialNumber = getWithValue(obj[5]);
					String gstinForDistribution = getWithValue(obj[6]);
					String actionType = getWithValue(obj[7]);
					document.setIsDelete(false);
					document.setIsdgstn(isdgstn);
					document.setSupplierGstin(supplierGstin);
					document.setDocType(documentType);
					document.setDocNum(documentNumber);
					document.setDocDate(DateUtil.parseObjToDate(documentDate));
					document.setItemSerialNum(
							Integer.parseInt(itemSerialNumber));
					document.setGstinDistribution(gstinForDistribution);
					document.setActionType(actionType);
					gstnList.add(isdgstn);
					document.setCreatedBy(userName);
					document.setCreatedOn(localDate);
					if (fileStatus.getSource()
							.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
						document.setDataOriginInTypeCode("E");
					} else {
						document.setDataOriginInTypeCode("S");
					}
					document.setDistrbDocKey(key);
					document.setFileId(fileStatus.getId());
					String prDocKey = derivePRKey(document.getDocDate(),
							isdgstn, supplierGstin, documentType,
							documentNumber);
					document.setPrDocKey(prDocKey);
					prDocKeySet.add(prDocKey);
					document.setUpdatedBy(userName);
					document.setUpdatedOn(localDate);
					if (actionType != null
							&& actionType.equalsIgnoreCase("CAN")) {
						List<IsdDistributionPsdEntity> actionDataList = isdDistributionPsdRepository
								.findByDistrbDocKeyAndActionTypeAndIsDeleteFalse(
										key, null);
						if (actionDataList.size() < 1) {
							document.setErrorCodes("ER0055");
						}
					}

					docHeaders.add(document);
				}

			});
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileGSTR6USERINPUTCHECK_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			List<String> isdGstinList = new ArrayList<>(gstnList);
			Map<String, List<String>> gstr6UserInputMap = new HashMap<>();

			List<Gstr6UserInputEntity> gstr6UserInputEntities = gstr6UserInputRepo
					.findByIsdGstins(isdGstinList);
			for (Gstr6UserInputEntity userInputEntity : gstr6UserInputEntities) {
				gstr6UserInputMap
						.computeIfAbsent(userInputEntity.getIsdGstin(),
								k -> new ArrayList<>())
						.add(userInputEntity.getGstin());
			}

			for (IsdDistributionPsdEntity docHeadEnt : docHeaders) {
				List<String> userInputGstins = gstr6UserInputMap
						.get(docHeadEnt.getIsdgstn());
				if (!userInputGstins
						.contains(docHeadEnt.getGstinDistribution())) {
					docHeadEnt.setErrorCodes("ER0052");
				}

			}
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileGSTR6USERINPUTCHECK_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileCHECKPRKEYValidation_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			chekPRKeyValidation(docHeaders, prDocKeySet, inwardDocMap);
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileCHECKPRKEYValidation_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileDOCHEADERUPDATE_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			ProcessingContext context = new ProcessingContext();
			settingFiledGstins(context);
			for (IsdDistributionPsdEntity docHeader : docHeaders) {
				docHeader.setTaxPeriod(
						inwardDocMap.get(docHeader.getPrDocKey()));
				isGstr6IsdDistrbtnFiled(docHeader, context);
				if (!Strings.isNullOrEmpty(docHeader.getErrorCodes())) {
					docHeader.setErrorDesc(
							deriveErrorDesc(docHeader.getErrorCodes()));
					docHeader.setIsError(true);
					docHeader.setIsProcessed(false);
				} else {
					docHeader.setIsError(false);
					docHeader.setIsProcessed(true);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("DocKey :{} ", docHeader.getPrDocKey());
					LOGGER.debug("inwardDocMap :{}", inwardDocMap);
				}

			}
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileDOCHEADERUPDATE_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileSOFTDELETE_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			softDelete(docKeyList);
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FileSOFTDELETE_END", "IsdDistrbtnFileArrivalHandler",
					"processProductFile", "");
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FilePROCESSDATASAVE_START",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
			isdDistributionPsdRepository.saveAll(docHeaders);
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.GSTR6_ISD_DISTRIBUTION,
					"GSTR6FilePROCESSDATASAVE_END",
					"IsdDistrbtnFileArrivalHandler", "processProductFile", "");
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Endining");
		return docHeaders;
	}

	private String deriveErrorDesc(String errorCodes) {
		String[] errorCode = errorCodes.split(",");
		List<String> errorDescList = new ArrayList<>();
		for (String code : errorCode) {
			errorDescList.add(code + "-" + ERROR_CODE_MAP.get(code));
		}
		return errorDescList.stream().collect(Collectors.joining(","));
	}

	private void chekPRKeyValidation(List<IsdDistributionPsdEntity> docHeaders,
			Set<String> prDocKeySet, Map<String, String> inwardDocMap) {
		List<String> prlist = new ArrayList<>(prDocKeySet);
		List<List<String>> chunks = Lists.partition(prlist, 2000);
		Map<String, List<Integer>> keyLineMap = new HashMap<>();
		for (List<String> chunk : chunks) {
			List<InwardTransDocument> activeRecords = inwardTransDocRepository
					.findIsdRecordsByDocKeys(chunk);
			for (InwardTransDocument document : activeRecords) {
				String docKey = document.getDocKey();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("DocKey Inside chekPRKeyValidation:{} ",
							docKey);
				if (docKey != null && document.getTaxperiod() != null) {
					LOGGER.debug("TaxPeriod Inside chekPRKeyValidation: {}",
							document.getTaxperiod());
					inwardDocMap.put(docKey, document.getTaxperiod());
				}

				List<InwardTransDocLineItem> lineItems = document
						.getLineItems();
				List<Integer> lineNumberList = new ArrayList<>();
				for (InwardTransDocLineItem docLineItem : lineItems) {
					lineNumberList.add(docLineItem.getLineNo());
				}
				keyLineMap.put(docKey, lineNumberList);
			}
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("inwardDocMap :{}", inwardDocMap);

		for (IsdDistributionPsdEntity psdEntity : docHeaders) {
			if (keyLineMap.containsKey(psdEntity.getPrDocKey())) {
				List<Integer> serialNumList = keyLineMap
						.get(psdEntity.getPrDocKey());
				if (!serialNumList.contains(psdEntity.getItemSerialNum())) {
					String errorCodes = psdEntity.getErrorCodes();
					errorCodes = errorCodes == null ? "ER0051"
							: errorCodes + ",ER0051";
					psdEntity.setErrorCodes(errorCodes);
				}
			} else {
				String errorCodes = psdEntity.getErrorCodes();
				errorCodes = errorCodes == null ? "ER0050"
						: errorCodes + ",ER0050";
				psdEntity.setErrorCodes(errorCodes);
			}
		}
	}

	private void isGstr6IsdDistrbtnFiled(IsdDistributionPsdEntity psdEntity,
			ProcessingContext context) {

		String gstin = psdEntity.getIsdgstn();
		String taxPeriod = psdEntity.getTaxPeriod();

		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + DOC_KEY_JOINER + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			String errorCodes = psdEntity.getErrorCodes();
			errorCodes = errorCodes == null ? "ER0054" : errorCodes + ",ER0054";
			psdEntity.setErrorCodes(errorCodes);
		}

	}

	private void settingFiledGstins(ProcessingContext context) {

		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR6", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {
			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	private String getWithValue(Object obj) {
		return obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj.toString().trim()) : null;
	}

	private Pair<String, String> populateErrorCodesAndDescription(
			List<ProcessingResult> processingResults) {
		List<String> descList = new ArrayList<>();
		List<String> errorCodeList = new ArrayList<>();
		for (ProcessingResult error : processingResults) {
			errorCodeList.add(error.getCode());
			String errorCodeDesc = error.getCode() + "-"
					+ ERROR_CODE_MAP.get(error.getCode());
			descList.add(errorCodeDesc);
		}
		String errorCode = errorCodeList.stream()
				.collect(Collectors.joining(","));
		String errorDesc = descList.stream().collect(Collectors.joining(","));
		return new Pair<String, String>(errorCode, errorDesc);
	}

	private void softDelete(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			int rowsEffected = 0;

			rowsEffected = isdDistributionPsdRepository
					.updateIsDeleteFlag(chunk);

			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in TBL_EWB_FU_HEADER table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private String derivePRKey(LocalDate date, String cgstin, String sgstin,
			String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}
}
