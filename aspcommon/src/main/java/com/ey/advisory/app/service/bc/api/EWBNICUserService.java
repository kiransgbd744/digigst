package com.ey.advisory.app.service.bc.api;

/**
 * @author vishal.verma
 *
 */

import java.util.List;

import com.ey.advisory.common.Gstindto;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;

public interface EWBNICUserService {

	List<EWBNICUser> getAllEWBNICUsers(List<String> gstiNsForEntity);

	int updateEWBNICUser(EWBNICUser ewbNICUser);

	List<Gstindto> getDistinctEWBGstin();
	
	EWBNICUser getEwbNicUserByGstin(String gstin);
}
