package com.ey.advisory.app.data.entities.gstr6;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "GETGSTR6_ISD_DETAILS")
@Setter
@Getter
@ToString
public class GetGstr6IsdDetailsEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR6_ISD_DETAILS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "USER_TYPE")
	private String userType;

	@Column(name = "CPTY")
	private String cptyp;

	@Column(name = "STATE_CODE")
	private String stateCode;

	@Column(name = "ISD_DOC_TYPE")
	private String isdDocType;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "DOC_NUMBER")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "CRDR_DOC_NUMBER")
	private String crdrDocNum;

	@Column(name = "CRDR_DOC_DATE")
	private LocalDate crdrDocDate;

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

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "ELIGIBLE_INDICATOR")
	private String elgIndicator;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

}
