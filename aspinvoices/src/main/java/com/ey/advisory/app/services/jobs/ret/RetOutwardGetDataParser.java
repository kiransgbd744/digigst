package com.ey.advisory.app.services.jobs.ret;

import java.util.Set;

import com.ey.advisory.app.data.entities.ret.GetRetOutwardEntity;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */

public interface RetOutwardGetDataParser {

	public Set<GetRetOutwardEntity> parseOutwardGetData(
			RetGetInvoicesReqDto dto, String apiResp);

}
