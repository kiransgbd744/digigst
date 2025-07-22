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
public class AdditionalDocDetails {
	
	@Expose
	@SerializedName("supportingDocURL")
	@XmlElement(name = "support-doc-url")
	private String supportingDocURL;

	@Expose
	@SerializedName("supportingDocBase64")
	@XmlElement(name = "support-doc-b64")
	private String supportingDocBase64;
	
	@Expose
	@SerializedName("addlInfo")
	@XmlElement(name = "add-inf")
	protected String additionalInformation;
	
	public static boolean isEmpty(AdditionalDocDetails additionalDtls) {
		AdditionalDocDetails addDtls = new AdditionalDocDetails();
		return addDtls.hashCode() == additionalDtls.hashCode();
	}

}
