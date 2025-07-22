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
public class GetImsCountGoodsTypeDtlsDto {
	
	//need serializable name of all_oth, inv_supp_isd, imp_gds having data type as List<GetImsCountDtlsDto>
	@Expose
	@SerializedName("all_oth")
	public GetImsCountDtlsDto allOther;

	@Expose
	@SerializedName("inv_supp_isd")
	public GetImsCountDtlsDto invSuppIsd;

	@Expose
	@SerializedName("imp_gds")
	public GetImsCountDtlsDto impGds;


}
