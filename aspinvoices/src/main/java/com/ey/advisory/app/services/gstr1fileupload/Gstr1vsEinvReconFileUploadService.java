package com.ey.advisory.app.services.gstr1fileupload;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isNumber;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.caches.EInvoiceDocTypeCache;
import com.ey.advisory.app.data.entities.client.Gstr1vsEinvReconRespErrorEntity;
import com.ey.advisory.app.data.entities.client.Gstr1vsEinvReconRespProcessedEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vsEinvReconRespErrorRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vsEinvReconRespProcessedRepository;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Gstr1vsEinvReconFileUploadService {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	Gstr1vsEinvReconRespProcessedRepository gstr1vsEinvReconRespProcessedRepository;

	@Autowired
	Gstr1vsEinvReconRespErrorRepository gstr1vsEinvReconRespErrorRepository;

	@Autowired
	@Qualifier("DefaultEInvoiceDocTypeCache")
	private EInvoiceDocTypeCache docTypeCache;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	DocRepository docRepository;

	private static final String UPLOAD = "Upload";
	private static final String DIGIGST = "DigiGST";
	private static final String EINV = "EINV";
	private static final String INVOICEASSESSABLEAMOUNT = "InvoiceAssessableAmount";
	private static final String FILE_UPLOAD = "FILE";

	private static final String IGSTAMOUNT = "IGSTAmount";
	private static final String CGSTAMOUNT = "CGSTAmount";
	private static final String SGSTAMOUNT = "SGSTAmount";
	private static final String CESSAMOUNT = "CessAmount";
	private static final String INVOICEVALUE = "InvoiceValue";

	private static final String RECON_ID = "ReconID";
	private static final String DOC_HEADER_ID = "DocHeaderId";
	private static final String GET_CALL_ID = "GetCallId";

	private static Map<String, List<String>> reportCatagoryMap = ImmutableMap
			.<String, List<String>>builder().put("1", Arrays.asList("D", "d"))
			.put("3", Arrays.asList("S", "N", "s", "n"))
			.put("4", Arrays.asList("S", "N", "s", "n"))
			.put("5", Arrays.asList("S", "N", "s", "n"))
			.put("6", Arrays.asList("S", "N", "s", "n")).build();

	private static final List<String> responseDExcludeList = Arrays.asList("3",
			"4", "5", "6");

	private static final List<String> reportCatagoryOneExcludeList = Arrays
			.asList("S", "N", "s", "n");

	private static final List<String> reasonsList = Arrays.asList("S", "N", "D",
			"s", "n", "d");

	private static final List<String> notInDigiGstValidation = Arrays
			.asList("1", "2", "6");

	private static final List<String> notInEinvValidation = Arrays.asList("4",
			"6");

	private static final List<String> expectedHeaderNamesList = Arrays.asList(
			"Response", "PreviousResponse", "Remarks", "MismatchReason",
			"ScoreOutof11", "ReportType", "PreviousReport", "Sub Category",
			"Reason for mismatch(Subcategory)", 
			"E-InvoiceStatus", "AutoDraftstatus", "AutoDraftedDate",
			"ErrorCode", "ErrorMessage","TaxPeriod_GSTN",
			"TaxPeriod_DigiGST", "CalenderMonth_DigiGST", "SupplierGSTIN_GSTN",
			"SupplierGSTIN_DigiGST", "RecipientGSTIN_GSTN",
			"RecipientGSTIN_DigiGST", "RecipientName_GSTN",
			"RecipientName_DigiGST", "DocType_GSTN", "DocType_DigiGST",
			"SupplyType_GSTN", "SupplyType_DigiGST", "DocumentNumber_GSTN",
			"DocumentNumber_DigiGST", "DocumentDate_GSTN",
			"DocumentDate_DigiGST", "TaxableValue_GSTN", "TaxableValue_DigiGST",
			"IGST_GSTN", "IGST_DigiGST", "CGST_GSTN", "CGST_DigiGST",
			"SGST_GSTN", "SGST_DigiGST", "CESS_GSTN", "CESS_DigiGST",
			"TotalTax_GSTN", "TotalTax_DigiGST", "InvoiceValue_GSTN",
			"InvoiceValue_DigiGST", "POS_GSTN", "POS_DigiGST",
			"ReverseChargeFlag_GSTN", "ReverseChargeFlag_DigiGST",
			"EcomGSTIN_GSTN", "EcomGSTIN_DigiGST", "PortCode_GSTN",
			"PortCode_DigiGST", "ShippingBillNumber_GSTN",
			"ShippingBillNumber_DigiGST", "ShippingBillDate_GSTN",
			"ShippingBillDate_DigiGST", "SourceType_GSTN", "IRN_GSTN",
			"IRN_DigiGST", "IRNGenDate_GSTN", "IRNGenDate_DigiGST",
			"TableType_GSTN", "TableType_DigiGST",
			"CustomerType", "CustomerCode", "AccountingVoucherNumber",
			"AccountingVoucherDate", "CompanyCode", "RecordStatusDigiGST",
			"E-InvoiceGetCallDate", "E-InvoiceGetCallTime", "ReconID",
			"ReconDate", "ReconTime", "DocHeaderId", "GetCallId",
			"DocKey(DigiGST)", "DocKey(EINV)", "ReportCategory",
			"PlantCode-DigiGST", "Division-DigiGST", "SubDivision-DigiGST",
			"Location-DigiGST", "ProfitCentre1-DigiGST",
			"ProfitCentre2-DigiGST", "ProfitCentre3-DigiGST");

	private static final int noOfColumns = expectedHeaderNamesList.size();

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	public void validateGstr1vsEinvReconFile(Long fileId, String fileName,
			String folderName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Validating Gstr1vsEinvReconFile";
			LOGGER.debug(msg);
		}

		try {

			InputStream inputStream = getFileInpStream(fileName, folderName,
					fileId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(noOfColumns);
			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(inputStream, layout, rowHandler, null);

			@SuppressWarnings("unchecked")
			List<String> actualHeaderNames = (List<String>) (List<?>) Arrays
					.asList(rowHandler.getHeaderRow());// dd
			validateHeaders(fileId, actualHeaderNames);

			List<Object[]> fileList = rowHandler.getFileUploadList();

			if (CollectionUtils.isEmpty(fileList)) {
				String msg = "Failed Empty file..";
				LOGGER.error(msg);
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}
			validation(fileList, fileId);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Validation is Done for File Id "
						+ "method for File Id" + fileId;
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {
			markFileAsFailed(fileId, ex.getMessage());
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private Pair<List<Long>, List<Long>> getDocIds(List<Object[]> fileList) {
		List<Long> docIdList = new ArrayList<>();
		List<Long> activeDocIdList = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside  getDocIds ";
			LOGGER.debug(msg);
		}

		for (Object[] o : fileList) {
			if (o[75] == null)
				continue;

			if (!StringUtils.isEmpty(String.valueOf(o[75]))) {
				try {
					docIdList.add(Long.valueOf(o[75].toString().trim()));
				} catch (NumberFormatException e) {
					LOGGER.error("Encountered Invalid Doc Header ID {} ",
							String.valueOf(o[75]));
				}
			}
		}
		List<List<Long>> chunks = Lists.partition(docIdList, 2000);

		for (List<Long> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			activeDocIdList.addAll(docRepository.getActiveIds(chunk));
		}

		return new Pair<>(docIdList, activeDocIdList);
	}

	private Map<Long, Triplet<Boolean, Boolean, Boolean>> getDocFlags(
			List<Long> totalDocIds) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside  getDocFlags ";
			LOGGER.debug(msg);
		}

		List<Object[]> objectList = new ArrayList<>();
		Map<Long, Triplet<Boolean, Boolean, Boolean>> flagsMap = new HashMap<>();

		List<List<Long>> chunks = Lists.partition(totalDocIds, 2000);
		for (List<Long> chunk : chunks) {
			objectList.addAll(
					docRepository.getGstnSaveSentAndErrorFlagsById(chunk));
		}

		for (Object[] o : objectList) {

			flagsMap.put((Long) o[0], new Triplet<Boolean, Boolean, Boolean>(
					(boolean) o[1], (boolean) o[2], (boolean) o[3]));

		}
		return flagsMap;
	}

	private void validation(List<Object[]> fileList, Long fileId) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = " Inside Gstr1vsEinvReconFile Validation "
						+ "method for File Id " + fileId;
				LOGGER.debug(msg);
			}
			Pair<List<Long>, List<Long>> docIdsPair = getDocIds(fileList);
			List<Long> totalDocIds = docIdsPair.getValue0();
			List<Long> activeDocIds = docIdsPair.getValue1();

			Map<Long, Triplet<Boolean, Boolean, Boolean>> docFlagMap = getDocFlags(
					totalDocIds);

			Map<String, String[]> processedMap = new HashMap<>();

			List<Long> inActiveDocIds = new ArrayList<>();
			List<Long> docIdsToBeTrue = new ArrayList<>();
			List<Long> docIdsToBeFalse = new ArrayList<>();
			List<Long> docIdsDResponse = new ArrayList<>();

			int inActiveCounts = 0;

			List<Gstr1vsEinvReconRespProcessedEntity> processedEntityList = new ArrayList<>();

			List<Gstr1vsEinvReconRespErrorEntity> errorEntityList = new ArrayList<>();
			List<String> docKeysDigiGst = new ArrayList<>();
			List<String> docKeysEinv = new ArrayList<>();

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			List<Long> entityIds = clientGroupService
					.findEntityDetailsForGroupCode();
			Map<String, Long> gstinAndEntityMap = clientGroupService
					.getGstinAndEntityMapForGroupCode(entityIds);
			Map<Long, List<EntityConfigPrmtEntity>> onBoardingmap = onboardingConfigParamCheck
					.getEntityAndConfParamMap();

			for (Object[] rowData : fileList) {

				List<ProcessingResult> validationResult = new ArrayList<>();
				String errorMessage = null;
				String errorCodes = null;

				rowDataValidation(validationResult, rowData, activeDocIds,
						docIdsToBeTrue, docIdsToBeFalse, docIdsDResponse,
						inActiveDocIds, docFlagMap, gstinAndEntityMap,
						onBoardingmap);

				List<String> errorArrayList = validationResult.stream()
						.filter(result -> result.getDescription() != null)
						.map(ProcessingResult::getDescription)
						.collect(Collectors.toList());

				List<String> errorCodeList = validationResult.stream()
						.filter(result -> result.getCode() != null)
						.map(ProcessingResult::getCode)
						.collect(Collectors.toList());

				String[] stringArray = new String[noOfColumns];

				for (int i = 0; i < stringArray.length; i++) {
					if (i < rowData.length) {
						stringArray[i] = rowData[i] == null ? null
								: String.valueOf(rowData[i]);

					}
				}

				if (!CollectionUtils.isEmpty(errorArrayList))
					errorMessage = String.join(",", errorArrayList);
				if (!CollectionUtils.isEmpty(errorCodeList))
					errorCodes = String.join(",", errorCodeList);

				String docKey = null;

				if (!Strings.isNullOrEmpty(stringArray[78])) {
					docKey = stringArray[78].trim();
				} else {
					docKey = stringArray[77].trim();
				}

				if (validationResult.isEmpty() || (errorCodeList.size() == 1
						&& errorCodeList.contains("ER5001"))) {

					if (processedMap.containsKey(docKey)) {

						makeErrorEntityList(processedMap.get(docKey),
								errorEntityList, "ER5051", "Duplicate Invoice",
								fileId, userName);
					}
					processedMap.put(docKey, stringArray);

				} else {

					makeErrorEntityList(stringArray, errorEntityList,
							errorCodes, errorMessage, fileId, userName);
				}

			}

			if (!processedMap.isEmpty()) {
				for (Map.Entry<String, String[]> entry : processedMap
						.entrySet()) {

					String docId = Strings.isNullOrEmpty(entry.getValue()[75])
							? "0" : entry.getValue()[75].trim();

					String reportCatagory = String
							.valueOf(entry.getValue()[79]);

					if (inActiveDocIds.contains(Long.valueOf(docId))) {
						inActiveCounts++;
					}

					makeProcessedEntityList(entry.getValue(),
							processedEntityList, fileId, userName,
							inActiveDocIds);

					if (reportCatagory.equals("1") || reportCatagory.equals("2")
							|| reportCatagory.equals("6")) {

						if (!Strings.isNullOrEmpty(entry.getValue()[78])) {
							docKeysEinv.add(entry.getValue()[78]);
						}

					}

					if (!Strings.isNullOrEmpty(entry.getValue()[77])) {
						docKeysDigiGst.add(entry.getValue()[77]);
					}

				}
			}

			try {

				if (!docKeysDigiGst.isEmpty()) {

					List<List<String>> chunks = Lists.partition(docKeysDigiGst,
							2000);

					for (List<String> chunk : chunks) {
						LOGGER.debug("Inside Chunk Method");
						gstr1vsEinvReconRespProcessedRepository
								.softDeleteDuplicateInvBydocKeySR(chunk);
					}
				}

				if (!docKeysEinv.isEmpty()) {

					List<List<String>> chunks = Lists.partition(docKeysEinv,
							2000);

					for (List<String> chunk : chunks) {
						LOGGER.debug("Inside Chunk Method");
						gstr1vsEinvReconRespProcessedRepository
								.softDeleteDuplicateInvBydocKeyEinv(chunk);
					}
				}

				if (!CollectionUtils.isEmpty(processedEntityList))
					gstr1vsEinvReconRespProcessedRepository
							.saveAll(processedEntityList);

				if (!CollectionUtils.isEmpty(errorEntityList))
					gstr1vsEinvReconRespErrorRepository
							.saveAll(errorEntityList);

				if (!CollectionUtils.isEmpty(docIdsToBeTrue)) {

					List<List<Long>> chunks = Lists.partition(docIdsToBeTrue,
							2000);
					for (List<Long> chunk : chunks) {
						LOGGER.debug("Inside Chunk Method");
						docRepository
								.updateIsSavetoGstnAndIsSendToGstnInToTrueByDocID(
										true, true, "N", chunk, fileId,
										FILE_UPLOAD, null, null, null, false,
										null, null);
					}

				}
				if (!CollectionUtils.isEmpty(docIdsToBeFalse)) {

					List<List<Long>> chunks = Lists.partition(docIdsToBeFalse,
							2000);
					for (List<Long> chunk : chunks) {
						LOGGER.debug("Inside Chunk Method");
						docRepository
								.updateIsSavetoGstnAndIsSendToGstnInToFalseByDocID(
										false, false, "S", chunk, fileId,
										FILE_UPLOAD, null, null, null, false,
										null, null);
					}

				}

				if (!CollectionUtils.isEmpty(docIdsDResponse)) {

					List<List<Long>> chunks = Lists.partition(docIdsDResponse,
							2000);
					for (List<Long> chunk : chunks) {
						LOGGER.debug("Inside Chunk Method");
						docRepository
								.updateIsSavetoGstnAndIsSendToGstnInToFalseByDocID(
										true, true, "D", chunk, fileId,
										FILE_UPLOAD, null, null, null, false,
										null, null);

					}

				}

			} catch (Exception e) {
				String msg = "Error occured during Saving Entity List";
				LOGGER.error(msg, e);
				throw new AppException(msg, e);
			}

			Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = fileStatusRepository
					.findById(fileId);
			if (gstr1FileStatusEntity.isPresent()) {
				gstr1FileStatusEntity.get().setProcessed(
						processedEntityList.size() - inActiveCounts);
				gstr1FileStatusEntity.get()
						.setError(errorEntityList.size() + inActiveCounts);
				gstr1FileStatusEntity.get().setTotal(
						processedEntityList.size() + errorEntityList.size());
				gstr1FileStatusEntity.get()
						.setFileStatus(JobStatusConstants.PROCESSED);
				fileStatusRepository.save(gstr1FileStatusEntity.get());
			}
		} catch (Exception e) {

			String msg = "Error occured during Validation";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	private void makeProcessedEntityList(String[] stringArray,
			List<Gstr1vsEinvReconRespProcessedEntity> processedEntityList,
			Long fileId, String userName, List<Long> inActiveDocIds) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside makeProcessedEntityList Method for File Id "
					+ fileId;
			LOGGER.debug(msg);
		}

		Gstr1vsEinvReconRespProcessedEntity processedEntity = new Gstr1vsEinvReconRespProcessedEntity();

		String errorCode = null;
		String errorDesc = null;
		String reportCatagory = String.valueOf(stringArray[79]);

		String docId = Strings.isNullOrEmpty(stringArray[75]) ? "0"
				: stringArray[75].trim();

		String tableTypeEinv = (stringArray[62] != null)
				? stringArray[62].trim() : "";
		String tableTypeDigiGST = (stringArray[63] != null)
				? stringArray[63].trim() : "";

		if (inActiveDocIds.contains(Long.valueOf(docId))) {
			processedEntity.setProcessed(false);
			errorCode = "ER5001";
			errorDesc = "Invoice is not Active in DigiGST";
		} else {
			processedEntity.setProcessed(true);
		}

		if (!Strings.isNullOrEmpty(stringArray[0])) {
			processedEntity
					.setUserResponse(stringArray[0].trim().toUpperCase());
		} else {
			processedEntity
					.setUserResponse(!Strings.isNullOrEmpty(stringArray[0])
							? stringArray[0].trim() : null);
		}
		processedEntity.setPrevResponse((!Strings.isNullOrEmpty(stringArray[1]))
				? stringArray[1].trim() : null);

		processedEntity.setSubCategory(trimTo100Chars(stringArray[7]));
		processedEntity.setReasonForMismatch(trimTo100Chars(stringArray[8]));

		processedEntity.setCgstinSR(trimTo25Chars(stringArray[20]));

		processedEntity.setCgstinEinv(trimTo25Chars(stringArray[19]));

		processedEntity.setSgstinSR((!Strings.isNullOrEmpty(stringArray[18]))
				? stringArray[18].trim() : null);
		processedEntity.setSgstinEinv((!Strings.isNullOrEmpty(stringArray[17]))
				? stringArray[17].trim() : null);
		processedEntity.setDocTypeSR((!Strings.isNullOrEmpty(stringArray[24]))
				? stringArray[24].trim() : null);
		processedEntity.setDocTypeEinv((!Strings.isNullOrEmpty(stringArray[23]))
				? stringArray[23].trim() : null);

		processedEntity.setTableTypeEinv((!Strings.isNullOrEmpty(tableTypeEinv))
				? tableTypeEinv.trim() : null);
		processedEntity
				.setTableTypeSR((!Strings.isNullOrEmpty(tableTypeDigiGST))
						? tableTypeDigiGST.trim() : null);

		String docNumSR = stringArray[28];
		if (docNumSR != null && docNumSR.contains("'")) {
			String docNumSRWithOutQuotes = docNumSR.replace("'", "");

			processedEntity
					.setDocNumSR((!Strings.isNullOrEmpty(docNumSRWithOutQuotes))
							? docNumSRWithOutQuotes.trim() : null);
		} else {
			processedEntity.setDocNumSR((!Strings.isNullOrEmpty(docNumSR))
					? docNumSR.trim() : null);
		}

		String docNumEinv = stringArray[27];
		if (docNumEinv != null && docNumEinv.contains("'")) {
			String docNumEinvWithOutQuotes = docNumEinv.replace("'", "");
			processedEntity.setDocNumEinv(
					(!Strings.isNullOrEmpty(docNumEinvWithOutQuotes))
							? docNumEinvWithOutQuotes.trim() : null);

		} else {
			processedEntity.setDocNumEinv((!Strings.isNullOrEmpty(docNumEinv))
					? docNumEinv.trim() : null);
		}
		processedEntity.setDocDateSR(DateUtil.parseObjToDate(stringArray[30]));
		processedEntity
				.setDocDateEinv(DateUtil.parseObjToDate((stringArray[29])));

		processedEntity.setTaxableValueSR(Strings.isNullOrEmpty(stringArray[32])
				? null : new BigDecimal((stringArray[32].trim())));
		processedEntity
				.setTaxableValueEinv(Strings.isNullOrEmpty(stringArray[31])
						? null : new BigDecimal((stringArray[31].trim())));

		processedEntity.setIgstAmtSR(Strings.isNullOrEmpty(stringArray[34])
				? null : new BigDecimal((stringArray[34].trim())));

		processedEntity.setIgstAmtEinv(Strings.isNullOrEmpty(stringArray[33])
				? null : new BigDecimal((stringArray[33].trim())));

		processedEntity.setCgstAmtSR(Strings.isNullOrEmpty(stringArray[36])
				? null : new BigDecimal((stringArray[36].trim())));

		processedEntity.setCgstAmtEinv(Strings.isNullOrEmpty(stringArray[35])
				? null : new BigDecimal((stringArray[35].trim())));

		processedEntity.setSgstAmtSR(Strings.isNullOrEmpty(stringArray[38])
				? null : new BigDecimal((stringArray[38].trim())));

		processedEntity.setSgstAmtEinv(Strings.isNullOrEmpty(stringArray[37])
				? null : new BigDecimal((stringArray[37].trim())));

		processedEntity.setCessAmtSR(Strings.isNullOrEmpty(stringArray[40])
				? null : new BigDecimal((stringArray[40].trim())));

		processedEntity.setCessAmtEinv(Strings.isNullOrEmpty(stringArray[39])
				? null : new BigDecimal((stringArray[39].trim())));

		processedEntity.setInvValSR(Strings.isNullOrEmpty(stringArray[44])
				? null : new BigDecimal((stringArray[44].trim())));

		processedEntity.setInvValEinv(Strings.isNullOrEmpty(stringArray[43])
				? null : new BigDecimal((stringArray[43].trim())));

		processedEntity.setReconId((!Strings.isNullOrEmpty(stringArray[72])
				? stringArray[72] : null));
		processedEntity.setDocHeaderId((!Strings.isNullOrEmpty(stringArray[75]))
				? Long.valueOf(stringArray[75].trim()) : null);
		processedEntity.setGetCallId((!Strings.isNullOrEmpty(stringArray[76]))
				? Long.valueOf(stringArray[76]) : null);
		processedEntity.setFileId(fileId);
		processedEntity.setDocKeySR((!Strings.isNullOrEmpty(stringArray[77]))
				? stringArray[77].trim() : null);
		processedEntity.setDocKeyEinv((!Strings.isNullOrEmpty(stringArray[78])
				? stringArray[78].trim() : null));
		//added 7 columns################################
		processedEntity.setPlantCode((!Strings.isNullOrEmpty(stringArray[80]))
				? stringArray[80].trim() : null);
		
		processedEntity.setDivision((!Strings.isNullOrEmpty(stringArray[81]))
				? stringArray[81].trim() : null);
		
		processedEntity.setSubDivision((!Strings.isNullOrEmpty(stringArray[82]))
				? stringArray[82].trim() : null);
		
		processedEntity.setLocation((!Strings.isNullOrEmpty(stringArray[83]))
				? stringArray[83].trim() : null);
		
		processedEntity.setProfitCentre1((!Strings.isNullOrEmpty(stringArray[84]))
				? stringArray[84].trim() : null);
		
		processedEntity.setProfitCentre2((!Strings.isNullOrEmpty(stringArray[85]))
				? stringArray[85].trim() : null);
		
		processedEntity.setProfitCentre3((!Strings.isNullOrEmpty(stringArray[86]))
				? stringArray[86].trim() : null);
		//######################################################3

		processedEntity.setReportCategory(Integer.parseInt(reportCatagory));

		String retPeriodEinv = stringArray[14];
		if (retPeriodEinv != null && retPeriodEinv.contains("'")) {
			String retPeriodEinvWithOutQuotes = retPeriodEinv.replace("'", "");

			processedEntity.setRetPeriodEinv(
					(!Strings.isNullOrEmpty(retPeriodEinvWithOutQuotes)
							? retPeriodEinvWithOutQuotes : null));
		} else {
			processedEntity
					.setRetPeriodEinv((!Strings.isNullOrEmpty(retPeriodEinv)
							? retPeriodEinv : null));
		}

		String retPeriodSR = stringArray[15];
		if (retPeriodSR != null && retPeriodSR.contains("'")) {
			String retPeriodSRWithOutQuotes = retPeriodSR.replace("'", "");

			processedEntity.setRetPeriodSR(
					(!Strings.isNullOrEmpty(retPeriodSRWithOutQuotes)
							? retPeriodSRWithOutQuotes : null));
		} else {
			processedEntity.setRetPeriodSR(
					(!Strings.isNullOrEmpty(retPeriodSR) ? retPeriodSR : null));
		}
		processedEntity.setErrorId(errorCode);
		processedEntity.setErrorDesc(errorDesc);
		processedEntity.setCreatedBy(userName);
		processedEntity.setCreatedOn(LocalDateTime.now());

		processedEntityList.add(processedEntity);
	}

	private String trimTo100Chars(String obj) {
		if (Strings.isNullOrEmpty(obj))
			return null;

		if (obj.length() > 100) {
			return obj.substring(0, 100);
		}
		return obj;
	}

	private String trimTo25Chars(String obj) {
		if (Strings.isNullOrEmpty(obj))
			return null;

		if (obj.length() > 25) {
			return obj.substring(0, 25);
		}
		return obj;
	}

	private void makeErrorEntityList(String[] stringArray,
			List<Gstr1vsEinvReconRespErrorEntity> listErrorDto, String errorId,
			String errorDesc, Long fileId, String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside makeProcessedEntityList Method for File Id "
					+ fileId;
			LOGGER.debug(msg);
		}

		Gstr1vsEinvReconRespErrorEntity errorReportEntity = new Gstr1vsEinvReconRespErrorEntity();

		errorReportEntity.setUserResponse(trimTo100Chars(stringArray[0]));

		errorReportEntity.setPrevResponse(trimTo100Chars(stringArray[1]));

		errorReportEntity.setRemarks(trimTo100Chars(stringArray[2]));
		errorReportEntity.setMismatchReason(trimTo100Chars(stringArray[3]));
		errorReportEntity.setScore(trimTo100Chars(stringArray[4]));
		errorReportEntity.setRptType(trimTo100Chars(stringArray[5]));
		errorReportEntity.setPrevRptTy(trimTo100Chars(stringArray[6]));
		errorReportEntity.setSubCategory(trimTo100Chars(stringArray[7]));
		errorReportEntity.setReasonForMismatch(trimTo100Chars(stringArray[8]));

		String retPeriodSR = stringArray[15];
		if (retPeriodSR != null && retPeriodSR.contains("'")) {
			String retPeriodSRWithOutQuotes = retPeriodSR.replace("'", "");

			errorReportEntity
					.setRetPeriodSR(trimTo100Chars(retPeriodSRWithOutQuotes));
		} else {
			errorReportEntity.setRetPeriodSR(trimTo100Chars(stringArray[15]));
		}

		String retPeriodEinv = stringArray[14];
		if (retPeriodEinv != null && retPeriodEinv.contains("'")) {
			String retPeriodEinvWithOutQuotes = retPeriodEinv.replace("'", "");

			errorReportEntity.setRetPeriodEinv(
					trimTo100Chars(retPeriodEinvWithOutQuotes));
		} else {
			errorReportEntity.setRetPeriodEinv(trimTo100Chars(stringArray[14]));

		}
		String calMonthSR = stringArray[16];
		if (calMonthSR != null && calMonthSR.contains("'")) {
			String calMonthSRWithOutQuotes = calMonthSR.replace("'", "");

			errorReportEntity
					.setCalMonthSR(trimTo100Chars(calMonthSRWithOutQuotes));

		} else {
			errorReportEntity.setCalMonthSR(trimTo100Chars(stringArray[16]));
		}
		errorReportEntity.setSgstinSR(trimTo100Chars(stringArray[18]));
		errorReportEntity.setSgstinEinv(trimTo100Chars(stringArray[17]));
		errorReportEntity.setCgstinSR(trimTo100Chars(stringArray[20]));
		errorReportEntity.setCgstinEinv(trimTo100Chars(stringArray[19]));

		errorReportEntity.setRecipientNameEinv(trimTo100Chars(stringArray[21]));
		errorReportEntity.setCustLegalName(trimTo100Chars(stringArray[22]));

		errorReportEntity.setDocTypeSR(trimTo100Chars(stringArray[24]));
		errorReportEntity.setDocTypeEinv(trimTo100Chars(stringArray[23]));
		errorReportEntity.setSupplyTypeSR(trimTo100Chars(stringArray[26]));
		errorReportEntity.setSupplyTypeEinv(trimTo100Chars(stringArray[25]));

		String docNumSR = stringArray[28];
		if (docNumSR != null && docNumSR.contains("'")) {
			String docNumSRWithOutQuotes = docNumSR.replace("'", "");
			errorReportEntity
					.setDocNumSR(trimTo100Chars(docNumSRWithOutQuotes));
		} else {
			errorReportEntity.setDocNumSR(trimTo100Chars(stringArray[28]));

		}

		String docNumEinv = stringArray[27];
		if (docNumEinv != null && docNumEinv.contains("'")) {
			String docNumEinvWithOutQuotes = docNumEinv.replace("'", "");
			errorReportEntity
					.setDocNumEinv(trimTo100Chars(docNumEinvWithOutQuotes));
		} else {
			errorReportEntity.setDocNumEinv(trimTo100Chars(stringArray[27]));
		}

		errorReportEntity.setDocDateSR(trimTo100Chars(stringArray[30]));
		errorReportEntity.setDocDateEinv(trimTo100Chars(stringArray[29]));

		errorReportEntity.setTaxableValueSR(trimTo100Chars(stringArray[32]));
		errorReportEntity.setTaxableValueEinv(trimTo100Chars(stringArray[31]));
		errorReportEntity.setIgstAmtSR(trimTo100Chars(stringArray[34]));
		errorReportEntity.setIgstAmtEinv(trimTo100Chars(stringArray[33]));
		errorReportEntity.setCgstAmtSR(trimTo100Chars(stringArray[36]));
		errorReportEntity.setCgstAmtEinv(trimTo100Chars(stringArray[35]));
		errorReportEntity.setSgstAmtSR(trimTo100Chars(stringArray[38]));
		errorReportEntity.setSgstAmtEinv(trimTo100Chars(stringArray[37]));
		errorReportEntity.setCessAmtSR(trimTo100Chars(stringArray[40]));
		errorReportEntity.setCessAmtEinv(trimTo100Chars(stringArray[39]));
		errorReportEntity.setTotalTaxEinv(trimTo100Chars(stringArray[41]));
		errorReportEntity.setTotalTaxSR(trimTo100Chars(stringArray[42]));
		errorReportEntity.setInvValSR(trimTo100Chars(stringArray[44]));
		errorReportEntity.setInvValEinv(trimTo100Chars(stringArray[43]));

		String billingPosSR = stringArray[46];
		if (billingPosSR != null && billingPosSR.contains("'")) {
			String billingPosSRWithOutQuotes = billingPosSR.replace("'", "");
			errorReportEntity
					.setBillingPosSR(trimTo100Chars(billingPosSRWithOutQuotes));

		} else {
			errorReportEntity.setBillingPosSR(trimTo100Chars(stringArray[46]));

		}
		String billingPosEinv = stringArray[45];
		if (billingPosEinv != null && billingPosEinv.contains("'")) {
			String billingPosEinvWithOutQuotes = billingPosEinv.replace("'",
					"");
			errorReportEntity.setBillingPosEinv(
					trimTo100Chars(billingPosEinvWithOutQuotes));
		} else {
			errorReportEntity
					.setBillingPosEinv(trimTo100Chars(stringArray[45]));

		}
		errorReportEntity.setRechrgFlagSR(trimTo100Chars(stringArray[48]));
		errorReportEntity.setRchrgFlagEinv(trimTo100Chars(stringArray[47]));
		errorReportEntity.setEcomGstinSR(trimTo100Chars(stringArray[50]));
		errorReportEntity.setEcomGstinEinv(trimTo100Chars(stringArray[49]));
		errorReportEntity.setPortCodeSR(trimTo100Chars(stringArray[52]));
		errorReportEntity.setPortCodeEinv(trimTo100Chars(stringArray[51]));

		errorReportEntity.setShipBillNumSR(trimTo100Chars(stringArray[54]));
		errorReportEntity.setShipBillNumEinv(trimTo100Chars(stringArray[53]));
		errorReportEntity.setShipBillDateSR(trimTo100Chars(stringArray[56]));
		errorReportEntity.setShipBillDateEinv(trimTo100Chars(stringArray[55]));

		errorReportEntity.setSrcTypeOfIRN(trimTo100Chars(stringArray[57]));
		errorReportEntity.setIrnSR(trimTo100Chars(stringArray[59]));
		errorReportEntity.setIrnEniv(trimTo100Chars(stringArray[58]));
		errorReportEntity.setIrnDateSR(trimTo100Chars(stringArray[61]));
		errorReportEntity.setIrnDateEinv(trimTo100Chars(stringArray[60]));
		errorReportEntity.setEnivStatus(trimTo100Chars(stringArray[9]));
		errorReportEntity.setAutoDraftStatus(trimTo100Chars(stringArray[10]));
		errorReportEntity.setAutoDraftedDate(trimTo100Chars(stringArray[11]));
		errorReportEntity.setErrorCode(trimTo100Chars(stringArray[12]));
		errorReportEntity.setErrorMessage(trimTo100Chars(stringArray[13]));
		errorReportEntity.setTableTypeSR(trimTo100Chars(stringArray[63]));
		errorReportEntity.setTableTypeEinv(trimTo100Chars(stringArray[62]));

		errorReportEntity.setCustType(trimTo100Chars(stringArray[64]));
		errorReportEntity.setCustCode(trimTo100Chars(stringArray[65]));
		errorReportEntity
				.setAccountingVoucherNum(trimTo100Chars(stringArray[66]));
		errorReportEntity
				.setAccountingVoucherDate(trimTo100Chars(stringArray[67]));
		errorReportEntity.setCompanyCode(trimTo100Chars(stringArray[68]));
		errorReportEntity
				.setRecordStatusDigiGst(trimTo100Chars(stringArray[69]));
		errorReportEntity.setEinvGetCallDate(trimTo100Chars(stringArray[70]));
		errorReportEntity.setEinvGetCallTime(trimTo100Chars(stringArray[71]));

		String setReconId = stringArray[72];
		if (setReconId != null && setReconId.contains("'")) {
			String setReconIdWithOutQuotes = setReconId.replace("'", "");

			errorReportEntity
					.setReconId(trimTo100Chars(setReconIdWithOutQuotes));

		} else {
			errorReportEntity.setReconId(trimTo100Chars(stringArray[72]));

		}
		errorReportEntity.setReconDate(trimTo100Chars(stringArray[73]));

		String reconTime = stringArray[74];
		if (reconTime != null && reconTime.contains("'")) {
			String reconTimeWithOutQuotes = reconTime.replace("'", "");
			errorReportEntity
					.setReconTime(trimTo100Chars(reconTimeWithOutQuotes));

		} else {
			errorReportEntity.setReconTime(trimTo100Chars(stringArray[74]));

		}
		errorReportEntity.setDocHeaderId(trimTo100Chars(stringArray[75]));

		errorReportEntity.setGetCallId(trimTo100Chars(stringArray[76]));
		errorReportEntity.setDocKeySR(trimTo100Chars(stringArray[77]));
		errorReportEntity.setDocKeyEinv(trimTo100Chars(stringArray[78]));
		errorReportEntity.setReportCategory(trimTo100Chars(stringArray[79]));
//added 7 columns--.
		errorReportEntity.setPlantCode(trimTo100Chars(stringArray[80]));
		errorReportEntity.setDivision(trimTo100Chars(stringArray[81]));
		errorReportEntity.setSubDivision(trimTo100Chars(stringArray[82]));
		errorReportEntity.setLocation(trimTo100Chars(stringArray[83]));
		errorReportEntity.setProfitCentre1(trimTo100Chars(stringArray[84]));
		errorReportEntity.setProfitCentre2(trimTo100Chars(stringArray[85]));
		errorReportEntity.setProfitCentre3(trimTo100Chars(stringArray[86]));
		//
		errorReportEntity.setFileId(String.valueOf(fileId));
		errorReportEntity.setErrorId(errorId);
		errorReportEntity.setErrorDesc(errorDesc);
		errorReportEntity.setCreatedBy(userName);
		errorReportEntity.setCreatedOn(LocalDateTime.now());
		listErrorDto.add(errorReportEntity);

	}

	private void markDocIdForUpdate(List<Long> docIdsToBeTrue,
			List<Long> docIdsToBeFalse, List<Long> docIdsDResponse,
			Object[] rowData) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside markDocIdForUpdate Method ";

			LOGGER.debug(msg);
		}

		Long docId = Long.valueOf(rowData[75].toString());
		String userResponse = (rowData[0].toString());

		if (userResponse.equalsIgnoreCase("N")) {

			if (docIdsToBeFalse.contains(docId)
					|| docIdsDResponse.contains(docId)) {
				docIdsToBeFalse.remove(docId);
				docIdsDResponse.remove(docId);
			}

			if (!docIdsToBeTrue.contains(docId)) {
				docIdsToBeTrue.add(docId);
			}
		}

		if (userResponse.equalsIgnoreCase("S")) {

			if (docIdsToBeTrue.contains(docId)
					|| docIdsDResponse.contains(docId)) {
				docIdsToBeTrue.remove(docId);
				docIdsDResponse.remove(docId);
			}

			if (!docIdsToBeFalse.contains(docId)) {
				docIdsToBeFalse.add(docId);
			}
		}

		if (userResponse.equalsIgnoreCase("D")) {

			if (docIdsToBeTrue.contains(docId)
					|| docIdsToBeFalse.contains(docId)) {
				docIdsToBeTrue.remove(docId);
				docIdsToBeFalse.remove(docId);
			}

			if (!docIdsDResponse.contains(docId)) {
				docIdsDResponse.add(docId);
			}

		}
	}

	private void rowDataValidation(List<ProcessingResult> validationResult,
			Object[] rowData, List<Long> activeDocIds,
			List<Long> docIdsToBeTrue, List<Long> docIdsToBeFalse,
			List<Long> docIdsDResponse, List<Long> inActiveDocIds,
			Map<Long, Triplet<Boolean, Boolean, Boolean>> docFlagMap,
			Map<String, Long> gstinAndEntityMap,
			Map<Long, List<EntityConfigPrmtEntity>> onBoardingmap) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Row Validation Method";
			LOGGER.debug(msg);
		}

		boolean isDocIdValid = isValidId(rowData, validationResult,
				DOC_HEADER_ID, activeDocIds, inActiveDocIds);

		isDocumentTypeValid(rowData, validationResult, DIGIGST, isDocIdValid);
		isDocumentTypeValid(rowData, validationResult, EINV, isDocIdValid);

		isSupplierGstinValid(rowData, validationResult, DIGIGST, isDocIdValid);
		isSupplierGstinValid(rowData, validationResult, EINV, isDocIdValid);

		isCustomerGstinValid(rowData, validationResult, DIGIGST, isDocIdValid);
		isCustomerGstinValid(rowData, validationResult, EINV, isDocIdValid);

		isDocumentNumberValid(rowData, validationResult, DIGIGST, isDocIdValid);
		isDocumentNumberValid(rowData, validationResult, EINV, isDocIdValid);

		isDocumentDateValid(rowData, validationResult, DIGIGST, isDocIdValid);
		isDocumentDateValid(rowData, validationResult, EINV, isDocIdValid);

		isTaxPeriodValid(rowData, validationResult, DIGIGST, isDocIdValid);
		isTaxPeriodValid(rowData, validationResult, EINV, isDocIdValid);
		
	

		isAmountValid(rowData, validationResult, DIGIGST,
				INVOICEASSESSABLEAMOUNT);
		isAmountValid(rowData, validationResult, EINV, INVOICEASSESSABLEAMOUNT);

		isAmountValid(rowData, validationResult, DIGIGST, IGSTAMOUNT);
		isAmountValid(rowData, validationResult, EINV, IGSTAMOUNT);

		isAmountValid(rowData, validationResult, DIGIGST, CGSTAMOUNT);
		isAmountValid(rowData, validationResult, EINV, CGSTAMOUNT);

		isAmountValid(rowData, validationResult, DIGIGST, SGSTAMOUNT);
		isAmountValid(rowData, validationResult, EINV, SGSTAMOUNT);

		isAmountValid(rowData, validationResult, DIGIGST, CESSAMOUNT);
		isAmountValid(rowData, validationResult, EINV, CESSAMOUNT);

		isAmountValid(rowData, validationResult, DIGIGST, INVOICEVALUE);
		isAmountValid(rowData, validationResult, EINV, INVOICEVALUE);

		isValidId(rowData, validationResult, RECON_ID, activeDocIds,
				inActiveDocIds);

		isValidId(rowData, validationResult, GET_CALL_ID, activeDocIds,
				inActiveDocIds);

		isGstr1ReturnFiled(rowData, validationResult, DIGIGST, isDocIdValid,
				gstinAndEntityMap, onBoardingmap);
		isGstr1ReturnFiled(rowData, validationResult, EINV, isDocIdValid,
				gstinAndEntityMap, onBoardingmap);

		boolean isReportCatagoryValid = isReportCatagoryValid(rowData,
				validationResult);

		if (isReportCatagoryValid) {

			boolean isUserRespValid = isUserResponseValid(rowData,
					validationResult, isDocIdValid, docFlagMap);

			if (validationResult.isEmpty() && isUserRespValid && isDocIdValid)
				markDocIdForUpdate(docIdsToBeTrue, docIdsToBeFalse,
						docIdsDResponse, rowData);

		}
		//added new 7 columns--US-78041
		isPlantCodeValid(rowData, validationResult);
		isDivisionValid(rowData, validationResult);
		isSubDivisionValid(rowData, validationResult);
		isLocationValid(rowData, validationResult);
		isProfitCenter1Valid(rowData, validationResult);
		isProfitCenter2Valid(rowData, validationResult);
		isProfitCenter3Valid(rowData, validationResult);
		

	}

	private boolean isUserResponseValid(Object[] rowData,
			List<ProcessingResult> validationResult, boolean isDocIdValid,
			Map<Long, Triplet<Boolean, Boolean, Boolean>> docFlagMap) {
		boolean isValid = true;
		String reportCatagory = String.valueOf(rowData[79]);

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isUserResponseValid Method";
			LOGGER.debug(msg);
		}

		if (isPresent(rowData[0])) {

			String userResponse = String.valueOf(rowData[0]);
			List<String> expectedResponse = new ArrayList<>();

			if (reportCatagory.equals("2")) {
				String errMsg = null;
				String errCode = null;

				if (reasonsList.contains(userResponse)) {
					errMsg = "Invalid response for the selected report type.";
					errCode = "ER5056";
				} else {
					errMsg = "User Response is not as per masters.";
					errCode = "ER5002";
				}
				ProcessingResult result = new ProcessingResult(UPLOAD, errCode,
						errMsg);
				validationResult.add(result);
				isValid = false;
				return isValid;
			}

			if (reportCatagoryMap.containsKey(reportCatagory)) {
				expectedResponse = reportCatagoryMap.get(reportCatagory);
			}

			if ((responseDExcludeList.contains(reportCatagory)
					&& userResponse.equalsIgnoreCase("D"))
					|| (reportCatagoryOneExcludeList.contains(userResponse)
							&& reportCatagory.equals("1"))) {

				String errMsg = "Invalid response for the selected report type.";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER5056",
						errMsg);
				validationResult.add(result);
				isValid = false;
				return isValid;
			}

			if (!expectedResponse.contains(userResponse)
					|| reportCatagory.equals("2")) {
				String errMsg = "User Response is not as per masters.";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER5002",
						errMsg);
				validationResult.add(result);
				isValid = false;
			}
		} else if (!reportCatagory.equals("2")) {

			String errMsg = "User Response Cannot be blank.";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER5003",
					errMsg);
			validationResult.add(result);
			isValid = false;

		}
		if (isDocIdValid) {
			Long docId = Long.valueOf(rowData[75].toString());

			Triplet<Boolean, Boolean, Boolean> docFlagTriplet = docFlagMap
					.get(docId);

			if (docFlagTriplet.getValue0() && !docFlagTriplet.getValue1()
					&& !docFlagTriplet.getValue2()) {
				String errMsg = "Save is in progress, Cannot accept User response";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER5055",
						errMsg);
				validationResult.add(result);
				isValid = false;

			}

		}
		return isValid;
	}

	private boolean isReportCatagoryValid(Object[] rowData,
			List<ProcessingResult> validationResult) {
		boolean isValid = true;

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isReportCatagoryValid Method";
			LOGGER.debug(msg);
		}

		if (isPresent(rowData[79])) {

			String reportCatagory = String.valueOf(rowData[79]);
			if (!isNumber(reportCatagory)
					|| Integer.parseInt(reportCatagory) > 6
					|| Integer.parseInt(reportCatagory) <= 0) {
				String errMsg = "Invalid Report Catagory.";
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER5054",
						errMsg);
				validationResult.add(result);
				isValid = false;
			}
		} else {
			String errMsg = " Report Catagory Cannot be blank.";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER5053",
					errMsg);
			validationResult.add(result);
			isValid = false;

		}
		return isValid;
	}

	private void isDocumentTypeValid(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			boolean isDocIdValid) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isDocumentTypeValid Method";
			LOGGER.debug(msg);
		}

		String reportCatagory = (rowData[79] != null)
				? rowData[79].toString().trim() : "";

		if ((notInEinvValidation.contains(reportCatagory)
				&& type.equalsIgnoreCase(EINV))
				|| (notInDigiGstValidation.contains(reportCatagory)
						&& type.equalsIgnoreCase(DIGIGST) && !isDocIdValid)) {
			return;
		}

		int index = type.equalsIgnoreCase(DIGIGST) ? 24 : 23;
		String errorCodeBlank = type.equalsIgnoreCase(DIGIGST) ? "ER5005"
				: "ER5006";
		String errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5007"
				: "ER5008";

		if (!isPresent(rowData[index])) {
			String errMsg = String.format("DocumentType(%s) cannot be empty.",
					type);
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		docTypeCache = StaticContextHolder.getBean(
				"DefaultEInvoiceDocTypeCache", EInvoiceDocTypeCache.class);
		int n = docTypeCache
				.finddocType(trimAndConvToUpperCase(rowData[index].toString()));
		if (n == 0) {
			String errMsg = String.format("Invalid DocumentType(%s).", type);
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeStuct, errMsg);
			validationResult.add(result);

		}
	}

	private void isTaxPeriodValid(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			boolean isDocIdValid) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isTaxPeriodValid Method";
			LOGGER.debug(msg);
		}

		String reportCatagory = (rowData[79] != null)
				? rowData[79].toString().trim() : "";

		if ((notInEinvValidation.contains(reportCatagory)
				&& type.equalsIgnoreCase(EINV))
				|| (notInDigiGstValidation.contains(reportCatagory)
						&& type.equalsIgnoreCase(DIGIGST) && !isDocIdValid)) {
			return;
		}

		int index = type.equalsIgnoreCase(DIGIGST) ? 15 : 14;
		String errorCodeBlank = type.equalsIgnoreCase(DIGIGST) ? "ER5057"
				: "ER5058";
		String errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5059"
				: "ER5060";
		if (!isPresent(rowData[index])) {
			String errMsg = String.format("Tax Period cannot be blank.");
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		String returnPeriod = rowData[index].toString();
		if (returnPeriod.contains("'")) {
			String returnPeriodWithOutQuotes = returnPeriod.replace("'", "");
			rowData[index] = returnPeriodWithOutQuotes;
			returnPeriod = returnPeriodWithOutQuotes;
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Tax Period Validation Method for";
			LOGGER.debug(msg);
		}
		if (returnPeriod.length() > 6) {
			String errMsg = String.format("Invalid Tax Period(%s).", type);

			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeStuct, errMsg);
			validationResult.add(result);
			if (returnPeriod.length() > 100) {
				rowData[index] = rowData[index].toString().substring(0, 100);
			}
			return;
		}

		if (!returnPeriod.matches("[0-9]+") || returnPeriod.length() != 6) {
			String errMsg = String.format("Invalid Tax Period(%s).", type);

			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeStuct, errMsg);
			validationResult.add(result);
			return;
		}
		if (returnPeriod.matches("[0-9]+")) {

			int month = Integer.valueOf(returnPeriod.substring(0, 2));
			if (returnPeriod.length() != 6 || month > 12 || month == 00) {
				String errMsg = String.format("Invalid Tax Period(%s).", type);

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				return;
			}
		}
	}
	
	
	/*private void isPlantCodeValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isTaxPeriodValid Method";
			LOGGER.debug(msg);
		}

		String plantCode = (rowData[80] != null)
				? rowData[80].toString().trim() : "";

		String errorCodeStuct = "ER5066";
		if (isPresent(rowData[80])) {
			
			if (plantCode.length() > 100) {
				String errMsg = String.format("Invalid Plant");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}*/

	private void isSupplierGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			boolean isDocIdValid) {
		int index = type.equalsIgnoreCase(DIGIGST) ? 18 : 17;
		String errorCode = type.equalsIgnoreCase(DIGIGST) ? "ER5009" : "ER5010";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isSupplierGstinValid Method";
			LOGGER.debug(msg);
		}

		String reportCatagory = (rowData[79] != null) ? rowData[79].toString()
				: "";

		if ((notInEinvValidation.contains(reportCatagory)
				&& type.equalsIgnoreCase(EINV))
				|| (notInDigiGstValidation.contains(reportCatagory)
						&& type.equalsIgnoreCase(DIGIGST) && !isDocIdValid)) {
			return;
		}

		if (!isPresent(rowData[index])) {
			String errMsg = String.format("SupplierGstin(%s) cannot be empty.",
					type);
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

		String vendorGstin = rowData[index].toString();
		if (!matcher.matches() || vendorGstin.length() != 15) {
			String errMsg = String.format("Invalid SupplierGstin(%s).", type);
			ProcessingResult result = new ProcessingResult(UPLOAD, errorCode,
					errMsg);
			validationResult.add(result);
		}

	}

	private void isCustomerGstinValid(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			boolean isDocIdValid) {
		int index = type.equalsIgnoreCase(DIGIGST) ? 20 : 19;
		String errorCode = type.equalsIgnoreCase(DIGIGST) ? "ER5061" : "ER5062";

		int tableTypeIndex = type.equalsIgnoreCase(DIGIGST) ? 63 : 62;

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isCustomerGstinValid Method";
			LOGGER.debug(msg);
		}

		String reportCatagory = (rowData[79] != null) ? rowData[79].toString()
				: "";

		if ((notInEinvValidation.contains(reportCatagory)
				&& type.equalsIgnoreCase(EINV))
				|| (notInDigiGstValidation.contains(reportCatagory)
						&& type.equalsIgnoreCase(DIGIGST) && !isDocIdValid)) {
			return;
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Customer GSTIN Validation Method";
			LOGGER.debug(msg);
		}

		if (rowData[tableTypeIndex] != null && Stream.of("B2B", "CDNR")
				.anyMatch(rowData[tableTypeIndex].toString()::equals)) {

			if (!isPresent(rowData[index])) {
				String errMsg = String
						.format("CustomerGSTIN(%s) cannot be blank.", type);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}

			String panRegex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			String tanRegex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			Pattern pattern = Pattern.compile(panRegex);
			Matcher matcher = pattern.matcher(rowData[index].toString());

			Pattern pattern1 = Pattern.compile(tanRegex);
			Matcher matcher1 = pattern1.matcher(rowData[index].toString());
			
			String recpGstin = rowData[index].toString();
		/*	if (index == 14 && recpGstin.length() != 15) {
				String errMsg = String.format("Invalid CustomerGSTIN(%s).",
						type);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}*/
			
			
			if ((index == 20 || index == 19 ) && recpGstin.length() != 15) {
				String errMsg = String.format("Invalid CustomerGSTIN(%s).",
						type);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
				return;
			}
		}

	}

	private void isDocumentNumberValid(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			boolean isDocIdValid) {

		String reportCatagory = (rowData[79] != null)
				? rowData[79].toString().trim() : "";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isDocumentNumberValid Method";
			LOGGER.debug(msg);
		}

		if ((notInEinvValidation.contains(reportCatagory)
				&& type.equalsIgnoreCase(EINV))
				|| (notInDigiGstValidation.contains(reportCatagory)
						&& type.equalsIgnoreCase(DIGIGST) && !isDocIdValid)) {
			return;
		}

		int index = type.equalsIgnoreCase(DIGIGST) ? 28 : 27;
		String errorCodeBlank = type.equalsIgnoreCase(DIGIGST) ? "ER5011"
				: "ER5012";
		String errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5013"
				: "ER5014";

		if (!isPresent(rowData[index])) {

			String errMsg = String.format("DocumentNumber(%s) cannot be empty.",
					type);
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);
			return;
		}

		String docNumber = rowData[index].toString();
		if (docNumber.contains("'")) {
			String returnPeriodWithOutQuotes = docNumber.replace("'", "");
			rowData[index] = returnPeriodWithOutQuotes;
			docNumber = returnPeriodWithOutQuotes;
		}

		if (rowData[index].toString().length() > 16) {

			String errMsg = String.format("Invalid DocumentNumber(%s).", type);
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeStuct, errMsg);
			validationResult.add(result);
			return;
		}

		String docNo = rowData[index].toString();
		String regex = "^[a-zA-Z0-9/-]*$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(docNo);
		if (!matcher.matches()) {
			String errMsg = String.format(" Invalid DocumentNumber(%s).", type);
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeStuct, errMsg);
			validationResult.add(result);
		}

	}

	private void isDocumentDateValid(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			boolean isDocIdValid) {

		int index = type.equalsIgnoreCase(DIGIGST) ? 30 : 29;
		String errorCodeBlank = type.equalsIgnoreCase(DIGIGST) ? "ER5015"
				: "ER5016";
		String errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5017"
				: "ER5018";

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isDocumentDateValid Method";
			LOGGER.debug(msg);
		}

		String reportCatagory = (rowData[79] != null) ? rowData[79].toString()
				: "";

		if (!isPresent(rowData[index])) {

			if (!(notInEinvValidation.contains(reportCatagory)
					&& type.equalsIgnoreCase(EINV))
					&& !(notInDigiGstValidation.contains(reportCatagory)
							&& type.equalsIgnoreCase(DIGIGST)
							&& !isDocIdValid)) {

				String errMsg = String
						.format("DocumentDate(%s) cannot be empty.", type);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeBlank, errMsg);
				validationResult.add(result);
				return;
			}
		}

		if (isPresent(rowData[index])) {

			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(rowData[index].toString());

			if (date == null) {
				String errMsg = String.format("Invalid DocumentDate(%s).",
						type);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
			}
		}
	}

	private void isAmountValid(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			String attributeName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isAmountValid Method";
			LOGGER.debug(msg);
		}

		int index = 0;
		String errorCodeStuct = null;
		if (attributeName.equalsIgnoreCase(INVOICEASSESSABLEAMOUNT)) {

			index = type.equalsIgnoreCase(DIGIGST) ? 32 : 31;
			errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5021"
					: "ER5022";

		} else if (attributeName.equalsIgnoreCase(IGSTAMOUNT)) {

			index = type.equalsIgnoreCase(DIGIGST) ? 34 : 33;
			errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5025"
					: "ER5026";

		} else if (attributeName.equalsIgnoreCase(CGSTAMOUNT)) {

			index = type.equalsIgnoreCase(DIGIGST) ? 36 : 35;
			errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5029"
					: "ER5030";

		} else if (attributeName.equalsIgnoreCase(SGSTAMOUNT)) {

			index = type.equalsIgnoreCase(DIGIGST) ? 38 : 37;
			errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5033"
					: "ER5034";

		} else if (attributeName.equalsIgnoreCase(CESSAMOUNT)) {

			index = type.equalsIgnoreCase(DIGIGST) ? 40 : 39;
			errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5037"
					: "ER5038";

		} else if (attributeName.equalsIgnoreCase(INVOICEVALUE)) {

			index = type.equalsIgnoreCase(DIGIGST) ? 44 : 43;
			errorCodeStuct = type.equalsIgnoreCase(DIGIGST) ? "ER5041"
					: "ER5042";

		}

		if (!isPresent(rowData[index])) {
			return;
		}

		if (!isDecimal(rowData[index].toString())) {
			String errMsg = String.format("Invalid %s(%s).", attributeName,
					type);

			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeStuct, errMsg);
			validationResult.add(result);
			return;
		}

	}
	

	private boolean isValidId(Object[] rowData,
			List<ProcessingResult> validationResult, String idType,
			List<Long> activeDocIds, List<Long> inActiveDocIds) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isValidId Method";
			LOGGER.debug(msg);
		}

		int index = 0;
		String errorCodeBlank = null;
		String errorCodeStuct = null;

		String reportCatagory = (rowData[79] != null)
				? rowData[79].toString().trim() : "";

		String userResponse = String.valueOf(rowData[0]);
		List<String> expectedResponse = new ArrayList<>();

		if (reportCatagoryMap.containsKey(reportCatagory)) {
			expectedResponse = reportCatagoryMap.get(reportCatagory);
		}

		if ((reportCatagory.equals("4")
				&& idType.equalsIgnoreCase(GET_CALL_ID))) {
			return false;
		}

		if (idType.equalsIgnoreCase(RECON_ID)) {

			index = 72;
			errorCodeBlank = "ER5043";
			errorCodeStuct = "ER5044";
		} else if (idType.equalsIgnoreCase(DOC_HEADER_ID)) {

			index = 75;
			errorCodeBlank = "ER5045";
			errorCodeStuct = "ER5046";

		} else if (idType.equalsIgnoreCase(GET_CALL_ID)) {
			index = 76;
			errorCodeBlank = "ER5047";
			errorCodeStuct = "ER5048";

		}

		if (!isPresent(rowData[index])) {

			if (reportCatagory.equalsIgnoreCase("6")
					&& expectedResponse.contains(userResponse)
					&& idType.equalsIgnoreCase(DOC_HEADER_ID)) {

				String errMsg = String.format(
						"Invoice is not Available in DigiGst,Response is not allowed");
				ProcessingResult result = new ProcessingResult(UPLOAD, "ER5063",
						errMsg);
				validationResult.add(result);

			}

			if (notInDigiGstValidation.contains(reportCatagory)
					&& idType.equalsIgnoreCase(DOC_HEADER_ID)) {

				return false;
			}

			String errMsg = String.format("%s cannot be empty.", idType);
			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeBlank, errMsg);
			validationResult.add(result);

			return false;
		}

		String id = rowData[index].toString();
		if (id.contains("'")) {
			String idWithOutQuotes = id.replace("'", "");
			rowData[index] = idWithOutQuotes;
			id = idWithOutQuotes;
		}

		if (!isNumber(rowData[index].toString())) {
			String errMsg = String.format("Invalid %s .", idType);

			ProcessingResult result = new ProcessingResult(UPLOAD,
					errorCodeStuct, errMsg);
			validationResult.add(result);
			return false;
		}

		if (!activeDocIds.contains(Long.valueOf(id))
				&& idType.equalsIgnoreCase(DOC_HEADER_ID)) {

			String errMsg = "Invoice is not Active in DigiGST";
			ProcessingResult result = new ProcessingResult(UPLOAD, "ER5001",
					errMsg);
			validationResult.add(result);

			if (!inActiveDocIds.contains(Long.valueOf(id))) {

				inActiveDocIds.add(Long.valueOf(id));
			}

			return false;

		}
		return true;
	}

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

	private void validateHeaders(Long fileId, List<String> actualHeaderNames) {
		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s "
								+ "and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != noOfColumns) {
				String msg = "The number of columns in the file should be 86. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = Collections
					.unmodifiableList(expectedHeaderNamesList);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected Vendor Upload data headers names "
								+ "%s and header count %d",
						expectedHeaderNames.toString(),
						expectedHeaderNames.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = expectedHeaderNames.equals(actualHeaderNames);

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

	private void isGstr1ReturnFiled(Object[] rowData,
			List<ProcessingResult> validationResult, String type,
			boolean isDocIdValid, Map<String, Long> gstinAndEntityMap,
			Map<Long, List<EntityConfigPrmtEntity>> onBoardingmap) {
		String errorCode = type.equalsIgnoreCase(DIGIGST) ? "ER5064" : "ER5065";

		int indexTaxperiod = type.equalsIgnoreCase(DIGIGST) ? 15 : 14;
		String taxPeriod = null;
		if (rowData[indexTaxperiod] != null) {
			taxPeriod = rowData[indexTaxperiod].toString();
			if (taxPeriod.contains("'")) {
				String returnPeriodWithOutQuotes = taxPeriod.replace("'", "");
				rowData[indexTaxperiod] = returnPeriodWithOutQuotes;
				taxPeriod = returnPeriodWithOutQuotes;
			}
		}

		int indexGstin = type.equalsIgnoreCase(DIGIGST) ? 18 : 17;

		String gstin = null;
		if (rowData[indexGstin] != null) {
			gstin = rowData[indexGstin].toString();
		}
		String answer = defineOnboardingAnswer(gstin, gstinAndEntityMap,
				onBoardingmap);

		if (!Strings.isNullOrEmpty(answer)
				&& GSTConstants.A.equalsIgnoreCase(answer) && gstin != null
				&& taxPeriod != null) {
			String groupCode = TenantContext.getTenantId();
			EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder
					.getBean("EhcacheGstinTaxperiod",
							EhcacheGstinTaxperiod.class);
			GstrReturnStatusEntity entity = ehcachegstinTaxPeriod.isGstinFiled(
					gstin, taxPeriod, "GSTR1", "FILED", groupCode);

			if (entity != null) {
				String errMsg = String.format(
						"GSTR1 for this tax period is already filed(%s).",
						type);
				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCode, errMsg);
				validationResult.add(result);
			}
		}

	}

	private String defineOnboardingAnswer(String gstin,
			Map<String, Long> gstinAndEntityMap,
			Map<Long, List<EntityConfigPrmtEntity>> onBoardingmap) {
		try {
			if (gstin == null || gstinAndEntityMap == null
					|| onBoardingmap == null)
				return null;
			Long entityId = gstinAndEntityMap.get(gstin);
			String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O34.name();
			OnboardingQuestionValidationsUtil util = StaticContextHolder
					.getBean("OnboardingQuestionValidationsUtil",
							OnboardingQuestionValidationsUtil.class);

			return util.valid(onBoardingmap, paramkryId, entityId);
		} catch (Exception e) {
			LOGGER.debug(
					"error while taking Onboarding answer for O34 question from onboarding for gstin{}",
					gstin);
		}
		return null;
	}
	
	private void isPlantCodeValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isTaxPeriodValid Method";
			LOGGER.debug(msg);
		}

		String plantCode = (rowData[80] != null)
				? rowData[80].toString().trim() : "";

		String errorCodeStuct = "ER5066";
		if (isPresent(rowData[80])) {
			
			if (plantCode.length() > 100) {
				String errMsg = String.format("Invalid Plant");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}
	
	
	private void isDivisionValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isDivisionValid Method";
			LOGGER.debug(msg);
		}

		String division = (rowData[81] != null)
				? rowData[81].toString().trim() : "";

		String errorCodeStuct = "ER5067";
		if (isPresent(rowData[81])) {
			
			if (division.length() > 100) {
				String errMsg = String.format("Invalid Division");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}
	
	private void isSubDivisionValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isDivisionValid Method";
			LOGGER.debug(msg);
		}

		String subdivision = (rowData[82] != null)
				? rowData[82].toString().trim() : "";

		String errorCodeStuct = "ER5068";
		if (isPresent(rowData[82])) {
			
			if (subdivision.length() > 100) {
				String errMsg = String.format("Invalid SubDivision");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}
	
	private void isLocationValid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isLocationValid Method";
			LOGGER.debug(msg);
		}

		String location = (rowData[83] != null)
				? rowData[83].toString().trim() : "";

		String errorCodeStuct = "ER5069";
		if (isPresent(rowData[83])) {
			
			if (location.length() > 100) {
				String errMsg = String.format("Invalid location");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}
	
	
	private void isProfitCenter1Valid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isProfitCenter1Valid Method";
			LOGGER.debug(msg);
		}

		String profitCenter1 = (rowData[84] != null)
				? rowData[84].toString().trim() : "";

		String errorCodeStuct = "ER5070";
		if (isPresent(rowData[84])) {
			
			if (profitCenter1.length() > 100) {
				String errMsg = String.format("Invalid ProfitCenter1");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}
	
	private void isProfitCenter2Valid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isProfitCenter2Valid Method";
			LOGGER.debug(msg);
		}

		String profitCenter2 = (rowData[85] != null)
				? rowData[85].toString().trim() : "";

		String errorCodeStuct = "ER5071";
		if (isPresent(rowData[85])) {
			
			if (profitCenter2.length() > 100) {
				String errMsg = String.format("Invalid ProfitCenter2");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}
	
	private void isProfitCenter3Valid(Object[] rowData,
			List<ProcessingResult> validationResult) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside isProfitCenter3Valid Method";
			LOGGER.debug(msg);
		}

		String profitCenter3 = (rowData[86] != null)
				? rowData[86].toString().trim() : "";

		String errorCodeStuct = "ER5072";
		if (isPresent(rowData[86])) {
			
			if (profitCenter3.length() > 100) {
				String errMsg = String.format("Invalid ProfitCenter3");

				ProcessingResult result = new ProcessingResult(UPLOAD,
						errorCodeStuct, errMsg);
				validationResult.add(result);
				
			}
		}
		return;
	}



}