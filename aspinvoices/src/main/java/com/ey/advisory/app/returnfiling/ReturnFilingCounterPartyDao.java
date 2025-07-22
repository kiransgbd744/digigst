/**
 * 
 */
package com.ey.advisory.app.returnfiling;

import java.util.List;

/**
 * @author Arun KA
 *
 */
public interface ReturnFilingCounterPartyDao {

	List<ReturnFilingCounterPartyStatusDto> getCounterPartyDetailsDB(
			String userName);

}
