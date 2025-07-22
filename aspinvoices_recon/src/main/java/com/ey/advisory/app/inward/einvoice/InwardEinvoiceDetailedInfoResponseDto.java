package com.ey.advisory.app.inward.einvoice;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class InwardEinvoiceDetailedInfoResponseDto {

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
	public String completedOn;

	@Expose
	public String mode;

	@Expose
	public String requestedBy;
}
