package com.ey.advisory.app.gstr2bpr.reconresponse.upload;

import org.javatuples.Pair;

public interface GSTR2bprPsdErrInfoReportDwnldService {

	public Pair<String,String>  getPsdData(Long batchId);

	public Pair<String,String>  getErrorData(Long batchId);

	public Pair<String,String>  getInfoData(Long batchId);

}
