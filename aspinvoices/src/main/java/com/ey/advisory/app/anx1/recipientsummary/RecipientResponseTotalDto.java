package com.ey.advisory.app.anx1.recipientsummary;
import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;


/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
public class RecipientResponseTotalDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Expose
	private Integer percent = 0;
	
	@Expose
	private Integer count = 0;
	
	@Expose
	private BigDecimal taxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxAmt = BigDecimal.ZERO;

	/**
	 * @param count
	 * @param taxableVal
	 * @param taxAmt
	 */
	public RecipientResponseTotalDto(Integer count, BigDecimal taxableVal,
			BigDecimal taxAmt, Integer percent) {
		super();
		this.count = count;
		this.taxableVal = taxableVal;
		this.taxAmt = taxAmt;
		this.percent = percent;
	}
	
	public RecipientResponseTotalDto() {
		
	}

	

}
