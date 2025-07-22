/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
public class Gstr1PdfProcResultDto {

	private String section;
	private String subSection;
	private String noOfRecords;
	private String taxableValue;
	private String igstAmt;
	private String cgstAmt;
	private String sgstAmt;
	private String cessAmt;

}
