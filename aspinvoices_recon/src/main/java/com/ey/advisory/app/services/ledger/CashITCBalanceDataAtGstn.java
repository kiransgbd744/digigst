package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface CashITCBalanceDataAtGstn {

	public String fromGstn(GetCashITCBalanceReqDto dto, String groupCode);
}
