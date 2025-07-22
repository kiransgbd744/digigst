/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1SaveToGstnRetryReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1RetryBatchMakerImpl;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1SaveToGstnRetryJobHandler")
public class Gstr1SaveToGstnRetryJobHandler {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepo;
	
	@Autowired
	@Qualifier("gstr1CancelledInvicesIdentifierImpl")
	private Gstr1CancelledInvicesIdentifier gstnCancelData;
	
	@Autowired
	@Qualifier("Gstr1RetryBatchMakerImpl")
	private Gstr1RetryBatchMakerImpl gstr1RetryBatchMaker;
	
	
	public void saveFailedBatchInvoices(String jsonReq, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Insede saveFailedBatchInvoices method");
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1SaveToGstnRetryReqDto requestDto = gson.fromJson(requestObject,
				Gstr1SaveToGstnRetryReqDto.class);
		List<Gstr1SaveBatchEntity> reTryEligibleBatches = batchRepo
				.findByRefIdAndIsDeleteFalse(requestDto.getRefId());
		reTryEligibleBatches.forEach(batch -> {

			Long id = batch.getId();
			String retPeriod = batch.getReturnPeriod();
			String gstin = batch.getSgstin();
			String section = batch.getSection();
			String operationType = batch.getOperationType();

			List<Object[]> docs = null;
			if (GSTConstants.CAN.equalsIgnoreCase(operationType)) {

				Map<String, Map<Long, Long>> map = gstnCancelData
						.findOrgCanInvoicesMap(gstin, retPeriod, groupCode,
								SaveToGstnOprtnType.CAN,null, null);

				if (map != null) {
					Map<Long, Long> batchOrgCanIdsMap = map.get(section);
					if (batchOrgCanIdsMap != null
							&& batchOrgCanIdsMap.keySet() != null) {
						docs = docRepo.findGstr1InvoiceLevelData(gstin,
								retPeriod, section, batchOrgCanIdsMap.keySet(), null, null);
					}
				}

			} else {

				if (isHorizontal(section)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("{} Invoice level Data Identifier",
								section);
					}
					docs = docRepo.findGstr1InvoiceLevelData(gstin, retPeriod,
							section, null, null, null);
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("{} Summary level Data Identifier",
								section);
					}
					docs = docRepo.findGstr1SummaryLevelData(gstin, retPeriod,
							section/*, id*/, null);
				}
			}

			if (docs == null || !docs.isEmpty()) {
				return;
			} else {

				gstr1RetryBatchMaker.reTrySaveGstr1Data(docs, batch);
			}
		});
	}
	
	private Boolean isHorizontal(String section) {

		return (APIConstants.B2B.equals(section)
				|| APIConstants.B2BA.equals(section)
				|| APIConstants.B2CL.equals(section)
				|| APIConstants.B2CLA.equals(section)
				|| APIConstants.EXP.equals(section)
				|| APIConstants.EXPA.equals(section)
				|| APIConstants.CDNR.equals(section)
				|| APIConstants.CDNRA.equals(section)
				|| APIConstants.CDNUR.equals(section)
				|| APIConstants.CDNURA.equals(section));
	}
}
