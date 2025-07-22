package com.ey.advisory.app.services.manageauthtoken;

import java.util.List;

import com.ey.advisory.app.docs.dto.ManageAuthTokenDetDto;

public interface AuthTokenDetailService {
	
	List<ManageAuthTokenDetDto> getAuthDetailsForGstins(List<String> gstins);

}
