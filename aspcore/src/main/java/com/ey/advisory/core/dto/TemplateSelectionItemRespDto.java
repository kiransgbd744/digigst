package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Umesha.M
 *
 */
@Data
public class TemplateSelectionItemRespDto {

	@Expose
	@SerializedName("goodsTaxInv")
	private Long goodsTaxInv;

	@Expose
	@SerializedName("goodsCDNotes")
	private Long goodsCDNotes;

	@Expose
	@SerializedName("serviceTaxInv")
	private Long serviceTaxInv;

	@Expose
	@SerializedName("serviceCDNotes")
	private Long serviceCDNotes;
	
	@Expose
	@SerializedName("gstr6IsdDistribution")
	private Long gstr6IsdDistribution;

	@Expose
	@SerializedName("gstr6CRDistribution")
	private Long gstr6CRDistribution;

	@Expose
	@SerializedName("gstr6IsdReDistribution")
	private Long gstr6IsdReDistribution;

	@Expose
	@SerializedName("gstr6CRReDistribution")
	private Long gstr6CRReDistribution;


}
