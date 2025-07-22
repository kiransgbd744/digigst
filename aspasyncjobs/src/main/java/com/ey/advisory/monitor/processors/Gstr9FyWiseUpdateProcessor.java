/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9ComputeReadyStatusFYRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9PeriodWiseRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Service("Gstr9FyWiseUpdateProcessor")
@Slf4j
public class Gstr9FyWiseUpdateProcessor implements TaskProcessor {

	@Autowired
	Gstr9PeriodWiseRepository gstr9PeriodWiseRepo;

	@Autowired
	Gstr9ComputeReadyStatusFYRepository gstr9FyRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"About to monitor GSTR9 Fy level for group '%s' ",
					message.getGroupCode());
			LOGGER.debug(logMsg);
		}

		try {
			JsonObject json = (new JsonParser().parse(message.getParamsJson())
					.getAsJsonObject());

			Integer fy = json.get("fy").getAsInt();
			Integer derivedAmdStPeriod = json.get("derivedAmdStPeriod")
					.getAsInt();
			Integer derviedAmdLtPeriod = json.get("derviedAmdLtPeriod")
					.getAsInt();

			List<String> gstr1Gstins = gstr9PeriodWiseRepo
					.findGstr1EligibleForFY(fy);

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"List of GSTR1 gstins eligible for FY '%s' - '%s' ",
						gstr1Gstins, fy);
				LOGGER.debug(logMsg);
			}
			
			if (!gstr1Gstins.isEmpty()) {
				gstr9FyRepo.updateGstr1GetStatus(gstr1Gstins, fy, true,
						LocalDateTime.now());

				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							" Updated GSTR1 gstins in FY '%s' ", gstr1Gstins);
					LOGGER.debug(logMsg);
				}
			}

			List<String> gstr1AmdGstins = gstr9PeriodWiseRepo
					.findGstr1EligibleForAmd(derivedAmdStPeriod,
							derviedAmdLtPeriod);

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"List of GSTR1 Amd gstins eligible for FY '%s' - '%s' ",
						gstr1AmdGstins, fy);
				LOGGER.debug(logMsg);
			}
			
			if (!gstr1AmdGstins.isEmpty()) {
				gstr9FyRepo.updateGstr1AmdGetStatus(gstr1AmdGstins, fy, true,
						LocalDateTime.now());

				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							" Updated GSTR1 Amd gstins in FY '%s' ",
							gstr1AmdGstins);
					LOGGER.debug(logMsg);
				}
			}

			List<String> gstr3bGstins = gstr9PeriodWiseRepo
					.findGstr3BEligibleForFY(fy);

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"List of GSTR3B gstins eligible for FY '%s' ",
						gstr3bGstins);
				LOGGER.debug(logMsg);
			}

			if (!gstr3bGstins.isEmpty()) {
				gstr9FyRepo.updateGstr3BGetStatus(gstr3bGstins, fy, true,
						LocalDateTime.now());
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							" Updated GSTR3B gstins in FY '%s' ", gstr1Gstins);
					LOGGER.debug(logMsg);
				}
			}
		} catch (Exception ex) {
			String msg = "Exception occured in GSTR9 FY level monitor job";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}
