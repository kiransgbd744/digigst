/**
 * 
 */
package com.ey.advisory.app.docs.dto.ret;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class RetPaymentTaxDto {

	@Expose
	@SerializedName("tx_py")
	private RetItemDetailsDto txpy;

	@Expose
	@SerializedName("pmt08_pd")
	private RetItemDetailsDto pmtpd;

	@Expose
	@SerializedName("neg_liab")
	private RetItemDetailsDto negliab;

	@Expose
	@SerializedName("tx_paid")
	private RetItemDetailsDto taxPaid;

	@Expose
	@SerializedName("nettx_py")
	private RetItemDetailsDto nettxpy;

}
