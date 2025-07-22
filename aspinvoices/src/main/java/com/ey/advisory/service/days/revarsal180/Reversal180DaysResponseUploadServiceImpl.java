package com.ey.advisory.service.days.revarsal180;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysResponseErrorRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysResponsePSDRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysResponseStageRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */

@Slf4j
@Component("Reversal180DaysResponseUploadServiceImpl")
public class Reversal180DaysResponseUploadServiceImpl
		implements Reversal180DaysResponseUploadService {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	private Reversal180DaysResponseStructuralValidation structuralValidation;

	@Autowired
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	private Reversal180DaysResponseStageRepository stgRepo;

	@Autowired
	private Reversal180DaysResponsePSDRepository psdRepo;

	@Autowired
	private Reversal180DaysResponseErrorRepository errorRepo;

	@Autowired
	GSTNDetailRepository gstnDetailRepository;

	private static final List<String> EXPECTEDHEADERNAMESLIST = Arrays.asList(
			"UserResponse-TaxPeriod (ITC Reversal)",
			"UserResponse-TaxPeriod (ITC Reclaim)", "ActionType",
			"CustomerGSTIN", "SupplierGSTIN", "SupplierName", "SupplierCode",
			"DocumentType", "DocumentNumber", "DocumentDate", "InvoiceValue",
			"Statutory Deductions Applicable", "StatutoryDeductionAmount",
			"AnyOtherDeductionAmount", "RemarksforDeductions",
			"DueDateofPayment", "PaymentReferenceStatus",
			"PaymentReferenceNumber", "PaymentReferenceDate",
			"PaymentDescription", "PaymentStatus(FullorPartial)",
			"PaidAmounttoSupplier", "CurrencyCode", "ExchangeRate",
			"UnpaidAmounttoSupplier", "PostingDate", "PlantCode",
			"ProfitCentre", "Division", "UserDefinedField1",
			"UserDefinedField2", "UserDefinedField3", "DocDate+180Days",
			"ReturnPeriod-PR", "ReturnPeriod-ReconResponse", "IGSTTaxPaid-PR",
			"CGSTTaxPaid-PR", "SGSTTaxPaid-PR", "CessTaxPaid-PR",
			"AvailableIGST-PR/ReconResponse", "AvailableCGST-PR/ReconResponse",
			"AvailableSGST-PR/ReconResponse", "AvailableCess-PR/ReconResponse",
			"ITCReversal/ReclaimStatus(DigiGST)",
			"ITCReversalReturnPeriod(DigiGST)-Indicative",
			"ReversalofIGST(DigiGST)-Indicative",
			"ReversalofCGST(DigiGST)-Indicative",
			"ReversalofSGST(DigiGST)-Indicative",
			"ReversalofCess(DigiGST)-Indicative",
			"ReClaimReturnPeriod(DigiGST)-Indicative",
			"ReClaimofIGST-Indicative", "ReClaimofCGST-Indicative",
			"ReClaimofSGST-Indicative", "ReClaimofCess-Indicative",
			"ITCReversalComputeDate&Time", "ITCReversalComputeRequestID",
			"ReconciliationDate&Time", "ReconciliationRequestID");

	private static final int NO_OF_COLUMNS = EXPECTEDHEADERNAMESLIST.size();
	private static final String DOC_KEY_JOINER = "|";

	@Override
	@Transactional(value = "clientTransactionManager")
	public void validateReversalResponseFile(Long fileId, String fileName,
			String folderName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Validating 180ReversalResponseFile";
			LOGGER.debug(msg);
		}
		try {
			InputStream in = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(
					NO_OF_COLUMNS);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(in, layout, rowHandler, null);

			validateHeaders(fileName, folderName, fileId);

			List<Object[]> fileList = rowHandler.getFileUploadList();

			if (CollectionUtils.isEmpty(fileList)) {
				String msg = "Failed Empty file";
				LOGGER.error(msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);

				throw new AppException(msg);
			}
			// convert rows into dto
			List<Reversal180DaysResponseUploadDto> rowsAsDtoList = fileList
					.stream().map(o -> convertRowsToDto(o))
					.collect(Collectors.toList());

			validation(rowsAsDtoList, fileId);

		} catch (Exception ex) {
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			markFileAsFailed(fileId, ex.getMessage());
			throw new AppException(msg, ex);
		}

	}

	@Transactional(value = "clientTransactionManager")
	public void validation(List<Reversal180DaysResponseUploadDto> dtoList,
			Long fileId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Revarsal180DaysResponseUploadServiceImpl "
					+ "Validation method ";
			LOGGER.debug(msg);
		}
		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			// Save entire rows to stage table before validation
			List<Reversal180DaysResponseStageEntity> stgEntityList = dtoList
					.stream()
					.map(o -> convertToStageEntity(o, fileId, userName))
					.collect(Collectors.toList());
			stgRepo.saveAll(stgEntityList);

			List<ProcessingResult> validationResult = null;

			List<String> gstinList = gstnDetailRepository.findAllGstin();
			Map<String, Reversal180DaysResponseUploadDto> processedMap = new HashMap<>();
			List<Reversal180DaysResponseUploadDto> processedDtoList = new ArrayList<>();

			List<Reversal180DaysResponseErrorEntity> errorEntityList = new ArrayList<>();
			Map<String, Reversal180DaysResponseUploadDto> activePmtStatMap = new HashMap<>();
			Set<String> activePRDocKeys = new HashSet<>();
			Set<String> activePmtDocKeys = new HashSet<>();
			for (Reversal180DaysResponseUploadDto rowData : dtoList) {
				validationResult = new ArrayList<>();
				String errorMessage = null;
				String errorCodes = null;

				// Send each row for validation
				structuralValidation.rowDataValidation(validationResult,
						rowData, gstinList);

				errorMessage = validationResult.stream()
						.filter(result -> result.getDescription() != null)
						.map(ProcessingResult::getDescription)
						.collect(Collectors.joining(","));

				errorCodes = validationResult.stream()
						.filter(result -> result.getCode() != null)
						.map(ProcessingResult::getCode)
						.collect(Collectors.joining(","));

				if (validationResult.isEmpty()) {
					// Create docKeys if row is valid
					String fy = GenUtil.getFinYear(
							DateUtil.parseObjToDate(rowData.getDocumentDate()));
					String docKey = createDocKey(rowData.getDocumentType(),
							rowData.getDocumentNum(), fy,
							rowData.getSupplierGstin(), rowData.getCustGstin(),
							rowData.getPaymentRefNumber(),
							rowData.getPaymentRefDate());

					String prDocKey = createPRDocKey(fy, rowData.getCustGstin(),
							rowData.getSupplierGstin(),
							rowData.getDocumentType(),
							rowData.getDocumentNum());

					/*
					 * Add rowData to map to check for Duplicate invoice using
					 * Dockey,if duplicate add it to errorList
					 */
					if (processedMap.containsKey(docKey)) {
						makeErrorEntityList(processedMap.get(docKey),
								errorEntityList, "ER1083", "Duplicate Invoice",
								fileId, userName);
					} else {
						rowData.setPrDocKey(prDocKey);
						rowData.setDocKey(docKey);
						processedMap.put(docKey, rowData);
					}

				} else {

					makeErrorEntityList(rowData, errorEntityList, errorCodes,
							errorMessage, fileId, userName);
				}
			}

			/*
			 * Make PaymentDockey and PRDockey list to fetch active keys
			 */

			List<String> docKeyList = processedMap.keySet().stream()
					.collect(Collectors.toList());

			List<List<String>> chunks = Lists.partition(docKeyList, 2000);
			for (List<String> chunk : chunks) {
				Map<String, Reversal180DaysResponseUploadDto> pmtDocKeyMap = structuralValidation
						.getActivePaymentDocKeys(chunk);
				activePmtStatMap.putAll(pmtDocKeyMap);
				activePmtDocKeys.addAll(pmtDocKeyMap.keySet());
			}
			processedMap.forEach((k, v) -> {
				if (!activePmtDocKeys.contains(k)) {
					makeErrorEntityList(v, errorEntityList, "ER1084",
							"Original record is not available in DigiGST",
							fileId, userName);
				} else {

					Reversal180DaysResponseUploadDto dto = activePmtStatMap
							.get(k);
					Pair<String, String> errorInfo = validateResponse(
							dto.getItcRevReclaimStatusDigi(), v);
					if (errorInfo == null) {
						v.setItcRevReclaimStatusDigi(
								dto.getItcRevReclaimStatusDigi());
						v.setInvoiceValue(dto.getInvoiceValue());
						v.setStatDeductionAmt(dto.getStatDeductionAmt());
						v.setAnyOtherDeductionAmt(
								dto.getAnyOtherDeductionAmt());
						v.setPaidAmtToSupplier(dto.getPaidAmtToSupplier());
						v.setUnpaidAmtToSupplier(dto.getUnpaidAmtToSupplier());
						v.setDocDate180Days(dto.getDocDate180Days());
						v.setIgstTaxPaidPR(dto.getIgstTaxPaidPR());
						v.setCgstTaxPaidPR(dto.getCgstTaxPaidPR());
						v.setSgstTaxPaidPR(dto.getSgstTaxPaidPR());
						v.setCessTaxPaidPR(dto.getCessTaxPaidPR());
						v.setAvailableIgstPR(dto.getAvailableIgstPR());
						v.setAvailableCgstPR(dto.getAvailableCgstPR());
						v.setAvailableSgstPR(dto.getAvailableSgstPR());
						v.setAvailableCessPR(dto.getAvailableCessPR());
						/*
						 * v.setRevIgstDigiIndicative(
						 * dto.getRevIgstDigiIndicative());
						 * v.setRevCgstDigiIndicative(
						 * dto.getRevCgstDigiIndicative());
						 * v.setRevSgstDigiIndicative(
						 * dto.getRevSgstDigiIndicative());
						 * v.setRevCessDigiIndicative(
						 * dto.getRevCessDigiIndicative());
						 * v.setReclaimIgstIndicative(
						 * dto.getReclaimIgstIndicative());
						 * v.setReclaimCgstIndicative(
						 * dto.getReclaimCgstIndicative());
						 * v.setReclaimSgstIndicative(
						 * dto.getReclaimSgstIndicative());
						 * v.setReclaimCessIndicative(
						 * dto.getReclaimCessIndicative());
						 */
						v.setComputeId(dto.getComputeId());
						v.setReconReportConfigID(dto.getReconReportConfigID());
						v.setItcRevRetPrdDigiIndicative(
								dto.getItcRevRetPrdDigiIndicative());
						v.setReclaimRetPrdDigiIndicative(
								dto.getReclaimRetPrdDigiIndicative());
						processedDtoList.add(v);
					} else {
						makeErrorEntityList(v, errorEntityList,
								errorInfo.getValue0(), errorInfo.getValue1(),
								fileId, userName);
					}

				}
			});

			List<String> prDocKeyList = processedDtoList.stream()
					.map(o -> o.getPrDocKey())
					.collect(Collectors.toCollection(ArrayList::new));

			List<List<String>> prChunks = Lists.partition(prDocKeyList, 2000);
			for (List<String> chunk : prChunks) {
				Set<String> prDocKeys = structuralValidation
						.getActivePRDocKeys(chunk);
				activePRDocKeys.addAll(prDocKeys);
			}

			// Create processed entity list to persist
			List<Reversal180DaysResponsePSDEntity> psdEntityList = processedDtoList
					.stream()
					.map(o -> makePsdEntityList(o, fileId, userName,
							activePmtDocKeys, activePRDocKeys))
					.collect(Collectors.toList());

			long psdCount = psdEntityList.stream().filter(o -> o.isPsd())
					.count();
			LOGGER.debug("psdCount {}", psdCount);
			long psdErrCount = psdEntityList.stream().filter(o -> !o.isPsd())
					.count();
			LOGGER.debug("psdErrCount {}", psdErrCount);

			LOGGER.debug("errorEntityList Size {}", errorEntityList.size());

			errorRepo.saveAll(errorEntityList);

			// softDelete with dockeys
			softDelete(docKeyList);

			psdRepo.saveAll(psdEntityList);

			LOGGER.debug("Updating fileId status {}", fileId);
			Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = fileStatusRepository
					.findById(fileId);
			if (gstr1FileStatusEntity.isPresent()) {
				gstr1FileStatusEntity.get().setProcessed((int) psdCount);
				gstr1FileStatusEntity.get()
						.setError(errorEntityList.size() + (int) psdErrCount);
				gstr1FileStatusEntity.get().setTotal((int) psdCount
						+ errorEntityList.size() + (int) psdErrCount);
				gstr1FileStatusEntity.get()
						.setFileStatus(JobStatusConstants.PROCESSED);
				fileStatusRepository.save(gstr1FileStatusEntity.get());
			}
		} catch (Exception ex) {
			String msg = "Exception Occured while validating REV180Resp file";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private Pair<String, String> validateResponse(String status,
			Reversal180DaysResponseUploadDto dto) {

		ProcessingResult validationResult = structuralValidation
				.isResponseValid(dto.getUserResponseRevTaxPeriod(),
						dto.getUserResponseReclaimTaxPeriod(), status);

		if (validationResult != null) {

			return new Pair<>(validationResult.getCode(),
					validationResult.getDescription());
		}
		return null;

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
					" %d rows affected in "
							+ "TBL_180_DAYS_RESP_REVERSAL table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private Reversal180DaysResponsePSDEntity makePsdEntityList(
			Reversal180DaysResponseUploadDto dto, Long fileId, String userName,
			Set<String> docKeyPmtDtlsList, Set<String> docKeyPRList) {

		Reversal180DaysResponsePSDEntity entity = new Reversal180DaysResponsePSDEntity();
		String errorCode = null;
		String errorDesc = null;

		/*
		 * For actionType CAN record should exist in DigiGst otherwise make it
		 * as error,This type of error records will be persisted in psd table
		 * with psd flag as false
		 */
		if ("CAN".equalsIgnoreCase(dto.getActionType())) {
			if (!(docKeyPmtDtlsList.contains(dto.getDocKey()))
					&& !(docKeyPRList.contains(dto.getPrDocKey()))) {
				errorCode = "ER1084";
				errorDesc = "Original record is not available in DigiGST";
			}

		}

		entity.setUserResponseReversal(checkNullAndTrim(
				removeQuotes(dto.getUserResponseRevTaxPeriod()), false, 0));

		entity.setUserResponseReclaim(checkNullAndTrim(
				removeQuotes(dto.getUserResponseReclaimTaxPeriod()), false, 0));

		if (!Strings.isNullOrEmpty(dto.getActionType())) {
			entity.setActionType(dto.getActionType().trim().toUpperCase());
		} else {
			entity.setActionType(
					checkNullAndTrim(dto.getActionType(), false, 0));
		}

		entity.setCustGstin(checkNullAndTrim(dto.getCustGstin(), false, 0));
		entity.setSupplierGstin(
				checkNullAndTrim(dto.getSupplierGstin(), false, 0));
		// entity.setSupplierName(
		// checkNullAndTrim(dto.getSupplierName(), true, 100));
		entity.setSupplierCode(
				checkNullAndTrim(dto.getSupplierCode(), true, 100));

		entity.setDocumentType(
				checkNullAndTrim(dto.getDocumentType(), false, 0));
		String docNum = removeQuotes(dto.getDocumentNum());

		entity.setDocumentNum(checkNullAndTrim(docNum, false, 0));
		entity.setDocumentDate(DateUtil.parseObjToDate(dto.getDocumentDate()));
		entity.setInvoiceValue(
				convertStringToBigDecimal(dto.getInvoiceValue()));

		if (Strings.isNullOrEmpty(dto.getStatDeductionApplicable())) {
			entity.setStatDeductionApplicable("N");
		} else {
			entity.setStatDeductionApplicable(
					dto.getStatDeductionApplicable().toUpperCase());
		}
		entity.setStatDeductionAmt(
				convertStringToBigDecimal(dto.getStatDeductionAmt()));
		entity.setAnyOtherDeductionAmt(
				convertStringToBigDecimal(dto.getAnyOtherDeductionAmt()));
		// entity.setRemarksForDeductions(
		// checkNullAndTrim(dto.getRemarksForDeductions(), true, 100));
		// entity.setPaymentDueDate(
		// DateUtil.parseObjToDate(dto.getPaymentDueDate()));
		// entity.setPaymentRefStatus(
		// checkNullAndTrim(dto.getPaymentRefStatus(), true, 100));

		entity.setPaymentRefNumber(
				checkNullAndTrim(dto.getPaymentRefNumber(), true, 500));

		entity.setPaymentDesc(
				checkNullAndTrim(dto.getPaymentDesc(), true, 500));
		entity.setPaymentRefDate(
				DateUtil.parseObjToDate(dto.getPaymentRefDate()));
		if (Strings.isNullOrEmpty(dto.getPaymentStatus())) {
			entity.setPaymentStatus("FP");
		} else {
			entity.setPaymentStatus(dto.getPaymentStatus().toUpperCase());

		}

		entity.setPaidAmtToSupplier(
				convertStringToBigDecimal((dto.getPaidAmtToSupplier())));
		// entity.setCurrencyCode(
		// checkNullAndTrim(dto.getCurrencyCode(), true, 100));
		// entity.setExchangeRate(
		// checkNullAndTrim(dto.getExchangeRate(), true, 100));
		entity.setUnpaidAmtToSupplier(
				convertStringToBigDecimal((dto.getUnpaidAmtToSupplier())));
		// entity.setPostingDate(DateUtil.parseObjToDate(dto.getPostingDate()));

		entity.setPlantCode(checkNullAndTrim(dto.getPlantCode(), true, 100));
		entity.setProfitCentre(
				checkNullAndTrim(dto.getProfitCentre(), true, 100));
		entity.setDivision(checkNullAndTrim(dto.getDivision(), true, 100));
		entity.setUserDefinedField1(
				checkNullAndTrim(dto.getUserDefinedField1(), true, 500));
		entity.setUserDefinedField2(
				checkNullAndTrim(dto.getUserDefinedField2(), true, 500));
		entity.setUserDefinedField3(
				checkNullAndTrim(dto.getUserDefinedField3(), true, 500));

		entity.setDocDate180Days(
				DateUtil.parseObjToDate(dto.getDocDate180Days()));
		entity.setRetPeriodPR(checkNullAndTrim(
				removeQuotes(dto.getRetPeriodPR()), true, 100));
		entity.setRetPeriodReconResp(checkNullAndTrim(
				removeQuotes(dto.getRetPeriodReconResp()), true, 100));

		entity.setIgstTaxPaidPR(
				convertStringToBigDecimal(dto.getIgstTaxPaidPR()));
		entity.setCgstTaxPaidPR(
				convertStringToBigDecimal(dto.getCgstTaxPaidPR()));
		entity.setSgstTaxPaidPR(
				convertStringToBigDecimal(dto.getSgstTaxPaidPR()));
		entity.setCessTaxPaidPR(
				convertStringToBigDecimal(dto.getCessTaxPaidPR()));
		entity.setAvailableIgstPR(
				convertStringToBigDecimal(dto.getAvailableIgstPR()));
		entity.setAvailableCgstPR(
				convertStringToBigDecimal(dto.getAvailableCgstPR()));
		entity.setAvailableSgstPR(
				convertStringToBigDecimal(dto.getAvailableSgstPR()));
		entity.setAvailableCessPR(
				convertStringToBigDecimal(dto.getAvailableCessPR()));
		entity.setItcRevReclaimStatusDigi(
				checkNullAndTrim(dto.getItcRevReclaimStatusDigi(), true, 100));

		entity.setItcRevRetPrdDigiIndicative(checkNullAndTrim(
				removeQuotes(dto.getItcRevRetPrdDigiIndicative()), true, 100));

		entity.setRevCessDigiIndicative(
				convertStringToBigDecimal(dto.getRevCessDigiIndicative()));
		entity.setRevCgstDigiIndicative(
				convertStringToBigDecimal(dto.getRevCgstDigiIndicative()));
		entity.setRevIgstDigiIndicative(
				convertStringToBigDecimal(dto.getRevIgstDigiIndicative()));
		entity.setRevSgstDigiIndicative(
				convertStringToBigDecimal(dto.getRevSgstDigiIndicative()));

		entity.setReclaimRetPrdDigiIndicative(checkNullAndTrim(
				removeQuotes(dto.getReclaimRetPrdDigiIndicative()), true, 100));
		entity.setReclaimCessIndicative(
				convertStringToBigDecimal(dto.getReclaimCessIndicative()));
		entity.setReclaimCgstIndicative(
				convertStringToBigDecimal(dto.getReclaimCgstIndicative()));
		entity.setReclaimIgstIndicative(
				convertStringToBigDecimal(dto.getReclaimIgstIndicative()));
		entity.setReclaimSgstIndicative(
				convertStringToBigDecimal(dto.getReclaimSgstIndicative()));

		entity.setItcRevComputeDateTime(stringToTime(
				dto.getItcRevComputeDateTime(), DateUtil.DATE_FORMAT1));
		entity.setComputeID(convertStringToLong(dto.getComputeId()));
		entity.setReconDateTime(
				stringToTime(dto.getReconDateTime(), DateUtil.DATE_FORMAT1));
		entity.setReconReportConfigId(
				convertStringToLong(dto.getReconReportConfigID()));

		entity.setDocKey(dto.getDocKey());
		entity.setCreatedOn(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setFileId(fileId);
		entity.setDelete(false);
		entity.setErrorCode(errorCode);
		entity.setErrorDesc(errorDesc);

		if (Strings.isNullOrEmpty(errorCode)) {
			entity.setPsd(true);
		} else {
			entity.setPsd(false);
		}
		return entity;

	}

	private void makeErrorEntityList(Reversal180DaysResponseUploadDto dto,
			List<Reversal180DaysResponseErrorEntity> errEntityList,
			String errorCode, String errorDesc, Long fileId, String userName) {
		Reversal180DaysResponseErrorEntity entity = new Reversal180DaysResponseErrorEntity();

		entity.setErrorCode(errorCode);
		entity.setErrorDesc(errorDesc);

		entity.setUserResponseReversal(checkNullAndTrim(
				removeQuotes(dto.getUserResponseRevTaxPeriod()), true, 100));
		entity.setUserResponseReclaim(checkNullAndTrim(
				removeQuotes(dto.getUserResponseReclaimTaxPeriod()), true,
				100));
		entity.setActionType(checkNullAndTrim(dto.getActionType(), true, 100));
		entity.setCustGstin(checkNullAndTrim(dto.getCustGstin(), true, 100));
		entity.setSupplierGstin(
				checkNullAndTrim(dto.getSupplierGstin(), true, 100));
		entity.setSupplierName(
				checkNullAndTrim(dto.getSupplierName(), true, 100));
		entity.setSupplierCode(
				checkNullAndTrim(dto.getSupplierCode(), true, 100));
		entity.setDocumentType(
				checkNullAndTrim(dto.getDocumentType(), true, 100));
		entity.setDocumentNum(
				checkNullAndTrim(dto.getDocumentNum(), true, 100));
		entity.setDocumentDate(
				checkNullAndTrim(dto.getDocumentDate(), true, 100));
		entity.setInvoiceValue(
				checkNullAndTrim(dto.getInvoiceValue(), true, 100));
		entity.setStatDeductionApplicable(
				checkNullAndTrim(dto.getStatDeductionApplicable(), true, 100));
		entity.setStatDeductionAmt(
				checkNullAndTrim(dto.getStatDeductionAmt(), true, 100));
		entity.setAnyOtherDeductionAmt(
				checkNullAndTrim(dto.getAnyOtherDeductionAmt(), true, 100));
		entity.setRemarksForDeductions(
				checkNullAndTrim(dto.getRemarksForDeductions(), true, 100));
		entity.setPaymentDueDate(
				checkNullAndTrim(dto.getPaymentDueDate(), true, 100));
		entity.setPaymentRefStatus(
				checkNullAndTrim(dto.getPaymentRefStatus(), true, 100));
		entity.setPaymentRefNumber(
				checkNullAndTrim(dto.getPaymentRefNumber(), true, 100));
		entity.setPaymentRefDate(
				checkNullAndTrim(dto.getPaymentRefDate(), true, 100));
		entity.setPaymentDesc(
				checkNullAndTrim(dto.getPaymentDesc(), true, 100));
		entity.setPaymentStatus(
				checkNullAndTrim(dto.getPaymentStatus(), true, 100));
		entity.setPaidAmtToSupplier(
				checkNullAndTrim(dto.getPaidAmtToSupplier(), true, 100));
		entity.setCurrencyCode(
				checkNullAndTrim(dto.getCurrencyCode(), true, 100));
		entity.setExchangeRate(
				checkNullAndTrim(dto.getExchangeRate(), true, 100));
		entity.setUnpaidAmtToSupplier(
				checkNullAndTrim(dto.getUnpaidAmtToSupplier(), true, 100));
		entity.setPostingDate(
				checkNullAndTrim(dto.getPostingDate(), true, 100));
		entity.setPlantCode(checkNullAndTrim(dto.getPlantCode(), true, 100));
		entity.setProfitCentre(
				checkNullAndTrim(dto.getProfitCentre(), true, 100));
		entity.setDivision(checkNullAndTrim(dto.getDivision(), true, 100));
		entity.setUserDefinedField1(
				checkNullAndTrim(dto.getUserDefinedField1(), true, 100));
		entity.setUserDefinedField2(
				checkNullAndTrim(dto.getUserDefinedField2(), true, 100));
		entity.setUserDefinedField3(
				checkNullAndTrim(dto.getUserDefinedField3(), true, 100));
		entity.setDocDate180Days(
				checkNullAndTrim(dto.getDocDate180Days(), true, 100));
		entity.setRetPeriodPR(checkNullAndTrim(
				removeQuotes(dto.getRetPeriodPR()), true, 100));
		entity.setRetPeriodReconResp(checkNullAndTrim(
				removeQuotes(dto.getRetPeriodReconResp()), true, 100));
		entity.setIgstTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getIgstTaxPaidPR()), true, 100));
		entity.setCgstTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getCgstTaxPaidPR()), true, 100));
		entity.setSgstTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getSgstTaxPaidPR()), true, 100));
		entity.setCessTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getCessTaxPaidPR()), true, 100));
		entity.setAvailableIgstPR(
				checkNullAndTrim(dto.getAvailableIgstPR(), true, 100));
		entity.setAvailableCgstPR(
				checkNullAndTrim(dto.getAvailableCgstPR(), true, 100));
		entity.setAvailableSgstPR(
				checkNullAndTrim(dto.getAvailableSgstPR(), true, 100));
		entity.setAvailableCessPR(
				checkNullAndTrim(dto.getAvailableCessPR(), true, 100));
		entity.setItcRevReclaimStatusDigi(
				checkNullAndTrim(dto.getItcRevReclaimStatusDigi(), true, 100));
		entity.setItcRevRetPrdDigiIndicative(checkNullAndTrim(
				removeQuotes(dto.getItcRevRetPrdDigiIndicative()), true, 100));
		entity.setRevIgstDigiIndicative(
				checkNullAndTrim(dto.getRevIgstDigiIndicative(), true, 100));
		entity.setRevCgstDigiIndicative(
				checkNullAndTrim(dto.getRevCgstDigiIndicative(), true, 100));
		entity.setRevSgstDigiIndicative(
				checkNullAndTrim(dto.getRevSgstDigiIndicative(), true, 100));
		entity.setRevCessDigiIndicative(
				checkNullAndTrim(dto.getRevCessDigiIndicative(), true, 100));
		entity.setReclaimRetPrdDigiIndicative(checkNullAndTrim(
				removeQuotes(dto.getReclaimRetPrdDigiIndicative()), true, 100));
		entity.setReclaimIgstIndicative(
				checkNullAndTrim(dto.getReclaimIgstIndicative(), true, 100));
		entity.setReclaimCgstIndicative(
				checkNullAndTrim(dto.getReclaimCgstIndicative(), true, 100));
		entity.setReclaimSgstIndicative(
				checkNullAndTrim(dto.getReclaimSgstIndicative(), true, 100));
		entity.setReclaimCessIndicative(
				checkNullAndTrim(dto.getReclaimCessIndicative(), true, 100));
		entity.setItcRevComputeDateTime(checkNullAndTrim(
				removeQuotes(dto.getItcRevComputeDateTime()), true, 100));
		entity.setComputeID(
				checkNullAndTrim(removeQuotes(dto.getComputeId()), true, 100));
		entity.setReconDateTime(checkNullAndTrim(
				removeQuotes(dto.getReconDateTime()), true, 100));
		entity.setReconReportConfigId(checkNullAndTrim(
				removeQuotes(dto.getReconReportConfigID()), true, 100));

		entity.setCreatedOn(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setFileId(fileId);
		entity.setDelete(false);
		errEntityList.add(entity);
	}

	// Doc Key - Document Type + Document No. + FY (Based on Doc Date) +
	// SupplierGSTIN + Customer GSTIN + Payment Reference No. + Payment
	// Reference Date

	private String createDocKey(String docType, String docNum, String finyear,
			String suppGstin, String custGstin, String paymentRefNum,
			String paymentrefDate) {

		StringBuilder docKey = new StringBuilder();

		docKey.append(docType).append(DOC_KEY_JOINER)
				.append(removeQuotes(docNum)).append(DOC_KEY_JOINER)
				.append(finyear);

		if (!Strings.isNullOrEmpty(suppGstin)) {
			docKey.append(DOC_KEY_JOINER).append(suppGstin);
		}
		docKey.append(DOC_KEY_JOINER).append(custGstin).append(DOC_KEY_JOINER)
				.append(paymentRefNum).append(DOC_KEY_JOINER)
				.append(DateUtil.parseObjToDate(paymentrefDate));

		return docKey.toString();
	}

	// PR Doc Key - FY (Based on Doc Date) +Customer GSTIN + SupplierGSTIN +
	// Document Type + Document No.

	private String createPRDocKey(String finyear, String custGstin,
			String suppGstin, String docType, String docNum) {

		StringBuilder prDocKey = new StringBuilder();

		prDocKey.append(finyear).append(DOC_KEY_JOINER).append(custGstin);

		if (!Strings.isNullOrEmpty(suppGstin)) {
			prDocKey.append(DOC_KEY_JOINER).append(suppGstin);
		}
		prDocKey.append(DOC_KEY_JOINER).append(docType).append(DOC_KEY_JOINER)
				.append(docNum);

		return prDocKey.toString();
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
					"Exception occured in reading File Revarsal180DaysResponseUpload Service Impl",
					e);
			throw new AppException(
					"Error occured while reading the file " + fileName, e);
		}
		return inputStream;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {

			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0,
					NO_OF_COLUMNS);

			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverseHeaderOnly(fin, layout, rowHandler, null);

			if (rowHandler.getHeaderRow() == null) {

				String msg = "The headers are empty.";
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
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
				String msg = "The number of columns in the file should be 55. "
						+ "Aborting the file processing.";
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);

				markFileAsFailed(fileId, msg);

				LOGGER.equals(msg);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected User response headers names "
								+ "%s and header count %d",
						EXPECTEDHEADERNAMESLIST.toString(),
						EXPECTEDHEADERNAMESLIST.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(EXPECTEDHEADERNAMESLIST,
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
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
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
			fileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = String
					.format("[SEVERE] Unable to mark the file as failed. "
							+ "Reason for file failure is: [ %s ]", reason);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private Reversal180DaysResponseUploadDto convertRowsToDto(Object[] arr) {
		Reversal180DaysResponseUploadDto dto = new Reversal180DaysResponseUploadDto();

		dto.setUserResponseRevTaxPeriod(checkNull(arr[0]));
		dto.setUserResponseReclaimTaxPeriod(checkNull(arr[1]));
		dto.setActionType(checkNull(arr[2]));
		dto.setCustGstin(checkNull(arr[3]));
		dto.setSupplierGstin(checkNull(arr[4]));
		dto.setSupplierName(checkNull(arr[5]));
		dto.setSupplierCode(checkNull(arr[6]));
		dto.setDocumentType(checkNull(arr[7]));
		dto.setDocumentNum(checkNull(arr[8]));
		dto.setDocumentDate(checkNull(arr[9]));
		dto.setInvoiceValue(checkNull(arr[10]));
		dto.setStatDeductionApplicable(checkNull(arr[11]));
		dto.setStatDeductionAmt(checkNull(arr[12]));
		dto.setAnyOtherDeductionAmt(checkNull(arr[13]));
		dto.setRemarksForDeductions(checkNull(arr[14]));
		dto.setPaymentDueDate(checkNull(arr[15]));
		dto.setPaymentRefStatus(checkNull(arr[16]));
		dto.setPaymentRefNumber(checkNull(arr[17]));
		dto.setPaymentRefDate(checkNull(arr[18]));
		dto.setPaymentDesc(checkNull(arr[19]));
		dto.setPaymentStatus(checkNull(arr[20]));
		dto.setPaidAmtToSupplier(checkNull(arr[21]));
		dto.setCurrencyCode(checkNull(arr[22]));
		dto.setExchangeRate(checkNull(arr[23]));
		dto.setUnpaidAmtToSupplier(checkNull(arr[24]));
		dto.setPostingDate(checkNull(arr[25]));
		dto.setPlantCode(checkNull(arr[26]));
		dto.setProfitCentre(checkNull(arr[27]));
		dto.setDivision(checkNull(arr[28]));
		dto.setUserDefinedField1(checkNull(arr[29]));
		dto.setUserDefinedField2(checkNull(arr[30]));
		dto.setUserDefinedField3(checkNull(arr[31]));
		dto.setDocDate180Days(checkNull(arr[32]));
		dto.setRetPeriodPR(checkNull(arr[33]));
		dto.setRetPeriodReconResp(checkNull(arr[34]));
		dto.setIgstTaxPaidPR((arr[35] != null)
				? checkForNegative(arr[35].toString()) : null);
		dto.setCgstTaxPaidPR((arr[36] != null)
				? checkForNegative(arr[36].toString()) : null);
		dto.setSgstTaxPaidPR((arr[37] != null)
				? checkForNegative(arr[37].toString()) : null);
		dto.setCessTaxPaidPR((arr[38] != null)
				? checkForNegative(arr[38].toString()) : null);
		dto.setAvailableIgstPR((arr[39] != null)
				? checkForNegative(arr[39].toString()) : null);
		dto.setAvailableCgstPR((arr[40] != null)
				? checkForNegative(arr[40].toString()) : null);
		dto.setAvailableSgstPR((arr[41] != null)
				? checkForNegative(arr[41].toString()) : null);
		dto.setAvailableCessPR((arr[42] != null)
				? checkForNegative(arr[42].toString()) : null);
		dto.setItcRevReclaimStatusDigi(checkNull(arr[43]));
		dto.setItcRevRetPrdDigiIndicative(checkNull(arr[44]));
		dto.setRevIgstDigiIndicative((arr[45] != null)
				? checkForNegative(arr[45].toString()) : null);
		dto.setRevCgstDigiIndicative((arr[46] != null)
				? checkForNegative(arr[46].toString()) : null);
		dto.setRevSgstDigiIndicative((arr[47] != null)
				? checkForNegative(arr[47].toString()) : null);
		dto.setRevCessDigiIndicative((arr[48] != null)
				? checkForNegative(arr[48].toString()) : null);
		dto.setReclaimRetPrdDigiIndicative(checkNull(arr[49]));
		dto.setReclaimIgstIndicative((arr[50] != null)
				? checkForNegative(arr[50].toString()) : null);
		dto.setReclaimCgstIndicative((arr[51] != null)
				? checkForNegative(arr[51].toString()) : null);
		dto.setReclaimSgstIndicative((arr[52] != null)
				? checkForNegative(arr[52].toString()) : null);
		dto.setReclaimCessIndicative((arr[53] != null)
				? checkForNegative(arr[53].toString()) : null);
		dto.setItcRevComputeDateTime(checkNull(arr[54]));
		dto.setComputeId(checkNull(arr[55]));
		dto.setReconDateTime(checkNull(arr[56]));
		dto.setReconReportConfigID(checkNull(arr[57]));
		return dto;
	}

	private Reversal180DaysResponseStageEntity convertToStageEntity(
			Reversal180DaysResponseUploadDto dto, Long fileId,
			String userName) {
		Reversal180DaysResponseStageEntity entity = new Reversal180DaysResponseStageEntity();

		entity.setUserResponseReversal(checkNullAndTrim(
				removeQuotes(dto.getUserResponseRevTaxPeriod()), true, 100));

		entity.setUserResponseReclaim(checkNullAndTrim(
				removeQuotes(dto.getUserResponseReclaimTaxPeriod()), true,
				100));
		entity.setActionType(checkNullAndTrim(dto.getActionType(), true, 100));
		entity.setCustGstin(checkNullAndTrim(dto.getCustGstin(), true, 100));
		entity.setSupplierGstin(
				checkNullAndTrim(dto.getSupplierGstin(), true, 100));
		entity.setSupplierName(
				checkNullAndTrim(dto.getSupplierName(), true, 100));
		entity.setSupplierCode(
				checkNullAndTrim(dto.getSupplierCode(), true, 100));
		entity.setDocumentType(
				checkNullAndTrim(dto.getDocumentType(), true, 100));
		entity.setDocumentNum(
				checkNullAndTrim(dto.getDocumentNum(), true, 100));
		entity.setDocumentDate(
				checkNullAndTrim(dto.getDocumentDate(), true, 100));
		entity.setInvoiceValue(
				checkNullAndTrim(dto.getInvoiceValue(), true, 100));
		entity.setStatDeductionApplicable(
				checkNullAndTrim(dto.getStatDeductionApplicable(), true, 100));
		entity.setStatDeductionAmt(
				checkNullAndTrim(dto.getStatDeductionAmt(), true, 100));
		entity.setAnyOtherDeductionAmt(
				checkNullAndTrim(dto.getAnyOtherDeductionAmt(), true, 100));
		entity.setRemarksForDeductions(
				checkNullAndTrim(dto.getRemarksForDeductions(), true, 100));
		entity.setPaymentDueDate(
				checkNullAndTrim(dto.getPaymentDueDate(), true, 100));
		entity.setPaymentRefStatus(
				checkNullAndTrim(dto.getPaymentRefStatus(), true, 100));
		entity.setPaymentRefNumber(
				checkNullAndTrim(dto.getPaymentRefNumber(), true, 100));
		entity.setPaymentRefDate(
				checkNullAndTrim(dto.getPaymentRefDate(), true, 100));
		entity.setPaymentDesc(
				checkNullAndTrim(dto.getPaymentDesc(), true, 100));
		entity.setPaymentStatus(
				checkNullAndTrim(dto.getPaymentStatus(), true, 100));
		entity.setPaidAmtToSupplier(
				checkNullAndTrim(dto.getPaidAmtToSupplier(), true, 100));
		entity.setCurrencyCode(
				checkNullAndTrim(dto.getCurrencyCode(), true, 100));
		entity.setExchangeRate(
				checkNullAndTrim(dto.getExchangeRate(), true, 100));
		entity.setUnpaidAmtToSupplier(
				checkNullAndTrim(dto.getUnpaidAmtToSupplier(), true, 100));
		entity.setPostingDate(
				checkNullAndTrim(dto.getPostingDate(), true, 100));
		entity.setPlantCode(checkNullAndTrim(dto.getPlantCode(), true, 100));
		entity.setProfitCentre(
				checkNullAndTrim(dto.getProfitCentre(), true, 100));
		entity.setDivision(checkNullAndTrim(dto.getDivision(), true, 100));
		entity.setUserDefinedField1(
				checkNullAndTrim(dto.getUserDefinedField1(), true, 100));
		entity.setUserDefinedField2(
				checkNullAndTrim(dto.getUserDefinedField2(), true, 100));
		entity.setUserDefinedField3(
				checkNullAndTrim(dto.getUserDefinedField3(), true, 100));
		entity.setDocDate180Days(
				checkNullAndTrim(dto.getDocDate180Days(), true, 100));
		entity.setRetPeriodPR(checkNullAndTrim(
				removeQuotes(dto.getRetPeriodPR()), true, 100));
		entity.setRetPeriodReconResp(checkNullAndTrim(
				removeQuotes(dto.getRetPeriodReconResp()), true, 100));
		entity.setIgstTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getIgstTaxPaidPR()), true, 100));
		entity.setCgstTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getCgstTaxPaidPR()), true, 100));
		entity.setSgstTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getSgstTaxPaidPR()), true, 100));
		entity.setCessTaxPaidPR(checkNullAndTrim(
				removeQuotes(dto.getCessTaxPaidPR()), true, 100));
		entity.setAvailableIgstPR(
				checkNullAndTrim(dto.getAvailableIgstPR(), true, 100));
		entity.setAvailableCgstPR(
				checkNullAndTrim(dto.getAvailableCgstPR(), true, 100));
		entity.setAvailableSgstPR(
				checkNullAndTrim(dto.getAvailableSgstPR(), true, 100));
		entity.setAvailableCessPR(
				checkNullAndTrim(dto.getAvailableCessPR(), true, 100));
		entity.setItcRevReclaimStatusDigi(
				checkNullAndTrim(dto.getItcRevReclaimStatusDigi(), true, 100));
		entity.setItcRevRetPrdDigiIndicative(checkNullAndTrim(
				removeQuotes(dto.getItcRevRetPrdDigiIndicative()), true, 100));
		entity.setRevIgstDigiIndicative(
				checkNullAndTrim(dto.getRevIgstDigiIndicative(), true, 100));
		entity.setRevCgstDigiIndicative(
				checkNullAndTrim(dto.getRevCgstDigiIndicative(), true, 100));
		entity.setRevSgstDigiIndicative(
				checkNullAndTrim(dto.getRevSgstDigiIndicative(), true, 100));
		entity.setRevCessDigiIndicative(
				checkNullAndTrim(dto.getRevCessDigiIndicative(), true, 100));
		entity.setReclaimRetPrdDigiIndicative(checkNullAndTrim(
				removeQuotes(dto.getReclaimRetPrdDigiIndicative()), true, 100));
		entity.setReclaimIgstIndicative(
				checkNullAndTrim(dto.getReclaimIgstIndicative(), true, 100));
		entity.setReclaimCgstIndicative(
				checkNullAndTrim(dto.getReclaimCgstIndicative(), true, 100));
		entity.setReclaimSgstIndicative(
				checkNullAndTrim(dto.getReclaimSgstIndicative(), true, 100));
		entity.setReclaimCessIndicative(
				checkNullAndTrim(dto.getReclaimCessIndicative(), true, 100));
		entity.setItcRevComputeDateTime(checkNullAndTrim(
				removeQuotes(dto.getItcRevComputeDateTime()), true, 100));
		entity.setComputeID(
				checkNullAndTrim(removeQuotes(dto.getComputeId()), true, 100));
		entity.setReconDateTime(checkNullAndTrim(
				removeQuotes(dto.getReconDateTime()), true, 100));
		entity.setReconReportConfigId(checkNullAndTrim(
				removeQuotes(dto.getReconReportConfigID()), true, 100));
		entity.setCreatedOn(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setFileId(fileId);
		entity.setDelete(false);

		return entity;
	}

	private String checkNullAndTrim(String data, boolean isTrimReqr,
			int trimLength) {

		if (Strings.isNullOrEmpty(data)) {
			return null;
		} else {
			if (isTrimReqr && data.length() > trimLength) {
				return data.substring(0, trimLength);
			}
		}
		return data.trim();
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

	private Long convertStringToLong(String data) {

		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		data = removeQuotes(data);
		return Long.parseLong(data);
	}

	private BigDecimal convertStringToBigDecimal(String amt) {

		if (Strings.isNullOrEmpty(amt)) {
			return null;
		}
		return new BigDecimal((amt.trim()));
	}

	private String checkNull(Object obj) {
		return (obj != null) ? obj.toString() : null;
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
			return null;
		}
	}

	private String checkForNegative(String amt) {
		if (Strings.isNullOrEmpty(amt)) {
			return null;
		}

		if (!NumberFomatUtil.isNumber(amt)) {
			return amt;
		}

		if (NumberFomatUtil.getBigDecimal(amt).signum() == -1) {
			return amt.replace("-", "");
		}
		return amt;
	}
}
