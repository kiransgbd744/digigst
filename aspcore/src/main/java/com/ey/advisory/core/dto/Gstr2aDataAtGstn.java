package com.ey.advisory.core.dto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr2aDataAtGstn {

	public Long findGstr2aDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonRequest);

}
