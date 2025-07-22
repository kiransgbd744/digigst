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
public class Gstr3b32PopUpSummaryItemDto {

	@XmlElement(name = "TypeOfSupp")
	private String TypeOfSupp;

	@XmlElement(name = "DigiCompPsup")
	private String DigiCompPsup;

	@XmlElement(name = "DigiCompTbVal")
	private BigDecimal DigiCompTbVal = BigDecimal.ZERO;

	@XmlElement(name = "DigiCompIgst")
	private BigDecimal DigiCompIgst = BigDecimal.ZERO;

	@XmlElement(name = "GstnCompPsup")
	private String GstnCompPsup;

	@XmlElement(name = "GstnCompTbVal")
	private BigDecimal GstnCompTbVal = BigDecimal.ZERO;

	@XmlElement(name = "GstnCompIgst")
	private BigDecimal GstnCompIgst = BigDecimal.ZERO;

	@XmlElement(name = "UiCompPsup")
	private String UiCompPsup;

	@XmlElement(name = "UiCompTbVal")
	private BigDecimal UiCompTbVal = BigDecimal.ZERO;

	@XmlElement(name = "UiCompIgst")
	private BigDecimal UiCompIgst = BigDecimal.ZERO;

}