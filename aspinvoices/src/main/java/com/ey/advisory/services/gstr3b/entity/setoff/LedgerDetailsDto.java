/**
 * 
 */
package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class LedgerDetailsDto {
	
	private String desc;

	private BigDecimal i = BigDecimal.ZERO;

	private BigDecimal c = BigDecimal.ZERO;

	private BigDecimal s = BigDecimal.ZERO;

	private BigDecimal cs = BigDecimal.ZERO;
	
	private BigDecimal total = BigDecimal.ZERO;

	private BigDecimal cri = BigDecimal.ZERO;

	private BigDecimal crc = BigDecimal.ZERO;

	private BigDecimal crs = BigDecimal.ZERO;
	
	private BigDecimal crcs = BigDecimal.ZERO;

	private BigDecimal crTotal = BigDecimal.ZERO;
	
	private BigDecimal nlbIgst = BigDecimal.ZERO;

	private BigDecimal nlbCgst = BigDecimal.ZERO;

	private BigDecimal nlbSgst = BigDecimal.ZERO;
	
	private BigDecimal nlbCess = BigDecimal.ZERO;
	
	private BigDecimal nlbTotal = BigDecimal.ZERO;
}
