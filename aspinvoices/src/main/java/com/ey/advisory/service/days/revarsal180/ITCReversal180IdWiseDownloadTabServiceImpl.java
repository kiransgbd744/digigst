/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.ITCReversal180ComputeRptDownloadRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishl.verma
 *
 */

@Slf4j
@Component("ITCReversal180IdWiseDownloadTabServiceImpl")
public class ITCReversal180IdWiseDownloadTabServiceImpl
		implements ITCReversal180IdWiseDownloadTabService {

	@Autowired
	@Qualifier("ITCReversal180ComputeRptDownloadRepository")
	ITCReversal180ComputeRptDownloadRepository repo;

	@Override
	public List<ITCReversal180DownloadTabDto> getDownloadData(
			Long computeId) {

		List<ITCReversal180DownloadTabDto> resp = new ArrayList<>();

		String msg = String.format(
				"Inside ITCReversal180IdWiseDownloadTabServiceImpl"
						+ ".getDownloadData() method, computId = %d",
				computeId);
		LOGGER.debug(msg);
		try {

			List<ITCReversal180ComputeRptDownloadEntity> dataList = repo
					.getDataList(computeId);

			resp = dataList.stream().map(o -> convertDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			msg = String.format("Error occured in "
					+ "ITCReversal180IdWiseDownloadTabServiceImpl", ex);
			LOGGER.error(msg);
			throw new AppException(ex);
		}

		return resp;
	}

	private ITCReversal180DownloadTabDto convertDto(
			ITCReversal180ComputeRptDownloadEntity o) {

		ITCReversal180DownloadTabDto dto = new ITCReversal180DownloadTabDto();
		dto.setFlag(o.getIsDownloadable());
		dto.setPath(o.getFilePath());
		dto.setReportName(o.getReportType().equalsIgnoreCase(
				"Payment Info Available - Reversal and Not Applicable")
						? "Reversal Report (Payment info. available & Reversal Applicable + Not Applicable)"
						: o.getReportType());

		return dto;
	}

}
