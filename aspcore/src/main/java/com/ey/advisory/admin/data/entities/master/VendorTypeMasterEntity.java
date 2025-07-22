package com.ey.advisory.admin.data.entities.master;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Entity
@Table(name = "VENDOR_TYPE_MASTER")
@Data
public class VendorTypeMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "DESCRIPTION")
	private String vendorType;

	@Expose
	@Column(name = "CODE")
	private String code;

	@Expose
	@Column(name = "HSN")
	private Integer hsn;
}
