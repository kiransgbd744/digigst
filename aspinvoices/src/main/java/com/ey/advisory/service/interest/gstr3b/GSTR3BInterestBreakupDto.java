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
public class GSTR3BInterestBreakupDto {

	
	@Expose
	@SerializedName("ret_period")
	private String returnPeriodBrkup;
	
	@Expose
	@SerializedName("profile")
	private String profile;
	
	@Expose
	@SerializedName("duedate")
	private String dueDate;
	
	@Expose
	@SerializedName("aato")
	private String annualAggregateTurnover;
	
	@Expose
	@SerializedName("aato_final_flag")
	private String aaroFinFlag;
	
	@Expose
	@SerializedName("aato_fy")
	private String aatoFY;
	
	@Expose
	@SerializedName("not_name")
	private String notificationName;
	
	@Expose
	@SerializedName("not_dt")
	private String notificationDate;
	
	@Expose
	@SerializedName("txpay")
	private GSTR3BTaxDto txPay;
	
	@Expose
	@SerializedName("txpay_declared")
	private GSTR3BTaxDto txPayDeclared;
	
	@Expose
	@SerializedName("challan")
	private List<GSTR3BChalanDto> chalans;
	
	@Expose
	@SerializedName("rates")
	private List<GSTR3BRateDto> rates;

}
