package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETGSTR1_RATE_SUMMARY")
@Data
public class Gstr1SummaryRateEntity extends GetGstr1SummaryEntity {

	@Expose
	@SerializedName("sec_nm")
	@Column(name = "SECTION_NAME")
	private String secName;

	@Expose
	@SerializedName("ttl_rec")
	@Column(name = "TOT_RECORD")
	private int ttlRec;

	@Expose
	@SerializedName("ttl_tax")
	@Column(name = "TOT_TAX")
	private BigDecimal ttlTax;

	@Expose
	@SerializedName("ttl_igst")
	@Column(name = "TOT_IGST")
	private BigDecimal ttlIgst;

	@Expose
	@SerializedName("ttl_sgst")
	@Column(name = "TOT_SGST")
	private BigDecimal ttlSgst;

	@Expose
	@SerializedName("ttl_cgst")
	@Column(name = "TOT_CGST")
	private BigDecimal ttlCgst;

	@Expose
	@SerializedName("ttl_val")
	@Column(name = "TOT_VALUE")
	private BigDecimal ttlVal;

	@Expose
	@SerializedName("ttl_cess")
	@Column(name = "TOT_CESS")
	private BigDecimal ttlCess;

	@Expose
	@SerializedName("act_tax")
	@Transient
	private BigDecimal actTax;

	@Expose
	@SerializedName("act_igst")
	@Transient
	private BigDecimal actIgst;

	@Expose
	@SerializedName("act_sgst")
	@Transient
	private BigDecimal actSgst;

	@Expose
	@SerializedName("act_cgst")
	@Transient
	private BigDecimal actCgst;

	@Expose
	@SerializedName("act_val")
	@Transient
	private BigDecimal actVal;

	@Expose
	@SerializedName("act_cess")
	@Transient
	private BigDecimal actCess;

/*	//HSN changes 
	
	@Expose
	@SerializedName("RecordType")
	@Column(name = "RECORD_TYPE")
	private String recordType;*/
}
