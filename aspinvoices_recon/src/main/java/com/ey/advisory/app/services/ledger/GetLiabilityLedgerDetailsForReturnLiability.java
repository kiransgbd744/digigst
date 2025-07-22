package com.ey.advisory.app.services.ledger;

import java.util.List;

import com.ey.advisory.app.docs.dto.ledger.LiabDetailsRespDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface GetLiabilityLedgerDetailsForReturnLiability {

	public List<LiabDetailsRespDto> findTax(String jsonReq);
}
