package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.api.GetEWBDetailsByDate;
import com.ey.advisory.gstnapi.repositories.client.EWBNICUserRepository;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy This class is responsible for Post Group Level Eway Bill
 *         for 3 Way Recon Get Eway Bill.
 */
@Slf4j
@Component("MonitorAndPostGrpGetEwbJobsProcessor")
public class MonitorAndPostGrpGetEwbJobsProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	EWBNICUserRepository ewbNICUserRepo;

	@Autowired
	@Qualifier("GetEWBDetailsByDateImpl")
	private GetEWBDetailsByDate getEWBDetailsByDate;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			Map<String, Config> configMap = configManager.getConfigs("GETEWB",
					"eligible.getewb", "DEFAULT");

			Map<String, Config> callDateConfigMap = configManager.getConfigs(
					"GETEWB", "getewb.config", TenantContext.getTenantId());

			String getewbDate = callDateConfigMap != null
					&& callDateConfigMap.get("getewb.config.ewbdate") != null
							? callDateConfigMap.get("getewb.config.ewbdate")
									.getValue()
							: String.valueOf(1);
			String groupCode = group.getGroupCode();

			String eligibleGrps = configMap != null
					&& configMap.get("eligible.getewb.groups") != null
							? configMap.get("eligible.getewb.groups").getValue()
							: null;

			if (eligibleGrps == null || eligibleGrps.trim().isEmpty()) {
				String logMsg = String
						.format("There is no Eligible Grps Config in the Master to do a Get Ewb Call,"
								+ " Hence returning the Job");
				LOGGER.error(logMsg);
				return;
			}

			List<String> grpDtls = Arrays.asList(eligibleGrps.split(","));

			if (!grpDtls.contains(groupCode)) {
				String logMsg = String.format(
						"Group Code %s is not a Part of Get Eway Bill Eligible List, Hence returning the Job",
						groupCode);
				LOGGER.error(logMsg);
				return;
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Executing MonitorGetEwayBillProcessor" + " job"
								+ ".executeForGroup()"
								+ " method for group: '%s'", groupCode);
				LOGGER.debug(logMsg);
			}

			LocalDateTime convGenDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());

			LocalDate ewbGenDate = convGenDate.toLocalDate()
					.minusDays(Long.valueOf(getewbDate));

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Excecuting the Get Eway Date for Date '%s'",
						ewbGenDate);
				LOGGER.debug(logMsg);
			}

			List<String> distGstin = ewbNICUserRepo.getDistinctGstins();

			// If Gstin available create a child job. Input is Date.
			if (distGstin.isEmpty()) {
				String logMsg = String
						.format("No EWB GSTIN's available to Post hence skipping "
								+ "Get Eway Bill's for  group '%s'", groupCode);
				LOGGER.debug(logMsg);
				return;
			}

			JsonObject jobParamsObj = new JsonObject();

			jobParamsObj.addProperty("ewbCallDate",
					ewbGenDate.format(FORMATTER));

			asyncJobsService.createJob(groupCode, JobConstants.POST_EWBJOBS,
					jobParamsObj.toString(), "SYSTEM", 1L, null, null);

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

}
