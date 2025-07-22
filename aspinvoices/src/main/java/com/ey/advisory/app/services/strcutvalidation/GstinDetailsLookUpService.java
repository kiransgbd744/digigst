package com.ey.advisory.app.services.strcutvalidation;

interface GstinDetailsLookUpService {
	
	public boolean isValidGstinForGroup(String gstin, String groupCode);
}
