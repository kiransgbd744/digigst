package com.ey.advisory.app.gmr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GmrOutwardEntityAndMonthDto {
	
	private String entityName;
	private String fromMonth;
	private String toMonth;

}
