package com.ey.advisory.admin.azurebus.service;

/**
 * @author Siva.Reddy
 *
 */
public interface ITPEventResponseService {

	String getListOfGroups();

	String getEntitiesDetails(String groupCode);

	String getUserDetails(String groupCode);
	
	void updateUserDetails(ITPEventRequestDto requestDto);
	
	String getBulkUserPermissionsDetails(ITPEventRequestParamsDto requestParams);


}
