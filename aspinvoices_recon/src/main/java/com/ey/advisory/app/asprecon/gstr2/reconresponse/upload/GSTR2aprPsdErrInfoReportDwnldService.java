package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

public interface GSTR2aprPsdErrInfoReportDwnldService {

	public String getPsdData(Long batchId);

	public String getErrorData(Long batchId);

	public String getInfoData(Long batchId);

}
