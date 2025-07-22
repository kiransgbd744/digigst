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
@Table(name = "GETGSTR1_HSNORSAC")
@ToString
@Getter
@Setter
public class GetGstr1HSNOrSACInvoicesEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_HSNORSAC_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "GSTIN")
	private String gstinOfTaxPayer;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "FLAG")
	private String flag;

	@Column(name = "INV_CHKSUM")
	private String invoiceCheckSum;

	@Column(name = "SERIAL_NUM")
	private Integer serialNumber;

	@Column(name = "HSNORSAC")
	private String hsnOrSac;

	@Column(name = "ITM_DESC")
	private String descOfGoodsSold;

	@Column(name = "ITM_UQC")
	private String unitOfMeasureOfGoodsSold;

	@Column(name = "ITM_QTY")
	private BigDecimal qtyOfGoodsSold;

	@Column(name = "TOT_VAL")
	private BigDecimal totalValue;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxValOfGoodsOrService;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmount;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmount;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmount;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmount;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Column(name = "TAX_RATE")
	private BigDecimal rate;
	
	@Column(name = "RECORD_TYPE")
	private String recordType;

}
