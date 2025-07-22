package com.ey.advisory.app.data.entities.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR9_DIGI_COMPUTE")
public class Gstr9DigiComputeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "GSTR9_CONFIG_ID")
	private Long configId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "FINANCIAL_YEAR")
	private Integer financialYear;

	@Column(name = "DERIVED_RETPERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "RET_PERIOD")
	private String retPeriod;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "SUBSECTION")
	private String subSection;

	@Column(name = "SUBSECTION_NAME")
	private String subSectionName;

	@Column(name = "SOURCE_TABLE_NAME")
	private String sourceTableName;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmount = BigDecimal.ZERO;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmount = BigDecimal.ZERO;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmount = BigDecimal.ZERO;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmount = BigDecimal.ZERO;

	@Column(name = "HSNSAC")
	private String hsnsac;

	@Column(name = "UQC")
	private String uqc;

	@Column(name = "QTY")
	private BigDecimal qty = BigDecimal.ZERO;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRate = BigDecimal.ZERO;

	@Column(name = "ISCONCESSTIONAL")
	private String isConcesstional;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;
}
