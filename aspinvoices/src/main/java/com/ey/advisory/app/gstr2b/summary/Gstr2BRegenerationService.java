package com.ey.advisory.app.gstr2b.summary;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr2b.Gstr2BRegenerationRespDto;

/**
 * 
 * @author ashutosh.kar
 *
 */
@Component("Gstr2BRegenerationService")
public interface Gstr2BRegenerationService {

	public List<Gstr2BRegenerationRespDto> getStatusData(List<String> gstins,
			int derivedStartPeriod, int derivedEndPeriod,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus,String appendMonthYear);
	
	/*public Map<String, String> getGstrReturnStatusMap(
			 List<String> totlTaxPeriods);*/

}
