package com.ey.advisory.app.docs.dto.gstr3B;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gstr3BFilingDetailsDTO {
	
	private Integer id;
	private String filingMode;
	private Long refId;	
	private String status;
	private String error;
	private String ackNo;
	private String createdTime;
	private String filingType;
	private boolean isDownloadFlag;
	private String createdOn;


}
