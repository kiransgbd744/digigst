package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class EcoDetailsSupGstr3B {

	@Expose
	@SerializedName("eco_sup")
	private SupDetailsGstr3B ecoSup;

	@Expose
	@SerializedName("eco_reg_sup")
	private SupDetailsGstr3B ecoRegSup;

}
