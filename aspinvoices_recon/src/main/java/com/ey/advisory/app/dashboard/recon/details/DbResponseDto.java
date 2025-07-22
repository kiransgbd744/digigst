package com.ey.advisory.app.dashboard.recon.details;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author vishal.verma
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DbResponseDto {
	
	@Expose
	private  BigDecimal percentage = BigDecimal.ZERO;
	
	@Expose
	private  String response;

	
}
