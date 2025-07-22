package com.ey.advisory.app.service.bc.api;

/**
 * @author vishal.verma
 *
 */

import lombok.Data;

@Data
public class ERPRequestLogEntitydto {

	private String reqPayload;

	private String nicReqPayload;

	private String nicResPayload;

}
