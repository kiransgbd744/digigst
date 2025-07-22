package com.ey.advisory.app.data.services.drc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.common.GstrEmailDetailsDto;
import com.ey.advisory.app.common.SendEmailService;
import com.ey.advisory.app.data.entities.drc.TblDrc01AutoGetCallDetails;
import com.ey.advisory.app.data.repositories.client.drc.Drc01AutoGetCallDetailsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Drc01CommEmailServiceImpl")
public class Drc01CommEmailServiceImpl implements Drc01CommEmailService {

	@Autowired
	@Qualifier("Drc01AutoGetCallDetailsRepository")
	private Drc01AutoGetCallDetailsRepository drc01AutoGetCallDetailsRepo;

	@Autowired
	@Qualifier("SendEmailServiceImpl")
	private SendEmailService sendEmailService;

	@Override
	public String persistAndSendDrc01Email(List<GstrEmailDetailsDto> reqDtos,
			TblDrc01AutoGetCallDetails entity, int reminderNumber) {
		String status = null;
		for (GstrEmailDetailsDto dto : reqDtos) {
			boolean isSent = sendEmailService.sendEmail(dto);
			if (isSent) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Email is sent. Calling updateEmailData to update::%s",
							dto);
					LOGGER.debug(msg);
				}
				updateEmailData(dto, entity, reminderNumber);
				
			 status ="Success";
			} else {
				String msg = String.format(
						"Failed to send email. IsSent: %s, for dto: %s", isSent,
						dto);
				LOGGER.error(msg);
				if (reminderNumber == 0) {
					drc01AutoGetCallDetailsRepo.updateEmailTypeAndFailedEmailStatus(
							entity.getId(), "Original", reminderNumber);

				}//have to pass emailTYPE IN ALL QUERY
					if (reminderNumber==1) {
						drc01AutoGetCallDetailsRepo
								.updateEmailTypeAndFailedEmailStatus(
										entity.getId(), "Reminder 1",reminderNumber);
					} else if (reminderNumber==2) {
						drc01AutoGetCallDetailsRepo
								.updateEmailTypeAndFailedEmailStatus(
										entity.getId(), "Reminder 2",reminderNumber);
					} else if (reminderNumber==3) {
						drc01AutoGetCallDetailsRepo
								.updateEmailTypeAndFailedEmailStatus(
										entity.getId(), "Reminder 3",reminderNumber);
						
					}
					
				
				status ="Failed";
			}
		}
		return status;
	}

	private void updateEmailData(GstrEmailDetailsDto dto,
			TblDrc01AutoGetCallDetails entity, int reminderNumber) {
		try {
			//have to update basis entity id
			if (reminderNumber==0) {
				drc01AutoGetCallDetailsRepo.updateEmailTypeAndEmailStatus(
						entity.getId(), "Original", reminderNumber);

			}

				if (reminderNumber==1) {
					drc01AutoGetCallDetailsRepo.updateEmailTypeAndEmailStatus(
							entity.getId(), "Reminder 1",reminderNumber);
				} else if (reminderNumber==2) {
					drc01AutoGetCallDetailsRepo.updateEmailTypeAndEmailStatus(
							entity.getId(), "Reminder 2",reminderNumber);
				} else if (reminderNumber==3) {
					drc01AutoGetCallDetailsRepo.updateEmailTypeAndEmailStatus(
							entity.getId(), "Reminder 3",reminderNumber);
				}

			
		} catch (Exception ee) {
			String msg = String.format("Exception while Updating Drc comm "
					+ " Email Mapping Data : %s", dto);
			LOGGER.error(msg, ee);
		}
	}

}
