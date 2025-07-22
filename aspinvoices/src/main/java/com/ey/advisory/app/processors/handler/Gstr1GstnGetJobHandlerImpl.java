/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Gstr1GstnGetJobHandlerImpl")
@Slf4j
public class Gstr1GstnGetJobHandlerImpl implements Gstr1GstnGetJobHandler {

	@Autowired
	@Qualifier("gstr1B2clB2claAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1B2clB2claAtGstn;

	@Autowired
	@Qualifier("gstr1B2csB2csaAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1B2csB2csaAtGstn;

	@Autowired
	@Qualifier("gstr1TxpTxpaAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1TxpTxpaAtGstn;

	@Autowired
	@Qualifier("gstr1NilRatedSupATGstnImpl")
	private Gstr1InvoicesAtGstn gstr1NilRatedSupATGstn;

	@Autowired
	@Qualifier("gstr1HSNSummaryAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1HSNSummaryAtGstn;

	@Autowired
	@Qualifier("gstr1DocIssuesAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1DocIssuesAtGstn;

	@Autowired
	@Qualifier("gstr1CdnrCdnraAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1CdnrCdnraAtGstnImpl;

	@Autowired
	@Qualifier("gstr1CdnurCdnuraAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1CdnurCdnuraAtGstn;

	@Autowired
	@Qualifier("gstr1AtAtaAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1AtAtaAtGstn;

	@Autowired
	@Qualifier("gstr1ExpExpaAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1ExpExpaAtGstn;

	@Autowired
	@Qualifier("gstr1B2bB2baAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1B2bB2baAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Gstr1SupEcomSupEcomAmdAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1SupEcomSupEcomAmdAtGstn;

	@Autowired
	@Qualifier("Gstr1EcomEcomAmdGstnImpl")
	private Gstr1InvoicesAtGstn gstr1EcomEcomAmdAtGstn;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr1GstnGetCall(String jsonReq, String groupCode) {

		// Need to call return filling status
		// api(GstnReturnFilingStatus.java)
		// If it is submitted/filed then after submit/file, the very first
		// time
		// we need to call the GET API's and next time not required to call.

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject,
				Gstr1GetInvoicesReqDto.class);
		TenantContext.setTenantId(groupCode);

		GetAnx1BatchEntity batch = batchRepo
				.findByIdAndIsDeleteFalse(Long.valueOf(dto.getBatchId()));
		Long requestId = null;
		if (batch.getType().equalsIgnoreCase(APIConstants.B2B)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", APIConstants.B2B);
			}
			requestId = gstr1B2bB2baAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.B2B, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.B2B);
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.B2CL)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.B2CL);
			}
			requestId = gstr1B2clB2claAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.B2CL, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.B2CL);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.EXP)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", APIConstants.EXP);
			}
			requestId = gstr1ExpExpaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.EXP, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.EXP);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.AT)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", APIConstants.AT);
			}
			requestId = gstr1AtAtaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.AT, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.AT);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.TXP)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", APIConstants.TXP);
			}
			requestId = gstr1TxpTxpaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.TXP, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.TXP);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.CDNR)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.CDNR);
			}
			requestId = gstr1CdnrCdnraAtGstnImpl.findInvFromGstn(dto, groupCode,
					APIConstants.CDNR, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.CDNR);
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.CDNUR)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.CDNUR);
			}
			requestId = gstr1CdnurCdnuraAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.CDNUR, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.CDNUR);
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.B2BA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.B2BA);
			}
			requestId = gstr1B2bB2baAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.B2BA, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.B2BA);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.B2CLA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.B2CLA);
			}
			requestId = gstr1B2clB2claAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.B2CLA, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.B2CLA);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.EXPA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.EXPA);
			}
			requestId = gstr1ExpExpaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.EXPA, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.EXPA);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.ATA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", APIConstants.ATA);
			}
			requestId = gstr1AtAtaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.ATA, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.ATA);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.TXPA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.TXPA);
			}
			requestId = gstr1TxpTxpaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.TXPA, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.TXPA);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.CDNRA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.CDNRA);
			}
			requestId = gstr1CdnrCdnraAtGstnImpl.findInvFromGstn(dto, groupCode,
					APIConstants.CDNRA, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.CDNRA);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.CDNURA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.CDNURA);
			}
			requestId = gstr1CdnurCdnuraAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.CDNURA, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.",
						APIConstants.CDNURA);
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.B2CS)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.B2CS);
			}
			requestId = gstr1B2csB2csaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.B2CS, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.B2CS);
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.B2CSA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.B2CSA);
			}
			requestId = gstr1B2csB2csaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.B2CSA, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.B2CSA);
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.NIL)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", APIConstants.NIL);
			}
			requestId = gstr1NilRatedSupATGstn.findInvFromGstn(dto, groupCode,
					APIConstants.NIL, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.NIL);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.HSN)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", APIConstants.HSN);
			}
			requestId = gstr1HSNSummaryAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.HSN, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", APIConstants.HSN);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.DOC_ISSUE)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.",
						APIConstants.DOC_ISSUE);
			}
			requestId = gstr1DocIssuesAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.DOC_ISSUE, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.",
						APIConstants.DOC_ISSUE);

			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.SUPECO)
				|| batch.getType().equalsIgnoreCase(APIConstants.SUPECOAMD)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", batch.getType());
			}
			requestId = gstr1SupEcomSupEcomAmdAtGstn.findInvFromGstn(dto,
					groupCode, batch.getType(), batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.", batch.getType());
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.ECOM)
				|| batch.getType().equalsIgnoreCase(APIConstants.ECOMAMD)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Started.", batch.getType());
			}
			requestId = gstr1EcomEcomAmdAtGstn.findInvFromGstn(dto, groupCode,
					batch.getType(), batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 {} Get Call is Ended.",
						batch.getType());
			}
		}

		else {
			LOGGER.error("This section is not handled in GSTR1 GET  {} :",
					batch.getType());
		}
		if (requestId != null) {
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		}
	}

}
