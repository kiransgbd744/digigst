package com.ey.advisory.app.docs.dto.gstr6a;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Gstr6ADashBoardRespDto {
	
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
	private String regType;
}
