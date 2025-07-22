package com.ey.advisory.app.gstr3b;

import java.util.List;

/**
 * @author Arun.KA
 *
 */
public interface Gstr3BSaveStatusDao {
	
	public List<Gst3BSaveStatusDto> getSaveStatus(String gstin,
			String taxPeriod);

}
