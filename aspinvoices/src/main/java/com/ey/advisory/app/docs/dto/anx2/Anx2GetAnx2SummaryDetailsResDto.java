package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author Anand3.M
 *
 */

public class Anx2GetAnx2SummaryDetailsResDto {

	private String table;

	private String docType;

	private int records;

	private BigDecimal invoiceValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal totalTax = BigDecimal.ZERO;

	private BigDecimal igst = BigDecimal.ZERO;

	private BigDecimal cgst = BigDecimal.ZERO;

	private BigDecimal sgst = BigDecimal.ZERO;

	private BigDecimal cess = BigDecimal.ZERO;

	private List<Anx2GetAnx2SummaryItemsResDto> items = null;

	public Anx2GetAnx2SummaryDetailsResDto() {
		super();

	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public List<Anx2GetAnx2SummaryItemsResDto> getItems() {
		return items;
	}

	public void setItems(List<Anx2GetAnx2SummaryItemsResDto> items) {
		this.items = items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cess == null) ? 0 : cess.hashCode());
		result = prime * result + ((cgst == null) ? 0 : cgst.hashCode());
		result = prime * result + ((docType == null) ? 0 : docType.hashCode());
		result = prime * result + ((igst == null) ? 0 : igst.hashCode());
		result = prime * result
				+ ((invoiceValue == null) ? 0 : invoiceValue.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + records;
		result = prime * result + ((sgst == null) ? 0 : sgst.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		result = prime * result
				+ ((taxableValue == null) ? 0 : taxableValue.hashCode());
		result = prime * result
				+ ((totalTax == null) ? 0 : totalTax.hashCode());
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
		Anx2GetAnx2SummaryDetailsResDto other = (Anx2GetAnx2SummaryDetailsResDto) obj;
		if (cess == null) {
			if (other.cess != null)
				return false;
		} else if (!cess.equals(other.cess))
			return false;
		if (cgst == null) {
			if (other.cgst != null)
				return false;
		} else if (!cgst.equals(other.cgst))
			return false;
		if (docType == null) {
			if (other.docType != null)
				return false;
		} else if (!docType.equals(other.docType))
			return false;
		if (igst == null) {
			if (other.igst != null)
				return false;
		} else if (!igst.equals(other.igst))
			return false;
		if (invoiceValue == null) {
			if (other.invoiceValue != null)
				return false;
		} else if (!invoiceValue.equals(other.invoiceValue))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (records != other.records)
			return false;
		if (sgst == null) {
			if (other.sgst != null)
				return false;
		} else if (!sgst.equals(other.sgst))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		if (taxableValue == null) {
			if (other.taxableValue != null)
				return false;
		} else if (!taxableValue.equals(other.taxableValue))
			return false;

		if (totalTax == null) {
			if (other.totalTax != null)
				return false;
		} else if (!totalTax.equals(other.totalTax))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Anx2GetAnx2SummaryDetailsResDto [table=" + table + ", docType="
				+ docType + ", records=" + records + ", invoiceValue="
				+ invoiceValue + ", taxableValue=" + taxableValue
				+ ", totalTax=" + totalTax + ", igst=" + igst + ", cgst=" + cgst
				+ ", sgst=" + sgst + ", cess=" + cess + ", items=" + items
				+ "]";
	}

}
