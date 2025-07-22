package com.ey.advisory.app.services.doc.gstr1a;

import static com.ey.advisory.common.GSTConstants.BUSINESS_VALIDATIONS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AInvoiceFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AAsEnteredInvoiceRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.validation.gstr1a.InvoiceFile.Gstr1AInvoiceFileValidationChain;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AInvoiceBusinessService")
@Slf4j
public class Gstr1AInvoiceBusinessService {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1InvoiceRepository;

	@Autowired
	@Qualifier("Gstr1AInvoiceFileValidationChain")
	private Gstr1AInvoiceFileValidationChain invoiceFileValidationChain;

	@Autowired
	@Qualifier("Gstr1AAsEnteredInvoiceRepository")
	private Gstr1AAsEnteredInvoiceRepository gstr1AsEnteredInvoiceRepository;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToGstr1AInvoiceDetailsConvertion")
	private SRFileToGstr1AInvoiceDetailsConvertion sRFileToInvoiceDetailsConvertion;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	private static final String CLASS_NAME = "Gstr1InvoiceBusinessService";
	private static final String DOC_KEY_JOINER = "|";

	public void processBusinessData(List<Object[]> listOfInvoices,
			List<Gstr1AAsEnteredInvEntity> strProcessRecords,
			List<Gstr1AAsEnteredInvEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AAsEnteredInvEntity> busErrorRecords = new ArrayList<>();
		List<Gstr1AAsEnteredInvEntity> infoProcessed = new ArrayList<>();
		List<Gstr1AAsEnteredInvEntity> busProcessRecords = new ArrayList<>();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();

		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();
		List<ProcessingResult> current = null;

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_PROCESS BUSINESS RULES_START", CLASS_NAME,
				"processBusinessData", null);
		ProcessingContext context = new ProcessingContext();
		settingFiledGstins(context);
		gstinInfocache(context);
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();
		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);
		for (Gstr1AAsEnteredInvEntity invoice : strProcessRecords) {
			invoice.setEntityId(gstinAndEntityMap.get(invoice.getSgstin()));
			invoice.setEntityConfigParamMap(map);
			List<ProcessingResult> results = invoiceFileValidationChain
					.validate(invoice, context);
			Long id = invoice.getId();
			if (results != null && !results.isEmpty()) {
				String key = invoice.getInvoiceKey();
				String keys = key.concat(GSTConstants.SLASH)
						.concat(id.toString());
				List<ProcessingResultType> listTypes = new ArrayList<>();
				for (ProcessingResult types : results) {
					ProcessingResultType type = types.getType();
					listTypes.add(type);
				}
				List<String> errorType = listTypes.stream()
						.map(object -> Objects.toString(object, null))
						.collect(Collectors.toList());

				current = businessValErrors.get(keys);
				if (current == null) {
					current = new ArrayList<>();
					if (errorType != null && !errorType.isEmpty()) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(key);
							errorKeys.add(key);
							businessValErrors.put(keys, results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors.put(keys, results);
						} else {
							infoWithProcessed.put(keys, results);
							infoKeys.add(key);
						}
					}
				} else {
					if (errorType != null && !errorType.isEmpty()) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(key);
							errorKeys.add(key);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else {
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
							infoKeys.add(key);
						}
					}
				}
			}
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_PROCESS BUSINESS RULES_END", CLASS_NAME,
				"processBusinessData", null);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SEPERATING_BUSINESS ERROR_AND_PROCESS RECORDS_START",
				CLASS_NAME, "processBusinessData", null);
		for (Gstr1AAsEnteredInvEntity process : strProcessRecords) {
			String key = process.getInvoiceKey();
			if (!errorKeys.contains(key)) {
				if (infoKeys != null && infoKeys.contains(key)) {
					process.setInfo(true);
					infoProcessed.add(process);
				}
				busProcessRecords.add(process);

			} else {
				if (infoKeys != null && infoKeys.contains(key)) {
					process.setInfo(true);
				}
				process.setError(true);
				busErrorRecords.add(process);
			}
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SEPERATING_BUSINESS ERROR_AND_PROCESS RECORDS_END",
				CLASS_NAME, "processBusinessData", null);

		LOGGER.error("businessErrorRecords ", busErrorRecords.size());
		LOGGER.error("businessProcessedRecords ", busProcessRecords.size());

		if (!busErrorRecords.isEmpty()) {
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS ERROR RECORDS CONVERSION_START",
					CLASS_NAME, "processBusinessData", null);
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS ERROR RECORDS CONVERSION_END",
					CLASS_NAME, "processBusinessData", null);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS ERROR RECORDS DB SAVE START",
					CLASS_NAME, "processBusinessData", null);
			verticalWebUploadErrorService
					.storedErrorGstr1InvRecords(busErrorRecords, errorMap);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS ERROR RECORDS DB SAVE END",
					CLASS_NAME, "processBusinessData", null);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS INFO RECORDS CONVERSION_START",
					CLASS_NAME, "processBusinessData", null);
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS INFO RECORDS CONVERSION_END",
					CLASS_NAME, "processBusinessData", null);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS INFO RECORDS DB SAVE START",
					CLASS_NAME, "processBusinessData", null);
			verticalWebUploadErrorService
					.storedErrorGstr1InvRecords(infoProcessed, errorMap);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS INFO RECORDS DB SAVE END",
					CLASS_NAME, "processBusinessData", null);
		}

		if (!busProcessRecords.isEmpty()) {

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS PROCESS  RECORDS ENTITY CONVERSION START",
					CLASS_NAME, "processBusinessData", null);

			List<Gstr1AInvoiceFileUploadEntity> invoiceDoc = sRFileToInvoiceDetailsConvertion
					.convertSRFileToInvoiceDoc(busProcessRecords,
							updateFileStatus);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ BUSINESS PROCESS  RECORDS ENTITY CONVERSION END",
					CLASS_NAME, "processBusinessData", null);
			// List<String> existProcessData = new ArrayList<>();
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ UPDATING EXISTING ENTRIES START",
					CLASS_NAME, "Updating existing entries", null);

			List<String> existProcessData = invoiceDoc.parallelStream()
					.map(Gstr1AInvoiceFileUploadEntity::getInvoiceKey)
					.distinct().collect(Collectors.toList());
			Set<String> gstinAndReturnPeriodSet = new HashSet<>();
			invoiceDoc.parallelStream()
					.forEach(dto -> gstinAndReturnPeriodSet.add(dto.getSgstin()
							+ DOC_KEY_JOINER + dto.getReturnPeriod()));
			gstinAndReturnPeriodSet.stream().forEach(x -> {
				gstr1InvoiceRepository.updateExistingEntries(
						x.split(DOC_KEY_JOINER)[0], x.split(DOC_KEY_JOINER)[1],
						"C", "EXCEL_UPLOAD");
			});

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ UPDATING EXISTING ENTRIES END", CLASS_NAME,
					"Updating existing entries", null);
			if (!existProcessData.isEmpty()) {
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"INVOICE_SERIES_ SOFT DELETE START", CLASS_NAME,
						"Updating existing entries", null);
				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(existProcessData, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(
							chunk -> docIds.addAll(gstr1InvoiceRepository
									.findActiveDocsByDocKeys(chunk)));
				}
				if (!docIds.isEmpty()) {
					List<List<Long>> docIdChunks = Lists.partition(docIds,
							2000);
					docIdChunks.forEach(docIdChunk -> {
						if (LOGGER.isDebugEnabled()) {
							String msg = String
									.format("List of DocIds which are about to get "
											+ "soft delete: %s", docIdChunk);
							LOGGER.debug(msg);
						}
						gstr1InvoiceRepository
								.updateDuplicateDocDeletionByDocKeys(docIdChunk,
										LocalDateTime.now(),
										updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Documents soft deleted successfully");
						}
					});
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"INVOICE_SERIES_ SOFT DELETE END", CLASS_NAME,
						"Updating existing entries", null);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ SAVING RECORDS RO PROCESS TABLE START",
					CLASS_NAME, "Updating existing entries", null);
			gstr1InvoiceRepository.saveAll(invoiceDoc);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ SAVING RECORDS RO PROCESS TABLE END",
					CLASS_NAME, "Updating existing entries", null);
		}

		totalRecords = listOfInvoices.size();
		errorRecords = busErrorRecords.size() + strErrRecords.size();
		processedRecords = totalRecords - errorRecords;
		information = infoProcessed.size();
		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	private void settingFiledGstins(ProcessingContext context) {
		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR1", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {

			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	private void gstinInfocache(ProcessingContext context) {
		List<GSTNDetailEntity> findDetails = gstinInfoRepository.findDetails();

		Map<String, String> gstinInfoMap = findDetails.stream()
				.collect(Collectors.toMap(GSTNDetailEntity::getGstin,
						GSTNDetailEntity::getRegistrationType));
		context.seAttribute("gstinInfoMap", gstinInfoMap);
	}

}