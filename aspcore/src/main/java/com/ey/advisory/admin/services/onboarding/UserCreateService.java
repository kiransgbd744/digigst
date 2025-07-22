package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.UserCreationDto;
import com.ey.advisory.core.dto.UserCreationReqDto;

/**
 * @author Umesha.M
 *
 */
public interface UserCreateService {

	public List<UserCreationDto> findUserDetails(
			final UserCreationReqDto userCreaReqDto);

	/**
	 * @param reqJson
	 * @return
	 */
	public String updateUserCreation(
			final List<UserCreationReqDto> userCreationReqDtos);

	/**
	 * @param reqJson
	 * @return
	 */
	public void deleteUserCreation(
			final List<UserCreationReqDto> userCreationReqDtos);
}
