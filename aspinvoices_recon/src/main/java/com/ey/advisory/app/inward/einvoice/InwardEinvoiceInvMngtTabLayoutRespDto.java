package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InwardEinvoiceInvMngtTabLayoutRespDto {

	@Expose
	public List<EinvoiceNestedReportDto> AttribDtls;

	@Expose
	public List<EinvoiceNestedReportDto> PrecDocDtls;

	@Expose
	public List<EinvoiceNestedReportDto> ContrDtls;
	
	@Expose
	public List<EinvoiceNestedReportDto> AddlDocDtls;
	
}
