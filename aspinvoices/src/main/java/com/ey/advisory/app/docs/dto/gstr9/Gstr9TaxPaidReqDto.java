package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9TaxPaidReqDto {
	
	@SerializedName("pd_by_cash")
	@Expose
	public List<Gstr9TaxPaidPdByCashReqDto> pdByCash;

}
