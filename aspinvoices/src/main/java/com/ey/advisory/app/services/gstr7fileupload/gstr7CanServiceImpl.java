package com.ey.advisory.app.services.gstr7fileupload;

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

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Service("gstr7CanServiceImpl")
@Slf4j
public class gstr7CanServiceImpl implements Gstr7CanService {

	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier("Gstr7ProcessedRepository")
	Gstr7ProcessedRepository docRepository;
	
	private static final String DOC_KEY_JOINER = "|";
	
	@Override
	public Map<String, List<ProcessingResult>> DistriButionCanLookUp(
			List<Gstr7AsEnteredTdsEntity> docs, ProcessingContext context) {

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<Object[]> orgDocDetails = new ArrayList<>();

			List<Gstr7AsEnteredTdsEntity> canDocs = docs.stream()
					.filter(doc -> GSTConstants.CAN
							.equalsIgnoreCase(doc.getActType()))
					.collect(Collectors.toList());

			canDocs.forEach(doc -> docKeySet.add(doc.getTdsKey()));
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
			


			canDocs.forEach(doc -> {
				List<ProcessingResult> results = new ArrayList<>();
			
				String docKey = doc.getTdsKey();
				List<ProcessingResult> validateFreez = validateFreezRecords(doc, context);
				results.addAll(validateFreez);
				Boolean IsSubmittedFlag = orgDocKeyMap.get(docKey);
				if (IsSubmittedFlag == null) {
					doc.setError(true);
					doc.setErrorCode("ER0518");
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());

					results.add(new ProcessingResult(APP_VALIDATION, "ER0518",
							"Record cannot be processed as original details are not found in the system",
							location));
				}
				

				if ( IsSubmittedFlag != null && IsSubmittedFlag) {
					doc.setError(true);
					doc.setErrorCode("ER0518");
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());

					results.add(new ProcessingResult(APP_VALIDATION, "ER0518",
							"Record cannot be processed as original details are not found in the system",
							location));
				}
				
				
				if (results != null && results.size() > 0) {
										
					retResultMap.put(docKey, results);
				}
				

			});
		} catch (Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ "orginal doccument for CAN invoices in gstr7 file: Exception "
					+ "is : ", ex);
		}

		

		return retResultMap;

	}
	
	public List<ProcessingResult> validateFreezRecords(Gstr7AsEnteredTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String gstin = document.getTdsGstin();
		String taxPeriod = document.getReturnPeriod();
		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + DOC_KEY_JOINER + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
					"GSTR7 for selected tax period  is already filed",
					location));
		}

		return errors;
	}
	
}
