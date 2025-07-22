package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author SriBhavya
 *
 */
@Setter
@Getter
@ToString
public class Gstr6ComputeGstr1SummaryRequestDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("periodFrom")
	private String periodFrom;

	@Expose
	@SerializedName("periodTo")
	private String periodTo;
}
