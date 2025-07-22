/*package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*

@Entity
@Table(name = "GSTR2A_GET_BATCH")
@Setter
@Getter
public class GetGstr2aBatchEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CGSTIN")
	private String cGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "GET_TYPE")
	private String type;

	@Column(name = "UNCHANGED_COUNT")
	private Long unchangedCount;

	@Column(name = "CHANGED_COUNT")
	private Long changedCount;

	@Column(name = "DELETED_COUNT")
	private Long deletedCount;

	@Column(name = "NEW_COUNT")
	private Long newCount;
	
	@Column(name = "PROCESSING_STATUS")
	private String processingStatus;
	
	@Column(name = "API_SECTION")
	private String apiSection;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "START_TIME")
	private LocalDateTime startTime;

	@Column(name = "END_TIME")
	private LocalDateTime endTime;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name="INVOICE_COUNT")
	private Integer invCount;

	@OneToMany(mappedBy = "b2bBatchIdGstr2a", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetGstr2aB2bInvoicesHeaderEntity> gstr2aB2bInvoices = new HashSet<>();

	@OneToMany(mappedBy = "cdnBatchIdGstr2a", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetGstr2aCdnCdnaInvoicesHeaderEntity> cdnInvoices = new HashSet<>();

	@OneToMany(mappedBy = "b2bBatchIdAnx2", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetAnx2B2bInvoicesHeaderEntity> anx2B2bInvoices = new HashSet<>();

	@OneToMany(mappedBy = "deBatchIdAnx2", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetAnx2DeInvoicesHeaderEntity> anx2DeInvoices = new HashSet<>();

	@OneToMany(mappedBy = "sezwpBatchIdAnx2", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetAnx2SezwpInvoicesHeaderEntity> anx2SezwpInvoices = new HashSet<>();

	@OneToMany(mappedBy = "sezwopBatchIdAnx2", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetAnx2SezwopInvoicesHeaderEntity> anx2SezwopInvoices = new HashSet<>();

	@OneToMany(mappedBy = "isdcBatchIdAnx2", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetAnx2IsdcInvoicesHeaderEntity> anx2IsdcInvoices = new HashSet<>();

	@OneToMany(mappedBy = "itcSumBatchIdAnx2", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected Set<GetAnx2ItcSummaryInvoicesEntity> anx2ItcSumInvoices = new HashSet<>();


}
*/