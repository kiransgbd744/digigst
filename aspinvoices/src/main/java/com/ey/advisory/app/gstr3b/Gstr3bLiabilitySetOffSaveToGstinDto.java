package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3bLiabilitySetOffSaveToGstinDto {

	@Expose
	@SerializedName("pdcash")
	private List<Gstr3BPaidCashDto> pdCash;
	
	@Expose
	@SerializedName("pditc")
	private Gstr3BPaidITCDto pdItcDto;
	
	@SerializedName("nettaxpay")
	@Expose
	private List<Gstr3BPaidNetTaxPayDto> netTaxPayItems;
	
	@Expose
	@SerializedName("pdnls")
	private List<Gstr3BSetOffSavePdNlsDto> pdnls;
	

}
