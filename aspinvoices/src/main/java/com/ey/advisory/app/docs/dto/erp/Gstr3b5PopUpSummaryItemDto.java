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
public class Gstr3b5PopUpSummaryItemDto {

	@XmlElement(name = "NatureSup")
	private String NatureSup;

	@XmlElement(name = "DigiInterSup")
	private BigDecimal DigiInterSup = BigDecimal.ZERO;

	@XmlElement(name = "DigiIntraSup")
	private BigDecimal DigiIntraSup = BigDecimal.ZERO;

	@XmlElement(name = "UiInterSup")
	private BigDecimal UiInterSup = BigDecimal.ZERO;

	@XmlElement(name = "UiIntraSup")
	private BigDecimal UiIntraSup = BigDecimal.ZERO;


}