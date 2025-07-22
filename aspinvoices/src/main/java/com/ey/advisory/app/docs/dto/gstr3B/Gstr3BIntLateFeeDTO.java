package com.ey.advisory.app.docs.dto.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr3BIntLateFeeDTO {

	@Expose
	@SerializedName("intr_details")
	private Gstr3BSecDetailsDTO intrDetails;
	
	@Expose
	@SerializedName("ltfee_details")
	private Gstr3BSecDetailsDTO lateFeeDetails;
}
