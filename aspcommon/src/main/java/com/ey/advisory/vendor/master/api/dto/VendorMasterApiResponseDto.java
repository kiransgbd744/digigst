package com.ey.advisory.vendor.master.api.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VendorMasterApiResponseDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;

	@SerializedName("Status")
	@Expose
	@XmlElement(name = "status")
	private String status;

	@SerializedName("PayloadId")
	@Expose
	@XmlElement(name = "payloadId")
	private String payloadId;

	@SerializedName("Msg")
	@Expose
	@XmlElement(name = "msg")
	private String msg;

}
