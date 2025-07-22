package com.ey.advisory.app.services.daos.get2a;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusRespDto;
import com.ey.advisory.app.docs.dto.GetGstr6DetailStatusMainDetailsRespDto;
import com.ey.advisory.app.docs.dto.GetGstr6DetailStatusSubItemDetailsRespDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.GetAnx2DetailsStatusItemsDto;

@Component("GetGstr2aStatusService")
public class GetGstr2aStatusService {

	private static final List<String> HEADER_LIST = Arrays.asList("B2B", "B2BA",
			"CDN", "CDNA", "ISD", "ISDA");

	@Autowired
	@Qualifier("GetGstr2aStatusFetchDaoImpl")
	private GetGstr2aStatusFetchDao getGstr2aStatusFetchDao;

	public GetGstr6DetailStatusMainDetailsRespDto findByCriteria(
			GetAnx2DetailStatusReqDto criteria) throws Exception {
		List<GetAnx2DetailStatusRespDto> summaryList = new ArrayList<GetAnx2DetailStatusRespDto>();
		List<Object[]> gstinList = getGstr2aStatusFetchDao
				.getGstinsByEntityId(criteria);

		if (!gstinList.isEmpty() && gstinList.size() > 0) {
			for (Object obj : gstinList) {
				GetAnx2DetailStatusRespDto statusRespDto = new GetAnx2DetailStatusRespDto();
				List<GetAnx2DetailsStatusItemsDto> finalItemsList = new ArrayList<GetAnx2DetailsStatusItemsDto>();
				String gstin = obj.toString();
				List<Object[]> itemsList = getGstr2aStatusFetchDao
						.getDataUploadedStatusDetails(gstin,
								criteria.getTaxPeriod());

				if (!itemsList.isEmpty() && itemsList.size() > 0) {
					Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
					for (Object itmObj[] : itemsList) {
						String key = (String) itmObj[3];
						String newDate = formatToIst(itmObj[4]);
						if (dataMap.containsKey(key)) {
							List<String> list = dataMap.get(key);
							list.add((String) itmObj[5] + "@" + newDate);
							dataMap.put(key, list);
						} else {
							List<String> list = new LinkedList<String>();
							list.add((String) itmObj[5] + "@" + newDate);
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
								latest[1] = " ";
								dto.setSuccessDatetime(latest[1]);
							}
							dto.setSuccessDatetime(latest[1]);
						}

						finalItemsList.add(dto);
					});
				} else {
					GetAnx2DetailsStatusItemsDto dto = new GetAnx2DetailsStatusItemsDto();
					dto.setSection(" - ");
					dto.setCallStatus(" - ");
					dto.setCallDatetime(" - ");
					dto.setSuccessStatus(" - ");
					dto.setSuccessDatetime(" - ");
					finalItemsList.add(dto);
				}

				statusRespDto.setGstin(gstin);
				statusRespDto.setItems(finalItemsList);
				summaryList.add(statusRespDto);
			}
		}

		GetGstr6DetailStatusMainDetailsRespDto detailsRespDto = divideSummaryListByStatusAndGstin(
				summaryList);
		return detailsRespDto;
	}

	private String formatToIst(Object object) {
		Timestamp date = (Timestamp) object;
		LocalDateTime dt = date.toLocalDateTime();
		LocalDateTime dateTimeFormatter = EYDateUtil.toISTDateTimeFromUTC(dt);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;
	}

	private GetGstr6DetailStatusMainDetailsRespDto divideSummaryListByStatusAndGstin(
			List<GetAnx2DetailStatusRespDto> summaryList) {

		GetGstr6DetailStatusMainDetailsRespDto mainDetailsRespDto = new GetGstr6DetailStatusMainDetailsRespDto();
		List<GetGstr6DetailStatusSubItemDetailsRespDto> lastCall = new ArrayList<GetGstr6DetailStatusSubItemDetailsRespDto>();
		List<GetGstr6DetailStatusSubItemDetailsRespDto> lastSuccess = new ArrayList<GetGstr6DetailStatusSubItemDetailsRespDto>();

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
			List<GetGstr6DetailStatusSubItemDetailsRespDto> lastCall,
			List<GetGstr6DetailStatusSubItemDetailsRespDto> lastSuccess,
			String gstin, List<GetAnx2DetailsStatusItemsDto> subItms) {
		GetGstr6DetailStatusSubItemDetailsRespDto call = new GetGstr6DetailStatusSubItemDetailsRespDto();
		GetGstr6DetailStatusSubItemDetailsRespDto success = new GetGstr6DetailStatusSubItemDetailsRespDto();

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
			GetGstr6DetailStatusSubItemDetailsRespDto call,
			GetGstr6DetailStatusSubItemDetailsRespDto success) {
		if (dto != null) {

			switch (dto.getSection()) {
			case "B2B": {
				call.setB2bTimeStamp(dto.getCallDatetime());
				call.setB2bStatus(dto.getCallStatus());
				success.setB2bTimeStamp(dto.getSuccessDatetime());
				success.setB2bStatus(dto.getSuccessStatus());
				break;
			}
			case "B2BA": {
				call.setB2baTimeStamp(dto.getCallDatetime());
				call.setB2baStatus(dto.getCallStatus());
				success.setB2baTimeStamp(dto.getSuccessDatetime());
				success.setB2baStatus(dto.getSuccessStatus());
				break;
			}
			case "CDN": {
				call.setCdnTimeStamp(dto.getCallDatetime());
				call.setCdnStatus(dto.getCallStatus());
				success.setCdnTimeStamp(dto.getSuccessDatetime());
				success.setCdnStatus(dto.getSuccessStatus());
				break;
			}
			case "CDNA": {
				call.setCdnaTimeStamp(dto.getCallDatetime());
				call.setCdnaStatus(dto.getCallStatus());
				success.setCdnaTimeStamp(dto.getSuccessDatetime());
				success.setCdnaStatus(dto.getSuccessStatus());
				break;
			}
			case "ISD": {
				call.setIsdTimeStamp(dto.getCallDatetime());
				call.setIsdStatus(dto.getCallStatus());
				success.setIsdTimeStamp(dto.getSuccessDatetime());
				success.setIsdStatus(dto.getSuccessStatus());
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
