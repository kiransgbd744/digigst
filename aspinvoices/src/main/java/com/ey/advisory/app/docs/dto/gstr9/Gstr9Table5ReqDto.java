package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table5ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("zero_rtd")
	private Gstr9Table5ZeroRtdReqDto table5ZeroRtdReqDto;

	@Expose
	@SerializedName("sez")
	private Gstr9Table5SezReqDto table5SezReqDto;

	@Expose
	@SerializedName("rchrg")
	private Gstr9Table5RchrgReqDto table5RchrgReqDto;
	
	@Expose
	@SerializedName("ecom_14")
	private Gstr9Table5RchrgReqDto table5C1RchrgReqDto;

	@Expose
	@SerializedName("exmt")
	private Gstr9Table5ExmtReqDto table5ExmtReqDto;

	@Expose
	@SerializedName("nil")
	private Gstr9Table5NilReqDto table5NilReqDto;

	@Expose
	@SerializedName("non_gst")
	private Gstr9Table5NonGstReqDto table5NonGstReqDto;

	@Expose
	@SerializedName("cr_nt")
	private Gstr9Table5CrNtReqDto table5CrNtReqDto;

	@Expose
	@SerializedName("dr_nt")
	private Gstr9Table5DbNtReqDto table5DbNtReqDto;

	@Expose
	@SerializedName("amd_pos")
	private Gstr9Table5AmdPosReqDto table5AmdPosReqDto;
	
	@Expose
	@SerializedName("amd_neg")
	private Gstr9Table5AmdNegReqDto table5AmdNegReqDto;

	@Expose
	@SerializedName("sub_totalAF")
	private Gstr9Table5SubTotalAfReqDto table5SubTotalAfReqDto;

	@Expose
	@SerializedName("sub_totalHK")
	private Gstr9Table5SubTotalHkReqDto table5SubTotalHkReqDto;
	
	@Expose
	@SerializedName("tover_tax_np")
	private Gstr9Table5ToverTaxNpReqDto table5ToverTaxNpReqDto;

	@Expose
	@SerializedName("total_tover")
	private Gstr9Table5TotalToverReqDto table5TotalToverReqDto;


}
