package com.ey.advisory.company.code.map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CompanyCodeMapItemRespDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("erpId")
	private Long erpId;

	@Expose
	@SerializedName("sourceId")
	private String sourceId;

	@Expose
	@SerializedName("companyCode")
	private String companyCode;

	@Expose
	@SerializedName("status")
	private boolean status;

}
