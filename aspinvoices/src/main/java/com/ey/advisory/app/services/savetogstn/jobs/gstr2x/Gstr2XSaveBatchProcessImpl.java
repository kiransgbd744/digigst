package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Gstr2XSaveBatchProcessImpl")
public class Gstr2XSaveBatchProcessImpl implements Gstr2XSectionWiseSaveBatchProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr2XSaveBatchProcessImpl.class);

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("batchHandler")
	private BatchHandler batchHandler;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Override
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto, String groupCode, String section,
			Long userRequestId, String taxDocType) {
		List<List<Long>> batchIdsList = batchDto.getIdsList();
		List<Gstr2XDto> batches = batchDto.getGstr2x();
		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute is started with {} batches", batches.size());
		}
		if (batches != null && !batches.isEmpty()) {
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				Gstr2XDto gstr2x = batches.get(i);
				Triplet<List<Long>, Gstr2XDto, String> batchInfo = new Triplet<>(idsList, gstr2x, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveGstr2XBatch(batchInfo, section, userRequestId, taxDocType);
				retList.addAll(refIds);
			}
		}
		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveGstr2XBatch(Triplet<List<Long>, Gstr2XDto, String> batchInfo,
			String section, Long userRequestId, String taxDocType) {
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, Gstr2XDto, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);
		while (true) {
			try {
				Triplet<List<Long>, Gstr2XDto, String> curBatch = batchStack.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section, userRequestId, taxDocType);
				retIds.add(refIds);
			} catch (PayloadSizeExceededException ex) {
				LOGGER.error("{} PayloadSizeExceededException Dont worry..., " + "We are handling it by Chunking.",
						section);
			}
			if (batchStack.isEmpty()) {
				LOGGER.info("End of {} batches", section);
				break;
			}
		}
		return retIds;
	}

	private SaveToGstnBatchRefIds process(Triplet<List<Long>, Gstr2XDto, String> batchInfo, String section,
			Long userRequestId, String taxDocType) {
		List<Long> idsList = batchInfo.getValue0();
		Gstr2XDto gstr2x = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
				gstr2x.getTaxperiod(), gstr2x.getGstin());
		try {
			saveToGstnEventStatus.EventEntry(gstr2x.getTaxperiod(),
					gstr2x.getGstin(), 20, groupCode);

			String batch = gson.toJson(gstr2x);
			LOGGER.error("batch {}", batch);
			Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(gstr2x.getGstin(),
					gstr2x.getTaxperiod(), section, groupCode,
					APIConstants.GSTR2X, idsList.size(), taxDocType, 
					0l, userRequestId);
			Long gstnBatchId = saveBatch.getId();
			APIResponse resp = null;
			try {
				saveToGstnEventStatus.EventEntry(gstr2x.getTaxperiod(),
						gstr2x.getGstin(), 30, groupCode);
				resp = gstnServer.gstr2XApiCall(groupCode, batch,
						gstr2x.getGstin(), gstr2x.getTaxperiod(), gstnBatchId);
			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				LOGGER.error("GSTR2X gstn api call exception '%s'", ex.getMessage());
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(gstr2x.getTaxperiod(),
						gstr2x.getGstin(), 60, groupCode);
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}
			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				// NEED TO ADD GSTR2x BATCH TABLE
				batchHandler.updateGstr2xProcBatch(gstnBatchId, groupCode,
						idsList,section);
				refId = batchHandler.updateRefIdAndTxnId(groupCode, gstnBatchId,
						resp);
				txnId = resp.getTxnId();
			} else {
				batchHandler.deleteBatch(gstnBatchId, groupCode);
			}
			oneResp.setGstnBatchId(gstnBatchId);
			oneResp.setRefId(refId);
			oneResp.setTxnId(txnId);
			oneResp.setReturnType(APIConstants.GSTR2X.toUpperCase());
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		return oneResp;
	}

}
