package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.SRFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
@Component("SRFileArrivalProcessor")
public class SRFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SRFileArrivalProcessor.class);

	@Autowired
	@Qualifier("SRFileArrivalHandler")
	private SRFileArrivalHandler srFileArrivalHandler;

	public void execute(Message message, AppExecContext context) {
		try {
			LOGGER.debug("SR File arrived");
			GstnApi gstnApi = StaticContextHolder.getBean("GstnApi",
					GstnApi.class);
			CommonContext.setDelinkingFlagContext(gstnApi
					.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()));
			srFileArrivalHandler.processSRFile(message, context);
			LOGGER.debug("SR File Arrival Processed");
		} catch (AppException e) {
			LOGGER.error("Exception while processing the SR file ",
					e);
			throw new AppException();
		}finally {
			CommonContext.clearContext();
		}
	}
}
