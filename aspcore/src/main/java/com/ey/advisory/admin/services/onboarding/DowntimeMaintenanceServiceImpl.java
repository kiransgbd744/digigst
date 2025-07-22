package com.ey.advisory.admin.services.onboarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.async.domain.master.EYRegularConfig;
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.DownTimeMaintenanceReqDto;
import com.ey.advisory.core.dto.DownTimeMaintenanceRespDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 * 
 * 
 */
@Component("DowntimeMaintenanceServiceImpl")
@Slf4j
public class DowntimeMaintenanceServiceImpl implements DowntimeMaintenanceService {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	EYConfigRepository eyConfigRepo;

	@Override
	public DownTimeMaintenanceRespDto updateConfigParmetr(DownTimeMaintenanceReqDto dto) {
		DownTimeMaintenanceRespDto resp = new DownTimeMaintenanceRespDto();
		String downMsg = "DownTime message successfully cleared";

		try {
			if (dto.isFlag()) {
				downMsg = String.format("%s <b>%s</b> to <b>%s</b>", 
                        dto.getDownTimemsg(), dto.getStartTime(), dto.getEndTime());
			}

			EYRegularConfig findEntry = eyConfigRepo.findByCategoryAndKeyAndGroupCode("DOWNTIMECONFIG",
					"downtime_maintenance", "DEFAULT");

			if (findEntry != null) {
				String updatedMessage = dto.isFlag() ? downMsg : "";
				eyConfigRepo.updateValueOnKey("downtime_maintenance", updatedMessage);
				resp.setDownTimeMsg(downMsg);
				resp.setId(findEntry.getId());
				LOGGER.debug("Successfully updated downtime message with ID: {}", findEntry.getId());
				return resp;
			} else {
				LOGGER.debug("No configuration entry found for downtime_maintenance");
			}

		} catch (Exception e) {
			LOGGER.error("Error occurred while updating downtime configuration: {}", e.getMessage(), e);
			throw new AppException();
		}

		return null;
	}

}
