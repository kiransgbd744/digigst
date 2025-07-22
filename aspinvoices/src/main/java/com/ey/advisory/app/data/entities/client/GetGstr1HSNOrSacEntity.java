/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


	
	
	@Entity
	@Table(name = "GETGSTR1_HSNORSAC")

	public class GetGstr1HSNOrSacEntity {

		@Expose
		@SerializedName("id")
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "ID")
		protected Long id;

		@Expose
		@SerializedName("batchId")
		@Column(name = "BATCH_ID")
		protected Long batchId;
		
		@Expose
		@SerializedName("gstin")
		@Column(name = "GSTIN")
		protected String gstin;

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
		@Column(name = "SERIAL_NUM")
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
		@SerializedName("ecomType")
		@Column(name = "ECOM_TYPE")
		protected String ecomType;

		@Expose
		@SerializedName("pos")
		@Column(name = "POS")
		protected String pos;

		@Expose
		@SerializedName("orgPos")
		@Column(name = "ORG_POS")
		protected String orgPos;

		@Expose
		@SerializedName("orgInvMonth")
		@Column(name = "ORG_INV_MONTH")
		protected String orgInvMonth;

		@Expose
		@SerializedName("suppType")
		@Column(name = "SUPPLY_TYPE")
		protected String suppType;

		@Expose
		@SerializedName("diff_percent")
		@Column(name = "DIFF_PERCENT")
		protected BigDecimal diffPercent;

		@Expose
		@SerializedName("taxRate")
		@Column(name = "TAX_RATE")
		protected BigDecimal taxRate;

		@Expose
		@SerializedName("taxValue")
		@Column(name = "TAXABLE_VALUE")
		protected BigDecimal taxValue;

		@Expose
		@SerializedName("igstAmt")
		@Column(name = "IGST_AMT")
		protected BigDecimal igstAmt;

		@Expose
		@SerializedName("cgstAmt")
		@Column(name = "CGST_AMT")
		protected BigDecimal cgstAmt;

		@Expose
		@SerializedName("sgstAmt")
		@Column(name = "SGST_AMT")
		protected BigDecimal sgstAmt;

		@Expose
		@SerializedName("cessAmt")
		@Column(name = "CESS_AMT")
		protected BigDecimal cessAmt;

		@Column(name = "IS_DELETE")
		private boolean isDelete;

		@Expose
		@SerializedName("docKey")
		@Column(name = "DOC_KEY")
		protected String docKey;

		

		@Column(name = "IS_FILED") //
		private boolean isFiled;

}
*/