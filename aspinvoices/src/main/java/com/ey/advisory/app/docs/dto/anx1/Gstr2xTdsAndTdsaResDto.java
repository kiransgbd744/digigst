package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr2xTdsAndTdsaResDto {

	@Expose
	@SerializedName("ctin")
	private String ctin;
	
	@Expose
	@SerializedName("amt_ded")
	private BigDecimal amtDed;
	
	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;
	
	@Expose
	@SerializedName("camt")
	private BigDecimal camt;
	
	@Expose
	@SerializedName("samt")
	private BigDecimal samt;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
	
	@Expose
	@SerializedName("flag")
	private String flag;
	
	@Expose
	@SerializedName("month")
	private String month;
	
	@Expose
	@SerializedName("omonth")
	private String omonth;
	}
