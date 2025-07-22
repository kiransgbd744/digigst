package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.List;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr7BatchMaker {
	public List<SaveToGstnBatchRefIds> saveGstr7Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId, ProcessingContext gstr7context);
}
