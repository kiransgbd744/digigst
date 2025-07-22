/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Siva Reddy
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetEInvDetailsSoapReqDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	@XmlElement(name = "gstin")
	private String gstin;

	@SerializedName("irn")
	@Expose
	@XmlElement(name = "irn-No")
	private String irn;


	@XmlElement(name = "id-token")
	private String idToken;
}