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
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.services.refidpolling.gstr1.Gstr1GstnResponseHandler;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for handling any other response that we get 
 * from GSTN, other than 'P' or 'PE'. For all such responses, we just need
 * to save ther response as part of batch. Nothing else has to be done.
 * 
 * @author Siva Reddy
 *
 */
@Slf4j
@Component("DefaultGstr1AErrorResponseHandler")
public class DefaultGstr1AErrorResponseHandler implements Gstr1GstnResponseHandler {
	
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
	@Qualifier("Gstr1ARRepository")
	private Gstr1ARRepository atAtaRepo;
	
	@Autowired
	@Qualifier("Gstr1ATARepository")
	private Gstr1ATARepository txpTxpaRepo; 
	
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
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;
	
	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1AInvoiceRepository;

	@Autowired
	private DocRepositoryGstr1A docRepoGstr1A;
	
	@Autowired
	private Gstr1ANilNonExtSummaryRepository nilSumryRepo;
	
	@Autowired
	private Gstr1ANilRepository nilVertRepo;
	
	@Autowired
	@Qualifier("Gstr1AUserInputNilExtnOnRepository")
	private Gstr1AUserInputNilExtnOnRepository nilUserRepo;
	
	@Autowired
	private Gstr1AUserInputHsnSacRepository hsnUserRepo;

	
	private static final List<String> SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.SEZWOP,
			APIConstants.SEZWP, APIConstants.EXPWP, APIConstants.EXPWOP,
			APIConstants.MIS, APIConstants.DE, APIConstants.EXP,
			APIConstants.EXPA, APIConstants.CDNUR, APIConstants.CDNURA,
			APIConstants.B2CL, APIConstants.B2CLA, APIConstants.CDNR,
			APIConstants.CDNRA, APIConstants.B2B, APIConstants.B2BA);
	public static final String ER = "ER";

	@Override
	public void handleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- ER response handling started for {} ",
					batch.getSection());
		}

		String status = ER;
		Integer errorCount = batch.getBatchSize();
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(),
				status, errorCount, now);
		if (batch.getSection() != null && batch.getReturnType() != null) {

			if (SECTIONS.contains(batch.getSection().toLowerCase())) {
			
				docRepoGstr1A.markDocsAsErrorForBatch(batch.getId(), now);
				
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.DOCISS)) {

				gstr1AInvoiceRepository.updateIserrorfalg(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.AT)
					|| batch.getSection().equalsIgnoreCase(APIConstants.ATA)) {

				gstr1AatAtaRepo.updateIserrorfalg(batch.getId());
				docRepoGstr1A.markDocsAsErrorForBatch(batch.getId(), now);
				atAtaRepo.updateIserrorfalg(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.NIL)) {

				gstr1AnilProcRepository.updateIserrorfalg(batch.getId());
				docRepoGstr1A.markDocsAsErrorForBatch(batch.getId(),now);
				//Vertical Summary table 
				nilSumryRepo.updateIserrorfalg(batch.getId());
				//Vertical Processed table 
				nilVertRepo.updateIserrorfalg(batch.getId());
				//user input table
				nilUserRepo.updateIserrorfalg(batch.getId());
				
			} else if (batch.getSection()
					.equalsIgnoreCase(APIConstants.HSNSUM)) {

				gstr1AhsnProcRepository.updateIserrorfalg(batch.getId());
				// There is no seperate Bifurcation type for HSN.
				// docRepo.updateIserrorfalg(batch.getId());
				//user input table
				hsnUserRepo.updateIserrorfalg(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.B2CS)
					|| batch.getSection()
							.equalsIgnoreCase(APIConstants.B2CSA)) {

				gstr1Ab2csProcRepository.updateIserrorfalg(batch.getId(),now);
				docRepoGstr1A.markDocsAsErrorForBatch(batch.getId(), now);
				gstr1Ab2csB2csaRepo.updateIserrorfalg(batch.getId());

			} else if (batch.getSection().equalsIgnoreCase(APIConstants.TXPD)
					|| batch.getSection()
							.equalsIgnoreCase(APIConstants.TXPDA)) {

				gstr1AtxpTxpaProcRepository.updateIserrorfalg(batch.getId());
				docRepoGstr1A.markDocsAsErrorForBatch(batch.getId(), now);
				txpTxpaRepo.updateIserrorfalg(batch.getId());
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- ER response handling completed for {} ",
					batch.getSection());
		}
	}

}
