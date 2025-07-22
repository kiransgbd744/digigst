/**
 * 
 */
package com.ey.advisory.app.data.services.drc01c;

import java.util.List;

import com.google.gson.JsonArray;

/**
 * @author vishal.verma
 *
 */
public interface DrcPartCSaveToGSTN {
	
	public JsonArray saveEntityLevelData(List<String> gstinsList, String taxPeriod);

}
