package com.ey.advisory.app.vendor.service;

import lombok.Data;
/**
 * 
 * @author vishal.verma
 *
 */

@Data

public class VendorProcRespDto {

	private String gstin;

	private String fy;

	private String retType;

	private String noOffiledRetured;

	private String totalNoOfReturnsCanBeFiled;

	private String complainceScore;
	
	private String itcMatchingScore;

}
