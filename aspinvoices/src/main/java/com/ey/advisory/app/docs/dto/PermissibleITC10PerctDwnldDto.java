package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh NK
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PermissibleITC10PerctDwnldDto {
	
	@Expose
	@SerializedName("rgstin")
	private String rgstin;
	
	@Expose
	@SerializedName("vgstin")
	private String vgstin;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("totalItc")
	private BigDecimal totalItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalIgst")
	private BigDecimal totalIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalCgst")
	private BigDecimal totalCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalSgst")
	private BigDecimal totalSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalCess")
	private BigDecimal totalCess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAvlItc")
	private BigDecimal totalAvlItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalAvailableIgst")
	private BigDecimal totalAvailableIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAvailableCgst")
	private BigDecimal totalAvailableCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalAvailableSgst")
	private BigDecimal totalAvailableSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAvailableCess")
	private BigDecimal totalAvailableCess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalPermItc")
	private BigDecimal totalPermItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalPermIgst")
	private BigDecimal totalPermIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalPermCgst")
	private BigDecimal totalPermCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalPermSgst")
	private BigDecimal totalPermSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalPermCess")
	private BigDecimal totalPermCess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalDeferredItc")
	private BigDecimal totalDeferredItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalDeferredIgst")
	private BigDecimal totalDeferredIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalDeferredCgst")
	private BigDecimal totalDeferredCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalDeferredSgst")
	private BigDecimal totalDeferredSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalDeferredCess")
	private BigDecimal totalDeferredCess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAcceptedPRItc")
	private BigDecimal totalAcceptedPRItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalAcceptedPRIgst")
	private BigDecimal totalAcceptedPRIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAcceptedPRCgst")
	private BigDecimal totalAcceptedPRCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalAcceptedPRSgst")
	private BigDecimal totalAcceptedPRSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAcceptedPRCess")
	private BigDecimal totalAcceptedPRCess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAccepted2AItc")
	private BigDecimal totalAccepted2AItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalAccepted2AIgst")
	private BigDecimal totalAccepted2AIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAccepted2ACgst")
	private BigDecimal totalAccepted2ACgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalAccepted2ASgst")
	private BigDecimal totalAccepted2ASgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalAccepted2ACess")
	private BigDecimal totalAccepted2ACess = BigDecimal.ZERO;


}
