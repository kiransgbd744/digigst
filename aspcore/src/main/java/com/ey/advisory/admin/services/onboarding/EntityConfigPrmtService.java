package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.EntityConfigPrmtReqDto;
import com.ey.advisory.core.dto.EntityConfigPrmtResDto;

public interface EntityConfigPrmtService {

	public List<EntityConfigPrmtResDto> getEntityConfigPrmt(
	        final EntityConfigPrmtReqDto dto);

	public void updateConfigParmetr(final List<EntityConfigPrmtReqDto> dtos);

}
