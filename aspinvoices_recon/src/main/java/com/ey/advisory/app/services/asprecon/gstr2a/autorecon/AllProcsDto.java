package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Saif.S
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AllProcsDto {

	private int sequence;
	private String procName;
	private boolean isGstinLevel;
	private String reportName;
	private String reportType;
	private boolean isIsdRecon;
}
