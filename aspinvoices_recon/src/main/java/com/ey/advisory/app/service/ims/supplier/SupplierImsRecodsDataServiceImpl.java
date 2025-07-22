package com.ey.advisory.app.service.ims.supplier;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Slf4j
@Component("SupplierImsRecodsDataServiceImpl")
public class SupplierImsRecodsDataServiceImpl implements SupplierImsRecodsDataService {
	
	@Autowired
	@Qualifier("SupplierImsRecodsDataDaoImpl")
	private SupplierImsRecodsDataDao supplierImsRecordDataDao;

	@Override
	public Pair<List<SupplierImsRecordDataResponseDto>, Integer> getSupplierImsRecodsScreenData(
			SupplierImsEntityReqDto reqDto, Pageable pageReq) {

		LOGGER.debug(
				"Inside SupplierImsRecodsDataServiceImpl.getSupplierImsRecodsScreenData() " + "methode reqDto {} :",
				reqDto.toString());

		List<SupplierImsRecordDataResponseDto> respList = new ArrayList<>();
		Integer totalCount = 0;

		Pair<List<SupplierImsRecordDataResponseDto>, Integer> resp = new Pair<>(respList, totalCount);

		try {
			resp = supplierImsRecordDataDao.findSupplierImsRecodsScreenData(reqDto, pageReq);

		} catch (Exception e) {
			LOGGER.error("Exception occured in SupplierImsRecodsDataServiceImpl {} :", e);
			throw new AppException(e);
		}

		return resp;
	}

}
