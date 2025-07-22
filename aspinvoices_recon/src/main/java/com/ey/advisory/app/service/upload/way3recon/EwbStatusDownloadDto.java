package com.ey.advisory.app.service.upload.way3recon;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EwbStatusDownloadDto {
	private String requestId;
	private long srNumber;
	private String gsin;
	private String getEwbStatus;
	private String ewbDate;
	private String getCallInitiatedOn;
	private String errorMessage;
}
