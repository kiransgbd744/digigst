package com.ey.advisory.app.data.entities.pdfreader;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author Akhilesh.Yadav
 *
 */
@Data
@Entity
@Table(name = "PDF_READER_LINE_ITEM")
@ToString
public class PDFResponseLineItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("itemNumber")
	@Column(name = "LINE_ITEM_NUMBER")
	private Integer itemNumber;

	@Expose
	@SerializedName("description")
	@Column(name = "DESCRIPTION")
	private String description;

	@Expose
	@SerializedName("hsnNumber")
	@Column(name = "HSN_NUMBER")
	private String hsnNumber;

	@Expose
	@SerializedName("quantity")
	@Column(name = "QUANTITY")
	private BigDecimal quantity;

	@Expose
	@SerializedName("unitPrice")
	@Column(name = "UNIT_PRICE")
	private BigDecimal unitPrice;

	@Expose
	@SerializedName("taxableAmount")
	@Column(name = "TAXABLE_AMOUNT")
	private BigDecimal taxableAmount;

	@Expose
	@SerializedName("unit")
	@Column(name = "UNIT")
	private String unit;

	@Expose
	@SerializedName("taxRate")
	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;

	@Expose
	@SerializedName("taxAmount")
	@Column(name = "TAX_AMOUNT")
	private BigDecimal taxAmount;

	@Expose
	@SerializedName("ttlamount")
	@Column(name = "TOTAL_AMOUNT")
	private BigDecimal ttlAmount;
	
	@Expose
	@SerializedName("pdfResTblId")
	@Column(name = "PDF_RESP_SUMMRY_TBL_ID")
	private Long pdfResTblId;

}
