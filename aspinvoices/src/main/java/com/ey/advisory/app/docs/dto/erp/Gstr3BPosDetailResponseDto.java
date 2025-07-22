package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class Gstr3BPosDetailResponseDto {

	@XmlElement(name = "MANDT")
	private String mandt;
	
	@XmlElement(name = "ENTITY")
	private String entity;
	
	@XmlElement(name = "RET_PER")
	private String retPeriod;
	
	@XmlElement(name = "GSTIN_NUM")
	private String gstinNum;
	
	@XmlElement(name = "TABLE_NAME")
	private String tableName;
	
	@XmlElement(name = "TABLE_STYPE")
	private String tableSubType;
	
	@XmlElement(name = "STATE")
	private String state;
	
	@XmlElement(name = "AMOUNT1")
	private BigDecimal amount1 = BigDecimal.ZERO;
	
	@XmlElement(name = "AMOUNT2")
	private BigDecimal amount2  = BigDecimal.ZERO;
	
    @XmlElement(name = "COMPANY_CODE")
	private String companyCode;
	
}
