package com.ey.advisory.app.docs.dto.gstr3B;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr3BInwSuppDTO {

	@Expose
	@SerializedName("isup_details")
	private List<Gstr3BSecDetailsDTO> isupDetails;
	
}
