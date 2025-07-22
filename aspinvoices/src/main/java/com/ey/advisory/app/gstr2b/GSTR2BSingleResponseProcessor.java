package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgsezHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BItcItemRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2bHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2baHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnraHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BSuppSmryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("GSTR2BSingleResponseProcessor")
public class GSTR2BSingleResponseProcessor
		implements Gstr2BGetJsonResponseProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	Gstr2BGetRevIntgHelper revIntgHelper;

	@Autowired
	@Qualifier("Gstr2RespParserImpl")
	private Gstr2RespParser parser;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2bHeaderRepository")
	private Gstr2GetGstr2BLinkingB2bHeaderRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2baHeaderRepository")
	private Gstr2GetGstr2BLinkingB2baHeaderRepository b2bAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnrHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnrHeaderRepository cdnrHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnraHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnraHeaderRepository cdnrAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdHeaderRepository")
	private Gstr2GetGstr2BIsdHeaderRepository isdHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdaHeaderRepository")
	private Gstr2GetGstr2BIsdaHeaderRepository isdAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgHeaderRepository")
	private Gstr2GetGstr2BImpgHeaderRepository impgHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgsezHeaderRepository")
	private Gstr2GetGstr2BImpgsezHeaderRepository impgSezHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BItcItemRepository")
	private Gstr2GetGstr2BItcItemRepository itcSummaryRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BSuppSmryRepository")
	private Gstr2GetGstr2BSuppSmryRepository suppSummaryRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Gstr2bAutoCommRepository")
	private Gstr2bAutoCommRepository gstr2bAutoCommRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomHeaderRepository ecomHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomaHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomaHeaderRepository ecomAHeaderRepo;

	@Autowired
	private Gstr2BDupCheckProcCallHandler procCallHandler;

	@Override
	public void processJsonResponse(String gstin, String taxPeriod,
			Long invocationId, List<Long> resultIds, boolean isAuto) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Invoking softlyDeleteHeader : Gstr2BGetSuccessHandler");
			}
			String userName = "SYSTEM";
			int cntItc = itcSummaryRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : Itc Summary table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						cntItc, gstin, taxPeriod);
			}
			int suppSmryCnt = suppSummaryRepo.softlyDeleteHeader(gstin,
					taxPeriod, LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : Supplier Summary table Row "
								+ "deleted {} for gstin {}, taxPeriod {}",
						suppSmryCnt, gstin, taxPeriod);
			}
			int b2bCnt = b2bHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : B2B table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						b2bCnt, gstin, taxPeriod);
			}
			int b2bAcnt = b2bAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : B2BA  table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						b2bAcnt, gstin, taxPeriod);
			}
			int cdnrCnt = cdnrHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : CDNR table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						cdnrCnt, gstin, taxPeriod);
			}
			int cdnrACnt = cdnrAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : CDNRA table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						cdnrACnt, gstin, taxPeriod);
			}
			int isdcnt = isdHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : ISD table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						isdcnt, gstin, taxPeriod);
			}
			int isdACnt = isdAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : ISDA table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						isdACnt, gstin, taxPeriod);
			}
			int impgCnt = impgHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : IMPG table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						impgCnt, gstin, taxPeriod);
			}
			int impgSezCnt = impgSezHeaderRepo.softlyDeleteHeader(gstin,
					taxPeriod, LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : IMPG SEZ table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						impgSezCnt, gstin, taxPeriod);
				LOGGER.debug(
						"Invoked All softlyDeleteHeader : Gstr2BGetSuccessHandler");
			}
			int ecomCnt = ecomHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler : Ecom table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						ecomCnt, gstin, taxPeriod);
			}
			int ecomAcnt = ecomAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2BGetSuccessHandler :EcomA  table Row deleted {}"
								+ " for gstin {}, taxPeriod {}",
						ecomAcnt, gstin, taxPeriod);
			}

			for (Long id : resultIds) {
				String apiResp = APIInvokerUtil.getResultById(id);

				Gstr2BGETDataDto reqDto = gson.fromJson(apiResp,
						Gstr2BGETDataDto.class);

				// parsing and saving json
				parser.pasrseResp(true, reqDto, invocationId);

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET2B Success Handler End of for Loop");
			}
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("gstin", gstin);
			jsonParams.addProperty("taxPeriod", taxPeriod);
			jsonParams.addProperty("invocationId", invocationId);
			String groupCode = TenantContext.getTenantId();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Gstr2b dup check proc method getting called for groupCode - {} ",
						groupCode);
			}

			String response = procCallHandler.callDupCheckProcs(gstin,
					taxPeriod, invocationId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Gstr2b dup check proc method ended for groupCode - {} ",
						groupCode);
			}

			if (response != null && "SUCCESS".equalsIgnoreCase(response)) {
				batchRepo.updateStatus(APIConstants.SUCCESS,
						APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
						taxPeriod, LocalDateTime.now(), isTokenResp, null,
						null);

				revIntgHelper.persistMonitorTaggingDtls(gstin, taxPeriod,
						invocationId);
				revIntgHelper.postRevIntgJob(invocationId, gstin, taxPeriod);

				if (isAuto) {
					gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.SUCCESS, "");
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.SUCCESS, null,
							null, LocalDateTime.now());
				} else {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"GET2B Success Handler, About to post Report Job - {}",
								groupCode);
					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2B_GET_CSV_REPORT,
							jsonParams.toString(), userName, 1L, null, null);
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"GET2B Success Handler, posted Report Job - {}",
								groupCode);
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.SUCCESS, null,
							null, LocalDateTime.now());
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"GET2B Success Handler, Processing is completed - {}",
								groupCode);
				}
			} else {

				String msg = "Gstr2b Proc Call Returned Invalid Response.";

				if (isAuto) {
					gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, msg);
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
							LocalDateTime.now());
				} else {
					gstnStatusRepo.updateStatus(gstin, taxPeriod,
							APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
							LocalDateTime.now());
				}
				batchRepo.updateStatus(APIConstants.FAILED,
						APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
						taxPeriod, LocalDateTime.now(), false, "ER-1000", msg);
			}
		} catch (Exception e) {
			LOGGER.error("Exception while handling success "
					+ "GSTR2BSingleResponseProcessor ", e);
			String msg = e.getMessage().length() > 500
					? e.getMessage().substring(0, 498) : e.getMessage();
			if (isAuto) {
				gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, msg);
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
						LocalDateTime.now());
			} else {
				gstnStatusRepo.updateStatus(gstin, taxPeriod,
						APIConstants.GSTR2B, APIConstants.FAILED, null, msg,
						LocalDateTime.now());
			}
			batchRepo.updateStatus(APIConstants.FAILED,
					APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
					taxPeriod, LocalDateTime.now(), false, "ER-1000", msg);
			throw new APIException(e.getLocalizedMessage());
		}
	}
}
