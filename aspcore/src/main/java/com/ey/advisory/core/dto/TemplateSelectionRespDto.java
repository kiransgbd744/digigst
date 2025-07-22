package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TemplateSelectionRespDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("groupId")
	private Long groupI;

	@Expose
	@SerializedName("entityName")
	private String entityName;

	@Expose
	@SerializedName("goodsTaxInv")
	private List<TemplateDto> goodsTaxInv;

	@Expose
	@SerializedName("goodsCDNotes")
	private List<TemplateDto> goodsCDNotes;

	@Expose
	@SerializedName("serviceTaxInv")
	private List<TemplateDto> serviceTaxInv;

	@Expose
	@SerializedName("serviceCDNotes")
	private List<TemplateDto> serviceCDNotes;
	
	@Expose
	@SerializedName("gstr6IsdDistribution")
	private List<TemplateDto> gstr6IsdDistribution;

	@Expose
	@SerializedName("gstr6CRDistribution")
	private List<TemplateDto> gstr6CRDistribution;

	@Expose
	@SerializedName("gstr6IsdReDistribution")
	private List<TemplateDto> gstr6IsdReDistribution;

	@Expose
	@SerializedName("gstr6CRReDistribution")
	private List<TemplateDto> gstr6CRReDistribution;

	@Expose
	@SerializedName("item")
	private TemplateSelectionItemRespDto item;

}
