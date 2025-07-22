package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "MASTER_DIVISION")
public class DivisionInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	@Expose
	@Column(name = "USER")
	private String user;
	
	@Expose
	@Column(name = "SUB_DIVISION")
	private String subDivision;

	@Expose
	@Column(name = "SUB_DIVISION_DESC")
	private String subDivisionDesc;

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

	/**
	 * @return the subDivisionDesc
	 */
	public String getSubDivisionDesc() {
		return subDivisionDesc;
	}

	/**
	 * @param subDivisionDesc the subDivisionDesc to set
	 */
	public void setSubDivisionDesc(String subDivisionDesc) {
		this.subDivisionDesc = subDivisionDesc;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DivisionInfoEntity [id=" + id + ", user=" + user
				+ ", subDivision=" + subDivision + ", subDivisionDesc="
				+ subDivisionDesc + "]";
	}
	
}
