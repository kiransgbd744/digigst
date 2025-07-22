/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class Gstr6IsdItcCrossDto {

	@Expose
	private BigDecimal iamti;

	@Expose
	private BigDecimal iamts;

	@Expose
	private BigDecimal iamtc;

	@Expose
	private BigDecimal samts;

	@Expose
	private BigDecimal samti;

	@Expose
	private BigDecimal camti;

	@Expose
	private BigDecimal camtc;

	@Expose
	private BigDecimal cess;
	
}
