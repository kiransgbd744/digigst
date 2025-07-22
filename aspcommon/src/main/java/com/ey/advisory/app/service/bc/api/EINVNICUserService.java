package com.ey.advisory.app.service.bc.api;

/**
 * @author vishal.verma
 *
 */

import java.util.List;

import com.ey.advisory.common.Gstindto;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;

public interface EINVNICUserService {

	List<EINVNICUser> getAllEINVNICUsers(List<String> gstiNsForEntity);

	int updateEINVNICUser(EINVNICUser einvNICUser);

	List<Gstindto> getDistinctEINVGstin();
	
	EINVNICUser getEinvUserByGstin(String gstin);
}
