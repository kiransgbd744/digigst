package com.ey.advisory.admin.services.onboarding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.ey.advisory.admin.data.repositories.client.MasterVendorRepository;
import com.ey.advisory.core.dto.MasterVendorReqDto;
import com.ey.advisory.core.dto.MasterVendorRespDto;
import com.ey.advisory.core.dto.Messages;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Slf4j
@Component("masterVendorService")
public class MasterVendorServiceImpl implements MasterVendorService {

	@Autowired
	@Qualifier("masterVendorRepository")
	private MasterVendorRepository masterVendorRepository;

	private static final List<String> OUTSIDEINDIA = ImmutableList.of("Y", "N",
			"y", "n");
	private static final List<String> SUPPLIER_TYPES = ImmutableList.of("UIN",
			"Composition", "Input Service Distributor", "GOVT DEPT", "NRI",
			"OIDAR", "SEZ", "Regular");

	/**
	 * This method used for Getting All Master Vendor details
	 */
	public List<MasterVendorRespDto> getMasterVendor(final Long entityId) {
		LOGGER.error("MasterVendorServiceImpl getMasterVendor begin");
		List<MasterVendorEntity> entities = masterVendorRepository
				.getMasterVendorDetails(entityId);
		List<MasterVendorRespDto> dtos = new ArrayList<>();
		if (!entities.isEmpty()) {
			entities.forEach(entity -> {
				MasterVendorRespDto dto = new MasterVendorRespDto();
				dto.setId(entity.getId());
				dto.setCustGstinPan(entity.getCustGstinPan());
				dto.setSupplierCode(entity.getSupplierCode());
				dto.setSupplierGstinPan(entity.getSupplierGstinPan());
				dto.setSupplierType(entity.getSupplierType());
				dto.setEmailId(entity.getEmailId());
				dto.setMobileNum(entity.getMobileNum());
				dto.setOutSideIndia(entity.getOutSideIndia());
				dto.setSupplierName(entity.getLegalName());
				dtos.add(dto);
			});
		}
		LOGGER.error("MasterVendorServiceImpl getMasterVendor end");
		return dtos;
	}

	/**
	 * This method used for Update a Master Vendor table
	 */
	@Override
	public Messages updateMasterVendor(List<MasterVendorReqDto> dtos) {
		LOGGER.error("MasterVendorServiceImpl updateMasterVendor begin");
		Pair<List<String>, List<MasterVendorEntity>> vendorList = convertMasterVendor(
				dtos);
		Messages msg = new Messages();
		// If based on id Record is exit doing update
		List<String> errorMsg = vendorList.getValue0();
		List<MasterVendorEntity> entities = vendorList.getValue1();
		if (errorMsg != null && !errorMsg.isEmpty()) {
			msg.setMessages(errorMsg);
		} else if (entities != null && !entities.isEmpty()) {
			masterVendorRepository.saveAll(entities);
		}
		LOGGER.error("MasterVendorServiceImpl updateMasterVendor end");
		return msg;
	}

	private Pair<List<String>, List<MasterVendorEntity>> convertMasterVendor(
			List<MasterVendorReqDto> dtos) {
		List<MasterVendorEntity> entities = new ArrayList<>();
		List<String> errorMsg = new ArrayList<>();

		dtos.forEach(dto -> {
			if (dto.getCustGstinPan() == null) {
				errorMsg.add("Customer gstin or Pan is mandatory");
			}
			if (dto.getCustGstinPan() != null
					&& dto.getCustGstinPan().length() == 15
					|| dto.getCustGstinPan().length() == 10) {
				if (dto.getCustGstinPan().length() == 15) {
					String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
							+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
							+ "[A-Za-z0-9][A-Za-z0-9]$";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(dto.getCustGstinPan());
					if (!matcher.matches()) {
						errorMsg.add(dto.getCustGstinPan()
								+ " Invalid Customer Gstn ");
					}
				}
				if (dto.getCustGstinPan().length() == 10) {
					String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
							+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(dto.getCustGstinPan());
					if (!matcher.matches()) {
						errorMsg.add(
								dto.getCustGstinPan() + " Invalid Pan Number");
					}
				}
			} else {
				errorMsg.add(dto.getSupplierGstinPan()
						+ "Invalid Customer Gstn or Pan");
			}
			if (dto.getSupplierGstinPan() == null) {
				errorMsg.add("Supplier gstin or Pan is mandatory");
			}
			if (dto.getSupplierGstinPan() != null
					&& dto.getSupplierGstinPan().length() == 15
					|| dto.getSupplierGstinPan().length() == 10) {
				if (dto.getSupplierGstinPan().length() == 15) {
					String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
							+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
							+ "[A-Za-z0-9][A-Za-z0-9]$";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern
							.matcher(dto.getSupplierGstinPan());
					if (!matcher.matches()) {
						errorMsg.add(dto.getSupplierGstinPan()
								+ "Invalid Supplier Gstn ");
					}
				}
				if (dto.getSupplierGstinPan().length() == 10) {
					String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
							+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern
							.matcher(dto.getSupplierGstinPan());
					if (!matcher.matches()) {
						errorMsg.add(dto.getSupplierGstinPan()
								+ " Invalid Pan Number");
					}
				}
			} else {
				errorMsg.add(dto.getSupplierGstinPan()
						+ "Invalid Supplier Gstn or Pan");
			}
			if (dto.getSupplierType() == null) {
				errorMsg.add("Supplier type is empty");
			}

			if (dto.getSupplierType() != null
					&& !SUPPLIER_TYPES.contains(dto.getSupplierType())) {
				errorMsg.add(
						dto.getSupplierType() + " Supplier type is invald");
			}

			if (dto.getOutSideIndia() != null
					&& !OUTSIDEINDIA.contains(dto.getOutSideIndia())) {
				errorMsg.add(
						dto.getOutSideIndia() + " Out side india is Invalid ");
			}

			if (dto.getSupplierCode() != null
					&& dto.getSupplierCode().length() > 100) {
				errorMsg.add(
						dto.getSupplierCode() + " Supplier code is Invalid");
			}

			if (dto.getSupplierName() != null
					&& dto.getSupplierName().length() > 100) {
				errorMsg.add(
						dto.getSupplierName() + " Supplier name is Invalid");
			}

			if (dto.getEmailId() != null && dto.getEmailId().length() > 100) {
				errorMsg.add(dto.getEmailId() + " Email id is invalid");
			}
			if (dto.getMobileNum() != null
					&& dto.getMobileNum().length() == 10) {
				errorMsg.add(dto.getMobileNum() + " Mobile number is invalid");
			}

			if (dto.getCustGstinPan() != null
					&& dto.getSupplierGstinPan() != null
					&& dto.getSupplierType() != null) {
				MasterVendorEntity entity = new MasterVendorEntity();
				entity.setId(dto.getId());
				entity.setCustGstinPan(dto.getCustGstinPan());
				entity.setSupplierCode(dto.getSupplierCode());
				entity.setSupplierGstinPan(dto.getSupplierGstinPan());
				entity.setSupplierType(dto.getSupplierType());
				entity.setEmailId(dto.getEmailId());
				entity.setMobileNum(dto.getMobileNum());
				entity.setOutSideIndia(dto.getOutSideIndia());
				entity.setLegalName(dto.getSupplierName());
				entity.setDelete(false);
				entities.add(entity);
			}
		});
		return new Pair<>(errorMsg, entities);
	}

	public void deleteMasterVendor(List<MasterVendorReqDto> dtos) {
		List<Long> ids = new ArrayList<>();
		dtos.forEach(dto -> {
			ids.add(dto.getId());
		});

		if (!ids.isEmpty()) {
			masterVendorRepository.deleteMasterVendor(ids);
		}
	}

	@Override
	public List<String> getAllMasterVendorGstins() {
		List<String> supplierGstinOrPanList = new ArrayList<>();
		List<MasterVendorEntity> allMasterVendor = masterVendorRepository
				.getAllMasterVendor();
		if (allMasterVendor != null && !allMasterVendor.isEmpty()) {
			allMasterVendor.forEach(masterVendor -> {
				String supplierGstinOrPan = masterVendor
						.getSupplierGstinPan();
				if (supplierGstinOrPan != null
						&& !supplierGstinOrPan.trim().isEmpty()) {
					supplierGstinOrPanList.add(supplierGstinOrPan);
				}
			});
		}
		return supplierGstinOrPanList;
	}
}
