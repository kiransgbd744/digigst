package com.ey.advisory.app.gstr2bprIms.reconresponse.upload;

import org.javatuples.Pair;

/**
 * @author ashutosh.kar
 *
 */
public interface GSTR2bprImsPsdErrInfoReportDwnldService {

	public Pair<String,String>  getPsdData(Long batchId);

	public Pair<String,String>  getErrorData(Long batchId);

}
