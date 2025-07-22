package com.ey.advisory.app.data.services.savetogstn.gstr9;

/**
 * 
 * @author Jithendra.B
 *
 */

public interface Gstr9SaveStatusEntryHandler {

	public void createGstr9SaveStatusEntry(String taxPeriod, String gstin,
			String refId, String status, String filePath, String groupCode,
			String request, String response);

}
