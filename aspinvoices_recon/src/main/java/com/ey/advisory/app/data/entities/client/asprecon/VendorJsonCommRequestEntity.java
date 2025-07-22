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
@Table(name = "VENDOR_JSON_COMM_REQUEST")
@Getter
@Setter
@ToString
public class VendorJsonCommRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "VENDOR_GSTIN")
	private String vendorGstin;

	@Column(name = "FILEPATH")
	private String filePath;
	
	@Column(name = "DOC_ID")
	private String docId;

}
