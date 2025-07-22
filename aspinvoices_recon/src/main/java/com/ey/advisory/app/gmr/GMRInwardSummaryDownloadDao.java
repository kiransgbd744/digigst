/**
 * 
 */
package com.ey.advisory.app.gmr;

import java.util.List;

/**
 * @author Ravindra V S
 *
 */
public interface GMRInwardSummaryDownloadDao {

	public List<GmrInwardSummaryDownloadDto> find(
			List<String> gstinList, Integer fromTaxPeriod, Integer toTaxPeriod);

}
