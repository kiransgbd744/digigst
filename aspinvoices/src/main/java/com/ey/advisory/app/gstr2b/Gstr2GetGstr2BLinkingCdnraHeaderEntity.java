package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GETGSTR2B_LINKING_CDNRA_HEADER")
public class Gstr2GetGstr2BLinkingCdnraHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2B_CDNRA_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CHECKSUM")
	private String checksum;

	@Column(name = "RGSTIN")
	private String rGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "GENDT")
	private LocalDateTime genDate;

	@Column(name = "SGSTIN")
	private String sGstin;

	@Column(name = "SUPPLIER_TRADE_NAME")
	private String supTradeName;

	@Column(name = "SUPPLIER_FILING_DT")
	private LocalDateTime supFilingDate;

	@Column(name = "SUPPLIER_FILING_PRD")
	private String supFilingPeriod;

	@Column(name = "NOTE_NUMBER")
	private String noteNumber;

	@Column(name = "NOTE_DATE")
	private LocalDateTime noteDate;

	@Column(name = "NOTE_TYPE")
	private String noteType;

	@Column(name = "INV_TYPE")
	private String invoiceType;

	@Column(name = "ORG_NOTE_NUMBER")
	private String orgNoteNumber;

	@Column(name = "ORG_NOTE_DATE")
	private LocalDateTime orgNoteDate;

	@Column(name = "ORG_NOTE_TYPE")
	private String orgNoteType;

	@Column(name = "NOTE_VALUE")
	private BigDecimal noteValue;

	@Column(name = "POS")
	private String pos;

	@Column(name = "REV")
	private String rev;

	@Column(name = "ITCAVAL")
	private String itcAvailable;

	@Column(name = "RSN")
	private String rsn;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercent;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "INVOCATION_ID")
	private Long invocationId;
	
	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Column(name = "LINKING_DOC_KEY")
	private String lnkingDocKey;

	@Column(name = "ITC_REJECTED")
	private boolean itcRejected;
	
	@Column(name = "ITC_RED_REQ")
	private String itcRedReq;
	
	@Column(name = "DECLARED_IGST")
	private BigDecimal declaredIgst;
	
	@Column(name = "DECLARED_SGST")
	private BigDecimal declaredSgst;
	
	@Column(name = "DECLARED_CGST")
	private BigDecimal declaredCgst;
	
	@Column(name = "DECLARED_CESS")
	private BigDecimal declaredCess;
	
	@Column(name = "IMS_STATUS")
	private String imsStatus;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<Gstr2GetGstr2BLinkingCdnraItemEntity> lineItems = new ArrayList<>();
}
