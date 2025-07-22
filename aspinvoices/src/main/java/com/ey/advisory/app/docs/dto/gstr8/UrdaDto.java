package com.ey.advisory.app.docs.dto.gstr8;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
public class UrdaDto {
	@SerializedName("ofp")
	@Expose
	private String ofp;

	@SerializedName("eid")
	@Expose
	private String eid;

	@SerializedName("oeid")
	@Expose
	private String oeid;

	@SerializedName("grsval")
	@Expose
	private BigDecimal grsval;

	@SerializedName("supret")
	@Expose
	private BigDecimal supret;

	@SerializedName("amt")
	@Expose
	private BigDecimal amt;

	@SerializedName("flag")
	@Expose
	private String flag;
	
	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	@Expose
	@SerializedName("error_msg")
	private String error_msg;
}
