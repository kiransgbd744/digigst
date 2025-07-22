/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
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
import jakarta.persistence.PersistenceContext;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2InwardDocRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysErrorRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysPSDRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysStageRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
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
@Component("Revarsal180DaysUploadFileArrivalServiceImpl")
public class Revarsal180DaysUploadFileArrivalServiceImpl
		implements Revarsal180DaysUploadFileArrivalService {
	List<String> persistedDockeys = null;

	private static final String DOC_KEY_JOINER = "|";

	@Autowired
	@Qualifier("Gstr2InwardDocRepository")
	private Gstr2InwardDocRepository gstr2InwardDocRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	private static String[] headerArray = { "ActionType", "CustomerGSTIN",
			"SupplierGSTIN", "SupplierName", "SupplierCode", "DocumentType",
			"DocumentNumber", "DocumentDate", "InvoiceValue",
			"Statutory Deductions Applicable", "StatutoryDeductionAmount",
			"AnyOtherDeductionAmount", "RemarksforDeductions",
			"DueDateofPayment", "PaymentReferenceNumber",
			"PaymentReferenceDate", "PaymentDescription",
			"PaymentStatus(FullorPartial)", "PaidAmounttoSupplier",
			"CurrencyCode", "ExchangeRate", "UnpaidAmounttoSupplier",
			"PostingDate", "PlantCode", "ProfitCentre", "Division",
			"UserDefinedField1", "UserDefinedField2", "UserDefinedField3" };

	private static List<String> HEADER_LIST = Arrays.asList(headerArray);

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Reversal180StructuralValidation")
	private Reversal180StructuralValidation structuralValidation;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("Reversal180DaysPSDRepository")
	private Reversal180DaysPSDRepository psdRepo;

	@Autowired
	@Qualifier("Reversal180DaysErrorRepository")
	private Reversal180DaysErrorRepository errRepo;

	@Autowired
	@Qualifier("Reversal180DaysStageRepository")
	private Reversal180DaysStageRepository stgRepo;

	@Autowired
	@Qualifier("Reversal180DaysPSDRepository")
	Reversal180DaysPSDRepository reversal180DaysPSDRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	private static final String paramValKeyId = "I41";

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
					"Exception occured in reading File GSTR1GSTINDelete Service Impl",
					e);
			throw new AppException(
					"Error occured while " + "reading the file " + fileName, e);
		}
		return inputStream;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {
			
			InputStream fin = getFileInpStream(fileName, folderName);
			/*String fileName1 = "180_Days_Payment_Reference - 33GSPTN0481G1ZA_012023 - CAN.csv";
			String folderName1 ="C:/Users/QE793HL/OneDrive - EY/Desktop/sameers docs/" + fileName1;
			
			File initialFile = new File(folderName1);
		    InputStream fin = new FileInputStream(initialFile);*/
		    
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 29);

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

			if (actualHeaderNames.size() != 29) {
				String msg = "The number of columns in the file should be 29. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
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
			String folderName, Long entityId) {

		LOGGER.debug("Validating user uploaded header with template header");

		try {
			// reading row data
			/*validateHeaders(fileName, folderName, fileId);
			String fileName1 = "180_Days_Payment_Reference - 33GSPTN0481G1ZA_012023 - CAN.csv";
			String folderName1 ="C:/Users/QE793HL/OneDrive - EY/Desktop/sameers docs/" + fileName1;
			
			File initialFile = new File(folderName1);
		    InputStream fin = new FileInputStream(initialFile);*/
			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(29);

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

			// saving to varchar table
			List<Reversal180DaysStageEntity> dumpReconds = fileList.stream()
					.map(o -> convertToStageEntity(o, fileId))
					.collect(Collectors.toList());

			List<String> docKeyList = dumpReconds.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			// dump softDelete
			dumpDoftDelete(docKeyList);
			// save
			stgRepo.saveAll(dumpReconds);

			Integer errorCount = 0;
			Integer psdCount = 0;

			boolean flag = true;

			Long ifFifoSelected = entityConfigPemtRepo
					.getIfFifoSelected(entityId, paramValKeyId);

			if (ifFifoSelected == null) {
				flag = false;
			}

			final boolean fifoFlag = flag;
			// validation and adding error code
			Triplet<List<Revarsal180DaysUploadDto>, Integer, Integer> entityTriplet = validation(
					fileList, fileId, errorCount, psdCount, entityId, flag);

			List<Revarsal180DaysUploadDto> entityList = entityTriplet
					.getValue0();
			psdCount = entityTriplet.getValue1();
			errorCount = entityTriplet.getValue2();

			List<String> inwardDocKeyList = entityList.stream()
					.filter(o -> o.isPsd()).map(o -> o.getInwardDocKey())
					.collect(Collectors.toCollection(ArrayList::new));
			
			List<String> canDocKeyList = entityList.stream()
					.filter(o -> o.isPsd() && "CAN".equalsIgnoreCase(o.getActionType()) ).map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			LOGGER.debug("InwardDocKeyList {} ", inwardDocKeyList);

			List<Revarsal180DaysUploadDto> cancelDocsList = entityList.stream()
					.filter(o -> o.getActionType().equalsIgnoreCase("CAN"))
					.collect(Collectors.toList());
			
			LOGGER.debug("CanDocKeyList {} ", cancelDocsList);

			List<String> persistedFinalDockeys = new ArrayList<>();

			if (!cancelDocsList.isEmpty()) {
				List<String> persistedCanDockeys = getPersistedCanDockeys(
						canDocKeyList);
				persistedFinalDockeys.addAll(persistedCanDockeys);
			}
			
			LOGGER.debug("Persisted Final DocKeys {} ", persistedFinalDockeys);

			
			List<Revarsal180DaysUploadDto> newDocsList = entityList.stream()
					.filter(o -> !o.getActionType().equalsIgnoreCase("CAN"))
					.collect(Collectors.toList());

			if (!newDocsList.isEmpty()) {
				List<String> persistedDockeys = getPersistedDockeys(
						inwardDocKeyList);
				persistedFinalDockeys.addAll(persistedDockeys);
			}

			// creating psd list
			List<Reversal180DaysPSDEntity> psdEntityList = entityList.stream()
					.filter(o -> o.isPsd()).map(o -> convertToPsdList(o, fileId,
							persistedFinalDockeys, fifoFlag))
					.collect(Collectors.toList());

			List<Reversal180DaysPSDEntity> processedCount = psdEntityList
					.stream().filter(o -> o.isPsd())
					.collect(Collectors.toList());

			List<Reversal180DaysPSDEntity> psdErrorCount = psdEntityList
					.stream().filter(o -> !o.isPsd())
					.collect(Collectors.toList());

			List<String> psdDocKeyList = entityList.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			// creating Error list
			List<Reversal180DaysErrorEntity> errEntityList = entityList.stream()
					.filter(o -> !o.isPsd())
					.map(o -> convertToErrorList(o, fileId))
					.collect(Collectors.toList());

			List<String> errDocKeyList = entityList.stream().distinct()
					.map(o -> o.getDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			// making isDelete flag true before saving in psd table
			LOGGER.debug("about to psd SoftDelete fileId {} ", fileId);
			softDelete(psdDocKeyList, true);
			LOGGER.debug("psd SoftDeleted fileId {} ", fileId);

			// making isDelete flag true before saving in Error table
			LOGGER.debug("about to Error SoftDelete fileId {} ", fileId);
			softDelete(errDocKeyList, false);
			LOGGER.debug("error SoftDeleted fileId {} ", fileId);

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
			fileStatusRepository.updateCountSummary(fileId, entityList.size(),
					processedCount.size(), errorCount + psdErrorCount.size());
			LOGGER.debug("updated count summry for fileId {}, About to "
					+ "update the user response", fileId);
			fileStatusRepository.updateFileStatus(fileId, "Processed");

		} catch (Exception ex) {

			String msg = "Failed, error while reading file.";
			ex.printStackTrace();
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

	private Triplet<List<Revarsal180DaysUploadDto>, Integer, Integer> validation(
			List<Object[]> fileList, Long fileId, Integer errorCount,
			Integer psdCount, Long entityId, boolean flag) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Revarsal180DaysUploadFileArrivalServiceImpl "
					+ "Validation method ";
			LOGGER.debug(msg);
		}
		List<ProcessingResult> validationResult = null;

		List<Revarsal180DaysUploadDto> processedEntityList = new ArrayList<>();

		// gstin info list
		List<String> gstinList = gSTNDetailRepository.findAllGstin();

		Map<String, Revarsal180DaysUploadDto> processedMap = new HashMap<>();

		Map<String, List<Revarsal180DaysUploadDto>> errorMap = new HashMap<>();

		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();
			String errorMessage = null;
			String errorCodes = null;

			structuralValidation.rowDataValidation(validationResult, rowData,
					gstinList, flag);

			List<String> errorArrayList = validationResult.stream()
					.filter(result -> result.getDescription() != null)
					.map(ProcessingResult::getDescription)
					.collect(Collectors.toList());

			List<String> errorCodeList = validationResult.stream()
					.filter(result -> result.getCode() != null)
					.map(ProcessingResult::getCode)
					.collect(Collectors.toList());

			String[] stringArray = new String[29];

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
						fileId, true, errorCodes, errorMessage, flag);

			} else {
				makeProcessedEntityList(stringArray, processedMap, errorMap,
						fileId, false, errorCodes, errorMessage, flag);

			}
		}

		processedMap.forEach((k, v) -> processedEntityList.add(v));
		errorMap.forEach((k, v) -> processedEntityList.addAll(v));

		List<Revarsal180DaysUploadDto> psdList = processedEntityList.stream()
				.filter(result -> result.isPsd()).collect(Collectors.toList());

		psdCount = psdList.size();
		errorCount = processedEntityList.size() - psdCount;

		return new Triplet<>(processedEntityList, psdCount, errorCount);
	}

	String userName = SecurityContext.getUser() != null
			? (SecurityContext.getUser().getUserPrincipalName() != null
					? SecurityContext.getUser().getUserPrincipalName()
					: "SYSTEM")
			: "SYSTEM";

	private void makeProcessedEntityList(String[] arr,
			Map<String, Revarsal180DaysUploadDto> processedMap,
			Map<String, List<Revarsal180DaysUploadDto>> errorMap, Long fileId,
			boolean isProceesedFlag, String errorCodes, String errorMessage,
			boolean flag) {

		Revarsal180DaysUploadDto entity = new Revarsal180DaysUploadDto();

		String custGstin = arr[1] != null ? arr[1].toString().trim() : null;
		String suppGstin = arr[2] != null ? arr[2].toString().trim() : null;
		String docType = arr[5] != null ? arr[5].toString().trim() : null;
		String docNum = arr[6] != null ? arr[6].toString().trim() : null;
		String docDate = arr[7] != null ? arr[7].toString().trim() : null;
		String fy = GenUtil.getFinYear(DateUtil.parseObjToDate(docDate));
		String paymentRefNum = arr[14] != null ? arr[14].toString().trim()
				: null;

		String paymentRefDate = arr[15] != null ? arr[15].toString().trim()
				: null;
		LOGGER.debug("Revarsal180DaysUploadDto paymentRefDate {}",
				paymentRefDate);

		LOGGER.debug(
				"Revarsal180DaysUploadDto DateUtil"
						+ ".parseObjToDate(paymentRefDate) {}",
				DateUtil.parseObjToDate(paymentRefDate));

		String payRefDateFormatted = paymentRefDate != null
				&& !paymentRefDate.isEmpty()
						? DateUtil.parseObjToDate(paymentRefDate).toString()
						: null;

		String[] paymentRefDateArray = payRefDateFormatted != null
				? payRefDateFormatted.split("T") : null;
		String paymentRefDateKey = paymentRefDateArray != null
				? paymentRefDateArray[0] : null;
		String docKey = createDocKey(docType, docNum, fy.toString(), suppGstin,
				custGstin, paymentRefNum, paymentRefDateKey);

		String inwardDocKey = createInwardDocKey(fy.toString(), custGstin,
				suppGstin, docType, docNum);
		entity.setInwardDocKey(inwardDocKey);
		entity.setActionType(arr[0] != null ? arr[0].toString().trim() : null);
		entity.setCustomerGSTIN(custGstin);
		entity.setSupplierGSTIN(suppGstin);
		entity.setSupplierName(
				arr[3] != null ? arr[3].toString().trim() : null);
		entity.setSupplierCode(
				arr[4] != null ? arr[4].toString().trim() : null);
		entity.setDocumentType(docType);
		entity.setDocumentNumber(docNum);
		entity.setDocumentDate(docDate);
		entity.setInvoiceValue(arr[8] != null ? arr[8].toString() : null);
		String statutoryDeductionApplicable = arr[9] != null
				? arr[9].toString().trim() : null;

		if (statutoryDeductionApplicable == null
				|| statutoryDeductionApplicable.isEmpty()) {
			entity.setStatutoryDeductionsApplicable("N");
		} else {
			entity.setStatutoryDeductionsApplicable(
					statutoryDeductionApplicable);
		}
		entity.setStatutoryDeductionAmount(
				arr[10] != null ? arr[10].toString().trim() : null);
		entity.setAnyOtherDeductionAmount(
				arr[11] != null ? arr[11].toString().trim() : null);
		entity.setRemarksforDeductions(
				arr[12] != null ? arr[12].toString().trim() : null);
		entity.setDueDateofPayment(arr[13] != null ? arr[13].toString() : null);
		entity.setPaymentReferenceNumber(paymentRefNum);
		entity.setPaymentReferenceDate(paymentRefDateKey);
		entity.setPaymentDescription(
				arr[16] != null ? arr[16].toString().trim() : null);
		String paymentStatus = arr[17] != null ? arr[17].toString().trim()
				: null;
		if (paymentStatus == null || paymentStatus.isEmpty()) {
			entity.setPaymentStatus("FP");
		} else {
			entity.setPaymentStatus(paymentStatus);
		}
		entity.setPaidAmounttoSupplier(
				arr[18] != null ? arr[18].toString() : null);
		entity.setCurrencyCode(
				arr[19] != null ? arr[19].toString().trim() : null);
		entity.setExchangeRate(
				arr[20] != null ? arr[20].toString().trim() : null);
		entity.setUnpaidAmounttoSupplier(
				arr[21] != null ? arr[21].toString() : null);
		entity.setPostingDate(arr[22] != null ? arr[22].toString() : null);
		entity.setPlantCode(arr[23] != null ? arr[23].toString().trim() : null);
		entity.setProfitCentre(
				arr[24] != null ? arr[24].toString().trim() : null);
		entity.setDivision(arr[25] != null ? arr[25].toString().trim() : null);
		entity.setUserDefinedField1(
				arr[26] != null ? arr[26].toString().trim() : null);
		entity.setUserDefinedField2(
				arr[27] != null ? arr[27].toString().trim() : null);
		entity.setUserDefinedField3(
				arr[28] != null ? arr[28].toString().trim() : null);
		entity.setErrorCode(errorCodes);
		entity.setErrorDesc(errorMessage);

		entity.setDocKey(docKey);
		entity.setFileId(fileId);
		entity.setPsd(isProceesedFlag);

		String errorCode = "";
		String errorMsg = "";

		if (isProceesedFlag && flag) {
			if ((StringUtils.isBlank(docType) && StringUtils.isBlank(docNum)
					&& StringUtils.isBlank(docDate))
					|| (!StringUtils.isBlank(docType)
							&& !StringUtils.isBlank(docNum)
							&& !StringUtils.isBlank(docDate))) {

				entity.setPsd(true);

			} else {
				errorCode = "ER-108-1";
				errorMsg = "Either Document Number, Document Type and Document "
						+ "Date all three should be present or all "
						+ "three should be absent for FIFO";
				entity.setPsd(false);
				entity.setErrorCode(errorCode);
				entity.setErrorDesc(errorMsg);
			}
		}

		if (isProceesedFlag) {
			if (processedMap.containsKey(docKey)) {
				Revarsal180DaysUploadDto existingEntity = processedMap
						.get(docKey);
				existingEntity.setPsd(false);
				if (!errorCode.isEmpty()) {
					errorCode = errorCode + "," + "ER-108";
					errorMsg = errorMsg + "," + "Duplicate Invoice key is not "
							+ "allowed in the same file/payload";
				} else {
					errorCode = "ER-108";
					errorMsg = "Duplicate Invoice key is not "
							+ "allowed in the same file/payload";
				}
				existingEntity.setErrorCode(errorCode);
				existingEntity.setErrorDesc(errorMsg);
				errorMap.computeIfAbsent(docKey,
						obj -> new ArrayList<Revarsal180DaysUploadDto>())
						.add(existingEntity);
				processedMap.put(docKey, entity);
			} else {
				processedMap.put(docKey, entity);
			}
		} else {
			errorMap.computeIfAbsent(docKey,
					obj -> new ArrayList<Revarsal180DaysUploadDto>())
					.add(entity);
		}

	}

	private void softDelete(List<String> docKeys, boolean flag) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = 0;
			if (flag) {
				rowsEffected = psdRepo.updateIsDeleteFlag(chunk);
			} else {
				rowsEffected = errRepo.updateIsDeleteFlag(chunk);
			}
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in " + "TBL_180_DAYS_REVERSAL table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private void dumpDoftDelete(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = stgRepo.updateIsDeleteFlag(chunk);
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in " + "TBL_180_DAYS_REVERSAL table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	// Doc Key - Document Type + Document No. + FY (Based on Doc Date) +
	// SupplierGSTIN + Customer GSTIN + Payment Reference No. + Payment
	// Reference Date

	private String createDocKey(String docType, String docNum, String finyear,
			String suppGstin, String CustGstin, String paymentRefNum,
			String paymentrefDate) {

		return new StringJoiner(DOC_KEY_JOINER).add(docType).add(docNum)
				.add(finyear).add(suppGstin).add(CustGstin).add(paymentRefNum)
				.add(paymentrefDate).toString();
	}

	private Reversal180DaysStageEntity convertToStageEntity(Object[] arr,
			Long fileId) {

		Reversal180DaysStageEntity entity = new Reversal180DaysStageEntity();

		String custGstin = arr[1] != null
				? (Strings.isNullOrEmpty(arr[1].toString().trim()) != true
						? arr[1].toString().trim() : null)
				: null;
		String suppGstin = arr[2] != null
				? (Strings.isNullOrEmpty(arr[2].toString().trim()) != true
						? arr[2].toString().trim() : null)
				: null;
		String docType = arr[5] != null
				? (Strings.isNullOrEmpty(arr[5].toString().trim()) != true
						? arr[5].toString().trim() : null)
				: null;
		String docNum = arr[6] != null
				? (Strings.isNullOrEmpty(arr[6].toString().trim()) != true
						? arr[6].toString().trim() : null)
				: null;
		String docDate = arr[7] != null
				? (Strings.isNullOrEmpty(arr[7].toString().trim()) != true
						? arr[7].toString().trim() : null)
				: null;
		String fy = GenUtil.getFinYear(DateUtil.parseObjToDate(docDate));
		String paymentRefNum = arr[14] != null
				? (Strings.isNullOrEmpty(arr[14].toString().trim()) != true
						? arr[14].toString().trim() : null)
				: null;
		String paymentRefDate = arr[15] != null
				? (Strings.isNullOrEmpty(arr[15].toString().trim()) != true
						? arr[15].toString().trim() : null)
				: null;
		String[] paymentRefDateArray = paymentRefDate != null
				? paymentRefDate.split("T") : null;
		String paymentRefDateKey = paymentRefDateArray != null
				? paymentRefDateArray[0] : null;

		String docKey = createDocKey(docType, docNum, fy.toString(), suppGstin,
				custGstin, paymentRefNum, paymentRefDateKey);

		entity.setActionType(arr[0] != null
				? (Strings.isNullOrEmpty(arr[0].toString().trim()) != true
						? arr[0].toString().trim() : null)
				: null);
		entity.setCustomerGSTIN(custGstin);
		entity.setSupplierGSTIN(suppGstin);
		entity.setSupplierName(arr[3] != null
				? (Strings.isNullOrEmpty(arr[3].toString().trim()) != true
						? arr[3].toString().trim() : null)
				: null);
		entity.setSupplierCode(arr[4] != null
				? (Strings.isNullOrEmpty(arr[4].toString().trim()) != true
						? arr[4].toString().trim() : null)
				: null);
		entity.setDocumentType(docType);
		entity.setDocumentNumber(docNum);
		entity.setDocumentDate(docDate);
		entity.setInvoiceValue(arr[8] != null
				? (Strings.isNullOrEmpty(arr[8].toString().trim()) != true
						? arr[8].toString().trim() : null)
				: null);

		String statutoryDeductionApplicable = arr[9] != null
				? (Strings.isNullOrEmpty(arr[9].toString().trim()) != true
						? arr[9].toString().trim() : null)
				: null;

		if (statutoryDeductionApplicable == null
				|| statutoryDeductionApplicable.isEmpty()) {
			entity.setStatutoryDeductionsApplicable("N");
		} else {
			entity.setStatutoryDeductionsApplicable(
					statutoryDeductionApplicable);
		}
		entity.setStatutoryDeductionAmount(arr[10] != null
				? (Strings.isNullOrEmpty(arr[10].toString().trim()) != true
						? arr[10].toString().trim() : null)
				: null);
		entity.setAnyOtherDeductionAmount(arr[11] != null
				? (Strings.isNullOrEmpty(arr[11].toString().trim()) != true
						? arr[11].toString().trim() : null)
				: null);
		entity.setRemarksforDeductions(arr[12] != null
				? (Strings.isNullOrEmpty(arr[12].toString().trim()) != true
						? arr[12].toString().trim() : null)
				: null);
		entity.setDueDateofPayment(arr[13] != null
				? (Strings.isNullOrEmpty(arr[13].toString().trim()) != true
						? arr[13].toString().trim() : null)
				: null);
		entity.setPaymentReferenceNumber(paymentRefNum);
		entity.setPaymentReferenceDate(paymentRefDate);
		entity.setPaymentDescription(arr[16] != null
				? (Strings.isNullOrEmpty(arr[16].toString().trim()) != true
						? arr[16].toString().trim() : null)
				: null);
		String paymentStatus = arr[17] != null
				? (Strings.isNullOrEmpty(arr[17].toString().trim()) != true
						? arr[17].toString().trim() : null)
				: null;
		if (paymentStatus == null || paymentStatus.isEmpty()) {
			entity.setPaymentStatus("FP");
		} else {
			entity.setPaymentStatus(paymentStatus);
		}
		entity.setPaidAmounttoSupplier(arr[18] != null
				? (Strings.isNullOrEmpty(arr[18].toString().trim()) != true
						? arr[18].toString().trim() : null)
				: null);
		entity.setCurrencyCode(arr[19] != null
				? (Strings.isNullOrEmpty(arr[19].toString().trim()) != true
						? arr[19].toString().trim() : null)
				: null);
		entity.setExchangeRate(arr[20] != null
				? (Strings.isNullOrEmpty(arr[20].toString().trim()) != true
						? arr[20].toString().trim() : null)
				: null);
		entity.setUnpaidAmounttoSupplier(arr[21] != null
				? (Strings.isNullOrEmpty(arr[21].toString().trim()) != true
						? arr[21].toString().trim() : null)
				: null);
		entity.setPostingDate(arr[22] != null
				? (Strings.isNullOrEmpty(arr[22].toString().trim()) != true
						? arr[22].toString().trim() : null)
				: null);
		entity.setPlantCode(arr[23] != null
				? (Strings.isNullOrEmpty(arr[23].toString().trim()) != true
						? arr[23].toString().trim() : null)
				: null);
		entity.setProfitCentre(arr[24] != null
				? (Strings.isNullOrEmpty(arr[24].toString().trim()) != true
						? arr[24].toString().trim() : null)
				: null);
		entity.setDivision(arr[25] != null
				? (Strings.isNullOrEmpty(arr[25].toString().trim()) != true
						? arr[25].toString().trim() : null)
				: null);
		entity.setUserDefinedField1(arr[26] != null
				? (Strings.isNullOrEmpty(arr[26].toString().trim()) != true
						? arr[26].toString().trim() : null)
				: null);
		entity.setUserDefinedField2(arr[27] != null
				? (Strings.isNullOrEmpty(arr[27].toString().trim()) != true
						? arr[27].toString().trim() : null)
				: null);
		entity.setUserDefinedField3(arr[28] != null
				? (Strings.isNullOrEmpty(arr[28].toString().trim()) != true
						? arr[28].toString().trim() : null)
				: null);
		entity.setDocKey(docKey);
		entity.setFileId(fileId);
		entity.setCreatedBy(userName);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setUpdatedBy(userName);
		entity.setUpdatedOn(LocalDateTime.now());
		entity.setActive(true);
		return entity;
	}

	private Reversal180DaysPSDEntity convertToPsdList(
			Revarsal180DaysUploadDto dto, Long fileId,
			List<String> persistedDockeys, boolean flag) {

		Reversal180DaysPSDEntity entity = new Reversal180DaysPSDEntity();
		entity.setActionType(dto.getActionType());
		entity.setCustomerGSTIN(dto.getCustomerGSTIN());
		entity.setSupplierGSTIN(dto.getSupplierGSTIN());
		entity.setSupplierName(dto.getSupplierName());
		entity.setSupplierCode(dto.getSupplierCode());
		entity.setDocumentType(dto.getDocumentType());
		entity.setDocumentNumber(dto.getDocumentNumber());

		LocalDate docDate = dto.getDocumentDate() != null
				? DateUtil.parseObjToDate(dto.getDocumentDate()) : null;
		entity.setDocumentDate(docDate);

		BigDecimal totalInvBal = dto.getInvoiceValue() != null
				&& !dto.getInvoiceValue().isEmpty()
						? new BigDecimal(dto.getInvoiceValue()) : null;
		entity.setInvoiceValue(totalInvBal);

		entity.setStatutoryDeductionsApplicable(
				dto.getStatutoryDeductionsApplicable());

		BigDecimal statutoryDeductionAmount = dto
				.getStatutoryDeductionAmount() != null
				&& !dto.getStatutoryDeductionAmount().isEmpty()
						? new BigDecimal(dto.getStatutoryDeductionAmount())
						: null;
		entity.setStatutoryDeductionAmount(statutoryDeductionAmount);

		BigDecimal anyOtherDeductionAmount = dto
				.getAnyOtherDeductionAmount() != null
				&& !dto.getAnyOtherDeductionAmount().isEmpty()
						? new BigDecimal(dto.getAnyOtherDeductionAmount())
						: null;
		entity.setAnyOtherDeductionAmount(anyOtherDeductionAmount);

		entity.setRemarksforDeductions(dto.getRemarksforDeductions());

		LocalDate dueDate = dto.getDueDateofPayment() != null
				? DateUtil.parseObjToDate(dto.getDueDateofPayment()) : null;
		entity.setDueDateofPayment(dueDate);
		entity.setPaymentReferenceNumber(dto.getPaymentReferenceNumber());

		LocalDate payRefDate = dto.getPaymentReferenceDate() != null
				? DateUtil.parseObjToDate(dto.getPaymentReferenceDate()) : null;
		entity.setPaymentReferenceDate(payRefDate);
		entity.setPaymentDescription(dto.getPaymentDescription());

		entity.setPaymentStatus(dto.getPaymentStatus());

		BigDecimal paidAmt = dto.getPaidAmounttoSupplier() != null
				&& !dto.getPaidAmounttoSupplier().isEmpty()
						? new BigDecimal(dto.getPaidAmounttoSupplier()) : null;
		entity.setPaidAmounttoSupplier(paidAmt);
		entity.setCurrencyCode(dto.getCurrencyCode());
		entity.setExchangeRate(dto.getExchangeRate());
		BigDecimal balAmt = dto.getUnpaidAmounttoSupplier() != null
				&& !dto.getUnpaidAmounttoSupplier().isEmpty()
						? new BigDecimal(dto.getUnpaidAmounttoSupplier())
						: null;
		entity.setUnpaidAmounttoSupplier(balAmt);
		LocalDate postingDate = dto.getPostingDate() != null
				? DateUtil.parseObjToDate(dto.getPostingDate()) : null;
		entity.setPostingDate(postingDate);
		entity.setPlantCode(dto.getPlantCode());
		entity.setProfitCentre(dto.getProfitCentre());
		entity.setDivision(dto.getDivision());
		entity.setUserDefinedField1(dto.getUserDefinedField1());
		entity.setUserDefinedField2(dto.getUserDefinedField2());
		entity.setUserDefinedField3(dto.getUserDefinedField3());
		//

		if (!flag) {

			DocHeaderValidation(dto, persistedDockeys, entity);
		}

		else if (flag && (!StringUtils.isBlank(dto.getDocumentType())
				&& !StringUtils.isBlank(dto.getDocumentDate())
				&& !StringUtils.isBlank(dto.getDocumentDate()))) {

			DocHeaderValidation(dto, persistedDockeys, entity);
		}

		entity.setDocKey(dto.getDocKey());
		entity.setFileId(fileId);
		entity.setCreatedBy(userName);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setUpdatedBy(userName);
		entity.setUpdatedOn(LocalDateTime.now());
		entity.setActive(true);
		return entity;
	}

	private void DocHeaderValidation(Revarsal180DaysUploadDto dto,
			List<String> persistedDockeys, Reversal180DaysPSDEntity entity) {
	
		if (persistedDockeys.contains(dto.getDocKey()) || persistedDockeys.contains(dto.getInwardDocKey())) {
			entity.setPsd(true);
		} 
		else {
			entity.setPsd(false);
			entity.setErrorCode("ER-110");
			entity.setErrorDesc("Original record is not available in DigiGST");
		}
	}

	private Reversal180DaysErrorEntity convertToErrorList(
			Revarsal180DaysUploadDto dto, Long fileId) {

		Reversal180DaysErrorEntity entity = new Reversal180DaysErrorEntity();
		entity.setActionType(dto.getActionType());
		entity.setCustomerGSTIN(dto.getCustomerGSTIN());
		entity.setSupplierGSTIN(dto.getSupplierGSTIN());
		entity.setSupplierName(dto.getSupplierName());
		entity.setSupplierCode(dto.getSupplierCode());
		entity.setDocumentType(dto.getDocumentType());
		entity.setDocumentNumber(dto.getDocumentNumber());
		String docDate = dto.getDocumentDate() != null
				? dto.getDocumentDate().toString() : null;
		entity.setDocumentDate(docDate);
		entity.setInvoiceValue(dto.getInvoiceValue());
		entity.setStatutoryDeductionsApplicable(
				dto.getStatutoryDeductionsApplicable());
		entity.setStatutoryDeductionAmount(dto.getStatutoryDeductionAmount());
		entity.setAnyOtherDeductionAmount(dto.getAnyOtherDeductionAmount());
		entity.setRemarksforDeductions(dto.getRemarksforDeductions());
		String dueDate = dto.getDueDateofPayment() != null
				? dto.getDueDateofPayment().toString() : null;
		entity.setDueDateofPayment(dueDate);
		entity.setPaymentReferenceNumber(dto.getPaymentReferenceNumber());
		String paymentRefDate = dto.getPaymentReferenceDate() != null
				? dto.getPaymentReferenceDate().toString() : null;
		entity.setPaymentReferenceDate(paymentRefDate);
		entity.setPaymentDescription(dto.getPaymentDescription());
		entity.setPaymentStatus(dto.getPaymentStatus());
		entity.setPaidAmounttoSupplier(dto.getPaidAmounttoSupplier());
		entity.setCurrencyCode(dto.getCurrencyCode());
		entity.setExchangeRate(dto.getExchangeRate());
		entity.setUnpaidAmounttoSupplier(dto.getUnpaidAmounttoSupplier());
		String postingDate = dto.getPostingDate() != null
				? dto.getPostingDate().toString() : null;
		entity.setPostingDate(postingDate);
		entity.setPlantCode(dto.getPlantCode());
		entity.setProfitCentre(dto.getProfitCentre());
		entity.setDivision(dto.getDivision());
		entity.setUserDefinedField1(dto.getUserDefinedField1());
		entity.setUserDefinedField2(dto.getUserDefinedField2());
		entity.setUserDefinedField3(dto.getUserDefinedField3());
		entity.setErrorCode(dto.getErrorCode());
		entity.setErrorDesc(dto.getErrorDesc());
		entity.setDocKey(dto.getDocKey());
		entity.setFileId(fileId);
		entity.setCreatedBy(userName);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setUpdatedBy(userName);
		entity.setUpdatedOn(LocalDateTime.now());
		entity.setActive(true);
		return entity;
	}

	private String createInwardDocKey(String finyear, String CustGstin,
			String suppGstin, String docType, String docNum) {

		return new StringJoiner(DOC_KEY_JOINER).add(finyear).add(CustGstin)
				.add(suppGstin).add(docType).add(docNum).toString();
	}

	private List<String> getPersistedDockeys(List<String> inwardDocKeyList) {

		List<String> fetcheddockeys = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(inwardDocKeyList, 2000);
		for (List<String> chunk : chunks) {
			List<String> fetcheddockeysChunks = gstr2InwardDocRepo
					.getActiveDocKeys(chunk);
			fetcheddockeys.addAll(fetcheddockeysChunks);
		}
		return fetcheddockeys;
	}

	private List<String> getPersistedCanDockeys(List<String> inwardDocKeyList) {

		List<String> fetcheddockeys = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(inwardDocKeyList, 2000);
		for (List<String> chunk : chunks) {
			List<String> fetcheddockeysChunks = reversal180DaysPSDRepository
					.getActiveCanDocKeys(chunk);
			fetcheddockeys.addAll(fetcheddockeysChunks);
		}
		return fetcheddockeys;
	}

}
