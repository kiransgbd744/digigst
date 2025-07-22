package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import org.javatuples.Pair;

/**
 * 
 * @author vishal.verma
 *
 */

public interface InwardEinvoiceInvMngtService {
	public Pair<List<InwardEinvoiceInvMngtRespDto>,Integer> findTableData(
			InwardEinvoiceInvMngtReqDto dto,int pageNum, int pageSize);
	
	public InwardEinvoiceInvMngtTabLayoutRespDto findTabLayoutData(String irn);

}
