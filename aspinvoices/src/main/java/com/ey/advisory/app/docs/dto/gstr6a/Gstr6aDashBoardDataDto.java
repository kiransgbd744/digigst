package com.ey.advisory.app.docs.dto.gstr6a;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
public class Gstr6aDashBoardDataDto {
	
	private String gstin;
	
	private String taxPeriod;
	
	private String section;
	
	private String status;
	
	private String endtime;
}
