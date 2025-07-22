/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Hemasundar.J
 *
 */
public interface Anx1InvoicesAtGstn {

	public ResponseEntity<String> findInvFromGstn(
			Anx1GetInvoicesReqDto dto, String groupCode, String type);
}
