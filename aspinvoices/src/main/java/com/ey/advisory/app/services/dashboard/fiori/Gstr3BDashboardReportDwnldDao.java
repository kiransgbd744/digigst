package com.ey.advisory.app.services.dashboard.fiori;

import java.io.IOException;

/**
 * @author Sakshi.jain
 *
 */
public interface Gstr3BDashboardReportDwnldDao {

	public String getDashbrdData(Long batchId) throws IOException;

}
