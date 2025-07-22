package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.LocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateIrnResponseDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;

	@SerializedName("AckNo")
	@Expose
	@XmlElement(name = "ack-no")
	private String ackNo;

	@SerializedName("AckDt")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "ack-dt")
	private LocalDateTime ackDt;

	@SerializedName("Irn")
	@Expose
	@XmlElement(name = "irn")
	private String irn;

	@SerializedName("DocType")
	@Expose
	@XmlElement(name = "doc-type")
	private String docType;

	@SerializedName("DocNum")
	@Expose
	@XmlElement(name = "doc-num")
	private String docNum;

	@SerializedName("DocDate")
	@Expose
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	@XmlElement(name = "doc-date")
	private LocalDate docDate;

	@SerializedName("SellerGstin")
	@Expose
	@XmlElement(name = "seller-gstin")
	private String sellerGstin;

	@SerializedName("BuyerGstin")
	@Expose
	@XmlElement(name = "buyer-gstin")
	private String buyerGstin;

	@SerializedName("SignedInvoice")
	@Expose
	@XmlElement(name = "signed-inv")
	private String signedInvoice;

	@SerializedName("SignedQRCode")
	@Expose
	@XmlElement(name = "signed-qrcd")
	private String signedQRCode;

	@SerializedName("FormattedQrCode")
	@Expose
	@XmlElement(name = "format-qrcd")
	private String formattedQrCode;

	@SerializedName("QrData")
	@Expose
	@XmlElement(name = "qr-date")
	private String qrData;

	@SerializedName("Status")
	@Expose
	@XmlElement(name = "status")
	private String status;

	@SerializedName("EwbNo")
	@Expose
	@XmlElement(name = "ewb-no")
	public String ewbNo;

	@SerializedName("EwbDt")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "ewb-dt")
	public LocalDateTime ewbDt;

	@SerializedName("EwbValidTill")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "ewb-valid-dt")
	public LocalDateTime ewbValidTill;

	@SerializedName("nicDistance")
	@Expose
	@XmlElement(name = "nic-dist")
	private String nicDistance;

	@SerializedName("errorCode")
	@Expose
	@XmlElement(name = "err-cd")
	private String errorCode;

	@SerializedName("succMsg")
	@Expose
	@XmlElement(name = "succ-msg")
	private String succMsg;

	@SerializedName("errorMessage")
	@Expose
	@XmlElement(name = "err-msg")
	private String errorMessage;

	@SerializedName("infoErrorCode")
	@Expose
	@XmlElement(name = "info-err-cd")
	private String infoErrorCode;

	@SerializedName("infoErrorMessage")
	@Expose
	@XmlElement(name = "info-err-msg")
	private String infoErrorMessage;

}
