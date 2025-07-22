package com.ey.advisory.app.docs.dto.anx2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Anx2ListGSTINForReconResp {

	private String cgstin;

	private String state;
	
	private LocalDateTime updatedDate;
	
	private String status;

		public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Anx2ListGSTINForReconResp [cgstin=" + cgstin + ", state="
				+ state + ", updatedDate=" + updatedDate + ", status=" + status
				+ "]";
	}

	
	

}
