package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.PublicApiConstants;

import lombok.extern.slf4j.Slf4j;

@Component("PublicApiSwitchingProcessor")
@Slf4j
public class PublicApiSwitchingProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	private EYConfigRepository eyConfigRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Waking up PublicApiSwitchingProcessor to check Public API "
							+ "Endpoint at : {} and IST is {}",
					LocalDateTime.now(),
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
		}

		Map<String, Config> publicCallconfigMap = configManager.getConfigs(
				PublicApiConstants.PUBLIC_API,
				PublicApiConstants.PUBLIC_CALL_API_KEY);

		String endPoint = publicCallconfigMap
				.get(PublicApiConstants.END_POINT_KEY) == null
						? PublicApiConstants.EY
						: publicCallconfigMap
								.get(PublicApiConstants.END_POINT_KEY)
								.getValue();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Found endPoint {} at {}", endPoint,
					LocalDateTime.now());
		}

		if (!PublicApiConstants.EY.equalsIgnoreCase(endPoint)) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Switching endPoint {} to EY at {}", endPoint,
						LocalDateTime.now());
			}
			eyConfigRepo.updateValueOnKey(PublicApiConstants.END_POINT_KEY,
					PublicApiConstants.EY);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Completed Switching Public API Endpoint at : {} and IST "
								+ "is {}",
						LocalDateTime.now(),
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
			}

		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Switching for Public API didn't happen as we are already"
								+ " in EY " + "Endpoint at : {} and IST is {}",
						LocalDateTime.now(),
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
			}

		}

	}

}
