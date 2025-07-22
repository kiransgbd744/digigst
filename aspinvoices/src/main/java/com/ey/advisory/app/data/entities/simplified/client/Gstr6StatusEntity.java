package com.ey.advisory.app.data.entities.simplified.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author SriBhavya
 *
 */
@Entity
@Table(name = "GSTR6_STATUS")
@Setter
@Getter
public class Gstr6StatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "BATCH_ID")
	private Long batchId;
	
	@Column(name = "ISD_GSTIN")
	private String isdGstin;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "CURRENT_RET_PERIOD")
	private String currentRetPeriod;
	
	@Column(name = "FROM_DERIVED_RET_PERIOD")
	private Integer fromDerRetPeroid;
	
	@Column(name = "TO_DERIVED_RET_PERIOD")
	private Integer toDerRetPeroid;
	
	@Column(name = "DIGI_GST_STATUS")
	private String digiGstStatus;
	
	@Column(name = "GSTIN_STATUS")
	private String gstinStatus;
	
	@Column(name = "CREDIT_STATUS")
	private String creditStatus;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;	
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "DIGI_GST_TIMESTAMP")
	private LocalDateTime digiGstTimeStamp;
	
	@Column(name = "GSTIN_TIMESTAMP")
	private LocalDateTime gstinTimeStamp;
	
	@Column(name = "CREDIT_TIMESTAMP")
	private LocalDateTime creditTimeStamp;
}

