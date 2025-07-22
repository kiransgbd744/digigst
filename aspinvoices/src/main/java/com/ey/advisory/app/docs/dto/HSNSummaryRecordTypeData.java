package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */
@Data
public class HSNSummaryRecordTypeData {

	@Expose
	@SerializedName("hsn_b2b")
	private List<HSNSummaryInvData> hsnB2b;

	@Expose
	@SerializedName("hsn_b2c")
	private List<HSNSummaryInvData> hsnB2c;

}
