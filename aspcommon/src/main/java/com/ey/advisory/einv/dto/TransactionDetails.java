package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TransactionDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	* GST- Goods and Services Tax Scheme
	*
	*/
	@SerializedName("TaxSch")
	@Expose
	private String taxSch;
	/**
	* Type of Supply: B2B-Business to Business, SEZWP - SEZ with payment, SEZWOP - SEZ without payment, EXPWP - Export with Payment, EXPWOP - Export without payment,DEXP - Deemed Export
	*
	*/
	@SerializedName("SupTyp")
	@Expose
	private String supTyp;
	/**
	* Y- whether the tax liability is payable under reverse charge
	*
	*/
	@SerializedName("RegRev")
	@Expose
	private String regRev;
	/**
	* GSTIN of e-Commerce operator
	*
	*/
	@SerializedName("EcmGstin")
	@Expose
	private String ecmGstin;
	
	/**
	* GSTIN of e-Commerce operator
	*
	*/
	@SerializedName("IgstOnIntra")
	@Expose
	private String igstOnIntra;

	public static boolean isEmpty(TransactionDetails transactionDetails) {
		TransactionDetails trd = new TransactionDetails();
		return transactionDetails.hashCode() == trd.hashCode();
	}
	

	

}
