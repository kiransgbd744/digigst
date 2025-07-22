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
public class Gstr7Table4SummaryItemDto {                   

	@XmlElement(name = "Omonth")
	private String month ;
	@XmlElement(name = "OrgGstn")
	private String originalgstin ;
	@XmlElement(name = "AmtPaid")
	private BigDecimal orgAmountPaid = BigDecimal.ZERO;
	@XmlElement(name = "RGstn")
	private String revisedgstin ;
	@XmlElement(name = "RAmtPaid")
	private BigDecimal revAmountPaid = BigDecimal.ZERO;
	@XmlElement(name = "RIgst")
	private BigDecimal igst = BigDecimal.ZERO;
	@XmlElement(name = "RCgst")
	private BigDecimal cgst = BigDecimal.ZERO;
	@XmlElement(name = "RSgst")
	private BigDecimal sgst = BigDecimal.ZERO;
	
}