package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6SummaryLateFeeMainDto {

	@Expose
	@SerializedName("lateFee")
	private Gstr6SummaryLateFeeDto lateFee;

	public Gstr6SummaryLateFeeDto getLateFee() {
		return lateFee;
	}

	public void setLateFee(Gstr6SummaryLateFeeDto lateFee) {
		this.lateFee = lateFee;
	}

}
