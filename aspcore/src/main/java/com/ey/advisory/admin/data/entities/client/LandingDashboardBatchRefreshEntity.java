package com.ey.advisory.admin.data.entities.client;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_LD_BATCH_REFRESH")
@Setter
@Getter
@ToString
public class LandingDashboardBatchRefreshEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_LD_BATCH_REFRESH_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "BATCH_ID", nullable = false)
	private Long batchId;
	
	@Expose
	@Column(name="ENTITY_ID")
	private Long entityId; 
	@Expose
	@Column(name = "DERIVED_RET_PERIOD")
	private String derRetPeriod;
	
	@Expose
	@Column(name="STATUS")
	private String status;
	
	@Expose
	@JsonIgnore
	@Column(name="LAST_REFRESHED_ON")
	private  LocalDateTime refreshTime;
	
	@Expose
	@Column(name="CREATE_DATE")
	private  LocalDateTime createdOn;
	
	@Expose
	@Column(name="CREATE_BY")
	private String createdBy;
	
	@Expose
	@Column(name="UPDATE_DATE")
	private  LocalDateTime updatedOn;
	
	@Expose
	@Column(name="IS_DELETE")
	private Boolean isdelete;
	
	
}
