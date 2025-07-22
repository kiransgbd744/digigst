/**
 * 
 */
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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class B2bItemDetails {
	
	@SerializedName("csamt")
	@Expose
	public BigDecimal csamt;
	
	@SerializedName("rt")
	@Expose
	public BigDecimal rt;
	
	@SerializedName("txval")
	@Expose
	public BigDecimal txval;
	
	@SerializedName("iamt")
	@Expose
	public BigDecimal iamt;
	
	@SerializedName("samt")
	@Expose
	public BigDecimal samt;
	
	@SerializedName("camt")
	@Expose
	public BigDecimal camt;

}
