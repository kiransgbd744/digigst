/**
 * 
 */
package com.ey.advisory.app.service.ims;

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
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ImsRecodsDataServiceImpl")
public class ImsRecodsDataServiceImpl implements ImsRecodsDataService {
	
	@Autowired
	@Qualifier("ImsRecodsDataDaoImpl")
	private ImsRecodsDataDao imsRecordDataDao;
	
	@Override
	 public Pair<List<ImsRecordDataResponseDto>,Integer> getImsRecodsScreenData(
				ImsRecordDataRequestDto reqDto, Pageable pageReq) {

		LOGGER.debug("Inside ImsRecodsDataServiceImpl.getImsRecodsScreenData() "
				+ "methode reqDto {} :", reqDto.toString());
		
		List<ImsRecordDataResponseDto> respList = new ArrayList<>();
		Integer totalCount = 0;

		 Pair<List<ImsRecordDataResponseDto>,Integer> resp = new Pair<>(respList, totalCount);

		try {
			resp = imsRecordDataDao.findImsRecodsScreenData(reqDto, pageReq);

		} catch (Exception e) {
			LOGGER.error("Exception occured in ImsRecodsDataServiceImpl {} :",
					e);
			throw new AppException(e);
		}

		return resp;
	}

}
