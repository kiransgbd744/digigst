package com.ey.advisory.app.docs.service.gstr6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.Gstr6CrossItcRequestDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.search.SearchResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr6CrossItcScreenServiceImpl")
@Slf4j
public class Gstr6CrossItcScreenServiceImpl
		implements GstrCrossItcSceenService {

	@Autowired
	@Qualifier("Gstr6CrossItcScreenDaoImpl")
	private Gstr6CrossItcScreenDao gstr6CrossItcScreenDao;

	@Override
	public SearchResult<Gstr6CrossItcRequestDto> getGstr6CrossItcvalues(
			Anx1ReportSearchReqDto reqDto) {

		try {
			LOGGER.debug(
					"Enter into service class of GSTR6 Determination class",
					Gstr6CrossItcScreenServiceImpl.class);
			SearchResult<Gstr6CrossItcRequestDto> gstr6DataResponse = gstr6CrossItcScreenDao
					.gstr6CrossItcScreenDetails(reqDto);
			LOGGER.debug("End service class of GSTR6 Determination class",
					Gstr6CrossItcScreenServiceImpl.class);
			return gstr6DataResponse;

		} catch (Exception e) {
			LOGGER.debug("Exception occuring in GSTR6 Service calss", e);
			throw new AppException(e);
		}

	}
}
