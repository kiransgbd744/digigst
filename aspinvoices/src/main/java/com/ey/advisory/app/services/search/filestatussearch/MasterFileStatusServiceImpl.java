package com.ey.advisory.app.services.search.filestatussearch;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.FileStatusDaoImpl;
import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.docs.dto.FileStatusResponseDto;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;

@Service("masterFileStatusService")
public class MasterFileStatusServiceImpl implements MasterFileStatusService {

	@Autowired
	@Qualifier("FileStatusDaoImpl")
	private FileStatusDaoImpl fileStatusDao;

	@Override
	public List<FileStatusResponseDto> getMasterFileStatus(
			FileStatusReqDto reqDto) {
		List<FileStatusResponseDto> respDtos = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT f.ID,f.CREATED_ON AS dateOfUpload,f.CREATED_BY ");
		sql.append(
				"AS uploadedBy,f.FILE_TYPE,f.FILE_NAME,COUNT(me.MASTER_FILE_ID) ");
		sql.append("AS errorCount FROM FILE_STATUS f LEFT JOIN MASTER_ERROR ");
		sql.append("me ON f.ID = me.MASTER_FILE_ID WHERE f.FILE_TYPE IN ");
		sql.append("('PRODUCT', 'CUSTOMER','VENDOR','ITEM') AND ");
		sql.append("f.FILE_TYPE=:fileType AND f.ENTITY_ID =:entityId AND ");
		sql.append("f.RECEIVED_DATE BETWEEN :fromDate AND :toDate ");
		sql.append("GROUP BY f.RECEIVED_DATE,f.CREATED_BY,f.FILE_TYPE,");
		sql.append("f.FILE_NAME,f.ID ORDER BY f.ID DESC");
		reqDto.getEntityId();
		List<Object[]> objs = fileStatusDao.masterFileStatus(sql.toString(),
				reqDto.getDataRecvFrom(), reqDto.getDataRecvTo(),
				reqDto.getFileType().toUpperCase(), reqDto.getEntityId());
		for (Object[] obj : objs) {
			FileStatusResponseDto responseDto = new FileStatusResponseDto();

			Long id = null;
			if (obj[0] != null && !obj[0].toString().trim().isEmpty()) {
				String idStr = String.valueOf(obj[0]).trim();
				id = Long.valueOf(idStr);
			}
			
			String uploadedDate = null;
			if (obj[1] != null) {
				LocalDateTime localDateTime = ((Timestamp) obj[1])
						.toLocalDateTime();
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("yyyy-MM-dd");
				localDateTime = EYDateUtil.toISTDateTimeFromUTC(localDateTime);
				uploadedDate = formatter.format(localDateTime);
			}

			String uploadedBy = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]) : "";
			String fileType = (obj[3] != null
					&& !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]) : "";
			String fileName = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]) : "";
			Integer errorCount = 0;
			if (obj[5] != null && !obj[5].toString().trim().isEmpty()) {
				errorCount = Integer.valueOf(obj[5].toString());
			}

			responseDto.setId(id);
			responseDto.setUploadedOn(uploadedDate);
			responseDto.setUploadedBy(uploadedBy);
			responseDto.setFileType(fileType);
			responseDto.setFileName(fileName);
			responseDto.setErrorCount(errorCount);
			respDtos.add(responseDto);
		}
		return respDtos;
	}

}
