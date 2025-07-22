package com.ey.advisory.ewb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EwayBillRequestDto {

	/**
	 * Supply Type
	 * 
	 */
	@SerializedName("supplyType")
	@Expose
	private String supplyType;
	/**
	 * Sub Supply Type
	 * 
	 */
	@SerializedName("subSupplyType")
	@Expose
	private String subSupplyType;
	/**
	 * Other Sub Supply Description
	 * 
	 */
	@SerializedName("subSupplyDesc")
	@Expose
	private String subSupplyDesc;
	/**
	 * Document Type
	 * 
	 */
	@SerializedName("docType")
	@Expose
	private String docType;
	/**
	 * Document Number (Alphanumeric with / and - are allowed)
	 * 
	 */
	@SerializedName("docNo")
	@Expose
	private String docNo;
	/**
	 * Document Date
	 * 
	 */
	@SerializedName("docDate")
	@Expose
	private LocalDate docDate;
	/**
	 * From GSTIN (Supplier or Consignor)
	 * 
	 */
	@SerializedName("fromGstin")
	@Expose
	private String fromGstin;
	/**
	 * From Trade Name (Consignor Trade name)
	 * 
	 */
	@SerializedName("fromTrdName")
	@Expose
	private String fromTrdName;
	/**
	 * From Address Line 1 (Valid Special Chars #,-,/)
	 * 
	 */
	@SerializedName("fromAddr1")
	@Expose
	private String fromAddr1;
	/**
	 * From Address Line 2(Valid Special Chars # , - ,/)
	 * 
	 */
	@SerializedName("fromAddr2")
	@Expose
	private String fromAddr2;
	/**
	 * From Place
	 * 
	 */
	@SerializedName("fromPlace")
	@Expose
	private String fromPlace;
	/**
	 * Actual From State Code
	 * 
	 */
	@SerializedName("actFromStateCode")
	@Expose
	private String actFromStateCode;
	/**
	 * From Pincode
	 * 
	 */
	@SerializedName("fromPincode")
	@Expose
	private Integer fromPincode;
	/**
	 * From State Code
	 * 
	 */
	@SerializedName("fromStateCode")
	@Expose
	private String fromStateCode;
	/**
	 * To GSTIN (Consignee or Recipient)
	 * 
	 */
	@SerializedName("toGstin")
	@Expose
	private String toGstin;
	/**
	 * To Trade Name (Consignee Trade name or Recipient Trade name)
	 * 
	 */
	@SerializedName("toTrdName")
	@Expose
	private String toTrdName;
	/**
	 * To Address Line 1 (Valid Special Chars #,-,/)
	 * 
	 */
	@SerializedName("toAddr1")
	@Expose
	private String toAddr1;
	/**
	 * To Address Line 2 (Valid Special Chars #,-,/)
	 * 
	 */
	@SerializedName("toAddr2")
	@Expose
	private String toAddr2;
	/**
	 * To Place
	 * 
	 */
	@SerializedName("toPlace")
	@Expose
	private String toPlace;
	/**
	 * To Pincode
	 * 
	 */
	@SerializedName("toPincode")
	@Expose
	private Integer toPincode;
	/**
	 * Actual To State Code
	 * 
	 */
	@SerializedName("actToStateCode")
	@Expose
	private String actToStateCode;
	/**
	 * To State Code
	 * 
	 */
	@SerializedName("toStateCode")
	@Expose
	private String toStateCode;
	/**
	 * Transaction type
	 * 
	 */
	@SerializedName("transactionType")
	@Expose
	private String transactionType;
	/**
	 * Sum of Taxable value and Tax value
	 * 
	 */
	@SerializedName("totalValue")
	@Expose
	private BigDecimal totalValue;
	/**
	 * CGST value
	 * 
	 */
	@SerializedName("cgstValue")
	@Expose
	private BigDecimal cgstValue;
	/**
	 * SGST value
	 * 
	 */
	@SerializedName("sgstValue")
	@Expose
	private BigDecimal sgstValue;
	/**
	 * IGST value
	 * 
	 */
	@SerializedName("igstValue")
	@Expose
	private BigDecimal igstValue;
	/**
	 * Cess value
	 * 
	 */
	@SerializedName("cessValue")
	@Expose
	private BigDecimal cessValue;
	/**
	 * Cess Non Advol value
	 * 
	 */
	@SerializedName("cessNonAdvolValue")
	@Expose
	private BigDecimal cessNonAdvolValue;
	/**
	 * Other charges, if any
	 * 
	 */
	@SerializedName("otherValue")
	@Expose
	private BigDecimal otherValue;
	/**
	 * Total Invoice Value (Including taxable value, tax value,and other charges
	 * if any)
	 * 
	 */
	@SerializedName("totInvValue")
	@Expose
	private BigDecimal totInvValue;
	/**
	 * Mode of transport (Road-1, Rail-2, Air-3, Ship-4)
	 * 
	 */
	@SerializedName("transMode")
	@Expose
	private String transMode;
	/**
	 * Distance (<4000 km)
	 * 
	 */
	@SerializedName("transDistance")
	@Expose
	private Integer transDistance;
	/**
	 * Name of the transporter
	 * 
	 */
	@SerializedName("transporterName")
	@Expose
	private String transporterName;
	/**
	 * 15 Digit Transporter GSTIN/TRANSIN
	 * 
	 */
	@SerializedName("transporterId")
	@Expose
	private String transporterId;
	/**
	 * Transport Document Number (Alphanumeric with / and – are allowed)
	 * 
	 */
	@SerializedName("transDocNo")
	@Expose
	private String transDocNo;
	/**
	 * Transport Document Date
	 * 
	 */
	@SerializedName("transDocDate")
	@Expose
	private LocalDate transDocDate;
	/**
	 * Vehicle Number
	 * 
	 */
	@SerializedName("vehicleNo")
	@Expose
	private String vehicleNo;
	/**
	 * Vehicle Type
	 * 
	 */
	@SerializedName("vehicleType")
	@Expose
	private String vehicleType;
	@SerializedName("itemList")
	@Expose
	private List<EwbItemDto> itemList = null;
	
	private Integer aspDistance;

}
