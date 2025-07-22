package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr1VerticalDocSeriesRespDto {

	private String sgstin;
	private String retPeriod;
	private Long docNatureId;
	private Long id;
	private String docNature;
	private String seriesFrom;
	private String seriesTo;
	private String total;
	private String cancelled;
	private String netIssued;
	private List<ErrorDescriptionDto> errorList;
	private Long sNo;
}
