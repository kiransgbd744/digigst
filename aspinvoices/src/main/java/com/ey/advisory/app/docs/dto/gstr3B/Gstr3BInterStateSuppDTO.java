package com.ey.advisory.app.docs.dto.gstr3B;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr3BInterStateSuppDTO {

	@Expose
	@SerializedName("unreg_details")
	private List<Gstr3BSecDetailsDTO> unregDetails;
	
	@Expose
	@SerializedName("comp_details")
	private List<Gstr3BSecDetailsDTO> compDetails;
	
	@Expose
	@SerializedName("uin_details")
	private List<Gstr3BSecDetailsDTO> uinDetails;
	
	
}
