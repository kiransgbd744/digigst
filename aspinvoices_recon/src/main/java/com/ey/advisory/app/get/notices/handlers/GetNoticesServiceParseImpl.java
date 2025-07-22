
package com.ey.advisory.app.get.notices.handlers;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.TblGetNoticesRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *  @author sakshi.jain
 *
 */

@Slf4j
@Component("GetNoticesServiceParseImpl")
public class GetNoticesServiceParseImpl {

	@Autowired
	@Qualifier("TblGetNoticesRepository")
	private TblGetNoticesRepository noticeRefIdRepo;

	public List<TblGetNoticesEntity> parseNoticeDetails(Long batchId,
			List<Long> resultIds, GstNoticesReqDto dto) {

		try {
			TenantContext.setTenantId(dto.getGroupcode());
			Gson gson = GsonUtil.newSAPGsonInstance();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GST Notice inside  GetNoticesServiceParseImpl for batch id {} ",
						batchId);
			}

			List<TblGetNoticesEntity> noticesList = noticeRefIdRepo
					.findByGstinAndIsDeleteFalse(dto.getGstin());

			Set<String> activeRefIdList = noticesList.stream()
					.map(TblGetNoticesEntity::getReferenceId)
					.collect(Collectors.toSet());
			List<TblGetNoticesEntity> incrementRefIdList = new ArrayList<>();

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("API Response for ResultId {}:", id);
				}
				JsonObject respObject = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				JsonArray dataArray = respObject.getAsJsonArray("notices");
				Type listType = new TypeToken<List<GetNoticeRefIdDto>>() {
				}.getType();
				List<GetNoticeRefIdDto> refIdDto = gson.fromJson(dataArray,
						listType);
				convertToIncrementRefIdEntity(refIdDto, activeRefIdList,
						incrementRefIdList, dto.getGstin(), dto.getBatchId());

			});

			return incrementRefIdList;

		} catch (Exception ex) {
			LOGGER.error(
					" Exception while parsing the Get notices api {} for BatchId {} ",
					ex, batchId);
			throw new AppException(ex);
		}
	}

	private List<TblGetNoticesEntity> convertToIncrementRefIdEntity(
			List<GetNoticeRefIdDto> refIdDtoList, Set<String> activeRefIdList,
			List<TblGetNoticesEntity> incrementRefIdList, String gstin,
			Long batchId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		
		refIdDtoList.stream()
				.filter(dto -> dto.getRefId() != null
						&& !activeRefIdList.contains(dto.getRefId()))
				.forEach(dto -> {
					TblGetNoticesEntity entity = new TblGetNoticesEntity();
					entity.setReferenceId(dto.getRefId());
					entity.setArn(dto.getArn());
					entity.setModuleCode(dto.getModuleCd());
					entity.setAlertCode(dto.getAlertCd());
					entity.setDateOfIssue(dto.getDateOfIssue() != null
							&& !dto.getDateOfIssue().isEmpty()
									? LocalDate.parse(dto.getDateOfIssue(),
											formatter)
									: null);
					entity.setDateOfResponse(dto.getDateOfRespond() != null
							&& !dto.getDateOfRespond().isEmpty() && "NA".equalsIgnoreCase(dto.getDateOfRespond()) 
									? LocalDate.parse(dto.getDateOfRespond(),
											formatter)
									: null);
					entity.setIsResponded(
							!Strings.isNullOrEmpty(dto.getDateOfRespond())
									? true : false);
					entity.setIsDelete(false);
					entity.setCreatedOn(LocalDateTime.now());
					incrementRefIdList.add(entity);
				});

		return incrementRefIdList;
	}

}
