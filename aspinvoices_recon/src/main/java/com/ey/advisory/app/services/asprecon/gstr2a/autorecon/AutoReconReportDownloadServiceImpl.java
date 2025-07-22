package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.AutoReconReqRecipientGstinEntity;
import com.ey.advisory.app.data.entities.client.asprecon.AutoReconRequestEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoReconReqRecipientGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoReconRequestRepository;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.ReportTypesDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("AutoReconReportDownloadServiceImpl")
public class AutoReconReportDownloadServiceImpl
		implements AutoReconReportDownloadService {

	@Autowired
	@Qualifier("AutoReconRequestRepository")
	private AutoReconRequestRepository autoReconRequestRepo;

	@Autowired
	@Qualifier("AutoReconReqRecipientGstinRepository")
	private AutoReconReqRecipientGstinRepository autoReconReqRecipientGstinRepo;
	
	private static final Map<String, String> map = Stream.of(new String[][] {
        { "Exact Match IMPG", "Import - Match" },
        { "Mismatch IMPG", "Import - Mismatch" },
        { "Addition in PR IMPG", "Import - Addition in PR" },
        { "Addition in 2A IMPG", "Import - Addition in 2A" }
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	
	private static List<String> statusList =
			ImmutableList.of("SUBMITTED", "INITIATED", "REPORT_GENERATION_INPROGRESS","IN_QUEUE");

	
	@Override
	public Pair<Long, Integer> createEntryAutoReconReportRequest(Long noOfRecipientsGstin,
			Long noOfReportTypes, String reportTypes, String fromTaxPeriod,
			String toTaxPeriod, String reconFromDate, String reconToDate,
			Long entityId) {
		String status = "SUBMITTED";
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		AutoReconRequestEntity autoReconReqEntity = new AutoReconRequestEntity();
		if (noOfRecipientsGstin == 0) {
			status = ReportStatusConstants.NO_DATA_FOUND;
		}

		try {
			autoReconReqEntity.setNoOfRecipientGstins(noOfRecipientsGstin);
			autoReconReqEntity.setNoOfReportTypes(noOfReportTypes);
			autoReconReqEntity.setReportTypes(reportTypes);
			if ((reconFromDate != null && reconFromDate != ""
					&& !reconFromDate.isEmpty())
					&& (reconToDate != null && reconToDate != ""
							&& !reconToDate.isEmpty())) {
				autoReconReqEntity
						.setReconFromDate(LocalDate.parse(reconFromDate));
				autoReconReqEntity.setReconToDate(LocalDate.parse(reconToDate));

			}
			if ((fromTaxPeriod != null && fromTaxPeriod != ""
					&& !fromTaxPeriod.isEmpty())
					&& (toTaxPeriod != null && toTaxPeriod != ""
							&& !toTaxPeriod.isEmpty())) {
				Integer derivedFromTaxPeriod = Integer.valueOf(fromTaxPeriod
						.substring(2).concat(fromTaxPeriod.substring(0, 2)));
				Integer derivedToTaxPeriod = Integer.valueOf(toTaxPeriod
						.substring(2).concat(toTaxPeriod.substring(0, 2)));

				autoReconReqEntity.setFromTaxPeriod(derivedFromTaxPeriod);
				autoReconReqEntity.setToTaxPeriod(derivedToTaxPeriod);
			}
			autoReconReqEntity.setStatus(status);
			autoReconReqEntity.setCreatedOn(LocalDateTime.now());
			autoReconReqEntity.setCreatedBy(userName);
			autoReconReqEntity.setUpdatedOn(LocalDateTime.now());
			autoReconReqEntity.setUpdatedBy(userName);
			autoReconReqEntity.setEntityId(entityId);
			int count = autoReconRequestRepo.getCountOfStatus(statusList);
			if(count > 0) {
				autoReconReqEntity.setStatus("IN_QUEUE");
			} else {
				autoReconReqEntity.setStatus(status);
			}
			autoReconRequestRepo.save(autoReconReqEntity);
			return new Pair<Long, Integer>(autoReconReqEntity.getRequestId(), count);
		} catch (Exception e) {
			LOGGER.error(
					"Exception while Persisting Auto Recon Request Details", e);
			throw new AppException(e);
		}
	}

	@Override
	public void createEntryAutoReconReqRecipientGstin(Long requestId,
			String recipientGstin) {
		AutoReconReqRecipientGstinEntity autoReconReqRGstinEntity = new AutoReconReqRecipientGstinEntity();
		try {
			autoReconReqRGstinEntity.setRequestId(requestId);
			autoReconReqRGstinEntity.setRecipientGstin(recipientGstin);
			autoReconReqRecipientGstinRepo.save(autoReconReqRGstinEntity);
		} catch (Exception e) {
			String msg = String.format("Exception while persisting AutoRecon"
					+ " Request Recipient Gstin for requestId :%s and"
					+ " gstin :%s", requestId, recipientGstin);
			LOGGER.error(msg, e);
			throw new AppException(e);
		}

	}

	@Override
	public Pair<List<AutoReconRequestEntity>, Integer> getAutoReconReqData(
			Long entityId, Pageable pageReq) {
		List<AutoReconRequestEntity> autoReconRequests = null;
		int totalCount = 0;
		try {
			autoReconRequests = autoReconRequestRepo
					.findByEntityIdOrderByRequestIdDesc(entityId, pageReq);
			totalCount = autoReconRequestRepo.getCountOfCreatedBy(entityId);
			if (autoReconRequests == null || autoReconRequests.isEmpty()) {
				String errMsg = String.format("No requests submitted");
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the Auto recon Request Data",
					ee);
			throw new AppException(ee);

		}

		return new Pair<>(autoReconRequests, totalCount);

	}

	@Override
	public List<AutoReconReqListingDto> getAutoReconReqResponse(
			List<AutoReconRequestEntity> autoReconReqList) {

		List<AutoReconReqListingDto> autoReconDataList = new ArrayList<>();
		try {
			autoReconReqList.forEach(request -> {
				AutoReconReqListingDto requestDto = new AutoReconReqListingDto();
				requestDto.setNoOfRecipientGstins(
						request.getNoOfRecipientGstins());
				requestDto.setNoOfReportTypes(request.getNoOfReportTypes());
				requestDto.setCreatedOn(EYDateUtil.fmtDate(
						EYDateUtil.toISTDateTimeFromUTC(request.getCreatedOn())));
				requestDto.setRequestId(request.getRequestId());
				requestDto.setStatus(request.getStatus());
				List<String> reportTypes = Arrays
						.asList(request.getReportTypes().split("\\s*,\\s*"));
				List<ReportTypesDto> jsonReportTypes = reportTypes.stream()
						.map(e -> listOfReportTypes(e))
						.collect(Collectors.toCollection(ArrayList::new));

				requestDto.setReportTypes(jsonReportTypes);
				String toTaxPeriod = "";
				if (request.getToTaxPeriod() != 0
						&& request.getReconToDate() == null) {
					toTaxPeriod = String.valueOf(request.getToTaxPeriod());
					toTaxPeriod = toTaxPeriod.substring(4)
							.concat(toTaxPeriod.substring(0, 4));
				} else {
					toTaxPeriod = request.getReconToDate().toString();
				}
				requestDto.setToTaxPeriod(toTaxPeriod);

				String fromTaxPeriod = "";
				if (request.getFromTaxPeriod() != 0
						&& request.getReconFromDate() == null) {
					fromTaxPeriod = String.valueOf(request.getFromTaxPeriod());
					fromTaxPeriod = fromTaxPeriod.substring(4)
							.concat(fromTaxPeriod.substring(0, 4));
				} else {
					fromTaxPeriod = request.getReconFromDate().toString();
				}
				requestDto.setFromTaxPeriod(fromTaxPeriod);

				if (request.getNoOfRecipientGstins() > 0) {
					List<AutoReconReqRecipientGstinEntity> recipientGstins = autoReconReqRecipientGstinRepo
							.findRecipientGstinByRequestId(
									request.getRequestId());
					List<String> gstins = new ArrayList<String>();
					for (AutoReconReqRecipientGstinEntity r : recipientGstins) {
						gstins.add(r.getRecipientGstin());
					}
					List<GstinDto> jsonGstins = gstins.stream()
							.map(e -> listOfGstins(e))
							.collect(Collectors.toCollection(ArrayList::new));

					requestDto.setRecipientGstins(jsonGstins);
				}
				autoReconDataList.add(requestDto);
			});
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the Auto Recon Report Data",
					ee);
			throw new AppException(ee);
		}
		return autoReconDataList;
	}

	private GstinDto listOfGstins(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}

	private ReportTypesDto listOfReportTypes(String e) {
		ReportTypesDto dto = new ReportTypesDto();
		
		String reportType = map.get(e);
		dto.setReportType(reportType != null ? reportType : e);
		return dto;
	}
	
}
