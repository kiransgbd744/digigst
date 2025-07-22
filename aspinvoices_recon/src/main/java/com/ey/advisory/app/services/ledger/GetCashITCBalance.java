package com.ey.advisory.app.services.ledger;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface GetCashITCBalance {

	public String findBalance(final String jsonReq, final String groupCode);
}
