
package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("PostReturnStatusEntryProcessor")
public class PostReturnStatusEntryProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstinDetailRepository;

	@Autowired
	private Environment env;

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

			createReturnFillingEntries(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Posting"
								+ " job for PostReturnFillingStatus group '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			LOGGER.error("Exception while Posting Return Filling entires", ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	private void createReturnFillingEntries(Message message, String groupCode) {
		List<Triplet<String, String, String>> combinationsList = new ArrayList<>();
		List<String> regSezGstins = new ArrayList<>();
		List<String> isdGstins = new ArrayList<>();
		String returnTypeStr = env.getProperty("return.filling.status.types");
		if (returnTypeStr == null) {
			LOGGER.error(
					"Return Types are not configured in Application.properties,"
							+ " Hence Return Status Entries cannot be created");
			return;
		}
		String[] returnTypes = returnTypeStr.split(",");
		LocalDate today = LocalDate.now();
		String[] datestr = today.toString().split("-");
		String taxPeriod = datestr[1].concat(datestr[0]);
		if (LOGGER.isDebugEnabled()) {
			String logMsg = "Fetching Active gstins but Posting yet to happen";
			LOGGER.debug(logMsg);
		}
		regSezGstins = gstinDetailRepository.getActiveRegularSEZGstins();

		if (regSezGstins != null && regSezGstins.isEmpty()) {
			LOGGER.error("There are no Active Reg/SEZ GSTIN for Group '%s',"
					+ " Hence Return Status Entries cannot be created");
		}
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetched Active REG/SEZ gstins '%s' for Group %s"
							+ "but Posting yet to happen",
					regSezGstins, groupCode);
			LOGGER.debug(logMsg);
		}

		isdGstins = gstinDetailRepository.getActiveISDGstins();

		if (isdGstins != null && isdGstins.isEmpty()) {
			LOGGER.error("There are no Active ISD GSTIN for Group '%s',"
					+ " Hence Return Status Entries cannot be created");
		}
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetched Active ISD gstins '%s' for Group %s"
							+ "but Posting yet to happen",
					regSezGstins, groupCode);
			LOGGER.debug(logMsg);
		}

		for (String gstin : regSezGstins)
			for (String returnType : returnTypes)
				if (!"GSTR6".equals(returnType))
					combinationsList.add(new Triplet<String, String, String>(
							gstin, taxPeriod, returnType));
		for (String gstin : isdGstins)
			combinationsList.add(new Triplet<String, String, String>(gstin,
					taxPeriod, "GSTR6"));

		List<GstrReturnStatusEntity> returnStatusEntries = new ArrayList<>();
		combinationsList.forEach(combination -> 
			returnStatusEntries.add(new GstrReturnStatusEntity(
					combination.getValue0(), combination.getValue1(),
					combination.getValue2(), "NEW", LocalDateTime.now()))
		);
		if (!returnStatusEntries.isEmpty())
			gstrReturnStatusRepository.saveAll(returnStatusEntries);
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Posted Return Status entries list"
					+ " for which Status will be tracked");
			LOGGER.debug(logMsg);
		}
	}

}
