package com.ey.advisory.gstr9.jsontocsv.processing.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.DownloadGetCallReports;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Component("DownloadGetCallReportsProcessor")
@Slf4j
public class DownloadGetCallReportsProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("DownloadGetCallReportsImpl")
	DownloadGetCallReports downloadGetCallReports;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			String jsonString = message.getParamsJson();
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long id = json.get("id").getAsLong();

			downloadGetCallReports.generateReport(id);

		} catch (Exception e) {
			String errMsg = "Error Occured in DownloadGetCallReportsProcessor";
			LOGGER.error(errMsg, e);
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(errMsg, e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}

	}

}
