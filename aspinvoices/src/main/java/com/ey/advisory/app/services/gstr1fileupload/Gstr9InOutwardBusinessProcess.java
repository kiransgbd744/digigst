package com.ey.advisory.app.services.gstr1fileupload;

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
import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService2;
import com.ey.advisory.app.services.docs.SRFileToGstr9InOutwardConvertion;
import com.ey.advisory.app.services.validation.gstr9.inoutward.Gstr9InOutwardValidationChain;
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

@Component("Gstr9InOutwardBusinessProcess")
@Slf4j
public class Gstr9InOutwardBusinessProcess {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr9UserInputRepository")
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToGstr9InOutwardConvertion")
	private SRFileToGstr9InOutwardConvertion sRFileToGstr9InOutwardConvertion;

	@Autowired
	@Qualifier("Gstr9InOutwardValidationChain")
	private Gstr9InOutwardValidationChain gstr9InOutwardValidationChain;

	public void processGstr9InOutwardBusinessPData(
			List<Gstr9OutwardInwardAsEnteredEntity> strProcessRecords,
			List<Gstr9OutwardInwardAsEnteredEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr9OutwardInwardAsEnteredEntity> businessErrorRecords = new ArrayList<>();
		List<Gstr9OutwardInwardAsEnteredEntity> infoProcessed = new ArrayList<>();
		List<Gstr9OutwardInwardAsEnteredEntity> busProcessRecords = new ArrayList<>();
		List<Gstr9UserInputEntity> saveAll = new ArrayList<>();

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

		for (Gstr9OutwardInwardAsEnteredEntity gstr9InOutward : strProcessRecords) {
			List<ProcessingResult> results = gstr9InOutwardValidationChain
					.validate(gstr9InOutward, null);
			Long id = gstr9InOutward.getId();
			if (!results.isEmpty()) {
				String key = gstr9InOutward.getGst9DocKey();
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
					if (!errorType.isEmpty()) {
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
					if (!errorType.isEmpty()) {
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

		for (Gstr9OutwardInwardAsEnteredEntity process : strProcessRecords) {
			String key = process.getGst9DocKey();
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
				businessErrorRecords.add(process);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("businessErrorRecords ", businessErrorRecords.size());
			LOGGER.debug("businessProcessedRecords ", busProcessRecords.size());
		}
		if (!businessValErrors.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService.storedErrorGstr9InOutwardRecords(
					businessErrorRecords, errorMap);
		}

		if (!infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedErrorGstr9InOutwardRecords(infoProcessed, errorMap);
		}

		List<String> existProcessData = new ArrayList<>();
		if (!busProcessRecords.isEmpty()) {
			List<Gstr9UserInputEntity> processedOfBusi = sRFileToGstr9InOutwardConvertion
					.convertSRFileToGstr9InOut(busProcessRecords,
							updateFileStatus);

			for (Gstr9UserInputEntity interestUpdate : processedOfBusi) {
				String gstr9InOutwardKey = interestUpdate.getDocKey();
				existProcessData.add(gstr9InOutwardKey);
			}

			if (!existProcessData.isEmpty()) {
				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(existProcessData, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(chunk -> docIds
							.addAll(gstr9UserInputRepository
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
						gstr9UserInputRepository
								.updateDuplicateDocDeletionByDocKeys(docIdChunk,
										LocalDateTime.now(),
										updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							String log = "Documents soft deleted successfully";
							LOGGER.debug(log);
						}
					});
				}
			}

			saveAll = gstr9UserInputRepository.saveAll(processedOfBusi);

		}

		processedRecords = (!saveAll.isEmpty()) ? saveAll.size() : 0;
		errorRecords = (!businessErrorRecords.isEmpty() || !strErrRecords.isEmpty())
				? businessErrorRecords.size() + strErrRecords.size() : 0;
		totalRecords = processedRecords + errorRecords;
		information = (!infoProcessed.isEmpty()) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);
	}
}