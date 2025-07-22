package com.ey.advisory.app.data.services.drc01c;

import java.util.List;

import com.ey.advisory.app.gstr3b.Gst3BSaveStatusDto;

/**
 * @author vishal.verma
 *
 */
public interface DRC01CSaveStatus {
	
	public List<Gst3BSaveStatusDto> getSaveStatus(String gstin,
			String taxPeriod);

}
