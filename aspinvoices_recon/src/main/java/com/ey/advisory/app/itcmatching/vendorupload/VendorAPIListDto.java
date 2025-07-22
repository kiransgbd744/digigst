package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data

public class VendorAPIListDto {
	
	//List<VendorAPIPushListDto> vendorData;
	  private List<VendorAPIPushListDto> vendorData = new ArrayList<>();
	

	
}