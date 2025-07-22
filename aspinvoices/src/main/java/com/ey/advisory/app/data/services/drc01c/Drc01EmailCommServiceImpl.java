package com.ey.advisory.app.data.services.drc01c;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.drc.DrcCommRespDto;
import com.ey.advisory.app.data.services.drc.DrcGetReminderFrequencyRespDto;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Drc01EmailCommServiceImpl")
public class Drc01EmailCommServiceImpl implements Drc01EmailCommService {

	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<DrcCommRespDto> getDRC1CommDetails(
			List<String> gstinList, DrcGetReminderFrequencyRespDto reqDto) {

		List<DrcCommRespDto> respList = null;
		String commType = reqDto.getCommType();
	
		try {
			String condtion = queryCondition(reqDto, commType);
			String queryString = createQuery(condtion);

			Query q = entityManager.createNativeQuery(queryString);
			
			if (reqDto.getFromTaxPeriod() != null) {
				q.setParameter("fromTaxPeriod", reqDto.getFromTaxPeriod());
			}

			if (reqDto.getToTaxPeriod() != null) {
				q.setParameter("toTaxPeriod", reqDto.getToTaxPeriod());
			}
			

			if(reqDto.getEmailType()!=null && !reqDto.getEmailType().isEmpty())
			{
				q.setParameter("emailType",reqDto.getEmailType());
			}
			
			q.setParameter("commType", reqDto.getCommType());
			q.setParameter("entityId", reqDto.getEntityId());
			q.setParameter("gstinList", gstinList);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data {} %s", reqDto);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			respList = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format(" Drc01EmailCommServiceImpl Error Occured while executing query",
					ex);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return respList;
	}

	private String createQuery(String condition) {

		String query = " SELECT REQUEST_ID, GSTIN, TAX_PERIOD, CREATED_ON, "
				+ " REPORT_STATUS, DOC_ID, EMAIL_STATUS,EMAIL_TYPE FROM DRC01_COMM_REQUEST "
				+ " WHERE ENTITY_ID =:entityId AND COMMUNICATION_TYPE =:commType AND GSTIN IN (:gstinList) "
				+ condition + " ORDER BY REQUEST_ID DESC";

		return query;
	}

	private String queryCondition(DrcGetReminderFrequencyRespDto reqDto,
			String commType) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Drc01EmailCommServiceImpl .queryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if (reqDto.getFromTaxPeriod() != null) {
			condition1.append(
					" AND DERIVED_RETURN_PERIOD BETWEEN :fromTaxPeriod ");
		}

		if (reqDto.getToTaxPeriod() != null) {
			condition1.append(" AND :toTaxPeriod ");
		}
		
		if(reqDto.getEmailType()!=null && !reqDto.getEmailType().isEmpty())
		{
			condition1.append(" AND EMAIL_TYPE IN (:emailType) ");
		}
		return condition1.toString();
	}
	
	private DrcCommRespDto convert(Object[] arr) {
		
		DrcCommRespDto dto = new DrcCommRespDto();
		dto.setRequestId(arr[0]!=null?Integer.valueOf(arr[0].toString()):null);
		dto.setGstin(arr[1]!=null?arr[1].toString():null);
		dto.setTaxPeriod(arr[2]!=null?arr[2].toString():null);
		dto.setEmailTime(arr[3]!=null?arr[3].toString():null);
		dto.setReportStatus(arr[4]!=null?arr[4].toString():null);
		dto.setDownload(arr[5]!=null?true:false);
		dto.setEmailStatus(arr[6]!=null?arr[6].toString():null);
		dto.setEmailType(arr[7]!=null?arr[7].toString():null);
		
		return dto;
	}

}