package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9SummaryDto;

/**
 * 
 * @author Rajesh N K
 *
 */
public interface Gstr9SummaryService {

	List<Gstr9SummaryDto> listGstr9ummary(List<String> gstnsLists,
			String fy);

}
