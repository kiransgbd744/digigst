
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ExportDetails implements Serializable {

	private static final long serialVersionUID = 3205403426879022226L;

	/**
	 * Shipping Bill No.
	 *
	 */
	@SerializedName("ShipBNo")
	@Expose
	private String shipBNo;
	/**
	 * Shipping Bill Date
	 *
	 */
	@SerializedName("ShipBDt")
	@Expose
	private LocalDate shipBDt;
	/**
	 * Port Code
	 *
	 */
	@SerializedName("Port")
	@Expose
	private String port;
	/**
	 * Options for supplier for refund. Y/N
	 *
	 */
	@SerializedName("RefClm")
	@Expose
	private String refClm;
	/**
	 * Additional Currency Code
	 *
	 */
	@SerializedName("ForCur")
	@Expose
	private String forCur;
	/**
	 * Country Code
	 *
	 */
	@SerializedName("CntCode")
	@Expose
	private String cntCode;

	@SerializedName("ExpDuty")
	@Expose
	private BigDecimal expDuty;
	
	public static boolean isEmpty(ExportDetails expDetails) {
		ExportDetails exportDetails = new ExportDetails();
		return expDetails.hashCode() == exportDetails.hashCode();
	}

}
