package com.ey.advisory.app.services.datasecurity;

import java.util.List;

import com.ey.advisory.app.docs.dto.AnxDataSecurityDto;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public interface AnxDataSecurityService {

	List<AnxDataSecurityDto> getAnxDataSecurityAttributes();
	
	AnxDataSecurityDto getAnxDataSecurityApplAttributes(Long entityId);
	
}
