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
public class AdvancePopupVerticalItemDto {

	@XmlElement(name = "TranType")
	private String tranType;

	@XmlElement(name = "BillingPos")
	private String billingPos;

	@XmlElement(name = "Rate")
	private BigDecimal rate;

	@XmlElement(name = "Ngar")
	private BigDecimal ngar;

	@XmlElement(name = "Igst")
	private BigDecimal igst;

	@XmlElement(name = "Cgst")
	private BigDecimal cgst;

	@XmlElement(name = "Sgst")
	private BigDecimal sgst;

	@XmlElement(name = "Cess")
	private BigDecimal cess;

	@XmlElement(name = "PrftCntr1")
	private String prftCntr1;

	@XmlElement(name = "PlantCode")
	private String plantCode;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "SalesOrg")
	private String salesOrg;

	@XmlElement(name = "DistChanl")
	private String distChanl;

	@XmlElement(name = "PrftCntr3")
	private String prftCntr3;

	@XmlElement(name = "PrftCntr4")
	private String prftCntr4;

	@XmlElement(name = "PrftCntr5")
	private String prftCntr5;

	@XmlElement(name = "PrftCntr6")
	private String prftCntr6;

	@XmlElement(name = "PrftCntr7")
	private String prftCntr7;

	@XmlElement(name = "PrftCntr8")
	private String prftCntr8;

	@XmlElement(name = "Udf1")
	private String udf1;

	@XmlElement(name = "Udf2")
	private String udf2;

	@XmlElement(name = "Udf3")
	private String udf3;
}
