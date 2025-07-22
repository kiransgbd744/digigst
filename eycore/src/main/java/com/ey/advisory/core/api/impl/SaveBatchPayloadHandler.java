/**
 * 
 */
package com.ey.advisory.core.api.impl;

/**
 * @author Hemasundar.J
 *
 */
public interface SaveBatchPayloadHandler {

	public String dumpSaveRequestPayload(String groupCode, Long id,
			String requestObject);
	
	public String dumpSaveResponsePayload(String groupCode, Long id,
			String requestObject);
	
	public String dumpGetReturnStatusResponsePayload(String groupCode, Long id,
			String requestObject);
}
