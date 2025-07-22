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
@Service("cashLedgerDetailsDataAtGstnImpl")
public class CashLedgerDetailsDataAtGstnImpl
		implements CashLedgerDetailsDataAtGstn {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CashLedgerDetailsDataAtGstnImpl.class);

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	/*
	 * @Override public String fromGstn(GetCashLedgerDetailsReqDto dto) { //
	 * Invoke the GSTN API - Get Return Status API and get the JSON. APIParam
	 * param1 = new APIParam("gstin", dto.getGstin()); APIParam param2 = new
	 * APIParam("fr_dt", dto.getFromDate()); APIParam param3 = new
	 * APIParam("to_dt", dto.getToDate()); APIParams params = new
	 * APIParams(groupCode, APIProviderEnum.GSTN,
	 * APIIdentifiers.LEDGER_GET_CASH, param1, param2, param3); //APIResponse
	 * resp = apiExecutor.execute(params, null);
	 * 
	 * String jsonResponse =
	 * "{ \"gstin\":\"05AVHPB7348G1ZP\",\"fr_dt\":\"30/05/2017\",\"to_dt\":\"01/06/2017\","
	 * +
	 * "\"op_bal\":{\"desc\":\"Opening Balance\",\"igstbal\":{\"tx\":12345678,\"intr\":10001306,\"pen\":10001217,\"fee\":10001126,\"oth\":10001044,\"tot\":10013725},"
	 * +
	 * "\"cgstbal\":{\"tx\":10000078,\"intr\":10004018,\"pen\":10002929,\"fee\":10001835,\"oth\":10004954,\"tot\":10013814},\"sgstbal\":{\"tx\":10002045,\"intr\":10001027,"
	 * +
	 * "\"pen\":10000940,\"fee\":10000847,\"oth\":10000859,\"tot\":10005718},\"cessbal\":{\"tx\":10000048,\"intr\":10000938,\"pen\":10000850,\"fee\":10000756,\"oth\":10000769,\"tot\":10003361},\"tot_rng_bal\":10036618},"
	 * +
	 * "\"cl_bal\":{\"desc\":\"Closing Balance\",\"igstbal\":{\"tx\":10000101,\"intr\":10000102,\"pen\":10000103,\"fee\":10000104,\"oth\":10000105,\"tot\":10000106},\"cgstbal\":"
	 * +
	 * "{\"tx\":10000201,\"intr\":10000201,\"pen\":10000203,\"fee\":10000204,\"oth\":10000205,\"tot\":10000206},\"sgstbal\":{\"tx\":10000301,\"intr\":10000302,\"pen\":10000303,\"fee\":10000304,\"oth\":10000305,\"tot\":10000306}"
	 * +
	 * ",\"cessbal\":{\"tx\":10000401,\"intr\":10000402,\"pen\":10000403,\"fee\":10000404,\"oth\":10000405,\"tot\":10000406},\"tot_rng_bal\":10037120},\"tr\":[{\"dpt_dt\":\"30/05/2017\","
	 * +
	 * "\"rpt_dt\":\"10/06/2017\",\"dpt_time\":\"11:20:10\",\"desc\":\"Other than reverse charge\",\"igst\":{\"tx\":10000201,\"intr\":10000202,\"pen\":10000203,\"fee\":10000204,\"oth\":10000205,\"tot\":10000206}"
	 * +
	 * ",\"cgst\":{\"tx\":10000207,\"intr\":10000208,\"pen\":10000209,\"fee\":10000210,\"oth\":10000211,\"tot\":10000212},\"sgst\":{\"tx\":10000213,\"intr\":10000214,\"pen\":10000215,\"fee\":10000216,\"oth\":10000217,\"tot\":10000218},\"cess\":{\"tx\":10000219,\"intr\":10000220,\"pen\":10000221,\"fee\":10000222,\"oth\":10000223,\"tot\":10000224},"
	 * +
	 * "\"igstbal\":{\"tx\":10000091,\"intr\":10000092,\"pen\":10000093,\"fee\":10000094,\"oth\":10000095,\"tot\":10000096},\"cgstbal\":{\"tx\":10000097,\"intr\":10000098,\"pen\":10000099,\"fee\":10000100,\"oth\":10000101,\"tot\":10000102},"
	 * +
	 * "\"sgstbal\":{\"tx\":10000103,\"intr\":10000104,\"pen\":10000105,\"fee\":10000106,\"oth\":10000107,\"tot\":10000108},\"cessbal\":{\"tx\":10000109,\"intr\":10000110,\"pen\":10000111,\"fee\":10000112,\"oth\":10000113,\"tot\":10000114},"
	 * +
	 * "\"tr_typ\":\"Cr\",\"tot_tr_amt\":10002100,\"tot_rng_bal\":10002100,\"ret_period\":\"122016\",\"refNo\":\"AA010123456789A\"},{\"dpt_dt\":\"01/06/2017\","
	 * +
	 * "\"desc\":\"Other than reverse charge\",\"igst\":{\"tx\":10000001,\"intr\":10000000,\"pen\":10000000,\"fee\":10000000,\"oth\":10000000,\"tot\":10000001},\"cgst\":{\"tx\":10000000,\"intr\":10000000,\"pen\":10000000,\"fee\":10000000,\"oth\":10000000,\"tot\":10000000},\"sgst\":{\"tx\":10000000,\"intr\":10000000,\"pen\":10000000,\"fee\":10000000,\"oth\":10000000,\"tot\":10000000},"
	 * +
	 * "\"cess\":{\"tx\":10000000,\"intr\":10000000,\"pen\":10000000,\"fee\":10000000,\"oth\":10000000,\"tot\":10000000},\"igstbal\":{\"tx\":19137,\"intr\":10001306,\"pen\":10001217,\"fee\":10001126,\"oth\":10001044,\"tot\":10000000},\"cgstbal\":{\"tx\":10000080,\"intr\":10004218,\"pen\":10003129,\"fee\":10002035,\"oth\":10004954,\"tot\":10000000},"
	 * +
	 * "\"sgstbal\":{\"tx\":10002238,\"intr\":10001127,\"pen\":10001040,\"fee\":10000947,\"oth\":10000859,\"tot\":10000000},\"cessbal\":"
	 * +
	 * "{\"tx\":10000120,\"intr\":10001038,\"pen\":10000950,\"fee\":10000856,\"oth\":10000769,\"tot\":10000000},\"tr_typ\":\"Dr\",\"tot_tr_amt\":10000001,\"tot_rng_bal\":10038190,\"ret_period\":\"122016\",\"refNo\":\"DC0506170000004\"},{\"dpt_dt\":\"01/06/2017\","
	 * +
	 * "\"desc\":\"Other than reverse charge\",\"igst\":{\"tx\":10000001,\"intr\":10000000,\"pen\":10000000,\"fee\":10000000,\"oth\":10000000,\"tot\":10000001},\"cgst\":{\"tx\":10000001,\"intr\":10000100,\"pen\":10000100,\"fee\":10000100,\"oth\":10000000,\"tot\":10000301},\"sgst\":{\"tx\":10000096,\"intr\":10000100,\"pen\":10000100,\"fee\":10000100,\"oth\":10000000,\"tot\":10000396},"
	 * +
	 * "\"cess\":{\"tx\":1000072,\"intr\":10000100,\"pen\":10000100,\"fee\":10000100,\"oth\":10000000,\"tot\":10000372},\"igstbal\":{\"tx\":19136,\"intr\":10001306,\"pen\":10001217,\"fee\":10001126,\"oth\":10001044,\"tot\":10000000},"
	 * +
	 * "\"cgstbal\":{\"tx\":10000079,\"intr\":10004118,\"pen\":10003029,\"fee\":10001935,\"oth\":10004954,\"tot\":10000000},\"sgstbal\":{\"tx\":10002142,\"intr\":10001027,\"pen\":10000940,\"fee\":10000847,\"oth\":10000859,\"tot\":10000000},"
	 * +
	 * "\"cessbal\":{\"tx\":10000048,\"intr\":10000938,\"pen\":10000850,\"fee\":10000756,\"oth\":10000769,\"tot\":10000000},\"tr_typ\":\"Dr\",\"tot_tr_amt\":10001070,\"tot_rng_bal\":10037120,\"ret_period\":\"122016\",\"refNo\":\"DC0506170000007\"}]}";
	 * 
	 * 
	 * String jsonResponse =
	 * "{ \"gstin\":\"05AVHPB7348G1ZP\",\"fr_dt\":\"30/05/2017\",\"to_dt\":\"01/06/2017\","
	 * +
	 * "\"op_bal\":{\"desc\":\"Opening Balance\",\"igstbal\":{\"tx\":12345678,\"intr\":1306,\"pen\":1217,\"fee\":1126,\"oth\":1044,\"tot\":13725},"
	 * +
	 * "\"cgstbal\":{\"tx\":78,\"intr\":4018,\"pen\":2929,\"fee\":1835,\"oth\":4954,\"tot\":13814},\"sgstbal\":{\"tx\":2045,\"intr\":1027,"
	 * +
	 * "\"pen\":940,\"fee\":847,\"oth\":859,\"tot\":5718},\"cessbal\":{\"tx\":48,\"intr\":938,\"pen\":850,\"fee\":756,\"oth\":769,\"tot\":3361},\"tot_rng_bal\":36618},"
	 * +
	 * "\"cl_bal\":{\"desc\":\"Closing Balance\",\"igstbal\":{\"tx\":101,\"intr\":102,\"pen\":103,\"fee\":104,\"oth\":105,\"tot\":106},\"cgstbal\":"
	 * +
	 * "{\"tx\":201,\"intr\":201,\"pen\":203,\"fee\":204,\"oth\":205,\"tot\":206},\"sgstbal\":{\"tx\":301,\"intr\":302,\"pen\":303,\"fee\":304,\"oth\":305,\"tot\":306}"
	 * +
	 * ",\"cessbal\":{\"tx\":401,\"intr\":402,\"pen\":403,\"fee\":404,\"oth\":405,\"tot\":406},\"tot_rng_bal\":37120},\"tr\":[{\"dpt_dt\":\"30/05/2017\","
	 * +
	 * "\"rpt_dt\":\"10/06/2017\",\"dpt_time\":\"11:20:10\",\"desc\":\"Other than reverse charge\",\"igst\":{\"tx\":201,\"intr\":202,\"pen\":203,\"fee\":204,\"oth\":205,\"tot\":206}"
	 * +
	 * ",\"cgst\":{\"tx\":207,\"intr\":208,\"pen\":209,\"fee\":210,\"oth\":211,\"tot\":212},\"sgst\":{\"tx\":213,\"intr\":214,\"pen\":215,\"fee\":216,\"oth\":217,\"tot\":218},\"cess\":{\"tx\":219,\"intr\":220,\"pen\":221,\"fee\":222,\"oth\":223,\"tot\":224},"
	 * +
	 * "\"igstbal\":{\"tx\":91,\"intr\":92,\"pen\":93,\"fee\":94,\"oth\":95,\"tot\":96},\"cgstbal\":{\"tx\":97,\"intr\":98,\"pen\":99,\"fee\":100,\"oth\":101,\"tot\":102},"
	 * +
	 * "\"sgstbal\":{\"tx\":103,\"intr\":104,\"pen\":105,\"fee\":106,\"oth\":107,\"tot\":108},\"cessbal\":{\"tx\":109,\"intr\":110,\"pen\":111,\"fee\":112,\"oth\":113,\"tot\":114},"
	 * +
	 * "\"tr_typ\":\"Cr\",\"tot_tr_amt\":2100,\"tot_rng_bal\":2100,\"ret_period\":\"122016\",\"refNo\":\"AA010123456789A\"},{\"dpt_dt\":\"01/06/2017\","
	 * +
	 * "\"desc\":\"Other than reverse charge\",\"igst\":{\"tx\":1,\"intr\":0,\"pen\":0,\"fee\":0,\"oth\":0,\"tot\":1},\"cgst\":{\"tx\":0,\"intr\":0,\"pen\":0,\"fee\":0,\"oth\":0,\"tot\":0},\"sgst\":{\"tx\":0,\"intr\":0,\"pen\":0,\"fee\":0,\"oth\":0,\"tot\":0},"
	 * +
	 * "\"cess\":{\"tx\":0,\"intr\":0,\"pen\":0,\"fee\":0,\"oth\":0,\"tot\":0},\"igstbal\":{\"tx\":9137,\"intr\":1306,\"pen\":1217,\"fee\":1126,\"oth\":1044,\"tot\":0},\"cgstbal\":{\"tx\":80,\"intr\":4218,\"pen\":3129,\"fee\":2035,\"oth\":4954,\"tot\":0},"
	 * +
	 * "\"sgstbal\":{\"tx\":2238,\"intr\":1127,\"pen\":1040,\"fee\":947,\"oth\":859,\"tot\":0},\"cessbal\":"
	 * +
	 * "{\"tx\":120,\"intr\":1038,\"pen\":950,\"fee\":856,\"oth\":769,\"tot\":0},\"tr_typ\":\"Dr\",\"tot_tr_amt\":1,\"tot_rng_bal\":38190,\"ret_period\":\"122016\",\"refNo\":\"DC0506170000004\"},{\"dpt_dt\":\"01/06/2017\","
	 * +
	 * "\"desc\":\"Other than reverse charge\",\"igst\":{\"tx\":1,\"intr\":0,\"pen\":0,\"fee\":0,\"oth\":0,\"tot\":1},\"cgst\":{\"tx\":1,\"intr\":100,\"pen\":100,\"fee\":100,\"oth\":0,\"tot\":301},\"sgst\":{\"tx\":96,\"intr\":100,\"pen\":100,\"fee\":100,\"oth\":0,\"tot\":396},"
	 * +
	 * "\"cess\":{\"tx\":72,\"intr\":100,\"pen\":100,\"fee\":100,\"oth\":0,\"tot\":372},\"igstbal\":{\"tx\":9136,\"intr\":1306,\"pen\":1217,\"fee\":1126,\"oth\":1044,\"tot\":0},"
	 * +
	 * "\"cgstbal\":{\"tx\":79,\"intr\":4118,\"pen\":3029,\"fee\":1935,\"oth\":4954,\"tot\":0},\"sgstbal\":{\"tx\":2142,\"intr\":1027,\"pen\":940,\"fee\":847,\"oth\":859,\"tot\":0},"
	 * +
	 * "\"cessbal\":{\"tx\":48,\"intr\":938,\"pen\":850,\"fee\":756,\"oth\":769,\"tot\":0},\"tr_typ\":\"Dr\",\"tot_tr_amt\":1070,\"tot_rng_bal\":37120,\"ret_period\":\"122016\",\"refNo\":\"DC0506170000007\"}]}";
	 * 
	 * String jsonResponse =
	 * "{ \"gstin\":\"05AVHPB7348G1ZP\",\"fr_dt\":\"30/05/2017\",\"to_dt\":\"01/06/2017\","
	 * +
	 * "\"op_bal\":{\"desc\":\"Opening Balance\",\"igstbal\":{\"tx\":9032,\"intr\":1306,\"pen\":1217,\"fee\":1126,\"oth\":1044,\"tot\":13725},"
	 * +
	 * "\"cgstbal\":{\"tx\":78,\"intr\":4018,\"pen\":2929,\"fee\":1835,\"oth\":4954,\"tot\":13814},\"sgstbal\":{\"tx\":2045,\"intr\":1027,"
	 * +
	 * "\"pen\":940,\"fee\":847,\"oth\":859,\"tot\":5718},\"cessbal\":{\"tx\":48,\"intr\":938,\"pen\":850,\"fee\":756,\"oth\":769,\"tot\":3361},\"tot_rng_bal\":36618},"
	 * +
	 * "\"cl_bal\":{\"desc\":\"Closing Balance\",\"igstbal\":{\"tx\":101,\"intr\":102,\"pen\":103,\"fee\":104,\"oth\":105,\"tot\":106},\"cgstbal\":"
	 * +
	 * "{\"tx\":201,\"intr\":201,\"pen\":203,\"fee\":204,\"oth\":205,\"tot\":206},\"sgstbal\":{\"tx\":301,\"intr\":302,\"pen\":303,\"fee\":304,\"oth\":305,\"tot\":306}"
	 * +
	 * ",\"cessbal\":{\"tx\":401,\"intr\":402,\"pen\":403,\"fee\":404,\"oth\":405,\"tot\":406},\"tot_rng_bal\":37120},\"tr\":[";
	 * 
	 * // Additional transactions for (int i = 0; i < 50; i++) { jsonResponse +=
	 * "{\"dpt_dt\":\"01/06/2017\"," +
	 * "\"desc\":\"Other than reverse charge\",\"igst\":{\"tx\":1,\"intr\":0,\"pen\":0,\"fee\":0,\"oth\":0,\"tot\":1},"
	 * +
	 * "\"cgst\":{\"tx\":1,\"intr\":100,\"pen\":100,\"fee\":100,\"oth\":0,\"tot\":301},\"sgst\":{\"tx\":96,\"intr\":100,\"pen\":100,\"fee\":100,\"oth\":0,\"tot\":396},"
	 * +
	 * "\"cess\":{\"tx\":72,\"intr\":100,\"pen\":100,\"fee\":100,\"oth\":0,\"tot\":372},\"igstbal\":{\"tx\":9136,\"intr\":1306,\"pen\":1217,\"fee\":1126,\"oth\":1044,\"tot\":0},"
	 * +
	 * "\"cgstbal\":{\"tx\":79,\"intr\":4118,\"pen\":3029,\"fee\":1935,\"oth\":4954,\"tot\":0},\"sgstbal\":{\"tx\":2142,\"intr\":1027,\"pen\":940,\"fee\":847,\"oth\":859,\"tot\":0},"
	 * +
	 * "\"cessbal\":{\"tx\":48,\"intr\":938,\"pen\":850,\"fee\":756,\"oth\":769,\"tot\":0},\"tr_typ\":\"Dr\",\"tot_tr_amt\":1070,\"tot_rng_bal\":37120,\"ret_period\":\"122016\",\"refNo\":\"DC0506170000"
	 * + (i + 8) + "\"},"; }
	 * 
	 * // Remove the trailing comma from the last transaction jsonResponse =
	 * jsonResponse.substring(0, jsonResponse.length() - 1);
	 * 
	 * // Complete the JSON string jsonResponse += "]}";
	 * 
	 * 
	 * APIResponse resp = new APIResponse(); resp.setResponse(jsonResponse); //
	 * If the GSTN API returns a failure code, then throw an exception // so
	 * that we can update the batch table with the error description. if
	 * (!resp.isSuccess()) { String msg =
	 * "Failed to do Get Cash Ledger Details from Gstn"; if
	 * (LOGGER.isErrorEnabled()) { LOGGER.error(msg); } JsonObject resp1 = new
	 * JsonObject(); resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E",
	 * msg))); return resp1.toString(); } // Build the Json Object. return
	 * resp.getResponse(); }
	 */

	@Override
	public String fromGstn(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("fr_dt", dto.getFromDate());
		APIParam param3 = new APIParam("to_dt", dto.getToDate());
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.LEDGER_GET_CASH, param1,
				param2, param3);
		try {
			APIResponse resp = apiExecutor.execute(params, null);
			if (!resp.isSuccess()) {
				String msg = String
						.format("Failed to do Get Cash Ledger Details from Gstn,"
								+ " Error Reponse is '%s'", resp.getError());
				LOGGER.error(msg);
				// throw new AppException(resp.getError().toString());
				return null;
			}
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the Cash Ledger API", ex);
			throw new AppException(ex);
		}
	}
	@Override
	public String fromRcmGstn(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
	
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.LEDGER_GET_RCM, param1);
		try {
			APIResponse resp = apiExecutor.execute(params, null);
			if (!resp.isSuccess()) {
				String msg = String
						.format("Failed to do Get RCM Ledger Opening Balance Details from Gstn,"
								+ " Error Reponse is '%s'", resp.getError());
				LOGGER.error(msg);
				// throw new AppException(resp.getError().toString());
				return null;
			}
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the Rcm Ledger opening Balance API", ex);
			throw new AppException(ex);
		}
	}
	
	@Override
	public String fromReclaimGstn(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
	
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.LEDGER_GET_RECLAIM, param1);
		try {
			APIResponse resp = apiExecutor.execute(params, null);
			if (!resp.isSuccess()) {
				String msg = String
						.format("Failed to do Get reclaim Ledger Opening Balance Details from Gstn,"
								+ " Error Reponse is '%s'", resp.getError());
				LOGGER.error(msg);
				// throw new AppException(resp.getError().toString());
				return null;
			}
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the reclaim Ledger opening Balance API", ex);
			throw new AppException(ex);
		}
	}
	

	@Override
	public String getCreditReversalAndReclaimfromGstn(
			GetCashLedgerDetailsReqDto dto) { //
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("frdt", dto.getFromDate());
		APIParam param3 = new APIParam("todt", dto.getToDate());
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN,APIIdentifiers.LEDGER_GET_CRREV_AND_RECLAIM_CASH, param1,param2,
				param3);
		try {
			APIResponse resp = apiExecutor.execute(params, null);
			if (!resp.isSuccess()) {
				String msg = String
						.format("Failed to get credit reversal and reclaim Ledger Details from Gstn,"
								+ " Error Reponse is '%s'", resp.getError());
				LOGGER.error(msg); // throw
				new AppException(resp.getError().toString());
				return null;
			}
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the reversal and reclaim Ledger Details", ex);
			//throw new AppException(ex);
			//return null;
		}
		return null;
	}

	@Override
	public String getCreditReversalAndReclaimfromGstnTest(
			GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("fr_dt", dto.getFromDate());
		APIParam param3 = new APIParam("to_dt", dto.getToDate());
		/*
		 * APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
		 * APIIdentifiers.LEDGER_GET_CASH, param1, param2, param3);
		 */
		// APIResponse resp = apiExecutor.execute(params, null);
		String jsonResponse = "{" +
			    "\"gstin\": \"24MAYAS0100E9Z9\"," +
			    "\"frdt\": \"01/05/2023\"," +
			    "\"todt\": \"01/06/2023\"," +
			    "\"opnbal\": {" +
			    "\"igst\": 100123," +
			    "\"cgst\": 101123," +
			    "\"sgst\": 102123," +
			    "\"cess\": 103123" +
			    "}," +
			    "\"tr\": [" +
			    "{" +
			    "\"trandt\": \"03/05/2023\"," +
			    "\"desc\": \"Opening Balance reported for <01/04/2022>\"," +
			    "\"refno\": \"1324\"," +
			    "\"itc4b2\": {" +
			    "\"igst\": 104123," +
			    "\"cgst\": 105123," +
			    "\"sgst\": 106123," +
			    "\"cess\": 107123" +
			    "}," +
			    "\"clsbal\": {" +
			    "\"igst\": 108123," +
			    "\"cgst\": 109123," +
			    "\"sgst\": 110123," +
			    "\"cess\": 111123" +
			    "}" +
			    "}," +
			    "{" +
			    "\"trandt\": \"03/05/2023\"," +
			    "\"rtnprd\": \"042023\"," +
			    "\"desc\": \"Filing of Form GSTR-3B/3BQ\"," +
			    "\"refno\": \"AA0612214478854\"," +
			    "\"itc4a5\": {" +
			    "\"igst\": 112123," +
			    "\"cgst\": 113123," +
			    "\"sgst\": 114123," +
			    "\"cess\": 115123" +
			    "}," +
			    "\"itc4b2\": {" +
			    "\"igst\": 116123," +
			    "\"cgst\": 117123," +
			    "\"sgst\": 118123," +
			    "\"cess\": 1189123" +
			    "}," +
			    "\"itc4d1\": {" +
			    "\"igst\": 119123," +
			    "\"cgst\": 120123," +
			    "\"sgst\": 121123," +
			    "\"cess\": 122123" +
			    "}," +
			    "\"clsbal\": {" +
			    "\"igst\": 123123," +
			    "\"cgst\": 124123," +
			    "\"sgst\": 125123," +
			    "\"cess\": 126123" +
			    "}" +
			    "}" +
			    "]," +
			    "\"clsbal\": {" +
			    "\"igst\": 127123," +
			    "\"cgst\": 128123," +
			    "\"sgst\": 129123," +
			    "\"cess\": 130123" +
			    "}" +
			    "}";


		APIResponse resp = new APIResponse();
		resp.setResponse(jsonResponse);
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			String msg = "Failed to do Get Cash Ledger Details from Gstn";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(msg);
			}
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return resp1.toString();
		}
		// Build the Json Object.
		return resp.getResponse();
	}
	
	@Override
	public String fromGstnDetailedRcmDetails(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("frdt", dto.getFromDat());
		APIParam param3 = new APIParam("todt", dto.getToDat());
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.GET_RCM_LEDGER_DETAILS, param1,
				param2, param3);
		try {
			APIResponse resp = apiExecutor.execute(params, null);
			if (!resp.isSuccess()) {
				String msg = String
						.format("Failed to do Get RCM Ledger Detailed Details from Gstn,"
								+ " Error Reponse is '%s'", resp.getError());
				LOGGER.error(msg);
				// throw new AppException(resp.getError().toString());
				return null;
			}
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the rcm Details Screen Ledger API", ex);
			throw new AppException(ex);
		}
	}
	
	@Override
	public String fromGstnDetailedNegativeDetails(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("from_date", dto.getFrmDate());
		APIParam param3 = new APIParam("to_date", dto.getToDte());
		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.NEGATIVE_LIABILITY, param1,
				param2, param3);
		try {
			APIResponse resp = apiExecutor.execute(params, null);
			if (!resp.isSuccess()) {
				String msg = String
						.format("Failed to do Get RCM Ledger Detailed Details from Gstn,"
								+ " Error Reponse is '%s'", resp.getError());
				LOGGER.error(msg);
				// throw new AppException(resp.getError().toString());
				return null;
			}
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the rcm Details Screen Ledger API", ex);
			throw new AppException(ex);
		}
	}

	
	//testing
	@Override
	public String fromGstnDetailedRcmDetailsTest(GetCashLedgerDetailsReqDto dto) {
		try {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		/*APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("frdt", dto.getFromDat());
		APIParam param3 = new APIParam("todt", dto.getToDat());*/
		/*APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.GET_RCM_LEDGER_DETAILS, param1,
				param2, param3);*/
		 String jsonResponse = "{\n" +
	                "  \"gstin\": \"24MAYAS0100E9Z9\",\n" +
	                "  \"frdt\": \"01/05/2023\",\n" +
	                "  \"todt\": \"01/06/2023\",\n" +
	                "  \"opnbal\": {\n" +
	                "    \"igst\": 395,\n" +
	                "    \"cgst\": 50,\n" +
	                "    \"sgst\": 497,\n" +
	                "    \"cess\": 4997\n" +
	                "  },\n" +
	                "  \"tr\": [\n" +
	                "    {\n" +
	                "      \"trandt\": \"03/05/2023\",\n" +
	                "      \"desc\": \"Opening Balance reported\",\n" +
	                "      \"refno\": \"1324\",\n" +
	                "      \"inwardsup_3_1d\": {\n" +
	                "        \"igst\": 100,\n" +
	                "        \"cgst\": 100,\n" +
	                "        \"sgst\": 100,\n" +
	                "        \"cess\": 100\n" +
	                "      },\n" +
	                "      \"clsbal\": {\n" +
	                "        \"igst\": 100,\n" +
	                "        \"cgst\": 100,\n" +
	                "        \"sgst\": 100,\n" +
	                "        \"cess\": 100\n" +
	                "      }\n" +
	                "    },\n" +
	                "    {\n" +
	                "      \"trandt\": \"03/05/2023\",\n" +
	                "      \"rtnprd\": \"042023\",\n" +
	                "      \"desc\": \"On filing of GSTR-3B/3BQ\",\n" +
	                "      \"refno\": \"AA0612214478854\",\n" +
	                "      \"itc4a2\": {\n" +
	                "        \"igst\": 100,\n" +
	                "        \"cess\": 100\n" +
	                "      },\n" +
	                "      \"itc4a3\": {\n" +
	                "        \"igst\": 100,\n" +
	                "        \"cgst\": 100,\n" +
	                "        \"sgst\": 100,\n" +
	                "        \"cess\": 100\n" +
	                "      },\n" +
	                "      \"inwardsup_3_1d\": {\n" +
	                "        \"igst\": 100,\n" +
	                "        \"cgst\": 100,\n" +
	                "        \"sgst\": 100,\n" +
	                "        \"cess\": 100\n" +
	                "      },\n" +
	                "      \"clsbal\": {\n" +
	                "        \"igst\": 100,\n" +
	                "        \"cgst\": 100,\n" +
	                "        \"sgst\": 100,\n" +
	                "        \"cess\": 100\n" +
	                "      }\n" +
	                "    }\n" +
	                "  ],\n" +
	                "  \"clsbal\": {\n" +
	                "    \"igst\": 395,\n" +
	                "    \"cgst\": 50,\n" +
	                "    \"sgst\": 497,\n" +
	                "    \"cess\": 4997\n" +
	                "  }\n" +
	                "}";
		APIResponse resp = new APIResponse();
		resp.setResponse(jsonResponse);
		
			
		
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the rcm Details Screen Ledger API", ex);
			throw new AppException(ex);
		}
	}
	
	@Override
	public String fromGstnDetailedNegativeDetailsTest(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
	/*	APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("from_date", dto.getFrmDate());
		APIParam param3 = new APIParam("to_date", dto.getToDte());*/
		/*APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.NEGATIVE_LIABILITY, param1,
				param2, param3);*/
		try {
		String jsonPayload = "{\n" +
                "  \"openbal\": [\n" +
                "    {\n" +
                "      \"desc\": \"Other than reverse charge\",\n" +
                "      \"trancd\": 30002,\n" +
                "      \"igst\": 11,\n" +
                "      \"cgst\": 12,\n" +
                "      \"sgst\": 13,\n" +
                "      \"cess\": 14\n" +
                "    },\n" +
                "    {\n" +
                "      \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "      \"trancd\": 15,\n" +
                "      \"igst\": 16,\n" +
                "      \"cgst\": 17,\n" +
                "      \"sgst\": 18,\n" +
                "      \"cess\": 19\n" +
                "    }\n" +
                "  ],\n" +
                "  \"closebal\": [\n" +
                "    {\n" +
                "      \"desc\": \"Other than reverse charge\",\n" +
                "      \"trancd\": 20,\n" +
                "      \"igst\": 21,\n" +
                "      \"cgst\": 22,\n" +
                "      \"sgst\": 23,\n" +
                "      \"cess\": 24\n" +
                "    },\n" +
                "    {\n" +
                "      \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "      \"trancd\": 25,\n" +
                "      \"igst\": 26,\n" +
                "      \"cgst\": 27,\n" +
                "      \"sgst\": 28,\n" +
                "      \"cess\": 29\n" +
                "    }\n" +
                "  ],\n" +
                "  \"negliabdtls\": [\n" +
                "    {\n" +
                "      \"rtnprd\": \"092019\",\n" +
                "      \"refno\": \"ab125632512123c\",\n" +
                "      \"trantyp\": \"Debit\",\n" +
                "      \"trandate\": \"12/10/2019\",\n" +
                "      \"desc\": \"Negative liability for GSTR3B/3BQ\",\n" +
                "      \"negliab\": [\n" +
                "        {\n" +
                "          \"desc\": \"Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 31,\n" +
                "          \"cgst\": 32,\n" +
                "          \"sgst\": 33,\n" +
                "          \"cess\": 34\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 35,\n" +
                "          \"igst\": 36,\n" +
                "          \"cgst\": 37,\n" +
                "          \"sgst\": 38,\n" +
                "          \"cess\": 39\n" +
                "        }\n" +
                "      ],\n" +
                "      \"negliabal\": [\n" +
                "        {\n" +
                "          \"desc\": \"Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 40,\n" +
                "          \"cgst\": 41,\n" +
                "          \"sgst\": 42,\n" +
                "          \"cess\": 43\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 30003,\n" +
                "          \"igst\": 44,\n" +
                "          \"cgst\": 45,\n" +
                "          \"sgst\": 46,\n" +
                "          \"cess\": 47\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"rtnprd\": \"092019\",\n" +
                "      \"refno\": \"ab125632512123c\",\n" +
                "      \"trantyp\": \"Credit\",\n" +
                "      \"trandate\": \"12/10/2019\",\n" +
                "      \"desc\": \"Negative liability for GSTR3B/3BQ\",\n" +
                "      \"negliab\": [\n" +
                "        {\n" +
                "          \"desc\": \"Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 48,\n" +
                "          \"cgst\": 49,\n" +
                "          \"sgst\": 50,\n" +
                "          \"cess\": 51\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 30003,\n" +
                "          \"igst\": 52,\n" +
                "          \"cgst\": 53,\n" +
                "          \"sgst\": 54,\n" +
                "          \"cess\": 55\n" +
                "        }\n" +
                "      ],\n" +
                "      \"negliabal\": [\n" +
                "        {\n" +
                "          \"desc\": \"Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 56,\n" +
                "          \"cgst\": 57,\n" +
                "          \"sgst\": 58,\n" +
                "          \"cess\": 59\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 30003,\n" +
                "          \"igst\": 60,\n" +
                "          \"cgst\": 61,\n" +
                "          \"sgst\": 62,\n" +
                "          \"cess\": 63\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
		APIResponse resp = new APIResponse();
		resp.setResponse(jsonPayload);
		
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the rcm Details Screen Ledger API", ex);
			throw new AppException(ex);
		}
	}
	
	@Override
	public String fromGstnRcmGetDetailsTest() {
		try {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		/*APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("frdt", dto.getFromDat());
		APIParam param3 = new APIParam("todt", dto.getToDat());*/
		/*APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.GET_RCM_LEDGER_DETAILS, param1,
				param2, param3);*/
			String jsonResponse = "{\n" +
		            "  \"gstin\": \"24MAYAS0100E9Z9\",\n" +
		            "  \"clsbal\": {\n" +
		            "    \"igst\": 100,\n" +
		            "    \"cgst\": 200,\n" +
		            "    \"sgst\": 200,\n" +
		            "    \"cess\": 100\n" +
		            "  }\n" +
		            "}";
		APIResponse resp = new APIResponse();
		resp.setResponse(jsonResponse);
		
			
		
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the rcm Details Screen Ledger API", ex);
			throw new AppException(ex);
		}
	}
	
	@Override
	public String fromGstnDetailedNegativeDetailsTest1() {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
	/*	APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("from_date", dto.getFrmDate());
		APIParam param3 = new APIParam("to_date", dto.getToDte());*/
		/*APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.NEGATIVE_LIABILITY, param1,
				param2, param3);*/
		try {
		String jsonPayload = "{\n" +
                "  \"openbal\": [\n" +
                "    {\n" +
                "      \"desc\": \"Other than reverse charge\",\n" +
                "      \"trancd\": 30002,\n" +
                "      \"igst\": 11,\n" +
                "      \"cgst\": 12,\n" +
                "      \"sgst\": 13,\n" +
                "      \"cess\": 14\n" +
                "    },\n" +
                "    {\n" +
                "      \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "      \"trancd\": 15,\n" +
                "      \"igst\": 16,\n" +
                "      \"cgst\": 17,\n" +
                "      \"sgst\": 18,\n" +
                "      \"cess\": 19\n" +
                "    }\n" +
                "  ],\n" +
                "  \"closebal\": [\n" +
                "    {\n" +
                "      \"desc\": \"Other than reverse charge\",\n" +
                "      \"trancd\": 20,\n" +
                "      \"igst\": 21,\n" +
                "      \"cgst\": 22,\n" +
                "      \"sgst\": 23,\n" +
                "      \"cess\": 24\n" +
                "    },\n" +
                "    {\n" +
                "      \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
                "      \"trancd\": 25,\n" +
                "      \"igst\": 26,\n" +
                "      \"cgst\": 27,\n" +
                "      \"sgst\": 28,\n" +
                "      \"cess\": 29\n" +
                "    }\n" +
                "  ],\n" +
                "  \"negliabdtls\": [\n" +
                "    {\n" +
                "      \"rtnprd\": \"092019\",\n" +
                "      \"refno\": \"ab125632512123c\",\n" +
                "      \"trantyp\": \"Debit\",\n" +
                "      \"trandate\": \"12/10/2019\",\n" +
                "      \"desc\": \"Negative liability for GSTR3B/3BQ\",\n" +
                "      \"negliab\": [\n" +
                "        {\n" +
                "          \"desc\": \"negliabdtls Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 31,\n" +
                "          \"cgst\": 32,\n" +
                "          \"sgst\": 33,\n" +
                "          \"cess\": 34\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"negliabdtls Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 35,\n" +
                "          \"igst\": 36,\n" +
                "          \"cgst\": 37,\n" +
                "          \"sgst\": 38,\n" +
                "          \"cess\": 39\n" +
                "        }\n" +
                "      ],\n" +
                "      \"negliabal\": [\n" +
                "        {\n" +
                "          \"desc\": \"negliabal Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 40,\n" +
                "          \"cgst\": 41,\n" +
                "          \"sgst\": 42,\n" +
                "          \"cess\": 43\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"negliabal Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 30003,\n" +
                "          \"igst\": 44,\n" +
                "          \"cgst\": 45,\n" +
                "          \"sgst\": 46,\n" +
                "          \"cess\": 47\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"rtnprd\": \"092019\",\n" +
                "      \"refno\": \"ab125632512123c\",\n" +
                "      \"trantyp\": \"Credit\",\n" +
                "      \"trandate\": \"12/10/2019\",\n" +
                "      \"desc\": \"Negative liability for GSTR3B/3BQ\",\n" +
                "      \"negliab\": [\n" +
                "        {\n" +
                "          \"desc\": \"negliab Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 48,\n" +
                "          \"cgst\": 49,\n" +
                "          \"sgst\": 50,\n" +
                "          \"cess\": 51\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"negliab Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 30003,\n" +
                "          \"igst\": 52,\n" +
                "          \"cgst\": 53,\n" +
                "          \"sgst\": 54,\n" +
                "          \"cess\": 55\n" +
                "        }\n" +
                "      ],\n" +
                "      \"negliabal\": [\n" +
                "        {\n" +
                "          \"desc\": \"negliabal Other than reverse charge\",\n" +
                "          \"trancd\": 30002,\n" +
                "          \"igst\": 56,\n" +
                "          \"cgst\": 57,\n" +
                "          \"sgst\": 58,\n" +
                "          \"cess\": 59\n" +
                "        },\n" +
                "        {\n" +
                "          \"desc\": \"negliabal Reverse charge and supplies made u/s 9(5)\",\n" +
                "          \"trancd\": 30003,\n" +
                "          \"igst\": 60,\n" +
                "          \"cgst\": 61,\n" +
                "          \"sgst\": 62,\n" +
                "          \"cess\": 63\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
		APIResponse resp = new APIResponse();
		resp.setResponse(jsonPayload);
		
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the rcm Details Screen Ledger API", ex);
			throw new AppException(ex);
		}
	}
	
	
	@Override
	public String fromRcmGstnTest(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
	
		try {
			String jsonString = "{ \"gstin\": \"24MAYAS0100E9Z9\", \"tr\": [ { \"trandt\": \"03-05-2023\", \"refno\": \"1324\", \"opnbal\": { \"igst\": 100, \"cgst\": 100, \"sgst\": 100, \"cess\": 100 } }, { \"trandt\": \"03-06-2023\", \"refno\": \"1424\", \"action\": \"A\", \"opnbal\": { \"igst\": 100, \"cgst\": 100, \"sgst\": 100, \"cess\": 100 } } ] }";
	        
			APIResponse resp = new APIResponse();
			resp.setResponse(jsonString);
			return resp.getResponse();
		
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the Rcm Ledger opening Balance API", ex);
			throw new AppException(ex);
		}
	}
	
	@Override
	public String fromNegativeGstnTest(GetCashLedgerDetailsReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
	
		try {
			String jsonString = "{ \"gstin\": \"24MAYAS0100E9Z9\", \"tr\": [ { \"trandt\": \"03-05-2023\", \"refno\": \"1324\", \"action\": \"S\", \"opnbal\": { \"igst\": 100, \"cgst\": 100, \"sgst\": 100, \"cess\": 100 } }, { \"trandt\": \"03-06-2023\", \"refno\": \"1424\", \"action\": \"A\", \"opnbal\": { \"igst\": 100, \"cgst\": 100, \"sgst\": 100, \"cess\": 100 } } ] }";
	         
			APIResponse resp = new APIResponse();
			resp.setResponse(jsonString);
			return resp.getResponse();
		
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the Rcm Ledger opening Balance API", ex);
			throw new AppException(ex);
		}
	}

}
