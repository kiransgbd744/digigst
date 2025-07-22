/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * @author vishal.verma
 *
 */



import lombok.Data;

@Entity
@Data
@Table(name = "TBL_INWARD_EINVOICE_ERP_REQUEST")
public class InwardEInvoiceERPRequestEntity {
	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_INWARD_EINVOICE_ERP_REQUEST_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "ERROR_DESC")
	private String errorDescription;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "IS_ERP_PUSH")
	private Boolean isErpPush;

	@Column(name = "ERP_PUSH_DATE")
	private LocalDateTime erpPushDate;

	@Column(name = "TOTAL_RECORD")
	private Long totalRecord;

	
}
