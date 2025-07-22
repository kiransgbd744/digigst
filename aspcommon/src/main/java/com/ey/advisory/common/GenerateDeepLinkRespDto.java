package com.ey.advisory.common;

import java.io.Serializable;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

/**
 * @author Siva Reddy
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateDeepLinkRespDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("docNo")
	@XmlElement(name = "doc-no")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@XmlElement(name = "doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate docDate;

	@Expose
	@SerializedName("docType")
	@XmlElement(name = "doc-type")
	protected String docType;
	
	@Expose
	@SerializedName("suppGstin")
	@XmlElement(name = "sgstin")
	protected String sgstin;
	
	@Expose
	@SerializedName("pan")
	@XmlElement(name = "pan")
	protected String pan;
	
	@Expose
	@SerializedName("url")
	private String qrUrl;
	
	
	@Expose
	@SerializedName("errorCode")
	@XmlElement(name = "error-code")
	private String errorCode;
	
	@Expose
	@SerializedName("errorMessage")
	@XmlElement(name = "error-message")
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
