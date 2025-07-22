package com.ey.advisory.app.docs.dto.gstr6;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Gstr6ErrorReport {

	@Expose
	@SerializedName("b2b")
	private List<Gstr6B2bDto> b2bInvoice;

	@Expose
	@SerializedName("b2ba")
	private List<Gstr6B2baDto> b2baInvoice;

	@Expose
	@SerializedName("cdn")
	private List<Gstr6CdnDto> cdnInvoice;

	@Expose
	@SerializedName("cdna")
	private List<Gstr6CdnaDto> cdnaInvoice;

	@Expose
	@SerializedName("isd")
	private List<Gstr6IsdDetailsDto> isdInvoice;

	@Expose
	@SerializedName("isda")
	private List<Gstr6IsdDetailsDto> isdaInvoice;
}
