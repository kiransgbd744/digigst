/**
 * 
 */
package com.ey.advisory.app.data.business.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun.KA
 *
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PayloadRespDto {

	@Expose
	@SerializedName("reqBody")
	@XmlElement(name = "req-body")
	private String reqBody;

	@Expose
	@SerializedName("nicReqPayload")
	@XmlElement(name = "nicreq-pay")
	private String nicReqPayload;

	@Expose
	@SerializedName("nicRespPayload")
	@XmlElement(name = "nicresp-pay")
	private String nicRespPayload;

	@Expose
	@SerializedName("cloudTimeStamp")
	@XmlElement(name = "cloud-tmstp")
	private String cloudTimeStamp;

	@Expose
	@SerializedName("apiType")
	@XmlElement(name = "api-type")
	private String apiType;

	@Expose
	@SerializedName("id")
	@XmlElement(name = "id")
	private Long id;

	@SerializedName("errorMessage")
	@Expose
	@XmlElement(name = "err-msg")
	private String errorMessage;
	

	@SerializedName("errorCode")
	@Expose
	@XmlElement(name = "err-code")
	private String errCode;
}
