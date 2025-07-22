/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author siva
 *
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
@ToString
public class Gstr3bTablibPopUpSummaryItemDto {

	@XmlElement(name = "Description")
	private String description;

	@XmlElement(name = "CashIgst")
	private BigDecimal cashIgst = BigDecimal.ZERO;

	@XmlElement(name = "CashCgst")
	private BigDecimal cashCgst = BigDecimal.ZERO;

	@XmlElement(name = "CashState")
	private BigDecimal cashState = BigDecimal.ZERO;

	@XmlElement(name = "CashCess")
	private BigDecimal cashCess = BigDecimal.ZERO;

	@XmlElement(name = "CashTotal")
	private BigDecimal CashTotal = BigDecimal.ZERO;

	@XmlElement(name = "CrdtIgst")
	private BigDecimal CrdtIgst = BigDecimal.ZERO;

	@XmlElement(name = "CrdtCgst")
	private BigDecimal CrdtCgst = BigDecimal.ZERO;

	@XmlElement(name = "CrdtState")
	private BigDecimal CrdtState = BigDecimal.ZERO;

	@XmlElement(name = "CrdtCess")
	private BigDecimal crdtCess = BigDecimal.ZERO;

	@XmlElement(name = "CrdtTotal")
	private BigDecimal crdtTotal = BigDecimal.ZERO;

	@XmlElement(name = "OthRevChgPbl")
	private BigDecimal othRevChgPbl = BigDecimal.ZERO;

	@XmlElement(name = "PdThrItcIgst")
	private BigDecimal pdThrItcIgst = BigDecimal.ZERO;

	@XmlElement(name = "PdThrItcCgst")
	private BigDecimal pdThrItcCgst = BigDecimal.ZERO;

	@XmlElement(name = "PdThrItcSgst")
	private BigDecimal pdThrItcSgst = BigDecimal.ZERO;

	@XmlElement(name = "PdThrItcCess")
	private BigDecimal pdThrItcCess = BigDecimal.ZERO;

	@XmlElement(name = "OrtaxTobePdcs")
	private BigDecimal ortaxTobePdcs = BigDecimal.ZERO;

	@XmlElement(name = "RevChgTaxPbl")
	private BigDecimal revChgTaxPbl = BigDecimal.ZERO;

	@XmlElement(name = "RctobePdCas")
	private BigDecimal rctobePdCas = BigDecimal.ZERO;

	@XmlElement(name = "IntrestPayble")
	private BigDecimal intrestPayble = BigDecimal.ZERO;

	@XmlElement(name = "IntTobePdCas")
	private BigDecimal intTobePdCas = BigDecimal.ZERO;

	@XmlElement(name = "LateFeePayble")
	private BigDecimal lateFeePayble = BigDecimal.ZERO;

	@XmlElement(name = "LtftobePdincas")
	private BigDecimal ltftobePdincas = BigDecimal.ZERO;

	@XmlElement(name = "UtliCashBal")
	private BigDecimal utliCashBal = BigDecimal.ZERO;

	@XmlElement(name = "AddCashReqd")
	private BigDecimal addCashReqd = BigDecimal.ZERO;

}