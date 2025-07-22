package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class Anx1AItemReviewSummaryReqDto {

	
	private String gstinNum;
	
	private String retPer;
	
	@XmlElement(name = "DataType")
	private String dataType;

	@XmlElement(name = "TaxTable")
	private String taxTable;

	@XmlElement(name = "TaxCate")
	private String taxCate;
	
	@XmlElement(name = "DocType")
	private String doctype;

	@XmlElement(name = "AspTotDoc")
	private BigInteger aspTotDoc;

	@XmlElement(name = "AspInval")
	private BigDecimal aspInval;

	@XmlElement(name = "AspTbval")
	private BigDecimal aspTbval;

	@XmlElement(name = "AspTxval")
	private BigDecimal aspTxval;

	@XmlElement(name = "AspIgstval")
	private BigDecimal aspIgstval;

	@XmlElement(name = "AspCgstval")
	private BigDecimal aspCgstval;

	@XmlElement(name = "AspSgstval")
	private BigDecimal aspSgstval;

	@XmlElement(name = "AspCessval")
	private BigDecimal aspCessval;

	@XmlElement(name = "AspSuppmode")
	private BigDecimal aspSuppmode;

	@XmlElement(name = "AspSuppreturn")
	private BigDecimal aspSuppreturn;

	@XmlElement(name = "AspSuppnet")
	private BigDecimal aspSuppnet;

	@XmlElement(name = "AspCount")
	private BigInteger aspCount;

	@XmlElement(name = "AspCancel")
	private BigDecimal aspCancel;

	@XmlElement(name = "AspNetissue")
	private BigDecimal aspNetissue;

	@XmlElement(name = "AspNilRsup")
	private BigDecimal aspNilRsup;

	@XmlElement(name = "AspExpSup")
	private BigDecimal aspExpSup;

	@XmlElement(name = "AspNonGsup")
	private BigDecimal aspNonGsup;

	@XmlElement(name = "GstSuppmode")
	private BigDecimal gstSuppmode;

	@XmlElement(name = "GstSuppreturn")
	private BigDecimal gstSuppreturn;

	@XmlElement(name = "GstSuppnet")
	private BigDecimal gstSuppnet;

	@XmlElement(name = "GstnDataStatus")
	private String gstnDataStatus;

	@XmlElement(name = "GstnTotDoc")
	private BigInteger gstnTotDoc;

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

	@XmlElement(name = "GstCancel")
	private BigDecimal gstCancel = BigDecimal.ZERO;

	@XmlElement(name = "GstNetissue")
	private BigDecimal gstNetissue = BigDecimal.ZERO;

	@XmlElement(name = "GstNilRsup")
	private BigDecimal gstNilRsup = BigDecimal.ZERO;

	@XmlElement(name = "GstExpSup")
	private BigDecimal gstExpSup = BigDecimal.ZERO;

	@XmlElement(name = "GstNonGsup")
	private BigDecimal gstNonGsup = BigDecimal.ZERO;

	@XmlElement(name = "MemoTotDoc")
	private BigInteger memoTotDoc = BigInteger.ZERO;

	@XmlElement(name = "MemoTbval")
	private BigDecimal memoTbval = BigDecimal.ZERO;

	@XmlElement(name = "MemoTxval")
	private BigDecimal memoTxval = BigDecimal.ZERO;

	@XmlElement(name = "MemoInval")
	private BigDecimal memoInval = BigDecimal.ZERO;

	@XmlElement(name = "MemoIgstval")
	private BigDecimal memoIgstval = BigDecimal.ZERO;

	@XmlElement(name = "MemoCgstval")
	private BigDecimal memoCgstval = BigDecimal.ZERO;

	@XmlElement(name = "MemoSgstval")
	private BigDecimal memoSgstval = BigDecimal.ZERO;

	@XmlElement(name = "MemoCessval")
	private BigDecimal memoCessval = BigDecimal.ZERO;

	@XmlElement(name = "DiffTotDoc")
	private BigInteger diffTotDoc = BigInteger.ZERO;

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

	@XmlElement(name = "DiffCancel")
	private BigDecimal diffCancel = BigDecimal.ZERO;

	@XmlElement(name = "DiffNetissue")
	private BigDecimal diffNetissue = BigDecimal.ZERO;

	@XmlElement(name = "DiffNilRsup")
	private BigDecimal diffNilRsup = BigDecimal.ZERO;

	@XmlElement(name = "DiffExpSup")
	private BigDecimal diffExpSup = BigDecimal.ZERO;

	@XmlElement(name = "DiffNonGsup")
	private BigDecimal diffNonGsup = BigDecimal.ZERO;

	@XmlElement(name = "MemoSuppmode")
	private BigDecimal memoSuppmode = BigDecimal.ZERO;

	@XmlElement(name = "MemoSuppreturn")
	private BigDecimal memoSuppreturn = BigDecimal.ZERO;

	@XmlElement(name = "MemoSuppnet")
	private BigDecimal memoSuppnet = BigDecimal.ZERO;

	@XmlElement(name = "Prctr")
	private String profitCenter;

	@XmlElement(name = "Werks")
	private String plantCode;

	@XmlElement(name = "Vkorg")
	private String salesOrganization;

	@XmlElement(name = "PareOrg")
	private String parchOrganization;

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
