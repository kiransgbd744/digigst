package com.ey.advisory.app.services.refidpolling.itc04;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04HeaderErrorsEntity;
import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.data.repositories.client.Itc04HeaderErrorsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Component("DefaultItc04GstnResponseHandler")
public class DefaultItc04GstnResponseHandler implements Itc04GstnResponseHandler {

	@Autowired
	@Qualifier("Itc04DocRepository")
	private Itc04DocRepository itc04DocRepository;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("Itc04HeaderErrorsRepository")
	private Itc04HeaderErrorsRepository docErrorRepository;

	@Autowired
	@Qualifier("Itc04PEResponserHandler")
	private Itc04PEResponserHandler itc04PEResponserHandler;

	private static final List<String> ITC04_SECTIONS = ImmutableList.of(APIConstants.M2JW, APIConstants.TABLE5A,
			APIConstants.TABLE5B, APIConstants.TABLE5C);

	private static final List<String> CUST_GSTIN_ERROR_CODES = ImmutableList.of("RET11410", "RET191113", "RET191119",
			"RET191125");

	public static final String ER = "ER";
	public static final String P = "P";
	public static final String PE = "PE";

	@Override
	public void phandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Itc04 ReturnStatus- P response handling" + "started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			itc04DocRepository.markDocsAsSavedForBatch(batch.getId(), now);
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), P, 0, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Itc04 ReturnStatus- P response handling" + "completed for {} ", batch.getSection());
		}
	}

	@Override
	public void pEhandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling ITC04 ReturnStatus- PE response handling" + "started for {} ", batch.getSection());
		}
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDate utcDateTimeFromLocal = EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now());
		Integer errorCount = batch.getBatchSize();
		if (batch.getSection() != null) {
			Triplet<List<String>, Map<String, Pair<String, String>>, Integer> secResponse = null;

			secResponse = itc04PEResponserHandler.response(root, batch, errorCount);

			Map<String, Pair<String, String>> docErrorMap = secResponse.getValue1();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling ITC04 ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
						errorCount, docErrorMap.keySet());
			}
			List<Itc04HeaderEntity> headerDocs = new ArrayList<>();						
			errorCount = docErrorMap.size();
			List<String> invKeySetAsList = new ArrayList<>(docErrorMap.keySet());
			itc04DocRepository.markDocsAsSavedForBatchByErroredDocKeys(batch.getId(), invKeySetAsList, now);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling ITC04 ReturnStatus- Only the Saved invoices of the batch {} is updated as SAVED_TO_GSTN ",
						batch.getId());
			}
			headerDocs = itc04DocRepository.findByGstnBatchIdAndDocKeyIn(batch.getId(), invKeySetAsList);			
			List<Itc04HeaderEntity> headerDocs1 = new ArrayList<>();
			for (Itc04HeaderEntity doc : headerDocs) {				
				String key = String.valueOf(doc.getDocKey());				
				Pair<String, String> error1 = docErrorMap.get(key);						
				doc.setGstnError(true);
				doc.setSavedToGstn(false);
				doc.setSavedToGSTNDate(utcDateTimeFromLocal);
				doc.setGstnErrorCode(error1.getValue0()!=null ?error1.getValue0() : null);
				doc.setGstnErrorDesc(error1.getValue1()!=null ?error1.getValue1() : null);
				doc.setModifiedOn(now);
				headerDocs1.add(doc);
			}
			if (headerDocs != null && !headerDocs.isEmpty()) {
				itc04DocRepository.saveAll(headerDocs1);
				//docErrorRepository.saveAll(errorDocList);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling ITC04 ReturnStatus- batch table is updating with batchId {}, status {}, errorcount {} ",
						batch.getId(), PE, errorCount);
			}
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), PE, errorCount, now);
		}
	}

	@Override
	public void ehandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling ITC04 ReturnStatus- ER response" + " handling started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			itc04DocRepository.markDocsAsErrorForBatch(batch.getId(), now);
			Integer errorCount = batch.getBatchSize();
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), ER, errorCount, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling ITC04 ReturnStatus- ER response handling" + " completed for {} ", batch.getSection());
		}
	}

}
