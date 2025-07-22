package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardRefreshRepository;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOProcCallService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("LandingDashboardProcessor")
public class LandingDashboardProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	@Qualifier("DashboardHOProcCallServiceImpl")
	private DashboardHOProcCallService procCallService;

	@Autowired
	LandingDashboardRefreshRepository ldRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void execute(Message message, AppExecContext context) {
		Long batchId = null;
		Long entityId = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Executing " + " LandingDashboardProcessor"
								+ ".executeForGroup()");
				LOGGER.debug(logMsg);
			}
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			batchId = json.get("batchId") != null
					? json.get("batchId").getAsLong() : null;
			String derivedTaxPeriod = json.get("derivedTaxPeriod")
					.getAsString();
			entityId = json.get("entityId").getAsLong();

			ldRepo.updateBatchStatus(batchId, "InProgress",
					LocalDateTime.now());

			procCallService.dashboardProcCall(derivedTaxPeriod, batchId,
					entityId);

		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing LandingDashboardProcessor to call "
							+ " proc for batchId :%s group code :%s and entityId :%s",
					batchId, message.getGroupCode(), entityId);
			ldRepo.updateBatchStatus(batchId, "FAILED", LocalDateTime.now());
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		}
	}

}
