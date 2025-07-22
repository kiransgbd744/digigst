
package com.ey.advisory.app.get.notices.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticeDocumentDetailEntity;
import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.TblGetNoticesRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author sakshi.jain
 *
 */

@Slf4j
@Component("RefIdDocumentParserServiceImpl")
public class RefIdDocumentParserServiceImpl {

	@Autowired
	@Qualifier("TblGetNoticesRepository")
	private TblGetNoticesRepository noticeRefIdRepo;

	@Autowired
	@Qualifier("DocumentDetailsParserServiceImpl")
	private DocumentDetailsParserServiceImpl DocDetailParseService;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	public static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	public Pair<List<TblGetNoticesEntity>, List<TblGetNoticeDocumentDetailEntity>> processRefIdNotice(
			Long batchId, GstNoticesReqDto dto,
			List<TblGetNoticesEntity> incrementRefIdList) {

		List<TblGetNoticeDocumentDetailEntity> documentDetailsEntityList = new ArrayList<>();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET NOTICE inside  processRefIdNotice for batch id {} ",
						batchId);
			}

			for (TblGetNoticesEntity refIdEntity : incrementRefIdList) {
				String refId = refIdEntity.getReferenceId();
				APIResponse resp = getDocumentDetails(refId,
						TenantContext.getTenantId(), dto.getGstin());
				if (resp.isSuccess()) {
					JsonObject obj = JsonParser.parseString(resp.getResponse())
							.getAsJsonObject();
					GetNoticeRefIdDto refIdDto = gson.fromJson(obj,
							GetNoticeRefIdDto.class);
					refIdEntity.setNoticeType(refIdDto.getNoticeType());
					refIdEntity.setDerivedFromTaxPeriod(Integer.valueOf(refIdDto
							.getTaxPeriod().getFromYear()
							+ getMonthInteger(
									refIdDto.getTaxPeriod().getFromMonth())));
					refIdEntity.setDerivedToTaxPeriod(Integer.valueOf(refIdDto
							.getTaxPeriod().getToYear()
							+ getMonthInteger(
									refIdDto.getTaxPeriod().getToMonth())));
					refIdEntity.setDueDateOfReply(
							refIdDto.getDueDateOfReply() != null
									&& !refIdDto.getDueDateOfReply().isEmpty()
									&& !"NA".equalsIgnoreCase(
											refIdDto.getDueDateOfReply())
													? LocalDate.parse(
															refIdDto.getDueDateOfReply(),
															formatter)
													: null);
					refIdEntity.setCreatedOn(LocalDateTime.now());
					documentDetailsEntityList.addAll(DocDetailParseService
							.processDocumentIdDetails(batchId, refIdEntity,
									refIdDto, dto.getGstin()));

				} else {
					String error = resp.getError().getErrorCode()
							+ resp.getError().getErrorDesc();
					String msg = " Exception occured in RefIdDocumentParserServiceImpl ";
					LOGGER.error(msg + error);
					refIdEntity.setIsDelete(true);
				}
			}
			return new Pair(incrementRefIdList, documentDetailsEntityList);

		} catch (Exception ex) {
			LOGGER.error(
					" Exception while parsing the Get notices ref id api {} ",
					ex);
			throw new AppException(ex);
		}
	}

	public APIResponse getDocumentDetails(String refId, String groupCode,
			String gstin) {
		APIParam param1 = new APIParam("refId", refId);
		APIParam param2 = new APIParam("gstin", gstin);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.NOTICE_REFID_DTL, param1, param2);

		return apiExecutor.execute(params, null);

	}

	public int getMonthInteger(String monthStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.ENGLISH);
		monthStr = monthStr.substring(0, 1).toUpperCase()
				+ monthStr.substring(1).toLowerCase();
		Date date = sdf.parse(monthStr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int monthNumber = cal.get(Calendar.MONTH) + 1;
		return monthNumber;
	}

}
