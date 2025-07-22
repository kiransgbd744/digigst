/**
* 
 */
package com.ey.advisory.app.services.docs.gstr7;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocItemEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr7trans.Gstr7TransDocHeaderRepository;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocSaveRespDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceOutwardDocSaveRespDto;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * * @author Siva.Reddy
 *
 */
@Slf4j
@Service("Gstr7TransDocSaveServiceImpl")
public class Gstr7TransDocSaveServiceImpl implements Gstr7TransDocSaveService {

	private static final String CLASS_NAME = "Gstr7TransDocSaveServiceImpl";

	@Autowired
	@Qualifier("Gstr7TransDocRulesValidatorService")
	private Gstr7TransDocRulesValidatorService salesDocRulesValSvc;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	private DocKeyGenerator<Gstr7TransDocHeaderEntity, String> docKeyGen;

	@Autowired
	@Qualifier("DefaultGstr7TransDocBifurcator")
	private Gstr7TransDocBifurcator<Gstr7TransDocHeaderEntity> defaultBifurcator;

	@Autowired
	@Qualifier("GSTR7TransDocSaveResp")
	private GSTR7TransDocSaveResp gstr7TransDocSaveResp;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("Gstr7TransDocHeaderRepository")
	private Gstr7TransDocHeaderRepository docHeaderRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	private Gstr7TransDefaultDuplicateDocCheckServiceImpl duplicateCheckServiceImpl;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String PIPE = "|";

	public static final String CONFI_KEY = "gstr7.transactional.cutOff";

	@Override
	@Transactional(value = "clientTransactionManager")
	public EInvoiceOutwardDocSaveRespDto saveDocuments(
			List<Gstr7TransDocHeaderEntity> documents, String sourceId,
			String headerPayloadId, List<AsyncExecJob> asyncJobs) {
		EInvoiceOutwardDocSaveRespDto finalRespDto = new EInvoiceOutwardDocSaveRespDto();

		try {
			int totalCount = documents.size();
			String groupCode = TenantContext.getTenantId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entered saveDocuments for groupCode{} ",
						groupCode);
			}

			ProcessingContext context = new ProcessingContext();
			context.seAttribute("groupCode", groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Validating documents for groupCode{} ",
						groupCode);
			}

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CALC_CALC_CONFIG_START", CLASS_NAME, "saveDocuments",
					null);
			convertCalcConfigAndSetValues(documents, sourceId, headerPayloadId);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CALC_CALC_CONFIG_END", CLASS_NAME, "saveDocuments", null);

			// markDuplicateDocuments(documents);
			settingFiledGstins(context);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BIFURCATION_START", CLASS_NAME, "saveDocuments", null);
			bifurateGstr7TransDoc(documents, new HashMap<>(), groupCode);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BIFURCATION_END", CLASS_NAME, "saveDocuments", null);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_GSTR1_START", CLASS_NAME, "saveDocuments", null);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_VALIDATION_START", CLASS_NAME, "saveDocuments",
					null);

			Map<String, Config> confiMap = configManager.getConfigs("GSTR7",
					CONFI_KEY, "DEFAULT");

			String configString = confiMap.get(CONFI_KEY) == null ? "202506"
					: confiMap.get(CONFI_KEY).getValue();

			context.seAttribute(CONFI_KEY, configString);
			// Bussiness validation start here
			DocRulesValidationResult<String> valResult = salesDocRulesValSvc
					.validate(documents, context);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_VALIDATION_END", CLASS_NAME, "saveDocuments",
					null);
			Map<String, List<ProcessingResult>> ruleProcessingResults = valResult
					.getProcessingResults();

			Map<String, List<ProcessingResult>> canChkprocessingResults = canDocLookUp(
					documents, context);

			canChkprocessingResults.forEach(
					(key, value) -> ruleProcessingResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));
            /*
			Map<String, List<ProcessingResult>> processingResults = DuplicatesRemove
					.eliminateDuplicates(ruleProcessingResults);
			*/

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Validated documents for groupCode{} ", groupCode);
			}

			// This is for locating the error
			Map<String, Gstr7TransDocHeaderEntity> errDocMap = new HashMap<>();
			// For each document with errors, set the isError to true.
			if (!ruleProcessingResults.isEmpty()) {
				for (Gstr7TransDocHeaderEntity doc : documents) {
					String docKey = docKeyGen.generateKey(doc);
					if (isError(ruleProcessingResults, docKey)) {
						errDocMap.put(docKey, doc);
					}
					// if (isInfo(processingResults, docKey)) {
					// doc.setIsInfo(true);
					// errDocMap.put(docKey, doc);
					// }

					if (isError(ruleProcessingResults, docKey)
							|| isInfo(ruleProcessingResults, docKey)) {
						populateErrorCodeAndErrorDescription(ruleProcessingResults,
								docKey, doc);
					}
				}

			}

			Map<String, List<OutwardTransDocError>> errorMap = convertErrors(
					ruleProcessingResults, errDocMap);

			// Get the list of document ids for hte existing documetns and keep
			// it aside, so that we can use this to populate the 'oldDocId'
			// value
			// while creating the response.
			Stream<Long> docIdStream = documents.stream()
					.map(doc -> doc.getId());
			List<Long> oldDocIds = docIdStream.filter(x -> x != null)
					.collect(Collectors.toList());

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DB_DOC_START", CLASS_NAME, "saveDocuments", null);

			// saving bussiness validated docs
			List<Gstr7TransDocHeaderEntity> savedDocs = saveDocs(documents,
					docKeyGen);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DB_DOC_END", CLASS_NAME, "saveDocuments", null);

			if (!GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
					.getDataOriginTypeCode().equalsIgnoreCase(
							documents.get(0).getDataOriginTypeCode())) {
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"CREATE_OUTWARD_DOC_SAVE_START", CLASS_NAME,
						"saveDocuments", null);
				List<EInvoiceDocSaveRespDto> docSaveRespDtos = gstr7TransDocSaveResp
						.createOutwardDocSaveAPIResponse(oldDocIds, savedDocs,
								errorMap);
				finalRespDto.setSavedDocsResp(docSaveRespDtos);
				finalRespDto.setProcessingResults(ruleProcessingResults);
				finalRespDto.setErrors(errorCount(savedDocs));
				finalRespDto.setTotalRecords(totalCount);
				// finalRespDto.setJobParamsList(jobParamsList);
				LOGGER.error("final Resp Dto {} ", finalRespDto);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"CREATE_OUTWARD_DOC_SAVE_END", CLASS_NAME,
						"saveDocuments", null);
			}

		} catch (Exception e) {
			LOGGER.error("Save Docs : Exception Occured:{} ", e);
			throw new AppException("Exception while saving the documents ",
					e.getMessage());
		}
		return finalRespDto;
	}

	private void bifurateGstr7TransDoc(
			List<Gstr7TransDocHeaderEntity> documents,
			Map<String, List<ProcessingResult>> processingResults,
			String groupCode) {

		ProcessingContext bifContext = new ProcessingContext();

		documents.stream()
				.filter(doc -> !"CAN".equalsIgnoreCase(doc.getSupplyType())
						&& !doc.isError())
				.forEach(doc -> {
					// if document has business rule error, it will not
					// be
					// eligible for
					// bifurcation
					doc = defaultBifurcator.bifurcate(doc, bifContext);
					List<ProcessingResult> results = new ArrayList<>();
					if (!defaultBifurcator.isBifurcated(doc)) {
						String docKey = docKeyGen.generateKey(doc);

						String[] errorLocations = new String[] {
								GSTConstants.DOC_NO };
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations);
						List<ProcessingResult> prList = processingResults
								.get(docKey);
						ProcessingResult pr = new ProcessingResult("ASP",
								"ER0501",
								"Transaction cannot be mapped to any of the Tables "
										+ "of the GSTR-7 Return Form",
								location);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Bifurcation for Document" + doc
									+ "  for groupCode " + groupCode);
						}
						// If the document does not have any validation
						// errors,
						// then
						// the prList will be null. In that case we need
						// to
						// create a
						// new list and add the errors to the list.

						if (prList != null) {
							prList.add(pr);
						} else {
							prList = new ArrayList<>();
							prList.add(pr);
							processingResults.put(docKey, prList);
						}
						results.add(pr);
						processingResults.put(docKey, results);
					}

				});

	}

	public List<Gstr7TransDocHeaderEntity> saveDocs(
			List<Gstr7TransDocHeaderEntity> docs,
			DocKeyGenerator<Gstr7TransDocHeaderEntity, String> docKeyGen) {

		// Get all the non-null ids from the list. This will be used to
		// mark the existing documents in the DB as deleted.
		List<Long> docIds = docs.stream()
				.filter(doc -> doc.getId() != null && !doc.isDelete())
				.map(doc -> doc.getId()).collect(Collectors.toList());
		Gstr7TransDocHeaderEntity firstOutwardDoc = docs.get(0);
		// Execute the repository query to udpate the isDelete to true for the
		// above ids. Also mark the updated date to the current date.
		// call the is delete to true repository method.
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"UPDATE_DOC_DELETE_START", CLASS_NAME, "saveDocs", null);
		if (!docIds.isEmpty()) {
			// LocalDateTime updatedDate = LocalDateTime.now();
			LocalDateTime updatedDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			docHeaderRepository.updateDocDeletion(docIds, updatedDate,
					firstOutwardDoc.getCreatedBy());

			docs.forEach(doc -> {
				doc.setGstnError(false);
				doc.setSavedToGstn(false);
				doc.setSentToGstn(false);
			});
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"UPDATE_DOC_DELETE_END", CLASS_NAME, "saveDocs", null);
		/**
		 * Start - Duplicate Document Check
		 */

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SOFT_DELETE_START", CLASS_NAME, "saveDocs", null);

		List<Gstr7TransDocHeaderEntity> activeDocList = docs.parallelStream()
				.filter(doc -> !doc.isDelete()).collect(Collectors.toList());
		if (!activeDocList.isEmpty()) {
			duplicateCheckServiceImpl.softDeleteDupDocs(activeDocList);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SOFT_DELETE_END", CLASS_NAME, "saveDocs", null);
		/**
		 * End - Duplicate Document Check
		 */

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"EINV_PROCESSING_STATUS_END", CLASS_NAME, "saveDocs", null);

		// Attach the document object with the line items.
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOCUMENT_OBJECT_WITH_LINE_ITEM_START", CLASS_NAME, "saveDocs",
				null);

		docs.forEach(document -> {
			String docKey = docKeyGen.generateKey(document);
			LocalDateTime createdDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// document.setCreatedDate(LocalDateTime.now());
			document.setCreatedOn(createdDate);
			document.setDerivedRetPeriod(
					GenUtil.getDerivedTaxPeriod(document.getReturnPeriod()));

			document.setDocKey(docKey);

			document.getLineItems().forEach(item -> {

				item.setDocument(document);
				item.setTaxperiod(document.getReturnPeriod());
				item.setDerivedTaxperiod(document.getDerivedRetPeriod());
			});

		});
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOCUMENT_OBJECT_WITH_LINE_ITEM_END", CLASS_NAME, "saveDocs",
				null);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocSaveService saveDocuments End");
		}

		// save all the documents.
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SAVE_DB_START", CLASS_NAME, "saveDocs", null);
		List<Gstr7TransDocHeaderEntity> outWardTransList = docHeaderRepository
				.saveAll(docs);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SAVE_DB_END", CLASS_NAME, "saveDocs", null);

		return outWardTransList;
	}

	private boolean isError(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey) {

		List<ProcessingResult> results = processingResults.get(docKey);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("results {} for DocKey {}  ", results, docKey);
		}
		if (results != null) {
			return results.stream()
					.anyMatch(r -> r.getType() == ProcessingResultType.ERROR);
		} else {
			return false;
		}
	}

	private boolean isInfo(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey) {
		List<ProcessingResult> results = processingResults.get(docKey);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("results {} for DocKey {}  ", results, docKey);
		}
		if (results != null) {
			return results.stream()
					.anyMatch(r -> r.getType() == ProcessingResultType.INFO);
		} else {
			return false;
		}
	}

	private int errorCount(List<Gstr7TransDocHeaderEntity> savedDocs) {

		List<Gstr7TransDocHeaderEntity> errorDocs = savedDocs.stream()
				.filter(doc -> doc.isError()).collect(Collectors.toList());
		return errorDocs.size();
	}

	private void markDuplicateDocuments(List<Gstr7TransDocHeaderEntity> docs) {

		// Get the map of documents will the keys and values as the list of
		// documents.
		// Map<String, List<OutwardTransDocument>> retResultMap = new
		// HashMap<>();

		Map<String, List<Gstr7TransDocHeaderEntity>> allDocsMap = docs.stream()
				.collect(Collectors
						.groupingBy(doc -> docKeyGen.generateKey(doc)));

		// Filter out the documents that have more than one element in the value
		// list.
		Map<String, List<Gstr7TransDocHeaderEntity>> duplicatesMap = allDocsMap
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

		// Iterate over all the documents in the duplicatesMap and set

		duplicatesMap.entrySet().forEach(entry -> {
			// String key = entry.getKey();
			List<Gstr7TransDocHeaderEntity> value = entry.getValue();
			int lastIndex = value.size() - 1;
			IntStream.range(0, value.size()).forEach(idx -> {
				Gstr7TransDocHeaderEntity item = value.get(idx);

				if (idx != lastIndex) {
					item.setDelete(true);
					item.setError(true);
					// item.setErrCodes(GSTConstants.ER15167);
				}
			});

		});

	}

	private void calculateAndSetTotalTaxAmt(Gstr7TransDocHeaderEntity document,
			Gstr7TransDocItemEntity item) {
		BigDecimal docIgstAmt = document.getIgstAmt();
		BigDecimal docCgstAmt = document.getCgstAmt();
		BigDecimal docSgstAmt = document.getSgstAmt();
		if (docIgstAmt == null) {
			docIgstAmt = BigDecimal.ZERO;
		}
		if (docCgstAmt == null) {
			docCgstAmt = BigDecimal.ZERO;
		}
		if (docSgstAmt == null) {
			docSgstAmt = BigDecimal.ZERO;
		}

		// Set Igst Amount to Document Header
		if (item.getIgstAmt() != null) {
			docIgstAmt = docIgstAmt.add(item.getIgstAmt());
			document.setIgstAmt(docIgstAmt);
		}
		// Set Cgst Amount to Document Header
		if (item.getCgstAmt() != null) {
			docCgstAmt = docCgstAmt.add(item.getCgstAmt());
			document.setCgstAmt(docCgstAmt);
		}
		// Set Sgst Amount to Document Header
		if (item.getSgstAmt() != null) {
			docSgstAmt = docSgstAmt.add(item.getSgstAmt());
			document.setSgstAmt(docSgstAmt);
		}

	}

	private void populateErrorCodeAndErrorDescription(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey, Gstr7TransDocHeaderEntity doc) {
		List<String> HeaderErrorList = new ArrayList<>();
		List<String> HeaderInfoList = new ArrayList<>();
		List<ProcessingResult> errors = processingResults.get(docKey);
		Map<Integer, List<String>> aitemErrorMap = new HashMap<>();
		Map<Integer, List<String>> aiteInfoMap = new HashMap<>();
		for (ProcessingResult error : errors) {

			TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
					.getLocation();
			if (loc == null) {
				if (ProcessingResultType.INFO.equals(error.getType())) {
					HeaderInfoList.add(error.getCode());
				} else {
					HeaderErrorList.add(error.getCode());
				}
			}

			else {
				Integer lineNo = loc.getLineNo();
				if (lineNo == null) {
					if (ProcessingResultType.INFO.equals(error.getType())) {
						HeaderInfoList.add(error.getCode());
					} else {
						HeaderErrorList.add(error.getCode());
					}
				} else {
					if (ProcessingResultType.INFO.equals(error.getType())) {
						aiteInfoMap
								.computeIfAbsent(lineNo, k -> new ArrayList<>())
								.add(error.getCode());
					} else {
						aitemErrorMap
								.computeIfAbsent(lineNo, k -> new ArrayList<>())
								.add(error.getCode());
					}
				}
			}
		}
		if (HeaderErrorList != null && HeaderErrorList.size() > 0) {
			doc.setErrorCodes(
					HeaderErrorList.stream().collect(Collectors.joining(",")));
		}
		if (HeaderInfoList != null && HeaderInfoList.size() > 0) {
			// doc.setInfoCodes(
			// HeaderInfoList.stream().collect(Collectors.joining(",")));
		}
		if (aitemErrorMap != null && aitemErrorMap.size() > 0) {
			IntStream.range(0, doc.getLineItems().size()).forEach(idx -> {
				Gstr7TransDocItemEntity item = doc.getLineItems().get(idx);
				if (aitemErrorMap.get(idx) != null) {
					item.setErrorCodes(aitemErrorMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}

	}

	private void settingFiledGstins(ProcessingContext context) {
		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR7", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {

			filedSet.add(entity.getGstin() + PIPE + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	public void convertCalcConfigAndSetValues(
			List<Gstr7TransDocHeaderEntity> documents, String sourceId,
			String headerPayloadId) {

		documents.forEach(document -> {

			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					calculateAndSetTotalTaxAmt(document, item);
				});

			}
		});
	}

	private Map<String, List<OutwardTransDocError>> convertErrors(
			Map<String, List<ProcessingResult>> results,
			Map<String, Gstr7TransDocHeaderEntity> errDocMap) {
		Map<String, List<OutwardTransDocError>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			Gstr7TransDocHeaderEntity outwardTransDocument = errDocMap.get(key);

			List<OutwardTransDocError> errors = new ArrayList<>();
			pResults.forEach(pr -> {
				// Instantiate the ent
				OutwardTransDocError error = new OutwardTransDocError();
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
						.getLocation();
				if (null != loc) { // In case of bifurcation failure, loc is
									// null
					Object[] arr = loc.getFieldIdentifiers();
					if (arr != null) {
						String[] fields = Arrays.copyOf(arr, arr.length,
								String[].class);
						String errField = StringUtils.join(fields, ',');
						error.setItemIndex(loc.getLineNo());
						if (loc.getLineNo() != null) {
							Integer lineNo = loc.getLineNo();
							Integer itemNo = outwardTransDocument
									.getItemNoForIndex(lineNo);
							error.setItemNum(itemNo);
						}
						error.setErrorField(errField);
					}
				}
				error.setErrorCode(pr.getCode());
				error.setErrorDesc(pr.getDescription());
				error.setErrorType(ProcessingResultType.ERROR == pr.getType()
						? "ERR" : "INFO");
				if (ProcessingResultType.ERROR == pr.getType()) {
					error.setType(GSTConstants.BUSINESS_VALIDATIONS);
				}
				error.setSource("ASP");
				errors.add(error);
			});
			map.put(key, errors);
		});
		return map;
	}

	private Map<String, List<ProcessingResult>> canDocLookUp(
			List<Gstr7TransDocHeaderEntity> docs, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_START", CLASS_NAME,
				"CanDocLookUp", null);

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<Object[]> orgDocDetails = new ArrayList<>();

			List<Gstr7TransDocHeaderEntity> canDocs = docs.stream()
					.filter(doc -> GSTConstants.CAN.equalsIgnoreCase(
							doc.getSupplyType()) && !doc.isDelete())
					.collect(Collectors.toList());

			canDocs.forEach(doc -> docKeySet.add(docKeyGen.generateKey(doc)));
			docKeys = new ArrayList<>(docKeySet);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("To check cancel Invoices The number of dockeys "
						+ "recieved from the iteration is : " + docKeys.size());
			}

			Config config = configManager.getConfig("EYInternal",
					"outward.save.chunksize");
			String chnkSizeStr = config != null ? config.getValue() : "2000";
			int chunkSize = Integer.parseInt(chnkSizeStr);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated DocKeys : %s, Size is %d,"
								+ " About to chunk the Dockeys list in org doc check....",
						docKeys, docKeys.size());
				LOGGER.debug(msg);
			}

			List<List<String>> docKeyChunks = Lists.partition(docKeys,
					chunkSize);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated %d Chunks of DocKeys of each Size is %d in org doc check",
						docKeyChunks.size(), chunkSize);
				LOGGER.debug(msg);
			}

			if (!docKeyChunks.isEmpty()) {
				docKeyChunks.forEach(chunk -> orgDocDetails
						.addAll(docHeaderRepository.findOrgDocByDocKey(chunk)));
			}

			Map<String, Boolean> orgDocKeyMap = orgDocDetails.stream()
					.collect(Collectors.toMap(obj -> String.valueOf(obj[0]),
							obj -> (Boolean) obj[1], (obj1, obj2) -> obj1));

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"The number of original records fetch from db is : "
								+ orgDocDetails.size());
			}

			canDocs.forEach(doc -> {
				List<ProcessingResult> results = new ArrayList<>();
				String docKey = docKeyGen.generateKey(doc);
				Boolean isOriginalDocPresent = orgDocKeyMap.get(docKey);
				if (isOriginalDocPresent == null) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());
					results.add(new ProcessingResult(APP_VALIDATION, "ER63046",
							"Document cannot be cancelled as the "
									+ "same was not reported to ASP System",
							location));
				}
				if (!results.isEmpty()) {
					Boolean complianceError = results.stream().anyMatch(
							r -> r.getType() == ProcessingResultType.ERROR);
					if (complianceError) {
						doc.setProcessed(false);
						doc.setError(true);
				}
				}
				retResultMap.put(docKey, results);

			});
		} catch (

		Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ "orginal doccument for CAN invoices : Exception "
					+ "is : ", ex);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_END", CLASS_NAME,
				"CanDocLookUp", null);

		return retResultMap;

	}

}
