package com.ey.advisory.app.data.services.anx1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.GetAnx2RequestIdWiseFetchDao;
import com.ey.advisory.app.docs.dto.anx1.GetAnx2RequestIdWiseReqDto;
import com.ey.advisory.app.docs.dto.anx1.GetAnx2RequestIdWiseRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;

@Service("GetAnx2RequestIdWiseFetchServiceImpl")
public class GetAnx2RequestIdWiseFetchServiceImpl
		implements GetAnx2RequestIdWiseFetchService {

	@Autowired
	@Qualifier("GetAnx2RequestIdWiseFetchDaoImpl")
	private GetAnx2RequestIdWiseFetchDao getAnx2RequestIdWiseFetchDao;

	public List<GetAnx2RequestIdWiseRespDto> getAnx2DetailsByRequestId(
			GetAnx2RequestIdWiseReqDto idWiseReqDto) throws Exception {

		List<GetAnx2RequestIdWiseRespDto> anx2RequestIdWiseRespDtos = getAnx2RequestIdWiseFetchDao
				.getAnx2DetailsByRequestId(idWiseReqDto);
		List<GetAnx2RequestIdWiseRespDto> requestIdWiseRespDtos = buildMapOrderByRequestIdWithGstins(
				anx2RequestIdWiseRespDtos);

		if (requestIdWiseRespDtos.size() == 0) {
			requestIdWiseRespDtos.add(buildDefaultData());
		}
		return requestIdWiseRespDtos;
	}

	private GetAnx2RequestIdWiseRespDto buildDefaultData() {
		GetAnx2RequestIdWiseRespDto respDto = new GetAnx2RequestIdWiseRespDto();
		respDto.setRequestId("");
		respDto.getGstin().add("");
		respDto.setTaxPeriod("");
		respDto.setStatus("");
		respDto.setInitiatedBy("");
		respDto.setInitiationDateTime("");
		respDto.setCompletionDateTime("");

		return respDto;
	}

	private List<GetAnx2RequestIdWiseRespDto> buildMapOrderByRequestIdWithGstins(
			List<GetAnx2RequestIdWiseRespDto> anx2RequestIdWiseRespDtos) {
		List<GetAnx2RequestIdWiseRespDto> requestIdWiseRespDtos = new ArrayList<>();
		Map<String, List<GetAnx2RequestIdWiseRespDto>> map = new HashMap<String, List<GetAnx2RequestIdWiseRespDto>>();
		anx2RequestIdWiseRespDtos.forEach(dto -> {
			String requestId = dto.getRequestId();
			if (map.containsKey(requestId)) {
				List<GetAnx2RequestIdWiseRespDto> list = map.get(requestId);
				list.add(dto);
				map.put(requestId, list);
			} else {
				List<GetAnx2RequestIdWiseRespDto> list = new ArrayList<>();
				list.add(dto);
				map.put(requestId, list);
			}
		});

		map.keySet().forEach(requestId -> {
			List<GetAnx2RequestIdWiseRespDto> list = map.get(requestId);
			GetAnx2RequestIdWiseRespDto respDto = new GetAnx2RequestIdWiseRespDto();
			list.forEach(dto -> {
				respDto.setRequestId(dto.getRequestId());
				List<String> gstn = dto.getGstin();
				if (gstn.isEmpty()) {
					List<String> a = new ArrayList<>();
					respDto.setGstin(a);
				} else {
					respDto.getGstin()
							.add(dto.getGstin().stream().findFirst().get());
				}
				respDto.setTaxPeriod(dto.getTaxPeriod());
				respDto.setStatus(dto.getStatus());
				respDto.setInitiatedBy(dto.getInitiatedBy());
				respDto.setInitiationDateTime(dto.getInitiationDateTime());
				respDto.setCompletionDateTime(dto.getCompletionDateTime());

			});
			requestIdWiseRespDtos.add(respDto);
		});

		return requestIdWiseRespDtos;
	}
}
