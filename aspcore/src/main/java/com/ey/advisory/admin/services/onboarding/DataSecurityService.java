package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.DataPermissionRespDto;
import com.ey.advisory.core.dto.DataSecurityAttriUserRespDto;
import com.ey.advisory.core.dto.DataSecurityReqDto;
import com.ey.advisory.core.dto.DataSecurityRespDto;

/**
 * @author Umesha.M
 *
 */
public interface DataSecurityService {

	/**
	 * @param reqJson
	 * @return
	 */
	public List<DataSecurityRespDto> getDataSecurity(
			final DataSecurityReqDto dataSecurityReqDto);

	/**
	 * @param reqJson
	 * @return
	 */
	public List<DataPermissionRespDto> getDataPermission(
			final DataSecurityReqDto dataSecurityReqDto);

	/*
	 * Getting all selected attributes for particular User
	 */

	public List<DataSecurityAttriUserRespDto> getDataPermissionForUser(
			final List<Long> entityList, String userName, String regType);

	public List<DataSecurityAttriUserRespDto> getDataPermissionForUser(String regType);

	/**
	 * @param reqJson
	 * @return
	 *
	 */
	public void updateDataPermission(
			final DataSecurityReqDto dataSecurityReqDto,
			final String loggedInUser);
}
