package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8SaveDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SummaryBatchHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr6.Gstr6SectionWiseSaveBatchProcess;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("Gstr8SaveBatchProcessImpl")
public class Gstr8SaveBatchProcessImpl
		implements Gstr6SectionWiseSaveBatchProcess {

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("batchHandler")
	private BatchHandler batchHandler;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("SummaryBatchHandler")
	private SummaryBatchHandler sumBatchHandler;

	@Override
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section, Long userRequestId,
			String taxDocType, ProcessingContext context) {
		List<List<Long>> batchIdsList = batchDto.getIdsList();
		List<Gstr8SaveDto> batches = batchDto.getGstr8();
		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		LOGGER.debug("execute is started with {} batches", batches.size());
		if (batches != null && !batches.isEmpty()) {
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				Gstr8SaveDto gstr8 = batches.get(i);
				Triplet<List<Long>, Gstr8SaveDto, String> batchInfo = new Triplet<>(
						idsList, gstr8, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveGstr8Batch(batchInfo,
						section, userRequestId, taxDocType);
				retList.addAll(refIds);
			}
		}
		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveGstr8Batch(
			Triplet<List<Long>, Gstr8SaveDto, String> batchInfo, String section,
			Long userRequestId, String taxDocType) {
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, Gstr8SaveDto, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);
		while (true) {
			try {
				Triplet<List<Long>, Gstr8SaveDto, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section,
						userRequestId, taxDocType);
				retIds.add(refIds);
			} catch (PayloadSizeExceededException ex) {
				List<Triplet<List<Long>, Gstr8SaveDto, String>> splitChunks = batchChunk(
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
			Triplet<List<Long>, Gstr8SaveDto, String> batchInfo, String section,
			Long userRequestId, String taxDocType) {
		List<Long> idsList = batchInfo.getValue0();
		Gstr8SaveDto gstr8 = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}", gstr8.getFp(),
				gstr8.getGstin());
		try {
			saveToGstnEventStatus.EventEntry(gstr8.getFp(), gstr8.getGstin(),
					20, groupCode);

			String batch = gson.toJson(gstr8);
			LOGGER.error("batch {}", batch);
			/*
			 * Long gstnBatchId = batchHandler.saveBatch(gstr7.getGstin(),
			 * gstr7.getTaxperiod(), section, groupCode, APIConstants.GSTR7,
			 * idsList.size(), taxDocType, 0l, userRequestId);
			 */
			Long gstnBatchId = null;
			Long hMaxId = null;

			List<Object[]> objs = batchSaveStatusRepository
					.selectMaxSumryRecord(gstr8.getGstin(), gstr8.getFp(),
							APIConstants.GSTR8.toUpperCase(),
							section.toUpperCase());

			Object[] obj = objs.get(0);
			if (obj != null && obj[0] != null) {
				gstnBatchId = obj[0] != null ? Long.parseLong(obj[0].toString())
						: null;
				hMaxId = obj[1] != null ? Long.parseLong(obj[1].toString())
						: null;
			} else {
				LOGGER.error(
						"No Valid Gstr7 Batch Id found for the section {}, "
								+ "Proc has not executed properly.",
						section);
				return null;
			}
			APIResponse resp = null;
			try {
				saveToGstnEventStatus.EventEntry(gstr8.getFp(),
						gstr8.getGstin(), 30, groupCode);
				resp = gstnServer.gstr8SaveApiCall(batch, gstr8.getGstin(),
						gstr8.getFp(), gstnBatchId);
			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				LOGGER.error("GSTR8 gstn api call exception", ex.getMessage());
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(gstr8.getFp(),
						gstr8.getGstin(), 60, groupCode);
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}
			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				// NEED TO ADD GSTR8 BATCH TABLE
				sumBatchHandler.gstr8BatchUpdate(gstr8.getFp(),
						gstr8.getGstin(), gstnBatchId, groupCode, idsList,
						section, hMaxId, taxDocType);

				refId = batchHandler.updateRefIdAndTxnId(groupCode, gstnBatchId,
						resp);
				txnId = resp.getTxnId();
			} else {
				batchHandler.deleteBatch(gstnBatchId, groupCode);
			}
			oneResp.setGstnBatchId(gstnBatchId);
			oneResp.setRefId(refId);
			oneResp.setTxnId(txnId);
			oneResp.setReturnType(APIConstants.GSTR8.toUpperCase());
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		return oneResp;
	}

	private List<Triplet<List<Long>, Gstr8SaveDto, String>> batchChunk(
			Gstr8SaveDto gstr8, String groupCode, String section) {
		LOGGER.error("{} Summary Json Exceeded Max Size 5MB ", section);
		return new ArrayList<>();
	}

}
