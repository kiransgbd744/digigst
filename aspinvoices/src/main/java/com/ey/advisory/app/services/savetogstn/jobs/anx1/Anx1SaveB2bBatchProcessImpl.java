package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2bData;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2bDocumentData;
import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
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

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("anx1SaveB2bBatchProcessImpl")
public class Anx1SaveB2bBatchProcessImpl implements Anx1SectionWiseSaveBatchProcess {


	@Autowired
	@Qualifier("batchHandler")
	private BatchHandler batchHandler;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Override
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section) {
		List<List<Long>> batchIdsList = batchDto.getIdsList();
		List<SaveAnx1> batches = batchDto.getAnx1();

		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		if (batches != null && !batches.isEmpty()) {
			LOGGER.debug("execute is started with {} batches", batches.size());
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				SaveAnx1 anx1 = batches.get(i);
				Triplet<List<Long>, SaveAnx1, String> batchInfo = new Triplet<>(
						idsList, anx1, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveAnx1Batch(batchInfo,
						section);
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
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
				anx1.getTaxperiod(), anx1.getSgstin());
		try {
			// status code 20 says status as SAVE TO GSTIN INITIATED
			saveToGstnEventStatus.EventEntry(anx1.getTaxperiod(),
					anx1.getSgstin(), 20, groupCode);

			String batch = gson.toJson(anx1);
			LOGGER.error("batch {}", batch);

			/**
			 * Save this bath in Gstr1_save_batch table.
			 */
			Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(anx1.getSgstin(),
					anx1.getTaxperiod(), section, groupCode, APIConstants.ANX1,
					idsList.size(), null, 0l, 0l);
			Long gstnBatchId = saveBatch.getId();
			APIResponse resp = null;
			try {
				// status code 30 says status as SAVE PROCESSED TO GSTIN
				saveToGstnEventStatus.EventEntry(anx1.getTaxperiod(),
						anx1.getSgstin(), 30, groupCode);
				resp = gstnServer.anx1ApiCall(groupCode, batch,
						anx1.getSgstin(), anx1.getTaxperiod(), gstnBatchId);
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
				 * update is_sent_to_gstn in Doc_Header table
				 */
				batchHandler.updateOutwardBatch(gstnBatchId, groupCode, idsList, null, null);
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
		}

		return oneResp;
	}

	private Pair<List<Long>, SaveAnx1> setChunkCtinData(SaveAnx1 anx1,
			List<Anx1B2bData> cTinList, String section) {

		SaveAnx1 anx = new SaveAnx1();
		List<Long> idsList = new ArrayList<>();

		anx.setSgstin(anx1.getSgstin());
		anx.setTaxperiod(anx1.getTaxperiod());
		anx.setB2bInvoice(cTinList);

		for (Anx1B2bData cData : cTinList) {
			for (Anx1B2bDocumentData iData : cData.getDocs()) {
				idsList.add(iData.getDocId());
			}
		}
		return new Pair<>(idsList, anx);
	}

	private Pair<List<Long>, SaveAnx1> setChunkInvData(SaveAnx1 anx1,
			List<Anx1B2bDocumentData> invList, String section) {

		SaveAnx1 anx = new SaveAnx1();
		List<Long> idsList = new ArrayList<>();
		List<Anx1B2bData> cTinList = new ArrayList<>();

		anx.setSgstin(anx1.getSgstin());
		anx.setTaxperiod(anx1.getTaxperiod());

		anx.setB2bInvoice(cTinList);
		Anx1B2bData cTin = anx1.getB2bInvoice().get(0);

		cTin.setDocs(invList);
		cTinList.add(cTin);

		for (Anx1B2bDocumentData data : invList) {
			idsList.add(data.getDocId());
		}
		return new Pair<>(idsList, anx);
	}

	private List<Triplet<List<Long>, SaveAnx1, String>> batchChunk(
			SaveAnx1 anx1, String groupCode, String section) {

		int ctinSize = anx1.getB2bInvoice().size();

		Pair<List<Long>, SaveAnx1> pair1 = null;
		Pair<List<Long>, SaveAnx1> pair2 = null;

		if (ctinSize > 1) {
			List<Anx1B2bData> cTinList1 = anx1.getB2bInvoice().subList(0,
					ctinSize / 2);
			List<Anx1B2bData> cTinList2 = anx1.getB2bInvoice()
					.subList(ctinSize / 2, ctinSize);

			pair1 = setChunkCtinData(anx1, cTinList1, section);
			pair2 = setChunkCtinData(anx1, cTinList2, section);

		} else {
			Anx1B2bData cTin1 = anx1.getB2bInvoice().get(0);
			int invSize = cTin1.getDocs().size();
			List<Anx1B2bDocumentData> invList1 = cTin1.getDocs().subList(0,
					invSize / 2);
			List<Anx1B2bDocumentData> invList2 = cTin1.getDocs()
					.subList(invSize / 2, invSize);
			pair1 = setChunkInvData(anx1, invList1, section);
			pair2 = setChunkInvData(anx1, invList2, section);

		}
		List<Triplet<List<Long>, SaveAnx1, String>> batchInfoList = new ArrayList<>();
		batchInfoList.add(
				new Triplet<>(pair1.getValue0(), pair1.getValue1(), groupCode));
		batchInfoList.add(
				new Triplet<>(pair2.getValue0(), pair2.getValue1(), groupCode));
		return batchInfoList;
	}
}
