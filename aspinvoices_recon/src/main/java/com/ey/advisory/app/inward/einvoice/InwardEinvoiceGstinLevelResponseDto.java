package com.ey.advisory.app.inward.einvoice;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class InwardEinvoiceGstinLevelResponseDto {

	@Expose
	@SerializedName(value = "Invoice")
	public List<GstinLevelInnerDto> invoice = new ArrayList<>();

	@Expose
	@SerializedName(value = "Credit")
	public List<GstinLevelInnerDto> credit = new ArrayList<>();

	@Expose
	@SerializedName(value = "Debit")
	public List<GstinLevelInnerDto> debit = new ArrayList<>();
	
}
