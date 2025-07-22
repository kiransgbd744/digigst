package com.ey.advisory.app.dashboard.homeOld;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mohit Basak
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardHOReqDto {
	
	@Expose
	private Long entityId;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private String category;
	
	@Expose
	private String entityPan;
	
	@Expose
	private String groupCode;
	
	@Expose
	private Integer id;
	
	
}
