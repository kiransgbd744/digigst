package com.ey.advisory.app.services.strcutvalidation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
@Component
public class GstinDetailsLookUpServiceImpl
		implements GstinDetailsLookUpService {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailsRepository;

	private Map<String, List<String>> gstinMap = new HashMap<>();

	@Override
	public boolean isValidGstinForGroup(String gstin, String groupCode) {
		if (!gstinMap.containsKey(groupCode)) {
			List<String> gstinList = gstinDetailsRepository.findAllGstin();
			gstinMap.put(groupCode, gstinList);
		}
		List<String> gstinList = gstinMap.get(groupCode);
		return gstinList.contains(gstin);
	}

}
