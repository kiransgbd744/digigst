package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GSTR1_SAVE_NILNONEXMPT")
@Data
public class NilProcEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Column(name = "TABLE_SECTION")
	private String table;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "AMOUNT")
	private BigDecimal amt;

	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSaved;

	@Column(name = "IS_ERROR")
	private boolean isError;

	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

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
