package com.ey.advisory.common;

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
public class AuthenticationRespDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;

	@SerializedName("errorCode")
	@Expose
	@XmlElement(name = "error-code")
	private String errorCode;

	@SerializedName("errorMessage")
	@Expose
	@XmlElement(name = "error-message")
	private String errorMessage;

}
