/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PayloadMetaDataItemDto {
	
	@XmlElement(name="CUSTOMERGSTIN")
	private String customerGSTIN;

	@XmlElement(name="SUPPLIERGSTIN")
	private String supplierGSTIN;
	
	@XmlElement(name="DOCUMENTTYPE")
	private String documentType;
	
	@XmlElement(name="DOCUMENTNUMBER")
	private String documentNumber;
	
	@XmlElement(name="DOCUMENTDATE")
	private String documentDate;
	
	@XmlElement(name="FISCALYEAR")
	private String fiscalYear;
	
	@XmlElement(name="PAYREFERENCENO")
	private String payReferenceNo ;
	
	@XmlElement(name="PAYREFERENCEDATE")
	private String payReferenceDate ;
	
	@XmlElement(name="ERRORDETAILS")
	private PayloadMetaDataErrorItemDto errorDetails;


}
