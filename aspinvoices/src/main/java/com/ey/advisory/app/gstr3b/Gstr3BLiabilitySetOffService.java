package com.ey.advisory.app.gstr3b;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BLiabilitySetOffService {

	public Gstr3BLiabilitySetOffDto get3BLiabilitySetOff(String gstin, 
			String taxPeriod, String authToken, String msg);
}
