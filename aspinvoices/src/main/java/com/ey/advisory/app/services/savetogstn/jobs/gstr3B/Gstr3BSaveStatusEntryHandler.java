package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

public interface Gstr3BSaveStatusEntryHandler {

	public void createGstr3BSaveStatusEntry(String taxPeriod, String gstin,
			String refId, String Status, String filePath, String groupCode,
			String request, String response, String apiAction);

}
