package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "MASTER_RATE")
public class RateMasterEntityClient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "IGST")
	private BigDecimal igst;

	@Expose
	@Column(name = "CGST")
	private BigDecimal cgst;

	@Expose
	@Column(name = "SGST")
	private BigDecimal sgst;

	@Expose

	@Column(name = "EFFECTIVE_DATE")
	private LocalDate effectiveDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public String toString() {
		return "RateMasterEntity [id=" + id + ", igst=" + igst + ", cgst="
				+ cgst + ", sgst=" + sgst + ", effectiveDate=" + effectiveDate
				+ "]";
	}

}
