package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("saveSummaryBatchProcessImpl")
public class Gstr1SaveSummaryBatchProcessImpl
		implements Gstr1SectionWiseSaveBatchProcess {

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
			String groupCode, String section, String operationType,
			Map<Long, Long> orgCanIdsMap, Long retryCount, Long userRequestId,
			ProcessingContext context) {
		List<List<Long>> batchIdsList = batchDto.getIdsList();
		List<SaveGstr1> batches = batchDto.getGstr1();
		String origin = batchDto.getOrigin();
		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();

		for (int i = 0; i < batches.size(); i++) {
			List<Long> idsList = batchIdsList.get(i);
			SaveGstr1 gstr1 = batches.get(i);
			Triplet<List<Long>, SaveGstr1, String> batchInfo = new Triplet<>(
					idsList, gstr1, groupCode);
			List<SaveToGstnBatchRefIds> refIds = saveGstr1Batch(batchInfo,
					section, operationType, orgCanIdsMap, retryCount,
					userRequestId, origin, context);
			retList.addAll(refIds);
		}

		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveGstr1Batch(
			Triplet<List<Long>, SaveGstr1, String> batchInfo, String section,
			String operationType, Map<Long, Long> orgCanIdsMap, Long retryCount,
			Long userRequestId, String origin, ProcessingContext context) {

		// Return collection.
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, SaveGstr1, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);

		while (true) {
			try {
				Triplet<List<Long>, SaveGstr1, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section,
						operationType, orgCanIdsMap, retryCount, userRequestId,
						origin, context);
				retIds.add(refIds);

			} catch (PayloadSizeExceededException ex) {
				List<Triplet<List<Long>, SaveGstr1, String>> splitChunks = batchChunk(
						batchInfo.getValue1(), batchInfo.getValue2(), section);
				splitChunks.forEach(obj -> batchStack.push(obj));
			}

			if (batchStack.isEmpty())
				break;
		}

		return retIds;
	}

	private SaveToGstnBatchRefIds process(
			Triplet<List<Long>, SaveGstr1, String> batchInfo, String section,
			String operationType, Map<Long, Long> orgCanIdsMap, Long retryCount,
			Long userRequestId, String origin, ProcessingContext context) {

		List<Long> idsList = batchInfo.getValue0();
		SaveGstr1 gstr1 = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
				gstr1.getTaxperiod(), gstr1.getSgstin());
		try {
			// status code 20 says status as SAVE TO GSTIN INITIATED
			saveToGstnEventStatus.EventEntry(gstr1.getTaxperiod(),
					gstr1.getSgstin(), 20, groupCode, section.toUpperCase());

			String batch = gson.toJson(gstr1);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1 SaveToGstn {} section batch json has formed as {}",
						section, batch);
			}

			/**
			 * Soft delete older batchs in save_batch table.
			 *//*
				 * 
				 * batchHandler.deleteOldSumryBatchs(gstr1.getSgstin(),
				 * gstr1.getTaxperiod(), section, groupCode,
				 * APIConstants.GSTR1);
				 * 
				 */

			Long gstnBatchId = null;
			Long hMaxId = null;
			Long v1MaxId = null;
			Long v2MaxId = null;
			Long userMaxId = null;

			String returnType = GenUtil.getReturnType(context);

			/**
			 * Save this bath in Gstr1_save_batch table.
			 */
			if (APIConstants.DOCISS.equalsIgnoreCase(section)) {
				Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(
						gstr1.getSgstin(), gstr1.getTaxperiod(), section,
						groupCode, returnType, idsList.size(), operationType,
						retryCount, userRequestId, origin);
				gstnBatchId = saveBatch.getId();
			} else {
				/*
				 * Long gstnBatchId =batchSaveStatusRepository.selectMaxSumryId(
				 * gstr1.getSgstin(), gstr1.getTaxperiod(), APIConstants.GSTR1,
				 * section);
				 */
				List<Object[]> objs = batchSaveStatusRepository
						.selectMaxSumryRecord(gstr1.getSgstin(),
								gstr1.getTaxperiod(), returnType.toUpperCase(),
								section.toUpperCase());

				if (CollectionUtils.isNotEmpty(objs)) {
					Object[] obj = objs.get(0);
					if (obj != null && obj[0] != null) {

						gstnBatchId = obj[0] != null
								? Long.parseLong(obj[0].toString()) : null;
						hMaxId = obj[1] != null
								? Long.parseLong(obj[1].toString()) : null;
						v1MaxId = obj[2] != null
								? Long.parseLong(obj[2].toString()) : null;
						v2MaxId = obj[3] != null
								? Long.parseLong(obj[3].toString()) : null;
						userMaxId = obj[4] != null
								? Long.parseLong(obj[4].toString()) : null;
					}
				} else {
					LOGGER.error(
							"No Valid Gstr1 Batch Id found for the section {}, "
									+ "Proc has not executed properly.",
							section);
					return null;
				}
			}

			APIResponse resp = null;
			try {
				// status code 30 says status as SAVE PROCESSED TO GSTIN
				saveToGstnEventStatus.EventEntry(gstr1.getTaxperiod(),
						gstr1.getSgstin(), 30, groupCode,
						section.toUpperCase());
				if (returnType
						.equalsIgnoreCase(APIConstants.GSTR1A.toUpperCase())) {
					resp = gstnServer.gstr1AApiCall(groupCode, batch,
							gstr1.getSgstin(), gstr1.getTaxperiod(),
							gstnBatchId);

				} else {
					resp = gstnServer.gstr1ApiCall(groupCode, batch,
							gstr1.getSgstin(), gstr1.getTaxperiod(),
							gstnBatchId);
				}
			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(gstr1.getTaxperiod(),
						gstr1.getSgstin(), 60, groupCode,
						section.toUpperCase());
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}

			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				/**
				 * update is_sent_to_gstn in Summry table
				 */
				sumBatchHandler.summaryBatchUpdate(gstr1.getTaxperiod(),
						gstr1.getSgstin(), gstnBatchId, groupCode, idsList,
						section, hMaxId, v1MaxId, v2MaxId, userMaxId);

				/**
				 * Soft delete older batchs in save_batch table.
				 */

				/*
				 * batchHandler.deleteOldSumryBatchs(gstr1.getSgstin(),
				 * gstr1.getTaxperiod(), section, groupCode, APIConstants.GSTR1,
				 * gstnBatchId);
				 */
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
			oneResp.setReturnType(returnType.toUpperCase());
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return oneResp;
	}

	private List<Triplet<List<Long>, SaveGstr1, String>> batchChunk(
			SaveGstr1 gstr1, String groupCode, String section) {
		LOGGER.error("{} Summary Json Exceeded Max Size 5MB ", section);
		return new ArrayList<>();
	}

}
