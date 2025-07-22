/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr7;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.repositories.client.gstr7trans.Gstr7TransDocHeaderRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */

@Service("Gstr7TransDefaultDuplicateDocCheckServiceImpl")
@Slf4j
public class Gstr7TransDefaultDuplicateDocCheckServiceImpl {

	@Autowired
	private DocKeyGenerator<Gstr7TransDocHeaderEntity, String> docKeyGen;

	@Autowired
	Gstr7TransDocHeaderRepository docRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public Gstr7TransDuplicateDocCheckDto checkDuplicateDocuments(
			List<Gstr7TransDocHeaderEntity> docs) throws AppException {
		Gstr7TransDuplicateDocCheckDto duplicateDocCheckDto = new Gstr7TransDuplicateDocCheckDto();
		List<Gstr7TransDocHeaderEntity> duplicateDocs = new ArrayList<>();

		docs.forEach(document -> {
			String docKey = docKeyGen.generateKey(document);
			List<Gstr7TransDocHeaderEntity> duplicateDocuments = docRepository
					.findDocsByDocKey(docKey);
			if (duplicateDocuments != null && !duplicateDocuments.isEmpty()) {
				duplicateDocuments.forEach(duplicateDoc -> {
					duplicateDocs.add(duplicateDoc);

				});
			}
		});
		duplicateDocCheckDto.setDocs(duplicateDocs);
		return duplicateDocCheckDto;
	}

	public void softDeleteDupDocs(
			List<Gstr7TransDocHeaderEntity> docs) {
		List<String> docKeys = new CopyOnWriteArrayList<>();
		List<String> canDocKeys = new CopyOnWriteArrayList<>();
		List<Long> docIds = new CopyOnWriteArrayList<>();
		Gstr7TransDocHeaderEntity firstDoc = docs.get(0);

		docs.parallelStream()
				.forEach(doc -> docKeys.add(docKeyGen.generateKey(doc)));

		docs.parallelStream().filter(
				doc -> doc.getSupplyType().equalsIgnoreCase(GSTConstants.CAN))
				.forEach(doc -> canDocKeys.add(docKeyGen.generateKey(doc)));

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
		String groupCode = TenantContext.getTenantId();

		if (!docKeyChunks.isEmpty()) {
			docKeyChunks.parallelStream().forEach(
					chunk -> lookUpAndGetDocDetails(chunk, docIds, groupCode));
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
				docRepository.updateDuplicateDocDeletionByDocKeys(docIdChunk,
						LocalDateTime.now(), firstDoc.getCreatedBy());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Documents soft deleted successfully");
				}
			});
		}
	}

	private void lookUpAndGetDocDetails(List<String> chunk, List<Long> docIds,
			String groupCode) {
		TenantContext.setTenantId(groupCode);
		docIds.addAll(docRepository.findActiveDocsByDocKeys(chunk));
	}
}
