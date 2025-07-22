package com.ey.advisory.bcadmin.common.service;


import java.util.List;

import com.ey.advisory.common.Gstindto;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;

public interface EWBNICUserService {

	List<EWBNICUser> getAllEWBNICUsers();

	int updateEWBNICUser(EWBNICUser ewbNICUser);

	List<Gstindto> getDistinctEWBGstin();
}
