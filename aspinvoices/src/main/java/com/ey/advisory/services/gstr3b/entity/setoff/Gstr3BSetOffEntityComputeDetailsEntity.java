
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
@Table(name = "GSTR3B_SETOFF_COMPUTE_DETAILS")
public class Gstr3BSetOffEntityComputeDetailsEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_SETOFF_COMPUTE_DETAILS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "REQUEST_ID")
	private Long requestId;

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
	
	/*@ManyToOne
	@JoinColumn(name = "REQUEST_ID", referencedColumnName = "ID")
	protected Gstr3BSetOffEntityComputeConfigEntity requestId;*/
}
