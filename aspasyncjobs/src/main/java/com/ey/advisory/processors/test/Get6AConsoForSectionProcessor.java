package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.app.services.jobs.erp.Get6AConsoForSectionHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Get6AConsoForSectionProcessor")
public class Get6AConsoForSectionProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get6AConsoForSectionProcessor.class);
	@Autowired
	@Qualifier("Get6AConsoForSectionHandler")
	private Get6AConsoForSectionHandler handler;

	/*
	 * When ever calling Get GSTR 2A Procedure getting Group Code,Params and scenario name as a input parameter.  
	 * 
	 */
	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String json = message.getParamsJson();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		try {
			Get2ARevIntReqDto dto = gson.fromJson(json,
					Get2ARevIntReqDto.class);
			//setting Group code into DTO
			dto.setGroupCode(groupCode);
			//setting scenario name into DTO
			Integer responseCode = handler.erpToGet6AConsoForSection(dto);
			
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Response Code:{}", responseCode);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException("Exception Occured:", e);
		}
	}
}
