package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
/**
 *  SriBhavya
 */
public interface GetGstr6aDetailStatusFetchDao {
	
	public List<Object[]> findDataByCriteria(
			GetAnx2DetailStatusReqDto criteria);

}
