/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceClientEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceRequestEntity;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnComplainceClientRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnComplianceRequestRepository;
import com.ey.advisory.app.services.vendorcomm.ReturnComplianceEmailCommDto;
import com.ey.advisory.app.services.vendorcomm.ReturnComplianceRequestDto;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Component("ReturnComplianceSummaryServiceImpl")
public class ReturnComplianceSummaryServiceImpl
		implements ReturnComplianceSummaryService {

	@Autowired
	private ReturnComplianceRequestRepository returnComplianceRequestRepository;

	@Autowired
	private ReturnComplainceClientRepository returnComplainceClientRepository;

	@Autowired
	private RecipientMasterUploadRepository recipientMasterUploadRepository;

	@Autowired
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	public List<ReturnComplianceRequestEntity> getComplianceDataByUserName(
			String userName) {
		List<ReturnComplianceRequestEntity> complianceReportList = null;
		try {
			complianceReportList = returnComplianceRequestRepository
					.findByCreatedBy(userName);
			if (complianceReportList == null
					|| complianceReportList.isEmpty()) {
				String errMsg = String.format("No requests submitted");
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception ee) {
			LOGGER.error(
					"Exception while fetching the NonComplaint Vendor Report Data",
					ee);
			throw new AppException(ee);
		}
		return complianceReportList;
	}

	@Override
	public List<ReturnComplianceRequestDto> getComplianceCommResponse(
			List<ReturnComplianceRequestEntity> complianceComReqList) {

		List<ReturnComplianceRequestDto> nonCompVendorComData = new ArrayList<>();
		try {
			complianceComReqList.forEach(client -> {
				ReturnComplianceRequestDto clientDto = new ReturnComplianceRequestDto();
				String formattedDate = getStandardTime(client.getCreatedOn());
				clientDto.setCreatedOn(formattedDate);
				clientDto.setNoOfGstins(client.getNoOfGstins());
				clientDto.setRequestId(client.getRequestId());
				clientDto.setStatus(client.getStatus());
				clientDto.setFinancialYear(client.getFinancialYear());
				List<ReturnComplianceClientEntity> complianceClientGstinList = null;
				if (client.getNoOfGstins() > 0) {
					complianceClientGstinList = getComplianceCgstinData(
							client.getRequestId());

					long count = complianceClientGstinList.stream().filter(
							v -> v.getEmailStatus().equalsIgnoreCase("SENT"))
							.count();

					clientDto.setTotalEmails(complianceClientGstinList.size());
					clientDto.setSentEmails(count);
				}
				if (client.getNoOfGstins() > 0) {

					List<String> gstins = new ArrayList<String>();
					for (ReturnComplianceClientEntity r : complianceClientGstinList) {
						gstins.add(r.getClientGstin());
					}
					List<GstinDto> jsonGstins = gstins.stream()
							.map(e -> listOfGstins(e))
							.collect(Collectors.toCollection(ArrayList::new));

					clientDto.setClientGstins(jsonGstins);

				}
				nonCompVendorComData.add(clientDto);
			});
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the Vendor Report Data", ee);
			throw new AppException(ee);
		}
		return nonCompVendorComData;
	}

	@Override
	public List<ReturnComplianceClientEntity> getComplianceCgstinData(
			Long requestId) {
		List<ReturnComplianceClientEntity> returnComplianceReqCgstinList = null;
		try {
			returnComplianceReqCgstinList = returnComplainceClientRepository
					.findByRequestId(requestId);
		} catch (Exception ee) {
			LOGGER.error(
					"Exception while fetching the return compliance client Report Data",
					ee);
			throw new AppException(ee);
		}
		return returnComplianceReqCgstinList;
	}

	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

	private GstinDto listOfGstins(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}

	@Override
	public Pair<List<ReturnComplianceEmailCommDto>, Integer> getComReturnEmailCommunicationDetails(
			Long requestId, Long entityId, int pageSize, int pageNum) {
		List<ReturnComplianceEmailCommDto> clientEmailCommDtoList = new ArrayList<>();
		List<RecipientMasterUploadEntity> recipientGstinInfoList = null;
		Integer totalCount = null;
		Map<String, String> emailStatusMap = new HashMap<>();
		Map<String, String> emailUpdateOnMap = new HashMap<>();
		Map<String, String> returnTypeMap = new HashMap<>();

		try {

			int recordsToStart = pageNum;
			int noOfRowstoFetch = pageSize;
			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			List<ReturnComplianceClientEntity> recGstinList = returnComplainceClientRepository
					.findByRequestId(requestId);

			recGstinList.forEach(obj -> {
				emailStatusMap.put(obj.getClientGstin(), obj.getEmailStatus());
				String formattedDate = getStandardTime(obj.getUpdatedOn());
				returnTypeMap.put(obj.getClientGstin(), obj.getReturnType());
				emailUpdateOnMap.put(obj.getClientGstin(), formattedDate);
			});

			List<String> reciepientGstInList = recGstinList.stream()
					.map(ReturnComplianceClientEntity::getClientGstin)
					.collect(Collectors.toList());

			EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
					.findEntityByEntityId(entityId);

			recipientGstinInfoList = recipientMasterUploadRepository
					.findByRecipientPANAndRecipientGstinInAndIsDeleteFalseAndIsRetCompStatusEmailTrueOrderByRecipientGstinAsc(
							entityInfoEntity.getPan(), reciepientGstInList,
							pageReq);
			totalCount = recipientMasterUploadRepository
					.findCountByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsRetCompStatusEmailTrue(
							entityInfoEntity.getPan(), reciepientGstInList);

			recipientGstinInfoList.forEach(eachObje -> {
				ReturnComplianceEmailCommDto clientEmailCommDto = new ReturnComplianceEmailCommDto();

				clientEmailCommDto.setClientGstin(eachObje.getRecipientGstin());
				clientEmailCommDto.setReturnType(
						returnTypeMap.get(eachObje.getRecipientGstin()));
				clientEmailCommDto.setEmailStatus(
						emailStatusMap.get(eachObje.getRecipientGstin()));
				clientEmailCommDto
						.addEmailTo(eachObje.getRecipientPrimEmailId());
				clientEmailCommDto.addEmailCC(eachObje.getRecipientEmailId2());
				clientEmailCommDto.addEmailCC(eachObje.getRecipientEmailId3());
				clientEmailCommDto.addEmailCC(eachObje.getRecipientEmailId4());
				clientEmailCommDto.addEmailCC(eachObje.getRecipientEmailId5());
				clientEmailCommDto.setRequestID(requestId);
				clientEmailCommDto.setUpdatedOn(
						emailUpdateOnMap.get(eachObje.getRecipientGstin()));

				clientEmailCommDtoList.add(clientEmailCommDto);

			});

		} catch (Exception e) {
			LOGGER.error("error", e);
			throw new AppException(e);
		}
		return new Pair<>(clientEmailCommDtoList, totalCount);
	}
}
