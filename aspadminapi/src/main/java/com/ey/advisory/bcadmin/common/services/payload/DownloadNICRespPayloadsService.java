package com.ey.advisory.bcadmin.common.services.payload;

import jakarta.servlet.http.HttpServletResponse;

public interface DownloadNICRespPayloadsService {

	public void downloadRespPayloads(String docNo, String sgstin, String irn,
			String mode, HttpServletResponse response);

}
