
package com.ey.advisory.monitor.processors;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Itc04StockTrackCompRepository;
import com.ey.advisory.app.data.services.itc04stocktrack.Itc04StockTrackService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Itc04StockTrackingInitiateReportProcessor")
public class Itc04StockTrackingInitiateReportProcessor
		extends DefaultMultiTenantTaskProcessor {


	@Autowired
	@Qualifier("Itc04StockTrackCompRepository")
	Itc04StockTrackCompRepository itc04StockTrackCompRepository;

	@Autowired
	Itc04StockTrackService itc04stockTracking;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring and Posting Return Status Entries"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonArray jsonArray = json.getAsJsonArray("ids");

			List<Long> idList = StreamSupport
					.stream(jsonArray.spliterator(), false)
					.map(JsonElement::getAsLong).collect(Collectors.toList());

			itc04StockTrackCompRepository.updateReportStatus(idList,
					APIConstants.INPROGRESS);

			itc04stockTracking.computeInitiateReport(idList);

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			LOGGER.error(
					"Exception while Triggering the Compute for ITC04 Stock Tracking",
					ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

}
