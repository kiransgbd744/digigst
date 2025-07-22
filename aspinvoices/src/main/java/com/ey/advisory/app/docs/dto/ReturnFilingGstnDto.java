package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReturnFilingGstnDto {
    	 
	@Expose
	@SerializedName(value = "arn")
	private String arn;
	
	@Expose
	@SerializedName(value = "ret_prd")
	private String ret_prd;
	
	@Expose
	@SerializedName(value = "mof")
	private String mof;
	
	@Expose
	@SerializedName(value = "dof")
	private String dof;
	
	@Expose
	@SerializedName(value = "rtntype")
	private String rtntype;
	
	@Expose
	@SerializedName(value = "status")
	private String status;
	
	@Expose
	@SerializedName(value = "valid")
	private String valid;

}
