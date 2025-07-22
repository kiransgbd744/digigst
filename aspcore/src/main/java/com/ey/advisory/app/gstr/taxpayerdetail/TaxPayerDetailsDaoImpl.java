package com.ey.advisory.app.gstr.taxpayerdetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.ApiCallLimitExceededException;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstnapi.services.GenerateAuthTokenService;
import com.ey.advisory.gstnapi.services.PublicApiEndPointResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("taxPayerDetailsDaoImpl")
public class TaxPayerDetailsDaoImpl implements TaxPayerDetailsDao {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("GenerateAuthTokenServiceImpl")
	GenerateAuthTokenService authTokenService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gstnAuthService;

	@Autowired
	private PublicApiEndPointResolver endPointResolver;

	@Override
	public APIResponse findTaxPayerDetails(String gstin, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Getting Tax Payer Details for %s "
					+ "Gstin from TaxPayerDetailsDaoLayer ", gstin);
			LOGGER.debug(msg);
		}

		APIParam param1 = new APIParam("gstin", gstin);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GET_GSTIN_DETAILS, param1);
		try {
			
			endPointResolver.resolveEndPoint(PublicApiConstants.SEARCH);
			PublicApiContext.setContextMap(PublicApiConstants.GSTIN, gstin);
			return apiExecutor.execute(params, null);
		} catch (ApiCallLimitExceededException e) {
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-1000", e.getMessage()));
		} catch (Exception e){
			APIResponse errResp = new APIResponse();
			return errResp.addError(new APIError("ER-2000", e.getMessage()));
		
		}

	}

}
