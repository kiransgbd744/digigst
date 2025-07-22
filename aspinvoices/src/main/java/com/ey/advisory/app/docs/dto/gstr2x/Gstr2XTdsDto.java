package com.ey.advisory.app.docs.dto.gstr2x;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Gstr2XTdsDto {
	@Expose
	@SerializedName("ctin")
	private String ctin;

	@Expose
	@SerializedName("month")
	private String month;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("flag")
	private String flag;

	//V2.0
	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("idt")
	private String idt;
	//
	
	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;
}
