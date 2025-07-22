package com.ey.advisory.app.gstr3b;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Table4d1ValidationStatusEntity;
import com.ey.advisory.app.data.repositories.client.Table4d1ValidationStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author akhilesh.yadav
 *
 */
@Slf4j
@Component("Table4d1ValidationtSaveUpdateServiceImpl")
public class Table4d1ValidationtSaveUpdateServiceImpl
		implements Table4d1ValidationtSaveUpdateService {

	@Autowired
	@Qualifier("Table4d1ValidationStatusRepository")
	private Table4d1ValidationStatusRepository validationRepo;

	@Override
	public Table4d1ValidationStatusDto saveValidationStatusInput(String gstin,
			String taxPeriod, Table4d1ValidationStatusDto validationInput) {

		Table4d1ValidationStatusEntity validationEntity = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Table4d1ValidationtSaveUpdateServiceImpl"
						+ ".saveValidationStatusInput begin, for gstin : "
						+ gstin + " and taxPeriod : " + " " + taxPeriod);
			}

			validationEntity = convertToEntity(gstin, taxPeriod,
					validationInput);

			validationEntity = validationRepo.save(validationEntity);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Table4d1ValidationtSaveUpdateServiceImpl"
						+ ".saveValidationStatusInput Saved validationInput "
						+ "Successfully ");
			}
		} catch (Exception ee) {
			String msg = "Error while saving Gstr3b table 4(b)(2)"
					+ " Other reversal changes to User Input table";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
		return convertToDto(validationEntity);
	}

	@Override
	public boolean isValidated(String gstin, String taxPeriod) {
		boolean flag = false;

		Table4d1ValidationStatusEntity entity = validationRepo
				.findByGstinAndTaxPeriodAndIsActive(gstin, taxPeriod);
		if (entity != null)
			flag = true;
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Table4d1ValidationtSaveUpdateServiceImpl"
					+ "isValidated "+ flag);
		}
		

		return flag;
	}

	@Override
	public int update4D1ValidationStatus(String gstin, String taxPeriod) {

		int updateCount = 0;

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		LocalDateTime utcDateTimeFromLocal = LocalDateTime.now();

		try {

			updateCount = validationRepo.updateActiveFlag(gstin, taxPeriod,
					utcDateTimeFromLocal, userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Table4d1ValidationtSaveUpdateServiceImpl"
						+ ".update4D1ValidationStatus Updated validationInput "
						+ "Successfully ");
			}

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);

		}
		return updateCount;
	}

	@Override
	public Table4d1ValidationStatusDto get4D1ValidationStatus(String gstin,
			String taxPeriod) {

		Table4d1ValidationStatusDto gstr3bValidationResp = new Table4d1ValidationStatusDto();

		try {

			Table4d1ValidationStatusEntity entity = validationRepo
					.findByGstinAndTaxPeriodAndIsActive(gstin, taxPeriod);
			gstr3bValidationResp = convertToDto(entity);

			if (entity == null) {
				gstr3bValidationResp.setGstin(gstin);
				gstr3bValidationResp.setTaxPeriod(taxPeriod);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Table4d1ValidationtSaveUpdateServiceImpl"
						+ ".get4D1ValidationStatus fetch validationInput "
						+ "Successfully ");
			}

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);

		}
		return gstr3bValidationResp;

	}

	private Table4d1ValidationStatusDto convertToDto(
			Table4d1ValidationStatusEntity obj) {

		Table4d1ValidationStatusDto dto = new Table4d1ValidationStatusDto();
		if (obj != null) {
			dto.setGstin(obj.getGstin());
			dto.setTaxPeriod(obj.getTaxPeriod());
			dto.setValidationFlag(obj.getValidationFlag());
			dto.setErrorFlag(obj.getErrorFlag());
			dto.setIsActive(obj.getIsActive());
		}

		return dto;
	}

	private Table4d1ValidationStatusEntity convertToEntity(String gstin,
			String taxPeriod, Table4d1ValidationStatusDto validationInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Table4d1ValidationtSaveUpdateServiceImpl"
					+ ".convertToEntity method :");
		}

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		Table4d1ValidationStatusEntity vStatusEntity = new Table4d1ValidationStatusEntity();

		vStatusEntity.setGstin(gstin);
		vStatusEntity.setTaxPeriod(taxPeriod);
		vStatusEntity.setValidationFlag(validationInput.getValidationFlag());
		vStatusEntity.setErrorFlag(validationInput.getErrorFlag());
		vStatusEntity.setIsActive(true);
		vStatusEntity.setCreatedOn(LocalDateTime.now());
		vStatusEntity.setCreatedBy(userName);
		// vStatusEntity.setUpdatedOn(null);
		// vStatusEntity.setUpdatedBy("");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Table4d1ValidationtSaveUpdateServiceImpl"
					+ ".convertToEntity, before returning "
					+ "Table4d1ValidationStatusEntity vStatusEntity :"
					+ vStatusEntity);
		}

		return vStatusEntity;

	}

}
