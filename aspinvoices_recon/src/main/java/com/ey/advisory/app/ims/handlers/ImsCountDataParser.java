package com.ey.advisory.app.ims.handlers;


import java.util.List;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Sakshi.jain
 *
 */
public interface ImsCountDataParser {

	public void parseImsCountData(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId, String jsonSTring);
	
}
