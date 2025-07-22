package com.ey.advisory.app.gmr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GmrInwardEntityAndMonthDto {
	
	private String entityName;
	private String fromMonth;
	private String toMonth;

}
