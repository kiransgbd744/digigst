package com.ey.advisory.app.services.ledger;

import java.util.List;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface GetITCLedgerDetails {

	public List<ItcDetailsRespDto> findITC(String jsonReq, String groupCode);
}
