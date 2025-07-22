/**
 * 
 */
package com.ey.advisory.app.docs.services.gstr6;

import java.util.List;

import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr6ProcessedDataDao {

	List<Object[]> getGstr6ProcessedRec(Gstr6SummaryRequestDto dto);
	
	List<Object[]> getGstr6ProcessedProcRecords(Gstr6SummaryRequestDto dto);

}
