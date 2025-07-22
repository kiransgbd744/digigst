/**
 * 
 */
package com.ey.advisory.app.gstr2b;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2BDashBoardDao {
	
	List<Gstr2BDashBoardErrorDto> getErrorCodeforGetCall(List<String> gstins, 
			int derivedStartPeriod, int derivedEndPeriod);

}
