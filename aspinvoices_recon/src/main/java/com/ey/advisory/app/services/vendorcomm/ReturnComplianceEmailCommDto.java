/**
 * 
 */
package com.ey.advisory.app.services.vendorcomm;

import java.util.HashSet;
import java.util.Set;

import com.ey.advisory.app.vendorcomm.dto.EmailIdDto;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Setter
@Getter
@ToString
public class ReturnComplianceEmailCommDto {

	private String clientGstin;
	private Set<EmailIdDto> EmailIdsTo = new HashSet<>();
	private Set<EmailIdDto> EmailIdsCC = new HashSet<>();
	private String emailStatus;
	private Long requestID;
	private String updatedOn;
	private String returnType;
	
	
	public void addEmailTo(String emailId) {

		if (!Strings.isNullOrEmpty(emailId)) {

			EmailIdsTo.add(new EmailIdDto(emailId));
		}
	}

	public void addEmailCC(String emailId) {

		if (!Strings.isNullOrEmpty(emailId)) {

			EmailIdsCC.add(new EmailIdDto(emailId));
		}
	}

}
