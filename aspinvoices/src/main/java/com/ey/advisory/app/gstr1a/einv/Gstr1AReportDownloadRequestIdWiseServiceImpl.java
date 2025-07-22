package com.ey.advisory.app.gstr1a.einv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ASubmittedReconConfigRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvRequesIdWiseDownloadTabDto;
import com.ey.advisory.app.gstr1.einv.Gstr1PrVsSubmReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr1AReportDownloadRequestIdWiseServiceImpl")
public class Gstr1AReportDownloadRequestIdWiseServiceImpl
		implements Gstr1AReportDownloadRequestIdWiseService {

	@Autowired
	@Qualifier("Gstr1APrVsSubmittedRequestIdWiseDaoImpl")
	private Gstr1APrVsSubmittedRequestIdWiseDao requestStatusDao;

	@Autowired
	@Qualifier("Gstr1ASubmittedReconConfigRepository")
	Gstr1ASubmittedReconConfigRepository gstr1ASubmReconrepo;

	@Override
	public List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1AReportDownloadRequestIdWiseServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
		}

		List<Gstr1PrVsSubmReconReportRequestStatusDto> response = requestStatusDao
				.getPrVsSubReportRequestStatus(reqDto, userName);

		Collector<Gstr1PrVsSubmReconReportRequestStatusDto, ?, Gstr1PrVsSubmReconReportRequestStatusDto> collector2 = Collectors
				.reducing(new Gstr1PrVsSubmReconReportRequestStatusDto(),
						(Irr1, Irr2) -> mergePrSubmDtos(Irr1, Irr2));

		Map<Object, Gstr1PrVsSubmReconReportRequestStatusDto> responseList = response
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<Gstr1PrVsSubmReconReportRequestStatusDto> res = new ArrayList<>(
				responseList.values());
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1AReportDownloadRequestIdWiseServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestStatus() method";
			LOGGER.debug(msg);
		}
		return res;
	}

	private Gstr1PrVsSubmReconReportRequestStatusDto mergePrSubmDtos(
			Gstr1PrVsSubmReconReportRequestStatusDto irr1,
			Gstr1PrVsSubmReconReportRequestStatusDto irr2) {

		Gstr1PrVsSubmReconReportRequestStatusDto irrs = new Gstr1PrVsSubmReconReportRequestStatusDto();

		if (irr1.getGstins() == null || irr1.getGstins().isEmpty()) {
			irrs.setGstins(irr2.getGstins());

		} else {
			irr1.getGstins().addAll(irr2.getGstins());
			irrs.setGstins(irr1.getGstins());

		}
		irrs.setCompletionOn(irr2.getCompletionOn());
		irrs.setInitiatedBy(irr2.getInitiatedBy());
		irrs.setInitiatedOn(irr2.getInitiatedOn());
		irrs.setRequestId(irr2.getRequestId());
		irrs.setStatus(irr2.getStatus());
		irrs.setPath(irr2.getPath());
		irrs.setFromTaxPeriod(irr2.getFromTaxPeriod());
		irrs.setToTaxPeriod(irr2.getToTaxPeriod());
		irrs.setGstinCount(irr1.getGstinCount() + 1);
		return irrs;
	}

	@Override
	public List<Gstr1EinvRequesIdWiseDownloadTabDto> getPrVsSubmDownloadData(
			Long configId) {
		List<Gstr1EinvRequesIdWiseDownloadTabDto> list = new ArrayList<>();

		String filePath = gstr1ASubmReconrepo.getPrVsSubmDataList(configId);
		Gstr1EinvRequesIdWiseDownloadTabDto gstr1ReqIdWiseDownloDto = new Gstr1EinvRequesIdWiseDownloadTabDto();
		gstr1ReqIdWiseDownloDto.setFlag(false);
		gstr1ReqIdWiseDownloDto.setPath(filePath);
		gstr1ReqIdWiseDownloDto
				.setReportName("GSTR1A Processed Records Vs Submitted Data");
		if (filePath != null)
			gstr1ReqIdWiseDownloDto.setFlag(true);

		list.add(gstr1ReqIdWiseDownloDto);
		return list;
	}
}
