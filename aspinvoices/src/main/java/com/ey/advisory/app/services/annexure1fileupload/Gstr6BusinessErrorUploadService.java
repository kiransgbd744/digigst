
package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.BUSINESS_VALIDATIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6DistributionExcelRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.services.docs.Gstr6DistrbtnDataConverter;
import com.ey.advisory.app.services.docs.SRFileToGstr6DistbtnDetailsConvertion;
import com.ey.advisory.app.services.structuralvalidation.gstr6.Gstr6BusinessValidationChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Component("Gstr6BusinessErrorUploadService")
public class Gstr6BusinessErrorUploadService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6BusinessErrorUploadService.class);

	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	private Gstr6DistributionRepository outwardB2cRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr6DistrbtnDataConverter")
	private Gstr6DistrbtnDataConverter sRFileToOutwardB2CDetailsConvertion;

	@Autowired
	@Qualifier("Gstr6VerticalWebUploadErrorService")
	private Gstr6VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("DistriButionCanServiceImpl")
	private DistriButionCanService canService;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("B2cBusinessValidationChain") private
	 * B2cBusinessValidationChain b2cBusinessValidationChain;
	 */

	@Autowired
	@Qualifier("Gstr6BusinessValidationChain")
	Gstr6BusinessValidationChain b2cBusinessValidationChain;

	@Autowired
	@Qualifier("Gstr6DistributionExcelRepository")
	private Gstr6DistributionExcelRepository outwardB2cExcelRepository;

	@Autowired
	@Qualifier("SRFileToGstr6DistbtnDetailsConvertion")
	SRFileToGstr6DistbtnDetailsConvertion sRFileToGstr6DetailsConvertion;
	
	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;
	
	private static final String PIPE = "|";

	@SuppressWarnings("unused")
	public void processBusinessData(List<Object[]> listOfB2c,
			List<Gstr6DistributionExcelEntity> strucProcessedRecords,
			List<Gstr6DistributionExcelEntity> structuralErrors,
			Gstr1FileStatusEntity updateFileStatus, ProcessingContext context) {

		List<Gstr6DistributionExcelEntity> businessErrors = new ArrayList<>();
		List<Gstr6DistributionExcelEntity> infoProcessed = new ArrayList<>();
		List<Gstr6DistributionExcelEntity> businessProcessed = new ArrayList<>();
		List<Gstr6DistributionExcelEntity> addFile = new ArrayList<>();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();

		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();
		List<Gstr6DistributionExcelEntity> strucProcessed = sRFileToOutwardB2CDetailsConvertion
				.convertProduct(strucProcessedRecords, updateFileStatus);
		List<ProcessingResult> current = null;
		for (Gstr6DistributionExcelEntity gstr6Dist : strucProcessed) {

			// business Rules Implementation Chain

			List<ProcessingResult> results = b2cBusinessValidationChain
					.validate(gstr6Dist, null);

			// .validate(gstr6Dist, null);
			// String key = null;
			Long id = gstr6Dist.getId();
			// List<ProcessingResult> results = null;
			if (results != null && results.size() > 0) {
				String key = gstr6Dist.getProcessKey();

				String keys = key.concat(GSTConstants.SLASH)
						.concat(id.toString());

				List<ProcessingResultType> listTypes = new ArrayList<>();
				for (ProcessingResult types : results) {
					ProcessingResultType type2 = types.getType();
					listTypes.add(type2);
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
							/*
							 * outwardB2cExcelRepository.b2cUpdateErrorInfo(key,
							 * id);
							 */
							errorInfo.add(key);
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
							/*
							 * outwardB2cExcelRepository.b2cUpdateErrorInfo(key,
							 * id);
							 */
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

		Map<String, List<ProcessingResult>> processingResults = new HashMap<>(
				businessValErrors);
		if (LOGGER.isDebugEnabled()) {
			for (Gstr6DistributionExcelEntity doc : strucProcessedRecords) {
				LOGGER.debug("Processed Records before CAN");
				LOGGER.debug(doc.toString());
				LOGGER.debug(processingResults.toString());
			}
		}
		settingFiledGstins(context);
		Map<String, List<ProcessingResult>> distriButionCanLookUp = canService
				.DistriButionCanLookUp(strucProcessed, context);

		distriButionCanLookUp
				.forEach((key, value) -> processingResults.merge(key, value,
						(v1, v2) -> Stream.of(v1, v2).flatMap(x -> x.stream())
								.collect(Collectors.toList())));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Distribution CAN");
			LOGGER.debug(distriButionCanLookUp.toString());
	}
		if (!distriButionCanLookUp.isEmpty()) {
			distriButionCanLookUp.entrySet().forEach(entry -> {

				String key = entry.getKey();
				String errkey = key.substring(0, key.lastIndexOf('-'));
				errorKeys.add(errkey);
				if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Error Keys loop");
						LOGGER.debug(errkey);
						LOGGER.debug(errorKeys.toString());
				}
			});
		}

		List<String> busiProcessedKeys = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			for (Gstr6DistributionExcelEntity doc : strucProcessedRecords) {
				LOGGER.debug("Processed Records After CAN");
				LOGGER.debug(doc.toString());
				LOGGER.debug("Processing Result");
				LOGGER.debug(processingResults.toString());
				LOGGER.debug("Error Keys");
				LOGGER.debug(errorKeys.toString());
			}
		}

		for (Gstr6DistributionExcelEntity process : strucProcessedRecords) {
			String key = process.getProcessKey();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Key loop");
				LOGGER.debug(key);
				LOGGER.debug(errorKeys.toString());
		}
			if (!errorKeys.contains(key)) {
				if (infoKeys != null && infoKeys.contains(key)) {
					process.setInfo(true);
					infoProcessed.add(process);
				}
				businessProcessed.add(process);
			} else{
				if (errorKeys != null && !errorKeys.contains(key)) {
					process.setInfo(true);
				}
				process.setError(true);
				businessErrors.add(process);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("business Processed");
				LOGGER.debug(businessProcessed.toString());
				LOGGER.debug("business Errors");
				LOGGER.debug(businessErrors.toString());
		}
		}
			
		/*	if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
				
			}
		}
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
		
		
		*/
		
		/*if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			outwardB2cExcelRepository.b2cUpdateErrorInfo(errorInfo, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			outwardB2cExcelRepository.b2cUpdateStructuralError(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			outwardB2cExcelRepository.b2cUpdateInfo(infoKeys, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrors = outwardB2cExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = outwardB2cExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}*/
	/*	if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessed = outwardB2cExcelRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}
*/
		LOGGER.error("businessErrorRecords ", businessErrors.size());
		LOGGER.error("businessProcessedRecords ", businessProcessed.size());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("business Processed Records After for Loop");
			LOGGER.debug(businessProcessed.toString());
			LOGGER.debug("business Errors Records After for Loop");
			LOGGER.debug(businessErrors.toString());
	}
		if (businessErrors != null || !businessErrors.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr6VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults, BUSINESS_VALIDATIONS,
							updateFileStatus);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Error Map Before Storing");
				LOGGER.debug(errorMap.toString());
				LOGGER.debug("business Errors Records After for Loop");
				LOGGER.debug(businessErrors.toString());
		}
			verticalWebUploadErrorService.storedErrorRecords(businessErrors,
					errorMap);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Error Map After Storing");
				LOGGER.debug(errorMap.toString());
				LOGGER.debug("business Errors ");
				LOGGER.debug(businessErrors.toString());
		}
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr6VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService.storedErrorRecords(infoProcessed,
					errorMap);
		}

		if (businessProcessed.size() > 0 && !businessProcessed.isEmpty()) {

			List<Gstr6DistributionEntity> processedBusi = sRFileToGstr6DetailsConvertion
					.convertProduct(businessProcessed, updateFileStatus);

			/**
			 * Converting to Absolute Values Start
			 */

			for (Gstr6DistributionEntity b2cProcessed : processedBusi) {

				if (b2cProcessed.getCessAmount() != null) {
					b2cProcessed
							.setCessAmount(b2cProcessed.getCessAmount().abs());
				}
				if (b2cProcessed.getCgstAsCgst() != null) {
					b2cProcessed
							.setCgstAsCgst(b2cProcessed.getCgstAsCgst().abs());
				}
				if (b2cProcessed.getCgstAsIgst() != null) {
					b2cProcessed
							.setCgstAsIgst(b2cProcessed.getCgstAsIgst().abs());
				}
				if (b2cProcessed.getIgstAsCgst() != null) {
					b2cProcessed
							.setIgstAsCgst(b2cProcessed.getIgstAsCgst().abs());
				}
				if (b2cProcessed.getIgstAsIgst() != null) {
					b2cProcessed
							.setIgstAsIgst(b2cProcessed.getIgstAsIgst().abs());
				}
				if (b2cProcessed.getIgstAsSgst() != null) {
					b2cProcessed
							.setIgstAsSgst(b2cProcessed.getIgstAsSgst().abs());
				}
				if (b2cProcessed.getSgstAsIgst() != null) {
					b2cProcessed
							.setSgstAsIgst(b2cProcessed.getSgstAsIgst().abs());
				}
				if (b2cProcessed.getSgstAsSgst() != null) {
					b2cProcessed
							.setSgstAsSgst(b2cProcessed.getSgstAsSgst().abs());
				}

				/**
				 * Converting to Absolute Values End
				 */
			}

			for (Gstr6DistributionEntity b2cProcessed : processedBusi) {
				String b2cInvKey = b2cProcessed.getProcessKey();
				outwardB2cRepository.updateSameInvKey(b2cInvKey);

			}
			// roundingCRValue(processedWithoutAbsOfBusi);
			outwardB2cRepository.saveAll(processedBusi);

		}

		totalRecords = (listOfB2c.size() != 0) ? listOfB2c.size() : 0;
		errorRecords = (businessErrors.size() != 0
				|| structuralErrors.size() != 0)
						? businessErrors.size() + structuralErrors.size() : 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}
	
	private void settingFiledGstins(ProcessingContext context) {
		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR6", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {

			filedSet.add(entity.getGstin() + PIPE + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}
	
	/*public static void main(String[] args) {
		List<ProcessingResult> list = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		errorLocations.add(GSTConstants.DOC_NO);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		list.add(new ProcessingResult(APP_VALIDATION, "ER1276",
				"GSTR6 for selected tax period  is already filed",
				location));
		

		Map<String, List<ProcessingResult>> processingResults = new HashMap<>();
		processingResults.put("33ABOPS9546G1Z3|CR|MAR21-B2B12|2021-03-20-5342", list);
		processingResults.put("33ABOPS9546G1Z3|CR|MAR21-B2B11|2021-03-20-5341", list);
		Map<String, List<ProcessingResult>> distriButionCanLookUp = new HashMap<>();
		distriButionCanLookUp.put("33ABOPS9546G1Z3|CR|CTT03|2021-03-22-5341", list);
		distriButionCanLookUp
				.forEach((key, value) -> processingResults.merge(key, value,
						(v1, v2) -> Stream.of(v1, v2).flatMap(x -> x.stream())
								.collect(Collectors.toList())));
		if (!distriButionCanLookUp.isEmpty()) {
			distriButionCanLookUp.entrySet().forEach(entry -> {

				String key = entry.getKey();
				String errkey = key.substring(0, key.lastIndexOf('-'));
				System.out.println(errkey);
			});
		}
		System.out.println(distriButionCanLookUp.toString());
	}*/

}
