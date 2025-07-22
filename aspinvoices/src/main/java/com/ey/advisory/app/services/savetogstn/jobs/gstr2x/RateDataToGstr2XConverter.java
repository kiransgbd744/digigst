package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;

/**
 * 
 * @author SriBhavya
 *
 */
public interface RateDataToGstr2XConverter {
	
	public SaveBatchProcessDto convertToGstr2XObject(List<Object[]> objects, String section, String groupCode,
			String taxDocType);

}
