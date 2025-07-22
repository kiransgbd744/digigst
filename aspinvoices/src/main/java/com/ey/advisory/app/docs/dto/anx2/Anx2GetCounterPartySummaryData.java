package com.ey.advisory.app.docs.dto.anx2;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Dibyakanta.sahoo
 *
 */

public class Anx2GetCounterPartySummaryData {
	
	@Expose
	@SerializedName("ctin")
	private  String ctin;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
    
	@Expose
	@SerializedName("totactsum")
	private List<Anx2GetActionSummaryData>  totactsum;
	

}
