package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr8BatchMaker {
	public List<SaveToGstnBatchRefIds> saveGstr8Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId);
}
