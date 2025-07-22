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
public class ReviwSumCommonPopupVerticalItemDto {

	@XmlElement(name = "TranType")
	private String tranType;

	@XmlElement(name = "NdBillingPos")
	private String ndBillingPos;

	@XmlElement(name = "NdHsnSac")
	private String ndHsnSac;

	@XmlElement(name = "NdUom")
	private String ndUom;

	@XmlElement(name = "NdQuantity")
	private BigDecimal ndQuantity;

	@XmlElement(name = "NdRate")
	private BigDecimal ndRate;

	@XmlElement(name = "NdTaxableVal")
	private BigDecimal ndTaxableVal;

	@XmlElement(name = "NdEcomGstin")
	private String ndEcomGstin;

	@XmlElement(name = "NdEcomSupVal")
	private BigDecimal ndEcomSupVal;

	@XmlElement(name = "AmtIgst")
	private BigDecimal amtIgst;

	@XmlElement(name = "AmtCgst")
	private BigDecimal amtCgst;

	@XmlElement(name = "AmtSgst")
	private BigDecimal amtSgst;

	@XmlElement(name = "AmtCess")
	private BigDecimal amtCess;

	@XmlElement(name = "AmtTotalVal")
	private BigDecimal amtTotalVal;

	@XmlElement(name = "OhPrftCntr1")
	private String ohPrftCntr1;

	@XmlElement(name = "OhPlantCode")
	private String ohPlantCode;

	@XmlElement(name = "OhDivision")
	private String ohDivision;

	@XmlElement(name = "OhLocation")
	private String ohLocation;

	@XmlElement(name = "OhSalesOrg")
	private String ohSalesOrg;

	@XmlElement(name = "OhDistChanl")
	private String ohDistChanl;

	@XmlElement(name = "OhPrftCntr3")
	private String ohPrftCntr3;

	@XmlElement(name = "OhPrftCntr4")
	private String ohPrftCntr4;

	@XmlElement(name = "OhPrftCntr5")
	private String ohPrftCntr5;

	@XmlElement(name = "OhPrftCntr6")
	private String ohPrftCntr6;

	@XmlElement(name = "OhPrftCntr7")
	private String ohPrftCntr7;

	@XmlElement(name = "OhPrftCntr8")
	private String ohPrftCntr8;

	@XmlElement(name = "OhUdf1")
	private String ohUdf1;

	@XmlElement(name = "OhUdf2")
	private String ohUdf2;

	@XmlElement(name = "OhUdf3")
	private String ohUdf3;
}
