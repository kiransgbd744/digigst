package com.ey.advisory.app.services.vendorcomm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.client.HttpClient;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorVGstinEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.NonCompVendorRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.NonCompVendorVGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorGstinDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.app.vendorcomm.dto.AzureEMailDto;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.NonCompVendorRequestDto;
import com.ey.advisory.app.vendorcomm.dto.VendorAzureEmailCommDto;
import com.ey.advisory.app.vendorcomm.dto.VendorEmailCommDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("NonComplaintVendorComServiceImpl")
public class NonComplaintVendorComServiceImpl
		implements NonComplaintVendorComService {

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private NonCompVendorRequestRepository nonCompVendorRequestRepository;

	@Autowired
	private NonCompVendorVGstinRepository nonCompVendorVGstinRepository;

	@Autowired
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private AsyncJobsService persistenceMngr;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("VendorEmailPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private Environment env;

	private static final String VENDOR_MASTER_DATA_NOT_FOUND = "Vendor Master data not found";

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	public List<GstinDto> getListOfVendorPans(Long entityId) {
		List<String> vendorPanList = new ArrayList<>();
		List<String> recipientPanList = entityInfoRepository
				.findPanByEntityId(entityId);

		vendorPanList = vendorMasterUploadEntityRepository
				.getDistinctActiveNonComplaintComVendorPans(recipientPanList);
		Collections.sort(vendorPanList);

		return vendorPanList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GstinDto listRecipientPan(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}

	@Override
	public List<GstinDto> getListOfNonComplaintComVendorGstin(
			List<String> vendorPans, Long entityId) {

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		List<String> vendorGstinList = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorPans, 2000);

		for (List<String> chunk : chunks) {
			vendorGstinList.addAll(vendorMasterUploadEntityRepository
					.findAllNonCompComVendorGstinByVendorPans(chunk,
							entityInfoEntity.getPan()));
		}
		Collections.sort(vendorGstinList);
		return vendorGstinList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<VendorGstinDto> getVendorNameForGstin(
			List<String> vendorGstInList, Long entityId) {
		List<VendorGstinDto> vendorGstinDtoList = new ArrayList<>();
		List<VendorMasterUploadEntity> vendorMasterUploadEntities = new ArrayList<>();

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		if (vendorGstInList.isEmpty())
			throw new AppException("vendor gstin list cannot be empty");
		if (vendorGstInList.contains("All")) {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findAll();
		} else {
			List<List<String>> chunks = Lists.partition(vendorGstInList, 2000);
			for (List<String> chunk : chunks) {
				vendorMasterUploadEntities
						.addAll(vendorMasterUploadEntityRepository
								.findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrue(
										entityInfoEntity.getPan(), chunk));
			}
		}

		if (CollectionUtils.isEmpty(vendorMasterUploadEntities))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);

		vendorMasterUploadEntities.forEach(eachObje -> {
			VendorGstinDto vendorGstinDto = new VendorGstinDto();
			vendorGstinDto.setVendorName(eachObje.getVendorName() != ""
					? eachObje.getVendorName() : null);
			vendorGstinDtoList.add(vendorGstinDto);
		});
		if (CollectionUtils.isEmpty(vendorGstinDtoList))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		return vendorGstinDtoList;
	}

	@Override
	public List<String> getListOfVendorCode(List<String> vendorNameList,
			List<String> vendorGstInList, Long entityId) {

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		List<VendorMasterUploadEntity> vendorMasterUploadEntities;
		if (CollectionUtils.isEmpty(vendorGstInList))
			throw new AppException("vendor gstin list cannot be empty");
		if (CollectionUtils.isEmpty(vendorNameList))
			throw new AppException("vendor name list cannot be empty");
		if (vendorNameList.contains("All")) {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findAll();
		} else {
			vendorMasterUploadEntities = vendorMasterUploadEntityRepository
					.findByRecipientPANAndVendorNameInAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrue(
							entityInfoEntity.getPan(), vendorNameList,
							vendorGstInList);
		}
		List<String> vendorCodeList = vendorMasterUploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorCode).sorted()
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(vendorCodeList))
			throw new AppException(VENDOR_MASTER_DATA_NOT_FOUND);
		return vendorCodeList;
	}

	@Override
	public Long createEntryNonComplaintVendorComReq(Long noOfVendorGstins,
			String financialYear, Long entityId) {
		String status = "SUBMITTED";
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		NonCompVendorRequestEntity nonCompVendorRequestEntity = new NonCompVendorRequestEntity();
		if (noOfVendorGstins == 0) {
			status = ReportStatusConstants.NO_DATA_FOUND;
		}

		try {
			nonCompVendorRequestEntity.setNoOfVendorGstins(noOfVendorGstins);
			nonCompVendorRequestEntity.setFinancialYear(financialYear);
			nonCompVendorRequestEntity.setStatus(status);
			nonCompVendorRequestEntity.setCreatedOn(LocalDateTime.now());
			nonCompVendorRequestEntity.setCreatedBy(userName);
			nonCompVendorRequestEntity.setUpdatedOn(LocalDateTime.now());
			nonCompVendorRequestEntity.setUpdatedBy(userName);
			nonCompVendorRequestEntity.setEntityId(entityId);
			nonCompVendorRequestRepository.save(nonCompVendorRequestEntity);
			return nonCompVendorRequestEntity.getRequestId();
		} catch (Exception e) {
			LOGGER.error(
					"Exception while Persisting NonComplaint Vendor Comm Request ",
					e);
			throw new AppException(e);
		}
	}

	@Override
	public void createEntryNonCompVendorReqVGstin(Long requestId,
			String vendorGstin, Set<String> returnType) {
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		NonCompVendorVGstinEntity nonCompVendorVGstinEntity = new NonCompVendorVGstinEntity();
		try {
			String returnTypeString = String.join(",", returnType);
			nonCompVendorVGstinEntity.setRequestId(requestId);
			nonCompVendorVGstinEntity.setVendorGstin(vendorGstin);
			nonCompVendorVGstinEntity.setCreatedOn(LocalDateTime.now());
			nonCompVendorVGstinEntity.setCreatedBy(userName);
			nonCompVendorVGstinEntity.setUpdatedOn(LocalDateTime.now());
			nonCompVendorVGstinEntity.setUpdatedBy(userName);
			nonCompVendorVGstinEntity.setReturnType(returnTypeString);
			nonCompVendorVGstinEntity.setEmailStatus("DRAFTED");
			nonCompVendorVGstinEntity.setReportStatus("SUBMITTED");
			nonCompVendorVGstinRepository.save(nonCompVendorVGstinEntity);
		} catch (Exception e) {
			LOGGER.error("Exception while Persisting NonCompVendorReqVGstin ",
					e);
			new AppException(e);

		}

	}

	@Override
	public String generateNonCompVendorReportUploadAsync(Long requestId) {

		JsonObject jsonParams = new JsonObject();
		List<AsyncExecJob> NonCompVendorReportAsync = new ArrayList<>();

		jsonParams.addProperty("id", requestId);
		NonCompVendorReportAsync
				.add(asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.NON_COMP_VENDOR_REPORT,
						jsonParams.toString(), "SYSTEM", 1L, null, null));

		if (!NonCompVendorReportAsync.isEmpty()) {
			persistenceMngr.createJobs(NonCompVendorReportAsync);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Non Complaint Vendor comm report upload Job has been submitted for requestId ",
					requestId);
		}
		return "Request has been submitted";
	}

	@Override
	public List<NonCompVendorRequestEntity> getNonCompVendorCommDataByUserName(
			String userName) {
		List<NonCompVendorRequestEntity> nonCompVendorReportList = null;
		try {
			nonCompVendorReportList = nonCompVendorRequestRepository
					.findByCreatedBy(userName);
			if (nonCompVendorReportList == null
					|| nonCompVendorReportList.isEmpty()) {
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

		return nonCompVendorReportList;
	}

	@Override
	public List<NonCompVendorRequestDto> getNonCompVendorCommResponse(
			List<NonCompVendorRequestEntity> nonCompVendorComReqList) {

		List<NonCompVendorRequestDto> nonCompVendorComData = new ArrayList<>();
		try {
			nonCompVendorComReqList.forEach(vendor -> {
				NonCompVendorRequestDto vendorDto = new NonCompVendorRequestDto();
				String formattedDate = getStandardTime(vendor.getCreatedOn());
				vendorDto.setCreatedOn(formattedDate);
				vendorDto.setNoOfVendorGstins(vendor.getNoOfVendorGstins());
				vendorDto.setRequestId(vendor.getRequestId());
				vendorDto.setStatus(vendor.getStatus());
				vendorDto.setFinancialYear(vendor.getFinancialYear());
				List<NonCompVendorVGstinEntity> nonCompVendorReqVgstinList = null;
				if (vendor.getNoOfVendorGstins() > 0) {
					nonCompVendorReqVgstinList = getNonCompVendorReqVgstinData(
							vendor.getRequestId());

					long count = nonCompVendorReqVgstinList.stream().filter(
							v -> v.getEmailStatus().equalsIgnoreCase("SENT"))
							.count();

					vendorDto.setTotalEmails(nonCompVendorReqVgstinList.size());
					vendorDto.setSentEmails(count);
				}
				if (vendor.getNoOfVendorGstins() > 0) {

					List<String> gstins = new ArrayList<String>();
					for (NonCompVendorVGstinEntity r : nonCompVendorReqVgstinList) {
						gstins.add(r.getVendorGstin());
					}
					List<GstinDto> jsonGstins = gstins.stream()
							.map(e -> listOfGstins(e))
							.collect(Collectors.toCollection(ArrayList::new));

					vendorDto.setVendorGstins(jsonGstins);

				}
				nonCompVendorComData.add(vendorDto);
			});
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the Vendor Report Data", ee);
			throw new AppException(ee);

		}

		return nonCompVendorComData;

	}

	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

	@Override
	public List<NonCompVendorVGstinEntity> getNonCompVendorReqVgstinData(
			Long requestId) {
		List<NonCompVendorVGstinEntity> nonCompVendorReqVgstinList = null;
		try {
			nonCompVendorReqVgstinList = nonCompVendorVGstinRepository
					.findByRequestId(requestId);
		} catch (Exception ee) {
			LOGGER.error(
					"Exception while fetching the NonComplaint Vendor Report Data",
					ee);
			throw new AppException(ee);

		}

		return nonCompVendorReqVgstinList;
	}

	private GstinDto listOfGstins(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}

	@Override
	public Pair<List<VendorEmailCommDto>, Integer> getNonCompEmailCommunicationDetails(
			Long requestId, Long entityId, int pageSize, int pageNum) {
		List<VendorEmailCommDto> vendorEmailCommDtoList = new ArrayList<>();
		List<VendorMasterUploadEntity> vendorMasterList = null;
		Integer totalCount = 0;
		Map<String, String> emailStatusMap = new HashMap<>();
		Map<String, String> emailUpdateOnMap = new HashMap<>();
		Map<String, String> returnTypeMap = new HashMap<>();

		try {

			List<NonCompVendorVGstinEntity> vendorGstinList = nonCompVendorVGstinRepository
					.findByRequestId(requestId);

			vendorGstinList.forEach(obj -> {
				emailStatusMap.put(obj.getVendorGstin(), obj.getEmailStatus());
				String formattedDate = getStandardTime(obj.getUpdatedOn());
				returnTypeMap.put(obj.getVendorGstin(), obj.getReturnType());
				emailUpdateOnMap.put(obj.getVendorGstin(), formattedDate);
			});

			List<String> vGstinList = vendorGstinList.stream()
					.map(NonCompVendorVGstinEntity::getVendorGstin)
					.collect(Collectors.toList());

			// do sort and chunk
			Collections.sort(vGstinList);
			EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
					.findEntityByEntityId(entityId);

			List<List<String>> chunks = Lists.partition(vGstinList, pageSize);

			vendorMasterList = vendorMasterUploadEntityRepository
					.findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrueOrderByVendorGstinAsc(
							entityInfoEntity.getPan(), chunks.get(pageNum));

			List<List<String>> countChunks = Lists.partition(vGstinList, 2000);
			for (List<String> countChunk : countChunks) {
				totalCount += vendorMasterUploadEntityRepository
						.findCountByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrue(
								entityInfoEntity.getPan(), countChunk);
			}

			vendorMasterList.forEach(eachObje -> {
				VendorEmailCommDto vendorEmailCommDto = new VendorEmailCommDto();

				vendorEmailCommDto.setVendorGstin(eachObje.getVendorGstin());
				vendorEmailCommDto.setVendorName(eachObje.getVendorName());
				vendorEmailCommDto
						.setVendPrimEmailId(eachObje.getVendPrimEmailId());

				vendorEmailCommDto
						.addSecondaryEmail(eachObje.getVendorEmailId1());
				vendorEmailCommDto
						.addSecondaryEmail(eachObje.getVendorEmailId2());
				vendorEmailCommDto
						.addSecondaryEmail(eachObje.getVendorEmailId3());
				vendorEmailCommDto
						.addSecondaryEmail(eachObje.getVendorEmailId4());

				vendorEmailCommDto
						.addRecipientEmail(eachObje.getRecipientEmailId1());
				vendorEmailCommDto
						.addRecipientEmail(eachObje.getRecipientEmailId2());
				vendorEmailCommDto
						.addRecipientEmail(eachObje.getRecipientEmailId3());
				vendorEmailCommDto
						.addRecipientEmail(eachObje.getRecipientEmailId4());

				vendorEmailCommDto
						.addRecipientEmail(eachObje.getRecipientEmailId5());

				vendorEmailCommDto.setEmailStatus(
						emailStatusMap.get(eachObje.getVendorGstin()));

				vendorEmailCommDto.setUpdatedOn(
						emailUpdateOnMap.get(eachObje.getVendorGstin()));

				vendorEmailCommDto.setVendorContactNumber(
						eachObje.getVendorContactNumber());
				vendorEmailCommDto.setReturnType(
						returnTypeMap.get(eachObje.getVendorGstin()));
				vendorEmailCommDto.setRequestID(requestId);

				vendorEmailCommDtoList.add(vendorEmailCommDto);

			});

		} catch (Exception e) {
			LOGGER.error("error", e);
			throw new AppException(e);
		}
		return new Pair<>(vendorEmailCommDtoList, totalCount);
	}

	@Override
	public String createReqPayloadForEmail(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		List<String> vendorList = null;
		List<VendorAzureEmailCommDto> venList = null;
		try {
			if (!env.containsProperty("vendorComm.azure.url")) {
				String msg = "Vendor Comm Azure url is not configured";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			String resourceUrl = env.getProperty("vendorComm.azure.url");
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			AzureEMailDto reqDto = gson.fromJson(reqObj, AzureEMailDto.class);
			venList = reqDto.getVendorDetails();
			venList.stream().forEach(obj -> {
				obj.setCreatedBy(userName);
				String returnType = obj.getReturnType();
				returnType = returnType.replaceAll(",", "-");
				obj.setReturnType(returnType);
			});
			vendorList = venList.stream()
					.map(VendorAzureEmailCommDto::getVendorGstin)
					.collect(Collectors.toCollection(ArrayList::new));
			List<List<VendorAzureEmailCommDto>> vChunk = Lists
					.partition(venList, 20);
			List<Runnable> tasks = vChunk.stream()
					.map(chunk -> new EmailExecTask(
							new AzureEMailDto(reqDto.getEntityName(), "NCVCOM",
									reqDto.getFy(),null, chunk),
							resourceUrl, TenantContext.getTenantId()))
					.collect(Collectors.toCollection(ArrayList::new));
			tasks.forEach(task -> execPool.execute(task));
			return "SUCCESS";

		} catch (Exception e) {
			if (vendorList != null && !vendorList.isEmpty())
				nonCompVendorVGstinRepository.updateEmailStatus(
						venList.get(0).getRequestID(), vendorList, "FAILED",
						LocalDateTime.now());
			LOGGER.error("error", e);
			throw new AppException(e);
		}

	}

}
