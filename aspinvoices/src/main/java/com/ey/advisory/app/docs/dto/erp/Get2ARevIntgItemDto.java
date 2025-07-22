package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Get2ARevIntgItemDto {

	@XmlElement(name = "Sgstin")
	private String sgstin;

	@XmlElement(name = "Cgstin")
	private String cgstin;

	@XmlElement(name = "TaxPeriod")
	private String taxPeriod;

	@XmlElement(name = "Cfs")
	private String cfs;

	@XmlElement(name = "CheckSum")
	private String checkSum;

	@XmlElement(name = "SupplierName")
	private String supplierName;

	@XmlElement(name = "StateName")
	private String stateName;

	@XmlElement(name = "DocNum")
	private String docNum;

	@XmlElement(name = "DocDate")
	private String docDate;

	@XmlElement(name = "InvNum")
	private String invNum;

	@XmlElement(name = "InvDate")
	private String invDate;

	@XmlElement(name = "Pos")
	private String pos;

	@XmlElement(name = "Rchrg")
	private String rchrg;

	@XmlElement(name = "InvType")
	private String invType;

	@XmlElement(name = "DiffPercent")
	private BigDecimal diffPercent = BigDecimal.ZERO;

	@XmlElement(name = "OrgInvNum")
	private String orgInvNum;

	@XmlElement(name = "OrgInvDate")
	private String orgInvDate;

	@XmlElement(name = "ItemNumber")
	private String itemNumber;

	@XmlElement(name = "CrdrPreGst")
	private String crdrPreGst;

	@XmlElement(name = "ItcEntitlement")
	private String itcEntitlement;

	@XmlElement(name = "Section")
	private String section;

	@XmlElement(name = "TaxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@XmlElement(name = "TaxRate")
	private BigDecimal taxRate = BigDecimal.ZERO;

	@XmlElement(name = "IgstAmt")
	private BigDecimal igstAmt = BigDecimal.ZERO;

	@XmlElement(name = "CgstAmt")
	private BigDecimal cgstAmt = BigDecimal.ZERO;

	@XmlElement(name = "SgstAmt")
	private BigDecimal sgstAmt = BigDecimal.ZERO;

	@XmlElement(name = "CessAmt")
	private BigDecimal cessAmt = BigDecimal.ZERO;

	@XmlElement(name = "InvVal")
	private BigDecimal invVal = BigDecimal.ZERO;

	@XmlElement(name = "DocKey")
	private String docKey;

}
