package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class EinvoiceDataStatusRequestItemDto {

	@XmlElement(name = "CompanyCode")
	private String companyCode;

	@XmlElement(name = "Entity")
	private String entity;

	@XmlElement(name = "RetPer")
	private String retPer;

	@XmlElement(name = "GstinNum")
	private String gstinNum;

	@XmlElement(name = "DataType")
	private String dataType;

	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "ProfitCenter1")
	private String profitCenter1;

	@XmlElement(name = "ProfitCenter2")
	private String profitCenter2;

	@XmlElement(name = "SalesOrg")
	private String salesOrg;

	@XmlElement(name = "PurchaseOrg")
	private String purchaseOrg;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "DistChanel")
	private String distChanel;

	@XmlElement(name = "PlantCode")
	private String plantCode;

	@XmlElement(name = "PushedDt")
	private String pushedDt;

	@XmlElement(name = "RetType")
	private String retType;

	@XmlElement(name = "RetSection")
	private String retSection;

	@XmlElement(name = "TCount")
	private BigInteger tCount = BigInteger.ZERO;

	@XmlElement(name = "TaxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@XmlElement(name = "TotalTax")
	private BigDecimal totalTax = BigDecimal.ZERO;

	@XmlElement(name = "Igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@XmlElement(name = "Cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@XmlElement(name = "Sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	@XmlElement(name = "Cess")
	private BigDecimal cess = BigDecimal.ZERO;

	@XmlElement(name = "DocType")
	private String docType;

}
