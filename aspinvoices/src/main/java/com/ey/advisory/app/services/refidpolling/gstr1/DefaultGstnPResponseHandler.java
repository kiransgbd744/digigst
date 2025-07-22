package com.ey.advisory.app.services.refidpolling.gstr1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.AtAtaProcRepository;
import com.ey.advisory.app.data.repositories.client.B2csProcRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GSTINDeleteDataRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputHsnSacRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputNilExtnOnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vsEinvReconRespProcessedRepository;
import com.ey.advisory.app.data.repositories.client.HsnProcRepository;
import com.ey.advisory.app.data.repositories.client.NilProcRepository;
import com.ey.advisory.app.data.repositories.client.TxpTxpaProcRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for handling the 'P' response that we get from
 * GSTN. Here we need to update all the invoices as part of the batch with the
 * 'isSaveToGstn' status as 'true' and also update the batch status as 'P'.
 * 
 * @author Siva Krishna
 *
 */
@Slf4j
@Component("DefaultGstnPResponseHandler")
public class DefaultGstnPResponseHandler implements Gstr1GstnResponseHandler {

	@Autowired
	@Qualifier("TxpTxpaProcRepository")
	private TxpTxpaProcRepository txpTxpaProcRepository;
	
	@Autowired
	@Qualifier("Gstr1ARRepository")
	private Gstr1ARRepository atAtaRepo;
	
	@Autowired
	@Qualifier("AtAtaProcRepository")
	private AtAtaProcRepository atAtaProcRepository;
	
	@Autowired
	@Qualifier("Gstr1B2CSRepository")
	private Gstr1B2CSRepository b2csB2csaRepo;
	
	@Autowired
	@Qualifier("Gstr1ATARepository")
	private Gstr1ATARepository txpTxpaRepo; 
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("NilProcRepository")
	private NilProcRepository nilProcRepository;

	@Autowired
	@Qualifier("HsnProcRepository")
	private HsnProcRepository hsnProcRepository;

	@Autowired
	@Qualifier("B2csProcRepository")
	private B2csProcRepository b2csProcRepository;

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1InvoiceRepository;

	@Autowired
	private DocRepository docRepo;
	
	@Autowired
	private Gstr1NilNonExtSummaryRepository nilSumryRepo;
	
	@Autowired
	private Gstr1NilRepository nilVertRepo;
	
	@Autowired
	@Qualifier("gstr1UserInputNilExtnOnRepository")
	private Gstr1UserInputNilExtnOnRepository nilUserRepo;
	
	@Autowired
	private Gstr1UserInputHsnSacRepository hsnUserRepo;
	
	@Autowired
	private Gstr1GSTINDeleteDataRepository autoDraftRepo;
	
	@Autowired
	private Gstr1vsEinvReconRespProcessedRepository deleteRespRepo;

	private static final List<String> SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.SEZWOP,
			APIConstants.SEZWP, APIConstants.EXPWP, APIConstants.EXPWOP,
			APIConstants.MIS, APIConstants.DE, APIConstants.EXP,
			APIConstants.EXPA, APIConstants.CDNUR, APIConstants.CDNURA,
			APIConstants.B2CL, APIConstants.B2CLA, APIConstants.CDNR,
			APIConstants.CDNRA, APIConstants.B2B, APIConstants.B2BA);
	public static final String P = "P";

	@Override
	public void handleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		// TODO Auto-generated method stub
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- P response handling started for {} ",
					batch.getSection());
		}
		String status = P;
		Integer errorCount = 0;
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(),
				status, errorCount, now);
		if (batch.getSection() != null && batch.getReturnType() != null) {

			if (SECTIONS.contains(batch.getSection().toLowerCase())) {
				if (APIConstants.DELETE_FILE_UPLOAD
						.equalsIgnoreCase(batch.getOperationType())) {
					autoDraftRepo.markDocsAsSavedForBatch(batch.getId());
				} else if (APIConstants.DELETE_RESPONSE
						.equalsIgnoreCase(batch.getOperationType())) {

					deleteRespRepo.markDocsAsSavedForBatch(batch.getId());
				} else {
					docRepo.markDocsAsSavedForBatch(batch.getId(), now);
				}

			} else if (batch.getSection()
					.equalsIgnoreCase(APIConstants.DOCISS)) {
				gstr1InvoiceRepository.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.AT)
					|| batch.getSection().equalsIgnoreCase(APIConstants.ATA)) {
				atAtaProcRepository.updateIsSavedToGstn(batch.getId());
				docRepo.markDocsAsSavedForBatch(batch.getId(), now);
				atAtaRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.NIL)) {
				nilProcRepository.updateIsSavedToGstn(batch.getId());
				docRepo.markDocsAsSavedForBatch(batch.getId(), now);
				//Vertical Summary table 
				nilSumryRepo.updateIsSavedToGstn(batch.getId());
				//Vertical Processed table 
				nilVertRepo.updateIsSavedToGstn(batch.getId());
				//user input table
				nilUserRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection()
					.equalsIgnoreCase(APIConstants.HSNSUM)) {
				hsnProcRepository.updateIsSavedToGstn(batch.getId());
				// There is no seperate Bifurcation type for HSN.
				// docRepo.updateIsSavedToGstn(batch.getId());
				//user input table
				hsnUserRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.B2CS)
					|| batch.getSection()
							.equalsIgnoreCase(APIConstants.B2CSA)) {
				b2csProcRepository.updateIsSavedToGstn(batch.getId(), now);
				docRepo.markDocsAsSavedForBatch(batch.getId(), now);
				b2csB2csaRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.TXP)
					|| batch.getSection().equalsIgnoreCase(APIConstants.TXPA)) {
				txpTxpaProcRepository.updateIsSavedToGstn(batch.getId());
				docRepo.markDocsAsSavedForBatch(batch.getId(), now);
				txpTxpaRepo.updateIsSavedToGstn(batch.getId());
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- P response handling completed for {} ",
					batch.getSection());
		}
	}
}
