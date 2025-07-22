/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.Gstr1EinvSeriesCompEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1EinvSeriesCompRepo;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("MonitorInvoiceSeriesProcessor")
public class MonitorInvoiceSeriesProcessor
		extends DefaultMultiTenantTaskProcessor {

	private static final List<String> STATUS = ImmutableList
			.of(APIConstants.INPROGRESS, "JOB_POSTED");

	@Autowired
	private Gstr1EinvSeriesCompRepo gstr1EinvSeriesCompRepo;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	
	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorInvoiceSeriesProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			List<Gstr1EinvSeriesCompEntity> inProgressEntries = gstr1EinvSeriesCompRepo
					.findByRequestStatusInAndIsActiveTrue(STATUS);

			if (!inProgressEntries.isEmpty()) {
				LOGGER.error(
						"InvoiceSeries compute is in InProgress, Hence skipping the job. Gstin {} TaxPeriod {} ",
						inProgressEntries.get(0).getGstin(),
						inProgressEntries.get(0).getReturnPeriod());
				return;
			}

			Gstr1EinvSeriesCompEntity einvEntry = gstr1EinvSeriesCompRepo
					.findTop1ByRequestStatusInAndIsActiveTrueOrderByIdAsc(
							Arrays.asList(APIConstants.INITIATED));

			if (einvEntry != null) {
				String optedImpl = invSeriesOptedImpl(
						Long.valueOf(einvEntry.getEntityId()));
				
				JsonObject jobParams = new JsonObject();
				String gstin = einvEntry.getGstin();
				String returnPeriod = einvEntry.getReturnPeriod();
				Long configId = einvEntry.getId();
				jobParams.addProperty("gstin", gstin);
				jobParams.addProperty("retPeriod", returnPeriod);
				jobParams.addProperty("configId", configId);
				jobParams.addProperty("implType", optedImpl);
				gstr1EinvSeriesCompRepo.updateRequestStatus(configId,
						"JOB_POSTED", null, null);
				User user = SecurityContext.getUser();
				String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GSTR1_EINV_SERIES_COMPUTE, jobParams.toString(),
						userName, 1L, null, null);
			} else {
				String logMsg = String.format(
						"No Entries available for InvoiceSeries compute " + " for group '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for Invoice Series compute group '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

		} catch (Exception ex) {
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
	
	private String invSeriesOptedImpl(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"Invoice Series to be computed by DigiGST , basis below options");
		return optAns;
	}
}
