/**
 * 
 */
package com.ey.advisory.ewb.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author KG712ZX
 *
 */
@Component("GetOtherPartyEWBImpl")
public class GetOtherPartyEWBImpl implements GetOtherPartyEWB {
	
	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public APIResponse getCounterPartyEwb(String gstin, LocalDate ewbGenDate) {
		
			String date = getStringDate(ewbGenDate);
			KeyValuePair<String, String> apiParam1 = new KeyValuePair<>(
					APIReqParamConstants.GSTIN, gstin);
			KeyValuePair<String, String> apiParam2 = new KeyValuePair<>(
					APIReqParamConstants.GET_OTHERPARTY_EWBS_DATE, 
					date);
			APIParams apiParams = new APIParams(APIProviderEnum.EWB,
					APIIdentifiers.GET_EWAYBILLS_FOR_OTHER_PARTY,"v1.03", apiParam1, apiParam2);
			return apiExecutor.execute(apiParams, null);
	}

	private String getStringDate(LocalDate date) {
		
		DateTimeFormatter formatter = 
				DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		return date.format(formatter);
	}

}
