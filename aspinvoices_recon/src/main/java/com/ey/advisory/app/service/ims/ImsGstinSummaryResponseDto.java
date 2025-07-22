package com.ey.advisory.app.service.ims;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ImsGstinSummaryResponseDto {
	
	@Expose
	private String getCallTimeStamp;

	@Expose
	private String table;

	@Expose
	private BigInteger gstnTotal = BigInteger.ZERO;

	@Expose
	private BigInteger gstnNoAction = BigInteger.ZERO;

	@Expose
	private BigInteger gstnAccepted = BigInteger.ZERO;

	@Expose
	private BigInteger gstnRejected = BigInteger.ZERO;

	@Expose
	private BigInteger gstnPendingTotal = BigInteger.ZERO;

	@Expose
	private BigInteger aspTotal = BigInteger.ZERO;

	@Expose
	private Integer aspNoAction = 0;

	@Expose
	private Integer aspAccepted = 0;

	@Expose
	private Integer aspRejected = 0;

	@Expose
	private Integer aspPendingTotal = 0;

	@Expose
	private String rowStyle;

	@Expose
	private List<ImsGstinSummaryResponseDto> items = new ArrayList<>();

}
