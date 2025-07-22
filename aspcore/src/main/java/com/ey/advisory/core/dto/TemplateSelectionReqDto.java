package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Umesha.M
 *
 */
@Data
public class TemplateSelectionReqDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("groupId")
	private Long groupId;

	@Expose
	@SerializedName("gstin")
	private List<String> gstin;

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
	private Long gstrCRReDistribution;

}
