package com.ey.advisory.app.services.vendorcomm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.VendorCommRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorReqRecipientGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqRecipientGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.VendorCommListingRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorCommReportDataService")
public class VendorCommReportDataService {

	@Autowired
	@Qualifier("VendorCommReqVgstinServiceImpl")
	private VendorCommReqVgstinService vendorCommReqVgstinService;

	@Autowired
	private VendorReqRecipientGstinRepository vendorReqRgstinRepo;

	@Autowired
	private VendorReqVendorGstinRepository vendorGstinRepo;

	@Autowired
	@Qualifier("VendorReqVendorGstinRepository")
	private VendorReqVendorGstinRepository vendorReqVendorGstinRepository;

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	public List<VendorCommListingRespDto> getVendorCommResponse(
			List<VendorCommRequestEntity> vendorComReqList) {

		List<VendorCommListingRespDto> vendorCommDataList = new ArrayList<>();

		try {
			List<Object[]> reqIdVendorResp = vendorGstinRepo.getVendorRespCnt();
			Map<Long, Long> reqIdVendorRespMap = new HashMap<>();

			reqIdVendorResp.stream()
					.forEach(obj -> reqIdVendorRespMap.put(
							Long.valueOf(obj[0].toString()),
							Long.valueOf(obj[1].toString())));

			// vendor email count
			List<Object[]> reqIdVendorEmailCnt = vendorGstinRepo
					.getVendorEmailSentCnt();
			Map<Long, Long> reqIdVendorEmailCntMap = new HashMap<>();

			reqIdVendorEmailCnt.stream()
					.forEach(obj -> reqIdVendorEmailCntMap.put(
							Long.valueOf(obj[0].toString()),
							Long.valueOf(obj[1].toString())));

			// request id receipeint gstins

			List<VendorReqRecipientGstinEntity> reqIdRecipntEntity = vendorReqRgstinRepo
					.findAll();

			Map<Long, List<VendorReqRecipientGstinEntity>> reqIdRecipntEntityMap = reqIdRecipntEntity
					.stream().collect(Collectors.groupingBy(
							VendorReqRecipientGstinEntity::getRequestId));

			for (VendorCommRequestEntity vendor : vendorComReqList) {
				// vendorComReqList.forEach(vendor -> {
				VendorCommListingRespDto vendorDto = new VendorCommListingRespDto();
				vendorDto.setReconType(vendor.getReconType());
				vendorDto.setNoOfRecipientGstins(
						vendor.getNoOfRecipientGstins());
				vendorDto.setNoOfReportTypes(vendor.getNoOfReportTypes());
				String formattedDate = getStandardTime(vendor.getCreatedOn());
				vendorDto.setCreatedOn(formattedDate);
				vendorDto.setNoOfVendorGstins(vendor.getNoOfVendorGstins());
				vendorDto.setRequestId(vendor.getRequestId());
				vendorDto.setStatus(vendor.getStatus());
				List<String> reportTypes = Arrays
						.asList(vendor.getReportTypes().split("\\s*,\\s*"));
				List<GstinDto> jsonReportTypes = reportTypes.stream()
						.map(e -> listOfGstins(e))
						.collect(Collectors.toCollection(ArrayList::new));

				vendorDto.setReportTypes(jsonReportTypes);
				String taxPeriod = getMonthNameFromNumber(
						vendor.getFromTaxPeriod()) + " - "
						+ getMonthNameFromNumber(vendor.getToTaxPeriod());
				vendorDto.setTaxPeriod(taxPeriod);
				vendorDto.setTotalEmails(vendor.getNoOfVendorGstins());

				if (vendor.getNoOfVendorGstins() > 0) {
					if (reqIdVendorEmailCntMap
							.containsKey(vendor.getRequestId())) {
						vendorDto.setSentEmails(reqIdVendorEmailCntMap
								.get(vendor.getRequestId()));
					} else
						vendorDto.setSentEmails(0L);
				}
				if (vendor.getNoOfRecipientGstins() > 0) {
					if (reqIdRecipntEntityMap
							.containsKey(vendor.getRequestId())) {
						List<String> gstins = new ArrayList<String>();
						for (VendorReqRecipientGstinEntity r : reqIdRecipntEntityMap
								.get(vendor.getRequestId())) {
							gstins.add(r.getRecipientGstin());
						}
						List<GstinDto> jsonGstins = gstins.stream()
								.map(e -> listOfGstins(e)).collect(Collectors
										.toCollection(ArrayList::new));

						vendorDto.setRecipientGstins(jsonGstins);
					}
				}
				if (reqIdVendorRespMap.containsKey(vendor.getRequestId()))
					vendorDto.setVendrResponded(
							reqIdVendorRespMap.get(vendor.getRequestId()));
				else
					vendorDto.setVendrResponded(0L);
				vendorCommDataList.add(vendorDto);
				// });
			}

		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the Vendor Report Data", ee);
			throw new AppException(ee);

		}

		return vendorCommDataList;

	}

	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

	private String getMonthNameFromNumber(String taxPeriod) {
		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		String monthString;
		switch (month) {
		case 1:
			monthString = "Jan";
			break;
		case 2:
			monthString = "Feb";
			break;
		case 3:
			monthString = "Mar";
			break;
		case 4:
			monthString = "Apr";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "Jun";
			break;
		case 7:
			monthString = "Jul";
			break;
		case 8:
			monthString = "Aug";
			break;
		case 9:
			monthString = "Sep";
			break;
		case 10:
			monthString = "Oct";
			break;
		case 11:
			monthString = "Nov";
			break;
		case 12:
			monthString = "Dec";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString + " " + taxPeriod.substring(4, 6);
	}

	private GstinDto listOfGstins(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}
}
