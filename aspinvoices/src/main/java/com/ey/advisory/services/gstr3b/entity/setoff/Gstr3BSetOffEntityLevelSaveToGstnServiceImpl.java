package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3BSaveLiabilitySetOffServiceImpl;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author vishal.verma
 *
 */
@Component("Gstr3BSetOffEntityLevelSaveToGstnServiceImpl")
public class Gstr3BSetOffEntityLevelSaveToGstnServiceImpl
		implements Gstr3BSetOffEntityLevelSaveToGstnService {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository satusRepository;

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffServiceImpl")
	private Gstr3BSaveLiabilitySetOffServiceImpl service;

	@Override
	public JsonArray saveEntityLevelData(List<String> gstinsList,
			String taxPeriod) {

		String groupCode = TenantContext.getTenantId();
		
		String msg = null;
		JsonArray respBody = new JsonArray();

		for (String gstin : gstinsList) {

			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			JsonObject json = new JsonObject();
			if (authStatus.equals("A")) {
				Long saveInprogressCnt = satusRepository
						.findByGstinAndTaxPeriodAndStatus(gstin, taxPeriod,
								APIConstants.SAVE_INITIATED);
				
				if (saveInprogressCnt > 0) {
					msg = "Save is already Inprogress";
					
				} else {
					msg = service.saveLiablityToGstn(gstin, taxPeriod,
							groupCode);
					
				}
			} else {
				msg = "Auth Token is Inactive, Please Activate : "+ gstin;
				
			}
			json.addProperty("gstin", gstin);
			json.addProperty("msg", msg);
			respBody.add(json);
		}
		return respBody;

	}
}
