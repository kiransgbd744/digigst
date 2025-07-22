package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author ashutosh.kar
 *
 */

public interface SupplierImsRecodsDataDao {

	public  Pair<List<SupplierImsRecordDataResponseDto>, Integer> findSupplierImsRecodsScreenData(
			SupplierImsEntityReqDto reqDto, Pageable pageReq);
}
