package com.ey.advisory.app.gstr2b.summary;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2BDetailsService {

	public Gstr2BDetailsRespDto getDetailsList(String gstin,
			String toTaxPeriod, String fromTaxPeriod);

}
