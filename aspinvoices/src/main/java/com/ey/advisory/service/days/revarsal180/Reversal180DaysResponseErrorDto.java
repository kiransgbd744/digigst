package com.ey.advisory.service.days.revarsal180;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */

@Data
public class Reversal180DaysResponseErrorDto
		extends ITCReversal180ReportDownloadDto {

	private String errorCode;
	private String errorDescription;

}
