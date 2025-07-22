package com.ey.advisory.app.common;

import org.springframework.stereotype.Component;

/**
 * @author Saif.S
 *
 */
@Component("SendEmailService")
public interface SendEmailService {

	boolean sendEmail(GstrEmailDetailsDto emailDto);

}
