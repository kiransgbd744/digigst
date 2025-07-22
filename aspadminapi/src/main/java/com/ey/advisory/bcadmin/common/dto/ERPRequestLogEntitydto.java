package com.ey.advisory.bcadmin.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ERPRequestLogEntitydto {

	private String reqPayload;

	private String nicReqPayload;

	private String nicResPayload;

}
