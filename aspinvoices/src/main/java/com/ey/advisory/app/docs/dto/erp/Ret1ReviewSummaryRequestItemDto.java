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
 * @author Mahesh.Golla
 *
 */
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Ret1ReviewSummaryRequestItemDto {
	
	@XmlElement(name ="Guid")
	private String gUid;
	
	@XmlElement(name="Entity")
	private String entity ;
	
	@XmlElement(name="Table")
	private String table ;
	
	@XmlElement(name="key")
	private String key ;
	
	@XmlElement(name = "EntityId")
	private String entityPan;
	
	@XmlElement(name="EyStatus")
	private String eyStatus ;
	
	@XmlElement(name = "Bukrs")
	private String companyCode;
	
	@XmlElement(name="RetPer")
	private String retPer;

	@XmlElement(name = "State")
	private String state;
	
	@XmlElement(name="GstinNum")
	private String gstinNum;
	
	@XmlElement(name = "SupplyType")
	private String supplyType;
	
	@XmlElement(name = "AspIgst")
	private BigDecimal aspIgst;
		
	@XmlElement(name = "AspCgst")
	private BigDecimal aspCgst;
	
	@XmlElement(name = "AspSgst")
	private BigDecimal aspSgst;
	
	@XmlElement(name = "AspCess")
	private BigDecimal aspCess;
	
	
	@XmlElement(name = "GstinIgst")
	private BigDecimal gstinIgst;
		
	@XmlElement(name = "GstinCgst")
	private BigDecimal gstinCgst;
	
	@XmlElement(name = "GstinSgst")
	private BigDecimal gstinSgst;
	
	@XmlElement(name = "GstinCess")
	private BigDecimal gstinCess;
	
	
	@XmlElement(name = "DiffIgst")
	private BigDecimal diffIgst;
		
	@XmlElement(name = "DiffCgst")
	private BigDecimal diffCgst;
	
	@XmlElement(name = "DiffSgst")
	private BigDecimal diffSgst;
	
	@XmlElement(name = "DiffCess")
	private BigDecimal diffCess;
	
	@XmlElement(name = "AspIgstInt")
	private BigDecimal aspIgstInt;
	
	@XmlElement(name = "AspCgstInt")	
	private BigDecimal aspCgstInt;
	
	@XmlElement(name = "AspSgstInt")
	private BigDecimal aspSgstInt;
	
	@XmlElement(name = "AspCessInt")
	private BigDecimal aspCessInt;
	
	@XmlElement(name = "AspCgstLate")
	private BigDecimal aspCgstLate;
	
	@XmlElement(name = "AspSgstLate")
	private BigDecimal aspSgstLate;
	
	@XmlElement(name = "GstinIgstInt")
	private BigDecimal gstinIgstInt;
	
	@XmlElement(name = "GstinCgstInt")
	private BigDecimal gstinCgstInt;
	
	@XmlElement(name = "GstinSgstInt")
	private BigDecimal gstinSgstInt;
	
	@XmlElement(name = "GstinCessInt")
	private BigDecimal gstinCessInt;
	
	@XmlElement(name = "GstinCgstLate")
	private BigDecimal gstinCgstLate;
	
	@XmlElement(name = "GstinSgstLate")
	private BigDecimal gstinSgstLate;
	
	@XmlElement(name="DiffIgstInt")
	private BigInteger diffIgstInt;
	
	@XmlElement(name="DiffCgstInt")
	private BigInteger diffCgstInt;
	
	@XmlElement(name="DiffSgstInt")
	private BigInteger diffSgstInt;
	
	@XmlElement(name="DiffCgstLate")
	private BigDecimal diffCgstLate;
	
	@XmlElement(name = "DiffSgstLate")
	private BigDecimal diffSgstLate;
	
	@XmlElement(name = "AspTaxPrc")
	private String aspTaxPrc;
	
	@XmlElement(name = "AspTaxOrc")
	private String aspTaxOrc;
	
	@XmlElement(name = "AspTaxAprc")
	private BigDecimal aspTaxAprc;
	
	@XmlElement(name = "AspTaxAporc")
	private BigDecimal aspTaxAporc;
	
	@XmlElement(name = "AspAdjLrc")
	private BigDecimal aspAdjLrc;
	
	
	@XmlElement(name = "AspAdjLorc")
	private BigDecimal aspAdjLorc;
	
	@XmlElement(name = "AspItcIgst")
	private BigDecimal aspItcIgst;
	
	@XmlElement(name = "AspItcCgst")
	private BigDecimal aspItcCgst;

	@XmlElement(name = "AspItcSgst")
	private BigDecimal aspItcSgst;
	
	@XmlElement(name = "AspItcCess")
	private BigDecimal aspItcCess;
	
	@XmlElement(name = "AspCashTax")
	private BigDecimal aspCashTax;
	
	@XmlElement(name = "AspCashIntr")
	private BigDecimal aspCashIntr;
	
	@XmlElement(name = "AspCashLate")
	private BigDecimal aspCashLate;
	
	@XmlElement(name = "GstinTaxPrc")
	private BigDecimal gstinTaxPrc;
	
	@XmlElement(name = "GstinTaxOrc")
	private BigDecimal gstinTaxOrc;
	
	@XmlElement(name = "GstinTaxAprc")
	private BigDecimal gstinTaxAprc;
	
	@XmlElement(name = "GstinTaxAporc")
	private BigDecimal gstinTaxAporc;
	
	@XmlElement(name = "GstinAdjLrc")
	private BigDecimal gstinAdjLrc;
	
	@XmlElement(name = "GstinAdjLorc")
	private BigDecimal gstinAdjLorc;
	
	@XmlElement(name = "GstinItcIgst")
	private BigDecimal gstinItcIgst;
	
	@XmlElement(name = "GstinItcCgst")
	private BigDecimal gstinItcCgst;
	
	@XmlElement(name = "GstinItcSgst")
	private BigDecimal gstinItcSgst;
	
	@XmlElement(name = "GstinItcCess")
	private BigDecimal gstinItcCess;
	
	@XmlElement(name ="GstinCashTax")
	private BigDecimal gstinCashTax;
	
	@XmlElement(name = "GstinCashIntr")
	private BigDecimal gstinCashIntr;
	
	@XmlElement(name = "GstinCashLate")
	private BigDecimal gstinCashLate;
	
	@XmlElement(name = "DiffTaxPrc")
	private BigDecimal diffTaxPrc;
	
	@XmlElement(name = "DiffTaxOrc")
	private BigDecimal diffTaxOrc;
	
	@XmlElement(name = "DiffTaxAprc")
	private BigDecimal diffTaxAprc;
	
	@XmlElement(name = "DiffTaxAporc")
	private BigDecimal diffTaxAporc;
	
	@XmlElement(name = "DiffAdjLrc")
	private BigDecimal diffAdjLrc;
	
	@XmlElement(name = "DiffAdjLorc")
	private BigDecimal diffAdjLorc;
	
	@XmlElement(name = "DiffItcIgst")
	private BigDecimal diffItcIgst;
	
	@XmlElement(name ="DiffItcCgst" )
	private BigDecimal diffItcCgst;
	
	@XmlElement(name = "DiffItcSgst")
	private BigDecimal diffItcSgst;
	
	@XmlElement(name = "DiffItcCess")
	private BigDecimal diffItcCess;
	
	@XmlElement(name = "DiffCashTax")
	private BigDecimal diffCashTax;
	
	@XmlElement(name = "DiffCashIntr")
	private BigDecimal diffCashIntr;
	
	@XmlElement(name = "DiffCashLate")
	private BigDecimal diffCashLate;
	
	@XmlElement(name = "TotTaxLiability")
	private BigDecimal totalTaxLiability;
	
	@XmlElement(name = "RevCharge")
	private BigDecimal revCharge;
	
	@XmlElement(name = "OthRevCharge")
	private BigDecimal othRevCharge;
	
	
	@XmlElement(name = "NetAvlItc")
	private BigDecimal netAvaiItc;
	
	@XmlElement(name="Tds")
	private BigDecimal tds ;
	
	@XmlElement(name="Tcs")
	private BigDecimal tcs ;
	
	@XmlElement(name = "Prctr")
	private String profitCenter;
	
	@XmlElement(name = "DocType")
	private String docType;
	
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

	public Ret1ReviewSummaryRequestItemDto
	                       add(Ret1ReviewSummaryRequestItemDto dto) {
		
		this.entity = dto.entity;
		this.companyCode = dto.companyCode;
		this.entityPan = dto.entityPan;
		this.retPer = dto.retPer;
		this.gstinNum = dto.gstinNum;
		this.eyStatus = dto.eyStatus;
		this.profitCenter = dto.profitCenter;
		this.plantCode = dto.plantCode;
		this.location = dto.location;
		this.division = dto.division;
		this.salesOrganization = dto.salesOrganization;
		this.distChannel = dto.distChannel;
		this.useraccess1 = dto.useraccess1;
		this.useraccess2  = dto.useraccess2;
		this.useraccess3 = dto.useraccess3;
		this.useraccess4  = dto.useraccess4;
		this.useraccess5 = dto.useraccess5;
		this.useraccess6  = dto.useraccess6;
		this.totalTaxLiability = 
				addBigDecimals(this.totalTaxLiability,dto.totalTaxLiability);
		this.revCharge = addBigDecimals(this.revCharge,dto.revCharge);
		this.othRevCharge = addBigDecimals(this.othRevCharge,dto.othRevCharge);
		this.tcs = addBigDecimals(this.tcs,dto.tcs);
		this.tds = addBigDecimals(this.tds,dto.tds);
		this.netAvaiItc = addBigDecimals(this.netAvaiItc,dto.netAvaiItc);
		this.key = dto.key;
		return this;
	}
	private BigDecimal addBigDecimals(BigDecimal bd1, BigDecimal bd2) {
		if (bd1 == null && bd2 == null)
			return BigDecimal.ZERO;
		if (bd1 == null)
			return bd2;
		if (bd2 == null)
			return bd1;
		return bd1.add(bd2);
	}
	public Ret1ReviewSummaryRequestItemDto 
	addSetoffValues(Ret1ReviewSummaryRequestItemDto summEntity) {
		this.entity = summEntity.entity;
		this.companyCode = summEntity.companyCode;
		this.entityPan = summEntity.entityPan;
		this.retPer = summEntity.retPer;
		this.gstinNum = summEntity.gstinNum;
		this.eyStatus = summEntity.eyStatus;
		this.profitCenter = summEntity.profitCenter;
		this.plantCode = summEntity.plantCode;
		this.location = summEntity.location;
		this.division = summEntity.division;
		this.salesOrganization = summEntity.salesOrganization;
		this.distChannel = summEntity.distChannel;
		this.useraccess1 = summEntity.useraccess1;
		this.useraccess2 = summEntity.useraccess2;
		this.useraccess3 = summEntity.useraccess3;
		this.useraccess4 = summEntity.useraccess4;
		this.useraccess5 = summEntity.useraccess5;
		this.useraccess6 = summEntity.useraccess6;
		this.table = summEntity.table;
		this.supplyType = summEntity.supplyType;
		this.aspTaxPrc = summEntity.aspTaxPrc;
		this.aspTaxOrc = summEntity.aspTaxOrc;
		this.aspTaxAprc = addBigDecimals(this.aspTaxAprc,
				summEntity.aspTaxAprc);
		this.aspTaxAporc = addBigDecimals(this.aspTaxAporc,
				summEntity.aspTaxAporc);

		this.aspAdjLrc = addBigDecimals(this.aspAdjLrc, summEntity.aspAdjLrc);
		this.aspAdjLorc = addBigDecimals(this.aspAdjLorc,
				summEntity.aspAdjLorc);
		this.aspItcIgst = addBigDecimals(this.aspItcIgst,
				summEntity.aspItcIgst);
		this.aspItcCgst = addBigDecimals(this.aspItcCgst,
				summEntity.aspItcCgst);
		this.aspItcSgst = addBigDecimals(this.aspItcSgst,
				summEntity.aspItcSgst);
		this.aspItcCess = addBigDecimals(this.aspItcCess,
				summEntity.aspItcCess);
		this.aspCashTax = addBigDecimals(this.aspCashTax,
				summEntity.aspCashTax);
		this.aspCashIntr = addBigDecimals(this.aspCashIntr,
				summEntity.aspCashIntr);
		this.aspCashLate = addBigDecimals(this.aspCashLate,
				summEntity.aspCashLate);
		this.gstinTaxPrc = addBigDecimals(this.gstinTaxPrc,
				summEntity.gstinTaxPrc);
		this.gstinTaxOrc = addBigDecimals(this.gstinTaxOrc,
				summEntity.gstinTaxOrc);
		this.gstinTaxAprc = addBigDecimals(this.gstinTaxAprc,
				summEntity.gstinTaxAprc);
		this.gstinTaxAporc = addBigDecimals(this.gstinTaxAporc,
				summEntity.gstinTaxAporc);

		this.gstinAdjLrc = addBigDecimals(this.gstinAdjLrc,
				summEntity.gstinAdjLrc);
		this.gstinAdjLorc = addBigDecimals(this.gstinAdjLorc,
				summEntity.gstinAdjLorc);
		this.gstinItcIgst = addBigDecimals(this.gstinItcIgst,
				summEntity.gstinItcIgst);
		this.gstinItcCgst = addBigDecimals(this.gstinItcCgst,
				summEntity.gstinItcCgst);
		this.gstinItcSgst = addBigDecimals(this.gstinItcSgst,
				summEntity.gstinItcSgst);
		this.gstinItcCess = addBigDecimals(this.gstinItcCess,
				summEntity.gstinItcCess);

		this.gstinCashTax = addBigDecimals(this.gstinCashTax,
				summEntity.gstinCashTax);
		this.gstinCashIntr = addBigDecimals(this.gstinCashIntr,
				summEntity.gstinCashIntr);
		this.gstinCashLate = addBigDecimals(this.gstinCashLate,
				summEntity.gstinCashLate);

		return this;

	}
	public Ret1ReviewSummaryRequestItemDto  
	addRefundValues(Ret1ReviewSummaryRequestItemDto summEntity) {
		return this;		
	}
	public Ret1ReviewSummaryRequestItemDto   
	addRet1Values(Ret1ReviewSummaryRequestItemDto summEntity) {
		this.entity = summEntity.entity;
		this.companyCode = summEntity.companyCode;
		this.entityPan = summEntity.entityPan;
		this.retPer = summEntity.retPer;
		this.gstinNum = summEntity.gstinNum;
		this.eyStatus = summEntity.eyStatus;
		this.profitCenter = summEntity.profitCenter;
		this.plantCode = summEntity.plantCode;
		this.location = summEntity.location;
		this.division = summEntity.division;
		this.salesOrganization = summEntity.salesOrganization;
		this.distChannel = summEntity.distChannel;
		this.useraccess1 = summEntity.useraccess1;
		this.useraccess2  = summEntity.useraccess2;
		this.useraccess3 = summEntity.useraccess3;
		this.useraccess4  = summEntity.useraccess4;
		this.useraccess5 = summEntity.useraccess5;
		this.useraccess6  = summEntity.useraccess6;
		this.table = summEntity.table;
		this.supplyType = summEntity.supplyType;
		this.aspIgst = addBigDecimals(this.aspIgst,summEntity.aspIgst);
		this.aspCgst = addBigDecimals(this.aspCgst,summEntity.aspCgst);
		this.aspSgst = addBigDecimals(this.aspSgst,summEntity.aspSgst);
		this.aspCess =addBigDecimals(this.aspCess,summEntity.aspCess);
		this.gstinIgst = addBigDecimals(this.gstinIgst,summEntity.gstinIgst);
		this.gstinCgst = addBigDecimals(this.gstinCgst,summEntity.gstinCgst);
		this.gstinSgst = addBigDecimals(this.gstinSgst,summEntity.gstinSgst);
		this.gstinCess = addBigDecimals(this.gstinCess,summEntity.gstinCess);
		return this;
	}
	public Ret1ReviewSummaryRequestItemDto 
	addInterestValues(Ret1ReviewSummaryRequestItemDto summEntity) {
		this.entity = summEntity.entity;
		this.companyCode = summEntity.companyCode;
		this.entityPan = summEntity.entityPan;
		this.retPer = summEntity.retPer;
		this.gstinNum = summEntity.gstinNum;
		this.eyStatus = summEntity.eyStatus;
		this.profitCenter = summEntity.profitCenter;
		this.plantCode = summEntity.plantCode;
		this.location = summEntity.location;
		this.division = summEntity.division;
		this.salesOrganization = summEntity.salesOrganization;
		this.distChannel = summEntity.distChannel;
		this.useraccess1 = summEntity.useraccess1;
		this.useraccess2 = summEntity.useraccess2;
		this.useraccess3 = summEntity.useraccess3;
		this.useraccess4 = summEntity.useraccess4;
		this.useraccess5 = summEntity.useraccess5;
		this.useraccess6 = summEntity.useraccess6;
		this.table = summEntity.table;
		this.supplyType = summEntity.supplyType;
		this.aspIgstInt = addBigDecimals(this.aspIgstInt,
				summEntity.aspIgstInt);
		this.aspCgstInt = addBigDecimals(this.aspCgstInt,
				summEntity.aspCgstInt);
		this.aspSgstInt = addBigDecimals(this.aspSgstInt,
				summEntity.aspSgstInt);
		this.aspCessInt = addBigDecimals(this.aspCessInt,
				summEntity.aspCessInt);
		this.aspCgstLate = addBigDecimals(this.aspCgstLate,
				summEntity.aspCgstLate);
		this.aspSgstLate = addBigDecimals(this.aspSgstLate,
				summEntity.aspSgstLate);
		this.gstinIgstInt = addBigDecimals(this.gstinIgstInt,
				summEntity.gstinIgstInt);
		this.gstinCgstInt = addBigDecimals(this.gstinCgstInt,
				summEntity.gstinCgstInt);
		this.gstinSgstInt = addBigDecimals(this.gstinSgstInt,
				summEntity.gstinSgstInt);
		this.gstinCessInt = addBigDecimals(this.gstinCessInt,
				summEntity.gstinCessInt);
		this.gstinCgstLate = addBigDecimals(this.gstinCgstLate,
				summEntity.gstinCgstLate);
		this.gstinSgstLate = addBigDecimals(this.gstinSgstLate,
				summEntity.gstinSgstLate);
		return this;
	}

}