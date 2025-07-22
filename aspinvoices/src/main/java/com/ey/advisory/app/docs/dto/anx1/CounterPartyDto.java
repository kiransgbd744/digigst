package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class CounterPartyDto {

	@Expose
	@SerializedName("ctin")
	private String ctin;
	@Expose
	@SerializedName("chksum")
	private String chksum;
	@Expose
	@SerializedName("rc")
	private Integer rc ;
	@Expose
	@SerializedName("ttl_val")
	private BigDecimal ttl_val = BigDecimal.ZERO;
	@Expose
	@SerializedName("ttl_txpd_cgst")
	private BigDecimal ttl_txpd_cgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("ttl_txpd_sgst")
	private BigDecimal ttl_txpd_sgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("ttl_txpd_igst")
	private BigDecimal ttl_txpd_igst = BigDecimal.ZERO;
	@Expose
	@SerializedName("ttl_txpd_cess")
	private BigDecimal ttl_txpd_cess = BigDecimal.ZERO;
}
