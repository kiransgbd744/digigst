package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.ELExtractRegDto;
import com.ey.advisory.core.dto.ElRegistrationReqDto;

/**
 * @author Umesha.M
 *
 */
public interface ELRegistrationService {

	/**
	 * @param jsonReq
	 * @return
	 */
	public List<ELExtractRegDto> getRegistrationDetails(
			ElRegistrationReqDto dto);

	/**
	 * @param jsonReq
	 * @return
	 */
	public String updateGstnInfoDetails(List<ElRegistrationReqDto> elRegReqDtos);

	/**
	 * @param jsonReq
	 * @return
	 */
	public void deleteGstnInfoDetails(
			final List<ElRegistrationReqDto> elRegistrationReqDtos);

}
