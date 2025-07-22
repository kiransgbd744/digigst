package com.ey.advisory.app.ims.handlers;


import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Sakshi.jain
 *
 */
public interface ImsInvoicesProcCallService {

	public void procCall(Gstr1GetInvoicesReqDto dto, Long batchId);
	
}
