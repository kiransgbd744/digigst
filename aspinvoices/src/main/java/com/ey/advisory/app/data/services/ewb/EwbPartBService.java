/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.TransportPartBDetailsDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface EwbPartBService {

	List<TransportPartBDetailsDto> getPartBDetailsByEwbNo(List<String> ewbNos);

}
