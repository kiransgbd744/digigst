package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Saif.S
 *
 */
@Getter
@Setter
public class MajorTaxPayFinalDto {

	private String hsnsac;
	private String per0;
	private String per1;
	private String per1_5;
	private String per3;
	private String per5;
	private String per7;
	private String per7_5;
	private String per12;
	private String per18;
	private String per28;
	private BigInteger drn;
	private String hsnTotalValue;
	

}
