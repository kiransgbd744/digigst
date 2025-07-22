package com.ey.advisory.app.glrecon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
/*
 * This default GLRecon upload represent service for uploading the files from
 * various sources reading the data from document and it will store or sent
 * excel file to Document Service
 */
@Component("GlRecongetFileUploadStatusServiceImpl")
@Slf4j
public class GlRecongetFileUploadStatusServiceImpl
		implements
			GlReconGetFileUploadStatusService {

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository GlReconFileStatusRepository;

	@Override
	public Pair<List<GlFileStatusDto>, Integer> getFileUploadStatus(
			GlFileStatusReqDto reqDto, int pageNum, int pageSize) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		List<GlFileStatusDto> respList = new ArrayList<>();
		int totalCount = 0;
		try {
			User user = SecurityContext.getUser();
			String userName = user != null
					? user.getUserPrincipalName()
					: "SYSTEM";
			List<GlReconFileStatusEntity> list = new ArrayList<>();
			LocalDateTime fromDate1 = null;
			LocalDateTime toDate1 = null;
			if (reqDto.getDataRecvFrom() != null
					&& reqDto.getDataRecvTo() != null) {
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("yyyy-MM-dd");
				fromDate1 = LocalDate.parse(reqDto.getDataRecvFrom(), formatter)
						.atStartOfDay();
				toDate1 = LocalDate.parse(reqDto.getDataRecvTo(), formatter)
						.atStartOfDay().plusDays(1);
			}
			Pageable pageReq = PageRequest.of(pageNum, pageSize, Direction.DESC,
					"id");

			if (reqDto.getFileType().isEmpty()) {
				if (reqDto.getFlag()) {
					reqDto.setFileType(
							Arrays.asList("Business_Unit_code", "Document_type",
									"Supply_Type", "GL_Code_Mapping_Master_GL",
									"Tax_code", "GL_dump_mapping_file"));
				} else {
					reqDto.setFileType(Arrays.asList("GL Dump"));
				}
			}

			if (reqDto.getStatus().isEmpty()) {
				reqDto.setStatus(
						Arrays.asList("Initiated", "Success", "Failed"));
			}
			// 180742--multi user access

			if (reqDto.getDataRecvFrom() != null
					&& reqDto.getDataRecvTo() != null) {

				//// this is for sr vs gl and masterfile search with filter
				list = GlReconFileStatusRepository
						.findAllByFileTypeInAndFileStatusInAndUpdatedOnBetweenAndCraetedBy(
								reqDto.getFileType(), reqDto.getStatus(),
								fromDate1, toDate1, userName, // Corrected to
																// match
																// 'craetedBy'
								pageReq);

				totalCount = GlReconFileStatusRepository
						.countByFileTypeInAndFileStatusInAndUpdatedOnBetweenAndCraetedBy(
								reqDto.getFileType(), reqDto.getStatus(),
								fromDate1, toDate1, userName); // Corrected to
																// match
																// 'craetedBy'

			} else {
				if (reqDto.getFlag()) {// this is for master data upload search
										// without any from and to Date
					list = GlReconFileStatusRepository
							.findAllByFileTypeInAndFileStatusIn(
									reqDto.getFileType(), reqDto.getStatus(),
									pageReq);

					totalCount = GlReconFileStatusRepository
							.countByFileTypeInAndFileStatusIn(
									reqDto.getFileType(), reqDto.getStatus());
				}
				// this is for sr vs gl search without any without any from and
				// to Date
				else {
					list = GlReconFileStatusRepository
							.findAllByFileTypeInAndFileStatusInAndCraetedBy(
									reqDto.getFileType(), reqDto.getStatus(),
									userName, // Added 'craetedBy' filter
									pageReq);

					totalCount = GlReconFileStatusRepository
							.countByFileTypeInAndFileStatusInAndCraetedBy(
									reqDto.getFileType(), reqDto.getStatus(),
									userName); // Added 'craetedBy' filter

				}
			}

			respList = list.stream().map(o -> convertRowsToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception e) {
			LOGGER.error(
					"ERROR OCCURED IN GlRecongetFileUploadStatusServiceImpl"
							+ e);
			throw new AppException(e);
		}

		return new Pair<List<GlFileStatusDto>, Integer>(respList, totalCount);
	}

	private GlFileStatusDto convertRowsToDto(GlReconFileStatusEntity arr) {
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");

		GlFileStatusDto obj = new GlFileStatusDto();
		obj.setFileId(arr.getId());
		obj.setFileName(arr.getFileName());
		obj.setStatus(arr.getFileStatus());
		obj.setUploadedBy(arr.getCraetedBy());
		obj.setFileType(fileTypeName(arr.getFileType()));
		obj.setCreatedOn(formatter
				.format(EYDateUtil.toISTDateTimeFromUTC(arr.getUpdatedOn()))
				.toString());

		String errorDesc = arr.getErrorDesc();
		if (errorDesc != null && !errorDesc.trim().isEmpty()) {
			obj.setErrorDescription(errorDesc);
		}
		if ("Invalid Header/Mismatch in Header"
				.equalsIgnoreCase(errorDesc != null ? errorDesc.trim() : "")) {
			obj.setDownloadFlag(false);
		} else {
			obj.setDownloadFlag(true);
		}
		
		if ("GL Code Mapping Master - GL"
				.equalsIgnoreCase(obj.getFileType())) {
			obj.setDownloadFlag(false);
		} 

		return obj;
	}

	private String fileTypeName(String fileType) {
		String respFileType = null;
		switch (fileType) {

			case "Business_Unit_code" :
				respFileType = "Business Unit Code";
				break;

			case "Document_type" :
				respFileType = "Document Type";
				break;

			case "Supply_Type" :
				respFileType = "Supply Type";
				break;

			case "GL_Code_Mapping_Master_GL" :
				respFileType = "GL Code Mapping Master - GL";
				break;

			case "Tax_code" :
				respFileType = "Tax Code";
				break;

			case "GL_dump_mapping_file" :
				respFileType = "GL dump - mapping file";
				break;
			default :
				respFileType = fileType;
				break;

		}
		return respFileType;
	}

}