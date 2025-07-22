package com.ey.advisory.app.reconewbvsitc04;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EwbVsItc04InitiateReconReportRequestStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private Long requestId = 0L;

	@Expose
	private BigInteger gstinCount = BigInteger.ZERO;

	@Expose
	private String financialYear;

	@Expose
	private String fromTaxPeriod;
	
	@Expose
	private String toTaxPeriod;

	@Expose
	private List<GstinDto> gstins;

	@Expose
	private String initiatedOn;

	@Expose
	private String initiatedBy;

	@Expose
	private String completionOn;

	@Expose
	private String status;

	@Expose
	private String emailId;

}
