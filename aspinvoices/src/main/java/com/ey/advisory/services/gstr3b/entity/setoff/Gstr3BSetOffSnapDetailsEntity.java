
package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Vishal.verma 
 *
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "GSTR3B_SETOFF_SNAP_DETAILS")
public class Gstr3BSetOffSnapDetailsEntity {

	public Gstr3BSetOffSnapDetailsEntity(String gstin, String taxPeriod,
			String section, Boolean isDelete, String createdBy,
			LocalDateTime createdOn, String updatedBy,
			LocalDateTime updatedOn) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.section = section;
		this.isDelete = isDelete;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
	}

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_SETOFF_SNAP_DETAILS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "IGST")
	private BigDecimal igst;

	@Column(name = "CGST")
	private BigDecimal cgst;

	@Column(name = "SGST")
	private BigDecimal sgst;

	@Column(name = "CESS")
	private BigDecimal cess;
	
	@Column(name = "TOTAL_TAX")
	private BigDecimal totalTax;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
}
