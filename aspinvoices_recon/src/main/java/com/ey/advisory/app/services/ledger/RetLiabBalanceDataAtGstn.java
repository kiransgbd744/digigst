package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.docs.dto.ledger.RetLiabBalanceReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface RetLiabBalanceDataAtGstn {

	public String fromGstn(RetLiabBalanceReqDto dto, String groupCode);
}
