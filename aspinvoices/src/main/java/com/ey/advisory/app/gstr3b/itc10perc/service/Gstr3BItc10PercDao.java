package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BItc10PercDao {
	
	public List<Gstr3BITCInnerDto> getDbResp(String gstin, String taxPeriod);

}
