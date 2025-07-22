/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Setter
@Getter
@ToString
@Table(name = "GETGSTR1_CDNR_CDNRA_DETAILS")
public class GetGstr1CDNRInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GSTIN")
	protected String sgstin;

	@Column(name = "RET_PERIOD")
	private String returnPeriod;

	@Column(name = "ACTION_REQUIRED")
	protected String actionRequired;

	@Column(name = "FROM_TIME")
	protected String fromTime;

	@Column(name = "CTIN")
	protected String cpGstin;

	@Column(name = "CFS")
	protected String counFillStatus;

	@Column(name = "FLAG")
	protected String flag;

	@Column(name = "CFLAG")
	protected String cPartyFlag;

	@Column(name = "ORG_PERIOD")
	protected LocalDate orgPeriod;

	@Column(name = "UPDBY")
	protected String uploadedBy;

	@Column(name = "CHKSUM")
	protected String chkSum;

	@Column(name = "ONT_NUM")
	protected String oriCredDebNum;

	@Column(name = "ONT_DDATE")
	protected LocalDate oriCredDebDate;

	@Column(name = "NTTY")
	protected String credDebRefVoucher;

	@Column(name = "NT_NUM")
	protected String credDebRefVoucherNum;

	@Column(name = "NT_DATE")
	protected String credDebRefVoucherDate;

	@Column(name = "PRE_GST")
	protected String preGstRegNote;

	@Column(name = "ORG_INV")
	protected String orgInvNum;

	@Column(name = "INV_DATE")
	protected LocalDate invDate;

	@Column(name = "VAL")
	protected BigDecimal totalNoteVal;

	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diffPercent;

	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Column(name = "TAX_VAL")
	protected BigDecimal TaxableValue;

	@Column(name = "SERIAL_NUM")
	protected int lineNumber;

	@Column(name = "RATE")
	protected BigDecimal Rate;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal csgstAmt;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;


}
*/