package com.ey.advisory.app.data.statelist;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.app.caches.DefaultStateCache;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Vishal.Verma
 *
 */

@Slf4j
@Component("StateListServiceImpl")
public class StateListServiceImpl implements StateListService {

	@Autowired
	DefaultStateCache defaultStateCache;

	@Override
	public List<StateListDto> findStates() {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside StateListServiceImpl"
					+ ".getAllState, calling StatecodeRepository.";
			LOGGER.debug(msg);
		}
		List<StateCodeInfoEntity> states = defaultStateCache.getStateCodeList();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside StateListServiceImpl"
					+ ".getAllState, executed stateCoderepo.findAll() method.";
			LOGGER.debug(msg);
		}
		List<StateListDto> stateList = states.stream()
				.map(obj -> (new StateListDto(obj.getStateCode().toString(),
						obj.getStateName().toString())))
				.collect(Collectors.toList());

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside StateListServiceImpl"
					+ ".getAllState, Created Response.";
			LOGGER.debug(msg + " " + stateList);
		}
		return stateList.stream().sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());

	}

}
