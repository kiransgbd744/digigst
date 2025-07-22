/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Umesha M
 *
 */
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Anx1ReviewSummaryRequestItemDto {

	@XmlElement(name ="Guid")
	private String gUid;
	
	@XmlElement(name="Entity")
	private String entity ;
	
	@XmlElement(name = "EntityId")
	private String entityPan;
	
	@XmlElement(name="RetPer")
	private String retPer ;
	
	@XmlElement(name="GstinNum")
	private String gstinNum ;

	@XmlElement(name="DataType")
	private String dataType ;
	
	
	@XmlElement(name="TaxCate")
	private String taxCate ;
	
	@XmlElement(name="TaxTable")
	private String taxTable ;
	
	@XmlElement(name="TaxDoctype")
	private String taxDoctype ;
	
	@XmlElement(name="Erdat")
	private String erDate;
	
	@XmlElement(name="Cputm")
	private String currentTime ;
	
	@XmlElement(name="EyTotDoc")
	private BigInteger eyTotDoc = BigInteger.ZERO;
	
	@XmlElement(name="EyTbval")
	private BigDecimal eyTbval = BigDecimal.ZERO;
	
	@XmlElement(name="EyTxval")
	private BigDecimal eyTxval = BigDecimal.ZERO;
	
	@XmlElement(name="EyInval")
	private BigDecimal eyInval = BigDecimal.ZERO;
	
	@XmlElement(name="EyIgstval")
	private BigDecimal eyIgstval = BigDecimal.ZERO;
	
	@XmlElement(name="EyCgstval")
	private BigDecimal eyCgstval = BigDecimal.ZERO;
	
	@XmlElement(name="EySgstval")
	private BigDecimal eySgstval = BigDecimal.ZERO;
	
	@XmlElement(name="EyCessval")
	private BigDecimal eyCessval = BigDecimal.ZERO;
	
	@XmlElement(name="GstnTotDoc")
	private BigInteger gstnTotDoc = BigInteger.ZERO;
	
	@XmlElement(name="GstnTbval")
	private BigDecimal gstnTbval = BigDecimal.ZERO;
	
	@XmlElement(name="GstnTxval")
	private BigDecimal gstnTxval = BigDecimal.ZERO;
	
	@XmlElement(name="GstnInval")
	private BigDecimal gstnInval = BigDecimal.ZERO;
	
	@XmlElement(name="GstnIgstval")
	private BigDecimal gstnIgstval = BigDecimal.ZERO;
	
	@XmlElement(name="GstnCgstval")
	private BigDecimal gstnCgstval = BigDecimal.ZERO;
	
	@XmlElement(name="GstnSgstval")
	private BigDecimal gstnSgstval = BigDecimal.ZERO;
	
	@XmlElement(name="GstnCessval")
	private BigDecimal gstnCessval = BigDecimal.ZERO;
	
	
	@XmlElement(name="MemoTotDoc")
	private BigInteger memoTotDoc = BigInteger.ZERO;
	
	@XmlElement(name="MemoTbval")
	private BigDecimal memoTbval = BigDecimal.ZERO;
	
	@XmlElement(name="MemoTxval")
	private BigDecimal memoTxval = BigDecimal.ZERO;
	
	@XmlElement(name="MemoInval")
	private BigDecimal memoInval = BigDecimal.ZERO;
	
	@XmlElement(name="MemoIgstval")
	private BigDecimal memoIgstval = BigDecimal.ZERO;
	
	@XmlElement(name="MemoCgstval")
	private BigDecimal memoCgstval = BigDecimal.ZERO;
	
	@XmlElement(name="MemoSgstval")
	private BigDecimal memoSgstval = BigDecimal.ZERO;
	
	@XmlElement(name="MemoCessval")
	private BigDecimal memoCessval = BigDecimal.ZERO;
	
	
	@XmlElement(name="DiffTotDoc")
	private BigInteger diffTotDoc = BigInteger.ZERO;
	
	@XmlElement(name="DiffTbval")
	private BigDecimal diffTbval = BigDecimal.ZERO;
		
	@XmlElement(name="DiffTxval")
	private BigDecimal diffTxval = BigDecimal.ZERO;
	
	@XmlElement(name="DiffInval")
	private BigDecimal diffInval = BigDecimal.ZERO;
	
	@XmlElement(name="DiffIgstval")
	private BigDecimal diffIgstval = BigDecimal.ZERO;
	
	@XmlElement(name="DiffCgstval")
	private BigDecimal diffCgstval = BigDecimal.ZERO;
	
	@XmlElement(name="DiffSgstval")
	private BigDecimal diffSgstval = BigDecimal.ZERO;
	
	@XmlElement(name="DiffCessval")
	private BigDecimal diffCessval = BigDecimal.ZERO;
	
	/*@XmlElement(name = "AspSuppmode")
	private BigDecimal aspSuppmode = BigDecimal.ZERO;
	
	@XmlElement(name = "AspSuppreturn")	
	private BigDecimal aspSuppreturn = BigDecimal.ZERO;
	
	@XmlElement(name = "AspSuppnet")
	private BigDecimal aspSuppnet = BigDecimal.ZERO;
	
	@XmlElement(name = "MemoSuppmode")
	private BigDecimal memoSuppmode = BigDecimal.ZERO;
	
	@XmlElement(name = "MemoSuppreturn")
	private BigDecimal memoSuppreturn = BigDecimal.ZERO;
	
	@XmlElement(name = "MemoSuppnet")
	private BigDecimal memoSuppnet = BigDecimal.ZERO;
	
	@XmlElement(name = "GstSuppmode")
	private BigDecimal gstSuppmode = BigDecimal.ZERO;
	
	@XmlElement(name = "GstSuppreturn")
	private BigDecimal gstSuppreturn = BigDecimal.ZERO;
	
	@XmlElement(name = "GstSuppnet")
	private BigDecimal gstSuppnet = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffSuppmode")
	private BigDecimal diffSuppmode = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffSuppreturn")	
	private BigDecimal diffSuppreturn = BigDecimal.ZERO;
	
	@XmlElement(name = "DiffSuppnet")
	private BigDecimal diffSuppnet = BigDecimal.ZERO;*/
	
	@XmlElement(name = "Prctr")
	private String profitCenter;
	
	@XmlElement(name = "Werks")
	private String plantCode;
	
	@XmlElement(name = "Bukrs")
	private String companyCode;
	
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