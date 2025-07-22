package com.ey.advisory.app.services.gstr1afileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAdvanceAdjustmentFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AATARepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.doc.gstr1a.SRFileToGstr1ATxpdDetailsConvertion;
import com.ey.advisory.app.services.validation.gstr1a.advanceAdjusted.Gstr1AAdvancedAdjustmentBusinessChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr1aTxpdBusinessProcess")
@Slf4j
public class Gstr1aTxpdBusinessProcess {

	@Autowired
	@Qualifier("Gstr1AATARepository")
	private Gstr1AATARepository gstr1ATARepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1AAdvancedAdjustmentBusinessChain")
	private Gstr1AAdvancedAdjustmentBusinessChain advancedAdjustmentBusinessChain;

	@Autowired
	@Qualifier("SRFileToGstr1ATxpdDetailsConvertion")
	private SRFileToGstr1ATxpdDetailsConvertion sRFileToATADetailsConvertion;

	public void processTxpdBusinessData(List<Object[]> listOfAtas,
			List<Gstr1AAsEnteredTxpdFileUploadEntity> strProcessRecords,
			List<Gstr1AAsEnteredTxpdFileUploadEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr1AAsEnteredTxpdFileUploadEntity> busErrorRecords = new ArrayList<>();
		List<Gstr1AAsEnteredTxpdFileUploadEntity> infoProcessed = new ArrayList<>();
		List<Gstr1AAsEnteredTxpdFileUploadEntity> busProcessRecords = new ArrayList<>();

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
		List<Gstr1AAsEnteredTxpdFileUploadEntity> strProcess = sRFileToATADetailsConvertion
				.convertSRFileToTxpd(strProcessRecords, updateFileStatus);
		for (Gstr1AAsEnteredTxpdFileUploadEntity advanceAdjust : strProcess) {
			List<ProcessingResult> results = advancedAdjustmentBusinessChain
					.validate(advanceAdjust, null);
			Long id = advanceAdjust.getId();
			if (results != null && results.size() > 0) {
				String key = advanceAdjust.getTxpdInvKey();
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
					if (errorType.size() > 0) {
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
					if (errorType.size() > 0) {
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
		for (String infoKey : infoWithProcessed.keySet()) {
			infoKeys.add(infoKey);
		}

		for (Gstr1AAsEnteredTxpdFileUploadEntity process : strProcessRecords) {
			String key = process.getTxpdInvKey();
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
		LOGGER.error("businessErrorRecords ", busErrorRecords.size());
		LOGGER.error("businessProcessedRecords ", busProcessRecords.size());
		if (busErrorRecords.size() > 0 && !busErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1TxpdRecords(busErrorRecords, errorMap);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1TxpdRecords(infoProcessed, errorMap);
		}

		if (busProcessRecords.size() > 0 && !busProcessRecords.isEmpty()) {

			List<Gstr1AAdvanceAdjustmentFileUploadEntity> b2csDoc = sRFileToATADetailsConvertion
					.convertSRFileToAtaDoc(busProcessRecords, updateFileStatus);

			List<String> existProcessData = new ArrayList<>();
			for (Gstr1AAdvanceAdjustmentFileUploadEntity b2cProcessed : b2csDoc) {
				String txpdInvKey = b2cProcessed.getTxpdInvKey();
				existProcessData.add(txpdInvKey);
			}
			if (existProcessData.size() > 0 && !existProcessData.isEmpty()) {

				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(existProcessData, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(chunk -> docIds.addAll(
							gstr1ATARepository.findActiveDocsByDocKeys(chunk)));
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
						gstr1ATARepository.updateDuplicateDocDeletionByDocKeys(
								docIdChunk, LocalDateTime.now(),
								updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Documents soft deleted successfully");
						}
					});
				}
			}

			gstr1ATARepository.saveAll(b2csDoc);
		}

		totalRecords = (listOfAtas.size() != 0) ? listOfAtas.size() : 0;
		errorRecords = (busErrorRecords.size() != 0
				|| strErrRecords.size() != 0)
						? busErrorRecords.size() + strErrRecords.size() : 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}
}