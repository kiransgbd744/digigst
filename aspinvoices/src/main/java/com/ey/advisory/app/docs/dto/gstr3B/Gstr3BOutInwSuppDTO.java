package com.ey.advisory.app.docs.dto.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr3BOutInwSuppDTO {

	@Expose
	@SerializedName("osup_det")
	private Gstr3BSecDetailsDTO osupDet;
	
	@Expose
	@SerializedName("osup_zero")
	private Gstr3BSecDetailsDTO osupZero;
	
	@Expose
	@SerializedName("osup_nil_exmp")
	private Gstr3BSecDetailsDTO osupNilExmp;
	
	@Expose
	@SerializedName("isup_rev")
	private Gstr3BSecDetailsDTO isupRev;
	
	@Expose
	@SerializedName("osup_nongst")
	private Gstr3BSecDetailsDTO osupNongst;
	
	
	
}
