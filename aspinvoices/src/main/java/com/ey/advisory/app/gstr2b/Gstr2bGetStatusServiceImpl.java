/**
 * 
 */
package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BB2bHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BB2baHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BCdnrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BCdnraHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BEcomHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BEcomaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgsezHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BItcItemRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BSuppSmryRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Component("Gstr2bGetStatusServiceImpl")
@Slf4j
public class Gstr2bGetStatusServiceImpl {
	
	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository respRepo;
	
	@Autowired
	@Qualifier("Gstr2BbatchUtil")
	private Gstr2BbatchUtil batchUtil;
	
	@Autowired
	@Qualifier("Gstr2RespParserImpl")
	private Gstr2RespParser parser;
	
	@Autowired
	@Qualifier("Gstr2GetGstr2BB2bHeaderRepository")
	private Gstr2GetGstr2BB2bHeaderRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BB2baHeaderRepository")
	private Gstr2GetGstr2BB2baHeaderRepository b2bAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BCdnrHeaderRepository")
	private Gstr2GetGstr2BCdnrHeaderRepository cdnrHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BCdnraHeaderRepository")
	private Gstr2GetGstr2BCdnraHeaderRepository cdnrAHeaderRepo;

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
	@Qualifier("Gstr2GetGstr2BEcomHeaderRepository")
	private Gstr2GetGstr2BEcomHeaderRepository ecomHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BEcomaHeaderRepository")
	private Gstr2GetGstr2BEcomaHeaderRepository ecomAHeaderRepo;

	@Transactional(value = "clientTransactionManager")
	public Pair<String, String> getStatus(SuccessResult result, String apiParams){
		
		List<Long> resultIds = result.getSuccessIds();
		String ctxParams = result.getCtxParams();
		Gson gson = GsonUtil.newSAPGsonInstance();

		String gstin = null;
		String taxPeriod = null;
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime modifiedOn = now;

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
				.getAsJsonObject();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GET Call is Success for the "
					+ "Gstr2bGetStatusServiceImpl");
		}
		
		
		//soft deleting data from tables.
		
		//softdeleting data from tables.

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inkoiking softlyDeleteHeader : Gstr2bGetStatusServiceImpl");
		}
		int cntItc = itcSummaryRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : Itc Summary table Row deleted {}", cntItc);
		}
		int suppSmryCnt = suppSummaryRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : Supplier Summary table Row deleted {}", suppSmryCnt);
		}
		int b2bCnt = b2bHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : B2B table Row deleted {}", b2bCnt);
		}
		int b2bAcnt = b2bAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : B2BA  table Row deleted {}", b2bAcnt);
		}
		int cdnrCnt = cdnrHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : CDNR table Row deleted {}", cdnrCnt);
		}
		int cdnrACnt = cdnrAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : CDNRA table Row deleted {}", cdnrACnt);
		}
		int isdcnt = isdHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : ISD table Row deleted {}", isdcnt);
		}
		int isdACnt = isdAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : ISDA table Row deleted {}", isdACnt);
		}
		int impgCnt = impgHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : IMPG table Row deleted {}", impgCnt);
		}
		int impgSezCnt = impgSezHeaderRepo.softlyDeleteHeader(gstin, taxPeriod, modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2bGetStatusServiceImpl : IMPG SEZ table Row deleted {}", impgSezCnt);
			LOGGER.debug("Inkoiking All softlyDeleteHeader : Gstr2bGetStatusServiceImpl");
		}
		int ecomCnt = ecomHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
				modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2BGetSuccessHandler : Ecom table Row deleted {}"
							+ " for gstin {}, taxPeriod {}",
							ecomCnt, gstin, taxPeriod);
		}
		int ecomAcnt = ecomAHeaderRepo.softlyDeleteHeader(gstin, taxPeriod,
				modifiedOn, userName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2BGetSuccessHandler :EcomA  table Row deleted {}"
							+ " for gstin {}, taxPeriod {}",
							ecomAcnt, gstin, taxPeriod);
		}
		

		for (Long id : resultIds) {
			String apiResp = APIInvokerUtil.getResultById(id);

			JsonObject requestObject = (new JsonParser()
					.parse((String) apiResp)).getAsJsonObject();

			Gstr2BGETDataDto reqDto = gson.fromJson(requestObject,
					Gstr2BGETDataDto.class);

			gstin = reqDto.getDetailedData() != null
					? reqDto.getDetailedData().getRGstin() : null;
			taxPeriod = reqDto.getDetailedData() != null
					? reqDto.getDetailedData().getTaxpeiod() : null;

			//parsing ad saving json
			parser.pasrseResp(true, reqDto, id);
		}
		

		boolean isTokenResp = false;
		if (resultIds != null && resultIds.size() > 1) {
			isTokenResp = true;
		}
		batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
				APIConstants.SUCCESS, null, null, isTokenResp, null);
		
		return new Pair<String, String>(gstin, taxPeriod);

	}
	

}
