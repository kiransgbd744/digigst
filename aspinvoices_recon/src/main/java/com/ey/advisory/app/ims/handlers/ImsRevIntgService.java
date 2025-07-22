package com.ey.advisory.app.ims.handlers;

import org.springframework.stereotype.Component;

/**
 * @author Ravindra V S
 *
 */
@Component("ImsRevIntgService")
public interface ImsRevIntgService {

	public Integer getImsErpPush(ImsRevIntgReqDto dto);
}
