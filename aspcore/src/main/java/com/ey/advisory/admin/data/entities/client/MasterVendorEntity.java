package com.ey.advisory.admin.data.entities.client;

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
@Table(name = "MASTER_VENDOR")
@Setter
@Getter
@ToString
public class MasterVendorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "CUST_GSTIN_PAN")
	private String custGstinPan;

	@Column(name = "SUPPLIER_CODE")
	private String supplierCode;

	@Column(name = "SUPPLIER_GSTIN_PAN")
	private String supplierGstinPan;

	@Column(name = "LEGAL_NAME")
	private String legalName;

	@Column(name = "SUPPLIER_TYPE")
	private String supplierType;

	@Column(name = "OUTSIDE_INDIA")
	private String outSideIndia;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "MOBILE_NUM")
	private String mobileNum;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "VENDOR_KEY")
	protected String vendorKey;

	public MasterVendorEntity add(MasterVendorEntity vendorMaster) {
		this.custGstinPan = vendorMaster.custGstinPan;
		this.supplierCode = vendorMaster.supplierCode;
		this.supplierGstinPan = vendorMaster.supplierGstinPan;
		this.legalName = vendorMaster.legalName;
		this.supplierType = vendorMaster.supplierType;
		this.outSideIndia = vendorMaster.outSideIndia;
		this.emailId = vendorMaster.emailId;
		this.outSideIndia = vendorMaster.outSideIndia;
		this.mobileNum = vendorMaster.mobileNum;
		this.fileId = vendorMaster.fileId;
		this.fileName = vendorMaster.fileName;
		return this;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((supplierGstinPan == null) ? 0
				: supplierGstinPan.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MasterVendorEntity other = (MasterVendorEntity) obj;
		if (supplierGstinPan == null) {
			if (other.supplierGstinPan != null)
				return false;
		} else if (!supplierGstinPan.equals(other.supplierGstinPan))
			return false;
		return true;
	}
}
