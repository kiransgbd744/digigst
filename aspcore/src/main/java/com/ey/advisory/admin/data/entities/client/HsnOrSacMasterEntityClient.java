package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "HSNSAC_CATEGORY")
public class HsnOrSacMasterEntityClient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "HSNSAC",  length = 10)
	private String hsnSac;

	@Expose
	@Column(name = "SGSTRTSTRANGE", precision = 6, scale = 3)
	private BigDecimal sgstRtstRange;

	@Expose
	@Column(name = "SGSTRTENDRANGE", precision = 6, scale = 3)
	private BigDecimal sgstTrTendRange;

	@Expose
	@Column(name = "SGSTRT", precision = 6, scale = 3)
	private BigDecimal sgstRt;

	@Expose
	@Column(name = "STATECODE", length = 2)
	private String stateCode;

	@Expose
	@Column(name = "CATEGORY", length = 30)
	private String category;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getHsnSac() {
		return hsnSac;
	}

	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	public BigDecimal getSgstRtstRange() {
		return sgstRtstRange;
	}

	public void setSgstRtstRange(BigDecimal sgstRtstRange) {
		this.sgstRtstRange = sgstRtstRange;
	}

	public BigDecimal getSgstTrTendRange() {
		return sgstTrTendRange;
	}

	public void setSgstTrTendRange(BigDecimal sgstTrTendRange) {
		this.sgstTrTendRange = sgstTrTendRange;
	}

	public BigDecimal getSgstRt() {
		return sgstRt;
	}

	public void setSgstRt(BigDecimal sgstRt) {
		this.sgstRt = sgstRt;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}