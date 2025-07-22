package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DashboardGstrReturnStatusDto {
	
	@Expose
	@SerializedName("anx1T")
	private int anx1T;
	
	@Expose
	@SerializedName("anx1S")
	private int anx1S;
	
	@Expose
	@SerializedName("anx2T")
	private int anx2T;
	
	@Expose
	@SerializedName("anx2S")
	private int anx2S;
	
	@Expose
	@SerializedName("ret1T")
	private int ret1T;
	
	@Expose
	@SerializedName("ret1F")
	private int ret1F;
	
	@Expose
	@SerializedName("gstr6T")
	private int gstr6T;
	
	@Expose
	@SerializedName("gstr6F")
	private int gstr6F;
	
	@Expose
	@SerializedName("gstr7T")
	private int gstr7T;
	
	@Expose
	@SerializedName("gstr7F")
	private int gstr7F;
	
	@Expose
	@SerializedName("gstr8T")
	private int gstr8T;
	
	@Expose
	@SerializedName("gstr8F")
	private int gstr8F;
	
	@Expose
	@SerializedName("anx1AT")
	private int anx1AT;
	
	@Expose
	@SerializedName("anx1AA")
	private int anx1AA;
	
	@Expose
	@SerializedName("ret1AT")
	private int ret1AT;
	
	@Expose
	@SerializedName("ret1AA")
	private int ret1AA;

}
