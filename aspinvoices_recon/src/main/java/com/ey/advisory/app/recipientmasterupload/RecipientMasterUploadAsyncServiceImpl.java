package com.ey.advisory.app.recipientmasterupload;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.RecipientMasterErrorReportRepository;
import com.ey.advisory.app.itcmatching.vendorupload.ErrorTypeDescription;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */

@Slf4j
@Component("RecipientMasterUploadAsyncServiceImpl")
public class RecipientMasterUploadAsyncServiceImpl
		implements RecipientMasterUploadAsyncService {

	private static final String UPLOAD = "Upload";

	private static final String ONLY_EMAIL_IDS_ARE_ACCEPTED = "Invalid Email Id";

	private static final String RECIPIENT_EMAIL_ID_CANNOT_BE_BLANK = "Recipient Primary EmailId cannot be blank";

	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\."
			+ "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
			+ "A-Z]{2,7}$";

	private static final List<String> validResponses = ImmutableList.of("y",
			"Y", "n", "N");

	private static final List<String> headerList = ImmutableList.of(
			"RecipientPAN", "RecipientGSTIN", "RecipientPrimaryE-MailID",
			"RecipientE-Mail-ID2", "RecipientE-Mail-ID3", "RecipientE-Mail-ID4",
			"RecipientE-Mail-ID5", "CCE-Mail-ID1", "CCE-Mail-ID2",
			"CCE-Mail-ID3", "CCE-Mail-ID4", "CCE-Mail-ID5", "GetGSTR2A-Email",
			"GetGSTR2B-Email", "ReturnComplianceStatus-Email", "DRC01B-Email",
			"DRC01C-Email", "Action");

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("RecipientMasterUploadRepository")
	private RecipientMasterUploadRepository recipientMasterUploadRepository;

	@Autowired
	@Qualifier("RecipientMasterErrorReportRepository")
	private RecipientMasterErrorReportRepository recipientMasterErrorReportRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	public void validateDataFileUpload(Long fileId, String fileName,
			String folderName) {

		LOGGER.debug("Validating user uploaded header with template header");

		Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepository
				.findById(fileId);

		if (!fileStatusEntity.isPresent()) {
			String errMsg = String.format(
					"No Record available for the file Name %s", fileName);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		try {
			String docId = fileStatusEntity.get().getDocId();

			InputStream fin = getFileInpStream(fileName, folderName, docId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(18);
			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(fin, layout, rowHandler, null);
			@SuppressWarnings("unchecked")
			List<String> actualHeaderNames = (List<String>) (List<?>) Arrays
					.asList(rowHandler.getHeaderRow());

			validateHeaders(fileId, actualHeaderNames);
			traverser.traverse(fin, layout, rowHandler, null);

			List<Object[]> fileList = rowHandler.getFileUploadList();

			if (CollectionUtils.isEmpty(fileList)) {
				String msg = "Failed Empty file..";
				LOGGER.error(msg);
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}
			LOGGER.debug(fileList.toString());
			// validating data by passing the List of Object which we are
			// getting from file
			validation(fileList, fileId);

		} catch (Exception ex) {
			markFileAsFailed(fileId, ex.getMessage());
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private void validation(List<Object[]> fileList, Long fileId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside RecipientMasterUploadAsyncServiceImpl.Validation()"
					+ "method ";
			LOGGER.debug(msg);
		}

		List<ProcessingResult> validationResult = null;
		Map<String, List<ProcessingResult>> errMap = new HashMap<>();

		Set<RecipientMasterUploadEntity> listProcessedDto = new HashSet<>();

		List<RecipientMasterErrorReportEntity> listErrorDto = new ArrayList<>();

		RecipientMasterUploadEntity masterUploadEntity = null;
		RecipientMasterErrorReportEntity errorReportEntity = null;
		fileList.toArray();
		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();

			masterUploadEntity = new RecipientMasterUploadEntity();

			errorReportEntity = new RecipientMasterErrorReportEntity();

			int num = validationResult.size();
			String errorMessage = null;
			String infoMessage = null;
			extractedValidationMethod(validationResult, rowData);
			List<String> errorArrayList = validationResult.stream()
					.filter(result -> result.getDescription() != null)
					.map(ProcessingResult::getDescription)
					.collect(Collectors.toList());
			List<String> infoArrayList = validationResult.stream()
					.filter(result -> result.getDescriptonForInfo() != null)
					.map(ProcessingResult::getDescriptonForInfo)
					.collect(Collectors.toList());
			String[] stringArray = new String[18];

			for (int i = 0; i < stringArray.length; i++) {
				if (i < rowData.length) {
					stringArray[i] = rowData[i] == null ? null
							: String.valueOf(rowData[i]);

				}
			}
			if (!CollectionUtils.isEmpty(errorArrayList))
				errorMessage = String.join(",", errorArrayList);
			if (!CollectionUtils.isEmpty(infoArrayList))
				infoMessage = String.join(",", infoArrayList);
			String key = createInvoiceKey(rowData);
			/**
			 * Set values for Vendor Master error report
			 */
			extractedMethodForDataSegregate(fileId, validationResult, errMap,
					listProcessedDto, listErrorDto, masterUploadEntity,
					errorReportEntity, rowData, num, errorMessage, infoMessage,
					stringArray, key);

		}

		if (!CollectionUtils.isEmpty(listProcessedDto)) {
			softDeleteDuplicateRecords(listProcessedDto);

			recipientMasterUploadRepository.saveAll(listProcessedDto);
		}

		if (!CollectionUtils.isEmpty(listErrorDto))
			recipientMasterErrorReportRepository.saveAll(listErrorDto);

		List<String> infoDescCount = new ArrayList<>();
		List<String> erroDescCount = new ArrayList<>();

		if (listErrorDto != null && !listErrorDto.isEmpty()) {

			listErrorDto.forEach(e -> {
				if (e.getError() != null && !e.getError().isEmpty()
						&& !e.getError().equalsIgnoreCase("null")) {
					erroDescCount.add(e.getError());
				} else if (e.getInformation() != null
						&& !e.getInformation().isEmpty()
						&& !e.getInformation().equalsIgnoreCase("null")) {
					infoDescCount.add(e.getInformation());
				}
			});
		}

		Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = fileStatusRepository
				.findById(fileId);
		if (gstr1FileStatusEntity.isPresent()) {
			gstr1FileStatusEntity.get().setProcessed(listProcessedDto.size());
			gstr1FileStatusEntity.get().setError(erroDescCount.size());
			gstr1FileStatusEntity.get().setInformation(infoDescCount.size());
			gstr1FileStatusEntity.get()
					.setTotal(listProcessedDto.size() + erroDescCount.size());
			gstr1FileStatusEntity.get()
					.setFileStatus(JobStatusConstants.PROCESSED);
			fileStatusRepository.save(gstr1FileStatusEntity.get());
		}

	}

	private void softDeleteDuplicateRecords(
			Set<RecipientMasterUploadEntity> listProcessedDto) {

		int softDeletedCount = 0;
		List<String> invKeysTobePersisted = new ArrayList<>();

		listProcessedDto.forEach(e -> {
			invKeysTobePersisted.add(e.getInvoiceKey());
		});

		List<List<String>> chunks = Lists.partition(invKeysTobePersisted, 2000);
		for (List<String> chunk : chunks) {

			int rowsEffected = recipientMasterUploadRepository
					.softDeleteDuplicateInv(chunk, EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
			softDeletedCount = softDeletedCount + rowsEffected;
		}

		LOGGER.debug("Soft Deleted Record  for Recipient Master Upload is {} ",
				softDeletedCount);

	}

	private void extractedValidationMethod(
			List<ProcessingResult> validationResult, Object[] rowData) {

		isValidRecipientPAN(rowData, validationResult);
		isValidRecipientGstin(rowData, validationResult);
		isValidRecipientPrimaryMailId(rowData, validationResult);
		isValidRecipientEmailId2(rowData, validationResult);
		isValidRecipientEmailId3(rowData, validationResult);
		isValidRecipientEmailId4(rowData, validationResult);
		isValidRecipientEmailId5(rowData, validationResult);
		isValidCceEmailId1(rowData, validationResult);
		isValidCceEmailId2(rowData, validationResult);
		isValidCceEmailId3(rowData, validationResult);
		isValidCceEmailId4(rowData, validationResult);
		isValidCceEmailId5(rowData, validationResult);
		isValidGetGSTR2AEmail(rowData, validationResult);
		isValidGetGSTR2BEmail(rowData, validationResult);
		isValidRetCompStatusEmail(rowData, validationResult);
		isValidDrc01bEmail(rowData, validationResult);
		isValidDrc01cEmail(rowData, validationResult);
		isValidAction(rowData, validationResult);
	}

	private void extractedMethodForDataSegregate(Long fileId,
			List<ProcessingResult> validationResult,
			Map<String, List<ProcessingResult>> errMap,
			Set<RecipientMasterUploadEntity> setProcessedDto,
			List<RecipientMasterErrorReportEntity> setErrorDto,
			RecipientMasterUploadEntity masterUploadEntity,
			RecipientMasterErrorReportEntity errorReportEntity,
			Object[] rowData, int num, String errorMessage, String infoMessage,
			String[] dummyArray, String key) {
		if (!validationResult.isEmpty()) {
			// if info errors then save in both the tables
			if (isOnlyInfoErrors(validationResult)) {
				extractedMethodToSaveErrorData(fileId, validationResult, errMap,
						setErrorDto, errorReportEntity, errorMessage,
						infoMessage, dummyArray, key);
				extractedMethodToSaveProcessedData(fileId, validationResult,
						setProcessedDto, masterUploadEntity, rowData,
						dummyArray, key, setErrorDto);
			}
			// if errors then save in error table
			else {
				extractedMethodToSaveErrorData(fileId, validationResult, errMap,
						setErrorDto, errorReportEntity, errorMessage,
						infoMessage, dummyArray, key);
			}
		} else {
			/// Set values for Vendor Master valid report

			extractedMethodToSaveProcessedData(fileId, validationResult,
					setProcessedDto, masterUploadEntity, rowData, dummyArray,
					key, setErrorDto);

		}
	}

	private boolean isOnlyInfoErrors(List<ProcessingResult> validationResult) {
		List<ProcessingResult> errorList = validationResult.stream().filter(
				result -> ProcessingResultType.ERROR.equals(result.getType()))
				.collect(Collectors.toList());
		return errorList.isEmpty();
	}

	private void extractedMethodToSaveErrorData(Long fileId,
			List<ProcessingResult> validationResult,
			Map<String, List<ProcessingResult>> errMap,
			List<RecipientMasterErrorReportEntity> setErrorDto,
			RecipientMasterErrorReportEntity errorReportEntity,
			String errorMessage, String infoMessage, String[] dummyArray,
			String key) {
		errMap.put(key, validationResult);
		if (errorMessage != null && !errorMessage.isEmpty()) {
			errorReportEntity.setError(errorMessage);
		}
		if (infoMessage != null && !infoMessage.isEmpty()) {
			errorReportEntity.setInformation(infoMessage);
		}
		if (errorReportEntity.getError() != null
				&& errorReportEntity.getInformation() == null) {
			errorReportEntity
					.setErrorTypeDeascription(ErrorTypeDescription.ERROR);
		} else if (errorReportEntity.getError() == null
				&& errorReportEntity.getInformation() != null) {
			errorReportEntity
					.setErrorTypeDeascription(ErrorTypeDescription.INFORMATION);
		} else {
			errorReportEntity.setErrorTypeDeascription(
					ErrorTypeDescription.ERRORANDINFORMATION);
		}
		errorReportEntity.setInvoiceKey(key);
		errorReportEntity.setRecipientPAN(dummyArray[0] != null
				? String.valueOf(dummyArray[0]).trim() : null);
		errorReportEntity.setRecipientGstin(dummyArray[1] != null
				? String.valueOf(dummyArray[1]).trim() : null);
		errorReportEntity.setRecipientPrimEmailId(dummyArray[2] != null
				? String.valueOf(dummyArray[2]).trim() : null);
		errorReportEntity.setRecipientEmailId2((dummyArray[3] != null)
				? String.valueOf(dummyArray[3]).trim() : null);
		errorReportEntity.setRecipientEmailId3((dummyArray[4] != null)
				? String.valueOf(dummyArray[4]).trim() : null);
		errorReportEntity.setRecipientEmailId4((dummyArray[5] != null)
				? String.valueOf(dummyArray[5]).trim() : null);
		errorReportEntity.setRecipientEmailId5((dummyArray[6] != null)
				? String.valueOf(dummyArray[6]).trim() : null);
		errorReportEntity.setCceEmailId1(dummyArray[7] != null
				? String.valueOf(dummyArray[7]).trim() : null);
		errorReportEntity.setCceEmailId2((dummyArray[8] != null)
				? String.valueOf(dummyArray[8]).trim() : null);
		errorReportEntity.setCceEmailId3((dummyArray[9] != null)
				? String.valueOf(dummyArray[9]).trim() : null);
		errorReportEntity.setCceEmailId4((dummyArray[10] != null)
				? String.valueOf(dummyArray[10]).trim() : null);
		errorReportEntity.setCceEmailId5((dummyArray[11] != null)
				? String.valueOf(dummyArray[11]).trim() : null);
		
		errorReportEntity.setIsGetGstr2AEmail((dummyArray[12] != null)
				? String.valueOf(dummyArray[12]).trim() : null);
		errorReportEntity.setIsGetGstr2BEmail((dummyArray[13] != null)
				? String.valueOf(dummyArray[13]).trim() : null);

		errorReportEntity.setIsRetCompStatusEmail((dummyArray[14] != null)
				? String.valueOf(dummyArray[14]).trim() : null);
		errorReportEntity.setIsDRC01BEmail((dummyArray[15] != null)
				? String.valueOf(dummyArray[15]).trim() : null);
		errorReportEntity.setIsDRC01CEmail((dummyArray[16] != null)
				? String.valueOf(dummyArray[16]).trim() : null);
		
		errorReportEntity.setIsDelete((dummyArray[17] != null)
				? String.valueOf(dummyArray[17]).trim() : null);
		errorReportEntity.setFileId(fileId);
		errorReportEntity.setCreatedOn(LocalDateTime.now());
		errorReportEntity.setCreatedBy(getUserName());
		errorReportEntity.setModifiedOn(LocalDateTime.now());
		errorReportEntity.setModifiedBy(getUserName());
		setErrorDto.add(errorReportEntity);
	}

	private boolean deleteCheckMethod(String[] dummyArray) {
		return dummyArray[17] != null && dummyArray[17].equalsIgnoreCase("D");
	}

	private void extractedMethodToSaveProcessedData(Long fileId,
			List<ProcessingResult> validationResult,
			Set<RecipientMasterUploadEntity> setProcessedDto,
			RecipientMasterUploadEntity masterUploadEntity, Object[] rowData,
			String[] dummyArray, String key,
			List<RecipientMasterErrorReportEntity> setErrorDto) {
		List<String> infoErrorList = validationResult.stream()
				.map(result -> result.getCode()).collect(Collectors.toList());

		masterUploadEntity.setInvoiceKey(key);
		masterUploadEntity.setRecipientPAN(String.valueOf(rowData[0]));
		masterUploadEntity.setRecipientGstin(String.valueOf(rowData[1]));
		masterUploadEntity.setRecipientPrimEmailId(String.valueOf(rowData[2]));

		if (!infoErrorList.contains("ER1006")) {
			masterUploadEntity.setRecipientEmailId2((rowData[3] != null)
					? String.valueOf(rowData[3]).trim() : null);
		}
		if (!infoErrorList.contains("ER1007")) {
			masterUploadEntity.setRecipientEmailId3((rowData[4] != null)
					? String.valueOf(rowData[4]).trim() : null);
		}
		if (!infoErrorList.contains("ER1008")) {
			masterUploadEntity.setRecipientEmailId4((rowData[5] != null)
					? String.valueOf(rowData[5]).trim() : null);
		}

		if (!infoErrorList.contains("ER1009")) {
			masterUploadEntity.setRecipientEmailId5((rowData[6] != null)
					? String.valueOf(rowData[6]).trim() : null);
		}

		if (!infoErrorList.contains("ER1010")) {
			masterUploadEntity.setCceEmailId1((rowData[7] != null)
					? String.valueOf(rowData[7]).trim() : null);
		}
		
		if (!infoErrorList.contains("ER1011")) {
			masterUploadEntity.setCceEmailId2((rowData[8] != null)
					? String.valueOf(rowData[8]).trim() : null);
		}
		
		if (!infoErrorList.contains("ER1012")) {
			masterUploadEntity.setCceEmailId3((rowData[9] != null)
					? String.valueOf(rowData[9]).trim() : null);
		}
		
		if (!infoErrorList.contains("ER1013")) {
			masterUploadEntity.setCceEmailId4((rowData[10] != null)
					? String.valueOf(rowData[10]).trim() : null);
		}
		
		if (!infoErrorList.contains("ER1014")) {
			masterUploadEntity.setCceEmailId5((rowData[11] != null)
					? String.valueOf(rowData[11]).trim() : null);
		}
		
		masterUploadEntity.setGetGstr2AEmail(
				String.valueOf(rowData[12]).equalsIgnoreCase("y"));
		masterUploadEntity.setGetGstr2BEmail(
				String.valueOf(rowData[13]).equalsIgnoreCase("y"));
		masterUploadEntity.setRetCompStatusEmail(
				String.valueOf(rowData[14]).equalsIgnoreCase("y"));
		masterUploadEntity.setDRC01BEmail(
				String.valueOf(rowData[15]).equalsIgnoreCase("y"));
		masterUploadEntity.setDRC01CEmail(
				String.valueOf(rowData[16]).equalsIgnoreCase("y"));
		
		
		masterUploadEntity.setDelete(deleteCheckMethod(dummyArray));
		masterUploadEntity.setFileId(fileId);
		masterUploadEntity.setCreatedOn(LocalDateTime.now());
		masterUploadEntity.setCreatedBy(getUserName());
		masterUploadEntity.setModifiedOn(LocalDateTime.now());
		masterUploadEntity.setModifiedBy(getUserName());

		if (setProcessedDto.contains(masterUploadEntity)) {
			Optional<RecipientMasterUploadEntity> existedObj = setProcessedDto
					.stream()
					.filter(eachObj -> eachObj.equals(masterUploadEntity))
					.findFirst();
			if (existedObj.isPresent()) {
				RecipientMasterErrorReportEntity dupRecord = new RecipientMasterErrorReportEntity();
				BeanUtils.copyProperties(existedObj.get(), dupRecord);
				dupRecord.setError("Duplicate");
				dupRecord.setErrorTypeDeascription(ErrorTypeDescription.ERROR);
				setErrorDto.add(dupRecord);
				setProcessedDto.remove(existedObj.get());
				setProcessedDto.add(masterUploadEntity);
			}
		} else {
			if (masterUploadEntity.isDelete()) {
				RecipientMasterUploadEntity persistedEntity = recipientMasterUploadRepository
						.findByInvoiceKeyAndIsDeleteFalse(
								masterUploadEntity.getInvoiceKey());
				if (persistedEntity != null && !persistedEntity.isDelete()) {
					persistedEntity.setDelete(true);
					persistedEntity.setModifiedOn(LocalDateTime.now());
					persistedEntity.setModifiedBy(getUserName());
					recipientMasterUploadRepository.save(persistedEntity);
					setProcessedDto.add(masterUploadEntity);
				} else {
					setProcessedDto.add(masterUploadEntity);
				}
			} else {
				setProcessedDto.add(masterUploadEntity);
			}
		}
	}

	private String getUserName() {
		return SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
	}

	private String createInvoiceKey(Object[] arr) {

		String recipientPan = (arr[0] != null) ? String.valueOf(arr[0]).trim()
				: "";

		String recipientGstin = (arr[1] != null) ? String.valueOf(arr[1]).trim()
				: "";

		return recipientPan + recipientGstin;
	}

	private void isValidAction(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String action = rowData[17] == null ? null
				: String.valueOf(rowData[17]);
		if (StringUtils.hasText(action)) {
			action = action.trim();
			if (!action.equalsIgnoreCase("d")) {
				String errMsg = "Invalid Action Type";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1020",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				return;
			}
		}
	}

	private void isValidDrc01cEmail(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String drc01cStatus = rowData[16] == null ? null
				: String.valueOf(rowData[16]);
		if (StringUtils.hasText(drc01cStatus)) {
			drc01cStatus = drc01cStatus.trim();
			if (!validResponses.contains(drc01cStatus)) {
				String errMsg = "Invalid Response";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1019",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				return;
			}
		}
	}
	
	private void isValidDrc01bEmail(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String drc01bStatus = rowData[15] == null ? null
				: String.valueOf(rowData[15]);
		if (StringUtils.hasText(drc01bStatus)) {
			drc01bStatus = drc01bStatus.trim();
			if (!validResponses.contains(drc01bStatus)) {
				String errMsg = "Invalid Response";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1018",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				return;
			}
		}
	}
	
	private void isValidRetCompStatusEmail(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String retCompStatus = rowData[14] == null ? null
				: String.valueOf(rowData[14]);
		if (StringUtils.hasText(retCompStatus)) {
			retCompStatus = retCompStatus.trim();
			if (!validResponses.contains(retCompStatus)) {
				String errMsg = "Invalid Response";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1017",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				return;
			}
		}
	}

	private void isValidGetGSTR2BEmail(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String getGstr2B = rowData[13] == null ? null
				: String.valueOf(rowData[13]);
		if (StringUtils.hasText(getGstr2B)) {
			getGstr2B = getGstr2B.trim();
			if (!validResponses.contains(getGstr2B)) {
				String errMsg = "Invalid Response";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1016",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				return;
			}
		}
	}

	
	private void isValidGetGSTR2AEmail(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String getGstr2A = rowData[12] == null ? null
				: String.valueOf(rowData[12]);
		if (StringUtils.hasText(getGstr2A)) {
			getGstr2A = getGstr2A.trim();
			if (!validResponses.contains(getGstr2A)) {
				String errMsg = "Invalid Response";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1015",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				return;
			}
		}
	}

	private void isValidCceEmailId5(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String cceEmailId5 = rowData[11] == null ? null
				: String.valueOf(rowData[11]);
		if (cceEmailId5 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(cceEmailId5);
			cceEmailId5 = cceEmailId5.trim();
			if ((StringUtils.hasText(cceEmailId5))
					&& (!matcher.matches() || cceEmailId5.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1014",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}
	
	private void isValidCceEmailId4(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String cceEmailId4 = rowData[10] == null ? null
				: String.valueOf(rowData[10]);
		if (cceEmailId4 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(cceEmailId4);
			cceEmailId4 = cceEmailId4.trim();
			if ((StringUtils.hasText(cceEmailId4))
					&& (!matcher.matches() || cceEmailId4.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1013",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}
	
	private void isValidCceEmailId3(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String cceEmailId3 = rowData[9] == null ? null
				: String.valueOf(rowData[9]);
		if (cceEmailId3 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(cceEmailId3);
			cceEmailId3 = cceEmailId3.trim();
			if ((StringUtils.hasText(cceEmailId3))
					&& (!matcher.matches() || cceEmailId3.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1012",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}
	
	private void isValidCceEmailId2(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String cceEmailId2 = rowData[8] == null ? null
				: String.valueOf(rowData[8]);
		if (cceEmailId2 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(cceEmailId2);
			cceEmailId2 = cceEmailId2.trim();
			if ((StringUtils.hasText(cceEmailId2))
					&& (!matcher.matches() || cceEmailId2.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1011",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}	

	private void isValidCceEmailId1(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String cceEmailId1 = rowData[7] == null ? null
				: String.valueOf(rowData[7]);
		if (cceEmailId1 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(cceEmailId1);
			cceEmailId1 = cceEmailId1.trim();
			if ((StringUtils.hasText(cceEmailId1))
					&& (!matcher.matches() || cceEmailId1.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1010",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}
	
	private void isValidRecipientEmailId5(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId5 = rowData[6] == null ? null
				: String.valueOf(rowData[6]);
		if (recipientEmailId5 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId5);
			recipientEmailId5 = recipientEmailId5.trim();
			if ((StringUtils.hasText(recipientEmailId5)) && (!matcher.matches()
					|| recipientEmailId5.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1009",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidRecipientEmailId4(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId4 = rowData[5] == null ? null
				: String.valueOf(rowData[5]);

		if (recipientEmailId4 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId4);
			recipientEmailId4 = recipientEmailId4.trim();
			if ((StringUtils.hasText(recipientEmailId4)) && (!matcher.matches()
					|| recipientEmailId4.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1008",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidRecipientEmailId3(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId3 = rowData[4] == null ? null
				: String.valueOf(rowData[4]);
		if (recipientEmailId3 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId3);
			recipientEmailId3 = recipientEmailId3.trim();
			if ((StringUtils.hasText(recipientEmailId3)) && (!matcher.matches()
					|| recipientEmailId3.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1007",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidRecipientEmailId2(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId2 = rowData[3] == null ? null
				: String.valueOf(rowData[3]);

		if (recipientEmailId2 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId2);
			recipientEmailId2 = recipientEmailId2.trim();
			if ((StringUtils.hasText(recipientEmailId2)) && (!matcher.matches()
					|| recipientEmailId2.length() > 50)) {
				String errMsg = ONLY_EMAIL_IDS_ARE_ACCEPTED;
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER1006",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidRecipientPrimaryMailId(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String emailRegex = EMAIL_REGEX;
		String recipientEmailId = rowData[2] == null ? null
				: String.valueOf(rowData[2]);

		if (!StringUtils.hasText(recipientEmailId)) {
			String errMsg = RECIPIENT_EMAIL_ID_CANNOT_BE_BLANK;
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER1004",
					errMsg);
			validationResult.add(result);
			return;
		}

		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(recipientEmailId);
		recipientEmailId = recipientEmailId.trim();
		if (!matcher.matches() || recipientEmailId.length() > 50) {
			String errMsg = "Invalid Recipient Primary Email Id";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER1005",
					errMsg);
			validationResult.add(result);
		}

	}

	private void isValidRecipientGstin(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String recipientGstin = rowData[1] == null ? null
				: String.valueOf(rowData[1]);
		if (!StringUtils.hasText(recipientGstin)) {
			String errMsg = "Recipient Gstin cannot be blank";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER1002",
					errMsg);
			validationResult.add(result);
			return;
		}

		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(recipientGstin);

		recipientGstin = recipientGstin.trim();
		if (!matcher.matches() || recipientGstin.length() != 15) {
			String errMsg = "Invalid Recipient GSTIN";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER1003",
					errMsg);
			validationResult.add(result);
		}

	}

	private void isValidRecipientPAN(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String recipientPAN = rowData[0] == null ? null
				: String.valueOf(rowData[0]);
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside ItcMatchingVendorDataFileUpld"
					+ ".isValidRecipientPAN() method,  " + "rowData : "
					+ rowData;
			LOGGER.debug(msg);
		}
		if (!StringUtils.hasText(recipientPAN)) {
			String errMsg = "Recipient PAN cannot be blank";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER1000",
					errMsg);
			validationResult.add(result);
			return;
		}
		String regex = "^[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(recipientPAN);

		recipientPAN = recipientPAN.trim();
		if (!matcher.matches() || recipientPAN.length() != 10) {
			String errMsg = "Invalid Recipeint PAN";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER1001",
					errMsg);
			validationResult.add(result);
		}

	}

	private void validateHeaders(Long fileId, List<String> actualHeaderNames) {
		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s "
								+ "and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != 18) {
				String msg = "The number of columns in the file should be 11. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = headerList;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected Recipient Master Upload data headers names "
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
				LOGGER.error(msg);
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			markFileAsFailed(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private boolean headersMatch(List<String> expected, List<String> actual) {
		return expected.equals(actual);
	}

	private void markFileAsFailed(Long fileId, String reason) {

		try {
			fileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = String
					.format("[SEVERE] Unable to mark the file as failed. "
							+ "Reason for file failure is: [ %s ]", reason);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private InputStream getFileInpStream(String fileName, String folderName,
			String docId) {

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
			// inputStream = document.getContentStream().getStream();

			/*
			 * Document doc = DocumentUtility.downloadDocument(fileName,
			 * folderName);
			 * LOGGER.debug("Downloaded Recipient Master DataFile Uploaded file"
			 * );
			 */
			return document.getContentStream().getStream();
		} catch (Exception ex) {
			throw new AppException(
					"Error occured while " + "reading the file " + fileName,
					ex);
		}
	}
}
