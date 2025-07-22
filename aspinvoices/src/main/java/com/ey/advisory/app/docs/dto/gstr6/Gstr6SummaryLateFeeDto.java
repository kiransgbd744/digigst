package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6SummaryLateFeeDto {

	@Expose
	@SerializedName("debitId")
	private String debitId;

	@Expose
	@SerializedName("cLamt")
	private BigDecimal cLamt;

	@Expose
	@SerializedName("sLamt")
	private BigDecimal sLamt;

	public String getDebitId() {
		return debitId;
	}

	public void setDebitId(String debitId) {
		this.debitId = debitId;
	}

	public BigDecimal getcLamt() {
		return cLamt;
	}

	public void setcLamt(BigDecimal cLamt) {
		this.cLamt = cLamt;
	}

	public BigDecimal getsLamt() {
		return sLamt;
	}

	public void setsLamt(BigDecimal sLamt) {
		this.sLamt = sLamt;
	}

}
