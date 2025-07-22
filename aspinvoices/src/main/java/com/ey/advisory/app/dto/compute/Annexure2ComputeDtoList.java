/**
 * 
 */
package com.ey.advisory.app.dto.compute;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Khalid1.Khan
 *
 */
public class Annexure2ComputeDtoList {

	@Expose
	@SerializedName("reconComputeList")
	private List<Annexure2ComputeDto> annexure2ComputeListDto;

	public List<Annexure2ComputeDto> getAnnexure2ComputeListDto() {
		return annexure2ComputeListDto;
	}

	public void setAnnexure2ComputeListDto(List<Annexure2ComputeDto> annexure2ComputeListDto) {
		this.annexure2ComputeListDto = annexure2ComputeListDto;
	}

	

}
