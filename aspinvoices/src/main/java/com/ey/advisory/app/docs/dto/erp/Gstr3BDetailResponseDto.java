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
public class Gstr3BDetailResponseDto {
	
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
	
	@XmlElement(name = "ENTITY_NAME")
	private String entityName;
	
	@XmlElement(name = "STATE")
	private String state;
	
	@XmlElement(name = "A_C_TAX_VAL")
	private BigDecimal computeTax = BigDecimal.ZERO;
	
	@XmlElement(name = "A_C_IGST")
	private BigDecimal computeIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "A_C_CGST")
	private BigDecimal computeCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "A_C_SGST")
	private BigDecimal computeSgst =  BigDecimal.ZERO;
	
	@XmlElement(name = "A_C_CESS")
	private BigDecimal  compute = BigDecimal.ZERO;
	

	@XmlElement(name = "A_U_TAX_VAL")
	private BigDecimal userComputeTax = BigDecimal.ZERO;
	
	@XmlElement(name = "A_U_IGST")
	private BigDecimal userComputeIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "A_U_CGST")
	private BigDecimal userComputeCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "A_U_SGST")
	private BigDecimal userComputeSgst =  BigDecimal.ZERO;
	
	@XmlElement(name = "A_U_CESS")
	private BigDecimal  userComputeCess = BigDecimal.ZERO;
	
	@XmlElement(name = "GSTIN_TAX_VAL")
	private BigDecimal  gstinTaxVal = BigDecimal.ZERO;
	
	@XmlElement(name = "GSTIN_IGST")
	private BigDecimal  gstinIgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GSTIN_CGST")
	private BigDecimal  gstinCgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GSTIN_SGST")
	private BigDecimal gstinSgst = BigDecimal.ZERO;
	
	@XmlElement(name = "GSTIN_CESS")
	private BigDecimal gstinCess = BigDecimal.ZERO;
	
	@XmlElement(name = "DIFF_TAX_VAL")
	private BigDecimal diffTaxVal = BigDecimal.ZERO;
	
	@XmlElement(name = "DIFF_IGST")
	private BigDecimal diffIgstVal = BigDecimal.ZERO;
	
	@XmlElement(name = "DIFF_CGST")
	private BigDecimal diffCgstVal = BigDecimal.ZERO;
	
	@XmlElement(name = "DIFF_SGST")
	private BigDecimal diffSgstVal = BigDecimal.ZERO;
	
	@XmlElement(name = "DIFF_CESS")
	private BigDecimal diffCessVal = BigDecimal.ZERO;
	
	@XmlElement(name = "COMPANY_CODE")
	private String companyCode;
	
	
}
