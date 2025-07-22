package com.ey.advisory.app.gstr2jsonupload;

import java.math.BigDecimal;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class B2bAItemDetails {
	
	@SerializedName("csamt")
	@Expose
	public BigDecimal csamt;
	
	@SerializedName("samt")
	@Expose
	public BigDecimal samt;
	
	@SerializedName("rt")
	@Expose
	public BigDecimal rt;
	
	@SerializedName("txval")
	@Expose
	public BigDecimal txval;
	
	@SerializedName("camt")
	@Expose
	public BigDecimal camt;
	
	@SerializedName("iamt")
	@Expose
	public BigDecimal iamt;

}
