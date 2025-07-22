package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiEntityRepository;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service("VendorMasterApiServiceImpl")
@Slf4j
public class VendorMasterApiServiceImpl implements VendorMasterApiService {
 
	@Autowired
	@Qualifier("VendorMasterApiEntityRepository")
	private VendorMasterApiEntityRepository vendorMasterApiRepo;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Override
	public List<GstinDto> getListOfApiVendorGstin(List<String> vendorPans,
			Long entityId) {

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		List<String> vendorGstinList = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorPans, 2000);

		for (List<String> chunk : chunks) {
			vendorGstinList.addAll(
					vendorMasterApiRepo.findAllNonCompVendorGstinByVendorPans(
							chunk, entityInfoEntity.getPan()));
		}
		vendorGstinList.removeIf(Objects::isNull);
		Collections.sort(vendorGstinList);
		return vendorGstinList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GstinDto listRecipientPan(String e) {
		GstinDto dto = new GstinDto();
		dto.setGstin(e);
		return dto;
	}

	@Override
	public List<GstinDto> getListOfApiVendorPans(Long entityId) {
		List<String> vendorGstinList = new ArrayList<>();
		List<String> validVendorGstinList = new ArrayList<>();
		List<String> vendorPanList = new ArrayList<>();
		List<String> recipientGstinList = gSTNDetailRepository
				.findgstinByEntityId(entityId);
		List<String> recipientPansList = new ArrayList<>();
		if (!recipientGstinList.isEmpty()) {
			recipientPansList = recipientGstinList.stream()
					.map(eachobj -> eachobj.substring(2, 12))
					.collect(Collectors.toList());
			
			vendorGstinList = vendorMasterApiRepo
					.getDistinctActiveGstins(recipientPansList);
			String regex1 = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$";
			Pattern pattern1 = Pattern.compile(regex1);
			if(vendorGstinList != null && !vendorGstinList.isEmpty()){
				for(String vendor : vendorGstinList){
					Matcher matcher1 = pattern1.matcher(vendor);
					if (matcher1.matches() && vendor.length() == 15) {
						validVendorGstinList.add(vendor);
					}
				}
			}
			
			vendorPanList = vendorMasterApiRepo
					.getDistinctActiveVendorPan(validVendorGstinList);
			
			vendorPanList.removeIf(Objects::isNull);
			Collections.sort(vendorPanList);
		}
		return vendorPanList.stream().map(e -> listRecipientPan(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<VendorMasterApiEntity> extractAndPopulateVendorProcessedRecords(
			Long entityId, List<String> vendorGstinsList,
			List<String> vendorPans) {
		List<VendorMasterApiEntity> uploadEntities = new ArrayList<>();
		List<String> recipientPanList = entityInfoRepository
				.findPanByEntityId(entityId);

		if (vendorGstinsList.isEmpty() && vendorPans.isEmpty()) {
			uploadEntities = vendorMasterApiRepo
					.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
		}

		else if (vendorGstinsList.isEmpty() && !vendorPans.isEmpty()) {
			List<List<String>> chunks = Lists.partition(vendorPans, 2000);
			for (List<String> chunk : chunks) {
				uploadEntities.addAll(vendorMasterApiRepo
						.findByIsDeleteFalseAndRecipientPANInAndVendorPANIn(
								recipientPanList, chunk));
			}
		} else {
			List<List<String>> chunks = Lists.partition(vendorGstinsList, 2000);
			for (List<String> chunk : chunks) {
				uploadEntities.addAll(vendorMasterApiRepo
						.findByIsDeleteFalseAndRecipientPANInAndVendorGstinIn(
								recipientPanList, chunk));
			}
		}
		return uploadEntities;
	}
	

	@Override
	public List<String> getOnlyGstInList(
			List<VendorMasterApiEntity> uploadEntities) {
		return uploadEntities.stream()
				.map(VendorMasterApiEntity::getVendorGstin).distinct()
				.collect(Collectors.toList());
	}
}
