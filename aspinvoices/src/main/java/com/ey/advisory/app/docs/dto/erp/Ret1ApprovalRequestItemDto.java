package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Ret1ApprovalRequestItemDto {

	@XmlElement(name = "Entity")
	private String entity;

	@XmlElement(name = "RetPer")
	private String retPer;

	@XmlElement(name = "GstinNum")
	private String gstinNum;

	@XmlElement(name = "SupplyType")
	private String supplyType;

	@XmlElement(name = "Table")
	private String tableSection;
	
	@XmlElement(name = "EntityId")
	private String entityPan;

	@XmlElement(name = "Bukrs")
	private String companyCode;

	@XmlElement(name = "Prctr")
	private String profitCenter;

	@XmlElement(name = "Werks")
	private String plantCode;

	@XmlElement(name = "Vkorg")
	private String salesOrganization;

	@XmlElement(name = "DistChannel")
	private String distChannel;

	@XmlElement(name = "Division")
	private String division;

	@XmlElement(name = "Useraccess1")
	private String useraccess1;

	@XmlElement(name = "Useraccess2")
	private String useraccess2;

	@XmlElement(name = "Useraccess3")
	private String useraccess3;

	@XmlElement(name = "Useraccess4")
	private String useraccess4;

	@XmlElement(name = "Useraccess5")
	private String useraccess5;

	@XmlElement(name = "Useraccess6")
	private String useraccess6;

	@XmlElement(name = "Location")
	private String location;

	// Asp
	@XmlElement(name = "AspIgst")
	private BigDecimal aspIgst = BigDecimal.ZERO;

	@XmlElement(name = "AspCgst")
	private BigDecimal aspCgst = BigDecimal.ZERO;

	@XmlElement(name = "AspSgst")
	private BigDecimal aspSgst = BigDecimal.ZERO;

	@XmlElement(name = "AspCess")
	private BigDecimal aspCess = BigDecimal.ZERO;

	// Gstin
	@XmlElement(name = "GstinIgst")
	private BigDecimal gstinIgst = BigDecimal.ZERO;

	@XmlElement(name = "GstinCgst")
	private BigDecimal gstinCgst = BigDecimal.ZERO;

	@XmlElement(name = "GstinSgst")
	private BigDecimal gstinSgst = BigDecimal.ZERO;

	@XmlElement(name = "GstinCess")
	private BigDecimal gstinCess = BigDecimal.ZERO;

	// Diff
	@XmlElement(name = "DiffIgst")
	private BigDecimal diffIgst = BigDecimal.ZERO;

	@XmlElement(name = "DiffCgst")
	private BigDecimal diffCgst = BigDecimal.ZERO;

	@XmlElement(name = "DiffSgst")
	private BigDecimal diffSgst = BigDecimal.ZERO;

	@XmlElement(name = "DiffCess")
	private BigDecimal diffCess = BigDecimal.ZERO;

	// Asp Int
	@XmlElement(name = "AspIgstInt")
	private BigInteger aspIgstInt = BigInteger.ZERO;

	@XmlElement(name = "AspCgstInt")
	private BigInteger aspCgstInt = BigInteger.ZERO;

	@XmlElement(name = "AspSgstInt")
	private BigInteger aspSgstInt = BigInteger.ZERO;

	@XmlElement(name = "AspCessInt")
	private BigInteger aspCessInt = BigInteger.ZERO;

	// Asp Late
	@XmlElement(name = "AspCgstLate")
	private BigDecimal aspCgstLate = BigDecimal.ZERO;

	@XmlElement(name = "AspSgstLate")
	private BigDecimal aspSgstLate = BigDecimal.ZERO;

	// Gstin Int
	@XmlElement(name = "GstinIgstInt")
	private BigInteger gstinIgstInt = BigInteger.ZERO;

	@XmlElement(name = "GstinCgstInt")
	private BigInteger gstinCgstInt = BigInteger.ZERO;

	@XmlElement(name = "GstinSgstInt")
	private BigInteger gstinSgstInt = BigInteger.ZERO;

	@XmlElement(name = "GstinCessInt")
	private BigInteger gstinCessInt = BigInteger.ZERO;

	// Gstin Late
	@XmlElement(name = "GstinCgstLate")
	private BigDecimal gstinCgstLate = BigDecimal.ZERO;

	@XmlElement(name = "GstinSgstLate")
	private BigDecimal gstinSgstLate = BigDecimal.ZERO;

	// Diff Int
	@XmlElement(name = "DiffIgstInt")
	private BigInteger diffIgstInt = BigInteger.ZERO;

	@XmlElement(name = "DiffCgstInt")
	private BigInteger diffCgstInt = BigInteger.ZERO;

	@XmlElement(name = "DiffSgstInt")
	private BigInteger diffSgstInt = BigInteger.ZERO;

	@XmlElement(name = "DiffCessInt")
	private BigInteger diffCessInt = BigInteger.ZERO;

	// Diff Late
	@XmlElement(name = "DiffCgstLate")
	private BigDecimal diffCgstLate = BigDecimal.ZERO;

	@XmlElement(name = "DiffSgstLate")
	private BigDecimal diffSgstLate = BigDecimal.ZERO;

	// Asp Tax
	@XmlElement(name = "AspTaxOrc")
	private String aspTaxOrc;

	@XmlElement(name = "AspTaxAprc")
	private BigDecimal aspTaxAprc = BigDecimal.ZERO;

	@XmlElement(name = "AspTaxAporc")
	private BigDecimal aspTaxAporc = BigDecimal.ZERO;

	// Asp Adj
	@XmlElement(name = "AspAdjLrc")
	private BigDecimal aspAdjLrc = BigDecimal.ZERO;

	@XmlElement(name = "AspAdjLorc")
	private BigDecimal aspAdjLorc = BigDecimal.ZERO;

	// Asp Itc
	@XmlElement(name = "AspItcIgst")
	private BigDecimal aspItcIgst = BigDecimal.ZERO;

	@XmlElement(name = "AspItcCgst")
	private BigDecimal aspItcCgst = BigDecimal.ZERO;

	@XmlElement(name = "AspItcSgst")
	private BigDecimal aspItcSgst = BigDecimal.ZERO;

	@XmlElement(name = "AspItcCess")
	private BigDecimal aspItcCess = BigDecimal.ZERO;

	// Asp Cash
	@XmlElement(name = "AspCashTax")
	private BigDecimal aspCashTax = BigDecimal.ZERO;

	@XmlElement(name = "AspCashIntr")
	private BigDecimal aspCashIntr = BigDecimal.ZERO;

	@XmlElement(name = "AspCashLate")
	private BigDecimal aspCashLate = BigDecimal.ZERO;

	// Gstin Tax
	@XmlElement(name = "GstinTaxPrc")
	private BigDecimal gstinTaxPrc = BigDecimal.ZERO;

	@XmlElement(name = "GstinTaxOrc")
	private BigDecimal gstinTaxOrc = BigDecimal.ZERO;

	@XmlElement(name = "GstinTaxAprc")
	private BigDecimal gstinTaxAprc = BigDecimal.ZERO;

	@XmlElement(name = "GstinTaxAporc")
	private BigDecimal gstinTaxAporc = BigDecimal.ZERO;

	// Gstin Adj
	@XmlElement(name = "GstinAdjLrc")
	private BigDecimal gstinAdjLrc = BigDecimal.ZERO;

	@XmlElement(name = "GstinAdjLorc")
	private BigDecimal gstinAdjLorc = BigDecimal.ZERO;

	// Gstin Itc
	@XmlElement(name = "GstinItcIgst")
	private BigDecimal gstinItcIgst = BigDecimal.ZERO;

	@XmlElement(name = "GstinItcCgst")
	private BigDecimal gstinItcCgst = BigDecimal.ZERO;

	@XmlElement(name = "GstinItcSgst")
	private BigDecimal gstinItcSgst = BigDecimal.ZERO;

	@XmlElement(name = "GstinItcCess")
	private BigDecimal gstinItcCess = BigDecimal.ZERO;

	// Gstin Cash
	@XmlElement(name = "GstinCashTax")
	private BigDecimal gstinCashTax = BigDecimal.ZERO;

	@XmlElement(name = "GstinCashIntr")
	private BigDecimal gstinCashIntr = BigDecimal.ZERO;

	@XmlElement(name = "GstinCashLate")
	private BigDecimal gstinCashLate = BigDecimal.ZERO;

	// Diff Tax
	@XmlElement(name = "DiffTaxPrc")
	private BigDecimal diffTaxPrc = BigDecimal.ZERO;

	@XmlElement(name = "DiffTaxOrc")
	private BigDecimal diffTaxOrc = BigDecimal.ZERO;

	@XmlElement(name = "DiffTaxAprc")
	private BigDecimal diffTaxAprc = BigDecimal.ZERO;

	@XmlElement(name = "DiffTaxAporc")
	private BigDecimal diffTaxAporc = BigDecimal.ZERO;

	// Diff Adj
	@XmlElement(name = "DiffAdjLrc")
	private BigDecimal diffAdjLrc = BigDecimal.ZERO;

	@XmlElement(name = "DiffAdjLorc")
	private BigDecimal diffAdjLorc = BigDecimal.ZERO;

	// Diff Itc
	@XmlElement(name = "DiffItcIgst")
	private BigDecimal diffItcIgst = BigDecimal.ZERO;

	@XmlElement(name = "DiffItcCgst")
	private BigDecimal diffItcCgst = BigDecimal.ZERO;

	@XmlElement(name = "DiffItcSgst")
	private BigDecimal diffItcSgst = BigDecimal.ZERO;

	@XmlElement(name = "DiffItcCess")
	private BigDecimal diffItcCess = BigDecimal.ZERO;

	// Diff Cash
	@XmlElement(name = "DiffCashTax")
	private BigDecimal diffCashTax = BigDecimal.ZERO;

	@XmlElement(name = "DiffCashIntr")
	private BigDecimal diffCashIntr = BigDecimal.ZERO;

	@XmlElement(name = "DiffCashLate")
	private BigDecimal diffCashLate = BigDecimal.ZERO;

}
