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
public class Gstr3bDifflibPopUpSummaryItemDto {

	@XmlElement(name = "Tab")
	private String tableName;
	
	@XmlElement(name = "TypeOfSupply")
	private String typeOfSupp;
	
	@XmlElement(name = "TaxableValue")
	private BigDecimal diffTaxbleVal = BigDecimal.ZERO;
	
	@XmlElement(name = "Igst")
	private BigDecimal diffIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "Cgst")
	private BigDecimal diffCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "Sgst")
	private BigDecimal diffSgst = BigDecimal.ZERO;
	
	@XmlElement(name = "Cess")
	private BigDecimal diffCess = BigDecimal.ZERO;
}