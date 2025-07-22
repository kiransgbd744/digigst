/**
 * 
 */
package com.ey.advisory.app.dashboard.homeOld;

import java.util.List;

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
public class DashboardHOOutwardSupplyUIDto {

	@Expose
	private List<DashboardInnerDto> taxValue ;

	@Expose
	private List<DashboardInnerDto> totalTax ;
	
}
