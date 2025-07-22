package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aTcsInvoicesHeader {

	@Column(name = "CGSTIN")
	private String cgstin;
	
	@Column(name = "TAX_PERIOD")
	private String retPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derRetPeriod;

	@Column(name = "CHKSUM")
	private String chkSum;
	
	@Column(name = "E_TIN")
	private String eTin;

	@Column(name = "MERCHANT_ID")
	private String merchantId;
	
	@Column(name = "SUPPLIER_INV_VALUE")
	private BigDecimal supVal;
	
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxbleVal;
	
	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;
	
	
	/*@Column(name = "IGST_RATE")
	private BigDecimal igstRate;

	@Column(name = "CGST_RATE")
	private BigDecimal cgstRate;

	@Column(name = "SGST_RATE")
	private BigDecimal sgstRate;

	@Column(name = "CESS_RATE")
	private BigDecimal cessRate;
*/	
	
	/*@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;*/
	
	@Column(name = "BATCH_ID")
	private Long batchId;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "INV_KEY")
	protected String inkKey;
}
