package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
/**
 * 
 * @author Sri Bhavya
 *
 */
public interface RateDataToGstr6Converter {

	public SaveBatchProcessDto convertToGstr6Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType);
}
