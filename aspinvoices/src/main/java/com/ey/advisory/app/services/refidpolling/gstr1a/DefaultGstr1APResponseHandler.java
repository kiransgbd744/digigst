package com.ey.advisory.app.services.refidpolling.gstr1a;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AARRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AATARepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AAtAtaProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AB2CSRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AB2csProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AHsnProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AInvoiceRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilNonExtSummaryRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ATxpTxpaProcRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AUserInputHsnSacRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AUserInputNilExtnOnRepository;
import com.ey.advisory.app.services.refidpolling.gstr1.Gstr1GstnResponseHandler;
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
 * @author Siva Reddy
 *
 */
@Slf4j
@Component("DefaultGstr1APResponseHandler")
public class DefaultGstr1APResponseHandler implements Gstr1GstnResponseHandler {

	@Autowired
	@Qualifier("Gstr1ATxpTxpaProcRepository")
	private Gstr1ATxpTxpaProcRepository gstr1AtxpTxpaProcRepository;
	
	@Autowired
	@Qualifier("Gstr1AARRepository")
	private Gstr1AARRepository gstr1AatAtaRepo;
	
	@Autowired
	@Qualifier("Gstr1AAtAtaProcRepository")
	private Gstr1AAtAtaProcRepository gstr1AatAtaProcRepository;
	
	@Autowired
	@Qualifier("Gstr1AB2CSRepository")
	private Gstr1AB2CSRepository gstr1Ab2csB2csaRepo;
	
	@Autowired
	@Qualifier("Gstr1AATARepository")
	private Gstr1AATARepository gstr1AtxpTxpaRepo; 
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("Gstr1ANilProcRepository")
	private Gstr1ANilProcRepository gstr1AnilProcRepository;

	@Autowired
	@Qualifier("Gstr1AHsnProcRepository")
	private Gstr1AHsnProcRepository gstr1AhsnProcRepository;

	@Autowired
	@Qualifier("Gstr1AB2csProcRepository")
	private Gstr1AB2csProcRepository gstr1Ab2csProcRepository;

	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1AInvoiceRepository;

	@Autowired
	private DocRepositoryGstr1A docRepogstr1A;
	
	@Autowired
	private Gstr1ANilNonExtSummaryRepository gstr1AnilSumryRepo;
	
	@Autowired
	private Gstr1ANilRepository gstr1AnilVertRepo;
	
	@Autowired
	@Qualifier("Gstr1AUserInputNilExtnOnRepository")
	private Gstr1AUserInputNilExtnOnRepository gstr1AnilUserRepo;
	
	@Autowired
	private Gstr1AUserInputHsnSacRepository gstr1AhsnUserRepo;
	
//	@Autowired
//	private Gstr1GSTINDeleteDataRepository autoDraftRepo;
//	
//	@Autowired
//	private Gstr1vsEinvReconRespProcessedRepository deleteRespRepo;

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
//				if (APIConstants.DELETE_FILE_UPLOAD
//						.equalsIgnoreCase(batch.getOperationType())) {
//					autoDraftRepo.markDocsAsSavedForBatch(batch.getId());
//				} else if (APIConstants.DELETE_RESPONSE
//						.equalsIgnoreCase(batch.getOperationType())) {
//
//					deleteRespRepo.markDocsAsSavedForBatch(batch.getId());
//				} else {
					docRepogstr1A.markDocsAsSavedForBatch(batch.getId(), now);
//				}

			} else if (batch.getSection()
					.equalsIgnoreCase(APIConstants.DOCISS)) {
				gstr1AInvoiceRepository.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.AT)
					|| batch.getSection().equalsIgnoreCase(APIConstants.ATA)) {
				gstr1AatAtaProcRepository.updateIsSavedToGstn(batch.getId());
				docRepogstr1A.markDocsAsSavedForBatch(batch.getId(), now);
				gstr1AatAtaRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.NIL)) {
				gstr1AnilProcRepository.updateIsSavedToGstn(batch.getId());
				docRepogstr1A.markDocsAsSavedForBatch(batch.getId(), now);
				//Vertical Summary table 
				gstr1AnilSumryRepo.updateIsSavedToGstn(batch.getId());
				//Vertical Processed table 
				gstr1AnilVertRepo.updateIsSavedToGstn(batch.getId());
				//user input table
				gstr1AnilUserRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection()
					.equalsIgnoreCase(APIConstants.HSNSUM)) {
				gstr1AhsnProcRepository.updateIsSavedToGstn(batch.getId());
				// There is no seperate Bifurcation type for HSN.
				// docRepo.updateIsSavedToGstn(batch.getId());
				//user input table
				gstr1AhsnUserRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.B2CS)
					|| batch.getSection()
							.equalsIgnoreCase(APIConstants.B2CSA)) {
				gstr1Ab2csProcRepository.updateIsSavedToGstn(batch.getId(), now);
				docRepogstr1A.markDocsAsSavedForBatch(batch.getId(), now);
				gstr1Ab2csB2csaRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.TXP)
					|| batch.getSection().equalsIgnoreCase(APIConstants.TXPA)) {
				gstr1AtxpTxpaProcRepository.updateIsSavedToGstn(batch.getId());
				docRepogstr1A.markDocsAsSavedForBatch(batch.getId(), now);
				gstr1AtxpTxpaRepo.updateIsSavedToGstn(batch.getId());
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- P response handling completed for {} ",
					batch.getSection());
		}
	}
}
