package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.STRUCTURAL_VALIDATIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.Gstr6DistributionExcelRepository;
import com.ey.advisory.app.services.docs.Gstr6DistrbtnExcelConvertion;
import com.ey.advisory.app.services.structuralvalidation.gstr6.Gstr6StructuralValidatorChain;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;


/**
 * 
 * @author Balakrishna.S
 *
 */


@Component("Gstr6StructuralErrorUploadService")
public class Gstr6StructuralErrorUploadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6StructuralErrorUploadService.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr6VerticalWebUploadErrorService")
	private Gstr6VerticalWebUploadErrorService gstr6VerticalWebUploadErrorService;

	
	/*@Autowired
	@Qualifier("B2cStructValidationChain")
	private B2cStructValidationChain b2cStructValidationChain;
	*/
	
	@Autowired
	@Qualifier("Gstr6StructuralValidatorChain")
	private Gstr6StructuralValidatorChain gstr6StructValidationChain;
/*	@Autowired
	@Qualifier("Gstr6DstrbtnBusinessValidationChain")
	private Gstr6DstrbtnBusinessValidationChain productBusinessValidationChain;
*/
	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;
	
/*	@Autowired
	@Qualifier("Gstr6VerticalWebErrorRepo")
	Gstr6VerticalWebErrorRepo gstr6VerticalWebErrorRepo;
*/
	@Autowired
	@Qualifier("Gstr6DistributionExcelRepository")
	private Gstr6DistributionExcelRepository gstr6DistrbtnExcelRepository;
	
	@Autowired
	@Qualifier("Gstr6DistrbtnExcelConvertion")
	private Gstr6DistrbtnExcelConvertion masterDataToProductConverter;

	@Autowired
	@Qualifier("B2cBusinessErrorUploadService")
	private B2cBusinessErrorUploadService b2cBusinessErrorUploadService;
	
	@Autowired
	@Qualifier("Gstr6BusinessErrorUploadService")
	Gstr6BusinessErrorUploadService gstr6BusinessErrorUploadService;
	
	@Autowired
	@Qualifier("DistriButionCanServiceImpl")
	private DistriButionCanService canService;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> listOfGstr6Distrbn,
			Gstr1FileStatusEntity updateFileStatus, ProcessingContext context) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		List<Gstr6DistributionExcelEntity> structuralErrorsRecords = new ArrayList<>();
		List<Gstr6DistributionExcelEntity> strucProcessedRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		List<Gstr6DistributionExcelEntity> excelData = masterDataToProductConverter
				.convertProduct(listOfGstr6Distrbn,
						updateFileStatus);
		
		softDelete(excelData);
		
		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<Gstr6DistributionExcelEntity> saveExcelAll = gstr6DistrbtnExcelRepository
				.saveAll(excelData);
		LOGGER.error("Ending Excel data Dumping process in to Table");
		
	/*	Map<String, List<ProcessingResult>> distriButionCanLookUp 
        = canService.DistriButionCanLookUp(saveExcelAll);
		*/
		
		
	//	Map<String, List<ProcessingResult>> processingResults = null;
		
		// validation Chain if done 
		
		Map<String, List<ProcessingResult>> processingResults = 
				gstr6StructValidationChain.validation(listOfGstr6Distrbn, saveExcelAll);

		
		/*Map<String, List<ProcessingResult>> processingResults = new HashMap<>(
				processingStructResults);

				distriButionCanLookUp.forEach(
				(key, value) -> processingResults.merge(key, value,
						(v1, v2) -> Stream.of(v1, v2)
								.flatMap(x -> x.stream()).collect(
										Collectors.toList())));
						*/
		
		
		List<String> structuralValKeys = new ArrayList<>();

		
		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		
	//	List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (Gstr6DistributionExcelEntity id : saveExcelAll) {
			String processedKey = id.getProcessKey();
			if (!structuralValKeys.contains(processedKey)) {
				processedKeys.add(processedKey);
				strucProcessedRecords.add(id);
			} else {
				id.setError(true);
				structuralErrorsRecords.add(id);
			}
		}
		
		
		
		LOGGER.error("strucErrorRecords ", structuralErrorsRecords.size());
		LOGGER.error("strucProcessRecords ", strucProcessedRecords.size());
		
		if (structuralErrorsRecords.size() > 0
				&& !structuralErrorsRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr6VerticalWebError>> errorMap = 
					gstr6VerticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);

			gstr6VerticalWebUploadErrorService
					.storedErrorRecords(structuralErrorsRecords, errorMap);

		}
		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			gstr6BusinessErrorUploadService.processBusinessData(listOfGstr6Distrbn,
					strucProcessedRecords, structuralErrorsRecords,
					updateFileStatus, context);
		} else {
			totalRecords = (saveExcelAll.size() != 0) ? saveExcelAll.size() : 0;
			errorRecords = (structuralErrorsRecords.size() != 0)
					? structuralErrorsRecords.size() : 0;
			processedRecords = totalRecords - errorRecords;
			information = 0;

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			gstr1FileStatusRepository.save(updateFileStatus);
		}
	}
	
	// Deleting Existing Record based on Processed Key
private void softDelete(List<Gstr6DistributionExcelEntity> excelData){
		
	Set<String> docKeySet = new HashSet<>();
	excelData.forEach(doc -> docKeySet.add(doc.getProcessKey()));
	
	List<String> docKeys = new ArrayList<>();
	docKeys = new ArrayList<>(docKeySet);

	if (LOGGER.isDebugEnabled()) {

		LOGGER.debug("To check cancel Invoices The number of dockeys "
				+ "recieved from the iteration is : " + docKeys.size());
	}

	Config config = configManager.getConfig("EYInternal",
			"outward.save.chunksize");
	String chnkSizeStr = config != null ? config.getValue() : "2000";
	int chunkSize = Integer.parseInt(chnkSizeStr);


	List<List<String>> docKeyChunks = Lists.partition(docKeys,
			chunkSize);

	
	if (!docKeyChunks.isEmpty()) {
		docKeyChunks.forEach(list -> gstr6DistrbtnExcelRepository.updateSameInvKey(list));
	}
	
	}
	
}