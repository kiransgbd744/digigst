package com.ey.advisory.service.hsn.summary;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Shashikant.Shukla
 *
 */
@Entity
@Table(name = "TBL_HSN_SMRY_CONFIG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HsnSummaryConfigEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "RECON_CONFIG_ID")
	private Long configId;
	
//	@Column(name = "ENTITY_ID")
//	private Long entityId;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "FROM_TAX_PERIOD")
	private Integer fromTaxPeriod;

	@Column(name = "TO_TAX_PERIOD")
	private Integer toTaxPeriod;

	@Column(name = "CREATED_ON")
	private LocalDateTime initatedOn;

	@Column(name = "CREATED_BY")
	private String initiatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime completedOn;
	
	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "STATUS")
	private String status;
}
