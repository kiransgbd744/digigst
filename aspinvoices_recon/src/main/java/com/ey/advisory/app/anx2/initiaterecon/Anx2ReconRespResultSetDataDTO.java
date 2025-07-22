package com.ey.advisory.app.anx2.initiaterecon;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Anx2ReconRespResultSetDataDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String gstin;

	@Expose
	private String userResponse;

	@Expose
	private BigDecimal taxPayable = BigDecimal.ZERO;

	public Anx2ReconRespResultSetDataDTO(String gstin, String userResponse,
			BigDecimal taxPayable) {
		super();
		this.gstin = gstin;
		this.userResponse = userResponse;
		this.taxPayable = taxPayable;
	}

	public Anx2ReconRespResultSetDataDTO() {
		super();
	}

	public String getKey() {
		return gstin + "|" + userResponse;
	}

}
