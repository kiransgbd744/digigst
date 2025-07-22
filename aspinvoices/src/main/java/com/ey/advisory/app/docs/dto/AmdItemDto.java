package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class AmdItemDto {

	@Expose
	@SerializedName("sgstin")
	private String sgstin;

	@Expose
	@SerializedName("tdname")
	private String tdName;

	@Expose
	@SerializedName("refdt")
	private String refDate;

	@Expose
	@SerializedName("txval")
	private BigDecimal taxValue;

	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;

}
