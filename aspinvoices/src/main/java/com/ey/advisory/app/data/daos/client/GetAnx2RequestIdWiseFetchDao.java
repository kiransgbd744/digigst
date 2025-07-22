package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.GetAnx2RequestIdWiseReqDto;
import com.ey.advisory.app.docs.dto.anx1.GetAnx2RequestIdWiseRespDto;

public interface GetAnx2RequestIdWiseFetchDao {

	List<GetAnx2RequestIdWiseRespDto> getAnx2DetailsByRequestId(
			GetAnx2RequestIdWiseReqDto idWiseReqDto) throws Exception;

}
