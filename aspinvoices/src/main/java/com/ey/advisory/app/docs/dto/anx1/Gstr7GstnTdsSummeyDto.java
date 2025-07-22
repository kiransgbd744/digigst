
package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr7GstnTdsSummeyDto {

	@Expose
	@SerializedName("ttl_igst")
	private BigDecimal ttl_igst;

	@Expose
	@SerializedName("ttl_cgst")
	private BigDecimal ttl_cgst;

	@Expose
	@SerializedName("ttl_sgst")
	private BigDecimal ttl_sgst;

	@Expose
	@SerializedName("ttl_amtDed")
	private BigDecimal ttl_amtDed;

	@Expose
	@SerializedName("no_rec")
	private Integer no_rec;

}