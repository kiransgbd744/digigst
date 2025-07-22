/**
 * 
 */
package com.ey.advisory.app.services.refidpolling.gstr1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Anx1B2cProcRepository;
import com.ey.advisory.app.data.repositories.client.Anx1EcomProcRepository;
import com.ey.advisory.app.data.repositories.client.Anx1ImpsProcRepository;
import com.ey.advisory.app.data.repositories.client.Anx1RevProcRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.InwardTable3HRepository;
import com.ey.advisory.app.data.repositories.client.OutwardB2cRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTable4Repository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Anx1DefaultGstnPResponseHandler")
public class Anx1DefaultGstnPResponseHandler implements Anx1GstnResponseHandler {

	
	@Autowired
	@Qualifier("Anx1B2cProcRepository")
	private Anx1B2cProcRepository anx1B2cProcRepository;

	@Autowired
	@Qualifier("Anx1RevProcRepository")
	private Anx1RevProcRepository anx1RevProcRepository;

	@Autowired
	@Qualifier("Anx1ImpsProcRepository")
	private Anx1ImpsProcRepository anx1ImpsProcRepository;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("Anx1EcomProcRepository")
	private Anx1EcomProcRepository anx1EcomProcRepository;

	@Autowired
	private DocRepository docRepo;

	@Autowired
	private InwardTransDocRepository inDocRepo;

	@Autowired
	private OutwardTable4Repository table4Repo;// OutwardGstnTable4Repository

	@Autowired
	private OutwardB2cRepository b2cRepo;// OutwardGstnB2cRepository

	@Autowired
	private InwardTable3HRepository table3h3i; // InwardTable3H3IGstnRepository

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

				docRepo.markDocsAsSavedForBatch(batch.getId(), now);
			}

			else if (batch.getSection().equalsIgnoreCase(APIConstants.B2C)) {
				anx1B2cProcRepository.updateIsSavedToGstn(batch.getId());
				docRepo.markDocsAsSavedForBatch(batch.getId(), now);
				b2cRepo.updateIsSavedToGstn(batch.getId());
			} else if (batch.getSection().equalsIgnoreCase(APIConstants.REV)) {
				anx1RevProcRepository.updateIsSavedToGstn(batch.getId());
				inDocRepo.updateIsSavedToGstn(batch.getId());
				table3h3i.updateIsSavedToGstn(batch.getId());

			} else if (batch.getSection().equalsIgnoreCase(APIConstants.IMPS)) {
				anx1ImpsProcRepository.updateIsSavedToGstn(batch.getId());
				inDocRepo.updateIsSavedToGstn(batch.getId());
				table3h3i.updateIsSavedToGstn(batch.getId());
			}

			else if (batch.getSection().equalsIgnoreCase(APIConstants.ECOM)) {
				anx1EcomProcRepository.updateIsSavedToGstn(batch.getId());
				docRepo.markDocsAsSavedForBatch(batch.getId(), now);
				b2cRepo.updateIsSavedToGstn(batch.getId());
				table4Repo.updateIsSavedToGstn(batch.getId());

			} else if (batch.getSection().equalsIgnoreCase(APIConstants.IMPG)) {

				inDocRepo.markDocsAsSavedForBatch(batch.getId(),now, null);
			}
			// need to do for mis

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- P response handling completed for {} ",
					batch.getSection());
		}
	}
}
