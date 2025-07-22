/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author KG712ZX
 *
 */

@Getter
@Setter
@ToString
public class GetCounterPartyRespListDto {
	
	@SerializedName("counterPartyRespList")
	@Expose
	private List<GetCounterPartyResDto> counterPartyRespList;
	
	@SerializedName("errorCode")
	@Expose
	private String errorCode;

	@SerializedName("errorMessage")
	@Expose
	private String errorDesc;

}
