package com.ey.advisory.app.returnfiling;

import java.util.List;

/**
 * @author Arun.KA
 *
 */
public interface ReturnFilingCounterParty {

	public List<ReturnFilingCounterPartyStatusDto> getCounterPartyStatus(
			String userName);

}
