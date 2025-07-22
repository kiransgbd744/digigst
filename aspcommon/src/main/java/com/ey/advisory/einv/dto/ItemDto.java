
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ItemDto implements Serializable {

	private static final long serialVersionUID = 2255917340896377460L;

	/**
	 * Serial No. of Item
	 *
	 */
	/*@SerializedName("ItemNo")
	@Expose
	private String itemNo;*/
	
	/**
	 * Serial No. of Item
	 *
	 */
	@SerializedName("SlNo")
	@Expose
	private String slNo;
	/**
	 * Product Description
	 *
	 */
	@SerializedName("PrdDesc")
	@Expose
	private String prdDesc;
	/**
	 * Specify whether the supply is service or not. Specify Y-for Service
	 *
	 */
	@SerializedName("IsServc")
	@Expose
	private String isServc;
	/**
	 * HSN Code
	 *
	 */
	@SerializedName("HsnCd")
	@Expose
	private String hsnCd;
	@SerializedName("BchDtls")
	@Expose
	private BatchDetails bchDtls;
	/**
	 * Bar Code
	 *
	 */
	@SerializedName("Barcde")
	@Expose
	private String barcde;
	/**
	 * Quantity
	 *
	 */
	@SerializedName("Qty")
	@Expose
	private BigDecimal qty;
	/**
	 * Free Quantity
	 *
	 */
	@SerializedName("FreeQty")
	@Expose
	private BigDecimal freeQty;
	/**
	 * Unit
	 *
	 */
	@SerializedName("Unit")
	@Expose
	private String unit;
	/**
	 * Unit Price - Rate
	 *
	 */
	@SerializedName("UnitPrice")
	@Expose
	private BigDecimal unitPrice;
	/**
	 * Gross Amount Amount (Unit Price * Quantity)
	 *
	 */
	@SerializedName("TotAmt")
	@Expose
	private BigDecimal totAmt;
	/**
	 * Discount
	 *
	 */
	@SerializedName("Discount")
	@Expose
	private BigDecimal discount;
	/**
	 * Pre tax value
	 *
	 */
	@SerializedName("PreTaxVal")
	@Expose
	private BigDecimal preTaxVal;
	/**
	 * Taxable Value (Total Amount -Discount)
	 *
	 */
	@SerializedName("AssAmt")
	@Expose
	private BigDecimal assAmt;
	/**
	 * The GST rate, represented as percentage that applies to the invoiced
	 * item. It will IGST rate only.
	 *
	 */
	@SerializedName("GstRt")
	@Expose
	private BigDecimal gstRt;
	/**
	 * Amount of IGST payable.
	 *
	 */
	@SerializedName("IgstAmt")
	@Expose
	private BigDecimal igstAmt;
	/**
	 * Amount of CGST payable.
	 *
	 */
	@SerializedName("CgstAmt")
	@Expose
	private BigDecimal cgstAmt;
	/**
	 * Amount of SGST payable.
	 *
	 */
	@SerializedName("SgstAmt")
	@Expose
	private BigDecimal sgstAmt;
	/**
	 * Cess Rate
	 *
	 */
	@SerializedName("CesRt")
	@Expose
	private BigDecimal cesRt;
	/**
	 * Cess Amount(Advalorem) on basis of rate and quantity of item
	 *
	 */
	@SerializedName("CesAmt")
	@Expose
	private BigDecimal cesAmt;
	/**
	 * Cess Non-Advol Amount
	 *
	 */
	@SerializedName("CesNonAdvlAmt")
	@Expose
	private BigDecimal cesNonAdvlAmt;
	/**
	 * State CESS Rate
	 *
	 */
	@SerializedName("StateCesRt")
	@Expose
	private BigDecimal stateCesRt;
	/**
	 * State CESS Amount
	 *
	 */
	@SerializedName("StateCesAmt")
	@Expose
	private BigDecimal stateCesAmt;
	/**
	 * State CESS Non Adval Amount
	 *
	 */
	@SerializedName("StateCesNonAdvlAmt")
	@Expose
	private BigDecimal stateCesNonAdvlAmt;
	/**
	 * Other Charges
	 *
	 */
	@SerializedName("OthChrg")
	@Expose
	private BigDecimal othChrg;
	/**
	 * Total Item Value = Assessable Amount + CGST Amt + SGST Amt + Cess Amt +
	 * CesNonAdvlAmt + StateCesAmt + StateCesNonAdvlAmt+Otherchrg
	 *
	 */
	@SerializedName("TotItemVal")
	@Expose
	private BigDecimal totItemVal;
	/**
	 * Order line referencee
	 *
	 */
	@SerializedName("OrdLineRef")
	@Expose
	private String ordLineRef;
	/**
	 * Orgin Country
	 *
	 */
	@SerializedName("OrgCntry")
	@Expose
	private String orgCntry;
	/**
	 * Serial number in case of each item having a unique number.
	 *
	 */
	@SerializedName("PrdSlNo")
	@Expose
	private String prdSlNo;
	@SerializedName("AttribDtls")
	@Expose
	private List<Attribute> attribDtls;

}
