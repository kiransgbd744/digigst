
package com.ey.advisory.app.gstr3b;

import java.util.List;

/**
 * @author Sakshi.jain
 *
 */
public interface Gstr3BGstinNewDashboardService {

	public List<Gstr3BNewSuppliesDto> get4SubsectionsData(String taxPeriod,
			String gstin, String subsection,Long entityId);
	
	public List<Gstr3BInfoSuppliesDto> getInfoSubsectionsData(String taxPeriod,
			String gstin, String subsection);
}
