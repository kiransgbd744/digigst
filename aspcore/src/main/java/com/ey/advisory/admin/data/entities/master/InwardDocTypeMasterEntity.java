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
@Table(name = "MASTER_DOCTYPE_INWARD")
public class InwardDocTypeMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "DOC_TYPE")
	private String docType;
	
	@Expose
	@Column(name = "DOC_TYPE_DESC")
	private String doctypeDesc;

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
	 * @return the doctypeDesc
	 */
	public String getDoctypeDesc() {
		return doctypeDesc;
	}

	/**
	 * @param doctypeDesc the doctypeDesc to set
	 */
	public void setDoctypeDesc(String doctypeDesc) {
		this.doctypeDesc = doctypeDesc;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InwardDocTypeMasterEntity [id=" + id + ", docType=" + docType
				+ ", doctypeDesc=" + doctypeDesc + "]";
	}



}
