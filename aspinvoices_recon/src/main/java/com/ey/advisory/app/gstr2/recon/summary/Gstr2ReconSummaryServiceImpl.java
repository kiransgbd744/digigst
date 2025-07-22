package com.ey.advisory.app.gstr2.recon.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ReconSummaryServiceImpl")
public class Gstr2ReconSummaryServiceImpl implements Gstr2ReconSummaryService {

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	private Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	private Gstr2ReconGstinRepository gstinDetails;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinIdentifier;

	@Autowired
	@Qualifier("Gstr2ReconSummaryDaoImpl")
	private Gstr2ReconSummaryDao dao;

	@Override
	public Gstr2ReconSummaryMasterDto getReconSummary(Long configId,
			List<String> gstin, List<String> returnPeriod, String reconType) {

		Gstr2ReconSummaryMasterDto resp = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Inside Gstr2ReconSummaryServiceImpl.getReconSummary()"
							+ "method{} configId %d : ",
					configId);
		}

		try {
			Gstr2ReconConfigEntity findByConfigId = reconConfigRepo
					.findByConfigId(configId);

			String fromTaxPeriod2A = findByConfigId.getFromTaxPeriod2A() != null
					? findByConfigId.getFromTaxPeriod2A().toString()
					: null;

			String toTaxPeriod2A = findByConfigId.getToTaxPeriod2A() != null
					? findByConfigId.getToTaxPeriod2A().toString()
					: null;
					
			String fromTaxPeriodPR = findByConfigId.getFromTaxPeriodPR() != null
					? findByConfigId.getFromTaxPeriodPR().toString()
					: findByConfigId.getFromDocDate().toString();

			String toTaxPeriodPR = findByConfigId.getToTaxPeriodPR() != null
					? findByConfigId.getToTaxPeriodPR().toString()
					: findByConfigId.getToDocDate().toString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"findByConfigId() method{} configId %d ,"
								+ " fromTaxPeriod2A  %s , toTaxPeriod2A %s :  "
								+ "fromTaxPeriodPR  %s , toTaxPeriodPR %s : ",
						configId, fromTaxPeriod2A, toTaxPeriod2A,
						fromTaxPeriodPR, toTaxPeriodPR);
				LOGGER.debug(msg);
			}

			List<String> gstinList = gstinDetails
					.findAllGstinsByConfigId(configId);
			
			List<GSTNDetailEntity> gstinType = gstinIdentifier
					.findRegTypeByGstinList(gstinList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" findAllGstinsByConfigId() {} ",
						gstinList);
			}
			List<GstinDto> gstins = gstinType.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			gstins.sort(Comparator.comparing(GstinDto::getGstin));

			List<Gstr2ReconSummaryDto> reconsummaryDto = dao
					.findReconSummary(configId, gstin, returnPeriod, reconType);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"findReconSummary() method{}  ",
						reconsummaryDto);
				LOGGER.debug(msg);
			}

			resp = new Gstr2ReconSummaryMasterDto(reconsummaryDto, gstins,
					toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
					fromTaxPeriodPR);

		} catch (Exception ex) {
			ex.printStackTrace();
			String msg = String.format(
					"Error occured : findReconSummary()  configId %d ",
					configId);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Before return Gstr2ReconSummaryServiceImpl"
					+ ".getReconSummary() method{}");
		}

		return resp;

	}

	private GstinDto convert(GSTNDetailEntity o) {
		GstinDto entity = new GstinDto();

		entity.setGstin(o.getGstin());
		entity.setGstinIdentifier(o.getRegistrationType());
		return entity;
	}

}
