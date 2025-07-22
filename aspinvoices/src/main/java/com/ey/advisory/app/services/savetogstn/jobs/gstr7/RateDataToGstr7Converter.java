package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
/**
 * 
 * @author SriBhavya
 *
 */
public interface RateDataToGstr7Converter {
	public SaveBatchProcessDto convertToGstr7Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType);
}
