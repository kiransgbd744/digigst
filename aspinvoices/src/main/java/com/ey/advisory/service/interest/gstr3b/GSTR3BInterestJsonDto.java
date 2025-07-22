/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class GSTR3BInterestJsonDto {
	
	@Expose
	@SerializedName("gstin")
	private String supplierGSTIN;
	
	@Expose
	@SerializedName("ret_period")
	private String returnPeriod;
	
	@Expose
	@SerializedName("filed_period")
	private String filedReturnPeriod;
	
	@Expose
	@SerializedName("filingdt")
	private String filingDate;
	
	@Expose
	@SerializedName("computedt")
	private String interestComputationDate;
	
	@Expose
	@SerializedName("txpay")
	private GSTR3BTaxDto taxPayable;
	
	@Expose
	@SerializedName("pdcash")
	private GSTR3BTaxDto pdCash;
	
	@Expose
	@SerializedName("pditc")
	private GSTR3BTaxDto pdITC;
	
	@Expose
	@SerializedName("interest")
	private GSTR3BTaxDto interest;
	
	@Expose
	@SerializedName("interestbreakup")
	private List<GSTR3BInterestBreakupDto> interestBreakups;

}
