package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.docs.dto.itc04.Itc04Dto;
import com.ey.advisory.app.docs.dto.itc04.Itc04M2jwDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5ADto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5BDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5CDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.google.gson.Gson;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Itc04SaveBatchProcessImpl")
public class Itc04SaveBatchProcessImpl implements Itc04SectionWiseSaveBatchProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(Itc04SaveBatchProcessImpl.class);

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
		List<Itc04Dto> batches = batchDto.getItc04();
		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		LOGGER.debug("execute is started with {} batches", batches.size());
		if (batches != null && !batches.isEmpty()) {
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				Itc04Dto itc04 = batches.get(i);
				Triplet<List<Long>, Itc04Dto, String> batchInfo = new Triplet<>(idsList, itc04, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveItc04Batch(batchInfo, section, userRequestId, taxDocType);
				retList.addAll(refIds);
			}
		}
		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveItc04Batch(Triplet<List<Long>, Itc04Dto, String> batchInfo, String section,
			Long userRequestId, String taxDocType) {
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, Itc04Dto, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);
		while (true) {
			try {
				Triplet<List<Long>, Itc04Dto, String> curBatch = batchStack.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section, userRequestId, taxDocType);
				retIds.add(refIds);
			} catch (PayloadSizeExceededException ex) {
				LOGGER.error("{} PayloadSizeExceededException Dont worry..., " + "We are handling it by Chunking.",
						section);
				/*
				 * List<Triplet<List<Long>, Itc04Dto, String>> splitChunks =
				 * batchChunk( batchInfo.getValue1(), batchInfo.getValue2(),
				 * section); splitChunks.forEach(obj -> batchStack.push(obj));
				 */
			}

			if (batchStack.isEmpty()) {
				LOGGER.info("End of {} batches", section);
				break;
			}
		}
		return retIds;
	}

	private SaveToGstnBatchRefIds process(Triplet<List<Long>, Itc04Dto, String> batchInfo, String section,
			Long userRequestId, String taxDocType) {
		List<Long> idsList = batchInfo.getValue0();
		Itc04Dto itc04 = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}", itc04.getTaxperiod(), itc04.getGstin());
		try {
			saveToGstnEventStatus.Itc04EventEntry(itc04.getTaxperiod(), itc04.getGstin(), 20, groupCode);

			String batch = gson.toJson(itc04);
			LOGGER.debug("batch {}", batch);
			Long gstnBatchId = batchHandler.itc04SaveBatch(itc04.getGstin(), itc04.getTaxperiod(), section, groupCode,
					APIConstants.ITC04, idsList.size(), taxDocType, 0l, userRequestId);
			APIResponse resp = null;
			try {
				saveToGstnEventStatus.Itc04EventEntry(itc04.getTaxperiod(), itc04.getGstin(), 30, groupCode);
				resp = gstnServer.itc04ApiCall(groupCode, batch, itc04.getGstin(), itc04.getTaxperiod(), gstnBatchId);
			} catch (java.lang.UnsupportedOperationException | AppException ex) {
				LOGGER.error("ITC04 gstn api call exception", ex.getMessage());
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.Itc04EventEntry(itc04.getTaxperiod(), itc04.getGstin(), 60, groupCode);
				throw new AppException(ex, "{} error while saving batch to Gstn");
			}
			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				// NEED TO ADD ITC04 BATCH TABLE
				batchHandler.updateItc04ProcBatch(gstnBatchId, groupCode, idsList, section);
				refId = batchHandler.updateRefIdAndTxnId(groupCode, gstnBatchId, resp);
				txnId = resp.getTxnId();
			} else {
				LOGGER.error("ITC04 gstn api Response is null for the bacthid : {}",gstnBatchId );
				batchHandler.deleteBatch(gstnBatchId, groupCode);
			}
			oneResp.setGstnBatchId(gstnBatchId);
			oneResp.setRefId(refId);
			oneResp.setTxnId(txnId);
			oneResp.setReturnType(APIConstants.ITC04.toUpperCase());
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId, refId);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		return oneResp;
	}

	/*private List<Triplet<List<Long>, Itc04Dto, String>> batchChunk(Itc04Dto saveItc04, String groupCode,
			String section) {
		int ctinSize = 0;
		if (APIConstants.M2JW.equalsIgnoreCase(section)) {
			ctinSize = saveItc04.getM2jw().size();
		} else if (APIConstants.TABLE5A.equalsIgnoreCase(section)) {
			ctinSize = saveItc04.getTable5a().size();
		} else if (APIConstants.TABLE5B.equalsIgnoreCase(section)) {
			ctinSize = saveItc04.getTable5b().size();
		} else if (APIConstants.TABLE5C.equalsIgnoreCase(section)) {
			ctinSize = saveItc04.getTable5c().size();
		}

		Pair<List<Long>, Itc04Dto> pair1 = null;
		Pair<List<Long>, Itc04Dto> pair2 = null;

		// if (ctinSize > 1) {
		if (APIConstants.M2JW.equalsIgnoreCase(section)) {
			List<Itc04M2jwDto> m2jwList1 = saveItc04.getM2jw().subList(0, ctinSize / 2);
			List<Itc04M2jwDto> m2jwList2 = saveItc04.getM2jw().subList(ctinSize / 2, ctinSize);
			pair1 = setChunkCtinData(saveItc04, m2jwList1, null, null, null, section);
			pair2 = setChunkCtinData(saveItc04, m2jwList2, null, null, null, section);
		} else if (APIConstants.TABLE5A.equalsIgnoreCase(section)) {
			List<Itc04Table5ADto> table5aList1 = saveItc04.getTable5a().subList(0, ctinSize / 2);
			List<Itc04Table5ADto> table5aList2 = saveItc04.getTable5a().subList(ctinSize / 2, ctinSize);
			pair1 = setChunkCtinData(saveItc04, null, table5aList1, null, null, section);
			pair2 = setChunkCtinData(saveItc04, null, table5aList2, null, null, section);
		} else if (APIConstants.TABLE5B.equalsIgnoreCase(section)) {
			List<Itc04Table5BDto> table5bList1 = saveItc04.getTable5b().subList(0, ctinSize / 2);
			List<Itc04Table5BDto> table5bList2 = saveItc04.getTable5b().subList(ctinSize / 2, ctinSize);
			pair1 = setChunkCtinData(saveItc04, null, null, table5bList1, null, section);
			pair2 = setChunkCtinData(saveItc04, null, null, table5bList2, null, section);
		} else if (APIConstants.TABLE5C.equalsIgnoreCase(section)) {
			List<Itc04Table5CDto> table5cList1 = saveItc04.getTable5c().subList(0, ctinSize / 2);
			List<Itc04Table5CDto> table5cList2 = saveItc04.getTable5c().subList(ctinSize / 2, ctinSize);
			pair1 = setChunkCtinData(saveItc04, null, null, null, table5cList1, section);
			pair2 = setChunkCtinData(saveItc04, null, null, null, table5cList2, section);
		}
		// }
		List<Triplet<List<Long>, Itc04Dto, String>> batchInfoList = new ArrayList<>();
		batchInfoList.add(new Triplet<>(pair1.getValue0(), pair1.getValue1(), groupCode));
		batchInfoList.add(new Triplet<>(pair2.getValue0(), pair2.getValue1(), groupCode));
		return batchInfoList;
	}

	private Pair<List<Long>, Itc04Dto> setChunkCtinData(Itc04Dto saveItc04, List<Itc04M2jwDto> m2jwList,
			List<Itc04Table5ADto> table5a, List<Itc04Table5BDto> table5b, List<Itc04Table5CDto> table5c,
			String section) {
		return null;
	}
*/
}
