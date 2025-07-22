/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.pr2b.reports.Gstr2ReconReportReGenerateHandler;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ReconReportReGenerateProcessor")
public class Gstr2ReconReportReGenerateProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2ReconReportReGenerateHandler")
	Gstr2ReconReportReGenerateHandler reportHandler;
	
	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;
	
	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr2ReconReportReGenerateProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		
		Long configId = json.get("configId").getAsLong();
		String reconType = json.get("reconType").getAsString();
		
		try{

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2ReconReportReGenerateProcessor, Invoking "
								+ "reportHandle() configId %s,- : ",
						configId.toString());
				LOGGER.debug(msg);

			}
			
			reportHandler.reportHandle(configId, reconType);
			
			
		} catch (Exception e) {
			LOGGER.error(
					"Error occured in Gstr2ReconReportReGenerateProcessor  "
					+ "configId {} ",
					configId.toString());
			
			reconConfigRepo.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now(),
					configId);
			throw new AppException(e);
		}
		
	}
	
	/*public static void main(String[] args) {
		
		 String[] arr = {"apple", "banana", "orange", "grape"};
	        List<String> list = new ArrayList<>(Arrays.asList("banana", "pear", "grape", "abc"));

	        List<String> commonElementsList = new ArrayList<>();
	        for (String element : list) {
	            if (Arrays.asList(arr).contains(element)) {
	                commonElementsList.add(element);
	            }
	        }

	        String[] commonElementsArray = commonElementsList.toArray(new String[0]);
	        System.out.println(Arrays.toString(commonElementsArray));
		
	}*/

}


