/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr9InOutwardVerticalDto {

	private String gstin;
	private String fy;
	private String tableNo;
	private String natureOfSup;
	private String taxableVal;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String interst;
	private String lateFee;
	private String penalty;
	private String others;
	private String aspInformationID;
	private String aspInformationDescription;
	private String aspErrorCode;
	private String aspErrorDescription;
}
