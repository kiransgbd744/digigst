package com.ey.advisory.admin.data.entities.master;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="EWB_STATUS_MASTER")
@Setter
@Getter
public class EwbStatusMasterEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID",nullable=false)
	private Long id;
	
	@Column(name="EWB_STATUS")
	private String ewbStatus;
	
	@Column(name="EWB_STATUS_DESCRIPTION")
	private String ewbStatusDescription;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_ON")
	private LocalDate createdOn;
}
