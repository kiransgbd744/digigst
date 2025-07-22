package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;

public interface GetGstr2aFailedStatusFetchDao {

	public List<Object[]> findDataByCriteria(
			GetAnx2DetailStatusReqDto criteria);

}
