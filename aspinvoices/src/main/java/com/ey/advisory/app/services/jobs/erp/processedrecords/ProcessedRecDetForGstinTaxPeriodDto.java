/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Nikhil.Duseja
 *
 */

@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class ProcessedRecDetForGstinTaxPeriodDto {
	
	@XmlElement(name="ENTITY")
	private String entity;
	
	@XmlElement(name="RET_PER")
	private String returnPeriod;
	
	@XmlElement(name="GSTIN_NUM")
	private String gstinNum;
	
	@XmlElement(name="DATA_TYPE")
	private String recordType;
	
	@XmlElement(name="ENTITY_NAME")
	private String entityName;
	
	@XmlElement(name="STATE")
	private String state;
	
	@XmlElement(name="DIVISION")
	private String Division;
	
	@XmlElement(name="PROFIT_CENTER")
	private String profitCenter;
	
	@XmlElement(name="STO_LOCATION")
	private String location;
	
	@XmlElement(name="PLANT_CODE")
	private String plantCode;
	
	@XmlElement(name="SALES_ORG")
	private String salesOrg;
	
	@XmlElement(name="PURCHASE_ORG")
	private String purchaseOrg;
	
	@XmlElement(name="DIST_CHANNEL")
	private String distrChannel;
	
	@XmlElement(name="USERACCESS1")
	private String userAccess1;
	
	@XmlElement(name="USERACCESS2")
	private String userAccess2;
	
	@XmlElement(name="USERACCESS3")
	private String userAccess3;
	
	@XmlElement(name="USERACCESS4")
	private String userAccess4;
	
	@XmlElement(name="USERACCESS5")
	private String userAccess5;
	
	@XmlElement(name="USERACCESS6")
	private String userAccess6;
	
	@XmlElement(name="DOC_COUNT")
	private Integer docCount;
	
	@XmlElement(name="SUPPLY_VAL")
	private BigDecimal taxableValue;
	
	@XmlElement(name="IGST_VAL")
	private BigDecimal igstAmt;
	
	@XmlElement(name = "CGST_VAL")
	private BigDecimal cgstAmt;
	
	@XmlElement(name = "SGST_VAL")
	private BigDecimal sgstAmt;
	
	@XmlElement(name = "CESS_VAL")
	private BigDecimal cessAmt;
	
	@XmlElement(name = "CREATEDON")
	private String createdOn;
	
	@XmlElement(name = "SAVE_STATUS")
	private String saveStatus;
	
	@XmlElement(name = "STATUS_DATE")
    private String statusDate;

	@XmlElement(name = "COMPANY_CODE")
	private String compCode;
	
}
