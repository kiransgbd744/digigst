/**
 * 
 */
package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface GenerateIrn {
	
	public APIResponse generateIrn(EinvoiceRequestDto requestDto, String gstin);

}
