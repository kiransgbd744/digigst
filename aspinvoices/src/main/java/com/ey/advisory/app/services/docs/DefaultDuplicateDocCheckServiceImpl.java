package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.DuplicateDocCheckDto;
import com.ey.advisory.app.docs.dto.DuplicateInwardDocCheckDto;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service("DefaultDuplicateDocCheckServiceImpl")
@Slf4j
public class DefaultDuplicateDocCheckServiceImpl
		implements DuplicateDocCheckService {

	@Autowired
	private DocKeyGenerator<OutwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("DocRepository")
	DocRepository docRepository;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public DuplicateDocCheckDto checkDuplicateDocuments(
			List<OutwardTransDocument> docs) {
		DuplicateDocCheckDto duplicateDocCheckDto = new DuplicateDocCheckDto();
		List<OutwardTransDocument> duplicateDocs = new ArrayList<>();
		List<OutwardTransDocument> gstnSubmmitedDuplicateDocs = new ArrayList<>();

		docs.forEach(document -> {
			String docKey = docKeyGen.generateKey(document);
			List<OutwardTransDocument> duplicateDocuments = docRepository
					.findDocsByDocKey(docKey);
			if (duplicateDocuments != null && !duplicateDocuments.isEmpty()) {
				duplicateDocuments.forEach(duplicateDoc -> {
					boolean submitted = duplicateDoc.isSubmitted();
					if (submitted) {
						gstnSubmmitedDuplicateDocs.add(duplicateDoc);
					} else {
						duplicateDocs.add(duplicateDoc);
					}
				});
			}
		});
		duplicateDocCheckDto.setDocs(duplicateDocs);
		duplicateDocCheckDto.setGstnSubmittedDocs(gstnSubmmitedDuplicateDocs);
		return duplicateDocCheckDto;
	}

	@Override
	public void softDeleteDuplicateDocuments(List<OutwardTransDocument> docs) {
		List<String> docKeys = new ArrayList<>();
		List<Long> docIds = new ArrayList<>();
		OutwardTransDocument firstDoc = docs.get(0);
		docs.forEach(doc -> docKeys.add(docKeyGen.generateKey(doc)));
		
		Config config = configManager.getConfig(
				"EYInternal", "outward.save.chunksize");
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
					.addAll(docRepository.findActiveDocsByDocKeys(chunk)));
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

	@Override
	public DuplicateInwardDocCheckDto checkInwardDuplicateDocuments(
			List<InwardTransDocument> docs,
			DocKeyGenerator<InwardTransDocument, String> docKeyGen,
			InwardTransDocRepository docHeaderRepository) {
		return null;
	}

	@Override
	public void softDeleteDuplicateDocsInward(List<InwardTransDocument> docs) {
		// TODO Auto-generated method stub
		
	}

}
