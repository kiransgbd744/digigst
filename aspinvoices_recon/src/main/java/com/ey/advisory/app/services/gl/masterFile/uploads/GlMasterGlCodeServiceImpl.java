package com.ey.advisory.app.services.gl.masterFile.uploads;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMasterEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMasterRepo;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Kiran s
 *
 */
@Component("GlMasterGlCodeServiceImpl")
@Slf4j
public class GlMasterGlCodeServiceImpl {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	// @Qualifier("GlCodeMasterRepo")
	GlCodeMasterRepo glCodeMasterRepo;

	public void convertGlCodeMasterSheetDataToList(Object[][] objList,
			int columnCount, String fileType, String fileId) {
		List<GlCodeMasterEntity> validEntityList = new ArrayList<>();
		List<GlCodeMasterEntity> invalidEntityList = new ArrayList<>();

		if (objList == null || objList.length == 0) {
			throw new RuntimeException(
					"Data/Value in any one of the columns is mandatory.");
		}

		for (Object[] obj : objList) {
			GlCodeMasterEntity entity = new GlCodeMasterEntity();

			entity.setCgstTaxGlCode(getNullableBigDecimal(obj[0]));
			entity.setSgstTaxGlCode(getNullableBigDecimal(obj[1]));
			entity.setIgstTaxGlCode(getNullableBigDecimal(obj[2]));
			entity.setUgstTaxGlCode(getNullableBigDecimal(obj[3]));
			entity.setCompensationCessGlCode(getNullableBigDecimal(obj[4]));
			entity.setKeralaCessGlCode(getNullableBigDecimal(obj[5]));
			entity.setRevenueGls(getNullableBigDecimal(obj[6]));
			entity.setExpenceGls(getNullableBigDecimal(obj[7]));
			entity.setExchangeRate(getNullableBigDecimal(obj[8]));
			entity.setDiffGl(getNullableBigDecimal(obj[9]));
			entity.setExportGl(getNullableBigDecimal(obj[9])); // assuming
																// intentional
																// duplication
			entity.setForexGlsPor(getNullableBigDecimal(obj[10]));
			entity.setTaxableAdvanceLiabilityGls(
					getNullableBigDecimal(obj[11]));
			entity.setNonTaxableAdvanceLiabilityGls(
					getNullableBigDecimal(obj[12]));
			entity.setCcAndStGls(getNullableBigDecimal(obj[13]));
			entity.setUnbilledRevenueGls(getNullableBigDecimal(obj[14]));
			entity.setBankAccGls(getNullableBigDecimal(obj[15]));
			entity.setInputTaxGls(getNullableBigDecimal(obj[16]));
			entity.setFixedAssetGls(getNullableBigDecimal(obj[17]));

			entity.setCreatedDate(LocalDateTime.now());
			entity.setUpdatedDate(LocalDateTime.now());
			entity.setFileId(fileId != null ? fileId.toString() : null);
			entity.setFileType(fileType);

			boolean isAllFieldsEmpty = true;
			for (int i = 0; i <= 17; i++) {
				if (!isNullOrEmpty(obj[i])) {
					isAllFieldsEmpty = false;
					break;
				}
			}

			if (isAllFieldsEmpty) {
				entity.setIsActive(false);
				entity.setErrorCode("ERGL1005");
				entity.setErrorDesc(
						"Data/Value in any one of the columns is mandatory.");
				invalidEntityList.add(entity);
			} else {
				entity.setIsActive(true);
				validEntityList.add(entity);
			}
		}

		// Save invalid and throw error
		if (!invalidEntityList.isEmpty()) {
			glCodeMasterRepo.saveAll(invalidEntityList);
			throw new RuntimeException(
					"Data/Value in any one of the columns is mandatory.");
		}

		// Save only valid entries (no soft deletion)
		if (!validEntityList.isEmpty()) {
			glCodeMasterRepo.saveAll(validEntityList);
		}
	}

	private boolean isNullOrEmpty(Object obj) {
		return obj == null || obj.toString().trim().isEmpty();
	}

	private BigDecimal getBigDecimal(Object obj) {
		if (obj == null || obj.toString().trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(obj.toString().trim());
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid decimal value: " + obj);
		}
	}
	
	

	private BigDecimal getNullableBigDecimal(Object obj) {
	    if (isNullOrEmpty(obj)) {
	        return null;
	    }
	    try {
	        return new BigDecimal(obj.toString().trim());
	    } catch (NumberFormatException e) {
	        throw new RuntimeException("Invalid decimal value: " + obj);
	    }
	}


}
