/**
 * 
 */
package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.google.gson.JsonArray;

/**
 * @author vishal.verma
 *
 */
public interface DrcPartBSaveToGSTN {
	
	public JsonArray saveEntityLevelData(List<String> gstinsList, String taxPeriod);

}
