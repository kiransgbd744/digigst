/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class TcsCreditData {

	
	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("etin")
	private String eTin;

	@Expose
	@SerializedName("m_id")
	private String mId;

	@Expose
	@SerializedName("sup_val")
	private BigDecimal supVal;
	
	@Expose
	@SerializedName("tx_val")
	private BigDecimal taxVal;
	
	@Expose
	@SerializedName("irt")
	private BigDecimal igstRate;
	
	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmt;
	
	@Expose
	@SerializedName("crt")
	private BigDecimal cgstRate;
	
	@Expose
	@SerializedName("camt")
	private BigDecimal cgstAmt;
	
	@Expose
	@SerializedName("srt")
	private BigDecimal sgstRate;
	
	@Expose
	@SerializedName("samt")
	private BigDecimal sgstAmt;
	
	
	@Expose
	@SerializedName("csrt")
	private BigDecimal cessRate;
	
	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmt;

	

}
