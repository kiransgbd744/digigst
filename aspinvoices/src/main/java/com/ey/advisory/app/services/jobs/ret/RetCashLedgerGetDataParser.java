package com.ey.advisory.app.services.jobs.ret;

import java.util.Set;

import com.ey.advisory.app.data.entities.ret.GetRefundClaimedEntity;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface RetCashLedgerGetDataParser {
	public Set<GetRefundClaimedEntity> parseCashLedgerGetData(
			RetGetInvoicesReqDto dto, String apiResp);

}
