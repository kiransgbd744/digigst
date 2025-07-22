/**
 * 
 */
package com.ey.advisory.app.gmr;

import java.util.List;

/**
 * @author Sakshi.jain
 *
 */
public interface GMROutwardSummaryDownloadDao {

	public List<GmrOutwardSummaryDownloadDto> find(
			List<String> gstinList, Integer fromTaxPeriod, Integer toTaxPeriod);

}
