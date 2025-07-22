package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Sri Bhavya
 *
 */

public interface Gstr6BatchMaker {

	public List<SaveToGstnBatchRefIds> saveGstr6Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId);
}
