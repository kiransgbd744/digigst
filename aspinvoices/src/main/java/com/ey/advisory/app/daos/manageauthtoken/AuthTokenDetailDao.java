package com.ey.advisory.app.daos.manageauthtoken;

import java.util.List;

import com.ey.advisory.app.docs.dto.ManageAuthTokenDetDto;

public interface AuthTokenDetailDao {
	
	List<ManageAuthTokenDetDto> findAuthDetailsForGstins(List<String> gstins);

}
