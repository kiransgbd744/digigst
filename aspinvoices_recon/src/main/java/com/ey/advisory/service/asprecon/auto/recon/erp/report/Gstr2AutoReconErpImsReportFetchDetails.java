/**
 * 
 */
package com.ey.advisory.service.asprecon.auto.recon.erp.report;

import java.io.IOException;

/**
 * @author kiran.s
 *
 */
public interface Gstr2AutoReconErpImsReportFetchDetails {

	public void generateImsReport(Long configId, Long entityId) throws IOException;
}
