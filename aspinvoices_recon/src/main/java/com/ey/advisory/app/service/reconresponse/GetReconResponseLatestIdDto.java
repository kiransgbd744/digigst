package com.ey.advisory.app.service.reconresponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetReconResponseLatestIdDto {

	
	private String gstin;
	private String taxPeriod;
	private String status;
	private Long requestId;
}
