package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.erp.Get2AConsolidatedRevIntgDto;
import com.ey.advisory.app.services.jobs.erp.Get2ARevIntgHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.gson.Gson;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("Get2AConsolidatedRevIntgProcessor")
public class Get2AConsolidatedRevIntgProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2AConsolidatedRevIntgProcessor.class);

	@Autowired
	@Qualifier("Get2ARevIntgHandler")
	private Get2ARevIntgHandler get2ARevIntgHandler;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Get2AConsolidatedRevIntgDto dto = null;
		try {
			dto = gson.fromJson(json, Get2AConsolidatedRevIntgDto.class);
			String destinationName = dto.getDestinationName();
			Long entityId = dto.getEntityId();
			String gstin = dto.getGstin();
			dto.setJobId(message.getId());
			if (groupcode != null && destinationName != null && entityId != null
					&& gstin != null) {

				List<String> initAndInProgressStatus = new ArrayList<>();
				initAndInProgressStatus
						.add(APIConstants.INITIATED.toUpperCase());
				initAndInProgressStatus
						.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
				List<String> initAndInprogressBatchs = batchRepo
						.findBatchByStatus(dto.getGstin(),
								APIConstants.GSTR2A_ERP.toUpperCase(),
								initAndInProgressStatus);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GSTR2A All GET Not eligible sections are {} ",
							initAndInprogressBatchs);
				}

				Integer respcode = get2ARevIntgHandler.get2AToErpPush(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get2A response code is {}", respcode);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Partial Request Params groupcode, "
									+ "destinationName, entityId and gstin are mandatory",
							dto);
				}
			}
		} catch (Exception e) {
			Long batchId = (dto != null && dto.getBatchId() != null) ? dto.getBatchId() : 0L;
			String errMsg = String.format(
					"Nalco Gstr2A Rev Integ Failed for batch id %s for group code %s",
					 batchId, TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
	}

}
