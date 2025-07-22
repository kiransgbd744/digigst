package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.text.WordUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2AReconRespUploadProcRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2PathRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconResponseUploadRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("GSTR2ASftpRespUploadServiceImpl")
public class GSTR2ASftpRespUploadServiceImpl
		implements GSTR2AAutoReconRespUploadService {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	private GSTR2ARespUploadStructuralValidation strucuralValidation;

//	@Autowired
//	@Qualifier("ReportDaoImpl")
//	private ReportDao reportDao;

	@Autowired
	private Gstr2PathRepository pathRepo;

	@Autowired
	private Gstr2ReconResponseUploadRepository repo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("Gstr2AReconRespUploadProcRepository")
	Gstr2AReconRespUploadProcRepository gstr2aProcRepo;
	
	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	private static final List<String> EXPECTED_HEADERNAMES_LIST = Arrays.asList(
			"SuggestedResponse", "UserResponse", "TaxPeriod-GSTR 3B",
			"ResponseRemarks", "MatchReason", "MismatchReason",
			"ReportCategory", "ReportType", "ERP Report Type", "TaxPeriod(2A)",
			"TaxPeriod(2B)", "TaxPeriod(PR)", "RecipientGSTIN(2A)",
			"RecipientGSTIN(PR)", "SupplierGSTIN(2A)", "SupplierGSTIN(PR)",
			"DocType(2A)", "DocType(PR)", "DocumentNumber(2A)",
			"DocumentNumber(PR)", "DocumentDate(2A)", "DocumentDate(PR)",
			"POS(2A)", "POS(PR)", "TaxableValue(2A)", "TaxableValue(PR)",
			"IGST(2A)", "IGST(PR)", "CGST(2A)", "CGST(PR)", "SGST(2A)",
			"SGST(PR)", "Cess(2A)", "Cess(PR)", "InvoiceValue(2A)",
			"InvoiceValue(PR)", "ReverseChargeFlag(2A)",
			"ReverseChargeFlag(PR)", "EligibilityIndicator", "AvailableIGST",
			"AvailableCGST", "AvailableSGST", "AvailableCESS",
			"ITC Availability (2B)", "Reason for ITC Unavailability (2B)",
			"ReturnFilingFrequency", "GSTR1-FilingStatus", "GSTR1-FilingDate",
			"GSTR1-FilingPeriod", "GSTR3B-FilingStatus", "GSTR3B-FilingDate",
			"SupplierGSTIN-Status", "SupplierGSTIN-CancellationDate(2A)",
			"VendorComplianceTrend", "SupplierCode", "BOE-ReferenceDate(2A)",
			"PortCode(2A)", "PortCode(PR)", "BillOfEntry(2A)",
			"BillOfEntry(PR)", "BillOfEntryDate(2A)", "BillOfEntryDate(PR)",
			"TableType(2A)", "SupplyType(2A)", "SupplyType(PR)", "CompanyCode",
			"PlantCode", "Division", "PurchaseOrganisation", "SourceIdentifier",
			"AccountingVoucherNumber", "AccountingVoucherDate", "VendorType",
			"HSN", "VendorRiskCategory", "VendorPaymentTerms(Days)",
			"VendorRemarks", "ApprovalStatus", "RecordStatus", "KeyDescription",
			"Recon Generated Date", "Reverse Integrated Date",
			"E-InvoiceApplicability", "IRN(2A)", "IRNDate(2A)", "QR-CodeCheck",
			"QR-CodeValidationResult", "QR-CodeMatchCount",
			"QR-CodeMismatchCount", "QR-MismatchAttributes",
			"UserDefinedField1", "UserDefinedField2", "UserDefinedField3",
			"UserDefinedField4", "UserDefinedField5", "SystemDefinedField1",
			"SystemDefinedField2", "SystemDefinedField3", "SystemDefinedField4",
			"SystemDefinedField5", "SystemDefinedField6", "SystemDefinedField7",
			"SystemDefinedField8", "SystemDefinedField9",
			"SystemDefinedField10", "RequestID", "IDPR", "ID2A");

	private static final int NO_OF_COLUMNS = EXPECTED_HEADERNAMES_LIST.size();

	private static final String DOC_KEY_JOINER = "|";

	private static final String INVALID_RESPONSE = "Invalid Response";

	private static final Map<String, String> PR_DOCTYPE_MAP = ImmutableMap
			.<String, String>builder().put("INV", "R").put("R", "R")
			.put("CR", "C").put("C", "C").put("DR", "D").put("D", "D")
			.put("DE", "R").put("SEWP", "R").put("SEWOP", "R").put("CBW", "R")
			.build();

	@Override
	public void validateResponse(Long fileId, String fileName,
			String folderName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Validating GSTR2AAutoReconResponse file";
			LOGGER.debug(msg);
		}
		try {
			
			Optional<Gstr1FileStatusEntity> updateFileStatus = fileStatusRepository.findById(fileId);
			String docId = null;
			if(updateFileStatus.isPresent())
			{
				docId = updateFileStatus.get().getDocId();
			}
			InputStream in = getFileInpStream(fileName, folderName,docId);
			
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(
					NO_OF_COLUMNS);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(in, layout, rowHandler, null);

			validateHeaders(fileName, folderName, fileId,docId);

			List<Object[]> fileList = rowHandler.getFileUploadList();
			
			for (Object[] arr: fileList) {
				for(int i=0;i<arr.length; i++){
					arr[i] = CommonUtility.singleQuoteCheck(arr[i]);
				}
				
			}

			if (CollectionUtils.isEmpty(fileList)) {
				String msg = "Failed Empty file";
				LOGGER.error(msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);

				throw new AppException(msg);
			}
			Set<String> docNo2A = new HashSet<>();
			Set<String> docNoPR = new HashSet<>();

			// convert rows into dto and collect docNum
			List<GSTR2AAutoReconRespUploadDTO> rowsAsDtoList = fileList.stream()
					.map(o -> convertRowsToDto(o, docNo2A, docNoPR))
					.collect(Collectors.toList());
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total rows in file {}", rowsAsDtoList.size());
			}
			
			boolean skipDocValidation = rowsAsDtoList.stream().anyMatch(
				    row -> "unlock".equalsIgnoreCase(row.getSuggestedResponse()) ||
				           "unlock".equalsIgnoreCase(row.getUserResponse())
				);
			Set<String> activeDocNo2A = new HashSet<>();
			Set<String> activeDocNoPR = new HashSet<>();

			// Fetch activeDocNo2A
			List<List<String>> chunks2A = Lists
					.partition(new ArrayList<String>(docNo2A), 2000);
			for (List<String> chunk : chunks2A) {
				Set<String> ducNum2A = strucuralValidation
						.getActive2ADocNumbers(chunk, skipDocValidation);
				activeDocNo2A.addAll(ducNum2A);
			}

			// Fetch activeDocNoPR
			List<List<String>> chunksPR = Lists
					.partition(new ArrayList<String>(docNoPR), 2000);
			for (List<String> chunk : chunksPR) {
				Set<String> ducNumPR = strucuralValidation
						.getActivePRDocNumbers(chunk, skipDocValidation);
				activeDocNoPR.addAll(ducNumPR);
			}

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("No. of activeDocNo2A {} and activeDocNoPR{}",
						activeDocNo2A.size(), activeDocNoPR.size());
			}

			validation(rowsAsDtoList, fileId, fileName, activeDocNo2A,
					activeDocNoPR);

		} catch (Exception ex) {
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			markFileAsFailed(fileId, ex.getMessage());
			throw new AppException(msg, ex);
		}

	}

	private void validation(List<GSTR2AAutoReconRespUploadDTO> dtoList,
			Long fileId, String fileName, Set<String> activeDocNo2A,
			Set<String> activeDocNoPR) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside GSTR2AAutoReconRespUploadServiceImpl "
					+ "Validation method ";
			LOGGER.debug(msg);
		}
		try {
			String createdBy = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			String batchId = getBatchid(fileId);
			List<ProcessingResult> validationResult = null;
			Set<String> invoiceKey2ASet = new HashSet<>();
			Set<String> invoiceKeyPRSet = new HashSet<>();
			Set<String> invoiceKeyCombSet = new HashSet<>();
			Set<String> activeInvoiceKeyCombSet = new HashSet<>();
			Set<String> activeinvoiceKey2ASet = new HashSet<>();
			Set<String> activeinvoiceKeyPRSet = new HashSet<>();

			List<Gstr2ReconResponseUploadEntity> entityList = new ArrayList<>();
			for (GSTR2AAutoReconRespUploadDTO rowData : dtoList) {

				validationResult = new ArrayList<>();
				String errorMessage = null;

				// Each row validation
				strucuralValidation.rowDataValidation(validationResult, rowData,
						activeDocNo2A, activeDocNoPR, null, false, true);

				errorMessage = validationResult.stream()
						.filter(result -> result.getDescription() != null)
						.map(ProcessingResult::getDescription)
						.collect(Collectors.joining(","));

				if (validationResult.isEmpty()) {
					errorMessage = checkForDuplicates(rowData, invoiceKey2ASet,
							invoiceKeyPRSet, invoiceKeyCombSet, errorMessage);
				}

				makeEntityList(entityList, rowData, batchId, createdBy,
						errorMessage);

			}

			/*
			 * If user is trying to respond with Unlock or Lock2 on an invoice
			 * combination that has not been uploaded, lookup on Active invoices
			 * combinations
			 */
			List<List<String>> chunksInvKeyPR = Lists
					.partition(new ArrayList<String>(invoiceKeyPRSet), 2000);

			List<List<String>> chunksInvKey2A = Lists
					.partition(new ArrayList<String>(invoiceKey2ASet), 2000);

			for (List<String> chunkPR : chunksInvKeyPR) {
				for (List<String> chunk2A : chunksInvKey2A) {

					Set<String> invKeyComb = strucuralValidation
							.getActiveInvoiceKeys(chunkPR, chunk2A);

					activeInvoiceKeyCombSet.addAll(invKeyComb);
				}
			}

			for (List<String> chunkPR : chunksInvKeyPR) {
				Set<String> activePRKeys = strucuralValidation
						.getActiveInvoicePRKeys(chunkPR);

				activeinvoiceKeyPRSet.addAll(activePRKeys);
			}

			for (List<String> chunk2A : chunksInvKey2A) {
				Set<String> active2AKeys = strucuralValidation
						.getActiveInvoice2AKeys(chunk2A);

				activeinvoiceKey2ASet.addAll(active2AKeys);
			}

			for (Gstr2ReconResponseUploadEntity e : entityList) {

				if (e.getFMResponse() != null && Stream.of("UNLOCK", "LOCK2")
						.anyMatch(e.getFMResponse()::equalsIgnoreCase)) {

					// Both the invoice keys are present
					if (isPresent(e.getInvoicekeyPR())
							&& isPresent(e.getInvoicekeyA2())) {

						String invComb = e.getInvoicekeyPR() + "-"
								+ e.getInvoicekeyA2();

						if (!activeInvoiceKeyCombSet.contains(invComb)) {
							e.setErrorCode("E");
							if (e.getErrorDesc() != null) {
								e.setErrorDesc(e.getErrorDesc()
										.concat("," + INVALID_RESPONSE));
								e.setEndDtm(LocalDateTime.now().toString());

							} else {
								e.setErrorDesc(INVALID_RESPONSE);
								e.setEndDtm(LocalDateTime.now().toString());

							}
						}
					}

					// Addition in PR
					if (isPresent(e.getInvoicekeyPR())
							&& !isPresent(e.getInvoicekeyA2())) {

						if (!activeinvoiceKeyPRSet
								.contains(e.getInvoicekeyPR())) {
							e.setErrorCode("E");
							if (e.getErrorDesc() != null) {
								e.setErrorDesc(e.getErrorDesc()
										.concat("," + INVALID_RESPONSE));
								e.setEndDtm(LocalDateTime.now().toString());

							} else {
								e.setErrorDesc(INVALID_RESPONSE);
								e.setEndDtm(LocalDateTime.now().toString());

							}
						}
					}

					// Addition in 2A
					if (isPresent(e.getInvoicekeyA2())
							&& !isPresent(e.getInvoicekeyPR())) {
						if (!activeinvoiceKey2ASet
								.contains(e.getInvoicekeyA2())) {

							e.setErrorCode("E");
							if (e.getErrorDesc() != null) {
								e.setErrorDesc(e.getErrorDesc()
										.concat("," + INVALID_RESPONSE));
								e.setEndDtm(LocalDateTime.now().toString());

							} else {
								e.setErrorDesc(INVALID_RESPONSE);
								e.setEndDtm(LocalDateTime.now().toString());

							}
						}

					}
				}
			}
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total entityList {} for batchId {}",
						entityList.size(), batchId);
			}
			repo.saveAll(entityList);
			fileStatusRepository.updateCountSummary(fileId, entityList.size(),
					0, 0);

			pathRepo.updatePath(Long.valueOf(batchId), null, null, fileName,
					fileId, null);

			// call to spc old proc USP_RECON_RESP_VALIDATE
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Getting proc list from TBL_2APR_RECON_RESP_UPLOAD for, BatchId {}",
						Long.valueOf(batchId));
			}
			String failurePocName = "USP_RECON_RESP_PSD_FAILURE";
			try {
				Map<Integer, String> procs = fetchStoredGSTR2aProcedures();
				for (Integer indx : procs.keySet()) {
					String procName = procs.get(indx);
					try {
						String status = executeGstr2aProcedure(procName, Long.valueOf(batchId));
						if (!(ReconStatusConstants.SUCCESS.equalsIgnoreCase(status) || "Success with Errors"
								.equalsIgnoreCase(status))) {
							String msg = String.format("GSTR2-APR Batch Id is '%s', Response "
									+ "from PROC %s did not " + "return success,"
									+ " Hence updating to Failed", batchId,
									procName);
							LOGGER.error(msg);
							throw new AppException(msg);
						}
					} catch (Exception ex) {
						String msg = String.format(
								"Calling GSTR2-APR failure Proc '%s' for Batch Id is '%s'",
								failurePocName, batchId);
						LOGGER.debug(msg);
						executeGstr2aProcedure(failurePocName, Long.valueOf(batchId));
						fileStatusRepository.updateFileStatus(fileId, "Failed");
						throw new AppException(ex);
					}
				}
				pathRepo.updatePath(Long.valueOf(batchId), null, null, fileName,
						fileId, null);
				fileStatusRepository.updateFileStatus(fileId, "Processed");
				
				LOGGER.debug("GSTR2-APR File is Processed for fileId: {}", fileId);
				
			} catch (Exception e) {
				String msg = String
						.format("Exception Occure while fetching Proc List for GSTR2-APR from TBL :'%s'",failurePocName);
				LOGGER.error(msg, e);
				fileStatusRepository.updateFileStatus(fileId, "Failed");
				throw new AppException(e);
			}
			

		} catch (Exception ex) {
			String msg = "Exception Occure while validating GSTR2A resp file";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	private String executeGstr2aProcedure(String procName, Long batchId)
			throws Exception {
		String status = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invking stor proc :%s , BatchId :%s", procName,
						Long.valueOf(batchId));
				LOGGER.debug(msg);
			}
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(procName);
			storedProc.registerStoredProcedureParameter("VAR_BATCHID",
					Long.class, ParameterMode.IN);
			storedProc.setParameter("VAR_BATCHID", batchId);
			long dbLoadStTime = System.currentTimeMillis();
			status = (String) storedProc.getSingleResult();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to excecute the Proc :'%s'"
								+ " with staus :'%s' from DB is '%d' millisecs.",
						procName, status, dbLoadTimeDiff);
				LOGGER.debug(msg);
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while calling proc :'%s' for Batch Id '%s'",
					procName, batchId);
			LOGGER.debug(msg);
			LOGGER.error(msg,e);
			if("USP_RECON_RESP_PSD_FAILURE".equalsIgnoreCase(procName))
			{
				failedBatAltUtility.prepareAndTriggerAlert(String.valueOf(batchId),
						"RECON_RESPONSE",String.format(" FAILURE PROCNAME : '%s' ",procName));
		
			}
			throw new AppException(msg);
		}
		return status;
	}

	private Map<Integer, String> fetchStoredGSTR2aProcedures() {
		Map<Integer, String> gstr2aProcMap = null;
		try {

			gstr2aProcMap = new TreeMap<>();
			List<Gstr2AReconRespUploadProcEntity> gstr2aProcList = gstr2aProcRepo
					.findAllActiveProc();

			gstr2aProcMap = gstr2aProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Getting alls GSTR 2APR recon response upload proc list '%s'",
						gstr2aProcMap.toString());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = String
					.format("Exception while fetching GSTR 2APR recon "
							+ "response upload proc list from DB ");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return gstr2aProcMap;
	}

	private String checkForDuplicates(GSTR2AAutoReconRespUploadDTO rowData,
			Set<String> invoiceKey2ASet, Set<String> invoiceKeyPRSet,
			Set<String> invoiceKeyCombSet, String errorMsg) {

		List<String> duplicateErrorList = new ArrayList<>();
		String invoiceKey2A = null;
		String invoiceKeyPR = null;
		
		String impgOrNonImpg = checkDataCategory(rowData);

		if (impgOrNonImpg.equalsIgnoreCase("non-IMPG")) {
			invoiceKey2A = createInvKey(rowData.getDocType2A(),
					rowData.getDocumentNumber2A(), rowData.getSupplierGstin2A(),
					rowData.getRecipientGstin2A(), rowData.getDocumentDate2A());

			// Duplicate Records 2A
			if (invoiceKey2A != null) {
				if (invoiceKey2ASet.contains(invoiceKey2A)) {
					duplicateErrorList.add("Duplicate Records 2A");
				} else {
					invoiceKey2ASet.add(invoiceKey2A);
					rowData.setInvoiceKeyA2(invoiceKey2A);
				}
			}

			invoiceKeyPR = createInvKey(rowData.getDocTypePR(),
					rowData.getDocumentNumberPR(), rowData.getSupplierGstinPR(),
					rowData.getRecipientGstinPR(), rowData.getDocumentDatePR());

			// Duplicate Records PR
			if (invoiceKeyPR != null) {
				if (invoiceKeyPRSet.contains(invoiceKeyPR)) {
					duplicateErrorList.add("Duplicate Records PR");
				} else {
					invoiceKeyPRSet.add(invoiceKeyPR);
					rowData.setInvoiceKeyPR(invoiceKeyPR);
				}

			}
		}else if (Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
				&& (!Strings.isNullOrEmpty(rowData.getTableType2A())
				&& rowData.getTableType2A()
						.equalsIgnoreCase("GETGSTR2A_IMPG_HEADER"))) {
			invoiceKey2A = createInvKeyImpg(rowData.getRecipientGstin2A(),
					rowData.getBillOfEntry2A(), DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDate2A()) != null
							? DateFormatForStructuralValidatons
									.parseObjToDate(rowData.getBillOfEntryDate2A())
									.toString()
							: rowData.getBillOfEntryDate2A());

			// Duplicate Records IMPG 2A
			if (invoiceKey2A != null) {
				if (invoiceKey2ASet.contains(invoiceKey2A)) {
					duplicateErrorList.add("Duplicate Records Impg 2A");
				} else {
					invoiceKey2ASet.add(invoiceKey2A);
					rowData.setInvoiceKeyA2(invoiceKey2A);
				}
			}

			invoiceKeyPR = createInvKeyImpg(rowData.getRecipientGstinPR(),
					rowData.getBillOfEntryPR(), DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDatePR()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDatePR())
							.toString()
					: rowData.getBillOfEntryDatePR());

			// Duplicate Records IMPG PR
			if (invoiceKeyPR != null) {
				if (invoiceKeyPRSet.contains(invoiceKeyPR)) {
					duplicateErrorList.add("Duplicate Records Impg PR");
				} else {
					invoiceKeyPRSet.add(invoiceKeyPR);
					rowData.setInvoiceKeyPR(invoiceKeyPR);
				}
			}
			
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"Inside Import SupplierGstin2A & PR null IMPG records invoiceKey2A - {}",
						invoiceKey2A);
				LOGGER.debug(
						"Inside Import SupplierGstin2A & PR null IMPG records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		} else if ((!Strings.isNullOrEmpty(rowData.getSupplierGstin2A()) 
				&& !Strings.isNullOrEmpty(rowData.getSupplierGstinPR()))
				&& (!Strings.isNullOrEmpty(rowData.getTableType2A()) && rowData.getTableType2A()
				.equalsIgnoreCase("GETGSTR2A_IMPGSEZ_HEADER"))) {
			invoiceKey2A = createInvKeyImpgSez(rowData.getRecipientGstin2A(),
					rowData.getSupplierGstin2A(), rowData.getBillOfEntry2A(),
					DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDate2A()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDate2A())
							.toString()
					: rowData.getBillOfEntryDate2A());

			// Duplicate Records IMPGEZ 2A
			if (invoiceKey2A != null) {
				if (invoiceKey2ASet.contains(invoiceKey2A)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ 2A");
				} else {
					invoiceKey2ASet.add(invoiceKey2A);
					rowData.setInvoiceKeyA2(invoiceKey2A);
				}
			}

			invoiceKeyPR = createInvKeyImpgSez(rowData.getRecipientGstinPR(),
					rowData.getSupplierGstinPR(), rowData.getBillOfEntryPR(),
					DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDatePR()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDatePR())
							.toString()
					: rowData.getBillOfEntryDatePR());

			// Duplicate Records IMPGEZ PR
			if (invoiceKeyPR != null) {
				if (invoiceKeyPRSet.contains(invoiceKeyPR)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ PR");
				} else {
					invoiceKeyPRSet.add(invoiceKeyPR);
					rowData.setInvoiceKeyPR(invoiceKeyPR);
				}
			}
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"Inside Import SupplierGstin2A & PR not null IMPGSEZ records invoiceKey2A - {}",
						invoiceKey2A);
				LOGGER.debug(
						"Inside Import SupplierGstin2A & PR not null IMPGSEZ records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		} else if(!Strings.isNullOrEmpty(rowData.getRecipientGstinPR())
				&& Strings.isNullOrEmpty(rowData.getRecipientGstin2A())
				&& !Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())
				&& rowData.getSupplyTypePR().equalsIgnoreCase("SEZG")){
			invoiceKey2A = createInvKeyImpgSez(rowData.getRecipientGstin2A(),
					rowData.getSupplierGstin2A(), rowData.getBillOfEntry2A(),
					DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDate2A()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDate2A())
							.toString()
					: rowData.getBillOfEntryDate2A());

			// Duplicate Records IMPGEZ 2A
			if (invoiceKey2A != null) {
				if (invoiceKey2ASet.contains(invoiceKey2A)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ 2A");
				} else {
					invoiceKey2ASet.add(invoiceKey2A);
					rowData.setInvoiceKeyA2(invoiceKey2A);
				}
			}

			invoiceKeyPR = createInvKeyImpgSez(rowData.getRecipientGstinPR(),
					rowData.getSupplierGstinPR(), rowData.getBillOfEntryPR(),
					DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDatePR()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDatePR())
							.toString()
					: rowData.getBillOfEntryDatePR());

			// Duplicate Records IMPGEZ PR
			if (invoiceKeyPR != null) {
				if (invoiceKeyPRSet.contains(invoiceKeyPR)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ PR");
				} else {
					invoiceKeyPRSet.add(invoiceKeyPR);
					rowData.setInvoiceKeyPR(invoiceKeyPR);
				}
			}
			
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"Inside Import RecipientGstinPR & SupplierGstinPR not null IMPGSEZ records invoiceKey2A - {}",
						invoiceKey2A);
				LOGGER.debug(
						"Inside Import RecipientGstinPR & SupplierGstinPR not null IMPGSEZ records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		} else if(!Strings.isNullOrEmpty(rowData.getRecipientGstin2A()) 
				&& Strings.isNullOrEmpty(rowData.getRecipientGstinPR())
				&& !Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2A())
				&& (!Strings.isNullOrEmpty(rowData.getTableType2A()) && rowData.getTableType2A()
						.equalsIgnoreCase("GETGSTR2A_IMPGSEZ_HEADER"))){
			invoiceKey2A = createInvKeyImpgSez(rowData.getRecipientGstin2A(),
					rowData.getSupplierGstin2A(), rowData.getBillOfEntry2A(),
					DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDate2A()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDate2A())
							.toString()
					: rowData.getBillOfEntryDate2A());

			// Duplicate Records IMPGEZ 2A
			if (invoiceKey2A != null) {
				if (invoiceKey2ASet.contains(invoiceKey2A)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ 2A");
				} else {
					invoiceKey2ASet.add(invoiceKey2A);
					rowData.setInvoiceKeyA2(invoiceKey2A);
				}
			}

			invoiceKeyPR = createInvKeyImpgSez(rowData.getRecipientGstinPR(),
					rowData.getSupplierGstinPR(), rowData.getBillOfEntryPR(),
					DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDatePR()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDatePR())
							.toString()
					: rowData.getBillOfEntryDatePR());

			// Duplicate Records IMPGEZ PR
			if (invoiceKeyPR != null) {
				if (invoiceKeyPRSet.contains(invoiceKeyPR)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ PR");
				} else {
					invoiceKeyPRSet.add(invoiceKeyPR);
					rowData.setInvoiceKeyPR(invoiceKeyPR);
				}
			}
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"Inside Import RecipientGstin2A & SupplierGstin2A not null IMPGSEZ records invoiceKey2A - {}",
						invoiceKey2A);
				LOGGER.debug(
						"Inside Import RecipientGstin2A & SupplierGstin2A not null IMPGSEZ records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		}else if(!Strings.isNullOrEmpty(rowData.getRecipientGstinPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
				&& Strings.isNullOrEmpty(rowData.getRecipientGstin2A())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstin2A())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())
				&& rowData.getSupplyTypePR().equalsIgnoreCase("IMPG")){	
			invoiceKey2A = createInvKeyImpg(rowData.getRecipientGstin2A(),
						rowData.getBillOfEntry2A(), DateFormatForStructuralValidatons
						.parseObjToDate(rowData.getBillOfEntryDate2A()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(rowData.getBillOfEntryDate2A())
								.toString()
						: rowData.getBillOfEntryDate2A());

			// Duplicate Records IMPG 2A
			if (invoiceKey2A != null) {
				if (invoiceKey2ASet.contains(invoiceKey2A)) {
					duplicateErrorList.add("Duplicate Records Impg 2A");
				} else {
					invoiceKey2ASet.add(invoiceKey2A);
					rowData.setInvoiceKeyA2(invoiceKey2A);
				}
			}
	
			invoiceKeyPR = createInvKeyImpg(rowData.getRecipientGstinPR(),
					rowData.getBillOfEntryPR(), DateFormatForStructuralValidatons
					.parseObjToDate(rowData.getBillOfEntryDatePR()) != null
					? DateFormatForStructuralValidatons
							.parseObjToDate(rowData.getBillOfEntryDatePR())
							.toString()
					: rowData.getBillOfEntryDatePR());
	
			// Duplicate Records IMPG PR
			if (invoiceKeyPR != null) {
				if (invoiceKeyPRSet.contains(invoiceKeyPR)) {
					duplicateErrorList.add("Duplicate Records Impg PR");
				} else {
					invoiceKeyPRSet.add(invoiceKeyPR);
					rowData.setInvoiceKeyPR(invoiceKeyPR);
				}
			}
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"Inside Import RecipientGstinPR not null & all null IMPG records invoiceKey2A - {}",
						invoiceKey2A);
				LOGGER.debug(
						"Inside Import RecipientGstinPR not null & all null IMPG records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		}

		String invoiceKyComb = invoiceKeyPR + "-" + invoiceKey2A;

		// Check Duplicate Invoice Combination Is Repeated
		if (invoiceKeyCombSet.contains(invoiceKyComb)) {
			duplicateErrorList.add("Duplicate Invoice Combination Is Repeated");
		} else {
			invoiceKeyCombSet.add(invoiceKyComb);
		}

		if (!duplicateErrorList.isEmpty()) {
			String duplicateErrors = String.join(",", duplicateErrorList);
			if (!Strings.isNullOrEmpty(errorMsg)) {
				errorMsg = errorMsg.concat(",").concat(duplicateErrors);
			} else {
				errorMsg = duplicateErrors;

			}
		}

		return errorMsg;
	}
	
	public String checkDataCategory(GSTR2AAutoReconRespUploadDTO rowData) {
	    boolean firstCondition = !Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
	            && !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())
	            && !Strings.isNullOrEmpty(rowData.getSupplyTypePR());

	    boolean secondCondition = !Strings.isNullOrEmpty(rowData.getBillOfEntry2A())
	            && !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2A());

	    String checkImpgorNot = null; 
	    if (firstCondition && secondCondition) {
	    	checkImpgorNot = "IMPG";
	    } else if(firstCondition) { 
	    	checkImpgorNot = "IMPG";
	    } else if(secondCondition) { 
	    	checkImpgorNot = "IMPG";
	    } else {
	    	checkImpgorNot = "non-IMPG";
	    }
		return checkImpgorNot;
	}


	private String createInvKeyImpg(String recipientGstin, String boeNum,
			String boeDate) {

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(recipientGstin) || !isPresent(boeNum)
				|| !isPresent(boeDate)) {
			return null;
		}

		docKey.append(recipientGstin).append(DOC_KEY_JOINER)
				.append(removeQuotes(boeNum)).append(DOC_KEY_JOINER)
				.append(boeDate);

		return docKey.toString();
	}

	private String createInvKeyImpgSez(String recipientGstin,
			String supplierGstin, String boeNum, String boeDate) {

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(recipientGstin) || !isPresent(supplierGstin)
				|| !isPresent(boeNum) || !isPresent(boeDate)) {
			return null;
		}

		docKey.append(recipientGstin).append(DOC_KEY_JOINER)
				.append(supplierGstin).append(DOC_KEY_JOINER)
				.append(removeQuotes(boeNum)).append(DOC_KEY_JOINER)
				.append(boeDate);

		return docKey.toString();
	}

	private InputStream getFileInpStream(String fileName, String folderName,String docId) {
		InputStream inputStream = null;
		try {

			Document document = null;
			
			if(Strings.isNullOrEmpty(docId))
			{
				 document = DocumentUtility.downloadDocument(fileName,folderName);
			}else
			{
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			
			if(document == null)
				
			{
				LOGGER.debug("Document is not available in repo");
				throw new AppException("Document is not available in repo ");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name:{}", document.getName());
			}
			inputStream = document.getContentStream().getStream();

		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in reading File GSTR2AAutoReconRespUploadService  Impl",
					e);
			throw new AppException(
					"Error occured while reading the file " + fileName, e);
		}
		return inputStream;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId, String docId) {
		try {

			InputStream fin = getFileInpStream(fileName, folderName, docId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0,
					NO_OF_COLUMNS);

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

			if (actualHeaderNames.size() != NO_OF_COLUMNS) {
				String msg = String.format(
						"The number of columns in the file should be %d. "
								+ "Aborting the file processing.",
						NO_OF_COLUMNS);
				markFileAsFailed(fileId, msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
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

	private String getBatchid(Long fileId) {

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth))
				+ (currentDay < 10 ? ("0" + currentDay)
						: String.valueOf(currentDay));

		return currentDate.concat(String.valueOf(fileId));
	}

	private GSTR2AAutoReconRespUploadDTO convertRowsToDto(Object[] arr,
			Set<String> docNo2A, Set<String> docNoPR) {

		GSTR2AAutoReconRespUploadDTO obj = new GSTR2AAutoReconRespUploadDTO();

		obj.setSuggestedResponse((arr[0] != null) ? arr[0].toString() : null);
		obj.setUserResponse((arr[1] != null) ? arr[1].toString() : null);
		obj.setTaxPeriodforGSTR3B((arr[2] != null) ? arr[2].toString() : null);
		obj.setResponseRemarks((arr[3] != null) ? arr[3].toString() : null);
		obj.setMatchReason((arr[4] != null) ? arr[4].toString() : null);
		obj.setMismatchReason((arr[5] != null) ? arr[5].toString() : null);
		obj.setReportCategory((arr[6] != null) ? arr[6].toString() : null);
		obj.setReportType((arr[7] != null) ? arr[7].toString() : null);
		obj.setErpReportType((arr[8] != null) ? arr[8].toString() : null);
		obj.setTaxPeriod2A((arr[9] != null) ? arr[9].toString() : null);
		obj.setTaxPeriod2B((arr[10] != null) ? arr[10].toString() : null);
		obj.setTaxPeriodPR((arr[11] != null) ? arr[11].toString() : null);

		// Setting recGstins to inv keys as inv keys are not there in uploaded
		// file
		obj.setInvoiceKeyUploadA2(
				(arr[12] != null) ? arr[12].toString() : null);
		obj.setInvoiceKeyUploadPR(
				(arr[13] != null) ? arr[13].toString() : null);

		obj.setRecipientGstin2A((arr[12] != null) ? arr[12].toString() : null);
		obj.setRecipientGstinPR((arr[13] != null) ? arr[13].toString() : null);

		obj.setSupplierGstin2A((arr[14] != null) ? arr[14].toString() : null);
		obj.setSupplierGstinPR((arr[15] != null) ? arr[15].toString() : null);
		obj.setDocType2A((arr[16] != null) ? arr[16].toString() : null);
		obj.setDocTypePR((arr[17] != null) ? arr[17].toString() : null);

		obj.setDocumentNumber2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setDocumentNumberPR((arr[19] != null) ? arr[19].toString() : null);
		if (!Strings.isNullOrEmpty(obj.getDocumentNumber2A())) {
			docNo2A.add(removeQuotes(obj.getDocumentNumber2A()));
		}

		if (!Strings.isNullOrEmpty(obj.getDocumentNumberPR())) {
			docNoPR.add(removeQuotes(obj.getDocumentNumberPR()));
		}

		obj.setDocumentDate2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setDocumentDatePR((arr[21] != null) ? arr[21].toString() : null);
		obj.setPos2A((arr[22] != null) ? arr[22].toString() : null);
		obj.setPosPR((arr[23] != null) ? arr[23].toString() : null);
		obj.setTaxableValue2A(
				(arr[24] != null) ? checkForNegative(arr[24].toString())
						: null);
		obj.setTaxableValuePR(
				(arr[25] != null) ? checkForNegative(arr[25].toString())
						: null);
		obj.setIgst2A((arr[26] != null) ? checkForNegative(arr[26].toString())
				: null);
		obj.setIgstPR((arr[27] != null) ? checkForNegative(arr[27].toString())
				: null);
		obj.setCgst2A((arr[28] != null) ? checkForNegative(arr[28].toString())
				: null);
		obj.setCgstPR((arr[29] != null) ? checkForNegative(arr[29].toString())
				: null);
		obj.setSgst2A((arr[30] != null) ? checkForNegative(arr[30].toString())
				: null);
		obj.setSgstPR((arr[31] != null) ? checkForNegative(arr[31].toString())
				: null);
		obj.setCess2A((arr[32] != null) ? checkForNegative(arr[32].toString())
				: null);
		obj.setCessPR((arr[33] != null) ? checkForNegative(arr[33].toString())
				: null);
		obj.setInvoiceValue2A(
				(arr[34] != null) ? checkForNegative(arr[34].toString())
						: null);
		obj.setInvoiceValuePR(
				(arr[35] != null) ? checkForNegative(arr[35].toString())
						: null);
		obj.setReverseChargeFlag2A(
				(arr[36] != null) ? arr[36].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[37] != null) ? arr[37].toString() : null);

		obj.setEligibilityIndicator(
				(arr[38] != null) ? arr[38].toString() : null);

		obj.setAvailableIGST(
				(arr[39] != null) ? checkForNegative(arr[39].toString())
						: null);
		obj.setAvailableCGST(
				(arr[40] != null) ? checkForNegative(arr[40].toString())
						: null);
		obj.setAvailableSGST(
				(arr[41] != null) ? checkForNegative(arr[41].toString())
						: null);
		obj.setAvailableCESS(
				(arr[42] != null) ? checkForNegative(arr[42].toString())
						: null);

		obj.setItcAvailability2B((arr[43] != null) ? arr[43].toString() : null);
		obj.setReasonForItcUnavailability2B(
				(arr[44] != null) ? arr[44].toString() : null);

		obj.setSupplierReturnFilingPeriodicity(
				(arr[45] != null) ? arr[45].toString() : null);
		obj.setGSTR1FilingStatus((arr[46] != null) ? arr[46].toString() : null);
		obj.setGSTR1FilingDate((arr[47] != null) ? arr[47].toString() : null);
		obj.setGSTR1FilingPeriod((arr[48] != null) ? arr[48].toString() : null);
		obj.setGSTR3BFilingStatus(
				(arr[49] != null) ? arr[49].toString() : null);

		obj.setGSTR3BFilingDate((arr[50] != null) ? arr[50].toString() : null);
		obj.setSupplierGSTINStatus(
				(arr[51] != null) ? arr[51].toString() : null);

		obj.setCancellationDate((arr[52] != null) ? arr[52].toString() : null);
		obj.setVendorComplianceTrend(
				(arr[53] != null) ? arr[53].toString() : null);
		obj.setSupplierCode((arr[54] != null) ? arr[54].toString() : null);
		obj.setBoeReferenceDate((arr[55] != null) ? arr[55].toString() : null);
		obj.setPortCode2A((arr[56] != null) ? arr[56].toString() : null);
		obj.setPortCodePR((arr[57] != null) ? arr[57].toString() : null);
		obj.setBillOfEntry2A((arr[58] != null) ? arr[58].toString() : null);
		obj.setBillOfEntryPR((arr[59] != null) ? arr[59].toString() : null);
		obj.setBillOfEntryDate2A((arr[60] != null) ? arr[60].toString() : null);
		obj.setBillOfEntryDatePR((arr[61] != null) ? arr[61].toString() : null);
		obj.setTableType2A((arr[62] != null) ? arr[62].toString() : null);
		obj.setSupplyType2A((arr[63] != null) ? arr[63].toString() : null);
		obj.setSupplyTypePR((arr[64] != null) ? arr[64].toString() : null);
		obj.setCompanyCode((arr[65] != null) ? arr[65].toString() : null);
		obj.setPlant((arr[66] != null) ? arr[66].toString() : null);
		obj.setDivision((arr[67] != null) ? arr[67].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[68] != null) ? arr[68].toString() : null);
		obj.setSourceIdentifier((arr[69] != null) ? arr[69].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[70] != null) ? arr[70].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[71] != null) ? arr[71].toString() : null);

		obj.setVendorType((arr[72] != null) ? arr[72].toString() : null);
		obj.setHSNorSAC((arr[73] != null) ? arr[73].toString() : null);
		obj.setVendorRiskCategory(
				(arr[74] != null) ? arr[74].toString() : null);
		obj.setVendorPaymentTerms_Days(
				(arr[75] != null) ? arr[75].toString() : null);
		obj.setVendorRemarks((arr[76] != null) ? arr[76].toString() : null);
		obj.setApprovalStatus((arr[77] != null) ? arr[77].toString() : null);
		obj.setRecordStatus((arr[78] != null) ? arr[78].toString() : null);
		obj.setKeyDescription((arr[79] != null) ? arr[79].toString() : null);
		obj.setReconGeneratedDate(
				(arr[80] != null) ? arr[80].toString() : null);
		obj.setReverseIntegratedDate(
				(arr[81] != null) ? arr[81].toString() : null);
		obj.setEInvoiceApplicability(
				(arr[82] != null) ? arr[82].toString() : null);
		obj.setIrn2A((arr[83] != null) ? arr[83].toString() : null);
		obj.setIrnDate2A((arr[84] != null) ? arr[84].toString() : null);
		obj.setQrCodeCheck((arr[85] != null) ? arr[85].toString() : null);
		obj.setQrCodeValidationResult(
				(arr[86] != null) ? arr[86].toString() : null);
		obj.setQrCodeMatchCount((arr[87] != null) ? arr[87].toString() : null);
		obj.setQrCodeMismatchCount(
				(arr[88] != null) ? arr[88].toString() : null);
		obj.setQrMismatchAttributes(
				(arr[89] != null) ? arr[89].toString() : null);
		obj.setUserDefinedField1((arr[90] != null) ? arr[90].toString() : null);
		obj.setUserDefinedField2((arr[91] != null) ? arr[91].toString() : null);
		obj.setUserDefinedField3((arr[92] != null) ? arr[92].toString() : null);
		obj.setUserDefinedField4((arr[93] != null) ? arr[93].toString() : null);
		obj.setUserDefinedField5((arr[94] != null) ? arr[94].toString() : null);

		obj.setSysDefinedField1((arr[95] != null) ? arr[95].toString() : null);
		obj.setSysDefinedField2((arr[96] != null) ? arr[96].toString() : null);
		obj.setSysDefinedField3((arr[97] != null) ? arr[97].toString() : null);
		obj.setSysDefinedField4((arr[98] != null) ? arr[98].toString() : null);
		obj.setSysDefinedField5((arr[99] != null) ? arr[99].toString() : null);

		obj.setSysDefinedField6(
				(arr[100] != null) ? arr[100].toString() : null);
		obj.setSysDefinedField7(
				(arr[101] != null) ? arr[101].toString() : null);
		obj.setSysDefinedField8(
				(arr[102] != null) ? arr[102].toString() : null);
		obj.setSysDefinedField9(
				(arr[103] != null) ? arr[103].toString() : null);
		obj.setSysDefinedField10(
				(arr[104] != null) ? arr[104].toString() : null);

		obj.setRequestID((arr[105] != null) ? arr[105].toString() : null);
		obj.setIDPR((arr[106] != null) ? arr[106].toString() : null);
		obj.setID2A((arr[107] != null) ? arr[107].toString() : null);

		return obj;

	}

	private List<Gstr2ReconResponseUploadEntity> makeEntityList(
			List<Gstr2ReconResponseUploadEntity> entityList,
			GSTR2AAutoReconRespUploadDTO dto, String batchID, String createdBy,
			String errorDesc) {
		Gstr2ReconResponseUploadEntity entity = new Gstr2ReconResponseUploadEntity();

		/*
		 * Set error only if record has any errors. Setting errorCode,errorDesc
		 * and endDtm says it's an error record
		 */
		/*
		 * if (!Strings.isNullOrEmpty(errorDesc)) { entity.setErrorCode("E");
		 * entity.setErrorDesc(errorDesc);
		 * entity.setEndDtm(LocalDateTime.now().toString());
		 * 
		 * }
		 * 
		 * String userResp = removeQuotes(dto.getUserResponse());
		 * 
		 * if (org.apache.commons.lang3.StringUtils.isNumeric(userResp)) {
		 * entity.setRspTaxPeriod3B(checkNullAndTrim(userResp, true, 500)); }
		 * else { // Sentence case formatting
		 * entity.setFMResponse(WordUtils.capitalizeFully(
		 * checkNullAndTrim(dto.getUserResponse(), true, 500))); }
		 * entity.setUserResponse(checkNullAndTrim(
		 * removeQuotes(dto.getUserResponse()), true, 500));
		 * 
		 * entity.setBatchID(batchID);
		 * 
		 * entity.setInvoicekeyPR( checkNullAndTrim(dto.getInvoiceKeyPR(), true,
		 * 500)); entity.setInvoicekeyA2(
		 * checkNullAndTrim(dto.getInvoiceKeyA2(), true, 500));
		 * 
		 * entity.setTaxPeriod3B( checkNullAndTrim(dto.getTaxPeriodforGSTR3B(),
		 * true, 500));
		 * 
		 * entity.setMatchReason( checkNullAndTrim(dto.getMatchReason(), true,
		 * 500));
		 * 
		 * entity.setMisMatchReason( checkNullAndTrim(dto.getMismatchReason(),
		 * true, 500)); entity.setReportCategory(
		 * checkNullAndTrim(dto.getReportCategory(), true, 500));
		 * entity.setReportType(checkNullAndTrim(dto.getReportType(), true,
		 * 500)); entity.setErpReportType(
		 * checkNullAndTrim(dto.getErpReportType(), true, 500));
		 * entity.setTaxPeriod2A(checkNullAndTrim(
		 * removeQuotes(dto.getTaxPeriod2A()), true, 500));
		 * entity.setTaxPeriod2B(checkNullAndTrim(
		 * removeQuotes(dto.getTaxPeriod2B()), true, 500));
		 * entity.setTaxPeriodPR(checkNullAndTrim(
		 * removeQuotes(dto.getTaxPeriodPR()), true, 500)); entity.setRGSTIN2A(
		 * checkNullAndTrim(dto.getRecipientGstin2A(), true, 500));
		 * entity.setRGSTINPR( checkNullAndTrim(dto.getRecipientGstinPR(), true,
		 * 500)); entity.setSGSTIN2A( checkNullAndTrim(dto.getSupplierGstin2A(),
		 * true, 500)); entity.setSGSTINPR(
		 * checkNullAndTrim(dto.getSupplierGstinPR(), true, 500));
		 * entity.setDocType2A(checkNullAndTrim(dto.getDocType2A(), true, 500));
		 * String docTypePR = checkNullAndTrim(dto.getDocTypePR(), true, 500);
		 * 
		 * entity.setDocTypePR(PR_DOCTYPE_MAP.containsKey(docTypePR) ?
		 * PR_DOCTYPE_MAP.get(docTypePR.toUpperCase()) : docTypePR);
		 * 
		 * entity.setDocumentNumber2A(checkNullAndTrim(
		 * removeQuotes(dto.getDocumentNumber2A()), true, 500));
		 * entity.setDocumentNumberPR(checkNullAndTrim(
		 * removeQuotes(dto.getDocumentNumberPR()), true, 500));
		 * entity.setDocDate2A(
		 * checkNullAndTrim(!Strings.isNullOrEmpty(dto.getDocumentDate2A()) ?
		 * DateUtil.parseObjToDate(dto.getDocumentDate2A()) .toString() : null,
		 * true, 500)); entity.setDocDatePR(
		 * checkNullAndTrim(!Strings.isNullOrEmpty(dto.getDocumentDatePR()) ?
		 * DateUtil.parseObjToDate(dto.getDocumentDatePR()) .toString() : null,
		 * true, 500));
		 * 
		 * entity.setPos2A( checkNullAndTrim(removeQuotes(dto.getPos2A()), true,
		 * 500)); entity.setPosPR(
		 * checkNullAndTrim(removeQuotes(dto.getPosPR()), true, 500));
		 * entity.setTaxable2A( checkNullAndTrim(dto.getTaxableValue2A(), true,
		 * 500)); entity.setTaxablePR( checkNullAndTrim(dto.getTaxableValuePR(),
		 * true, 500)); entity.setIGST2A(checkNullAndTrim(dto.getIgst2A(), true,
		 * 500)); entity.setIGSTPR(checkNullAndTrim(dto.getIgstPR(), true,
		 * 500)); entity.setCGST2A(checkNullAndTrim(dto.getCgst2A(), true,
		 * 500)); entity.setCGSTPR(checkNullAndTrim(dto.getCgstPR(), true,
		 * 500)); entity.setSGST2A(checkNullAndTrim(dto.getSgst2A(), true,
		 * 500)); entity.setSGSTPR(checkNullAndTrim(dto.getSgstPR(), true,
		 * 500)); entity.setCESS2A(checkNullAndTrim(dto.getCess2A(), true,
		 * 500)); entity.setCESSPR(checkNullAndTrim(dto.getCessPR(), true,
		 * 500)); entity.setInvoiceValue2A(
		 * checkNullAndTrim(dto.getInvoiceValue2A(), true, 500));
		 * entity.setInvoiceValuePR( checkNullAndTrim(dto.getInvoiceValuePR(),
		 * true, 500)); entity.setItcAvailability2B(
		 * checkNullAndTrim(dto.getItcAvailability2B(), true, 500));
		 * entity.setReasonForITCUnavailabilty2B(checkNullAndTrim(
		 * dto.getReasonForItcUnavailability2B(), true, 500));
		 * entity.setCfsFlag( checkNullAndTrim(dto.getGSTR1FilingStatus(), true,
		 * 500)); entity.setGstr1FilingDate(
		 * checkNullAndTrim(dto.getGSTR1FilingDate(), true, 500));
		 * entity.setGstr1FilingPeriod(checkNullAndTrim(
		 * removeQuotes(dto.getGSTR1FilingPeriod()), true, 500));
		 * entity.setGstr3BFilingStatus(
		 * checkNullAndTrim(dto.getGSTR3BFilingStatus(), true, 500));
		 * entity.setReverseChargeFlag2A(
		 * checkNullAndTrim(dto.getReverseChargeFlag2A(), true, 500));
		 * entity.setReversChargeReg(
		 * checkNullAndTrim(dto.getReverseChargeFlagPR(), true, 500));// PR
		 * entity.setPlantCode(checkNullAndTrim(dto.getPlant(), true, 500));
		 * entity.setDivision(checkNullAndTrim(dto.getDivision(), true, 500));
		 * entity.setPurchaseOrg(
		 * checkNullAndTrim(dto.getPurchaseOrganisation(), true, 500));
		 * entity.setTableType(checkNullAndTrim(dto.getTableType2A(), true,
		 * 500)); entity.setSupplyType2A(
		 * checkNullAndTrim(dto.getSupplyType2A(), true, 500));
		 * entity.setSupplyTypePR( checkNullAndTrim(dto.getSupplyTypePR(), true,
		 * 500)); entity.setAccountingVoucherNumber(
		 * checkNullAndTrim(dto.getAccountingVoucherNumber(), true, 500));
		 * entity.setAccountingVoucherDate(
		 * checkNullAndTrim(dto.getAccountingVoucherDate(), true, 500));
		 * entity.setApprovalStatus( checkNullAndTrim(dto.getApprovalStatus(),
		 * true, 500)); entity.setRecordStatus(
		 * checkNullAndTrim(dto.getRecordStatus(), true, 500));
		 * entity.setKeyDescription( checkNullAndTrim(dto.getKeyDescription(),
		 * true, 500)); entity.setReconGenDate(
		 * checkNullAndTrim(dto.getReconGeneratedDate(), true, 500));
		 * 
		 * // new added
		 * 
		 * entity.setReverseInteDate(
		 * checkNullAndTrim(dto.getReverseIntegratedDate(), true, 500));
		 * 
		 * entity.setIrn2A(checkNullAndTrim(dto.getIrn2A(), true, 500));
		 * entity.setIrnDate2A(checkNullAndTrim(dto.getIrnDate2A(), true, 500));
		 * entity.setUserDefinedField1(
		 * checkNullAndTrim(dto.getUserDefinedField1(), true, 500));
		 * entity.setUserDefinedField2(
		 * checkNullAndTrim(dto.getUserDefinedField2(), true, 500));
		 * entity.setUserDefinedField3(
		 * checkNullAndTrim(dto.getUserDefinedField3(), true, 500));
		 * entity.setUserDefinedField4(
		 * checkNullAndTrim(dto.getUserDefinedField4(), true, 500));
		 * entity.setUserDefinedField5(
		 * checkNullAndTrim(dto.getUserDefinedField5(), true, 500));
		 * entity.setConfigId(
		 * checkNullAndTrim(removeQuotes(dto.getRequestID()), true, 500));
		 * 
		 * entity.setIDPR(checkNullAndTrim(dto.getIDPR(), true, 500));
		 * entity.setID2A(checkNullAndTrim(dto.getID2A(), true, 500));
		 * 
		 * entity.setCreatedBy(checkNullAndTrim(createdBy, true, 500));
		 * entity.setCreateDTM(LocalDateTime.now());
		 */

		if (!Strings.isNullOrEmpty(errorDesc)) {
			entity.setErrorCode("E");
			entity.setErrorDesc(errorDesc);
			entity.setEndDtm(LocalDateTime.now().toString());

		}

		String userResp = removeQuotes(dto.getUserResponse());

		if (org.apache.commons.lang3.StringUtils.isNumeric(userResp)) {
			entity.setRspTaxPeriod3B(checkNullAndTrim(userResp, true, 500));
		} else {
			// Sentence case formatting
			entity.setFMResponse(WordUtils.capitalizeFully(
					checkNullAndTrim(dto.getUserResponse(), true, 500)));
		}
		entity.setSuggestedResponse(checkNullAndTrim(
				removeQuotes(dto.getSuggestedResponse()), true, 500));

		entity.setUserResponse(checkNullAndTrim(
				removeQuotes(dto.getUserResponse()), true, 500));

		entity.setBatchID(batchID);

		entity.setInvoicekeyPR(
				checkNullAndTrim(dto.getInvoiceKeyPR(), true, 500));
		entity.setInvoicekeyA2(
				checkNullAndTrim(dto.getInvoiceKeyA2(), true, 500));

		entity.setTaxPeriod3B(
				checkNullAndTrim(dto.getTaxPeriodforGSTR3B(), true, 500));

		entity.setFmComment(
				checkNullAndTrim(dto.getResponseRemarks(), true, 500));

		entity.setMatchReason(
				checkNullAndTrim(dto.getMatchReason(), true, 500));

		entity.setMisMatchReason(
				checkNullAndTrim(dto.getMismatchReason(), true, 500));
		entity.setReportCategory(
				checkNullAndTrim(dto.getReportCategory(), true, 500));
		entity.setReportType(checkNullAndTrim(dto.getReportType(), true, 500));
		entity.setErpReportType(
				checkNullAndTrim(dto.getErpReportType(), true, 500));
		entity.setTaxPeriod2A(checkNullAndTrim(
				removeQuotes(dto.getTaxPeriod2A()), true, 500));
		entity.setTaxPeriod2B(checkNullAndTrim(
				removeQuotes(dto.getTaxPeriod2B()), true, 500));
		entity.setTaxPeriodPR(checkNullAndTrim(
				removeQuotes(dto.getTaxPeriodPR()), true, 500));
		entity.setRGSTIN2A(
				checkNullAndTrim(dto.getRecipientGstin2A(), true, 500));
		entity.setRGSTINPR(
				checkNullAndTrim(dto.getRecipientGstinPR(), true, 500));
		entity.setSGSTIN2A(
				checkNullAndTrim(dto.getSupplierGstin2A(), true, 500));
		entity.setSGSTINPR(
				checkNullAndTrim(dto.getSupplierGstinPR(), true, 500));
		String docType2A = checkNullAndTrim(dto.getDocType2A(), true, 500);
		entity.setDocType2A(PR_DOCTYPE_MAP.containsKey(docType2A)
				? PR_DOCTYPE_MAP.get(docType2A.toUpperCase())
				: docType2A);

		String docTypePR = checkNullAndTrim(dto.getDocTypePR(), true, 500);

		entity.setDocTypePR(PR_DOCTYPE_MAP.containsKey(docTypePR)
				? PR_DOCTYPE_MAP.get(docTypePR.toUpperCase())
				: docTypePR);

		entity.setDocumentNumber2A(checkNullAndTrim(
				removeQuotes(dto.getDocumentNumber2A()), true, 500));
		entity.setDocumentNumberPR(checkNullAndTrim(
				removeQuotes(dto.getDocumentNumberPR()), true, 500));
		entity.setDocDate2A(checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getDocumentDate2A()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getDocumentDate2A())
								.toString()
						: dto.getDocumentDate2A(),
				true, 500));

		entity.setDocDatePR(checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getDocumentDatePR()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getDocumentDatePR())
								.toString()
						: dto.getDocumentDatePR(),
				true, 500));

		entity.setPos2A(
				checkNullAndTrim(removeQuotes(dto.getPos2A()), true, 500));
		entity.setPosPR(
				checkNullAndTrim(removeQuotes(dto.getPosPR()), true, 500));

		entity.setTaxable2A(
				checkNullAndTrim(dto.getTaxableValue2A(), true, 500));
		entity.setTaxablePR(
				checkNullAndTrim(dto.getTaxableValuePR(), true, 500));
		entity.setIGST2A(checkNullAndTrim(dto.getIgst2A(), true, 500));
		entity.setIGSTPR(checkNullAndTrim(dto.getIgstPR(), true, 500));
		entity.setCGST2A(checkNullAndTrim(dto.getCgst2A(), true, 500));
		entity.setCGSTPR(checkNullAndTrim(dto.getCgstPR(), true, 500));
		entity.setSGST2A(checkNullAndTrim(dto.getSgst2A(), true, 500));
		entity.setSGSTPR(checkNullAndTrim(dto.getSgstPR(), true, 500));
		entity.setCESS2A(checkNullAndTrim(dto.getCess2A(), true, 500));
		entity.setCESSPR(checkNullAndTrim(dto.getCessPR(), true, 500));
		entity.setInvoiceValue2A(
				checkNullAndTrim(dto.getInvoiceValue2A(), true, 500));
		entity.setInvoiceValuePR(
				checkNullAndTrim(dto.getInvoiceValuePR(), true, 500));
		entity.setReverseChargeFlag2A(
				checkNullAndTrim(dto.getReverseChargeFlag2A(), true, 500));
		entity.setReversChargeReg(
				checkNullAndTrim(dto.getReverseChargeFlagPR(), true, 500));// PR

		entity.setEligibilityIndicator(
				checkNullAndTrim(dto.getEligibilityIndicator(), true, 500));

		entity.setUpldAvblIGSTPR(checkNullAndTrim(
				removeQuotes(dto.getAvailableIGST()), true, 500));
		entity.setUpldAvblCGSTPR(checkNullAndTrim(
				removeQuotes(dto.getAvailableCGST()), true, 500));
		entity.setUpldAvblSGSTPR(checkNullAndTrim(
				removeQuotes(dto.getAvailableSGST()), true, 500));
		entity.setUpldAvblCessPR(checkNullAndTrim(
				removeQuotes(dto.getAvailableCESS()), true, 500));

		entity.setItcAvailability2B(
				checkNullAndTrim(dto.getItcAvailability2B(), true, 500));
		entity.setReasonForITCUnavailabilty2B(checkNullAndTrim(
				dto.getReasonForItcUnavailability2B(), true, 500));

		entity.setSupplierReturnFilingPeriodicty(checkNullAndTrim(
				dto.getSupplierReturnFilingPeriodicity(), true, 500));

		entity.setCfsFlag(
				checkNullAndTrim(dto.getGSTR1FilingStatus(), true, 500));
		entity.setGstr1FilingDate(
				checkNullAndTrim(DateFormatForStructuralValidatons
						.parseObjToDate(dto.getGSTR1FilingDate()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getGSTR1FilingDate())
								.toString()
						: dto.getGSTR1FilingDate(), true, 500));
		entity.setGstr1FilingPeriod(checkNullAndTrim(
				removeQuotes(dto.getGSTR1FilingPeriod()), true, 500));
		entity.setGstr3BFilingStatus(
				checkNullAndTrim(dto.getGSTR3BFilingStatus(), true, 500));

		entity.setGstr3bFilingDate(
				checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getGSTR3BFilingDate()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getGSTR3BFilingDate())
								.toString()
						: dto.getGSTR3BFilingDate(), true, 500));

		entity.setSupplierGstinSTatus(
				checkNullAndTrim(dto.getSupplierGSTINStatus(), true, 500));

		entity.setCancellationDate(
				checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getCancellationDate()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getCancellationDate())
								.toString()
						: dto.getCancellationDate(), true, 500));
		entity.setVendorComplianceTrend(
				checkNullAndTrim(dto.getVendorComplianceTrend(), true, 500));
		entity.setSupplierCode(
				checkNullAndTrim(dto.getSupplierCode(), true, 500));
		entity.setBoeReferenceDate(
				checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getBoeReferenceDate()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getBoeReferenceDate())
								.toString()
						: dto.getBoeReferenceDate(), true, 500));
		entity.setPortCode2A(checkNullAndTrim(dto.getPortCode2A(), true, 500));
		entity.setPortCodePR(checkNullAndTrim(dto.getPortCodePR(), true, 500));
		entity.setBillOfEntry2A(
				checkNullAndTrim(dto.getBillOfEntry2A(), true, 500));
		entity.setBillOfEntryPR(
				checkNullAndTrim(dto.getBillOfEntryPR(), true, 500));
		
		entity.setBillOfEntryDate2A(checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getBillOfEntryDate2A()) != null
				? DateFormatForStructuralValidatons
						.parseObjToDate(dto.getBillOfEntryDate2A())
						.toString()
				: dto.getBillOfEntryDate2A(), true, 500));
		
		entity.setBillOFEntryDatePR(checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getBillOfEntryDatePR()) != null
				? DateFormatForStructuralValidatons
						.parseObjToDate(dto.getBillOfEntryDatePR())
						.toString()
				: dto.getBillOfEntryDatePR(), true, 500));

		entity.setTableType(checkNullAndTrim(dto.getTableType2A(), true, 500));
		entity.setSupplyType2A(
				checkNullAndTrim(dto.getSupplyType2A(), true, 500));
		entity.setSupplyTypePR(
				checkNullAndTrim(dto.getSupplyTypePR(), true, 500));

		entity.setCompanyCode(
				checkNullAndTrim(dto.getCompanyCode(), true, 500));

		entity.setPlantCode(checkNullAndTrim(dto.getPlant(), true, 500));
		entity.setDivision(checkNullAndTrim(dto.getDivision(), true, 500));
		entity.setPurchaseOrg(
				checkNullAndTrim(dto.getPurchaseOrganisation(), true, 500));

		entity.setSourceIdentifier(
				checkNullAndTrim(dto.getSourceIdentifier(), true, 500));

		entity.setAccountingVoucherNumber(
				checkNullAndTrim(dto.getAccountingVoucherNumber(), true, 500));
		entity.setAccountingVoucherDate(
				checkNullAndTrim(DateFormatForStructuralValidatons
						.parseObjToDate(dto.getAccountingVoucherDate()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getAccountingVoucherDate())
								.toString()
						: dto.getAccountingVoucherDate(), true, 500));

		entity.setVendorType(checkNullAndTrim(dto.getVendorType(), true, 500));
		entity.setHsn(checkNullAndTrim(dto.getHSNorSAC(), true, 500));
		entity.setVendorRiskCategory(
				checkNullAndTrim(dto.getVendorRiskCategory(), true, 500));
		entity.setVendorPaymentTermsDays(
				checkNullAndTrim(dto.getVendorPaymentTerms_Days(), true, 500));
		entity.setVendorRemarks(
				checkNullAndTrim(dto.getVendorRemarks(), true, 500));

		entity.setApprovalStatus(
				checkNullAndTrim(dto.getApprovalStatus(), true, 500));
		entity.setRecordStatus(
				checkNullAndTrim(dto.getRecordStatus(), true, 500));
		entity.setKeyDescription(
				checkNullAndTrim(dto.getKeyDescription(), true, 500));
		entity.setReconGenDate(
				checkNullAndTrim(DateFormatForStructuralValidatons
						.parseObjToDate(dto.getReconGeneratedDate()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getReconGeneratedDate())
								.toString()
						: dto.getReconGeneratedDate(), true, 500));

		entity.setReverseInteDate(
				checkNullAndTrim(	DateFormatForStructuralValidatons
				.parseObjToDate(dto.getReverseIntegratedDate()) != null
						? DateFormatForStructuralValidatons
								.parseObjToDate(dto.getReverseIntegratedDate())
								.toString()
						: dto.getReverseIntegratedDate(), true, 500));

		entity.setEinvApplicability(
				checkNullAndTrim(dto.getEInvoiceApplicability(), true, 500));

		entity.setIrn2A(checkNullAndTrim(dto.getIrn2A(), true, 500));
		entity.setIrnDate2A(checkNullAndTrim(DateFormatForStructuralValidatons
				.parseObjToDate(dto.getIrnDate2A()) != null
				? DateFormatForStructuralValidatons
						.parseObjToDate(dto.getIrnDate2A())
						.toString()
				: dto.getIrnDate2A(), true, 500));

		entity.setQrCodeCheck(
				checkNullAndTrim(dto.getQrCodeCheck(), true, 500));
		entity.setQrCodeValidationResult(
				checkNullAndTrim(dto.getQrCodeValidationResult(), true, 500));
		entity.setQrCodeMatchCount(
				checkNullAndTrim(dto.getQrCodeMatchCount(), true, 500));
		entity.setQrCodeMismatchCount(
				checkNullAndTrim(dto.getQrCodeMismatchCount(), true, 500));
		entity.setQrMismatchAttributes(
				checkNullAndTrim(dto.getQrMismatchAttributes(), true, 500));

		entity.setUserDefinedField1(
				checkNullAndTrim(dto.getUserDefinedField1(), true, 500));
		entity.setUserDefinedField2(
				checkNullAndTrim(dto.getUserDefinedField2(), true, 500));
		entity.setUserDefinedField3(
				checkNullAndTrim(dto.getUserDefinedField3(), true, 500));
		entity.setUserDefinedField4(
				checkNullAndTrim(dto.getUserDefinedField4(), true, 500));
		entity.setUserDefinedField5(
				checkNullAndTrim(dto.getUserDefinedField5(), true, 500));

		entity.setSystemDefinedField1(
				checkNullAndTrim(dto.getSysDefinedField1(), true, 500));
		entity.setSystemDefinedField2(
				checkNullAndTrim(dto.getSysDefinedField2(), true, 500));
		entity.setSystemDefinedField3(
				checkNullAndTrim(dto.getSysDefinedField3(), true, 500));
		entity.setSystemDefinedField4(
				checkNullAndTrim(dto.getSysDefinedField4(), true, 500));
		entity.setSystemDefinedField5(
				checkNullAndTrim(dto.getSysDefinedField5(), true, 500));

		entity.setSystemDefinedField6(
				checkNullAndTrim(dto.getSysDefinedField6(), true, 500));
		entity.setSystemDefinedField7(
				checkNullAndTrim(dto.getSysDefinedField7(), true, 500));
		entity.setSystemDefinedField8(
				checkNullAndTrim(dto.getSysDefinedField8(), true, 500));
		entity.setSystemDefinedField9(
				checkNullAndTrim(dto.getSysDefinedField9(), true, 500));
		entity.setSystemDefinedField10(
				checkNullAndTrim(dto.getSysDefinedField10(), true, 500));

		entity.setConfigId(
				checkNullAndTrim(removeQuotes(dto.getRequestID()), true, 500));

		entity.setIDPR(checkNullAndTrim(dto.getIDPR(), true, 500));
		entity.setID2A(checkNullAndTrim(dto.getID2A(), true, 500));

		entity.setCreatedBy(checkNullAndTrim(createdBy, true, 500));
		entity.setCreateDTM(LocalDateTime.now());

		entityList.add(entity);
		return entityList;
	}

	// FY | SGSTIN | Doc-Type | Doc_Num | RGSTIN
	private String createInvKey(String docType, String docNum, String suppGstin,
			String recipientGstin, String docDate) {

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(docType) || !isPresent(docNum) || !isPresent(suppGstin)
				|| !isPresent(recipientGstin) || !isPresent(docDate)) {
			return null;
		}

		docType = PR_DOCTYPE_MAP.containsKey(docType)
				? PR_DOCTYPE_MAP.get(docType.toUpperCase())
				: docType;

		String fy = GenUtil.getFinYear(
				DateFormatForStructuralValidatons.parseObjToDate(docDate));
		fy = fy.substring(0, 4) + "-" + fy.substring(4, 6);

		docKey.append(fy).append(DOC_KEY_JOINER).append(suppGstin)
				.append(DOC_KEY_JOINER).append(docType).append(DOC_KEY_JOINER)
				.append(removeQuotes(docNum)).append(DOC_KEY_JOINER)
				.append(recipientGstin);

		return docKey.toString();
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