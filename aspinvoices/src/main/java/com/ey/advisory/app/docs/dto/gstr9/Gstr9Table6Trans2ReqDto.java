package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table6Trans2ReqDto {

	 @Expose
	 @SerializedName("camt")
	 private BigDecimal camt = new BigDecimal("0.00");
	
	 @Expose
	 @SerializedName("samt")
	 private BigDecimal samt = new BigDecimal("0.00");

}
