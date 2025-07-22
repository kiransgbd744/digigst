package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.docs.dto.ledger.GetOtherthanReturnLedgerReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface OtherthanReturnLedgerDataAtGstn {

	public String fromGstn(GetOtherthanReturnLedgerReqDto dto, String groupCode);
}
