/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */

@Entity
@Table(name = "ITC04_ITEM")
@Setter
@Getter
@ToString
public class Itc04ItemEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ITC04_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("qRetPeriod")
	@Column(name = "QRETURN_PERIOD")
	protected String qRetPeriod;
	
	@Expose
	@SerializedName("retPeriodDocKey")
	@Column(name = "RETURN_PERIOD_DOCKEY")
	protected String retPeriodDocKey;

	@Expose
	@SerializedName("goodsRecDate")
	@Column(name = "GOODS_RECEIVING_DATE")
	protected LocalDate goodsReceivingDate;

	@Expose
	@SerializedName("typeOfGoods")
	@Column(name = "GOODS_TYPE")
	protected String typeOfGoods;

	@Expose
	@SerializedName("itemNo")
	@Column(name = "ITM_SER_NO")
	protected String itemSerialNumber;

	@Expose
	@SerializedName("itemIndex")
	@Column(name = "ITEM_INDEX")
	protected Integer itemIndex;

	@Expose
	@SerializedName("prodDes")
	@Column(name = "PRODUCT_DESC")
	protected String productDescription;

	@Expose
	@SerializedName("productCode")
	@Column(name = "PRODUCT_CODE")
	protected String productCode;

	@Expose
	@SerializedName("natureOfJw")
	@Column(name = "NATURE_OF_JW")
	protected String natureOfJw;

	@Expose
	@SerializedName("hsn")
	@Column(name = "ITM_HSNSAC")
	protected String hsn;

	@Expose
	@SerializedName("uqc")
	@Column(name = "ITM_UQC")
	protected String uqc;

	@Expose
	@SerializedName("qnt")
	@Column(name = "ITM_QTY")
	protected BigDecimal qnt;

	@Expose
	@SerializedName("lossesUqc")
	@Column(name = "LOSSES_UQC")
	protected String lossesUqc;

	@Expose
	@SerializedName("lossesQnt")
	@Column(name = "LOSSES_QTY")
	protected BigDecimal lossesQnt;

	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableValue;

	@Expose
	@SerializedName("igstRt")
	@Column(name = "IGST_RATE")
	protected BigDecimal igstRate;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmount;

	@Expose
	@SerializedName("cgstRt")
	@Column(name = "CGST_RATE")
	protected BigDecimal cgstRate;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgstRt")
	@Column(name = "SGST_RATE")
	protected BigDecimal sgstRate;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmount;

	@Expose
	@SerializedName("cessSpeRate")
	@Column(name = "CESS_RATE_SPECIFIC")
	protected BigDecimal cessSpecificRate;

	@Expose
	@SerializedName("cessSpAmt")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected BigDecimal cessSpecificAmount;

	@Expose
	@SerializedName("cessAdvRate")
	@Column(name = "CESS_RATE_ADVALOREM")
	protected BigDecimal cessAdvaloremRate;

	@Expose
	@SerializedName("cessAdvAmt")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected BigDecimal cessAdvaloremAmount;

	@Expose
	@SerializedName("stCessSpeRt")
	@Column(name = "STATE_CESS_RATE_SPECIFIC")
	protected BigDecimal stateCessSpecificRate;

	@Expose
	@SerializedName("stCessSpeAmt")
	@Column(name = "STATE_CESS_AMT_SPECIFIC")
	protected BigDecimal stateCessSpecificAmount;

	@Expose
	@SerializedName("stCessAdvRt")
	@Column(name = "STATE_CESS_RATE_ADVALOREM")
	protected BigDecimal stateCessAdvaloremRate;

	@Expose
	@SerializedName("stCessAdvAmt")
	@Column(name = "STATE_CESS_AMT_ADVALOREM")
	protected BigDecimal stateCessAdvaloremAmount;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected BigDecimal totalValue;
	
	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;
	
	@Expose
	@SerializedName("taxRate")
	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;

	@Expose
	@SerializedName("udf1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("udf2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("udf3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("udf4")
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;
	@Expose
	@SerializedName("udf5")
	@Column(name = "USERDEFINED_FIELD5")
	protected String userdefinedfield5;

	@Expose
	@SerializedName("udf6")
	@Column(name = "USERDEFINED_FIELD6")
	protected String userdefinedfield6;

	@Expose
	@SerializedName("udf7")
	@Column(name = "USERDEFINED_FIELD7")
	protected String userdefinedfield7;

	@Expose
	@SerializedName("udf8")
	@Column(name = "USERDEFINED_FIELD8")
	protected String userDefinedField8;

	@Expose
	@SerializedName("udf9")
	@Column(name = "USERDEFINED_FIELD9")
	protected String userdefinedfield9;

	@Expose
	@SerializedName("udf10")
	@Column(name = "USERDEFINED_FIELD10")
	protected String userdefinedfield10;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("infoCodes")
	@Column(name = "INFORMATION_CODES")
	private String infoCodes;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Itc04HeaderEntity document;
}
