package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETGSTR1_NIL_SUMMARY")
@Data
public class Gstr1SummaryNilEntity extends GetGstr1SummaryEntity {

	@Expose
	@SerializedName("sec_nm")
	@Column(name = "SECTION_NAME")
	private String secName;

	@Expose
	@SerializedName("ttl_rec")
	@Column(name = "TOT_RECORD")
	private int ttlRec;

	@Expose
	@SerializedName("ttl_expt_amt")
	@Column(name = "TOT_EXPT_AMT")
	private BigDecimal ttlExptAmt;

	@Expose
	@SerializedName("ttl_ngsup_amt")
	@Column(name = "TOT_NGSUP_AMT")
	private BigDecimal ttlNgsupAmt;

	@Expose
	@SerializedName("ttl_nilsup_amt")
	@Column(name = "TOT_NILSUP_AMT")
	private BigDecimal ttlNilsupAmt;

	
}
