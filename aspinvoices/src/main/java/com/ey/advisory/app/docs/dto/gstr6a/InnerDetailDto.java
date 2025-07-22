package com.ey.advisory.app.docs.dto.gstr6a;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InnerDetailDto {
	
	@Expose
	private String taxPeriod;

	@Expose
	private String initiatedOn;

	@Expose
	private String status;

}
