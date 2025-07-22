package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table15ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("rfd_clmd")
	private Gstr9Table15RfdClmdReqDto gstr9Table15RfdClmdReqDto;

	@Expose
	@SerializedName("rfd_sanc")
	private Gstr9Table15RfdSancReqDto gstr9Table15RfdSancReqDto;

	@Expose
	@SerializedName("rfd_rejt")
	private Gstr9Table15RfdRejtReqDto gstr9Table15RfdRejtReqDto;

	@Expose
	@SerializedName("rfd_pend")
	private Gstr9Table15RfdPendReqDto gstr9Table15RfdPendReqDto;

	@Expose
	@SerializedName("tax_dmnd")
	private Gstr9Table15TaxDmndtReqDto gstr9Table15TaxDmndtReqDto;

	@Expose
	@SerializedName("tax_paid")
	private Gstr9Table15TaxPaidReqDto gstr9Table15TaxPaidReqDto;

	@Expose
	@SerializedName("dmnd_pend")
	private Gstr9Table15TotalDmndPendReqDto gstr9Table15TotalDmndPendReqDto;

}
