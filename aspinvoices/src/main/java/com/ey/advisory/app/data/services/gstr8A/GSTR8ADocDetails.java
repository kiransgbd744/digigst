/**
 * 
 */
package com.ey.advisory.app.data.services.gstr8A;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Arun.KA
 *
 */

@Data
public class GSTR8ADocDetails {
	
	@SerializedName("inum")
	@Expose
	String invoiceNum;
	
	@SerializedName("idt")
	@Expose
	LocalDate invoiceDate;//changed from string
	
	@SerializedName("oinum")
	@Expose
	String origiInvoiceNo;
	
	@SerializedName("oidt")
	@Expose
	LocalDate origiInvoiceDate;//changed from string--B2Ba
	
	@SerializedName("ntty")
	@Expose
	String noteType;
	
	@SerializedName("nt_num")
	@Expose
	String noteNumber;
	
	@SerializedName("nt_dt")
	@Expose
	LocalDate noteDate;//changed from string--Cdn
	
	@SerializedName("ont_num")
	@Expose
	String origiNoteNum;
	
	@SerializedName("ont_dt")
	@Expose
	LocalDate origiNoteDate;//changed
	
	@SerializedName("ontty")
	@Expose
	String origiNoteType;
	
	@SerializedName("val")
	@Expose
	BigDecimal val;//changes from string
	
	@SerializedName("pos")
	@Expose
	String pos;
	
	@SerializedName("rchrg")
	@Expose
	String reverseCharge;
	
	@SerializedName("txval")
	@Expose
	BigDecimal taxableValue;//changed from string
	
	@SerializedName("iamt")
	@Expose
	BigDecimal iamt;//changed from string
	
	@SerializedName("samt")
	@Expose
	BigDecimal samt;//changed from string
	
	@SerializedName("camt")
	@Expose
	BigDecimal camt;//changed from string
	
	@SerializedName("csamt")
	@Expose
	BigDecimal csamt;//changed from string
	
	@SerializedName("inv_typ")
	@Expose
	String invoiceType;
	
	@SerializedName("iseligible")
	@Expose
	String isEligible;
	
	@SerializedName("reason")
	@Expose
	String reason;
	
	@SerializedName("rt")
	@Expose
	String rt;
	
	
    	 
    	 

}
