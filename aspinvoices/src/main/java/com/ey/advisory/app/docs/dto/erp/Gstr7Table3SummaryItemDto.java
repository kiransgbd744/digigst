/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author siva
 *
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class Gstr7Table3SummaryItemDto {                   

	@XmlElement(name = "OrgGstn")
	private String gstin;
	@XmlElement(name = "AmtPaid")
	private BigDecimal amountPaid = BigDecimal.ZERO;
	@XmlElement(name = "Igst")
	private BigDecimal igst = BigDecimal.ZERO;
	@XmlElement(name = "Cgst")
	private BigDecimal cgst = BigDecimal.ZERO;
	@XmlElement(name = "Sgst")
	private BigDecimal sgst = BigDecimal.ZERO;
	
}