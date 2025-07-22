package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InwardEinvoiceJsonRespDto {
	
	@Expose
	@SerializedName("data")
	private List<InwardEinvoiceJsonItemRespDto> data;
	
	/*@Expose
	@SerializedName("requestDate")
	private LocalDateTime requestDate;*/


}
