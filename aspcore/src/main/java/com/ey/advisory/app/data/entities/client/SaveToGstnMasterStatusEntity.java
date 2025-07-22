package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "MASTER_SAVE_REQUEST")
public class SaveToGstnMasterStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STATUS_CODE", nullable = false)
	private int statusCode;
	
	@Column(name="SAVE_REQ_STATUS")
	private String status;

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatus(String status) {
		this.status = status;
	} 
	
}
