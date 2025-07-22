package com.ey.advisory.app.docs.dto;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Component
public class B2CSRespDto {

	private String status;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
