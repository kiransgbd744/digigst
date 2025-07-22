/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BSetOffSavePdNlsDto {

	@Expose
	@SerializedName("trans_typ")
	private Long transType;

	@Expose
	@SerializedName("liab_ldg_id")
	private Long ledgId;

	@Expose
	@SerializedName("ipd")
	private BigDecimal pdIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cpd")
	private BigDecimal pdCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("spd")
	private BigDecimal pdSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cspd")
	private BigDecimal pdCess = BigDecimal.ZERO;

}