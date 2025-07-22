package com.ey.advisory.app.docs.dto.gstr7;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Gstr7TdsDto {

	@Expose
	@SerializedName("gstin_ded")
	private String gstin_ded;

	@Expose
	@SerializedName("amt_ded")
	private BigDecimal amt_ded;

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;

	@Expose
	@SerializedName("camt")
	private BigDecimal camt;

	@Expose
	@SerializedName("samt")
	private BigDecimal samt;

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;

	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	@Expose
	@SerializedName("deductee_name")
	private String deducteeName;

	@Expose
	@SerializedName("chksum")
	private String chksum;
	
	@Expose
	@SerializedName("inv")
	private List<Gstr7TdsTdsaInvDto> gstr7TdsTdsaInvDto;

}
