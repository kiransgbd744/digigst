package com.ey.advisory.app.gstr3b;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arun.KA
 *
 */
@Getter
@Setter
public class Gst3BSaveStatusDto {
	
	private BigInteger id;
	private String refId;	
	private String status;
	private BigInteger errorCnt;
	private String createdTime;
	private String action;
	private Boolean isErrorDownloadFlag = false;
	private String createdOn;
}
