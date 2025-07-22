package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Anx2ReconResponseDTO {
	
	@Expose
	@SerializedName("summaryData")
	private List<Anx2ReconResponseTableDTO> summaryData;
	
	@Expose
	@SerializedName("detailsData")
	private List<Anx2ReconResponseDetailsDTO> detailsData;
	
}
