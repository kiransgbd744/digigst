package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;

/**
 * 
 * @author SriBhavya
 *
 */
public interface RateDataToItc04Converter {
	public SaveBatchProcessDto convertToItc04Object(List<Object[]> objects, String section, String groupCode,
			String taxDocType);
}
