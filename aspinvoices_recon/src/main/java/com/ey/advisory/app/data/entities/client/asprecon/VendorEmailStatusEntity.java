package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name= "VENDOR_EMAIL_STATUS")
@Getter
@Setter
@ToString
public class VendorEmailStatusEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Expose
	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Expose
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Expose
	@Column(name="STATUS")
	private String status;
	
	@Expose
	@Column(name="RECON_TYPE")
	private String reconType;
	
	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Column(name = "IS_DELETE")
	private Boolean isDelete;

}
