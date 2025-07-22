package com.ey.advisory.app.gstr2b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Gstr2BDashBoardRespDto {
	
	@Expose
	private String gstin;
	
	@Expose
	private List<InnerDetailDto> taxPeriodDetails;

	@Expose
	@SerializedName("stateName")
	private String stateName;

	@Expose
	@SerializedName("authStatus")
	private String authStatus;
	
	@Expose
	@SerializedName("status2B")
	private String status2B;
	
	
}
