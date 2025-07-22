package com.ey.advisory.app.docs.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Component
public class DocDeleteReqDto {
	
	@Expose
	@SerializedName("docIds")
	private List<Long> docIds;

	/**
	 * @return the docIds
	 */
	public List<Long> getDocIds() {
		return docIds;
	}

	/**
	 * @param docIds the docIds to set
	 */
	public void setDocIds(List<Long> docIds) {
		this.docIds = docIds;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("DocDeleteReqDto [docIds=%s]", docIds);
	}
}
