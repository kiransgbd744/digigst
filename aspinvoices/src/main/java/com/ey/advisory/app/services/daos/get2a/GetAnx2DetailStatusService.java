package com.ey.advisory.app.services.daos.get2a;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusMainDetailsRespDto;
import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusRespDto;
import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusSubItemDetailsRespDto;
import com.ey.advisory.core.dto.GetAnx2DetailsStatusItemsDto;

@Component("GetAnx2DetailStatusService")
public class GetAnx2DetailStatusService {

	private static final List<String> HEADER_LIST = Arrays.asList("B2B", "DE",
			"SEZWP", "SEZWOP", "ISD", "B2BA", "DEA", "SEZWPA", "SEZWOPA",
			"ISDA", "GETSUM");

	@Autowired
	@Qualifier("GetAnx2DetailStatusFetchDaoImpl")
	private GetAnx2DetailStatusFetchDao getAnx2DetailStatusServiceFetchDao;

	public GetAnx2DetailStatusMainDetailsRespDto findByCriteria(
			GetAnx2DetailStatusReqDto criteria) throws Exception {
		List<GetAnx2DetailStatusRespDto> summaryList = new ArrayList<GetAnx2DetailStatusRespDto>();
		List<Object[]> gstinList = getAnx2DetailStatusServiceFetchDao
				.getGstinsByEntityId(criteria.getEntityId());

		if (!gstinList.isEmpty() && gstinList.size() > 0) {
			for (Object obj : gstinList) {
				GetAnx2DetailStatusRespDto statusRespDto = new GetAnx2DetailStatusRespDto();
				List<GetAnx2DetailsStatusItemsDto> finalItemsList = new ArrayList<GetAnx2DetailsStatusItemsDto>();
				String gstin = obj.toString();
				List<Object[]> itemsList = getAnx2DetailStatusServiceFetchDao
						.getDataUploadedStatusDetails(gstin,
								criteria.getTaxPeriod());

				if (!itemsList.isEmpty() && itemsList.size() > 0) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy : hh:mm:ss");
					Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
					for (Object itmObj[] : itemsList) {
						String key = (String) itmObj[3];
						if (dataMap.containsKey(key)) {
							List<String> list = dataMap.get(key);
							list.add((String) itmObj[5] + "@"
									+ dateFormat.format(itmObj[4]));
							dataMap.put(key, list);
						} else {
							List<String> list = new LinkedList<String>();
							list.add((String) itmObj[5] + "@"
									+ dateFormat.format(itmObj[4]));
							dataMap.put(key, list);
						}
					}

					dataMap.forEach((section, items) -> {

						GetAnx2DetailsStatusItemsDto dto = new GetAnx2DetailsStatusItemsDto();

						dto.setSection(section);
						List<String> list = dataMap.get(section);

						if (list.size() == 2) {
							String latest[] = list.get(0).split("@");
							String past[] = list.get(1).split("@");
							dto.setCallStatus(latest[0].substring(0, 1)
									.toUpperCase()
									+ latest[0].substring(1).toLowerCase());
							dto.setCallDatetime(latest[1]);
							dto.setSuccessStatus(past[0].substring(0, 1)
									.toUpperCase()
									+ past[0].substring(1).toLowerCase());
							dto.setSuccessDatetime(past[1]);
						} else if (list.size() == 1) {
							String latest[] = list.get(0).split("@");
							dto.setCallStatus(latest[0].substring(0, 1)
									.toUpperCase()
									+ latest[0].substring(1).toLowerCase());
							dto.setCallDatetime(latest[1]);
							if (latest[0].equalsIgnoreCase("FAILED")) {
								latest[0] = "-";
								dto.setSuccessStatus(latest[0]);
							}
							dto.setSuccessStatus(latest[0].substring(0, 1)
									.toUpperCase()
									+ latest[0].substring(1).toLowerCase());
							if (latest[0].equalsIgnoreCase("-")) {
								latest[1] =" ";
								dto.setSuccessDatetime(latest[1]);
							}
							dto.setSuccessDatetime(latest[1]);
						}

						finalItemsList.add(dto);
					});
				} else {
					GetAnx2DetailsStatusItemsDto dto = new GetAnx2DetailsStatusItemsDto();
					dto.setSection("");
					dto.setCallStatus("");
					dto.setCallDatetime("");
					dto.setSuccessStatus("");
					dto.setSuccessDatetime("");
					finalItemsList.add(dto);
				}

				statusRespDto.setGstin(gstin);
				statusRespDto.setItems(finalItemsList);
				summaryList.add(statusRespDto);
			}
		}

		GetAnx2DetailStatusMainDetailsRespDto detailsRespDto = divideSummaryListByStatusAndGstin(
				summaryList);
		return detailsRespDto;
	}

	private GetAnx2DetailStatusMainDetailsRespDto divideSummaryListByStatusAndGstin(
			List<GetAnx2DetailStatusRespDto> summaryList) {

		GetAnx2DetailStatusMainDetailsRespDto mainDetailsRespDto = new GetAnx2DetailStatusMainDetailsRespDto();
		List<GetAnx2DetailStatusSubItemDetailsRespDto> lastCall = new ArrayList<GetAnx2DetailStatusSubItemDetailsRespDto>();
		List<GetAnx2DetailStatusSubItemDetailsRespDto> lastSuccess = new ArrayList<GetAnx2DetailStatusSubItemDetailsRespDto>();

		if (!summaryList.isEmpty() && summaryList.size() > 0) {
			for (GetAnx2DetailStatusRespDto dto : summaryList) {
				String gstin = dto.getGstin();
				List<GetAnx2DetailsStatusItemsDto> subItms = dto.getItems();
				if (!subItms.isEmpty() && subItms.size() > 0) {
					addAndEnrichTheDataToSubItems(lastCall, lastSuccess, gstin,
							subItms);
				}
			}
		}

		mainDetailsRespDto.getLastCall().addAll(lastCall);
		mainDetailsRespDto.getLastSuccess().addAll(lastSuccess);
		return mainDetailsRespDto;
	}

	private void addAndEnrichTheDataToSubItems(
			List<GetAnx2DetailStatusSubItemDetailsRespDto> lastCall,
			List<GetAnx2DetailStatusSubItemDetailsRespDto> lastSuccess,
			String gstin, List<GetAnx2DetailsStatusItemsDto> subItms) {
		GetAnx2DetailStatusSubItemDetailsRespDto call = new GetAnx2DetailStatusSubItemDetailsRespDto();
		GetAnx2DetailStatusSubItemDetailsRespDto success = new GetAnx2DetailStatusSubItemDetailsRespDto();

		for (String mainItm : HEADER_LIST) {
			GetAnx2DetailsStatusItemsDto dto = null;
			Optional<GetAnx2DetailsStatusItemsDto> itemDto = subItms.stream()
					.filter(itm -> (!itm.getSection().equals("")
							&& itm.getSection().equals(mainItm)))
					.findFirst();
			if (itemDto.isPresent()) {
				dto = itemDto.get();
			} else {
				dto = new GetAnx2DetailsStatusItemsDto();
				dto.setSection(mainItm);
				dto.setCallDatetime("");
				dto.setCallStatus("-");
				dto.setSuccessDatetime("");
				dto.setSuccessStatus("-");
			}

			call.setGstin(gstin);
			success.setGstin(gstin);
			identifyAndEnrichData(dto, call, success);
		}
		lastCall.add(call);
		lastSuccess.add(success);
	}

	private void identifyAndEnrichData(GetAnx2DetailsStatusItemsDto dto,
			GetAnx2DetailStatusSubItemDetailsRespDto call,
			GetAnx2DetailStatusSubItemDetailsRespDto success) {
		if (dto != null) {

			switch (dto.getSection()) {
			case "B2B": {
				call.setB2bLastUpdate(dto.getCallDatetime());
				call.setB2bStatus(dto.getCallStatus());
				success.setB2bLastUpdate(dto.getSuccessDatetime());
				success.setB2bStatus(dto.getSuccessStatus());
				break;
			}
			case "DE": {
				call.setDeLastUpdate(dto.getCallDatetime());
				call.setDeStatus(dto.getCallStatus());
				success.setDeLastUpdate(dto.getSuccessDatetime());
				success.setDeStatus(dto.getSuccessStatus());
				break;
			}
			case "SEZWP": {
				call.setSezwpLastUpdate(dto.getCallDatetime());
				call.setSezwpStatus(dto.getCallStatus());
				success.setSezwpLastUpdate(dto.getSuccessDatetime());
				success.setSezwpStatus(dto.getSuccessStatus());
				break;
			}
			case "SEZWOP": {
				call.setSezwopLastUpdate(dto.getCallDatetime());
				call.setSezwopStatus(dto.getCallStatus());
				success.setSezwopLastUpdate(dto.getSuccessDatetime());
				success.setSezwopStatus(dto.getSuccessStatus());
				break;
			}
			case "ISD": {
				call.setIsdLastUpdate(dto.getCallDatetime());
				call.setIsdStatus(dto.getCallStatus());
				success.setIsdLastUpdate(dto.getSuccessDatetime());
				success.setIsdStatus(dto.getSuccessStatus());
				break;

			}
			case "B2BA": {
				call.setB2baLastUpdate(dto.getCallDatetime());
				call.setB2baStatus(dto.getCallStatus());
				success.setB2baLastUpdate(dto.getSuccessDatetime());
				success.setB2baStatus(dto.getSuccessStatus());
				break;
			}
			case "DEA": {
				call.setDeaLastUpdate(dto.getCallDatetime());
				call.setDeaStatus(dto.getCallStatus());
				success.setDeaLastUpdate(dto.getSuccessDatetime());
				success.setDeaStatus(dto.getSuccessStatus());
				break;
			}
			case "SEZWPA": {
				call.setSezwpaLastUpdate(dto.getCallDatetime());
				call.setSezwpaStatus(dto.getCallStatus());
				success.setSezwpaLastUpdate(dto.getSuccessDatetime());
				success.setSezwpaStatus(dto.getSuccessStatus());
				break;
			}
			case "SEZWOPA": {
				call.setSezwopaLastUpdate(dto.getCallDatetime());
				call.setSezwopaStatus(dto.getCallStatus());
				success.setSezwopaLastUpdate(dto.getSuccessDatetime());
				success.setSezwopaStatus(dto.getSuccessStatus());
				break;
			}
			case "ISDA": {
				call.setIsdaLastUpdate(dto.getCallDatetime());
				call.setIsdaStatus(dto.getCallStatus());
				success.setIsdaLastUpdate(dto.getSuccessDatetime());
				success.setIsdaStatus(dto.getSuccessStatus());
				break;
			}
			case "GETSUM": {
				call.setGetLastUpdate(dto.getCallDatetime());
				call.setGetStatus(dto.getCallStatus());
				success.setGetLastUpdate(dto.getSuccessDatetime());
				success.setGetStatus(dto.getSuccessStatus());
				break;
			}
			default: {
				throw new RuntimeException(
						dto.getSection() + " -> is a Invalid Status Header..");
			}

			}
		} else {
			throw new RuntimeException(
					dto.getSection() + " -> not found from db");
		}
	}

}