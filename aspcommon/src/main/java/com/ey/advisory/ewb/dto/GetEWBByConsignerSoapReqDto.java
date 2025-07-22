/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Arun KA
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetEWBByConsignerSoapReqDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	@XmlElement(name = "gstin")
	private String gstin;

	@SerializedName("docNo")
	@XmlElement(name = "doc-no")
	protected String docNo;

	@SerializedName("docType")
	@XmlElement(name = "doc-type")
	protected String docType;

	@XmlElement(name = "id-token")
	private String idToken;
}