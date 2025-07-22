package com.ey.advisory.app.inward.einvoice;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class InwardEinvoiceStatusScreenResponseDto {

	@Expose
	public String gstin;

	@Expose
	public String state;

	@Expose
	public String regType;

	@Expose
	public String authToken;

	@Expose
	public String status;

	@Expose
	public String overallTimeStamp;

	@Expose
	public String b2bTimeStamp;

	@Expose
	public String b2bStatus;

	@Expose
	public String sezwpTimeStamp;

	@Expose
	public String sezwpStatus;

	@Expose
	public String sezwopTimeStamp;

	@Expose
	public String sezwopStatus;

	@Expose
	public String dexpTimeStamp;

	@Expose
	public String dexpStatus;

	@Expose
	public String expwpTimeStamp;

	@Expose
	public String expwpStatus;

	@Expose
	public String expwopTimeStamp;

	@Expose
	public String expwopStatus;

}
