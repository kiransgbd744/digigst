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
public class Get2BRevIntgSummaryDto {

	@XmlElement(name = "RECIPIENT_GSTIN")
	private String Rgstin;

	@XmlElement(name = "COUNT_DOC")
	private Integer count;

	@XmlElement(name = "DESCRIPTION")
	private String description;

	@XmlElement(name = "TAX_VALUE")
	private BigDecimal taxablevalue = BigDecimal.ZERO;

	@XmlElement(name = "IGST_T_TAX")
	private BigDecimal totalTaxIgst = BigDecimal.ZERO;

	@XmlElement(name = "CGST_T_TAX")
	private BigDecimal totalTaxCgst = BigDecimal.ZERO;

	@XmlElement(name = "SGST_T_TAX")
	private BigDecimal totalTaxSgst = BigDecimal.ZERO;

	@XmlElement(name = "CESS_T_TAX")
	private BigDecimal totalTaxCess = BigDecimal.ZERO;

	@XmlElement(name = "IGST_AVAIL_ITC")
	private BigDecimal availItcIgst = BigDecimal.ZERO;

	@XmlElement(name = "CGST_AVAIL_ITC")
	private BigDecimal availItcCgst = BigDecimal.ZERO;

	@XmlElement(name = "SGST_AVAIL_ITC")
	private BigDecimal availItcSgst = BigDecimal.ZERO;

	@XmlElement(name = "CESS_AVAIL_ITC")
	private BigDecimal availItcCess = BigDecimal.ZERO;

	@XmlElement(name = "IGST_UNAVAIL_ITC")
	private BigDecimal nonAvailItcIgst = BigDecimal.ZERO;

	@XmlElement(name = "CGST_UNAVAIL_ITC")
	private BigDecimal nonAvailItcCgst = BigDecimal.ZERO;

	@XmlElement(name = "SGST_UNAVAIL_ITC")
	private BigDecimal nonAvailItcSgst = BigDecimal.ZERO;

	@XmlElement(name = "CESS_UNAVAIL_ITC")
	private BigDecimal nonAvailItcCess = BigDecimal.ZERO;

	@XmlElement(name = "TABLE_TYPE")
	private String tableType;

	@XmlElement(name = "CREATED_ON")
	private String createdOn;

	@XmlElement(name = "RETURN_PERIOD")
	private String retPeriod;

	@XmlElement(name = "ENTITY_NAME")
	private String entityName;

	@XmlElement(name = "ENTITY_PAN")
	private String entityPan;
}
