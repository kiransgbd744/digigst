package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SummaryBatchHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("anx1SaveSummaryBatchProcessImpl")
public class Anx1SaveSummaryBatchProcessImpl implements Anx1SectionWiseSaveBatchProcess {

	@Autowired
	@Qualifier("batchHandler")
	private BatchHandler batchHandler;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("SummaryBatchHandler")
	private SummaryBatchHandler sumBatchHandler;
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Override
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section) {
		List<List<Long>> batchIdsList = batchDto.getIdsList();
		List<SaveAnx1> batches = batchDto.getAnx1();

		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		if (batches != null && !batches.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("execute is started with {} batches",
						batches.size());
			}
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				SaveAnx1 anx1 = batches.get(i);
				Triplet<List<Long>, SaveAnx1, String> batchInfo = new Triplet<>(
						idsList, anx1, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveAnx1Batch(batchInfo,
						section);
				if (refIds != null && !refIds.isEmpty())
					retList.addAll(refIds);
			}
		}
		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveAnx1Batch(
			Triplet<List<Long>, SaveAnx1, String> batchInfo, String section) {

		// Return collection.
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, SaveAnx1, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);

		while (true) {
			try {
				Triplet<List<Long>, SaveAnx1, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section);
				retIds.add(refIds);

			} catch (PayloadSizeExceededException ex) {
				LOGGER.error("{} PayloadSizeExceededException Dont worry..., "
						+ "We are handling it by Chunking.", section);
				List<Triplet<List<Long>, SaveAnx1, String>> splitChunks = batchChunk(
						batchInfo.getValue1(), batchInfo.getValue2(), section);
				splitChunks.forEach(obj -> batchStack.push(obj));
			}

			if (batchStack.isEmpty()) {
				LOGGER.info("End of {} batches", section);
				break;
			}
		}

		return retIds;
	}

	private SaveToGstnBatchRefIds process(
			Triplet<List<Long>, SaveAnx1, String> batchInfo, String section) {

		List<Long> idsList = batchInfo.getValue0();
		SaveAnx1 anx1 = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		if(LOGGER.isInfoEnabled()) {
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
				anx1.getTaxperiod(), anx1.getSgstin());
		}
		try {
			// status code 20 says status as SAVE TO GSTIN INITIATED
			saveToGstnEventStatus.EventEntry(anx1.getTaxperiod(),
					anx1.getSgstin(), 20, groupCode);

			String batch = gson.toJson(anx1);
			LOGGER.error("batch {}", batch);

			/**
			 * Soft delete older batchs in save_batch table.
			 *//*
			
			batchHandler.deleteOldSumryBatchs(anx1.getSgstin(),
					anx1.getTaxperiod(), section, groupCode, APIConstants.ANX1);
			*//**
			 * Save this bath in save_batch table.
			 *//*
			Long gstnBatchId = batchHandler.saveBatch(anx1.getSgstin(),
					anx1.getTaxperiod(), section, groupCode, APIConstants.ANX1,
					idsList.size());*/
			
			/*Long gstnBatchId =batchSaveStatusRepository.selectMaxSumryId(
			anx1.getSgstin(), anx1.getTaxperiod(), 
			APIConstants.ANX1, section);*/
			
			List<Object[]> objs =batchSaveStatusRepository.selectMaxSumryRecord(
					anx1.getSgstin(), anx1.getTaxperiod(), 
					APIConstants.ANX1.toUpperCase(), section.toUpperCase());
			Long gstnBatchId = null;
			Long hMaxId = null;
			Long v1MaxId = null;
			Long v2MaxId = null;
			Long userMaxId = null;
			
			Object[] obj = objs.get(0);
			if (obj != null && obj[0] != null) {
				
				gstnBatchId = obj[0] != null ? Long.parseLong(obj[0].toString())
						: null;
				hMaxId = obj[1] != null ? Long.parseLong(obj[1].toString())
						: null;
				v1MaxId = obj[2] != null ? Long.parseLong(obj[2].toString())
						: null;
				v2MaxId = obj[3] != null ? Long.parseLong(obj[3].toString())
						: null;
				userMaxId = obj[4] != null ? Long.parseLong(obj[4].toString())
						: null;
			} else {
				LOGGER.error(
						"No Valid Anx1 Batch Id found for the section {}, "
						+ "Proc has not executed properly.",
						section);
				return null;
			}
			
			APIResponse resp = null;
			try {
				// status code 30 says status as SAVE PROCESSED TO GSTIN
				saveToGstnEventStatus.EventEntry(anx1.getTaxperiod(),
						anx1.getSgstin(), 30, groupCode);
				resp = gstnServer.anx1ApiCall(groupCode, batch, anx1.getSgstin(),
						anx1.getTaxperiod(), gstnBatchId);
			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				LOGGER.error("Anx1 gstn api call exception", ex.getMessage());
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(anx1.getTaxperiod(),
						anx1.getSgstin(), 60, groupCode);
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}
			
			String refId = null;
			String txnId = null;
			
			if (resp.getResponse() != null) {
				
				/**
				 * update is_sent_to_gstn in Summry table
				 */
				sumBatchHandler.summaryBatchUpdate(anx1.getTaxperiod(),
						anx1.getSgstin(), gstnBatchId, groupCode, idsList,
						section.toUpperCase(), hMaxId, v1MaxId, v2MaxId,
						userMaxId);
				/**
				 * Soft delete older batchs in save_batch table.
				 */
				/*batchHandler.deleteOldSumryBatchs(anx1.getSgstin(),
						anx1.getTaxperiod(), section, groupCode,
						APIConstants.ANX1, gstnBatchId);*/

				/**
				 * update the RefId and txnId in save_batch table
				 */
				refId = batchHandler.updateRefIdAndTxnId(groupCode, gstnBatchId,
						resp);
				txnId = resp.getTxnId();
			} else {
				batchHandler.deleteBatch(gstnBatchId, groupCode);
			}

			/**
			 * Setting to return the ref and its batch id
			 */
			oneResp.setGstnBatchId(gstnBatchId);
			oneResp.setRefId(refId);
			oneResp.setTxnId(txnId);
			oneResp.setReturnType(APIConstants.ANX1.toUpperCase());
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg,ex);
		}

		return oneResp;
	}

	private List<Triplet<List<Long>, SaveAnx1, String>> batchChunk(
			SaveAnx1 anx1, String groupCode, String section) {
		LOGGER.error("{} Summary Json Exceeded Max Size 5MB ", section);
		return new ArrayList<>();
	}

}
