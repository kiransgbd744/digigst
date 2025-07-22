package com.ey.advisory.bcadmin.common.service;

import java.util.List;

import com.ey.advisory.common.Gstindto;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;

public interface EINVNICUserService {

	List<EINVNICUser> getAllEINVNICUsers();

	int updateEINVNICUser(EINVNICUser einvNICUser);

	List<Gstindto> getDistinctEINVGstin();
}
