package com.ey.advisory.gstr2.userdetails;

import java.util.List;

/**
 * @author Umesha.M
 *
 */
public interface UserCreateDetailService {

	public List<UserCreationDto> findUserDetail(UserCreationReqDto dto);

}
