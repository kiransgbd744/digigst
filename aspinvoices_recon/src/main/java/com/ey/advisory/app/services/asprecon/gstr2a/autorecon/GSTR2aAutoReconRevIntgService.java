package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import org.springframework.stereotype.Component;

/**
 * @author Jithendra.B
 *
 */
@Component("GSTR2aAutoReconRevIntgService")
public interface GSTR2aAutoReconRevIntgService {

	public Integer autoReconGet2AToErpPush(GSTR2aAutoReconRevIntgReqDto dto);
}
