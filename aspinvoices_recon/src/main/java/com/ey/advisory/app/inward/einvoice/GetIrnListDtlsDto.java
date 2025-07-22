package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetIrnListDtlsDto {
	
	@Expose
	@SerializedName("irnList")
	public List<GetIrnCtinListDtlsDto> irnList;
	
	@Expose
	@SerializedName("requestDate")
	public String requestDate;
	
	

}
