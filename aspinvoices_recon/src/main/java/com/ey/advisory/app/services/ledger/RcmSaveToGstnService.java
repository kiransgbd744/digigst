package com.ey.advisory.app.services.ledger;

public interface RcmSaveToGstnService {
	
	
	public void rcmSaveToGstnApiCall(String data, String gstin,Long id);
	public void reclaimSaveToGstnApiCall(String data, String gstin,Long id);
}
