package com.ey.advisory.app.gstr2b.summary;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr2BDetailsRespDto {
	
	@Expose
	private List<Gstr2BDetailsDto> itcSuppliesFromRegPerson;
	
	@Expose
	private List<Gstr2BDetailsDto> inwardSuppliesFromIsd;
	
	@Expose
	private List<Gstr2BDetailsDto> inwardSuppliesRevChrg;
	
	@Expose
	private List<Gstr2BDetailsDto> importOfGoods;
	
	@Expose
	private List<Gstr2BDetailsDto> others;
	
	@Expose
	private Gstr2BDetailsDto total;
	
	
	

}
