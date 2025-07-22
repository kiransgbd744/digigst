package com.ey.advisory.app.service.ims;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsErrorInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderB2BARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderCNARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderCNRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderDNARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderDNRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderECOMARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceHeaderECOMRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsProcessedInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterConfigEntityRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.FailedBatchAlertUtility;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("ImsRespUploadServiceImpl")
public class ImsRespUploadServiceImpl implements ImsRespUploadService {

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT8 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm");

	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private ImsInvoiceHeaderB2BARepository b2bARepository;

	@Autowired
	private ImsInvoiceHeaderB2BRepository b2bRepository;

	@Autowired
	private ImsInvoiceHeaderCNRepository cnRepository;

	@Autowired
	private ImsInvoiceHeaderDNARepository dnARepository;

	@Autowired
	private ImsInvoiceHeaderECOMRepository ecomRepository;

	@Autowired
	private ImsInvoiceHeaderCNARepository cnARepository;

	@Autowired
	private ImsInvoiceHeaderDNRepository dnRepository;

	@Autowired
	private ImsInvoiceHeaderECOMARepository ecomARepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	private ImsUploadStructuralValidation strucuralValidation;

	@Autowired
	private ImsProcessedInvoiceRepository psdRepo;

	@Autowired
	private ImsErrorInvoiceRepository errRepo;

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	@Autowired
	@Qualifier("VendorMasterConfigEntityRepository")
	VendorMasterConfigEntityRepository regRepo;

	private static final List<String> EXPECTED_HEADERNAMES_LIST = Arrays.asList(
			"User IMS Response", "IMS Response Remarks", "Action (GSTN)",
			"Action (DigiGST)", "Action (DigiGST) DateTime", "Saved to GSTN",
			"Active in IMS (GSTN)", "Table Type", "Recipient GSTIN",
			"Supplier GSTIN", "Supplier Legal Name", "Supplier Trade Name",
			"Document Type", "Document Number", "Document Date",
			"Taxable Value", "IGST", "CGST", "SGST", "Cess", "Total Tax",
			"Invoice Value", "POS", "Form Type", "GSTR1-Filing Status",
			"GSTR1-Filing Period", "ITC Reduction Required",
			"IGST Declared to reduce ITC", "CGST Declared to reduce ITC",
			"SGST Declared to reduce ITC", "Cess Declared to reduce ITC",
			"Original Document Number", "Original Document Date",
			"Pending Action Blocked", "Checksum", "Get Call Date Time",
			"IMS UniqueID");

	private static final int NO_OF_COLUMNS = EXPECTED_HEADERNAMES_LIST.size();

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public void validateResponse(Long fileId, String fileName,
			String folderName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Validating ImsRespUploadServiceImpl file";
			LOGGER.debug(msg);
		}

		Optional<Gstr1FileStatusEntity> updateFileStatus = fileStatusRepository
				.findById(fileId);

		List<Object[]> vendorLegalTradList = regRepo.getLegalNameTradeName();

		Map<String, Object[]> vendorMap = vendorLegalTradList != null
				? vendorLegalTradList.stream()
						.collect(Collectors.toMap(o -> (String) o[0],
								Function.identity()))
				: new HashMap<>();

		String docId = null;
		// String userName = "Harcoded";

		if (updateFileStatus.isPresent()) {
			docId = updateFileStatus.get().getDocId();

		}
		String userName = updateFileStatus.get().getUpdatedBy();

		try {

			InputStream in = getFileInpStream(fileName, folderName, docId);

			/*
			 * fileName = "IMS Records Report_Honda_sp0156.csv"; folderName =
			 * "C:\\Users\\QD194RK\\Downloads\\" + fileName;
			 * 
			 * File initialFile = new File(folderName); InputStream in = new
			 * FileInputStream(initialFile);
			 */

			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0,
					NO_OF_COLUMNS);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(in, layout, rowHandler, null);

			validateHeaders(fileName, folderName, fileId, rowHandler);

			List<Object[]> fileList = rowHandler.getFileUploadList();

			if (CollectionUtils.isEmpty(fileList)) {

				String msg = "Failed Empty file";
				LOGGER.error(msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateFileStatusAndErrorDesc(fileId,
						"FAILED", msg);

				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("filelist post removal size -> {} and filelist {}",
						fileList.size(), fileList.toString());
			}

			// convert rows into dto and collect docNum
			List<ImsActionResponseDto> rowsAsDtoList = fileList.stream()
					.map(o -> convertRowsToDto(o, fileId, vendorMap))
					.collect(Collectors.toList());
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total rows in file {} {}", rowsAsDtoList.size(),
						rowsAsDtoList);
			}

			Map<String, List<ImsActionResponseDto>> dtoMap = rowsAsDtoList
					.stream().collect(
							Collectors.groupingBy(
									dto -> Optional
											.ofNullable(
													dto.getTableTypeDerived())
											.orElse("UNKNOWN"),
									Collectors.toList()));

			List<Long> notAvailableIds = new ArrayList<>();
			List<Object[]> headerObjList = new ArrayList<>();
			dtoMap.forEach((tableType, uniqueIds) -> {
				Pair<List<Long>, List<Object[]>> objPair = lookupRepository(
						tableType, uniqueIds);

				notAvailableIds.addAll(objPair.getValue0());
				headerObjList.addAll(objPair.getValue1());

			});

			Map<String, ImsHeaderDto> headerObjListMap = mapFromHeaderObjList(
					headerObjList);

			Integer errorCount = 0;
			Integer psdCount = 0;

			// validation and adding error code
			Triplet<List<ImsActionResponseDto>, Integer, Integer> entityTriplet = validation(
					fileList, fileId, errorCount, psdCount, notAvailableIds,
					headerObjListMap);

			List<ImsActionResponseDto> entityList = entityTriplet.getValue0();
			psdCount = entityTriplet.getValue1();
			errorCount = entityTriplet.getValue2();

			// creating psd list
			List<ImsProcessedInvoiceEntity> psdEntityList = entityList.stream()
					.filter(o -> o.isPsd()).map(o -> convertToPsdList(o, fileId,
							headerObjListMap, userName, vendorMap))
					.collect(Collectors.toList());

			List<String> psdDocKeyList = psdEntityList.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			// creating Error list
			List<ImsErrorInvoiceEntity> errEntityList = entityList.stream()
					.filter(o -> !o.isPsd())
					.map(o -> convertToErrorList(o, fileId, vendorMap))
					.collect(Collectors.toList());

			// making isDelete flag true before saving in psd table
			LOGGER.debug("about to psd SoftDelete fileId {} ", fileId);
			// softDelete(psdDocKeyList, true);
			softDelete(psdDocKeyList);

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
			fileStatusRepository.updateCountSummary(fileId,
					rowsAsDtoList.size(),
					rowsAsDtoList.size() - errEntityList.size(),
					errEntityList.size());
			LOGGER.debug("updated count summry for fileId {}, About to "
					+ "update the user response", fileId);
			fileStatusRepository.updateFileStatus(fileId, "Processed");

		} catch (Exception ex) {
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			markFileAsFailed(fileId, ex.getMessage());
			throw new AppException(msg, ex);
		}

	}

	private Pair<List<Long>, List<Object[]>> lookupRepository(String tableType,
			List<ImsActionResponseDto> uniqueIds) {
		List<Long> allIds = getIds(tableType, uniqueIds).stream()
				.map(Pair::getValue0).collect(Collectors.toList());

		List<List<Long>> chunks = Lists.partition(allIds, 2000);
		List<Long> activeIds = new ArrayList<>();
		List<Object[]> headerObjects = new ArrayList<>();

		for (List<Long> chunkedIds : chunks) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Processing chunked IDs: {}", chunkedIds);
			}
			if (chunkedIds.isEmpty()) {
				continue;
			}

			processChunk(tableType, chunkedIds, activeIds, headerObjects);
		}

		// Filter out active IDs from the original list
		allIds.removeAll(activeIds);

		return new Pair<>(allIds, headerObjects);
	}

	private void processChunk(String tableType, List<Long> chunkedIds,
			List<Long> activeIds, List<Object[]> headerObjects) {
		List<Long> availableIds = new ArrayList<>();
		List<Object[]> repositoryResults = new ArrayList<>();

		switch (tableType) {
		case "B2B":
			availableIds = b2bRepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = b2bRepository.findAllById(availableIds);
			}
			break;
		case "B2BA":
			availableIds = b2bARepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = b2bARepository.findAllById(availableIds);
			}
			break;
		case "ECOM":
			availableIds = ecomRepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = ecomRepository.findAllById(availableIds);
			}
			break;
		case "ECOMA":
			availableIds = ecomARepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = ecomARepository.findAllById(availableIds);
			}
			break;
		case "CN":
			availableIds = cnRepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = cnRepository.findAllById(availableIds);
			}
			break;
		case "DN":
			availableIds = dnRepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = dnRepository.findAllById(availableIds);
			}
			break;
		case "CNA":
			availableIds = cnARepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = cnARepository.findAllById(availableIds);
			}
			break;
		case "DNA":
			availableIds = dnARepository.findActiveIds(chunkedIds);
			if (!availableIds.isEmpty()) {
				repositoryResults = dnARepository.findAllById(availableIds);
			}
			break;
		default:
			LOGGER.error("Unknown tableType encountered: {}", tableType);
			return;
		}

		if (availableIds != null && !availableIds.isEmpty()) {
			activeIds.addAll(availableIds);
			headerObjects.addAll(repositoryResults);
		}
	}

	private List<Pair<Long, String>> getIds(String tableType,
			List<ImsActionResponseDto> uniqueIds) {
		List<Pair<Long, String>> ids = new ArrayList<>();

		if (tableType != null) {
			for (ImsActionResponseDto dto : uniqueIds) {
				if (dto != null) {
					String uniqueId = dto.getImsUniqueID();
					if (uniqueId != null) {
						String[] idStrArr = uniqueId.split("-");
						if (idStrArr.length > 1) {
							try {
								Long id = Long.parseLong(idStrArr[1]);
								String docType = dto.getDocumentType();
								ids.add(Pair.with(id, docType));
							} catch (NumberFormatException e) {
								LOGGER.error(
										"error occurred in getIds method{} :",
										e);
							}
						}
					}
				}
			}
		}
		return ids;
	}

	private Triplet<List<ImsActionResponseDto>, Integer, Integer> validation(
			List<Object[]> fileList, Long fileId, Integer errorCount,
			Integer psdCount, List<Long> notAvialbleList,
			Map<String, ImsHeaderDto> headerObjListMap) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Ims Action Upload Validation method ";
			LOGGER.debug(msg);
		}
		List<ProcessingResult> validationResult = null;

		List<ImsActionResponseDto> processedEntityList = new ArrayList<>();

		Map<String, ImsActionResponseDto> processedMap = new HashMap<>();

		Map<String, List<ImsActionResponseDto>> errorMap = new HashMap<>();

		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();
			String errorMessage = null;
			String errorCodes = null;

			strucuralValidation.rowDataValidation(validationResult, rowData,
					notAvialbleList, headerObjListMap);

			List<String> errorArrayList = validationResult.stream()
					.filter(result -> result.getDescription() != null)
					.map(ProcessingResult::getDescription)
					.collect(Collectors.toList());

			List<String> errorCodeList = validationResult.stream()
					.filter(result -> result.getCode() != null)
					.map(ProcessingResult::getCode)
					.collect(Collectors.toList());

			String[] stringArray = new String[EXPECTED_HEADERNAMES_LIST.size()];

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

		List<ImsActionResponseDto> psdList = processedEntityList.stream()
				.filter(result -> result.isPsd()).collect(Collectors.toList());

		psdCount = psdList.size();
		errorCount = processedEntityList.size() - psdCount;

		return new Triplet<>(processedEntityList, psdCount, errorCount);
	}

	private void makeProcessedEntityList(String[] arr,
			Map<String, ImsActionResponseDto> processedMap,
			Map<String, List<ImsActionResponseDto>> errorMap, Long fileId,
			boolean isProcessed, String errorCodes, String errorMessage) {

		ImsActionResponseDto obj = new ImsActionResponseDto();
		obj.setActionResponse((arr[0] != null) ? arr[0].toString() : null);
		obj.setResponseRemarks((arr[1] != null) ? arr[1].toString() : null);
		obj.setActionGSTN((arr[2] != null) ? arr[2].toString() : null);
		obj.setActionDigiGST((arr[3] != null) ? arr[3].toString() : null);
		obj.setActionDigiGSTDateTime(
				(arr[4] != null) ? arr[4].toString() : null);
		obj.setSavedToGSTN((arr[5] != null) ? arr[5].toString() : null);
		obj.setAvailableInIms((arr[6] != null) ? arr[6].toString() : null);
		obj.setTableType((arr[7] != null) ? arr[7].toString() : null);
		obj.setRecipientGSTIN((arr[8] != null) ? arr[8].toString() : null);
		obj.setSupplierGSTIN((arr[9] != null) ? arr[9].toString() : null);
		obj.setSupplierLegalName((arr[10] != null) ? arr[10].toString() : null);
		obj.setSupplierTradeName((arr[11] != null) ? arr[11].toString() : null);
		obj.setDocumentType((arr[12] != null) ? arr[12].toString() : null);
		obj.setDocumentNumber(
				removeQuotes((arr[13] != null) ? arr[13].toString() : null));
		obj.setDocumentDate((arr[14] != null) ? arr[14].toString() : null);
		obj.setTaxableValue((arr[15] != null) ? arr[15].toString() : null);
		obj.setIgst((arr[16] != null) ? arr[16].toString() : null);
		obj.setCgst((arr[17] != null) ? arr[17].toString() : null);
		obj.setSgst((arr[18] != null) ? arr[18].toString() : null);
		obj.setCess((arr[19] != null) ? arr[19].toString() : null);
		obj.setTotalTax((arr[20] != null) ? arr[20].toString() : null);
		obj.setInvoiceValue((arr[21] != null) ? arr[21].toString() : null);
		obj.setPos((arr[22] != null) ? arr[22].toString() : null);
		obj.setFormType((arr[23] != null) ? arr[23].toString() : null);
		obj.setGstr1FilingStatus((arr[24] != null) ? arr[24].toString() : null);
		obj.setGstr1FilingPeriod((arr[25] != null) ? arr[25].toString() : null);

		// V1.1
		obj.setItcRedReq((arr[26] != null) ? arr[26].toString() : null);
		obj.setDeclaredIgst((arr[27] != null) ? arr[27].toString() : null);
		obj.setDeclaredCgst((arr[28] != null) ? arr[28].toString() : null);
		obj.setDeclaredSgst((arr[29] != null) ? arr[29].toString() : null);
		obj.setDeclaredCess((arr[30] != null) ? arr[30].toString() : null);

		obj.setOriginalDocumentNumber(
				removeQuotes((arr[31] != null) ? arr[31].toString() : null));
		obj.setOriginalDocumentDate(
				(arr[32] != null) ? arr[32].toString() : null);
		obj.setPendingActionBlocked(
				(arr[33] != null) ? arr[33].toString() : null);
		obj.setChecksum((arr[34] != null) ? arr[34].toString() : null);
		obj.setGetCallDateTime((arr[35] != null) ? arr[35].toString() : null);
		obj.setImsUniqueID((arr[36] != null) ? arr[36].toString() : null);

		obj.setDocKey(
				createInvKey(obj.getSupplierGSTIN(), obj.getRecipientGSTIN(),
						obj.getDocumentNumber(), obj.getDocumentDate(),
						obj.getDocumentType(), obj.getTableType()));
		obj.setFileId(fileId);
		obj.setPsd(isProcessed);
		obj.setErrorDesc(errorMessage);

		// duplicate uniqueId
		if (isProcessed) {
			if (processedMap.containsKey(obj.getImsUniqueID())) {
				ImsActionResponseDto existingEntity = processedMap
						.get(obj.getImsUniqueID());
				existingEntity.setPsd(false);
				existingEntity.setErrorDesc("Duplicate UniqueID");
				errorMap.computeIfAbsent(obj.getImsUniqueID(),
						var -> new ArrayList<ImsActionResponseDto>())
						.add(existingEntity);
				processedMap.put(obj.getImsUniqueID(), obj);
			} else {
				processedMap.put(obj.getImsUniqueID(), obj);
			}

		} else {
			errorMap.computeIfAbsent(obj.getImsUniqueID(),
					var -> new ArrayList<ImsActionResponseDto>()).add(obj);
		}

	}

	private InputStream getFileInpStream(String fileName, String folderName,
			String docId) {
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession:{} ", openCmisSession);
			}

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
			LOGGER.error(
					"Exception occured in reading File ImsRespUploadServiceImpl {}",
					e);
			throw new AppException("Error occured while reading the file {}",
					e);
		}
		return inputStream;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId, FileUploadDocRowHandler<?> rowHandler) {
		try {

			if (rowHandler.getHeaderRow() == null) {

				String msg = "The headers are empty.";
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateFileStatusAndErrorDesc(fileId,
						"FAILED", msg);
				markFileAsFailed(fileId, msg);
				LOGGER.error(msg);
				throw new AppException(msg);

			}

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

			if (actualHeaderNames.size() != NO_OF_COLUMNS) {
				String msg = String.format(
						"The number of columns in the file should be %d. "
								+ "Aborting the file processing.",
						NO_OF_COLUMNS);
				markFileAsFailed(fileId, msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateFileStatusAndErrorDesc(fileId,
						"FAILED", msg);
				LOGGER.equals(msg);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected User response headers names "
								+ "%s and header count %d",
						EXPECTED_HEADERNAMES_LIST.toString(),
						EXPECTED_HEADERNAMES_LIST.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(EXPECTED_HEADERNAMES_LIST,
					actualHeaderNames);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Comparing two files header name " + "and count: %s",
						isMatch);
				LOGGER.debug(msg);
			}

			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateFileStatusAndErrorDesc(fileId,
						"FAILED", msg);
				markFileAsFailed(fileId, msg);
				LOGGER.error(msg);
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

	private boolean headersMatch(List<String> expected, List<String> actual) {
		return !Streams
				.zip(actual.stream(), expected.stream(),
						(a, e) -> createPair(a, e))
				.anyMatch(p -> !p.getValue0().equals(p.getValue1()));
	}

	private Pair<String, String> createPair(String val1, String val2) {
		String val1Str = (val1 == null) ? "" : val1.trim().toUpperCase();
		String val2Str = (val2 == null) ? "" : val2.trim().toUpperCase();
		return new Pair<>(val1Str, val2Str);
	}

	private void markFileAsFailed(Long fileId, String reason) {

		try {
			if (reason.length() > 200)
				reason = reason.substring(0, 200);
			fileStatusRepository.updateErrorFieNameById(fileId, reason);
			fileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = String
					.format("[SEVERE] Unable to mark the file as failed. "
							+ "Reason for file failure is: [ %s ]", reason);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private ImsActionResponseDto convertRowsToDto(Object[] arr, Long fileId,
			Map<String, Object[]> vendorMap) {
		ImsActionResponseDto obj = new ImsActionResponseDto();
		obj.setActionResponse((arr[0] != null) ? arr[0].toString() : null);
		obj.setResponseRemarks((arr[1] != null) ? arr[1].toString() : null);
		obj.setActionGSTN((arr[2] != null) ? arr[2].toString() : null);
		obj.setActionDigiGST((arr[3] != null) ? arr[3].toString() : null);
		obj.setActionDigiGSTDateTime(
				(arr[4] != null) ? arr[4].toString() : null);
		obj.setSavedToGSTN((arr[5] != null) ? arr[5].toString() : null);
		obj.setAvailableInIms((arr[6] != null) ? arr[6].toString() : null);
		obj.setTableType((arr[7] != null) ? arr[7].toString() : null);
		obj.setRecipientGSTIN((arr[8] != null) ? arr[8].toString() : null);
		obj.setSupplierGSTIN((arr[9] != null) ? arr[9].toString() : null);
		obj.setSupplierLegalName((arr[10] != null) ? arr[10].toString() : null);
		obj.setSupplierTradeName((arr[11] != null) ? arr[11].toString() : null);
		if (!vendorMap.isEmpty()) {
			Object[] vendrArr = vendorMap.containsKey(obj.getSupplierGSTIN())
					? vendorMap.get(obj.getSupplierGSTIN())
					: null;

			if (vendrArr != null) {
				if (vendrArr.length > 1) {
					obj.setSupplierLegalName(
							vendrArr[1] != null ? vendrArr[1].toString()
									: null);
				}
				if (vendrArr.length > 2) {
					obj.setSupplierTradeName(
							vendrArr[2] != null ? vendrArr[2].toString()
									: null);
				}

			}
		}
		obj.setDocumentType((arr[12] != null) ? arr[12].toString() : null);
		obj.setDocumentNumber(
				removeQuotes((arr[13] != null) ? arr[13].toString() : null));
		obj.setDocumentDate(
				(arr[14] != null) ? arr[14].toString().toString() : null);
		obj.setTaxableValue((arr[15] != null) ? arr[15].toString() : null);
		obj.setIgst((arr[16] != null) ? arr[16].toString() : null);
		obj.setCgst((arr[17] != null) ? arr[17].toString() : null);
		obj.setSgst((arr[18] != null) ? arr[18].toString() : null);
		obj.setCess((arr[19] != null) ? arr[19].toString() : null);
		obj.setTotalTax((arr[20] != null) ? arr[20].toString() : null);
		obj.setInvoiceValue((arr[21] != null) ? arr[21].toString() : null);
		obj.setPos((arr[22] != null) ? arr[22].toString() : null);
		obj.setFormType((arr[23] != null) ? arr[23].toString() : null);
		obj.setGstr1FilingStatus((arr[24] != null) ? arr[24].toString() : null);
		obj.setGstr1FilingPeriod((arr[25] != null) ? arr[25].toString() : null);

		// V1.1
		obj.setItcRedReq((arr[26] != null) ? arr[26].toString() : null);
		obj.setDeclaredIgst((arr[27] != null) ? arr[27].toString() : null);
		obj.setDeclaredCgst((arr[28] != null) ? arr[28].toString() : null);
		obj.setDeclaredSgst((arr[29] != null) ? arr[29].toString() : null);
		obj.setDeclaredCess((arr[30] != null) ? arr[30].toString() : null);

		obj.setOriginalDocumentNumber(
				removeQuotes((arr[31] != null) ? arr[31].toString() : null));
		obj.setOriginalDocumentDate(
				(arr[32] != null) ? arr[32].toString() : null);
		obj.setPendingActionBlocked(
				(arr[33] != null) ? arr[33].toString() : null);
		obj.setChecksum((arr[34] != null) ? arr[34].toString() : null);
		obj.setGetCallDateTime((arr[35] != null) ? arr[35].toString() : null);
		obj.setImsUniqueID((arr[36] != null) ? arr[36].toString() : null);

		obj.setFileId(fileId);

		obj.setTableTypeDerived(
				getTableTypeFromImsUniqueId(obj.getImsUniqueID()));

		return obj;
	}

	/*
	 * if (!Strings.isNullOrEmpty(obj.getDocumentNumberPR())) {
	 * docNoPR.add(removeQuotes(obj.getDocumentNumberPR())); }
	 * 
	 * obj.setSgst2B((arr[39] != null) ? checkForNegative(arr[39].toString()) :
	 * null);
	 * 
	 * 
	 * obj.setOrgDocNumber2B( (arr[69] != null) ?
	 * removeQuotes(arr[69].toString()) : null); obj.setOrgDocNumberPR( (arr[70]
	 * != null) ? removeQuotes(arr[70].toString()) : null);
	 * 
	 * 
	 */

	// Invoice Key = SuppGSTIN + RGSTIN + Doc No + Doc Date + Doc Type + Table
	// Type
	private String createInvKey(String suppGstin, String recipientGstin,
			String docNum, String docDate, String docType, String tableType) {

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(docType) || !isPresent(docNum) || !isPresent(suppGstin)
				|| !isPresent(recipientGstin) || !isPresent(docDate)) {
			return null;
		}

		// String savedocKey = stin + "|" + rtin "|"+ docNum + "|" + taxPeriod;

		docKey.append(suppGstin).append(DOC_KEY_JOINER).append(recipientGstin)
				.append(DOC_KEY_JOINER).append(removeQuotes(docNum))
				.append(DOC_KEY_JOINER).append(removeQuotes(docDate))
				.append(DOC_KEY_JOINER).append(docType).append(DOC_KEY_JOINER)
				.append(tableType);

		return docKey.toString();
	}

	/*
	 * private String checkNullAndTrim(String data, boolean isTrimReqr, int
	 * trimLength) {
	 * 
	 * if (Strings.isNullOrEmpty(data)) { return null; } else { if (isTrimReqr
	 * && data.length() > trimLength) { return data.substring(0, trimLength); }
	 * } return data.trim(); }
	 */

	private String removeQuotes(String data) {
		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		if (data.contains("'")) {
			return data.replace("'", "");
		}
		if (data.contains("`")) {
			return data.replace("`", "");
		}

		return data;

	}

	private void softDelete(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = 0;

			rowsEffected = psdRepo.updateIsDeleteFlag(chunk, userName);
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in TBL_GETIMS_PROCESSED table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private ImsProcessedInvoiceEntity convertToPsdList(
			ImsActionResponseDto rspDto, Long fileId,
			Map<String, ImsHeaderDto> headerObjListMap, String userName,
			Map<String, Object[]> vendorMap) {

		ImsHeaderDto rowData = headerObjListMap.get(rspDto.getImsUniqueID());
		ImsProcessedInvoiceEntity dto = new ImsProcessedInvoiceEntity();

		String actionResp = rspDto.getActionResponse();

		if (actionResp != null && !actionResp.isEmpty()) {

			if (actionResp.equalsIgnoreCase("Accept")
					|| actionResp.equalsIgnoreCase("A"))
				actionResp = "A";
			else if (actionResp.equalsIgnoreCase("Reject")
					|| actionResp.equalsIgnoreCase("R"))
				actionResp = "R";
			else if (actionResp.equalsIgnoreCase("Pending")
					|| actionResp.equalsIgnoreCase("P"))
				actionResp = "P";
			else if (actionResp.equalsIgnoreCase("No Action")
					|| actionResp.equalsIgnoreCase("N")) {
				actionResp = "N";
			}

			dto.setActionResponse(actionResp.toUpperCase());
		}

		if (rspDto.getResponseRemarks() != null
				&& rspDto.getResponseRemarks().length() > 1000) {

			dto.setResponseRemarks(
					rspDto.getResponseRemarks().substring(0, 1000));
		} else {
			dto.setResponseRemarks(rspDto.getResponseRemarks());

		}

		String actionGSTN = rowData.getAction();
		if (actionGSTN != null && !actionGSTN.isEmpty()) {
			if (actionGSTN.equalsIgnoreCase("Accept")
					|| actionGSTN.equalsIgnoreCase("A")
					|| actionGSTN.equalsIgnoreCase("Accepted"))
				actionGSTN = "A";
			else if (actionGSTN.equalsIgnoreCase("Reject")
					|| actionGSTN.equalsIgnoreCase("R")
					|| actionGSTN.equalsIgnoreCase("Rejected"))
				actionGSTN = "R";
			else if (actionGSTN.equalsIgnoreCase("Pending")
					|| actionGSTN.equalsIgnoreCase("P"))
				actionGSTN = "P";
			else if (actionGSTN.equalsIgnoreCase("No Action")
					|| actionGSTN.equalsIgnoreCase("N")) {
				actionGSTN = "N";
			}
			dto.setActionGstn(actionGSTN.toUpperCase());
		}
		String actionDigi = rspDto.getActionDigiGST();
		if (actionDigi != null && !actionDigi.isEmpty()) {
			if (actionDigi.equalsIgnoreCase("Accept")
					|| actionDigi.equalsIgnoreCase("A")
					|| actionDigi.equalsIgnoreCase("Accepted"))
				actionDigi = "A";
			else if (actionDigi.equalsIgnoreCase("Reject")
					|| actionDigi.equalsIgnoreCase("R")
					|| actionDigi.equalsIgnoreCase("Rejected"))
				actionDigi = "R";
			else if (actionDigi.equalsIgnoreCase("Pending")
					|| actionDigi.equalsIgnoreCase("P"))
				actionDigi = "P";
			else if (actionDigi.equalsIgnoreCase("No Action")
					|| actionDigi.equalsIgnoreCase("N")) {
				actionDigi = "N";
			} else {
				actionDigi = null;
			}

			dto.setActionDigi(actionDigi.toUpperCase());
		}

		dto.setDigiActionDateTime(stringToTime(
				rspDto.getActionDigiGSTDateTime(), DATE_TIME_FORMATTER));
		dto.setIsSavedToGstin(false);
		dto.setTableType(rowData.getTableType());
		dto.setRecipientGstin(rowData.getRecipientGstin());
		dto.setSupplierGstin(rowData.getSupplierGstin());
		dto.setSupplierLegalName(rowData.getSupplierLegalName());
		dto.setSupplierTradeName(rowData.getSupplierTradeName());

		if (!vendorMap.isEmpty()) {
			Object[] vendrArr = vendorMap.containsKey(dto.getSupplierGstin())
					? vendorMap.get(dto.getSupplierGstin())
					: null;
			if (vendrArr != null) {
				dto.setSupplierLegalName(
						vendrArr[1] != null ? vendrArr[1].toString() : null);

				dto.setSupplierTradeName(
						vendrArr[2] != null ? vendrArr[2].toString() : null);

			}
		}

		dto.setInvoiceType(rowData.getInvoiceType());
		dto.setInvoiceNumber(rowData.getInvoiceNumber());
		dto.setInvoiceDate(rowData.getInvoiceDate());

		dto.setTaxableValue(rowData.getTaxableValue());
		BigDecimal igstAmt = rowData.getIgstAmt() != null ? rowData.getIgstAmt()
				: BigDecimal.ZERO;
		BigDecimal cgstAmt = rowData.getCgstAmt() != null ? rowData.getCgstAmt()
				: BigDecimal.ZERO;
		BigDecimal sgstAmt = rowData.getSgstAmt() != null ? rowData.getSgstAmt()
				: BigDecimal.ZERO;
		BigDecimal cessAmt = rowData.getCessAmt() != null ? rowData.getCessAmt()
				: BigDecimal.ZERO;

		dto.setIgstAmt(igstAmt);
		dto.setCgstAmt(cgstAmt);
		dto.setSgstAmt(sgstAmt);
		dto.setCessAmt(cessAmt);

		dto.setTotalTax(igstAmt.add(cgstAmt).add(sgstAmt).add(cessAmt));
		dto.setInvoiceValue(rowData.getInvoiceValue());
		dto.setPos(rowData.getPos());
		dto.setFormType(rowData.getFormType());
		dto.setFilingStatus(rowData.getFilingStatus());
		dto.setReturnPeriod(rowData.getReturnPeriod());
		Long dervidedRetPeriod = rowData.getReturnPeriod() != null
				? Long.valueOf(GenUtil
						.convertTaxPeriodToInt(rowData.getReturnPeriod()))
				: null;
		dto.setDerivedRetPeriod(dervidedRetPeriod);

		dto.setOrgDocDate(rowData.getOrgInvoiceDate());
		dto.setOrgDocNum(rowData.getOrgInvoiceNumber());

		dto.setIsPendingActionBlocked(rowData.getIsPendingActionBlocked());
		dto.setChksum(rowData.getChksum());
		dto.setGetCallDateTime(rowData.getCreatedOn());
		dto.setImsUniqueId(rspDto.getImsUniqueID());

		dto.setDocKey(rowData.getDocKey());
		dto.setFileId(fileId);
		dto.setIsDelete(false);
		dto.setIsSentToGstin(false);
		dto.setCreatedBy(userName);
		dto.setCreatedOn(LocalDateTime.now());
		dto.setGstnSaveDockey(createGstnSaveDockey(rowData.getDocKey(),
				rowData.getReturnPeriod()));
		dto.setGstnInvType(rowData.getGstnInvType());
		dto.setLnkingDocKey(rowData.getLinkingDocKey());
		
		//V1.1
		
		String itcReqRaw = rspDto.getItcRedReq();
		if (itcReqRaw != null && !itcReqRaw.trim().isEmpty()) {

		    String flag = itcReqRaw.trim().toUpperCase();
		    flag = (flag.startsWith("Y")) ? "Y" :
		           (flag.startsWith("N")) ? "N" : null; 

		    dto.setItcRedReq(flag);
		}

		dto.setDeclIgst(convertStringToBigDecimal(rspDto.getDeclaredIgst()));
		dto.setDeclCgst(convertStringToBigDecimal(rspDto.getDeclaredCgst()));
		dto.setDeclSgst(convertStringToBigDecimal(rspDto.getDeclaredSgst()));
		dto.setDeclCess(convertStringToBigDecimal(rspDto.getDeclaredCess()));

		
		

		return dto;
	}

	public String createGstnSaveDockey(String docKey, String retPeriod) {

		String[] docKeyArr = docKey.split("\\|");

		StringBuilder saveDocKey = new StringBuilder();

		// String savedocKey = stin + "|" + rtin "|"+ docNum + "|" + taxPeriod;

		saveDocKey.append(docKeyArr[0]).append(DOC_KEY_JOINER)
				.append(docKeyArr[1]).append(DOC_KEY_JOINER)
				.append(removeQuotes(docKeyArr[2])).append(DOC_KEY_JOINER)
				.append(retPeriod);

		return saveDocKey.toString();
	}

	private ImsErrorInvoiceEntity convertToErrorList(
			ImsActionResponseDto rowData, Long fileId,
			Map<String, Object[]> vendorMap) {
		ImsErrorInvoiceEntity dto = new ImsErrorInvoiceEntity();

		dto.setActionResponse(rowData.getActionResponse());
		dto.setResponseRemarks(rowData.getResponseRemarks());
		dto.setActionGstn(rowData.getActionGSTN());
		dto.setActionDigi(rowData.getActionDigiGST());
		dto.setDigiActionDateTime(rowData.getActionDigiGSTDateTime());
		// dto.setIsSavedToGstin(rowData.getSavedToGSTN);
		dto.setTableType(rowData.getTableType());
		dto.setRecipientGstin(rowData.getRecipientGSTIN());
		dto.setSupplierGstin(rowData.getSupplierGSTIN());
		dto.setSupplierLegalName(rowData.getSupplierLegalName());
		dto.setSupplierTradeName(rowData.getSupplierTradeName());

		if (!vendorMap.isEmpty()) {
			Object[] vendrArr = vendorMap.containsKey(dto.getSupplierGstin())
					? vendorMap.get(dto.getSupplierGstin())
					: null;
			if (vendrArr != null) {
				dto.setSupplierLegalName(
						vendrArr[1] != null ? vendrArr[1].toString() : null);

				dto.setSupplierTradeName(
						vendrArr[2] != null ? vendrArr[2].toString() : null);

			}
		}

		dto.setInvoiceType(rowData.getDocumentType());
		dto.setInvoiceNumber(rowData.getDocumentNumber());
		dto.setInvoiceDate(rowData.getDocumentDate());
		dto.setTaxableValue(rowData.getTaxableValue());
		dto.setIgstAmt(rowData.getIgst());
		dto.setCgstAmt(rowData.getCgst());
		dto.setSgstAmt(rowData.getSgst());
		dto.setCessAmt(rowData.getCess());
		dto.setTotalTax(rowData.getTotalTax());
		dto.setInvoiceValue(rowData.getInvoiceValue());
		dto.setPos(rowData.getPos());
		dto.setFormType(rowData.getFormType());
		dto.setFilingStatus(rowData.getGstr1FilingStatus());
		dto.setReturnPeriod(rowData.getGstr1FilingPeriod());
		dto.setOrgDocNum(rowData.getOriginalDocumentNumber());
		dto.setOrgDocDate(rowData.getOriginalDocumentDate());
		dto.setIsPendingActionBlocked(rowData.getPendingActionBlocked());
		dto.setChksum(rowData.getChecksum());
		dto.setGetCallDateTime(rowData.getGetCallDateTime());
		dto.setImsUniqueId(rowData.getImsUniqueID());
		dto.setAvailableInIms(rowData.getAvailableInIms());
		dto.setFileId(fileId);
		dto.setIsDelete(false);
		dto.setErrorDescription(rowData.getErrorDesc());
		dto.setCreatedBy("SYSTEM");
		dto.setCreatedOn(LocalDateTime.now());
		dto.setItcRedReq(rowData.getItcRedReq());
		dto.setDeclIgst(rowData.getDeclaredIgst());
		dto.setDeclCgst(rowData.getDeclaredCgst());
		dto.setDeclSgst(rowData.getDeclaredSgst());
		dto.setDeclCess(rowData.getDeclaredCess());

		return dto;
	}

	private BigDecimal convertStringToBigDecimal(String amt) {

		if (Strings.isNullOrEmpty(amt)) {
			return null;
		}
		return new BigDecimal((amt.trim()));
	}

	public LocalDateTime stringToTime(String dateTime,
			DateTimeFormatter formatter) {

		if (Strings.isNullOrEmpty(dateTime)) {
			return null;
		}
		dateTime = removeQuotes(dateTime);
		try {
			return LocalDateTime.parse(dateTime, formatter);
		} catch (Exception e) {

			try {
				return LocalDateTime.parse(dateTime, SUPPORTED_DATE_FORMAT8);
			} catch (Exception ex) {
				return null;
			}
		}

	}

	private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
			.appendPattern("dd-MM-yyyy HH:mm:ss").optionalStart()
			.appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, true)
			.optionalEnd().toFormatter();

	private Map<String, ImsHeaderDto> mapFromHeaderObjList(
			List<Object[]> headerObjList) {
		Map<String, ImsHeaderDto> map = new HashMap<>();

		for (Object[] objArr : headerObjList) {
			if (objArr[0] instanceof Long) {
				ImsHeaderDto dto = new ImsHeaderDto();
				dto.setId((Long) objArr[0]);
				dto.setRecipientGstin((String) objArr[1]);
				dto.setSupplierGstin((String) objArr[2]);
				dto.setSupplierLegalName((String) objArr[3]);
				dto.setSupplierTradeName((String) objArr[4]);
				dto.setInvoiceNumber((String) objArr[5]);
				dto.setInvoiceType((String) objArr[6]);
				dto.setInvoiceDate((Date) objArr[7]);
				dto.setAction((String) objArr[8]);
				dto.setIsPendingActionBlocked((String) objArr[9]);
				dto.setFormType((String) objArr[10]);
				dto.setReturnPeriod((String) objArr[11]);
				dto.setDerivedRetPeriod((Long) objArr[12]);
				dto.setFilingStatus((String) objArr[13]);
				dto.setInvoiceValue((BigDecimal) objArr[14]);
				dto.setTaxableValue((BigDecimal) objArr[15]);
				dto.setIgstAmt((BigDecimal) objArr[16]);
				dto.setCgstAmt((BigDecimal) objArr[17]);
				dto.setSgstAmt((BigDecimal) objArr[18]);
				dto.setCessAmt((BigDecimal) objArr[19]);
				dto.setPos((String) objArr[20]);
				dto.setChksum((String) objArr[21]);
				dto.setDocKey((String) objArr[22]);
				dto.setCreatedOn((LocalDateTime) objArr[23]);
				dto.setTableType((String) objArr[24]);
				if (objArr[26] != null && !objArr[26].toString().isEmpty()) {
					dto.setOrgInvoiceDate((Date) objArr[26]);
				}
				dto.setOrgInvoiceNumber((String) objArr[25]);
				dto.setGstnInvType((String) objArr[27]);
				dto.setLinkingDocKey(
						objArr[28] != null && !objArr[28].toString().isEmpty()
								? (String) objArr[28]
								: null);
				// dto.setRemark((String) objArr[29]);
				map.put(dto.getTableType() + "-" + dto.getId(), dto);

			}
		}
		return map;
	}

	private String getTableTypeFromImsUniqueId(String uniqueId) {

		if (uniqueId != null) {

			String[] idStrArr = uniqueId.split("-");
			if (idStrArr.length > 1) {
				try {
					String tableType = idStrArr[0];
					LOGGER.debug("TableType is  {}", tableType);
					return tableType;

				} catch (Exception e) {
					LOGGER.error("TableType is invalid {}", e);
					return "InvalidTableType";
				}
			}

		}
		return "InvalidTableType";
	}

	private Long getImsId(String uniqueId) {
		return Long.parseLong(uniqueId.split("-")[1]);

	}

	public static boolean isPresent(Object obj) {

		if (obj == null)
			return false;
		if (obj instanceof String) {
			return !(obj.toString()).trim().isEmpty();
		}
		return true;
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * String docKey =
	 * "33GSPTN0481G1ZA|33GSPTN0482G1Z9|QA-114316-M104|15-03-2024|INV|B2B";
	 * 
	 * String[] docKeyArr = docKey.split("\\|");
	 * 
	 * StringBuilder saveDocKey = new StringBuilder();
	 * 
	 * // String savedocKey = stin + "|" + rtin "|"+ docNum + "|" + taxPeriod;
	 * 
	 * saveDocKey.append(docKeyArr[0]).append(DOC_KEY_JOINER)
	 * .append(docKeyArr[1]).append(DOC_KEY_JOINER)
	 * .append(docKeyArr[2]).append(DOC_KEY_JOINER) .append("052020");
	 * 
	 * System.out.println(saveDocKey);
	 * 
	 * }
	 */

}
