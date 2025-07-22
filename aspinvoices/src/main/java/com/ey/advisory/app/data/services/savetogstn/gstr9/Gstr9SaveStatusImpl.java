package com.ey.advisory.app.data.services.savetogstn.gstr9;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9SaveStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9SaveStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.data.services.gstr9.Gstr9InwardUtil;
import com.ey.advisory.app.docs.dto.gstr9.Gst9SaveStatusDto;
import com.ey.advisory.app.docs.dto.gstr9.Gst9StatusTimeStampsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr9SaveStatusImpl")
public class Gstr9SaveStatusImpl implements Gstr9SaveStatus {

	@Autowired
	private Gstr9SaveStatusRepository gstr9SaveStatusRepository;

	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	private Gstr9HsnProcessedRepository gstr9HsnProcessedRepository;

	@Override
	public List<Gst9SaveStatusDto> getSaveStatus(String gstin, String fy) {

		List<Gst9SaveStatusDto> dtoList = null;
		try {
			String retPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);

			List<Gstr9SaveStatusEntity> gstr9SaveStatusEntityList = gstr9SaveStatusRepository
					.findByGstinAndTaxPeriodOrderByIdDesc(gstin, retPeriod);

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			dtoList = gstr9SaveStatusEntityList.stream()
					.map(o -> convertEntityToList(o, formatter))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception e) {
			String msg = "Unexpected error while retriving Gstr9 Save Status ";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

		return dtoList;
	}

	private Gst9SaveStatusDto convertEntityToList(
			Gstr9SaveStatusEntity gstr9SaveStatusEntity,
			DateTimeFormatter formatter) {
		Gst9SaveStatusDto dto = new Gst9SaveStatusDto();
		dto.setId(BigInteger.valueOf(gstr9SaveStatusEntity.getId()));
		dto.setRefId(gstr9SaveStatusEntity.getRefId());
		dto.setErrorCnt(
				BigInteger.valueOf(gstr9SaveStatusEntity.getErrorCount()));

		LocalDateTime createdTimeist = EYDateUtil
				.toISTDateTimeFromUTC(gstr9SaveStatusEntity.getCreatedOn());
		dto.setCreatedTime(formatter.format(createdTimeist));

		dto.setStatus(gstr9SaveStatusEntity.getStatus());
		return dto;
	}

	@Override
	public void downloadSaveStatusDetails(Long id,
			HttpServletResponse response) {

		try {
			Optional<Gstr9SaveStatusEntity> entity = gstr9SaveStatusRepository
					.findById(id);
			if (!entity.isPresent())
				return;

			String fileName = entity.get().getFilePath();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr9 Filename to be downloaded : %s ", fileName);
				LOGGER.debug(msg);
			}

			Document document = DocumentUtility.downloadDocument(fileName,
					APIConstants.GSTR9_SAVE_ERROR_FOLDER);

			if (document == null)
				return;
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr9 Document is available in the Repo, Doc Name is %s",
						fileName);
				LOGGER.debug(msg);
			}
			InputStream inputStream = document.getContentStream().getStream();
			response.setContentType("application/json");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename =%s ", fileName));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			String msg = " Error while downloading Gstr9 error json. ";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	@Override
	public Gst9StatusTimeStampsDto getStatusTimeStamps(String gstin,
			String fy) {
		Gst9StatusTimeStampsDto dto = new Gst9StatusTimeStampsDto();

		try {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			String taxPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);

			Optional<Gstr9SaveStatusEntity> e = gstr9SaveStatusRepository
					.findFirstByGstinAndTaxPeriodOrderByIdDesc(gstin,
							taxPeriod);

			if (e.isPresent()) {
				Gstr9SaveStatusEntity entity = e.get();
				LocalDateTime saveToGstnTimeStamp = EYDateUtil
						.toISTDateTimeFromUTC(entity.getUpdatedOn());
				dto.setSaveToGstnTimeStamp(
						formatter.format(saveToGstnTimeStamp));
			}

			Optional<GstnUserRequestEntity> optionalUserRequest = gstnUserRequestRepo
					.findTop1ByGstinAndTaxPeriodAndRequestTypeAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
							gstin, taxPeriod, "GET", "GSTR9");

			if (optionalUserRequest.isPresent()) {
				GstnUserRequestEntity lastUserRequest = optionalUserRequest
						.get();
				LocalDateTime updateGstnTime = EYDateUtil
						.toISTDateTimeFromUTC(lastUserRequest.getCreatedOn());
				dto.setUpdateGstnTimeStamp(formatter.format(updateGstnTime));
			}

			boolean isEligibleForSave = isActiveRecordsAvailableForSave(gstin,
					taxPeriod);

			dto.setDataAvlble(isEligibleForSave);

		} catch (Exception e) {

			String msg = "Error while fetching status timestamps";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

		return dto;
	}

	private boolean isActiveRecordsAvailableForSave(String gstin,
			String retPeriod) {

		Long activeUserUnputCount = gstr9UserInputRepository
				.findActiveCounts(gstin, retPeriod);

		Long activeHsnCount = gstr9HsnProcessedRepository
				.findActiveHsnCounts(gstin, retPeriod);

		return activeUserUnputCount > 0 || activeHsnCount > 0;

	}

}
