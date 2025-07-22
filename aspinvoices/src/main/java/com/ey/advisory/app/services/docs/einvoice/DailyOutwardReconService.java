/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import java.util.List;

import com.ey.advisory.app.docs.dto.DailyOutwardAndInwardReconDto;
import com.ey.advisory.app.docs.dto.DailyOutwardAndInwardReconReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface DailyOutwardReconService {

	public void persistData(List<DailyOutwardAndInwardReconDto> payload);

	public List<DailyOutwardAndInwardReconDto> getData(DailyOutwardAndInwardReconReqDto request);
}
