/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.ey.advisory.common.AppException;

/**
 * @author Khalid1.Khan
 *
 */
public interface Gstr3BEntityDashboardService {

	List<Gstr3BEntityDashboardDto> getEntityDashBoard(String taxPeriod,
			List<String> gstnsList, Long entityId) throws AppException;

}
