package com.ey.advisory.app.services.annexure1fileupload;

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
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.CrossItcAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.CrossItcProcessEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.CrossItcProcessRepository;
import com.ey.advisory.app.services.docs.SRFileToCrossItcDetailsConvertion;
import com.ey.advisory.app.services.validation.crossitc.CrossItcValidationChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.SecurityContext;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("CrossItcBusinessErrorUploadService")
@Slf4j
public class CrossItcBusinessErrorUploadService {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToCrossItcDetailsConvertion")
	private SRFileToCrossItcDetailsConvertion sRFileToCrossItcDetailsConvertion;

	@Autowired
	@Qualifier("CrossItcValidationChain")
	private CrossItcValidationChain crossItcValidationChain;

	@Autowired
	@Qualifier("CrossItcProcessRepository")
	private CrossItcProcessRepository crossItcProcessRepository;

	public void processBusinessData(List<Object[]> cewbList,
			List<CrossItcAsEnteredEntity> strProcessRecords,
			List<CrossItcAsEnteredEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";

		List<CrossItcAsEnteredEntity> businessErrorRecords = new ArrayList<>();
		List<CrossItcAsEnteredEntity> infoProcessed = new ArrayList<>();
		List<CrossItcAsEnteredEntity> busProcessRecords = new ArrayList<>();

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

		for (CrossItcAsEnteredEntity cewb : strProcessRecords) {
			List<ProcessingResult> results = crossItcValidationChain.validate(cewb,
					null);
			Long id = cewb.getId();
			if (!results.isEmpty()) {
				String key = cewb.getCrossItcDocKey();
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

		for (CrossItcAsEnteredEntity process : strProcessRecords) {
			String key = process.getCrossItcDocKey();
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
		if (businessErrorRecords.size() > 0
				&& !businessErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorCrossItcRecords(businessErrorRecords, errorMap);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorCrossItcRecords(infoProcessed, errorMap);
		}

		if (busProcessRecords.size() > 0 && !busProcessRecords.isEmpty()) {

			List<CrossItcProcessEntity> crossItc = sRFileToCrossItcDetailsConvertion
					.convertSRFileToCrossItc(busProcessRecords,
							updateFileStatus);

			List<String> existProcessData = new ArrayList<>();
			for (CrossItcProcessEntity crossItcProcessed : crossItc) {
				String b2csInvKey = crossItcProcessed.getCrossItcDocKey();
				existProcessData.add(b2csInvKey);
			}
			if (existProcessData.size() > 0 && !existProcessData.isEmpty()) {

				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(existProcessData, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(
							chunk -> docIds.addAll(crossItcProcessRepository
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
						crossItcProcessRepository
								.updateDuplicateDocDeletionByDocKeys(docIdChunk,
										LocalDateTime.now(),
										updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Documents soft deleted successfully");
						}
					});
				}
			}
			crossItcProcessRepository.saveAll(crossItc);
		}

		totalRecords = (cewbList.size() != 0) ? cewbList.size() : 0;
		errorRecords = (businessErrorRecords.size() != 0
				|| strErrRecords.size() != 0)
						? businessErrorRecords.size() + strErrRecords.size()
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