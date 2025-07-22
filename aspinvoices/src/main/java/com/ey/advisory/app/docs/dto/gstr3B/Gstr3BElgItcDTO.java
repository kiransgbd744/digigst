package com.ey.advisory.app.docs.dto.gstr3B;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr3BElgItcDTO {
	
	@Expose
	@SerializedName("itc_avl")
	private List<Gstr3BSecDetailsDTO> itc_avl;
	
	@Expose
	@SerializedName("itc_rev")
	private List<Gstr3BSecDetailsDTO> itcRev;
	
	@Expose
	@SerializedName("itc_net")
	private Gstr3BSecDetailsDTO itcNet;
	
	@Expose
	@SerializedName("itc_inelg")
	private List<Gstr3BSecDetailsDTO> itcInelg;

}
