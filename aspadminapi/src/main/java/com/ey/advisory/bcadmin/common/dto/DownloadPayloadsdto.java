package com.ey.advisory.bcadmin.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DownloadPayloadsdto {

	private String docNo;
	private String sgstin;
	private String irn;
	private String apiType;

}
