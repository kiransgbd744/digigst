package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.TermCondRespDto;
import com.ey.advisory.core.dto.TermConditionsReqDto;

public interface TermCondtionsService {

	public List<TermCondRespDto> getTermCondi(
			final TermConditionsReqDto reqDto);

	public void saveTermCondi(final List<TermConditionsReqDto> reqDtos);

	public void deleteTermCondtions(final TermConditionsReqDto reqDtos);

}
