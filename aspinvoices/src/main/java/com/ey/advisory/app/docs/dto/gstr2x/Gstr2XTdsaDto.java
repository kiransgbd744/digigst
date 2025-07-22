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
public class Gstr2XTdsaDto {
	@Expose
	@SerializedName("ctin")
	private String ctin;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("month")
	private String month;

	@Expose
	@SerializedName("omonth")
	private String omonth;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("flag")
	private String flag;
	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;
}
