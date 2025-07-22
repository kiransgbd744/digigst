/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

/**
 * @author Hemasundar.J
 *
 */
public interface AnxErpScenarioControlHelper {

	/**
	 * This method is useful for fetching the Minimum and Maximum ranges of 
	 * outward/sales data. Here Minimum and Maximum ranges are Primary key
	 * of OUTWARD table. This method can be used for sending incremental/
	 * invoice level data to ERP
	 * @param gstin
	 * @param scenarioId
	 * @param destination
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public Pair<Long, Long> getRange(String gstin, Long scenarioId,
			OutwardTransDocument out) throws Exception;

	/**
	 * This method is useful for fetching the Minimum and Maximum ranges of 
	 * inward/purchase data. Here Minimum and Maximum ranges are Primary key
	 * of INWARD table. This method can be used for sending incremental/
	 * invoice level data to ERP
	 * @param gstin
	 * @param scenarioId
	 * @param destination
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public Pair<Long, Long> getRange(String gstin, Long scenarioId,
			InwardTransDocument out) throws Exception;
	
	/**
	 * This method is useful for fetching Maximum range of outward/sales data. 
	 * Here Maximum range is Primary key of OUTWARD table. This method can be 
	 * used for sending overall summary level data to ERP
	 * @param gstin
	 * @param scenarioId
	 * @param destination
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public Long getMaxRange(String gstin, Long scenarioId,
			OutwardTransDocument out) throws Exception;
	
	
	/**
	 * This method is useful for fetching Maximum range of inward/purchase data. 
	 * Here Maximum range is Primary key of INWARD table. This method can be 
	 * used for sending overall summary level data to ERP
	 * @param gstin
	 * @param scenarioId
	 * @param destination
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public Long getMaxRange(String gstin, Long scenarioId,
			InwardTransDocument out) throws Exception;
	
	
	public Long getBatchId(String gstin, Long scenarioId,
			Long batchId, OutwardTransDocument out) throws Exception;
	
	
	public Long getBatchId(String gstin, Long scenarioId,
			Long batchId, InwardTransDocument out)
			throws Exception;

	
	

}
