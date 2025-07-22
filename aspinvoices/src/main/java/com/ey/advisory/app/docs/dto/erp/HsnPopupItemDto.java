package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class HsnPopupItemDto {

	@XmlElement(name = "Slno")
	private int slno;

	@XmlElement(name = "Hsn")
	private BigInteger hsn;

	@XmlElement(name = "Uqc")
	private String uqc;

	@XmlElement(name = "AspQty")
	private BigDecimal aspQunty;

	@XmlElement(name = "AspTotalVal")
	private BigDecimal aspTotalVal;

	@XmlElement(name = "AspTotalTax")
	private BigDecimal aspTotalTax;

	@XmlElement(name = "AspIgst")
	private BigDecimal aspIgst;

	@XmlElement(name = "AspCgst")
	private BigDecimal aspCgst;

	@XmlElement(name = "AspSgst")
	private BigDecimal aspSgst;

	@XmlElement(name = "AspCess")
	private BigDecimal aspCess;

	@XmlElement(name = "EditQty")
	private BigDecimal editQunty;

	@XmlElement(name = "EditTotalVal")
	private BigDecimal editTotalVal;

	@XmlElement(name = "EditTotalTax")
	private BigDecimal edtTotalTax;

	@XmlElement(name = "EditIgst")
	private BigDecimal editIgst;

	@XmlElement(name = "EditCgst")
	private BigDecimal editCgst;

	@XmlElement(name = "EditSgst")
	private BigDecimal editSgst;

	@XmlElement(name = "EditCess")
	private BigDecimal editCess;
}
