package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aTDSADetailsHeader {

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "RET_PERIOD")
	protected String returnPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;

	@Column(name = "GSTIN_DED")
	protected String gstindeductee;

	@Column(name = "ORG_GSTIN_DED")
	protected String orggstindeductee;

	@Column(name = "AMT_DED")
	protected BigDecimal amtdeducted;

	@Column(name = "ORG_AMT_DED")
	protected BigDecimal orgamtdeducted;

	@Column(name = "ORG_MONTH")
	protected String mnthded;

	@Column(name = "IGST_AMT")
	protected BigDecimal integratedamt;

	@Column(name = "CGST_AMT")
	protected BigDecimal centralamt;

	@Column(name = "SGST_AMT")
	protected BigDecimal stateamt;
	
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
	
	@Column(name = "INV_KEY")
	protected String inkKey;
}
