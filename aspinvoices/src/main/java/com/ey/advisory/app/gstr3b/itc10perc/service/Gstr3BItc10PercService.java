package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BItc10PercService {
	
	public List<Gstr3BItc10PercDto> getItcData(String gstin, String taxPeriod, Long entityId);

}
