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
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("EInvoiceGenService")
public class EInvoiceGenService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final String EINVOICE_INITIATED = "EInvoice is Initiated";
	private static final String EINVOICE_NOT_INITIATED = "EInvoice is not Initiated";
	private static final String EINVOICE_ALREADY_INITIATED = "EInvoice already Initiated";
	private static final String E = "E";
	private static final String S = "S";

	public List<EInvAndEwbGenResponse> eInvoiceGen(
			EInvAndEwbGenRequest request) {

		List<EInvAndEwbGenResponse> finalResp = new ArrayList<>();

		List<OutwardTransDocument> einvoceStatus = docRepository
				.fetchEnivStatusById(request.getDocIds());

		for (OutwardTransDocument invStatus : einvoceStatus) {

			Long docid = invStatus.getId();
			EInvAndEwbGenResponse resp = new EInvAndEwbGenResponse();
			if (invStatus.geteInvStatus() != null && invStatus.geteInvStatus()
					.equals(EInvoiceStatus.ASP_PROCESSED
							.geteInvoiceStatusCode())) {
				Long headerid = invStatus.getId();
				createEinvoiceAndEwbAsyncJob(headerid);
				docRepository.updateEInvStatusFlag(headerid);

				resp.setDocId(docid);
				resp.setType(S);
				resp.setMessage(EINVOICE_INITIATED);
			} else if (invStatus.geteInvStatus() != null && invStatus
					.geteInvStatus().equals(EInvoiceStatus.IRN_IN_PROGRESS
							.geteInvoiceStatusCode())) {
				resp.setDocId(docid);
				resp.setType(E);
				resp.setMessage(EINVOICE_ALREADY_INITIATED);
			}
			if (invStatus.geteInvStatus() == null) {
				resp.setDocId(docid);
				resp.setType(E);
				resp.setMessage(EINVOICE_NOT_INITIATED);
			}
			if (invStatus.geteInvStatus() != null
					&& !invStatus.geteInvStatus()
							.equals(EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
					&& !invStatus.geteInvStatus()
							.equals(EInvoiceStatus.IRN_IN_PROGRESS
									.geteInvoiceStatusCode())) {
				resp.setDocId(docid);
				resp.setType(E);
				resp.setMessage(EINVOICE_NOT_INITIATED);
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
				JobConstants.EINVOICE_ASYNC, jsonParams.toString(), "SYSTEM",
				1L, null, null);
	}

}
