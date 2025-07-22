package com.ey.advisory.app.gstr2aprIms.reconresponse.upload;

import org.javatuples.Pair;

/**
 * @author Akhilesh.Yadav
 *
 */
public interface GSTR2aprImsPsdErrInfoReportDwnldService {

	public Pair<String,String>  getPsdData(Long batchId);

	public Pair<String,String>  getErrorData(Long batchId);

}
