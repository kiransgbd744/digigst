/**
 * 
 */
package com.ey.advisory.app.jsonpushback;

import java.util.List;

/**
 * @author Khalid1.Khan
 *
 */
public interface PushJsonToErpDao {

	List<BatchErrorResponseDto> getErrorRecords(Long batchId, String gstin);

	List<BatchErrorResponseDto> getAspErrorRecords(String gstin, Long value0,
			Long value1);

}
