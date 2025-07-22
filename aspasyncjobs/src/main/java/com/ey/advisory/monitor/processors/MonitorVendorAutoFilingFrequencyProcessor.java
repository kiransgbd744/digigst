package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MonitorVendorAutoFilingFrequencyProcessor")
public class MonitorVendorAutoFilingFrequencyProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final String FINANCIAL_YEAR = "financialYear";

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Group Code:{}", groupCode);
		}
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		try {
			List<Long> entityIds = entityInfoRepository
					.findEntityIdsByGroupCode(groupCode);
			List<Long> optedEntities = entityConfigPemtRepo
					.getAllEntitiesOpted2B(entityIds, "G26");
			for (Long entityId : optedEntities) {
				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("entityId", entityId);
				String fy = GenUtil.getFinYear(LocalDate.now());
				String financialYear = fy.substring(0, 4) + "-" + fy.substring(4);
				jsonParams.addProperty(FINANCIAL_YEAR, financialYear);
				jsonParams.addProperty("complianceType", "Vendor");
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.Get_RETURN_FILING_FREQUENCY,
						jsonParams.toString(), userName, 5L, 0L, 0L);
			}

		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor Filing Frequency:",
					e);
		}
	}
}
