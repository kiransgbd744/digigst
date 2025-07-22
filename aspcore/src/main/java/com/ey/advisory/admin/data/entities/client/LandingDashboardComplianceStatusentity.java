package com.ey.advisory.admin.data.entities.client;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_LD_RETURN_COMPLIANCE_STATUS")
@Setter
@Getter
@ToString
public class LandingDashboardComplianceStatusentity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Expose
	@Column(name="ENTITY_ID")
	private Long entityId; 
	@Expose
	@Column(name = "GSTIN")
	private String gstin;
	
	@Expose
	@Column(name = "DERIVED_RET_PERIOD")
	private String derRetPeriod;
	
	@Expose
	@Column(name="REG_TYPE")
	private String regType;
	
	@Expose
	@Column(name="STATE_NAME")
	private String stateName;
	
	@Expose
	@Column(name="OUTWARD_SUPPLY")
	private BigDecimal outwardSupply = BigDecimal.ZERO;
	
	@Expose
	@Column(name="TOTAL_TAX")
	private BigDecimal totalTax = BigDecimal.ZERO;
	
	@Expose
	@Column(name="IGST")
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	@Column(name="CGST")
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name="SGST")
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name="CESS")
	private BigDecimal cess = BigDecimal.ZERO;
	

	@Expose
	@Column(name="CASH_LEDGER")
	private BigDecimal cashLedger = BigDecimal.ZERO;
	

	@Expose
	@Column(name="CREDIT_LEDGER")
	private BigDecimal creditLedger = BigDecimal.ZERO;
	
	@Expose
	@Column(name="LIABILITY_LEDGER")
	private BigDecimal liabLdger = BigDecimal.ZERO;
	
	@Expose
	@Column(name="BATCH_ID")
	private Long batchId;
	
	@Expose
	@Column(name="IS_ACTIVE")
	private Boolean isActive;
	
	
}
