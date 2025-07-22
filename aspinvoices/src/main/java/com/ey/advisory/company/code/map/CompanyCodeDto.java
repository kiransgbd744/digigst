package com.ey.advisory.company.code.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CompanyCodeDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("companyCode")
	private String companyCode;
}
