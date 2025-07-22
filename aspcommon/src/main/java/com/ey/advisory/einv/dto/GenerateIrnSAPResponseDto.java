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
public class GenerateIrnSAPResponseDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;

	@SerializedName("ackNo")
	@Expose
	@XmlElement(name = "ack-no")
	private String ackNo;

	@SerializedName("ackDt")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "ack-dt")
	private LocalDateTime ackDt;

	@SerializedName("irn")
	@Expose
	@XmlElement(name = "irn")
	private String irn;

	@SerializedName("docType")
	@Expose
	@XmlElement(name = "doc-type")
	private String docType;

	@SerializedName("docNum")
	@Expose
	@XmlElement(name = "doc-num")
	private String docNum;

	@SerializedName("docDate")
	@Expose
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	@XmlElement(name = "doc-date")
	private LocalDate docDate;

	@SerializedName("sellerGstin")
	@Expose
	@XmlElement(name = "seller-gstin")
	private String sellerGstin;

	@SerializedName("buyerGstin")
	@Expose
	@XmlElement(name = "buyer-gstin")
	private String buyerGstin;

	@SerializedName("signedInvoice")
	@Expose
	@XmlElement(name = "signed-inv")
	private String signedInvoice;

	@SerializedName("signedQRCode")
	@Expose
	@XmlElement(name = "signed-qrcd")
	private String signedQRCode;

	@SerializedName("formattedQrCode")
	@Expose
	@XmlElement(name = "format-qrcd")
	private String formattedQrCode;

	@SerializedName("qrData")
	@Expose
	@XmlElement(name = "qr-date")
	private String qrData;

	@SerializedName("status")
	@Expose
	@XmlElement(name = "status")
	private String status;

	@SerializedName("ewbNo")
	@Expose
	@XmlElement(name = "ewb-no")
	public String ewbNo;

	@SerializedName("ewbDt")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "ewb-dt")
	public LocalDateTime ewbDt;

	@SerializedName("ewbValidTill")
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
