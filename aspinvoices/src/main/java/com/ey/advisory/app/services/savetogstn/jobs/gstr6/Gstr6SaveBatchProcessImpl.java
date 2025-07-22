package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

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
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2baDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2baInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnNtData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnaDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnaNtData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDetailsDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdElglstDto;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.BatchHandler;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;

/**
 * 
 * @author Sri Bhavya
 *
 */

@Service("Gstr6SaveBatchProcessImpl")
public class Gstr6SaveBatchProcessImpl implements Gstr6SectionWiseSaveBatchProcess {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SaveBatchProcessImpl.class);

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
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section,Long userRequestId,String taxDocType, ProcessingContext context) {
		List<List<Long>> batchIdsList = batchDto.getIdsList();
		List<SaveGstr6> batches = batchDto.getGstr6();
		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		LOGGER.debug("execute is started with {} batches", batches.size());
		if (batches != null && !batches.isEmpty()) {
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				SaveGstr6 gstr6 = batches.get(i);
				Triplet<List<Long>, SaveGstr6, String> batchInfo = new Triplet<>(
						idsList, gstr6, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveGstr6Batch(batchInfo,
						section,userRequestId,taxDocType);
				retList.addAll(refIds);
			}
		}
		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveGstr6Batch(
			Triplet<List<Long>, SaveGstr6, String> batchInfo, String section, 
			Long userRequestId, String taxDocType) {
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, SaveGstr6, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);
		while (true) {
			try {
				Triplet<List<Long>, SaveGstr6, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section,
						userRequestId,taxDocType);
				retIds.add(refIds);
			} catch (PayloadSizeExceededException ex) {
				LOGGER.error("{} PayloadSizeExceededException Dont worry..., "
						+ "We are handling it by Chunking.", section);
				List<Triplet<List<Long>, SaveGstr6, String>> splitChunks = batchChunk(
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
			Triplet<List<Long>, SaveGstr6, String> batchInfo, String section, 
			Long userRequestId,String taxDocType) {
		List<Long> idsList = batchInfo.getValue0();
		SaveGstr6 gstr6 = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
				gstr6.getTaxperiod(), gstr6.getGstin());
		try {
			saveToGstnEventStatus.EventEntry(gstr6.getTaxperiod(),
					gstr6.getGstin(), 20, groupCode);

			String batch = gson.toJson(gstr6);
			LOGGER.error("batch {}", batch);
			Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(gstr6.getGstin(),
					gstr6.getTaxperiod(), section, groupCode,
					APIConstants.GSTR6, idsList.size(), taxDocType, 
					0l, userRequestId);
			Long gstnBatchId = saveBatch.getId();
			APIResponse resp = null;
			try {
				saveToGstnEventStatus.EventEntry(gstr6.getTaxperiod(),
						gstr6.getGstin(), 30, groupCode);
				resp = gstnServer.gstr6ApiCall(groupCode, batch,
						gstr6.getGstin(), gstr6.getTaxperiod(), gstnBatchId);
			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				LOGGER.error("GSTR6 gstn api call exception", ex.getMessage());
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(gstr6.getTaxperiod(),
						gstr6.getGstin(), 60, groupCode);
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}
			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				// NEED TO ADD GSTR6 BATCH TABLE
				batchHandler.updateGstr6ProcBatch(gstnBatchId, groupCode,
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
			oneResp.setReturnType(APIConstants.GSTR6.toUpperCase());
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		return oneResp;
	}

	private List<Triplet<List<Long>, SaveGstr6, String>> batchChunk(
			SaveGstr6 saveGstr6, String groupCode, String section) {
		int ctinSize = 0;
		if (APIConstants.B2B.equalsIgnoreCase(section)) {
			ctinSize = saveGstr6.getB2bInvoice().size();
		} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
			ctinSize = saveGstr6.getB2baInvoice().size();
		} else if (APIConstants.CDN.equalsIgnoreCase(section)) {
			ctinSize = saveGstr6.getCdnInvoice().size();
		} else if (APIConstants.CDNA.equalsIgnoreCase(section)) {
			ctinSize = saveGstr6.getCdnaInvoice().size();
		} else if (APIConstants.ISD.equalsIgnoreCase(section)) {
			ctinSize = saveGstr6.getIsdInvoice().getInelglst().size();
		} else if (APIConstants.ISDA.equalsIgnoreCase(section)) {
			ctinSize = saveGstr6.getIsdaInvoice().size();
		}

		Pair<List<Long>, SaveGstr6> pair1 = null;
		Pair<List<Long>, SaveGstr6> pair2 = null;
		Pair<List<Long>, SaveGstr6> pair3 = null;
		Pair<List<Long>, SaveGstr6> pair4 = null;

		if (ctinSize > 1) {
			if (APIConstants.B2B.equalsIgnoreCase(section)) {
				List<Gstr6B2bDto> b2bList1 = saveGstr6.getB2bInvoice()
						.subList(0, ctinSize / 2);
				List<Gstr6B2bDto> b2bList2 = saveGstr6.getB2bInvoice()
						.subList(ctinSize / 2, ctinSize);
				pair1 = setChunkCtinData(saveGstr6, b2bList1, null, null, null,
						null,null,section);
				pair2 = setChunkCtinData(saveGstr6, b2bList2, null, null, null,
						null,null,section);
			} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
				List<Gstr6B2baDto> b2baList1 = saveGstr6.getB2baInvoice()
						.subList(0, ctinSize / 2);
				List<Gstr6B2baDto> b2baList2 = saveGstr6.getB2baInvoice()
						.subList(ctinSize / 2, ctinSize);
				pair1 = setChunkCtinData(saveGstr6, null, b2baList1, null, null,
						null,null,section);
				pair2 = setChunkCtinData(saveGstr6, null, b2baList2, null, null,
						null,null,section);
			} else if (APIConstants.CDN.equalsIgnoreCase(section)) {
				List<Gstr6CdnDto> cdnList1 = saveGstr6.getCdnInvoice()
						.subList(0, ctinSize / 2);
				List<Gstr6CdnDto> cdnList2 = saveGstr6.getCdnInvoice()
						.subList(ctinSize / 2, ctinSize);
				pair1 = setChunkCtinData(saveGstr6, null, null, cdnList1, null,
						null,null, section);
				pair2 = setChunkCtinData(saveGstr6, null, null, cdnList2, null,
						null,null, section);
			} else if (APIConstants.CDNA.equalsIgnoreCase(section)) {
				List<Gstr6CdnaDto> cdnaList1 = saveGstr6.getCdnaInvoice()
						.subList(0, ctinSize / 2);
				List<Gstr6CdnaDto> cdnaList2 = saveGstr6.getCdnaInvoice()
						.subList(ctinSize / 2, ctinSize);
				pair1 = setChunkCtinData(saveGstr6, null, null, null, cdnaList1,
						null,null,section);
				pair2 = setChunkCtinData(saveGstr6, null, null, null, cdnaList2,
						null,null,section);
			} else if (APIConstants.ISD.equalsIgnoreCase(section) ||
					APIConstants.ISDA.equalsIgnoreCase(section)) {
				Gstr6IsdDetailsDto isdData = saveGstr6.getIsdInvoice();
				// Elglst Data
				int invSize = isdData.getElglst().size();
				List<Gstr6IsdElglstDto> invList1 = isdData.getElglst()
						.subList(0, invSize / 2);
				List<Gstr6IsdElglstDto> invList2 = isdData.getElglst()
						.subList(invSize / 2, invSize);
				pair1 = setChunkCtinData(saveGstr6, null, null, null, null,
						invList1, null, section);
				pair2 = setChunkCtinData(saveGstr6, null, null, null, null,
						invList2, null, section);

				// InElglst Data
				int invSize1 = isdData.getInelglst().size();
				List<Gstr6IsdElglstDto> invList1Elgst = isdData.getInelglst()
						.subList(0, invSize1 / 2);
				List<Gstr6IsdElglstDto> invList2Elgst = isdData.getInelglst()
						.subList(invSize1 / 2, invSize1);

				pair3 = setChunkInvData(saveGstr6, null, null, null, null, null,
						invList1Elgst, section);
				pair4 = setChunkInvData(saveGstr6, null, null, null, null, null,
						invList2Elgst, section);
			} 
		} else {
			if (APIConstants.B2B.equalsIgnoreCase(section)) {
				Gstr6B2bDto b2bData = saveGstr6.getB2bInvoice().get(0);
				int invSize = b2bData.getInv().size();
				List<Gstr6B2bInvoiceData> invList1 = b2bData.getInv().subList(0,
						invSize / 2);
				List<Gstr6B2bInvoiceData> invList2 = b2bData.getInv()
						.subList(invSize / 2, invSize);
				pair1 = setChunkInvData(saveGstr6, invList1, null, null, null,
						null, null, section);
				pair2 = setChunkInvData(saveGstr6, invList2, null, null, null,
						null, null, section);

			} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
				Gstr6B2baDto b2baData = saveGstr6.getB2baInvoice().get(0);
				int invSize = b2baData.getInv().size();
				List<Gstr6B2baInvoiceData> invList1 = b2baData.getInv()
						.subList(0, invSize / 2);
				List<Gstr6B2baInvoiceData> invList2 = b2baData.getInv()
						.subList(invSize / 2, invSize);
				pair1 = setChunkInvData(saveGstr6, null, invList1, null, null,
						null, null, section);
				pair2 = setChunkInvData(saveGstr6, null, invList2, null, null,
						null, null, section);

			} else if (APIConstants.CDN.equalsIgnoreCase(section)) {
				Gstr6CdnDto cdnData = saveGstr6.getCdnInvoice().get(0);
				int invSize = cdnData.getNt().size();
				List<Gstr6CdnNtData> invList1 = cdnData.getNt().subList(0,
						invSize / 2);
				List<Gstr6CdnNtData> invList2 = cdnData.getNt()
						.subList(invSize / 2, invSize);
				pair1 = setChunkInvData(saveGstr6, null, null, invList1, null,
						null, null, section);
				pair2 = setChunkInvData(saveGstr6, null, null, invList2, null,
						null, null, section);

			} else if (APIConstants.CDNA.equalsIgnoreCase(section)) {
				Gstr6CdnaDto cdnaData = saveGstr6.getCdnaInvoice().get(0);
				int invSize = cdnaData.getNt().size();
				List<Gstr6CdnaNtData> invList1 = cdnaData.getNt().subList(0,
						invSize / 2);
				List<Gstr6CdnaNtData> invList2 = cdnaData.getNt()
						.subList(invSize / 2, invSize);
				pair1 = setChunkInvData(saveGstr6, null, null, null, invList1,
						null, null, section);
				pair2 = setChunkInvData(saveGstr6, null, null, null, invList2,
						null, null, section);

			} else if ((APIConstants.ISD.equalsIgnoreCase(section))
					|| (APIConstants.ISDA.equalsIgnoreCase(section))) {
				Gstr6IsdDetailsDto isdData = saveGstr6.getIsdInvoice();
				// Elglst Data
				int invSize = isdData.getElglst().size();
				List<Gstr6IsdElglstDto> invList1 = isdData.getElglst()
						.subList(0, invSize / 2);
				List<Gstr6IsdElglstDto> invList2 = isdData.getElglst()
						.subList(invSize / 2, invSize);
				pair1 = setChunkInvData(saveGstr6, null, null, null, null,
						invList1, null, section);
				pair2 = setChunkInvData(saveGstr6, null, null, null, null,
						invList2, null, section);

				// InElglst Data
				int invSize1 = isdData.getInelglst().size();
				List<Gstr6IsdElglstDto> invList1Elgst = isdData.getInelglst()
						.subList(0, invSize1 / 2);
				List<Gstr6IsdElglstDto> invList2Elgst = isdData.getInelglst()
						.subList(invSize1 / 2, invSize1);

				pair3 = setChunkInvData(saveGstr6, null, null, null, null, null,
						invList1Elgst, section);
				pair4 = setChunkInvData(saveGstr6, null, null, null, null, null,
						invList2Elgst, section);
			}
		}
		List<Triplet<List<Long>, SaveGstr6, String>> batchInfoList = new ArrayList<>();
		if ((APIConstants.ISD.equalsIgnoreCase(section))
				|| (APIConstants.ISDA.equalsIgnoreCase(section))) {
			batchInfoList.add(new Triplet<>(pair1.getValue0(),
					pair1.getValue1(), groupCode));
			batchInfoList.add(new Triplet<>(pair2.getValue0(),
					pair2.getValue1(), groupCode));

			batchInfoList.add(new Triplet<>(pair3.getValue0(),
					pair3.getValue1(), groupCode));
			batchInfoList.add(new Triplet<>(pair4.getValue0(),
					pair4.getValue1(), groupCode));

		} else {
			batchInfoList.add(new Triplet<>(pair1.getValue0(),
					pair1.getValue1(), groupCode));
			batchInfoList.add(new Triplet<>(pair2.getValue0(),
					pair2.getValue1(), groupCode));
		}

		return batchInfoList;
	}

	private Pair<List<Long>, SaveGstr6> setChunkInvData(SaveGstr6 saveGstr6,
			List<Gstr6B2bInvoiceData> b2bInvList,
			List<Gstr6B2baInvoiceData> b2baInvList,
			List<Gstr6CdnNtData> cdnInvList, List<Gstr6CdnaNtData> cdnaInvList,
			List<Gstr6IsdElglstDto> isdElgstList,
			List<Gstr6IsdElglstDto> isdInElglstList, String section) {
		SaveGstr6 gstr6 = new SaveGstr6();
		List<Long> idsList = new ArrayList<>();

		gstr6.setGstin(saveGstr6.getGstin());
		gstr6.setTaxperiod(saveGstr6.getTaxperiod());

		if (APIConstants.B2B.equalsIgnoreCase(section)) {
			List<Gstr6B2bDto> b2binList = new ArrayList<>();
			Gstr6B2bDto b2bIn = null;

			gstr6.setB2bInvoice(b2binList);
			b2bIn = saveGstr6.getB2bInvoice().get(0);

			b2bIn.setInv(b2bInvList);
			b2binList.add(b2bIn);

			for (Gstr6B2bInvoiceData data : b2bInvList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr6);
		} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
			List<Gstr6B2baDto> b2bainList = new ArrayList<>();
			Gstr6B2baDto b2baIn = null;

			gstr6.setB2baInvoice(b2bainList);
			b2baIn = saveGstr6.getB2baInvoice().get(0);

			b2baIn.setInv(b2baInvList);
			b2bainList.add(b2baIn);

			for (Gstr6B2baInvoiceData data : b2baInvList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr6);
		} else if (APIConstants.CDN.equalsIgnoreCase(section)) {
			List<Gstr6CdnDto> cdninList = new ArrayList<>();
			Gstr6CdnDto cdnIn = null;

			gstr6.setCdnInvoice(cdninList);
			cdnIn = saveGstr6.getCdnInvoice().get(0);

			cdnIn.setNt(cdnInvList);
			cdninList.add(cdnIn);

			for (Gstr6CdnNtData data : cdnInvList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr6);
		} else if (APIConstants.CDNA.equalsIgnoreCase(section)) {
			List<Gstr6CdnaDto> cdnainList = new ArrayList<>();
			Gstr6CdnaDto cdnaIn = null;

			gstr6.setCdnaInvoice(cdnainList);
			cdnaIn = saveGstr6.getCdnaInvoice().get(0);

			cdnaIn.setNt(cdnaInvList);
			cdnainList.add(cdnaIn);

			for (Gstr6CdnaNtData data : cdnaInvList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr6);
		} else if (APIConstants.CDNA.equalsIgnoreCase(section)) {
			List<Gstr6CdnaDto> cdnainList = new ArrayList<>();
			Gstr6CdnaDto cdnaIn = null;

			gstr6.setCdnaInvoice(cdnainList);
			cdnaIn = saveGstr6.getCdnaInvoice().get(0);

			cdnaIn.setNt(cdnaInvList);
			cdnainList.add(cdnaIn);

			for (Gstr6CdnaNtData data : cdnaInvList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr6);
		} else if ((APIConstants.ISD.equalsIgnoreCase(section))
				|| (APIConstants.ISDA.equalsIgnoreCase(section))) {			
			Gstr6IsdDetailsDto isdIn = null;
			
			gstr6.setIsdInvoice(isdIn);
			isdIn = saveGstr6.getIsdInvoice();

			isdIn.setElglst(isdElgstList);
			isdIn.setInelglst(isdInElglstList);
			

			for (Gstr6IsdElglstDto data : isdElgstList) {
				idsList.add(data.getDocId());
			}
			for (Gstr6IsdElglstDto data : isdInElglstList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr6);
		}
		return new Pair<>(idsList, gstr6);
	}

	private Pair<List<Long>, SaveGstr6> setChunkCtinData(SaveGstr6 saveGstr6,
			List<Gstr6B2bDto> b2bList, List<Gstr6B2baDto> b2baList,
			List<Gstr6CdnDto> cdnList, List<Gstr6CdnaDto> cdnaList,
			List<Gstr6IsdElglstDto> isdElgstList,
			List<Gstr6IsdElglstDto> isdInElglstList, String section) {
		SaveGstr6 gstr6 = new SaveGstr6();
		List<Long> idsList = new ArrayList<>();

		gstr6.setGstin(saveGstr6.getGstin());
		gstr6.setTaxperiod(saveGstr6.getTaxperiod());

		if ((APIConstants.B2B.equalsIgnoreCase(section))
				&& (b2bList != null && !b2bList.isEmpty())) {
			gstr6.setB2bInvoice(b2bList);
			for (Gstr6B2bDto cData : b2bList) {
				for (Gstr6B2bInvoiceData iData : cData.getInv()) {
					idsList.add(iData.getDocId());
				}
			}
		} else if ((APIConstants.B2BA.equalsIgnoreCase(section))
				&& (b2baList != null && !b2baList.isEmpty())) {
			gstr6.setB2baInvoice(b2baList);
			for (Gstr6B2baDto cData : b2baList) {
				for (Gstr6B2baInvoiceData iData : cData.getInv()) {
					idsList.add(iData.getDocId());
				}
			}
		} else if ((APIConstants.CDN.equalsIgnoreCase(section))
				&& (cdnList != null && !cdnList.isEmpty())) {
			gstr6.setCdnInvoice(cdnList);
			for (Gstr6CdnDto cData : cdnList) {
				for (Gstr6CdnNtData iData : cData.getNt()) {
					idsList.add(iData.getDocId());
				}
			}
		} else if ((APIConstants.CDNA.equalsIgnoreCase(section))
				&& (cdnaList != null && !cdnaList.isEmpty())) {
			gstr6.setCdnaInvoice(cdnaList);
			for (Gstr6CdnaDto cData : cdnaList) {
				for (Gstr6CdnaNtData iData : cData.getNt()) {
					idsList.add(iData.getDocId());
				}
			}
		} else if ((APIConstants.ISD.equalsIgnoreCase(section))
				|| (APIConstants.ISDA.equalsIgnoreCase(section)) ) {
			
			Gstr6IsdDetailsDto isdInvoice = new Gstr6IsdDetailsDto();
			isdInvoice.setElglst(isdElgstList);
			isdInvoice.setInelglst(isdInElglstList);
			
			gstr6.setIsdInvoice(isdInvoice);
			
				for (Gstr6IsdElglstDto iData : isdElgstList) {
					idsList.add(iData.getDocId());
				}
				for (Gstr6IsdElglstDto iData : isdInElglstList) {
					idsList.add(iData.getDocId());
				}
			
		}
		return new Pair<>(idsList, gstr6);
	}

}
