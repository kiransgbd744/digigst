package com.ey.advisory.einv.api;

import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;

/**
 * @author Siva Reddy
 *
 */
public interface GenerateEWBByIrn {

	public APIResponse generateEWBByIrn(GenerateEWBByIrnNICReqDto req);

}
