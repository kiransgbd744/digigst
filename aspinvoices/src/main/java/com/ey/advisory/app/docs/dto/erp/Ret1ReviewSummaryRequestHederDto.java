/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Umesha M
 *
 */
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Ret1ReviewSummaryRequestHederDto {

	@XmlElement(name="Entity")
	private String entity ;
	
	@XmlElement(name = "Bukrs")
	private String companyCode;
	
	@XmlElement(name="RetPer")
	private String retPer;
	
	@XmlElement(name="GstinNum")
	private String gstinNum;
	
	@XmlElement(name = "EntityId")
	private String entityPan;
	
	@XmlElement(name = "EyStatus")
	private String eyStatus;
	
	@XmlElement(name="EyDate")
	private String eyDate;
	
	@XmlElement(name="EyTime")
	private String eyTime;
	
	@XmlElement(name="TotTaxLiability")
	private BigDecimal totTaxLiability = BigDecimal.ZERO;
	
	@XmlElement(name="RevCharge")
	private BigDecimal revCharge = BigDecimal.ZERO;

	@XmlElement(name = "OthRevCharge")
	private BigDecimal othRevCharge = BigDecimal.ZERO;
	
	@XmlElement(name = "NetAvlItc")
	private BigDecimal netAvlItc = BigDecimal.ZERO;
	
	@XmlElement(name = "Tds")
	private BigDecimal tds = BigDecimal.ZERO;

	@XmlElement(name = "Tcs")
	private BigDecimal tcs = BigDecimal.ZERO;
	
	@XmlElement(name = "Diff")
	private BigDecimal diff = BigDecimal.ZERO;
	
	@XmlElement(name="ItemData")
	private List<Ret1ReviewSummaryRequestItemDto> itemDto;
	
}