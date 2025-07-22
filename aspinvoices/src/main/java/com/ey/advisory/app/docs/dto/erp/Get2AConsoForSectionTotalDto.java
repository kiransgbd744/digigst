package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class Get2AConsoForSectionTotalDto {

	private Long id;

	private String sectionName;

	private String sgstin;
	private String cgstin;
	private String ctin;
	private String taxPeriod;
	private String cfs;
	private String chksum;
	private String suppInvNum;
	private String suppInvDate;
	private BigDecimal suppInvVal = BigDecimal.ZERO;
	private String orgInvNum;
	private String orgInvDate;
	private String pos;
	private String rchrg;
	private String invType;
	private BigDecimal diffPercent = BigDecimal.ZERO;
	private BigDecimal igstAmt = BigDecimal.ZERO;
	private BigDecimal cgstAmt = BigDecimal.ZERO;
	private BigDecimal sgstAmt = BigDecimal.ZERO;
	private BigDecimal cessAmt = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private String actionTaken;
	private String dataCategory;
	private String suppTdLgName;
	private String cfsGstr3b;
	private String cancelDate;
	private String fileDate;
	private String filePeriod;
	private String orgInvAmdPer;
	private String orgInvAmdTyp;
	private String supplyType;
	private String irnNum;
	private String irnGenDate;
	private String irnSourceType;
	private String noteDate;
	private String noteNumber;
	private String noteType;
	private BigDecimal noteValue = BigDecimal.ZERO;
	private String orgNoteDate;
	private String orgNoteNumber;
	private String orgNoteType;
	private String invNumber;
	private String pgst;
	private String invDate;
	private String uplodedBy;
	private String dflag;
	private String docDate;
	private String docNum;
	private String isdDocType;
	private String itcElg;
	private String gstin;
	private String retPeriod;
	private String fromTime;
	private String boeCreatedDt;
	private String boeNum;
	private String boeRefDate;
	private String portCode;
	private String amdhistKey;
	private String isAmendtBoe;
	private String tradeName;
	private String invKey;
	private String erpBatchId;
	private String getBatchId;
	private String userRequestId;
	private String invStatus;
	private String cflag;
	private String apiSection;
	private String retType;
	private String modifiedTime;


	//Item values stored
	private String itemNumber;
	private BigDecimal itemIgstAmt = BigDecimal.ZERO;
	private BigDecimal itemCgstAmt = BigDecimal.ZERO;
	private BigDecimal itemCessAmt = BigDecimal.ZERO;
	private BigDecimal itemSgstAmt = BigDecimal.ZERO;
	private BigDecimal itemTaxableValue = BigDecimal.ZERO;
	private BigDecimal taxRate = BigDecimal.ZERO;
}
