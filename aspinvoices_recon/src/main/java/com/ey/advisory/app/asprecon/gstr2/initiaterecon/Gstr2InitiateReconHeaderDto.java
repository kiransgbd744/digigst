/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2InitiateReconHeaderDto {
	

	@Expose
	@SerializedName("section")
	protected String section;

	@Expose
	@SerializedName("gstr2Count")
	protected int gstr2Count;

	@Expose
	@SerializedName("gstr2TaxableValue")
	protected BigDecimal gstr2TaxableValue;

	@Expose
	@SerializedName("gstr2IGST")
	protected BigDecimal gstr2IGST;

	@Expose
	@SerializedName("gstr2CGST")
	protected BigDecimal gstr2CGST;

	@Expose
	@SerializedName("gstr2SGST")
	protected BigDecimal gstr2SGST;

	@Expose
	@SerializedName("gstr2Cess")
	protected BigDecimal gstr2Cess;

	@Expose
	@SerializedName("prCount")
	protected Integer prCount;

	@Expose
	@SerializedName("prTaxableValue")
	protected BigDecimal prTaxableValue;

	@Expose
	@SerializedName("prIGST")
	protected BigDecimal prIGST;

	@Expose
	@SerializedName("prCGST")
	protected BigDecimal prCGST;

	@Expose
	@SerializedName("prSGST")
	protected BigDecimal prSGST;

	@Expose
	@SerializedName("prCess")
	protected BigDecimal prCess;

	@Expose
	@SerializedName("avIGST")
	protected BigDecimal avIgst;

	@Expose
	@SerializedName("avCGST")
	protected BigDecimal avCgst;

	@Expose
	@SerializedName("avSGST")
	protected BigDecimal avSgst;

	@Expose
	@SerializedName("avCess")
	protected BigDecimal avCess;

	@Expose
	@SerializedName("items")
	private List<Object> lineItems;

	
}
