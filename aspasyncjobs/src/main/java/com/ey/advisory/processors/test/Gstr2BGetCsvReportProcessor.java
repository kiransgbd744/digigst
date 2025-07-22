/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.gstr2b.Gstr2bCompleteReport;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2BGetCsvReportProcessor")
public class Gstr2BGetCsvReportProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("Gstr2bCompleteReport")
	private Gstr2bCompleteReport reportService;
	
	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin Gstr2BGetCsvReportProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		try {
			String jsonString = message.getParamsJson();
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);
			
			String gstin = json.get("gstin").getAsString();
			String taxPeriod = json.get("taxPeriod").getAsString();
			
			List<String> gstins = Arrays.asList(gstin);
			List<String> taxPeriods = Arrays.asList(taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2BGetCsvReportProcessor invoking getGstr2bCompleteReport method :%s",
						message.toString());
				LOGGER.debug(msg);
			}

			Pair<String, String> csvGenPath = reportService.getGstr2bCompleteReport(gstins,
					taxPeriods, "ALL", message.getId());
			
			String filePath = csvGenPath != null ? csvGenPath.getValue0()
					: null;
			String docId = csvGenPath != null ? csvGenPath.getValue1() : null;
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"response recevied from getGstr2bCompleteReport "
						+ "method filePath : %s docId : %s ",
						filePath, docId);
				LOGGER.debug(msg);
			}

			gstnStatusRepo.updateStatusAndDocId(gstin, taxPeriod,
					APIConstants.GSTR2B, APIConstants.SUCCESS,
					csvGenPath != null ? csvGenPath.getValue1() : null,
					csvGenPath != null ? csvGenPath.getValue0() : null, null,
					LocalDateTime.now());


		} catch (Exception ex) {
			LOGGER.error("Error Occured in Gstr2BGetCsvReportProcessor");
			throw new AppException(ex);
		}

	}

}
