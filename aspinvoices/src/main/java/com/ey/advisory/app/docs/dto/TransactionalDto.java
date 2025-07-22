/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class TransactionalDto {

	@Expose
	private String gstin;

	@Expose
	private String taxPeriod;

	@Expose
	private String sourceId;

	@Expose
	private String status;
	
	@Expose
	private String docType;

	@Expose
	private String supplyType;
	
	@Expose
	@SerializedName("erpNumofinv")
	private Integer erpNumOfInvs;
	
	@Expose
	@SerializedName("erpTotinvVal")
	private String erpTotalInvValue;

	@Expose
	@SerializedName("erpAccValue")
	private String erpAccValue;

	@Expose
	private String erpIgst;
	
	@Expose
	private String erpCgst;

	@Expose
	private String erpSgst;
	
	@Expose
	private String erpCess;
	
	@Expose
	@SerializedName("cloudNumofinv")
	private Integer cloudNumOfInvs;

	@Expose
	@SerializedName("cloudTotinvVal")
	private String cloudTotalInvValue;

	@Expose
	@SerializedName("cloudAccValue")
	private String cloudAccValue;

	@Expose
	private String cloudIgst;
	
	@Expose
	private String cloudCgst;

	@Expose
	private String cloudSgst;
	
	@Expose
	private String cloudCess;

}
