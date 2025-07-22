package com.ey.advisory.app.gstr2b;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2BRequestStatusService {

	public List<Gstr2bGet2bRequestStatusDto> getRequestWiseStatus(
			String userName);

}
