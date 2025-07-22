/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item")
public class Attribute {

	@XmlElement(name = "DOCUMENTTYPE")
	private String documentType;

	@XmlElement(name = "SUPPLIERGSTIN")
	private String supplierGSTIN;

	@XmlElement(name = "DOCUMENTNUMBER")
	private String documentNumber;

	@XmlElement(name = "DOCUMENTDATE")
	private String documentDate;

	@XmlElement(name = "IRN")
	private String IRN;

	@XmlElement(name = "LINENUMBER")
	private String lineNumber;

	@XmlElement(name = "ATTRIBUTENAME")
	private String attributeName;

	@XmlElement(name = "ATTRIBUTEVALUE")
	private String attributeValue;

}





