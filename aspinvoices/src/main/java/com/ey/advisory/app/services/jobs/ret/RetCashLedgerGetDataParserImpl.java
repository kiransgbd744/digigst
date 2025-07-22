package com.ey.advisory.app.services.jobs.ret;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.ret.GetRefundClaimedEntity;
import com.ey.advisory.app.docs.dto.ret.RetIntAlertDto;
import com.ey.advisory.app.docs.dto.ret.RetItemDetailsDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */

@Slf4j
@Service("RetCashLedgerGetDataParserImpl")
public class RetCashLedgerGetDataParserImpl
		implements RetCashLedgerGetDataParser {

	@Override
	public Set<GetRefundClaimedEntity> parseCashLedgerGetData(
			RetGetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetRefundClaimedEntity> entities = new TreeSet<>();
		try {

			JsonObject jsonRetTableCashLedger = (new JsonParser())
					.parse(apiResp).getAsJsonObject()
					.get(APIConstants.INTALERT.toLowerCase()).getAsJsonObject();
			RetIntAlertDto retIntAlertDto = gson
					.fromJson(jsonRetTableCashLedger, RetIntAlertDto.class);

			RetItemDetailsDto dto123 = new RetItemDetailsDto();
			dto123.setRetIntAlertDto(retIntAlertDto);
			// String chksumRetCash = retIntAlertDto.getChksum();

			Map<String, Object> mapRetTblCashLedger = getAsRetTblCashLedger(
					dto123);

			mapRetTblCashLedger.forEach((key, val) -> {
				GetRefundClaimedEntity entity = new GetRefundClaimedEntity();

				entity.setGstin(dto.getGstin());
				// entity.setChksum(chksumRetCash);
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, (RetItemDetailsDto) val,
						APIConstants.INTALERT.toLowerCase(), entity);

				entities.add(entity);

			});

		} catch (Exception e) {
			String msg = "Failed to Parse Intalert  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);

		}
		return entities;
	}

	private Map<String, Object> getAsRetTblCashLedger(RetItemDetailsDto dto) {
		Map<String, Object> map = new TreeMap<>();
		map.put(APIConstants.LATEFILEPMT,
				dto.getRetIntAlertDto().getLatefilepmt08());
		map.put(APIConstants.LATERETFILE,
				dto.getRetIntAlertDto().getLateretfile());
		map.put(APIConstants.LATEREPDOC,
				dto.getRetIntAlertDto().getLaterepdoc());
		map.put(APIConstants.REJDOC, dto.getRetIntAlertDto().getRejdoc());
		map.put(APIConstants.AMENDDOC, dto.getRetIntAlertDto().getAmenddoc());
		return map;
	}

	private GetRefundClaimedEntity setEntity(String key, RetItemDetailsDto val,
			String type, GetRefundClaimedEntity entity) {

		// entity.setTableType(type);
		entity.setTableSection(key);
		entity.setGetDescription(key);

		// entity.setPaidItcIgst(val.getIgstAmount());
		// entity.setPaidItcCgst(val.getCgstAmount());
		// entity.setPaidItcSgst(val.getSgstAmount());
		// entity.setPaidItcCess(val.getCessAmount());

		return entity;

	}
}
