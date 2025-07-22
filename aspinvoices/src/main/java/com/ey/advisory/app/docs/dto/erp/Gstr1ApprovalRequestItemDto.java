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
public class Gstr1ApprovalRequestItemDto {

	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "CompanyCode")
	private String companyCode;

	@XmlElement(name = "RetPer")
	private String retPer;

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "GstinNum")
	private String gstinNum;

	@XmlElement(name = "DataType")
	private String dataType;

	@XmlElement(name = "TaxCate")
	private String taxCate;

	// Asp Values
	@XmlElement(name = "AspCount")
	private BigInteger aspCount = BigInteger.ZERO;

	@XmlElement(name = "AspTbval")
	private BigDecimal aspTbval = BigDecimal.ZERO;

	@XmlElement(name = "AspTxval")
	private BigDecimal aspTxval = BigDecimal.ZERO;

	@XmlElement(name = "AspInval")
	private BigDecimal aspInval = BigDecimal.ZERO;

	@XmlElement(name = "AspIgstval")
	private BigDecimal aspIgstval = BigDecimal.ZERO;

	@XmlElement(name = "AspCgstval")
	private BigDecimal aspCgstval = BigDecimal.ZERO;

	@XmlElement(name = "AspSgstval")
	private BigDecimal aspSgstval = BigDecimal.ZERO;

	@XmlElement(name = "AspCessval")
	private BigDecimal aspCessval = BigDecimal.ZERO;

	@XmlElement(name = "AspCancel")
	private BigDecimal aspCancel = BigDecimal.ZERO;

	@XmlElement(name = "AspNetissue")
	private BigDecimal aspNetissue = BigDecimal.ZERO;

	@XmlElement(name = "AspNilRsup")
	private BigDecimal aspNilRsup = BigDecimal.ZERO;

	@XmlElement(name = "AspExpSup")
	private BigDecimal aspExpSup = BigDecimal.ZERO;

	@XmlElement(name = "AspNonGsup")
	private BigDecimal aspNonGsup = BigDecimal.ZERO;

	// Gstn Values
	@XmlElement(name = "GstnCount")
	private BigInteger gstnCount = BigInteger.ZERO;

	@XmlElement(name = "GstnTbval")
	private BigDecimal gstnTbval = BigDecimal.ZERO;

	@XmlElement(name = "GstnTxval")
	private BigDecimal gstnTxval = BigDecimal.ZERO;

	@XmlElement(name = "GstnInval")
	private BigDecimal gstnInval = BigDecimal.ZERO;

	@XmlElement(name = "GstnIgstval")
	private BigDecimal gstnIgstval = BigDecimal.ZERO;

	@XmlElement(name = "GstnCgstval")
	private BigDecimal gstnCgstval = BigDecimal.ZERO;

	@XmlElement(name = "GstnSgstval")
	private BigDecimal gstnSgstval = BigDecimal.ZERO;

	@XmlElement(name = "GstnCessval")
	private BigDecimal gstnCessval = BigDecimal.ZERO;

	@XmlElement(name = "GstNilRsup")
	private BigDecimal gstNilRsup = BigDecimal.ZERO;

	@XmlElement(name = "GstExpSup")
	private BigDecimal gstExpSup = BigDecimal.ZERO;

	@XmlElement(name = "GstNonGsup")
	private BigDecimal gstNonGsup = BigDecimal.ZERO;

	@XmlElement(name = "GstCancel")
	private BigDecimal gstCancel = BigDecimal.ZERO;

	@XmlElement(name = "GstNetissue")
	private BigDecimal gstNetissue = BigDecimal.ZERO;

	@XmlElement(name = "DiffCount")
	private BigInteger diffCount = BigInteger.ZERO;

	@XmlElement(name = "DiffTbval")
	private BigDecimal diffTbval = BigDecimal.ZERO;

	@XmlElement(name = "DiffTxval")
	private BigDecimal diffTxval = BigDecimal.ZERO;

	@XmlElement(name = "DiffInval")
	private BigDecimal diffInval = BigDecimal.ZERO;

	@XmlElement(name = "DiffIgstval")
	private BigDecimal diffIgstval = BigDecimal.ZERO;

	@XmlElement(name = "DiffCgstval")
	private BigDecimal diffCgstval = BigDecimal.ZERO;

	@XmlElement(name = "DiffSgstval")
	private BigDecimal diffSgstval = BigDecimal.ZERO;

	@XmlElement(name = "DiffCessval")
	private BigDecimal diffCessval = BigDecimal.ZERO;

	@XmlElement(name = "DiffNilRsup")
	private BigDecimal diffNilRsup = BigDecimal.ZERO;

	@XmlElement(name = "DiffExpSup")
	private BigDecimal diffExpSup = BigDecimal.ZERO;

	@XmlElement(name = "DiffNonGsup")
	private BigDecimal diffNonGsup = BigDecimal.ZERO;

	@XmlElement(name = "DiffCancel")
	private BigDecimal diffCancel = BigDecimal.ZERO;

	@XmlElement(name = "DiffNetissue")
	private BigDecimal diffNetissue = BigDecimal.ZERO;

	@XmlElement(name = "Prctr")
	private String profitCenter;

	@XmlElement(name = "Werks")
	private String plantCode;

	@XmlElement(name = "Vkorg")
	private String salesOrganization;

	@XmlElement(name = "Bukrs")
	private String bukrs;

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

}
