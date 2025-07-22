package com.ey.advisory.app.docs.dto.anx2;

import org.springframework.stereotype.Service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx2Reconciliation2aDto")
public class Anx2Reconciliation2aDto {

	@Expose
	@SerializedName("batchId")
	private Long batchId;

	@Expose
	@SerializedName("section")
	private String section;

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	@Override
	public String toString() {
		return "Anx2Reconciliation2aDto [batchId=" + batchId + ", section="
				+ section + "]";
	}

}
