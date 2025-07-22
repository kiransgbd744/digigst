package com.ey.advisory.app.services.jobs.ret;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.dto.RetGetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */

public interface RetInvoicesAtGstn {
	public ResponseEntity<String> findInvFromGstn(RetGetInvoicesReqDto dto,
			String groupCode);
}
