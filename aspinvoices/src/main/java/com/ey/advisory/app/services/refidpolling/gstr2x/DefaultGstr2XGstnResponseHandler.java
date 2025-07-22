package com.ey.advisory.app.services.refidpolling.gstr2x;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2XProcessedTcsTdsEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XVerticalErrorEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2XProcessedTcsTdsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2xVerticalErrorRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Component("DefaultGstr2XGstnResponseHandler")
public class DefaultGstr2XGstnResponseHandler implements Gstr2XGstnResponseHandler {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("Gstr2XProcessedTcsTdsRepository")
	private Gstr2XProcessedTcsTdsRepository gstr2XProcessedTcsTdsRepository;

	@Autowired
	@Qualifier("Gstr2XPEResponserHandler")
	private Gstr2XPEResponserHandler gstr2XPEResponserHandler;

	@Autowired
	@Qualifier("Gstr2xVerticalErrorRepository")
	private Gstr2xVerticalErrorRepository gstr2xVerticalErrorRepository;

	public static final String ER = "ER";
	public static final String P = "P";
	public static final String PE = "PE";

	@Override
	public void phandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr2X ReturnStatus- P response handling" + "started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr2XProcessedTcsTdsRepository.markDocsAsSavedForBatch(batch.getId(), now);
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), P, 0, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr2X ReturnStatus- P response handling" + "completed for {} ", batch.getSection());
		}
	}

	@Override
	public void pEhandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling GSTR2X ReturnStatus- PE response handling" + "started for {} ", batch.getSection());
		}
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDate utcDateTimeFromLocal = EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now());
		Integer errorCount = batch.getBatchSize();
		if (batch.getSection() != null) {
			Triplet<List<String>, Map<String, Gstr2XVerticalErrorEntity>, Integer> secResponse = gstr2XPEResponserHandler
					.response(root, batch, errorCount);
			Map<String, Gstr2XVerticalErrorEntity> docErrorMap = secResponse.getValue1();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling GSTR2X ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
						errorCount, docErrorMap.keySet());
			}
			errorCount = docErrorMap.size();
			List<String> invKeySetAsList = new ArrayList<>(docErrorMap.keySet());
			gstr2XProcessedTcsTdsRepository.markDocsAsSavedForBatchByErroredDocKeys(batch.getId(), invKeySetAsList,
					now);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling GSTR2X ReturnStatus- Only the Saved invoices of the batch {} is updated as SAVED_TO_GSTN ",
						batch.getId());
			}
			List<Gstr2XProcessedTcsTdsEntity> headerDocs = gstr2XProcessedTcsTdsRepository
					.findByGstnBatchIdAndDocKeyIn(batch.getId(), invKeySetAsList);
			List<Gstr2XVerticalErrorEntity> errorDocList = new ArrayList<>();
			List<Gstr2XProcessedTcsTdsEntity> headerDocs1 = new ArrayList<>();
			for (Gstr2XProcessedTcsTdsEntity doc : headerDocs) {
				String key = String.valueOf(doc.getDocKey());
				Gstr2XVerticalErrorEntity error = docErrorMap.get(key);
				error.setCommonId(doc.getId());
				error.setGstin(doc.getGstin());
				error.setReturnPeriod(doc.getRetPeriod());
				error.setFileId(doc.getFileId());
				error.setCreatedBy(APIConstants.SYSTEM);
				error.setCreatedOn(now);
				errorDocList.add(error);

				doc.setGstnError(true);
				doc.setSavedToGstn(false);
				doc.setSavedToGstnDate(utcDateTimeFromLocal);
				doc.setGstnErrorCode(error.getErrorCode());
				doc.setGstnErrorDesc(error.getErrorDesc());
				doc.setModifiedOn(now);
				headerDocs1.add(doc);
			}
			if (headerDocs != null && !headerDocs.isEmpty()) {
				gstr2XProcessedTcsTdsRepository.saveAll(headerDocs1);
				gstr2xVerticalErrorRepository.saveAll(errorDocList);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling GSTR2X ReturnStatus- batch table is updating with batchId {}, status {}, errorcount {} ",
						batch.getId(), PE, errorCount);
			}
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), PE, errorCount, now);
		}

	}

	@Override
	public void ehandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr2X ReturnStatus- ER response" + " handling started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr2XProcessedTcsTdsRepository.markDocsAsErrorForBatch(batch.getId(), now);
			Integer errorCount = batch.getBatchSize();
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), ER, errorCount, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr2X ReturnStatus- ER response handling" + " completed for {} ",
					batch.getSection());
		}

	}

}
