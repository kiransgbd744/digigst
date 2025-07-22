/**
 * 
 */
package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.services.jobs.erp.Anx1ApprovalReqRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1ApprovalReqRevIntegrationProcessor")
public class Anx1ApprovalReqRevIntegrationProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApprovalReqRevIntegrationProcessor.class);

	@Autowired
	private Anx1ApprovalReqRevIntegrationHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		LOGGER.debug(
				"Anx1 Approval Req DocsRevIntegration Data Execute method is "
						+ "ON with groupcode {} and params {}",
				groupcode, json);
		Gson gson = GsonUtil.newSAPGsonInstance();

		ApprovalStatusReqDto dto = gson.fromJson(json,
				ApprovalStatusReqDto.class);
		String destinationName = dto.getDestinationName();
		String returnPeriod = dto.getReturnPeriod();
		String gstin = dto.getGstin();
		Long entityId = dto.getEntityId();
		if (groupcode != null && destinationName != null && returnPeriod != null
				&& gstin != null && entityId != null) {

			Integer respcode = handler.approvalRequestToErp(dto);

			LOGGER.debug("Response code is {}", respcode);
			LOGGER.info("Anx1ErrorDocsRevIntegration Processed with args {} ",
					json);
		} else {
			LOGGER.debug(
					"Partial Request Params groupcode, entityId, "
							+ "destinationName, returnPeriod and gstin are mandatory",
					dto);
		}

	}

}