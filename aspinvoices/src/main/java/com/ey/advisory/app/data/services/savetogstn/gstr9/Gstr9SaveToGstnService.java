package com.ey.advisory.app.data.services.savetogstn.gstr9;


/**
 * 
 * @author Jithendra.B
 *
 */

public interface Gstr9SaveToGstnService {

	public String saveGstr9DataToGstn(String gstin, String fy);
	
	public void callGstr9SaveToGstn(String gstin, String fy,String groupCode);
}
