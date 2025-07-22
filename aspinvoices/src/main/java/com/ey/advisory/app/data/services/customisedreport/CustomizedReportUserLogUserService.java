/**
 * 
 */
package com.ey.advisory.app.data.services.customisedreport;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("CustomizedReportUserLogUserService")
public class CustomizedReportUserLogUserService {

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	public CustomizedReportUserLogUserDto getData(String reportType, 
				Long entityId, String userName) {
		
		CustomizedReportUserLogUserDto dto = new CustomizedReportUserLogUserDto();

		try {
		
		Optional<CustomisedFieldSelEntity> entity = custFieldSeleRepo
				.findByReportTypeAndEntityIdAndIsActiveTrue(reportType, entityId);

		LocalDateTime createdOn = entity.get().getCreatedOn();

		LocalDateTime istDateTimeFromUTC = EYDateUtil.toISTDateTimeFromUTC(createdOn);

		String[] dateTime = istDateTimeFromUTC.toString().split("T");
		String dateAndTime = dateTime[0] + " | " + dateTime[1].substring(0, 8);

		UserCreationEntity userEntity = repo.findUserEntityByUserName(userName);

		dto.setEmailId(userEntity.getEmail());
		dto.setTimeStamp(dateAndTime);
		}catch(Exception ex) {
		LOGGER.error("Error while getting data from "
				+ "CustomizedReportUserLogUserService.getData()");
		throw new AppException(ex);
		}
		return dto;

	}

}
