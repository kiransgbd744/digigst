package com.ey.advisory.app.data.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;
import com.ey.advisory.app.dashboard.apiCall.ApiGstinDetailsDto;
import com.ey.advisory.app.dashboard.apiCall.TaxPeriodDetailsDto;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service("Auto2bCallStatusServiceImpl")
@Slf4j
public class Auto2bCallStatusServiceImpl implements Auto2bCallStatusService {

	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	private static final String INITIATED = "INITIATED";
	private static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";
	private static final String INPROGRESS = "INPROGRESS";

	@Autowired
	@Qualifier("Gstr2bAutoCommRepository")
	Gstr2bAutoCommRepository gstr2bAutoCommRepo;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	GstnUserRequestRepository gstnUserRequestRepository;

	@Override
	public List<Gstr2bAutoCommEntity> fetchGetStatus(List<String> gstinDtoList,
			Integer derivedStartPeriod, Integer derivedEndPeriod,
			String returnType) {

		return gstr2bAutoCommRepo.getStatus(gstinDtoList, derivedStartPeriod,
				derivedEndPeriod, returnType);
	}

	@Override
	public List<ApiGstinDetailsDto> getTaxPeriodDetails(
			List<Gstr2bAutoCommEntity> getBatchEntityDetails,
			String returnType) {

		Collector<Gstr2bAutoCommEntity, ?, TaxPeriodDetailsDto> collector3 = Collectors
				.reducing(new TaxPeriodDetailsDto(), cpi -> convertDto(cpi),
						(cpt1, cpt2) -> merge(cpt1, cpt2));

		Collector<Gstr2bAutoCommEntity, ?, Map<String, TaxPeriodDetailsDto>> collector2 = Collectors
				.groupingBy(obj -> obj.getTaxPeriod(), collector3);

		Collector<Gstr2bAutoCommEntity, ?, Map<String, Map<String, TaxPeriodDetailsDto>>> collector1 = Collectors
				.groupingBy(o -> o.getGstin(), collector2);

		Map<String, Map<String, TaxPeriodDetailsDto>> map1 = getBatchEntityDetails
				.stream().collect(collector1);

		LOGGER.info("Group by Gstin and Tax period has been completed");

		List<ApiGstinDetailsDto> getTaxperiodDetails = map1.entrySet().stream()
				.map(es -> createGstinDetails(es.getKey(), es.getValue()))
				.collect(Collectors.toList());

		List<String> listGstin = new ArrayList<>();
		getTaxperiodDetails.forEach(e -> {
			listGstin.add(e.getGstin());

		});
		// String returnType1 = getBatchEntityDetails.get(0).getReturnType();

		List<Object[]> getGstinUserReq = gstnUserRequestRepository
				.findActiveGetStatus(listGstin, returnType, "GET");

		Map<String, LocalDateTime> reqInitMap = new HashMap<>();

		getGstinUserReq.forEach(e -> {

			reqInitMap.put(String.valueOf(e[0]) + "|" + String.valueOf(e[1]),
					(LocalDateTime) e[2]);
		});

		getTaxperiodDetails.stream()
				.map(e -> e.getGstin() + "|" + e.getTaxPeriodDetails())
				.collect(Collectors.toList());

		getTaxperiodDetails.forEach(e -> {

			String gstin = e.getGstin();
			List<TaxPeriodDetailsDto> taxPrdDetails = e.getTaxPeriodDetails();
			taxPrdDetails.forEach(taxObj -> {
				String gstinTaxPeriodKey = String.format("%s|%s", gstin,
						taxObj.getTaxPeriod());

				if (reqInitMap.containsKey(gstinTaxPeriodKey)) {
					LocalDateTime initiatedOn = EYDateUtil.toISTDateTimeFromUTC(
							reqInitMap.get(gstinTaxPeriodKey));
					taxObj.setInitiatedOn(initiatedOn);
				}

			});
		});

		return getTaxperiodDetails;
	}

	/*
	 * public List<ApiFyGstinDetailsDto> getFyPeriodDetails(
	 * List<Gstr2bAutoCommEntity> getBatchEntityDetails, Map<String, String>
	 * gstnRegMap) {
	 * 
	 * List<ApiFyGstinDetailsDto> fyDetailsDtoList = new ArrayList<>(); for (int
	 * i = 0; i < getBatchEntityDetails.size(); i++) {
	 * 
	 * String gstin = getBatchEntityDetails.get(i).getGstin();
	 * ApiFyGstinDetailsDto fyDetailsDto = new ApiFyGstinDetailsDto();
	 * fyDetailsDto.setGstin(gstin); fyDetailsDto
	 * .setTaxPeriod(getBatchEntityDetails.get(i).getTaxPeriod()); String status
	 * = getBatchEntityDetails.get(i).getStatus(); Boolean csvGenFlag =
	 * getBatchEntityDetails.get(i) .getCsvGenerationFlag(); if
	 * ("SUCCESS".equalsIgnoreCase(status) && csvGenFlag == true) {
	 * fyDetailsDto.setApiStatus("SUCCESS"); } else if
	 * ("SUCCESS".equalsIgnoreCase(status) && csvGenFlag == false) {
	 * fyDetailsDto.setApiStatus("SUCCESS_REPORT_GEN_INPROGRESS"); } else {
	 * fyDetailsDto.setApiStatus("FAILED"); }
	 * fyDetailsDto.setInitiatedOn(EYDateUtil.toISTDateTimeFromUTC(
	 * getBatchEntityDetails.get(i).getUpdatedOn()));// Ist
	 * fyDetailsDto.setGetCallDate(EYDateUtil .toISTDateTimeFromUTC(
	 * getBatchEntityDetails.get(i).getUpdatedOn()) .toLocalDate());
	 * fyDetailsDto.setGetCallTime(
	 * getConverterTime(EYDateUtil.toISTDateTimeFromUTC(
	 * getBatchEntityDetails.get(i).getUpdatedOn())));
	 * fyDetailsDto.setRegistrationType(gstnRegMap.get(gstin));
	 * fyDetailsDtoList.add(fyDetailsDto); } return fyDetailsDtoList; }
	 */

	private TaxPeriodDetailsDto convertDto(Gstr2bAutoCommEntity cpi) {
		TaxPeriodDetailsDto txpd = new TaxPeriodDetailsDto();
		txpd.setTaxPeriod(cpi.getTaxPeriod());
		txpd.setInitiatedOn(cpi.getUpdatedOn() == null ? cpi.getCreatedOn()
				: cpi.getUpdatedOn());

		if (SUCCESS.equals(cpi.getStatus())) {
			txpd.setSuccessSections(cpi.getSection());
		}
		if (SUCCESS_WITH_NO_DATA.equals(cpi.getStatus())) {
			txpd.setSuccessWithNoDataSections(cpi.getSection());
		}
		if (FAILED.equals(cpi.getStatus())) {
			txpd.setFailedSections(cpi.getSection());
		}
		if (INITIATED.equals(cpi.getStatus())
				|| INPROGRESS.equalsIgnoreCase(cpi.getStatus())) {
			txpd.setInProgressSections(Strings.isNullOrEmpty(cpi.getSection())
					? "NA" : cpi.getSection().toUpperCase());
		}
		return txpd;
	}

	private TaxPeriodDetailsDto merge(TaxPeriodDetailsDto txpd1,
			TaxPeriodDetailsDto txpd2) {
		String succSection = "";
		String failedSection = "";
		String inprogressSection = "";
		String successWithNoDataSection = "";
		LocalDateTime initiatedOn = null;
		if (txpd1.getSuccessSections() != null
				&& !txpd1.getSuccessSections().isEmpty()) {
			succSection = txpd1.getSuccessSections();
			if (txpd2.getSuccessSections() != null
					&& !txpd2.getSuccessSections().isEmpty()) {
				succSection = succSection + " " + txpd2.getSuccessSections();
			}
		} else {
			succSection = txpd2.getSuccessSections();
		}
		if (txpd1.getFailedSections() != null
				&& !txpd1.getFailedSections().isEmpty()) {
			failedSection = txpd1.getFailedSections();
			if (txpd2.getFailedSections() != null
					&& !txpd2.getFailedSections().isEmpty()) {
				failedSection = failedSection + " " + txpd2.getFailedSections();
			}
		} else {
			failedSection = txpd2.getFailedSections();
		}

		if (txpd1.getInProgressSections() != null
				&& !txpd1.getInProgressSections().isEmpty()) {
			inprogressSection = txpd1.getInProgressSections();
			if (txpd2.getInProgressSections() != null
					&& !txpd2.getInProgressSections().isEmpty()) {
				inprogressSection = inprogressSection + " "
						+ txpd2.getInProgressSections();
			}
		} else {
			inprogressSection = txpd2.getInProgressSections();
		}

		if (txpd1.getSuccessWithNoDataSections() != null
				&& !txpd1.getSuccessWithNoDataSections().isEmpty()) {
			successWithNoDataSection = txpd1.getSuccessWithNoDataSections();
			if (txpd2.getSuccessWithNoDataSections() != null
					&& !txpd2.getSuccessWithNoDataSections().isEmpty()) {
				successWithNoDataSection = successWithNoDataSection + " "
						+ txpd2.getSuccessWithNoDataSections();
			}
		} else {
			successWithNoDataSection = txpd2.getSuccessWithNoDataSections();
		}

		if (txpd2.getInitiatedOn() != null) {
			initiatedOn = EYDateUtil
					.toISTDateTimeFromUTC(txpd2.getInitiatedOn());
		} else if (txpd1.getInitiatedOn() != null) {
			initiatedOn = EYDateUtil
					.toISTDateTimeFromUTC(txpd1.getInitiatedOn());
		}

		return new TaxPeriodDetailsDto(txpd2.getTaxPeriod(), initiatedOn,
				succSection, failedSection, inprogressSection,
				successWithNoDataSection);
	}

	private ApiGstinDetailsDto createGstinDetails(String sGstin,
			Map<String, TaxPeriodDetailsDto> map) {

		ApiGstinDetailsDto apiGstindetails = new ApiGstinDetailsDto();
		apiGstindetails.setGstin(sGstin);

		List<TaxPeriodDetailsDto> taxPeriodDetailsDto = map.entrySet().stream()
				.map(es -> es.getValue()).collect(Collectors.toList());
		taxPeriodDetailsDto.forEach(x -> {
			boolean isSuccess = !Strings.isNullOrEmpty(x.getSuccessSections());
			boolean isFailed = !Strings.isNullOrEmpty(x.getFailedSections());
			boolean isInProgress = !Strings
					.isNullOrEmpty(x.getInProgressSections());
			boolean isSuccessWithNoData = !Strings
					.isNullOrEmpty(x.getSuccessWithNoDataSections());

			if (isInProgress) {
				x.setApiStatus("INPROGRESS");
			} else if (isSuccess && isFailed && !isInProgress) {
				x.setApiStatus("PARTIAL_SUCCESS");
			} else if (isFailed || (isFailed && isSuccessWithNoData)) {
				x.setApiStatus("FAILED");
			} else if (isSuccess || (isSuccess && isSuccessWithNoData)) {
				x.setApiStatus("SUCCESS");
			} else {
				x.setApiStatus("SUCCESS_WITH_NO_DATA");
			}
		});

		apiGstindetails.setTaxPeriodDetails(taxPeriodDetailsDto);
		return apiGstindetails;
	}

}
