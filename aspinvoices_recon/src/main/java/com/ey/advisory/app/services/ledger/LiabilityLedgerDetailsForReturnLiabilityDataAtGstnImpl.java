package com.ey.advisory.app.services.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("liabilityLedgerDetailsForReturnLiabilityDataAtGstnImpl")
public class LiabilityLedgerDetailsForReturnLiabilityDataAtGstnImpl
		implements LiabilityLedgerDetailsForReturnLiabilityDataAtGstn {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LiabilityLedgerDetailsForReturnLiabilityDataAtGstnImpl.class);

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;
	
	@Override
	public String fromGstn(GetCashITCBalanceReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getRetPeriod());
		APIParam param3 = new APIParam("fr_dt", dto.getRetPeriod());
		APIParam param4 = new APIParam("to_dt", dto.getRetPeriod());

		APIParams params = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, APIIdentifiers.LEDGER_GET_TAX, param1,
				param2, param3, param4);
		try {
			APIResponse resp = apiExecutor.execute(params, null);

			// If the GSTN API returns a failure code, then throw an exception
			if (!resp.isSuccess()) {
				String msg = String.format(
						"Failed to do Get Liability Details from Gstn, Error Response is '%s'",
						resp.getError());
				LOGGER.error(msg);
				//throw new AppException(resp.getError().toString());
				return null;
			}
			return resp.getResponse();
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking the Liability Ledger API",
					ex);
			throw new AppException(ex.getMessage());
		}
	}


	@Override
	public String fromGstnTestLiab(GetCashITCBalanceReqDto dto) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		/*APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getRetPeriod());
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.LEDGER_GET_TAX, param1, param2);
		APIResponse resp = apiExecutor.execute(params, null);*/
		String jsonResp = "{\"gstin\":\"06AAAAA0000A1Z6\",\"ret_period\":\"032017\",\"tr\":"
                + "[{\"dt\":\"09/03/2017\",\"ref_no\":\"AM051216747183N\",\"tot_rng_bal\":100000,\"tot_tr_amt\":100001,"
                + "\"tr_typ\":\"Dr\",\"desc\":\"Other than reverse charge\",\"dschrg_typ\":\"\",\"igst\":"
                + "{\"intr\":100002,\"oth\":100003,\"tx\":100004,\"fee\":100005,\"pen\":100006,\"tot\":100007},\"sgst\":"
                + "{\"intr\":100008,\"oth\":100009,\"tx\":100010,\"fee\":100011,\"pen\":100012,\"tot\":100013},\"cgst\":"
                + "{\"intr\":100014,\"oth\":100015,\"tx\":100016,\"fee\":100017,\"pen\":100018,\"tot\":100019},\"cess\":"
                + "{\"intr\":100020,\"oth\":100021,\"tx\":100022,\"fee\":100023,\"pen\":100024,\"tot\":100025},\"igstbal\":"
                + "{\"intr\":100026,\"oth\":100027,\"tx\":100028,\"fee\":100029,\"pen\":100030,\"tot\":100031},\"sgstbal\":"
                + "{\"intr\":100032,\"oth\":100033,\"tx\":100034,\"fee\":100035,\"pen\":100036,\"tot\":100037},\"cgstbal\":"
                + "{\"intr\":100038,\"oth\":100039,\"tx\":100040,\"fee\":100041,\"pen\":100042,\"tot\":100043},\"cessbal\":"
                + "{\"intr\":100044,\"oth\":100045,\"tx\":100046,\"fee\":100047,\"pen\":100048,\"tot\":100049}},"
                + "{\"dt\":\"10/03/2017\",\"ref_no\":\"DI0506170000004\",\"tot_rng_bal\":100050,\"tot_tr_amt\":100051,"
                + "\"tr_typ\":\"Cr\",\"desc\":\"Other than reverse charge\",\"dschrg_typ\":\"credit\",\"igst\":"
                + "{\"intr\":100052,\"oth\":100053,\"tx\":100054,\"fee\":100055,\"pen\":100056,\"tot\":100057},\"sgst\":{\"intr\":100058,\"oth\":100059,\"tx\""
                + ":100060,\"fee\":100061,\"pen\":100062,\"tot\":100063},\"cgst\":{\"intr\":100064,\"oth\":100065,\"tx\":100066,\"fee\":100067,\"pen\":100068,"
                + "\"tot\":100069},\"cess\":{\"intr\":100070,\"oth\":100071,\"tx\":100072,\"fee\":100073,\"pen\":100074,\"tot\":100075},\"igstbal\":"
                + "{\"intr\":100076,\"oth\":100077,\"tx\":100078,\"fee\":100079,\"pen\":100080,\"tot\":100081},\"sgstbal\":"
                + "{\"intr\":100082,\"oth\":100083,\"tx\":100084,\"fee\":100085,\"pen\":100086,\"tot\":100087},\"cgstbal\":"
                + "{\"intr\":100088,\"oth\":100089,\"tx\":100090,\"fee\":100091,\"pen\":100092,\"tot\":100093},\"cessbal\":"
                + "{\"intr\":100094,\"oth\":100095,\"tx\":100096,\"fee\":100097,\"pen\":100098,\"tot\":100099}}],\"cl_bal\":"
                + "{\"tot_rng_bal\":100100,\"desc\":\"Closing Balance\",\"igstbal\":"
                + "{\"intr\":100101,\"oth\":100102,\"tx\":100103,\"fee\":100104,\"pen\":100105,\"tot\":100106},\"sgstbal\":"
                + "{\"intr\":100107,\"oth\":100108,\"tx\":100109,\"fee\":100110,\"pen\":100111,\"tot\":100112},\"cgstbal\":"
                + "{\"intr\":100113,\"oth\":100114,\"tx\":100115,\"fee\":100116,\"pen\":100117,\"tot\":100118},\"cessbal\":"
                + "{\"intr\":100119,\"oth\":100120,\"tx\":100121,\"fee\":100122,\"pen\":100123,\"tot\":100124}}}";

		
		APIResponse resp = new APIResponse();
		resp.setResponse(jsonResp);
		
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		/*if (!resp.isSuccess()) {
			String msg = "Failed to do Get Cash ITC Balance from Gstn";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(msg);
			}
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}*/
		// Build the Json Object.
		return resp.getResponse();
	}

}
