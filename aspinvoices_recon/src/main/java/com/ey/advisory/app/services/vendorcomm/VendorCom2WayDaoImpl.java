package com.ey.advisory.app.services.vendorcomm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.VendorReportDownloadDto;
import com.ey.advisory.app.vendorcomm.dto.VendorResponseDataDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.Lists;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorCom2WayDaoImpl")
public class VendorCom2WayDaoImpl implements VendorCom2WayDao {

	@Autowired
	VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Pair<List<VendorResponseDataDto>, Integer> getVendrData(
			VendorReportDownloadDto jsonDto, int pageNum, int pageLimit) {
		List<VendorResponseDataDto> vendrResp = new ArrayList<>();
		int totalRec = 0;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("inside  VendorCom2WayDaoImpl with params {} ",
						jsonDto);
			}

			StringBuffer condition = new StringBuffer();
			List<String> vendorGstins = new ArrayList<>();

			List<String> finalVendorGstins = new ArrayList<>();
			if (!Collections.isEmpty(jsonDto.getVendorGstins())) {
				for (GstinDto gstin : jsonDto.getVendorGstins()) {
					vendorGstins.add(gstin.getGstin());
				}
			} else {
				EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
						.findEntityByEntityId(
								Long.valueOf(jsonDto.getEntityId()));

				vendorGstins = vendorMasterUploadEntityRepository
						.getAllVendorGstinByRecipientPan(
								Arrays.asList(entityInfoEntity.getPan()));
			}

			if (Collections.isEmpty(vendorGstins)) {
				return null;
			} else {

				if (!Collections.isEmpty(jsonDto.getVendorPans())) {
					List<String> venPan = jsonDto.getVendorPans().stream()
							.map(GstinDto::getGstin)
							.collect(Collectors.toList());
					for (String gstin : vendorGstins) {
						String pan = gstin.substring(2, 12);

						if (venPan.contains(pan))
							finalVendorGstins.add(gstin);
					}
				} else {
					finalVendorGstins = vendorGstins;
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("final vendor gstins computed {}",
						finalVendorGstins);
			}

			if (Collections.isEmpty(finalVendorGstins)) {
				return null;
			}

			String queryStr = null;

			if (jsonDto.getStatus() != null && !jsonDto.getStatus().isEmpty())
				condition.append(" AND CHD.IS_READ IN (:isRead)");

			if (jsonDto.getRespDate() != null
					&& !jsonDto.getRespDate().isEmpty())
				condition.append(
						" AND to_varchar(CHD.UPDATED_ON,'dd-Mon-yyyy') =:respDate ");

			queryStr = createQuery(condition);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("query string created for vendor response {}",
						queryStr);
			}
			Query q = entityManager.createNativeQuery(queryStr);

			q.setParameter("vendorGstins", finalVendorGstins);

			if (jsonDto.getStatus() != null && !jsonDto.getStatus().isEmpty()) {
				q.setParameter("isRead", jsonDto.getStatus());
			}

			if (jsonDto.getRespDate() != null
					&& !jsonDto.getRespDate().isEmpty())
				q.setParameter("respDate", jsonDto.getRespDate());

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = q.getResultList();
			totalRec = resultList.size();

			if (Collections.isEmpty(resultList))
				return null;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("with the query string total objects obtained {}",
						resultList.size());
			}
			List<List<Object[]>> partitionList = Lists.partition(resultList,
					pageLimit);

			List<Object[]> finalResultList = partitionList.get(pageNum);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"with the query string the partitioned objects obtained {}",
						resultList.size());
			}

			if (Collections.isEmpty(finalResultList))
				return null;

			vendrResp = finalResultList.stream().map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"with the query string the partitioned objects obtained {}",
						vendrResp);
			}

		} catch (Exception e) {
			String msg = "Exception while getting the vendor response data";
			LOGGER.error(msg, e);
			throw new AppException(e, msg);
		}

		return new Pair<>(vendrResp, totalRec);
	}

	private String createQuery(StringBuffer condition) {
		return " select CHD.UPDATED_ON, CHD.VENDOR_GSTIN,HIST.VENDOR_NAME, CHD.REQUEST_ID, "
				+ " CHD.TOTAL_RECORDS, PRT.FROM_TAX_PERIOD, PRT.TO_TAX_PERIOD, "
				+ " CHD.RESPONDED_RECORDS_CNT, CHD.RESPONDED_FILE_PATH, "
				+ " CHD.TOTAL_FILE_PATH, CHD.IS_READ FROM VENDOR_COMM_REQUEST PRT "
				+ " INNER JOIN VENDOR_REQUEST_VGSTIN CHD "
				+ " ON PRT.REQUEST_ID = CHD.REQUEST_ID LEFT JOIN "
				+ " VENDOR_EMAIL_HISTORY HIST ON HIST.REQUEST_ID = CHD.REQUEST_ID AND "
				+ " HIST.VENDOR_GSTIN = CHD.VENDOR_GSTIN WHERE "
				+ " CHD.VENDOR_GSTIN IN (:vendorGstins) AND CHD.IS_RESPONDED = true "
				+ condition + " order by CHD.UPDATED_ON desc;";

	}

	private VendorResponseDataDto convertToDto(Object arr[]) {

		VendorResponseDataDto dto = new VendorResponseDataDto();
		dto.setDate(dateChange(arr[0].toString().substring(0, 19)).substring(0,
				10));
		dto.setVendorPan(arr[1].toString().substring(2, 12));
		dto.setVendorGstin(arr[1].toString());
		dto.setVendorName(arr[2].toString());
		dto.setReqId(arr[3].toString());
		dto.setTotalRec(arr[4].toString());
		dto.setTaxPeriod(getMonthNameFromNumber(arr[5].toString()) + " - "
				+ getMonthNameFromNumber(arr[6].toString()));
		dto.setRespRecords(arr[7].toString());

		String respfilePath = arr[8] != null ? arr[8].toString() : null;

		if (respfilePath == null || respfilePath.isEmpty()) {
			dto.setRespDownld(false);
			dto.setRespDowldPath(null);
		} else {
			dto.setRespDownld(true);
			dto.setRespDowldPath(respfilePath);
		}

		String totalRespfilePath = arr[9] != null ? arr[9].toString() : null;

		if (totalRespfilePath == null || totalRespfilePath.isEmpty()) {
			dto.setTotRespDownld(false);
			dto.setTotalRespDowldPath(null);
		} else {
			dto.setTotRespDownld(true);
			dto.setTotalRespDowldPath(totalRespfilePath);
		}

		dto.setStatus(arr[10].toString() == "true" ? "Read" : "Unread");

		return dto;
	}

	private String getMonthNameFromNumber(String taxPeriod) {
		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		String monthString;
		switch (month) {
		case 1:
			monthString = "Jan";
			break;
		case 2:
			monthString = "Feb";
			break;
		case 3:
			monthString = "Mar";
			break;
		case 4:
			monthString = "Apr";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "Jun";
			break;
		case 7:
			monthString = "Jul";
			break;
		case 8:
			monthString = "Aug";
			break;
		case 9:
			monthString = "Sep";
			break;
		case 10:
			monthString = "Oct";
			break;
		case 11:
			monthString = "Nov";
			break;
		case 12:
			monthString = "Dec";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString + " " + taxPeriod.substring(4, 6);
	}

	private String dateChange(String oldDate) {
		DateTimeFormatter formatter = null;
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTimes = LocalDateTime.parse(oldDate, formatter);
		LocalDateTime dateTimeFormatter = EYDateUtil
				.toISTDateTimeFromUTC(dateTimes);
		DateTimeFormatter FOMATTER = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		String newdate = FOMATTER.format(dateTimeFormatter);
		return newdate;

	}

}
