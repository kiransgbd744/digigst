/**
 * 
 */
package com.ey.advisory.app.services.itc04;

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

import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Itc04CanDocCheckServiceImpl")
@Slf4j
public class Itc04CanDocCheckServiceImpl
		implements Itc04OriginalDocCheckService {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Autowired
	private DocKeyGenerator<Itc04HeaderEntity, String> docKeyGen;
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("Itc04DocRepository")
	private Itc04DocRepository itc04DocRepository;

	private static final String CLASS_NAME = "Itc04CanDocCheckServiceImpl";

	@Override
	public Map<String, List<ProcessingResult>> checkForItc04OrgInvoices(
			List<Itc04HeaderEntity> docs) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Itc04 Function to check whether Cancel invoice "
							+ "is present or not in the db with ");
			LOGGER.debug(msg);
		}

		Map<String, List<ProcessingResult>> canLooksProcessingResult = CanDocLookUp(
				docs);

		return canLooksProcessingResult;
	}

	private Map<String, List<ProcessingResult>> CanDocLookUp(
			List<Itc04HeaderEntity> docs) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_START", CLASS_NAME,
				"CanDocLookUp", null);

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<String> orgDocDetails = new ArrayList<>();

			List<Itc04HeaderEntity> canDocs = docs.stream()
					.filter(doc -> GSTConstants.CAN
							.equalsIgnoreCase(doc.getActionType()))
					.collect(Collectors.toList());

			canDocs.forEach(doc -> docKeySet.add(docKeyGen.generateKey(doc)));
			docKeys = new ArrayList<>(docKeySet);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("To check cancel Invoices The number of dockeys "
						+ "recieved from the iteration is : " + docKeys.size());
			}

			Config config = configManager.getConfig("EYInternal",
					"outward.save.chunksize");
			String chnkSizeStr = config != null ? config.getValue() : "2000";
			int chunkSize = Integer.parseInt(chnkSizeStr);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated DocKeys : %s, Size is %d,"
								+ " About to chunk the Dockeys list in org doc check....",
						docKeys, docKeys.size());
				LOGGER.debug(msg);
			}

			List<List<String>> docKeyChunks = Lists.partition(docKeys,
					chunkSize);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated %d Chunks of DocKeys of each Size is %d in org doc check",
						docKeyChunks.size(), chunkSize);
				LOGGER.debug(msg);
			}

			if (!docKeyChunks.isEmpty()) {
				docKeyChunks.forEach(chunk -> orgDocDetails.addAll(
						itc04DocRepository.findCancelDocsByDocKeys(chunk)));
			}

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"The number of original records fetch from db is : "
								+ orgDocDetails.size());
			}

			canDocs.forEach(doc -> {
				List<ProcessingResult> results = new ArrayList<>();
				String docKey = docKeyGen.generateKey(doc);
				if (!orgDocDetails.contains(docKey)) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.TABLE_NUMBER);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());

					results.add(new ProcessingResult(APP_VALIDATION, "ER5867",
							"Original record not found in the system",
							location));
				}

				retResultMap.put(docKey, results);

			});
		} catch (Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ "orginal doccument for Inward CAN invoices : Exception "
					+ "is : ", ex);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_END", CLASS_NAME,
				"CanDocLookUp", null);

		return retResultMap;

	}

}
