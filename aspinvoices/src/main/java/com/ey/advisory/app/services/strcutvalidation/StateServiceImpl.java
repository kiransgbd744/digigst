package com.ey.advisory.app.services.strcutvalidation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.common.StaticContextHolder;
@Component("StateServiceImpl")
public class StateServiceImpl implements StateService {

	private StatecodeRepository statesRepository;

	private List<String> stateCodesList = new ArrayList<String>();

	@Override
	public boolean isValidStateCode(String stateCode) {
		statesRepository = StaticContextHolder.
				getBean("StatecodeRepository",StatecodeRepository.class);
		if (stateCodesList.isEmpty()) {
			stateCodesList = statesRepository.findAllStates();
		}
		return stateCodesList.stream().anyMatch(stateCode::equalsIgnoreCase);
	}


}
