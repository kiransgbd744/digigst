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
public class Gstr7ReviewSummaryItemDto {                   
/*
private BigDecimal ProfitCentre1;
private BigDecimal ProfitCentre2;
private BigDecimal PlantCode;
private BigDecimal Division;
private BigDecimal Location;
private BigDecimal SalesOrg;
private BigDecimal PurchOrg;
private BigDecimal DistChannel;*/

	@XmlElement(name = "TableSection")
	private String tableSection ;
	@XmlElement(name = "AspCount")
	private Integer aspCount;
	@XmlElement(name = "AspTotalAmt")
	private BigDecimal aspTotalAmt = BigDecimal.ZERO;
	@XmlElement(name = "AspIgst")
	private BigDecimal aspIgst = BigDecimal.ZERO;
	@XmlElement(name = "AspCgst")
	private BigDecimal aspCgst = BigDecimal.ZERO;
	@XmlElement(name = "AspSgst")
	private BigDecimal aspSgst = BigDecimal.ZERO;
	@XmlElement(name = "GstnCount")
	private Integer gstnCount ;
	@XmlElement(name = "GstnTotalAmt")
	private BigDecimal gstnTotalAmt = BigDecimal.ZERO;
	@XmlElement(name = "GstnIgst")
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	@XmlElement(name = "GstnCgst")
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	@XmlElement(name = "GstnSgst")
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	@XmlElement(name = "DiffCount")
	private Integer diffCount;
	@XmlElement(name = "DiffTotalAmt")
	private BigDecimal diffTotalAmt = BigDecimal.ZERO;
	@XmlElement(name = "DiffIgst")
	private BigDecimal diffIgst = BigDecimal.ZERO;
	@XmlElement(name = "DiffCgst")
	private BigDecimal diffCgst = BigDecimal.ZERO;
	@XmlElement(name = "DiffSgst")
	private BigDecimal diffSgst = BigDecimal.ZERO;
	
}