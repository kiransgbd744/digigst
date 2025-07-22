package com.ey.advisory.app.services.strcutvalidation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.PortCodeRepository;
@Component
public class PortServiceImpl implements PortService{
	
	private PortCodeRepository portCodeRepository;
	
	private List<String> portCodes = new ArrayList<String>();

	@Override
	public boolean isValidPortCode(String portCode) {
		if (portCodes.isEmpty()) {
			portCodes = portCodeRepository.findAllPortCodes();
		}
		return portCodes.stream().anyMatch(portCode::equalsIgnoreCase);
	}

}
