package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.gstr7.Gstr7TransSRFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr7TransFileArrivalProcessor")
@Slf4j
public class Gstr7TransFileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr7TransSRFileArrivalHandler")
	private Gstr7TransSRFileArrivalHandler srFileArrivalHandler;


	public void execute(Message message, AppExecContext context) {
		try {
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"COMPRENSIVE_SR_FILE_ARRIVAL_PROCESSOR_START",
					"ComprehensiveEinvoiceFileArrivalProcessor", "execute",
					null);
			List<AsyncExecJob> jobList = new ArrayList<>();
			srFileArrivalHandler.processEInvoiceSRFile(message, context,
					jobList);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"COMPRENSIVE_SR_FILE_ARRIVAL_PROCESSOR_END",
					"ComprehensiveEinvoiceFileArrivalProcessor", "execute",
					null);
		} catch (AppException e) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
}
