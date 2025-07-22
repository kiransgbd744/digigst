package com.ey.advisory.app.inward.einvoice;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@Entity
@Table(name = "TBL_GETIRN_PRECEEDING_DOC_DTL")
public class GetIrnPreceedingDocDetailEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIRN_PRECEEDING_DOC_DTL_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "HEADER_ID")
	private Long headerId;

	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDateTime docDate;

	@Column(name = "PRE_INV_NUM")
	private String preInvNum;

	@Column(name = "PRE_INV_DATE")
	private LocalDateTime preInvDate;

	@Column(name = "INV_REFERENCE")
	private String invReference;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "BATCHID")
	private Long batchId;
	
}
