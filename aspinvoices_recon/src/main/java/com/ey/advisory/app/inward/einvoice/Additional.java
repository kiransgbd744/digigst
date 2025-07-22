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
@XmlRootElement(name = "ADDITIONAL")
public class Additional {

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

	@XmlElement(name = "SUPPORTINGDOCURL")
	private String supportingDocURL;

	@XmlElement(name = "SUPPORTINGDOC")
	private String supportingDocument;

	@XmlElement(name = "ADDNFORMATION")
	private String additionalInformation;

}
