package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.List;

import com.google.gson.JsonArray;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BSetOffEntityLevelSaveToGstnService {
	
	public JsonArray saveEntityLevelData(List<String> gstinsList, String taxPeriod);

}
