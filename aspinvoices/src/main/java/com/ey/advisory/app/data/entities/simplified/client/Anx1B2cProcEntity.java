/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "ANXSAVE_B2C")
@Data
public class Anx1B2cProcEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercent;

	@Column(name = "SECTION7_OF_IGST_FLAG")
	private String sec7;

	@Column(name = "RFNDELG")
	private String rfndElg;

	@Column(name = "POS")
	private Integer pos;

	@Column(name = "TAX_RATE")
	private BigDecimal rate;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "IGST")
	private BigDecimal igst;

	@Column(name = "CGST")
	private BigDecimal cgst;

	@Column(name = "SGST")
	private BigDecimal sgst;

	@Column(name = "CESS")
	private BigDecimal cess;

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

	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDate sentToGSTNDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDate savedToGSTNDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

}
