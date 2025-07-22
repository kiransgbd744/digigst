package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.glrecon.dump.GlReconDumpFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

@Component("GLReconDumpFileArrivalProcessor")
public class GLReconDumpFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GLReconDumpFileArrivalProcessor.class);

	@Autowired
	@Qualifier("GlReconDumpFileArrivalHandler")
	private GlReconDumpFileArrivalHandler glReconDumpFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Gl Recon Dump file Arrived");
		glReconDumpFileArrivalHandler.processProductFile(message, context);

	}

}
