package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface LiabilityLedgerDetailsForReturnLiabilityDataAtGstn {

	public String fromGstn(GetCashITCBalanceReqDto dto);
	public String fromGstnTestLiab(GetCashITCBalanceReqDto dto);
}
