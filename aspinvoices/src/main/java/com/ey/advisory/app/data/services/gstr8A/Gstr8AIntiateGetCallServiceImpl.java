/**
 * 
 */
package com.ey.advisory.app.data.services.gstr8A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ASummaryDetailsRepository;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Component("Gstr8AIntiateGetCallServiceImpl")
@Slf4j
public class Gstr8AIntiateGetCallServiceImpl {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	GstnCommonServiceUtil gstnCommonUtil;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	Gstr8ASummaryDetailsRepository gstr8ASummaryDetailsRepo;

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	public void getGstnCall(String gstin, String fy, String retPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside getGstnCall for Gstin {} and Fy {} ", gstin,
					fy);
		}


		List<GstnGetStatusEntity> list = gstinGetStatusRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, retPeriod,
						APIConstants.GSTR8A);

		if (list.isEmpty()) {
			GstnGetStatusEntity saveEntity = createGetStatusEntity(gstin,
					retPeriod,
					APIConstants.INITIATED, "GET8A", null);

			gstinGetStatusRepo.save(saveEntity);
		} else {
			gstinGetStatusRepo.updateStatus(gstin, retPeriod,
					APIConstants.GSTR8A, APIConstants.INITIATED,
					LocalDateTime.now(), null);
		}

		String groupCode = TenantContext.getTenantId();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		JsonObject jobParams = new JsonObject();
		jobParams.addProperty("gstin", gstin);
		jobParams.addProperty("fy", fy);
		jobParams.addProperty("retPeriod", retPeriod);
		asyncJobsService.createJob(groupCode, JobConstants.GSTR8A_GET_CALL,
				jobParams.toString(), userName, 1L, null, null);
	}

	private GstnGetStatusEntity createGetStatusEntity(String gstin,
			String taxPeriod, String status, String section, String errDesc) {

		GstnGetStatusEntity gstnGetStatusEntity = new GstnGetStatusEntity();

		Integer derivedTaxPeriod = Integer.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

		gstnGetStatusEntity.setGstin(gstin);
		gstnGetStatusEntity.setTaxPeriod(taxPeriod);
		gstnGetStatusEntity.setReturnType("GSTR8A");
		gstnGetStatusEntity.setSection(section);
		gstnGetStatusEntity.setCreatedOn(LocalDateTime.now());
		gstnGetStatusEntity.setDerivedTaxPeriod(derivedTaxPeriod);
		gstnGetStatusEntity.setStatus(status);
		gstnGetStatusEntity.setUpdatedOn(LocalDateTime.now());
		return gstnGetStatusEntity;

	}

}
