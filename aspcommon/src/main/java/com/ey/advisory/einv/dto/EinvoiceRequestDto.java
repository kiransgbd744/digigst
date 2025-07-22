
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class EinvoiceRequestDto implements Serializable {
	
	private static final  long serialVersionUID = 7365884294874645319L;

	/**
	 * Invoice Reference No.
	 * 
	 */
	@SerializedName("Irn")
	@Expose
	private String irn;
	
	@SerializedName("Version")
	@Expose
	private String version;
	
	@SerializedName("TaxSch")
	@Expose
	private String taxSch;
	
	@Transient
	private String docCategory;
	
	@SerializedName("TranDtls")
	@Expose
	private TransactionDetails tranDtls;
	@SerializedName("DocDtls")
	@Expose
	private DocumentDetails docDtls;
	@SerializedName("SellerDtls")
	@Expose
	private SellerDetails sellerDtls;
	@SerializedName("BuyerDtls")
	@Expose
	private BuyerDetails buyerDtls;
	@SerializedName("DispDtls")
	@Expose
	private DispatchDetails dispDtls;
	@SerializedName("ShipDtls")
	@Expose
	private ShippingDetails shipDtls;
	@SerializedName("ItemList")
	@Expose
	private List<ItemDto> itemList ;
	@SerializedName("ValDtls")
	@Expose
	private ValueDetails valDtls;
	@SerializedName("PayDtls")
	@Expose
	private PayeeDetails payDtls;
	@SerializedName("RefDtls")
	@Expose
	private RefDtls refDtls;
	@SerializedName("ExpDtls")
	@Expose
	private ExportDetails expDtls;
	@SerializedName("EwbDtls")
	@Expose
	private EinvEwbDetails ewbDetails;
	@SerializedName("AddlDocDtls")
	@Expose
	private List<AddlDocument> addlDocDtls;
	@SerializedName("AckNo")
	@Expose
	private String ackNum;
	@SerializedName("AckDt")
	@Expose
	private String ackDt;
	
	

}
