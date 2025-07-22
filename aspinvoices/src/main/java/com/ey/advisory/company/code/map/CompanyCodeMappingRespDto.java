package com.ey.advisory.company.code.map;

import java.util.List;

import com.ey.advisory.core.dto.ErpCompanyCodeDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CompanyCodeMappingRespDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("entityName")
	private String entityName;

	@Expose
	@SerializedName("sourceId")
	private List<ErpCompanyCodeDto> sourceId;

	@Expose
	@SerializedName("companyCode")
	private List<CompanyCodeDto> companyCode;

	@Expose
	@SerializedName("item")
	private List<CompanyCodeMapItemRespDto> compCodeMapItemDto;
}
