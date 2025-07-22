/**
 * 
 */
package com.ey.advisory.core.dto;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class EInvoiceMasterSaveDto {

	@Expose
	@SerializedName("irn")
	protected String irn;

	@Expose
	@SerializedName("signedInvoice")
	protected String signedInvoice;

	@Expose
	@SerializedName("signedQRCode")
	protected String signedQRCode;

	@Expose
	@SerializedName("ackNo")
	protected String ackNo;

	@Expose
	@SerializedName("ackDt")
	protected LocalDateTime ackDate;

	@Expose
	@SerializedName("status")
	protected String status;

	/*
	 * @Expose
	 * 
	 * @SerializedName("qrCode") protected String qrCode;
	 */

	@Expose
	@SerializedName("formattedQrCode")
	protected String formattedQRCode;

	@Expose
	@SerializedName("cancelDate")
	protected LocalDateTime cancelDate;

	@Expose
	@SerializedName("cancelReason")
	protected String cancelReason;

	@Expose
	@SerializedName("cancelRemarks")
	protected String cancelRemarks;

}
