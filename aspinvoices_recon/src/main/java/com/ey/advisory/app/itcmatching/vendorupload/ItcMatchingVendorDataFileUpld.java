package com.ey.advisory.app.itcmatching.vendorupload;

import java.io.InputStream;
import java.math.BigDecimal;
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
import org.javatuples.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorApiUploadStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterErrorReportEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */

@Slf4j
@Component("ItcMatchingVendorDataFileUpld")
public class ItcMatchingVendorDataFileUpld {

	private static final String UPLOAD = "Upload";

	private static final String ONLY_EMAIL_IDS_ARE_ACCEPTED = "Invalid Email Id";

	private static final String VENDOR_EMAIL_ID_CANNOT_BE_BLANK = "Vendor EmailId cannot be blank";

	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\."
			+ "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
			+ "A-Z]{2,7}$";

	private static final List<String> validResponses = ImmutableList.of("y",
			"Y", "n", "N");

	private static final List<String> headerList = ImmutableList.of(
			"RecipientPAN", "VendorPAN", "VendorGSTIN", "SupplierCode",
			"VendorName", "VendorPrimaryE-MailID", "VendorPrimaryContactNumber",
			"VendorE-Mail-ID1", "VendorE-Mail-ID2", "VendorE-Mail-ID3",
			"VendorE-Mail-ID4", "RecipientE-Mail-ID1", "RecipientE-Mail-ID2",
			"RecipientE-Mail-ID3", "RecipientE-Mail-ID4", "RecipientE-Mail-ID5",
			"VendorType", "HSN", "VendorRiskCategory",
			"VendorPaymentTerms(Days)", "VendorRemarks", "Approval Status",
			"ExcludeVendor Remarks", "VendorCom", "ExcludeVendor",
			"NonComplaintCom", "CreditEligibility", "Action");

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("VendorMasterErrorReportEntityRepository")
	private VendorMasterErrorReportEntityRepository vendorMasterErrorReportEntityRepository;

	@Autowired
	@Qualifier("VendorApiUploadStatusRepository")
	VendorApiUploadStatusRepository apiStatusRepository;

	@Autowired
	@Qualifier("VendorMasterApiPayloadRepository")
	private VendorMasterApiPayloadRepository payloadRepo;

	@Autowired
	@Qualifier("DefaultVendorTypeCache")
	private VendorTypeCache vendorTypeCache;

	public void validateDataFileUpload(Long fileId, String fileName,
			String folderName) {

		LOGGER.debug("Validating user uploaded header with template header");

		try {

			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(28);
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
			validation(fileList, fileId, null, null);

		} catch (Exception ex) {
			markFileAsFailed(fileId, ex.getMessage());
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void validation(List<Object[]> fileList, Long fileId, String refId,
			String payloadId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside ItcMatchingVendorDataFileUpld.Validation()"
					+ "method ";
			LOGGER.debug(msg);
		}

		List<ProcessingResult> validationResult = null;
		Map<String, List<ProcessingResult>> errMap = new HashMap<>();

		Set<VendorMasterUploadEntity> listProcessedDto = new HashSet<>();

		List<VendorMasterErrorReportEntity> listErrorDto = new ArrayList<>();

		VendorMasterUploadEntity masterUploadEntity = null;
		VendorMasterErrorReportEntity errorReportEntity = null;

		for (Object[] rowData : fileList) {

			validationResult = new ArrayList<>();

			masterUploadEntity = new VendorMasterUploadEntity();

			errorReportEntity = new VendorMasterErrorReportEntity();

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
			String[] stringArray = new String[28];

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

			LOGGER.debug("infoMessage :: " + infoMessage);
			String key = createInvoiceKey(rowData);
			/**
			 * Set values for Vendor Master error report
			 */
			extractedMethodForDataSegregate(fileId, refId, payloadId,
					validationResult, errMap, listProcessedDto, listErrorDto,
					masterUploadEntity, errorReportEntity, rowData, num,
					errorMessage, infoMessage, stringArray, key);

		}

		if (!CollectionUtils.isEmpty(listProcessedDto)) {
			retainAndSetFetchedVendors(listProcessedDto);
			softDeleteDuplicateRecords(listProcessedDto);

			vendorMasterUploadEntityRepository.saveAll(listProcessedDto);
		}

		if (!CollectionUtils.isEmpty(listErrorDto))
			vendorMasterErrorReportEntityRepository.saveAll(listErrorDto);

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

		if (fileId != null) {
			Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = fileStatusRepository
					.findById(fileId);
			if (gstr1FileStatusEntity.isPresent()) {
				gstr1FileStatusEntity.get()
						.setProcessed(listProcessedDto.size());
				gstr1FileStatusEntity.get().setError(erroDescCount.size());
				gstr1FileStatusEntity.get()
						.setInformation(infoDescCount.size());
				gstr1FileStatusEntity.get().setTotal(
						listProcessedDto.size() + erroDescCount.size());
				gstr1FileStatusEntity.get()
						.setFileStatus(JobStatusConstants.PROCESSED);
				fileStatusRepository.save(gstr1FileStatusEntity.get());
			}
		} else if (refId != null) {
			Optional<VendorApiUploadStatusEntity> vendorApiStatus = apiStatusRepository
					.findByRefId(refId);
			if (vendorApiStatus.isPresent()) {
				vendorApiStatus.get().setProcessed(listProcessedDto.size());
				vendorApiStatus.get().setError(erroDescCount.size());
				vendorApiStatus.get().setInformation(infoDescCount.size());
				vendorApiStatus.get().setTotal(
						listProcessedDto.size() + erroDescCount.size());
				vendorApiStatus.get()
						.setApiStatus(JobStatusConstants.PROCESSED);
				vendorApiStatus.get().setUpdatedOn(
						EYDateUtil.toUTCDateTimeFromIST(LocalDateTime.now()));
				apiStatusRepository.save(vendorApiStatus.get());
			}
		} else if (payloadId != null) {
			
			Optional<VendorApiUploadStatusEntity> vendorApiStatus = apiStatusRepository
					.findByRefId(payloadId);
			int totalCount = listProcessedDto.size() + erroDescCount.size();
			int errorCount = erroDescCount.size();
			if (vendorApiStatus.isPresent()) {
				vendorApiStatus.get().setProcessed(listProcessedDto.size());
				vendorApiStatus.get().setError(errorCount);
				vendorApiStatus.get().setInformation(infoDescCount.size());
				vendorApiStatus.get().setTotal(totalCount);
				vendorApiStatus.get()
						.setApiStatus(JobStatusConstants.PROCESSED);
				vendorApiStatus.get().setUpdatedOn(
						EYDateUtil.toUTCDateTimeFromIST(LocalDateTime.now()));
				apiStatusRepository.save(vendorApiStatus.get());
			}
			
			if (totalCount == errorCount) {
				payloadRepo.updateStatus(payloadId, "E", LocalDateTime.now());
			} else if (errorCount == 0) {
				payloadRepo.updateStatus(payloadId, "P", LocalDateTime.now());
			} else {
				payloadRepo.updateStatus(payloadId, "PE", LocalDateTime.now());

			}
		}
	}

	private void softDeleteDuplicateRecords(
			Set<VendorMasterUploadEntity> listProcessedDto) {

		int softDeletedCount = 0;
		List<String> invKeysTobePersisted = new ArrayList<>();

		listProcessedDto.forEach(e -> {
			invKeysTobePersisted.add(e.getInvoiceKey());
		});

		List<List<String>> chunks = Lists.partition(invKeysTobePersisted, 2000);
		for (List<String> chunk : chunks) {

			int rowsEffected = vendorMasterUploadEntityRepository
					.softDeleteDuplicateInv(chunk, EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
			softDeletedCount = softDeletedCount + rowsEffected;
		}

		LOGGER.debug("Soft Deleted Record  for Vendor Master Upload is {} ",
				softDeletedCount);

	}

	private void retainAndSetFetchedVendors(
			Set<VendorMasterUploadEntity> listProcessedDto) {

		List<String> vendorGstinsToBePersisted = new ArrayList<>();

		listProcessedDto.forEach(e -> {
			if (!vendorGstinsToBePersisted.contains(e.getVendorGstin()))
				vendorGstinsToBePersisted.add(e.getVendorGstin());
		});

		List<String> fetchedVgstins = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorGstinsToBePersisted,
				2000);
		for (List<String> chunk : chunks) {
			List<String> fetchedGstinsChunks = vendorMasterUploadEntityRepository
					.getFetchedTrueVendorGstins(chunk);
			fetchedVgstins.addAll(fetchedGstinsChunks);
		}
		for (VendorMasterUploadEntity s : listProcessedDto) {
			if (fetchedVgstins.contains(s.getVendorGstin())) {
				s.setFetched(true);
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format("Setting fetched as true for "
							+ "VendorGstin: '%s'", s.getVendorGstin());
					LOGGER.debug(logMsg);
				}
			}
		}
	}

	private void extractedValidationMethod(
			List<ProcessingResult> validationResult, Object[] rowData) {

		Pair<Boolean, String> vendorComPair = isValidFlag(rowData,
				validationResult, "VendorCom");
		Pair<Boolean, String> excludeVendorPair = isValidFlag(rowData,
				validationResult, "ExcludeVendor");
		Pair<Boolean, String> nonComplaintComPair = isValidFlag(rowData,
				validationResult, "NonComplaintCom");
		// Pair<Boolean, String> creditEligibilityPair = isValidFlag(rowData,
		// validationResult, "CreditEligibility");

		// need to pass all pairs to check if all flags are valid
		boolean valid = isAllFlagsAreValid(vendorComPair, excludeVendorPair,
				nonComplaintComPair);
		if (!valid)
			return;
		if (vendorComPair.getValue1().equalsIgnoreCase("y")
				|| nonComplaintComPair.getValue1().equalsIgnoreCase("y")) {
			isValidRecipientPAN(rowData, validationResult);
			isValidVendorPan(rowData, validationResult);
			isValidVendorGstin(rowData, validationResult);
			isValidVendorCode(rowData, validationResult);
			isValidVendorName(rowData, validationResult);
			isValidVendorPrimaryMailId(rowData, validationResult);
			isValidVendorPrimaryContactNumber(rowData, validationResult);
			isValidVendorEmailId1(rowData, validationResult);
			isValidVendorEmailId2(rowData, validationResult);
			isValidVendorEmailId3(rowData, validationResult);
			isValidVendorEmailId4(rowData, validationResult);
			isValidRecipientEmailId1(rowData, validationResult);
			isValidRecipientEmailId2(rowData, validationResult);
			isValidRecipientEmailId3(rowData, validationResult);
			isValidRecipientEmailId4(rowData, validationResult);
			isValidRecipientEmailId5(rowData, validationResult);
			isValidVendorType(rowData, validationResult);
			isValidHsn(rowData, validationResult);
			isValidVendorRiskCategory(rowData, validationResult);
			isValidVendorPaymentTerms(rowData, validationResult);
			isValidVendorRemarks(rowData, validationResult);
			isValidApprovalStatus(rowData, validationResult);
			isValidExcludeVendorRemarks(rowData, validationResult);
			isValidAction(rowData, validationResult);
		} else {
			isValidRecipientPAN(rowData, validationResult);
			isValidVendorPan(rowData, validationResult);
			isValidVendorGstin(rowData, validationResult);
			isValidExcludeVendorRemarks(rowData, validationResult);
		}

	}

	private boolean isAllFlagsAreValid(Pair<Boolean, String> vendorComPair,
			Pair<Boolean, String> excludeVendorPair,
			Pair<Boolean, String> nonComplaintComPair) {
		return vendorComPair.getValue0() && excludeVendorPair.getValue0()
				&& nonComplaintComPair.getValue0();
	}

	private void extractedMethodForDataSegregate(Long fileId, String refId,
			String payloadId, List<ProcessingResult> validationResult,
			Map<String, List<ProcessingResult>> errMap,
			Set<VendorMasterUploadEntity> setProcessedDto,
			List<VendorMasterErrorReportEntity> setErrorDto,
			VendorMasterUploadEntity masterUploadEntity,
			VendorMasterErrorReportEntity errorReportEntity, Object[] rowData,
			int num, String errorMessage, String infoMessage,
			String[] dummyArray, String key) {
		if (!validationResult.isEmpty()) {
			// if info errors then save in both the tables
			LOGGER.debug(validationResult.toString());
			if (isOnlyInfoErrors(validationResult)) {
				extractedMethodToSaveErrorData(fileId, refId, payloadId,
						validationResult, errMap, setErrorDto,
						errorReportEntity, errorMessage, infoMessage,
						dummyArray, key);
				extractedMethodToSaveProcessedData(fileId, refId, payloadId,
						validationResult, setProcessedDto, masterUploadEntity,
						rowData, dummyArray, key, setErrorDto);
			}
			// if errors then save in error table
			else {
				extractedMethodToSaveErrorData(fileId, refId, payloadId,
						validationResult, errMap, setErrorDto,
						errorReportEntity, errorMessage, infoMessage,
						dummyArray, key);
			}
		} else {
			/// Set values for Vendor Master valid report

			extractedMethodToSaveProcessedData(fileId, refId, payloadId,
					validationResult, setProcessedDto, masterUploadEntity,
					rowData, dummyArray, key, setErrorDto);

		}
	}

	private boolean isOnlyInfoErrors(List<ProcessingResult> validationResult) {
		List<ProcessingResult> errorList = validationResult.stream().filter(
				result -> ProcessingResultType.ERROR.equals(result.getType()))
				.collect(Collectors.toList());
		return errorList.isEmpty();
	}

	private void extractedMethodToSaveErrorData(Long fileId, String refId,
			String payloadId, List<ProcessingResult> validationResult,
			Map<String, List<ProcessingResult>> errMap,
			List<VendorMasterErrorReportEntity> setErrorDto,
			VendorMasterErrorReportEntity errorReportEntity,
			String errorMessage, String infoMessage, String[] dummyArray,
			String key) {
		errMap.put(key, validationResult);
		LOGGER.debug("validationResult :: " + validationResult);
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
		errorReportEntity.setRecipientPAN(String.valueOf(dummyArray[0]));
		errorReportEntity.setVendorGstin(String.valueOf(dummyArray[2]));
		errorReportEntity
				.setVendorPAN(String.valueOf(dummyArray[2]).length() == 15
						? String.valueOf(dummyArray[2]).substring(2, 12)
						: null);
		errorReportEntity.setVendorCode((dummyArray[3] != null)
				? String.valueOf(dummyArray[3]).trim() : null);
		errorReportEntity.setVendorName((dummyArray[4] != null)
				? String.valueOf(dummyArray[4]).trim() : null);
		errorReportEntity.setVendPrimEmailId((dummyArray[5] != null)
				? String.valueOf(dummyArray[5]).trim() : null);
		errorReportEntity.setVendorContactNumber((dummyArray[6] != null)
				? String.valueOf(dummyArray[6]).trim() : null);
		errorReportEntity.setVendorEmailId1((dummyArray[7] != null)
				? String.valueOf(dummyArray[7]).trim() : null);
		errorReportEntity.setVendorEmailId2((dummyArray[8] != null)
				? String.valueOf(dummyArray[8]).trim() : null);
		errorReportEntity.setVendorEmailId3((dummyArray[9] != null)
				? String.valueOf(dummyArray[9]).trim() : null);
		errorReportEntity.setVendorEmailId4((dummyArray[10] != null)
				? String.valueOf(dummyArray[10]).trim() : null);
		errorReportEntity.setRecipientEmailId1((dummyArray[11] != null)
				? String.valueOf(dummyArray[11]).trim() : null);
		errorReportEntity.setRecipientEmailId2((dummyArray[12] != null)
				? String.valueOf(dummyArray[12]).trim() : null);
		errorReportEntity.setRecipientEmailId3((dummyArray[13] != null)
				? String.valueOf(dummyArray[13]).trim() : null);
		errorReportEntity.setRecipientEmailId4((dummyArray[14] != null)
				? String.valueOf(dummyArray[14]).trim() : null);
		errorReportEntity.setRecipientEmailId5((dummyArray[15] != null)
				? String.valueOf(dummyArray[15]).trim() : null);
		errorReportEntity.setVendorType((dummyArray[16] != null)
				? String.valueOf(dummyArray[16]).trim() : null);
		errorReportEntity.setHsn((dummyArray[17] != null)
				? String.valueOf(dummyArray[17]).trim() : null);
		errorReportEntity.setVendorRiskCategory((dummyArray[18] != null)
				? String.valueOf(dummyArray[18]).trim() : null);
		errorReportEntity.setVendorPaymentTerms((dummyArray[19] != null)
				? String.valueOf(dummyArray[19]).trim() : null);
		errorReportEntity.setVendorRemarks((dummyArray[20] != null)
				? String.valueOf(dummyArray[20]).trim() : null);
		errorReportEntity.setApprovalStatus((dummyArray[21] != null)
				? String.valueOf(dummyArray[21]).trim() : null);
		errorReportEntity.setExcludeVendorRemarks((dummyArray[22] != null)
				? String.valueOf(dummyArray[22]).trim() : null);
		errorReportEntity.setIsVendorCom((dummyArray[23] != null)
				? String.valueOf(dummyArray[23]).trim() : null);
		errorReportEntity.setIsExcludeVendor((dummyArray[24] != null)
				? String.valueOf(dummyArray[24]).trim() : null);
		errorReportEntity.setIsNonComplaintCom((dummyArray[25] != null)
				? String.valueOf(dummyArray[25]).trim() : null);
		errorReportEntity.setIsCreditEligibility((dummyArray[26] != null)
				? String.valueOf(dummyArray[26]).trim() : null);
		errorReportEntity.setIsDelete((dummyArray[27] != null)
				? String.valueOf(dummyArray[27]).trim() : null);
		errorReportEntity.setFileId(fileId);
		errorReportEntity.setRefId(refId);
		errorReportEntity.setPayloadId(payloadId);
		errorReportEntity.setCreatedOn(LocalDateTime.now());
		errorReportEntity.setCreatedBy(getUserName());
		errorReportEntity.setModifiedOn(LocalDateTime.now());
		errorReportEntity.setModifiedBy(getUserName());
		setErrorDto.add(errorReportEntity);
	}

	private boolean deleteCheckMethod(String[] dummyArray) {
		return dummyArray[27] != null && dummyArray[27].equalsIgnoreCase("D");
	}

	private void extractedMethodToSaveProcessedData(Long fileId, String refId,
			String payloadId, List<ProcessingResult> validationResult,
			Set<VendorMasterUploadEntity> setProcessedDto,
			VendorMasterUploadEntity masterUploadEntity, Object[] rowData,
			String[] dummyArray, String key,
			List<VendorMasterErrorReportEntity> setErrorDto) {
		List<String> infoErrorList = validationResult.stream()
				.map(result -> result.getCode()).collect(Collectors.toList());

		masterUploadEntity.setInvoiceKey(key);
		masterUploadEntity.setRecipientPAN(String.valueOf(rowData[0]));
		masterUploadEntity.setVendorGstin(String.valueOf(rowData[2]));
		if (!infoErrorList.contains("ER3003")) {
			masterUploadEntity
					.setVendorPAN(String.valueOf(rowData[2]).length() == 15
							? String.valueOf(rowData[2]).substring(2, 12) : "");
		}
		if (!infoErrorList.contains("ER3006")) {
			masterUploadEntity.setVendorCode((rowData[3] != null)
					? String.valueOf(rowData[3]).trim() : "");
		}
		if (!infoErrorList.contains("ER3008")) {
			masterUploadEntity.setVendorName((rowData[4] != null)
					? String.valueOf(rowData[4]).trim() : "");
		}
		masterUploadEntity.setVendPrimEmailId(
				(rowData[5] != null) ? String.valueOf(rowData[5]).trim() : "");
		if (!infoErrorList.contains("ER3012")) {
			masterUploadEntity.setVendorContactNumber((rowData[6] != null)
					? String.valueOf(rowData[6]).trim() : "");
		}
		if (!infoErrorList.contains("ER3014")) {
			masterUploadEntity.setVendorEmailId1((rowData[7] != null)
					? String.valueOf(rowData[7]).trim() : "");
		}
		if (!infoErrorList.contains("ER3016")) {
			masterUploadEntity.setVendorEmailId2((rowData[8] != null)
					? String.valueOf(rowData[8]).trim() : "");
		}
		if (!infoErrorList.contains("ER3018")) {
			masterUploadEntity.setVendorEmailId3((rowData[9] != null)
					? String.valueOf(rowData[9]).trim() : "");
		}
		if (!infoErrorList.contains("ER3020")) {
			masterUploadEntity.setVendorEmailId4((rowData[10] != null)
					? String.valueOf(rowData[10]).trim() : "");
		}
		if (!infoErrorList.contains("ER3022")) {
			masterUploadEntity.setRecipientEmailId1((rowData[11] != null)
					? String.valueOf(rowData[11]).trim() : "");
		}
		if (!infoErrorList.contains("ER3024")) {
			masterUploadEntity.setRecipientEmailId2((rowData[12] != null)
					? String.valueOf(rowData[12]).trim() : "");
		}
		if (!infoErrorList.contains("ER3026")) {
			masterUploadEntity.setRecipientEmailId3((rowData[13] != null)
					? String.valueOf(rowData[13]).trim() : "");
		}
		if (!infoErrorList.contains("ER3028")) {
			masterUploadEntity.setRecipientEmailId4((rowData[14] != null)
					? String.valueOf(rowData[14]).trim() : "");
		}
		if (!infoErrorList.contains("ER3030")) {
			masterUploadEntity.setRecipientEmailId5((rowData[15] != null)
					? String.valueOf(rowData[15]).trim() : "");
		}
		if (!infoErrorList.contains("ER3031")
				&& !infoErrorList.contains("ER3032")) {
			masterUploadEntity.setVendorType((rowData[16] != null)
					? vendorTypeCache.findVendorType(
							String.valueOf(rowData[16]).trim().toUpperCase())
					: "");
		}
		if (!infoErrorList.contains("ER3033")
				&& !infoErrorList.contains("ER3034")) {
			String hsn = rowData[17] != null
					? String.valueOf(rowData[17]).trim() : null;
			masterUploadEntity
					.setHsn(hsn != null ? Integer.valueOf(hsn) : null);
			LOGGER.debug("Inside setHsn ::" + hsn);
		}
		if (!infoErrorList.contains("ER3035")
				&& !infoErrorList.contains("ER3036")) {
			masterUploadEntity.setVendorRiskCategory((rowData[18] != null)
					? VendorMasterUtil.getVendorRiskCategory(
							String.valueOf(rowData[18]).trim().toLowerCase())
					: "");
		}
		if (!infoErrorList.contains("ER3037")
				&& !infoErrorList.contains("ER3038")) {
			String paymentTerms = rowData[19] != null
					? String.valueOf(rowData[19]).trim() : null;
			masterUploadEntity.setVendorPaymentTerms(paymentTerms != null
					? Integer.valueOf(paymentTerms) : null);
		}
		if (!infoErrorList.contains("ER3040")) {
			masterUploadEntity.setVendorRemarks((rowData[20] != null)
					? String.valueOf(rowData[20]).trim() : "");
		}
		if (!infoErrorList.contains("ER3041")
				&& !infoErrorList.contains("ER3042")) {
			masterUploadEntity.setApprovalStatus((rowData[21] != null)
					? VendorMasterUtil.getApprovalStatus(
							String.valueOf(rowData[21]).trim().toUpperCase())
					: "");
			LOGGER.debug("Inside setApprovalStatus::"
					+ VendorMasterUtil.getApprovalStatus(
							String.valueOf(rowData[21]).trim().toUpperCase()));
		}

		if (!infoErrorList.contains("ER3044")) {
			masterUploadEntity.setExcludeVendorRemarks((rowData[22] != null)
					? String.valueOf(rowData[22]).trim() : "");
		}
		masterUploadEntity.setVendorCom(
				String.valueOf(rowData[23]).equalsIgnoreCase("y"));
		masterUploadEntity.setExcludeVendor(
				String.valueOf(rowData[24]).equalsIgnoreCase("y"));
		masterUploadEntity.setNonComplaintCom(
				String.valueOf(rowData[25]).equalsIgnoreCase("y"));
		masterUploadEntity.setCreditEligibility(
				String.valueOf(rowData[26]).equalsIgnoreCase("y"));

		masterUploadEntity.setDelete(deleteCheckMethod(dummyArray));
		masterUploadEntity.setFileId(fileId);
		masterUploadEntity.setRefId(refId);
		masterUploadEntity.setPayloadId(payloadId);
		masterUploadEntity.setCreatedOn(LocalDateTime.now());
		masterUploadEntity.setCreatedBy(getUserName());
		masterUploadEntity.setModifiedOn(LocalDateTime.now());
		masterUploadEntity.setModifiedBy(getUserName());

		if (setProcessedDto.contains(masterUploadEntity)) {
			Optional<VendorMasterUploadEntity> existedObj = setProcessedDto
					.stream()
					.filter(eachObj -> eachObj.equals(masterUploadEntity))
					.findFirst();
			if (existedObj.isPresent()) {
				VendorMasterErrorReportEntity dupRecord = new VendorMasterErrorReportEntity();
				BeanUtils.copyProperties(existedObj.get(), dupRecord);
				dupRecord.setError("Duplicate");
				dupRecord.setErrorTypeDeascription(ErrorTypeDescription.ERROR);
				dupRecord.setHsn(String.valueOf(existedObj.get().getHsn()));
				dupRecord.setVendorPaymentTerms(String
						.valueOf(existedObj.get().getVendorPaymentTerms()));
				dupRecord.setIsVendorCom(
						existedObj.get().isVendorCom() == true ? "Y" : "N");
				dupRecord.setIsExcludeVendor(
						existedObj.get().isExcludeVendor() == true ? "Y" : "N");
				dupRecord.setIsNonComplaintCom(
						existedObj.get().isNonComplaintCom() == true ? "Y"
								: "N");
				dupRecord.setIsCreditEligibility(
						existedObj.get().isCreditEligibility() == true ? "Y"
								: "N");
				setErrorDto.add(dupRecord);
				setProcessedDto.remove(existedObj.get());
				setProcessedDto.add(masterUploadEntity);
			}
		} else {
			if (masterUploadEntity.isDelete()) {
				VendorMasterUploadEntity persistedEntity = vendorMasterUploadEntityRepository
						.findByInvoiceKeyAndIsDeleteFalse(
								masterUploadEntity.getInvoiceKey());
				if (persistedEntity != null && !persistedEntity.isDelete()) {
					persistedEntity.setDelete(true);
					persistedEntity.setModifiedOn(LocalDateTime.now());
					persistedEntity.setModifiedBy(getUserName());
					vendorMasterUploadEntityRepository.save(persistedEntity);
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

		String vendorGstin = (arr[2] != null) ? String.valueOf(arr[2]).trim()
				: "";

		return recipientPan + vendorGstin;
	}

	private void isValidAction(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String action = rowData[27] == null ? null
				: String.valueOf(rowData[27]);
		if (StringUtils.hasText(action)) {
			action = action.trim();
			if (!action.equalsIgnoreCase("d")) {
				String errMsg = "Invalid Action";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3054",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				return;
			}
		}
	}

	private Pair<Boolean, String> isValidFlag(Object[] rowData,
			List<ProcessingResult> validationResult, String fieldName) {
		int index = 0;
		String errorCode = null;
		String defaultFlag = null;
		if (fieldName.equals("VendorCom")) {
			index = 23;
			errorCode = "ER3046";
			defaultFlag = "Y";
		} else if (fieldName.equals("ExcludeVendor")) {
			index = 24;
			errorCode = "ER3048";
			defaultFlag = "N";
		} else if (fieldName.equals("NonComplaintCom")) {
			index = 25;
			errorCode = "ER3050";
			defaultFlag = "N";
		} else if (fieldName.equals("CreditEligibility")) {
			index = 26;
			errorCode = "ER3052";
			defaultFlag = "N";
		}

		String flag = rowData[index] == null ? defaultFlag
				: String.valueOf(rowData[index]);
		if (StringUtils.hasText(flag)) {
			flag = flag.trim();
			if (!validResponses.contains(flag)) {
				String errMsg = String.format("Invalid %s Response", fieldName);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return new Pair<>(false, flag);
			}
		}
		rowData[index] = flag;
		return new Pair<>(true, flag);
	}

	private void isValidExcludeVendorRemarks(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorRemarks = rowData[22] == null ? null
				: String.valueOf(rowData[22]);

		if (vendorRemarks != null) {
			vendorRemarks = vendorRemarks.trim();
			if (vendorRemarks.length() > 150) {
				String errMsg = "Invalid Exclude Vendor Remarks";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3044",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidApprovalStatus(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String approvalStatus = rowData[21] == null ? null
				: String.valueOf(rowData[21]);

		LOGGER.debug("ApprovalStatus :: " + approvalStatus);
		if (approvalStatus != null) {
			approvalStatus = approvalStatus.trim();
			if (approvalStatus.length() > 50) {
				String errMsg = "Invalid Approval Status";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3041",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
			if (!VendorMasterUtil
					.isApprovalStatus(approvalStatus.toUpperCase())) {
				LOGGER.debug("isValidApprovalStatus :: " + VendorMasterUtil
						.isApprovalStatus(approvalStatus.toUpperCase()));
				String errMsg = "Invalid Approval Status";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3042",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
				LOGGER.debug(result.toString());
			}
		}

	}

	private void isValidVendorRemarks(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorRemarks = rowData[20] == null ? null
				: String.valueOf(rowData[20]);

		if (vendorRemarks != null) {
			vendorRemarks = vendorRemarks.trim();
			if (vendorRemarks.length() > 150) {
				String errMsg = "Invalid Vendor Remarks";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3040",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidVendorPaymentTerms(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorPaymentTerms = rowData[19] == null ? null
				: String.valueOf(rowData[19]);

		if (vendorPaymentTerms != null) {
			vendorPaymentTerms = vendorPaymentTerms.trim();
			if (vendorPaymentTerms.length() > 3) {
				String errMsg = "Invalid Vendor Payment Terms";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3037",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			} else {
				String regex = "[0-9]+";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(vendorPaymentTerms);
				if (!m.matches()) {
					String errMsg = "Invalid Vendor Payment Terms";
					ProcessingResult result = new ProcessingResult(UPLOAD,
							"ER3038", errMsg, ProcessingResultType.INFO);
					validationResult.add(result);
				}
			}
		}

	}

	private void isValidVendorRiskCategory(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorRiskCategory = rowData[18] == null ? null
				: String.valueOf(rowData[18]);

		if (vendorRiskCategory != null) {
			vendorRiskCategory = vendorRiskCategory.trim();
			if (vendorRiskCategory.length() > 25) {
				String errMsg = "Invalid Vendor Risk Category";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3035",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
			if (!VendorMasterUtil
					.isVendorRiskCategory(vendorRiskCategory.toLowerCase())) {
				String errMsg = "Invalid Vendor Risk Category";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3036",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidHsn(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String hsn = rowData[17] == null ? null : String.valueOf(rowData[17]);

		LOGGER.debug("HSN::" + hsn);
		if (hsn != null) {
			hsn = hsn.trim();
			if (hsn.length() > 8) {
				String errMsg = "Invalid HSN";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3033",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			} else {
				String regex = "[0-9]+";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(hsn);
				if (!m.matches()) {
					String errMsg = "Invalid HSN";
					ProcessingResult result = new ProcessingResult(UPLOAD,
							"ER3034", errMsg, ProcessingResultType.INFO);
					validationResult.add(result);
				} else {
					String vendorType = rowData[16] == null ? null
							: String.valueOf(rowData[16]);
					if (vendorType != null) {
						vendorType = vendorType.trim();
						if (!vendorTypeCache
								.isHSN(vendorType.toUpperCase() + hsn)) {
							LOGGER.debug(vendorType.toUpperCase() + hsn);
							LOGGER.debug("isValidHSN :: " + vendorTypeCache
									.isHSN(vendorType.toUpperCase() + hsn));
							String errMsg = "Invalid HSN";
							ProcessingResult result = new ProcessingResult(
									UPLOAD, "ER3034", errMsg,
									ProcessingResultType.INFO);
							validationResult.add(result);
							LOGGER.debug(result.toString());
						}
					}
				}
			}
		}
	}

	private void isValidVendorType(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorType = rowData[16] == null ? null
				: String.valueOf(rowData[16]);

		if (vendorType != null) {
			vendorType = vendorType.trim();
			if (vendorType.length() > 50) {
				String errMsg = "Invalid Vendor Type";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3031",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
			if (!vendorTypeCache.isVendorType(vendorType.toUpperCase())) {
				String errMsg = "Invalid Vendor Type";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3032",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidRecipientEmailId5(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId5 = rowData[15] == null ? null
				: String.valueOf(rowData[15]);
		if (recipientEmailId5 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId5);
			recipientEmailId5 = recipientEmailId5.trim();
			if ((StringUtils.hasText(recipientEmailId5)) && (!matcher.matches()
					|| recipientEmailId5.length() > 50)) {
				String errMsg = "Invalid Recipient Email ID (5)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3030",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidRecipientEmailId4(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId4 = rowData[14] == null ? null
				: String.valueOf(rowData[14]);

		if (recipientEmailId4 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId4);
			recipientEmailId4 = recipientEmailId4.trim();
			if ((StringUtils.hasText(recipientEmailId4)) && (!matcher.matches()
					|| recipientEmailId4.length() > 50)) {
				String errMsg = "Invalid Recipient Email ID (4)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3028",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidRecipientEmailId3(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId3 = rowData[13] == null ? null
				: String.valueOf(rowData[13]);
		if (recipientEmailId3 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId3);
			recipientEmailId3 = recipientEmailId3.trim();
			if ((StringUtils.hasText(recipientEmailId3)) && (!matcher.matches()
					|| recipientEmailId3.length() > 50)) {
				String errMsg = "Invalid Recipient Email ID (3)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3026",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidRecipientEmailId2(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId2 = rowData[12] == null ? null
				: String.valueOf(rowData[12]);

		if (recipientEmailId2 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId2);
			recipientEmailId2 = recipientEmailId2.trim();
			if ((StringUtils.hasText(recipientEmailId2)) && (!matcher.matches()
					|| recipientEmailId2.length() > 50)) {
				String errMsg = "Invalid Recipient Email ID (2)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3024",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidRecipientEmailId1(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String recipientEmailId1 = rowData[11] == null ? null
				: String.valueOf(rowData[11]);

		if (recipientEmailId1 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(recipientEmailId1);
			recipientEmailId1 = recipientEmailId1.trim();
			if ((StringUtils.hasText(recipientEmailId1)) && (!matcher.matches()
					|| recipientEmailId1.length() > 50)) {
				String errMsg = "Invalid Recipient Email ID (1)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3022",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidVendorEmailId4(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorEmailId4 = rowData[10] == null ? null
				: String.valueOf(rowData[10]);

		if (vendorEmailId4 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(vendorEmailId4);
			vendorEmailId4 = vendorEmailId4.trim();
			if ((StringUtils.hasText(vendorEmailId4))
					&& (!matcher.matches() || vendorEmailId4.length() > 50)) {
				String errMsg = "Invalid Vendor Email ID (4)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3020",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidVendorEmailId3(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorEmailId3 = rowData[9] == null ? null
				: String.valueOf(rowData[9]);
		if (vendorEmailId3 != null) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(vendorEmailId3);
			vendorEmailId3 = vendorEmailId3.trim();
			if ((StringUtils.hasText(vendorEmailId3))
					&& (!matcher.matches() || vendorEmailId3.length() > 50)) {
				String errMsg = "Invalid Vendor Email ID (3)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3018",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidVendorEmailId2(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorEmailId2 = rowData[8] == null ? null
				: String.valueOf(rowData[8]);
		if (vendorEmailId2 != null) {
			vendorEmailId2 = vendorEmailId2.trim();
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(vendorEmailId2);
			if ((StringUtils.hasText(vendorEmailId2))
					&& (!matcher.matches() || vendorEmailId2.length() > 50)) {
				String errMsg = "Invalid Vendor Email ID (2)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3016",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidVendorEmailId1(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String vendorEmailId1 = rowData[7] == null ? null
				: String.valueOf(rowData[7]);

		if (vendorEmailId1 != null) {
			vendorEmailId1 = vendorEmailId1.trim();
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(vendorEmailId1);
			if ((StringUtils.hasText(vendorEmailId1))
					&& (!matcher.matches() || vendorEmailId1.length() > 50)) {
				String errMsg = "Invalid Vendor Email ID (1)";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3014",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidVendorPrimaryContactNumber(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String mobileNumber = rowData[6] == null ? null
				: String.valueOf(rowData[6]);
		if (mobileNumber != null) {
			if (rowData[6].toString().contains(".") && rowData[6].toString()
					.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)")) {
				BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
				docNoDecimalFormat = new BigDecimal(rowData[6].toString());
				Long supplierPhoneLong = docNoDecimalFormat.longValue();
				rowData[6] = String.valueOf(supplierPhoneLong);
				mobileNumber = rowData[6].toString();
			}
			String regexWithCountry = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
			Pattern pattern = Pattern.compile(regexWithCountry);
			Matcher matcher = pattern.matcher(mobileNumber);
			if (!matcher.matches() || mobileNumber.length() > 15) {
				String errMsg = "Invalid Primary Contact Number";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3012",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}
	}

	private void isValidVendorPrimaryMailId(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String emailRegex = EMAIL_REGEX;
		String vendorEmailId = rowData[5] == null ? null
				: String.valueOf(rowData[5]);

		if (!StringUtils.hasText(vendorEmailId)) {
			String errMsg = VENDOR_EMAIL_ID_CANNOT_BE_BLANK;
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER3009",
					errMsg);
			validationResult.add(result);
			return;
		}

		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(vendorEmailId);
		vendorEmailId = vendorEmailId.trim();
		if (!matcher.matches() || vendorEmailId.length() > 50) {
			String errMsg = "Invalid Primary Vendor Email Id";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER3010",
					errMsg);
			validationResult.add(result);
		}

	}

	private void isValidVendorName(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorName = rowData[4] == null ? null
				: String.valueOf(rowData[4]);

		if (vendorName != null) {
			vendorName = vendorName.trim();
			if (vendorName.length() > 100) {
				String errMsg = "Invalid Vendor Name";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3008",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidVendorCode(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorCode = rowData[3] == null ? null
				: String.valueOf(rowData[3]);

		if (vendorCode != null) {
			vendorCode = vendorCode.trim();
			if (vendorCode.length() > 100) {
				String errMsg = "Invalid Supplier Code";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3006",
						errMsg, ProcessingResultType.INFO);
				validationResult.add(result);
			}
		}

	}

	private void isValidVendorGstin(Object[] rowData,
			List<ProcessingResult> validationResult) {

		String vendorGstin = rowData[2] == null ? null
				: String.valueOf(rowData[2]);
		if (!StringUtils.hasText(vendorGstin)) {
			String errMsg = "Vendor GSTIN cannot be blank";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER3004",
					errMsg);
			validationResult.add(result);
			return;
		}

		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(vendorGstin);

		vendorGstin = vendorGstin.trim();
		if (!matcher.matches() || vendorGstin.length() != 15) {
			String errMsg = "Invalid Vendor GSTIN";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER3005",
					errMsg);
			validationResult.add(result);
		}

	}

	private void isValidVendorPan(Object[] rowData,
			List<ProcessingResult> validationResult) {
		String vendorPan = rowData[1] == null ? null
				: String.valueOf(rowData[1]);

		if (StringUtils.hasText(vendorPan)) {
			String regex = "^[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}";

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(vendorPan);
			vendorPan = vendorPan.trim();
			if (!matcher.matches() || vendorPan.length() != 10) {
				String errMsg = "Invalid Vendor PAN";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER3003",
						errMsg);
				validationResult.add(result);
			}
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
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER3001",
					errMsg);
			validationResult.add(result);
			return;
		}
		String regex = "^[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}";

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(recipientPAN);

		recipientPAN = recipientPAN.trim();
		if (!matcher.matches() || recipientPAN.length() != 10) {
			String errMsg = "Invalid Recipient PAN";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER3002",
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

			if (actualHeaderNames.size() != 28) {
				String msg = "The number of columns in the file should be 28. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = headerList;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected Vendor Upload data headers names "
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

	private InputStream getFileInpStream(String fileName, String folderName) {

		try {
			Document doc = DocumentUtility.downloadDocument(fileName,
					folderName);
			LOGGER.debug("Downloaded Vendor DataFile Uploaded file");
			return doc.getContentStream().getStream();
		} catch (Exception ex) {
			throw new AppException(
					"Error occured while " + "reading the file " + fileName,
					ex);
		}
	}
}
