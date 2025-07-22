package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Get2BRevIntReqDto;
import com.ey.advisory.app.services.jobs.erp.Get2BConsolidatedHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;

/**
 * 
 * @author Sruthi
 *
 */

@Service("Get2BConsolidateRevIntgProcessor")
public class Get2BConsolidateRevIntgProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2BConsolidateRevIntgProcessor.class);

	@Autowired
	@Qualifier("Get2BConsolidatedHandler")
	private Get2BConsolidatedHandler handler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String json = message.getParamsJson();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Get2BRevIntReqDto dto=null;
		try {

			 dto = gson.fromJson(json,
					Get2BRevIntReqDto.class);
dto.setJobId(message.getId());
			dto.setGroupCode(groupCode);
			handler.Get2BConsolidateRptToErp(dto);

		} catch (Exception e) {
			Long requestId = (dto != null && dto.getRequestId() != null) ? dto.getRequestId() : 0L;
			String errMsg = String.format(
					"Gstr2B Rev Integ Failed for batch id %s for group code %s",
					requestId, TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}

	}

	public static void main(String[] args) {
		System.out.println("Ram");
	}
}
