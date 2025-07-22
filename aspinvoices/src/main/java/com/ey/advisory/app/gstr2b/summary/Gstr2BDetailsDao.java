package com.ey.advisory.app.gstr2b.summary;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2BDetailsDao {
	
	public List<Gstr2BDetailsDto> getDetailsResp(List<String> gstin,
			String toTaxPeriod, String fromTaxPeriod);	

}
