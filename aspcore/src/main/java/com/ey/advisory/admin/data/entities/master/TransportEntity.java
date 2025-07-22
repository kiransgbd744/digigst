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
@Table(name = "MASTER_TRANSPORT")
public class TransportEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "PARTICULARS")
	private String particulars;

	@Expose
	@Column(name = "EXPECTED_INPUT")
	private String Input;

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
	 * @return the input
	 */
	public String getInput() {
		return Input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(String input) {
		Input = input;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransportEntity [id=" + id + ", particulars=" + particulars
				+ ", Input=" + Input + "]";
	}

	
}