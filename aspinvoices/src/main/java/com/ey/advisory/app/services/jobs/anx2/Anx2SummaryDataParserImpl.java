/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetANX2B2B_DE_SummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1SummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx2ActionSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx2CounterPartyTotalActionSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx2IsdcSummaryEntity;
import com.ey.advisory.app.data.repositories.client.Anx1SummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx2B2BDESummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx2CounterPartySummaryRepository;
import com.ey.advisory.app.data.repositories.client.Anx2IsdcSummaryRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx2ActionSummaryRepository;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetActionSummaryData;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetCounterPartySummaryData;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetSectionSummaryData;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetSummaryData;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Dibyakanta.sahoo
 *
 */
@Component("Anx2SummaryDataParserImpl")
@Slf4j
public class Anx2SummaryDataParserImpl implements Anx2SummaryDataParser {

	public static String B2B = "B2B";
	public static String SEZWP = "SEZWP";
	public static String SEZWOP = "SEZWOP";
	public static String DE = "DE";
	public static String B2C = "B2C";
	public static String ECOM = "ECOM";
	public static String IMPS = "IMPS";
	public static String IMPG = "IMPG";
	public static String MIS = "MIS";
	public static String IMPGSEZ = "IMPGSEZ";
	public static String REV = "REV";
	public static String EXPWP = "EXPWP";
	public static String EXPWOP = "EXPWOP";

	@Autowired
	@Qualifier("Anx1SummaryAtGstnRepository")
	private Anx1SummaryAtGstnRepository repository;

	@Autowired
	@Qualifier("Anx2B2BDESummaryAtGstnRepository")
	private Anx2B2BDESummaryAtGstnRepository secSummRepository;

	@Autowired
	@Qualifier("Anx2IsdcSummaryRepository")
	private Anx2IsdcSummaryRepository isdcRepository;

	@Autowired
	@Qualifier("GetAnx2ActionSummaryRepository")
	private GetAnx2ActionSummaryRepository actionSumRepository;

	@Autowired
	@Qualifier("Anx2CounterPartySummaryRepository")
	private Anx2CounterPartySummaryRepository counterPartySummRepository;

	@Override
	public void parseAnx2SummaryData(Anx2GetInvoicesReqDto dto, String apiResp,
			Long batchId) {
		GetAnx1SummaryEntity parentSummaryEntity = new GetAnx1SummaryEntity();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject respObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			Anx1GetSummaryData jsonSummaryData = gson.fromJson(respObject,
					Anx1GetSummaryData.class);

			parentSummaryEntity.setGstin(jsonSummaryData.getGstin());
			parentSummaryEntity.setRetPeriod(jsonSummaryData.getReturnPeriod());
			parentSummaryEntity.setChecksum(jsonSummaryData.getChecksum());
			parentSummaryEntity
					.setSummaryType(jsonSummaryData.getSummaryType());
			// parentSummaryEntity.setLastSummaryTs(null);
			parentSummaryEntity.setBatchId(batchId);

			List<Anx1GetSectionSummaryData> jsonSectionSummaryList = jsonSummaryData
					.getSecsum();
			if (!jsonSectionSummaryList.isEmpty()
					&& jsonSectionSummaryList.size() > 0) {
				jsonSectionSummaryList.forEach(jsonSectionSummaryData -> {
					GetANX2B2B_DE_SummaryEntity entity = new GetANX2B2B_DE_SummaryEntity();
					if (jsonSectionSummaryData.getSecnm()
							.equalsIgnoreCase("B2B")
							|| jsonSectionSummaryData.getSecnm()
									.equalsIgnoreCase("DE")) {

						entity.setBatchId(batchId.toString());
						entity.setChksum(jsonSectionSummaryData.getChksum());
						entity.setGstIn(jsonSummaryData.getGstin());
						entity.setIsdelete(false);
						entity.setRetsec(jsonSectionSummaryData.getSecnm());
						entity.setTaxperiod(jsonSummaryData.getReturnPeriod());
						entity.setTtcess(jsonSectionSummaryData.getTotalCess());
						entity.setTtcgst(jsonSectionSummaryData.getTotalCgst());
						entity.setTtsgst(jsonSectionSummaryData.getTotalSgst());
						entity.setTtdoc(jsonSectionSummaryData.getTotalDoc());
						entity.setTtigst(jsonSectionSummaryData.getTotalIgst());
						entity.setTttax(jsonSectionSummaryData.getNetTax());
						entity.setTttaxval(new BigDecimal(
								jsonSectionSummaryData.getTotalVal()));

						entity.setTtval(new BigDecimal(
								jsonSectionSummaryData.getTotalVal()));

						secSummRepository.save(entity);
					}

					Set<GetAnx2ActionSummaryEntity> actionSumEntites = new HashSet<>();
					Set<Anx1GetActionSummaryData> jsonActionSummaryDatas = jsonSectionSummaryData
							.getActionsum();
					if (jsonActionSummaryDatas != null
							&& !jsonActionSummaryDatas.isEmpty()
							&& jsonActionSummaryDatas.size() > 0) {
						jsonActionSummaryDatas
								.forEach(jsonActionSummaryData -> {
									GetAnx2ActionSummaryEntity actionSummaryEntity = new GetAnx2ActionSummaryEntity();
									actionSummaryEntity.setAction(
											jsonActionSummaryData.getAction());

									actionSummaryEntity
											.setTtdoC(jsonActionSummaryData
													.getTotalDoc());
									actionSummaryEntity.setTtlval(
											new BigDecimal(jsonActionSummaryData
													.getTotalVal()));
									actionSummaryEntity.setTttax(
											jsonActionSummaryData.getNetTax());
									actionSummaryEntity
											.setTtigst(jsonActionSummaryData
													.getTotalIgst());
									actionSummaryEntity
											.setTtcgst(jsonActionSummaryData
													.getTotalCgst());
									actionSummaryEntity
											.setTtsgst(jsonActionSummaryData
													.getTotalSgst());
									actionSummaryEntity
											.setTtcess(jsonActionSummaryData
													.getTotalCess());

									// actionSummaryEntity.setHeaderId(jsonActionSummaryData.getId());

									actionSumEntites.add(actionSummaryEntity);
								});
					}
					actionSumRepository.saveAll(actionSumEntites);
					// sectionSummaryEntity.setActionsum(actionSumEntites);

					Set<GetAnx2CounterPartyTotalActionSummaryEntity> counterParySumEntities = new HashSet<>();
					Set<Anx1GetCounterPartySummaryData> jsonCounterPartySummaryDatas = jsonSectionSummaryData
							.getCptysum();
					if (jsonCounterPartySummaryDatas != null
							&& !jsonCounterPartySummaryDatas.isEmpty()
							&& jsonCounterPartySummaryDatas.size() > 0) {
						jsonCounterPartySummaryDatas
								.forEach(jsonCounterPartySummaryData -> {
									GetAnx2CounterPartyTotalActionSummaryEntity counterPartySummaryEntity = new GetAnx2CounterPartyTotalActionSummaryEntity();
									counterPartySummaryEntity.setCgstin(
											jsonCounterPartySummaryData
													.getCtin());
									counterPartySummaryEntity.setChkSum(
											jsonCounterPartySummaryData
													.getChksum());
									counterPartySummaryEntity.setTtdoc(
											jsonCounterPartySummaryData
													.getTotalDoc());
									counterPartySummaryEntity
											.setHeaderId(entity);
									counterParySumEntities
											.add(counterPartySummaryEntity);

								});
					}
					counterPartySummRepository.saveAll(counterParySumEntities);

					if (jsonSectionSummaryData.getSecnm()
							.equalsIgnoreCase("ISDC")) {
						GetAnx2IsdcSummaryEntity isdentity = new GetAnx2IsdcSummaryEntity();
						isdentity.setBatchId(batchId);

						// setters

						isdentity.setRetSec(jsonSectionSummaryData.getSecnm());
						isdentity
								.setTtdoc(jsonSectionSummaryData.getTotalDoc());
						isdentity.setTtval(new BigDecimal(
								jsonSectionSummaryData.getTotalVal()));
						isdentity.setTtigst(
								jsonSectionSummaryData.getTotalIgst());
						isdentity.setTtcgst(
								jsonSectionSummaryData.getTotalCgst());
						isdentity.setTtsgst(
								jsonSectionSummaryData.getTotalSgst());
						isdentity.setTtcess(
								jsonSectionSummaryData.getTotalCess());
						isdentity.setTttax(jsonSectionSummaryData.getNetTax());
						isdentity.setTttaxval(new BigDecimal(
								jsonSectionSummaryData.getTotalVal()));
						isdcRepository.save(isdentity);
					}

				});

			}

		} catch (Exception e) {
			String msg = "failed to parse Anx2 summary response";
			LOGGER.error(msg, e);

		}
		// return parentSummaryEntity;
		// return null;
	}

}