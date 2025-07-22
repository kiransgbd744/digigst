/*package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr1B2csDetailsEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*

@Service("DefaultVerticalDuplicateDocCheckServiceImpl")
@Slf4j
public class DefaultVerticalDuplicateDocCheckServiceImpl
		                          implements DuplicateVerticalDocCheckService {

	@Autowired
	@Qualifier("Gstr1B2CSRepository")
	private Gstr1B2CSRepository gstr1B2CSRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public List<Gstr1B2csDetailsEntity> softDeleteDuplicateDocuments(
			List<Gstr1B2csDetailsEntity> docs) {
		List<String> docKeys = new ArrayList<>();
		List<Long> docIds = new ArrayList<>();
		Gstr1B2csDetailsEntity firstDoc = docs.get(0);
		docs.forEach(doc -> docKeys.add(doc.getInvB2csKey()));

		Config config = configManager.getConfig("EYInternal",
				"outward.save.chunksize");
		String chnkSizeStr = config != null ? config.getValue() : "2000";
		int chunkSize = Integer.parseInt(chnkSizeStr);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Generated DocKeys : %s, Size is %d,"
							+ " About to chunk the Dockeys list....",
					docKeys, docKeys.size());
			LOGGER.debug(msg);
		}

		List<List<String>> docKeyChunks = Lists.partition(docKeys, chunkSize);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Generated %d Chunks of DocKeys of each Size is %d",
					docKeyChunks.size(), chunkSize);
			LOGGER.debug(msg);
		}

		if (!docKeyChunks.isEmpty()) {
			docKeyChunks.forEach(chunk -> docIds
					.addAll(gstr1B2CSRepository.findActiveDocsByDocKeys(chunk)));
		}

		if (!docIds.isEmpty()) {
			List<List<Long>> docIdChunks = Lists.partition(docIds, chunkSize);
			docIdChunks.forEach(docIdChunk -> {
				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("List of DocIds which are about to get "
									+ "soft delete: %s", docIdChunk);
					LOGGER.debug(msg);
				}
				gstr1B2CSRepository.updateDuplicateDocDeletionByDocKeys(docIdChunk,
						LocalDateTime.now(), firstDoc.getCreatedBy());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Documents soft deleted successfully");
				}
			});
		}

	}
}
*/