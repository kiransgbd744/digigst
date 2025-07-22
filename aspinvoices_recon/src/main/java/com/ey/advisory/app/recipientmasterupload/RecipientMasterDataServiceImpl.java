package com.ey.advisory.app.recipientmasterupload;

import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.app.recipientmaster.dto.RecipientGstinDto;
import com.ey.advisory.app.recipientmaster.dto.RecipientMasterDataDto;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Slf4j
@Service
public class RecipientMasterDataServiceImpl
		implements RecipientMasterDataService {

	@Autowired
	RecipientMasterUploadRepository recipientMasterRepo;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Override
	public Pair<List<RecipientMasterDataDto>, Integer> listRecipientMasterData(
			List<String> recipientPanList, List<String> recipientGstinsList,
			int pageSize, int pageNum) {

		try {
			int recordsToStart = pageNum;

			int noOfRowstoFetch = pageSize;

			int totalCount = 0;

			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			List<RecipientMasterUploadEntity> recipientMasterUploadEntity = null;
			if (CollectionUtils.isEmpty(recipientGstinsList)) {
				recipientMasterUploadEntity = recipientMasterRepo
						.findByRecipientPANInAndIsDeleteFalse(recipientPanList,
								pageReq);
				totalCount = recipientMasterRepo
						.findCountByRecipientPANInAndIsDeleteFalse(
								recipientPanList);

			} else if (CollectionUtils.isEmpty(recipientPanList)) {
				recipientMasterUploadEntity = recipientMasterRepo
						.findByRecipientGstinInAndIsDeleteFalse(
								recipientGstinsList, pageReq);
				totalCount = recipientMasterRepo
						.findCountByRecipientGstinInAndIsDeleteFalse(
								recipientGstinsList);

			} else {
				recipientMasterUploadEntity = recipientMasterRepo
						.findByRecipientPANInAndRecipientGstinInAndIsDeleteFalse(
								recipientPanList, recipientGstinsList, pageReq);
				totalCount = recipientMasterRepo
						.findCountByRecipientPANInAndRecipientGstinInAndIsDeleteFalse(
								recipientPanList, recipientGstinsList);
			}

			List<RecipientMasterDataDto> listDto = recipientMasterUploadEntity
					.stream().map(e -> convertToDto(e))
					.collect(Collectors.toList());

			return new Pair<>(listDto, totalCount);

		} catch (Exception ex) {
			String msg = String.format(
					"Error Occured while executing query  %s ", ex.toString());
			ex.printStackTrace();
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

	private RecipientMasterDataDto convertToDto(
			RecipientMasterUploadEntity entity) {

		RecipientMasterDataDto dto = new RecipientMasterDataDto();

		dto.setInvoiceKey(entity.getInvoiceKey());
		dto.setRecipientGstin(entity.getRecipientGstin());
		dto.setRecipientPAN(entity.getRecipientPAN());
		dto.setRecipientEmailId2(entity.getRecipientEmailId2());
		dto.setRecipientEmailId3(entity.getRecipientEmailId3());
		dto.setRecipientEmailId4(entity.getRecipientEmailId4());
		dto.setRecipientEmailId5(entity.getRecipientEmailId5());
		dto.setRecipientPrimEmailId(entity.getRecipientPrimEmailId());
		dto.setCceEmailId1(entity.getCceEmailId1());
		dto.setCceEmailId2(entity.getCceEmailId2());
		dto.setCceEmailId3(entity.getCceEmailId3());
		dto.setCceEmailId4(entity.getCceEmailId4());
		dto.setCceEmailId5(entity.getCceEmailId5());

		if (entity.isGetGstr2AEmail()) {
			dto.setIsGetGstr2AEmail("Y");
		} else {
			dto.setIsGetGstr2AEmail("N");
		}
		if (entity.isGetGstr2BEmail()) {
			dto.setIsGetGstr2BEmail("Y");
		} else {
			dto.setIsGetGstr2BEmail("N");
		}
		if (entity.isRetCompStatusEmail()) {
			dto.setIsRetCompStatusEmail("Y");
		} else {
			dto.setIsRetCompStatusEmail("N");
		}
		if (entity.isDRC01BEmail()) {
			dto.setIsDrc01b("Y");
		} else {
			dto.setIsDrc01b("N");
		}
		if (entity.isDRC01CEmail()) {
			dto.setIsDrc01c("Y");
		} else {
			dto.setIsDrc01c("N");
		}
		return dto;
	}

	@Override
	public List<RecipientGstinDto> getListOfRecipientGstinList(Long entityId) {

		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(entityId);

		List<RecipientMasterUploadEntity> recipientMasterUploadEntity = recipientMasterRepo
				.findByRecipientPANAndIsDeleteFalse(entityInfoEntity.getPan());

		List<RecipientGstinDto> listdto = recipientMasterUploadEntity.stream()
				.map(e -> convertToDto1(e)).collect(Collectors.toList());

		return listdto;

	}

	private RecipientGstinDto convertToDto1(RecipientMasterUploadEntity e) {

		RecipientGstinDto gstinsDto = new RecipientGstinDto();
		gstinsDto.setRecipientGstin(e.getRecipientGstin());
		return gstinsDto;
	}

}
