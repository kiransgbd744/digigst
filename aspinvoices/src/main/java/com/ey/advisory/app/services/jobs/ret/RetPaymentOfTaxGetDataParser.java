package com.ey.advisory.app.services.jobs.ret;

import java.util.Set;

import com.ey.advisory.app.data.entities.ret.GetRetPaymentOfTaxEntity;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface RetPaymentOfTaxGetDataParser {
	public Set<GetRetPaymentOfTaxEntity> parsePaymentTaxGetData(
			RetGetInvoicesReqDto dto, String apiResp);

}
