package com.ey.advisory.app.services.gstr1afileupload;

import static com.ey.advisory.common.GSTConstants.BUSINESS_VALIDATIONS;

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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AB2csDetailsEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AB2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSAsEnteredRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSGstnRepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.doc.gstr1a.SRFileToGstr1AB2CSDetailsConvertion;
import com.ey.advisory.app.services.validation.gstr1a.b2cs.Gstr1AB2csValidationChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Gstr1AB2csBusinessProcess")
@Slf4j
public class Gstr1AB2csBusinessProcess {

	@Autowired
	@Qualifier("Gstr1AB2csValidationChain")
	private Gstr1AB2csValidationChain b2csValidationChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1B2CSAsEnteredRepository")
	private Gstr1B2CSAsEnteredRepository gstr1B2CSAsEnteredRepository;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToGstr1AB2CSDetailsConvertion")
	private SRFileToGstr1AB2CSDetailsConvertion sRFileToB2CSDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1AB2CSRepository")
	private Gstr1AB2CSRepository gstr1AB2CSRepository;

	@Autowired
	@Qualifier("Gstr1B2CSGstnRepository")
	private Gstr1B2CSGstnRepository gstr1B2CSGstnRepository;

	public void processBusinessData(List<Object[]> listOfB2cs,
			List<Gstr1AAsEnteredB2csEntity> strucProcessedRecords,
			List<Gstr1AAsEnteredB2csEntity> structuralErrorsRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AAsEnteredB2csEntity> busErrorRecords = new ArrayList<>();
		List<Gstr1AAsEnteredB2csEntity> infoProcessed = new ArrayList<>();
		List<Gstr1AAsEnteredB2csEntity> busProcessRecords = new ArrayList<>();

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
		List<Gstr1AAsEnteredB2csEntity> strucProcessed = sRFileToB2CSDetailsConvertion
				.convertSRFileToOutward(strucProcessedRecords,
						updateFileStatus);
		for (Gstr1AAsEnteredB2csEntity b2cs : strucProcessed) {
			List<ProcessingResult> results = b2csValidationChain.validate(b2cs,
					null);
			Long id = b2cs.getId();
			if (results != null && results.size() > 0) {
				String key = b2cs.getInvB2csKey();
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

		for (Gstr1AAsEnteredB2csEntity process : strucProcessedRecords) {
			String key = process.getInvB2csKey();
			if (!errorKeys.contains(key)) {
				if (infoKeys != null && infoKeys.contains(key)) {
					process.setInfo(true);
					infoProcessed.add(process);
				}
				busProcessRecords.add(process);
			} else {
				if (errorInfo != null && errorInfo.contains(key)) {
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
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1B2csRecords(busErrorRecords, errorMap);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1B2csRecords(infoProcessed, errorMap);
		}

		if (busProcessRecords.size() > 0 && !busProcessRecords.isEmpty()) {

			List<Gstr1AB2csDetailsEntity> b2csDoc = sRFileToB2CSDetailsConvertion
					.convertSRFileToOutwardTransDoc(busProcessRecords,
							updateFileStatus);

			List<String> existProcessData = new ArrayList<>();
			for (Gstr1AB2csDetailsEntity b2cProcessed : b2csDoc) {
				String b2csInvKey = b2cProcessed.getInvB2csKey();
				existProcessData.add(b2csInvKey);
			}
			if (existProcessData.size() > 0 && !existProcessData.isEmpty()) {

				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(existProcessData, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks
							.forEach(chunk -> docIds.addAll(gstr1AB2CSRepository
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
						gstr1AB2CSRepository
								.updateDuplicateDocDeletionByDocKeys(docIdChunk,
										LocalDateTime.now(),
										updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Documents soft deleted successfully");
						}
					});
				}
			}
			gstr1AB2CSRepository.saveAll(b2csDoc);
		}

		totalRecords = (listOfB2cs.size() != 0) ? listOfB2cs.size() : 0;
		errorRecords = (busErrorRecords.size() != 0
				|| structuralErrorsRecords.size() != 0)
						? busErrorRecords.size()
								+ structuralErrorsRecords.size()
						: 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}
}