package com.ey.advisory.app.data.entities.client;

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
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "ITC04_ERR_ITEM")
@Setter
@Getter
@ToString
public class Itc04ItemErrorsEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ITC04_ERR_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("tableNumber")
	@Column(name = "TABLE_NUMBER")
	protected String tableNumber;

	@Expose
	@SerializedName("actionType")
	@Column(name = "ACTION_TYPE")
	protected String actionType;

	@Expose
	@SerializedName("fyDocDate")
	@Column(name = "FI_YEAR_DC_DATE")
	protected String fyDocDate;

	@Expose
	@SerializedName("fyjwDocDate")
	@Column(name = "FI_YEAR_JWDC_DATE")
	protected String fyjwDocDate;

	@Expose
	@SerializedName("fyInvDate")
	@Column(name = "FI_YEAR_INV_DATE")
	protected String fyInvDate;

	@Expose
	@SerializedName("fy")
	@Column(name = "FI_YEAR")
	protected String fy;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("qRetPeriod")
	@Column(name = "QRETURN_PERIOD")
	protected String qRetPeriod;

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String supplierGstin;

	@Expose
	@SerializedName("dChallanNo")
	@Column(name = "DELIVERY_CHALLAN_NO")
	protected String deliveryChallanaNumber;

	@Expose
	@SerializedName("dChalanDate")
	@Column(name = "DELIVERY_CHALLAN_DATE")
	protected String deliveryChallanaDate;

	@Expose
	@SerializedName("jdChallanNo")
	@Column(name = "JW_DELIVERY_CHALLAN_NO")
	protected String jwDeliveryChallanaNumber;

	@Expose
	@SerializedName("jdChalanDate")
	@Column(name = "JW_DELIVERY_CHALLAN_DATE")
	protected String jwDeliveryChallanaDate;

	@Expose
	@SerializedName("invNumber")
	@Column(name = "INV_NUM")
	protected String invNumber;

	@Expose
	@SerializedName("invDate")
	@Column(name = "INV_DATE")
	protected String invDate;

	@Expose
	@SerializedName("jwGstin")
	@Column(name = "JW_GSTIN")
	protected String jobWorkerGstin;

	@Expose
	@SerializedName("jwStateCode")
	@Column(name = "JW_STATE_CODE")
	protected String jobWorkerStateCode;

	@Expose
	@SerializedName("jwId")
	@Column(name = "JW_ID")
	protected String jobWorkerId;

	@Expose
	@SerializedName("jwName")
	@Column(name = "JW_NAME")
	protected String jobWorkerName;

	@Expose
	@SerializedName("jwType")
	@Column(name = "JW_WORKER_TYPE")
	protected String jobWorkerType;

	@Expose
	@SerializedName("itemAccAmt")
	@Column(name = "TAXABLE_VALUE")
	protected String itemAccAmt;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected String igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected String cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected String sgstAmount;

	@Expose
	@SerializedName("cessAdvAmt")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected String cessAdvaloremAmount;

	@Expose
	@SerializedName("cessSpAmt")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected String cessSpecificAmount;

	@Expose
	@SerializedName("stCessAdvAmt")
	@Column(name = "STATE_CESS_AMT_ADVALOREM")
	protected String stateCessAdvaloremAmount;

	@Expose
	@SerializedName("stCessSpeAmt")
	@Column(name = "STATE_CESS_AMT_SPECIFIC")
	protected String stateCessSpecificAmount;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected String totalValue;

	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	protected String postingDate;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("sourceId")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	@Expose
	@SerializedName("sourFileName")
	@Column(name = "SOURCE_FILE_NAME")
	protected String sourceFileName;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("profitCentre1")
	@Column(name = "PROFIT_CENTRE1")
	protected String profitCentre1;

	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@SerializedName("accVoucNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accVoucDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected String accountingVoucherDate;

	@Expose
	@SerializedName("stCessAdvRate")
	@Column(name = "STATE_CESS_RATE_ADVALOREM")
	protected String stateCessAdvaloremRate;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	@Expose
	@SerializedName("goodsRecDate")
	@Column(name = "GOODS_RECEIVING_DATE")
	protected String goodsReceivingDate;

	@Expose
	@SerializedName("typesOfGoods")
	@Column(name = "GOODS_TYPE")
	protected String typesOfGoods;

	@Expose
	@SerializedName("itemSno")
	@Column(name = "ITM_SER_NO")
	protected String itemSerialNumber;

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
	protected String qnt;

	@Expose
	@SerializedName("lossesUqc")
	@Column(name = "LOSSES_UQC")
	protected String lossesUqc;

	@Expose
	@SerializedName("lossesQnt")
	@Column(name = "LOSSES_QTY")
	protected String lossesQnt;

	@Expose
	@SerializedName("igstRate")
	@Column(name = "IGST_RATE")
	protected String igstRate;

	@Expose
	@SerializedName("cgstRate")
	@Column(name = "CGST_RATE")
	protected String cgstRate;

	@Expose
	@SerializedName("sgstRate")
	@Column(name = "SGST_RATE")
	protected String sgstRate;

	@Expose
	@SerializedName("cessAdvRate")
	@Column(name = "CESS_RATE_ADVALOREM")
	protected String cessAdvaloremRate;

	@Expose
	@SerializedName("cessSpeRate")
	@Column(name = "CESS_RATE_SPECIFIC")
	protected String cessSpecificRate;

	@Expose
	@SerializedName("stcessSpeRate")
	@Column(name = "STATE_CESS_RATE_SPECIFIC")
	protected String stateCessSpecificRate;

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;

	@Expose
	@SerializedName("userdef1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("userdef2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("userdef3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("userdef4")
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;
	@Expose
	@SerializedName("userdef5")
	@Column(name = "USERDEFINED_FIELD5")
	protected String userdefinedfield5;

	@Expose
	@SerializedName("userdef6")
	@Column(name = "USERDEFINED_FIELD6")
	protected String userdefinedfield6;

	@Expose
	@SerializedName("userdef7")
	@Column(name = "USERDEFINED_FIELD7")
	protected String userdefinedfield7;

	@Expose
	@SerializedName("userdef8")
	@Column(name = "USERDEFINED_FIELD8")
	protected String userDefinedField8;

	@Expose
	@SerializedName("userdef9")
	@Column(name = "USERDEFINED_FIELD9")
	protected String userdefinedfield9;

	@Expose
	@SerializedName("userdef10")
	@Column(name = "USERDEFINED_FIELD10")
	protected String userdefinedfield10;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	protected String errCodes;

	@Expose
	@SerializedName("infoCodes")
	@Column(name = "INFORMATION_CODES")
	protected String infoCodes;

	
	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Itc04HeaderErrorsEntity document;
}