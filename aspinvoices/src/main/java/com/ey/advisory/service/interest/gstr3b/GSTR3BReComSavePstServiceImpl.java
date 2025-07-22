/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BSaveStatusEntryHandlerImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("GSTR3BReComSavePstServiceImpl")
public class GSTR3BReComSavePstServiceImpl
		implements GSTR3BReComSavePstService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("gstr3BSaveStatusEntryHandlerImpl")
	Gstr3BSaveStatusEntryHandlerImpl gstr3BSaveStatusEntryHandlerImpl;

	@Override
	public Pair<String, String> gstr3bReComSavePstImp(String apiAction,
			String gstin, String retPeriod, String reqData) {

		Pair<String, String> apiStatus = getRefIdforReComInt(apiAction, gstin,
				retPeriod, reqData);
		return apiStatus;
	}
	private Pair<String, String> getRefIdforReComInt(String apiAction,
			String gstin, String taxPeriod, String reqData) {
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN,
					apiAction.equalsIgnoreCase(APIConstants.GSTR3B_RECOMPUTE)
							? APIIdentifiers.GSTR3B_RECOMPINT
							: APIIdentifiers.GSTR3B_SAVEPAST_LIABILITY,
					param1, param2);
			APIResponse resp = apiExecutor.execute(params, reqData);

			if (resp.isSuccess()) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"GSTR3B Re-Compute for Gstn Call Completed"
									+ " for Polling and recieved response as :%s",
							resp.getResponse());
					LOGGER.debug(msg);
				}
				String saveJsonResp = resp.getResponse();
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser
						.parse(saveJsonResp);
				String refId = jsonObject.get(APIIdentifiers.REFERENECE_ID)
						.getAsString();

				gstr3BSaveStatusEntryHandlerImpl.createGstr3BSaveStatusEntry(
						taxPeriod, gstin, refId,
						apiAction
								.equalsIgnoreCase(APIConstants.GSTR3B_RECOMPUTE)
										? APIConstants.RECOMPUTE_INITIATED
										: APIConstants.SAVEPSTLIAB_INITIATED,
						null, TenantContext.getTenantId(), reqData,
						resp.getResponse(), apiAction);

				return new Pair<>("Success", refId);
			} else {
				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();
				String msg = String.format(
						"GSTR3B '%s' for gstin '%s' and taxPeriod '%s'."
								+ " ErrorCode: '%s' and ErrorDesc: '%s'",
						apiAction, gstin, taxPeriod, errorCode, errorDesc);
				LOGGER.error(msg);
				return new Pair<>("Error", resp.getError().getErrorDesc());
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking GSTR3B  for '%s',"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					apiAction, gstin, taxPeriod);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}

	}
}
