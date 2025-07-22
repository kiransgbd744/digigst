package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr3BDashboardSummaryDto {
	
	@XmlElement(name = "MANDT")
	private String mandt;
	
	@XmlElement(name = "ENTITY")
	private String entity;
	
	@XmlElement(name = "RET_PER")
	private String retPeriod;
	
	@XmlElement(name = "GSTIN_NUM")
	private String gstinNum;
	
	@XmlElement(name = "ENTITY_NAME")
	private String entityName;
	
	@XmlElement(name = "STATE")
	private String state;
		
	@XmlElement(name = "SAVE_STATUS")
	private String saveStatus;
	
	@XmlElement(name = "STATUS_DATE")
	private String statusDate;
	
	@XmlElement(name = "T_LIABILITY")
	private BigDecimal tLiability = BigDecimal.ZERO;
	
	@XmlElement(name = "T_ITC")
	private BigDecimal tItc = BigDecimal.ZERO;
	
	@XmlElement(name = "N_LIABILITY")
	private BigDecimal nLiability = BigDecimal.ZERO;
	
	@XmlElement(name = "AS_ON")
	private String asOn;
	
	@XmlElement(name = "COMPANY_CODE")
	private String companyCode;
	
}
