package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.List;


/**
 * @author Ravindra V S
 *
 */
public interface Gstr3BSetOffEntityDashboardService {

	List<Gstr3bSetOffEntityDashboardRespDto> getStatusData(
			List<String> gstnsList, String taxPeriod);

}
