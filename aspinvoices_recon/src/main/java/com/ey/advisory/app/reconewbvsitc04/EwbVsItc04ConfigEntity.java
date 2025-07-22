package com.ey.advisory.app.reconewbvsitc04;

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
 * @author Ravindra V S
 *
 */
@Entity
@Table(name = "TBL_EWBVSITC04_RECON_CONFIG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EwbVsItc04ConfigEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "REQUESTID")
	private Long configId;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "FY")
	private String fy;

	@Column(name = "ITC_FROM_TAXPERIOD")
	private Integer itcFromTaxPeriod;
	
	@Column(name = "ITC_TO_TAXPERIOD")
	private Integer itcToTaxPeriod;
	
	@Column(name = "EWB_FROM_TAXPERIOD")
	private Integer ewbFromTaxPeriod;
	
	@Column(name = "EWB_TO_TAXPERIOD")
	private Integer ewbToTaxPeriod;
	
	@Column(name = "CRITERIA")
	private String criteria;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "INITIATED_BY")
	private String initiatedBy;

	@Column(name = "INITIATED_ON")
	private LocalDateTime initiatedOn;
	
	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

}
