package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Service("DistriButionCanServiceImpl")
@Slf4j
public class DistriButionCanServiceImpl implements DistriButionCanService {

	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	Gstr6DistributionRepository docRepository;
	
	private static final String PIPE = "|";
	
	@Override
	public Map<String, List<ProcessingResult>> DistriButionCanLookUp(
			List<Gstr6DistributionExcelEntity> docs, ProcessingContext context) {

		//Integer is = 0;
		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<Object[]> orgDocDetails = new ArrayList<>();

			List<Gstr6DistributionExcelEntity> canDocs = docs.stream()
					.filter(doc -> GSTConstants.CAN
							.equalsIgnoreCase(doc.getSupplyType()))
					.collect(Collectors.toList());
			if (LOGGER.isDebugEnabled()) {
				for (Gstr6DistributionExcelEntity doc : canDocs) {
					LOGGER.debug(doc.toString());
				}
			}
			
			canDocs.forEach(doc -> docKeySet.add(doc.getProcessKey()));
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
				docKeyChunks.forEach(chunk -> orgDocDetails.addAll(
						docRepository.findCancelDocsCountsByDocKeys(chunk)));
			}

			Map<String, Boolean> orgDocKeyMap = orgDocDetails.stream()
					.collect(Collectors.toMap(obj -> String.valueOf(obj[0]),
							obj -> (Boolean) obj[1], (obj1, obj2) -> obj1));
			

			
		//	canDocs.forEach(doc -> {
				for(Gstr6DistributionExcelEntity doc : canDocs){
				List<ProcessingResult> results = new ArrayList<>();
				List<ProcessingResult> validate = validate(doc);
				if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Before Freeze CAN");
						LOGGER.debug(doc.toString());
				}
				List<ProcessingResult> validateFreez = validateFreezRecords(doc, context);

				results.addAll(validate);
				results.addAll(validateFreez);
				
				String docKey = doc.getProcessKey();
				Boolean IsSubmittedFlag = orgDocKeyMap.get(docKey);
				if (IsSubmittedFlag == null) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());

					results.add(new ProcessingResult(APP_VALIDATION, "ER0518",
							"Document cannot be cancelled as the "
									+ "same was not reported to ASP System",
							location));
				}
				
				
					if ( IsSubmittedFlag != null && IsSubmittedFlag) {
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(GSTConstants.DOC_NO);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								errorLocations.toArray());

						results.add(new ProcessingResult(APP_VALIDATION, "ER0518",
								"Document cannot be cancelled as the "
										+ "same was not reported to ASP System",
								location));
					}


				if (results != null && results.size() > 0) {
					Long id = doc.getId();		
					String keys = 
							docKey.concat(GSTConstants.SLASH).concat(id.toString());
					retResultMap.put(keys, results);
				}
				//is++;
				}
		}
		//	});
			catch (Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ "orginal doccument for CAN invoices in DistriBution fi;e: Exception "
					+ "is : ", ex);
		}

		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("After Freeze CAN");
			LOGGER.debug(retResultMap.toString());
	}
		return retResultMap;

	}
	public List<ProcessingResult> validate(Gstr6DistributionExcelEntity document) {
		String groupCode = TenantContext.getTenantId();

		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.

		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);

		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getIsdGstin());
		if (gstin == null) {

			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.GSTIN);
			TransDocProcessingResultLoc location 
			               = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER3056",
					"ISD GSTIN is not as per On-Boarding data",
					location));
			return errors;

		}
		if (gstin != null && gstin.getRegistrationType() != null) {
			if (!GSTConstants.ISD
					.equalsIgnoreCase(gstin.getRegistrationType())) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.GSTIN);
				TransDocProcessingResultLoc location 
				          = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3056",
						"ISD GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
		}
		return errors;
	}
	
	public List<ProcessingResult> validateFreezRecords(Gstr6DistributionExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String gstin = document.getIsdGstin();
		String taxPeriod = document.getRetPeriod();
		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + PIPE + taxPeriod;
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Key to check Filing Status : " + key);
		}
		if (filedSet != null && filedSet.contains(key)) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
					"GSTR6 for selected tax period  is already filed",
					location));
			document.setError(true);
			//document.setDelete(true);
		}

		return errors;
	}
}
