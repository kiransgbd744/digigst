/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr2b;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr2BLinkingDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr2bLinkingService {

	List<Gstr2bLinkingResponseDto> getGstr2bLinkingRecords(
			Gstr2BLinkingDto dto);

}
