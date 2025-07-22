/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Itc04DuplicateDocCheckServiceImpl")
@Slf4j
public class Itc04DuplicateDocCheckServiceImpl
		implements Itc04DuplicateDocCheckService {

	@Autowired
	private DocKeyGenerator<Itc04HeaderEntity, String> docKeyGen;

	@Autowired
	@Qualifier("Itc04DocRepository")
	Itc04DocRepository itc04DocRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void softDeleteDupDocsDetails(List<Itc04HeaderEntity> docs) {
		List<String> docKeys = new ArrayList<>();
		List<Long> docIds = new ArrayList<>();
		List<String> saveToGstinDocs = new ArrayList<>();
		Itc04HeaderEntity firstDoc = docs.get(0);
		docs.forEach(doc -> docKeys.add(docKeyGen.generateKey(doc)));

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
			docKeyChunks.forEach(chunk -> {
				docIds.addAll(
						itc04DocRepository.findActiveDocsByDocKeys(chunk));
				saveToGstinDocs.addAll(itc04DocRepository
						.findActiveGstinSaveStatusTrue(chunk));
			});
		}
		docs.forEach(doc -> {
			String docKey = docKeyGen.generateKey(doc);

			if (!saveToGstinDocs.isEmpty()) {
				if (saveToGstinDocs.contains(docKey)) {
					doc.setSaveFlag("E");
				}
			}
		});

		if (!docIds.isEmpty()) {
			List<List<Long>> docIdChunks = Lists.partition(docIds, chunkSize);
			docIdChunks.forEach(docIdChunk -> {
				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("List of DocIds which are about to get "
									+ "soft delete: %s", docIdChunk);
					LOGGER.debug(msg);
				}
				itc04DocRepository.updateDuplicateDocDeletionByDocKeys(
						docIdChunk, LocalDateTime.now(),
						firstDoc.getCreatedBy());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Documents soft deleted successfully");
				}
			});
		}
	}

}
