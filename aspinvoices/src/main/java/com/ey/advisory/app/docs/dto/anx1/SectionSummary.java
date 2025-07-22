package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class SectionSummary {
	@Expose
	@SerializedName("sec_nm")
	private String sec_nm;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
	
	@Expose
	@SerializedName("rc")
	private Integer rc;
	@Expose
	@SerializedName("ttl_val")
	private BigDecimal ttl_val;
	
	@Expose
	@SerializedName("ttl_txpd_cgst")
	private BigDecimal ttl_txpd_cgst;
	
	@Expose
	@SerializedName("ttl_txpd_sgst")
	private BigDecimal ttl_txpd_sgst;
	
	@Expose
	@SerializedName("ttl_txpd_igst")
	private BigDecimal ttl_txpd_igst;
	
	@Expose
	@SerializedName("ttl_txpd_cess")
	private BigDecimal ttl_txpd_cess;
	
	@Expose
	@SerializedName("cpty_sum")
	private List<CounterPartyDto> cptySum;

}
