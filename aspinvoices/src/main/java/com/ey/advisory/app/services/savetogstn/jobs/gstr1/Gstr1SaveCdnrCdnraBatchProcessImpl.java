package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.CDNRInvoices;
import com.ey.advisory.app.docs.dto.CreditDebitNote;
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
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("saveCdnrCdnraBatchProcessImpl")
public class Gstr1SaveCdnrCdnraBatchProcessImpl implements Gstr1SectionWiseSaveBatchProcess {

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
			String groupCode, String section, String operationType,
			Map<Long, Long> orgCanIdsMap, Long retryCount, Long userRequestId, ProcessingContext context) {
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
					section, operationType, orgCanIdsMap, retryCount, userRequestId, origin, context);
			retList.addAll(refIds);
		}

		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveGstr1Batch(
			Triplet<List<Long>, SaveGstr1, String> batchInfo, String section,
			String operationType, Map<Long, Long> orgCanIdsMap,
			Long retryCount, Long userRequestId, String origin, ProcessingContext context) {

		// Return collection.
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, SaveGstr1, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);

		while (true) {
			try {
				Triplet<List<Long>, SaveGstr1, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section,
						operationType, orgCanIdsMap, retryCount, userRequestId, origin, context);
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
			String operationType, Map<Long, Long> orgCanIdsMap,
			Long retryCount, Long userRequestId, String origin, ProcessingContext context) {

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
				LOGGER.debug("Gstr1 SaveToGstn {} section batch json has formed as {}", section, batch);
			}

			/**
			 * Save this bath in Gstr1_save_batch table.
			 */
			
			String returnType = GenUtil.getReturnType(context);

			Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(gstr1.getSgstin(),
					gstr1.getTaxperiod(), section, groupCode,
					returnType, idsList.size(), operationType,
					retryCount, userRequestId, origin);
			Long gstnBatchId = saveBatch.getId();
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
			} catch (java.lang.UnsupportedOperationException ex) {
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(gstr1.getTaxperiod(),
						gstr1.getSgstin(), 60, groupCode, section.toUpperCase());
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}
			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				if (APIConstants.DELETE_FILE_UPLOAD.equalsIgnoreCase(operationType)) {

					batchHandler.updateAutoDraftBatch(gstnBatchId, groupCode,
							idsList, operationType, orgCanIdsMap);
				} else if (APIConstants.DELETE_RESPONSE
						.equalsIgnoreCase(operationType)) {

					batchHandler.updateDeleteResponseBatch(gstnBatchId,
							groupCode, idsList, operationType, orgCanIdsMap);
				} else {
					/**
					 * update is_sent_to_gstn in Doc_Header table
					 */
					if (returnType.equalsIgnoreCase(
							APIConstants.GSTR1A.toUpperCase())) {
						batchHandler.updateOutwardBatchGstr1A(gstnBatchId,
								groupCode, idsList, operationType,
								orgCanIdsMap);
					} else {
						batchHandler.updateOutwardBatch(gstnBatchId, groupCode,
								idsList, operationType, orgCanIdsMap);
					}

				}
				/**
				 * update the RefId and txnId in save_batch table
				 */
				if (returnType
						.equalsIgnoreCase(APIConstants.GSTR1A.toUpperCase())) {
					refId = batchHandler.updateGstr1ARefIdAndTxnId(groupCode,
							gstnBatchId, resp);
				} else {
					refId = batchHandler.updateRefIdAndTxnId(groupCode,
							gstnBatchId, resp);
				}
				
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
		}

		return oneResp;
	}

	private Pair<List<Long>, SaveGstr1> setChunkCtinData(SaveGstr1 gstr1,
			List<CDNRInvoices> cTinList, String section) {

		SaveGstr1 gstr = new SaveGstr1();
		List<Long> idsList = new ArrayList<>();
		gstr.setSgstin(gstr1.getSgstin());
		gstr.setTaxperiod(gstr1.getTaxperiod());
		gstr.setCur_gt(gstr1.getCur_gt());
		gstr.setGt(gstr1.getGt());
		if (APIConstants.CDNR.equals(section)) {
			gstr.setCdnrInvoice(cTinList);
		} else {
			gstr.setCdnraInvoice(cTinList);
		}
		for (CDNRInvoices cData : cTinList) {
			for (CreditDebitNote iData : cData.getCreditDebitNoteDetails()) {
				idsList.add(iData.getDocId());
			}
		}
		return new Pair<>(idsList, gstr);
	}

	private Pair<List<Long>, SaveGstr1> setChunkInvData(SaveGstr1 gstr1,
			List<CreditDebitNote> invList, String section) {

		SaveGstr1 gstr = new SaveGstr1();
		List<Long> idsList = new ArrayList<>();
		List<CDNRInvoices> cTinList = new ArrayList<>();

		gstr.setSgstin(gstr1.getSgstin());
		gstr.setTaxperiod(gstr1.getTaxperiod());
		gstr.setCur_gt(gstr1.getCur_gt());
		gstr.setGt(gstr1.getGt());

		CDNRInvoices cTin = null;
		if (APIConstants.CDNR.equals(section)) {
			gstr.setCdnrInvoice(cTinList);
			cTin = gstr1.getCdnrInvoice().get(0);
		} else {
			gstr.setCdnraInvoice(cTinList);
			cTin = gstr1.getCdnraInvoice().get(0);
		}
		cTin.setCreditDebitNoteDetails(invList);
		cTinList.add(cTin);

		for (CreditDebitNote data : invList) {
			idsList.add(data.getDocId());
		}
		return new Pair<>(idsList, gstr);
	}

	private List<Triplet<List<Long>, SaveGstr1, String>> batchChunk(
			SaveGstr1 gstr1, String groupCode, String section) {

		int ctinSize = 0;
		if (APIConstants.CDNR.equals(section)) {
			ctinSize = gstr1.getCdnrInvoice().size();
		} else {
			ctinSize = gstr1.getCdnraInvoice().size();
		}
		Pair<List<Long>, SaveGstr1> pair1 = null;
		Pair<List<Long>, SaveGstr1> pair2 = null;

		if (ctinSize > 1) {
			List<CDNRInvoices> cTinList1 = null;
			List<CDNRInvoices> cTinList2 = null;
			if (APIConstants.CDNR.equals(section)) {
				cTinList1 = gstr1.getCdnrInvoice().subList(0, ctinSize / 2);
				cTinList2 = gstr1.getCdnrInvoice().subList(ctinSize / 2,
						ctinSize);
			} else {
				cTinList1 = gstr1.getCdnraInvoice().subList(0, ctinSize / 2);
				cTinList2 = gstr1.getCdnraInvoice().subList(ctinSize / 2,
						ctinSize);
			}
			pair1 = setChunkCtinData(gstr1, cTinList1, section);
			pair2 = setChunkCtinData(gstr1, cTinList2, section);

		} else {
			CDNRInvoices cTin1 = null;
			if (APIConstants.CDNR.equals(section)) {
				cTin1 = gstr1.getCdnrInvoice().get(0);
			} else {
				cTin1 = gstr1.getCdnraInvoice().get(0);
			}
			int invSize = cTin1.getCreditDebitNoteDetails().size();
			List<CreditDebitNote> invList1 = cTin1.getCreditDebitNoteDetails()
					.subList(0, invSize / 2);
			List<CreditDebitNote> invList2 = cTin1.getCreditDebitNoteDetails()
					.subList(invSize / 2, invSize);
			pair1 = setChunkInvData(gstr1, invList1, section);
			pair2 = setChunkInvData(gstr1, invList2, section);

		}
		List<Triplet<List<Long>, SaveGstr1, String>> batchInfoList = new ArrayList<>();
		batchInfoList.add(
				new Triplet<>(pair1.getValue0(), pair1.getValue1(), groupCode));
		batchInfoList.add(
				new Triplet<>(pair2.getValue0(), pair2.getValue1(), groupCode));
		return batchInfoList;
	}
}
