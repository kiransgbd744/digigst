package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aAmdhistHeader {

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "RET_PERIOD")
	protected String retPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derRetPeriod;

	@Column(name = "PORT_CODE")
	protected String portCode;

	@Column(name = "BOE_NUM")
	protected Long boeNum;

	@Column(name = "BOE_CREATED_DATE")
	protected String boeDate;

	@Column(name = "AMD_TYPE")
	protected String amdType;

	@Column(name = "SGSTIN")
	protected String amdSgstin;

	@Column(name = "TRADE_NAME")
	protected String amdTradeName;

	@Column(name = "BOE_REF_DATE")
	protected String amdBoeRefDate;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal amdTaxVal;

	@Column(name = "IGST_AMT")
	protected BigDecimal amdIgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal amdCessAmt;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	protected boolean isDelete;
	
	@Column(name = "SUPPLY_TYPE")
	protected String supType;
	
	@Column(name = "AMDHIST_KEY")
	protected String amdHistKey;
	
	@Column(name = "PARENT_SECTION")
	protected String parentSection;
	
	@Column(name = "INV_KEY")
	protected String invKey;

}
