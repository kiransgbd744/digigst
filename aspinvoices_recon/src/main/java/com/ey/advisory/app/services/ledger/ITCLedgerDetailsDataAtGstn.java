package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface ITCLedgerDetailsDataAtGstn {

	public String fromGstn(GetCashLedgerDetailsReqDto dto);
	public String fromGstnTest(GetCashLedgerDetailsReqDto dto);
	
}
