/**
 * 
 */
package com.ey.advisory.service.asprecon.auto.recon.erp.report;

import java.io.IOException;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2AutoReconErpReportFetchDetails {

	public void generateReport(Long configId, Long entityId) throws IOException;
}
