/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.BCApiErpScenarioMasterRepository;
import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyInvocControlRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.ewb.data.entities.clientBusiness.BCApiErpScenarioMasterEntity;
import com.ey.advisory.ewb.reverseinteg.GetCPDao;
import com.ey.advisory.ewb.reverseinteg.ReverseIntegParamsDto;
import com.ey.advisory.gstnapi.repositories.client.EWBNICUserRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid.Khan
 *
 */
@Component("MonitorCounterPartyRevIntegProcessor")
@Slf4j
public class MonitorCounterPartyRevIntegProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	EWBNICUserRepository ewbNicUserRepo;

	@Autowired
	@Qualifier("GetCPDaoImpl")
	GetCPDao getCpdao;

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	CounterPartyInvocControlRepository counterPartyInvocControlRepo;

	@Autowired
	BCApiErpScenarioMasterRepository scenarioMasterRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			BCApiErpScenarioMasterEntity masterEntity = scenarioMasterRepo
					.findByScenarioName("EwbCounterPartyRevIntg");

			if (masterEntity == null) {
				LOGGER.error(
						"No scenerio found , Hence skipping the ewb counter "
								+ "party reverse integration for group :"
								+ " {}",
						TenantContext.getTenantId());
				return;

			}

			List<ReverseIntegParamsDto> reqList = getCpdao
					.getDistinctGstinDate();
			reqList.forEach(
					o -> createAsyncJob(o, group, masterEntity.getId()));
		} catch (Exception e) {
			String msg = "Exception occured while posting ewb reverse integration job";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	private void createAsyncJob(ReverseIntegParamsDto o, Group group,
			Long scenerioId) {

		Gson gson = new Gson();
		o.setScenarioId(scenerioId);
		String jsonParams = gson.toJson(o);
		
		counterPartyInvocControlRepo.updateRevIntgStatus(o.getGstin(), o.getLocalDate(), "POSTED");
		//update control table revintstaus = "posted" for gstin and date
		asyncJobsService.createJob(group.getGroupCode(),
				"GetCounterPartyRevInteg", jsonParams, "SYSTEM", 1L, null,
				null);
	}

}
