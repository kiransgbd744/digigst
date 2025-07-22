package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table4ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("b2c")
	private Gstr9Table4B2CReqDto table4B2CReqDto;

	@Expose
	@SerializedName("b2b")
	private Gstr9Table4B2BReqDto table4B2BReqDto;

	@Expose
	@SerializedName("exp")
	private Gstr9Table4ExpReqDto table4ExpReqDto;

	@Expose
	@SerializedName("sez")
	private Gstr9Table4SezReqDto table4SezReqDto;

	@Expose
	@SerializedName("deemed")
	private Gstr9Table4DeemedReqDto table4DeemedReqDto;

	@Expose
	@SerializedName("at")
	private Gstr9Table4AtReqDto table4AtReqDto;

	@Expose
	@SerializedName("rchrg")
	private Gstr9Table4RchrgReqDto table4RchrgReqDto;
	
	@Expose
	@SerializedName("ecom")
	private Gstr9Table4RchrgReqDto table4RchrgG1ReqDto;

	@Expose
	@SerializedName("cr_nt")
	private Gstr9Table4CrNtReqDto table4CrntReqDto;

	@Expose
	@SerializedName("dr_nt")
	private Gstr9Table4DrNtReqDto table4drntReqDto;

	@Expose
	@SerializedName("amd_pos")
	private Gstr9Table4AmdPosReqDto table4AmdPosReqDto;

	@Expose
	@SerializedName("amd_neg")
	private Gstr9Table4AmdNegReqDto table4AmdNegReqDto;

	@Expose
	@SerializedName("sub_totalAG")
	private Gstr9Table4SubTotalAGReqDto table4SubTotalAGReqDto;

	@Expose
	@SerializedName("sub_totalIL")
	private Gstr9Table4SubTotalILReqDto table4SubTotalILReqDto;

	@Expose
	@SerializedName("sup_adv")
	private Gstr9Table4SubAdvReqDto table4SubAdvReqDto;

}
