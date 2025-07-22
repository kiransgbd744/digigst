package com.ey.advisory.app.services.jobs.gstr6;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6LateFeeEntity;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6LateFeeDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6LateOffSetDetails;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr6LateFeeDataParserImpl")
public class Gstr6LateFeeDataParserImpl implements Gstr6LateFeeDataParser {

	@Override
	public List<GetGstr6LateFeeEntity> parseLateFeeData(Gstr6GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		JsonObject jsonObject = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr6LateFeeEntity> entities = new ArrayList<>();
		try {
			if (APIConstants.LATEFEE.equalsIgnoreCase(dto.getType())) {
				jsonObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.LATEFEE.toLowerCase())
						.getAsJsonObject();
			}

			Type listType = new TypeToken<Gstr6LateFeeDto>() {
			}.getType();

			Gstr6LateFeeDto gstr6LateFeeDto = gson.fromJson(jsonObject, listType);
			if (gstr6LateFeeDto != null) {
				GetGstr6LateFeeEntity entity = new GetGstr6LateFeeEntity();
				entity.setCgstAmt(gstr6LateFeeDto.getCLamt());
				entity.setSgstAmt(gstr6LateFeeDto.getSLamt());
				entity.setDebitNumber(BigDecimal.valueOf(Long.valueOf(gstr6LateFeeDto.getDebitId())));

				String lateDate = gstr6LateFeeDto.getDate();
				if (lateDate != null) {
					entity.setLateDate(LocalDate.parse(lateDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				Gstr6LateOffSetDetails offSetDetails = gstr6LateFeeDto.getForoffset();
				entity.setLiabId(offSetDetails.getLiabId());
				entity.setTranCd(offSetDetails.getTranCd());
				entity.setBatchId(batchId);
				entity.setCreatedBy(APIConstants.SYSTEM);
				entity.setModifiedOn(LocalDateTime.now());
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				entity.setCreatedOn(convertNow);

				entities.add(entity);
			}

		} catch (Exception e) {
			String msg = "Failed to Parse Gstr6 Late Fee Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

}
