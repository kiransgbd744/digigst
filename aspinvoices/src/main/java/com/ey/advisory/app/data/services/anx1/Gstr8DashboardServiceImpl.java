package com.ey.advisory.app.data.services.anx1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr8DashboardServiceImpl")
@Slf4j
public class Gstr8DashboardServiceImpl implements Gstr8DashboardService {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	private static final String TCS = "TCS";

	@Override
	public List<GstinDto> getAllSupplierGstins(List<Long> entityIds) {

		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> list = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(TCS);
		list = gSTNDetailRepository.filterTcsGstinBasedByRegType(list,
				regTypeList);

		return list.stream().map(o -> convertToGstins(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GstinDto convertToGstins(String gstin) {
		GstinDto obj = new GstinDto();
		obj.setGstin((String) gstin);
		return obj;
	}
}
