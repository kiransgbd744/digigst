/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.DuplicateInwardDocCheckDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDuplicateDocCheckDto;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */

@Service("EInvoiceDefaultDuplicateDocCheckServiceImpl")
@Slf4j
public class EInvoiceDefaultDuplicateDocCheckServiceImpl
		implements EInvoiceDuplicateDocCheckService {

	@Autowired
	private DocKeyGenerator<OutwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("DocRepository")
	DocRepository docRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public EInvoiceDuplicateDocCheckDto checkDuplicateDocuments(
			List<OutwardTransDocument> docs) throws AppException {
		EInvoiceDuplicateDocCheckDto duplicateDocCheckDto = new EInvoiceDuplicateDocCheckDto();
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
	public void softDeleteDupDocsAndRetainEwbDetails(
			List<OutwardTransDocument> docs) {
		List<String> docKeys = new CopyOnWriteArrayList<>();
		List<String> canDocKeys = new CopyOnWriteArrayList<>();
		List<Long> docIds = new CopyOnWriteArrayList<>();
		OutwardTransDocument firstDoc = docs.get(0);
		List<Object[]> ewbDocs = new CopyOnWriteArrayList<>();
		List<Object[]> einvDocs = new CopyOnWriteArrayList<>();
		List<Object[]> einvGstnSaveStatusList = new CopyOnWriteArrayList<>();
		List<Object[]> gsntBifuractionList = new CopyOnWriteArrayList<>();

		Map<String, Quartet<LocalDate, Long, LocalDateTime, Integer>> ewbDocsMap = new ConcurrentHashMap<>();
		Map<String, Quintet<LocalDate, String, LocalDateTime, Integer, String>> einvDocsMap = new ConcurrentHashMap<>();
		Map<String, Pair<LocalDate, String>> einvGstnSaveStatusMap = new ConcurrentHashMap<>();
		Map<String, String> gstnBifFuractionMap = new ConcurrentHashMap<>();

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
		List<List<String>> canDocKeyChunks = Lists.partition(canDocKeys,
				chunkSize);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Generated %d Chunks of DocKeys of each Size is %d",
					docKeyChunks.size(), chunkSize);
			LOGGER.debug(msg);
		}
		String groupCode = TenantContext.getTenantId();

		if (!docKeyChunks.isEmpty()) {
			docKeyChunks.parallelStream()
					.forEach(chunk -> lookUpAndGetDocDetails(chunk, docIds,
							ewbDocs, einvDocs, einvGstnSaveStatusList,
							groupCode));
		}
		
		if (!canDocKeyChunks.isEmpty()) {
			canDocKeyChunks.forEach(chunk -> gsntBifuractionList
					.addAll(docRepository.findActiveOrgDocs(chunk)));
		}
		if (!ewbDocs.isEmpty()) {

			ewbDocs.parallelStream().forEach(obj -> {
				String docKey = (String) obj[0];
				LocalDate docDate = (LocalDate) obj[1];
				Long reaTainedewayBillNum = (Long) obj[2];
				LocalDateTime reaTainedewbDate = (LocalDateTime) obj[3];
				Integer ewbStatus = (Integer) obj[4];
				ewbDocsMap.put(docKey, new Quartet<>(docDate,
						reaTainedewayBillNum, reaTainedewbDate, ewbStatus));
				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug(
							"reaTainedewayBillNum{} and ewbStatus{} for docKey{}",
							reaTainedewayBillNum, ewbStatus, docKey);
				}
			});
		}
		if (!einvDocs.isEmpty()) {

			einvDocs.parallelStream().forEach(obj -> {
				String docKey = (String) obj[0];
				LocalDate docDate = (LocalDate) obj[1];
				String irnResponse = (String) obj[2];
				LocalDateTime ackdate = (LocalDateTime) obj[3];
				Integer retainedirnStatus = (Integer) obj[4];
				String ackNum = (String) obj[5];
				einvDocsMap.put(docKey, new Quintet<>(docDate, irnResponse,
						ackdate, retainedirnStatus, ackNum));
				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("irnResponse{} and ackdate{} for docKey{}",
							irnResponse, ackdate, docKey);
				}
			});
		}
		if (!einvGstnSaveStatusList.isEmpty()) {
			einvGstnSaveStatusList.parallelStream().forEach(obj -> {
				String docKey = (String) obj[0];
				LocalDate docDate = (LocalDate) obj[1];
				String einvGstnstatus = (String) obj[2];
				einvGstnSaveStatusMap.put(docKey,
						new Pair<>(docDate, einvGstnstatus));

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("einvGstnstatus{} and docDate{} for docKey{}",
							einvGstnstatus, docDate, docKey);
				}
			});
		}
		if (!gsntBifuractionList.isEmpty()) {
			gsntBifuractionList.parallelStream().forEach(obj -> {
				String docKey = (String) obj[0];
				String gstnBifuraction = (String) obj[1];
				if (!Strings.isNullOrEmpty(gstnBifuraction)) {
					gstnBifFuractionMap.put(docKey, gstnBifuraction);
				}				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gsntBifuractionList{} for docKey{}",
							gstnBifuraction, docKey);
				}
			});
		}
		docs.parallelStream().forEach(doc -> {
			String docKey = docKeyGen.generateKey(doc);
			if (ewbDocsMap.containsKey(docKey) && ewbDocsMap.get(docKey)
					.getValue0().isEqual(doc.getDocDate())) {
				doc.setEwbNoresp(ewbDocsMap.get(docKey).getValue1());
				doc.setEwbDateResp(ewbDocsMap.get(docKey).getValue2());
				doc.setEwbStatus(ewbDocsMap.get(docKey).getValue3());

			}
			if (einvDocsMap.containsKey(docKey) && einvDocsMap.get(docKey)
					.getValue0().isEqual(doc.getDocDate())) {
				doc.setIrnResponse(einvDocsMap.get(docKey).getValue1());
				doc.setAckDate(einvDocsMap.get(docKey).getValue2());
				doc.setIrnStatus(einvDocsMap.get(docKey).getValue3());
				doc.setAckNum(einvDocsMap.get(docKey).getValue4());

			}
			if (einvGstnSaveStatusMap.containsKey(docKey)
					&& einvGstnSaveStatusMap.get(docKey).getValue0()
							.isEqual(doc.getDocDate())) {
				doc.setEinvGstnSaveStatus(
						einvGstnSaveStatusMap.get(docKey).getValue1());
			}
			if (gstnBifFuractionMap.containsKey(docKey)
					&& doc.getSupplyType().equalsIgnoreCase(GSTConstants.CAN)) {
				doc.setGstnBifurcation(gstnBifFuractionMap.get(docKey));
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
			InwardTransDocRepository docHeaderRepository) throws AppException {
		// TODO Auto-generated method stub
		return null;
	}

	private void lookUpAndGetDocDetails(List<String> chunk, List<Long> docIds,
			List<Object[]> ewbDocs, List<Object[]> einvDocs,
			List<Object[]> einvGstnSaveStatusList, String groupCode) {
		TenantContext.setTenantId(groupCode);
		docIds.addAll(docRepository.findActiveDocsByDocKeys(chunk));
		ewbDocs.addAll(docRepository.findActiveEwbDocsByDocKeys(chunk));
		einvDocs.addAll(docRepository.findActiveEinvoiceDocsByDocKeys(chunk));
		einvGstnSaveStatusList
				.addAll(docRepository.findActiveEinvGstinSaveStatus(chunk));
	}
}
