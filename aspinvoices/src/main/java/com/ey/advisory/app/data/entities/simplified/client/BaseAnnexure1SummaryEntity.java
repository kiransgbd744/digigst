package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class BaseAnnexure1SummaryEntity {
		
		private String docType;

		private String tableSection;

		private BigDecimal taxbleValue;

		private BigDecimal taxPayable;

		private BigDecimal invoiceValue;

		private BigDecimal igstAmt;

		private BigDecimal cgstAmt;

		private BigDecimal sgstAmt;

		private BigDecimal cessAmt;

		private BigDecimal recordCount;

		/**
		 * @return the docType
		 */
		public String getDocType() {
			return docType;
		}

		/**
		 * @param docType the docType to set
		 */
		public void setDocType(String docType) {
			this.docType = docType;
		}

		/**
		 * @return the tableSection
		 */
		public String getTableSection() {
			return tableSection;
		}

		/**
		 * @param tableSection the tableSection to set
		 */
		public void setTableSection(String tableSection) {
			this.tableSection = tableSection;
		}

		/**
		 * @return the taxbleValue
		 */
		public BigDecimal getTaxbleValue() {
			return taxbleValue;
		}

		/**
		 * @param taxbleValue the taxbleValue to set
		 */
		public void setTaxbleValue(BigDecimal taxbleValue) {
			this.taxbleValue = taxbleValue;
		}

		/**
		 * @return the taxPayable
		 */
		public BigDecimal getTaxPayable() {
			return taxPayable;
		}

		/**
		 * @param taxPayable the taxPayable to set
		 */
		public void setTaxPayable(BigDecimal taxPayable) {
			this.taxPayable = taxPayable;
		}

		/**
		 * @return the invoiceValue
		 */
		public BigDecimal getInvoiceValue() {
			return invoiceValue;
		}

		/**
		 * @param invoiceValue the invoiceValue to set
		 */
		public void setInvoiceValue(BigDecimal invoiceValue) {
			this.invoiceValue = invoiceValue;
		}

		/**
		 * @return the igstAmt
		 */
		public BigDecimal getIgstAmt() {
			return igstAmt;
		}

		/**
		 * @param igstAmt the igstAmt to set
		 */
		public void setIgstAmt(BigDecimal igstAmt) {
			this.igstAmt = igstAmt;
		}

		/**
		 * @return the cgstAmt
		 */
		public BigDecimal getCgstAmt() {
			return cgstAmt;
		}

		/**
		 * @param cgstAmt the cgstAmt to set
		 */
		public void setCgstAmt(BigDecimal cgstAmt) {
			this.cgstAmt = cgstAmt;
		}

		/**
		 * @return the sgstAmt
		 */
		public BigDecimal getSgstAmt() {
			return sgstAmt;
		}

		/**
		 * @param sgstAmt the sgstAmt to set
		 */
		public void setSgstAmt(BigDecimal sgstAmt) {
			this.sgstAmt = sgstAmt;
		}

		/**
		 * @return the cessAmt
		 */
		public BigDecimal getCessAmt() {
			return cessAmt;
		}

		/**
		 * @param cessAmt the cessAmt to set
		 */
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

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format(
					"BaseAnnexure1SummaryEntity [docType=%s, tableSection=%s,"
					+ "taxbleValue=%s, taxPayable=%s, invoiceValue=%s, "
					+ "igstAmt=%s, cgstAmt=%s, sgstAmt=%s, cessAmt=%s, "
					+ "recordCount=%s]",
					docType, tableSection, taxbleValue, taxPayable,
					invoiceValue, igstAmt, cgstAmt, sgstAmt, cessAmt,
					recordCount);
		}

}
