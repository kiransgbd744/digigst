package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETGSTR1_NILEXTNON")
@Setter
@Getter
@ToString
public class GetGstr1NilRatedEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_NILEXTNON_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("flag")
	@Column(name = "FLAG")
	protected String flag;

	@Expose
	@SerializedName("invChksum")
	@Column(name = "INV_CHKSUM")
	protected String invChksum;

	@Expose
	@SerializedName("nilAmt")
	@Column(name = "NIL_AMT")
	protected BigDecimal nilAmt;
	@Expose
	@SerializedName("exptAmt")
	@Column(name = "EXPT_AMT")
	protected BigDecimal exptAmt;

	@Expose
	@SerializedName("nonSupAmt")
	@Column(name = "NONGST_SUP_AMT")
	protected BigDecimal nonSupAmt;

	@Expose
	@SerializedName("suppType")
	@Column(name = "SUPPLY_TYPE")
	protected String suppType;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

}