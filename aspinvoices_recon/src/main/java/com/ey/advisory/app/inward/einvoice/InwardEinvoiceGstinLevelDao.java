package com.ey.advisory.app.inward.einvoice;

import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */

public interface InwardEinvoiceGstinLevelDao {
	public List<GstinLevelInnerDto> findGstinLevelData(
			InwardEinvoiceGstinLevelReqDto criteria);

}
