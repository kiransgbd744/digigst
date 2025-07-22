package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class BaseGstr1SummaryEntity {

	//@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;

	//@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedTaxPeriod;

	//@Column(name = "TABLE_SECTION")
	private String tableSection;

	//@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxbleValue;

	//@Column(name = "TAX_PAYABLE")
	private BigDecimal taxPayable;

	//@Column(name = "INVOICE_VALUE")
	private BigDecimal invoiceValue;

	//@Column(name = "IGST")
	private BigDecimal igstAmt;

	//@Column(name = "CGST")
	private BigDecimal cgstAmt;

	//@Column(name = "SGST")
	private BigDecimal sgstAmt;

	//@Column(name = "CESS")
	private BigDecimal cessAmt;

	//@Column(name = "RECORD_COUNT")
	private BigDecimal recordCount;

	public String getTableSection() {
		return tableSection;
	}

	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}

	public String getSupplierGstin() {
		return supplierGstin;
	}

	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
	}

	public Integer getDerivedTaxPeriod() {
		return derivedTaxPeriod;
	}

	public void setDerivedTaxPeriod(Integer derivedTaxPeriod) {
		this.derivedTaxPeriod = derivedTaxPeriod;
	}

	public BigDecimal getTaxbleValue() {
		return taxbleValue;
	}

	public void setTaxbleValue(BigDecimal taxbleValue) {
		this.taxbleValue = taxbleValue;
	}

	public BigDecimal getTaxPayable() {
		return taxPayable;
	}

	public void setTaxPayable(BigDecimal taxPayable) {
		this.taxPayable = taxPayable;
	}

	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	/**
	 * @return the recordCount
	 */
	public BigDecimal getRecordCount() {
		return recordCount;
	}

	/**
	 * @param recordCount the recordCount to set
	 */
	public void setRecordCount(BigDecimal recordCount) {
		this.recordCount = recordCount;
	}

}
