package com.ey.advisory.app.services.configuremaster.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.views.client.MasterErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service("masterErrorReportsDao")
public class MasterErrorReportsDaoImpl implements MasterErrorReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManger;

	@Override
	public List<MasterErrorRecordsDto> getMasterError(
	        Anx1FileStatusReportsReqDto request) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT error_record,error_code,error_description ");
		sql.append("FROM MASTER_ERROR WHERE file_name=:fileName");
		Query q = entityManger.createNativeQuery(sql.toString());
		q.setParameter("fileName", request.getFileName());
		List<Object[]> list = q.getResultList();

		return list.parallelStream().map(o -> converError(o))
		        .collect(Collectors.toCollection(ArrayList::new));
	}

	private MasterErrorRecordsDto converError(Object[] obj) {
		MasterErrorRecordsDto recordsDto = new MasterErrorRecordsDto();
		recordsDto.setErrorRecord(obj[0] != null ? obj[0].toString() : null);
		recordsDto.setErrorCode(obj[1] != null ? obj[1].toString() : null);
		recordsDto
		        .setErrorDescription(obj[2] != null ? obj[2].toString() : null);
		return recordsDto;
	}
}
