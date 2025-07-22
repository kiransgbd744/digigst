package com.ey.advisory.app.dashboard.homeOld;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mohit.basak
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardHOReturnComplianceStatusDto {

	@Expose
	private String supplierGstin;
	
	@Expose
	private String gstinState;
	
	@Expose
	private String regType;
	
	@Expose
	private String status;

	@Expose
	private BigDecimal outwardSupply = BigDecimal.ZERO;

	@Expose
	private BigDecimal totalTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal iGST = BigDecimal.ZERO;

	@Expose
	private BigDecimal cGST = BigDecimal.ZERO;

	@Expose
	private BigDecimal sGST = BigDecimal.ZERO;

	@Expose
	private BigDecimal cESS = BigDecimal.ZERO;

	@Expose
	private BigDecimal cashLedger = BigDecimal.ZERO;

	@Expose
	private BigDecimal creditLedger = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal liabilityLedger = BigDecimal.ZERO;

	@Expose
	private Boolean gstr1Flag ;
	
	@Expose
	private Boolean gstr1AFlag ;
	
	@Expose
	private Boolean gstr3BFlag ;

	@Expose
	private Boolean gstr6Flag ;

	@Expose
	private Boolean gstr7Flag ;


	
}
