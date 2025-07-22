package com.ey.advisory.service.gstr1.sales.register;

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
@Table(name = "TBL_SRVSDIGIGST_RECON_CONFIG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesRegisterConfigEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long configId;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "CRITERIA")
	private String criteria;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "FROM_TAX_PERIOD")
	private Integer fromTaxPeriod;

	@Column(name = "TO_TAX_PERIOD")
	private Integer toTaxPeriod;

	@Column(name = "INITIATED_ON")
	private LocalDateTime initatedOn;

	@Column(name = "INITIATED_BY")
	private String initiatedBy;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

	@Column(name = "STATUS")
	private String status;
}
