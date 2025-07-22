package com.ey.advisory.app.anx2.initiaterecon;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("ReconResponseDataStatusServiceImpl")
public class ReconResponseDataStatusServiceImpl
		implements ReconResponseDataStatusService {

	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository fileRepo;

	@Override
	public List<ReconResponseDataStatusDto> getReconResponseDataStatus(
			ReconResponseDataStatusReqDto req) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside ReconResponseDataStatusServiceImpl"
					+ ".getReconResponseDataStatus method, req : " + req);
		}
		String dataType = req.getDataType();
		String fileType = req.getFileType();
		String uploadFromDateString = req.getUploadFromDate();
		LocalDate uploadFromDate = LocalDate.parse(uploadFromDateString);
		String uploadToDateString = req.getUploadToDate();
		LocalDate uploadToDate = LocalDate.parse(uploadToDateString);
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Invoking "
					+ "findByDataTypeAndFileTypeAndReceivedDateBetween()"
					+ " method, " + "dataType : " + dataType + "fileType : "
					+ fileType + "uploadFromDate : " + uploadFromDate
					+ "uploadToDate : " + "" + uploadToDate);
		}
		List<Gstr1FileStatusEntity> responseList = fileRepo
				.findByDataTypeAndFileTypeAndUpdatedByAndReceivedDateBetween(
						dataType, fileType, userName, uploadFromDate,
						uploadToDate, Sort.by("id").descending());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Invoked "
					+ "findByDataTypeAndFileTypeAndReceivedDateBetween()"
					+ " method, responseList : " + responseList);
		}

		List<ReconResponseDataStatusDto> resList = responseList.stream()
				.map(o -> convertDto(o)).collect(Collectors.toList());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Befor returning  "
					+ "findByDataTypeAndFileTypeAndReceivedDateBetween()"
					+ " method, resList : " + resList);
		}
		return resList;
	}

	private ReconResponseDataStatusDto convertDto(Gstr1FileStatusEntity dto) {

		ReconResponseDataStatusDto resDto = new ReconResponseDataStatusDto();

		resDto.setDataType(dto.getDataType());
		resDto.setDateOfUpload(dto.getReceivedDate());
		resDto.setErrorCount(dto.getError());
		resDto.setFileName(dto.getFileName());
		resDto.setFileStatus(dto.getFileStatus());
		resDto.setFileType(dto.getFileType());
		resDto.setProcessedCount(dto.getProcessed());
		resDto.setTotalRecordsCount(dto.getTotal());
		resDto.setUploadeBy(dto.getUpdatedBy());
		resDto.setFileId(dto.getId());
		return resDto;

	}

}
