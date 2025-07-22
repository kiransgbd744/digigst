package com.ey.advisory.app.docs.dto.gstr6;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6CdnaDto {

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("cfs")
	private String cfs;

	@Expose
	@SerializedName("nt")
	private List<Gstr6CdnaNtData> nt;
	
	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	

}
