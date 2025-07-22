package com.ey.advisory.vendor.master.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An Outward Document represents a concrete Financial Document like an Invoice
 * or a credit note.
 * 
 * @author Shashikant.Shukla
 *
 */

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VendorMasterApiReqDocument {
	@Expose
	@SerializedName("idToken")
	@XmlElement(name = "id-token")
	private String idTokens;

	@Expose
	@SerializedName("checkSum")
	private String checkSum;

	@Expose
	@SerializedName("payloadId")
	@XmlElement(name = "payload-id")
	private String payloadId;

	@Expose
	@SerializedName("companyCode")
	@XmlElement(name = "company-code")
	private String companyCode;

	@Expose
	@SerializedName("sourceId")
	@XmlElement(name = "source-id")
	private String sourceId;

	@Expose
	@SerializedName("pushType")
	private String pushType;
	
	@Expose
	@SerializedName("jsonString")
	private String jsonString;
}
