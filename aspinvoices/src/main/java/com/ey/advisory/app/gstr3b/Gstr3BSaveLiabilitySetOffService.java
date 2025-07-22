package com.ey.advisory.app.gstr3b;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BSaveLiabilitySetOffService {
	
	public String saveLiablityToGstn(String gstin, String taxPeriod, 
			String groupCode);

}
