/**
 * 
 */
package com.ey.advisory.app.data.services.drc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.drc.TblDrcDetails;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc.TblDrcSaveStatusRepo;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("DrcPartBSaveToGSTNImpl")
public class DrcPartBSaveToGSTNImpl implements DrcPartBSaveToGSTN {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("TblDrcSaveStatusRepo")
	private TblDrcSaveStatusRepo satusRepository;

	@Autowired
	@Qualifier("DrcPartBSaveToGSTNService")
	private DrcPartBSaveToGSTNService service;

	@Autowired
	@Qualifier("TblDrcDetailsRepo")
	private TblDrcDetailsRepo drcDetailsRepo;

	@Override
	public JsonArray saveEntityLevelData(List<String> gstinsList,
			String taxPeriod) {

		String groupCode = TenantContext.getTenantId();

		String msg = null;
		JsonArray respBody = new JsonArray();

		try {

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

						TblDrcDetails drcDetialsEntity = drcDetailsRepo
								.findByGstinAndTaxPeriodAndIsActiveTrue(gstin,
										taxPeriod);

						String refId = drcDetialsEntity!= null 
								? drcDetialsEntity.getRefId() : null;

						if (refId != null) {

							msg = service.saveDRCToGstn(gstin, taxPeriod,
									groupCode);
						} else {
							msg = "No Data Available to Save";
						}
					}
				} else {
					msg = "Auth Token is Inactive, Please Activate.";

				}
				json.addProperty("gstin", gstin);
				json.addProperty("msg", msg);
				respBody.add(json);
			}

		} catch (Exception e) {
			msg = "Unexpected error occured in DrcPartBSaveToGSTNImpl";
			LOGGER.error(msg, e);
			throw new AppException(e);
		}

		return respBody;

	}
}