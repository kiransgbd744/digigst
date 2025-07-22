package com.ey.advisory.app.data.services.anx1;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.app.data.daos.client.ITC04PopupFetchDao;
import com.ey.advisory.app.docs.dto.simplified.ITC04PopupRespDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ITC04PopupReqDto;
import com.google.common.collect.Lists;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("ITC04PopupService")
public class ITC04PopupService {

	@Autowired
	@Qualifier("ITC04PopupFetchDao")
	private ITC04PopupFetchDao itc04PopupFetchDaoImpl;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<ITC04PopupRespDto> findByCriteria(ITC04PopupReqDto dto) {
		List<ITC04PopupRespDto> respDtos = Lists.newArrayList();

		ITC04PopupReqDto reqDto = processedRecordsCommonSecParam
				.setItc04DataSecuritySearchParams(dto);

		Map<String, List<String>> attributes = reqDto.getDataSecAttrs();
		if (!CollectionUtils.isEmpty(attributes)) {
			List<String> gstinList = attributes.get(OnboardingConstant.GSTIN);
			List<String> divisionList = attributes
					.get(OnboardingConstant.DIVISION);
			List<String> pcList = attributes.get(OnboardingConstant.PC);
			List<String> pc2List = attributes.get(OnboardingConstant.PC2);
			List<String> plantList = attributes.get(OnboardingConstant.PLANT);

			respDtos.addAll(itc04PopupFetchDaoImpl.findByCriteria(dto,
					gstinList, divisionList, pcList, pc2List, plantList));
		} else {
			List<Long> entityIds = Lists
					.newArrayList(dto.getEntityId());
			if (!CollectionUtils.isEmpty(entityIds)) {

				Map<String, String> map = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds, map);
				List<String> gstinList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);
				respDtos.addAll(itc04PopupFetchDaoImpl.findByCriteria(dto,
						gstinList, null, null, null, null));
			}
		}

		return respDtos;
	}
}
