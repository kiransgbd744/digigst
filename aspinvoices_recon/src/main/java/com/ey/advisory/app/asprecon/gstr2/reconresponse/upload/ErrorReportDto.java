package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportDto;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorReportDto extends Gstr2InitiateReconReportDto{

	private String errorId;
	private String errorDescription;
}
