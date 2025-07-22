package com.ey.advisory.app.services.ledger;

import java.util.List;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * @author Arun.KA
 *
 */
public interface GetSummaryLedgerBalanceDao {

	public List<GetSummaryLedgerBalanceDto> getSummaryLedgerDetails(
			List<String> gstins);

}
