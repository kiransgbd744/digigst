package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.util.List;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
/**
 * 
 * @author Siva.Reddy
 *
 */
public interface RateDataToGstr8Converter {
	public SaveBatchProcessDto convertToGstr8Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType);
}
