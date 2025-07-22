package com.ey.advisory.app.dashboard.homeOld;


import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mohit.basak
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardInnerDto {

	@Expose
	private String action ;

	
	@Expose
	private BigDecimal resp = BigDecimal.ZERO;

}

