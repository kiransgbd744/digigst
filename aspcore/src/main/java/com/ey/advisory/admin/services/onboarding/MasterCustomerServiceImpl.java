package com.ey.advisory.admin.services.onboarding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.data.repositories.client.MasterCustomerRepository;
import com.ey.advisory.admin.services.gstin.jobs.GSTinConstants;
import com.ey.advisory.core.dto.MasterCustomerReqDto;
import com.ey.advisory.core.dto.MasterCustomerRespDto;
import com.ey.advisory.core.dto.Messages;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Slf4j
@Component("masterCustomerService")
public class MasterCustomerServiceImpl implements MasterCustomerService {

	@Autowired
	@Qualifier("masterCustomerRepository")
	private MasterCustomerRepository masterCustomerRepository;

	private static final List<String> RECIPIENT_TYPES = ImmutableList.of("UIN",
	        "Composition", "Input Service Distributor", "GOVT DEPT", "NRI",
	        "OIDAR", "SEZ", "Regular");

	private static final List<String> OUTSIDEINDIA = ImmutableList.of("Y", "N",
	        "y", "n");

	/*
	 * This method used for Get All Master Customer details.
	 * 
	 * @see
	 */
	@Override
	public List<MasterCustomerRespDto> getMasterCustomer(final Long entityId) {
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("MasterCustomerServiceImpl getMasterCustomer begin");
		}
		List<MasterCustomerRespDto> masterCustomerRespDtos = new ArrayList<>();
		// Getting all customer Details from Master Customer Table.
		List<MasterCustomerEntity> masterCustomerEntities = masterCustomerRepository
		        .getAllMasterCustomer(entityId);
		if (!masterCustomerEntities.isEmpty()) {
			masterCustomerEntities.forEach(masterCustomerEntity -> {
				MasterCustomerRespDto dto = new MasterCustomerRespDto();
				dto.setId(masterCustomerEntity.getId());
				dto.setRecipientGstnOrPan(
				        masterCustomerEntity.getRecipientGstnOrPan());
				dto.setLegalName(masterCustomerEntity.getLegalName());
				dto.setRecipientCode(masterCustomerEntity.getRecipientCode());
				dto.setRecipientType(masterCustomerEntity.getRecipientType());
				dto.setOutSideIndia(masterCustomerEntity.getOutSideIndia());
				dto.setEmailId(masterCustomerEntity.getEmailId());
				dto.setMobileNum(masterCustomerEntity.getMobileNum());
				dto.setTradeName(masterCustomerEntity.getTradeName());
				masterCustomerRespDtos.add(dto);
			});
		}
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("MasterCustomerServiceImpl getMasterCustomer end");
		}
		return masterCustomerRespDtos;
	}

	@Override
	public Messages updateMasterCustomer(List<MasterCustomerReqDto> dtos) {
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("MasterCustomerServiceImpl updateMasterCustomer begin");
		}

		// if it is already exiting record doing an update a record.

		Pair<List<String>, List<MasterCustomerEntity>> pairOfErrorCustomer = convertMaster(
		        dtos);
		Messages message = new Messages();
		List<String> errorMsg = pairOfErrorCustomer.getValue0();
		List<MasterCustomerEntity> masterCustomerEntities = pairOfErrorCustomer
		        .getValue1();
		if (null != errorMsg && !errorMsg.isEmpty()) {
			message.setMessages(errorMsg);
		} else if (!masterCustomerEntities.isEmpty()) {
			masterCustomerRepository.saveAll(masterCustomerEntities);
		}
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("MasterCustomerServiceImpl updateMasterCustomer end");
		}
		return message;
	}

	private Pair<List<String>, List<MasterCustomerEntity>> convertMaster(
	        List<MasterCustomerReqDto> dtos) {

		List<MasterCustomerEntity> masterCustomerEntities = new ArrayList<>();
		List<String> errorMessage = new ArrayList<>();
		if (null != dtos && !dtos.isEmpty()) {
			dtos.forEach(dto -> {
				if (dto.getSupplierGstnOrPan() == null) {
					errorMessage.add(dto.getSupplierGstnOrPan()
					        + "Supplier Gstn or Pan is mandatory ");
				}
				if (dto.getSupplierGstnOrPan() != null) {
					if (dto.getSupplierGstnOrPan().length() == 15
					        || dto.getSupplierGstnOrPan().length() == 10) {
						if (dto.getSupplierGstnOrPan().length() == 15) {
							String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
							        + "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
							        + "[A-Za-z0-9][A-Za-z0-9]$";
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern
							        .matcher(dto.getSupplierGstnOrPan());
							if (!matcher.matches()) {
								errorMessage.add(dto.getSupplierGstnOrPan()
								        + "Invalid Supplier Gstn ");
							}
						}
						if (dto.getSupplierGstnOrPan().length() == 10) {
							String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
							        + "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern
							        .matcher(dto.getSupplierGstnOrPan());
							if (!matcher.matches()) {
								errorMessage.add(dto.getSupplierGstnOrPan()
								        + "Invalid Supplier Pan");
							}
						}
					} else {
						errorMessage.add(dto.getSupplierGstnOrPan()
						        + "Invalid Supplier Gstn or Pan");
					}
				}

				if (dto.getRecipientGstnOrPan() == null) {
					errorMessage.add(dto.getRecipientGstnOrPan()
					        + "Customer Gstn or Pan is mandatory ");
				}
				if (dto.getRecipientGstnOrPan() != null) {
					if (dto.getRecipientGstnOrPan().length() == 15
					        || dto.getRecipientGstnOrPan().length() == 10) {
						if (dto.getRecipientGstnOrPan().length() == 15) {
							String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
							        + "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
							        + "[A-Za-z0-9][A-Za-z0-9]$";
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern
							        .matcher(dto.getRecipientGstnOrPan());
							if (!matcher.matches()) {
								errorMessage.add(dto.getRecipientGstnOrPan()
								        + "Invalid Customer Gstn ");
							}
						}
						if (dto.getRecipientGstnOrPan().length() == 10) {
							String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
							        + "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern
							        .matcher(dto.getRecipientGstnOrPan());
							if (!matcher.matches()) {
								errorMessage.add(dto.getRecipientGstnOrPan()
								        + "Invalid Supplier Pan");
							}
						}
					} else {
						errorMessage.add(dto.getRecipientGstnOrPan()
						        + "Invalid Customer Gstn or Pan");
					}
				}
				if (dto.getLegalName() == null) {
					errorMessage
					        .add(dto.getLegalName() + "Legal Name Madatory ");

				}
				if (dto.getLegalName() != null
				        && dto.getLegalName().length() > 100) {
					errorMessage
					        .add(dto.getLegalName() + "Legal Name is Invalid ");
				}
				if (dto.getTradeName() != null
				        && dto.getTradeName().length() > 100) {
					errorMessage
					        .add(dto.getTradeName() + "Trade Name is Invalid ");
				}
				if (dto.getRecipientType() == null) {
					errorMessage.add(dto.getTradeName()
					        + "Recipient Type is mandatory ");
				}
				if (dto.getRecipientType() != null
				        && !RECIPIENT_TYPES.contains(dto.getRecipientType())) {
					errorMessage.add(dto.getRecipientType()
					        + " Recipient Type is Invalid ");

				}
				if (dto.getRecipientCode() != null
				        && dto.getRecipientCode().length() > 100) {
					errorMessage.add(dto.getRecipientCode()
					        + " Recipient Code is Invalid ");
				}
				if (dto.getOutSideIndia() != null
				        && !OUTSIDEINDIA.contains(dto.getOutSideIndia())) {
					errorMessage.add(dto.getOutSideIndia()
					        + " Out side india is Invalid ");
				}

				if (dto.getRecipientGstnOrPan() != null && !GSTinConstants.Y
				        .equalsIgnoreCase(dto.getOutSideIndia())) {
					errorMessage.add(dto.getOutSideIndia()
					        + " If Y then Recipient GSTIN/PAN "
					        + "will not be there");
				}
				if (dto.getEmailId() != null
				        && dto.getEmailId().length() > 100) {
					errorMessage.add(dto.getEmailId() + "Email Id is Invalid ");
				}
				if (dto.getMobileNum() != null
				        && dto.getMobileNum().length() > 10) {
					errorMessage.add(
					        dto.getMobileNum() + "Mobile Number is Invalid ");
				}
				if (dto.getSupplierGstnOrPan() != null
				        && dto.getRecipientGstnOrPan() != null
				        && dto.getLegalName() != null
				        && dto.getRecipientType() != null) {
					MasterCustomerEntity entity = new MasterCustomerEntity();
					entity.setId(dto.getId());
					entity.setRecipientGstnOrPan(dto.getRecipientGstnOrPan());
					entity.setLegalName(dto.getLegalName());
					entity.setRecipientCode(dto.getRecipientCode());
					entity.setRecipientType(dto.getRecipientType());
					entity.setOutSideIndia(dto.getOutSideIndia());
					entity.setEmailId(dto.getEmailId());
					entity.setMobileNum(dto.getMobileNum());
					entity.setDelete(false);
					masterCustomerEntities.add(entity);
				}
			});
		}
		return new Pair<>(errorMessage, masterCustomerEntities);
	}

	public void deleteMasterCustomer(List<MasterCustomerReqDto> dtos) {
		List<Long> ids = new ArrayList<>();
		dtos.forEach(dto -> {
			ids.add(dto.getId());
		});

		if (!ids.isEmpty()) {
			masterCustomerRepository.deleteMasterCustomer(ids);
		}
	}

	@Override
	public List<String> getAllMasterCustomerGstins() {
		List<String> recipientGstnOrPanList = new ArrayList<>();
		List<MasterCustomerEntity> allMasterCustomer = masterCustomerRepository
				.getAllMasterCustomer();
		if (allMasterCustomer != null && !allMasterCustomer.isEmpty()) {
			allMasterCustomer.forEach(masterCustomer -> {
				String recipientGstnOrPan = masterCustomer
						.getRecipientGstnOrPan();
				if (recipientGstnOrPan != null
						&& !recipientGstnOrPan.trim().isEmpty()) {
					recipientGstnOrPanList.add(recipientGstnOrPan);
				}
			});
		}
		return recipientGstnOrPanList;
	}
	
}
