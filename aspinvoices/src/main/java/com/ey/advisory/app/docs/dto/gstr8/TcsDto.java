/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr8;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class TcsDto {
	
	@SerializedName("stin")
	@Expose
	private String stin;

	@SerializedName("supR")
	@Expose
	private BigDecimal supR;

	@SerializedName("retsupR")
	@Expose
	private BigDecimal retsupR;

	@SerializedName("supU")
	@Expose
	private BigDecimal supU;

	@SerializedName("retsupU")
	@Expose
	private BigDecimal retsupU;

	@SerializedName("amt")
	@Expose
	private BigDecimal amt;

	@SerializedName("camt")
	@Expose
	private BigDecimal camt;

	@SerializedName("samt")
	@Expose
	private BigDecimal samt;

	@SerializedName("iamt")
	@Expose
	private BigDecimal iamt;

	@SerializedName("flag")
	@Expose
	private String flag;
	
	@SerializedName("chksum")
	@Expose
	private String chksum;
	
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
