package com.ey.advisory.app.gstr2jsonupload;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CdnAItemDetails {
	
	@SerializedName("rt")
	@Expose
	public BigDecimal rt;
	
	@SerializedName("txval")
	@Expose
	public BigDecimal txval;
	
	@SerializedName("iamt")
	@Expose
	public BigDecimal iamt;
	
	@SerializedName("camt")
	@Expose
	public BigDecimal camt;
	
	@SerializedName("samt")
	@Expose
	public BigDecimal samt;
	
	@SerializedName("csamt")
	@Expose
	public BigDecimal Csamt;
	
}
