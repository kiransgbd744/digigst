package com.ey.advisory.app.data.entities.client.asprecon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "VENDOR_REQUEST_RGSTIN")
@Getter
@Setter
@ToString
public class VendorReqRecipientGstinEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Column(name="RECIPIENT_GSTIN")
	private String recipientGstin;
	
	
	
}
