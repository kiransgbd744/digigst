package com.ey.advisory.app.data.entities.gstr6;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "GETGSTR6_ITC_DETAILS")
@Setter
@Getter
@ToString
public class GetGstr6ItcDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "TOTAL_ITC")
	private boolean totalItc;

	@Column(name = "INELG_ITC")
	private boolean inElgItc;

	@Column(name = "ELG_ITC")
	private boolean elgItc;

	@Column(name = "ISD_ITC_CROSS")
	private boolean isdItcCross;

	@Column(name = "DES")
	private BigDecimal des;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "IGST_AMT_AS_IGST")
	private BigDecimal igstAmtIgst;

	@Column(name = "IGST_AMT_AS_SGST")
	private BigDecimal igstAmtSgst;

	@Column(name = "IGST_AMT_AS_CGST")
	private BigDecimal igstAmtCgst;

	@Column(name = "SGST_AMT_AS_SGST")
	private BigDecimal sgstAmtSgst;

	@Column(name = "SGST_AMT_AS_IGST")
	private BigDecimal sgstAmtIgst;

	@Column(name = "CGST_AMT_AS_IGST")
	private BigDecimal cgstAmtIgst;

	@Column(name = "CGST_AMT_AS_CGST")
	private BigDecimal cgstAmtCgst;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "GO_LIVE")
	private String goLive;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

}
