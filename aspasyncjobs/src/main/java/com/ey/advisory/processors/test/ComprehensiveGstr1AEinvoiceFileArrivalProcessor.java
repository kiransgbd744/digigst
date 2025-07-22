package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.doc.gstr1a.Gstr1AComprehensiveSRFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("ComprehensiveGstr1AEinvoiceFileArrivalProcessor")
@Slf4j
public class ComprehensiveGstr1AEinvoiceFileArrivalProcessor
		implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1AComprehensiveSRFileArrivalHandler")
	private Gstr1AComprehensiveSRFileArrivalHandler srFileArrivalHandler;

	@Autowired
	private AsyncJobsService persistenceMngr;

	public void execute(Message message, AppExecContext context) {
		try {
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"COMPRENSIVE_SR_FILE_ARRIVAL_PROCESSOR_START",
					"ComprehensiveEinvoiceFileArrivalProcessor", "execute",
					null);
			List<AsyncExecJob> jobList = new ArrayList<>();
			GstnApi gstnApi = StaticContextHolder.getBean("GstnApi",
					GstnApi.class);
			CommonContext.setDelinkingFlagContext(gstnApi
					.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()));
			srFileArrivalHandler.processEInvoiceSRFile(message, context,
					jobList);
			if (!jobList.isEmpty())
				persistenceMngr.createJobs(jobList);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"COMPRENSIVE_SR_FILE_ARRIVAL_PROCESSOR_END",
					"ComprehensiveEinvoiceFileArrivalProcessor", "execute",
					null);
		} catch (AppException e) {
			LOGGER.error("Exception while processing the SR file ", e);
			throw e;
		} finally {
			CommonContext.clearContext();
		}
	}
}
