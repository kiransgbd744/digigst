package com.ey.advisory.app.services.refidpolling.gstr8;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr8.Gstr8DocErrorEntity;
import com.ey.advisory.app.data.entities.gstr8.Gstr8UploadProcessedEntity;
import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8UploadPsdRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("DefaultGstr8GstnResponseHandler")
public class DefaultGstr8GstnResponseHandler
		implements Gstr8GstnResponseHandler {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	private Gstr8UploadPsdRepository gstr8UploadPsdRepository;

	@Autowired
	@Qualifier("Gstr8ResponserHandler")
	Gstr8ResponseHandler gstr8ResponserHandler;

	@Autowired
	@Qualifier("Gstr8DocErrorRepository")
	private Gstr8DocErrorRepository gstr8DocErrorRepository;

	public static final String ER = "ER";
	public static final String P = "P";
	public static final String PE = "PE";

	// Processed records
	@Override
	public void phandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- P response handling"
					+ "started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr8UploadPsdRepository.markDocsAsSavedForBatch(batch.getId(),
					now);
			batchSaveStatusRepository
					.updateGstr1SaveBatchbyBatchId(batch.getId(), P, 0, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- P response handling"
					+ "completed for {} ", batch.getSection());
		}

	}

	// Records processed with errors
	@Override
	public void pEhandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- PE response handling"
					+ "started for {} ", batch.getSection());
		}
		Integer errorCount = batch.getBatchSize();
		if (batch.getSection() != null) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			Triplet<List<String>, Map<String, Gstr8DocErrorEntity>, Integer> secResponse = null;
			secResponse = gstr8ResponserHandler.response(root, batch,
					errorCount);
			Map<String, Gstr8DocErrorEntity> docErrorMap = secResponse
					.getValue1();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr8 ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
						errorCount, docErrorMap.keySet());
			}
			List<Gstr8UploadProcessedEntity> headerDocs = new ArrayList<>();
			errorCount = docErrorMap.size();
			List<String> invKeySetAsList = new ArrayList<>(
					docErrorMap.keySet());
			List<Gstr8UploadProcessedEntity> processedActiveRecords = new ArrayList<>();
			for (String details : invKeySetAsList) {
				String[] values = details.split("\\|");
				int length = values.length;
				String ecomGstin = values[0];
				String returnPeriod = values[1];
				String sTin = values[2];
				String section = values[3];
				if (length == 4) {
					gstr8UploadPsdRepository
							.markDocsAsSavedForBatchByErroredDocKeys(
									batch.getId(), ecomGstin, returnPeriod,
									sTin, section);
					headerDocs = gstr8UploadPsdRepository
							.findByGstnBatchIdAndDocKeyIn(batch.getId(),
									ecomGstin, returnPeriod, sTin, section);
					processedActiveRecords.addAll(headerDocs);
				} else {
					String originalRetPeriod = values[4];
					gstr8UploadPsdRepository
							.markDocsAsSavedForBatchByErroredDocKeys(
									batch.getId(), ecomGstin, returnPeriod,
									sTin, section, originalRetPeriod);
					headerDocs = gstr8UploadPsdRepository
							.findByGstnBatchIdAndDocKeyIn(batch.getId(),
									ecomGstin, returnPeriod, sTin, section,
									originalRetPeriod);
					processedActiveRecords.addAll(headerDocs);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr8 ReturnStatus- Only the Saved invoices of the batch {} is updated as SAVED_TO_GSTN ",
						batch.getId());
			}

			List<Gstr8DocErrorEntity> errorDocList = new ArrayList<>();
			List<Gstr8UploadProcessedEntity> headerDocs1 = new ArrayList<>();
			for (Gstr8UploadProcessedEntity doc : headerDocs) {
				String key = String.valueOf(doc.getDocKey());
				// Using the invoice key, get the error object
				Gstr8DocErrorEntity error1 = docErrorMap.get(key);
				// Extra logic in summary PE case to create new error object
				// for all gstin keys.
				Gstr8DocErrorEntity error = new Gstr8DocErrorEntity();
				error.setErrorCode(error1.getErrorCode());
				error.setErrorType(GSTConstants.ERR);
				error.setErrorSource(GSTConstants.GSTN_VALIDATION);
				error.setErrorDescription(error1.getErrorDescription());
				error.setCreatedBy(GSTConstants.SYSTEM);
				error.setCreatedOn(LocalDate.now());
				error.setValType(GSTConstants.BUSINESS_VALIDATIONS);
				doc.setGstnError(true);
				doc.setSavedGstn(false);
				doc.setSavedGstnDate(LocalDate.now());
				doc.setGstnErrorCode(error.getErrorCode());
				doc.setGstnErrorDesc(error.getErrorDescription());
				doc.setUpdatedDate(now);
				headerDocs1.add(doc);
			}
			if (headerDocs != null && !headerDocs.isEmpty()) {
				gstr8UploadPsdRepository.saveAll(headerDocs1);
				gstr8DocErrorRepository.saveAll(errorDocList);
			}
			// we are getting summary level error count so changed the logic to
			// detail level error count
			Integer batchErrorCount = gstr8UploadPsdRepository
					.countOfBatchRecords(batch.getSgstin(),
							batch.getReturnPeriod(), batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr7 ReturnStatus- batch table is updating with batchId {}, "
								+ "status {}, errorcount {} ",
						batch.getId(), PE, batchErrorCount);
			}
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(
					batch.getId(), PE, batchErrorCount, now);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr7 ReturnStatus- PE response handling"
						+ " completed for {} ", batch.getSection());
			}

		} else {
			LOGGER.error(
					"Pooling Gstr7 ReturnStatus- UnKnown section {}"
							+ " found in PE case this should be handled here.",
					batch.getSection());
		}

	}

	// Error Records
	@Override
	public void ehandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- ER response"
					+ " handling started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			Integer errorCount = batch.getBatchSize();
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(
					batch.getId(), ER, errorCount, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- ER response handling"
					+ " completed for {} ", batch.getSection());
		}

	}

}
