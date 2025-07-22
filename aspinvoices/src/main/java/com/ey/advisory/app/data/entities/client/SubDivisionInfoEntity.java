package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "MASTER_SUBDIVISION")
public class SubDivisionInfoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	@Expose
	@Column(name = "USER")
	private String user;
	
	@Expose
	@Column(name = "DIVISION")
	private String division;

	@Expose
	@Column(name = "SUB_DIVISION")
	private String subDivision;
	/**
	 * @return the subDivision
	 */
	public String getSubDivision() {
		return subDivision;
	}

	/**
	 * @param subDivision the subDivision to set
	 */
	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}

	@Expose
	@Column(name = "DIVISION_DESC")
	private String divisionDesc;

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
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}

	/**
	 * @param division the division to set
	 */
	public void setDivision(String division) {
		this.division = division;
	}

	/**
	 * @return the divisionDesc
	 */
	public String getDivisionDesc() {
		return divisionDesc;
	}

	/**
	 * @param divisionDesc the divisionDesc to set
	 */
	public void setDivisionDesc(String divisionDesc) {
		this.divisionDesc = divisionDesc;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubDivisionInfoEntity [id=" + id + ", user=" + user
				+ ", division=" + division + ", subDivision=" + subDivision
				+ ", divisionDesc=" + divisionDesc + "]";
	}

	

}
