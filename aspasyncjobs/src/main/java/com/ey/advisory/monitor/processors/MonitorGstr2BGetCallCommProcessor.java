package com.ey.advisory.monitor.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.gstr2b.Gstr2BGetReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("MonitorGstr2BGetCallCommProcessor")
public class MonitorGstr2BGetCallCommProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			List<Long> entityIds = entityInfoRepo.findActiveEntityIds();
			List<Long> optedEntities = entityConfigPemtRepo
					.getAllEntitiesOpted2B(entityIds, "I28");

			if (optedEntities.isEmpty()) {
				LOGGER.info("There are no entities opted for"
						+ "Auto 2B for group {}", group.getGroupCode());
				return;
			}
			LOGGER.debug("Entities opted for gstr2b are {}", optedEntities);

			List<String> optedGstins = gSTNDetailRepo
					.findByEntityId(optedEntities);

			LOGGER.debug("Gstins opted for gstr2b are {}", optedGstins);

			if (optedGstins.isEmpty()) {
				LOGGER.info("There are no  gstins opted for"
						+ "auto 2B for group {}", group.getGroupCode());
				return;
			}

			String taxPeriod = GenUtil.getCurrentTaxPeriod();

			List<String> inActiveGstins = new ArrayList<>();
			List<String> activeGstins = new ArrayList<>();
			evalStatusAndPostGet2BJob(group, optedGstins, inActiveGstins,
					activeGstins, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("RecipientGstins opted for communication which"
						+ "are Inactive{}", inActiveGstins);
			}
			postAsyncJobForActiveAndInactiveGstins(group, inActiveGstins,
					activeGstins, taxPeriod);
		} catch (Exception ee) {
			LOGGER.error("Exception occured while monitoring 2B Get"
					+ " Call and Communication", ee);
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ee,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private void evalStatusAndPostGet2BJob(Group group,
			List<String> recipientGstins, List<String> inActiveGstins,
			List<String> activeGstins, String taxPeriod) {

		Map<String, String> authTokenStatuses = authTokenService
				.getAuthTokenStatusForGstins(recipientGstins);

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<AsyncExecJob> eligibleJobs = new ArrayList<>();
		authTokenStatuses.forEach((k, v) -> {
			if (v.equalsIgnoreCase("A")) {
				JsonObject jsonObj = new JsonObject();
				Gstr2BGetReqDto dto = new Gstr2BGetReqDto();
				dto.setGstins(Arrays.asList(k));
				dto.setTaxPeriod(Arrays.asList(taxPeriod));
				dto.setAutoEligible(true);
				jsonObj.add("req", gson.toJsonTree(dto));
				AsyncExecJob job = asyncJobsService.createJobAndReturn(
						group.getGroupCode(), JobConstants.GSTR2B_GET,
						jsonObj.toString(), "SYSTEM", 1L, null, null);
				eligibleJobs.add(job);
				activeGstins.add(k);
			} else {
				inActiveGstins.add(k);
			}
		});
		if (!eligibleJobs.isEmpty())
			asyncJobsService.createJobs(eligibleJobs);
	}

	private void postAsyncJobForActiveAndInactiveGstins(Group group,
			List<String> inActiveGstins, List<String> activeGstins,
			String taxPeriod) {
		JsonObject obj = new JsonObject();
		obj.addProperty("iGstins", inActiveGstins.toString());
		obj.addProperty("aGstins", activeGstins.toString());
		obj.addProperty("taxPeriod", taxPeriod);
		asyncJobsService.createJob(group.getGroupCode(),
				JobConstants.GSTR2B_INITIATE_STATUS, obj.toString(), "SYSTEM",
				1L, null, null);
	}
}
