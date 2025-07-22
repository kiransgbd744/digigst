package com.ey.advisory.app.services.jobs.ret;

import java.util.Set;

import com.ey.advisory.app.data.entities.ret.GetRetInterestLateFeeEntity;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface RetTable6GetDataParser {
	public Set<GetRetInterestLateFeeEntity> parseTable6GetData(
			RetGetInvoicesReqDto dto, String apiResp);

}
