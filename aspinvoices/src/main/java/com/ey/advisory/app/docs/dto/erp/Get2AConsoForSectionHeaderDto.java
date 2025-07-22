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
public class Get2AConsoForSectionHeaderDto {

	@XmlElement(name = "SectionName")
	private String sectionName;

	@XmlElement(name = "Sgstin")
	private String sgstin;

	@XmlElement(name = "Cgstin")
	private String cgstin;

	@XmlElement(name = "Ctin")
	private String ctin;

	@XmlElement(name = "TaxPeriod")
	private String taxPeriod;

	@XmlElement(name = "Cfs")
	private String cfs;

	@XmlElement(name = "Chksum")
	private String chksum;

	@XmlElement(name = "SuppInvNum")
	private String suppInvNum;

	@XmlElement(name = "SuppInvDate")
	private String suppInvDate;

	@XmlElement(name = "SuppInvVal")
	private BigDecimal suppInvVal;

	@XmlElement(name = "OrgInvNum")
	private String orgInvNum;

	@XmlElement(name = "OrgInvDate")
	private String orgInvDate;

	@XmlElement(name = "Pos")
	private String pos;

	@XmlElement(name = "Rchrg")
	private String rchrg;

	@XmlElement(name = "InvType")
	private String invType;

	@XmlElement(name = "DiffPercent")
	private BigDecimal diffPercent;

	@XmlElement(name = "IgstAmt")
	private BigDecimal igstAmt;

	@XmlElement(name = "CgstAmt")
	private BigDecimal cgstAmt;

	@XmlElement(name = "SgstAmt")
	private BigDecimal sgstAmt;

	@XmlElement(name = "CessAmt")
	private BigDecimal cessAmt;

	@XmlElement(name = "TaxableValue")
	private BigDecimal taxableValue;

	@XmlElement(name = "ActionTaken")
	private String actionTaken;

	@XmlElement(name = "DataCategory")
	private String dataCategory;

	@XmlElement(name = "SuppTdLgName")
	private String suppTdLgName;

	@XmlElement(name = "CfsGstr3b")
	private String cfsGstr3b;

	@XmlElement(name = "CancelDate")
	private String cancelDate;

	@XmlElement(name = "FileDate")
	private String fileDate;

	@XmlElement(name = "FilePeriod")
	private String filePeriod;

	@XmlElement(name = "OrgInvAmdPer")
	private String orgInvAmdPer;

	@XmlElement(name = "OrgInvAmdTyp")
	private String orgInvAmdTyp;

	@XmlElement(name = "SupplyType")
	private String supplyType;

	@XmlElement(name = "IrnNum")
	private String irnNum;

	@XmlElement(name = "IrnGenDate")
	private String irnGenDate;

	@XmlElement(name = "IrnSourceType")
	private String irnSourceType;

	@XmlElement(name = "NoteDate")
	private String noteDate;

	@XmlElement(name = "NoteNumber")
	private String noteNumber;

	@XmlElement(name = "NoteType")
	private String noteType;

	@XmlElement(name = "NoteValue")
	private BigDecimal noteValue;

	@XmlElement(name = "OrgNoteDate")
	private String orgNoteDate;

	@XmlElement(name = "OrgNoteNumber")
	private String orgNoteNumber;

	@XmlElement(name = "OrgNoteType")
	private String orgNoteType;

	@XmlElement(name = "InvNumber")
	private String invNumber;

	@XmlElement(name = "PGst")
	private String pgst;

	@XmlElement(name = "InvDate")
	private String invDate;

	@XmlElement(name = "UplodedBy")
	private String uplodedBy;

	@XmlElement(name = "DFlag")
	private String dflag;

	@XmlElement(name = "DocDate")
	private String docDate;

	@XmlElement(name = "DocNum")
	private String docNum;

	@XmlElement(name = "IsdDocType")
	private String isdDocType;

	@XmlElement(name = "ItcElg")
	private String itcElg;

	@XmlElement(name = "Gstin")
	private String gstin;

	@XmlElement(name = "RetPeriod")
	private String retPeriod;

	@XmlElement(name = "FromTime")
	private String fromTime;

	@XmlElement(name = "BoeCreatedDt")
	private String boeCreatedDt;

	@XmlElement(name = "BoeNum")
	private String boeNum;

	@XmlElement(name = "BoeRefDate")
	private String boeRefDate;

	@XmlElement(name = "PortCode")
	private String portCode;

	@XmlElement(name = "AmdhistKey")
	private String amdhistKey;

	@XmlElement(name = "IsAmendtBoe")
	private String isAmendtBoe;

	@XmlElement(name = "TradeName")
	private String tradeName;

	@XmlElement(name = "InvKey")
	private String invKey;

	@XmlElement(name = "ErpBatchId")
	private String erpBatchId;

	@XmlElement(name = "GetBatchId")
	private String getBatchId;

	@XmlElement(name = "UserRequestId")
	private String userRequestId;

	@XmlElement(name = "InvStatus")
	private String invStatus;

	@XmlElement(name = "CFlag")
	private String cflag;

	@XmlElement(name = "ApiSection")
	private String apiSection;

	@XmlElement(name = "RetType")
	private String retType;

	@XmlElement(name = "ModfOnDate")
	private String modfOnDate;

	@XmlElement(name = "ModfOnTime")
	private String modfOnTime;

	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "LineItem")
	private Get2AConsoForSectionLineItemDto lineItemDtos;

}
