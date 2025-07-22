package com.ey.advisory.app.gstr2b.summary;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hema G M
 *
 */
@Data
@NoArgsConstructor
public class Gstr2BSummaryDto {

	@Expose
	private String gstin;
	
	@Expose
	private String stateName;
	
	@Expose
	private String authToken;
	
	@Expose
	private String getGstr2bStatus;
	
	@Expose
	private String status;
	
	@Expose
	private Integer vendorPanCount;
	
	@Expose
	private Integer vendorGstinCount;
	
	@Expose
	private Integer count;
	
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
	
}
