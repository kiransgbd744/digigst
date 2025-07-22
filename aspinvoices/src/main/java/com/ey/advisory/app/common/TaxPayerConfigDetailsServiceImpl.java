/**
 * 
 */
package com.ey.advisory.app.common;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;
import com.ey.advisory.app.docs.dto.TaxPayerConfigDto;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 *
 */
@Slf4j
@Service("TaxPayerConfigDetailsServiceImpl")
public class TaxPayerConfigDetailsServiceImpl
		implements TaxPayerConfigDetailsService {

	@Autowired
	@Qualifier("TaxPayerConfigDetailsDaoImpl")
	TaxPayerConfigDetailsDao taxPayerConfigDao;

	@Override
	public List<TaxPayerConfigDto> getTaxPayerConfigDetails(
			boolean einvApplicable) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Getting TaxPayerConfigDetailsServiceImpl "
					+ "TaxPayerConfig" + " Details";
			LOGGER.debug(msg);
		}
		List<TaxPayerConfigDto> returnFinalDto = new ArrayList<>();
		// Getting the Data Fro, Gstin Db and Converting to final Dto with
		// Required Fields

		List<GstinValidatorEntity> configResult = taxPayerConfigDao
				.getTaxPayerConfigDetailsFromDb(einvApplicable);
		if (!configResult.isEmpty()) {
			configResult.forEach(obj -> {
				TaxPayerConfigDto returnObj = convertToDtoObj(obj);
				returnFinalDto.add(returnObj);
			});
		}
		return returnFinalDto;

	}

	public TaxPayerConfigDto convertToDtoObj(GstinValidatorEntity obj) {
		TaxPayerConfigDto resp = new TaxPayerConfigDto();

		String dateOfUpload = EYDateUtil
				.toISTDateTimeFromUTC(obj.getDateOfUpload()).toString();
		resp.setDateOfUpload(
				obj.getDateOfUpload() != null
						? (dateOfUpload.length() > 19
								? dateOfUpload.split("\\.")[0] : dateOfUpload)
						: null);

		resp.setFileName(obj.getFileName());
		resp.setNoOfGstins(obj.getNoOfGstins());
		resp.setRequestId(obj.getRequestId());
		resp.setStatus(obj.getStatus());
		resp.setUploadedBy(obj.getCreatedBy());
		return resp;
	}

}
