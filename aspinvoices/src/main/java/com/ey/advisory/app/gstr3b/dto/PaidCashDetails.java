package com.ey.advisory.app.gstr3b.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaidCashDetails {

	@SerializedName("liab_ldg_id")
	@Expose
	private String liabledgerId;

	@SerializedName("trans_typ")
	@Expose
	private String transType;

	@SerializedName("ipd")
	@Expose
	private BigDecimal igstPaid = BigDecimal.ZERO;

	@SerializedName("cpd")
	@Expose
	private BigDecimal cgstPaid = BigDecimal.ZERO;

	@SerializedName("spd")
	@Expose
	private BigDecimal sgstPaid = BigDecimal.ZERO;

	@SerializedName("cspd")
	@Expose
	private BigDecimal cessPaid = BigDecimal.ZERO;

	@SerializedName("i_intrpd")
	@Expose
	private BigDecimal igstIntPaid = BigDecimal.ZERO;

	@SerializedName("c_intrpd")
	@Expose
	private BigDecimal cgstIntPaid = BigDecimal.ZERO;

	@SerializedName("s_intrpd")
	@Expose
	private BigDecimal sgstIntPaid = BigDecimal.ZERO;

	@SerializedName("cs_intrpd")
	@Expose
	private BigDecimal cessIntPaid = BigDecimal.ZERO;

	@SerializedName("c_lfeepd")
	@Expose
	private BigDecimal cgstLateFeePaid = BigDecimal.ZERO;

	@SerializedName("s_lfeepd")
	@Expose
	private BigDecimal sgstLateFeePaid = BigDecimal.ZERO;
	
	@SerializedName("cs_lfeepd")
	@Expose
	private BigDecimal cessLateFeePaid = BigDecimal.ZERO;
	
	@SerializedName("i_lfeepd")
	@Expose
	private BigDecimal igstLateFeePaid = BigDecimal.ZERO;

}
