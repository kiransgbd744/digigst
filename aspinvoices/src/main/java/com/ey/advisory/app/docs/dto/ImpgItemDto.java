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
public class ImpgItemDto {

	@Expose
	@SerializedName("refdt")
	private String refDate;

	@Expose
	@SerializedName("portcd")
	private String portCode;

	@Expose
	@SerializedName("benum")
	private Long beNum;

	@Expose
	@SerializedName("bedt")
	private String beDate;

	@Expose
	@SerializedName("txval")
	private BigDecimal txValue;

	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;

	@Expose
	@SerializedName("amd")
	private String amd;

}
