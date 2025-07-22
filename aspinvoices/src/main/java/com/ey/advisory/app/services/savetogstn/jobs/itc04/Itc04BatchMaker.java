package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
/**
 * 
 * @author SriBhavya
 *
 */
public interface Itc04BatchMaker {

	List<SaveToGstnBatchRefIds> saveItc04Data(String groupCode, String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId);

}
