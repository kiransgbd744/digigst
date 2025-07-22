package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 
 * @author Anand3.M
 *
 */

public class GetDataSummaryResDto {
	
	private LocalDate receivedDate;
	private LocalDate docDate;
	private String gstin;
	private String period;
	private String type;
	private String section;
	private Integer count ;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal toatlTaxes= BigDecimal.ZERO;
	private BigDecimal igst= BigDecimal.ZERO;
	private BigDecimal cgst= BigDecimal.ZERO;
	private BigDecimal sgst= BigDecimal.ZERO;
	private BigDecimal cess= BigDecimal.ZERO;
	private String authToken;
	private String status;

	
	public LocalDate getReceivedDate() {
		return receivedDate;
	}

	public LocalDate getDocDate() {
		return docDate;
	}

	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
	}

	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getToatlTaxes() {
		return toatlTaxes;
	}

	public void setToatlTaxes(BigDecimal toatlTaxes) {
		this.toatlTaxes = toatlTaxes;
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

	public String getAuthToken() {
		return authToken;
	}

	public GetDataSummaryResDto() {
		super();
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	@Override
	public String toString() {
		return "ReturnSummaryResDto [receivedDate=" + receivedDate
				+ ", docDate=" + docDate + ", gstin=" + gstin + ", period="
				+ period + ", type=" + type + ", section=" + section
				+ ", count=" + count + ", taxableValue=" + taxableValue
				+ ", toatlTaxes=" + toatlTaxes + ", igst=" + igst + ", cgst="
				+ cgst + ", sgst=" + sgst + ", cess=" + cess + ", authToken="
				+ authToken + ", status=" + status + "]";
	}

	public GetDataSummaryResDto(String section) {
		this.section = section;
	}

	public GetDataSummaryResDto(LocalDate receivedDate, LocalDate docDate,
			String gstin, String period, String type, String section, Integer count,
			BigDecimal taxableValue, BigDecimal toatlTaxes, BigDecimal igst,
			BigDecimal cgst, BigDecimal sgst, BigDecimal cess, String authToken,
			String status) {
		this.receivedDate = receivedDate;
		this.docDate = docDate;
		this.gstin = gstin;
		this.period = period;
		this.type= type;
		this.section = section;
		this.count = count;
		this.taxableValue = 
				(taxableValue != null) ? taxableValue : BigDecimal.ZERO;
		this.toatlTaxes = (toatlTaxes!= null) ? toatlTaxes : BigDecimal.ZERO;
		this.igst = (igst!= null) ? igst : BigDecimal.ZERO;
		this.cgst = (cgst!= null) ? cgst : BigDecimal.ZERO;
		this.sgst = (sgst!= null) ? sgst : BigDecimal.ZERO;
		this.cess = (cess!= null) ? cess : BigDecimal.ZERO;
		this.authToken = authToken;
		this.status = status;
	}

	public GetDataSummaryResDto add(GetDataSummaryResDto newObj) {
		this.receivedDate = newObj.receivedDate;
		this.docDate = newObj.docDate;
		this.gstin = newObj.gstin;
		this.period = newObj.period;
		this.type = newObj.type;
		this.section = newObj.section;
		this.count = newObj.count;
		this.taxableValue = 
			this.taxableValue.add(newObj.taxableValue);
		this.toatlTaxes = this.toatlTaxes.add(newObj.toatlTaxes);
		this.igst = this.igst.add(newObj.igst);
		this.cgst = this.cgst.add(newObj.cgst);
		this.sgst = this.sgst.add(newObj.sgst);
		this.cess = this.cess.add(newObj.cess);
		this.authToken = newObj.authToken;
		this.status = newObj.status;
		return this;
	}
}
	
	

