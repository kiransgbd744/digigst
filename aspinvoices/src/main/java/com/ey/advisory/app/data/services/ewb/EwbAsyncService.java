/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.ewb.dto.EwbResponseDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EwbAsyncService {
	
	void generateEwayBill(Long Id);
	EwbResponseDto processGenerateEwb(OutwardTransDocument doc);

}
