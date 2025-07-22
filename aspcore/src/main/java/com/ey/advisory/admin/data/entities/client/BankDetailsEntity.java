package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "BANK_DETAILS")
@Data
public class BankDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GROUP_ID")
	private Long groupId;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN_ID")
	private Long gstinId;

	@Column(name = "BANK_ACCOUNT")
	private String bankAcct;

	@Column(name = "IFSC_CODE")
	private String ifscCode;

	@Column(name = "BENEFICIARY")
	private String beneficiary;

	@Column(name = "PAYMENT_DUE_DATE")
	private LocalDate paymentDueDate;

	@Column(name = "PAYMENT_TERM")
	private String paymentTerm;

	@Column(name = "PAYMENT_INSTRUCTION")
	private String paymentInstruction;
	
	@Column(name = "BANK_NAME")
	private String bankName;
	
	@Column(name = "BANK_ADDRESS")
	private String bankAdd;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
}
