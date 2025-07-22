package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsaDto;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
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
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Gstr7SaveBatchProcessImpl")
public class Gstr7SaveBatchProcessImpl
		implements Gstr6SectionWiseSaveBatchProcess {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7SaveBatchProcessImpl.class);

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
		List<SaveGstr7> batches = batchDto.getGstr7();
		List<SaveToGstnBatchRefIds> retList = new ArrayList<>();
		LOGGER.debug("execute is started with {} batches", batches.size());
		if (batches != null && !batches.isEmpty()) {
			for (int i = 0; i < batches.size(); i++) {
				List<Long> idsList = batchIdsList.get(i);
				SaveGstr7 gstr7 = batches.get(i);
				Triplet<List<Long>, SaveGstr7, String> batchInfo = new Triplet<>(
						idsList, gstr7, groupCode);
				List<SaveToGstnBatchRefIds> refIds = saveGstr7Batch(batchInfo,
						section, userRequestId, taxDocType, context);
				retList.addAll(refIds);
			}
		}
		return retList;
	}

	private List<SaveToGstnBatchRefIds> saveGstr7Batch(
			Triplet<List<Long>, SaveGstr7, String> batchInfo, String section,
			Long userRequestId, String taxDocType, ProcessingContext context) {
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, SaveGstr7, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);
		while (true) {
			try {
				Triplet<List<Long>, SaveGstr7, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, section,
						userRequestId, taxDocType, context);
				retIds.add(refIds);
			} catch (PayloadSizeExceededException ex) {
				LOGGER.error("{} PayloadSizeExceededException Dont worry..., "
						+ "We are handling it by Chunking.", section);
				List<Triplet<List<Long>, SaveGstr7, String>> splitChunks = batchChunk(
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
			Triplet<List<Long>, SaveGstr7, String> batchInfo, String section,
			Long userRequestId, String taxDocType, ProcessingContext context) {
		List<Long> idsList = batchInfo.getValue0();
		SaveGstr7 gstr7 = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
				gstr7.getTaxperiod(), gstr7.getGstin());
		try {
			saveToGstnEventStatus.EventEntry(gstr7.getTaxperiod(),
					gstr7.getGstin(), 20, groupCode);

			String batch = gson.toJson(gstr7);
			LOGGER.error("batch {}", batch);
			
			Long gstnBatchId = null;
			Long hMaxId = null;

			boolean isTransactional = (boolean) context
					.getAttribute(APIConstants.TRANSACTIONAL);
			String returnType = isTransactional
							? APIConstants.GSTR7_TRANSACTIONAL.toUpperCase()
							: APIConstants.GSTR7.toUpperCase();

			List<Object[]> objs = batchSaveStatusRepository
					.selectMaxSumryRecord(gstr7.getGstin(),
							gstr7.getTaxperiod(), returnType,
							section.toUpperCase());

			if (CollectionUtils.isNotEmpty(objs)) {
				Object[] obj = objs.get(0);
				if (obj != null && obj[0] != null) {
					gstnBatchId = obj[0] != null
							? Long.parseLong(obj[0].toString()) : null;
					hMaxId = obj[1] != null ? Long.parseLong(obj[1].toString())
							: null;
				} else {
					LOGGER.error(
							"No Valid Gstr7 Batch Id found for the section {}, "
									+ "Proc has not executed properly.",
							section);
					return null;
				}
			}
			APIResponse resp = null;
			try {
				saveToGstnEventStatus.EventEntry(gstr7.getTaxperiod(),
						gstr7.getGstin(), 30, groupCode);

				if (APIConstants.GSTR7_TRANSACTIONAL
						.equalsIgnoreCase(returnType)) {
					Gstr1SaveBatchEntity saveBatch = batchHandler.saveBatch(
							gstr7.getGstin(), gstr7.getTaxperiod(), section,
							groupCode, returnType, idsList.size(), taxDocType,
							0l, userRequestId);
					gstnBatchId = saveBatch.getId();
				}

				resp = gstnServer.gstr7ApiCall(groupCode, batch,
						gstr7.getGstin(), gstr7.getTaxperiod(), gstnBatchId);
			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				LOGGER.error("GSTR7 gstn api call exception", ex.getMessage());
				batchHandler.deleteBatch(gstnBatchId, groupCode);
				// status code 60 says status as SAVE FAILED AT GSTIN.
				saveToGstnEventStatus.EventEntry(gstr7.getTaxperiod(),
						gstr7.getGstin(), 60, groupCode);
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}
			String refId = null;
			String txnId = null;
			if (resp.getResponse() != null) {
				// NEED TO ADD GSTR7 BATCH TABLE
				sumBatchHandler.gstr7BatchUpdate(gstr7.getTaxperiod(),
						gstr7.getGstin(), gstnBatchId, groupCode, idsList,
						section, hMaxId, taxDocType, isTransactional);
				refId = batchHandler.updateRefIdAndTxnId(groupCode, gstnBatchId,
						resp);
				txnId = resp.getTxnId();
			} else {
				batchHandler.deleteBatch(gstnBatchId, groupCode);
			}
			oneResp.setGstnBatchId(gstnBatchId);
			oneResp.setRefId(refId);
			oneResp.setTxnId(txnId);
			oneResp.setReturnType(returnType);
			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		return oneResp;
	}

	private List<Triplet<List<Long>, SaveGstr7, String>> batchChunk(
			SaveGstr7 saveGstr7, String groupCode, String section) {
		int ctinSize = 0;
		if (APIConstants.TDS.equalsIgnoreCase(section)) {
			ctinSize = saveGstr7.getTdsInvoice().size();
		} else if (APIConstants.TDSA.equalsIgnoreCase(section)) {
			ctinSize = saveGstr7.getTdsaInvoice().size();
		}

		Pair<List<Long>, SaveGstr7> pair1 = null;
		Pair<List<Long>, SaveGstr7> pair2 = null;

		if (ctinSize > 1) {
			if (APIConstants.TDS.equalsIgnoreCase(section)) {
				List<Gstr7TdsDto> tdsList1 = saveGstr7.getTdsInvoice()
						.subList(0, ctinSize / 2);
				List<Gstr7TdsDto> tdsList2 = saveGstr7.getTdsInvoice()
						.subList(ctinSize / 2, ctinSize);
				pair1 = setChunkCtinData(saveGstr7, tdsList1, null, section);
				pair2 = setChunkCtinData(saveGstr7, tdsList2, null, section);
			} else if (APIConstants.TDSA.equalsIgnoreCase(section)) {
				List<Gstr7TdsaDto> tdsaList1 = saveGstr7.getTdsaInvoice()
						.subList(0, ctinSize / 2);
				List<Gstr7TdsaDto> tdsaList2 = saveGstr7.getTdsaInvoice()
						.subList(ctinSize / 2, ctinSize);
				pair1 = setChunkCtinData(saveGstr7, null, tdsaList1, section);
				pair2 = setChunkCtinData(saveGstr7, null, tdsaList2, section);
			}

		} else {
			if (APIConstants.TDS.equalsIgnoreCase(section)) {
				// Gstr7TdsDto tdsData = saveGstr7.getTdsInvoice().get(0);
				int invSize = saveGstr7.getTdsInvoice().size();
				List<Gstr7TdsDto> invList1 = saveGstr7.getTdsInvoice()
						.subList(0, invSize / 2);
				List<Gstr7TdsDto> invList2 = saveGstr7.getTdsInvoice()
						.subList(invSize / 2, invSize);
				pair1 = setChunkInvData(saveGstr7, invList1, null, section);
				pair2 = setChunkInvData(saveGstr7, invList2, null, section);

			} else if (APIConstants.TDSA.equalsIgnoreCase(section)) {
				// Gstr7TdsaDto b2baData = saveGstr7.getTdsaInvoice().get(0);
				int invSize = saveGstr7.getTdsaInvoice().size();
				List<Gstr7TdsaDto> invList1 = saveGstr7.getTdsaInvoice()
						.subList(0, invSize / 2);
				List<Gstr7TdsaDto> invList2 = saveGstr7.getTdsaInvoice()
						.subList(invSize / 2, invSize);
				pair1 = setChunkInvData(saveGstr7, null, invList1, section);
				pair2 = setChunkInvData(saveGstr7, null, invList2, section);
			}
		}
		List<Triplet<List<Long>, SaveGstr7, String>> batchInfoList = new ArrayList<>();
		batchInfoList.add(
				new Triplet<>(pair1.getValue0(), pair1.getValue1(), groupCode));
		batchInfoList.add(
				new Triplet<>(pair2.getValue0(), pair2.getValue1(), groupCode));

		return batchInfoList;
	}

	private Pair<List<Long>, SaveGstr7> setChunkInvData(SaveGstr7 saveGstr7,
			List<Gstr7TdsDto> tdsList, List<Gstr7TdsaDto> tdsaList,
			String section) {

		SaveGstr7 gstr7 = new SaveGstr7();
		List<Long> idsList = new ArrayList<>();

		gstr7.setGstin(saveGstr7.getGstin());
		gstr7.setTaxperiod(saveGstr7.getTaxperiod());

		if (APIConstants.TDS.equalsIgnoreCase(section)) {
			List<Gstr7TdsDto> tdsinList = new ArrayList<>();

			gstr7.setTdsInvoice(tdsinList);
			tdsinList = saveGstr7.getTdsInvoice();

			tdsinList.addAll(tdsinList);

			for (Gstr7TdsDto data : tdsList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr7);
		} else if (APIConstants.TDSA.equalsIgnoreCase(section)) {
			List<Gstr7TdsaDto> tdsainList = new ArrayList<>();

			gstr7.setTdsaInvoice(tdsainList);
			tdsainList = saveGstr7.getTdsaInvoice();

			tdsainList.addAll(tdsainList);

			for (Gstr7TdsaDto data : tdsaList) {
				idsList.add(data.getDocId());
			}
			return new Pair<>(idsList, gstr7);
		}
		return new Pair<>(idsList, gstr7);
	}

	private Pair<List<Long>, SaveGstr7> setChunkCtinData(SaveGstr7 saveGstr7,
			List<Gstr7TdsDto> tdsList, List<Gstr7TdsaDto> tdsaList,
			String section) {

		SaveGstr7 gstr7 = new SaveGstr7();
		List<Long> idsList = new ArrayList<>();

		gstr7.setGstin(saveGstr7.getGstin());
		gstr7.setTaxperiod(saveGstr7.getTaxperiod());

		if ((APIConstants.TDS.equalsIgnoreCase(section))
				&& (tdsList != null && !tdsList.isEmpty())) {
			gstr7.setTdsInvoice(tdsList);
			for (Gstr7TdsDto cData : tdsList) {
				idsList.add(cData.getDocId());
			}
		} else if ((APIConstants.TDSA.equalsIgnoreCase(section))
				&& (tdsaList != null && !tdsaList.isEmpty())) {
			gstr7.setTdsaInvoice(tdsaList);
			for (Gstr7TdsaDto cData : tdsaList) {
				idsList.add(cData.getDocId());
			}
		}
		return new Pair<>(idsList, gstr7);
	}

}
