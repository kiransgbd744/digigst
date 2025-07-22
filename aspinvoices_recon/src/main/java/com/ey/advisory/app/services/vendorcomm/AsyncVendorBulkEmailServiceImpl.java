package com.ey.advisory.app.services.vendorcomm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.asprecon.VendorEmailHistoryEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorEmailStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorEmailHistoryRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorEmailStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.app.vendorcomm.dto.AzureEMailDto;
import com.ey.advisory.app.vendorcomm.dto.VendorAzureEmailCommDto;
import com.ey.advisory.app.vendorcomm.dto.VendorEmailCommDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

		
@Slf4j
@Component("AsyncVendorBulkEmailServiceImpl")
public class AsyncVendorBulkEmailServiceImpl
implements AsyncVendorBulkEmailService{
	
	@Autowired
	private VendorReqVendorGstinRepository vendorReqVendorGstinRepository;

	@Autowired
	VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("VendorEmailPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private VendorEmailHistoryRepository vendrHistryRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo;
	
	@Autowired
	@Qualifier("VendorEmailStatusRepository")
	VendorEmailStatusRepository vendorEmailStatusRepository;


	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	
	@Override
	public String sendAll(Long id, Long entityId, Long requestId) {
		String response= null;
		try{
			Pair<List<VendorEmailCommDto>, Integer> vendorEmailCommDtoListPair = 
					getEmailCommDetails(requestId, entityId);
			List<VendorEmailCommDto> vendorEmailCommDtoList = vendorEmailCommDtoListPair.getValue0();
			Collections.sort(vendorEmailCommDtoList,
					new Comparator<VendorEmailCommDto>() {
						public int compare(VendorEmailCommDto o1,
								VendorEmailCommDto o2) {
							return (o1.getVendorGstin()
									.compareTo(o2.getVendorGstin()));
						}
					});
			if (LOGGER.isDebugEnabled()) {
				String msg =("vendorEmailCommDtoList size -> {} and vendorEmailCommDtoList ,vendorEmailCommDtoList.size(), vendorEmailCommDtoList.toString()");
				LOGGER.debug(msg);
			}
			
			Optional<VendorEmailStatusEntity> entity = vendorEmailStatusRepository.findById(id);
			
			if((vendorEmailCommDtoListPair.getValue0())!=null) {
			response = createRequestPayloadForEmail(vendorEmailCommDtoListPair.getValue0(),entityId, entity.get().getReconType());
			}
		}catch (Exception e) {
			LOGGER.error("error while getting response", e);
			throw new AppException(e);
		}
		
	
    return response;
	}
	
	@Override
	public Pair<List<VendorEmailCommDto>, Integer> getEmailCommDetails(
			Long requestId, Long entityId) {
		List<VendorEmailCommDto> vendorEmailCommDtoList = new ArrayList<>();
		List<VendorMasterUploadEntity> vendorMasterList = new ArrayList<>();
		Integer totalCount = 0;
		Map<String, String> emailStatusMap = new HashMap<>();
		Map<String, String> emailUpdateOnMap = new HashMap<>();

		try {
			Long strtTime = System.currentTimeMillis();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("vendorGstinList start -> {}", strtTime);
			}
			List<VendorReqVendorGstinEntity> vendorGstinList = vendorReqVendorGstinRepository
					.findByRequestId(requestId);
			List<String> vGstinList = new ArrayList<>();
			vendorGstinList.forEach(obj -> {
				emailStatusMap.put(obj.getVendorGstin(), obj.getEmailStatus());
				String formattedDate = getStandardTime(obj.getUpdatedOn());
				emailUpdateOnMap.put(obj.getVendorGstin(), formattedDate);
				vGstinList.add(obj.getVendorGstin());
			});

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("vendorGstinList end {}, difference -> {}",
						System.currentTimeMillis(),
						(System.currentTimeMillis() - strtTime));
			}

			strtTime = System.currentTimeMillis();
			// do sort and chunk
			Collections.sort(vGstinList);
			EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
					.findEntityByEntityId(entityId);

			//List<List<String>> chunks = Lists.partition(vGstinList, pageSize);
			List<List<String>> chunks = Lists.partition(vGstinList, 2000);
			for (List<String> chunk : chunks) {
				List<VendorMasterUploadEntity> list = vendorMasterUploadEntityRepository
				.findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrueOrderByVendorGstinAsc(
						entityInfoEntity.getPan(), chunk);
			vendorMasterList.addAll(list);
			}
	/*		List<List<String>> countChunks = Lists.partition(vGstinList, 2000);
			for (List<String> countChunk : countChunks) {

				totalCount += vendorMasterUploadEntityRepository
						.findCountByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrue(
								entityInfoEntity.getPan(), countChunk);
			}*/

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("vendor master List end {}, difference -> {}",
						System.currentTimeMillis(),
						(System.currentTimeMillis() - strtTime));
			}

			strtTime = System.currentTimeMillis();
			// History Map
			List<VendorEmailHistoryEntity> historyList = vendrHistryRepository
					.findByRequestId(requestId);

			Map<String, VendorEmailHistoryEntity> histryMap = historyList
					.stream()
					.collect(Collectors.toMap(
							VendorEmailHistoryEntity::getVendorGstin,
							Function.identity()));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("vendor history map end {}, difference -> {}",
						System.currentTimeMillis(),
						(System.currentTimeMillis() - strtTime));
			}

			strtTime = System.currentTimeMillis();
			vendorMasterList.forEach(eachObje -> {
				VendorEmailCommDto vendorEmailCommDto = new VendorEmailCommDto();
				if (histryMap.containsKey(eachObje.getVendorGstin())) {
					VendorEmailHistoryEntity histryObj = histryMap
							.get(eachObje.getVendorGstin());

					vendorEmailCommDto = convertToVendorDto(histryObj,
							requestId, eachObje.getVendorGstin());

					histryMap.remove(eachObje.getVendorGstin());

				} else {
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

					vendorEmailCommDto.setVendorContactNumber(
							eachObje.getVendorContactNumber());

					vendorEmailCommDto.setUpdatedOn(
							emailUpdateOnMap.get(eachObje.getVendorGstin()));

					vendorEmailCommDto.setVendorName(eachObje.getVendorName());

					vendorEmailCommDto
							.setVendorGstin(eachObje.getVendorGstin());

					vendorEmailCommDto.setRequestID(requestId);
				}

				vendorEmailCommDtoList.add(vendorEmailCommDto);

			});

			/*if (!histryMap.isEmpty()) {
				Iterator<String> iterator = histryMap.keySet().iterator();
				while (iterator.hasNext()) {
					String vendorGstin = iterator.next();
					VendorEmailHistoryEntity obj = histryMap.get(vendorGstin);
					VendorEmailCommDto vendorEmailCommDto = new VendorEmailCommDto();

					vendorEmailCommDto = convertToVendorDto(obj, requestId,
							vendorGstin);
					

					vendorEmailCommDtoList.add(vendorEmailCommDto);

				}
			}*/
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("vendor dto end {}, difference -> {}",
						System.currentTimeMillis(),
						(System.currentTimeMillis() - strtTime));
			}

		} catch (Exception e) {
			LOGGER.error("error", e);
			throw new AppException(e);
		}
		return new Pair<>(vendorEmailCommDtoList, 0);
	}

	@Override
	public String createRequestPayloadForEmail(List<VendorEmailCommDto> vendorEmailCommDtoListPair, Long entityId, String reconType) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		List<String> vendorList = null;
		List<VendorAzureEmailCommDto> venList = null;

		try {

		String e =entityInfoDetailsRepository.entityNameById(Arrays.asList(entityId));
		
			 venList = convertToVendorAzureEmailCommDto(vendorEmailCommDtoListPair);
			
			 //String	returnType= venList.get(0).getReturnType();
		
		//	AzureEMailDto reqDto = new AzureEMailDto(entityName, userName, userName, userName, vendorAzureEmailCommDtoList);
			//venList = vendorAzureEmailCommDtoList;
			boolean isAPOpted = true;
			/*//List<Long> ids = entityInfoRepo
					.findActiveIdByEntityName(reqDto.getEntityName());*/
			if ("2A_PR".equalsIgnoreCase(reconType)) {
				List<Long> optedEntities = entityConfigPemtRepo
						.getAllEntitiesOpted2B((Arrays.asList(entityId)), "I27");

				if (optedEntities.isEmpty()) {
					isAPOpted = false;
				}

				String rGstinprocName = null;

				if (isAPOpted) {
					rGstinprocName = "vendorCommSummRGstinDataAPOpted";
				} else {
					rGstinprocName = "vendorCommSummRGstinDataNonAp";
				}

			

				for (VendorAzureEmailCommDto venList1 : venList) {
					StoredProcedureQuery rGstinDataProc = entityManager
							.createNamedStoredProcedureQuery(rGstinprocName);
					venList1.setVendrName(venList1.getVendorName());
					venList1.setCreatedBy(userName);
					List<String> rlist = null;
					rGstinDataProc.setParameter("P_REQUEST_ID",
							venList1.getRequestID());
					rGstinDataProc.setParameter("P_VENDOR_GSTIN",
							venList1.getVendorGstin());

					rlist = rGstinDataProc.getResultList();

					String rGstin = null;
					if (!rlist.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Recipient Gstin Record count email : ",
									rlist.size());
							LOGGER.debug(msg);
						}
						rGstin = String.join(",", rlist);
					}

					venList1.setRGSTIN(rGstin);
				}

				if (!env.containsProperty("vendorComm.azure.2APR.url")) {
					String msg = "Vendor Comm Azure url is not configured";
					LOGGER.error(msg);
					throw new AppException(msg);
				}
				String resourceUrl = env
						.getProperty("vendorComm.azure.2APR.url");

				vendorList = venList.stream()
						.map(VendorAzureEmailCommDto::getVendorGstin)
						.collect(Collectors.toCollection(ArrayList::new));
				List<List<VendorAzureEmailCommDto>> vChunk = Lists
						.partition(venList, 20);

				List<Runnable> tasks = vChunk.stream()
						.map(chunk -> new EmailExecTask(
								new AzureEMailDto(e,
										"VCOM", null, reconType,
										chunk),
								resourceUrl, TenantContext.getTenantId()))
						.collect(Collectors.toCollection(ArrayList::new));
				tasks.forEach(task -> execPool.execute(task));
			} else {

				String rGstinprocName = "vendorComm2BPRRgstinReport";

				for (VendorAzureEmailCommDto venList1 : venList) {
					StoredProcedureQuery rGstinDataProc = entityManager
							.createNamedStoredProcedureQuery(rGstinprocName);

					venList1.setVendrName(venList1.getVendorName());
					venList1.setCreatedBy(userName);
					List<String> rlist = null;
					/*rGstinDataProc.registerStoredProcedureParameter(
							"P_REQUEST_ID", Long.class, ParameterMode.IN);
					*/rGstinDataProc.setParameter("P_REQUEST_ID",
							venList1.getRequestID());
					/*rGstinDataProc.registerStoredProcedureParameter(
							"P_VENDOR_GSTIN", String.class, ParameterMode.IN);
*/
					rGstinDataProc.setParameter("P_VENDOR_GSTIN",
							venList1.getVendorGstin());

					rlist = rGstinDataProc.getResultList();

					String rGstin = null;
					if (!rlist.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Recipient Gstin Record count email : ",
									rlist.size());
							LOGGER.debug(msg);
						}
						rGstin = String.join(",", rlist);
					}

					venList1.setRGSTIN(rGstin);
				//	entityManager.clear();
				}
				if (!env.containsProperty("vendorComm.azure.2APR.url")) {
					String msg = "Vendor Comm Azure url is not configured";
					LOGGER.error(msg);
					throw new AppException(msg);
				}
				String resourceUrl = env
						.getProperty("vendorComm.azure.2APR.url");

				/*
				 * venList.stream().forEach(obj -> obj.setCreatedBy(userName));
				 */
				vendorList = venList.stream()
						.map(VendorAzureEmailCommDto::getVendorGstin)
						.collect(Collectors.toCollection(ArrayList::new));
				List<List<VendorAzureEmailCommDto>> vChunk = Lists
						.partition(venList, 20);

				List<Runnable> tasks = vChunk.stream()
						.map(chunk -> new EmailExecTask(
								new AzureEMailDto(e,
										"VCOM", null, reconType,
										chunk),
								resourceUrl, TenantContext.getTenantId()))
						.collect(Collectors.toCollection(ArrayList::new));
				tasks.forEach(task -> execPool.execute(task));
			}
			return "SUCCESS";
			
		} catch (Exception e) {
			if (vendorList != null && !vendorList.isEmpty())
				vendorReqVendorGstinRepository.updateEmailStatus(
						venList.get(0).getRequestID(), vendorList, "FAILED",
						LocalDateTime.now());
			LOGGER.error("error", e);
			throw new AppException(e);
		}

	}
	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

	private VendorEmailCommDto convertToVendorDto(VendorEmailHistoryEntity obj,
			Long requestId, String gstin) {
		VendorEmailCommDto vendorEmailCommDto = new VendorEmailCommDto();

		vendorEmailCommDto.setVendPrimEmailId(obj.getVendrPrimryEmail());

		String[] secEmail = obj.getVendrTotalSecEmail().split(",");
		for (String email : secEmail) {
			vendorEmailCommDto.addSecondaryEmail(email);
		}

		String[] recpEmail = obj.getTotalRecipEmail().split(",");

		for (String email : recpEmail) {
			vendorEmailCommDto.addRecipientEmail(email);
		}
		String status = obj.getEmailStatus();
		if (status.equalsIgnoreCase("FAILED")) {
			vendorEmailCommDto.setEmailStatus(obj.getEmailStatus());
		} else {
			String emailStatus = null;
			int counter = obj.getCounter();
			if (counter > 1) {
				emailStatus = "REMINDER " + (counter - 1) + " ("
						+ getStandardTime(obj.getCreatedOn()).toString() + ")";
			} else
				emailStatus = "SENT";

			vendorEmailCommDto.setEmailStatus(emailStatus);
		}
		vendorEmailCommDto
				.setUpdatedOn(getStandardTime(obj.getCreatedOn()).toString());
		vendorEmailCommDto.setVendorName(obj.getVendorName());
		vendorEmailCommDto.setVendorContactNumber(obj.getVendorContactNumber());
		vendorEmailCommDto.setVendorGstin(gstin);
		vendorEmailCommDto.setRequestID(requestId);
		return vendorEmailCommDto;
	}
	  public List<VendorAzureEmailCommDto> convertToVendorAzureEmailCommDto(
	            List<VendorEmailCommDto> vendorEmailCommDtoList){
		  List<VendorAzureEmailCommDto> vendorAzureEmailList=  new ArrayList<>();
		  
		  for (VendorEmailCommDto obj : vendorEmailCommDtoList) {
			  VendorAzureEmailCommDto  dto = new VendorAzureEmailCommDto();
			  			
			  if(obj.getRecipientEmailIds()!=null){
					Set<String> recipientEmailList = obj.getRecipientEmailIds().stream().map(t -> t.getEmailId()).collect(Collectors.toSet());
			 dto.setTotalRecpEmailIds(recipientEmailList);
			 dto.setRecipientEmailIds(recipientEmailList);
					  }
			  if(obj.getSecondaryEmailIds()!=null){
					Set<String> secEmailList = obj.getSecondaryEmailIds().stream().map(t -> t.getEmailId()).collect(Collectors.toSet());
			 dto.setTotalSecEmailIds(secEmailList);
			 dto.setSecondaryEmailIds(secEmailList);
					  }
			 dto.setVendorName(obj.getVendorName());
			 dto.setVendorGstin(obj.getVendorGstin());
			 dto.setVendPrimEmailId(obj.getVendPrimEmailId());
			 dto.setEmailStatus(obj.getEmailStatus());
			 dto.setVendorContactNumber(obj.getVendorContactNumber());
			 dto.setRequestID(obj.getRequestID());
			 dto.setReconType(obj.getReturnType());
			 dto.setUpdatedOn(obj.getUpdatedOn());
			 
			 
			 vendorAzureEmailList.add(dto);
		  
	            	
	            }
		  return vendorAzureEmailList;
	  }      
	       

}
