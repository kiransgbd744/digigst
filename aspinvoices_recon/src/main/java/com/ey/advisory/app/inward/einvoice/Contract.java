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
public class Contract {

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

	@XmlElement(name = "RECEIPTADVICEREF")
	private String receiptAdviceReference;

	@XmlElement(name = "RECEIPTADVICEDT")
	private String receiptAdviceDate;

	@XmlElement(name = "TENDERREFERENCE")
	private String tenderReference;

	@XmlElement(name = "CONTRACTREFERENC")
	private String contractReference;

	@XmlElement(name = "EXTERNALREFERENC")
	private String externalReference;

	@XmlElement(name = "PROJECTREFERENCE")
	private String projectReference;

	@XmlElement(name = "CUSTOMERPOREFNO")
	private String customerPOReferenceNumber;

	@XmlElement(name = "CUSTOMERPOREFDT")
	private String customerPOReferenceDate;

}





