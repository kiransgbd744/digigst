package com.ey.advisory.admin.services.onboarding;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.dto.DownTimeMaintenanceReqDto;
import com.ey.advisory.core.dto.DownTimeMaintenanceRespDto;

/**
 * 
 * @author ashutosh.kar
 * 
 * 
 */
@Component("DowntimeMaintenanceService")
public interface DowntimeMaintenanceService {

	public DownTimeMaintenanceRespDto updateConfigParmetr(DownTimeMaintenanceReqDto dto);
}
