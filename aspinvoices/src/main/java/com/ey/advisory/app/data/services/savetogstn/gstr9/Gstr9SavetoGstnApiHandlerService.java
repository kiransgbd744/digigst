package com.ey.advisory.app.data.services.savetogstn.gstr9;

import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;


/**
 * 
 * @author Jithendra.B
 *
 */

public interface Gstr9SavetoGstnApiHandlerService {

	public void executeGstr9SaveToGstn(
			GetDetailsForGstr9ReqDto getDetailsForGstr9ReqDto);

}
