package com.ey.advisory.admin.data.entities.master;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
/**
 * 
 * @author Siva.Nandam
 *
 */
/**
 * 
 * This class is works as portCode master Table
 *
 */
@Entity
@Table(name = "MASTER_PORT")

public class PortCodeInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Expose    
	@Column(name = "PORT_CODE")
	private String portCode;
	
	@Expose
	@Column(name = "LOC_DESC")
	private String localDesc;

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
	 * @return the portCode
	 */
	public String getPortCode() {
		return portCode;
	}

	/**
	 * @param portCode the portCode to set
	 */
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	/**
	 * @return the localDesc
	 */
	public String getLocalDesc() {
		return localDesc;
	}

	/**
	 * @param localDesc the localDesc to set
	 */
	public void setLocalDesc(String localDesc) {
		this.localDesc = localDesc;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PortCodeInfoEntity [id=" + id + ", portCode=" + portCode
				+ ", localDesc=" + localDesc + "]";
	}

}
