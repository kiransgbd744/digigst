/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2Data;
import com.ey.advisory.app.docs.dto.anx2.Anx2DocumentData;
import com.ey.advisory.app.docs.dto.anx2.SaveAnx2;
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
 * @author Hemasundar.J
 *
 */
@Service("Anx2SaveBatchProcessImpl")
@Slf4j
public class Anx2SaveBatchProcessImpl implements Anx2SectionWiseSaveBatchProcess {

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
		List<SaveAnx2> batches = batchDto.getAnx2();

		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		if (batches != null && !batches.isEmpty()) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 execute is started with {} batches", batches.size());
			}
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				SaveAnx2 anx2 = batches.get(i);
				Triplet<List<Long>, SaveAnx2, String> batchInfo = new Triplet<>(
						idsList, anx2, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveAnx2Batch(batchInfo,
						section);
				retList.addAll(refIds);
			}
		}

		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveAnx2Batch(
			Triplet<List<Long>, SaveAnx2, String> batchInfo, String section) {

		// Return collection.
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, SaveAnx2, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);

		while (true) {
			try {
				Triplet<List<Long>, SaveAnx2, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section);
				retIds.add(refIds);

			} catch (PayloadSizeExceededException ex) {
				LOGGER.error("{} PayloadSizeExceededException Dont worry..., "
						+ "We are handling it by Chunking.", section);
				List<Triplet<List<Long>, SaveAnx2, String>> splitChunks = batchChunk(
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
			Triplet<List<Long>, SaveAnx2, String> batchInfo, String section) {

		List<Long> idsList = batchInfo.getValue0();
		SaveAnx2 anx2 = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
				anx2.getTaxperiod(), anx2.getCgstin());
		try {
			// status code 20 says status as SAVE TO GSTIN INITIATED
			saveToGstnEventStatus.EventEntry(anx2.getTaxperiod(),
					anx2.getCgstin(), 20, groupCode);

			String batch = gson.toJson(anx2);
			LOGGER.error("batch {}", batch);

			/**
			 * Save this bath in Gstr1_save_batch table.
			 */
			Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(anx2.getCgstin(),
					anx2.getTaxperiod(), section, groupCode, APIConstants.ANX2,
					idsList.size(), null, 0l, 0l);
			Long gstnBatchId = saveBatch.getId();
			APIResponse resp = null;
			try {
				// status code 30 says status as SAVE PROCESSED TO GSTIN
				saveToGstnEventStatus.EventEntry(anx2.getTaxperiod(),
						anx2.getCgstin(), 30, groupCode);
				resp = gstnServer.anx2ApiCall(groupCode, batch,
						anx2.getCgstin(), anx2.getTaxperiod(), gstnBatchId);
			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				LOGGER.error("Anx1 gstn api call exception", ex.getMessage());
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(anx2.getTaxperiod(),
						anx2.getCgstin(), 60, groupCode);

				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}

			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				/**
				 * update is_sent_to_gstn in Doc_Header table
				 */
				batchHandler.updateAnx2ProcBatch(gstnBatchId, groupCode, idsList);
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
			oneResp.setReturnType(APIConstants.ANX2.toUpperCase());
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}

		return oneResp;
	}

	private Pair<List<Long>, SaveAnx2> setChunkCtinData(SaveAnx2 anx1,
			List<Anx2Data> cTinList, String section) {

		SaveAnx2 anx = new SaveAnx2();
		List<Long> idsList = new ArrayList<>();

		anx.setCgstin(anx1.getCgstin());
		anx.setTaxperiod(anx1.getTaxperiod());
		
		if (APIConstants.B2B.equalsIgnoreCase(section)) {
			anx.setB2bInvoice(cTinList);
		} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
			anx.setB2baInvoice(cTinList);
		} else if (APIConstants.DE.equalsIgnoreCase(section)) {
			anx.setDeInvoice(cTinList);
		} else if (APIConstants.DEA.equalsIgnoreCase(section)) {
			anx.setDeaInvoice(cTinList);
		} else if (APIConstants.SEZWP.equalsIgnoreCase(section)) {
			anx.setSezwpInvoice(cTinList);
		} else if (APIConstants.SEZWPA.equalsIgnoreCase(section)) {
			anx.setSezwpaInvoice(cTinList);
		} else if (APIConstants.SEZWOP.equalsIgnoreCase(section)) {
			anx.setSezwopInvoice(cTinList);
		} else if (APIConstants.SEZWOPA.equalsIgnoreCase(section)) {
			anx.setSezwopaInvoice(cTinList);
		}

		for (Anx2Data cData : cTinList) {
			for (Anx2DocumentData iData : cData.getInvoiceData()) {
				idsList.add(iData.getDocId());
			}
		}
		return new Pair<>(idsList, anx);
	}

	private Pair<List<Long>, SaveAnx2> setChunkInvData(SaveAnx2 anx2,
			List<Anx2DocumentData> invList, String section) {

		SaveAnx2 anx = new SaveAnx2();
		List<Long> idsList = new ArrayList<>();
		List<Anx2Data> cTinList = new ArrayList<>();

		anx.setCgstin(anx2.getCgstin());
		anx.setTaxperiod(anx2.getTaxperiod());
		Anx2Data cTin = null;
		
		if (APIConstants.B2B.equalsIgnoreCase(section)) {
			anx.setB2bInvoice(cTinList);
			cTin = anx2.getB2bInvoice().get(0);
		} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
			anx.setB2baInvoice(cTinList);
			cTin = anx2.getB2baInvoice().get(0);
		} else if (APIConstants.DE.equalsIgnoreCase(section)) {
			anx.setDeInvoice(cTinList);
			cTin = anx2.getDeInvoice().get(0);
		} else if (APIConstants.DEA.equalsIgnoreCase(section)) {
			anx.setDeaInvoice(cTinList);
			cTin = anx2.getDeaInvoice().get(0);
		} else if (APIConstants.SEZWP.equalsIgnoreCase(section)) {
			anx.setSezwpInvoice(cTinList);
			cTin = anx2.getSezwpInvoice().get(0);
		} else if (APIConstants.SEZWPA.equalsIgnoreCase(section)) {
			anx.setSezwpaInvoice(cTinList);
			cTin = anx2.getSezwpaInvoice().get(0);
		} else if (APIConstants.SEZWOP.equalsIgnoreCase(section)) {
			anx.setSezwopInvoice(cTinList);
			cTin = anx2.getSezwopInvoice().get(0);
		} else if (APIConstants.SEZWOPA.equalsIgnoreCase(section)) {
			anx.setSezwopaInvoice(cTinList);
			cTin = anx2.getSezwopaInvoice().get(0);
		}
		
		

		cTin.setInvoiceData(invList);
		cTinList.add(cTin);

		for (Anx2DocumentData data : invList) {
			idsList.add(data.getDocId());
		}
		return new Pair<>(idsList, anx);
	}

	private List<Triplet<List<Long>, SaveAnx2, String>> batchChunk(
			SaveAnx2 anx2, String groupCode, String section) {

		int ctinSize = 0;
		if (APIConstants.B2B.equalsIgnoreCase(section)) {
			ctinSize = anx2.getB2bInvoice().size();
		} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
			ctinSize = anx2.getB2baInvoice().size();
		} else if (APIConstants.DE.equalsIgnoreCase(section)) {
			ctinSize = anx2.getDeInvoice().size();
		} else if (APIConstants.DEA.equalsIgnoreCase(section)) {
			ctinSize = anx2.getDeaInvoice().size();
		} else if (APIConstants.SEZWP.equalsIgnoreCase(section)) {
			ctinSize = anx2.getSezwpInvoice().size();
		} else if (APIConstants.SEZWPA.equalsIgnoreCase(section)) {
			ctinSize = anx2.getSezwpaInvoice().size();
		} else if (APIConstants.SEZWOP.equalsIgnoreCase(section)) {
			ctinSize = anx2.getSezwopInvoice().size();
		} else if (APIConstants.SEZWOPA.equalsIgnoreCase(section)) {
			ctinSize = anx2.getSezwopaInvoice().size();
		}

		Pair<List<Long>, SaveAnx2> pair1 = null;
		Pair<List<Long>, SaveAnx2> pair2 = null;

		if (ctinSize > 1) {
			List<Anx2Data> cTinList1 = null;
			List<Anx2Data> cTinList2 = null;

			if (APIConstants.B2B.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getB2bInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getB2bInvoice().subList(ctinSize / 2,
						ctinSize);
			} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getB2baInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getB2baInvoice().subList(ctinSize / 2,
						ctinSize);
			} else if (APIConstants.DE.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getDeInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getDeInvoice().subList(ctinSize / 2, ctinSize);
			} else if (APIConstants.DEA.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getDeaInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getDeaInvoice().subList(ctinSize / 2,
						ctinSize);
			} else if (APIConstants.SEZWP.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getSezwpInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getSezwpInvoice().subList(ctinSize / 2,
						ctinSize);
			} else if (APIConstants.SEZWPA.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getSezwpaInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getSezwpaInvoice().subList(ctinSize / 2,
						ctinSize);
			} else if (APIConstants.SEZWOP.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getSezwopInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getSezwopInvoice().subList(ctinSize / 2,
						ctinSize);
			} else if (APIConstants.SEZWOPA.equalsIgnoreCase(section)) {
				cTinList1 = anx2.getSezwopaInvoice().subList(0, ctinSize / 2);
				cTinList2 = anx2.getSezwopaInvoice().subList(ctinSize / 2,
						ctinSize);
			}

			pair1 = setChunkCtinData(anx2, cTinList1, section);
			pair2 = setChunkCtinData(anx2, cTinList2, section);

		} else {
			Anx2Data cTin1 = null;

			if (APIConstants.B2B.equalsIgnoreCase(section)) {
				cTin1 = anx2.getB2bInvoice().get(0);
			} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
				cTin1 = anx2.getB2baInvoice().get(0);
			} else if (APIConstants.DE.equalsIgnoreCase(section)) {
				cTin1 = anx2.getDeInvoice().get(0);
			} else if (APIConstants.DEA.equalsIgnoreCase(section)) {
				cTin1 = anx2.getDeaInvoice().get(0);
			} else if (APIConstants.SEZWP.equalsIgnoreCase(section)) {
				cTin1 = anx2.getSezwpInvoice().get(0);
			} else if (APIConstants.SEZWPA.equalsIgnoreCase(section)) {
				cTin1 = anx2.getSezwpaInvoice().get(0);
			} else if (APIConstants.SEZWOP.equalsIgnoreCase(section)) {
				cTin1 = anx2.getSezwopInvoice().get(0);
			} else if (APIConstants.SEZWOPA.equalsIgnoreCase(section)) {
				cTin1 = anx2.getSezwopaInvoice().get(0);
			}

			int invSize = cTin1.getInvoiceData().size();
			List<Anx2DocumentData> invList1 = cTin1.getInvoiceData()
					.subList(0, invSize / 2);
			List<Anx2DocumentData> invList2 = cTin1.getInvoiceData()
					.subList(invSize / 2, invSize);

			pair1 = setChunkInvData(anx2, invList1, section);
			pair2 = setChunkInvData(anx2, invList2, section);

		}
		List<Triplet<List<Long>, SaveAnx2, String>> batchInfoList = new ArrayList<>();
		batchInfoList.add(
				new Triplet<>(pair1.getValue0(), pair1.getValue1(), groupCode));
		batchInfoList.add(
				new Triplet<>(pair2.getValue0(), pair2.getValue1(), groupCode));
		return batchInfoList;
	}
}
