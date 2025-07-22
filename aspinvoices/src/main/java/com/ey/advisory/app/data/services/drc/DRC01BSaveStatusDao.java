package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.ey.advisory.app.gstr3b.Gst3BSaveStatusDto;

/**
 * @author vishal.verma
 *
 */
public interface DRC01BSaveStatusDao {
	
	public List<Gst3BSaveStatusDto> getSaveStatus(String gstin,
			String taxPeriod);

}
