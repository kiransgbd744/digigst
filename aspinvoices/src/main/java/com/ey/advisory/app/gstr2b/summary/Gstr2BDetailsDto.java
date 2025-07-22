package com.ey.advisory.app.gstr2b.summary;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr2BDetailsDto {

	
	@Expose
	private Integer gstinCount;
	
	@Expose
	private String Rgstin;
	
	@Expose
	private Integer panCount;
	
	@Expose
	private Integer count;
	
	@Expose
	private String tableName;
	
	@Expose
	private BigDecimal taxablevalue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal totalTaxIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal totalTaxCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal totalTaxSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal totalTaxCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal availItcIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal availItcCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal availItcSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal availItcCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal nonAvailItcIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal nonAvailItcCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal nonAvailItcSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal nonAvailItcCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal rejectedItcIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal rejectedItcCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal rejectedItcSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal rejectedItcCess = BigDecimal.ZERO;
	
	@Expose
	private String level = "L2";
	
}
