package com.ey.advisory.app.service.bc.api;

/**
 * @author vishal.verma
 *
 */

import lombok.Data;

@Data
public class DownloadPayloadsdto {

	private String docNo;
	private String sgstin;
	private String irn;
	private String apiType;

}
