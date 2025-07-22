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

@Component("GetGstr1DetailStatusService")
public class GetGstr1DetailStatusService {

	private static final List<String> HEADER_LIST = Arrays.asList("B2B", "B2BA",
			"CDN", "CDNA", "ISD", "ISDA", "B2CL", "B2CLA", "CDNR", "CDNRA",
			"NIL", "HSN", "DOC_ISSUE", "CDNUR", "CDNURA", "EXP", "EXPA", "B2CS",
			"B2CSA", "AT", "ATA", "TXP", "TXPA");

	@Autowired
	@Qualifier("GetGstr1StatusFetchDaoImpl")
	private GetGstr1StatusFetchDaoImpl getGstr1DetailStatusFetchDao;

	public GetGstr6DetailStatusMainDetailsRespDto findByCriteria(
			GetAnx2DetailStatusReqDto criteria) throws Exception {
		List<GetAnx2DetailStatusRespDto> summaryList = new ArrayList<GetAnx2DetailStatusRespDto>();
		List<Object[]> gstinList = getGstr1DetailStatusFetchDao
				.getGstinsByEntityId(criteria);

		if (!gstinList.isEmpty() && gstinList.size() > 0) {
			for (Object obj : gstinList) {
				GetAnx2DetailStatusRespDto statusRespDto = new GetAnx2DetailStatusRespDto();
				List<GetAnx2DetailsStatusItemsDto> finalItemsList = new ArrayList<GetAnx2DetailsStatusItemsDto>();
				String gstin = obj.toString();
				List<Object[]> itemsList = getGstr1DetailStatusFetchDao
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
			case "ISDA": {
				call.setIsdaTimeStamp(dto.getCallDatetime());
				call.setIsdaStatus(dto.getCallStatus());
				success.setIsdaTimeStamp(dto.getSuccessDatetime());
				success.setIsdaStatus(dto.getSuccessStatus());
				break;
			}
			case "B2CL": {
				call.setB2clTimeStamp(dto.getCallDatetime());
				call.setB2clStatus(dto.getCallStatus());
				success.setB2clTimeStamp(dto.getSuccessDatetime());
				success.setB2clStatus(dto.getSuccessStatus());
				break;
			}
			case "B2CLA": {
				call.setB2claTimeStamp(dto.getCallDatetime());
				call.setB2claStatus(dto.getCallStatus());
				success.setB2claTimeStamp(dto.getSuccessDatetime());
				success.setB2claStatus(dto.getSuccessStatus());
				break;
			}
			case "CDNR": {
				call.setCdnrTimeStamp(dto.getCallDatetime());
				call.setCdnrStatus(dto.getCallStatus());
				success.setCdnrTimeStamp(dto.getSuccessDatetime());
				success.setCdnrStatus(dto.getSuccessStatus());
				break;
			}
			case "CDNRA": {
				call.setCdnraTimeStamp(dto.getCallDatetime());
				call.setCdnraStatus(dto.getCallStatus());
				success.setCdnraTimeStamp(dto.getSuccessDatetime());
				success.setCdnraStatus(dto.getSuccessStatus());
				break;
			}
			case "NIL": {
				call.setNilNonTimeStamp(dto.getCallDatetime());
				call.setNilNonStatus(dto.getCallStatus());
				success.setNilNonTimeStamp(dto.getSuccessDatetime());
				success.setNilNonStatus(dto.getSuccessStatus());
				break;
			}
			case "HSN": {
				call.setHsnTimeStamp(dto.getCallDatetime());
				call.setHsnStatus(dto.getCallStatus());
				success.setHsnTimeStamp(dto.getSuccessDatetime());
				success.setHsnStatus(dto.getSuccessStatus());
				break;
			}
			case "DOC_ISSUE": {
				call.setDocSeriesTimeStamp(dto.getCallDatetime());
				call.setDocSeriesStatus(dto.getCallStatus());
				success.setDocSeriesTimeStamp(dto.getSuccessDatetime());
				success.setDocSeriesStatus(dto.getSuccessStatus());
				break;
			}
			case "CDNUR": {
				call.setCdnurTimeStamp(dto.getCallDatetime());
				call.setCdnurStatus(dto.getCallStatus());
				success.setCdnurTimeStamp(dto.getSuccessDatetime());
				success.setCdnurStatus(dto.getSuccessStatus());
				break;
			}
			case "CDNURA": {
				call.setCdnuraTimeStamp(dto.getCallDatetime());
				call.setCdnuraStatus(dto.getCallStatus());
				success.setCdnuraTimeStamp(dto.getSuccessDatetime());
				success.setCdnuraStatus(dto.getSuccessStatus());
				break;
			}
			case "EXP": {
				call.setExpTimeStamp(dto.getCallDatetime());
				call.setExpStatus(dto.getCallStatus());
				success.setExpTimeStamp(dto.getSuccessDatetime());
				success.setExpStatus(dto.getSuccessStatus());
				break;
			}
			case "EXPA": {
				call.setExpaTimeStamp(dto.getCallDatetime());
				call.setExpaStatus(dto.getCallStatus());
				success.setExpaTimeStamp(dto.getSuccessDatetime());
				success.setExpaStatus(dto.getSuccessStatus());
				break;
			}
			case "B2CS": {
				call.setB2csTimeStamp(dto.getCallDatetime());
				call.setB2csStatus(dto.getCallStatus());
				success.setB2csTimeStamp(dto.getSuccessDatetime());
				success.setB2csStatus(dto.getSuccessStatus());
				break;
			}
			case "B2CSA": {
				call.setB2csaTimeStamp(dto.getCallDatetime());
				call.setB2csaStatus(dto.getCallStatus());
				success.setB2csaTimeStamp(dto.getSuccessDatetime());
				success.setB2csaStatus(dto.getSuccessStatus());
				break;
			}
			case "AT": {
				call.setAdvRTimeStamp(dto.getCallDatetime());
				call.setAdvRStatus(dto.getCallStatus());
				success.setAdvRTimeStamp(dto.getSuccessDatetime());
				success.setAdvRStatus(dto.getSuccessStatus());
				break;
			}
			case "ATA": {
				call.setAdvRATimeStamp(dto.getCallDatetime());
				call.setAdvRAStatus(dto.getCallStatus());
				success.setAdvRATimeStamp(dto.getSuccessDatetime());
				success.setAdvRAStatus(dto.getSuccessStatus());
				break;
			}
			case "TXP": {
				call.setAdvATimeStamp(dto.getCallDatetime());
				call.setAdvAStatus(dto.getCallStatus());
				success.setAdvATimeStamp(dto.getSuccessDatetime());
				success.setAdvAStatus(dto.getSuccessStatus());
				break;
			}
			case "TXPA": {
				call.setAdvAATimeStamp(dto.getCallDatetime());
				call.setAdvAAStatus(dto.getCallStatus());
				success.setAdvAATimeStamp(dto.getSuccessDatetime());
				success.setAdvAAStatus(dto.getSuccessStatus());
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
