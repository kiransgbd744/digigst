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
public class Gstr3bReviewSummaryItemDto {

	@XmlElement(name = "ProfitCentre1")
	private String profitCentre1;

	@XmlElement(name = "ProfitCentre2")
	private String profitCentre2;

	@XmlElement(name = "PlantCode")
	private String plantCode;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "SalesOrg")
	private String salesOrg;

	@XmlElement(name = "PurchOrg")
	private String purchOrg;

	@XmlElement(name = "DistChannel")
	private String distChannel;

	@XmlElement(name = "TableName")
	private String tableName;
	
	@XmlElement(name = "TypeOfSupp")
	private String typeOfSupp;
	
	//private BigDecimal computedTaxableVal = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiTaxbleVal")
	private BigDecimal digiTaxbleVal  = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiIgst")
	private BigDecimal digiIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiCgst")
	private BigDecimal digiCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiSgst")
	private BigDecimal digiSgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiCess")
	private BigDecimal digiCess = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnCompTaxle")
	private BigDecimal gstnCompTaxle = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnCompIgst")
	private BigDecimal gstnCompIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnCompCgst")
	private BigDecimal gstnCompCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnCompSgst")
	private BigDecimal gstnCompSgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnCompCess")
	private BigDecimal gstnCompCess = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnTaxbleVal")
	private BigDecimal gstnTaxbleVal = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnIgst")
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnCgst")
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnSgst")
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GstnCess")
	private BigDecimal gstnCess = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffTaxbleVal")
	private BigDecimal diffTaxbleVal = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffIgst")
	private BigDecimal diffIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffCgst")
	private BigDecimal diffCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffSgst")
	private BigDecimal diffSgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffCess")
	private BigDecimal diffCess = BigDecimal.ZERO;
	
	
	@XmlElement(name = "DigiUiTbVal")
	private BigDecimal DigiUiTbVal = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiUiIgst")
	private BigDecimal DigiUiIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiUiCgst")
	private BigDecimal DigiUiCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiUiSgst")
	private BigDecimal DigiUiSgst = BigDecimal.ZERO;
	
	@XmlElement(name = "DigiUiCess")
	private BigDecimal DigiUiCess = BigDecimal.ZERO;
	

}