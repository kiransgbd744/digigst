package com.ey.advisory.admin.services.onboarding;

import com.ey.advisory.admin.data.entities.client.User;

public interface UserLoadService {
	
	public User loadUser(String groupCode,String usrPrnplName);

}
