package com.ey.advisory.admin.data.entities.master;


import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Siva.Nandam
 *
 */
@Entity
@Table(name = "MASTER_CATEGORY")
public class CatogeryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "PARTICULARS")
	private String particulars;

	@Expose
	@Column(name = "EXPECTED_INPUT")
	private String exceptedInputs;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the particulars
	 */
	public String getParticulars() {
		return particulars;
	}

	/**
	 * @param particulars the particulars to set
	 */
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	/**
	 * @return the exceptedInputs
	 */
	public String getExceptedInputs() {
		return exceptedInputs;
	}

	/**
	 * @param exceptedInputs the exceptedInputs to set
	 */
	public void setExceptedInputs(String exceptedInputs) {
		this.exceptedInputs = exceptedInputs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DocCatogeryEntity [id=" + id + ", particulars=" + particulars
				+ ", exceptedInputs=" + exceptedInputs + "]";
	}

	
}