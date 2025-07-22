/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetAnx1ActionSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1B2cSecSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1CounterPartySummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1DocTypeSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1EcomSecSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1ExpwpExpwopSecSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1ImpgsezRevSecSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1ImpsImpgMisSecSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1SectionSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1SummaryEntity;
import com.ey.advisory.app.data.repositories.client.Anx1ActionSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1B2CSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1CounterPartySummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1DocTypeSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1EcomSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1ExpwpwopSecSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1ImpgSezSecSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1ImpsgMisSecSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1SecSummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx1SummaryAtGstnRepository;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetActionSummaryData;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetCounterPartySummaryData;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetDocumentTypeSummaryData;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetSectionSummaryData;
import com.ey.advisory.app.docs.dto.anx1.Anx1GetSummaryData;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1SummaryDataParserImpl")
public class Anx1SummaryDataParserImpl implements Anx1SummaryDataParser {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1SummaryDataParserImpl.class);

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
	@Qualifier("Anx1SecSummaryAtGstnRepository")
	private Anx1SecSummaryAtGstnRepository secSummRepository;

	@Autowired
	@Qualifier("Anx1ActionSummaryAtGstnRepository")
	private Anx1ActionSummaryAtGstnRepository actionSumRepository;

	@Autowired
	@Qualifier("Anx1CounterPartySummaryAtGstnRepository")
	private Anx1CounterPartySummaryAtGstnRepository counterPartySummRepository;

	@Autowired
	@Qualifier("Anx1DocTypeSummaryAtGstnRepository")
	private Anx1DocTypeSummaryAtGstnRepository docTypeSummRepository;

	@Autowired
	@Qualifier("Anx1B2CSummaryAtGstnRepository")
	private Anx1B2CSummaryAtGstnRepository b2cSummRepository;

	@Autowired
	@Qualifier("Anx1EcomSummaryAtGstnRepository")
	private Anx1EcomSummaryAtGstnRepository ecomSummRepository;

	@Autowired
	@Qualifier("Anx1ExpwpwopSecSummaryAtGstnRepository")
	private Anx1ExpwpwopSecSummaryAtGstnRepository expSummRepository;

	@Autowired
	@Qualifier("Anx1ImpgSezSecSummaryAtGstnRepository")
	private Anx1ImpgSezSecSummaryAtGstnRepository impgSezSummRepository;

	@Autowired
	@Qualifier("Anx1ImpsgMisSecSummaryAtGstnRepository")
	private Anx1ImpsgMisSecSummaryAtGstnRepository impsgmisSummRepository;

	@Override
	public GetAnx1SummaryEntity parseAnx1SummaryData(Anx1GetInvoicesReqDto dto,
			String apiResp, Long batchId) {
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
			parentSummaryEntity.setLastSummaryTs(null);
			GetAnx1SummaryEntity saveParentEntity = repository
					.save(parentSummaryEntity);

			List<GetAnx1SectionSummaryEntity> childSectionSumListEntities = new 
					       ArrayList<>();

			List<Anx1GetSectionSummaryData> jsonSectionSummaryList = jsonSummaryData
					.getSecsum();
			if (!jsonSectionSummaryList.isEmpty()
					&& jsonSectionSummaryList.size() > 0) {
				jsonSectionSummaryList.forEach(jsonSectionSummaryData -> {
					GetAnx1SectionSummaryEntity sectionSummaryEntity = new 
							GetAnx1SectionSummaryEntity();
					sectionSummaryEntity
							.setSecNum(jsonSectionSummaryData.getSecnm());
					sectionSummaryEntity
							.setChksum(jsonSectionSummaryData.getChksum());
					sectionSummaryEntity.setTotalDocNum(
							jsonSectionSummaryData.getTotalDoc());
					sectionSummaryEntity.setTotalDocVal(
							jsonSectionSummaryData.getTotalVal());
					sectionSummaryEntity
							.setNetTax(jsonSectionSummaryData.getNetTax());
					sectionSummaryEntity.setTotalIgst(
							jsonSectionSummaryData.getTotalIgst());
					sectionSummaryEntity.setTotalCess(
							jsonSectionSummaryData.getTotalCess());
					sectionSummaryEntity
							.setSecSummaryId(saveParentEntity.getId());

					secSummRepository.save(sectionSummaryEntity);
					Set<GetAnx1ActionSummaryEntity> actionSumEntites = new HashSet<>();
					Set<Anx1GetActionSummaryData> jsonActionSummaryDatas = jsonSectionSummaryData
							.getActionsum();
					if (jsonActionSummaryDatas != null
							&& !jsonActionSummaryDatas.isEmpty()
							&& jsonActionSummaryDatas.size() > 0) {
						jsonActionSummaryDatas
								.forEach(jsonActionSummaryData -> {
									GetAnx1ActionSummaryEntity actionSummaryEntity = new GetAnx1ActionSummaryEntity();
									actionSummaryEntity.setAction(
											jsonActionSummaryData.getAction());
									actionSummaryEntity.setChksum(
											jsonActionSummaryData.getChksum());
									actionSummaryEntity.setTotalDocNum(
											jsonActionSummaryData
													.getTotalDoc());
									actionSummaryEntity.setTotalDocVal(
											jsonActionSummaryData
													.getTotalVal());
									actionSummaryEntity.setNetTax(
											jsonActionSummaryData.getNetTax());
									actionSummaryEntity
											.setTotalIgst(jsonActionSummaryData
													.getTotalIgst());
									actionSummaryEntity
											.setTotalCgst(jsonActionSummaryData
													.getTotalCgst());
									actionSummaryEntity
											.setTotalSgst(jsonActionSummaryData
													.getTotalSgst());
									actionSummaryEntity
											.setTotalCess(jsonActionSummaryData
													.getTotalCess());
									actionSummaryEntity.setActionSecSumId(
											sectionSummaryEntity);

									actionSumEntites.add(actionSummaryEntity);
								});
					}
					actionSumRepository.saveAll(actionSumEntites);
					sectionSummaryEntity.setActionsum(actionSumEntites);

					Set<GetAnx1CounterPartySummaryEntity> counterParySumEntities = new HashSet<>();
					Set<Anx1GetCounterPartySummaryData> jsonCounterPartySummaryDatas = jsonSectionSummaryData
							.getCptysum();
					if (jsonCounterPartySummaryDatas != null
							&& !jsonCounterPartySummaryDatas.isEmpty()
							&& jsonCounterPartySummaryDatas.size() > 0) {
						jsonCounterPartySummaryDatas
								.forEach(jsonCounterPartySummaryData -> {
									GetAnx1CounterPartySummaryEntity counterPartySummaryEntity = new GetAnx1CounterPartySummaryEntity();
									counterPartySummaryEntity
											.setCtin(jsonCounterPartySummaryData
													.getCtin());
									counterPartySummaryEntity.setChksum(
											jsonCounterPartySummaryData
													.getChksum());
									counterPartySummaryEntity.setTotalDocNum(
											jsonCounterPartySummaryData
													.getTotalDoc());
									counterPartySummaryEntity.setTotalDocVal(
											jsonCounterPartySummaryData
													.getTotalVal());
									counterPartySummaryEntity.setNetTax(
											jsonCounterPartySummaryData
													.getNetTax());
									counterPartySummaryEntity.setTotalIgst(
											jsonCounterPartySummaryData
													.getTotalIgst());
									counterPartySummaryEntity.setTotalCgst(
											jsonCounterPartySummaryData
													.getTotalCgst());
									counterPartySummaryEntity.setTotalSgst(
											jsonCounterPartySummaryData
													.getTotalSgst());
									counterPartySummaryEntity.setTotalCess(
											jsonCounterPartySummaryData
													.getTotalCess());
									counterPartySummaryEntity
											.setCounterSecSumId(
													sectionSummaryEntity);

									counterParySumEntities
											.add(counterPartySummaryEntity);
								});
					}
					counterPartySummRepository.saveAll(counterParySumEntities);
					sectionSummaryEntity.setCptysum(counterParySumEntities);

					Set<GetAnx1DocTypeSummaryEntity> docTypeSummaryEntities = new HashSet<>();
					Set<Anx1GetDocumentTypeSummaryData> jsonDocumentTypeSummaryDatas = jsonSectionSummaryData
							.getDoctypsum();
					if (jsonDocumentTypeSummaryDatas != null
							&& !jsonDocumentTypeSummaryDatas.isEmpty()
							&& jsonDocumentTypeSummaryDatas.size() > 0) {
						jsonDocumentTypeSummaryDatas
								.forEach(jsonDocumentTypeSummaryData -> {
									GetAnx1DocTypeSummaryEntity anx1DocTypeSummaryEntity = new GetAnx1DocTypeSummaryEntity();
									anx1DocTypeSummaryEntity.setAction(
											jsonDocumentTypeSummaryData
													.getAction());
									anx1DocTypeSummaryEntity.setChksum(
											jsonDocumentTypeSummaryData
													.getChksum());
									anx1DocTypeSummaryEntity.setTotalDocNum(
											jsonDocumentTypeSummaryData
													.getTotalDoc());
									anx1DocTypeSummaryEntity.setTotalDocVal(
											jsonDocumentTypeSummaryData
													.getTotalVal());
									anx1DocTypeSummaryEntity.setNetTax(
											jsonDocumentTypeSummaryData
													.getNetTax());
									anx1DocTypeSummaryEntity.setTotalIgst(
											jsonDocumentTypeSummaryData
													.getTotalIgst());
									anx1DocTypeSummaryEntity.setTotalCgst(
											jsonDocumentTypeSummaryData
													.getTotalCgst());
									anx1DocTypeSummaryEntity.setTotalSgst(
											jsonDocumentTypeSummaryData
													.getTotalSgst());
									anx1DocTypeSummaryEntity.setTotalCess(
											jsonDocumentTypeSummaryData
													.getTotalCess());
									anx1DocTypeSummaryEntity.setDocSecSumId(
											sectionSummaryEntity);

									docTypeSummaryEntities
											.add(anx1DocTypeSummaryEntity);
								});
					}
					docTypeSummRepository.saveAll(docTypeSummaryEntities);
					sectionSummaryEntity.setDoctypsum(docTypeSummaryEntities);

					childSectionSumListEntities.add(sectionSummaryEntity);

					if (sectionSummaryEntity.getSecNum()
							.equalsIgnoreCase("B2C")) {
						GetAnx1B2cSecSummaryEntity entity = new GetAnx1B2cSecSummaryEntity();
						entity.setB2cSumId(parentSummaryEntity.getId());
						entity.setSecNum(sectionSummaryEntity.getSecNum());
						entity.setChksum(sectionSummaryEntity.getChksum());
						entity.setTotalDocNum(
								sectionSummaryEntity.getTotalDocNum());
						entity.setNetTax(sectionSummaryEntity.getNetTax());
						entity.setTotalDocVal(
								sectionSummaryEntity.getTotalDocVal());
						entity.setTotalIgst(
								sectionSummaryEntity.getTotalIgst());
						entity.setTotalCess(
								sectionSummaryEntity.getTotalCess());
						entity.setDelete(false);
						b2cSummRepository.save(entity);
					}

					if (sectionSummaryEntity.getSecNum()
							.equalsIgnoreCase("ECOM")) {
						GetAnx1EcomSecSummaryEntity entity = new GetAnx1EcomSecSummaryEntity();
						entity.setEcomSummaryId(saveParentEntity.getId());
						entity.setSecNum(sectionSummaryEntity.getSecNum());
						entity.setChksum(sectionSummaryEntity.getChksum());
						entity.setTotalDocNum(
								sectionSummaryEntity.getTotalDocNum());
						entity.setNetTax(sectionSummaryEntity.getNetTax());
						entity.setTotalDocVal(
								sectionSummaryEntity.getTotalDocVal());
						entity.setTotalIgst(
								sectionSummaryEntity.getTotalIgst());
						entity.setTotalCess(
								sectionSummaryEntity.getTotalCess());
						entity.setDelete(false);
						ecomSummRepository.save(entity);
					}

					if (sectionSummaryEntity.getSecNum()
							.equalsIgnoreCase("IMPS")
							|| sectionSummaryEntity.getSecNum()
									.equalsIgnoreCase("IMPG")
							|| sectionSummaryEntity.getSecNum()
									.equalsIgnoreCase("MIS")) {
						GetAnx1ImpsImpgMisSecSummaryEntity entity = new GetAnx1ImpsImpgMisSecSummaryEntity();
						entity.setImpsSummaryId(saveParentEntity.getId());
						entity.setSecNum(sectionSummaryEntity.getSecNum());
						entity.setChksum(sectionSummaryEntity.getChksum());
						entity.setTotalDocNum(
								sectionSummaryEntity.getTotalDocNum());
						entity.setNetTax(sectionSummaryEntity.getNetTax());
						entity.setTotalDocVal(
								sectionSummaryEntity.getTotalDocVal());
						entity.setTotalIgst(
								sectionSummaryEntity.getTotalIgst());
						entity.setTotalCess(
								sectionSummaryEntity.getTotalCess());
						entity.setDelete(false);
						impsgmisSummRepository.save(entity);
					}

					if (sectionSummaryEntity.getSecNum()
							.equalsIgnoreCase("IMPGSEZ")
							|| sectionSummaryEntity.getSecNum()
									.equalsIgnoreCase("REV")) {
						GetAnx1ImpgsezRevSecSummaryEntity entity = new GetAnx1ImpgsezRevSecSummaryEntity();
						entity.setImpgsezSummaryId(
								sectionSummaryEntity.getSecSummaryId());
						entity.setSecNum(sectionSummaryEntity.getSecNum());
						entity.setChksum(sectionSummaryEntity.getChksum());
						entity.setTotalDocNum(
								sectionSummaryEntity.getTotalDocNum());
						entity.setNetTax(sectionSummaryEntity.getNetTax());
						entity.setTotalDocVal(
								sectionSummaryEntity.getTotalDocVal());
						entity.setTotalIgst(
								sectionSummaryEntity.getTotalIgst());
						entity.setTotalCess(
								sectionSummaryEntity.getTotalCess());
						entity.setDelete(false);
						impgSezSummRepository.save(entity);
					}
					if (sectionSummaryEntity.getSecNum()
							.equalsIgnoreCase("EXPWOP")
							|| sectionSummaryEntity.getSecNum()
									.equalsIgnoreCase("EXPWP")) {
						GetAnx1ExpwpExpwopSecSummaryEntity entity = new GetAnx1ExpwpExpwopSecSummaryEntity();
						entity.setExpwpSummaryId(saveParentEntity.getId());
						entity.setSecNum(sectionSummaryEntity.getSecNum());
						entity.setChksum(sectionSummaryEntity.getChksum());
						entity.setTotalDocNum(
								sectionSummaryEntity.getTotalDocNum());
						entity.setNetTax(sectionSummaryEntity.getNetTax());
						entity.setTotalDocVal(
								sectionSummaryEntity.getTotalDocVal());
						entity.setTotalIgst(
								sectionSummaryEntity.getTotalIgst());
						entity.setTotalCess(
								sectionSummaryEntity.getTotalCess());
						entity.setDelete(false);

						expSummRepository.save(entity);
					}

				});

			}

		} catch (Exception e) {
			String msg = "failed to parse Anx1 summary response";
			LOGGER.error(msg, e);
		}
		return parentSummaryEntity;
	}

}