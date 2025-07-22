package com.ey.advisory.gstnapi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIReqParts;
import com.ey.advisory.core.api.impl.APIResponseDecryptor;
import com.ey.advisory.core.api.impl.APIResponseHandler;
import com.ey.advisory.core.api.impl.APIResponseProcessor;
import com.ey.advisory.core.api.impl.SaveBatchReqAndRespPayloadDumpHelper;

@Component("GSTNAPISuccessResponseHandler")
public class GSTNAPISuccessResponseHandler implements APIResponseHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSTNAPISuccessResponseHandler.class);

	@Autowired
	@Qualifier("APIResponseProcessorImpl")
	private APIResponseProcessor apiResponseProcessor;

	@Autowired
	@Qualifier("APIResponseDecryptorImpl")
	private APIResponseDecryptor decryptor;

	@Autowired
	private SaveBatchReqAndRespPayloadDumpHelper saveBatchHelper;

	@Override
	public APIResponse handleResponse(APIParams params, APIConfig config,
			APIExecParties parties, APIReqParts reqParts, APIAuthInfo authInfo,
			String response, Map<String, Object> context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "GSTN API returned Success!! About to "
					+ "decrypt and post process the response...";
			LOGGER.debug(msg);
		}

		// Decrypt the encrypted data.
		APIResponse resp = decryptor.decrypt(params, config, parties, authInfo,
				response, context);
		
		//Storing the SaveToGstn and Return status api success response 
		//Jsons in batch table.
		saveBatchHelper.dumpRespJsonPayload(params, resp.getResponse());
		
		APIResponse retResp = apiResponseProcessor.processResponse(params,
				config, parties, authInfo, resp, context);
		String txnId = (String) context.get(APIContextConstants.TXN_ID_KEY);
		retResp.setTxnId(txnId);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Successfully decrypted the API Response from "
							+ "GSTN and processed the response!! for txn - '%s'",
					txnId);
			LOGGER.debug(msg);
		}

		return retResp;
	}

}
