package com.ey.advisory.monitor.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.services.drc01c.Drc01cGstnService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Vishal.verma This class is responsible for Posting DRC01C Get Calls.
 */
@Slf4j
@Component("MonitorAndPostDrc01CGetJobProcessor")
public class MonitorAndPostDrc01CGetJobProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("Drc01cGstnServiceImpl")
	private Drc01cGstnService drcGstnService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			Map<String, Config> configMap = configManager.getConfigs("DRC",
					"eligible.getdrc", "DEFAULT");

			String groupCode = group.getGroupCode();

			String eligibleGrps = configMap != null
					&& configMap.get("eligible.getdrc.groups") != null
							? configMap.get("eligible.getdrc.groups").getValue()
							: null;

			if (eligibleGrps == null || eligibleGrps.trim().isEmpty()) {
				String logMsg = String
						.format("There is no Eligible Grps Config in the Master"
								+ " to do a Get DRC01C Job Call,"
								+ " Hence returning the Job");
				LOGGER.error(logMsg);
				return;
			}

			List<String> grpDtls = Arrays.asList(eligibleGrps.split(","));

			if (!grpDtls.contains(groupCode)) {
				String logMsg = String.format(
						"Group Code %s is not a Part of Get DRC01C"
						+ " Eligible List, Hence returning the Job",
						groupCode);
				LOGGER.error(logMsg);
				return;
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Executing MonitorAndPostDrc01CGetJobProcessor"
								+ " job" + ".executeForGroup()"
								+ " method for group: '%s'", groupCode);
				LOGGER.debug(logMsg);
			}

			List<String> distGstin = gstinDetailRepo.getGstr1Gstr3bActiveGstns();

			if (distGstin.isEmpty()) {
				String logMsg = String
						.format("No DRC GSTIN's available to Post hence skipping "
								+ " for  group '%s'", groupCode);
				LOGGER.debug(logMsg);
				return;
			}
			Pair<String, String> taxPeriods = GenUtil
					.getCurrentAndPrevTaxPeriod();
			drcGstnService.getDrcRetComList(distGstin, taxPeriods.getValue1(),
					true,null);

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
