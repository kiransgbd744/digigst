package com.ey.advisory.app.gstr2bpr.ims.reconresponse.upload;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.text.WordUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2BIMSReconRespUploadProcEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GSTR2bprRespUploadStgRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2BIMSReconRespUploadProcRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2BPRPathRepository;
import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprAutoReconRespUploadDTO;
import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprRespUploadStgEntity;
import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprRespUploadStructuralValidation;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DateUtil;
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
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("GSTR2bprImsReconRespUploadServiceImpl")
public class GSTR2bprImsReconRespUploadServiceImpl
		implements GSTR2bprImsReconRespUploadService {

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
	private GSTR2bprRespUploadStructuralValidation strucuralValidation;

	@Autowired
	private GSTR2bprRespUploadStgRepository stgRepo;

	@Autowired
	private Gstr2BPRPathRepository pathRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	@Qualifier("Gstr2BIMSReconRespUploadProcRepository")
	Gstr2BIMSReconRespUploadProcRepository gstr2bProcRepo;

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	private Object rowData;

	private static final List<String> EXPECTED_HEADERNAMES_LIST = Arrays.asList(
			"User IMS Response", "IMS Response Remarks", "Action (GSTN)",
			"Pending Action Blocked", "IMS Get Call Date Time",
			"Action (DigiGST)", "Action (DigiGST) DateTime", "Saved to GSTN",
			"Active in IMS (GSTN)", "SuggestedResponse", "UserResponse",
			"Tax Period- GSTR 3B", "ResponseRemarks", "MatchingScoreOutof-12",
			"Match Reason", "MismatchReason", "Report Category", "ReportType",
			"PreviousReportType(2B)", "PreviousReportType(PR)", "TaxPeriod(2B)",
			"TaxPeriod(PR)", "ITC Reversed Tax Period",
			"ITC Reclaimed Tax Period", "CalendarMonth", "RecipientGSTIN(2B)",
			"RecipientGSTIN(PR)", "SupplierGSTIN(2B)", "SupplierGSTIN(PR)",
			"SupplierLegalName(2B)", "SupplierTradeName(2B)",
			"SupplierName(PR)", "DocType(2B)", "DocType(PR)",
			"DocumentNumber(2B)", "DocumentNumber(PR)", "DocumentDate(2B)",
			"DocumentDate(PR)", "GST%(2B)", "GST%(PR)", "POS(2B)", "POS(PR)",
			"TaxableValue(2B)", "TaxableValue(PR)", "IGST(2B)", "IGST(PR)",
			"CGST(2B)", "CGST(PR)", "SGST(2B)", "SGST(PR)", "Cess(2B)",
			"Cess(PR)", "TotalTax(2B)", "TotalTax(PR)", "Total Tax (Diff)",
			"Total Tax (PR>2B)", "Total Tax (2B>PR)", "InvoiceValue(2B)",
			"InvoiceValue(PR)", "ReverseChargeFlag(2B)",
			"ReverseChargeFlag(PR)", "EligibilityIndicator", "AvailableIGST",
			"AvailableCGST", "AvailableSGST", "AvailableCess",
			"ITCReversalIdentifier", "ITC Availability",
			"Reason for ITC Unavailability", "GSTR1-FilingStatus",
			"GSTR1-FilingDate", "GSTR1-FilingPeriod", "GSTR3B-FilingStatus",
			"GSTR3B-FilingDate", "SupplierGSTIN-CancellationDate",
			"GSTR2B Generation Date", "OrgInvAmendmentPeriod",
			"OrgAmendmentType", "CDN-DelinkingFlag",
			"DifferentialPercentage(2B)", "DifferentialPercentage(PR)",
			"OrgDocNumber(2B)", "OrgDocNumber(PR)", "OrgDocDate(2B)",
			"OrgDocDate(PR)", "OrgSupplierGSTIN(PR)", "OrgSupplierName(PR)",
			"CRDRPreGST(2B)", "CRDRPreGST(PR)", "BOE-ReferenceDate(2B)",
			"Bill of Entry Created Date(2B)", "PortCode(2B)", "PortCode(PR)",
			"BillOfEntry(2B)", "BillOfEntry(PR)", "BillOfEntryDate(2B)",
			"BillOfEntryDate(PR)", "BOE-Amended", "TableType(2B)",
			"SupplyType(2B)", "SupplyType(PR)", "UserID", "SourceIdentifier",
			"SourceFileName", "ProfitCentre1", "PlantCode", "Division",
			"Location", "PurchaseOrganisation", "ProfitCentre2",
			"ProfitCentre3", "ProfitCentre4", "ProfitCentre5", "ProfitCentre6",
			"ProfitCentre7", "GLAssessableValue", "GLIGST", "GLCGST", "GLSGST",
			"GLAdvaloremCess", "GLSpecificCess", "GLStateCessAdvalorem",
			"SupplierType", "SupplierCode", "SupplierAddress1",
			"SupplierAddress2", "SupplierLocation", "SupplierPincode",
			"StateApplyingCess", "CIF", "CustomDuty", "HSN", "ProductCode",
			"ProductDescription", "CategoryOfProduct", "UQC", "Quantity",
			"CessAdvaloremRate", "CessAdvaloremAmount", "CessSpecificRate",
			"CessSpecificAmount", "StateCessAdvaloremRate",
			"StateCessAdvaloremAmount", "ItemOtherCharges", "ClaimRefundFlag",
			"AutoPopulateToRefund", "AdjustementReferenceNo",
			"AdjustementReferenceDate", "CommonSupplyIndicator",
			"ITCEntitlement", "ReasonForCreditDebitNote",
			"AccountingVoucherNumber", "AccountingVoucherDate", "GLPostingDate",
			"CustomerPOReferenceNumber", "CustomerPOReferenceDate",
			"PurchaseOrderValue", "Vendor Tax Paid Percentage", "VendorType",
			"HSN", "VendorRiskCategory", "VendorPaymentTerms(Days)",
			"VendorRemarks", "Source Type of IRN", "IRN (2B)", "IRN (PR)",
			"IRNDate (2B)", "IRNDate (PR)", "UserDefinedField1",
			"UserDefinedField2", "UserDefinedField3", "UserDefinedField4",
			"UserDefinedField5", "UserDefinedField6", "UserDefinedField7",
			"UserDefinedField8", "UserDefinedField9", "UserDefinedField10",
			"UserDefinedField11", "UserDefinedField12", "UserDefinedField13",
			"UserDefinedField14", "UserDefinedField15", "UserDefinedField28",
			"EWBNumber", "EWBDate", "MatchingID", "RequestID", "IDPR", "ID2B",
			"InvoiceKeyPR", "InvoiceKey2B", "IMS UniqueID", "ReferenceIDPR",
			"ReferenceID2B");

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
			String folderName, Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Validating GSTR2bprImsReconRespUploadServiceImpl file";
			LOGGER.debug(msg);
		}
		try {

			Optional<Gstr1FileStatusEntity> updateFileStatus = fileStatusRepository
					.findById(fileId);
			String docId = null;
			if (updateFileStatus.isPresent()) {
				docId = updateFileStatus.get().getDocId();
			}

			InputStream in = getFileInpStream(fileName, folderName, docId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0,
					NO_OF_COLUMNS);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(in, layout, rowHandler, null);

			validateHeaders(fileName, folderName, fileId, rowHandler);

			List<Object[]> fileList = rowHandler.getFileUploadList();
			// fileList.remove(0);

			if (CollectionUtils.isEmpty(fileList)) {

				String msg = "Failed Empty file";
				LOGGER.error(msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);

				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("filelist post removal size -> {} and filelist ",
						fileList.size(), fileList.toString());
			}
			Set<String> docNo2B = new HashSet<>();
			Set<String> docNoPR = new HashSet<>();

			// convert rows into dto and collect docNum
			List<GSTR2bprAutoReconRespUploadDTO> rowsAsDtoList = fileList
					.stream().map(o -> convertRowsToDto(o, docNo2B, docNoPR))
					.collect(Collectors.toList());
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total rows in file {}", rowsAsDtoList.size());
			}
			Set<String> activeDocNo2B = new HashSet<>();
			Set<String> activeDocNoPR = new HashSet<>();
			
			boolean skipDocValidation = rowsAsDtoList.stream().anyMatch(
				    row -> "unlock".equalsIgnoreCase(row.getSuggestedResponse()) ||
				           "unlock".equalsIgnoreCase(row.getUserResponse())
				);

			// Fetch activeDocNo2A
			List<List<String>> chunks2B = Lists
					.partition(new ArrayList<String>(docNo2B), 2000);
			for (List<String> chunk : chunks2B) {
				Set<String> ducNum2B = strucuralValidation
						.getActive2BDocNumbers(chunk, skipDocValidation);
				activeDocNo2B.addAll(ducNum2B);
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

				LOGGER.debug("No. of activeDocNo2B {} and activeDocNoPR{}",
						activeDocNo2B.size(), activeDocNoPR.size());
			}

			validation(rowsAsDtoList, fileId, fileName, activeDocNo2B,
					activeDocNoPR, entityId);

		} catch (Exception ex) {
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			markFileAsFailed(fileId, ex.getMessage());
			throw new AppException(msg, ex);
		}

	}

	private void validation(List<GSTR2bprAutoReconRespUploadDTO> dtoList,
			Long fileId, String fileName, Set<String> activeDocNo2B,
			Set<String> activeDocNoPR, Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside GSTR2bprAutoReconRespUploadServiceImpl "
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
			Set<String> invoiceKey2BSet = new HashSet<>();
			Set<String> invoiceKeyPRSet = new HashSet<>();
			Set<String> invoiceKeyCombSet = new HashSet<>();
			Set<String> activeInvoiceKeyCombSet = new HashSet<>();
			Set<String> activeinvoiceKey2BSet = new HashSet<>();
			Set<String> activeinvoiceKeyPRSet = new HashSet<>();
			boolean computeOnBoardingPrm = getComputeOnBoardingPrm(entityId);

			List<GSTR2bprRespUploadStgEntity> entityList = new ArrayList<>();

			Set<String> imsUniqueIdList = new HashSet<>();

			for (GSTR2bprAutoReconRespUploadDTO rowData : dtoList) {
				
				boolean is2bValidationReq = false;
				boolean isimsValidationReq = false;

				
				validationResult = new ArrayList<>();
				String errorMessage = null;
				String optedOption3B = "A";

				if (!Strings.isNullOrEmpty(rowData.getImsUserResponse())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" IMS user response value {} ",
								rowData.getImsUserResponse());
					}

					isimsValidationReq = true;
				}

				if (!Strings.isNullOrEmpty(rowData.getUserResponse())) {
					is2bValidationReq = true;
				}

				if (Strings.isNullOrEmpty(rowData.getImsUserResponse())
						&& Strings.isNullOrEmpty(rowData.getUserResponse())) {
					isimsValidationReq = true;
					is2bValidationReq = true;

				}

				optedOption3B = onbrdOptionOpted(entityId);

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug(
							"rowData Before Validation: " + rowData.toString());
				}
				// Each row validation
				strucuralValidation.rowDataValidation(validationResult, rowData,
						activeDocNo2B, activeDocNoPR, computeOnBoardingPrm,
						optedOption3B, imsUniqueIdList, isimsValidationReq,
						is2bValidationReq);

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug(
							"rowData After Validation: " + rowData.toString());
				}
				errorMessage = validationResult.stream()
						.filter(result -> result.getDescription() != null)
						.map(ProcessingResult::getDescription)
						.collect(Collectors.joining(","));

				if (is2bValidationReq) {
					if (validationResult.isEmpty()) {
						errorMessage = checkForDuplicates(rowData,
								invoiceKey2BSet, invoiceKeyPRSet,
								invoiceKeyCombSet, errorMessage);
					}
				}
				makeEntityList(entityList, rowData, batchId, createdBy,
						errorMessage);
				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("rowData After makeEntityList: "
							+ rowData.toString());
				}
				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("rowData After makeEntityList: "
							+ rowData.toString());
				}

			}

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("entityList after 1st For loop: "
						+ Arrays.toString(entityList.toArray()));
			}

			/*
			 * If user is trying to respond with Unlock or Lock2 on an invoice
			 * combination that has not been uploaded, lookup on Active invoices
			 * combinations
			 */

				List<List<String>> chunksInvKeyPR = Lists.partition(
						new ArrayList<String>(invoiceKeyPRSet), 2000);

				List<List<String>> chunksInvKey2B = Lists.partition(
						new ArrayList<String>(invoiceKey2BSet), 2000);

				for (List<String> chunkPR : chunksInvKeyPR) {
					for (List<String> chunk2B : chunksInvKey2B) {

						Set<String> invKeyComb = strucuralValidation
								.getActiveInvoiceKeys(chunkPR, chunk2B);

						activeInvoiceKeyCombSet.addAll(invKeyComb);
					}
				}

				for (List<String> chunkPR : chunksInvKeyPR) {
					Set<String> activePRKeys = strucuralValidation
							.getActiveInvoicePRKeys(chunkPR);

					activeinvoiceKeyPRSet.addAll(activePRKeys);
				}

				for (List<String> chunk2B : chunksInvKey2B) {
					Set<String> active2BKeys = strucuralValidation
							.getActiveInvoice2BKeys(chunk2B);

					activeinvoiceKey2BSet.addAll(active2BKeys);
				}

				for (GSTR2bprRespUploadStgEntity e : entityList) {
			if(!Strings.isNullOrEmpty(e.getUserResponse())){
					
					if (e.getFMResponse() != null
							&& Stream.of("UNLOCK", "LOCK2").anyMatch(
									e.getFMResponse()::equalsIgnoreCase)) {

						// Both the invoice keys are present
						if (isPresent(e.getInvoicekeyPR())
								&& isPresent(e.getInvoicekey2B())) {

							String invComb = e.getInvoicekeyPR() + "-"
									+ e.getInvoicekey2B();

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
								&& !isPresent(e.getInvoicekey2B())) {

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
						if (isPresent(e.getInvoicekey2B())
								&& !isPresent(e.getInvoicekeyPR())) {
							if (!activeinvoiceKey2BSet
									.contains(e.getInvoicekey2B())) {

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
				}}
			

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("entityList after 2nd For loop: "
						+ Arrays.toString(entityList.toArray()));
			}
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total entityList {} for batchId {}",
						entityList.size(), batchId);
			}
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("entityList Before Saving: "
						+ Arrays.toString(entityList.toArray()));
			}
			stgRepo.saveAll(entityList);
			fileStatusRepository.updateCountSummary(fileId, entityList.size(),
					0, 0);

			pathRepo.updatePath(Long.valueOf(batchId), null, null, fileName,
					fileId, null);

			// call to spc old proc USP_RECON_2BPR_RESP_VALIDATE
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Getting proc list from TBL_2BPR_IMS_RECON_RESP_UPLOAD for, BatchId {}",
						Long.valueOf(batchId));
			}
			String failurePocName = "USP_RECON_2BPR_RESP_PSD_FAILURE_IMS";
			try {
				Map<Integer, String> procs = fetchStoredGSTR2bProcedures();
				for (Integer indx : procs.keySet()) {
					String procName = procs.get(indx);
					try {
						String status = executeGstr2bProcedure(procName,
								Long.valueOf(batchId));
						if (!(ReconStatusConstants.SUCCESS
								.equalsIgnoreCase(status)
								|| "Success with Errors"
										.equalsIgnoreCase(status))) {
							String msg = String.format(
									"GSTR2BPR_IMS Batch Id is '%s', Response "
											+ "from PROC %s did not "
											+ "return success,"
											+ " Hence updating to Failed",
									batchId, procName);
							LOGGER.error(msg);
							throw new AppException(msg);
						}
					} catch (Exception ex) {
						String msg = String.format(
								"Calling GSTR2BPR_IMS failure Proc '%s' for Batch Id is '%s'",
								failurePocName, batchId);
						LOGGER.debug(msg);
						executeGstr2bProcedure(failurePocName,
								Long.valueOf(batchId));
						fileStatusRepository.updateFileStatus(fileId, "Failed");
						throw new AppException(ex);
					}
				}
				pathRepo.updatePath(Long.valueOf(batchId), null, null, fileName,
						fileId, null);
				fileStatusRepository.updateFileStatus(fileId, "Processed");

				LOGGER.debug("GSTR2BPR_IMS File is Processed for fileId: {}",
						fileId);

			} catch (Exception e) {
				String msg = String.format(
						"Exception Occure while fetching Proc List for GSTR2BPR_IMS from TBL :'%s'",
						failurePocName);
				LOGGER.error(msg, e);
				fileStatusRepository.updateFileStatus(fileId, "Failed");
				throw new AppException(e);
			}

		} catch (Exception ex) {
			String msg = "Exception Occured while validating GSTR2bpr resp file";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private String executeGstr2bProcedure(String procName, Long batchId)
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
			LOGGER.error(msg, e);
			if ("USP_RECON_2BPR_RESP_PSD_FAILURE_IMS"
					.equalsIgnoreCase(procName)) {
				failedBatAltUtility.prepareAndTriggerAlert(
						String.valueOf(batchId), "RECON_RESPONSE_2BPR+IMS",
						String.format(" FAILURE PROCNAME : '%s' ", procName));

			}
			throw new AppException(msg);
		}
		return status;
	}

	private Map<Integer, String> fetchStoredGSTR2bProcedures() {
		Map<Integer, String> gstr2bProcMap = null;
		try {

			gstr2bProcMap = new TreeMap<>();
			List<Gstr2BIMSReconRespUploadProcEntity> gstr2bProcList = gstr2bProcRepo
					.findAllActiveProc();

			gstr2bProcMap = gstr2bProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Getting all GSTR 2BPR+IMS recon response upload proc list '%s'",
						gstr2bProcMap.toString());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = String
					.format("Exception while fetching GSTR 2BPR + IMS recon "
							+ "response upload proc list from DB ");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return gstr2bProcMap;
	}

	private List<GSTR2bprRespUploadStgEntity> makeEntityList(
			List<GSTR2bprRespUploadStgEntity> entityList,
			GSTR2bprAutoReconRespUploadDTO dto, String batchID,
			String createdBy, String errorDesc) {
		GSTR2bprRespUploadStgEntity entity = new GSTR2bprRespUploadStgEntity();

		/*
		 * Set error only if record has any errors. Setting errorCode,errorDesc
		 * and endDtm says it's an error record
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

		entity.setBatchID(batchID);

		entity.setInvoicekeyPR(
				checkNullAndTrim(dto.getInvoiceKeyPR(), true, 500));
		entity.setInvoicekey2B(
				checkNullAndTrim(dto.getInvoiceKeyA2(), true, 500));
		entity.setTaxPeriod2B(checkNullAndTrim(
				removeQuotes(dto.getTaxPeriod2B()), true, 500));
		entity.setTaxPeriodPR(checkNullAndTrim(
				removeQuotes(dto.getTaxPeriodPR()), true, 500));
		entity.setCalanderMonthPR(checkNullAndTrim(
				removeQuotes(dto.getCalendarMonth()), true, 500));
		entity.setRGSTIN2B(
				checkNullAndTrim(dto.getRecipientGstin2B(), true, 500));
		entity.setRGSTINPR(
				checkNullAndTrim(dto.getRecipientGstinPR(), true, 500));
		entity.setSGSTIN2B(
				checkNullAndTrim(dto.getSupplierGstin2B(), true, 500));
		entity.setSGSTINPR(
				checkNullAndTrim(dto.getSupplierGstinPR(), true, 500));

		String docType2B = checkNullAndTrim(dto.getDocType2B(), true, 500);
		entity.setDocType2B(PR_DOCTYPE_MAP.containsKey(docType2B)
				? PR_DOCTYPE_MAP.get(docType2B.toUpperCase()) : docType2B);

		String docTypePR = checkNullAndTrim(dto.getDocTypePR(), true, 500);
		entity.setDocTypePR(PR_DOCTYPE_MAP.containsKey(docTypePR)
				? PR_DOCTYPE_MAP.get(docTypePR.toUpperCase()) : docTypePR);

		entity.setDocumentNumber2B(checkNullAndTrim(
				removeQuotes(dto.getDocumentNumber2B()), true, 500));
		entity.setDocumentNumberPR(checkNullAndTrim(
				removeQuotes(dto.getDocumentNumberPR()), true, 500));
		entity.setDocDate2B(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getDocumentDate2B()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getDocumentDate2B())
												.toString()
										: dto.getDocumentDate2B(),
						true, 500));
		entity.setDocDatePR(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getDocumentDatePR()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getDocumentDatePR())
												.toString()
										: dto.getDocumentDatePR(),
						true, 500));
		entity.setCfsFlag(
				checkNullAndTrim(dto.getGSTR1FilingStatus(), true, 500));
		entity.setIGSTPR(checkNullAndTrim(dto.getIgstPR(), true, 500));
		entity.setCGSTPR(checkNullAndTrim(dto.getCgstPR(), true, 500));
		entity.setSGSTPR(checkNullAndTrim(dto.getSgstPR(), true, 500));
		entity.setCESSPR(checkNullAndTrim(dto.getCessPR(), true, 500));
		entity.setTaxablePR(
				checkNullAndTrim(dto.getTaxableValuePR(), true, 500));
		entity.setUpldAvblIGSTPR(
				checkNullAndTrim(dto.getAvailableIGST(), true, 500));
		entity.setUpldAvblCGSTPR(
				checkNullAndTrim(dto.getAvailableCGST(), true, 500));
		entity.setUpldAvblSGSTPR(
				checkNullAndTrim(dto.getAvailableSGST(), true, 500));
		entity.setUpldAvblCessPR(
				checkNullAndTrim(dto.getAvailableCESS(), true, 500));
		entity.setIGST2B(checkNullAndTrim(dto.getIgst2B(), true, 500));
		entity.setCGST2B(checkNullAndTrim(dto.getCgst2B(), true, 500));
		entity.setSGST2B(checkNullAndTrim(dto.getSgst2B(), true, 500));
		entity.setCESS2B(checkNullAndTrim(dto.getCess2B(), true, 500));
		entity.setTaxable2B(
				checkNullAndTrim(dto.getTaxableValue2B(), true, 500));
		entity.setTableType(checkNullAndTrim(dto.getTableType2B(), true, 500));
		entity.setIDPR(checkNullAndTrim(dto.getIDPR(), true, 500));
		entity.setID2B(checkNullAndTrim(dto.getID2B(), true, 500));
		entity.setCreatedBy(checkNullAndTrim(createdBy, true, 500));
		entity.setCreateDTM(LocalDateTime.now());
		entity.setSuggestedResponse(checkNullAndTrim(
				removeQuotes(dto.getSuggestedResponse()), true, 500));

		entity.setUserResponse(checkNullAndTrim(
				removeQuotes(dto.getUserResponse()), true, 500));

		entity.setFmComment(
				checkNullAndTrim(dto.getResponseRemarks(), true, 500));

		entity.setReversChargeReg(
				checkNullAndTrim(dto.getReverseChargeFlagPR(), true, 500));// PR

		entity.setTaxPeriod3B(
				checkNullAndTrim(dto.getTaxPeriodforGSTR3B(), true, 500));

		entity.setMatchingScore(
				checkNullAndTrim(dto.getMatchingScoreOutof12(), true, 500));
		entity.setMatchReason(
				checkNullAndTrim(dto.getMatchReason(), true, 500));
		entity.setMisMatchReason(
				checkNullAndTrim(dto.getMismatchReason(), true, 500));
		entity.setReportCategory(
				checkNullAndTrim(dto.getReportCategory(), true, 500));
		entity.setReportType(checkNullAndTrim(dto.getReportType(), true, 500));
		entity.setPreviousReportType2B(
				checkNullAndTrim(dto.getPreviousReportType2B(), true, 500));
		entity.setPreviousReportTypePR(
				checkNullAndTrim(dto.getPreviousReportTypePR(), true, 500));
		entity.setSupplierLegalName2B(
				checkNullAndTrim(dto.getSupplierLegalName2B(), true, 500));
		entity.setSupplierTradeName2B(
				checkNullAndTrim(dto.getSupplierTradeName2B(), true, 500));
		entity.setSupplierNamePR(
				checkNullAndTrim(dto.getSupplierNamePR(), true, 500));
		entity.setPos2B(
				checkNullAndTrim(removeQuotes(dto.getPos2B()), true, 500));
		entity.setPosPR(
				checkNullAndTrim(removeQuotes(dto.getPosPR()), true, 500));
		entity.setTotalTax2B(checkNullAndTrim(dto.getTotalTax2B(), true, 500));
		entity.setTotalTaxPR(checkNullAndTrim(dto.getTotalTaxPR(), true, 500));
		entity.setInvoiceValue2B(
				checkNullAndTrim(dto.getInvoiceValue2B(), true, 500));
		entity.setInvoiceValuePR(
				checkNullAndTrim(dto.getInvoiceValuePR(), true, 500));
		entity.setItcAvailability2B(
				checkNullAndTrim(dto.getItcAvailability2B(), true, 500));
		entity.setReasonForITCUnavailabilty2B(checkNullAndTrim(
				dto.getReasonForItcUnavailability2B(), true, 500));
		entity.setGenerationDate2B(
				checkNullAndTrim(removeQuotes(DateFormatForStructuralValidatons
						.parseObjToDate(dto.getGenerationDate2B()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getGenerationDate2B())
										.toString()
								: dto.getGenerationDate2B()),
						true, 500));
		entity.setGstr1FilingDate(
				checkNullAndTrim(
						DateFormatForStructuralValidatons.parseObjToDate(
								dto.getGSTR1FilingDate()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getGSTR1FilingDate())
												.toString()
										: dto.getGSTR1FilingDate(),
						true, 500));
		entity.setGstr1FilingPeriod(checkNullAndTrim(
				removeQuotes(dto.getGSTR1FilingPeriod()), true, 500));
		entity.setGstr3BFilingStatus(
				checkNullAndTrim(dto.getGSTR3BFilingStatus(), true, 500));
		entity.setCancellationDate(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getCancellationDate()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getCancellationDate())
										.toString()
								: dto.getCancellationDate(),
				true, 500));
		entity.setOrgInvAmendmentPeriod(
				checkNullAndTrim(dto.getOrgInvAmendmentPeriod(), true, 500));
		entity.setOrgInvAmendmentType(
				checkNullAndTrim(dto.getOrgAmendmentType(), true, 500));
		entity.setCdnDelinkingFlag(
				checkNullAndTrim(dto.getCDNDelinkingFlag(), true, 500));
		entity.setReverseChargeFlag2B(
				checkNullAndTrim(dto.getReverseChargeFlag2B(), true, 500));
		entity.setDifferentialPercentage2B(
				checkNullAndTrim(dto.getDifferentialPercentage2B(), true, 500));
		entity.setDifferentialPercentagePR(
				checkNullAndTrim(dto.getDifferentialPercentagePR(), true, 500));
		entity.setOrgDocNumber2B(
				checkNullAndTrim(dto.getOrgDocNumber2B(), true, 500));
		entity.setOrgDocNumberPR(
				checkNullAndTrim(dto.getOrgDocNumberPR(), true, 500));
		entity.setOrgDocDate2B(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getOrgDocDate2B()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getOrgDocDate2B())
												.toString()
										: dto.getOrgDocDate2B(),
						true, 500));
		entity.setOrgDocDatePR(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getOrgDocDatePR()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getOrgDocDatePR())
												.toString()
										: dto.getOrgDocDatePR(),
						true, 500));
		entity.setOrgSupplierGstinPR(
				checkNullAndTrim(dto.getOrgSupplierGstinPR(), true, 500));
		entity.setOrgSupplierNamePR(
				checkNullAndTrim(dto.getOrgSupplierNamePR(), true, 500));
		entity.setCrdrPreGst2B(
				checkNullAndTrim(dto.getCRDRPreGST2B(), true, 500));
		entity.setCrdrPreGstPR(
				checkNullAndTrim(dto.getCRDRPreGSTPR(), true, 500));
		entity.setBoeReferenceDate(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getBoeReferenceDate()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getBoeReferenceDate())
										.toString()
								: dto.getBoeReferenceDate(),
				true, 500));
		entity.setPortCode2B(checkNullAndTrim(dto.getPortCode2B(), true, 500));
		entity.setBillOfEntry2B(
				checkNullAndTrim(dto.getBillOfEntry2B(), true, 500));
		entity.setBillOfEntryDate2B(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getBillOfEntryDate2B()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getBillOfEntryDate2B())
										.toString()
								: dto.getBillOfEntryDate2B(),
				true, 500));
		entity.setBoeAmended(checkNullAndTrim(dto.getBoeAmended(), true, 500));
		entity.setUserID(checkNullAndTrim(dto.getUserID(), true, 500));
		entity.setSourceFileName(
				checkNullAndTrim(dto.getSourceFileName(), true, 500));
		entity.setSourceIdentifier(
				checkNullAndTrim(dto.getSourceIdentifier(), true, 500));

		entity.setProfitCentre1(
				checkNullAndTrim(dto.getProfitCentre1(), true, 500));
		entity.setPlantCode(checkNullAndTrim(dto.getPlant(), true, 500));
		entity.setDivision(checkNullAndTrim(dto.getDivision(), true, 500));
		entity.setLocation(checkNullAndTrim(dto.getLocation(), true, 500));
		entity.setPurchaseOrg(
				checkNullAndTrim(dto.getPurchaseOrganisation(), true, 500));
		entity.setProfitCentre2(
				checkNullAndTrim(dto.getProfitCentre2(), true, 500));
		entity.setProfitCentre3(
				checkNullAndTrim(dto.getProfitCentre3(), true, 500));
		entity.setProfitCentre4(
				checkNullAndTrim(dto.getProfitCentre4(), true, 500));
		entity.setProfitCentre5(
				checkNullAndTrim(dto.getProfitCentre5(), true, 500));
		entity.setProfitCentre6(
				checkNullAndTrim(dto.getProfitCentre6(), true, 500));
		entity.setProfitCentre7(
				checkNullAndTrim(dto.getProfitCentre7(), true, 500));
		entity.setGlAssessableValue(
				checkNullAndTrim(dto.getGLCodeAssessableValue(), true, 500));
		entity.setGlIgst(checkNullAndTrim(dto.getGLCodeIGST(), true, 500));
		entity.setGlCgst(checkNullAndTrim(dto.getGLCodeCGST(), true, 500));
		entity.setGlSgst(checkNullAndTrim(dto.getGLCodeSGST(), true, 500));
		entity.setGlAdvaloremCess(
				checkNullAndTrim(dto.getGLCodeAdvaloremCess(), true, 500));
		entity.setGlSpecificCess(
				checkNullAndTrim(dto.getGLCodeSpecificCess(), true, 500));
		entity.setGlStateCessAdvalorem(
				checkNullAndTrim(dto.getGLCodeStateCessAdvalorem(), true, 500));
		entity.setSupplyType2B(
				checkNullAndTrim(dto.getSupplyType2B(), true, 500));
		entity.setSupplyTypePR(
				checkNullAndTrim(dto.getSupplyTypePR(), true, 500));
		entity.setSupplierType(
				checkNullAndTrim(dto.getSupplierType(), true, 500));
		entity.setSupplierCode(
				checkNullAndTrim(dto.getSupplierCode(), true, 500));
		entity.setSupplierAddress1(
				checkNullAndTrim(dto.getSupplierAddress1(), true, 500));
		entity.setSupplierAddress2(
				checkNullAndTrim(dto.getSupplierAddress2(), true, 500));
		entity.setSupplierLocation(
				checkNullAndTrim(dto.getSupplierLocation(), true, 500));
		entity.setSupplierPincode(
				checkNullAndTrim(dto.getSupplierPincode(), true, 500));
		entity.setStateApplyingCess(
				checkNullAndTrim(dto.getStateApplyingCess(), true, 500));
		entity.setPortCodePR(checkNullAndTrim(dto.getPortCodePR(), true, 500));
		entity.setBillOfEntryPR(
				checkNullAndTrim(dto.getBillOfEntryPR(), true, 500));
		entity.setBillOFEntryDatePR(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getBillOfEntryDatePR()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getBillOfEntryDatePR())
										.toString()
								: dto.getBillOfEntryDatePR(),
				true, 500));
		entity.setCif(checkNullAndTrim(dto.getCIFValue(), true, 500));
		entity.setCustomDuty(checkNullAndTrim(dto.getCustomDuty(), true, 500));
		entity.setHsn(checkNullAndTrim(dto.getHSNorSAC(), true, 500));
		entity.setProductCode(
				checkNullAndTrim(dto.getProductCode(), true, 500));
		entity.setProductDesc(
				checkNullAndTrim(dto.getProductDescription(), true, 500));
		entity.setCategoryOfProduct(
				checkNullAndTrim(dto.getCategoryOfProduct(), true, 500));
		entity.setUqc(checkNullAndTrim(dto.getUqc(), true, 500));
		entity.setQnty(checkNullAndTrim(dto.getQuantity(), true, 500));
		entity.setCessAdvaloremAmt(
				checkNullAndTrim(dto.getAdvaloremCessAmount(), true, 500));
		entity.setCessSpecificAmt(
				checkNullAndTrim(dto.getSpecificCessAmount(), true, 500));
		entity.setStateCessAdvaloremAmt(
				checkNullAndTrim(dto.getStateCessAdvaloremAmount(), true, 500));
		entity.setItemOtherCharges(
				checkNullAndTrim(dto.getItemOtherCharges(), true, 500));
		entity.setClaimRefundFlag(
				checkNullAndTrim(dto.getClaimRefundFlag(), true, 500));
		entity.setAutoPopulateToRefund(
				checkNullAndTrim(dto.getAutoPopulateToRefund(), true, 500));
		entity.setAdjustmentRefNo(
				checkNullAndTrim(dto.getAdjustementReferenceNo(), true, 500));
		entity.setAdjustmentRefDate(checkNullAndTrim(
				DateFormatForStructuralValidatons.parseObjToDate(
						dto.getAdjustementReferenceDate()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getAdjustementReferenceDate())
										.toString()
								: dto.getAdjustementReferenceDate(),
				true, 500));
		entity.setEligibilityIndicator(
				checkNullAndTrim(dto.getEligibilityIndicator(), true, 500));
		entity.setCommonSupplyIndicator(
				checkNullAndTrim(dto.getCommonSupplyIndicator(), true, 500));
		entity.setItcEntitlement(
				checkNullAndTrim(dto.getITCEntitlement(), true, 500));
		entity.setItcReversalIdentifier(
				checkNullAndTrim(dto.getITCReversalIdentifier(), true, 500));
		entity.setReasonForCrDrNote(
				checkNullAndTrim(dto.getReasonForCreditDebitNote(), true, 500));
		entity.setAccountingVoucherNumber(
				checkNullAndTrim(dto.getAccountingVoucherNumber(), true, 500));
		entity.setAccountingVoucherDate(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getAccountingVoucherDate()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getAccountingVoucherDate())
										.toString()
								: dto.getAccountingVoucherDate(),
				true, 500));
		entity.setGlPostingDate(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getGlPostingDate()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getGlPostingDate())
												.toString()
										: dto.getGlPostingDate(),
						true, 500));
		entity.setCustomerPORefNumber(
				checkNullAndTrim(dto.getCustomerPORefNumber(), true, 500));
		entity.setCustomerPORefDate(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getCustomerPORefDate()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getCustomerPORefDate())
										.toString()
								: dto.getCustomerPORefDate(),
				true, 500));
		entity.setPurchaseOrderValue(
				checkNullAndTrim(dto.getPurchaseOrderValue(), true, 500));
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
		entity.setUserDefinedField6(
				checkNullAndTrim(dto.getUserDefinedField6(), true, 500));
		entity.setUserDefinedField7(
				checkNullAndTrim(dto.getUserDefinedField7(), true, 500));
		entity.setUserDefinedField8(
				checkNullAndTrim(dto.getUserDefinedField8(), true, 500));
		entity.setUserDefinedField9(
				checkNullAndTrim(dto.getUserDefinedField9(), true, 500));
		entity.setUserDefinedField10(
				checkNullAndTrim(dto.getUserDefinedField10(), true, 500));
		entity.setUserDefinedField11(
				checkNullAndTrim(dto.getUserDefinedField11(), true, 500));
		entity.setUserDefinedField12(
				checkNullAndTrim(dto.getUserDefinedField12(), true, 500));
		entity.setUserDefinedField13(
				checkNullAndTrim(dto.getUserDefinedField13(), true, 500));
		entity.setUserDefinedField14(
				checkNullAndTrim(dto.getUserDefinedField14(), true, 500));
		entity.setUserDefinedField15(
				checkNullAndTrim(dto.getUserDefinedField15(), true, 500));
		entity.setUserDefinedField28(
				checkNullAndTrim(dto.getUserDefinedField28(), true, 500));
		entity.setEwbNumber(
				checkNullAndTrim(dto.getEWayBillNumber(), true, 500));
		entity.setEwbDate(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getEWayBillDate()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getEWayBillDate())
												.toString()
										: dto.getEWayBillDate(),
						true, 500));
		entity.setMatchingId(checkNullAndTrim(dto.getMatchingID(), true, 500));
		entity.setRefIDPR(checkNullAndTrim(dto.getReferenceIDPR(), true, 500));
		entity.setRefID2B(checkNullAndTrim(dto.getReferenceID2B(), true, 500));
		entity.setConfigId(
				checkNullAndTrim(removeQuotes(dto.getRequestID()), true, 500));

		entity.setIrn2B(checkNullAndTrim(dto.getIrn2B(), true, 500));
		entity.setIrnPR(checkNullAndTrim(dto.getIrnPR(), true, 500));
		entity.setIrnDate2B(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getIrnDate2B()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getIrnDate2B())
												.toString()
										: dto.getIrnDate2B(),
						true, 500));
		entity.setIrnDatePR(
				checkNullAndTrim(
						DateFormatForStructuralValidatons
								.parseObjToDate(dto.getIrnDatePR()) != null
										? DateFormatForStructuralValidatons
												.parseObjToDate(
														dto.getIrnDatePR())
												.toString()
										: dto.getIrnDatePR(),
						true, 500));

		entity.setItcReversedTaxPeriod(checkNullAndTrim(
				removeQuotes(dto.getItcReversedTaxPeriod()), true, 500));
		entity.setItcReclaimedTaxPeriod(checkNullAndTrim(
				removeQuotes(dto.getItcReclaimedTaxPeriod()), true, 500));
		entity.setGstr3bFilingDate(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getGstr3bFilingDate()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getGstr3bFilingDate())
										.toString()
								: dto.getGstr3bFilingDate(),
				true, 500));
		entity.setSupplierGSTINStatus(
				checkNullAndTrim(dto.getSupplierGSTINStatus(), true, 500));
		entity.setBillOfEntryCreatedDate2B(checkNullAndTrim(
				DateFormatForStructuralValidatons.parseObjToDate(
						dto.getBillOfEntryCreatedDate2B()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getBillOfEntryCreatedDate2B())
										.toString()
								: dto.getBillOfEntryCreatedDate2B(),
				true, 500));
		entity.setVendorTaxPaidVariance(
				checkNullAndTrim(dto.getVendorTaxPaidVariance(), true, 500));
		entity.setVendorType(checkNullAndTrim(dto.getVendorType(), true, 500));
		entity.setHsnVendor(checkNullAndTrim(dto.getHsnVendor(), true, 500));
		entity.setVendorRiskCategory(
				checkNullAndTrim(dto.getVendorRiskCategory(), true, 500));
		entity.setVendorPaymentTermsDays(
				checkNullAndTrim(dto.getVendorPaymentTermsDays(), true, 500));
		entity.setVendorRemarks(
				checkNullAndTrim(dto.getVendorRemarks(), true, 500));
		entity.setSystemDefinedField1(
				checkNullAndTrim(dto.getSystemDefinedField1(), true, 500));
		entity.setSystemDefinedField2(
				checkNullAndTrim(dto.getSystemDefinedField2(), true, 500));
		entity.setSystemDefinedField3(
				checkNullAndTrim(dto.getSystemDefinedField3(), true, 500));
		entity.setSystemDefinedField4(
				checkNullAndTrim(dto.getSystemDefinedField4(), true, 500));
		entity.setSystemDefinedField5(
				checkNullAndTrim(dto.getSystemDefinedField5(), true, 500));
		if(!Strings.isNullOrEmpty(dto.getImsUserResponse())){
			entity.setImsUserResponse(dto.getImsUserResponse().toUpperCase());
		}
		entity.setImsResponseRemarks(
				checkNullAndTrim(dto.getImsResponseRemark(), true, 500));
		entity.setImsUniqueId(
				checkNullAndTrim(dto.getImsUniqueId(), true, 500));
		entity.setActionGstn(checkNullAndTrim(dto.getActionGstn(), true, 500));
		entity.setIsPendingActionBlocked(
				checkNullAndTrim(dto.getPendingActionbloacked(), true, 500));
		entity.setGetCallTime(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getImsGetCallDateTime()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getImsGetCallDateTime())
										.toString()
								: dto.getImsGetCallDateTime(),
				true, 500));

		entity.setActionDigigst(
				checkNullAndTrim(dto.getActionDigi(), true, 500));

		entity.setDigigstDateTime(checkNullAndTrim(
				DateFormatForStructuralValidatons
						.parseObjToDate(dto.getActionDigiDateTime()) != null
								? DateFormatForStructuralValidatons
										.parseObjToDate(
												dto.getActionDigiDateTime())
										.toString()
								: dto.getActionDigiDateTime(),
				true, 500));

		entity.setIsSaveToGstn(
				checkNullAndTrim(dto.getImsSaveToGstn(), true, 500));
		entity.setAvalInIms(checkNullAndTrim(dto.getActiveInIms(), true, 500));
		entity.setTotalTaxDiff(
				checkNullAndTrim(dto.getImsTotalTaxDiff(), true, 500));
		entity.setPrTaxGreaterThan2B(
				checkNullAndTrim(dto.getImsTotalTaxPRGreater2B(), true, 500));
		entity.setTax2BGreaterThanPr(
				checkNullAndTrim(dto.getImsTotalTax2BGreaterPR(), true, 500));
		entity.setInvoicekey2B(
				checkNullAndTrim(dto.getInvoiceKeyUpload2B(), true, 500));
		entity.setInvoicekeyPR(
				checkNullAndTrim(dto.getInvoiceKeyUploadPR(), true, 500));
		
		
		entityList.add(entity);
		return entityList;
	}

	private String checkForDuplicates(GSTR2bprAutoReconRespUploadDTO rowData,
			Set<String> invoiceKey2BSet, Set<String> invoiceKeyPRSet,
			Set<String> invoiceKeyCombSet, String errorMsg) {

		List<String> duplicateErrorList = new ArrayList<>();
		String invoiceKey2B = null;
		String invoiceKeyPR = null;

		String impgOrNonImpg = checkDataCategory(rowData);

		if (impgOrNonImpg.equalsIgnoreCase("non-IMPG")) {
			invoiceKey2B = createInvKey(rowData.getDocType2B(),
					rowData.getDocumentNumber2B(), rowData.getSupplierGstin2B(),
					rowData.getRecipientGstin2B(), rowData.getDocumentDate2B());

			// Duplicate Records 2B
			if (invoiceKey2B != null) {
				if (invoiceKey2BSet.contains(invoiceKey2B)) {
					duplicateErrorList.add("Duplicate Records 2B");
				} else {
					invoiceKey2BSet.add(invoiceKey2B);
					rowData.setInvoiceKeyA2(invoiceKey2B);
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

		} else if (Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
				&& (Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
						|| "URP".equalsIgnoreCase(rowData.getSupplierGstinPR()))
				&& (!Strings.isNullOrEmpty(rowData.getTableType2B())
						&& rowData.getTableType2B()
								.equalsIgnoreCase("GETGSTR2B_IMPG_HEADER"))) {
			invoiceKey2B = createInvKeyImpg(rowData.getRecipientGstin2B(),
					rowData.getBillOfEntry2B(), rowData.getBillOfEntryDate2B());

			// Duplicate Records IMPG 2B
			if (invoiceKey2B != null) {
				if (invoiceKey2BSet.contains(invoiceKey2B)) {
					duplicateErrorList.add("Duplicate Records Impg 2B");
				} else {
					invoiceKey2BSet.add(invoiceKey2B);
					rowData.setInvoiceKeyA2(invoiceKey2B);
				}
			}

			invoiceKeyPR = createInvKeyImpg(rowData.getRecipientGstinPR(),
					rowData.getBillOfEntryPR(), rowData.getBillOfEntryDatePR());

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
						"Inside Import SupplierGstin2A & PR null IMPG records invoiceKey2B - {}",
						invoiceKey2B);
				LOGGER.debug(
						"Inside Import SupplierGstin2A & PR null IMPG records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		} else if ((!Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
				&& !Strings.isNullOrEmpty(rowData.getSupplierGstinPR()))
				&& (!Strings.isNullOrEmpty(rowData.getTableType2B())
						&& rowData.getTableType2B().equalsIgnoreCase(
								"GETGSTR2B_IMPGSEZ_HEADER"))) {
			invoiceKey2B = createInvKeyImpgSez(rowData.getRecipientGstin2B(),
					rowData.getSupplierGstin2B(), rowData.getBillOfEntry2B(),
					rowData.getBillOfEntryDate2B());

			// Duplicate Records IMPGEZ 2B
			if (invoiceKey2B != null) {
				if (invoiceKey2BSet.contains(invoiceKey2B)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ 2B");
				} else {
					invoiceKey2BSet.add(invoiceKey2B);
					rowData.setInvoiceKeyA2(invoiceKey2B);
				}
			}

			invoiceKeyPR = createInvKeyImpgSez(rowData.getRecipientGstinPR(),
					rowData.getSupplierGstinPR(), rowData.getBillOfEntryPR(),
					rowData.getBillOfEntryDatePR());

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
						"Inside Import SupplierGstin2A & PR not null IMPGSEZ records invoiceKey2B - {}",
						invoiceKey2B);
				LOGGER.debug(
						"Inside Import SupplierGstin2A & PR not null IMPGSEZ records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		} else if (!Strings.isNullOrEmpty(rowData.getRecipientGstinPR())
				&& Strings.isNullOrEmpty(rowData.getRecipientGstin2B())
				&& !Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())
				&& rowData.getSupplyTypePR().equalsIgnoreCase("SEZG")) {
			invoiceKey2B = createInvKeyImpgSez(rowData.getRecipientGstin2B(),
					rowData.getSupplierGstin2B(), rowData.getBillOfEntry2B(),
					rowData.getBillOfEntryDate2B());

			// Duplicate Records IMPGEZ 2B
			if (invoiceKey2B != null) {
				if (invoiceKey2BSet.contains(invoiceKey2B)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ 2B");
				} else {
					invoiceKey2BSet.add(invoiceKey2B);
					rowData.setInvoiceKeyA2(invoiceKey2B);
				}
			}

			invoiceKeyPR = createInvKeyImpgSez(rowData.getRecipientGstinPR(),
					rowData.getSupplierGstinPR(), rowData.getBillOfEntryPR(),
					rowData.getBillOfEntryDatePR());

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
						"Inside Import RecipientGstinPR & SupplierGstinPR not null IMPGSEZ records invoiceKey2B - {}",
						invoiceKey2B);
				LOGGER.debug(
						"Inside Import RecipientGstinPR & SupplierGstinPR not null IMPGSEZ records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		} else if (!Strings.isNullOrEmpty(rowData.getRecipientGstin2B())
				&& Strings.isNullOrEmpty(rowData.getRecipientGstinPR())
				&& !Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntry2B())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2B())
				&& (!Strings.isNullOrEmpty(rowData.getTableType2B())
						&& rowData.getTableType2B().equalsIgnoreCase(
								"GETGSTR2B_IMPGSEZ_HEADER"))) {
			invoiceKey2B = createInvKeyImpgSez(rowData.getRecipientGstin2B(),
					rowData.getSupplierGstin2B(), rowData.getBillOfEntry2B(),
					rowData.getBillOfEntryDate2B());

			// Duplicate Records IMPGEZ 2B
			if (invoiceKey2B != null) {
				if (invoiceKey2BSet.contains(invoiceKey2B)) {
					duplicateErrorList.add("Duplicate Records Impg SEZ 2B");
				} else {
					invoiceKey2BSet.add(invoiceKey2B);
					rowData.setInvoiceKeyA2(invoiceKey2B);
				}
			}

			invoiceKeyPR = createInvKeyImpgSez(rowData.getRecipientGstinPR(),
					rowData.getSupplierGstinPR(), rowData.getBillOfEntryPR(),
					rowData.getBillOfEntryDatePR());

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
						"Inside Import RecipientGstin2B & SupplierGstin2B not null IMPGSEZ records invoiceKey2B - {}",
						invoiceKey2B);
				LOGGER.debug(
						"Inside Import RecipientGstin2B & SupplierGstin2B not null IMPGSEZ records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		} else if (!Strings.isNullOrEmpty(rowData.getRecipientGstinPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryPR())
				&& Strings.isNullOrEmpty(rowData.getRecipientGstin2B())
				&& Strings.isNullOrEmpty(rowData.getSupplierGstin2B())
				&& (Strings.isNullOrEmpty(rowData.getSupplierGstinPR())
						|| "URP".equalsIgnoreCase(rowData.getSupplierGstinPR()))
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())
				&& rowData.getSupplyTypePR().equalsIgnoreCase("IMPG")) {
			invoiceKey2B = createInvKeyImpg(rowData.getRecipientGstin2B(),
					rowData.getBillOfEntry2B(), rowData.getBillOfEntryDate2B());

			// Duplicate Records IMPG 2A
			if (invoiceKey2B != null) {
				if (invoiceKey2BSet.contains(invoiceKey2B)) {
					duplicateErrorList.add("Duplicate Records Impg 2B");
				} else {
					invoiceKey2BSet.add(invoiceKey2B);
					rowData.setInvoiceKeyA2(invoiceKey2B);
				}
			}

			invoiceKeyPR = createInvKeyImpg(rowData.getRecipientGstinPR(),
					rowData.getBillOfEntryPR(), rowData.getBillOfEntryDatePR());

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
						"Inside Import RecipientGstinPR not null & all null IMPG records invoiceKey2B - {}",
						invoiceKey2B);
				LOGGER.debug(
						"Inside Import RecipientGstinPR not null & all null IMPG records invoiceKeyPR - {}",
						invoiceKeyPR);
			}
		}

		String invoiceKyComb = invoiceKeyPR + "-" + invoiceKey2B;

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

	public String checkDataCategory(GSTR2bprAutoReconRespUploadDTO rowData) {
		boolean firstCondition = !Strings
				.isNullOrEmpty(rowData.getBillOfEntryPR())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDatePR())
				&& !Strings.isNullOrEmpty(rowData.getSupplyTypePR())
				&& ((rowData.getSupplyTypePR().equalsIgnoreCase("IMPG")
						|| rowData.getSupplyTypePR().equalsIgnoreCase("SEZG")));

		boolean secondCondition = !Strings
				.isNullOrEmpty(rowData.getBillOfEntry2B())
				&& !Strings.isNullOrEmpty(rowData.getBillOfEntryDate2B());

		String checkImpgorNot = null;
		if (firstCondition && secondCondition) {
			checkImpgorNot = "IMPG";
		} else if (firstCondition) {
			checkImpgorNot = "IMPG";
		} else if (secondCondition) {
			checkImpgorNot = "IMPG";
		} else {
			checkImpgorNot = "non-IMPG";
		}
		return checkImpgorNot;
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
					"Exception occured in reading File GSTR2bprReconRespUploadServiceImpl",
					e);
			throw new AppException(
					"Error occured while reading the file " + fileName, e);
		}
		return inputStream;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId, FileUploadDocRowHandler<?> rowHandler) {
		try {

			/*
			 * InputStream fin = getFileInpStream(fileName, folderName);
			 * TabularDataSourceTraverser traverser = traverserFactory
			 * .getTraverser(fileName); TabularDataLayout layout = new
			 * DummyTabularDataLayout(0, 0, 0, NO_OF_COLUMNS);
			 * 
			 * @SuppressWarnings("rawtypes") FileUploadDocRowHandler rowHandler
			 * = new FileUploadDocRowHandler<>();
			 * traverser.traverseHeaderOnly(fin, layout, rowHandler, null);
			 */
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

	private GSTR2bprAutoReconRespUploadDTO convertRowsToDto(Object[] arr,
			Set<String> docNo2B, Set<String> docNoPR) {

		GSTR2bprAutoReconRespUploadDTO obj = new GSTR2bprAutoReconRespUploadDTO();

		obj.setImsUserResponse((arr[0] != null) ? arr[0].toString() : null);

		obj.setImsResponseRemark((arr[1] != null) ? arr[1].toString() : null);

		obj.setActionGstn((arr[2] != null) ? arr[2].toString() : null);

		obj.setPendingActionbloacked(
				(arr[3] != null) ? arr[3].toString() : null);

		obj.setImsGetCallDateTime((arr[4] != null) ? arr[4].toString() : null);

		obj.setActionDigi((arr[5] != null) ? arr[5].toString() : null);

		obj.setActionDigiDateTime((arr[6] != null) ? arr[6].toString() : null);

		obj.setImsSaveToGstn((arr[7] != null) ? arr[7].toString() : null);

		obj.setActiveInIms((arr[8] != null) ? arr[8].toString() : null);
		obj.setSuggestedResponse((arr[9] != null) ? arr[9].toString() : null);
		obj.setUserResponse((arr[10] != null) ? arr[10].toString() : null);
		obj.setTaxPeriodforGSTR3B(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setResponseRemarks((arr[12] != null) ? arr[12].toString() : null);
		obj.setMatchingScoreOutof12(
				(arr[13] != null) ? arr[13].toString() : null);
		obj.setMatchReason((arr[14] != null) ? arr[14].toString() : null);
		obj.setMismatchReason((arr[15] != null) ? arr[15].toString() : null);
		obj.setReportCategory((arr[16] != null) ? arr[16].toString() : null);
		obj.setReportType((arr[17] != null) ? arr[17].toString() : null);
		obj.setPreviousReportType2B(
				(arr[18] != null) ? arr[18].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[19] != null) ? arr[19].toString() : null);
		obj.setTaxPeriod2B((arr[20] != null) ? arr[20].toString() : null);
		obj.setTaxPeriodPR((arr[21] != null) ? arr[21].toString() : null);
		obj.setItcReversedTaxPeriod(
				(arr[22] != null) ? arr[22].toString() : null);
		obj.setItcReclaimedTaxPeriod(
				(arr[23] != null) ? arr[23].toString() : null);

		obj.setCalendarMonth((arr[24] != null) ? arr[24].toString() : null);
		obj.setRecipientGstin2B((arr[25] != null) ? arr[25].toString() : null);
		obj.setRecipientGstinPR((arr[26] != null) ? arr[26].toString() : null);

		obj.setSupplierGstin2B((arr[27] != null) ? arr[27].toString() : null);
		obj.setSupplierGstinPR((arr[28] != null) ? arr[28].toString() : null);
		obj.setSupplierLegalName2B(
				(arr[29] != null) ? arr[29].toString() : null);
		obj.setSupplierTradeName2B(
				(arr[30] != null) ? arr[30].toString() : null);
		obj.setSupplierNamePR((arr[31] != null) ? arr[31].toString() : null);
		obj.setDocType2B((arr[32] != null) ? arr[32].toString() : null);
		obj.setDocTypePR((arr[33] != null) ? arr[33].toString() : null);
		obj.setDocumentNumber2B((arr[34] != null) ? arr[34].toString() : null);
		obj.setDocumentNumberPR((arr[35] != null) ? arr[35].toString() : null);

		if (!Strings.isNullOrEmpty(obj.getDocumentNumber2B())) {
			docNo2B.add(removeQuotes(obj.getDocumentNumber2B()));
		}

		if (!Strings.isNullOrEmpty(obj.getDocumentNumberPR())) {
			docNoPR.add(removeQuotes(obj.getDocumentNumberPR()));
		}

		obj.setDocumentDate2B((arr[36] != null) ? arr[36].toString() : null);
		obj.setDocumentDatePR((arr[37] != null) ? arr[37].toString() : null);
		obj.setGSTPercent2B((arr[38] != null) ? arr[38].toString() : null);
		obj.setGSTPercentPR((arr[39] != null) ? arr[39].toString() : null);
		obj.setPos2B((arr[40] != null) ? arr[40].toString() : null);
		obj.setPosPR((arr[41] != null) ? arr[41].toString() : null);

		obj.setTaxableValue2B((arr[42] != null)
				? checkForNegative(arr[42].toString()) : null);
		obj.setTaxableValuePR((arr[43] != null)
				? checkForNegative(arr[43].toString()) : null);
		obj.setIgst2B((arr[44] != null) ? checkForNegative(arr[44].toString())
				: null);
		obj.setIgstPR((arr[45] != null) ? checkForNegative(arr[45].toString())
				: null);
		obj.setCgst2B((arr[46] != null) ? checkForNegative(arr[46].toString())
				: null);
		obj.setCgstPR((arr[47] != null) ? checkForNegative(arr[47].toString())
				: null);
		obj.setSgst2B((arr[48] != null) ? checkForNegative(arr[48].toString())
				: null);
		obj.setSgstPR((arr[49] != null) ? checkForNegative(arr[49].toString())
				: null);
		obj.setCess2B((arr[50] != null) ? checkForNegative(arr[50].toString())
				: null);
		obj.setCessPR((arr[51] != null) ? checkForNegative(arr[51].toString())
				: null);
		obj.setTotalTax2B((arr[52] != null)
				? checkForNegative(arr[52].toString()) : null);
		obj.setTotalTaxPR((arr[53] != null)
				? checkForNegative(arr[53].toString()) : null);

		// TO-DO
		obj.setImsTotalTaxDiff((arr[54] != null)
				? checkForNegative(arr[54].toString()) : null);
		obj.setImsTotalTaxPRGreater2B(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setImsTotalTax2BGreaterPR(
				(arr[56] != null) ? arr[56].toString() : null);

		obj.setInvoiceValue2B((arr[57] != null)
				? checkForNegative(arr[57].toString()) : null);
		obj.setInvoiceValuePR((arr[58] != null)
				? checkForNegative(arr[58].toString()) : null);
		obj.setReverseChargeFlag2B(
				(arr[59] != null) ? arr[59].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[60] != null) ? arr[60].toString() : null);
		obj.setEligibilityIndicator(
				(arr[61] != null) ? arr[61].toString() : null);

		obj.setAvailableIGST((arr[62] != null)
				? checkForNegative(arr[62].toString()) : null);
		obj.setAvailableCGST((arr[63] != null)
				? checkForNegative(arr[63].toString()) : null);
		obj.setAvailableSGST((arr[64] != null)
				? checkForNegative(arr[64].toString()) : null);
		obj.setAvailableCESS((arr[65] != null)
				? checkForNegative(arr[65].toString()) : null);
		obj.setITCReversalIdentifier(
				(arr[66] != null) ? arr[66].toString() : null);
		obj.setItcAvailability2B((arr[67] != null) ? arr[67].toString() : null);
		obj.setReasonForItcUnavailability2B(
				(arr[68] != null) ? arr[68].toString() : null);

		obj.setGSTR1FilingStatus((arr[69] != null) ? arr[69].toString() : null);
		obj.setGSTR1FilingDate((arr[70] != null) ? arr[70].toString() : null);
		obj.setGSTR1FilingPeriod((arr[71] != null) ? arr[71].toString() : null);
		obj.setGSTR3BFilingStatus(
				(arr[72] != null) ? arr[72].toString() : null);
		obj.setGstr3bFilingDate((arr[73] != null) ? arr[73].toString() : null);

		obj.setCancellationDate((arr[74] != null) ? arr[74].toString() : null);
		obj.setGenerationDate2B((arr[75] != null) ? arr[75].toString() : null);
		obj.setOrgInvAmendmentPeriod(
				(arr[76] != null) ? arr[76].toString() : null);
		obj.setOrgAmendmentType((arr[77] != null) ? arr[77].toString() : null);
		obj.setCDNDelinkingFlag((arr[78] != null) ? arr[78].toString() : null);
		obj.setDifferentialPercentage2B(
				(arr[79] != null) ? arr[79].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[80] != null) ? arr[80].toString() : null);
		obj.setOrgDocNumber2B(
				(arr[81] != null) ? removeQuotes(arr[81].toString()) : null);
		obj.setOrgDocNumberPR(
				(arr[82] != null) ? removeQuotes(arr[82].toString()) : null);

		obj.setOrgDocDate2B((arr[83] != null) ? arr[83].toString() : null);
		obj.setOrgDocDatePR((arr[84] != null) ? arr[84].toString() : null);
		obj.setOrgSupplierGstinPR(
				(arr[85] != null) ? arr[85].toString() : null);
		obj.setOrgSupplierNamePR((arr[86] != null) ? arr[86].toString() : null);
		obj.setCRDRPreGST2B((arr[87] != null) ? arr[87].toString() : null);
		obj.setCRDRPreGSTPR((arr[88] != null) ? arr[88].toString() : null);
		obj.setBoeReferenceDate((arr[89] != null) ? arr[89].toString() : null);
		obj.setBillOfEntryCreatedDate2B(
				(arr[90] != null) ? arr[90].toString() : null);

		obj.setPortCode2B((arr[91] != null) ? arr[91].toString() : null);
		obj.setPortCodePR((arr[92] != null) ? arr[92].toString() : null);

		obj.setBillOfEntry2B((arr[93] != null) ? arr[93].toString() : null);
		obj.setBillOfEntryPR((arr[94] != null) ? arr[94].toString() : null);

		obj.setBillOfEntryDate2B((arr[95] != null) ? arr[95].toString() : null);
		obj.setBillOfEntryDatePR((arr[96] != null) ? arr[96].toString() : null);

		obj.setBoeAmended((arr[97] != null) ? arr[97].toString() : null);
		obj.setTableType2B((arr[98] != null) ? arr[98].toString() : null);
		obj.setSupplyType2B((arr[99] != null) ? arr[99].toString() : null);
		obj.setSupplyTypePR((arr[100] != null) ? arr[100].toString() : null);

		obj.setUserID((arr[101] != null) ? arr[101].toString() : null);
		obj.setSourceIdentifier(
				(arr[102] != null) ? arr[102].toString() : null);
		obj.setSourceFileName((arr[103] != null) ? arr[103].toString() : null);
		obj.setProfitCentre1((arr[104] != null) ? arr[104].toString() : null);
		obj.setPlant((arr[105] != null) ? arr[105].toString() : null);
		obj.setDivision((arr[106] != null) ? arr[106].toString() : null);
		obj.setLocation((arr[107] != null) ? arr[107].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[108] != null) ? arr[108].toString() : null);
		obj.setProfitCentre2((arr[109] != null) ? arr[109].toString() : null);
		obj.setProfitCentre3((arr[110] != null) ? arr[110].toString() : null);
		obj.setProfitCentre4((arr[111] != null) ? arr[111].toString() : null);
		obj.setProfitCentre5((arr[112] != null) ? arr[112].toString() : null);
		obj.setProfitCentre6((arr[113] != null) ? arr[113].toString() : null);
		obj.setProfitCentre7((arr[114] != null) ? arr[114].toString() : null);
		obj.setGLCodeAssessableValue(
				(arr[115] != null) ? arr[115].toString() : null);
		obj.setGLCodeIGST((arr[116] != null) ? arr[116].toString() : null);
		obj.setGLCodeCGST((arr[117] != null) ? arr[117].toString() : null);
		obj.setGLCodeSGST((arr[118] != null) ? arr[118].toString() : null);
		obj.setGLCodeAdvaloremCess(
				(arr[119] != null) ? arr[119].toString() : null);
		obj.setGLCodeSpecificCess(
				(arr[120] != null) ? arr[120].toString() : null);
		obj.setGLCodeStateCessAdvalorem(
				(arr[121] != null) ? arr[121].toString() : null);

		obj.setSupplierType((arr[122] != null) ? arr[122].toString() : null);
		obj.setSupplierCode((arr[123] != null) ? arr[123].toString() : null);
		obj.setSupplierAddress1(
				(arr[124] != null) ? arr[124].toString() : null);
		obj.setSupplierAddress2(
				(arr[125] != null) ? arr[125].toString() : null);
		obj.setSupplierLocation(
				(arr[126] != null) ? arr[126].toString() : null);
		obj.setSupplierPincode((arr[127] != null) ? arr[127].toString() : null);
		obj.setStateApplyingCess(
				(arr[128] != null) ? arr[128].toString() : null);
		obj.setCIFValue((arr[129] != null) ? arr[129].toString() : null);
		obj.setCustomDuty((arr[130] != null) ? arr[130].toString() : null);
		obj.setHSNorSAC((arr[131] != null) ? arr[131].toString() : null);
		obj.setProductCode((arr[132] != null) ? arr[132].toString() : null);
		obj.setProductDescription(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setCategoryOfProduct(
				(arr[134] != null) ? arr[134].toString() : null);
		obj.setUqc((arr[135] != null) ? arr[135].toString() : null);
		obj.setQuantity((arr[136] != null) ? arr[136].toString() : null);
		obj.setAdvaloremCessRate(
				(arr[137] != null) ? arr[137].toString() : null);
		obj.setAdvaloremCessAmount(
				(arr[138] != null) ? arr[138].toString() : null);
		obj.setSpecificCessRate(
				(arr[139] != null) ? arr[139].toString() : null);
		obj.setSpecificCessAmount(
				(arr[140] != null) ? arr[140].toString() : null);
		obj.setStateCessAdvaloremRate(
				(arr[141] != null) ? arr[141].toString() : null);
		obj.setStateCessAdvaloremAmount(
				(arr[142] != null) ? arr[142].toString() : null);
		obj.setItemOtherCharges(
				(arr[143] != null) ? arr[143].toString() : null);
		obj.setClaimRefundFlag((arr[144] != null) ? arr[144].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[145] != null) ? arr[145].toString() : null);
		obj.setAdjustementReferenceNo(
				(arr[146] != null) ? arr[146].toString() : null);
		obj.setAdjustementReferenceDate(
				(arr[147] != null) ? arr[147].toString() : null);
		obj.setCommonSupplyIndicator(
				(arr[148] != null) ? arr[148].toString() : null);
		obj.setITCEntitlement((arr[149] != null) ? arr[149].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[150] != null) ? arr[150].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[151] != null) ? arr[151].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[152] != null) ? arr[152].toString() : null);
		obj.setGlPostingDate((arr[153] != null) ? arr[153].toString() : null);
		obj.setCustomerPORefNumber(
				(arr[154] != null) ? arr[154].toString() : null);
		obj.setCustomerPORefDate(
				(arr[155] != null) ? arr[155].toString() : null);
		obj.setPurchaseOrderValue(
				(arr[156] != null) ? arr[156].toString() : null);

		obj.setVendorTaxPaidVariance(
				(arr[157] != null) ? arr[157].toString() : null);
		obj.setVendorType((arr[158] != null) ? arr[158].toString() : null);
		obj.setHsnVendor((arr[159] != null) ? arr[159].toString() : null);
		obj.setVendorRiskCategory(
				(arr[160] != null) ? arr[160].toString() : null);
		obj.setVendorPaymentTermsDays(
				(arr[161] != null) ? arr[161].toString() : null);
		obj.setVendorRemarks((arr[162] != null) ? arr[162].toString() : null);
		obj.setSourceTypeofIrn((arr[163] != null) ? arr[163].toString() : null);
		obj.setIrn2B((arr[164] != null) ? arr[164].toString() : null);
		obj.setIrnPR((arr[165] != null) ? arr[165].toString() : null);
		obj.setIrnDate2B((arr[166] != null) ? arr[166].toString() : null);
		obj.setIrnDatePR((arr[167] != null) ? arr[167].toString() : null);

		obj.setUserDefinedField1(
				(arr[168] != null) ? arr[168].toString() : null);
		obj.setUserDefinedField2(
				(arr[169] != null) ? arr[169].toString() : null);
		obj.setUserDefinedField3(
				(arr[170] != null) ? arr[170].toString() : null);
		obj.setUserDefinedField4(
				(arr[171] != null) ? arr[171].toString() : null);
		obj.setUserDefinedField5(
				(arr[172] != null) ? arr[172].toString() : null);
		obj.setUserDefinedField6(
				(arr[173] != null) ? arr[173].toString() : null);
		obj.setUserDefinedField7(
				(arr[174] != null) ? arr[174].toString() : null);
		obj.setUserDefinedField8(
				(arr[175] != null) ? arr[175].toString() : null);
		obj.setUserDefinedField9(
				(arr[176] != null) ? arr[176].toString() : null);
		obj.setUserDefinedField10(
				(arr[177] != null) ? arr[177].toString() : null);
		obj.setUserDefinedField11(
				(arr[178] != null) ? arr[178].toString() : null);
		obj.setUserDefinedField12(
				(arr[179] != null) ? arr[179].toString() : null);
		obj.setUserDefinedField13(
				(arr[180] != null) ? arr[180].toString() : null);
		obj.setUserDefinedField14(
				(arr[181] != null) ? arr[181].toString() : null);
		obj.setUserDefinedField15(
				(arr[182] != null) ? arr[182].toString() : null);
		obj.setUserDefinedField28(
				(arr[183] != null) ? arr[183].toString() : null);

		obj.setEWayBillNumber((arr[184] != null) ? arr[184].toString() : null);
		obj.setEWayBillDate((arr[185] != null) ? arr[185].toString() : null);
		obj.setMatchingID((arr[186] != null) ? arr[186].toString() : null);
		obj.setRequestID((arr[187] != null) ? arr[187].toString() : null);
		obj.setIDPR((arr[188] != null) ? arr[188].toString() : null);
		obj.setID2B((arr[189] != null) ? arr[189].toString() : null);
		obj.setInvoiceKeyUploadPR(
				(arr[190] != null) ? arr[190].toString() : null);
		obj.setInvoiceKeyUpload2B(
				(arr[191] != null) ? arr[191].toString() : null);
		obj.setImsUniqueId((arr[192] != null) ? arr[192].toString() : null);

		obj.setReferenceIDPR((arr[193] != null) ? arr[193].toString() : null);
		obj.setReferenceID2B((arr[194] != null) ? arr[194].toString() : null);

		return obj;

	}

	// FY | RGSTIN | Doc-Type | Doc_Num | SGSTIN
	private String createInvKey(String docType, String docNum, String suppGstin,
			String recipientGstin, String docDate) {

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(docType) || !isPresent(docNum) || !isPresent(suppGstin)
				|| !isPresent(recipientGstin) || !isPresent(docDate)) {
			return null;
		}

		docType = PR_DOCTYPE_MAP.containsKey(docType)
				? PR_DOCTYPE_MAP.get(docType.toUpperCase()) : docType;

		String fy = GenUtil.getFinYear(
				DateFormatForStructuralValidatons.parseObjToDate(docDate));
		fy = fy.substring(0, 4) + "-" + fy.substring(4, 6);

		docKey.append(fy).append(DOC_KEY_JOINER).append(suppGstin)
				.append(DOC_KEY_JOINER).append(docType).append(DOC_KEY_JOINER)
				.append(removeQuotes(docNum)).append(DOC_KEY_JOINER)
				.append(recipientGstin);

		return docKey.toString();
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
				.append(dateFormatConversion(boeDate));

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
				.append(dateFormatConversion(boeDate));

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
		if (data.contains("`")) {
			return data.replace("`", "");
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

	private boolean getComputeOnBoardingPrm(Long entityId) {
		boolean isGSTR2BBase = false;
		try {
			EntityConfigPrmtEntity entityConfig = entityConfigRepo
					.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
							TenantContext.getTenantId(), entityId, "I45");
			String paramValue = entityConfig != null
					? entityConfig.getParamValue() : "A";
			LOGGER.debug(
					"GSTR2bprReconRespUploadServiceImpl Onboarding answer is {}",
					paramValue);
			if ("B".equals(paramValue) || "C".equals(paramValue)) {
				isGSTR2BBase = true;
			}
			return isGSTR2BBase;
		} catch (Exception ee) {
			String msg = "Exception occered while Onboard param";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		return optAns;
	}

	private String dateFormatConversion(String dateTimeString) {
		// String dateTimeString = "20-01-2023";
		if (Strings.isNullOrEmpty(dateTimeString))
			return null;
		else {
			DateTimeFormatter inputFormatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");
			LocalDate localIrnDate = DateUtil.parseObjToDate(dateTimeString);
			return localIrnDate.format(inputFormatter).toString();
 
		}}

}
