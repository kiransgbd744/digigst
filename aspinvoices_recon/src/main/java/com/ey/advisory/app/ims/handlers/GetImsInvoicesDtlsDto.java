package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetImsInvoicesDtlsDto {
	
	@Expose
	@SerializedName("b2b")
	public List<GetImsInvoicesSectionDtlsDto> b2b;

	//need same defination for b2ba, b2bdn, b2bdna, b2bcn, b2bcna, ecom, ecoma
	@Expose
	@SerializedName("b2ba")
	public List<GetImsInvoicesSectionDtlsDto> b2ba;

	@Expose
	@SerializedName("b2bdn")
	public List<GetImsInvoicesSectionDtlsDto> b2bdn;

	@Expose
	@SerializedName("b2bdna")
	public List<GetImsInvoicesSectionDtlsDto> b2bdna;

	@Expose
	@SerializedName("b2bcn")
	public List<GetImsInvoicesSectionDtlsDto> b2bcn;

	@Expose
	@SerializedName("b2bcna")
	public List<GetImsInvoicesSectionDtlsDto> b2bcna;

	@Expose
	@SerializedName("ecom")
	public List<GetImsInvoicesSectionDtlsDto> ecom;

	@Expose
	@SerializedName("ecoma")
	public List<GetImsInvoicesSectionDtlsDto> ecoma;
}
