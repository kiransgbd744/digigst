package com.ey.advisory.app.glrecon.dump;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.data.entities.client.GlDumpErrorEntity;
import com.ey.advisory.app.data.entities.client.GlDumpProcessedEntity;
import com.ey.advisory.app.data.entities.client.GlDumpStagingEntity;
import com.ey.advisory.app.data.repositories.client.GlDumpApiUploadStatusRepository;
import com.ey.advisory.app.data.repositories.client.GlReconDumpErrorRepository;
import com.ey.advisory.app.data.repositories.client.GlReconDumpPsdRepository;
import com.ey.advisory.app.data.repositories.client.GlReconDumpStagingRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Service("GlReconDumpFileArrivalHandler")
public class GlReconDumpFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GlReconDumpFileArrivalHandler.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("GLReconDumpHeaderCheckService")
	private GLReconDumpHeaderCheckService headerCheckService;

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glReconFileStatusRepository;

	@Autowired
	@Qualifier("GlReconDumpStagingRepository")
	private GlReconDumpStagingRepository glDumpStagingRepository;

	@Autowired
	@Qualifier("GlReconDumpPsdRepository")
	private GlReconDumpPsdRepository glDumpPsdRepository;

	@Autowired
	@Qualifier("GlReconDumpErrorRepository")
	private GlReconDumpErrorRepository glDumpErrorRepository;

	@Autowired
	@Qualifier("GLReconDumpStructuralChain")
	private GLReconDumpStructuralChain glReconStructuralChain;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GlDumpApiUploadStatusRepository")
	GlDumpApiUploadStatusRepository apiStatusRepository;

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";
	private static final Map<String, String> ERROR_CODE_MAP = new ImmutableMap.Builder<String, String>()
			.put("ER0001", "Transaction Type cannot be left blank")
			.put("ER0002",
					"Invalid Transaction Type: Please enter 'I' for Inward or 'O' for Outward transactions")
			.put("ER0004", "Please enter the Fiscal Year in YYYY format")
			.put("ER0005", "Period cannot be left blank")
			.put("ER0006",
					"Invalid Period Format: Please enter the period in the format MMYYYY")
			.put("ER0007", "G/L Account cannot be left blank")
			.put("ER0008", "G/L Account cannot be more than 500 characters")
			.put("ER0009",
					"ERP Document type cannot be more than 500 characters")
			.put("ER0010", "Accounting Document Number cannot be left blank")
			.put("ER0011",
					"Accounting Document Number cannot be more than 500 characters")
			.put("ER0012", "Accounting Document date cannot be left blank")
			.put("ER0013", "Invalid Accounting Document date format")
			.put("ER0014",
					"Invalid Posting Date: Please enter in specified format.")
			.put("ER0016",
					"Invalid  Amount In Local Currency: Please enter in specified format.")
			.put("ER0017",
					"Invalid Clearing Date: Please enter in specified format.")
			.put("ER0018",
					"Invalid Payment Date: Please enter in specified format.")
			.put("ER0019",
					"Invalid MIGO Date: Please enter in specified format.")
			.put("ER0020",
					"Invalid MIRO Date: Please enter in specified format.")
			.put("ER0021",
					"Invalid Entry Date: Please enter in specified format.")
			.put("ER0022",
					"Company Code cannot be different across all line items in a single invoice.")
			.put("ER0023",
					"Fiscal Year cannot be different across all line items in a single invoice")
			.put("ER0024",
					"Period cannot be different across all line items in a single invoice.")
			.put("ER0025",
					"Business Place cannot be different across all line items in a single invoice.")
			.put("ER0026",
					" ERP Document Type cannot be different across all line items in a single invoice.")

			.build();

	public void processProductFile(Message message, AppExecContext context) {

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String docId = msg.getDocId();
		Long fileId = Long.valueOf(msg.getFileId());
		String userName = message.getUserName();
		try {

			Optional<GlReconFileStatusEntity> gstr1FileStatusEntity = glReconFileStatusRepository
					.findByDocId(fileId.toString());
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}

			if (document == null) {
				String errMsg = String.format(
						"Unable to find the file %s in folder %s", fileName,
						fileFolder);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			InputStream inputStream = document.getContentStream().getStream();

			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);// XlsxLightCellsTraverser
			TabularDataLayout layout = new DummyTabularDataLayout(75);

			GLReconDumpKeyBuilder documentKeyBuilder = new GLReconDumpKeyBuilder();
			GLReconDumpUploadDocRowHandler rowHandler = new GLReconDumpUploadDocRowHandler<String>(
					documentKeyBuilder);
			traverser.traverse(inputStream, layout, rowHandler, null);

			Object[] getHeaders = rowHandler.getHeaderData();
			GlReconFileStatusEntity updateFileStatus = glReconFileStatusRepository
					.getFileName(fileName);

			Pair<Boolean, String> checkHeaderFormat = headerCheckService
					.validate(getHeaders);//
			if (checkHeaderFormat == null || !checkHeaderFormat.getValue0()) {

				LOGGER.error("Headers not matched");
				updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
				glReconFileStatusRepository.save(updateFileStatus);
				return;
			}

			Map<String, List<Object[]>> documentMap = ((GLReconDumpUploadDocRowHandler<?>) rowHandler)
					.getDocumentMap();

			saveStageRecords(documentMap, updateFileStatus.getId(), userName);

			Map<String, List<ProcessingResult>> strValidation = glReconStructuralChain
					.validation(documentMap);

			List<String> listKeys = new ArrayList<>();
			for (String keys : strValidation.keySet()) {
				listKeys.add(keys);
			}

			Map<String, List<Object[]>> processMapObj = new HashMap<>();
			Map<String, List<Object[]>> errDocMapObj = new HashMap<>();

			for (String keys : documentMap.keySet()) {
				if (!listKeys.contains(keys)) {
					List<Object[]> list = documentMap.get(keys);
					processMapObj.put(keys, list);
				} else {
					List<Object[]> list = documentMap.get(keys);
					errDocMapObj.put(keys, list);
				}
			}

			if (!strValidation.isEmpty()) {
				try {
					saveErrDocAndDoc(documentKeyBuilder, documentMap,
							updateFileStatus, strValidation, processMapObj,
							errDocMapObj, userName);

				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					glReconFileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP, e);

				}

			} else {
				try {
					convertFileToGlDumpPsdDocument(documentMap,
							documentKeyBuilder, updateFileStatus, userName);
				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					glReconFileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP, e);

				}
			}

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_GL_RECON_STATUS_COUNT_UPD");

			storedProc.execute();
			glReconFileStatusRepository.updateStatus(fileId, "Processed");
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			glReconFileStatusRepository.updateStatus(fileId, "Failed");
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
	private void saveErrDocAndDoc(GLReconDumpKeyBuilder documentKeyBuilder,
			Map<String, List<Object[]>> documentMap,
			GlReconFileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMapObj,
			Map<String, List<Object[]>> errDocMapObj, String userName) {

		try {
			if (!errDocMapObj.isEmpty()) {
				convertFileToGlReconDumpError(errDocMapObj, processingResults,
						updateFileStatus, userName);
			}
			if (!documentMapObj.isEmpty()) {
				convertFileToGlDumpPsdDocument(documentMapObj,
						documentKeyBuilder, updateFileStatus, userName);
			}

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			glReconFileStatusRepository.updateStatus(updateFileStatus.getId(),
					"Failed");
			throw new AppException(EXCEPTION_APP, e);
		}
	}

	public void convertFileToGlReconDumpError(
			Map<String, List<Object[]>> errDocMapObj,
			Map<String, List<ProcessingResult>> processingResults,
			GlReconFileStatusEntity fileStatus, String userName) {
		try {
			saveErrorRecords(errDocMapObj, processingResults, fileStatus,
					userName, null);
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			glReconFileStatusRepository.updateStatus(fileStatus.getId(),
					"Failed");
			throw new AppException(EXCEPTION_APP);
		}
		LOGGER.error("GL Dump to ErrorFile Convertion "
				+ "convertFileToGlReconDumpError Endining");
	}

	public void convertFileToGlDumpPsdDocument(
			Map<String, List<Object[]>> documentMapObj,
			GLReconDumpKeyBuilder documentKeyBuilder,
			GlReconFileStatusEntity fileStatus, String userName) {
		List<GlDumpProcessedEntity> docHeaders = new ArrayList<>();
		try {
			Map<String, List<Object[]>> processMapObj = new HashMap<>();
			Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
			Map<String, String> errorDescMap = new HashMap<>();
			documentMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				Set<String> companyCodes = new HashSet<>();
				Set<String> fiscalYears = new HashSet<>();
				Set<String> taxPeriods = new HashSet<>();
				Set<String> businessPlaces = new HashSet<>();
				Set<String> erpDocumentTypes = new HashSet<>();

				for (Object[] obj : objs) {
					String companyCode = obj[1] != null
							? obj[1].toString().trim() : null;
					String fiscalYear = obj[2] != null
							? obj[2].toString().trim() : null;
					String taxPeriod = obj[3] != null ? obj[3].toString().trim()
							: null;
					String businessPlace = obj[4] != null
							? obj[4].toString().trim() : null;
					String erpDocumentType = obj[10] != null
							? obj[10].toString().trim() : null;

					if (companyCode != null) {
						companyCodes.add(companyCode);
					}
					if (fiscalYear != null) {
						fiscalYears.add(fiscalYear);
					}
					if (taxPeriod != null) {
						taxPeriods.add(taxPeriod);
					}
					if (businessPlace != null) {
						businessPlaces.add(businessPlace);
					}
					if (erpDocumentType != null) {
						erpDocumentTypes.add(erpDocumentType);
					}
				}
				if ((companyCodes.size() <= 1) && (fiscalYears.size() <= 1)
						&& (taxPeriods.size() <= 1)
						&& (businessPlaces.size() <= 1)
						&& (erpDocumentTypes.size() <= 1)) {
					processMapObj.put(key, objs);
				} else {
					String errorCodes = null;
					if (companyCodes.size() > 1) {
						errorCodes = errorCodes == null ? "ER0022"
								: errorCodes + ",ER0022";
					}
					if (fiscalYears.size() > 1) {
						errorCodes = errorCodes == null ? "ER0023"
								: errorCodes + ",ER0023";
					}
					if (taxPeriods.size() > 1) {
						errorCodes = errorCodes == null ? "ER0024"
								: errorCodes + ",ER0024";
					}
					if (businessPlaces.size() > 1) {
						errorCodes = errorCodes == null ? "ER0025"
								: errorCodes + ",ER0025";
					}
					if (erpDocumentTypes.size() > 1) {
						errorCodes = errorCodes == null ? "ER0026"
								: errorCodes + ",ER0026";
					}
					errorDescMap.put(key, errorCodes);
					errDocMapObj.put(key, objs);
				}
			});
			saveErrorRecords(errDocMapObj, null, fileStatus, userName,
					errorDescMap);
			saveProcessedRecords(processMapObj, fileStatus, userName);

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		glReconFileStatusRepository.updateStatus(fileStatus.getId(), "Failed");
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Endining");
	}

	private String deriveErrorDesc(String errorCodes) {
		String[] errorCode = errorCodes.split(",");
		List<String> errorDescList = new ArrayList<>();
		for (String code : errorCode) {
			errorDescList.add(ERROR_CODE_MAP.get(code));
		}
		return errorDescList.stream().collect(Collectors.joining(","));
	}

	private Pair<String, String> populateErrorCodesAndDescription(
			List<ProcessingResult> processingResults) {
		List<String> descList = new ArrayList<>();
		List<String> errorCodeList = new ArrayList<>();
		for (ProcessingResult error : processingResults) {
			errorCodeList.add(error.getCode());
			String errorCodeDesc = ERROR_CODE_MAP.get(error.getCode());
			descList.add(errorCodeDesc);
		}
		String errorCode = errorCodeList.stream()
				.collect(Collectors.joining(","));
		String errorDesc = descList.stream().collect(Collectors.joining(","));
		return new Pair<String, String>(errorCode, errorDesc);
	}

	private void softDeleteProcessed(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			int rowsEffected = 0;

			rowsEffected = glDumpPsdRepository.updateIsDeleteFlag(chunk);

			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in TBL_EWB_FU_HEADER table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private void softDeleteError(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			int rowsEffected = 0;

			rowsEffected = glDumpErrorRepository.updateIsDeleteFlag(chunk);

			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in TBL_EWB_FU_HEADER table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private String truncate(int index, String data) {
		return data.substring(0, Math.min(index, data.length()));
	}

	private void saveStageRecords(Map<String, List<Object[]>> documentMap,
			Long fileId, String userName) {
		List<GlDumpStagingEntity> stagingEntities = new ArrayList<>();

		documentMap.forEach((key, rows) -> {
			for (Object[] row : rows) {

				GlDumpStagingEntity entity = new GlDumpStagingEntity();
				entity.setTransactionType(row[0] != null
						? truncate(50, row[0].toString().trim()) : null);
				entity.setCompanyCode(row[1] != null
						? truncate(1000, row[1].toString().trim()) : null);
				entity.setFiscalYear(row[2] != null
						? truncate(50, row[2].toString().trim()) : null);
				entity.setTaxPeriod(row[3] != null
						? truncate(50, row[3].toString().trim()) : null);
				entity.setBussinessPlace(row[4] != null
						? truncate(1000, row[4].toString().trim()) : null);
				entity.setBusinessArea(row[5] != null
						? truncate(1000, row[5].toString().trim()) : null);
				entity.setGlAccount(row[6] != null
						? truncate(1000, row[6].toString().trim()) : null);
				entity.setGlDescription(row[7] != null
						? truncate(1000, row[7].toString().trim()) : null);
				entity.setText(row[8] != null
						? truncate(1000, row[8].toString().trim()) : null);
				entity.setAssignmentNumber(row[9] != null
						? truncate(1000, row[9].toString().trim()) : null);
				entity.setErpDocType(row[10] != null
						? truncate(1000, row[10].toString().trim()) : null);
				entity.setAccountingVoucherNumber(row[11] != null
						? truncate(1000, row[11].toString().trim()) : null);
				entity.setAccountingVoucherDate(row[12] != null
						? truncate(1000, row[12].toString().trim()) : null);
				entity.setItemNumber(row[13] != null
						? truncate(50, row[13].toString().trim()) : null);
				entity.setPostingKey(row[14] != null
						? truncate(1000, row[14].toString().trim()) : null);
				entity.setPostingDate(row[15] != null
						? truncate(1000, row[15].toString().trim()) : null);
				entity.setAmountInLocalCurrency(row[16] != null
						? truncate(1000, row[16].toString().trim()) : null);
				entity.setLocalCurrencyCode(row[17] != null
						? truncate(1000, row[17].toString().trim()) : null);
				entity.setClearingDocNumber(row[18] != null
						? truncate(1000, row[18].toString().trim()) : null);
				entity.setClearingDocDate(row[19] != null
						? truncate(1000, row[19].toString().trim()) : null);
				entity.setCustomerCode(row[20] != null
						? truncate(1000, row[20].toString().trim()) : null);
				entity.setCustomerName(row[21] != null
						? truncate(1000, row[21].toString().trim()) : null);
				entity.setCustomerGstin(row[22] != null
						? truncate(1000, row[22].toString().trim()) : null);
				entity.setSupplierCode(row[23] != null
						? truncate(1000, row[23].toString().trim()) : null);
				entity.setSupplierName(row[24] != null
						? truncate(1000, row[24].toString().trim()) : null);
				entity.setSupplierGstin(row[25] != null
						? truncate(1000, row[25].toString().trim()) : null);
				entity.setPlantCode(row[26] != null
						? truncate(1000, row[26].toString().trim()) : null);
				entity.setCostCentre(row[27] != null
						? truncate(1000, row[27].toString().trim()) : null);
				entity.setProfitCentre(row[28] != null
						? truncate(1000, row[28].toString().trim()) : null);
				entity.setSpecialGlIndicator(row[29] != null
						? truncate(1000, row[29].toString().trim()) : null);
				entity.setReference(row[30] != null
						? truncate(1000, row[30].toString().trim()) : null);
				entity.setAmountinDocumentCurrency(row[31] != null
						? truncate(1000, row[31].toString().trim()) : null);
				entity.setEffectiveExchangeRate(row[32] != null
						? truncate(1000, row[32].toString().trim()) : null);
				entity.setDocumentCurrencyCode(row[33] != null
						? truncate(1000, row[33].toString().trim()) : null);
				entity.setAccountType(row[34] != null
						? truncate(1000, row[34].toString().trim()) : null);
				entity.setTaxCode(row[35] != null
						? truncate(1000, row[35].toString().trim()) : null);
				entity.setWithHoldingTaxAmount(row[36] != null
						? truncate(1000, row[36].toString().trim()) : null);
				entity.setWithHoldingExemptAmount(row[37] != null
						? truncate(1000, row[37].toString().trim()) : null);
				entity.setWithHoldingTaxBaseAmount(row[38] != null
						? truncate(1000, row[38].toString().trim()) : null);
				entity.setInvoiceReference(row[39] != null
						? truncate(1000, row[39].toString().trim()) : null);
				entity.setDebitCreditIndicator(row[40] != null
						? truncate(1000, row[40].toString().trim()) : null);
				entity.setPaymentDate(row[41] != null
						? truncate(1000, row[41].toString().trim()) : null);
				entity.setPaymentBlock(row[42] != null
						? truncate(1000, row[42].toString().trim()) : null);
				entity.setPaymentReference(row[43] != null
						? truncate(1000, row[43].toString().trim()) : null);
				entity.setTermsOfPayment(row[44] != null
						? truncate(1000, row[44].toString().trim()) : null);
				entity.setMaterial(row[45] != null
						? truncate(1000, row[45].toString().trim()) : null);
				entity.setReferenceKey1(row[46] != null
						? truncate(1000, row[46].toString().trim()) : null);
				entity.setOffSettingAccountType(row[47] != null
						? truncate(1000, row[47].toString().trim()) : null);
				entity.setOffSettingAccountNumber(row[48] != null
						? truncate(1000, row[48].toString().trim()) : null);
				entity.setDocumentHeaderText(row[49] != null
						? truncate(1000, row[49].toString().trim()) : null);
				entity.setBillingDocNumber(row[50] != null
						? truncate(1000, row[50].toString().trim()) : null);
				entity.setBillingDocDate(row[51] != null
						? truncate(25, row[51].toString().trim()) : null);
				entity.setMigoNumber(row[52] != null
						? truncate(1000, row[52].toString().trim()) : null);
				entity.setMigoDate(row[53] != null
						? truncate(1000, row[53].toString().trim()) : null);
				entity.setMiroNumber(row[54] != null
						? truncate(1000, row[54].toString().trim()) : null);
				entity.setMiroDate(row[55] != null
						? truncate(1000, row[55].toString().trim()) : null);
				entity.setExpenseGlMapping(row[56] != null
						? truncate(1000, row[56].toString().trim()) : null);
				entity.setSegment(row[57] != null
						? truncate(1000, row[57].toString().trim()) : null);
				entity.setGeoLevel(row[58] != null
						? truncate(1000, row[58].toString().trim()) : null);
				entity.setStateName(row[59] != null
						? truncate(100, row[59].toString().trim()) : null);
				entity.setUserId(row[60] != null
						? truncate(1000, row[60].toString().trim()) : null);
				entity.setParkedBy(row[61] != null
						? truncate(1000, row[61].toString().trim()) : null);
				entity.setEntryDate(row[62] != null
						? truncate(1000, row[62].toString().trim()) : null);
				entity.setTimeOfEntry(row[63] != null
						? truncate(1000, row[63].toString().trim()) : null);
				entity.setRemarks(row[64] != null
						? truncate(1000, row[64].toString().trim()) : null);
				entity.setUserDefinedField1(row[65] != null
						? truncate(1000, row[65].toString().trim()) : null);
				entity.setUserDefinedField2(row[66] != null
						? truncate(1000, row[66].toString().trim()) : null);
				entity.setUserDefinedField3(row[67] != null
						? truncate(1000, row[67].toString().trim()) : null);
				entity.setUserDefinedField4(row[68] != null
						? truncate(1000, row[68].toString().trim()) : null);
				entity.setUserDefinedField5(row[69] != null
						? truncate(1000, row[69].toString().trim()) : null);
				entity.setUserDefinedField6(row[70] != null
						? truncate(1000, row[70].toString().trim()) : null);
				entity.setUserDefinedField7(row[71] != null
						? truncate(1000, row[71].toString().trim()) : null);
				entity.setUserDefinedField8(row[72] != null
						? truncate(1000, row[72].toString().trim()) : null);
				entity.setUserDefinedField9(row[73] != null
						? truncate(1000, row[73].toString().trim()) : null);
				entity.setUserDefinedField10(row[74] != null
						? truncate(1000, row[74].toString().trim()) : null);
				entity.setDocKey(truncate(1000, key));
				entity.setIsDelete(false);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setCreatedBy(userName);
				entity.setFileId(fileId);

				stagingEntities.add(entity);
			}
		});

		glDumpStagingRepository.saveAll(stagingEntities);
	}

	private void saveErrorRecords(Map<String, List<Object[]>> errorDocMap,
			Map<String, List<ProcessingResult>> processingResults,
			GlReconFileStatusEntity fileStatus, String userName,
			Map<String, String> errorDescMap) {
		List<GlDumpErrorEntity> errorEntities = new ArrayList<>();
		List<String> errorKeys = new ArrayList<>();
		errorDocMap.forEach((key, rows) -> {
			Pair<String, String> errorCodeDescPair = null;
			if (processingResults != null) {
				List<ProcessingResult> errorList = processingResults.get(key);
				errorCodeDescPair = populateErrorCodesAndDescription(errorList);
			}
			for (Object[] row : rows) {

				GlDumpErrorEntity entity = new GlDumpErrorEntity();
				entity.setTransactionType(row[0] != null
						? truncate(50, row[0].toString().trim()) : null);
				entity.setCompanyCode(row[1] != null
						? truncate(1000, row[1].toString().trim()) : null);
				entity.setFiscalYear(row[2] != null
						? truncate(50, row[2].toString().trim()) : null);
				entity.setTaxPeriod(row[3] != null
						? truncate(50, row[3].toString().trim()) : null);
				entity.setBussinessPlace(row[4] != null
						? truncate(1000, row[4].toString().trim()) : null);
				entity.setBusinessArea(row[5] != null
						? truncate(1000, row[5].toString().trim()) : null);
				entity.setGlAccount(row[6] != null
						? truncate(1000, row[6].toString().trim()) : null);
				entity.setGlDescription(row[7] != null
						? truncate(1000, row[7].toString().trim()) : null);
				entity.setText(row[8] != null
						? truncate(1000, row[8].toString().trim()) : null);
				entity.setAssignmentNumber(row[9] != null
						? truncate(1000, row[9].toString().trim()) : null);
				entity.setErpDocType(row[10] != null
						? truncate(1000, row[10].toString().trim()) : null);
				entity.setAccountingVoucherNumber(row[11] != null
						? truncate(1000, row[11].toString().trim()) : null);
				entity.setAccountingVoucherDate(row[12] != null
						? truncate(1000, row[12].toString().trim()) : null);
				entity.setItemNumber(row[13] != null
						? truncate(50, row[13].toString().trim()) : null);
				entity.setPostingKey(row[14] != null
						? truncate(1000, row[14].toString().trim()) : null);
				entity.setPostingDate(row[15] != null
						? truncate(1000, row[15].toString().trim()) : null);
				entity.setAmountInLocalCurrency(row[16] != null
						? truncate(1000, row[16].toString().trim()) : null);
				entity.setLocalCurrencyCode(row[17] != null
						? truncate(1000, row[17].toString().trim()) : null);
				entity.setClearingDocNumber(row[18] != null
						? truncate(1000, row[18].toString().trim()) : null);
				entity.setClearingDocDate(row[19] != null
						? truncate(1000, row[19].toString().trim()) : null);
				entity.setCustomerCode(row[20] != null
						? truncate(1000, row[20].toString().trim()) : null);
				entity.setCustomerName(row[21] != null
						? truncate(1000, row[21].toString().trim()) : null);
				entity.setCustomerGstin(row[22] != null
						? truncate(1000, row[22].toString().trim()) : null);
				entity.setSupplierCode(row[23] != null
						? truncate(1000, row[23].toString().trim()) : null);
				entity.setSupplierName(row[24] != null
						? truncate(1000, row[24].toString().trim()) : null);
				entity.setSupplierGstin(row[25] != null
						? truncate(1000, row[25].toString().trim()) : null);
				entity.setPlantCode(row[26] != null
						? truncate(1000, row[26].toString().trim()) : null);
				entity.setCostCentre(row[27] != null
						? truncate(1000, row[27].toString().trim()) : null);
				entity.setProfitCentre(row[28] != null
						? truncate(1000, row[28].toString().trim()) : null);
				entity.setSpecialGlIndicator(row[29] != null
						? truncate(1000, row[29].toString().trim()) : null);
				entity.setReference(row[30] != null
						? truncate(1000, row[30].toString().trim()) : null);
				entity.setAmountinDocumentCurrency(row[31] != null
						? truncate(1000, row[31].toString().trim()) : null);
				entity.setEffectiveExchangeRate(row[32] != null
						? truncate(1000, row[32].toString().trim()) : null);
				entity.setDocumentCurrencyCode(row[33] != null
						? truncate(1000, row[33].toString().trim()) : null);
				entity.setAccountType(row[34] != null
						? truncate(1000, row[34].toString().trim()) : null);
				entity.setTaxCode(row[35] != null
						? truncate(1000, row[35].toString().trim()) : null);
				entity.setWithHoldingTaxAmount(row[36] != null
						? truncate(1000, row[36].toString().trim()) : null);
				entity.setWithHoldingExemptAmount(row[37] != null
						? truncate(1000, row[37].toString().trim()) : null);
				entity.setWithHoldingTaxBaseAmount(row[38] != null
						? truncate(1000, row[38].toString().trim()) : null);
				entity.setInvoiceReference(row[39] != null
						? truncate(1000, row[39].toString().trim()) : null);
				entity.setDebitCreditIndicator(row[40] != null
						? truncate(1000, row[40].toString().trim()) : null);
				entity.setPaymentDate(row[41] != null
						? truncate(1000, row[41].toString().trim()) : null);
				entity.setPaymentBlock(row[42] != null
						? truncate(1000, row[42].toString().trim()) : null);
				entity.setPaymentReference(row[43] != null
						? truncate(1000, row[43].toString().trim()) : null);
				entity.setTermsOfPayment(row[44] != null
						? truncate(1000, row[44].toString().trim()) : null);
				entity.setMaterial(row[45] != null
						? truncate(1000, row[45].toString().trim()) : null);
				entity.setReferenceKey1(row[46] != null
						? truncate(1000, row[46].toString().trim()) : null);
				entity.setOffSettingAccountType(row[47] != null
						? truncate(1000, row[47].toString().trim()) : null);
				entity.setOffSettingAccountNumber(row[48] != null
						? truncate(1000, row[48].toString().trim()) : null);
				entity.setDocumentHeaderText(row[49] != null
						? truncate(1000, row[49].toString().trim()) : null);
				entity.setBillingDocNumber(row[50] != null
						? truncate(1000, row[50].toString().trim()) : null);
				entity.setBillingDocDate(row[51] != null
						? truncate(25, row[51].toString().trim()) : null);
				entity.setMigoNumber(row[52] != null
						? truncate(1000, row[52].toString().trim()) : null);
				entity.setMigoDate(row[53] != null
						? truncate(1000, row[53].toString().trim()) : null);
				entity.setMiroNumber(row[54] != null
						? truncate(1000, row[54].toString().trim()) : null);
				entity.setMiroDate(row[55] != null
						? truncate(1000, row[55].toString().trim()) : null);
				entity.setExpenseGlMapping(row[56] != null
						? truncate(1000, row[56].toString().trim()) : null);
				entity.setSegment(row[57] != null
						? truncate(1000, row[57].toString().trim()) : null);
				entity.setGeoLevel(row[58] != null
						? truncate(1000, row[58].toString().trim()) : null);
				entity.setStateName(row[59] != null
						? truncate(100, row[59].toString().trim()) : null);
				entity.setUserId(row[60] != null
						? truncate(1000, row[60].toString().trim()) : null);
				entity.setParkedBy(row[61] != null
						? truncate(1000, row[61].toString().trim()) : null);
				entity.setEntryDate(row[62] != null
						? truncate(1000, row[62].toString().trim()) : null);
				entity.setTimeOfEntry(row[63] != null
						? truncate(1000, row[63].toString().trim()) : null);
				entity.setRemarks(row[64] != null
						? truncate(1000, row[64].toString().trim()) : null);
				entity.setUserDefinedField1(row[65] != null
						? truncate(1000, row[65].toString().trim()) : null);
				entity.setUserDefinedField2(row[66] != null
						? truncate(1000, row[66].toString().trim()) : null);
				entity.setUserDefinedField3(row[67] != null
						? truncate(1000, row[67].toString().trim()) : null);
				entity.setUserDefinedField4(row[68] != null
						? truncate(1000, row[68].toString().trim()) : null);
				entity.setUserDefinedField5(row[69] != null
						? truncate(1000, row[69].toString().trim()) : null);
				entity.setUserDefinedField6(row[70] != null
						? truncate(1000, row[70].toString().trim()) : null);
				entity.setUserDefinedField7(row[71] != null
						? truncate(1000, row[71].toString().trim()) : null);
				entity.setUserDefinedField8(row[72] != null
						? truncate(1000, row[72].toString().trim()) : null);
				entity.setUserDefinedField9(row[73] != null
						? truncate(1000, row[73].toString().trim()) : null);
				entity.setUserDefinedField10(row[74] != null
						? truncate(1000, row[74].toString().trim()) : null);

				entity.setDocKey(key);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setCreatedBy(userName);
				entity.setIsDelete(false);
				entity.setFileId(fileStatus.getId());
				entity.setDataOriginType(fileStatus.getSource());
				if (errorCodeDescPair != null) {
					entity.setErrorCode(errorCodeDescPair.getValue0());
					entity.setErrorDesc(errorCodeDescPair.getValue1());
				} else if (errorDescMap != null) {
					String errorDesc = deriveErrorDesc(errorDescMap.get(key));
					entity.setErrorCode(errorDescMap.get(key));
					entity.setErrorDesc(errorDesc);
				}
				errorKeys.add(key);
				errorEntities.add(entity);
			}
		});

		softDeleteError(errorKeys);
		glDumpErrorRepository.saveAll(errorEntities);
	}

	public void saveProcessedRecords(Map<String, List<Object[]>> documentMap,
			GlReconFileStatusEntity fileStatus, String userName) {
		List<GlDumpProcessedEntity> processedEntities = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<String> psdKeys = new ArrayList<>();
		documentMap.forEach((key, rows) -> {
			psdKeys.add(key);
			for (Object[] row : rows) {
				GlDumpProcessedEntity entity = new GlDumpProcessedEntity();
				entity.setTransactionType(
						row[0] != null ? row[0].toString().trim() : null);
				entity.setCompanyCode(
						row[1] != null ? row[1].toString().trim() : null);
				entity.setFiscalYear(row[2] != null
						? Long.valueOf(row[2].toString().trim()) : null);
				entity.setTaxPeriod(
						row[3] != null ? row[3].toString().trim() : null);
				entity.setDerivedTaxPeriod(
						GenUtil.getDerivedTaxPeriod(row[3].toString().trim())
								.toString());
				entity.setBussinessPlace(
						row[4] != null ? row[4].toString().trim() : null);
				entity.setBusinessArea(
						row[5] != null ? row[5].toString().trim() : null);
				entity.setGlAccount(
						row[6] != null ? row[6].toString().trim() : null);
				entity.setGlDescription(
						row[7] != null ? row[7].toString().trim() : null);
				entity.setText(
						row[8] != null ? row[8].toString().trim() : null);
				entity.setAssignmentNumber(
						row[9] != null ? row[9].toString().trim() : null);
				entity.setErpDocType(
						row[10] != null ? row[10].toString().trim() : null);
				entity.setAccountingVoucherNumber(
						row[11] != null ? row[11].toString().trim() : null);

				entity.setAccountingVoucherDate(
						row[12] != null
								? LocalDate.parse(
										row[12].toString().trim()
												.substring(0, 10).trim(),
										formatter)
								: null);
				entity.setItemNumber(row[13] != null
						? Long.valueOf(row[13].toString().trim()) : null);
				entity.setPostingKey(
						row[14] != null ? row[14].toString().trim() : null);
				entity.setPostingDate(row[15] != null ? LocalDate.parse(
						row[15].toString().trim().substring(0, 10).trim(),
						formatter) : null);
				entity.setAmountInLocalCurrency(row[16] != null
						? new BigDecimal(row[16].toString().trim()) : null);
				entity.setLocalCurrencyCode(
						row[17] != null ? row[17].toString().trim() : null);
				entity.setClearingDocNumber(
						row[18] != null ? row[18].toString().trim() : null);
				entity.setClearingDocDate(row[19] != null ? LocalDate.parse(
						row[19].toString().trim().substring(0, 10).trim(),
						formatter) : null);
				entity.setCustomerCode(
						row[20] != null ? row[20].toString().trim() : null);
				entity.setCustomerName(
						row[21] != null ? row[21].toString().trim() : null);
				entity.setCustomerGstin(
						row[22] != null ? row[22].toString().trim() : null);
				entity.setSupplierCode(
						row[23] != null ? row[23].toString().trim() : null);
				entity.setSupplierName(
						row[24] != null ? row[24].toString().trim() : null);
				entity.setSupplierGstin(
						row[25] != null ? row[25].toString().trim() : null);
				entity.setPlantCode(
						row[26] != null ? row[26].toString().trim() : null);
				entity.setCostCentre(
						row[27] != null ? row[27].toString().trim() : null);
				entity.setProfitCentre(
						row[28] != null ? row[28].toString().trim() : null);
				entity.setSpecialGlIndicator(
						row[29] != null ? row[29].toString().trim() : null);
				entity.setReference(
						row[30] != null ? row[30].toString().trim() : null);
				entity.setAmountinDocumentCurrency(
						row[31] != null ? row[31].toString().trim() : null);
				entity.setEffectiveExchangeRate(
						row[32] != null ? row[32].toString().trim() : null);
				entity.setDocumentCurrencyCode(
						row[33] != null ? row[33].toString().trim() : null);
				entity.setAccountType(
						row[34] != null ? row[34].toString().trim() : null);
				entity.setTaxCode(
						row[35] != null ? row[35].toString().trim() : null);
				entity.setWithHoldingTaxAmount(
						row[36] != null ? row[36].toString().trim() : null);
				entity.setWithHoldingExemptAmount(
						row[37] != null ? row[37].toString().trim() : null);
				entity.setWithHoldingTaxBaseAmount(
						row[38] != null ? row[38].toString().trim() : null);
				entity.setInvoiceReference(
						row[39] != null ? row[39].toString().trim() : null);
				entity.setDebitCreditIndicator(
						row[40] != null ? row[40].toString().trim() : null);
				entity.setPaymentDate(row[41] != null ? LocalDate.parse(
						row[41].toString().trim().substring(0, 10).trim(),
						formatter) : null);
				entity.setPaymentBlock(
						row[42] != null ? row[42].toString().trim() : null);
				entity.setPaymentReference(
						row[43] != null ? row[43].toString().trim() : null);
				entity.setTermsOfPayment(
						row[44] != null ? row[44].toString().trim() : null);
				entity.setMaterial(
						row[45] != null ? row[45].toString().trim() : null);
				entity.setReferenceKey1(
						row[46] != null ? row[46].toString().trim() : null);
				entity.setOffSettingAccountType(
						row[47] != null ? row[47].toString().trim() : null);
				entity.setOffSettingAccountNumber(
						row[48] != null ? row[48].toString().trim() : null);
				entity.setDocumentHeaderText(
						row[49] != null ? row[49].toString().trim() : null);
				entity.setBillingDocNumber(
						row[50] != null ? row[50].toString().trim() : null);
				entity.setBillingDocDate(
						row[51] != null ? row[51].toString().trim() : null);
				entity.setMigoNumber(
						row[52] != null ? row[52].toString().trim() : null);
				entity.setMigoDate(row[53] != null
						? LocalDate.parse(row[53].toString().trim(), formatter)
						: null);
				entity.setMiroNumber(
						row[54] != null ? row[54].toString().trim() : null);
				entity.setMiroDate(row[55] != null
						? LocalDate.parse(row[55].toString().trim(), formatter)
						: null);
				entity.setExpenseGlMapping(
						row[56] != null ? row[56].toString().trim() : null);
				entity.setSegment(
						row[57] != null ? row[57].toString().trim() : null);
				entity.setGeoLevel(
						row[58] != null ? row[58].toString().trim() : null);
				entity.setStateName(
						row[59] != null ? row[59].toString().trim() : null);
				entity.setUserId(
						row[60] != null ? row[60].toString().trim() : null);
				entity.setParkedBy(
						row[61] != null ? row[61].toString().trim() : null);
				entity.setEntryDate(row[62] != null
						? LocalDate.parse(row[62].toString().trim(), formatter)
						: null);
				entity.setTimeOfEntry(
						row[63] != null ? row[63].toString().trim() : null);
				entity.setRemarks(
						row[64] != null ? row[64].toString().trim() : null);
				entity.setUserDefinedField1(
						row[65] != null ? row[65].toString().trim() : null);
				entity.setUserDefinedField2(
						row[66] != null ? row[66].toString().trim() : null);
				entity.setUserDefinedField3(
						row[67] != null ? row[67].toString().trim() : null);
				entity.setUserDefinedField4(
						row[68] != null ? row[68].toString().trim() : null);
				entity.setUserDefinedField5(
						row[69] != null ? row[69].toString().trim() : null);
				entity.setUserDefinedField6(
						row[70] != null ? row[70].toString().trim() : null);
				entity.setUserDefinedField7(
						row[71] != null ? row[71].toString().trim() : null);
				entity.setUserDefinedField8(
						row[72] != null ? row[72].toString().trim() : null);
				entity.setUserDefinedField9(
						row[73] != null ? row[73].toString().trim() : null);
				entity.setUserDefinedField10(
						row[74] != null ? row[74].toString().trim() : null);

				entity.setDocKey(key);
				entity.setIsDelete(false);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setCreatedBy(userName);
				entity.setFileId(fileStatus.getId());
				entity.setDataOriginType(fileStatus.getSource());

				processedEntities.add(entity);
			}
		});
		softDeleteError(psdKeys);
		glDumpPsdRepository.saveAll(processedEntities);
	}

	public void validation(Map<String, List<Object[]>> documentMap,
			GlReconFileStatusEntity fileStatus, String payloadId) {
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		GLReconDumpKeyBuilder documentKeyBuilder = new GLReconDumpKeyBuilder();
		GLReconDumpUploadDocRowHandler rowHandler = new GLReconDumpUploadDocRowHandler<String>(
				documentKeyBuilder);
		saveStageRecords(documentMap, fileStatus.getId(), userName);

		Map<String, List<ProcessingResult>> strValidation = glReconStructuralChain
				.validation(documentMap);

		List<String> listKeys = new ArrayList<>();
		for (String keys : strValidation.keySet()) {
			listKeys.add(keys);
		}

		Map<String, List<Object[]>> processMapObj = new HashMap<>();
		Map<String, List<Object[]>> errDocMapObj = new HashMap<>();

		for (String keys : documentMap.keySet()) {
			if (!listKeys.contains(keys)) {
				List<Object[]> list = documentMap.get(keys);
				processMapObj.put(keys, list);
			} else {
				List<Object[]> list = documentMap.get(keys);
				errDocMapObj.put(keys, list);
			}
		}

		if (!strValidation.isEmpty()) {
			try {
				saveErrDocAndDoc(documentKeyBuilder, documentMap, fileStatus,
						strValidation, processMapObj, errDocMapObj, userName);

			} catch (Exception e) {
				LOGGER.error("Error Occured:{} ", e);
				fileStatus.setFileStatus(JobStatusConstants.FAILED);
				glReconFileStatusRepository.save(fileStatus);
				throw new AppException(EXCEPTION_APP, e);

			}

		} else {
			try {
				convertFileToGlDumpPsdDocument(documentMap, documentKeyBuilder,
						fileStatus, userName);
			} catch (Exception e) {
				LOGGER.error("Error Occured:{} ", e);
				fileStatus.setFileStatus(JobStatusConstants.FAILED);
				glReconFileStatusRepository.save(fileStatus);
				throw new AppException(EXCEPTION_APP, e);

			}
		}

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_GL_RECON_STATUS_COUNT_UPD");

		storedProc.execute();
		glReconFileStatusRepository.updateStatus(fileStatus.getId(),
				"Processed");
		List<GlDumpErrorEntity> errorEntities = glDumpErrorRepository
				.findAllByFileId(fileStatus.getId());
		if (errorEntities != null && !errorEntities.isEmpty())
			apiStatusRepository.updateErrorStatus(payloadId, "Error");
		
	}
}
