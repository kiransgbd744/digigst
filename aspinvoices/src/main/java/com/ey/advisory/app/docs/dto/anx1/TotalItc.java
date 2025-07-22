package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class TotalItc {
	@Expose
	@SerializedName("iamti")
	private BigDecimal iamti = BigDecimal.ZERO;
	@Expose
	@SerializedName("iamtc")
	private BigDecimal iamtc = BigDecimal.ZERO;
	@Expose
	@SerializedName("iamts")
	private BigDecimal iamts = BigDecimal.ZERO;
	@Expose
	@SerializedName("camti")
	private BigDecimal camti = BigDecimal.ZERO;
	@Expose
	@SerializedName("camtc")
	private BigDecimal camtc = BigDecimal.ZERO;
	@Expose
	@SerializedName("samti")
	private BigDecimal samti = BigDecimal.ZERO;
	@Expose
	@SerializedName("samts")
	private BigDecimal samts = BigDecimal.ZERO;
	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;
	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt = BigDecimal.ZERO;
	@Expose
	@SerializedName("camt")
	private BigDecimal camt = BigDecimal.ZERO;
	@Expose
	@SerializedName("samt")
	private BigDecimal samt = BigDecimal.ZERO;
	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt = BigDecimal.ZERO;

}
