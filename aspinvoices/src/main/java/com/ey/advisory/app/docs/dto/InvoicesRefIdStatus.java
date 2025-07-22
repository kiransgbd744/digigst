package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvoicesRefIdStatus {

	@Expose
	@SerializedName("resp")
	private List<ReturnStatusRefIdDto> resp;

	public List<ReturnStatusRefIdDto> getResp() {
		return resp;
	}

	public void setResp(List<ReturnStatusRefIdDto> resp) {
		this.resp = resp;
	}

	}
