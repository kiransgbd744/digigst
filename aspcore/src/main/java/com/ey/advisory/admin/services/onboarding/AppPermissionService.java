package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.AppPermissionReqDto;
import com.ey.advisory.core.dto.AppPermissionRespDto;
import com.ey.advisory.core.dto.AppProfileResp;

public interface AppPermissionService {

	public List<AppPermissionRespDto> getPermissions(
			final AppPermissionReqDto apPermReqDto);

	public void updatePermissions(
			final List<AppPermissionReqDto> appPermReqDtos);

	public List<AppProfileResp> getProfilePermission();
}
