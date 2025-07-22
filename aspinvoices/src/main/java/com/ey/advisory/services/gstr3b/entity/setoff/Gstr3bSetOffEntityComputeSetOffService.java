package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.List;

/**
 * @author visal.verma
 *
 */
public interface Gstr3bSetOffEntityComputeSetOffService {
	
	public String getComputeStatus(List<String> gstinList, String taxPeriod);

}
