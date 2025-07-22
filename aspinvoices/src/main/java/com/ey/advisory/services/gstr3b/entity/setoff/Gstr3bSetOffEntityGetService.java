package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.List;

/**
 * @author Hema G M
 *
 */


public interface Gstr3bSetOffEntityGetService {

	public String getStatusData(List<String> gstins, String taxPeriod);
	
	
}