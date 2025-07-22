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
@Table(name = "TBL_LD_OUTWARD_SUPPLY_GSTR1_VS_GSTR3B")
@Setter
@Getter
@ToString
public class LandingDashboardGstr1VsGstr3BEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Expose
	@Column(name="ENTITY_ID")
	private Long entityId; 
	
	@Expose
	@Column(name = "DERIVED_RET_PERIOD")
	private String derRetPeriod;
	
	@Expose
	@Column(name="G1_TAXABLE_VALUE")
	private BigDecimal g1TaxValue  = BigDecimal.ZERO;
	
	@Expose
	@Column(name="G1_TOTAL_TAX")
	private BigDecimal g1totalTax = BigDecimal.ZERO;
	
	@Expose
	@Column(name="G3_TAXABLE_VALUE")
	private BigDecimal g3TaxableValue = BigDecimal.ZERO;
	
	@Expose
	@Column(name="G3_TOTAL_TAX")
	private BigDecimal g3TotalTax = BigDecimal.ZERO;
	
	@Expose
	@Column(name="DIFF_TAXABLE_VALUE")
	private BigDecimal diffTaxVal = BigDecimal.ZERO;
	
	@Expose
	@Column(name="DIFF_TOTAL_TAX")
	private BigDecimal diffTotalTax = BigDecimal.ZERO;
		
	@Expose
	@Column(name="BATCH_ID")
	private Long batchId;
	
	@Expose
	@Column(name="IS_ACTIVE")
	private Boolean isActive;
	
	
}
