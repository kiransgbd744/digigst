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

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "MASTER_CUSTOMER")
@Setter
@Getter
@ToString
public class MasterCustomerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "CUST_GSTIN_PAN")
	private String recipientGstnOrPan;

	@Column(name = "LEGAL_NAME")
	private String legalName;

	@Column(name = "TRADE_NAME")
	private String tradeName;

	@Column(name = "RECIPIENT_TYPE")
	private String recipientType;

	@Column(name = "RECIPIENT_CODE")
	private String recipientCode;

	@Column(name = "OUTSIDE_INDIA")
	private String outSideIndia;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "MOBILE_NUM")
	private String mobileNum;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CUSTOMER_KEY")
	protected String custKey;

	public MasterCustomerEntity add(MasterCustomerEntity custMaster) {
		this.recipientGstnOrPan = custMaster.recipientGstnOrPan;
		this.legalName = custMaster.legalName;
		this.tradeName = custMaster.tradeName;
		this.recipientType = custMaster.recipientType;
		this.recipientCode = custMaster.recipientCode;
		this.outSideIndia = custMaster.outSideIndia;
		this.emailId = custMaster.emailId;
		this.custKey = custMaster.custKey;
		this.mobileNum = custMaster.mobileNum;
		this.fileId = custMaster.fileId;
		this.fileName = custMaster.fileName;
		return this;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recipientGstnOrPan == null) ? 0
				: recipientGstnOrPan.hashCode());
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
		MasterCustomerEntity other = (MasterCustomerEntity) obj;
		if (recipientGstnOrPan == null) {
			if (other.recipientGstnOrPan != null)
				return false;
		} else if (!recipientGstnOrPan.equals(other.recipientGstnOrPan))
			return false;
		return true;
	}
}