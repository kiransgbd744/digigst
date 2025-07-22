package com.ey.advisory.app.services.gstr1;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr1PopScreenRecordsFinalResponseDto {

	private List<Gstr1PopScreenRecordsResponseDto> lastCall = Lists
			.newArrayList();

	private List<Gstr1PopScreenRecordsResponseDto> lastSuccess = Lists
			.newArrayList();
}
