package com.ey.advisory.app.services.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("iTCLedgerDetailsDataAtGstnImpl")
public class ITCLedgerDetailsDataAtGstnImpl
		implements ITCLedgerDetailsDataAtGstn {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CashLedgerDetailsDataAtGstnImpl.class);

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;
	
	@Override
	public String fromGstn(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("fr_dt", dto.getFromDate());
		APIParam param3 = new APIParam("to_dt", dto.getToDate());
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.LEDGER_GET_ITC, param1,
				param2, param3);
		APIResponse resp = apiExecutor.execute(params, null);
		// If the GSTN API returns a failure code, then throw an exception
		if (!resp.isSuccess()) {
			String msg = String.format(
					"Failed to do Get ITC Ledger Details from Gstn, Error Response is '%s'",
					resp.getError());
			LOGGER.error(msg);
			//throw new AppException(resp.getError().toString());
			return null;
		}
		return resp.getResponse();
	}
	
		@Override
	public String fromGstnTest(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("fr_dt", dto.getFromDate());
		APIParam param3 = new APIParam("to_dt", dto.getToDate());
	
		String respJson = "{\"itcLdgDtls\":{\"gstin\":\"05AVHPB7348G1ZP\",\"fr_dt\":\"31/05/2017\","
				+ "\"to_dt\":\"01/06/2017\",\"op_bal\":{\"desc\":\"Opening Balance\",\"igstTaxBal\":395,"
				+ "\"cgstTaxBal\":501234,\"sgstTaxBal\":492337,\"cessTaxBal\":493397,\"tot_rng_bal\":594439},"
				+ "\"tr\":[{\"dt\":\"31/05/2017\",\"desc\":\"Transitional  Cenvat Credit / VAT credit \""
				+ ",\"ref_no\":\"AA0520-1000008D\",\"ret_period\":\"122016\",\"sgstTaxAmt\":345251,\"cgstTaxAmt\":1221300,"
				+ "\"igstTaxAmt\":513100,\"cessTaxAmt\":0,\"igstTaxBal\":913213,\"cgstTaxBal\":91419,\"sgstTaxBal\":234220,\"cessTaxBal\":495690,"
				+ "\"tot_rng_bal\":578187,\"tot_tr_amt\":700000,\"tr_typ\":\"Cr\"},{\"dt\":\"01/06/2017\",\"desc\":"
				+ "\"Other than reverse charge\",\"ref_no\":\"DI0506170000005\",\"ret_period\":\"122016\",\"sgstTaxAmt\":11111,"
				+ "\"cgstTaxAmt\":078229,\"igstTaxAmt\":123344,\"cessTaxAmt\":235545,\"igstTaxBal\":237777,\"cgstTaxBal\":4327123,\"sgstTaxBal\":111111,"
				+ "\"cessTaxBal\":496783,\"tot_rng_bal\":517673,\"tot_tr_amt\":678991,\"tr_typ\":\"Dr\"}],\"cl_bal\":"
				+ "{\"desc\":\"Closing Balance\",\"igstTaxBal\":977774,\"cgstTaxBal\":123456,\"sgstTaxBal\":989896,\"cessTaxBal\":493483,"
				+ "\"tot_rng_bal\":517378}},\"provCrdBalList\":{\"provCrdBal\":[{\"ret_period\":\"122016\",\"cgstProCrBal\":234567,"
				+ "\"igstProCrBal\":343434,\"sgstProCrBal\":464646,\"cessProCrBal\":828282,\"totProCrBal\":162162}]}}";
		
		APIResponse resp = new APIResponse();
		resp.setResponse(respJson);
		
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			String msg = "Failed to do Get ITC Ledger Details from Gstn";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(msg);
			}
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		// Build the Json Object.
		return resp.getResponse();
	}
	
	

}
