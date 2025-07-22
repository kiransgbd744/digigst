package com.ey.advisory.app.services.gstr7fileupload;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7AsEnteredTdsRepository;
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
@Service("gstr7FilingLookUpServiceImpl")
@Slf4j
public class gstr7FilingLookUpServiceImpl implements Gstr7CanService {

	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier("Gstr7AsEnteredTdsRepository")
	Gstr7AsEnteredTdsRepository docRepository;
	
	@Autowired
	@Qualifier("SignAndFileRepository")
	SignAndFileRepository signAndFileRepository;
	private static final String DOC_KEY_JOINER = "|";
	
	@Override
	public Map<String, List<ProcessingResult>> DistriButionCanLookUp(
			List<Gstr7AsEnteredTdsEntity> docs, ProcessingContext context) {

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<String> orgDocDetails = new ArrayList<>();
			docs.forEach(doc -> docKeySet.add(keyGeneration(doc)));
			docKeys = new ArrayList<>(docKeySet);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("To check  Invoices The number of dockeys "
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
						signAndFileRepository.findSubmitDocsCountsByDocKeys(chunk)));
			}

			docs.forEach(doc -> {
				List<ProcessingResult> results = new ArrayList<>();
			
			
				String docKey = keyGeneration(doc);
				
				if(!orgDocDetails.isEmpty() && !orgDocDetails.contains(docKey)){
					doc.setErrorCode("ER2031");
					doc.setError(true);
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());

					results.add(new ProcessingResult(APP_VALIDATION, "ER2031",
							"Return Period for the month is already filed",
							location));
					
				}
				if (results != null && results.size() > 0) {
										
					retResultMap.put(docKey, results);
				}


				

			});
		} catch (Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ " doccument  in gstr7 file: Exception "
					+ "is : ", ex);
		}

		

		return retResultMap;

	}
	private String keyGeneration(Gstr7AsEnteredTdsEntity doc){
		return new StringJoiner(DOC_KEY_JOINER)
				.add(doc.getTdsGstin())
				.add(doc.getReturnPeriod())
				.toString();
		
	}
	
}
