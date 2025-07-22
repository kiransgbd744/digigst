package com.ey.advisory.app.services.jobs.ret;

import java.util.Set;

import com.ey.advisory.app.data.entities.ret.GetRetInwardEntity;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface RetInwardGetDataParser {

	public Set<GetRetInwardEntity> parseInwardGetData(RetGetInvoicesReqDto dto,
			String apiResp);

}
