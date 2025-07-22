package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import java.io.IOException;

import org.javatuples.Pair;

/**
 * @author vishal.verma
 *
 */
public interface ReportDao {

	public Pair<String,String> getPsdData(Long batchId) throws IOException;

	public Pair<String,String> getErrorData(Long BatchId) throws IOException;
	
	public Pair<String,String> getInfoData(Long batchId) throws IOException;
}
