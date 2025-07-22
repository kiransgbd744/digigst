/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.einvoice.EInvAndEwbGenRequest;
import com.ey.advisory.app.docs.dto.einvoice.EInvAndEwbGenResponse;
import com.ey.advisory.common.EwbStatus;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("EwbGenService")
public class EwbGenService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final String EWB_INITIATED = "EWB is Initiated";
	private static final String EWB_NOT_INITIATED = "EWB is not Initiated";
	private static final String EWB_ALREADY_INITIATED = "EWB already Initiated";
	private static final String E = "E";
	private static final String S = "S";

	public List<EInvAndEwbGenResponse> ewbGenAction(
			EInvAndEwbGenRequest request) {

		List<EInvAndEwbGenResponse> finalResp = new ArrayList<>();

		List<OutwardTransDocument> ewbStatusDetails = docRepository
				.fetchEnivStatusById(request.getDocIds());

		for (OutwardTransDocument ewbStatus : ewbStatusDetails) {

			Long docid = ewbStatus.getId();
			EInvAndEwbGenResponse resp = new EInvAndEwbGenResponse();
			if (ewbStatus.getEwbStatus() != null && ewbStatus.getEwbStatus()
					.equals(EwbStatus.ASP_PROCESSED.getEwbStatusCode())) {
				Long headerid = ewbStatus.getId();
				createEinvoiceAndEwbAsyncJob(headerid);
				docRepository.updateEwbStatusFlag(headerid);

				resp.setDocId(docid);
				resp.setType(S);
				resp.setMessage(EWB_INITIATED);

			} else if (ewbStatus.getEwbStatus() != null
					&& ewbStatus.getEwbStatus().equals(
							EwbStatus.IRN_IN_PROGRESS.getEwbStatusCode())) {
				resp.setDocId(docid);
				resp.setType(E);
				resp.setMessage(EWB_ALREADY_INITIATED);
			}

			if (ewbStatus.getEwbStatus() == null) {
				resp.setDocId(docid);
				resp.setType(E);
				resp.setMessage(EWB_NOT_INITIATED);
			}
			if (ewbStatus.getEwbStatus() != null
					&& !ewbStatus.getEwbStatus()
							.equals(EwbStatus.ASP_PROCESSED.getEwbStatusCode())
					&& !ewbStatus.getEwbStatus().equals(
							EwbStatus.IRN_IN_PROGRESS.getEwbStatusCode())) {
				resp.setDocId(docid);
				resp.setType(E);
				resp.setMessage(EWB_NOT_INITIATED);
			}
			finalResp.add(resp);

		}
		return finalResp;
	}

	private void createEinvoiceAndEwbAsyncJob(Long id) {
		JsonObject jsonParams = new JsonObject();
		Gson gson = new Gson();
		jsonParams.addProperty("id", id);

		asyncJobsService.createJob(TenantContext.getTenantId(),
				JobConstants.GENERATE_EWAYBILL, jsonParams.toString(), "SYSTEM",
				1L, null, null);
	}

}
