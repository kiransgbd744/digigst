package com.ey.advisory.app.gstr3b;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BGetLedgerDetails {
	
	public LedgerRespDto getLedgerdetails(String gstin, 
							String taxPeriod);
}
