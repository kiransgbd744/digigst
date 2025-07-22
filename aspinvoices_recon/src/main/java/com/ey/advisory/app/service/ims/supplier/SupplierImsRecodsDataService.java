package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 
 *  @author ashutosh.kar
 *
 */

@Component("SupplierImsRecodsDataService")
public interface SupplierImsRecodsDataService {

	Pair<List<SupplierImsRecordDataResponseDto>, Integer> getSupplierImsRecodsScreenData(SupplierImsEntityReqDto reqDto,
			Pageable pageReq);

}
