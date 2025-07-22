package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr2XBatchMaker {
	
	public List<SaveToGstnBatchRefIds> saveGstr2XData(String groupCode, String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId);

}
