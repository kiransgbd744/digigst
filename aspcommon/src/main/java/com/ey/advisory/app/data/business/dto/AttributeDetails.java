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
public class AttributeDetails {
	
	@Expose
	@SerializedName("attributeName")
	@XmlElement(name = "attr-nm")
	protected String attributeName;

	@Expose
	@SerializedName("attributeValue")
	@XmlElement(name = "attr-val")
	protected String attributeValue;
	
	public static boolean isEmpty(AttributeDetails attributeDtls) {
		AttributeDetails attrDtls = new AttributeDetails();
		return attrDtls.hashCode() == attributeDtls.hashCode();
	}


}
