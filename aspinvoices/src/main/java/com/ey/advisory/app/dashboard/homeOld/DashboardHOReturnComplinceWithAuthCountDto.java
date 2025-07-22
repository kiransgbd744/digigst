package com.ey.advisory.app.dashboard.homeOld;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DashboardHOReturnComplinceWithAuthCountDto {
	
	@Expose
	private List<DashboardHOReturnComplianceStatusDto> supplyDetailsDto;
	
	@Expose
	private int authTokenTotal = 0;

	@Expose
	private int authTokenActive  = 0;
	
	@Expose
	private int authTokenInActive  = 0;


}
