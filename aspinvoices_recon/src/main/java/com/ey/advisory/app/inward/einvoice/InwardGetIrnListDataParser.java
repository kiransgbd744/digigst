package com.ey.advisory.app.inward.einvoice;


import java.util.List;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Sakshi.jain
 *
 */
public interface InwardGetIrnListDataParser {

	public List<String> parseIrnListData(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId);
	
}
