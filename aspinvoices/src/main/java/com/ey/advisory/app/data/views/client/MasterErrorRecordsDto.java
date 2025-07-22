package com.ey.advisory.app.data.views.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MasterErrorRecordsDto {

	private String errorRecord;
	private String errorCode;
	private String errorDescription;

}
