package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr2BDashBoardErrorDto {

	private String errorCode;
	
	private String errorMsg;
	
	private String gstin;
	
	private String taxPeriod;
	
	private String status;
	
	private String filePath;
	
	private LocalDateTime createdOn;
	
	private String docId;
}
