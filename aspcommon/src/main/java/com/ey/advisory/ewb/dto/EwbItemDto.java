/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */
@Setter
@Getter
@ToString
public class EwbItemDto implements Serializable{

/**
* Product / Item Name
*
*/
@SerializedName("productName")
@Expose
public String productName;
/**
* Product / Item description
*
*/
@SerializedName("productDesc")
@Expose
public String productDesc;
/**
* HSN Code
*
*/
@SerializedName("hsnCode")
@Expose
public String hsnCode;
/**
* Quantity
*
*/
@SerializedName("quantity")
@Expose
public BigDecimal quantity;
/**
* Unit
*
*/
@SerializedName("qtyUnit")
@Expose
public String qtyUnit;
/**
* Taxable Amount
*
*/
@SerializedName("taxableAmount")
@Expose
public BigDecimal taxableAmount;
/**
* SGST Rate of Tax
*
*/
@SerializedName("sgstRate")
@Expose
public BigDecimal sgstRate;
/**
* CGST Rate of Tax
*
*/
@SerializedName("cgstRate")
@Expose
public BigDecimal cgstRate;
/**
* IGST Rate of Tax
*
*/
@SerializedName("igstRate")
@Expose
public BigDecimal igstRate;
/**
* Cess Rate of Tax
*
*/
@SerializedName("cessRate")
@Expose
public BigDecimal cessRate;
/**
* Cess Non-Advolerum
*
*/
@SerializedName("cessNonadvol")
@Expose
public BigDecimal cessNonadvol;
private final static long serialVersionUID = -3029953604256580752L;

}
