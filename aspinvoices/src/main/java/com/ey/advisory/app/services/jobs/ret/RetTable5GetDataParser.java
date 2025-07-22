package com.ey.advisory.app.services.jobs.ret;

import java.util.Set;

import com.ey.advisory.app.data.entities.ret.GetRetTable5Entity;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface RetTable5GetDataParser {

	public Set<GetRetTable5Entity> parseTable5GetData(RetGetInvoicesReqDto dto,
			String apiResp);

}
