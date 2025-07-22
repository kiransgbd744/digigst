package com.ey.advisory.service.gstr1.sales.register;

import java.io.Serializable;
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
public class Gstr1SalesRegisterRequestStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private Long requestId = 0L;

	@Expose
	private Integer gstinCount = 0;

	@Expose
	private String reconType;

	@Expose
	private String criteria;	

	@Expose
	private Integer toTaxPeriod;

	@Expose
	private Integer fromTaxPeriod;

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

}
