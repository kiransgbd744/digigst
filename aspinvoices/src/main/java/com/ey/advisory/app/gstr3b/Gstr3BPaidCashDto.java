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
public class Gstr3BPaidCashDto {

	@Expose
	@SerializedName("trans_typ")
	private Long transType;

	@Expose
	@SerializedName("liab_ldg_id")
	private Long ledgId;

	@Expose
	@SerializedName("ipd")
	private BigDecimal pdIgst;

	@Expose
	@SerializedName("cpd")
	private BigDecimal pdCgst;

	@Expose
	@SerializedName("spd")
	private BigDecimal pdSgst;

	@Expose
	@SerializedName("cspd")
	private BigDecimal pdCess;

	@Expose
	@SerializedName("i_intrpd")
	private BigDecimal intrIgst;

	@Expose
	@SerializedName("c_intrpd")
	private BigDecimal intrCgst;

	@Expose
	@SerializedName("s_intrpd")
	private BigDecimal intrSgst;

	@Expose
	@SerializedName("cs_intrpd")
	private BigDecimal intrCess;

	@Expose
	@SerializedName("c_lfeepd")
	private BigDecimal lateFeeCgst;

	@Expose
	@SerializedName("s_lfeepd")
	private BigDecimal lateFeeSgst;

}
