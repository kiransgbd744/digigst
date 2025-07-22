package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.ims.handlers.ImsRevIntgReqDto;
import com.ey.advisory.app.ims.handlers.ImsRevIntgService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
@Component("ImsRevIntgProcessor")
public class ImsRevIntgProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("ImsRevIntgServiceImpl")
	private ImsRevIntgService service;
	
	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin ImsRevIntgProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"ImsRevIntgProcessor Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			ImsRevIntgReqDto dto = gson.fromJson(jsonString,
					ImsRevIntgReqDto.class);
			dto.setGroupCode(groupCode);
			LOGGER.debug(
					"ImsRevIntgProcessor status is in progress ");
			
			Integer respcode = service.getImsErpPush(dto);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" Ims  ERP Push response code is {}",
						respcode);
			}
		} catch (Exception e) {
			String msg = "ImsRevIntgProcessor got interrupted. ";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

}
