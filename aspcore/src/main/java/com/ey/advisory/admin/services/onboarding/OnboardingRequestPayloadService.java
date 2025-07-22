/**
 * 
 */
package com.ey.advisory.admin.services.onboarding;

import java.sql.Clob;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.OnboardingRequestPayloadEntity;
import com.ey.advisory.admin.data.repositories.client.OnboardingRequestPayloadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("OnboardingRequestPayloadService")
public class OnboardingRequestPayloadService {
	
	@Autowired
	@Qualifier("OnboardingRequestPayloadRepository")
	private OnboardingRequestPayloadRepository payloadRepo;
	
	
	public Long savePayoadRequest(String jsonReq, Integer entityId, String category) {

		OnboardingRequestPayloadEntity entity = new OnboardingRequestPayloadEntity();

		Clob reqPaylaod;
		try {
			reqPaylaod = new javax.sql.rowset.serial.SerialClob(
					jsonReq.toCharArray());

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()
										: "SYSTEM";

				if (userName == null || userName.isEmpty()) {
					userName = "SYSTEM";
				}

				entity.setCreatedBy(userName);
				entity.setCreatedOn(LocalDateTime.now());
				entity.setEntityId(entityId);
				//entity.setErrorMsg(errorMsg);
				entity.setReqPaylaod(reqPaylaod);
				//entity.setRespPaylaod(respPaylaod);
				entity.setCategory(category);
				OnboardingRequestPayloadEntity savedEntity = payloadRepo.save(entity);
				return savedEntity.getId();

			
		} catch (Exception e) {
			String msg = "Exception occured in OnboardingRequestPayloadService";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

}
